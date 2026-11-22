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

# Native Maven Plugin: Usage

Dan T. Tran  
<dantran@gmail.com>  
September 29, 2005

## Usage

The following contains summary of all available goals in native-maven-plugin. They work together in a Maven
custom build lifecycle, see [Build Lifecycle](./lifecycle.html). To use the plugin:

* Determine your packaging type
* Determine your linker dependencies
* Determine your include paths and source files
* Determine your compiler and linker providers
* Determine your compiler and linker arguments

The plugin requires Maven 3.9.0 or later and Java 8 or later.

## Packaging Type

Native plugin supports the following packaging types:

* dll
* exe
* lib
* a
* o
* so
* sl
* dylib
* jnilib
* uexe

One of these packaging types will go into your pom.xml's `<packaging>` element.

The native build lifecycle uses the packaging type as the extension of the build artifact (ie dll, exe, lib, etc).
However, it has minimal knowledge to build each specific extension. You must configure appropriate compiler/linker
options at build phase.

"uexe" is a place holder for an executable without an extension, usually found on UNIX environments. The final
artifact will have the extension "uexe".

Since this plugin uses its own lifecycle, you need to set the plugin's `<extensions>` to `true`.

Here is an example of declaring your packaging type and plugin header:

```xml
<project>
  ...
  <packaging>so</packaging>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <version><!-- latest release --></version>
        <extensions>true</extensions>
        <configuration>
          ...
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

## Dependencies

* Dependencies with type `lib`, `so`, `a`, `sl`, `jnilib`, `dylib`, `o` and `obj` will be linked to the final
  artifact at link step.
* Dependencies with type `inczip` are unpacked to be part of the system include path at compile time.
* You can explicitly add linker dependencies through the normal linker options.

Internally, this plugin copies all dependencies to `${project.build.directory}/lib` without the version tag. The
directory is set by `externalLibDirectory`.

The following example shows 2 .so dependency files, an `inczip` dependency, and other files in the linker options
to be linked to the final output file.

```xml
<project>
  ...
  <dependencies>
    <dependency>
      <groupId>someGroup</groupId>
      <artifactId>someId</artifactId>
      <version>someVersion</version>
      <type>so</type>
    </dependency>
    <dependency>
      <groupId>someGroup</groupId>
      <artifactId>someId2</artifactId>
      <version>someVersion</version>
      <type>so</type>
    </dependency>
    <dependency>
      <groupId>someGroup</groupId>
      <artifactId>someId2</artifactId>
      <version>someVersion</version>
      <type>inczip</type>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <extensions>true</extensions>
        <configuration>
          ...
          <linkerStartOptions>
            <linkerStartOption>-shared -lxml2 -lz -lpthread -ldl</linkerStartOption>
          </linkerStartOptions>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

## Include paths and source files

Include paths and source files are configured using `<sources>`, a list of `<source>` elements. The following
example shows a set of compilable source files consisting of:

* All `.cpp` files, `file1.c` and `file2.c` under the `${basedir}/../src/main/native` directory, excluding
  `somefile.cpp`
* `file3.c` and `file4.c` under `${basedir}/src/main/native`

Include paths consist of all `<directory>` elements under `<sources>`.

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>native-maven-plugin</artifactId>
      <extensions>true</extensions>
      <configuration>
        ...
        <sources>
          <source>
            <!-- relative to your project directory -->
            <directory>../src/main/native</directory>
            <!-- ant style wild cards -->
            <includes>
              <include>**/*.cpp</include>
            </includes>
            <excludes>
              <exclude>somefile.cpp</exclude>
            </excludes>
            <fileNames>
              <fileName>file1.c</fileName>
              <fileName>file2.c</fileName>
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

          <!-- additional include path -->
          <source>
            <directory>...</directory>
            <!-- use this to have maven deploying header files with an inczip type -->
            <!-- typically used for the header files of library artifacts -->
            <deployable>true</deployable>
          </source>

          <!-- additional system include path -->
          <source>
            <directory>...</directory>
            <dependencyAnalysisParticipation>false</dependencyAnalysisParticipation>
          </source>
        </sources>
        ...
      </configuration>
    </plugin>
  </plugins>
</build>
```

## Compiler/Linker Provider

If you are on a UNIX system, use the default `generic`, or `generic-classic`, but override `compilerExecutable` and
`linkerExecutable` with the executables specific to your build. Default values for both generic compiler and
linker are "gcc". Ensure the executables are on your path. See [Providers](./providers.html) for the list.

On Windows, use Windows supported compiler and linker such as "msvc" and "bcc".

Each provided compiler/linker provider assumes minimal knowledge of available options. The following contains a
list of options that will be automatically injected to final command lines:

* `/c` for all compilers
* Output options such as "-o" for generic compiler/linker, "-Fo" for msvc compiler, and "/out:" for msvc linker, etc.

Other notes:

* The javah goal is invoked automatically at the `generate-sources` phase for all shared library packaging types
  (dll, so, etc). However, it is skipped when there is no `javahClassNames` available. The goal is deprecated and
  removed in the next major version, because the `javah` tool does not exist in Java 10 and later. Set
  `headerOutputDirectory` of the `maven-compiler-plugin` (the `-h` option of `javac`) to generate the JNI headers
  instead. See the [JNI example](./examples/jni-dll.html).
* The manifest goal is invoked right after the linker step for MSVC that supports manifest files (ie Visual Studio
  2005 and 2008).
* Concurrent compilations are supported via the `numberOfConcurrentCompilation` configuration.

The following example uses `generic-classic` with the executable `CC`. Note that since we do not change the linker
provider, it will use the same name as the compiler provider.

```xml
<build>
  <plugins>
    <plugin>
      ...
      <configuration>
        <compilerProvider>generic-classic</compilerProvider>
        <compilerExecutable>CC</compilerExecutable>
        ...
      </configuration>
    </plugin>
  </plugins>
</build>
```

## Compiler/Linker arguments

You can inject your specific compiler and linker arguments to the final compiler and linker command lines via:

* `<compilerStartOptions>`
* `<compilerMiddleOptions>`
* `<compilerEndOptions>`
* `<linkerStartOptions>`
* `<linkerMiddleOptions>`
* `<linkerEndOptions>`

Usually a pair of `<compilerStartOptions>` and `<linkerStartOptions>` should be sufficient.

## Skipping the plugin

Set `-Dnative.skip=true` to skip the goals of the plugin for a build.
