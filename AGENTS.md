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

- There are none that the build runs. The POMs under `native-maven-plugin/src/it` (`jni`,
  `linkages`, `staticlib`) are not wired to `maven-invoker-plugin`, there is no `run-its`
  profile, and they have no `verify.groovy`. They use JUnit 4 and an old `mojo-parent`, and
  target an old plugin snapshot, so treat them as examples. Do not update them.

## Continuous integration

- `.github/workflows/maven.yml` calls the shared `maven-verify.yml@v5` workflow from
  `apache/maven-gh-actions-shared`, with `verify` as the build goal. It does not use `-P run-its`
  because Maven 4 fails on a requested profile that does not exist.
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

