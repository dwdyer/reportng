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
import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Comparator;
import org.apache.velocity.VelocityContext;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestNGMethod;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

/**
 * Enhanced HTML reporter for TestNG that uses Velocity templates to generate its
 * output.
 * @author Daniel Dyer
 */
public class HTMLReporter extends AbstractReporter
{
    private static final String FRAMES_PROPERTY = "org.uncommons.reportng.frames";

    private static final String TEMPLATES_PATH = "templates/html/";
    private static final String INDEX_FILE = "index.html";
    private static final String SUITES_FILE = "suites.html";
    private static final String OVERVIEW_FILE = "overview.html";
    private static final String GROUPS_FILE = "groups.html";
    private static final String RESULTS_FILE = "results.html";
    private static final String OUTPUT_FILE = "output.html";
    private static final String STYLE_FILE = "reportng.css";
    private static final String JS_FILE = "reportng.js";

    private static final String SUITE_KEY = "suite";
    private static final String SUITES_KEY = "suites";
    private static final String GROUPS_KEY = "groups";
    private static final String RESULT_KEY = "result";

    private static final String REPORT_DIRECTORY = "html";

    private static final Comparator<ITestNGMethod> METHOD_COMPARATOR = new TestMethodComparator();


    public HTMLReporter()
    {
        super(TEMPLATES_PATH);
    }

    
    /**
     * Generates a set of HTML files that contain data about the outcome of
     * the specified test suites.
     * @param suites Data about the test runs.
     * @param outputDirectoryName The directory in which to create the report.
     */
    public void generateReport(List<XmlSuite> xmlSuites,
                               List<ISuite> suites,
                               String outputDirectoryName)
    {
        removeEmptyDirectories(new File(outputDirectoryName));
        
        boolean useFrames = System.getProperty(FRAMES_PROPERTY, "true").equals("true");

        File outputDirectory = new File(outputDirectoryName, REPORT_DIRECTORY);
        outputDirectory.mkdir();

        try
        {
            if (useFrames)
            {
                createFrameset(outputDirectory);
            }
            createOverview(suites, outputDirectory, !useFrames);
            createSuiteList(suites, outputDirectory);
            createGroups(suites, outputDirectory);
            createResults(suites, outputDirectory);
            createLog(outputDirectory);
            copyResources(outputDirectory);
        }
        catch (Exception ex)
        {
            throw new ReportNGException("Failed generating HTML report.", ex);
        }
    }


    /**
     * Create the index file that sets up the frameset.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createFrameset(File outputDirectory) throws Exception
    {
        VelocityContext context = createContext();
        generateFile(new File(outputDirectory, INDEX_FILE),
                     INDEX_FILE + TEMPLATE_EXTENSION,
                     context);
    }


    private void createOverview(List<ISuite> suites,
                                File outputDirectory,
                                boolean isIndex) throws Exception
    {
        VelocityContext context = createContext();
        context.put(SUITES_KEY, suites);
        generateFile(new File(outputDirectory, isIndex ? INDEX_FILE : OVERVIEW_FILE),
                     OVERVIEW_FILE + TEMPLATE_EXTENSION,
                     context);
    }


    /**
     * Create the navigation frame.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createSuiteList(List<ISuite> suites,
                                 File outputDirectory) throws Exception
    {
        VelocityContext context = createContext();
        context.put(SUITES_KEY, suites);
        generateFile(new File(outputDirectory, SUITES_FILE),
                     SUITES_FILE + TEMPLATE_EXTENSION,
                     context);
    }


    /**
     * Generate a results file for each test in each suite.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createResults(List<ISuite> suites,
                               File outputDirectory) throws Exception
    {
        int index = 1;
        for (ISuite suite : suites)
        {
            int index2 = 1;
            for (ISuiteResult result : suite.getResults().values())
            {
                VelocityContext context = createContext();
                context.put(RESULT_KEY, result);
                generateFile(new File(outputDirectory, "suite" + index + "_test" + index2 + '_' + RESULTS_FILE),
                             RESULTS_FILE + TEMPLATE_EXTENSION,
                             context);
                ++index2;
            }
            ++index;
        }
    }


    /**
     * Generate a groups list for each suite.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createGroups(List<ISuite> suites,
                              File outputDirectory) throws Exception
    {
        int index = 1;
        for (ISuite suite : suites)
        {
            SortedMap<String, SortedSet<ITestNGMethod>> groups = sortGroups(suite.getMethodsByGroups());
            if (!groups.isEmpty())
            {
                VelocityContext context = createContext();
                context.put(SUITE_KEY, suite);
                context.put(GROUPS_KEY, groups);
                generateFile(new File(outputDirectory, "suite" + index + "_" + GROUPS_FILE),
                             GROUPS_FILE + TEMPLATE_EXTENSION,
                             context);                
            }
            ++index;
        }
    }


    /**
     * Generate a groups list for each suite.
     * @param outputDirectory The target directory for the generated file(s).
     */
    private void createLog(File outputDirectory) throws Exception
    {
        if (!Reporter.getOutput().isEmpty())
        {
            VelocityContext context = createContext();
            generateFile(new File(outputDirectory, OUTPUT_FILE),
                         OUTPUT_FILE + TEMPLATE_EXTENSION,
                         context);
        }
    }


    /**
     * Sorts groups alphabetically and also sorts methods within groups alphabetically
     * (class name first, then method name).  Also eliminates duplicate entries.
     */
    private SortedMap<String, SortedSet<ITestNGMethod>> sortGroups(Map<String, Collection<ITestNGMethod>> groups)
    {
        SortedMap<String, SortedSet<ITestNGMethod>> sortedGroups = new TreeMap<String, SortedSet<ITestNGMethod>>();
        for (Map.Entry<String, Collection<ITestNGMethod>> entry : groups.entrySet())
        {
            SortedSet<ITestNGMethod> methods = new TreeSet<ITestNGMethod>(METHOD_COMPARATOR);
            methods.addAll(entry.getValue());
            sortedGroups.put(entry.getKey(), methods);
        }
        return sortedGroups;
    }


    /**
     * Reads the CSS and JavaScript files from the JAR file and writes them to
     * the output directory.
     * @param outputDirectory Where to put the resources.
     * @throws IOException If the resources can't be read or written.
     */
    private void copyResources(File outputDirectory) throws IOException
    {
        copyResource(outputDirectory, STYLE_FILE, STYLE_FILE);
        copyResource(outputDirectory, JS_FILE, JS_FILE);
    }
}
