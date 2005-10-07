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
import org.codehaus.mojo.natives.NativeSources;
import org.codehaus.mojo.natives.compiler.Compiler;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.mojo.natives.manager.CompilerManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Compile source files into native object files
 * @goal compile
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
     * @description Compiler options
     * @parameter 
     */
    private String [] compilerStartOptions;
    
    
    /**
     * @description Compiler options to produce native object file
     * @parameter 
     */
    private String [] compilerMiddleOptions;
    
    /**
     * @description Compiler options to produce native object file
     * @parameter default-value=""
     */
    private String [] compilerEndOptions;
    
    /**
     * @parameter default-value=""
     * @description Directory name of OS specific include directory under ${java.home}/../include directory
     * @optional
     */

    private String javahOS;

    /**
     * Compilable source files see TODO api of NativeSource here
     * @parameter 
     * 
     */
    protected NativeSources [] sources;
    
    
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

    	this.addAdditionalIncludePath();
    	
    	CompilerConfiguration config = new CompilerConfiguration();
    	config.setProviderHome( this.providerHome );
    	config.setBaseDir( this.basedir );
    	config.setExecutable( this.compilerExecutable );
    	config.setStartOptions( removeEmptyOptions( this.compilerStartOptions ) );
    	config.setMiddleOptions( removeEmptyOptions( this.compilerMiddleOptions ) );
    	config.setEndOptions( removeEmptyOptions( this.compilerEndOptions ) );
    	config.setIncludePaths( NativeSources.getIncludePaths( this.sources ) );
    	config.setSystemIncludePaths( NativeSources.getSystemIncludePaths( this.sources ) );
    	config.setOutputDirectory ( this.outputDirectory );
        
        List objectFiles;
    	try 
    	{
    		objectFiles = compiler.compile( config, NativeSources.getAllSourceFiles( this.sources ) );
    	}
    	catch ( NativeBuildException e ) 
    	{
    		throw new MojoExecutionException ( e.getMessage(), e );
    	}
        
    	this.saveCompilerOuputFilePaths( objectFiles );

    }

    
    private void addJavaHIncludePaths( List includePaths )
    {
        if ( this.javahOS != null && this.javahOS.trim().length() > 0 )
        {
            File jdkIncludeDir = new File( System.getProperty( "java.home" )  + "/../include" ) ;
            
            NativeSources jdkIncludeSource = new NativeSources();
            
            jdkIncludeSource.setDirectory( jdkIncludeDir );
            
            jdkIncludeSource.setDependencyAnalysisParticipation( false );
            
            includePaths.add( jdkIncludeSource );
            
            File jdkOsIncludeDir = new File ( jdkIncludeDir.getPath() + "/" +  this.javahOS );
            
            NativeSources jdkIncludeOsSource = new NativeSources();
            
            jdkIncludeOsSource.setDirectory( jdkOsIncludeDir );
            
            jdkIncludeOsSource.setDependencyAnalysisParticipation( false );
            
            includePaths.add( jdkIncludeOsSource );
        }
    }
    
    
    /**
     * Pickup additional source paths that previous phases added to source root 
     * Note: we intentional ignore the first item of source root since this
     * plugin never use it.
     */
	private void addAdditionalIncludePath()
	{
        List additionalIncludePaths = project.getCompileSourceRoots();
        
        if ( additionalIncludePaths.size() < 2 || this.javahOS == null || this.javahOS.trim().length() == 0 )
        {
            return;
        }
        
        if (  this.sources == null )
        {
            return;
        }
        
        List sourceArray = new ArrayList( Arrays.asList( this.sources ) );
        
        this.addJavaHIncludePaths( sourceArray );
        
        if ( additionalIncludePaths.size() > 1 )
        {
            for ( int i = 1; i < additionalIncludePaths.size(); ++i )
            {
                File genIncludeDir = new File ( additionalIncludePaths.get( i ).toString() );
                
                NativeSources genIncludeSource = new NativeSources();
                
                genIncludeSource.setDirectory( genIncludeDir );
                
                sourceArray.add( genIncludeSource );
            }
		}

        this.sources = ( NativeSources [] ) sourceArray.toArray( new NativeSources[0] );
        
	}
}
