//=============================================================================
// Copyright 2006-2013 Daniel W. Dyer
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.testng.IInvokedMethod;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;

/**
 * Utility class that provides various helper methods that can be invoked
 * from a Velocity template.
 * @author Daniel Dyer
 */
public class ReportNGUtils
{
    private static final NumberFormat DURATION_FORMAT = new DecimalFormat("#0.000");
    private static final NumberFormat PERCENTAGE_FORMAT = new DecimalFormat("#0.00%");

    /**
     * Returns the aggregate of the elapsed times for each test result.
     * @param context The test results.
     * @return The sum of the test durations.
     */
    public long getDuration(ITestContext context)
    {
        long duration = getDuration(context.getPassedConfigurations().getAllResults());
        duration += getDuration(context.getPassedTests().getAllResults());
        // You would expect skipped tests to have durations of zero, but apparently not.
        duration += getDuration(context.getSkippedConfigurations().getAllResults());
        duration += getDuration(context.getSkippedTests().getAllResults());
        duration += getDuration(context.getFailedConfigurations().getAllResults());
        duration += getDuration(context.getFailedTests().getAllResults());
        return duration;
    }


    /**
     * Returns the aggregate of the elapsed times for each test result.
     * @param results A set of test results.
     * @return The sum of the test durations.
     */
    private long getDuration(Set<ITestResult> results)
    {
        long duration = 0;
        for (ITestResult result : results)
        {
            duration += (result.getEndMillis() - result.getStartMillis());
        }
        return duration;
    }


    public String formatDuration(long startMillis, long endMillis)
    {
        long elapsed = endMillis - startMillis;
        return formatDuration(elapsed);
    }


    public String formatDuration(long elapsed)
    {
        double seconds = (double) elapsed / 1000;
        return DURATION_FORMAT.format(seconds);
    }


    /**
     * Convert a Throwable into a list containing all of its causes.
     * @param t The throwable for which the causes are to be returned. 
     * @return A (possibly empty) list of {@link Throwable}s.
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


    /**
     * Retrieves all log messages associated with a particular test result.
     * @param result Which test result to look-up.
     * @return A list of log messages.
     */
    public List<String> getTestOutput(ITestResult result)
    {
        return Reporter.getOutput(result);
    }


    /**
     * Retieves the output from all calls to {@link org.testng.Reporter#log(String)}
     * across all tests.
     * @return A (possibly empty) list of log messages.
     */
    public List<String> getAllOutput()
    {
        return Reporter.getOutput();
    }


    public boolean hasArguments(ITestResult result)
    {
        return result.getParameters().length > 0;
    }


    public String getArguments(ITestResult result)
    {
        Object[] arguments = result.getParameters();
        List<String> argumentStrings = new ArrayList<String>(arguments.length);
        for (Object argument : arguments)
        {
            argumentStrings.add(renderArgument(argument));
        }
        return commaSeparate(argumentStrings);
    }


    /**
     * Decorate the string representation of an argument to give some
     * hint as to its type (e.g. render Strings in double quotes).
     * @param argument The argument to render.
     * @return The string representation of the argument.
     */
    private String renderArgument(Object argument)
    {
        if (argument == null)
        {
            return "null";
        }
        else if (argument instanceof String)
        {
            return "\"" + argument + "\"";
        }
        else if (argument instanceof Character)
        {
            return "\'" + argument + "\'";
        }
        else
        {
            return argument.toString();
        }
    }


    /**
     * @param result The test result to be checked for dependent groups.
     * @return True if this test was dependent on any groups, false otherwise.
     */
    public boolean hasDependentGroups(ITestResult result)
    {
        return result.getMethod().getGroupsDependedUpon().length > 0;
    }



    /**
     * @return A comma-separated string listing all dependent groups.  Returns an
     * empty string it there are no dependent groups.
     */
    public String getDependentGroups(ITestResult result)
    {
        String[] groups = result.getMethod().getGroupsDependedUpon();
        return commaSeparate(Arrays.asList(groups));
    }


    /**
     * @param result The test result to be checked for dependent methods.
     * @return True if this test was dependent on any methods, false otherwise.
     */
    public boolean hasDependentMethods(ITestResult result)
    {
        return result.getMethod().getMethodsDependedUpon().length > 0;
    }



    /**
     * @return A comma-separated string listing all dependent methods.  Returns an
     * empty string it there are no dependent methods.
     */
    public String getDependentMethods(ITestResult result)
    {
        String[] methods = result.getMethod().getMethodsDependedUpon();
        return commaSeparate(Arrays.asList(methods));
    }
    
    
    public boolean hasSkipException(ITestResult result) 
    {
    	return result.getThrowable() instanceof SkipException;
    }
    
    
    public String getSkipExceptionMessage(ITestResult result) 
    {
        return hasSkipException(result) ? result.getThrowable().getMessage() : "";
    }


    public boolean hasGroups(ISuite suite)
    {
        return !suite.getMethodsByGroups().isEmpty();
    }


    /**
     * Takes a list of Strings and combines them into a single comma-separated
     * String.
     * @param strings The Strings to combine.
     * @return The combined, comma-separated, String.
     */
    private String commaSeparate(Collection<String> strings)
    {
        StringBuilder buffer = new StringBuilder();
        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext())
        {
            String string = iterator.next();
            buffer.append(string);
            if (iterator.hasNext())
            {
                buffer.append(", ");
            }
        }
        return buffer.toString();
    }


    /**
     * Replace any angle brackets, quotes, apostrophes or ampersands with the
     * corresponding XML/HTML entities to avoid problems displaying the String in
     * an XML document.  Assumes that the String does not already contain any
     * entities (otherwise the ampersands will be escaped again).
     * @param s The String to escape.
     * @return The escaped String.
     */
    public String escapeString(String s)
    {
        if (s == null)
        {
            return null;
        }
        
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < s.length(); i++)
        {
            buffer.append(escapeChar(s.charAt(i)));
        }
        return buffer.toString();
    }


    /**
     * Converts a char into a String that can be inserted into an XML document,
     * replacing special characters with XML entities as required.
     * @param character The character to convert.
     * @return An XML entity representing the character (or a String containing
     * just the character if it does not need to be escaped).
     */
    private String escapeChar(char character)
    {
        switch (character)
        {
            case '<': return "&lt;";
            case '>': return "&gt;";
            case '"': return "&quot;";
            case '\'': return "&apos;";
            case '&': return "&amp;";
            default: return String.valueOf(character);
        }
    }


    /**
     * Works like {@link #escapeString(String)} but also replaces line breaks with
     * &lt;br /&gt; tags and preserves significant whitespace. 
     * @param s The String to escape.
     * @return The escaped String.
     */
    public String escapeHTMLString(String s)
    {
        if (s == null)
        {
            return null;
        }
        
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < s.length(); i++)
        {
            char ch = s.charAt(i);
            switch (ch)
            {
                case ' ':
                    // All spaces in a block of consecutive spaces are converted to
                    // non-breaking space (&nbsp;) except for the last one.  This allows
                    // significant whitespace to be retained without prohibiting wrapping.
                    char nextCh = i + 1 < s.length() ? s.charAt(i + 1) : 0;
                    buffer.append(nextCh==' ' ? "&nbsp;" : " ");
                    break;
                case '\n':
                    buffer.append("<br/>\n");
                    break;
                default:
                    buffer.append(escapeChar(ch));
            }
        }
        return buffer.toString();
    }


    /**
     * TestNG returns a compound thread ID that includes the thread name and its numeric ID,
     * separated by an 'at' sign.  We only want to use the thread name as the ID is mostly
     * unimportant and it takes up too much space in the generated report.
     * @param threadId The compound thread ID.
     * @return The thread name.
     */
    public String stripThreadName(String threadId)
    {
        if (threadId == null)
        {
            return null;
        }
        else
        {
            int index = threadId.lastIndexOf('@');
            return index >= 0 ? threadId.substring(0, index) : threadId;
        }
    }


    /**
     * Find the earliest start time of the specified methods.
     * @param methods A list of test methods.
     * @return The earliest start time.
     */
    public long getStartTime(List<IInvokedMethod> methods)
    {
        long startTime = System.currentTimeMillis();
        for (IInvokedMethod method : methods)
        {
            startTime = Math.min(startTime, method.getDate());
        }
        return startTime;
    }


    public long getEndTime(ISuite suite, IInvokedMethod method, List<IInvokedMethod> methods)
    {
        boolean found = false;
        for (IInvokedMethod m : methods)
        {
            if (m == method)
            {
                found = true;
            }
            // Once a method is found, find subsequent method on same thread.
            else if (found && m.getTestMethod().getId().equals(method.getTestMethod().getId()))
            {
                return m.getDate();
            }
        }
        return getEndTime(suite, method);
    }


    /**
     * Returns the timestamp for the time at which the suite finished executing.
     * This is determined by finding the latest end time for each of the individual
     * tests in the suite.
     * @param suite The suite to find the end time of. 
     * @return The end time (as a number of milliseconds since 00:00 1st January 1970 UTC).
     */
    private long getEndTime(ISuite suite, IInvokedMethod method)
    {
        // Find the latest end time for all tests in the suite.
        for (Map.Entry<String, ISuiteResult> entry : suite.getResults().entrySet())
        {
            ITestContext testContext = entry.getValue().getTestContext();
            for (ITestNGMethod m : testContext.getAllTestMethods())
            {
                if (method == m)
                {
                    return testContext.getEndDate().getTime();
                }
            }
            // If we can't find a matching test method it must be a configuration method.
            for (ITestNGMethod m : testContext.getPassedConfigurations().getAllMethods())
            {
                if (method == m)
                {
                    return testContext.getEndDate().getTime();
                }
            }
            for (ITestNGMethod m : testContext.getFailedConfigurations().getAllMethods())
            {
                if (method == m)
                {
                    return testContext.getEndDate().getTime();
                }
            }
        }
        throw new IllegalStateException("Could not find matching end time.");
    }


    public String formatPercentage(int numerator, int denominator)
    {
        return PERCENTAGE_FORMAT.format(numerator / (double) denominator);
    }
    
    public String getImageString(String s)
{
    String regex = "(<img(.*?)/>)";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(s);
    while (matcher.find()) {
        String group = matcher.group(1);
        //可根据实际情况多个图片 全部一起return
        return group;
    }
    return "";
}


public String removeImage(String s)
{
    return  s.replaceAll("<img(.*?)/>","");
}
}
