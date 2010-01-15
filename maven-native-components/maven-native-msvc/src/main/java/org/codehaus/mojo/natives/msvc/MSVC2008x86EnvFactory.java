package org.codehaus.mojo.natives.msvc;

/*
 * The MIT License
 * 
 * Copyright (c) 2004, The Codehaus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.util.EnvUtil;

/**
 * Microsoft Visual Studio 9.0\Common7\Tools\vsvars32.bat environment
 *
 */

public class MSVC2008x86EnvFactory
    extends AbstractMSVCEnvFactory
{
    private static final String MSVS2008_INSTALL_ENV_KEY = "MSVS2008_INSTALL_DIR";

    private static final String DEFAULT_MSVS2008_INSTALL_DIR = getProgramFilesX86() + "\\Microsoft Visual Studio 9.0";

    private static final String VS90COMNTOOLS_DIR = EnvUtil.getEnv( "VS90COMNTOOLS", "VS90COMNTOOLS",
                                                                    "VS90COMNTOOLS_DIR" );

    protected Map createEnvs()
        throws NativeBuildException
    {
        Map envs = new HashMap();

        File vsInstallDir = new File( EnvUtil.getEnv( MSVS2008_INSTALL_ENV_KEY, MSVS2008_INSTALL_ENV_KEY,
                                                      DEFAULT_MSVS2008_INSTALL_DIR ) );
        if ( !vsInstallDir.isDirectory() )
        {
            throw new NativeBuildException( vsInstallDir.getPath() + " is not a directory." );
        }
        envs.put( "VSINSTALLDIR", vsInstallDir.getPath() );

        File vcInstallDir = new File( vsInstallDir.getPath() + "\\VC" );
        if ( !vcInstallDir.isDirectory() )
        {
            throw new NativeBuildException( vcInstallDir.getPath() + " is not a directory." );
        }
        envs.put( "VCINSTALLDIR", vcInstallDir.getPath() );

        File frameworkDir = new File( getSystemRoot() + "\\Microsoft.NET\\Framework" );
        envs.put( "FrameworkDir", frameworkDir.getPath() );

        File windowsSDKDir = new File( "C:\\Program Files" + "\\Microsoft SDKs\\Windows\\v6.0A" );
        String value = RegQuery.getValue( "REG_SZ", "HKLM\\SOFTWARE\\Microsoft\\Microsoft SDKs\\Windows", "CurrentInstallFolder" );
        if ( value == null )
        {
            windowsSDKDir = new File( value );
        }
        
        envs.put( "WindowsSdkDir", windowsSDKDir.getPath() );

        String frameworkVersion = "v2.0.50727";
        envs.put( "FrameworkVersion", frameworkVersion );

        String framework35Version = "v3.5";
        envs.put( "Framework35Version", framework35Version );

        String devEnvDir = VS90COMNTOOLS_DIR + "..\\IDE";
        envs.put( "DevEnvDir", devEnvDir );

        //set "PATH=%WindowsSdkDir%bin;%PATH%"
        //@set PATH=C:\Program Files (x86)\Microsoft Visual Studio 9.0\Common7\IDE;C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\BIN;C:\Program Files (x86)\Microsoft Visual Studio 9.0\Common7\Tools;C:\Windows\Microsoft.NET\Framework\v3.5;C:\Windows\Microsoft.NET\Framework\v2.0.50727;C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\VCPackages;%PATH%

        //setup new PATH
        String currentPathEnv = System.getProperty( "java.library.path" );

        String newPathEnv = devEnvDir + ";" + vcInstallDir.getPath() + "\\bin" + ";" + VS90COMNTOOLS_DIR + ";"
            + frameworkDir + "\\" + framework35Version + ";" + frameworkDir + "\\" + frameworkVersion + ";"
            + vcInstallDir.getPath() + "\\VCPackages" + ";" + windowsSDKDir.getPath() + "\\bin;" + currentPathEnv;

        envs.put( "PATH", newPathEnv );

        //setup new INCLUDE PATH
        //@set INCLUDE=C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\ATLMFC\INCLUDE;C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\INCLUDE;%INCLUDE%

        String currentIncludeEnv = EnvUtil.getEnv( "INCLUDE" );

        String newIncludeEnv = vcInstallDir.getPath() + "\\ATLMFC\\INCLUDE;" + vcInstallDir.getPath() + "\\INCLUDE;"
            + windowsSDKDir.getPath() + "\\include;" + currentIncludeEnv;

        envs.put( "INCLUDE", newIncludeEnv );

        //
        //setup new LIB PATH
        //@set LIB=C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\ATLMFC\LIB;C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\LIB;%LIB%
        //
        String currentLibEnv = EnvUtil.getEnv( "LIB" );

        String newLibEnv = vcInstallDir.getPath() + "\\ATLMFC\\LIB;" + vcInstallDir.getPath() + "\\LIB;"
            + windowsSDKDir.getPath() + "\\LIB;" + currentLibEnv;

        envs.put( "LIB", newLibEnv );

        //@set LIBPATH=C:\Windows\Microsoft.NET\Framework\v3.5;C:\Windows\Microsoft.NET\Framework\v2.0.50727;C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\ATLMFC\LIB;C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\LIB;%LIBPATH%

        String currentLibPathEnv = EnvUtil.getEnv( "LIBPATH" );

        String newLibPathEnv = frameworkDir + "\\" + framework35Version + ";" + frameworkDir + "\\" + frameworkVersion
            + ";" + vcInstallDir.getPath() + "\\ATLMFC\\LIB;" + vcInstallDir.getPath() + "\\LIB;" + currentLibPathEnv;

        envs.put( "LIBPATH", newLibPathEnv );

        return envs;

    }

}
