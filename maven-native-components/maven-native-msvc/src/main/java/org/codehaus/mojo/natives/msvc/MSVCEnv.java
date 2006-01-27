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

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Settup ENV according msvc  VCVARS32.BAT
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */
public class MSVCEnv 
{
	private static File DEFAULT_MSVC6_HOME = new File( "C:/Program Files/Microsoft Visual Studio" );

	private static File DEFAULT_MSVC2003_HOME = new File( "C:/Program Files/Microsoft Visual Studio .NET 2003" );

	public static void setupMSVC6CommandLineEnv( File vsDir, Commandline cl )
	    throws NativeBuildException
	{
		File msvc6Home = checkMSVCHome ( vsDir, DEFAULT_MSVC6_HOME );
				
		Map envs = createAdditionalMSVC6Envs ( msvc6Home );
		
		setupCommandlineEnv( envs, cl );
		
	}
	
	public static void setupMSVC2003CommandLineEnv( File vsDir, Commandline cl )
        throws NativeBuildException
    {
    	File msvc6Home = checkMSVCHome ( vsDir, DEFAULT_MSVC2003_HOME );
			
	    Map envs = createAdditionalMSVC2003Envs ( msvc6Home );
	
	    setupCommandlineEnv( envs, cl );
    }	
		
	private static File checkMSVCHome ( File userGivenHomeDir, File defaultHomeDir )
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
			//TODO move this to an env object similar to Ant Environment
			envValue = System.getenv( envKey );
		}
		catch ( Error e )
		{
			//JDK 1.4?
			//according to my tests, msvc should work even this fails
			
			//ignore
		}
		
		return envValue;
	}
    
	private static Map createAdditionalMSVC6Envs( File vsDir )
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

	static final String DEFAULT_VS2005_INSTALL_DIR = "c:/Program Files/Microsoft Visual Studio 8";
	
	private static Map createAdditionalMSVC2003Envs( File vcInstallDir )
	{
		
		Map envs = new HashMap();
				
		File vsInstallDir = new File( vcInstallDir.getPath() + "/Common7/IDE" );
		
		//TODO get winhome dir
		File frameworkDir= new File( "c:/WINDOWS/Microsoft.NET/Framework" );
		envs.put( "FrameworkDir", frameworkDir.getPath() );
		
		File frameworkSDKDir= new File( vcInstallDir.getPath() + "/SDK/v1.1" );
		envs.put( "FrameworkSDKDir", frameworkSDKDir.getPath() );
				
		String frameworkVersion= "v1.1.4322";
		envs.put( "frameworkVersion", frameworkVersion );
		
		File devEnvDir= vsInstallDir;
		
		File msvcDir= new File ( vcInstallDir.getPath() + "/VC7" );
				
		//setup new PATH
		String currentPath = getEnv ( "PATH" );
				
		String newPath = devEnvDir.getPath()+";" + 
					     msvcDir.getPath() + "\\BIN;" + 
					     vcInstallDir.getPath() + "\\Common7\\Tools;" +
					     vcInstallDir.getPath() + "\\Common7\\Tools\\bin\\prerelease;" +
					     vcInstallDir.getPath() + "\\Common7\\Tools\\bin;" +
					     frameworkSDKDir.getPath() + "\\bin;" + 
					     frameworkDir.getPath() + "\\" + frameworkVersion + ";" + 
					     currentPath; 

		envs.put( "PATH", newPath );
		
		//setup new INCLUDE PATH
		String currentIncludePath = getEnv( "INCLUDE" );
		
		String newIncludePath = msvcDir.getPath() + "\\ATLMFC\\INCLUDE;" + 
                                msvcDir.getPath() + "\\INCLUDE;" + 
				                msvcDir.getPath() + "\\PlatformSDK\\include\\prerelease;" + 
				                msvcDir.getPath() + "\\PlatformSDK\\include;" + 
				                frameworkSDKDir.getPath() + "\\include;" +
                                currentIncludePath ; 

		envs.put( "INCLUDE", newIncludePath );
		

		//
		//setup new LIB PATH
		//
		String currentLibPath = getEnv( "LIB" );
		
		String newLibPath = msvcDir.getPath() + "\\ATLMFC\\LIB;" + 
				            msvcDir.getPath() + "\\LIB;" + 
				            msvcDir.getPath() + "\\PlatformSDK\\lib\\prerelease;" + 
				            msvcDir.getPath() + "\\PlatformSDK\\lib;" + 
                            currentLibPath ; 		
		
		envs.put( "LIB", newLibPath );
		
		return envs;
		
	}
    
	private static Map createAdditionalMSVC2005x86Envs( File vsInstallDir )
	{
		Map envs = new HashMap();
				
		envs.put( "VSINSTALLDIR", vsInstallDir.getPath() );
		
		File vcInstallDir = new File( vsInstallDir.getPath() + "/VC" );	
		envs.put( "VCINSTALLDIR", vcInstallDir.getPath() );
		
		//TODO get winhome dir
		File frameworkDir= new File( "c:/WINDOWS/Microsoft.NET/Framework" );
		envs.put( "FrameworkDir", frameworkDir.getPath() );

		String frameworkVersion= "v2.0.50727";
		envs.put( "FrameworkVersion", frameworkVersion );

		File frameworkSDKDir= new File( vsInstallDir.getPath() + "/SDK/v2.0" );
		envs.put( "FrameworkVersion", frameworkVersion );
				
		File devEnvDir= new File( vsInstallDir.getPath() + "/Common7/IDE" );
		envs.put( "DevEnvDir", devEnvDir );

		File platformSDKDir= new File( vcInstallDir.getPath() + "/PlatformSDK" );
		
		//setup new PATH
		String currentPath = getEnv ( "PATH" );
				
		String newPath = devEnvDir.getPath()+";" + 
		                 vcInstallDir.getPath() + "\\BIN;" + 
		                 vcInstallDir.getPath() + "\\Common7\\Tools;" +
		                 vcInstallDir.getPath() + "\\Common7\\Tools\\bin;" +
		                 platformSDKDir.getPath() + "\\BIN;" + 
					     frameworkSDKDir.getPath() + "\\BIN;" + 
					     frameworkDir.getPath() + "\\" + frameworkVersion + ";" + 
					     vcInstallDir.getPath() + "\\VCPackages;" + 
					     currentPath; 

		envs.put( "PATH", newPath );
		
		//setup new INCLUDE PATH
		String currentIncludePath = getEnv( "INCLUDE" );
		
		String newIncludePath = vcInstallDir.getPath() + "\\ATLMFC\\INCLUDE;" + 
		                        vcInstallDir.getPath() + "\\INCLUDE;" + 
				                platformSDKDir.getPath() + "\\INCLUDE;" +
				                frameworkSDKDir.getPath() + "\\INCLUDE;" +
                                currentIncludePath ; 

		envs.put( "INCLUDE", newIncludePath );
		

		//
		//setup new LIB PATH
		//
		String currentLibPath = getEnv( "LIB" );
		
		String newLibPath = vcInstallDir.getPath() + "\\ATLMFC\\LIB;" + 
		                    vcInstallDir.getPath() + "\\LIB;" + 
		                    platformSDKDir.getPath() + "\\LIB;" + 
			                frameworkSDKDir.getPath() + "\\LIB;" +
                            currentLibPath ; 		
		
		envs.put( "LIB", newLibPath );
		
		//
		//setup new LIBPATH
		//

		String currentLibPathPath = frameworkDir.getPath() + "\\" + frameworkVersion + ";" +
		                            vcInstallDir.getPath() + "\\ATLMFC\\LIB";

		envs.put( "LIBPATH", currentLibPathPath );

		return envs;
		
	}
	
	private static Map createAdditionalMSVC2005x86AMD64Envs( File vsInstallDir )
	{
		Map envs = new HashMap();
				
		envs.put( "VSINSTALLDIR", vsInstallDir.getPath() );
		
		File vcInstallDir = new File( vsInstallDir.getPath() + "/VC" );	
		envs.put( "VCINSTALLDIR", vcInstallDir.getPath() );
		
		//TODO get winhome dir
		File frameworkDir= new File( "c:/WINDOWS/Microsoft.NET/Framework" );
		envs.put( "FrameworkDir", frameworkDir.getPath() );

		String frameworkVersion= "v2.0.50727";
		envs.put( "FrameworkVersion", frameworkVersion );

		File frameworkSDKDir= new File( vsInstallDir.getPath() + "/SDK/v2.0" );
		envs.put( "FrameworkVersion", frameworkVersion );
				
		File devEnvDir= new File( vsInstallDir.getPath() + "/Common7/IDE" );
		envs.put( "DevEnvDir", devEnvDir );

		File platformSDKDir= new File( vcInstallDir.getPath() + "/PlatformSDK" );
		
		//setup new PATH
		String currentPath = getEnv ( "PATH" );
				
		String newPath = devEnvDir.getPath()+";" + 
                         vcInstallDir.getPath() + "\\BIN\\x86_amd64;" + 
		                 vcInstallDir.getPath() + "\\BIN;" + 
		                 vcInstallDir.getPath() + "\\Common7\\Tools;" +
		                 vcInstallDir.getPath() + "\\Common7\\Tools\\bin;" +
		                 platformSDKDir.getPath() + "\\BIN;" + 
					     frameworkSDKDir.getPath() + "\\BIN;" + 
					     frameworkDir.getPath() + "\\" + frameworkVersion + ";" + 
					     vcInstallDir.getPath() + "\\VCPackages;" + 
					     currentPath; 

		envs.put( "PATH", newPath );
		
		//setup new INCLUDE PATH
		String currentIncludePath = getEnv( "INCLUDE" );
		
		String newIncludePath = vcInstallDir.getPath() + "\\ATLMFC\\INCLUDE;" + 
		                        vcInstallDir.getPath() + "\\INCLUDE;" + 
				                platformSDKDir.getPath() + "\\INCLUDE;" +
				                frameworkSDKDir.getPath() + "\\INCLUDE;" +
                                currentIncludePath ; 

		envs.put( "INCLUDE", newIncludePath );
		

		//
		//setup new LIB PATH
		//
		String currentLibPath = getEnv( "LIB" );
		
		String newLibPath = vcInstallDir.getPath() + "\\ATLMFC\\LIB\\AMD64;" + 
		                    vcInstallDir.getPath() + "\\LIB\\AMD64;" + 
		                    platformSDKDir.getPath() + "\\LIB\\AMD64;" + 
			                frameworkSDKDir.getPath() + "\\LIB\\AMD64;" +
                            currentLibPath ; 		
		
		envs.put( "LIB", newLibPath );
		
		//
		//setup new LIBPATH
		//

		String currentLibPathPath = getEnv( "LIBPATH" );
		
		String newLibPathPath = vcInstallDir.getPath() + "\\ATLMFC\\LIB\\AMD64" +
                                currentLibPathPath ; 		

		envs.put( "LIBPATH", newLibPathPath );

		return envs;
		
	}
	
	private static Map createAdditionalMSVC2005AMD64Envs( File vsInstallDir )
	{
		Map envs = new HashMap();
				
		envs.put( "VSINSTALLDIR", vsInstallDir.getPath() );
		
		File vcInstallDir = new File( vsInstallDir.getPath() + "/VC" );	
		envs.put( "VCINSTALLDIR", vcInstallDir.getPath() );
		
		//TODO get winhome dir
		File frameworkDir= new File( "c:/WINDOWS/Microsoft.NET/Framework64" );
		envs.put( "FrameworkDir", frameworkDir.getPath() );

		String frameworkVersion= "v2.0.50727";
		envs.put( "FrameworkVersion", frameworkVersion );

		File frameworkSDKDir= new File( vsInstallDir.getPath() + "/SDK/v2.0 64bit" );
		envs.put( "FrameworkVersion", frameworkVersion );
				
		File devEnvDir= new File( vsInstallDir.getPath() + "/Common7/IDE" );
		envs.put( "DevEnvDir", devEnvDir );

		File platformSDKDir= new File( vcInstallDir.getPath() + "/PlatformSDK" );
		
		//setup new PATH
		String currentPath = getEnv ( "PATH" );
				
		String newPath = vcInstallDir.getPath() + "\\BIN\\amd64;" + 
                         platformSDKDir.getPath() + "\\BIN\\WIN64\\AMD;" + 
		                 platformSDKDir.getPath() + "\\BIN;" + 
					     frameworkDir.getPath() + "\\" + frameworkVersion + ";" + 
					     vcInstallDir.getPath() + "\\VCPackages;" + 
                         devEnvDir.getPath()+";" + 
		                 vcInstallDir.getPath() + "\\Common7\\Tools;" +
					     frameworkSDKDir.getPath() + "\\BIN;" +
					     currentPath; 

		envs.put( "PATH", newPath );
		
		//setup new INCLUDE PATH
		String currentIncludePath = getEnv( "INCLUDE" );
		
		String newIncludePath = vcInstallDir.getPath() + "\\ATLMFC\\INCLUDE;" + 
		                        vcInstallDir.getPath() + "\\INCLUDE;" + 
				                platformSDKDir.getPath() + "\\INCLUDE;" +
				                frameworkSDKDir.getPath() + "\\INCLUDE;" +
                                currentIncludePath ; 

		envs.put( "INCLUDE", newIncludePath );
		

		//
		//setup new LIB PATH
		//
		String currentLibPath = getEnv( "LIB" );
		
		String newLibPath = vcInstallDir.getPath() + "\\ATLMFC\\LIB\\AMD64;" + 
		                    vcInstallDir.getPath() + "\\LIB\\AMD64;" + 
		                    platformSDKDir.getPath() + "\\LIB\\AMD64;" + 
			                frameworkSDKDir.getPath() + "\\LIB\\AMD64;" +
                            currentLibPath ; 		
		
		envs.put( "LIB", newLibPath );
		
		//
		//setup new LIBPATH
		//

		String currentLibPathPath = getEnv( "LIBPATH" );
		
		String newLibPathPath = vcInstallDir.getPath() + "\\ATLMFC\\LIB\\AMD64" +
                             currentLibPathPath ; 		

		envs.put( "LIBPATH", newLibPathPath );

		return envs;
		
	}
	
}
