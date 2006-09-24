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
import org.codehaus.mojo.natives.manager.LinkerManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Link all previously built object and dependent library files into final build
 * artifact
 * 
 * @goal link
 * @phase package
 * @requiresDependencyResolution
 * 
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 */
public class NativeLinkMojo
    extends AbstractNativeMojo
{

    /**
     * Override this property if permitted by compilerProvider
     * 
     * @parameter default-value="generic"
     * @optional
     * @description provider type
     */
    private String compilerProvider;

    /**
     * Default value is ${compilerProvider}
     * 
     * @parameter
     * @optional
     */
    private String linkerProvider;

    /**
     * Override this property if permitted by linkerProvider
     * 
     * @parameter
     * @optional
     * @description default to compilerType if not provided
     */
    private String linkerExecutable;

    /**
     * @parameter
     * @optional
     */
    private List linkerStartOptions;

    /**
     * @parameter
     * @optional
     */
    private List linkerMiddleOptions;

    /**
     * @parameter
     * @optional
     */
    private List linkerEndOptions;

    /**
     * Option to reorder dependency list, each item has the format of
     * ${groupId}:${artifactId}
     * 
     * @parameter
     * @optional
     */

    private List linkingOrderLibs;

    /**
     * Map of of project artifacts.
     * 
     * @parameter expression="${project.artifactMap}"
     * @required
     * @readonly
     */
    private Map projectArtifactMap;

    /**
     * Comma separated extension type to be installed/deployed. Use this option
     * to deploy library file produced by dll build on windows
     * 
     * @parameter default-value=""
     * @optional
     */
    private String linkerSecondaryOutputExtensions = "";

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

    /**
     * Dependent libraries with version + classifier removed are copied to this
     * directory to be linked to the build artifact
     * 
     * @parameter expression="${project.build.directory}/lib"
     * @required
     * @readonly
     */

    private File externalLibDirectory;

    public void execute()
        throws MojoExecutionException
    {

        Linker linker = this.getLinker();

        this.config = this.createLinkerConfiguration();

        try
        {
            List allCompilerOuputFiles = (List) this.getPluginContext().get( AbstractNativeMojo.LINKER_INPUT_LIST_NAME );

            linker.link( config, allCompilerOuputFiles );
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

        primaryArtifact.setFile( new File( this.outputDirectory + "/" + this.project.getBuild().getFinalName() + "."
            + this.project.getArtifact().getArtifactHandler().getExtension() ) );

        this.attachSecondaryArtifacts();
    }

    private LinkerConfiguration createLinkerConfiguration()
        throws MojoExecutionException
    {
        LinkerConfiguration config = new LinkerConfiguration();
        config.setWorkingDirectory( this.project.getBasedir() );
        config.setExecutable( this.linkerExecutable );
        config.setStartOptions( removeEmptyOptions( this.linkerStartOptions ) );
        config.setMiddleOptions( removeEmptyOptions( this.linkerMiddleOptions ) );
        config.setEndOptions( removeEmptyOptions( this.linkerEndOptions ) );
        config.setOutputDirectory( this.outputDirectory );
        config.setOutputFileName( this.project.getBuild().getFinalName() );
        config.setOutputFileExtension( this.project.getArtifact().getArtifactHandler().getExtension() );
        config.setExternalLibDirectory( this.externalLibDirectory );
        config.setExternalLibFileNames( this.getLibFileNames() );
        config.setEnvFactoryName( this.envFactoryName );

        return config;
    }

    private Linker getLinker()
        throws MojoExecutionException
    {
        Linker linker;

        try
        {
            if ( this.linkerProvider == null )
            {
                this.linkerProvider = this.compilerProvider;
            }

            linker = this.manager.getLinker( this.linkerProvider );
        }
        catch ( NoSuchNativeProviderException pe )
        {
            throw new MojoExecutionException( pe.getMessage() );
        }

        return linker;
    }

    private void attachSecondaryArtifacts()
    {
        String[] tokens = StringUtils.split( this.linkerSecondaryOutputExtensions, "," );

        for ( int i = 0; i < tokens.length; ++i )
        {
            // TODO: shouldn't need classifer
            Artifact artifact = artifactFactory.createArtifact( project.getGroupId(), project.getArtifactId(), project
                .getVersion(), null, tokens[i].trim() );
            artifact.setFile( new File( this.outputDirectory + "/" + this.project.getBuild().getFinalName() + "."
                + tokens[i].trim() ) );

            project.addAttachedArtifact( artifact );
        }

    }

    private List getLibFileNames()
        throws MojoExecutionException
    {
        List libList = new ArrayList();

        Set artifacts = this.project.getArtifacts();

        for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();

            String libFileName = FileUtils.filename( this.getDependencyFile( artifact, true ).getPath() );

            libList.add( libFileName );
        }

        libList = this.reorderLibDependencies( libList );

        return libList;
    }

    /**
     * convert dependencyLinkingOrders to a file list
     * 
     * @return
     */
    private List getDependenciesFileOrderList()
        throws MojoExecutionException
    {
        List list = new ArrayList();

        if ( this.linkingOrderLibs != null )
        {
            for ( Iterator i = linkingOrderLibs.iterator(); i.hasNext(); )
            {
                String element = i.next().toString();

                Artifact artifact = (Artifact) projectArtifactMap.get( element );

                if ( artifact != null )
                {
                    String libFileName = FileUtils.filename( this.getDependencyFile( artifact, false ).getPath() );

                    list.add( libFileName );
                }
                else
                {
                    throw new MojoExecutionException( element + " not found on project dependencies." );
                }
            }
        }

        return list;
    }

    private List reorderLibDependencies( List libs )
        throws MojoExecutionException
    {
        List requestedOrderList = getDependenciesFileOrderList();

        if ( requestedOrderList.size() != 0 )
        {
            // remove from original list first
            for ( Iterator i = requestedOrderList.iterator(); i.hasNext(); )
            {
                libs.remove( i.next() );
            }

            for ( Iterator i = libs.iterator(); i.hasNext(); )
            {
                requestedOrderList.add( i.next() );
            }

            return requestedOrderList;
        }
        else
        {
            return libs;
        }
    }

    private File getDependencyFile( Artifact artifact, boolean doCopy )
        throws MojoExecutionException
    {
        File newLocation = artifact.getFile();

        newLocation = new File( this.externalLibDirectory, artifact.getArtifactId() + "."
            + artifact.getArtifactHandler().getExtension() );

        try
        {
            if ( doCopy && ( !newLocation.exists() || newLocation.lastModified() <= artifact.getFile().lastModified() ) )
            {
                FileUtils.copyFile( artifact.getFile(), newLocation );
            }
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException( "Unable to copy dependency to staging area" );
        }

        return newLocation;
    }

    ////////////////////////////////////// UNIT TEST HELPERS //////////////////////////////////

    /**
     * For unittest only
     */
    private LinkerConfiguration config;
    
    protected LinkerConfiguration getLgetLinkerConfiguration()
    {
        return this.config;
    }

}
