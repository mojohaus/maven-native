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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.NativeSources;
import org.codehaus.mojo.natives.manager.LinkerManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @goal link
 * @phase package
 * @requiresDependencyResolution 
 * @description Link all previously built object files and dependent lib files
 *
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id:$
 */
public class NativeLinkMojo
    extends AbstractNativeMojo
{

    /**
     * @parameter default-value="generic"
     * @optional
     * @description provider type
     */
    private String compilerType;
	
    /**
     * @parameter
     * @optional
     */
    private String linkerType;

    /**
     * @parameter 
     * @optional
     * @description default to compilerType if not provided
     */
    private String linkerExecutable;

    /**
     * @parameter 
     * @optional
     */
    private String [] linkerStartOptions = new String[0];
    
    /**
     * @parameter 
     * @optional
     */
    private String [] linkerMiddleOptions = new String[0];
    
    /**
     * @parameter 
     * @optional
     */
    private String [] linkerEndOptions = new String[0];
    
    /**
     * @parameter expression="${project.artifactId}-${project.version}"
     * @optional
     */
    
    private String finalName;
    
    /**
     * @parameter 
     * @optional
     * @description rename a set of dependencies to desired name
     */
    
    private Map renameDependencyLibs = new HashMap();
    
    /**
     * @parameter 
     * @optional
     * @description The order of dependencies to link
     */
    
    private String [] dependencyLinkingOrders = new String[0];    
    
    /**
     * @parameter default-value="" 
     * @optional
     * @description some linkers create more than one artifacts
     * TODO use String []
     */
    private String linkerSecondaryOuputExtensions = "";

    /**
     * @parameter expression="${project.artifact.artifactHandler.extension}" 
     * @required
     * @readonly
     */
    private String linkerPrimaryOutputFileExtension;
    
    /**
     * @parameter expression="${component.org.codehaus.mojo.natives.manager.LinkerManager}"
     * @required
     * @readonly
     */
  
    private LinkerManager manager;
    
    /**
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;
    
    public void execute()
        throws MojoExecutionException
    {
        
    	Linker linker = this.getLinker();
    	      	
	    LinkerConfiguration config = this.getLinkerConfiguration();
	    
    	try 
    	{
    		linker.link( config, NativeSources.getAllSourceFiles( this.sources ) );
    	}
    	catch ( IOException ioe )
    	{
    		throw new MojoExecutionException( ioe.getMessage(), ioe );
    	}
    	catch ( NativeBuildException nbe )
    	{
    		throw new MojoExecutionException( nbe.getMessage(), nbe );
    	}
    	
    	Artifact primaryArtifact = this.project.getArtifact();
    	    	
    	primaryArtifact.setFile( new File( this.outputDirectory + "/" + this.finalName + "." + this.linkerPrimaryOutputFileExtension )) ;
    	
    	this.attachSecondaryArtifacts();
    }
    
    private LinkerConfiguration getLinkerConfiguration()
        throws MojoExecutionException
    {
    	LinkerConfiguration config = new LinkerConfiguration();
    	config.setProviderHome( this.providerHome );
    	config.setWorkingDirectory( this.basedir );
    	config.setExecutable( this.linkerExecutable );
    	config.setObjectFileExtention( this.objectFileExtension );
    	config.setStartOptions( removeEmptyOptions( this.linkerStartOptions ) );
    	config.setMiddleOptions( removeEmptyOptions( this.linkerMiddleOptions ) );
    	config.setEndOptions( removeEmptyOptions( this.linkerEndOptions ) );
    	config.setOutputDirectory( this.outputDirectory );
    	config.setOutputFileName( this.finalName );
    	config.setOutputFileExtension( this.linkerPrimaryOutputFileExtension );
    	config.setExternalLibraries( this.getLibDependencies() );
    	
    	return config;
    }
    private Linker getLinker()
        throws MojoExecutionException
    {
    	Linker linker;
    	
       	try 
    	{
    		if ( this.linkerType == null )
    		{
    			this.linkerType = this.compilerType;
    		}
    		
    		linker = this.manager.getLinker( this.linkerType );
    	}
    	catch ( NoSuchNativeProviderException pe )
    	{
    		throw new MojoExecutionException( pe.getMessage() );
    	}   
    	
    	return linker;
    }
    
    
    private void attachSecondaryArtifacts()
    {
    	String [] tokens = StringUtils.split( this.linkerSecondaryOuputExtensions, "," );
    	
    	for ( int i = 0; i < tokens.length; ++i )
    	{
            // TODO: shouldn't need classifer
            Artifact artifact = artifactFactory.createArtifact( project.getGroupId(),
                                                                              project.getArtifactId(),
                                                                              project.getVersion(), null, tokens[i].trim());
            artifact.setFile( new File( this.outputDirectory + "/" + this.finalName + "." + tokens[i].trim() )) ; 

            project.addAttachedArtifact( artifact );
    	}
    	
    }
    
    private File [] getLibDependencies()
      throws MojoExecutionException
    {
        
        List libList = new ArrayList();
        
        Set artifacts = this.project.getArtifacts();

        for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
        {
        	/* TODO should be handled by compiler specific type */
        	Artifact artifact = (Artifact) iter.next();
            if ( 
                  "a".equals  ( artifact.getArtifactHandler().getExtension() )  ||
                  "so".equals  ( artifact.getArtifactHandler().getExtension() )  ||
                  "lib".equals( artifact.getArtifactHandler().getExtension() )  ||
                  "o".equals( artifact.getArtifactHandler().getExtension()   )  ||
                  "obj".equals( artifact.getArtifactHandler().getExtension() )  
               ) 
            {
                File libLocation = this.renameDependencyIfRequired( artifact );

                this.getLog().info("Found dependency lib: " + libLocation );
                
            	libList.add( libLocation );
            }
        }
        
        return ( File [] ) libList.toArray( new File [0] );
    }    
    
    private File renameDependencyIfRequired( Artifact artifact )
      throws MojoExecutionException
    {
        File newLocation = artifact.getFile();

        //TODO need to handle groupId too 
        String renameArtifact = (String) this.renameDependencyLibs.get( artifact.getArtifactId() );
            
        if ( renameArtifact != null )
        {   
            newLocation = new File ( this.outputDirectory.getPath() + "/" + renameArtifact + "." + artifact.getArtifactHandler().getExtension() );
  
            try 
            {
                if ( ! newLocation.exists() || newLocation.lastModified() <= artifact.getFile().lastModified() )
                {
                    this.getLog().info( "Rename dependencies: " + artifact.getFile() + " to " + newLocation );
                    FileUtils.copyFile( artifact.getFile(), newLocation );
                }
            }
            catch ( IOException ioe )
            {
                throw new MojoExecutionException( "Unable to rename artifact ");
            }
        }
        
        return newLocation;
    }
    
}
