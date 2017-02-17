package org.codehaus.mojo.natives.noop;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.javah.Javah;
import org.codehaus.mojo.natives.javah.JavahConfiguration;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Helper class to test native-maven-plugin' javah mojo
 */
@Component(role = Javah.class, hint = "noop")
public class NoopJavah
    implements Javah
{

    public void compile( JavahConfiguration config )
        throws NativeBuildException
    {

    }

}
