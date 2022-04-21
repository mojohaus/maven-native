package org.codehaus.mojo.natives;

import java.io.File;
import java.util.List;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

public class NativeSourcesTest
    extends PlexusTestCase
{
    File workDirectory;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        workDirectory = new File( getBasedir(), "/target/NativeSourceTest" );
        if ( workDirectory.exists() )
        {
            FileUtils.deleteDirectory( workDirectory );
        }

        workDirectory.mkdirs();
    }

    public void testFileNamesOnly()
    {
        NativeSources source = new NativeSources();
        source.setDirectory( this.workDirectory );
        String[] fileNames = { "file1.c", "file2.c" };
        source.setFileNames( fileNames );

        List<File> files = source.getFiles();

        assertEquals( 2, files.size() );
    }

    public void testEmptyIncludes()
    {
        NativeSources source = new NativeSources();
        source.setDirectory( this.workDirectory );
        String[] fileNames = { "file1.c", "file2.c" };
        source.setFileNames( fileNames );

        String[] includes = { "*.c", "*.cpp" };

        source.setIncludes( includes );

        List<File> files = source.getFiles();

        assertEquals( 2, files.size() );
    }

    // merge include wild card list with fileNames array
    public void testMerge()
        throws Exception
    {
        NativeSources source = new NativeSources();
        source.setDirectory( this.workDirectory );
        String[] fileNames = { "file1.c", "file2.c" };
        source.setFileNames( fileNames );

        String[] includes = { "*.c", "*.cpp" };

        // this file is unique comparing to above list
        File nodupFile = new File( this.workDirectory, "file3.c" );
        nodupFile.createNewFile();

        source.setIncludes( includes );

        List<File> files = source.getFiles();

        assertEquals( 3, files.size() );
    }

    public void testDuplicateFiles()
        throws Exception
    {
        NativeSources source = new NativeSources();
        source.setDirectory( this.workDirectory );
        String[] fileNames = { "file1.c", "file2.c" };
        source.setFileNames( fileNames );

        String[] includes = { "*.c", "*.cpp" };

        File dupFile = new File( this.workDirectory, "file1.c" );
        dupFile.createNewFile();

        source.setIncludes( includes );

        List<File> files = source.getFiles();

        assertEquals( 2, files.size() );
    }

    public void testEmpty()
    {
        NativeSources source = new NativeSources();
        source.setDirectory( this.workDirectory );
        String[] includes = { "*.*" };
        source.setIncludes( includes );

        List<File> files = source.getFiles();

        assertEquals( 0, files.size() );
    }

    public void testGetAllSourceFiles()
        throws Exception
    {
        NativeSources source = new NativeSources();

        source.setDirectory( this.workDirectory );

        String[] includes = { "*.c", "*.cpp" };
        source.setIncludes( includes );

        String[] fileNames = { "file1.c", "file2.c" };
        source.setFileNames( fileNames );

        // this file is unique comparing to above list
        File nodupFile = new File( this.workDirectory, "file3.c" );
        nodupFile.createNewFile();

        NativeSources[] sources = { source, source };

        File[] files = NativeSources.getAllSourceFiles( sources );

        assertEquals( 6, files.length );
    }

    public void testEmptyFileNames()
        throws Exception
    {
        NativeSources source = new NativeSources();

        source.setDirectory( this.workDirectory );

        String[] includes = { "*.c", "*.cpp" };
        source.setIncludes( includes );

        File someFile = new File( this.workDirectory, "someFile.c" );
        someFile.createNewFile();

        List<File> files = source.getFiles();

        assertEquals( 1, files.size() );

    }

    public void testExcludes()
        throws Exception
    {

        new File( this.workDirectory, "someFile.c" ).createNewFile();
        new File( this.workDirectory, "someFile.cpp" ).createNewFile();

        NativeSources source = new NativeSources();

        source.setDirectory( this.workDirectory );

        String[] includes = { "*.*" };
        source.setIncludes( includes );
        assertEquals( 2, source.getFiles().size() );

        String[] excludes = { "*.cpp" };
        source.setExcludes( excludes );
        assertEquals( 1, source.getFiles().size() );

        String[] excludes2 = { "*.c" };
        source.setExcludes( excludes2 );
        assertEquals( 1, source.getFiles().size() );

        String[] excludes3 = { "someFile.c" };
        source.setExcludes( excludes3 );
        assertEquals( 1, source.getFiles().size() );

        String[] excludes4 = { "someFile.c", "someFile.cpp" };
        source.setExcludes( excludes4 );
        assertEquals( 0, source.getFiles().size() );

    }

}
