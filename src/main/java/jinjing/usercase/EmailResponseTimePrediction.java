package jinjing.usercase;

import java.util.ArrayList;

import jinjing.dataimport.ThreadDataSet;
import jinjing.dataimport.email.EmailThreadParser;
import jinjing.rule.basicfeature.ContainsFollowMessageRule;
import jinjing.rule.basicfeature.EmailRecipientIdsRule;
import jinjing.rule.basicfeature.EmailRecipientNumRule;
import jinjing.rule.basicfeature.EmailSenderIdRule;
import jinjing.rule.basicfeature.EmailSubjectLengthRule;
import jinjing.rule.basicfeature.FirstResponseTimeRule;
import jinjing.rule.basicfeature.IBasicFeatureRule;
import jinjing.rule.filterrule.HasResponseFilterRule;
import jinjing.rule.superfeature.ISuperFeatureRule;
import jinjing.rule.superfeature.model.weka.IWekaModelRule;
import jinjing.rule.superfeature.model.weka.WekaLinearRegressionModelRule;
import jinjing.rule.superfeature.model.weka.WekaLogisticRegressionModelRule;
import jinjingdataconvert.BasicFeatureExtractor;
import jinjingdataconvert.IntermediateDataSet;
import jinjingdataconvert.SuperFeatureExtractor;
import jinjingdataconvert.ThreadDataFilter;
import jinjingdataconvert.WekaDataInitializer;
import jinjingdataconvert.WekaDataSet;

/**
 * We use an email response time prediction case derived from the ongoing work of 
 * the UNC-CH group; some researchers in our group are exploring prediction model 
 * for email response time. Temporally we are focusing on predicting response 
 * existence and the first response time based on the initiating message.
 * The work can be found at https://bitbucket.org/jbartel/recipientprediction/.
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class EmailResponseTimePrediction {

	public static void main(String[] args) throws Exception {
		
		/** Create an email parser to parse files into thread data set */
		EmailThreadParser emailParser = new EmailThreadParser();
		ThreadDataSet dataset1 = emailParser.parse("subjects.txt", "attachments.txt", "messages.txt");
		
		/** Select & copy those with response into another thread data set */
		HasResponseFilterRule filterRule = new HasResponseFilterRule(null);
		ThreadDataFilter filter = new ThreadDataFilter();
		ThreadDataSet dataset2 = filter.filt(dataset1, filterRule);
			
		/** predefine the email address (anonymized) number */
		int addressNum = 170;
		
		/** Create rules to extract basic features of questions.
		 *  These features are human judger related, so rules are defined to read files
		 */
		IBasicFeatureRule[] basicRules = new IBasicFeatureRule[4];
		basicRules[0] = new EmailSenderIdRule("senderId", addressNum);
		basicRules[1] = new EmailRecipientIdsRule("recipient", addressNum);
		basicRules[2] = new EmailSubjectLengthRule("subjectLength");
		basicRules[3] = new EmailRecipientNumRule("recipientNum");
		
		/** Create rules to extract basic measurements of answer quality.
		 */
		IBasicFeatureRule[] basicRules1 = new IBasicFeatureRule[1];
		basicRules1[0] = new ContainsFollowMessageRule("hasResponse");
		
		IBasicFeatureRule[] basicRules2 = new IBasicFeatureRule[1];
		basicRules2[0] = new FirstResponseTimeRule("responseTime");
		
		/** Create intermediate data initializer to define particular
		  * intermediate data initializing in basic feature extracting
		  * Here we use Weka intermediate dataset
		  */
		WekaDataInitializer initializer = new WekaDataInitializer();
		BasicFeatureExtractor basicExtractor = new BasicFeatureExtractor(initializer);
		
		/** Extract basic features & measurements , stored in an IntermediateDataSet */
		IntermediateDataSet featureSet1 = basicExtractor.extract(dataset1, "feature", basicRules);
		IntermediateDataSet measure1 = basicExtractor.extract(dataset1, "measure1", basicRules1);
		IntermediateDataSet featureSet2 = basicExtractor.extract(dataset2, "feature", basicRules);
		IntermediateDataSet measure2 = basicExtractor.extract(dataset2, "measure2", basicRules2);
		
		/** Merge features and measurement "hasResponse" in the same intermediate data set
		 * to fit logistic regression model
		 */
		IntermediateDataSet predictionSet1 = featureSet1.mergeByAttributes(measure1);
		/** Set attribute index of dependent variable */
		predictionSet1.setTargetIndex(); 
		
		/** Merge features and measurement "responseTime" in the same intermediate data set
		 * to fit linear regression model
		 */	
		IntermediateDataSet predictionSet2 = featureSet2.mergeByAttributes(measure2);
		/** Set attribute index of dependent variable */
		predictionSet2.setTargetIndex();
		
		/** save a prediction data set */
		predictionSet2.save("temp.arff");
		
		/** split data set into [trainset, testset] with 80% instances as trainset */
		IntermediateDataSet[] traintest1 = predictionSet1.splitToTrainAndTest(0.8);
		IntermediateDataSet[] traintest2 = predictionSet2.splitToTrainAndTest(0.8);
		
		/** Create rule of logistic regression and train.
		 */
		ISuperFeatureRule[] logi = new ISuperFeatureRule[1];
		ArrayList<String> domain = new ArrayList<String>(2);
		domain.add("y"); domain.add("n");
		logi[0] = new WekaLogisticRegressionModelRule("hasResponse", domain);		
		((IWekaModelRule)logi[0]).train(traintest1[0], null);
		
		/** Create rule of linear regression and train.
		 */
		ISuperFeatureRule[] linear = new ISuperFeatureRule[1];
		linear[0] = new WekaLinearRegressionModelRule("responseTime");		
		((IWekaModelRule)linear[0]).train(traintest2[0], null);
		
		/** Get prediction for test sets.
		 */
		SuperFeatureExtractor superExtractor = new SuperFeatureExtractor(initializer);
		IntermediateDataSet finDataSet1 = superExtractor.extract(traintest1[1], "testCluster", logi);
		IntermediateDataSet finDataSet2 = superExtractor.extract(traintest2[1], "testCluster", linear);
		
		/** Evaluate the models with Weka built-in evaluation
		 */
		((IWekaModelRule)logi[0]).evaluate((WekaDataSet)traintest1[0], (WekaDataSet)traintest1[1]);
		((IWekaModelRule)linear[0]).evaluate((WekaDataSet)traintest2[0], (WekaDataSet)traintest2[1]);

	}

}
