package org.codehaus.mojo.natives.noop;

import java.io.File;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = Linker.class, hint = "noop")
public class NoopLinker
    implements Linker
{

    public File link( LinkerConfiguration config, List compilerOutputFiles )
        throws NativeBuildException
    {
        String fileName = config.getOutputFileName();

        if ( config.getOutputFileExtension() != null )
        {
            fileName += "." + config.getOutputFileExtension();
        }
        return new File( config.getOutputDirectory(), fileName );
    }

}
