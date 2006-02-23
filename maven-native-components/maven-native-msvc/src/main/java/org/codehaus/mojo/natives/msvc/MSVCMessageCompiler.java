package org.codehaus.mojo.natives.msvc;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.compiler.MessageCompilerConfiguration;
import org.codehaus.mojo.natives.compiler.AbstractMessageCompiler;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;

/**
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */
public class MSVCMessageCompiler 
    extends AbstractMessageCompiler
{
	
	protected Commandline getCommandLine( MessageCompilerConfiguration config, File source )
	   throws NativeBuildException
	{
				
		Commandline cl = new Commandline();
        
        cl.setExecutable( "mc" );
        
        cl.addArguments( config.getOptions() );
        
        if ( config.getOutputDirectory() != null && config.getOutputDirectory().getPath().trim().length() != 0 )
        {
            cl.createArgument().setValue( "-r" );
            cl.createArgument().setValue( config.getOutputDirectory().getPath() );

            cl.createArgument().setValue( "-h" );
            cl.createArgument().setValue( config.getOutputDirectory().getPath() );
            
        }

        if ( config.getDebugOutputDirectory() != null && config.getDebugOutputDirectory().getPath().trim().length() != 0 )
        {
            cl.createArgument().setValue( "-x" );
            cl.createArgument().setValue( config.getDebugOutputDirectory().getPath() );
        }
        
        cl.createArgument().setValue( source.getPath() );
        
		return cl;
	}

}
