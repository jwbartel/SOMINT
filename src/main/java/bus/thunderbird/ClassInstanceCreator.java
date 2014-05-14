package bus.thunderbird;


public class ClassInstanceCreator {

	public Object createInstance(String className, ClassLoader classLoader){
		Object retVal = null;
		
		try {
			Class classObj = java.lang.Class.forName(className, true, classLoader);
			retVal =  classObj.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
