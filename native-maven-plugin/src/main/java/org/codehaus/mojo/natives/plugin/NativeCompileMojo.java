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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.NativeSources;
import org.codehaus.mojo.natives.compiler.Compiler;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.mojo.natives.manager.CompilerManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;

/**
 * Compile source files into native object files
 * @goal compile
 * @phase compile
 */

public class NativeCompileMojo
    extends AbstractNativeMojo
{

    /**
     * Compiler Provider Type
     * @parameter default-value="generic"
     * @required
     * @since 1.0-alpha-2
     */
    private String compilerProvider;

    /**
     * Use this field to override object file extension.
     * The default extentions are .obj and .o on Windows and Unix respectively
     * @parameter 
     * @since 1.0-alpha-2
     */
    private String objectFileExtension;

    /**
     * Use this field to override provider specific compiler executable
     * @parameter 
     * @since 1.0-alpha-2
     */
    private String compilerExecutable;

    /**
     * Compiler options
     * @parameter 
     * @since 1.0-alpha-2
     */
    private List compilerStartOptions;

    /**
     * Compiler options
     * @parameter 
     * @since 1.0-alpha-2
     */
    private List compilerMiddleOptions;

    /**
     * Compiler options
     * @parameter
     * @since 1.0-alpha-2
     */
    private List compilerEndOptions;

    /**
     * Javah OS name.
     * ${jdkIncludePath} and ${jdkIncludePath}/${javaOS} are added to system include path 
     * when this field is set
     * @parameter 
     * @since 1.0-alpha-2
     */

    private String javahOS;

    /**
     * JDK native include directory
     * @parameter  default-value="${java.home}/../include"
     * @since 1.0-alpha-2
     */

    private File jdkIncludePath;

    /**
     * Array of NativeSources containing include directories and source files. 
     * @parameter 
     * @since 1.0-alpha-2
     */
    protected NativeSources[] sources = new NativeSources[0];

    /**
     * Compiler output files ( .o, .obj, etc) location
     * @parameter default-value="${project.build.directory}/objs"
     * @required
     * @since 1.0-alpha-4
     */
    protected File compilerOutputDirectory;
    
    /**
     * Number of parallel compilation threads
     * @parameter default-value="1"
     * @since 1.0-alpha-4
     */
    protected int numberOfConcurrentCompilation;
    

    /**
     * Internal 
     * @component
     * @since 1.0-alpha-2
     */

    private CompilerManager manager;

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
     * Note: we intentionally ignore the first item of source root ( ie ${project.build.directory}/classes since this
     * plugin never use it.
     */
    private void addAdditionalIncludePath()
        throws MojoExecutionException
    {
        List additionalIncludePaths = project.getCompileSourceRoots();
        
        boolean includeDependencyFound =  this.getPluginContext().get( AbstractNativeMojo.INCZIP_FOUND ) != null;

        if ( !includeDependencyFound )
        {
            if ( additionalIncludePaths == null || additionalIncludePaths.size() < 2 )
            {
                return;
            }
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

        if ( includeDependencyFound )
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
        config.setNumberOfConcurrentCompilation( numberOfConcurrentCompilation );

        return config;
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
