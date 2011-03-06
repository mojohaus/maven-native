package org.codehaus.mojo.natives.plugin;

/*
 * The MIT License
 * 
 * Copyright (c) 2004, The Codehaus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.mojo.natives.manager.LinkerManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Link all previously built object and dependent library files into final build
 * artifact
 *
 * @goal link
 * @phase package
 * @requiresDependencyResolution
 */
public class NativeLinkMojo
    extends AbstractNativeMojo
{

    /**
     * Override this property if permitted by compilerProvider
     *
     * @parameter default-value="generic"
     * @required
     * @since 1.0-alpha-2
     */
    private String compilerProvider;

    /**
     * Default value is ${compilerProvider}
     *
     * @parameter
     * @since 1.0-alpha-2
     */
    private String linkerProvider;

    /**
     * Override this property if permitted by linkerProvider. Default to compilerType if not provided
     *
     * @parameter
     * @since 1.0-alpha-2
     */
    private String linkerExecutable;

    /**
     * Additional linker command options
     * @parameter
     * @since 1.0-alpha-2
     */
    private List linkerStartOptions;

    /**
     * Additional linker command options
     * @parameter
     * @since 1.0-alpha-2
     */
    private List linkerMiddleOptions;

    /**
     * Additional linker command options
     * @parameter
     * @since 1.0-alpha-2
     */
    private List linkerEndOptions;

    /**
     * Option to reorder dependency list, each item has the format of
     * ${groupId}:${artifactId}
     *
     * @parameter
     * @since 1.0-alpha-2
     */

    private List linkingOrderLibs;

    /**
     * Comma separated extension type to be installed/deployed. Use this option
     * to deploy library file produced by dll build on windows
     *
     * @parameter default-value=""
     * @since 1.0-alpha-2
     */
    private String linkerSecondaryOutputExtensions = "";

    /**
     * Where to place the final packaging
     * @parameter default-value="${project.build.directory}"
     * @required
     * @since 1.0-alpha-2
     */
    protected File linkerOutputDirectory;

    /**
     * Internal
     * @component
     * @since 1.0-alpha-2
     */
    private LinkerManager manager;

    /**
     * Internal
     * @component
     * @since 1.0-alpha-2
     */
    private ArtifactFactory artifactFactory;

    /**
     * Dependent libraries with version + classifier removed are copied to this
     * directory to be linked to the build artifact
     *
     * @parameter default-value="${project.build.directory}/lib"
     * @required
     */

    private File externalLibDirectory;

    /**
     * Option to install primary artifact as a classifier, useful to install/deploy 
     * debug artifacts
     * @parameter 
     * @since 1.0-alpha-2
     */
    private String classifier = null;

    /**
     * Attach the linker's outputs to maven project be installed/deployed. Turn this off if you have 
     * other mean of deployment, for example using maven-assembly-plugin to deploy your own bundle 
     * @parameter default-value="true"
     * @since 1.0-alpha-2
     */
    private boolean attach = true;
    
    /**
     * For project with lots of object files on windows, turn this flag to resolve Windows commandline length limit
     * @parameter default-value="false"
     * @since 1.0-alpha-7
     */
    private boolean usingLinkerResponseFile;

    public void execute()
        throws MojoExecutionException
    {

        if ( StringUtils.isEmpty( this.classifier ) )
        {
            this.classifier = null;
        }

        Linker linker = this.getLinker();

        this.config = this.createLinkerConfiguration();

        try
        {
            List allCompilerOuputFiles = this.getAllCompilersOutputFileList();

            File outputFile = linker.link( config, allCompilerOuputFiles );

            //to be used by post linker mojo  like native:manifest
            this.getPluginContext().put( AbstractNativeMojo.LINKER_OUTPUT_PATH, outputFile );
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException( ioe.getMessage(), ioe );
        }
        catch ( NativeBuildException nbe )
        {
            throw new MojoExecutionException( nbe.getMessage(), nbe );
        }

        if ( this.attach )
        {
            this.attachPrimaryArtifact();

            this.attachSecondaryArtifacts();
        }
    }

    private LinkerConfiguration createLinkerConfiguration()
        throws MojoExecutionException
    {
        LinkerConfiguration config = new LinkerConfiguration();
        config.setWorkingDirectory( this.workingDirectory );
        config.setExecutable( this.linkerExecutable );
        config.setStartOptions( removeEmptyOptions( this.linkerStartOptions ) );
        config.setMiddleOptions( removeEmptyOptions( this.linkerMiddleOptions ) );
        config.setEndOptions( removeEmptyOptions( this.linkerEndOptions ) );
        config.setOutputDirectory( this.linkerOutputDirectory );
        config.setOutputFileName( this.project.getBuild().getFinalName() );
        config.setOutputFileExtension( this.project.getArtifact().getArtifactHandler().getExtension() );
        config.setExternalLibDirectory( this.externalLibDirectory );
        config.setExternalLibFileNames( this.getLibFileNames() );
        config.setEnvFactory( this.getEnvFactory() );
        config.setUsingLinkerResponseFile( usingLinkerResponseFile );

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

    /**
     * 
     */
    private void attachPrimaryArtifact()
    {
        Artifact artifact = this.project.getArtifact();

        if ( null == this.classifier )
        {
            artifact.setFile( new File( this.linkerOutputDirectory + "/" + this.project.getBuild().getFinalName() + "."
                + this.project.getArtifact().getArtifactHandler().getExtension() ) );
        }
        else
        {
            //install primary artifact as a classifier

            DefaultArtifact clone = new DefaultArtifact( artifact.getGroupId(), artifact.getArtifactId(), artifact
                .getVersionRange().cloneOf(), artifact.getScope(), artifact.getType(), classifier, artifact
                .getArtifactHandler(), artifact.isOptional() );

            clone.setRelease( artifact.isRelease() );
            clone.setResolvedVersion( artifact.getVersion() );
            clone.setResolved( artifact.isResolved() );
            clone.setFile( artifact.getFile() );

            if ( artifact.getAvailableVersions() != null )
            {
                clone.setAvailableVersions( new ArrayList( artifact.getAvailableVersions() ) );
            }

            clone.setBaseVersion( artifact.getBaseVersion() );
            clone.setDependencyFilter( artifact.getDependencyFilter() );

            if ( artifact.getDependencyTrail() != null )
            {
                clone.setDependencyTrail( new ArrayList( artifact.getDependencyTrail() ) );
            }

            clone.setDownloadUrl( artifact.getDownloadUrl() );
            clone.setRepository( artifact.getRepository() );

            clone.setFile( new File( this.linkerOutputDirectory + "/" + this.project.getBuild().getFinalName() + "."
                + this.project.getArtifact().getArtifactHandler().getExtension() ) );

            project.setArtifact( clone );
        }
    }

    private void attachSecondaryArtifacts()
    {
        String[] tokens = StringUtils.split( this.linkerSecondaryOutputExtensions, "," );

        for ( int i = 0; i < tokens.length; ++i )
        {
            // TODO: shouldn't need classifier
            Artifact artifact = artifactFactory.createArtifact( project.getGroupId(), project.getArtifactId(), project
                .getVersion(), this.classifier, tokens[i].trim() );
            artifact.setFile( new File( this.linkerOutputDirectory + "/" + this.project.getBuild().getFinalName() + "."
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

            if ( INCZIP_TYPE.equals( artifact.getType() ) )
            {
                continue;
            }

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

                Artifact artifact = lookupDependencyUsingGroupArtifactIdPair( element );

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
    
    /**
     * Look up library in dependency list using groupId:artifactId key
     * Note: we can not use project.artifactMap due the introduction of inczip dependency
     * where 2 dependency with the same artifactId and groupId, but differs by extension type
     * make the map not suitable for lookup
     * 
     * @param groupArtifactIdPair
     * @return
     * @throws MojoExecutionException
     */
    private Artifact lookupDependencyUsingGroupArtifactIdPair( String groupArtifactIdPair )
        throws MojoExecutionException
    {
        String [] tokens = StringUtils.split( groupArtifactIdPair, ":" );
        
        if ( tokens.length != 2 )
        {
            throw new MojoExecutionException( "Invalid groupId and artifactId pair: " + groupArtifactIdPair );
        }
        
        Set allDependencyArtifacts = project.getDependencyArtifacts();
        
        for ( Iterator iter = allDependencyArtifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( INCZIP_TYPE.equals( artifact.getType() ) )
            {
                continue;
            }
            
            if ( tokens[0].equals( artifact.getGroupId())  && tokens[1].equals( artifact.getArtifactId() ) )
            {
                return artifact;
            }
        }
        
        return null;
        
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

        File newLocation = new File( this.externalLibDirectory, artifact.getArtifactId() + "."
            + artifact.getArtifactHandler().getExtension() );

        try
        {
            if ( doCopy && !artifact.getFile().isDirectory() && ( !newLocation.exists() || newLocation.lastModified() <= artifact.getFile().lastModified() ) )
            {
                FileUtils.copyFile( artifact.getFile(), newLocation );
            }
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException( "Unable to copy dependency to staging area.  Could not copy "
                + artifact.getFile() + " to " + newLocation, ioe );
        }

        return newLocation;
    }

    ////////////////////////////////////// UNIT TEST HELPERS //////////////////////////////////

    /**
     * For unit test only
     */
    private LinkerConfiguration config;

    protected LinkerConfiguration getLgetLinkerConfiguration()
    {
        return this.config;
    }

}
