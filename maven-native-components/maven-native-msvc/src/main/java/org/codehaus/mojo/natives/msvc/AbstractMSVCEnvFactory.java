package org.codehaus.mojo.natives.msvc;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.codehaus.mojo.natives.AbstractEnvFactory;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.util.EnvUtil;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.util.cli.StreamConsumer;

public abstract class AbstractMSVCEnvFactory extends AbstractEnvFactory {

    protected static String getProgramFiles() {
        return EnvUtil.getEnv("ProgramFiles", "ProgramFiles", "C:\\Program Files");
    }

    protected static String getProgramFilesX86() {
        return EnvUtil.getEnv("ProgramFiles(x86)", "ProgramFiles", getProgramFiles());
    }

    protected static String getSystemRoot() {
        return EnvUtil.getEnv("SystemRoot", "SystemRoot", "C:\\WINDOWS");
    }

    protected Map<String, String> createEnvs(String commonToolEnvKey, String platform) throws NativeBuildException {

        File tmpEnvExecFile = null;
        try {
            File vsCommonToolDir = this.getCommonToolDirectory(commonToolEnvKey);

            File vsInstallDir = this.getVisualStudioInstallDirectory(vsCommonToolDir);

            if (!vsInstallDir.isDirectory()) {
                throw new NativeBuildException(vsInstallDir.getPath() + " is not a directory.");
            }

            tmpEnvExecFile = this.createEnvWrapperFile(vsInstallDir, platform);

            Commandline cl = new Commandline();
            cl.setExecutable(tmpEnvExecFile.getAbsolutePath());

            EnvStreamConsumer stdout = new EnvStreamConsumer();
            StreamConsumer stderr = new DefaultConsumer();

            CommandLineUtils.executeCommandLine(cl, stdout, stderr);

            return stdout.getParsedEnv();
        } catch (Exception e) {
            throw new NativeBuildException("Unable to retrieve env", e);
        } finally {
            if (tmpEnvExecFile != null) {
                tmpEnvExecFile.delete();
            }
        }
    }

    private File getCommonToolDirectory(String commonToolEnvKey) throws NativeBuildException {
        String envValue = System.getenv(commonToolEnvKey);
        if (envValue == null) {
            throw new NativeBuildException("Environment variable: " + commonToolEnvKey + " not available.");
        }

        return new File(envValue);
    }

    private File getVisualStudioInstallDirectory(File commonToolDir) throws NativeBuildException {
        try {
            return new File(commonToolDir, "../..").getCanonicalFile();
        } catch (IOException e) {
            throw new NativeBuildException(
                    "Unable to contruct Visual Studio install directory using: " + commonToolDir, e);
        }
    }

    protected File createEnvWrapperFile(File vsInstallDir, String platform) throws IOException {

        File tmpFile = File.createTempFile("msenv", ".bat");

        String buffer = "@echo off\r\n"
                + "call \"" + vsInstallDir + "\"" + "\\VC\\vcvarsall.bat "
                + platform + "\n\r"
                + "echo " + EnvStreamConsumer.START_PARSING_INDICATOR + "\r\n"
                + "set\n\r";

        FileUtils.fileWrite(tmpFile.getAbsolutePath(), buffer);

        return tmpFile;
    }
}
