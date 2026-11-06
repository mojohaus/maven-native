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
-------------------

September 29, 2005

Build Lifecycle

The native build lifecycle consists of the following phases with corresponding goals:

*-----------------------+---------------------------------------------------------------+------------+
| <Phase>               | <Goal>                                                        | Notes
*-----------------------+---------------------------------------------------------------+------------+
| initialize            |org.codehaus.mojo:native-maven-plugin:initialize               |
*-----------------------+---------------------------------------------------------------+------------+
| generate-sources      | org.codehaus.mojo:native-maven-plugin:unzipinc                |
*-----------------------+---------------------------------------------------------------+------------+
| generate-sources      | org.codehaus.mojo:native-maven-plugin:javah                   | all shared libraries custom build lifecycle
*-----------------------+---------------------------------------------------------------+------------+
| compile               | org.codehaus.mojo:native-maven-plugin:resource-compile        | enable via resources configuration, dll and exe only
*-----------------------+---------------------------------------------------------------+------------+
| compile               | org.codehaus.mojo:native-maven-plugin:compile                 |
*-----------------------+---------------------------------------------------------------+------------+
| compile               | org.codehaus.mojo:native-maven-plugin:link                    |
*-----------------------+---------------------------------------------------------------+------------+
| compile               | org.codehaus.mojo:native-maven-plugin:manifest                | ( dll and exe only )
*-----------------------+---------------------------------------------------------------+------------+
| process-test-resources| org.apache.maven.plugins:maven-resources-plugin:testResources |
*-----------------------+---------------------------------------------------------------+------------+
| test-compile          | org.apache.maven.plugins:maven-compiler-plugin:testCompile    |
*-----------------------+---------------------------------------------------------------+------------+
| test                  | org.apache.maven.plugins:maven-surefire-plugin:test           |
*-----------------------+---------------------------------------------------------------+------------+
| package               | org.codehaus.mojo:native-maven-plugin:inczip                  | enable via NativeSources
*-----------------------+---------------------------------------------------------------+------------+
| install               | org.apache.maven.plugins:maven-install-plugin:install         |
*-----------------------+---------------------------------------------------------------+------------+
| deploy                | org.apache.maven.plugins:maven-deploy-plugin:deploy           |
*-----------------------+---------------------------------------------------------------+------------+
