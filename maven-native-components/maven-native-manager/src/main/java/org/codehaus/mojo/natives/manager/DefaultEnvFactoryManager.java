package org.codehaus.mojo.natives.manager;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.mojo.natives.EnvFactory;
import org.codehaus.mojo.natives.NativeBuildException;

/**
 * Construct EnvFactory
 *
 */
public class DefaultEnvFactoryManager
    implements EnvFactoryManager
{
    private Map envFactoryCache = new HashMap();
    
    public EnvFactory getEnvFactory( String className )
        throws NativeBuildException
    {
        EnvFactory envFactory = (EnvFactory) envFactoryCache.get( className );
        
        if ( envFactory == null )
        {
            try
            {
                envFactory = (EnvFactory) Class.forName( className ).newInstance();
                envFactoryCache.put(  className, envFactory );
            }
            catch ( Exception e )
            {
                throw new NativeBuildException( "Unable to find EnvFactory: " + className );
            }
        }
        
        return envFactory;
        
    }

}
