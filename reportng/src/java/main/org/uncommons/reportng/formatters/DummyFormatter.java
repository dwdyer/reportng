package org.uncommons.reportng.formatters;


public class DummyFormatter implements Formatter {

	public String prettifyTestClassName(String className) {
		return className;
	}

	public String prettifyTestMethodName(String testMethod) {
		return testMethod;
	}

}