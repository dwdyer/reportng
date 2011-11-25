package org.uncommons.reportng.formatters;



public interface Formatter {

	public String prettifyTestClassName(String className);

	public String prettifyTestMethodName(String testMethod);

}