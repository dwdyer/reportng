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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.xml.XmlSuite;

/**
 * Enhanced HTML reporter for TestNG that uses Velocity templates to generate its
 * output.
 * @author Daniel Dyer
 */
public class HTMLReporter implements IReporter
{
    private static final String ENCODING = "UTF-8";
    private static final String INDEX_FILE = "index.html";
    private static final String SUITES_FILE = "suites.html";
    private static final String SUMMARY_FILE = "summary.html";
    private static final String RESULTS_FILE = "results.html";
    private static final String STYLE_FILE = "reportng.css";
    private static final String TEMPLATE_EXTENSION = ".vm";

    private static final String SUITES_KEY = "suites";
    private static final String SUITE_KEY = "suite";
    private static final String RESULT_KEY = "result";
    private static final String UTILS_KEY ="utils";

    private static final ReportNGUtils UTILS = new ReportNGUtils();

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
        File outputDirectory = new File(outputDirectoryName);

        try
        {
            Velocity.setProperty("resource.loader", "classpath");
            Velocity.setProperty("classpath.resource.loader.class",
                                 "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init();
        }
        catch (Exception ex)
        {
            // TO DO: Decide what to do about this exception.
            ex.printStackTrace();
        }

        try
        {
            createIndex(outputDirectory);
            createSuiteList(suites, outputDirectory);
            createSummaries(suites, outputDirectory);
            createResults(suites, outputDirectory);
            copyStyleSheet(outputDirectory);
        }
        catch (IOException ex)
        {
            // TO DO: Decide what to do about this exception.
            ex.printStackTrace();
        }
        catch (Exception ex)
        {
            // TO DO: Decide what to do about this velocity exception.
            ex.printStackTrace();
        }
    }


    /**
     * Generate the specified output file by merging the specified
     * Velocity template with the supplied context.
     */
    private void generateFile(File file,
                              String template,
                              VelocityContext context) throws Exception
    {
        Writer writer = new BufferedWriter(new FileWriter(file));
        try
        {
            Velocity.mergeTemplate(template,
                                   ENCODING,
                                   context,
                                   writer);
            writer.flush();
        }
        finally
        {
            writer.close();
        }
    }


    private void createIndex(File outputDirectory) throws Exception
    {
        generateFile(new File(outputDirectory, INDEX_FILE),
                     INDEX_FILE + TEMPLATE_EXTENSION,
                     createContext());
    }


    private void createSuiteList(List<ISuite> suites, File outputDirectory) throws Exception
    {
        VelocityContext context = createContext();
        context.put(SUITES_KEY, suites);
        generateFile(new File(outputDirectory, SUITES_FILE),
                     SUITES_FILE + TEMPLATE_EXTENSION,
                     context);
    }


    private void createSummaries(List<ISuite> suites, File outputDirectory) throws Exception
    {
        int index = 1;
        for (ISuite suite : suites)
        {
            VelocityContext context = createContext();
            context.put(SUITE_KEY, suite);
            generateFile(new File(outputDirectory, "suite" + index + '_' + SUMMARY_FILE),
                         SUMMARY_FILE + TEMPLATE_EXTENSION,
                         context);
            ++index;
        }
    }


    private void createResults(List<ISuite> suites, File outputDirectory) throws Exception
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
                index2++;
            }
            ++index;
        }
    }


    /**
     * Reads the CSS file from the JAR file and writes it to the output directory.
     * @param outputDirectory Where to put the stylesheet.
     * @throws IOException If the stylesheet can't be read or written.
     */
    private void copyStyleSheet(File outputDirectory) throws IOException
    {
        InputStream resourceStream = ClassLoader.getSystemResourceAsStream(STYLE_FILE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));
        File styleFile = new File(outputDirectory, STYLE_FILE);
        Writer writer = new BufferedWriter(new FileWriter(styleFile));
        String line = reader.readLine();
        while (line != null)
        {
            writer.write(line);
            line = reader.readLine();
        }
        writer.flush();
        writer.close();
    }


    /**
     * Helper method that creates a Velocity context and initialises it
     * with a reference to the ReportNG utils.
     */
    private VelocityContext createContext()
    {
        VelocityContext context = new VelocityContext();
        context.put(UTILS_KEY, UTILS);
        return context;
    }
}
