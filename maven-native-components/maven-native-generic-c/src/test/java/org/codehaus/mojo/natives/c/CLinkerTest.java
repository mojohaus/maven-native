package org.codehaus.mojo.natives.c;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.Os;

public class CLinkerTest
    extends PlexusTestCase
{
    private LinkerConfiguration config;

    private String basedir;

    public void setUp()
        throws Exception
    {
        super.setUp();
        
        this.config = new LinkerConfiguration();
        this.basedir = getBasedir();
        config.setWorkingDirectory( new File( basedir ) );
        config.setOutputDirectory( new File( basedir, "target" ) );
        config.setOutputFileExtension( "exe" );
        config.setOutputFileName( "test" );
    }

    public void testDefaultLinkerExecutable()
        throws Exception
    {
        Properties properties = this.link( config, new ArrayList( 0 ) );

        String cli = properties.get( "cli" ).toString();

        assertTrue( "Default linker is not gcc.", cli.startsWith( "gcc" ) );
        assertEquals( basedir, properties.get( "workingDirectory" ).toString() );
        assertTrue( cli.indexOf( "-o " + config.getOutputFilePath() ) != -1 );

    }

    public void testOverrideLinkerExecutable()
        throws Exception
    {
        config.setExecutable( "ld" );

        Properties properties = this.link( config, new ArrayList( 0 ) );

        String cli = properties.get( "cli" ).toString();

        assertTrue( "Unable to change default linker.", cli.startsWith( "ld" ) );

    }

    public void testObjectFileList()
        throws Exception
    {
        ArrayList objectFiles = new ArrayList( 2 );
        objectFiles.add( new File( "file1.o" ) );
        objectFiles.add( new File( "file2.o" ) );

        Properties properties = this.link( config, objectFiles );

        String cli = properties.get( "cli" ).toString();

        assertTrue( cli.indexOf( "file1.o file2.o" ) != -1 );

    }

    public void testRelativeObjectFileList()
        throws Exception
    {
        ArrayList objectFiles = new ArrayList( 2 );
        objectFiles.add( new File( config.getOutputDirectory(), "file1.o" ) );
        objectFiles.add( new File( config.getOutputDirectory(), "file2.o" ) );

        Properties properties = this.link( config, objectFiles );
        
        String cli = properties.get( "cli" ).toString();

        System.out.println( cli.toString() );
        
        if ( Os.isFamily( "windows" ) )
        {
            assertTrue( cli.indexOf( "target\\file1.o target\\file2.o" ) != -1 );
        }
        else
        {
            assertTrue( cli.indexOf( "target/file1.o target/file2.o" ) != -1 );
        }

    }

    public void testOptions()
        throws Exception
    {
        String[] options = { "-o1", "-o2", "-o3" };
        config.setStartOptions( options );

        Properties properties = this.link( config, new ArrayList( 0 ) );

        String cli = properties.get( "cli" ).toString();

        assertTrue( cli.indexOf( "-o1 -o2 -o3" ) != -1 );

    }

    public void testExternalUnixLibraries()
        throws Exception
    {
        config.setExternalLibDirectory( new File( "theLib" ) );

        List externalLibFileNames = new ArrayList();

        externalLibFileNames.add( "file0.lib" );

        externalLibFileNames.add( "file0.o" );

        externalLibFileNames.add( "file1.obj" );

        externalLibFileNames.add( "file1.so" );

        externalLibFileNames.add( "libfile2.so" );

        externalLibFileNames.add( "libfile3.a" );

        config.setExternalLibFileNames( externalLibFileNames );

        Properties properties = this.link( config, new ArrayList( 0 ) );

        String cli = properties.get( "cli" ).toString();

        assertTrue( "Invalid external libraries settings: " + cli,
                    cli.indexOf( "-LtheLib -lfile1 -lfile2 -lfile3" ) != -1 );

    }

    private Properties link( LinkerConfiguration config, ArrayList objectFiles )
        throws Exception
    {
        Linker linker = new CLinkerSimulator();

        linker.link( config, objectFiles );

        Properties properties = new Properties();

        properties.load( new FileInputStream( config.getOutputFilePath() ) );
        
        return properties;
    }
    
}
