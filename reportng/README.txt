How to use ReportNG
===================

To use the reporting plug-in, set the "listeners" attribute of the "testng"
element in your Ant build file.  This attribute takes a comma-separated list of
reporter class names.  The class names for the ReportNG reporters are:

  org.uncommons.reportng.HTMLReporter
  org.uncommons.reportng.JUnitXMLReporter

You may also want to disable the default TestNG reporters by setting the
"useDefaultListeners" attribute to "false".

Your Ant task will probably look something like this:

  <testng classpathref="test-path"
          outputdir="${test-results.dir}"
          haltonfailure="true"
          useDefaultListeners="false"
          listeners="org.uncommons.reportng.HTMLReporter">
    <xmlfileset dir="." includes="testng.xml"/>
    <sysproperty key="org.uncommons.reportng.title" value="My Test Report"/>
  </testng>


If you are not using Ant to run TestNG (i.e. you are using Maven, the command
line or an IDE plug-in), please refer to the TestNG documentation
(http://testng.org/doc/documentation-main.html#running-testng) to find out how
to register custom listeners/reporters.


Supported System Properties
---------------------------

The following optional system properties can be set (via nested "<sysproperty>"
elements within the "<testng>" element) in order to customise the report
output:

  org.uncommons.reportng.coverage-report
      A relative or absolute URL that links to a test coverage report.

  org.uncommons.reportng.frames
      Defaults to "true".  If set to "false", generates the HTML report without
      using a frameset.  No navigation page is generated and the overview page
      becomes the index page.

  org.uncommons.reportng.show-expected-exceptions
      Set to "true" or "false" to specify whether the stack-traces of expected
      exceptions should be included in the output for passed test cases.  The
      default is "false" because the presence of stack-traces for successful
      tests may be confusing.

  org.uncommons.reportng.title
      Used to over-ride the report title.
