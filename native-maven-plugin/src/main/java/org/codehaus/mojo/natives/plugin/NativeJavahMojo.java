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

import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.artifact.Artifact;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.javah.Javah;
import org.codehaus.mojo.natives.javah.JavahConfiguration;
import org.codehaus.mojo.natives.manager.JavahManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.plexus.util.FileUtils;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.io.IOException;
import java.io.File;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Generate jni include files based on a set of class names
 * @goal javah
 * @description generate jni include files
 * @phase generate-sources
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 * @requiresDependencyResolution compile
 */

public class NativeJavahMojo
    extends AbstractMojo
{
    
    /**
     * The compiler implementation to use. 
     * If this attribute is not set, the default compiler for the current VM will be used.
     * Currently the only available implemention is 'default'.
     * @parameter default-value="default"
     * @required
     */
    private String implementation;	

    /**
     * List of class names to generate native files. Default is all
     * JNI classes available in the classpath excluding the 
     * transitive dependencies.     
     * @parameter 
     */
    private String [] classNames;

    /**
     * Internal readonly property.
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @description 
     */
    private MavenProject project;

    /**
     * Generated native source files go here
     * @parameter default-value="${project.build.directory}/native/javah" 
     * @required
     */
    private File outputDirectory;

    /**
     * if configured will be combined with outputDirectory to pass into javah's -o option
     * @parameter 
     * @optional
     */
    private String outputFileName;
    
    /**
     * Enable javah verbose mode
     * @parameter default-value="false"
     * @optional
     */

    private boolean verbose;
    
    /**
     * To look up javah implementation
     * @parameter expression="${component.org.codehaus.mojo.natives.manager.JavahManager}"
     * @required
     */

    private JavahManager manager;
    
    public void execute()
        throws MojoExecutionException
    {
    	if ( ! this.outputDirectory.exists() )
    	{
    		this.outputDirectory.mkdirs();
    	}

    	JavahConfiguration config = new JavahConfiguration();
    	config.setVerbose( this.verbose );
    	config.setDestDir( this.outputDirectory );
        config.setFileName( this.outputFileName );
    	config.setClassPaths( this.getJavahClassPath() );
    	config.setClassNames( this.getNativeClassNames() );
    	
    	try
    	{
       	    Javah javah = this.manager.getJavah( this.implementation );
       	     		
            javah.compile( config );
    	}
    	catch ( NoSuchNativeProviderException pe )
    	{
    		throw new MojoExecutionException( pe.getMessage() );
    	}    	
    	catch ( NativeBuildException e )
    	{
    		throw new MojoExecutionException( "Error running javah command", e );
    	}
    	
    	this.project.addCompileSourceRoot( this.outputDirectory.getAbsolutePath() );
        
    }
    
    private List getJavahArtifacts()
    {
        List list = new ArrayList();
        
        Set artifacts = this.project.getDependencyArtifacts();

        for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
        	
            // TODO: utilise appropriate methods from project builder
            // TODO: scope handler
            // Include runtime and compile time libraries
            if ( !Artifact.SCOPE_PROVIDED.equals( artifact.getScope() ) &&
                !Artifact.SCOPE_TEST.equals( artifact.getScope() ) )
            {
            	list.add( artifact );
            }
        }
        
        return list;
    }

    private String [] getJavahClassPath()
    {
        List artifacts = this.getJavahArtifacts();

        String [] classPaths = new String[ artifacts.size() ];
        
        Iterator iter = artifacts.iterator();
        
        for ( int i = 0 ; i < classPaths.length; ++i ) 
        {
            Artifact artifact = (Artifact) iter.next();
            
            classPaths[i] = artifact.getFile().getPath();
        }
        
        return classPaths;
    }
    
    
	/**
	 * 
	 * Get appliable class names to be "javahed" 
	 * 
     */
 
    private String [] getNativeClassNames() 
        throws MojoExecutionException
    {
        if ( this.classNames != null )
        {
            return this.classNames;
        }

        //scan the immediate dependency list for jni classes
        
        List artifacts = this.getJavahArtifacts();
        
        List scannedClassNames = new ArrayList();

        for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();

        	this.getLog().info("Parsing " + artifact.getFile() + " for native classes." );
            
           	try 
           	{
           		Enumeration zipEntries  = new ZipFile( artifact.getFile() ).entries();
           		
           		while ( zipEntries.hasMoreElements() )
           		{
           			ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
            			
           			if ( "class".equals( FileUtils.extension( zipEntry.getName() ) ) )
           			{
           	            ClassParser parser = new ClassParser( artifact.getFile().getPath(), zipEntry.getName() );
            	            
           	    		JavaClass clazz  = parser.parse();

           	    		Method [] methods = clazz.getMethods();
            	    		
           	    		for ( int j = 0; j < methods.length; ++j )
           	    		{
           	    			if ( methods[j].isNative() )
           	    			{
                                scannedClassNames.add( clazz.getClassName() );

           	    	        	this.getLog().info("Found native class: " + clazz.getClassName() );
           	        			
           	        			break;
           	    			}
           	    		}
           			}
           		}//endwhile
            }
            catch ( IOException ioe )
            {
            	throw new MojoExecutionException( "Error searching for native class in dependencies", ioe );
            }
        }
    	
    	return ( String [] ) scannedClassNames.toArray( new String[0] );
    }
    
}
