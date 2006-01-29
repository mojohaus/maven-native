package org.codehaus.mojo.natives.util;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.plexus.util.cli.Commandline;

public class EnvUtil
{
    public static String getEnv( String envKey )
    {
        return getEnv( envKey, "" );
    }

    public static String getEnv( String envKey, String defaultValue )
    {
        return getEnv( envKey, null, defaultValue );
    }

    public static String getEnv( String envKey, String alternateSystemProperty, String defaultValue )
    {
        String envValue = "";

        try
        {
            //TODO move this to an env object similar to Ant Environment
            envValue = System.getenv( envKey );

            if ( envValue == null &&  alternateSystemProperty != null )
            {
                envValue = System.getProperty( alternateSystemProperty );
            }
        }
        catch ( Error e )
        {
            //JDK 1.4?
            if ( alternateSystemProperty != null )
            {
                envValue = getProperty( alternateSystemProperty );
            }
        }

        if ( envValue == null )
        {
            envValue = defaultValue;
        }

        return envValue;
    }

    private static String getProperty( String key )
    {
        if ( key != null )
        {
            return System.getProperty( key );
        }

        return null;
    }

    public static void setupCommandlineEnv( Map envs, Commandline cl )
    {
        Iterator iter = envs.keySet().iterator();

        while ( iter.hasNext() )
        {
            String key = (String) iter.next();

            cl.addEnvironment( key, (String) envs.get( key ) );
        }
    }

}
