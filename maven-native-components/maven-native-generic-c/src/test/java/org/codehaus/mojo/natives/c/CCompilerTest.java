package org.codehaus.mojo.natives.c;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;

public class CCompilerTest
    extends PlexusTestCase
{
    private CCompiler compiler;

    private CompilerConfiguration config;

    private static File sourceFile = new File( "source.c" );

    private static File objectFile = new File( "object.o" );

    private static String simpleArgv = "-o object.o -c source.c";

    public void setUp()
        throws Exception
    {
        super.setUp();

        this.compiler = new CCompiler();
        this.config = new CompilerConfiguration();
    }

    public void testSimpleCompilation()
        throws Exception
    {
        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );
        assertTrue( StringUtils.contains( cl.toString(), "gcc " + simpleArgv ));
    }

    public void testNonDefaultExecutable()
        throws Exception
    {
        this.config.setExecutable( "cc" );
        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );
        assertTrue(  StringUtils.contains( cl.toString(), "cc " + simpleArgv));
    }

    public void testStartOptions()
        throws Exception
    {
        String[] startOptions = { "-s1", "-s2" };
        config.setStartOptions( startOptions );

        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );

        assertTrue(  StringUtils.contains( cl.toString(), "gcc -s1 -s2 " + simpleArgv ));
    }

    public void testIncludePaths()
        throws Exception
    {
        File[] includePaths = { new File( "p1" ), new File( "p2" ) };

        config.setIncludePaths( includePaths );

        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );

        assertTrue(  StringUtils.contains( cl.toString(), "gcc -Ip1 -Ip2 " + simpleArgv ));
    }

    public void testSystemIncludePaths()
        throws Exception
    {
        File[] includePaths = { new File( "p1" ), new File( "p2" ) };

        File[] systemIncludePaths = { new File( "sp1" ), new File( "sp2" ) };

        config.setIncludePaths( includePaths );

        config.setSystemIncludePaths( systemIncludePaths );

        Commandline cl = compiler.getCommandLine( sourceFile, objectFile, config );

        assertTrue(  StringUtils.contains( cl.toString(), "gcc -Ip1 -Ip2 -Isp1 -Isp2 " + simpleArgv ));
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

        assertTrue(  StringUtils.contains( cl.toString(), "gcc -s1 -s2 -Ip1 -Ip2 -m1 -m2 " + simpleArgv ) );
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

        assertTrue(  StringUtils.contains( cl.toString(), "gcc -s1 -s2 -Ip1 -Ip2 -m1 -m2 " + simpleArgv + " -e1 -e2" ));
    }
}
