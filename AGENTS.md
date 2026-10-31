# Agent guide for maven-native

This file is for automated agents and human contributors working on this repository. It records
what the code and the POMs do not make obvious.

## Modules

|                                   Module                                   |                                                Contents                                                |
|----------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| `maven-native-test`                                                        | Test utilities (`TestUtils`), published as a test-jar for the component modules                        |
| `maven-native-api`                                                         | Interfaces and base classes for compilers, linkers, `javah`, ranlib, manifests, and the source parsers |
| `maven-native-components`                                                  | Parent of the modules below                                                                            |
| `maven-native-manager`                                                     | The `*Manager` classes that look up an implementation by provider name                                 |
| `maven-native-bcc`, `-generic-c`, `-gnucobol`, `-javah`, `-mingw`, `-msvc` | One toolchain each, implementing the API                                                               |
| `native-maven-plugin`                                                      | The Maven plugin: the `native:*` mojos and the lifecycle mappings in `META-INF/plexus/components.xml`  |

The packaging types (`lib`, `a`, `so`, `dll`, `exe` and others) and their lifecycles are declared
in `native-maven-plugin/src/main/resources/META-INF/plexus/components.xml`, not in Java.

## Build gates

- `spotless-maven-plugin` from mojo-parent runs during `process-sources` in every module, and
  `sortpom-maven-plugin` sorts every POM. Run `mvn spotless:apply` before committing. Spotless
  formats Java with palantir-java-format and also reformats Markdown, including this file and the
  README.
- Only `native-maven-plugin` declares `<prerequisites><maven>3.9.0</maven>`. The modules compile
  against `mavenVersion` (3.9.16, set in the root POM).
- Fields of the mojo classes are plugin parameters; renaming one changes the plugin's
  configuration API.
- The project is MIT licensed, unlike most of MojoHaus. Keep each file's existing header.

## Unit tests

- JUnit 5 only, under each module's `src/test/java`. Several component tests need
  `maven-native-test` as a test-jar, so build with `-am` when you build one module:

  ```shell
  mvn -B verify -pl native-maven-plugin -am
  ```
- Mojo tests in `native-maven-plugin` use `@MojoTest` and `@InjectMojo` with a POM from
  `src/test/resources`. Those POMs select the `noop` provider, so the `Noop*` classes in
  `src/test/java` stand in for the real compilers and linkers.

## Integration tests

- `maven-invoker-plugin` runs them under the `run-its` profile of `native-maven-plugin/pom.xml`.
  mojo-parent only manages the plugin version; it defines no profile. The ITs call `gcc`, `ar` and
  `ranlib`, so they need those on the path:

  ```shell
  mvn -B -P run-its verify -pl native-maven-plugin -am
  mvn -B -P run-its verify -pl native-maven-plugin -am -DskipTests -Dinvoker.test=jni-linux
  ```
- Each IT is an aggregator directory directly under `native-maven-plugin/src/it`, with an
  `invoker.properties` that selects the OS family and a `verify.groovy` of plain `assert`s:

  |                IT                 |                        Runs on                        |                              Asserts                               |
  |-----------------------------------|-------------------------------------------------------|--------------------------------------------------------------------|
  | `linkages-linux`                  | Linux                                                 | static and dynamic builds link, and the executables print the text |
  | `jni-linux`                       | Linux                                                 | `javac -h` header, `libhello.so` exports the symbol, JNI test ran  |
  | `jni-macosx`                      | macOS                                                 | the same with `libhello.dylib`                                     |
  | `jni-windows`, `linkages-windows` | Windows with `VSnnnCOMNTOOLS` set (`selector.groovy`) | the same with MSVC                                                 |

  Their modules live in `src/it/jni` and `src/it/linkages` and are listed by relative path, together
  with the parent POMs: Maven 4 resolves the parents of reactor modules from the reactor only.

- The fixtures take the plugin version from `-Dnative.plugin.version`, which the invoker execution
  sets; set it yourself to build one by hand.

- JNI headers come from `javac -h` in the `hello` module. The `native:javah` goal needs a `javah`
  executable, which JDK 10 and later do not ship, so no IT runs it.

- On JDK 9 and later the `jdkIncludePath` default of `native:compile` (`${java.home}/../include`)
  points outside the JDK, so the JNI fixtures set it from `jdk.include.path`.

- The macOS and Windows ITs were written without access to those systems. Treat a failure there as a
  fixture problem first.

## Continuous integration

- `.github/workflows/maven.yml` calls the shared `maven-verify.yml` workflow of `mojohaus/.github`.
  Its default build goal is `-P "?run-its" verify`, so the ITs run on every matrix entry (Linux,
  macOS and Windows; JDK 8, 21 and 25 where the matrix does not exclude them).
- The site build runs `package`, not `compile`, because the `maven-native-generic-c` site needs
  the `maven-native-test` test-jar.
- Branches pushed to this repository build under the `push` event.

## Dependencies

- Dependabot updates are monthly and grouped, and it skips `native-maven-plugin/src/it` and
  `native-maven-plugin/src/test/resources`. Pins in `.github/dependabot.yml`: Maven core artifacts
  stay below 3.10, Checkstyle below 10, and `plexus-xml` below 4, because 4.x depends on the
  Maven 4 XML API.

## Pull requests

- Squash and rebase merges are enabled; merge commits are not. Release Drafter, through the shared
  `apache/maven-gh-actions-shared` workflow, builds the release notes from pull request titles
  and labels, so the title should describe the change for a user.

