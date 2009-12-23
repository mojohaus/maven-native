package org.codehaus.mojo.natives.msvc;

import org.codehaus.mojo.natives.EnvFactory;
import org.codehaus.mojo.natives.util.EnvUtil;

public abstract class AbstractMSVCEnvFactory
    implements EnvFactory
{

    protected static String getProgramFiles()
    {
        return EnvUtil.getEnv( "ProgramFiles", "ProgramFiles", "C:/Program Files" );
    }

    protected static String getSystemRoot()
    {
        return EnvUtil.getEnv( "SystemRoot", "SystemRoot", "C:/WINDOWS" );
    }

}
