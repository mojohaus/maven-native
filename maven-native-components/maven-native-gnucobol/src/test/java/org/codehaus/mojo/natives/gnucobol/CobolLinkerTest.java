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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;

public class CobolLinkerTest extends PlexusTestCase {
    private GNUCOBOLLinker linker;

    private LinkerConfiguration config;

    private static final File objectFile0 = new File("source1.o");

    private static final File objectFile1 = new File("source2.o");

    private List<File> defaultObjectFiles;

    private String basedir;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        this.defaultObjectFiles = new ArrayList<>();
        this.defaultObjectFiles.add(objectFile0);
        this.defaultObjectFiles.add(objectFile1);

        this.linker = new GNUCOBOLLinker();
        this.config = new LinkerConfiguration();
        this.basedir = getBasedir();
        config.setWorkingDirectory(new File(basedir));
        config.setOutputDirectory(new File(basedir, "target"));
        config.setOutputFileExtension("exe");
        config.setOutputFileName("test");
    }

    public void testDefaultLinkerExecutable() {
        Commandline cl = this.getCommandline();

        assertEquals("cobc", cl.getLiteralExecutable());
        assertEquals(basedir, cl.getWorkingDirectory().getPath());
    }

    public void testOverrideLinkerExecutable() {
        config.setExecutable("cobc-alt");

        Commandline cl = this.getCommandline();

        assertEquals("cobc-alt", cl.getLiteralExecutable());
    }

    /**
     * cobc has no build-mode flag by default, and its own default is "-m" (a dynamically loadable module) rather than
     * "-x" (an executable). The linker deliberately emits no mode flag, leaving the choice to the caller. Pinned so
     * the default cannot change unnoticed.
     */
    public void testNoBuildModeFlagByDefault() {
        Commandline cl = this.getCommandline();

        List<String> args = Arrays.asList(cl.getArguments());
        assertFalse("cobc build mode must not be forced by the linker", args.contains("-x"));
        assertFalse("cobc build mode must not be forced by the linker", args.contains("-m"));
        assertFalse("cobc build mode must not be forced by the linker", args.contains("-b"));
    }

    /**
     * An executable is produced by passing "-x" through the start options, which must land before the output option.
     */
    public void testExecutableBuildModeViaStartOptions() {
        config.setStartOptions(new String[] {"-x"});

        Commandline cl = this.getCommandline();

        String[] args = cl.getArguments();
        assertEquals("-x", args[0]);
        assertEquals("-o", args[1]);
    }

    public void testObjectFileList() {
        Commandline cl = this.getCommandline();

        int index = Arrays.asList(cl.getArguments()).indexOf("source1.o");
        assertTrue(index >= 0);
        assertEquals("source2.o", cl.getArguments()[index + 1]);
    }

    public void testLinkerResponseFile() {
        this.config.setUsingLinkerResponseFile(true);
        this.config.setWorkingDirectory(new File(getBasedir(), "target"));
        Commandline cl = this.getCommandline();

        assertTrue(Arrays.asList(cl.getArguments()).contains("@objectsFile"));
    }

    public void testRelativeObjectFileList() {
        ArrayList<File> objectFiles = new ArrayList<>(2);
        objectFiles.add(new File(config.getOutputDirectory(), "file1.o"));
        objectFiles.add(new File(config.getOutputDirectory(), "file2.o"));

        Commandline cl = this.getCommandline(objectFiles);

        int index = Arrays.asList(cl.getArguments()).indexOf("target" + File.separator + "file1.o");
        assertTrue(index >= 0);
        assertEquals("target" + File.separator + "file2.o", cl.getArguments()[index + 1]);
    }

    public void testOptions() {
        String[] options = {"-o1", "-o2", "-o3"};
        config.setStartOptions(options);

        Commandline cl = this.getCommandline();

        int index = Arrays.asList(cl.getArguments()).indexOf("-o1");
        assertTrue(index >= 0);
        assertEquals("-o2", cl.getArguments()[index + 1]);
        assertEquals("-o3", cl.getArguments()[index + 2]);
    }

    public void testExternalUnixLibraries() {
        config.setExternalLibDirectory(new File("theLib"));

        List<String> externalLibFileNames = new ArrayList<>();
        externalLibFileNames.add("file0.lib");
        externalLibFileNames.add("file0.o");
        externalLibFileNames.add("file1.obj");
        externalLibFileNames.add("file1.so");
        externalLibFileNames.add("libfile2.so");
        externalLibFileNames.add("libfile3.a");

        config.setExternalLibFileNames(externalLibFileNames);

        Commandline cl = this.getCommandline(new ArrayList<>(0));

        int index = Arrays.asList(cl.getArguments()).indexOf("-LtheLib");
        assertTrue(index >= 0);
        assertEquals("-lfile1", cl.getArguments()[index + 1]);
        assertEquals("-lfile2", cl.getArguments()[index + 2]);
        assertEquals("-lfile3", cl.getArguments()[index + 3]);
    }

    private Commandline getCommandline() throws NativeBuildException {
        return this.linker.createLinkerCommandLine(defaultObjectFiles, config);
    }

    private Commandline getCommandline(List<File> objectFiles) throws NativeBuildException {
        return this.linker.createLinkerCommandLine(objectFiles, config);
    }
}
