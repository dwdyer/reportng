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

import org.testng.annotations.Test;
import org.testng.Reporter;

/**
 * Some test failures to be included in the sample output.
 * @author Daniel Dyer
 */
@Test(groups = "should-fail")
public class FailedTests
{
    @Test
    public void assertionFailure()
    {
        assert false : "This test failed.";
    }


    @Test
    public void assertionFailureWithMultilineMessage()
    {
        assert false : "This test failed.\nIts message is on multiple lines.\n     The last one has leading whitespace.";
    }


    @Test
    public void assertionFailureWithOutput()
    {
        Reporter.log("Here is some output from an unsuccessful test.");
        assert false : "This test failed.";
    }


    @Test
    public void exceptionThrown()
    {
        throw new IllegalStateException("Test failed.",
                                        new UnsupportedOperationException()); // Nested cause.
    }
}
