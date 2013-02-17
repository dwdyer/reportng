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

import java.io.File;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import org.apache.velocity.VelocityContext;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.IClass;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

/**
 * JUnit XML reporter for TestNG that uses Velocity templates to generate its
 * output.
 * @author Daniel Dyer
 */
public class JUnitXMLReporter extends AbstractReporter
{
    private static final String RESULTS_KEY = "results";

    private static final String TEMPLATES_PATH = "org/uncommons/reportng/templates/xml/";
    private static final String RESULTS_FILE = "results.xml";

    private static final String REPORT_DIRECTORY = "xml";


    public JUnitXMLReporter()
    {
        super(TEMPLATES_PATH);
    }


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
        removeEmptyDirectories(new File(outputDirectoryName));

        File outputDirectory = new File(outputDirectoryName, REPORT_DIRECTORY);
        boolean created = outputDirectory.mkdirs();
        if (!created) {
            throw new ReportNGException("Failed to create output directory " + outputDirectory.getAbsolutePath());
        }

        Collection<TestClassResults> flattenedResults = flattenResults(suites);

        for (TestClassResults results : flattenedResults)
        {
            VelocityContext context = createContext();
            context.put(RESULTS_KEY, results);

            try
            {
                generateFile(new File(outputDirectory, results.getTestClass().getName() + '_' + RESULTS_FILE),
                             RESULTS_FILE + TEMPLATE_EXTENSION,
                             context);
            }
            catch (Exception ex)
            {
                throw new ReportNGException("Failed generating JUnit XML report.", ex);
            }
        }
    }


    /**
     * Flatten a list of test suite results into a collection of results grouped by test class.
     * This method basically strips away the TestNG way of organising tests and arranges
     * the results by test class.
     */
    private Collection<TestClassResults> flattenResults(List<ISuite> suites)
    {
        Map<IClass, TestClassResults> flattenedResults = new HashMap<IClass, TestClassResults>();
        for (ISuite suite : suites)
        {
            for (ISuiteResult suiteResult : suite.getResults().values())
            {
                // Failed and skipped configuration methods are treated as test failures.
                organiseByClass(suiteResult.getTestContext().getFailedConfigurations().getAllResults(), flattenedResults);
                organiseByClass(suiteResult.getTestContext().getSkippedConfigurations().getAllResults(), flattenedResults);
                // Successful configuration methods are not included.

                organiseByClass(suiteResult.getTestContext().getFailedTests().getAllResults(), flattenedResults);
                organiseByClass(suiteResult.getTestContext().getSkippedTests().getAllResults(), flattenedResults);
                organiseByClass(suiteResult.getTestContext().getPassedTests().getAllResults(), flattenedResults);
            }
        }
        return flattenedResults.values();
    }


    private void organiseByClass(Set<ITestResult> testResults,
                                 Map<IClass, TestClassResults> flattenedResults)
    {
        for (ITestResult testResult : testResults)
        {
            getResultsForClass(flattenedResults, testResult).addResult(testResult);
        }
    }


    /**
     * Look-up the results data for a particular test class.
     */
    private TestClassResults getResultsForClass(Map<IClass, TestClassResults> flattenedResults,
                                                ITestResult testResult)
    {
        TestClassResults resultsForClass = flattenedResults.get(testResult.getTestClass());
        if (resultsForClass == null)
        {
            resultsForClass = new TestClassResults(testResult.getTestClass());
            flattenedResults.put(testResult.getTestClass(), resultsForClass);
        }
        return resultsForClass;
    }


    /**
     * Groups together all of the data about the tests results from the methods
     * of a single test class.
     */
    public static final class TestClassResults
    {
        private final IClass testClass;
        private final Collection<ITestResult> failedTests = new LinkedList<ITestResult>();
        private final Collection<ITestResult> skippedTests = new LinkedList<ITestResult>();
        private final Collection<ITestResult> passedTests = new LinkedList<ITestResult>();

        private long duration = 0;


        private TestClassResults(IClass testClass)
        {
            this.testClass = testClass;
        }


        public IClass getTestClass()
        {
            return testClass;
        }


        /**
         * Adds a test result for this class.  Organises results by outcome.
         */
        void addResult(ITestResult result)
        {
            switch (result.getStatus())
            {
                case ITestResult.SKIP:
                {
                    if (META.allowSkippedTestsInXML())
                    {
                        skippedTests.add(result);
                        break;
                    }
                    // Intentional fall-through (skipped tests marked as failed if XML doesn't support skips).
                }
                case ITestResult.FAILURE:
                case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
                {
                    failedTests.add(result);
                    break;
                }
                case ITestResult.SUCCESS:
                {
                    passedTests.add(result);
                    break;
                }
            }
            duration += (result.getEndMillis() - result.getStartMillis());
        }


        public Collection<ITestResult> getFailedTests()
        {
            return failedTests;
        }


        public Collection<ITestResult> getSkippedTests()
        {
            return skippedTests;
        }


        public Collection<ITestResult> getPassedTests()
        {
            return passedTests;
        }


        public long getDuration()
        {
            return duration;
        }
    }
}
