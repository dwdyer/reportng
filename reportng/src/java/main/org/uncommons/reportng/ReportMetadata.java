package org.uncommons.reportng;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.UnknownHostException;
import java.net.InetAddress;

/**
 * Provides access to static information useful when generating a report.
 * @author Daniel Dyer
 */
public final class ReportMetadata
{
    private static final String PROPERTY_KEY_PREFIX = "org.uncommons.reportng.";
    private static final String TITLE_KEY = PROPERTY_KEY_PREFIX + "title";
    private static final String DEFAULT_TITLE = "Test Results Report";
    private static final String COVERAGE_KEY = PROPERTY_KEY_PREFIX + "coverage-report";
    private static final String EXCEPTIONS_KEY = PROPERTY_KEY_PREFIX + "show-expected-exceptions";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEEE dd MMMM yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm z");
    

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


    /**
     * @return The URL (absolute or relative) of an HTML coverage report associated
     * with the test run.  Null if there is no coverage report.
     */
    public String getCoverageLink()
    {
        return System.getProperty(COVERAGE_KEY);
    }


    public boolean shouldShowExpectedExceptions()
    {
        return System.getProperty(EXCEPTIONS_KEY, "false").equalsIgnoreCase("true");
    }


    /**
     * @return The user account used to run the tests and the host name of the
     * test machine.
     */
    public String getUser() throws UnknownHostException
    {
        String user = System.getProperty("user.name");
        String host = InetAddress.getLocalHost().getHostName();
        return user + "@" + host;
    }


    public String getJavaInfo()
    {
        String version = System.getProperty("java.version");
        String vendor = System.getProperty("java.vendor");
        return "Java " + version + " (" + vendor + ")";
    }


    public String getPlatform()
    {
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String architecture = System.getProperty("os.arch");
        return osName + " " + osVersion + " (" + architecture +")";
    }
}
