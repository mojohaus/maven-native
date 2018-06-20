package org.codehaus.mojo.natives.msvc;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.codehaus.mojo.natives.AbstractEnvFactory;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.util.cli.StreamConsumer;

public abstract class AbstractCommunityEnvFactory extends AbstractEnvFactory {
    protected Map<String, String> createEnvs(String version, String platform)
        throws NativeBuildException {
        File tmpEnvExecFile = null;
        try {
            String vsCommunityPath = RegQuery.getValue(
                "REG_SZ",
                "HKLM\\SOFTWARE\\WOW6432Node\\Microsoft\\VisualStudio\\SxS\\VS7",
                version
            );
            if (vsCommunityPath == null) {
                throw new NativeBuildException(
                    String.format("Can not find VS Community version '%s'", version)
                );
            }
            if (!vsCommunityPath.endsWith("Community\\")) {
                throw new NativeBuildException(
                    String.format("Directory '%s' is not a VS Community directory", vsCommunityPath)
                );
            }
            File communityDir = new File(vsCommunityPath);
            if (!communityDir.isDirectory()) {
                throw new NativeBuildException(
                    String.format("Path '%s' is not a directory", vsCommunityPath)
                );
            }

            tmpEnvExecFile = this.createEnvWrapperFile(communityDir, platform);

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

    private File createEnvWrapperFile(File vsInstallDir, String platform)
        throws IOException {

        File tmpFile = File.createTempFile("msenv", ".bat");

        StringBuffer buffer = new StringBuffer();
        buffer.append("@echo off\r\n");
        buffer.append("call \"").append(vsInstallDir).append("\"")
            .append("\\VC\\Auxiliary\\Build\\vcvarsall.bat " + platform + "\n\r");
        buffer.append("echo " + EnvStreamConsumer.START_PARSING_INDICATOR).append("\r\n");
        buffer.append("set\n\r");
        FileUtils.fileWrite(tmpFile.getAbsolutePath(), buffer.toString());

        return tmpFile;
    }
}
