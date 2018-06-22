package org.codehaus.mojo.natives.msvc;

import org.codehaus.mojo.natives.NativeBuildException;

import java.util.Map;

public class Community2017x86AMD64EnvFactory extends AbstractCommunityEnvFactory
{

    @Override
    protected Map<String, String> createEnvs()
            throws NativeBuildException
    {
        return this.createEnvs( "15.0", "x86_amd64" );
    }
}
