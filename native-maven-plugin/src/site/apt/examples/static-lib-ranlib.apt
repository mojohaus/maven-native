 ------
 Maven 2 Native Plugin - Static Library Example
 ------
 Dan T. Tran
 <dantran@gmail.com>
 ------
 September 29, 2005


Build static library including ranlib on Unix

  This example uses the default compiler(gcc) to build the source, then
  override the linkerProvider to have Unix 'ar' to build the static 
  library, after that apply Unix 'ranlib' on the output of 'ar' command.
  Complete example is
  {{{http://svn.codehaus.org/mojo/trunk/mojo/maven-native/native-maven-plugin/src/it/linkages/linux-x86/libFuncStatic}here}}.
  

-------------------
<project>
   ...
  <packaging>a</packaging>
   ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <extensions>true</extensions>
        <configuration>
          <compilerStartOptions>
            <compilerStartOption>${commonCompilerOptions}</compilerStartOption>
          </compilerStartOptions>
          
          <sources>
            <source>
              <directory>${native.source.dir}</directory>
              <fileNames>
                <fileName>helloSubFunc.c</fileName>
              </fileNames>
            </source>  
            <source>
              <directory>${native.source.dir}/include</directory>
            </source>            
          </sources>
            
          <linkerProvider>ar</linkerProvider>
          <linkerStartOptions>
            <linkerStartOption>-r</linkerStartOption>
          </linkerStartOptions>
          
        </configuration>

        <!-- ranlib the output of the linker, ie the static lib -->
        <executions>
          <execution>
            <id>ranlib</id>
            <phase>package</package>
            <goals>
              <goal>ranlib</goal>
            </goals>
          </execution>
        </executions>
      
      </plugin>

    </plugins>    

  </build>
    
</project>
-------------------

  
  
  


