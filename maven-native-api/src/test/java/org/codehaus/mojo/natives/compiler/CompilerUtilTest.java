package org.codehaus.mojo.natives.compiler;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.Os;

public class CompilerUtilTest
    extends PlexusTestCase
{

    public void testGetObjectFileFromSourceWithNoExtension()
        throws Exception
    {
        File source;

        if ( Os.isFamily( "windows" ) )
        {
            source = new File( "..\\dir1\\dir2\\fileWithoutExtenstion" );
        }
        else
        {
            source = new File( "../dir1/dir2/fileWithoutExtenstion" );
        }

        File objectFile = AbstractCompiler.getObjectFile( source, new File( "outputDirectory" ), null );

        assertEquals( new File( "outputDirectory/fileWithoutExtenstion." + AbstractCompiler.getObjectFileExtension( null ) ),
                      objectFile );
    }

    public void testGetObjectFileWithKnownExtension()
        throws Exception
    {
        
        File source = new File( "target/somefile.c" );

        File objectFile = AbstractCompiler.getObjectFile( source, new File( "outputDirectory" ), "someext");

        assertEquals( new File( "outputDirectory/somefile." + "someext" ),
                      objectFile );
    }
}
