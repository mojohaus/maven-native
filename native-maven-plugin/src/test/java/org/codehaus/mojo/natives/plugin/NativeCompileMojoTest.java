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
        File pluginXml = new File( getBasedir(), "src/test/resources/compile/plugin-config.xml" );
        NativeCompileMojo mojo = (NativeCompileMojo) lookupMojo( "compile", pluginXml );
        assertNotNull( mojo );
    }

    public void testJavahOS()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/compile/plugin-config-with-javahOS.xml" );
        NativeCompileMojo mojo = (NativeCompileMojo) lookupMojo( "compile", pluginXml );
        assertNotNull( mojo );
        
        mojo.setPluginContext( new HashMap() );
        
        mojo.execute();
        
        CompilerConfiguration config = mojo.getCompilerConfiguration();
        
        assertNotNull( config );
        
        
        
    }

}
