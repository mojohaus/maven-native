package org.codehaus.mojo.natives.c;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

        assertEquals("gcc", cl.getLiteralExecutable() );
        assertEquals( basedir, cl.getWorkingDirectory().getPath() );

    }

    public void testOverrideLinkerExecutable()
        throws Exception
    {
        config.setExecutable( "ld" );

        Commandline cl = this.getCommandline();

        assertEquals("ld", cl.getLiteralExecutable() );

    }

    public void testObjectFileList()
        throws Exception
    {
        Commandline cl = this.getCommandline();

        int index = Arrays.asList(cl.getArguments()).indexOf("source1.o");
        assertTrue(index >= 0);
        assertEquals("source2.o", cl.getArguments()[index+1]);
    }

    public void testLinkerResponseFile()
        throws Exception
    {
        this.config.setUsingLinkerResponseFile( true );
        this.config.setWorkingDirectory( new File( getBasedir(), "target" ) );
        Commandline cl = this.getCommandline();
        
        assertTrue( Arrays.asList(cl.getArguments()).indexOf("@objectsFile") >= 0 );
    }

    public void testRelativeObjectFileList()
        throws Exception
    {
        ArrayList objectFiles = new ArrayList( 2 );
        objectFiles.add( new File( config.getOutputDirectory(), "file1.o" ) );
        objectFiles.add( new File( config.getOutputDirectory(), "file2.o" ) );

        Commandline cl = this.getCommandline( objectFiles );

        int index = Arrays.asList(cl.getArguments()).indexOf("target" + File.separator + "file1.o");
        assertTrue(index >= 0);
        assertEquals("target" + File.separator + "file2.o", cl.getArguments()[index+1]);

    }

    public void testOptions()
        throws Exception
    {
        String[] options = { "-o1", "-o2", "-o3" };
        config.setStartOptions( options );

        Commandline cl = this.getCommandline( );
        
        int index = Arrays.asList(cl.getArguments()).indexOf("-o1");
        assertTrue(index >= 0);
        assertEquals("-o2", cl.getArguments()[index + 1]);
        assertEquals("-o3", cl.getArguments()[index + 2]);

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

        Commandline cl = this.getCommandline( new ArrayList( 0 ) );

        int index = Arrays.asList(cl.getArguments()).indexOf("-LtheLib");
        assertTrue(index >= 0);
        assertEquals("-lfile1", cl.getArguments()[index + 1]);
        assertEquals("-lfile2", cl.getArguments()[index + 2]);
        assertEquals("-lfile3", cl.getArguments()[index + 3]);
    }

    // ///////////////////////// HELPERS //////////////////////////////////////
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
