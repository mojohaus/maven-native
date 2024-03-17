package org.codehaus.mojo.natives.msvc;

import java.util.Map;

import org.codehaus.plexus.PlexusTestCase;
import org.junit.Assert;

public class MSVC2012EnvFactoryTest extends PlexusTestCase {

    public void testMSVC2012x86Env() {

        if (System.getenv("VS110COMNTOOLS") != null) {
            MSVC2012x86EnvFactory envFact = new MSVC2012x86EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            Assert.assertTrue(env.containsKey("VSINSTALLDIR"));
            Assert.assertTrue(env.containsKey("INCLUDE"));
            Assert.assertTrue(env.containsKey("FrameworkVersion32"));
        }
    }
}
