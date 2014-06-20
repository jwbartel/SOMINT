package prediction.features.messages;

import java.text.SimpleDateFormat;
import java.util.Date;

import snml.dataimport.MessageData;
import snml.dataimport.MsgDataConfig;
import snml.dataimport.ThreadData;
import snml.dataimport.email.EmailDataConfig;
import snml.rule.NumericFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Extract the time period in seconds from starting message sent till
 * the first response message sent
 */
public class SecondsToFirstResponseRule extends NumericFeatureRule implements IBasicFeatureRule{

	/**
	  * Create an FirstResponseTimeRule
	  * 
	  * @param destFeatureName name for extracted feature
	  */
	public SecondsToFirstResponseRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Extract the time period length from starting message sent till
	 * the first response message sent from given thread
	 * 
	 * @param aThread the source thread data
	 * @return the time period length from starting message sent till
	 * 			the first response message sent from given thread 
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		
		MessageData msg1 = aThread.getKthEarlest(0);
		MessageData msg2 = aThread.getKthEarlest(1);
		if(msg1==null || msg2==null){
			return Double.POSITIVE_INFINITY;
		}
		
		String date1 = (String)msg1.getAttribute(EmailDataConfig.DATE_DEFAULT);
		String date2 = (String)msg2.getAttribute(EmailDataConfig.DATE_DEFAULT);
		
		SimpleDateFormat format = new SimpleDateFormat(MsgDataConfig.DATEFORMAT_DEFAULT);
		Date d1 = format.parse(date1);
		Date d2 = format.parse(date2);
		
		Double time = (d2.getTime()-d1.getTime())/(1000.0);
		return time;
	}
	
	

}
