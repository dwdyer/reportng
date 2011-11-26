package org.uncommons.reportng.sample;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DataProviderTest {

    @DataProvider(name = "arrayProvider")
    public Object[][] dataArray()
    {
        return new Object[][]{{"One", 1.0d}, {"Two", 2.0d}, {"Three", 3.0d}};
    }


    @Test(dataProvider = "arrayProvider")
    public void testProvider(String data1, double data2)
    {
        // Do nothing.
    }
}
