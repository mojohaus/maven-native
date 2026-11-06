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

# Introduction

Native Maven Plugin

Use this plugin to compile c and c++ source under Maven 2 with  
compilers such as gcc, msvc, gcj etc ...

## Usage

Instructions on how to use the Native Maven Plugin can be found on the [usage page](./usage.html).

## Goals Overview

The following contains summaries of all main goals. They work together in [custom build lifecycle](./lifecycle.html).

* [native:initialize](./initialize-mojo.html): Initialize build lifecycle

* [native:unzipinc](./unzipinc-mojo.html): Unpack dependencies with .inczip type to be included in include path at compile time

* [native:compile](./compile-mojo.html): Compile source files into native object files

* [native:inczip](./inczip-mojo.html): Bundle header files to be installed/deployed

* [native:link](./link-mojo.html): Link all previously built object and dependent library files into final build artifact

* [native:javah](./javah-mojo.html): Generate JNI include files based on a set of class names

* [native:ranlib](./ranlib-mojo.html): ranlib a Unix linker output file

* [native:resource-compile](./resource-compile-mojo.html): Compile Windows resource files

* [native:compile-message](./compile-message-mojo.html): Compile Windows message files

* [native:manifest](./manifest-mojo.html): Build native manifest ( msvc only )

## Examples

* Buildable examples can be found in
  [SCM](http://svn.codehaus.org/mojo/trunk/mojo/maven-native/native-maven-plugin/src/it/)

* [Build DLL with JNI](./examples/jni-dll.html).

* [Build static library including ranlib](./examples/static-lib-ranlib.html).

* [Build share library on Unix](./examples/share-lib.html).

