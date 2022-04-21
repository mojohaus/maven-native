package org.codehaus.mojo.natives.javah;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;
import static org.junit.Assert.*;

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

    public void testDefaultJavahExecutable()
    {
        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand( config );

        File outputDir = new File( getBasedir(), "target/native" );

        assertEquals( "javah", cl.getLiteralExecutable() );
        assertArrayEquals( new String[] { "-d", outputDir.getAbsolutePath(), "-classpath",
                "path1" + File.pathSeparator + "path2", "className1", "className2" }, cl.getArguments() );
    }

    public void testConfiguredJavahExecutable()
    {
        File javaBin = new File( "/java/home/bin" );

        JavahExecutable javah = new JavahExecutable();
        config.setJavahPath( javaBin );
        Commandline cl = javah.createJavahCommand( config );

        File outputDir = new File( getBasedir(), "target/native" );

        assertEquals( javaBin.getAbsolutePath(), cl.getLiteralExecutable() );
        assertArrayEquals( new String[] { "-d", outputDir.getAbsolutePath(), "-classpath",
                "path1" + File.pathSeparator + "path2", "className1", "className2" }, cl.getArguments() );
    }

    public void testJavahExecutableDashoOption()
    {
        config.setFileName( "fileName" );
        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand( config );

        File outputFile = new File( getBasedir(), "target/native/" + "fileName" );
        assertArrayEquals( new String[] { "-o", outputFile.getAbsolutePath(), "-classpath",
                "path1" + File.pathSeparator + "path2", "className1", "className2" }, cl.getArguments() );
    }

    public void testWorkingDirectory()
    {
        JavahExecutable javah = new JavahExecutable();

        File workingDirectory = new File( getBasedir() );
        config.setWorkingDirectory( workingDirectory );

        Commandline cl = javah.createJavahCommand( config );

        assertEquals( workingDirectory, cl.getWorkingDirectory() );
    }
}
