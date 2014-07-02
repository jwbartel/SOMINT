package snml.rule;

import java.util.Collection;

/**
 * Abstract, superclass of all rules extracting a set of objects
 * as a feature
 */
public class ObjectSetFeatureRule extends FeatureRule {

	public ObjectSetFeatureRule(String destFeatureName) {
		super(destFeatureName);
	}

	@Override
	public void checkValid(Object val) throws Exception {
		if(val!=null){
			if (!val.getClass().isArray() &&  !(val instanceof Collection)) {
				throw new Exception("must be a collection of items");
			}
		}

	}

}
