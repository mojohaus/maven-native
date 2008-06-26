package org.codehaus.mojo.natives.javah;

import java.io.File;

import org.apache.commons.lang.StringUtils;
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

    public void testDefaultJavahExecutable()
        throws Exception
    {
        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand( config );

        File outputDir = new File( getBasedir(), "target/native" );

        assertEquals( "javah", cl.getExecutable() );
        assertTrue(  StringUtils.contains( cl.toString(),
                                            "javah -d " + outputDir.getPath() + " -classpath path1"
                                                + File.pathSeparator + "path2 className1 className2" ) );
    }

    public void testConfiguredJavahExecutable()
        throws Exception
    {
        File javaBin = new File( "/java/home/bin" );

        JavahExecutable javah = new JavahExecutable();
        config.setJavahPath( javaBin );
        Commandline cl = javah.createJavahCommand( config );

        File outputDir = new File( getBasedir(), "target/native" );

        assertEquals( javaBin.getAbsolutePath(), cl.getExecutable() );
        assertTrue( StringUtils.contains( cl.toString(),
                                            javaBin.getAbsolutePath() + " -d " + outputDir.getPath()
                                                + " -classpath path1" + File.pathSeparator
                                                + "path2 className1 className2" ) );

    }

    public void testJavahExecutableDashoOption()
        throws Exception
    {
        config.setFileName( "fileName" );
        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand( config );

        File outputFile = new File( getBasedir(), "target/native/" + "fileName" );
        assertTrue(  StringUtils.contains( cl.toString(),
                                            "javah -o " + outputFile.getPath() + " -classpath path1"
                                                + File.pathSeparator + "path2 className1 className2" ) );
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
