package org.codehaus.mojo.natives.c;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.mojo.natives.util.EnvUtil;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * A simulator of CLinker to dump the contents of commandline to ouput file as a 
 * set of properties to be validated by test cases
 * @author dtran
 *
 */
public class CLinkerSimulator
    extends CLinker
{
    public File link( LinkerConfiguration config, List compilerOutputFiles ) 
        throws NativeBuildException, IOException
    {
        Commandline cl = this.createLinkerCommandLine( compilerOutputFiles, config );
        
        EnvUtil.setupCommandlineEnv( cl, config.getEnvFactoryName() );

        Properties properties = new Properties();
        
        properties.setProperty( "cli", cl.toString() );
        
        properties.setProperty( "workingDirectory", cl.getWorkingDirectory().getPath() );
       
        String [] envs = cl.getEnvironments();
        
        OutputStream o = new FileOutputStream( config.getOutputFilePath() );
        
        properties.store( o, "" );
        
        o.close();
 
        return new File( config.getOutputFilePath() );
    }

}
