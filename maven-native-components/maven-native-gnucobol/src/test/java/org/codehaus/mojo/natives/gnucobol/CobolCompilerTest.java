package org.codehaus.mojo.natives.gnucobol;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;

import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;

public class CobolCompilerTest
    extends PlexusTestCase
{
    private GNUCOBOLCompiler compiler;

    private CompilerConfiguration config;

    private static File sourceFile = new File( "source.cob" );

    private static File objectFile = new File( "object.o" );

    private static String[] simpleArgv = { "-o", "object.o", "-c", "source.cob" };

    public void setUp()
        throws Exception
    {
        super.setUp();

        this.compiler = new GNUCOBOLCompiler();
        this.config = new CompilerConfiguration();
    }

    public void testSimpleCompilation()
        throws Exception
    {
        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );
        assertArrayEquals( new String[] { "cobc", simpleArgv[0], simpleArgv[1], simpleArgv[2], simpleArgv[3] },
                cl.getRawCommandline() );
    }

    public void testNonDefaultExecutable()
        throws Exception
    {
        this.config.setExecutable( "cobol" );
        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );
        assertArrayEquals( new String[] { "cobol", simpleArgv[0], simpleArgv[1], simpleArgv[2], simpleArgv[3] },
                cl.getRawCommandline() );
    }

    public void testStartOptions()
        throws Exception
    {
        String[] startOptions = { "-s1", "-s2" };
        config.setStartOptions( startOptions );

        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );

        assertArrayEquals(
                new String[] { "cobc", "-s1", "-s2", simpleArgv[0], simpleArgv[1], simpleArgv[2], simpleArgv[3] },
                cl.getRawCommandline() );
    }

    public void testIncludePaths()
        throws Exception
    {
        File[] includePaths = { new File( "p1" ), new File( "p2" ) };

        config.setIncludePaths( includePaths );

        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );

        assertArrayEquals(
                new String[] { "cobc", "-Ip1", "-Ip2", simpleArgv[0], simpleArgv[1], simpleArgv[2], simpleArgv[3] },
                cl.getRawCommandline() );
    }

    public void testSystemIncludePaths()
        throws Exception
    {
        File[] includePaths = { new File( "p1" ), new File( "p2" ) };

        File[] systemIncludePaths = { new File( "sp1" ), new File( "sp2" ) };

        config.setIncludePaths( includePaths );

        config.setSystemIncludePaths( systemIncludePaths );

        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );

        assertArrayEquals( new String[] { "cobc", "-Ip1", "-Ip2", "-Isp1", "-Isp2", simpleArgv[0], simpleArgv[1],
                simpleArgv[2], simpleArgv[3] }, cl.getRawCommandline() );
    }

    public void testMiddleOptions()
        throws Exception
    {
        File[] includePaths = { new File( "p1" ), new File( "p2" ) };
        config.setIncludePaths( includePaths );

        String[] startOptions = { "-s1", "-s2" };
        String[] middleOptions = { "-m1", "-m2" };
        config.setStartOptions( startOptions );
        config.setMiddleOptions( middleOptions );

        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );

        assertArrayEquals( new String[] { "cobc", "-s1", "-s2", "-Ip1", "-Ip2", "-m1", "-m2", simpleArgv[0],
                simpleArgv[1], simpleArgv[2], simpleArgv[3] }, cl.getRawCommandline() );
    }

    public void testEndOptions()
        throws Exception
    {
        File[] includePaths = { new File( "p1" ), new File( "p2" ) };
        config.setIncludePaths( includePaths );

        String[] startOptions = { "-s1", "-s2" };
        String[] middleOptions = { "-m1", "-m2" };
        String[] endOptions = { "-e1", "-e2" };
        config.setStartOptions( startOptions );
        config.setMiddleOptions( middleOptions );
        config.setEndOptions( endOptions );

        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );

        assertArrayEquals( new String[] { "cobc", "-s1", "-s2", "-Ip1", "-Ip2", "-m1", "-m2", simpleArgv[0],
                simpleArgv[1], simpleArgv[2], simpleArgv[3], "-e1", "-e2" }, cl.getRawCommandline() );
    }
}
