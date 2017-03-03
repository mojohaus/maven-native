package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import org.apache.commons.codec.digest.DigestUtils;

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

        assertEquals("080d283edcdbfaeb7ccf7f6a6cdeb071", DigestUtils.md5Hex(new FileInputStream(new File("target/generated-sources/c/dump.h"))));
        assertEquals("4d50d16822d0eff1edd89f1b872975bc", DigestUtils.md5Hex(new FileInputStream(new File("target/generated-sources/c/dump.c"))));
    }
}
