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

import java.io.File;
import java.util.List;
import org.apache.velocity.VelocityContext;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.xml.XmlSuite;

/**
 * JUnit XML reporter for TestNG that uses Velocity templates to generate its
 * output.
 * @author Daniel Dyer
 */
public class JUnitXMLReporter extends AbstractReporter
{
    private static final String RESULT_KEY = "result";
    private static final String RESULTS_FILE = "results.xml";

    /**
     * Generates a set of XML files (JUnit format) that contain data about the
     * outcome of the specified test suites.
     * @param suites Data about the test runs.
     * @param outputDirectoryName The directory in which to create the report.
     */
    public void generateReport(List<XmlSuite> xmlSuites,
                               List<ISuite> suites,
                               String outputDirectoryName)
    {
        int index = 1;
        for (ISuite suite : suites)
        {
            int index2 = 1;
            for (ISuiteResult result : suite.getResults().values())
            {
                VelocityContext context = createContext();
                context.put(RESULT_KEY, result);
                try
                {
                    generateFile(new File(outputDirectoryName, "suite" + index + "_test" + index2 + '_' + RESULTS_FILE),
                                 RESULTS_FILE + TEMPLATE_EXTENSION,
                                 context);
                }
                catch (Exception ex)
                {
                    throw new ReportNGException("Failed generating JUnit XML report.", ex);
                }
                index2++;
            }
            ++index;
        }

    }
}
