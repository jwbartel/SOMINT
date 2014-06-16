package jinjing.rule.basicfeature;

import java.text.SimpleDateFormat;
import java.util.Date;

import jinjing.dataimport.MessageData;
import jinjing.dataimport.MsgDataConfig;
import jinjing.dataimport.ThreadData;
import jinjing.dataimport.email.EmailDataConfig;
import jinjing.rule.NumericFeatureRule;

/**
 * Extract the time period length from starting message sent till
 * the first response message sent
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class FirstResponseTimeRule extends NumericFeatureRule implements IBasicFeatureRule{

	/**
	  * Create an FirstResponseTimeRule
	  * 
	  * @param destFeatureName name for extracted feature
	  */
	public FirstResponseTimeRule(String destFeatureName) {
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
			throw new Exception("Conversation has no response");
		}
		
		String date1 = (String)msg1.getAttribute(EmailDataConfig.DATE_DEFAULT);
		String date2 = (String)msg2.getAttribute(EmailDataConfig.DATE_DEFAULT);
		
		SimpleDateFormat format = new SimpleDateFormat(MsgDataConfig.DATEFORMAT_DEFAULT);
		Date d1 = format.parse(date1);
		Date d2 = format.parse(date2);
		
		Long time = (d2.getTime()-d1.getTime())/(60*1000);
		return time.doubleValue();
	}
	
	

}
