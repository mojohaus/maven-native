package org.codehaus.mojo.natives.c;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;

import junit.framework.TestCase;

public class CLinkerTest
    extends TestCase
{
    private LinkerConfiguration config ;
    
    private String basedir;
    
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
        
        assertTrue( "Default linker is not gcc.",  cli.startsWith( "gcc") );
        assertEquals( basedir, properties.get( "workingDirectory" ).toString() );
        assertTrue( cli.indexOf( "-o " + config.getOutputFilePath() ) != -1  );
        
    }

    public void testOverrideLinkerExecutable()
        throws Exception
    {
        Linker linker = new CLinkerSimulator();
        
        config.setExecutable( "ld" );

        linker.link( config, new ArrayList( 0 ) );

        Properties properties = new Properties();

        properties.load( new FileInputStream( config.getOutputFilePath() ) );

        String cli = properties.get( "cli" ).toString();

        assertTrue( "Unable to change default linker.", cli.startsWith( "ld" ) );

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
        
        assertTrue( cli.indexOf( "file1.o file2.o" ) != -1  );
        
    }

    public void testOptions()
        throws Exception
    {
        String [] options = {"-o1", "-o2", "-o3" };
        config.setStartOptions(  options );

        Linker linker = new CLinkerSimulator();

        linker.link( config, new ArrayList(0) );

        Properties properties = new Properties();

        properties.load( new FileInputStream( config.getOutputFilePath() ) );

        String cli = properties.get( "cli" ).toString();

        assertTrue( cli.indexOf( "-o1 -o2 -o3" ) != -1 );

    }

    public void testExternalUnixLibraries()
        throws Exception
    {
        config.setExternalLibDirectory( new File ( "theLib" ) );
        
        List externalLibFileNames = new ArrayList();
     
        externalLibFileNames.add( "file0.lib" );

        externalLibFileNames.add( "file0.o" );

        externalLibFileNames.add( "file1.obj" );
        
        externalLibFileNames.add( "file1.so" );
        
        externalLibFileNames.add( "libfile2.so" );

        externalLibFileNames.add( "libfile3.a" );
        
        config.setExternalLibFileNames( externalLibFileNames );
        
        Linker linker = new CLinkerSimulator();

        linker.link( config, new ArrayList( 0 ) );

        Properties properties = new Properties();

        properties.load( new FileInputStream( config.getOutputFilePath() ) );

        String cli = properties.get( "cli" ).toString();

        assertTrue( "Invalid external libraries settings: " + cli, cli.indexOf( "-LtheLib -lfile1 -lfile2 -lfile3" ) != -1 );
        
    }
    
}
