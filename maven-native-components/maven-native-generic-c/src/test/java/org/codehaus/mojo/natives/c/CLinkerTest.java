package org.codehaus.mojo.natives.c;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;

import junit.framework.TestCase;

public class CLinkerTest
    extends TestCase
{
    LinkerConfiguration config ;
    
    String basedir;
    
    public void setUp()
    {
        this.config = new LinkerConfiguration();
        this.basedir = System.getProperty( "basedir" );
        config.setWorkingDirectory( new File( basedir ) );
        config.setOutputDirectory( new File ( basedir, "target") );
        config.setOutputFileExtension( "exe" );
        config.setOutputFileName( "test" );
    }
    
    
    public void testDefaultLinkerExecutable()
        throws Exception
    {
        Linker linker = new CLinkerSimulator();
        
        linker.link( config, new ArrayList(0) );
        
        Properties properties = new Properties();
        
        properties.load( new FileInputStream( config.getOutputFilePath() ) );
        
        String cli = properties.get( "cli" ).toString();
        assertTrue( cli.startsWith( "gcc") );
        assertEquals( basedir, properties.get( "workingDirectory" ).toString() );
        assertTrue( cli.contains( "-o " + config.getOutputFilePath() ) );
        
    }

    public void testObjectFileList()
        throws Exception
    {           
        ArrayList objectFiles = new ArrayList(2);
        objectFiles.add ( new File( "file1.o" ) );
        objectFiles.add ( new File( "file2.o" ) );
                
        Linker linker = new CLinkerSimulator();
        
        linker.link( config, objectFiles );
        
        Properties properties = new Properties();
        
        properties.load( new FileInputStream( config.getOutputFilePath() ) );
        
        String cli = properties.get( "cli" ).toString();
        
        assertTrue( cli.contains( "file1.o file2.o" ) );
        
    }

}
