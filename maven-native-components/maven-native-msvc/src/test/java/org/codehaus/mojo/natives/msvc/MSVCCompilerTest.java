package org.codehaus.mojo.natives.msvc;

import java.io.File;

import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.codehaus.mojo.natives.test.TestUtils.formPlatformCommandline;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MSVCCompilerTest extends PlexusTestCase {
    private MSVCCompiler compiler;

    private CompilerConfiguration config;

    private static File sourceFile = new File("source.c");

    private static File objectFile = new File("object.obj");

    private static String[] simpleArgv = {"/Foobject.obj", "-c", "source.c"};

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();

        this.compiler = new MSVCCompiler();
        this.config = new CompilerConfiguration();
    }

    @Test
    void simpleCompilation() {
        Commandline cl = compiler.getCommandLine(sourceFile, objectFile, config);
        String[] expected = new String[] {"cl.exe", simpleArgv[0], simpleArgv[1], simpleArgv[2]};
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }

    @Test
    void nullOptionsNoNPE() {
        // Test that null middle and end options don't cause NPE - reproduces issue from GitHub
        File[] includePaths = {new File("p1"), new File("p2")};
        config.setIncludePaths(includePaths);

        String[] startOptions = {"-s1", "-s2"};
        config.setStartOptions(startOptions);
        // middleOptions and endOptions are intentionally left as null to test the fix

        Commandline cl = compiler.getCommandLine(sourceFile, objectFile, config);

        String[] expected =
                new String[] {"cl.exe", "-s1", "-s2", "-Ip1", "-Ip2", simpleArgv[0], simpleArgv[1], simpleArgv[2]};
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }
}
