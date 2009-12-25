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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.NativeSources;
import org.codehaus.mojo.natives.compiler.Compiler;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.mojo.natives.manager.CompilerManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

/**
 * Compile source files into native object files
 * @goal compile
 * @phase compile
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 */

public class NativeCompileMojo
    extends AbstractNativeMojo
{

    /**
     * Compiler Provider Type
     * @parameter default-value="generic"
     * @required
     */
    private String compilerProvider;

    /**
     * Use this field to override object file extension.
     * The default extenstions are .obj and .o on Windows and Unix respectively
     * @parameter 
     * @optional
     */
    private String objectFileExtension;

    /**
     * Use this field to override provider specific compiler executable
     * @parameter 
     * @optional
     */
    private String compilerExecutable;

    /**
     * Compiler options
     * @parameter 
     */
    private List compilerStartOptions;

    /**
     * Compiler options 
     * @description Compiler options to produce native object file
     * @parameter 
     */
    private List compilerMiddleOptions;

    /**
     * Compiler options 
     * @description Compiler options to produce native object file
     * @parameter
     */
    private List compilerEndOptions;

    /**
     * Javah OS name.
     * ${jdkIncludePath} and ${jdkIncludePath}/${javaOS} are added to system include path 
     * when this field is set
     * @parameter 
     * @optional
     */

    private String javahOS;

    /**
     * JDK native include directory
     * @parameter  default-value="${java.home}/../include"
     * @optional
     */

    private File jdkIncludePath;

    /**
     * Array of NativeSources containing include directories and source files. 
     * @parameter 
     * @optional
     */
    protected NativeSources[] sources = new NativeSources[0];

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File compilerOutputDirectory;

    /**
     * Internal 
     * @component
     * @required
     * @readonly
     */

    private CompilerManager manager;

    /**
     * Directory to unpack .inczip dependency files to be included as system include path
     * @parameter expression="${project.build.directory}/native/include"
     * @required
     */
    private File dependencyIncludeDirectory;

    /**
     * Internal
     * @parameter expression="${project.build.directory}/native/markers"
     * @required
     */
    private File dependencyIncZipMarkerDirectory;

    public void execute()
        throws MojoExecutionException
    {

        Compiler compiler;

        try
        {
            compiler = this.manager.getCompiler( this.compilerProvider );
        }
        catch ( NoSuchNativeProviderException pe )
        {
            throw new MojoExecutionException( pe.getMessage() );
        }

        if ( this.javahOS != null )
        {
            this.addJavaHIncludePaths();
        }

        this.addAdditionalIncludePath();

        CompilerConfiguration config = this.createProviderConfiguration();

        List objectFiles;
        try
        {
            //may want to remove this when we are dont with msvc2008 debugging
            if ( this.getLog().isDebugEnabled() && getEnvFactory() != null )
            {
                Map alterEnvMap = this.getEnvFactory().getEnvironmentVariables();

                Iterator iter = alterEnvMap.keySet().iterator();

                while ( iter.hasNext() )
                {
                    String key = (String) iter.next();

                    System.out.print( key + "=" );
                    System.out.println( (String) alterEnvMap.get( key ) );
                }
            }

            objectFiles = compiler.compile( config, NativeSources.getAllSourceFiles( this.sources ) );
        }
        catch ( NativeBuildException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        this.saveCompilerOutputFilePaths( objectFiles );

    }

    private void addJavaHIncludePaths()
    {
        List sourceArray = new ArrayList( Arrays.asList( this.sources ) );

        NativeSources jdkIncludeSource = new NativeSources();

        jdkIncludeSource.setDirectory( this.jdkIncludePath );

        jdkIncludeSource.setDependencyAnalysisParticipation( false );

        sourceArray.add( jdkIncludeSource );

        File jdkOsIncludeDir = new File( this.jdkIncludePath, this.javahOS );

        NativeSources jdkIncludeOsSource = new NativeSources();

        jdkIncludeOsSource.setDirectory( jdkOsIncludeDir );

        jdkIncludeOsSource.setDependencyAnalysisParticipation( false );

        sourceArray.add( jdkIncludeOsSource );

        this.sources = (NativeSources[]) sourceArray.toArray( new NativeSources[sourceArray.size()] );

    }

    /**
     * Pickup additional source paths that previous phases added to source root 
     * Note: we intentionally ignore the first item of source root since this
     * plugin never use it.
     */
    private void addAdditionalIncludePath()
        throws MojoExecutionException
    {
        List additionalIncludePaths = project.getCompileSourceRoots();

        if ( additionalIncludePaths == null || additionalIncludePaths.size() < 2 )
        {
            return;
        }

        if ( this.sources == null )
        {
            return;
        }

        List sourceArray = new ArrayList( Arrays.asList( this.sources ) );

        if ( additionalIncludePaths.size() > 1 )
        {
            for ( int i = 1; i < additionalIncludePaths.size(); ++i )
            {
                File genIncludeDir = new File( additionalIncludePaths.get( i ).toString() );

                NativeSources genIncludeSource = new NativeSources();

                genIncludeSource.setDirectory( genIncludeDir );

                sourceArray.add( genIncludeSource );
            }
        }

        if ( unpackIncZipDepenedencies() )
        {
            NativeSources dependencyIncludeSource = new NativeSources();
            dependencyIncludeSource.setDependencyAnalysisParticipation( false );
            dependencyIncludeSource.setDirectory( this.dependencyIncludeDirectory );

            sourceArray.add( dependencyIncludeSource );
        }

        this.sources = (NativeSources[]) sourceArray.toArray( new NativeSources[sourceArray.size()] );

    }

    /*
     * use protected scope for unit test purpose
     */
    protected CompilerConfiguration createProviderConfiguration()
        throws MojoExecutionException
    {
        this.config = new CompilerConfiguration();
        config.setWorkingDirectory( this.workingDirectory );
        config.setExecutable( this.compilerExecutable );
        config.setStartOptions( removeEmptyOptions( this.compilerStartOptions ) );
        config.setMiddleOptions( removeEmptyOptions( this.compilerMiddleOptions ) );
        config.setEndOptions( removeEmptyOptions( this.compilerEndOptions ) );
        config.setIncludePaths( NativeSources.getIncludePaths( this.sources ) );
        config.setSystemIncludePaths( NativeSources.getSystemIncludePaths( this.sources ) );
        config.setOutputDirectory( this.compilerOutputDirectory );
        config.setObjectFileExtension( this.objectFileExtension );
        config.setEnvFactory( this.getEnvFactory() );

        return config;
    }

    private boolean unpackIncZipDepenedencies()
        throws MojoExecutionException
    {
        List files = getIncZipDependencies();

        Iterator iter = files.iterator();

        for ( int i = 0; i < files.size(); ++i )
        {
            Artifact artifact = (Artifact) iter.next();
            File incZipFile = artifact.getFile();

            File marker = new File( this.dependencyIncZipMarkerDirectory, artifact.getGroupId() + "."
                + artifact.getArtifactId() );

            if ( !marker.exists() || marker.lastModified() < incZipFile.lastModified() )
            {
                try
                {
                    unpackInZipFile( incZipFile );

                    marker.delete();

                    marker.createNewFile();
                }
                catch ( IOException e )
                {
                    throw new MojoExecutionException( e.getMessage(), e );
                }
            }
        }
        
        return files.size() != 0;

    }

    protected void unpackInZipFile( File incZipFile )
        throws MojoExecutionException
    {

        try
        {
            UnArchiver archiver = new ZipUnArchiver();
            archiver.setOverwrite( true );
            archiver.extract( incZipFile.getPath(), this.dependencyIncludeDirectory );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

    }

    /**
     * Get all .inczip compile time dependencies  
     * @return
     */
    private List getIncZipDependencies()
    {
        List list = new ArrayList();

        List artifacts = this.project.getCompileArtifacts();

        if ( artifacts != null )
        {

            for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
            {
                Artifact artifact = (Artifact) iter.next();

                //pick up only jar files
                if ( !"inczip".equals( artifact.getType() ) )
                {
                    continue;
                }

                list.add( artifact );
            }
        }

        return list;
    }

    ////////////////////////////////////// UNIT TEST HELPERS ////////////////////////////////

    /**
     * For unittest only
     */
    private CompilerConfiguration config;

    /**
     * Internal only for test harness purpose
     * @return
     */
    protected CompilerConfiguration getCompilerConfiguration()
    {
        return this.config;
    }
}
