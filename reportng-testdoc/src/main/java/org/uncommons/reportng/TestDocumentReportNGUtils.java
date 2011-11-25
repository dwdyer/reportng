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
	
	@Override
    public String getArguments(ITestResult result)
    {
    	String className = result.getTestClass().getName();
    	
    	JavaDocBuilder builder = new JavaDocBuilder();
    	String path = getClass().getResource("/").getFile() 
			+ "../../src/test/java/" + className.replace(".", "/") + ".java";
    	File f = new File(path);
    	
    	System.out.println(path);
    	
    	try {
	    	JavaSource src = builder.addSource(f);
	    	JavaClass cls = src.getClasses()[0];
	    	
	    	JavaMethod[] methods = cls.getMethods();
	
	    	List<String> params = new ArrayList<String>();
	    	for(JavaMethod m : methods) {
	    		if (!m.getName().equals(result.getMethod().getMethodName())) {
	    			continue;
	    		}
	    		for (JavaParameter p : m.getParameters()) {
	    			params.add(p.getName());
	    		}
	    	}
	    	
	        Object[] arguments = result.getParameters();
	        List<String> argumentStrings = new ArrayList<String>(arguments.length);
	        for (int i=0; i<arguments.length; i++)
	        {
	        	Object argument = arguments[i];
	        	
	        	TestDoxFormatter formatter = new TestDoxFormatter();
	        	String prettifyTestMethodName = "";
	        	if (params.size() == arguments.length)
	        		prettifyTestMethodName = " &nbsp; ==> " + formatter.prettifyTestMethodName(params.get(i)) + ": ";
	            argumentStrings.add(prettifyTestMethodName + renderArgument(argument));
	        }
	        return breakLineSeparate(argumentStrings);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
    }

	@Override
	public Formatter getFormatter() {
		return new TestDoxFormatter(META.getFilteredNameSuffix(), "test");
	}	
}
