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

# Maven 2 Native Plugin - EnvFactory

Dan T. Tran  
<dantran@gmail.com>  
September 29, 2005

## Environment Factories

Instead of setting up your compiler/linker command line environment prior
running maven to build your artifact, you can use `<envFactoryName>` to
configure one of the following built-in environments. The value is the fully
qualified class name:

* `org.codehaus.mojo.natives.msvc.Community2017x64EnvFactory`
* `org.codehaus.mojo.natives.msvc.Community2017x86EnvFactory`
* `org.codehaus.mojo.natives.msvc.Community2017x86AMD64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC6EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2003EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2003ToolkitEnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2005AMD64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2005x86EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2005x86AMD64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2008x86EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2008x86AMD64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2010x86EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2010x86AMD64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2012x86EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2012x86AMD64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2013x86EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2013x86AMD64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2019x86EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2019x64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2019x86AMD64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2019AMD64x86EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2022x86EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2022x64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2022x86AMD64EnvFactory`
* `org.codehaus.mojo.natives.msvc.MSVC2022AMD64x86EnvFactory`
* `org.codehaus.mojo.natives.bcc.BCCEnvFactory`

This feature is extremely useful when you have a need to use various compilers
in your build system and it is impossible to set up single
global environment to accommodate all compilers.

The following example shows how to configure MSVC 2022

```xml
<project>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <extensions>true</extensions>
        <configuration>
          <compilerProvider>msvc</compilerProvider>
          <envFactoryName>org.codehaus.mojo.natives.msvc.MSVC2022x64EnvFactory</envFactoryName>
          ...
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

To create your own environment factory, package your own implementation of the
[EnvFactory interface](https://github.com/mojohaus/maven-native/blob/master/maven-native-api/src/main/java/org/codehaus/mojo/natives/EnvFactory.java)
into a jar, and have the jar as this plugin's dependency. The class needs a
public constructor without arguments, and `envFactoryName` is its fully qualified name.

```xml
<project>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <extensions>true</extensions>
        <dependencies>
          <dependency>
            your EnvFactory package
          </dependency>
        </dependencies>
        <configuration>
          <envFactoryName>com.example.MyEnvFactory</envFactoryName>
          ...
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

