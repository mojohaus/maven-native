 ------
 Maven 2 Native Plugin
 ------
 Marco Lessard
 <marco.lessard@marmitamots.ca>
 ------
 September 14, 2009

Compile Java code using GNU gcj

  The example uses GNU gcj to generate native executable from java source.

  Note:
      
  * <--main=full.class.name> is mandatory and must be the complete name of the java class that implements the main() entry.
      
  * Checkout real examples in {{{http://svn.codehaus.org/mojo/trunk/mojo/maven-native/native-maven-plugin/src/it/gcj}svn}}
  
  []
  
-------------------
<project>
  [...]
  <packaging>uexe</packaging>
  
  [...]
  
  <build>   
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <extensions>true</extensions>
        <configuration>
          <compilerExecutable>gcj</compilerExecutable>
          <compilerOutputDirectory}${project.build.directory}/objs</compilerOutputDirectory>
          <sources>
            <source>
              <directory>src/main/java</directory>
              <includes>
                <include>**/*.java</include>
              </includes>
            </source>
          </sources>
          <compilerStartOptions>
            <compilerStartOption>-O2</compilerStartOption>
          </compilerStartOptions>
          
          <linkerExecutable>gcj</linkerExecutable>
          <linkerStartOptions>
            <linkerStartOption>--main=full.class.name</linkerStartOption>
          </linkerStartOptions>
          
         </configuration>
       </plugin>
    </plugins>
  </build>
    
    
</project>
-------------------

