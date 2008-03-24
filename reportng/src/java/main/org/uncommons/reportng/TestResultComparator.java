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

import java.util.Comparator;
import org.testng.ITestResult;

/**
 * Comparator for sorting TestNG test results alphabetically by method name.
 * @author Daniel Dyer
 */
class TestResultComparator implements Comparator<ITestResult>
{
    public int compare(ITestResult result1, ITestResult result2)
    {
        return result1.getName().compareTo(result2.getName());
    }
}
