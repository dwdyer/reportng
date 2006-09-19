package org.uncommons.reportng;

import java.util.*;
import java.text.*;
import org.testng.*;

/**
 * Utility class that provides various helper methods that can be invoked
 * from a Velocity template.
 * @author Daniel Dyer
 */
public class ReportNGUtils
{
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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


    public String formatDate(Date date)
    {
        return DATE_FORMAT.format(date);
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


    public String escapeString(String s)
    {
        return s.replace("<", "&lt;").replace(">", "&gt;");
    }
}
