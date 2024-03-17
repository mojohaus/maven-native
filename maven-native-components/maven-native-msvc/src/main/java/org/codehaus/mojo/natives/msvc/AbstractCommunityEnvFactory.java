package org.codehaus.mojo.natives.msvc;

import java.io.File;
import java.util.Map;

import org.codehaus.mojo.natives.AbstractEnvFactory;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.plexus.util.cli.Commandline;

public abstract class AbstractCommunityEnvFactory extends AbstractEnvFactory {
    private AbstractMSVC2017CircaEnvFactoryHelper helper = new AbstractMSVC2017CircaEnvFactoryHelper();

    protected Map<String, String> createEnvs(String version, String platform) throws NativeBuildException {
        File tmpEnvExecFile = null;
        try {
            String vsCommunityPath = queryVSInstallPath(version);

            if (vsCommunityPath == null) {
                throw new NativeBuildException(String.format("Can not find VS Community version '%s'", version));
            }
            if (!vsCommunityPath.endsWith("Community\\") && !vsCommunityPath.endsWith("Community")) {
                throw new NativeBuildException(
                        String.format("Directory '%s' is not a VS Community directory", vsCommunityPath));
            }
            File communityDir = new File(vsCommunityPath);
            if (!communityDir.isDirectory()) {
                throw new NativeBuildException(String.format("Path '%s' is not a directory", vsCommunityPath));
            }

            tmpEnvExecFile = this.helper.createEnvWrapperFile(communityDir, platform);

            Commandline cl = new Commandline();
            cl.setExecutable(tmpEnvExecFile.getAbsolutePath());

            return executeCommandLine(cl);

        } catch (NativeBuildException e) {
            throw e;
        } catch (Exception e) {
            throw new NativeBuildException("Unable to retrieve env", e);
        } finally {
            if (tmpEnvExecFile != null) {
                tmpEnvExecFile.delete();
            }
        }
    }

    protected String queryVSInstallPath(String version) {
        return helper.queryVSInstallPath(version);
    }

    protected Map<String, String> executeCommandLine(Commandline command) throws NativeBuildException {
        return helper.executeCommandLine(command);
    }
}
