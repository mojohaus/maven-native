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
import org.codehaus.mojo.natives.util.EnvUtil;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Add BCC's bin directory to environment path
 * 
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */

public class BCCEnv 
{
	private static File DEFAULT_BCC_HOME = new File( "C:/Borland/bcc" );


	public static void setupBCCCommandLineEnv( File providerHome, Commandline cl )
	    throws NativeBuildException
	{
		File bccHome = checkBCCHome ( providerHome, DEFAULT_BCC_HOME );
				
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
	
    
	private static Map createAdditionalBCCEnvs( File bccDir )
	{
		Map envs = new HashMap();
		
		if ( bccDir == null )
		{
			return envs;
		}
		
		//setup new PATH
		String currentPath = EnvUtil.getEnv ( "PATH" );
		
		String newPath = bccDir.getPath()+"\\BIN;" + 
					     currentPath; 

		envs.put( "PATH", newPath );

		
		return envs;
		
	}
	
}
