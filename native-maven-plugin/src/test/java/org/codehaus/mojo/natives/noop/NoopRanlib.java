package org.codehaus.mojo.natives.noop;


import java.io.File;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.Ranlib;

/**
 * Helper class to test native-maven-plugin' javah mojo
 */
public class NoopRanlib
    implements Ranlib 
{

    public void run( File file )
        throws NativeBuildException
    {

    }

}
