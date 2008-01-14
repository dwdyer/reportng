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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.testng.IClass;
import org.testng.IResultMap;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * Utility class that provides various helper methods that can be invoked
 * from a Velocity template.
 * @author Daniel Dyer
 */
public class ReportNGUtils
{
    private static final String PROPERTY_KEY_PREFIX = "org.uncommons.reportng.";
    private static final String TITLE_KEY = PROPERTY_KEY_PREFIX + "title";
    private static final String DEFAULT_TITLE = "Test Results Report";
    private static final String COVERAGE_KEY = PROPERTY_KEY_PREFIX + "coverage-report";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEEE dd MMMM yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm z");
    private static final NumberFormat DURATION_FORMAT = new DecimalFormat("#0.000");

    /**
     * Comparator for sorting tests alphabetically by method name.
     */
    private static final Comparator<ITestResult> TEST_RESULT_COMPARATOR = new Comparator<ITestResult>()
    {
        public int compare(ITestResult result1, ITestResult result2)
        {
            return result1.getName().compareTo(result2.getName());
        }
    };


    /**
     * Comparator for sorting classes alphabetically by fully-qualified name.
     */
    private static final Comparator<IClass> TEST_CLASS_COMPARATOR = new Comparator<IClass>()
    {
        public int compare(IClass class1, IClass class2)
        {
            return class1.getName().compareTo(class2.getName());
        }
    };


    /**
     * The date/time at which this report is being generated.
     */
    private final Date reportTime = new Date();


    /**
     * Format the date portion of the specified date/time using the
     * long format.
     */
    public String formatDate(Date date)
    {
        return DATE_FORMAT.format(date);
    }


    /**
     * Format the time portion of the specified date/time using the
     * 24 hour clock.
     */
    public String formatTime(Date date)
    {
        return TIME_FORMAT.format(date);
    }


    public String formatDuration(long startMillis, long endMillis)
    {
        long elapsed = endMillis - startMillis;
        double seconds = (double) elapsed / 1000;
        return DURATION_FORMAT.format(seconds);
    }


    public String formatDuration(Date start, Date end)
    {
        return formatDuration(start.getTime(), end.getTime());
    }


    /**
     * @return The date/time at which the reporting commenced.
     */
    public Date getReportTime()
    {
        return reportTime;
    }


    public SortedMap<IClass, SortedSet<ITestResult>> sortByTestClass(IResultMap results)
    {
        SortedMap<IClass, SortedSet<ITestResult>> sortedResults = new TreeMap<IClass, SortedSet<ITestResult>>(TEST_CLASS_COMPARATOR);
        for (ITestResult result : results.getAllResults())
        {
            SortedSet<ITestResult> resultsForClass = sortedResults.get(result.getTestClass());
            if (resultsForClass == null)
            {
                resultsForClass = new TreeSet<ITestResult>(TEST_RESULT_COMPARATOR);
                sortedResults.put(result.getTestClass(), resultsForClass);
            }
            resultsForClass.add(result);
        }
        return sortedResults;
    }


    /**
     * Convert a Throwable into a list containing the throwable itself and all
     * of its causes.
     */
    public List<Throwable> getCauses(Throwable t)
    {
        List<Throwable> causes = new LinkedList<Throwable>();
        Throwable next = t;
        while (next.getCause() != null)
        {
            next = next.getCause();
            causes.add(next);
        }
        return causes;
    }


    public List<String> getTestOutput(ITestResult result)
    {
        List<String> unescapedOutput = Reporter.getOutput(result);
        List<String> escapedOutput = new ArrayList<String>(unescapedOutput.size());
        for (String s : unescapedOutput)
        {
            escapedOutput.add(escapeString(s));
        }
        return escapedOutput;
    }


    /**
     * Replace any angle brackets or ampersands with the corresponding HTML entities
     * to avoid problems displaying the String in an HTML page.  Assumes that the
     * String does not already contain any ampersand entities (otherwise they will be
     * escaped again).
     * @param s The String to escape.
     * @return The escaped String.
     */
    public String escapeString(String s)
    {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }


    public String getReportTitle()
    {
        return System.getProperty(TITLE_KEY, DEFAULT_TITLE);
    }


    /**
     * Returns the URL (absolute or relative) of an HTML coverage report associated
     * with the test run.  Returns null if there is no coverage report.
     */
    public String getCoverageLink()
    {
        return System.getProperty(COVERAGE_KEY);
    }
}
