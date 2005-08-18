package org.codehaus.mojo.natives.msvc;

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

import org.codehaus.plexus.util.cli.Commandline;

/**
 * Settup ENV according msvc 6.0's VCVARS32.BAT
 * @author dtran
 *
 */
public class MSVCEnv 
{
	public static void setCommandLineEnv( File vsDir, Commandline cl )
	{
		Map envs = createAdditionEnvs ( vsDir );
		
		Iterator iter = envs.keySet().iterator();
		
		while ( iter.hasNext() )
		{
			String key = (String ) iter.next();
			
			cl.addEnvironment( key, (String) envs.get( key ) );
		}
	}
		
	public static Map createAdditionEnvs( File vsDir )
	{
		Map envs = new HashMap();
		
		if ( vsDir == null )
		{
			return envs;
		}
		
		String vcOsDir = "WINNT";
		
		String winDir = getEnv( "windir" );
		
		File vsCommonDir = new File ( vsDir + "/Common" );
		
		File vsCommonToolDir = new File ( vsCommonDir + "/TOOLS" );
		
		File msDevDir = new File ( vsCommonDir + "/msdev98" );
		
		File msvcDir = new File ( vsDir + "/VC98" );
		
		envs.put( "MSVCDir", msvcDir.getPath() );
		
		//setup new PATH
		String currentPath = getEnv ( "PATH" );
		
		String newPath = msDevDir.getPath()+"\\BIN;" + 
					     msvcDir.getPath() + "\\BIN;" + 
					     vsCommonToolDir.getPath() + "\\" + vcOsDir + ";" +
					     vsCommonToolDir.getPath() + ";" + 
					     winDir + ";" + 
					     currentPath; 

		envs.put( "PATH", newPath );

		//setup new INCLUDE PATH
		String currentIncludePath = getEnv( "INCLUDE" );
		
		String newIncludePath = msvcDir.getPath() + "\\ATL\\INCLUDE;" + 
                                msvcDir.getPath() + "\\INCLUDE;" + 
				                msvcDir.getPath() + "\\MFC\\INCLUDE;" + 
                                vsCommonToolDir.getPath() + vcOsDir + ";" +
                                vsCommonToolDir.getPath() + ";" + 
                                currentIncludePath ; 

		envs.put( "INCLUDE", newIncludePath );

		//
		//setup new LIB PATH
		//
		String currentLibPath = getEnv( "LIB" );
		
		String newLibPath = msvcDir.getPath() + "\\LIB;" + 
				            msvcDir.getPath() + "\\MFC\\LIB;" + 
                            currentLibPath ; 		
		
		envs.put( "LIB", newLibPath );
		
		return envs;
		
	}
	
    private static String getEnv( String envKey ) 
	{
		String envValue = "";
		
		try 
		{
			envValue = System.getenv( envKey );
		}
		catch ( Error e )
		{
			//according to my tests, msvc should work even this fails
			
			//ignore
		}
		
		return envValue;
	}
	
}
