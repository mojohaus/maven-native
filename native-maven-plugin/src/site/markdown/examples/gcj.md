<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

# Maven 2 Native Plugin

Marco Lessard
<marco.lessard@marmitamots.ca>
------------------------------

September 14, 2009

Compile Java code using GNU gcj

The example uses GNU gcj to generate native executable from java source.

Note:

* <--main=full.class.name> is mandatory and must be the complete name of the java class that implements the main() entry.

* Checkout real examples in [svn](http://svn.codehaus.org/mojo/trunk/mojo/maven-native/native-maven-plugin/src/it/gcj)

```xml
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
```

