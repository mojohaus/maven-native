package org.codehaus.mojo.natives.msvc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class AbstractCommunityEnvFactoryTest extends PlexusTestCase {

    @Test
    void pathValidation() throws Exception {
        CommunityEnvFactoryMock mock = new CommunityEnvFactoryMock();

        try {
            mock.setVsInstallPathMock("C:\\Program Files\\VS 2017\\Ultimate");
            mock.createEnvs("17.0", "x86");
            fail("Path validation must fail");
        } catch (NativeBuildException e) {
            assertEquals("Directory 'C:\\Program Files\\VS 2017\\Ultimate' is not a VS Community directory",
                    e.getMessage());
        }

        try {
            mock.setVsInstallPathMock(null);
            mock.createEnvs("17.0", "x86");
            fail("Path validation must fail");
        } catch (NativeBuildException e) {
            assertEquals("Can not find VS Community version '17.0'", e.getMessage());
        }

        File tempDir = createCommunityDir(true);
        try {
            mock.setVsInstallPathMock(tempDir.getAbsolutePath());
            mock.createEnvs("17.0", "x86");
            fail("Path validation must fail");
        } catch (NativeBuildException e) {
            assertEquals(e.getMessage(), String.format("Path '%s' is not a directory", tempDir.getAbsolutePath()));
        }
    }

    @Test
    void commandGeneration() throws Exception {
        CommunityEnvFactoryMock mock = new CommunityEnvFactoryMock();

        File tempDir = createCommunityDir(false);
        Assertions.assertDoesNotThrow(() -> {
            mock.setVsInstallPathMock(tempDir.getAbsolutePath());
            mock.createEnvs("17.0", "x64");
            assertTrue(mock.getRecordedCommandFileContent().contains(tempDir.getAbsolutePath()));
        }, "Must not fail");
    }

    private File createCommunityDir(boolean isFile) throws IOException {
        File tempDir = FileUtils.createTempFile("test", "dir", null);
        if (!tempDir.mkdir()) {
            fail("Failed to create temporary directory");
        }
        File tempCommunityFile = new File(tempDir, "Community");
        if (isFile) {
            if (!tempCommunityFile.createNewFile()) {
                fail("Failed to create Community file");
            }
        } else {
            if (!tempCommunityFile.mkdir()) {
                fail("Failed to create Community directory");
            }
        }
        FileUtils.forceDeleteOnExit(tempDir);
        FileUtils.forceDeleteOnExit(tempCommunityFile);
        return tempCommunityFile;
    }
}

class CommunityEnvFactoryMock extends AbstractCommunityEnvFactory {
    private String vsInstallPathMock;
    private String recordedCommandFile;

    void setVsInstallPathMock(String value) {
        vsInstallPathMock = value;
    }

    String getRecordedCommandFileContent() {
        return recordedCommandFile;
    }

    @Override
    protected String queryVSInstallPath(String version) {
        return vsInstallPathMock;
    }

    @Override
    protected Map<String, String> executeCommandLine(Commandline command) throws NativeBuildException {
        try {
            recordedCommandFile = FileUtils.fileRead(command.getLiteralExecutable());
        } catch (IOException e) {
            throw new NativeBuildException("Failed to capture file output");
        }
        return new HashMap<>();
    }

    @Override
    protected Map<String, String> createEnvs() throws NativeBuildException {
        return null;
    }
}
