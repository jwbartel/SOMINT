package jinjing.dataimport.json.yahooanswers;

/**
 * Define fields name/type of a YahooAnswers answer.
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class YahooAnswersAnswerConfig extends YahooAnswersDataConfig{

	/** attribute name of answer reference */
	//TODO: Haven't seen a reference != null
	public static String REREFENCE 				= "Reference";
	public static String REREFENCE_DATATYPE 	= null;
	
	/** attribute name of vote number received for best answer */
	public static String BEST 					= "Best";
	
	/** data type of BEST */
	public static String BEST_DATATYPE 			= INT;
	
	/**
	   * Create an YahooAnswer answer data configure.
	   * Initialize attribute type map and attribute types.
	   *
	   */
	public YahooAnswersAnswerConfig(){
		super();
		
		//attributeTypes.put(BEST, ?);
	}
}
