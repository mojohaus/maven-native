package org.codehaus.mojo.natives.bcc;

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
import org.codehaus.mojo.natives.linker.AbstractLinker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.util.List;


public class TLibLinker
    extends AbstractLinker
{

	public static final String EXECUTABLE = "tlib";

       
	protected Commandline createLinkerCommandLine( List objectFiles, LinkerConfiguration config )
        throws NativeBuildException
	{
	    Commandline cl = new Commandline();
        
	    cl.setWorkingDirectory( config.getWorkingDirectory().getPath() );

	    String executable = EXECUTABLE;
	    
	    if ( config.getExecutable() != null && config.getExecutable().trim().length() != 0 )
	    {
	    	executable = config.getExecutable();
	    }
	    
	    cl.createArg().setValue( executable );
	    
	    cl.createArg().setValue( "\"" + config.getOutputFile() + "\"" );

	    for ( int i = 0; i < config.getStartOptions().length; ++i )
	    {
	      cl.createArg().setValue( config.getStartOptions()[i] );
	    }

	    for ( int i = 0; i < objectFiles.size(); ++i )
	    {
	    	File objFile = (File) objectFiles.get(i);

		    cl.createArg().setValue( "+\"" + objFile.getPath() + "\"" );
	    }
        
	    return cl;
		
	}

}
