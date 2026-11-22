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

Dan T. Tran  
<dantran@gmail.com>  
September 29, 2005

## Build a MSVC DLL with JNI headers

The example builds a Windows DLL from native code that implements the `native` methods of a Java class. The JNI
header files are written by `javac` in the Java module, with its `-h` option, and are then part of the native
build. The `javah` goal of the plugin is deprecated, because the `javah` tool does not exist in Java 10 and later.

Note:

* User is responsible to provide all necessary compiler and linker options.
* `envFactoryName` is used to set up compiler and linker specific environments. This feature is extremely useful
  when you need to use various compilers in your build system and it is impossible to set up single global
  environment to accommodate all compilers. See [Environments](../envfactory.html).
* Check out the real examples in
  [the integration tests](https://github.com/mojohaus/maven-native/tree/master/native-maven-plugin/src/it/jni).
* On Java 8, the plugin finds the JDK `include` directory by itself. Since 1.2.0 it also finds it on Java 9 and
  later. Set `jdkIncludePath` if your JDK does not have the standard layout. `javahOS` names the subdirectory with
  `jni_md.h`, such as `win32`, `linux` or `darwin`.

First, the Java module writes the JNI header to a directory of its own:

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <compilerArgs>
      <arg>-h</arg>
      <arg>${project.build.directory}/native/javah</arg>
    </compilerArgs>
  </configuration>
</plugin>
```

Then the native module depends on the Java module and adds that directory to its include paths:

```xml
<project>
  ...
  <packaging>dll</packaging>

  <dependencies>
    <dependency>
      your jar which has jni interface.
    </dependency>
    <dependency>
      other native library ( .lib, .so, .a, .o, etc)
      to be linked in.
    </dependency>
    ...
  </dependencies>

  ...

  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <extensions>true</extensions>
        <configuration>
          <!-- add the jdk include directories to the system include path -->
          <javahOS>win32</javahOS>

          <!-- setup compiler and linker environment according to the vcvars batch file of the Visual Studio -->
          <!-- without this setting, you will need to setup the environment outside of Maven                -->
          <envFactoryName>org.codehaus.mojo.natives.msvc.MSVC2022x64EnvFactory</envFactoryName>

          <compilerProvider>msvc</compilerProvider>

          <compilerStartOptions>
            <compilerStartOption> /MD /W4 -O</compilerStartOption>
            <compilerStartOption>-D_WIN32_WINNT=0x0500 </compilerStartOption>
          </compilerStartOptions>

          <sources>
            <source>
              <!-- relative to your project directory -->
              <directory>../src/main/native</directory>
              <fileNames>
                <fileName>file1.c</fileName>
                <fileName>file2.c</fileName>
                <fileName>...</fileName>
              </fileNames>
            </source>
            <source>
              <directory>src/main/native</directory>
              <fileNames>
                <fileName>file3.c</fileName>
                <fileName>file4.c</fileName>
                <fileName>...</fileName>
              </fileNames>
            </source>

            <!-- the JNI header written by javac -h in the Java module -->
            <source>
              <directory>../java/target/native/javah</directory>
            </source>

            <!-- additional system include path -->
            <source>
              <directory>...</directory>
              <dependencyAnalysisParticipation>false</dependencyAnalysisParticipation>
            </source>
          </sources>

          <!-- deploy the accompanying .lib file as well -->
          <linkerSecondaryOutputExtensions>lib</linkerSecondaryOutputExtensions>

          <linkerStartOptions>
            <linkerStartOption> /INCREMENTAL:NO /DLL user32.lib advapi32.lib oldnames.lib kernel32.lib </linkerStartOption>
          </linkerStartOptions>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

