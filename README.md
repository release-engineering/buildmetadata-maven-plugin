buildmetadata-maven-plugin
==========================

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

    '<project>  
      ...
      <build>
        <plugins>
          <plugin>
            <groupId>com.redhat.rcm.maven.plugin</groupId>
            <artifactId>buildmetadata-maven-plugin</artifactId>
            <version>1.0</version>
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
      </build>
      ...
    </project>


Runtime Example
-------

Assuming the plugin configuration in pom matches the example provisded above the following command will create a
buildmetadata.xml file in the generated jar file under the META-INF direcory of the archive.

`mvn install buildmetadata:provide-buildmetadata`

You can view the generated build.properties file in the archive without extracting it with the following command:

`unzip -p target/HelloWorld.jar  META-INF/buildmetadata.xml`

Notes
-----

For further infoprmation please visit the upstream providers web site at 
http://www.smartics.eu/buildmetadata-maven-plugin/

