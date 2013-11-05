buildmetadata-maven-plugin
==========================

The build metadata maven plugin creates a detailed report of the various build time parameters employed during a build.
The information includes useful data that can be used to provide better transparency and accountability of the build
process. Meta data includes build times and dates, user environment information and Java/Maven command line options.
The reporting is configurable and extensible as well as being adaptable for projects using multiple artifacts.

Maven Repository
----------------
You can access the binary artifact via the JBoss release repository:

http://repository.jboss.org/nexus/content/repositories/releases/com/redhat/rcm/maven/plugin/buildmetadata-maven-plugin/1.3.0/

Metadata Description
--------------------

SCM information

1. revision number
2. revision date
3. locally modified files
4. URL of SCM server

System information

5. build time
6. operating system
7. name
8. architecture
9. version

Java runtime

10. vendor
11. name
12. version
13. virtual machine
14. compiler
15. JAVA_OPTS

Maven execution information

16. Maven version
17. active profiles
18. environment properties
19. command line and executed goals

MAVEN_OPTS

20. build user
21. build host name
22. Artifact and version
        group ID
        artifact ID
        build version
23. project info
24. home page URL
25. categories
26. tags

System Requirements
-------------------
The following specifies the minimum requirements to run this Maven plugin:

1.  Maven 2.0
2.  JDK 1.5

Installation Instructions
-------------------------
Clone the repository to your workspace and build with maven:

1. `git clone https://github.com/sbadakhc/buildmetadata-maven-plugin.git`
2. `mvn install`
3. Edit your project pom.xml to include the following: 

The options in the plugin's configuration section will produce a report that will include the command line executed 
along with the Maven and Java Options.  For the full options available and details on how to extend functionality please
refer to the upstream documentation linked below.

    '<project>  
      ...
      <!-- Set up the repository to fetch the buildmetadata-maven-plugin from -->
      <repositories>
        <repository>
          <id>repository.jboss.org</id>
          <name>JBoss Releases</name>
          <url>http://repository.jboss.org/nexus/content/repositories/releases/</url>
        </repository>
      </repositories>

      <pluginRepositories>
        <pluginRepository>
          <id>repository.jboss.org</id>
          <name>JBoss Releases</name>
          <url>http://repository.jboss.org/nexus/content/repositories/releases/</url>
        </pluginRepository>
      </pluginRepositories>

      ... 
      <!-- Declare the dependancy on the buildmetadata-maven-plugin -->
      <dependencies>
        <dependency>
          <groupId>com.redhat.rcm.maven.plugin</groupId>
          <artifactId>buildmetadata-maven-plugin</artifactId>
          <version>1.3.0</version>
        </dependency>
      </dependencies>

      ...
      <!-- Configure the buildmetadata-maven-plugin with the required reporting options -->
      <build>
        <plugins>
          <plugin>
            <groupId>com.redhat.rcm.maven.plugin</groupId>
            <artifactId>buildmetadata-maven-plugin</artifactId>
            <version>1.3.0</version>
            <executions>
              <execution>
                <phase>initialize</phase>
                <goals>
                  <goal>provide-buildmetadata</goal>
                </goals>
                <configuration>
                  <createPropertiesReport>false</createPropertiesReport>
                  <xmlOutputFile>${project.build.outputDirectory}/META-INF/buildmetadata.xml</xmlOutputFile>
                  <hideCommandLineInfo>false</hideCommandLineInfo>
                  <hideMavenOptsInfo>false</hideMavenOptsInfo>
                  <hideJavaOptsInfo>false</hideJavaOptsInfo>
                </configuration>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
    ...
    </project>

Runtime Example
---------------

Assuming the plugin configuration in your projects pom.xml matches the example provided then simply executing maven with
the install goal will create a buildmetadata.xml file in the generated jar file under the META-INF direcory of the
archive.

`mvn install`

You can view the generated build.properties file in the archive without extracting it with the following command:

`unzip -p example/MyApp/target/MyApp-1.0-SNAPSHOT.jar META-INF/buildmetadata.xml`

The report will resemble the example below:

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <buildmetadata xmlns="http://github.com/sbadakhc/buildmetadata-maven-plugin" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://github.com/sbadakhc/buildmetadata-maven-plugin http://github.com/sbadakhc/buildmetadata-maven-plugin">
    <name>1.0-20131105-SNAPSHOT</name>
    <version>1.0-SNAPSHOT</version>
    <groupId>com.redhat.rcm</groupId>
    <artifactId>HelloWorld</artifactId>
    <date>2013-11-05T08:57:22</date>
    <timestamp>1383638242882</timestamp>
    <build-year>2013</build-year>
    <project>
      <copyright-year>null-2013</copyright-year>
    </project>
    <scm/>
    <runtime>
      <build-server>localhost.localdomain</build-server>
      <build-user>sbadakhc</build-user>
      <os>
        <arch>amd64</arch>
        <name>Linux</name>
        <version>3.11.6-200.fc19.x86_64</version>
      </os>
     <java>
        <name>OpenJDK Runtime Environment</name>
        <version>1.7.0_45-mockbuild_2013_10_16_17_47-b00</version>
        <vendor>Oracle Corporation</vendor>
        <vm>OpenJDK 64-Bit Server VM</vm>
        <compiler>HotSpot 64-Bit Tiered Compilers</compiler>
     </java>
     <maven>
       <version>3.0.5</version>
       <commandline>-X -e install</commandline>
       <execution-project>com.redhat.rcm:HelloWorld:jar:1.0-SNAPSHOT</execution-project>
       <is-excution-root>true</is-excution-root>
       <goals>
         <goal>install</goal>
       </goals>
       <options>-Xmx4096m -XX:MaxPermSize=2048m -Xnoclassgc -XX:+DisableExplicitGC</options>
     </maven>
    </runtime>
    <misc>
      <metadata name="build.date.pattern">dd.MM.yyyy HH:mm:ss</metadata>
    </misc>
    </buildmetadata>

Goals
-----
The following goals are supported.  For a full explanation please refer to the upstream providers documentation.

1. buildmetadata:build-point
2. buildmetadata:buildmetadata-report
3. buildmetadata:provide-buildmetadata


Known Issues
------------

With *NIX implementations of Maven the mvn shell wrapper script will need to explicitly declare the following varible 
to capture Maven command line arguments:

    export MAVEN_CMD_LINE_ARGS="$@"

Red Hat/Fedora users should edit the /usr/bin/mvn wrapper file for /usr/share/maven/bin/mvn to include this declaration
and prevent the changes being lost as a result of future packages upgrades.

Notes
-----

For further infoprmation please visit the upstream providers web site at 
http://www.smartics.eu/buildmetadata-maven-plugin/

