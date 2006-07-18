package org.codehaus.mojo.natives.util;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.logging.Logger;

public class CommandLineUtil 
{
	public static void execute( Commandline cl, Logger logger ) 
		throws NativeBuildException
	{
		int ok;
		
		try 
		{
			DefaultConsumer stdout = new DefaultConsumer();

			DefaultConsumer stderr = stdout;
			
			logger.info( cl.toString() );
			
			ok = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
		}
		catch ( CommandLineException ecx) 
		{
			throw new NativeBuildException( "Error executing command line", ecx );
		}
        
		if ( ok != 0 )
		{
			throw new NativeBuildException( "Error executing command line. Exit code:" + ok );
		}		
	}
}
