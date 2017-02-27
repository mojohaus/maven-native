package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.HashMap;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class NativeDumpMojoTest extends AbstractMojoTestCase {

    public void testMojoLookup() throws Exception {
        File pluginXml = new File(getBasedir(), "src/test/resources/dump/plugin-config.xml");
        NativeDumpMojo mojo = (NativeDumpMojo) lookupMojo("dump", pluginXml);
        assertNotNull(mojo);
    }

    public void testSources() throws Exception {
        File pluginXml = new File(getBasedir(), "src/test/resources/dump/plugin-config.xml");
        NativeDumpMojo mojo = (NativeDumpMojo) lookupMojo("dump", pluginXml);
        assertNotNull(mojo);

        mojo.setPluginContext(new HashMap());
        mojo.execute();

    }
}
