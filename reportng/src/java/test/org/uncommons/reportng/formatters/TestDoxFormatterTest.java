package org.uncommons.reportng.formatters;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class TestDoxFormatterTest {

	Formatter formatter = new TestDoxFormatter();
	
	@Test
	public void fullyQualifiedClassNameFormatter() {
		String name = "com.acxiom.generalmotors.basetypes.NameHashAndEqualsTest";
		
		String parsed = formatter.prettifyTestClassName(name);
		
		assertEquals(parsed, "Name hash and equals");
	}
	
	@Test
	public void fullyQualifiedIntegrationClassNameFormatter() {
		formatter.setTestClassSuffix("IntegrationTest");
		
		String name = "com.acxiom.generalmotors.basetypes.NameHashAndEqualsIntegrationTest";
		
		String parsed = formatter.prettifyTestClassName(name);
		
		assertEquals(parsed, "Name hash and equals");
	}	
		
}
