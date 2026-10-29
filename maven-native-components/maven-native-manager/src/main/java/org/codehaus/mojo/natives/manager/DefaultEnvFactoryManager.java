package org.codehaus.mojo.natives.manager;

import javax.inject.Named;
import javax.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.mojo.natives.EnvFactory;
import org.codehaus.mojo.natives.NativeBuildException;

/**
 * Construct EnvFactory
 */
@Named("default")
@Singleton
public class DefaultEnvFactoryManager implements EnvFactoryManager {
    private Map<String, EnvFactory> envFactoryCache = new HashMap<>();

    @Override
    public EnvFactory getEnvFactory(String className) throws NativeBuildException {
        EnvFactory envFactory = envFactoryCache.get(className);

        if (envFactory == null) {
            try {
                envFactory = (EnvFactory) Class.forName(className).newInstance();
                envFactoryCache.put(className, envFactory);
            } catch (Exception e) {
                throw new NativeBuildException("Unable to find EnvFactory: " + className);
            }
        }

        return envFactory;
    }
}
