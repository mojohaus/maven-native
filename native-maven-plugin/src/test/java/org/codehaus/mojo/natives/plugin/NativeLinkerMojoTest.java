package org.codehaus.mojo.natives.plugin;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class NativeLinkerMojoTest
    extends AbstractMojoTestCase
{
    public void testMojoLookup()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/linker/plugin-config.xml" );
        NativeLinkMojo mojo = (NativeLinkMojo) lookupMojo( "link", pluginXml );
        assertNotNull( mojo );
    }

}
