package org.codehaus.mojo.natives.compiler;

import java.io.File;
import org.codehaus.plexus.util.cli.Commandline;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.parser.Parser;
import org.codehaus.mojo.natives.parser.CParser;

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

/**
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */

public abstract class AbstractCCompiler 
    extends AbstractCompiler
{
	//resuable parser in one Compilation session
	
	private Parser parser = new CParser();
	
	protected abstract String getOutputFileOption();
	

	protected Parser getParser() 
	{
		return this.parser;
	}
	protected Commandline getCommandLine(File src, File dest, CompilerConfiguration config )
	    throws NativeBuildException
	{
        if ( config.getExecutable() == null || config.getExecutable().trim().length() == 0 )
        {
            config.setExecutable ( "gcc" );
        }
        
	    Commandline cl = new Commandline();
	    
	    cl.setWorkingDirectory( config.getBaseDir().getPath() );

	    cl.createArgument().setValue( config.getExecutable() );
	    
	    for ( int i =0 ;i < config.getStartOptions().length; ++i ) 
	    {
	    	cl.createArgument().setValue( config.getStartOptions()[i]);
	    }
	    
	    File [] includePaths = config.getIncludePaths();
	    
	    for ( int i = 0 ; i < includePaths.length; ++i )
	    {
	    	cl.createArgument().setValue( "-I" + includePaths[i].getPath() );
	    }

	    File [] systemIncludePaths = config.getSystemIncludePaths();
	    
	    for ( int i = 0 ; i < systemIncludePaths.length; ++i )
	    {
	    	cl.createArgument().setValue( "-I" + systemIncludePaths[i].getPath() );
	    }	    
	    
	    String outputFileOption = this.getOutputFileOption();

        if ( outputFileOption.endsWith( " " ) )
        {
            cl.createArgument().setValue( outputFileOption.trim() );
            cl.createArgument().setValue( dest.getPath() );
        }
        else
        {
            cl.createArgument().setValue( outputFileOption + dest.getPath() );
        }      
        
	    cl.createArgument().setValue("-c");
	    cl.createArgument().setValue( src.getPath() );

	    return cl;
	}
    

}
