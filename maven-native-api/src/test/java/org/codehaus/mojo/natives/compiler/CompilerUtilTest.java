package org.codehaus.mojo.natives.compiler;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.Os;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerUtilTest extends PlexusTestCase {

    @Test
    void getObjectFileFromSourceWithNoExtension() {
        File source;

        if (Os.isFamily("windows")) {
            source = new File("..\\dir1\\dir2\\fileWithoutExtenstion");
        } else {
            source = new File("../dir1/dir2/fileWithoutExtenstion");
        }

        File outputDirectory = new File("outputDirectory");
        File objectFile = AbstractCompiler.getObjectFile(source, outputDirectory, null);

        assertEquals(
                new File(outputDirectory, "fileWithoutExtenstion." + AbstractCompiler.getObjectFileExtension(null)),
                objectFile);
    }

    @Test
    void getObjectFileWithKnownExtension() {

        File source = new File("target/somefile.c");

        File outputDirectory = new File("outputDirectory");
        File objectFile = AbstractCompiler.getObjectFile(source, outputDirectory, "someext");

        assertEquals(new File(outputDirectory, "somefile." + "someext"), objectFile);
    }
}
