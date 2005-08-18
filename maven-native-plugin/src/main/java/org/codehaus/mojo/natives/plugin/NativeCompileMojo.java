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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.compiler.Compiler;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.mojo.natives.manager.CompilerManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.mojo.natives.util.FileSet;
import org.codehaus.plexus.util.FileUtils;

import java.io.IOException;

import java.util.List;

/**
 * @goal compile
 * @description compile all source into native object files
 * @phase compile
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id:$
 */

public class NativeCompileMojo
    extends AbstractNativeMojo
{

    /**
     * @parameter default-value="generic"
     * @required
     * @description Compiler Provider Type
     */
    private String compilerType;
        
    /**
     * @parameter 
     * @optional
     * @description override provider specific executable
     */
    private String compilerExecutable;
	
    /**
     * @description Comma separated include paths. They are not participating in dependency analysis
     * ${java.home}/../include will be appended automaticall at compile time if javah is involved.
     * @parameter default-value=""
     */
    private String systemIncludePaths;

    /**
     * TODO use File [] instead
     * @description Comma separated include paths to be participate in dependency analysis
     * @parameter default-value=""
     */
    private String includePaths;

    /**
     * @description Compiler options to produce native object file
     * @parameter default-value=""
     */
    private String compilerOptionsStart;
    
    /**
     * @description Compiler options to produce native object file
     * @parameter default-value=""
     */
    private String compilerOptionsMiddle;
    
    /**
     * @description Compiler options to produce native object file
     * @parameter default-value=""
     */
    private String compilerOptionsEnd;
    
    /**
     * @parameter default-value=""
     * @description Directory name of OS specific include directory under ${java.home}/../include directory
     * @optional
     */

    private String javahOS;

    /**
     * @parameter expression="${component.org.codehaus.mojo.natives.manager.CompilerManager}"
     * @required
     */

    private CompilerManager manager;

    public void execute()
        throws MojoExecutionException
    {
    	Compiler compiler;
    	try 
    	{
    	    compiler = this.manager.getCompiler( this.compilerType );
    	}
    	catch ( NoSuchNativeProviderException pe )
    	{
    		throw new MojoExecutionException( pe.getMessage() );
    	}
    	
    	FileUtils.mkdir( project.getBuild().getDirectory() );
    	
        List additionalIncludePaths = project.getCompileSourceRoots();
        
        if ( additionalIncludePaths.size() > 1 )
        {
        	//javah was invoked
        	
        	String jdkIncludeDir = System.getProperty("java.home");
        	
        	this.systemIncludePaths += "," + jdkIncludeDir + "/../include";
        	
        	if ( this.javahOS != null && this.javahOS.trim().length() > 0 )
        	{
            	this.systemIncludePaths += "," + jdkIncludeDir + "/../include/" + this.javahOS;
        	}
        	
            for ( int i = 1; i < additionalIncludePaths.size(); ++i )
            {
            	String additionalPath = (String) additionalIncludePaths.get( i );
            	this.includePaths += ",";
            	this.includePaths += additionalPath;
            }
        	
        }
        
    	CompilerConfiguration config = new CompilerConfiguration();
    	config.setProviderHome( this.providerHome );
    	config.setBaseDir( this.basedir );
    	config.setExecutable( this.compilerExecutable );
    	config.setOptionsStart( this.compilerOptionsStart );
    	config.setOptionsMiddle( this.compilerOptionsMiddle );
    	config.setOptionsEnd( this.compilerOptionsEnd);
    	config.setIncludePaths( this.includePaths );
    	config.setSystemIncludePaths( this.systemIncludePaths );
    	config.setObjectFileExtension ( this.objectFileExtension );
    	config.setOutputDirectory ( this.outputDirectory );
    	    	
    	try 
    	{
    		FileSet sourceSet = new FileSet( this.sourceDir, this.sourceIncludes, this.sourceExcludes ) ;
    		
    		compiler.compile( config, sourceSet );
    	}
    	catch ( IOException ioe )
    	{
    		throw new MojoExecutionException ( "Error retreiving source files.  Reason: " + ioe.getMessage(), ioe );
    	}
    	catch ( NativeBuildException e ) 
    	{
    		throw new MojoExecutionException ( e.getMessage(), e );
    	}

    }
    
}
