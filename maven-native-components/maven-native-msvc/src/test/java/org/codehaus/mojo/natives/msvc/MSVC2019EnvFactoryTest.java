package org.codehaus.mojo.natives.msvc;

import java.util.Map;
import java.util.TreeMap;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.junit.Assert;

public class MSVC2019EnvFactoryTest extends PlexusTestCase {
    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    public void setUp() throws Exception {
        super.setUp();

        LoggerManager loggerManager = lookup(LoggerManager.class);

        logger = loggerManager.getLoggerForComponent(this.getClass().toString());
    }

    public void testMSVC2019x64Env() {

        if (System.getenv("VS160COMNTOOLS") != null) {
            MSVC2019x64EnvFactory envFact = new MSVC2019x64EnvFactory();
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

    public void testMSVC2019x86Env() {

        if (System.getenv("VS160COMNTOOLS") != null) {
            MSVC2019x86EnvFactory envFact = new MSVC2019x86EnvFactory();
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

    public void testMSVC2019x86AMD64Env() {

        if (System.getenv("VS160COMNTOOLS") != null) {
            MSVC2019x86AMD64EnvFactory envFact = new MSVC2019x86AMD64EnvFactory();
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

    public void testMSVC2019AMD64x864Env() {

        if (System.getenv("VS160COMNTOOLS") != null) {
            MSVC2019AMD64x86EnvFactory envFact = new MSVC2019AMD64x86EnvFactory();
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
