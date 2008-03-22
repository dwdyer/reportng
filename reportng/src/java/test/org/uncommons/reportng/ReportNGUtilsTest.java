// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.reportng;

import org.testng.annotations.Test;

/**
 * Unit test for {@link ReportNGUtils}.
 * Originally contributed by "bdamm"
 * (see https://reportng.dev.java.net/issues/show_bug.cgi?id=17).
 */
public class ReportNGUtilsTest
{
	private final ReportNGUtils utils = new ReportNGUtils();

	@Test
	public void testEscapeTags()
    {
		final String originalString = "</ns1:ErrorCode>";
		String escapedString = utils.escapeString(originalString);
		assert escapedString.equals("&lt;/ns1:ErrorCode&gt;") : "Wrong escaping: " + escapedString;
	}


    @Test
    public void testEscapeQuotes()
    {
        final String originalString = "\"Hello\'";
        String escapedString = utils.escapeString(originalString);
        assert escapedString.equals("&quot;Hello&apos;") : "Wrong escaping: " + escapedString;
    }


    @Test
    public void testEscapeAmpersands()
    {
        final String originalString = "&&";
        String escapedString = utils.escapeString(originalString);
        assert escapedString.equals("&amp;&amp;") : "Wrong escaping: " + escapedString;        
    }


    @Test
	public void testEscapeSpaces()
    {
		final String originalString = "    ";
        // Spaces should not be escaped in XML...
        String escapedString = utils.escapeString(originalString);
        assert escapedString.equals(originalString) : "Wrong escaping: " + escapedString;
        // ...only in HTML.
        escapedString = utils.escapeHTMLString(originalString);
		assert escapedString.equals("&nbsp;&nbsp;&nbsp; ") : "Wrong escaping: " + escapedString;
	}
}