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

# Maven 2 Native Plugin - Buidl Lifecycle

Dan T. Tran  
<dantran@gmail.com>  
September 29, 2005

## Build Lifecycle

Each native packaging type has its own build lifecycle, declared in the plugin's `META-INF/plexus/components.xml`.
It consists of the following phases with the corresponding goals. The goals of the plugin are in the plugin
`org.codehaus.mojo:native-maven-plugin`; a goal that is bound to a phase runs there in every build with that
packaging.

|         Phase          |                             Goal                              |                                         Notes                                         |
|------------------------|---------------------------------------------------------------|---------------------------------------------------------------------------------------|
| initialize             | org.codehaus.mojo:native-maven-plugin:initialize              |                                                                                       |
| generate-sources       | org.codehaus.mojo:native-maven-plugin:unzipinc                |                                                                                       |
| generate-sources       | org.codehaus.mojo:native-maven-plugin:javah                   | `dll`, `so`, `sl`, `dylib` and `jnilib` only; deprecated, see below                   |
| compile                | org.codehaus.mojo:native-maven-plugin:resource-compile        | `dll` and `exe` only; enable it with the `resources` configuration                    |
| compile                | org.codehaus.mojo:native-maven-plugin:compile                 |                                                                                       |
| compile                | org.codehaus.mojo:native-maven-plugin:link                    |                                                                                       |
| compile                | org.codehaus.mojo:native-maven-plugin:manifest                | `dll` and `exe` only                                                                  |
| process-test-resources | org.apache.maven.plugins:maven-resources-plugin:testResources |                                                                                       |
| test-compile           | org.apache.maven.plugins:maven-compiler-plugin:testCompile    |                                                                                       |
| test                   | org.apache.maven.plugins:maven-surefire-plugin:test           |                                                                                       |
| package                | org.codehaus.mojo:native-maven-plugin:inczip                  | All packaging types except `exe`, `o` and `uexe`; enable it with `deployable` sources |
| install                | org.apache.maven.plugins:maven-install-plugin:install         |                                                                                       |
| deploy                 | org.apache.maven.plugins:maven-deploy-plugin:deploy           |                                                                                       |

The goals `ranlib` and `compile-message` are not part of any lifecycle. Bind them to a phase in an
`<execution>`, as the [static library example](./examples/static-lib-ranlib.html) does for `ranlib`.

The `javah` goal is deprecated and will be removed in the next major version, because the `javah` tool does not
exist in Java 10 and later. Generate the JNI headers with the `-h` option of `javac` instead; see the
[JNI example](./examples/jni-dll.html).

Add `<extensions>true</extensions>` to the plugin, as shown on the [usage page](./usage.html), so that Maven knows
the packaging types and their lifecycles.
