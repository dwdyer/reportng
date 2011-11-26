package org.uncommons.reportng;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestDocumentReportNGUtilsTest {

	private static final String LOCAL_TEST_CLASS_PATH = "../../src/test/java/";
	
	TestDocumentReportNGUtils utils = new TestDocumentReportNGUtils();

	@BeforeMethod
	public void setup() {
		System.setProperty("org.uncommons.reportng.testdoc.test-class-path", LOCAL_TEST_CLASS_PATH);
	}
	
	@Test
	public void testNullForNotDeclaredTestPath() {
		assertEquals(utils.getTestClassPath(), "../../src/test/java/");
	}
	
	@Test
	public void testMergingArgumentsWithNonExistingClassParameterNames() {
		String className = "org.uncommons.reportng.sample.DataProviderTestTest";
		List<String> args = Arrays.asList("data1", "data2");
		
		List<String> actual = utils.mergeParametersNamesWithArgumentStrigns(
				className, 
				null, args);
		
		assertEquals(actual, args);
	}
	
	@Test
	public void testMergingArgumentsWithParameterNames() {
		String className = "org.uncommons.reportng.sample.DataProviderTest";
		String methodName = "testProvider";
		List<String> args = Arrays.asList("One", "1.0");
		
		List<String> actual = utils.mergeParametersNamesWithArgumentStrigns(
				className, 
				methodName, args);
		
		assertEquals(actual.size(), args.size());
		assertEquals(actual.get(0), " &nbsp; => Data1: One");
		assertEquals(actual.get(1), " &nbsp; => Data2: 1.0");
	}	
	
	@Test
	public void testMergingArgumentsWithParameterNamesWithoutSource() {
		System.clearProperty("org.uncommons.reportng.testdoc.test-class-path");
		
		String className = "org.uncommons.reportng.sample.DataProviderTest";
		String methodName = "testProvider";
		List<String> args = Arrays.asList("One", "1.0");
		
		List<String> actual = utils.mergeParametersNamesWithArgumentStrigns(
				className, 
				methodName, args);
		
		assertEquals(actual.size(), args.size());
		assertEquals(actual.get(0), "One");
		assertEquals(actual.get(1), "1.0");
	}	
}
