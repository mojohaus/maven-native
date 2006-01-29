package org.codehaus.mojo.natives.util;

public class EnvUtil 
{
    public static String getEnv( String envKey )
    {
        return getEnv( envKey, "" );
    }

    public static String getEnv( String envKey, String defaultValue )
    {
        String envValue = null;

        try
        {
            //TODO move this to an env object similar to Ant Environment
            envValue = System.getenv( envKey );
        }
        catch ( Error e )
        {
            //JDK 1.4?
        }

        if ( envValue == null )
        {
            envValue = defaultValue ;
        }
        
        return envValue;
    }
    
    public static String getEnv( String envKey, String alternateSystemProperty, String defaultValue )
    {
        String envValue = "";

        try
        {
            //TODO move this to an env object similar to Ant Environment
            envValue = System.getenv( envKey );
        }
        catch ( Error e )
        {
            //JDK 1.4?
            if ( alternateSystemProperty != null )
            {
                envValue = System.getProperty( alternateSystemProperty );
            }
        }

        if ( envValue == null )
        {
            envValue = defaultValue;
        }

        return envValue;
    }
}
