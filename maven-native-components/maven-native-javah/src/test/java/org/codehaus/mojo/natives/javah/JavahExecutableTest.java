package org.codehaus.mojo.natives.javah;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;

public class JavahExecutableTest
    extends PlexusTestCase
{
    private JavahConfiguration config;

    public void setUp()
        throws Exception
    {
        super.setUp();

        this.config = new JavahConfiguration();

        String[] classPaths = { "path1", "path2" };
        config.setClassPaths( classPaths );

        String[] classNames = { "className1", "className2" };
        config.setClassNames( classNames );

        config.setOutputDirectory( new File( getBasedir(), "target/native" ) );

    }

    public void testJavahExecutable()
        throws Exception
    {
        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand( config );

        File outputDir = new File( getBasedir(), "target/native" );
        
        assertEquals( "javah", cl.getExecutable() );
        assertEquals( "javah -d " + outputDir.getPath() + " -classpath path1" + File.pathSeparator
            + "path2 className1 className2", cl.toString() );
    }

    public void testJavahExecutableDashoOption()
        throws Exception
    {
        config.setFileName( "fileName" );
        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand( config );

        File outputFile = new File( getBasedir(), "target/native/" + "fileName" );
        assertEquals( "javah -o " + outputFile.getPath() + " -classpath path1"
            + File.pathSeparator + "path2 className1 className2", cl.toString() );
    }

    public void testWorkingDirectory()
        throws Exception
    {
        JavahExecutable javah = new JavahExecutable();
        
        File workingDirectory = new File( getBasedir() );
        config.setWorkingDirectory( workingDirectory );
        
        Commandline cl = javah.createJavahCommand( config );
        
        assertEquals( workingDirectory, cl.getWorkingDirectory() );
    }
}
