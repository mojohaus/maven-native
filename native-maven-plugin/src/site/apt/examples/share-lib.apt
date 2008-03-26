 ------
 Maven 2 Native Plugin - Share Library Example
 ------
 Dan T. Tran
 <dantran@gmail.com>
 ------
 September 29, 2005


Build share library on Unix

  This example uses the default compiler(gcc) and linker(gcc) to build
  a share library on Unix.  Note that user is responsible to specify
  all the need option to make a share library at link time.
  Complete example is
  {{{http://svn.codehaus.org/mojo/trunk/mojo/maven-native/native-maven-plugin/src/it/linkages/linux-x86/libFuncDynamic}here}}.
  

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
                <fileName>helloFunc.c</fileName>
              </fileNames>
            </source>  
            <source>
              <directory>${native.source.dir}/include</directory>
            </source>            
          </sources>
            
          <linkerStartOptions>
            <linkerStartOption>-shared</linkerStartOption>
          </linkerStartOptions>
                    
        </configuration>
        
      </plugin>

    </plugins>    

  </build>
</project>
-------------------

  
  
  


