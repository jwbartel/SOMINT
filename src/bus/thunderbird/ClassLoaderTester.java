package bus.thunderbird;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderTester {
	
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		URL[] urls = new URL[1];
		urls[0] = new URL("file:/home/bartizzi/tutorial_ext/java/Prediction Code.jar"); 
		
		URLClassLoader classLoader = URLClassLoader.newInstance(urls, ClassLoader.getSystemClassLoader());
		
		
		Class predictionCheckerClass = java.lang.Class.forName("bus.thunderbird.PredictionFetcher", true, classLoader);
		Method predictionCheckerMethod = predictionCheckerClass.getMethod("getPredictions",null);
		
		
		System.out.println(predictionCheckerMethod.invoke(null));

	}

}
