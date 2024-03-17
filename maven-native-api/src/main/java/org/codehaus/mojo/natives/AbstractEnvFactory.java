package org.codehaus.mojo.natives;

import java.util.Map;

public abstract class AbstractEnvFactory implements EnvFactory {

    private static Map<String, String> envs;

    @Override
    public synchronized Map<String, String> getEnvironmentVariables() throws NativeBuildException {
        if (envs == null) {
            envs = createEnvs();
        }

        return envs;
    }

    protected abstract Map<String, String> createEnvs() throws NativeBuildException;
}
