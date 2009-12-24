package org.codehaus.mojo.natives;

import java.util.Map;

public abstract class AbstractEnvFactory
    implements EnvFactory
{

    /* until we can do one more refactoring
    private static Map envs;
    
    public synchronized Map getEnvironmentVariables() 
        throws NativeBuildException
    {
        if ( envs == null )
        {
            envs = createEnvs();
        }
        
        return envs;
    }
    */

    protected abstract Map createEnvs()
        throws NativeBuildException;

}
