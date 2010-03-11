//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.reportng.sample;

import java.util.Arrays;
import java.util.Iterator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * An example of using a TestNG DataProviderTest.  This used in the sample
 * report to verfiy that ReportNG can deal with this scenario correctly.
 * @author Daniel Dyer
 */
public class DataProviderTest
{
    @DataProvider(name = "arrayProvider")
    public Object[][] dataArray()
    {
        return new Object[][]{{"One", 1.0d}, {"Two", 2.0d}, {"Three", 3.0d}};
    }


    @DataProvider(name = "iteratorProvider")
    public Iterator<Object[]> dataIterator()
    {
        return Arrays.asList(new Object[]{"One", 1.0d},
                             new Object[]{"Two", 2.0d},
                             new Object[]{"Three", 3.0d}).iterator();
    }


    @Test(groups = "should-pass", dataProvider = "arrayProvider")
    public void testProvider(String data1, double data2)
    {
        // Do nothing.
    }


    @Test(groups = "should-pass", dataProvider = "iteratorProvider")
    public void testProvider2(String data1, double data2)
    {
        // Do nothing.
    }
}
