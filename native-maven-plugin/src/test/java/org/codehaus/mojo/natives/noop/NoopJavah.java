package org.codehaus.mojo.natives.noop;


import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.javah.Javah;
import org.codehaus.mojo.natives.javah.JavahConfiguration;

/**
 * Helper class to test native-maven-plugin' javah mojo
 *
 */
public class NoopJavah
    implements Javah 
{

    public void compile( JavahConfiguration config )
        throws NativeBuildException
    {

    }

}
