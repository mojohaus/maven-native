package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;

public class NativeCompileMojoTest
    extends AbstractMojoTestCase
{
    public void testMojoLookup()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/compiler/plugin-config.xml" );
        NativeCompileMojo mojo = (NativeCompileMojo) lookupMojo( "compile", pluginXml );
        assertNotNull( mojo );
    }

    public void testSources()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/compiler/plugin-config.xml" );
        NativeCompileMojo mojo = (NativeCompileMojo) lookupMojo( "compile", pluginXml );
        assertNotNull( mojo );
        
        //must init this
        mojo.setPluginContext( new HashMap() );
        
        //simulate extra path to be added into includePath
        mojo.getProject().addCompileSourceRoot( "dummy" );
        //until the maven project stub is fixed
        //mojo.getProject().addCompileSourceRoot( "target/javah" );
        

        mojo.execute();

        CompilerConfiguration config = mojo.getCompilerConfiguration();

        assertNotNull( config );
        
        // validate data from pom which is fed into mojo.execute()
        
        assertEquals( new File( getBasedir() ), config.getWorkingDirectory() );
        
        assertEquals( "someExe", config.getExecutable() );

        assertEquals( new File( "target" ), config.getOutputDirectory() );

        assertEquals( 4, config.getStartOptions().length );
        assertEquals( "-s0", config.getStartOptions()[0] );
        assertEquals( "-s1", config.getStartOptions()[1] );
        assertEquals( "-s2", config.getStartOptions()[2] );
        assertEquals( "-s3", config.getStartOptions()[3] );
        
        assertEquals( 4, config.getMiddleOptions().length );
        assertEquals( "-m0", config.getMiddleOptions()[0] );
        assertEquals( "-m1", config.getMiddleOptions()[1] );
        assertEquals( "-m2", config.getMiddleOptions()[2] );
        assertEquals( "-m3", config.getMiddleOptions()[3] );

        assertEquals( 4, config.getEndOptions().length );
        assertEquals( "-e0", config.getEndOptions()[0] );
        assertEquals( "-e1", config.getEndOptions()[1] );
        assertEquals( "-e2", config.getEndOptions()[2] );
        assertEquals( "-e3", config.getEndOptions()[3] );
        
        // there are 2 source element with dependencyAnalysisParticipation is set to false
        assertEquals( 2, config.getSystemIncludePaths().length );
        assertEquals( new File( "src/main/native/dir4" ), config.getSystemIncludePaths()[0] );
        assertEquals( new File( "src/main/native/dir5" ), config.getSystemIncludePaths()[1] );
        
        // there are 3 normal source elements + 2 javah include
        assertEquals( 3, config.getIncludePaths().length );
        assertEquals( new File( "src/main/native/dir1" ), config.getIncludePaths()[0] );
        assertEquals( new File( "src/main/native/dir2" ), config.getIncludePaths()[1] );
        assertEquals( new File( "src/main/native/dir3" ), config.getIncludePaths()[2] );
        //until maven project stub is fixes were we can simulate more than 1 source root in the list
        //assertEquals( new File( "target/javah" ), config.getIncludePaths()[3] );
        
        //we have 4 source files, so in output directory, there must be 4 object files
        List objectFileList =  mojo.getAllCompilersOutputFileList();
        assertEquals( 4, objectFileList.size() );
        assertEquals( new File( "target/file11.o" ), (File) objectFileList.get( 0 ) );
        assertEquals( new File( "target/file12.o" ), (File) objectFileList.get( 1 ) );
        assertEquals( new File( "target/file21.o" ), (File) objectFileList.get( 2 ) );
        assertEquals( new File( "target/file22.o" ), (File) objectFileList.get( 3 ) );
        
    }

    public void testJavahOS()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/compiler/plugin-config-javahOS.xml" );
        NativeCompileMojo mojo = (NativeCompileMojo) lookupMojo( "compile", pluginXml );
        assertNotNull( mojo );

        mojo.setPluginContext( new HashMap() );

        mojo.execute();

        CompilerConfiguration config = mojo.getCompilerConfiguration();

        assertNotNull( config );

        //javah induces 2 system include paths into configuration
        assertEquals( 2, config.getSystemIncludePaths().length );
        //note "somJDKPath comes from the simulated pom above
        assertEquals( new File( "someJDKPath" ), config.getSystemIncludePaths()[0] );
        assertEquals( new File( "someJDKPath/someOS" ), config.getSystemIncludePaths()[1] );
    }

}
