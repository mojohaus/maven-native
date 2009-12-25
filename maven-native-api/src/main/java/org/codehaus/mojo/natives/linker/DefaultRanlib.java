package org.codehaus.mojo.natives.linker;

import java.io.File;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.util.CommandLineUtil;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.cli.Commandline;

public class DefaultRanlib
    extends AbstractLogEnabled
    implements Ranlib
{
    public void run( File file )
        throws NativeBuildException
    {
        Commandline cl = new Commandline();

        cl.setExecutable( "ranlib" );

        cl.createArg().setValue( file.getAbsolutePath() );

        CommandLineUtil.execute( cl, this.getLogger() );
    }
}
