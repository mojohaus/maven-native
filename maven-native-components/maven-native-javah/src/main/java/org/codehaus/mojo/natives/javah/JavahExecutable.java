package org.codehaus.mojo.natives.javah;

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
import org.codehaus.mojo.natives.util.CommandLineUtil;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.Commandline;


import java.io.File;

import java.util.List;

/**
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */

public class JavahExecutable 
    extends AbstractJavah 
{
	public JavahExecutable()
	{
	}
	
	public List compile( JavahConfiguration config ) 
	    throws NativeBuildException
	{
		Commandline cl = this.createJavahCommand( config );
		
		CommandLineUtil.execute( cl, this.getLogger() );
        
		return null;
	}
	
	private Commandline createJavahCommand( JavahConfiguration config) 
	    throws NativeBuildException
	{
		File javahExecutable = this.getJavaHExecutable();
		
		if ( ! javahExecutable.exists() )
		{
            String message = "Unable to locate the javah executable. " +
            	             "Please ensure you are using JDK 1.4 or above and\n" +
                             "not a JRE.\n";
        
            throw new NativeBuildException(message);
		}
		
	    Commandline cl = new Commandline();
	    
	    cl.createArgument().setValue( javahExecutable.getPath() );

        if ( config.getFileName() != null && config.getFileName().length() > 0 )
        {
            File outputFile = new File( config.getDestdir() + "/" + config.getFileName() );
            cl.createArgument().setValue( "-o" );
            cl.createArgument().setFile( outputFile );
            
        }
        else
        {
	        if ( config.getDestdir() != null && config.getDestdir().length() > 0 )
	        {
	    	    cl.createArgument().setValue( "-d" );
	    	    cl.createArgument().setValue( config.getDestdir() );
	        }
        }

        char classPathSeparator = System.getProperty( "path.separator" ).charAt( 0 );
        
        String [] classPaths = config.getClassPaths();
        
        StringBuffer classPathBuffer = new StringBuffer();
        
        for ( int i = 0 ; i < classPaths.length ; ++ i )
        {
            classPathBuffer.append( classPaths[i] );
            if ( i != classPaths.length - 1 )
            {
                classPathBuffer.append( classPathSeparator );
            }
        }

     	cl.createArgument().setValue( "-classpath" );
	    	
	   	cl.createArgument().setValue( classPathBuffer.toString() );

	    if ( config.getVerbose() )
	    {
	    	cl.createArgument().setValue( "-verbose" );
	    }

        cl.addArguments( config.getClassNames() );
	    
        return cl;
	}
	
	protected File getJavaHExecutable()
	{
		String javahExt = "";
		
		if ( Os.isFamily( "windows") )
		{
			javahExt = ".exe";
		}

		return new File( System.getProperty( "java.home" ), "../bin/javah" + javahExt );
	}

}
