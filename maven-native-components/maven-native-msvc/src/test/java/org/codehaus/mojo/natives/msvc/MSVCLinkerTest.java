package org.codehaus.mojo.natives.msvc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.codehaus.mojo.natives.test.TestUtils.formPlatformCommandline;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MSVCLinkerTest extends PlexusTestCase {
    private MSVCLinker linker;

    private LinkerConfiguration config;

    private static final File objectFile0 = new File("source1.obj");

    private static final File objectFile1 = new File("source2.obj");

    private List<File> defautlObjectFiles;

    private String basedir;

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();

        this.defautlObjectFiles = new ArrayList<>();
        this.defautlObjectFiles.add(objectFile0);
        this.defautlObjectFiles.add(objectFile1);

        this.linker = new MSVCLinker();
        this.config = new LinkerConfiguration();
        this.basedir = getBasedir();
        config.setWorkingDirectory(new File(basedir));
        config.setOutputDirectory(new File(basedir, "target"));
        config.setOutputFileExtension("exe");
        config.setOutputFileName("test");
    }

    @Test
    void defaultLinkerExecutable() {
        Commandline cl = this.getCommandline();
        assertEquals("link.exe", cl.getLiteralExecutable());
        assertEquals(basedir, cl.getWorkingDirectory().getPath());
    }

    @Test
    void simpleLinkerCommand() {
        Commandline cl = this.getCommandline();
        String[] expected = new String[] {"link.exe", "/out:" + config.getOutputFile(), "source1.obj", "source2.obj"};
        assertArrayEquals(formPlatformCommandline(expected), cl.getCommandline());
    }

    // ///////////////////////// HELPERS //////////////////////////////////////
    private Commandline getCommandline() throws NativeBuildException {
        return this.linker.createLinkerCommandLine(defautlObjectFiles, config);
    }
}
