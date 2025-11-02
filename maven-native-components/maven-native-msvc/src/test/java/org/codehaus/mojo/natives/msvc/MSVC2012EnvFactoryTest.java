package org.codehaus.mojo.natives.msvc;

import java.util.Map;

import org.codehaus.plexus.PlexusTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MSVC2012EnvFactoryTest extends PlexusTestCase {

    @Test
    void msvc2012x86Env() {

        if (System.getenv("VS110COMNTOOLS") != null) {
            MSVC2012x86EnvFactory envFact = new MSVC2012x86EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            Assertions.assertTrue(env.containsKey("VSINSTALLDIR"));
            Assertions.assertTrue(env.containsKey("INCLUDE"));
            Assertions.assertTrue(env.containsKey("FrameworkVersion32"));
        }
    }
}
