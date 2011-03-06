package org.codehaus.mojo.natives.c;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.Commandline;

public class CLinkerTest
    extends PlexusTestCase
{
    private CLinker linker;

    private LinkerConfiguration config;

    private static final File objectFile0 = new File( "source1.o" );

    private static final File objectFile1 = new File( "source2.o" );

    private List defautlObjectFiles;

    private String basedir;

    public void setUp()
        throws Exception
    {
        super.setUp();

        this.defautlObjectFiles = new ArrayList();
        this.defautlObjectFiles.add( objectFile0 );
        this.defautlObjectFiles.add( objectFile1 );

        this.linker = new CLinker();
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
        Commandline cl = this.getCommandline();

       	assertTrue( cl.getExecutable().endsWith("gcc") );

        assertEquals( basedir, cl.getWorkingDirectory().getPath() );

    }

    public void testOverrideLinkerExecutable()
        throws Exception
    {
        config.setExecutable( "ld" );

        Commandline cl = this.getCommandline();

       	assertTrue( cl.getExecutable().endsWith("ld") );

    }

    public void testObjectFileList()
        throws Exception
    {
        Commandline cl = this.getCommandline();

        assertTrue( cl.toString().indexOf( "source1.o source2.o" ) != -1 );

    }
    
    public void testLinkerResponseFile()
        throws Exception
    {
        this.config.setUsingLinkerResponseFile( true );
        this.config.setWorkingDirectory( new File( getBasedir(), "target" ) );
        Commandline cl = this.getCommandline();
        assertTrue( cl.toString().indexOf( "@objectsFile" ) != -1 );
    }
    

    public void testRelativeObjectFileList()
        throws Exception
    {
        ArrayList objectFiles = new ArrayList( 2 );
        objectFiles.add( new File( config.getOutputDirectory(), "file1.o" ) );
        objectFiles.add( new File( config.getOutputDirectory(), "file2.o" ) );

        Commandline cl = this.getCommandline( objectFiles );
        
        String cli = cl.toString();

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

        String cli = this.getCommandline().toString();
        
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
        
        String cli = this.getCommandline( new ArrayList( 0 ) ).toString();

        assertTrue( "Invalid external libraries settings: " + cli,
                    cli.indexOf( "-LtheLib -lfile1 -lfile2 -lfile3" ) != -1 );

    }

    /////////////////////////// HELPERS //////////////////////////////////////
    private Commandline getCommandline()
        throws NativeBuildException
    {
        return this.linker.createLinkerCommandLine( defautlObjectFiles, config );
    }

    private Commandline getCommandline( List objectFiles )
        throws NativeBuildException
    {
        return this.linker.createLinkerCommandLine( objectFiles, config );
    }

}
