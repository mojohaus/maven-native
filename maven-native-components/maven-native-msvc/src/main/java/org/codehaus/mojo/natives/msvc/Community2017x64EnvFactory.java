package org.codehaus.mojo.natives.msvc;

import java.util.Map;

import org.codehaus.mojo.natives.NativeBuildException;

public class Community2017x64EnvFactory extends AbstractCommunityEnvFactory {

    @Override
    protected Map<String, String> createEnvs() throws NativeBuildException {
        return this.createEnvs("15.0", "x64");
    }
}
