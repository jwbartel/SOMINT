package snml.dataconvert.weka;

import java.lang.reflect.Array;
import java.text.ParseException;

import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataSet;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * An intermediate data implemented in Weka format
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class WekaData implements IntermediateData {
	
	/** The wrapped Weka instance */
	protected Instance inst;
	
	/** The Weka dataset which contains the instances */
	protected Instances relatedDataset;

	/**
	   * Create a Weka intermediate data with given number of attributes 
	   *
	   * @param attrNum number of attributes the instance will have
	   */
	public WekaData(int attrNum){
		inst = new DenseInstance(attrNum);
	}
	
	/**
	   * Create a Weka intermediate data with given Weka instance
	   *
	   * @param inst a Weka instance
	   */
	public WekaData(Instance inst){
		this.inst = inst;
	}
	
	/**
	   * Set the Weka intermediate dataset which contains the Weka instance
	   *
	   * @param dataset the intermediate dataset
	   */
	public void setRelatedDataset(WekaDataSet dataset){	
		this.relatedDataset = dataset.getDataSet();
	}
	
	/**
	   * Geth the wrapped Weka instance
	   *
	   * @return the wrapped Weka instance
	   */
	public Instance getInstValue(){
		return inst;
	}

	/**
	   * Set the value of a string/numeric/nominal attribute. 
	   * The attribute value must match the type of the attribute. 
	   * Must not change the dataset in any way.
	   *
	   * @param dataset associate dataset of current instance
	   * @param attrIndex index of attribute whose value is to be set
	   * @param val value to set to the attribute
	   * @return next attribute index
	   */
	@Override
	public int setAttrValue(IntermediateDataSet dataset, 
			int attrIndex,
			Object val) {		
		
		if(val instanceof Integer){
			val = ((Integer)val).doubleValue();
		}
		
		if(val instanceof Double){		
			inst.setValue(((WekaDataSet)dataset).attribute(attrIndex), (Double)val);
			attrIndex++;	
		}else if(val instanceof String){
			inst.setValue(((WekaDataSet)dataset).attribute(attrIndex), (String)val);
			attrIndex++;	
		}else if(val instanceof Double[] || val instanceof double[]){
			for(int j=0; j<Array.getLength(val); j++){				
				inst.setValue(((WekaDataSet)dataset).attribute(attrIndex++), 
						Array.getDouble(val, j));
			}
		}
		return attrIndex;
	}


	/**
	   * Set the value of a date attribute. 
	   * The attribute value must match the type of the attribute. 
	   * Must not change the dataset in any way.
	   *
	   * @param dataset associate dataset of current instance
	   * @param attrIndex index of attribute whose value is to be set
	   * @param val value to set to the attribute
	   * @return next attribute index
	   */
	@Override
	public int setDateAttrValue(IntermediateDataSet dataset, int attrIndex,
			Object val) {
		if(val instanceof Double){		
			inst.setValue(((WekaDataSet)dataset).attribute(attrIndex), (Double)val);
		}else if(val instanceof String){
			double time;
			try {
				time = ((WekaDataSet)dataset).attribute(attrIndex).parseDate((String)val);
				inst.setValue(((WekaDataSet)dataset).attribute(attrIndex),time);
			} catch (ParseException e) {
				System.out.println("Data format wrong");
				e.printStackTrace();
			}						
		}
		return attrIndex+1;		
	}


	/**
	   * Set the value of an attribute if the value is missing. 
	   * Must not change the dataset in any way.
	   *
	   * @param dataset associate dataset of current instance
	   * @param attrIndex index of attribute whose value is to be set
	   * @return next attribute index
	   */
	@Override
	public int setMissing(IntermediateDataSet dataset, int attrIndex) {
		return attrIndex+1;		
	}


	/**
	   * Get the string value of an attribute.
	   *
	   * @param attrIndex index of target attribute
	   * @return string value of the attribute
	   */
	@Override
	public String getStringAttrValue(int attrIndex) {
		return inst.stringValue(attrIndex);
	}

	/**
	   * Get the string value of an attribute.
	   *
	   * @param attrName name of target attribute
	   * @return string value of the attribute
	   */
	@Override
	public String getStringAttrValue(String attrName) {
		Attribute attr = relatedDataset.attribute(attrName);
		return inst.stringValue(attr);
	}

	/**
	   * Get the numeric(double) value of an attribute.
	   *
	   * @param attrIndex index of target attribute
	   * @return double value of the attribute
	   */
	@Override
	public double getNumericAttrValue(int attrIndex) {
		return inst.value(attrIndex);
	}

	/**
	   * Get the numeric(double) value of an attribute.
	   *
	   * @param attrName name of target attribute
	   * @return double value of the attribute
	   */
	@Override
	public double getNumericAttrValue(String attrName) {
		Attribute attr = relatedDataset.attribute(attrName);
		return inst.value(attr);
	}

	/**
	   * Get the value of an date attribute.
	   *
	   * @param attrIndex index of target attribute
	   * @return string value of the date attribute
	   */
	@Override
	public String getDateAttrValue(int attrIndex) {
		double time = inst.value(attrIndex);
		Attribute attr = relatedDataset.attribute(attrIndex);
		String date = attr.formatDate(time);
		
		return date;
	}

	/**
	   * Get the value of an date attribute.
	   *
	   * @param attrName name of target attribute
	   * @return string value of the date attribute
	   */
	@Override
	public String getDateAttrValue(String attrName) {		
		Attribute attr = relatedDataset.attribute(attrName);
		double time = inst.value(attr);
		String date = attr.formatDate(time);
		
		return date;
	}
	

}
