package org.codehaus.mojo.natives.bcc;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import java.io.File;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Add BCC's bin directory to environment path
 * @author dtran
 *
 */

public class BCCEnv 
{
	private static File DEFAULT_BCC_HOME = new File( "C:/Borland/bcc" );


	public static void setupBCCCommandLineEnv( File vsDir, Commandline cl )
	    throws NativeBuildException
	{
		File bccHome = checkBCCHome ( vsDir, DEFAULT_BCC_HOME );
				
		Map envs = createAdditionalBCCEnvs ( bccHome );
		
		setupCommandlineEnv( envs, cl );
		
	}
	
	private static File checkBCCHome ( File userGivenHomeDir, File defaultHomeDir )
	{
		File homeDir = userGivenHomeDir;
		
		if ( homeDir == null || !homeDir.isDirectory() )
		{
			homeDir =  defaultHomeDir;
			
			if ( ! homeDir.isDirectory() )
			{
				String message = "User given: " + userGivenHomeDir + 
				                  " or the fall back " + defaultHomeDir + 
				                  " directory is not available.";
				
				new NativeBuildException( message );
			}
		}				
		
		return homeDir;
				
	}
	
	private static void setupCommandlineEnv( Map envs, Commandline cl )
	{
		Iterator iter = envs.keySet().iterator();
		
		while ( iter.hasNext() )
		{
			String key = (String ) iter.next();
			
			cl.addEnvironment( key, (String) envs.get( key ) );
		}
	}
	
    private static String getEnv( String envKey ) 
	{
		String envValue = "";
		
		try 
		{
			//TODO move this to an env object to will work for JVM 1.4.x
			//  similar to Ant Environment
			envValue = System.getenv( envKey );
		}
		catch ( Error e )
		{
			//according to my tests, it should work even this fails
			
			//ignore
		}
		
		return envValue;
	}
    
	private static Map createAdditionalBCCEnvs( File bccDir )
	{
		Map envs = new HashMap();
		
		if ( bccDir == null )
		{
			return envs;
		}
		
		//setup new PATH
		String currentPath = getEnv ( "PATH" );
		
		String newPath = bccDir.getPath()+"\\BIN;" + 
					     currentPath; 

		envs.put( "PATH", newPath );

		
		return envs;
		
	}
	
}
