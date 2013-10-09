ldmeta-maven-plugin
=======================

This plugin generates build meta data. This includes the build version and build date which are used to create a full
version property that contains the version, the build date and revision.

The meta data includes

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

Goals
-----

buildmetadata:build-point
buildmetadata:buildmetadata-report
buildmetadata:provide-buildmetadata
                                              
Usage
-----

You should specify the version in your project's plugin configuration:

Eample configuration

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.redhat.rcm</groupId>
  <artifactId>HelloWorld</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>HelloWorld</name>
  <url>http://maven.apache.org</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <pluginManagement>
      <plugins>

        <plugin>
          <groupId>com.redhat.rcm.maven.plugin</groupId>
          <artifactId>buildmetadata-maven-plugin</artifactId>
          <version>1.2</version>
          <executions>
            <execution>
              <phase>initialize</phase>
              <goals>
                <goal>provide-buildmetadata</goal>
              </goals>
              <configuration>
                <createPropertiesReport>false</createPropertiesReport>
                <xmlOutputFile>${project.build.outputDirectory}/META-INF/buildmetadata.xml</xmlOutputFile>
              </configuration>
            </execution>
          </executions>
        </plugin>

      </plugins>
    </pluginManagement>
  </build>

</project>

                                              

Runtime Example
-------

The following command will create a build.properties file in the generated jar file.

`mvn install buildmetadata:provide-buildmetadata`

You can view the generated build.properties file in the archive without extracting it with the following command:

`unzip -p target/HelloWorld.jar  META-INF/build.properties`

Notes
-----

For further infoprmation please visit the upstream providers web site at 
http://www.smartics.eu/buildmetadata-maven-plugin/

