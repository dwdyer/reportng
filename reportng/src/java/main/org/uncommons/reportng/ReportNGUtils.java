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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import org.testng.IClass;
import org.testng.IResultMap;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.ISuite;

/**
 * Utility class that provides various helper methods that can be invoked
 * from a Velocity template.
 * @author Daniel Dyer
 */
public class ReportNGUtils
{
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


    public SortedMap<IClass, List<ITestResult>> sortByTestClass(IResultMap results)
    {
        SortedMap<IClass, List<ITestResult>> sortedResults = new TreeMap<IClass, List<ITestResult>>(TEST_CLASS_COMPARATOR);
        for (ITestResult result : results.getAllResults())
        {
            List<ITestResult> resultsForClass = sortedResults.get(result.getTestClass());
            if (resultsForClass == null)
            {
                resultsForClass = new ArrayList<ITestResult>();
                sortedResults.put(result.getTestClass(), resultsForClass);
            }
            int index = Collections.binarySearch(resultsForClass, result, TEST_RESULT_COMPARATOR);
            if (index < 0)
            {
                index = Math.abs(index + 1);
            }
            resultsForClass.add(index, result);
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
        if (argument instanceof String)
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


    public boolean hasDependentGroups(ITestResult result)
    {
        return result.getMethod().getGroupsDependedUpon().length > 0;
    }


    public String getDependentGroups(ITestResult result)
    {
        String[] groups = result.getMethod().getGroupsDependedUpon();
        return commaSeparate(Arrays.asList(groups));
    }


    public boolean hasDependentMethods(ITestResult result)
    {
        return result.getMethod().getMethodsDependedUpon().length > 0;
    }


    public String getDependentMethods(ITestResult result)
    {
        String[] methods = result.getMethod().getMethodsDependedUpon();
        return commaSeparate(Arrays.asList(methods));
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
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            case '\'':
                return "&apos;";
            case '&':
                return "&amp;";
            default:
                return String.valueOf(character);
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
                {
                    // All spaces in a block of consecutive spaces are converted to
                    // non-breaking space (&nbsp;) except for the last one.  This allows
                    // significant whitespace to be retained without prohibiting wrapping.
                    char nextCh = i + 1 < s.length() ? s.charAt(i + 1) : 0;
                    buffer.append(nextCh==' ' ? "&nbsp;" : " ");
                    break;
                }
                case '\n':
                {
                    buffer.append("<br/>\n");
                    break;
                }
                default:
                {
                    buffer.append(escapeChar(ch));
                }
            }
        }
        return buffer.toString();
    }
}
