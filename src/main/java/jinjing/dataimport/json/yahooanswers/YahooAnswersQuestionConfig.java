package jinjing.dataimport.json.yahooanswers;

/**
 * Define fields name/type of YahooAnswers question.
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class YahooAnswersQuestionConfig extends YahooAnswersDataConfig{	
	
	/** attribute name of question id */
	public static String ID 				= "id";
	
	/** attribute name of question type {open, closed, ...} */
	public static String TYPE 				= "type";
	
	/** attribute name of question title */
	public static String SUBJECT 			= "Subject";	
	
	/** attribute name of question category */
	public static String CATEGORY 			= "Category";
	
	/** attribute name of number of its answers */
	public static String NUMANSWERS			= "NumAnswers";
	
	/** attribute name of number of its comments */
	public static String NUMCOMMENTS		= "NumComments";
	
	/** attribute name of content of chosen answer */
	public static String CHOSENANSWER				= "ChosenAnswer";
	
	/** attribute name of author id of chosen answer */
	public static String CHOSENANSWERID				= "ChosenAnswerId";
	
	/** attribute name of Author nick of chosen answer */
	public static String CHOSENANSWERNICK			= "ChosenAnswerNick";
	
	/** attribute name of timestamp of chosen answer */
	public static String CHOSENANSWERTIMESTAMP		= "ChosenAnswerTimeStamp";
	
	/** attribute name of award timestamp of chosen answer */
	public static String CHOSENANSWERAWARDTIMESTAMP	= "ChosenAnswerAwardTimeStamp";
		
	/** attribute name of answers */
	public static String ANSWERS			= "Answers";
	
	/** attribute name of comments */
	public static String COMMENTS			= "Comments";
	
	/**
	   * Create an YahooAnswer question data configure.
	   * Initialize attribute type map and attribute types.
	   *
	   */
	public YahooAnswersQuestionConfig(){
		super();
		
		attributeTypes.put(ID, STRING);
		attributeTypes.put(TYPE, "{Open, Answered, Deleted}");
		attributeTypes.put(SUBJECT, STRING);
		
		attributeTypes.put(CATEGORY, JSON);
		
		attributeTypes.put(NUMANSWERS, INT);
		attributeTypes.put(NUMCOMMENTS, INT);
		
		attributeTypes.put(CHOSENANSWER, STRING);
		attributeTypes.put(CHOSENANSWERID, STRING);
		attributeTypes.put(CHOSENANSWERNICK, STRING);
		attributeTypes.put(CHOSENANSWERTIMESTAMP, LONG);
		attributeTypes.put(CHOSENANSWERAWARDTIMESTAMP, LONG);
		
		attributeTypes.put(ANSWERS, MESSAGE);
		attributeTypes.put(COMMENTS, JSON);
	}
	

}
