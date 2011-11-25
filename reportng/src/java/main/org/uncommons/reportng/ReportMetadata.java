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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Provides access to static information useful when generating a report.
 * @author Daniel Dyer
 */
public final class ReportMetadata
{
    static final String PROPERTY_KEY_PREFIX = "org.uncommons.reportng.";
    static final String TITLE_KEY = PROPERTY_KEY_PREFIX + "title";
    static final String DEFAULT_TITLE = "Test Results Report";
    static final String COVERAGE_KEY = PROPERTY_KEY_PREFIX + "coverage-report";
    static final String EXCEPTIONS_KEY = PROPERTY_KEY_PREFIX + "show-expected-exceptions";
    static final String OUTPUT_KEY = PROPERTY_KEY_PREFIX + "escape-output";
    static final String XML_DIALECT_KEY = PROPERTY_KEY_PREFIX + "xml-dialect";
    static final String STYLESHEET_KEY = PROPERTY_KEY_PREFIX + "stylesheet";
    static final String LOCALE_KEY = PROPERTY_KEY_PREFIX + "locale";
    static final String NAME_SUFFIX = PROPERTY_KEY_PREFIX + "name-suffix";
    static final String CUSTOM_UTILS_CLASS = PROPERTY_KEY_PREFIX + "custom-utils-class";
    static final String TEMPLATES_PATH = PROPERTY_KEY_PREFIX + "templates-path";
    static final String DEFAULT_TEMPLATES_PATH = "org/uncommons/reportng/templates/html/";
    static final String VELOCITY_LOG_KEY = PROPERTY_KEY_PREFIX + "velocity-log";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEEE dd MMMM yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm z");
    
    private static ReportMetadata instance;

    private ReportMetadata() {
    	
    }
    
    public static synchronized ReportMetadata getReportMetadata() {
    	if (null == instance) {
    		instance = new ReportMetadata();
    	}
    	
    	return instance;
    }
    
    /**
     * The date/time at which this report is being generated.
     */
    private final Date reportTime = new Date();


    /**
     * @return A String representation of the report date.
     * @see #getReportTime()
     */
    public String getReportDate()
    {
        return DATE_FORMAT.format(reportTime);
    }


    /**
     * @return A String representation of the report time.
     * @see #getReportDate()
     */
    public String getReportTime()
    {
        return TIME_FORMAT.format(reportTime);
    }


    public String getReportTitle()
    {
        return System.getProperty(TITLE_KEY, DEFAULT_TITLE);
    }

    public String getFilteredNameSuffix()
    {
        return System.getProperty(NAME_SUFFIX, "");
    }    
    
    public String getTemplatesPath() 
    {
    	return System.getProperty(TEMPLATES_PATH, DEFAULT_TEMPLATES_PATH);
    }
    
    /**
     * @return The URL (absolute or relative) of an HTML coverage report associated
     * with the test run.  Null if there is no coverage report.
     */
    public String getCoverageLink()
    {
        return System.getProperty(COVERAGE_KEY);
    }


    /**
     * If a custom CSS file has been specified, returns the path.  Otherwise
     * returns null.
     * @return A {@link File} pointing to the stylesheet, or null if no stylesheet
     * is specified.
     */
    public File getStylesheetPath()
    {
        String path = System.getProperty(STYLESHEET_KEY);
        return path == null ? null : new File(path);
    }


    /**
     * Returns false (the default) if stack traces should not be shown for
     * expected exceptions.
     * @return True if stack traces should be shown even for expected exceptions,
     * false otherwise.
     */
    public boolean shouldShowExpectedExceptions()
    {
        return System.getProperty(EXCEPTIONS_KEY, "false").equalsIgnoreCase("true");
    }


    /**
     * Returns true (the default) if log text should be escaped when displayed in a
     * report.  Turning off escaping allows you to do something link inserting
     * link tags into HTML reports, but it also means that other output could
     * accidentally corrupt the mark-up.
     * @return True if reporter log output should be escaped when displayed in a
     * report, false otherwise.
     */
    public boolean shouldEscapeOutput()
    {
        return System.getProperty(OUTPUT_KEY, "true").equalsIgnoreCase("true");
    }


    /**
     * If the XML dialect has been set to "junit", we will render all skipped tests
     * as failed tests in the XML.  Otherwise we use TestNG's extended version of
     * the XML format that allows for "<skipped>" elements.
     */
    public boolean allowSkippedTestsInXML()
    {
        return !System.getProperty(XML_DIALECT_KEY, "testng").equalsIgnoreCase("junit");
    }


    /**
     * @return True if Velocity should generate a log file, false otherwise.
     */
    public boolean shouldGenerateVelocityLog()
    {
        return System.getProperty(VELOCITY_LOG_KEY, "false").equalsIgnoreCase("true");
    }


    /**
     * @return The user account used to run the tests and the host name of the
     * test machine.
     * @throws UnknownHostException If there is a problem accessing the machine's host name.
     */
    public String getUser() throws UnknownHostException
    {
        String user = System.getProperty("user.name");
        String host = InetAddress.getLocalHost().getHostName();
        return user + '@' + host;
    }


    public String getJavaInfo()
    {
        return String.format("Java %s (%s)",
                             System.getProperty("java.version"),
                             System.getProperty("java.vendor"));
    }


    public String getPlatform()
    {
        return String.format("%s %s (%s)",
                             System.getProperty("os.name"),
                             System.getProperty("os.version"),
                             System.getProperty("os.arch"));
    }


    /**
     * @return The locale specified by the System properties, or the platform default locale
     * if none is specified.
     */
    public Locale getLocale()
    {
        if (System.getProperties().containsKey(LOCALE_KEY))
        {
            String locale = System.getProperty(LOCALE_KEY);
                String[] components = locale.split("_", 3);
            switch (components.length)
            {
                case 1: return new Locale(locale);
                case 2: return new Locale(components[0], components[1]);
                case 3: return new Locale(components[0], components[1], components[2]);
                default: System.err.println("Invalid locale specified: " + locale);  
            }
        }
        return Locale.getDefault();
    }
    
	public ReportNGUtils getUtilsClass() {
		String utilsClazz = System.getProperty(CUSTOM_UTILS_CLASS);

		ReportNGUtils utils;
		try {
			utils = (ReportNGUtils) Class.forName(utilsClazz).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
//			utils = new ReportNGUtils();
		}

		return utils;
	}    
}
