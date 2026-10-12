package org.codehaus.mojo.natives.msvc;

import java.util.Map;
import java.util.TreeMap;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MSVC2022EnvFactoryTest {
    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    @BeforeEach
    void setUp() {
        logger = new ConsoleLogger(Logger.LEVEL_INFO, getClass().toString());
    }

    @Test
    void msvc2022x64Env() {

        if (System.getenv("VS170COMNTOOLS") != null) {
            MSVC2022x64EnvFactory envFact = new MSVC2022x64EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            logger.info(envFact.getClass().getName() + "env=" + env);
            Map<String, String> envCaseInsenstiveKeys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            envCaseInsenstiveKeys.putAll(env);
            assertTrue(envCaseInsenstiveKeys.containsKey("VSINSTALLDIR"));
            assertTrue(envCaseInsenstiveKeys.containsKey("INCLUDE"));
            assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_HOST_ARCH"));
            assertEquals("x64", envCaseInsenstiveKeys.get("VSCMD_ARG_HOST_ARCH"));
            assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_TGT_ARCH"));
            assertEquals("x64", envCaseInsenstiveKeys.get("VSCMD_ARG_TGT_ARCH"));
        }
    }

    @Test
    void msvc2022x86Env() {

        if (System.getenv("VS170COMNTOOLS") != null) {
            MSVC2022x86EnvFactory envFact = new MSVC2022x86EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            logger.info(envFact.getClass().getName() + ":env=" + env);
            Map<String, String> envCaseInsenstiveKeys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            envCaseInsenstiveKeys.putAll(env);
            assertTrue(envCaseInsenstiveKeys.containsKey("VSINSTALLDIR"));
            assertTrue(envCaseInsenstiveKeys.containsKey("INCLUDE"));
            assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_HOST_ARCH"));
            assertEquals("x86", envCaseInsenstiveKeys.get("VSCMD_ARG_HOST_ARCH"));
            assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_TGT_ARCH"));
            assertEquals("x86", envCaseInsenstiveKeys.get("VSCMD_ARG_TGT_ARCH"));
        }
    }

    @Test
    void msvc2022x86AMD64Env() {

        if (System.getenv("VS170COMNTOOLS") != null) {
            MSVC2022x86AMD64EnvFactory envFact = new MSVC2022x86AMD64EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            logger.info(envFact.getClass().getName() + ":env=" + env);
            Map<String, String> envCaseInsenstiveKeys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            envCaseInsenstiveKeys.putAll(env);
            assertTrue(envCaseInsenstiveKeys.containsKey("VSINSTALLDIR"));
            assertTrue(envCaseInsenstiveKeys.containsKey("INCLUDE"));
            assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_HOST_ARCH"));
            assertEquals("x86", envCaseInsenstiveKeys.get("VSCMD_ARG_HOST_ARCH"));
            assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_TGT_ARCH"));
            assertEquals("x64", envCaseInsenstiveKeys.get("VSCMD_ARG_TGT_ARCH"));
        }
    }

    @Test
    void msvc2022amd64x864Env() {

        if (System.getenv("VS170COMNTOOLS") != null) {
            MSVC2022AMD64x86EnvFactory envFact = new MSVC2022AMD64x86EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            logger.info(envFact.getClass().getName() + ":env=" + env);
            Map<String, String> envCaseInsenstiveKeys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            envCaseInsenstiveKeys.putAll(env);
            assertTrue(envCaseInsenstiveKeys.containsKey("VSINSTALLDIR"));
            assertTrue(envCaseInsenstiveKeys.containsKey("INCLUDE"));
            assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_HOST_ARCH"));
            assertEquals("x64", envCaseInsenstiveKeys.get("VSCMD_ARG_HOST_ARCH"));
            assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_TGT_ARCH"));
            assertEquals("x86", envCaseInsenstiveKeys.get("VSCMD_ARG_TGT_ARCH"));
        }
    }
}
