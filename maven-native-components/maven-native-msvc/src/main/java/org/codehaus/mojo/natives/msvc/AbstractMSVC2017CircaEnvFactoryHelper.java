package org.codehaus.mojo.natives.msvc;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.util.cli.StreamConsumer;


/**
 * Encapsulate some behavior common to Visual Studio since 2017.
 */
class AbstractMSVC2017CircaEnvFactoryHelper {

    protected File createEnvWrapperFile(File vsInstallDir, String platform)
            throws IOException
    {
    
        File tmpFile = File.createTempFile( "msenv", ".bat" );

        String buffer = "@echo off\r\n"
                + "call \"" + vsInstallDir + "\""
                + "\\VC\\Auxiliary\\Build\\vcvarsall.bat " + platform + "\r\n"
                + "echo " + EnvStreamConsumer.START_PARSING_INDICATOR + "\r\n"
                + "set\r\n";
        FileUtils.fileWrite( tmpFile.getAbsolutePath(), buffer );
    
        return tmpFile;
    }

    protected Map<String, String> executeCommandLine(Commandline command) throws NativeBuildException
    {
        EnvStreamConsumer stdout = new EnvStreamConsumer();
        StreamConsumer stderr = new DefaultConsumer();
    
        try
        {
            CommandLineUtils.executeCommandLine( command, stdout, stderr );
        }
        catch ( CommandLineException e )
        {
            throw new NativeBuildException( "Failed to execute vcvarsall.bat" );
        }
    
        return stdout.getParsedEnv();
    }

    protected String queryVSInstallPath(String version)
    {
        return RegQuery.getValue(
                "REG_SZ",
                "HKLM\\SOFTWARE\\WOW6432Node\\Microsoft\\VisualStudio\\SxS\\VS7",
                version
        );
    }

}
