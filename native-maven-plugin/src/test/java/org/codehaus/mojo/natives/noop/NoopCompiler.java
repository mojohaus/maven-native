package org.codehaus.mojo.natives.noop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.mojo.natives.compiler.Compiler;

/**
 * Helper class to test native-maven-plugin
 * @author dtran
 *
 */
public class NoopCompiler
    implements Compiler
{

    public List compile( CompilerConfiguration config, File[] sourceFiles )
        throws NativeBuildException
    {
        // TODO Auto-generated method stub
        return new ArrayList( 0 );
    }

}
