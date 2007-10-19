// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
package org.uncommons.reportng.sample;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * A configuration failure for testing that the report correctly reports
 * configuration failures.
 * @author Daniel Dyer
 */
public class FailedConfiguration
{
    /**
     * A configuration method that will fail causing any test cases
     * in this class to be skipped.
     */
    @BeforeClass
    public void configure()
    {
        throw new RuntimeException("Configuration failed.");
    }

    /**
     * This test ought to be skipped since the configuration for this
     * class will fail.
     */
    @Test
    public void thisShouldBeSkipped()
    {

    }
}
