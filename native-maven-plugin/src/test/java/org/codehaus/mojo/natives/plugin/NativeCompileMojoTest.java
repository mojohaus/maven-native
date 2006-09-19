package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.HashMap;

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
