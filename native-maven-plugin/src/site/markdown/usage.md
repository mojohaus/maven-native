 ------
 Native Maven Plugin: Usage
 ------
 Dan T. Tran
 <dantran@gmail.com>
 ------
 September 29, 2005

Usage

  The following contains summary of all available goals in native-maven-plugin.  They work together in a Maven custom build lifecycle

  * Determine your packaging type 
  
  * Determine your linker dependencies
      
  * Determine your include paths and source files
  
  * Determine your compiler and linker providers
  
  * Determine your compiler and linker arguments  
   
  []
  
* Packaging Type

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
  
  []
  
  One of these packaging types will go into the your pom.xml's packaging element.
  
  The native build lifecyle uses packaging type as the extension of 
  the build artifact( ie dll, exe, lib, etc ). However, it has minimal knowledge 
  to build each specific extension.  You must configure appropriate compiler/linker 
  options at build phase.
  
  "uexe" is a place holder for executable without extension name usually found
  on UNIX environment.  The final artifact will have extension of "uexe".  
  
  Since this plugin uses its own lifecycle, you need to enable plugin's <extensions> to true
  
  Here is example of declaring your packaging type and plugin header
  
----------------------
  
  <project>
   ...
   <packaging>so</packaging>
   ...
   <build>   
     <plugins>
       <plugin>
         <groupId>org.codehaus.mojo</groupId>
         <artifactId>native-maven-plugin</artifactId>
         <extensions>true</extensions>
         <configuration>
            ...
         </configuration>
       </plugin>
     </plugins>
   </build>
----------------------
      
* Dependencies

    * Dependencies with type <lib>, <so> and <a>, <sl>, <jnilib>, <dylib> <o>, and <obj>    
      will be linked to final artifact at link step.
    
    * Dependencies with type <inczip> are unpacked to be part of system include path at compile time.
      
    * You can explicitly add linker dependencies through normal linker option.
    
    []
    
    Internally, this plugin copies all dependencies to $\{project.build.directory}/lib
    without the version tag.  
    
    The following example shows 2 .so dependency files, and other files in
    linker option to be linked to the final output file.
  
----------------------
  
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
       <version>someVersion<version>
       <type>so</type>
     </dependency>
     <dependency>
       <groupId>someGroup</groupId>
       <artifactId>someId2</artifactId>
       <version>someVersion<version>
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
           ....
           <linkerStartOptions>
             <linkerStartOption>-shared -lxml2 -lz -lpthread -ldl</linkerStartOption> 
           </linkerStartOptions>
         </configuration>
       </plugin>
     </plugins>
   </build>
    
----------------------   
   
     
* Include paths and source files  

  Include paths and source files are configured using <Source> configuration.
  The following example shows a set of compilable source files consist of:
  
    * All <.cpp> files, <file1.c>, and <file2.c> under 
      <$\{basedir}/../src/main/native> directory excluding <somefile.cpp>
      
    * <file3.c> and <file4.c> under $\{basedir}/src/main/native>
   
   []
   
  Include paths consist of all \<{directory}\> under \<{sources}\>
  
------------------------

   <build>   
     <plugins>
       <plugin>
         <groupId>org.codehaus.mojo</groupId>
         <artifactId>native-maven-plugin</artifactId>
         <extensions>true</extensions>
         <configuration>
           .....
           <sources>
             <source>
               <-- relative to your project directory -->
               <directory>../src/main/native</directory>
               <!-- ant base wild card files>
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
               <!-- use this to have maven deploying header files with a .inzip type -->
               <!-- typically used to for library artifacts header files --->
               <deployable>true</deployable>
             </source>          

             <!-- additional system include path -->
             <source>
               <directory>...</directory>
               <dependencyAnalysisParticipation>false</dependencyAnalysisParticipation>
             </source>          
           </sources>
           ....         
         </configuration>
       </plugin>
     </plugins>
   </build>
------------------------

* Compiler/Linker Provider

  If you are on a UNIX system, use the default <generic>, or <generic-classic> but overwrite 
  <compilerExecutable> and <linkerExecutable> configuration specific to 
  your build. Default values for both generic compiler and linker are "gcc".
  Ensure the executables are on your path.
  
  On Windows, use Windows supported compiler and linker such as "msvc" and "bcc"
  
  Each provided compiler/linker provider assumes minimal knowledge of available options.
  The following contains a list of options that will be automatically injected to final 
  command lines:
  
    * /c for all compilers
    
    * Output options such as "-o" for generic compiler/linker,  "-Fo" for msvc compiler, 
      and "/out:" msvc linker, etc. 
      
    []
    
  Other notes:
  
    * Javah goal is invoked automatically at <generate-sources> phase for all shared library ( dll, so, etc ). See example for details.
      However, it is skipped when there is no <javahClassNames> available. 
  
    * Manifest goal is invoked right after linker step for MSVC that support manifest file ( ie Visual Studio 2005 and 2008 )
    
    * Concurrent compilations are supported via <numberOfConcurrentCompilation> configuration.
    
    
    []
    
    
    The following example use <generic-classic> with executable as <CC>.
    Note since we dont change the linker provider, it is will use the same
    name as the compiler provider name.
    
------------------------

   <build>   
     <plugins>
       <plugin>
          ...
         <configuration>
           <compilerProvider>generic-classic</compilerProvider>
           <compilerExecutable>CC</compilerExecutable>
           ....         
         </configuration>
       </plugin>
     </plugins>
   </build>
   
-----------------------        
  
* Compiler/Linker arguments 

  You can inject your specific compiler and linker arguments to the final compiler 
  and linker command lines via:
  
    * \<compilerStartOptions\>
    
    * \<compilerMiddleOptions\>
    
    * \<compilerEndOptions\>
    
    * \<linkerStartOptions\>
    
    * \<linkerMiddleOptions\>
    
    * \<linkerEndOptions\> 
    
    []
    
  Usually a pair of \<compilerStartOptions\> and \<linkerStartOptions\>
  should be sufficient.
  
  
  

