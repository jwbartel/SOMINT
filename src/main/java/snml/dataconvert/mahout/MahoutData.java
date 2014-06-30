package snml.dataconvert.mahout;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.IntermediateRecommendationData;


/**
 * An intermediate recommendation data implemented in Mahout format
 *
 */
public class MahoutData extends IntermediateRecommendationData {

	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	List<Object> attrValues = new ArrayList<>();
	
	private void addAttrValue(int attrIndex, Object val) {
		if (attrIndex >= attrValues.size()) {
			attrValues.add(attrIndex, val);
		} else {
			attrValues.set(attrIndex, val);
		}
	}
	
	@Override
	public int setAttrValue(IntermediateDataSet dataset, int attrIndex,
			Object val) {
		if (val.getClass().isArray()) {
			int attrLength = Array.getLength(val);
			for (int i=0; i < attrLength; i++) {
				addAttrValue(attrIndex, val);
				attrIndex++;
			}
		} else {
			attrValues.add(attrIndex, val);
			attrIndex++;
		}
		return attrIndex;
	}

	@Override
	public int setDateAttrValue(IntermediateDataSet dataset, int attrIndex,
			Object val) {
		if(val instanceof Double || val instanceof Long){		
			attrValues.add(attrIndex, val);
		}else if(val instanceof String){
			try {
				long time = dateFormat.parse((String) val).getTime();
				addAttrValue(attrIndex, time);
			} catch (ParseException e) {
				System.out.println("Data format wrong");
				e.printStackTrace();
			}						
		}
		return attrIndex+1;	
	}

	@Override
	public int setMissing(IntermediateDataSet dataset, int attrIndex) {
		return attrIndex+1;
	}

	@Override
	public String getStringAttrValue(int attrIndex) {
		return attrValues.get(attrIndex).toString();
	}

	@Override
	public String getStringAttrValue(String attrName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getNumericAttrValue(int attrIndex) {
		Object val = getAttrValue(attrIndex);
		if (val instanceof String || val instanceof Number) {
			return Double.parseDouble(val.toString());
		} else {
			return (double) val;
		}
	}

	@Override
	public double getNumericAttrValue(String attrName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDateAttrValue(int attrIndex) {
		Object val = getAttrValue(attrIndex);
		if (val instanceof String) {
			return (String) val;
		} else {
			long time = Long.parseLong(val.toString());
			return dateFormat.format(new Date(time));
		}
	}

	@Override
	public String getDateAttrValue(String attrName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int length() {
		return attrValues.size();
	}

	@Override
	public Object getAttrValue(int attrIndex) {
		return attrValues.get(attrIndex);
	}

}
