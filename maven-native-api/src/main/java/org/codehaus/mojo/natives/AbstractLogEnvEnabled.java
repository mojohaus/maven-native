package org.codehaus.mojo.natives;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.mojo.natives.util.EnvUtil;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.cli.Commandline;

public class AbstractLogEnvEnabled
    extends AbstractLogEnabled
    implements Initializable
{
    private static Map EMPTY_ENV = new HashMap();
    /**
     * Additional environment properties that the implementation can fill in
     */
    private Map environmentVariables;
        
    /**
     * Default Container lifecyle initialize phase
     */
    public void initialize() 
    {
        
    }
    
    protected void setEnvironmentVariables ( Map envs )
    {
        this.environmentVariables = envs;
    }
    
    protected Map getEnvironmentVariables()
    {
        if ( this.environmentVariables == null )
        {
            return EMPTY_ENV;
        }
        
        return this.environmentVariables;
    }
        
    /**
     * Add additional environment properties to commandline.
     * 
     * @param cl
     * @param config
     */
    protected void setupCommandlineEnv( Commandline cl,  ConfigurationBase config )
    {
        if ( config.getEnvironmentVariables() != null )
        {
            EnvUtil.setupCommandlineEnv( config.getEnvironmentVariables(), cl );
        }
        
        if ( this.getEnvironmentVariables() != null )
        {
            EnvUtil.setupCommandlineEnv( this.getEnvironmentVariables(), cl );
        }        
        
    }    

}
