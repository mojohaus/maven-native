package org.codehaus.mojo.natives.c;

import java.io.File;

import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * 
 * @author dtran
 *
 */
public class CCompilerTest
    extends PlexusTestCase
{
     public void testDefaultExecutable()
         throws Exception
     {
         CompilerConfiguration config = new CompilerConfiguration();
         CCompiler compiler = new CCompiler();
         Commandline cl = compiler.getCommandLine( new File( "source.c") , new File( "object.o" ) , config );
         assertEquals( "gcc -o object.o -c source.c", cl.toString() );
     }
}
