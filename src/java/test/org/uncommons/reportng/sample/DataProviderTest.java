package org.uncommons.reportng.sample;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * An example of using a TestNG DataProviderTest.  This used in the sample
 * report to verfiy that ReportNG can deal with this scenario correctly.
 * @author Daniel Dyer
 */
public class DataProviderTest
{
    @DataProvider(name = "provider")
    public Object[][] data()
    {
        return new Object[][]{{"One", 1.0d}, {"Two", 2.0d}, {"Three", 3.0d}};
    }


    @Test(groups = "should-pass", dataProvider = "provider")
    public void testProvider(String data1, double data2)
    {
        // Do nothing.
    }
}
