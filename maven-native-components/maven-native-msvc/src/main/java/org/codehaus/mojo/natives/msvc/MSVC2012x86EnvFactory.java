package org.codehaus.mojo.natives.msvc;

import java.util.Map;

import org.codehaus.mojo.natives.NativeBuildException;

public class MSVC2012x86EnvFactory
    extends AbstractMSVCEnvFactory
{

    protected Map createEnvs()
        throws NativeBuildException
    {
        return this.createEnvs( "VS110COMNTOOLS", "x86" );
    }

}
