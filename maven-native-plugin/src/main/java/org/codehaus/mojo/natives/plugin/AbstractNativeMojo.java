package org.codehaus.mojo.natives.plugin;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.natives.NativeSources;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id:$
 */
public abstract class AbstractNativeMojo
    extends AbstractMojo
{
	
    /**
     * @parameter 
     * @optional
     * @description Some compiler can take advantage of this setting to add 
     *              additional environments ( ex msvc, bcc, etc)
     */
    protected File providerHome;
	
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
	
	
    /**
     * @description where to place the final packaging
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    protected File outputDirectory;

    /**
     * TODO component needs to handle this
     * @parameter expression="${basedir}
     * @required
     * @readonly
     */
	protected File basedir;

    
    /**
     * Compilable source files see TODO api of NativeSource here
     * @parameter 
     * 
     */
    protected NativeSources [] sources;
    
   /**
     * @parameter expression="${objectFileExtension}" default-value="o"
     * @description The extension of object file name. The default extension should work for all compilers
     * @optional
     */
    protected String objectFileExtension;    
    
    
    protected static String [] removeEmptyOptions( String [] args )
    {
    	return trimParams ( args );
    }
    
    static String [] trimParams( String [] args )
    {
    	if ( args == null )
    	{
    		return new String[0];
    	}

    	List tokenArray = new ArrayList();

    	for ( int i = 0; i < args.length; ++i )
    	{    		
    		if ( args[i] == null || args[i].length() == 0 )
    		{
    			continue;
    		}
    		
    		String [] tokens = StringUtils.split( args[i] );

    		for ( int k = 0 ; k < tokens.length; ++k )
    		{  			
    			if ( tokens[k] == null || tokens[k].trim().length() == 0 )
    			{
    				continue;
    			}
    			
    			tokenArray.add( tokens[k].trim() ); 			
    		}
    	}
    	
		return (String []) tokenArray.toArray( new String[0] );
    }
    
	static void executeCommandline( Commandline cl, Log logger ) 
       throws MojoExecutionException
    {
        int ok;

        try 
        {
    	    DefaultConsumer stdout = new DefaultConsumer();

    	    DefaultConsumer stderr = stdout;

    	    logger.info( cl.toString() );

    	    ok = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
        }
        catch ( CommandLineException ecx) 
        {
        	throw new MojoExecutionException( "Error executing command line", ecx );
        }

        if ( ok != 0 )
        {
        	throw new MojoExecutionException( "Error executing command line. Exit code:" + ok );
        }		
    }    
}
