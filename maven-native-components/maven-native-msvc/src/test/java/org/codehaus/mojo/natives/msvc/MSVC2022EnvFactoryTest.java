package org.codehaus.mojo.natives.msvc;

import java.util.Map;
import java.util.TreeMap;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.junit.Assert;

public class MSVC2022EnvFactoryTest extends PlexusTestCase {
    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    public void setUp() throws Exception {
        super.setUp();

        LoggerManager loggerManager = lookup(LoggerManager.class);

        logger = loggerManager.getLoggerForComponent(this.getClass().toString());
    }

    public void testMSVC2022x64Env() {

        if (System.getenv("VS170COMNTOOLS") != null) {
            MSVC2022x64EnvFactory envFact = new MSVC2022x64EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            logger.info(envFact.getClass().getName() + "env=" + env);
            Map<String, String> envCaseInsenstiveKeys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            envCaseInsenstiveKeys.putAll(env);
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSINSTALLDIR"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("INCLUDE"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_HOST_ARCH"));
            Assert.assertEquals("x64", envCaseInsenstiveKeys.get("VSCMD_ARG_HOST_ARCH"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_TGT_ARCH"));
            Assert.assertEquals("x64", envCaseInsenstiveKeys.get("VSCMD_ARG_TGT_ARCH"));
        }
    }

    public void testMSVC2022x86Env() {

        if (System.getenv("VS170COMNTOOLS") != null) {
            MSVC2022x86EnvFactory envFact = new MSVC2022x86EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            logger.info(envFact.getClass().getName() + ":env=" + env);
            Map<String, String> envCaseInsenstiveKeys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            envCaseInsenstiveKeys.putAll(env);
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSINSTALLDIR"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("INCLUDE"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_HOST_ARCH"));
            Assert.assertEquals("x86", envCaseInsenstiveKeys.get("VSCMD_ARG_HOST_ARCH"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_TGT_ARCH"));
            Assert.assertEquals("x86", envCaseInsenstiveKeys.get("VSCMD_ARG_TGT_ARCH"));
        }
    }

    public void testMSVC2022x86AMD64Env() {

        if (System.getenv("VS170COMNTOOLS") != null) {
            MSVC2022x86AMD64EnvFactory envFact = new MSVC2022x86AMD64EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            logger.info(envFact.getClass().getName() + ":env=" + env);
            Map<String, String> envCaseInsenstiveKeys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            envCaseInsenstiveKeys.putAll(env);
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSINSTALLDIR"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("INCLUDE"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_HOST_ARCH"));
            Assert.assertEquals("x86", envCaseInsenstiveKeys.get("VSCMD_ARG_HOST_ARCH"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_TGT_ARCH"));
            Assert.assertEquals("x64", envCaseInsenstiveKeys.get("VSCMD_ARG_TGT_ARCH"));
        }
    }

    public void testMSVC2022AMD64x864Env() {

        if (System.getenv("VS170COMNTOOLS") != null) {
            MSVC2022AMD64x86EnvFactory envFact = new MSVC2022AMD64x86EnvFactory();
            Map<String, String> env = envFact.getEnvironmentVariables();
            logger.info(envFact.getClass().getName() + ":env=" + env);
            Map<String, String> envCaseInsenstiveKeys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            envCaseInsenstiveKeys.putAll(env);
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSINSTALLDIR"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("INCLUDE"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_HOST_ARCH"));
            Assert.assertEquals("x64", envCaseInsenstiveKeys.get("VSCMD_ARG_HOST_ARCH"));
            Assert.assertTrue(envCaseInsenstiveKeys.containsKey("VSCMD_ARG_TGT_ARCH"));
            Assert.assertEquals("x86", envCaseInsenstiveKeys.get("VSCMD_ARG_TGT_ARCH"));
        }
    }
}
