 ------
 Introduction
 ------
 Dan T. Tran
 <dantran@gmail.com>
 ------
 2010-05-22
 ------

Native Maven Plugin

  Use this plugin to compile c and c++ source under Maven 2 with  
  compilers such as gcc, msvc, gcj etc ... 

* Usage
  
  Instructions on how to use the Native Maven Plugin can be found on the {{{./usage.html}usage page}}.
  
* Goals Overview

  The following contains summaries of all main goals. They work together in {{{./lifecycle.html}custom build lifecycle }}.

  * {{{./initialize-mojo.html}native:initialize}}: Initialize build lifecycle 
  
  * {{{./unzipinc-mojo.html}native:unzipinc}}: Unpack dependencies with .inczip type to be included in include path at compile time
   
  * {{{./compile-mojo.html}native:compile}}: Compile source files into native object files 

  * {{{./inczip-mojo.html}native:inczip}}: Bundle header files to be installed/deployed 
  
  * {{{./link-mojo.html}native:link}}: Link all previously built object and dependent library files into final build artifact 
  
  * {{{./javah-mojo.html}native:javah}}: Generate JNI include files based on a set of class names
   
  * {{{./ranlib-mojo.html}native:ranlib}}: ranlib a Unix linker output file 
  
  * {{{./resource-compile-mojo.html}native:resource-compile}}: Compile Windows resource files 
  
  * {{{./compile-message-mojo.html}native:compile-message}}: Compile Windows message files 

  * {{{./manifest-mojo.html}native:manifest}}: Build native manifest ( msvc only ) 
  
  []
  
  
* Examples

  * Buildable examples can be found in 
  {{{http://svn.codehaus.org/mojo/trunk/mojo/maven-native/native-maven-plugin/src/it/}SCM}}

  * {{{./examples/jni-dll.html}Build DLL with JNI}}.

  * {{{./examples/static-lib-ranlib.html}Build static library including ranlib}}.
  
  * {{{./examples/share-lib.html}Build share library on Unix}}.
  
