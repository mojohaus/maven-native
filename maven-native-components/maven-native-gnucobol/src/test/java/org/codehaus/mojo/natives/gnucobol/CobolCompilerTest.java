/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.codehaus.mojo.natives.gnucobol;

import java.io.File;

import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;

import static org.codehaus.mojo.natives.test.TestUtils.formPlatformCommandline;
import static org.junit.Assert.assertArrayEquals;

public class CobolCompilerTest extends PlexusTestCase {
    private GNUCOBOLCompiler compiler;

    private CompilerConfiguration config;

    private static final File sourceFile = new File("source.cob");

    private static final File objectFile = new File("object.o");

    private static final String[] simpleArgv = {"-o", "object.o", "-c", "source.cob"};

    @Override
    public void setUp() throws Exception {
        super.setUp();

        this.compiler = new GNUCOBOLCompiler();
        this.config = new CompilerConfiguration();
    }

    public void testSimpleCompilation() {
        Commandline cl = compiler.getCommandLine(sourceFile, objectFile, config);

        String[] expected = new String[] {"cobc", simpleArgv[0], simpleArgv[1], simpleArgv[2], simpleArgv[3]};
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }

    public void testNonDefaultExecutable() {
        this.config.setExecutable("cobc-alt");
        Commandline cl = compiler.getCommandLine(sourceFile, objectFile, config);
        String[] expected = new String[] {"cobc-alt", simpleArgv[0], simpleArgv[1], simpleArgv[2], simpleArgv[3]};
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }

    public void testStartOptions() {
        String[] startOptions = {"-s1", "-s2"};
        config.setStartOptions(startOptions);

        Commandline cl = compiler.getCommandLine(sourceFile, objectFile, config);

        String[] expected =
                new String[] {"cobc", "-s1", "-s2", simpleArgv[0], simpleArgv[1], simpleArgv[2], simpleArgv[3]};
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }

    public void testIncludePaths() {
        File[] includePaths = {new File("p1"), new File("p2")};

        config.setIncludePaths(includePaths);

        Commandline cl = compiler.getCommandLine(sourceFile, objectFile, config);

        String[] expected =
                new String[] {"cobc", "-Ip1", "-Ip2", simpleArgv[0], simpleArgv[1], simpleArgv[2], simpleArgv[3]};
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }

    public void testSystemIncludePaths() {
        File[] includePaths = {new File("p1"), new File("p2")};

        File[] systemIncludePaths = {new File("sp1"), new File("sp2")};

        config.setIncludePaths(includePaths);

        config.setSystemIncludePaths(systemIncludePaths);

        Commandline cl = compiler.getCommandLine(sourceFile, objectFile, config);

        String[] expected = new String[] {
            "cobc", "-Ip1", "-Ip2", "-Isp1", "-Isp2", simpleArgv[0], simpleArgv[1], simpleArgv[2], simpleArgv[3]
        };
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }

    public void testMiddleOptions() {
        File[] includePaths = {new File("p1"), new File("p2")};
        config.setIncludePaths(includePaths);

        String[] startOptions = {"-s1", "-s2"};
        String[] middleOptions = {"-m1", "-m2"};
        config.setStartOptions(startOptions);
        config.setMiddleOptions(middleOptions);

        Commandline cl = compiler.getCommandLine(sourceFile, objectFile, config);

        String[] expected = new String[] {
            "cobc",
            "-s1",
            "-s2",
            "-Ip1",
            "-Ip2",
            "-m1",
            "-m2",
            simpleArgv[0],
            simpleArgv[1],
            simpleArgv[2],
            simpleArgv[3]
        };
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }

    public void testEndOptions() {
        File[] includePaths = {new File("p1"), new File("p2")};
        config.setIncludePaths(includePaths);

        String[] startOptions = {"-s1", "-s2"};
        String[] middleOptions = {"-m1", "-m2"};
        String[] endOptions = {"-e1", "-e2"};
        config.setStartOptions(startOptions);
        config.setMiddleOptions(middleOptions);
        config.setEndOptions(endOptions);

        Commandline cl = compiler.getCommandLine(sourceFile, objectFile, config);

        String[] expected = new String[] {
            "cobc",
            "-s1",
            "-s2",
            "-Ip1",
            "-Ip2",
            "-m1",
            "-m2",
            simpleArgv[0],
            simpleArgv[1],
            simpleArgv[2],
            simpleArgv[3],
            "-e1",
            "-e2"
        };
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }
}
