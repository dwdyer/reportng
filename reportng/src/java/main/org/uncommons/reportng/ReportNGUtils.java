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
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEEE dd MMMM yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm z");
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final NumberFormat DURATION_FORMAT = new DecimalFormat("#0.000s");

    private static final Comparator<ITestResult> TEST_RESULT_COMPARATOR = new Comparator<ITestResult>()
    {
        public int compare(ITestResult result1, ITestResult result2)
        {
            return result1.getName().compareTo(result2.getName());
        }
    };


    private static final Comparator<IClass> TEST_CLASS_COMPARATOR = new Comparator<IClass>()
    {
        public int compare(IClass class1, IClass class2)
        {
            return class1.getName().compareTo(class2.getName());
        }
    };


    // The date/time at which this report is being generated.
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


    /**
     * Format the specified date and time using the "reverse order"
     * format (year, month, day, hours, minutes, seconds).
     */
    public String formatDateTime(Date date)
    {
        return DATE_TIME_FORMAT.format(date);
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
     * Replace any angle brackets with the corresponding HTML entities to avoid
     * problems displaying the String in an HTML page.
     * @param s The String to escape.
     * @return The escaped String.
     */
    public String escapeString(String s)
    {
        return s.replace("<", "&lt;").replace(">", "&gt;");
    }
}
