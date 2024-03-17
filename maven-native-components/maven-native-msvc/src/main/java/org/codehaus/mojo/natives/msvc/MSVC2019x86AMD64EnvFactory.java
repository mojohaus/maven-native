package org.codehaus.mojo.natives.msvc;

import java.util.Map;

import org.codehaus.mojo.natives.NativeBuildException;

public class MSVC2019x86AMD64EnvFactory extends AbstractMSVC2019EnvFactory {

    /**
     * Each env factory needs its own static field, otherwise
     * the different "vcvarsall <platform>" calls will end up
     * colliding with each other.
     */
    private static Map<String, String> envs2019x86amd64;

    @Override
    public synchronized Map<String, String> getEnvironmentVariables() throws NativeBuildException {
        if (envs2019x86amd64 == null) {
            envs2019x86amd64 = createEnvs();
        }

        return envs2019x86amd64;
    }

    @Override
    protected Map<String, String> createEnvs() throws NativeBuildException {
        return this.createEnvs("VS160COMNTOOLS", "x86_x64");
    }
}
