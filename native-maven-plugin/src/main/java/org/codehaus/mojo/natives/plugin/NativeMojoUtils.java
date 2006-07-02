package org.codehaus.mojo.natives.plugin;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */

public class NativeMojoUtils
{
    /**
     * Remove/trim empty or null member of a string array
     * @param args
     * @return
     */
    public static String [] trimParams( List args )
    {
        if ( args == null )
        {
            return new String[0];
        }

        List tokenArray = new ArrayList();

        for ( int i = 0; i < args.size(); ++i )
        {           
            String arg = (String) args.get( i );
            
            if ( arg == null || arg.length() == 0 )
            {
                continue;
            }
            
            String [] tokens = StringUtils.split( arg );

            for ( int k = 0 ; k < tokens.length; ++k )
            {           
                if ( tokens[k] == null || tokens[k].trim().length() == 0 )
                {
                    continue;
                }
                
                tokenArray.add( tokens[k].trim() );             
            }
        }
        
        return (String []) tokenArray.toArray( new String[0] );
    }
    
}
