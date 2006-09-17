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

        config.setDestDir( new File( getBasedir() ) );

    }

    public void testJavahExecutable()
        throws Exception
    {
        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand( config );

        assertEquals( "javah", cl.getExecutable() );
        assertEquals( "javah -d " + getBasedir() + " -classpath path1" + File.pathSeparator
            + "path2 className1 className2", cl.toString() );
    }

    public void testJavahExecutableDashoOption()
        throws Exception
    {
        config.setFileName( "fileName" );
        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand( config );

        assertEquals( "javah -o " + getBasedir() + File.separator + "fileName " + "-classpath path1"
            + File.pathSeparator + "path2 className1 className2", cl.toString() );
    }
}
