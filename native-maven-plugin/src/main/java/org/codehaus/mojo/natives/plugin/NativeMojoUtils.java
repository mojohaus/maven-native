package org.codehaus.mojo.natives.plugin;

/*
 * The MIT License 
 *
 * Copyright (c) 2005-2006, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;


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
        
        return (String []) tokenArray.toArray( new String[ tokenArray.size() ] );
    }
    
}
