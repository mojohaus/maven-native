 ------
 Maven 2 Native Plugin - Providers
 ------
 Dan T. Tran
 <dantran@gmail.com>
 ------
 September 29, 2005

Compiler/Linker Providers

*Compiler Provider

*-----------------+------------+-------------------------------------------+
| Provider        | Executable | Note
*-----------------+------------+-------------------------------------------+
| generic         | gcc        | Compiler with "-o " option ( gcc, Sun CC, hpux cc, etc )
*-----------------+------------+-------------------------------------------+
| generic-classic | gcc        | Compiler with "-o" option ( gcc, IBM xlc, etc )
*-----------------+------------+-------------------------------------------+
| msvc            | cl         |
*-----------------+------------+-------------------------------------------+
| bcc             | bcc32      |
*-----------------+------------+-------------------------------------------+
| mingw           | gcc        | Compiler with "-o " option, targeting MinGW
*-----------------+------------+-------------------------------------------+
| gnucobol        | cobc       | GNU COBOL compiler; sources are compiled to objects with "-c"
*-----------------+------------+-------------------------------------------+

*Linker Provider

*-----------------+------------+--------------------------------------------+
| Provider        | Executable | Note
*-----------------+------------+--------------------------------------------+
| generic         | gcc        | Linker with "-o " option ( gcc, Sun CC, hpux, cc, unix ld, etc )
*-----------------+------------+--------------------------------------------+
| generic-classic | gcc        | Linker with "-o" option ( gcc, IBM xlc, etc )
*-----------------+------------+--------------------------------------------+
| msvc            | link       |
*-----------------+------------+--------------------------------------------+
| bcc             | ilink32    | 
*-----------------+------------+--------------------------------------------+
| tlib            | tlib       |
*-----------------+------------+--------------------------------------------+
| mingw           | gcc        | Linker with "-o " option, targeting MinGW
*-----------------+------------+--------------------------------------------+
| gnucobol        | cobc       | GNU COBOL linker, see the build mode note below
*-----------------+------------+--------------------------------------------+

*GNU COBOL build mode

  The <<<gnucobol>>> linker passes no build-mode flag, so <<<cobc>>> applies its own
  default of <<<-m>>> and produces a dynamically loadable module. This is the inverse
  of the <<<gcc>>>-based providers, where the bare form produces an executable.

  To link an executable, pass <<<-x>>> through the linker start options:

+-----------------+
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>native-maven-plugin</artifactId>
  <extensions>true</extensions>
  <configuration>
    <compilerProvider>gnucobol</compilerProvider>
    <linkerProvider>gnucobol</linkerProvider>
    <linkerStartOptions>
      <linkerStartOption>-x</linkerStartOption>
    </linkerStartOptions>
  </configuration>
</plugin>
+-----------------+

*COPY dependency scanning

  Copybook dependencies are detected from <<<COPY>>> directives so that a change to a
  copybook forces recompilation of the sources that use it. The directive must be the
  first word on the line and may be quoted with single or double quotes, or left
  unquoted. Both source formats are handled: a sequence number in columns 1 to 6 is
  skipped, and a line whose indicator column holds "*" or "/" is treated as a comment.

  A name that cannot be resolved against the configured include paths is ignored, so an
  unresolved <<<COPY>>> never fails the build.
