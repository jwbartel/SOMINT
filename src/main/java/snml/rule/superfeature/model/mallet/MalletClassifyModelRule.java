package snml.rule.superfeature.model.mallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import snml.rule.superfeature.model.ClassifyModelRule;
import cc.mallet.classify.Classifier;

/**
 * Abstract, superclass of all Mallet classify model extracting feature rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class MalletClassifyModelRule extends ClassifyModelRule {

	/** Mallet classifier */
	Classifier classifier;
	
	String srcAttrName;
	
	/**
	   * Initialize name and value domain for extracted feature
	   * Initialize source attriubte name
	   * 
	   * @param featureName name for extracted feature
	   * @param srcAttrName name of source attribute name
	   * @param aDomain domain of classes' names
	   */
	public MalletClassifyModelRule(String featureName, String srcAttrName, ArrayList<String> aDomain) {
		super(featureName, aDomain);
		this.srcAttrName = srcAttrName;
	}

	/**
	 * Save trained model to given path in Mallet format
	 * 
	 * @param modelFilePath path to save model
	 * @throws Exception while saving process has error
	 */
	@Override
	public void save(String modelFilePath) throws Exception {
		File file = new File(modelFilePath);
		
		ObjectOutputStream oos =
	            new ObjectOutputStream(new FileOutputStream (file));
	    oos.writeObject (classifier);
	    oos.close();

	}

	/**
	 * Load trained model from given path in Mallet format
	 * 
	 * @param modelFilePath path to load model
	 * @throws Exception while loading process has error
	 */
	@Override
	public void load(String modelFilePath) throws Exception {
		File file = new File(modelFilePath);
		
		ObjectInputStream ois =
	            new ObjectInputStream (new FileInputStream (file));
	    classifier = (Classifier) ois.readObject();
	    ois.close();
	}

}
