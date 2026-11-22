# MojoHaus Maven Native

[![The MIT License](https://img.shields.io/github/license/mojohaus/maven-native.svg?label=License)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.mojo/native-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/org.codehaus.mojo/native-maven-plugin)
[![Build Status](https://github.com/mojohaus/maven-native/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/mojohaus/maven-native/actions/workflows/maven.yml?query=branch%3Amaster)

The [native-maven-plugin](https://www.mojohaus.org/maven-native/native-maven-plugin/) compiles and links native
code, and packages the result (`exe`, `dll`, `so`, `a` and other types) as a Maven artifact. The repository also
holds the libraries behind it: the API, the toolchain providers for `gcc`, MSVC, BCC, MinGW and GNU COBOL, and the
provider manager.

The plugin requires Maven 3.9.0 or later and Java 8 or later, and it needs the compiler and linker of your
platform on the path. It defines its own lifecycle for each packaging type, so declare it with extensions:

```xml
<packaging>so</packaging>
...
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>native-maven-plugin</artifactId>
  <version><!-- latest release --></version>
  <extensions>true</extensions>
</plugin>
```

The plugin site has the [usage guide](https://www.mojohaus.org/maven-native/native-maven-plugin/usage.html) and
examples. Conventions for contributors and agents are in [AGENTS.md](AGENTS.md).
