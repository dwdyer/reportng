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
package org.uncommons.reportng;

import org.testng.ITestNGMethod;
import java.util.Comparator;

/**
 * Comparator for sorting TestNG test methods.  Sorts method alphabeticaly
 * (first by fully-qualified class name, then by method name).
 * @author Daniel Dyer
 */
class TestMethodComparator implements Comparator<ITestNGMethod>
{
    public int compare(ITestNGMethod method1,
                       ITestNGMethod method2)
    {
        int compare = method1.getRealClass().getName().compareTo(method2.getRealClass().getName());
        if (compare == 0)
        {
            compare = method1.getMethodName().compareTo(method2.getMethodName());
        }
        return compare;
    }
}
