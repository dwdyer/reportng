package org.uncommons.reportng.formatters;


public class TestDoxFormatter implements Formatter {
	private static String CLASS_SUFFIX = "Test";
	private static String METHOD_PREFIX = "test";

	public String prettifyTestClassName(String className) {
		className = ((className == null)?"":className);
		className = trimPackagePrefix(className);
		className = trimTestSuffix(className);
		return toHumaneString(className);
	}

	public String prettifyTestMethodName(String testMethod) {
		testMethod = ((testMethod == null)?"":testMethod);
		testMethod = trimTestPrefix(testMethod);
		return toHumaneString(testMethod);
	}

	private String toHumaneString(String input) {
		StringBuffer buffer = new StringBuffer();
		for (int x = 0; x < input.length(); x++) {
			char ch = input.charAt(x);
			if (x == 0) {
				buffer.append(Character.toUpperCase(ch));
			} else if (x != 0 && Character.isUpperCase(ch)) {
				buffer.append(" ");
				buffer.append(Character.toLowerCase(ch));
			} else {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}

	private String trimTestPrefix(String testMethod) {
		if (testMethod.startsWith(METHOD_PREFIX)) {
			testMethod = testMethod.substring(METHOD_PREFIX.length());
		}
		return testMethod;
	}

	private String trimTestSuffix(String className) {
		if (className.endsWith(CLASS_SUFFIX)) {
			className = className.substring(0,
					className.lastIndexOf(CLASS_SUFFIX));
		}
		return className;
	}

	private String trimPackagePrefix(String className) {
		if (className.lastIndexOf(".") != -1) {
			className = className.substring(className.lastIndexOf(".") + 1);
		}
		return className;
	}

	public boolean isATestMethod(String method) {
		if (method == null) return false;
		return method.startsWith(METHOD_PREFIX);
	}

	public boolean isTestClass(String clazz) {
		if (clazz == null) return false;
		return clazz.endsWith(CLASS_SUFFIX);
	}

	public void setTestClassSuffix(String s) {
		CLASS_SUFFIX = s;
	}
}