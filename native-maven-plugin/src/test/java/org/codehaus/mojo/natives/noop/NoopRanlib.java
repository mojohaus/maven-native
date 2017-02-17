package org.codehaus.mojo.natives.noop;

import java.io.File;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.Ranlib;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Helper class to test native-maven-plugin' javah mojo
 */
@Component(role = Ranlib.class, hint = "noop")
public class NoopRanlib
    implements Ranlib
{

    public void run( File file )
        throws NativeBuildException
    {

    }

}
