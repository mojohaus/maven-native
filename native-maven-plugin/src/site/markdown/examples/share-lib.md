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

# Maven 2 Native Plugin - Share Library Example

Dan T. Tran
<dantran@gmail.com>
-------------------

September 29, 2005

Build share library on Unix

This example uses the default compiler(gcc) and linker(gcc) to build
a share library on Unix.  Note that user is responsible to specify
all the need option to make a share library at link time.
Complete example is
[here](http://svn.codehaus.org/mojo/trunk/mojo/maven-native/native-maven-plugin/src/it/linkages/linux-x86/libFuncDynamic).

```xml
<project>
   ...
  <packaging>a</packaging>
   ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <extensions>true</extensions>
        <configuration>
          <compilerStartOptions>
            <compilerStartOption>${commonCompilerOptions}</compilerStartOption>
          </compilerStartOptions>
          
          <sources>
            <source>
              <directory>${native.source.dir}</directory>
              <fileNames>
                <fileName>helloFunc.c</fileName>
              </fileNames>
            </source>  
            <source>
              <directory>${native.source.dir}/include</directory>
            </source>            
          </sources>
            
          <linkerStartOptions>
            <linkerStartOption>-shared</linkerStartOption>
          </linkerStartOptions>
                    
        </configuration>
        
      </plugin>

    </plugins>    

  </build>
</project>
```

