package org.uncommons.reportng.formatters;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class TestDoxFormatterTest {

	@Test
	public void fullyQualifiedClassNameFormatter() {
		Formatter formatter = new TestDoxFormatter();
		String name = "com.acxiom.generalmotors.basetypes.NameHashAndEqualsTest";
		
		String parsed = formatter.prettifyTestClassName(name);
		
		assertEquals(parsed, "Name hash and equals");
	}
	
	@Test
	public void fullyQualifiedIntegrationClassNameFormatter() {
		Formatter formatter = new TestDoxFormatter("IntegrationTest", "test");
		
		String name = "com.acxiom.generalmotors.basetypes.NameHashAndEqualsIntegrationTest";
		
		String parsed = formatter.prettifyTestClassName(name);
		
		assertEquals(parsed, "Name hash and equals");
	}	
		
}
