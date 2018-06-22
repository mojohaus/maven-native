package org.codehaus.mojo.natives.msvc;

import org.codehaus.mojo.natives.AbstractEnvFactory;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class AbstractCommunityEnvFactory extends AbstractEnvFactory
{

    protected Map<String, String> createEnvs(String version, String platform)
            throws NativeBuildException
    {
        File tmpEnvExecFile = null;
        try
        {
            String vsCommunityPath = queryVSInstallPath( version );

            if ( vsCommunityPath == null )
            {
                throw new NativeBuildException(
                        String.format( "Can not find VS Community version '%s'", version )
                );
            }
            if ( !vsCommunityPath.endsWith( "Community\\" ) && !vsCommunityPath.endsWith( "Community" ) )
            {
                throw new NativeBuildException(
                        String.format( "Directory '%s' is not a VS Community directory", vsCommunityPath )
                );
            }
            File communityDir = new File( vsCommunityPath );
            if ( !communityDir.isDirectory() )
            {
                throw new NativeBuildException(
                        String.format( "Path '%s' is not a directory", vsCommunityPath )
                );
            }

            tmpEnvExecFile = this.createEnvWrapperFile( communityDir, platform );

            Commandline cl = new Commandline();
            cl.setExecutable( tmpEnvExecFile.getAbsolutePath() );

            return executeCommandLine( cl );

        } catch ( NativeBuildException e )
        {
            throw e;
        } catch ( Exception e )
        {
            throw new NativeBuildException( "Unable to retrieve env", e );
        } finally
        {
            if ( tmpEnvExecFile != null )
            {
                tmpEnvExecFile.delete();
            }
        }
    }

    protected String queryVSInstallPath(String version)
    {
        return RegQuery.getValue(
                "REG_SZ",
                "HKLM\\SOFTWARE\\WOW6432Node\\Microsoft\\VisualStudio\\SxS\\VS7",
                version
        );
    }

    protected Map<String, String> executeCommandLine(Commandline command) throws NativeBuildException
    {
        EnvStreamConsumer stdout = new EnvStreamConsumer();
        StreamConsumer stderr = new DefaultConsumer();

        try
        {
            CommandLineUtils.executeCommandLine( command, stdout, stderr );
        } catch ( CommandLineException e )
        {
            throw new NativeBuildException( "Failed to execute vcvarsall.bat" );
        }

        return stdout.getParsedEnv();
    }

    private File createEnvWrapperFile(File vsInstallDir, String platform)
            throws IOException
    {

        File tmpFile = File.createTempFile( "msenv", ".bat" );

        StringBuffer buffer = new StringBuffer();
        buffer.append( "@echo off\r\n" );
        buffer.append( "call \"" ).append( vsInstallDir ).append( "\"" )
                .append( "\\VC\\Auxiliary\\Build\\vcvarsall.bat " + platform + "\r\n" );
        buffer.append( "echo " + EnvStreamConsumer.START_PARSING_INDICATOR ).append( "\r\n" );
        buffer.append( "set\r\n" );
        FileUtils.fileWrite( tmpFile.getAbsolutePath(), buffer.toString() );

        return tmpFile;
    }
}
