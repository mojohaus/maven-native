package org.codehaus.mojo.natives.msvc;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MSVC2012EnvFactoryTest {

    @Test
    void msvc2012x86Env() {

        if (System.getenv("VS110COMNTOOLS") != null) {
            MSVC2012x86EnvFactory envFact = new MSVC2012x86EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            assertTrue(env.containsKey("VSINSTALLDIR"));
            assertTrue(env.containsKey("INCLUDE"));
            assertTrue(env.containsKey("FrameworkVersion32"));
        }
    }
}
