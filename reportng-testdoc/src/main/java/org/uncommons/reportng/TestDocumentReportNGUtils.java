package org.uncommons.reportng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.ITestResult;
import org.uncommons.reportng.formatters.Formatter;
import org.uncommons.reportng.formatters.TestDoxFormatter;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

public class TestDocumentReportNGUtils extends ReportNGUtils {

	private static ReportMetadata META = ReportMetadata.getReportMetadata();
	
	static String TEST_CLASS_PATH_PROPERTY = 
		ReportMetadata.PROPERTY_KEY_PREFIX + "testdoc.test-class-path";
	
	public String getTestClassPath() {
		return System.getProperty(TEST_CLASS_PATH_PROPERTY, null);
	}

	private JavaClass getClassWrapperBySource(String className, File f) {
		JavaDocBuilder builder = new JavaDocBuilder();
		JavaSource src;
		try {
			src = builder.addSource(f);
		} catch (Exception e) {
			return null;
		}
		
    	//trimpackage
		if (className.lastIndexOf(".") != -1) {
			className = className.substring(className.lastIndexOf(".") + 1);
		}	
		
    	for (JavaClass cls : src.getClasses()) {
    		if (cls.getName().equals(className)) {
    			return cls;
    		}
    	}
    	
    	return null;
	}
	
	private JavaClass getClassWrapper(String className) {

		final String JAVA_CLASS_SUFFIX = ".java";
		
		String path = getClass().getResource("/").getFile()
				+ getTestClassPath() 
				+ className.replace(".", "/")
				+ JAVA_CLASS_SUFFIX;
    	File f = new File(path);

    	JavaClass cls = getClassWrapperBySource(className, f);
    	
    	//this will extract parameter names from the compiled class
    	//which eventually means they will be named p0, p1 etc
    	//I don't like it
//    	if (cls == null) {
//    		JavaDocBuilder builder = new JavaDocBuilder();
//    		cls = builder.getClassByName(className);
//    	}
    	
    	return cls;
	}
	
	public List<String> mergeParametersNamesWithArgumentStrigns(
			String className, 
			String methodName, 
			List<String> argumentStrings) {
		
		JavaClass cls = getClassWrapper(className);
		
		if (cls != null) {
	    	JavaMethod[] methods = cls.getMethods();
	    	
	    	List<String> params = new ArrayList<String>();
	    	for(JavaMethod m : methods) {
	    		if (!m.getName().equals(methodName)) {
	    			continue;
	    		}
	    		for (JavaParameter p : m.getParameters()) {
	    			params.add(p.getName());
	    		}
	    	}
	
	    	List<String> mergedArgumentStrings = new ArrayList<String>();
	    	if (params.size() == argumentStrings.size()) {
		        for (int i=0; i<params.size(); i++)
		        {
		        	
		        	TestDoxFormatter formatter = new TestDoxFormatter();
		        	String prettifyTestMethodName = " &nbsp; => " + formatter.prettifyTestMethodName(params.get(i)) + ": ";
		        	mergedArgumentStrings.add(prettifyTestMethodName + argumentStrings.get(i));
		        }		
		        argumentStrings = mergedArgumentStrings;
	    	}
		}
    	return argumentStrings;
	}
	
	@Override
    public String getArguments(ITestResult result)
    {
    	String className = result.getTestClass().getName();
    	String methodName = result.getMethod().getMethodName();

    	Object[] arguments = result.getParameters();
        List<String> argumentStrings = new ArrayList<String>(arguments.length);
        for (Object argument : arguments)
        {
        	argumentStrings.add(renderArgument(argument));
        }
        
        return breakLineSeparate(mergeParametersNamesWithArgumentStrigns(className, methodName, argumentStrings));
    }

	@Override
	public Formatter getFormatter() {
		return new TestDoxFormatter(META.getFilteredNameSuffix(), "test");
	}	
}
