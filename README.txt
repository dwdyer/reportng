Test doc readme addendum
---------------------------

Tiny disclaimer: I'm branching the original ReportNG to ammend the report slightly 
- so that it is aligned with the CMMI template in the project I'm currently involved in. 

Changes include: 
 * fixing pom.xml to allow Maven builds,
 * adding formatter classes (to prettify class and method names)
   (for example substitute class and method names with a human readable form)
 * allow tests filtering (only classes ending with
   "org.uncommons.reportng.name-suffix" will be included in the report)
 * budle formmater together with pluggable ReportNGUtils
   through "org.uncommons.reportng.custom-utils-class"
 * hacking velocity templates to parse the names
   and removing hardcoded path for class-result.html.vm template 
 * adding a sample testdoc project which includes
   - QDox dependency to resolve test methods 
     parameters names and add them to the report. That way method 
     arguments in the test methods are called by the original, sourcecode
     names.
   - additional templates in org/uncommons/reportng/testdoc/html folder

This is the very early stage and I'm planning to rework the hacks. Plan is to
 * make ReportNG configurable to use external Velocity templates
 * make ReportNG pluggable so that user can define what formatters are used
   (standard, human readable, resolving parameter names, etc). 


Fully blown, extended configuration
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-surefire-plugin</artifactId>
	<version>2.10</version>
	<configuration>
		<systemPropertyVariables>
			<org.uncommons.reportng.velocity-log>true</org.uncommons.reportng.velocity-log>
			<org.uncommons.reportng.frames>false</org.uncommons.reportng.frames>
			<org.uncommons.reportng.name-suffix>IntegrationTest</org.uncommons.reportng.name-suffix>
			<org.uncommons.reportng.custom-utils-class>
				org.uncommons.reportng.TestDocumentReportNGUtils
			</org.uncommons.reportng.custom-utils-class>
			<org.uncommons.reportng.templates-path>
				org/uncommons/reportng/testdoc/html/
			</org.uncommons.reportng.templates-path>
		</systemPropertyVariables>
		<properties>
			<property>
				<name>testname</name>
				<value>${project.name} unit tests</value>
			</property>
			<property>
				<name>listener</name>
				<value>org.uncommons.reportng.HTMLReporter</value>
			</property>
		</properties>
		<workingDirectory>target/</workingDirectory>
	</configuration>			
</plugin>

