package org.codehaus.mojo.natives;

public interface EnvFactoryManager
{
    EnvFactory getEnvFactory( String className )
        throws NativeBuildException;
}
