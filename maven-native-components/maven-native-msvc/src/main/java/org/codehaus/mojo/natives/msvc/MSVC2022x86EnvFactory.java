package org.codehaus.mojo.natives.msvc;

import java.util.Map;

import org.codehaus.mojo.natives.NativeBuildException;

public class MSVC2022x86EnvFactory extends AbstractMSVC2022EnvFactory {

    /**
     * Each env factory needs its own static field, otherwise
     * the different "vcvarsall <platform>" calls will end up
     * colliding with each other.
     */
    private static Map<String, String> envs2022x86;

    @Override
    public synchronized Map<String, String> getEnvironmentVariables() throws NativeBuildException {
        if (envs2022x86 == null) {
            envs2022x86 = createEnvs();
        }

        return envs2022x86;
    }

    @Override
    protected Map<String, String> createEnvs() throws NativeBuildException {
        return this.createEnvs("VS170COMNTOOLS", "x86");
    }
}
