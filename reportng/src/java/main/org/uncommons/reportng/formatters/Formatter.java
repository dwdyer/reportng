package org.uncommons.reportng.formatters;



public interface Formatter {

	public String prettifyTestClassName(String className);

	public String prettifyTestMethodName(String testMethod);

	public boolean isATestMethod(String method);

	public boolean isTestClass(String clazz);

	public void setTestClassSuffix(String suffix);

}