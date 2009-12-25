package org.codehaus.mojo.natives.manager;

import org.codehaus.mojo.natives.EnvFactory;
import org.codehaus.mojo.natives.NativeBuildException;

public interface EnvFactoryManager
{
    EnvFactory getEnvFactory( String className )
        throws NativeBuildException;
}
