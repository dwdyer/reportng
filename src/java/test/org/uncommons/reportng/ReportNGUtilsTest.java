package org.uncommons.reportng;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@link ReportNGUtils}.
 * Contributed by "bdamm" (see https://reportng.dev.java.net/issues/show_bug.cgi?id=17).
 */
public class ReportNGUtilsTest
{
	private final ReportNGUtils utils = new ReportNGUtils();

	@Test
	public void testEscapeHtmlLtGt()
    {
		String testString = "</ns1:ErrorCode>";

		String result = utils.escapeHTMLString(testString);

		Assert.assertEquals(result, "&lt;/ns1:ErrorCode&gt;");
	}


    @Test
	public void testEscapeHtmlNbsp()
    {
		String testString = "    ";

		String result = utils.escapeHTMLString(testString);

		Assert.assertEquals(result, "&nbsp;&nbsp;&nbsp; ");
	}
}