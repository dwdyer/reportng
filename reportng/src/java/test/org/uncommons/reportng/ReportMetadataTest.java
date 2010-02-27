// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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

import java.util.Locale;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link ReportMetadata} class.
 * @author Daniel Dyer
 */
public class ReportMetadataTest
{
    @Test
    public void testDefaultLocale()
    {
        // Unset any previously set property.
        System.getProperties().remove(ReportMetadata.LOCALE_KEY);
        // Make sure we know what the default locale is before we start.
        Locale.setDefault(new Locale("en", "GB"));

        ReportMetadata metadata = new ReportMetadata();
        String locale = metadata.getLocale().toString();
        assert locale.equals("en_GB") : "Wrong locale: " + locale;
    }


    @Test
    public void testLocaleLanguageOnly()
    {
        // Unset any previously set property.
        System.setProperty(ReportMetadata.LOCALE_KEY, "fr");

        ReportMetadata metadata = new ReportMetadata();
        String locale = metadata.getLocale().toString();
        assert locale.equals("fr") : "Wrong locale: " + locale;
    }


    @Test
    public void testLocaleLanguageAndCountry()
    {
        // Unset any previously set property.
        System.setProperty(ReportMetadata.LOCALE_KEY, "fr_CA");

        ReportMetadata metadata = new ReportMetadata();
        String locale = metadata.getLocale().toString();
        assert locale.equals("fr_CA") : "Wrong locale: " + locale;
    }


    @Test
    public void testLocaleLanguageCountryAndVariant()
    {
        // Unset any previously set property.
        System.setProperty(ReportMetadata.LOCALE_KEY, "fr_CA_POSIX");

        ReportMetadata metadata = new ReportMetadata();
        String locale = metadata.getLocale().toString();
        assert locale.equals("fr_CA_POSIX") : "Wrong locale: " + locale;
    }
}
