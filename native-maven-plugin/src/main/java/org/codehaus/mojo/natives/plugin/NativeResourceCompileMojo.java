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
import org.codehaus.mojo.natives.compiler.ResourceCompiler;
import org.codehaus.mojo.natives.compiler.ResourceCompilerConfiguration;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.mojo.natives.manager.ResourceCompilerManager;
import org.codehaus.plexus.util.FileUtils;

import java.util.List;

/**
 * Compile Windows resource files
 * @goal resource-compile
 * @phase generate-sources
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 */

public class NativeResourceCompileMojo
    extends AbstractNativeMojo
{

    /**
     * Compiler Provider Type
     * @parameter default-value="msvc"
     * @required
     */
    private String provider;

    /**
     * Use this field to override provider specific resource compiler executable
     * @parameter 
     * @optional
     */
    private String resourceCompilerExecutable;

    /**
     * Resource compiler options
     * @parameter 
     */
    private List resourceCompilerOptions;

    /**
     * Array of NativeSources containing include directories and source files
     * @parameter
     */

    private NativeSources[] sources;

    /**
     * @parameter expression="${component.org.codehaus.mojo.natives.manager.ResourceCompilerManager}"
     * @required
     */

    private ResourceCompilerManager manager;

    public void execute()
        throws MojoExecutionException
    {

        if ( !this.outputDirectory.exists() )
        {
            this.outputDirectory.mkdirs();
        }

        ResourceCompiler compiler = this.getResourceCompiler();

        FileUtils.mkdir( project.getBuild().getDirectory() );

        ResourceCompilerConfiguration config = new ResourceCompilerConfiguration();
        config.setExecutable( this.resourceCompilerExecutable );
        config.setWorkingDirectory( this.workingDirectory );
        config.setOptions( NativeMojoUtils.trimParams( this.resourceCompilerOptions ) );
        config.setOutputDirectory( this.outputDirectory );
        config.setEnvFactoryName( this.envFactoryName );

        List resourceOutputFiles;
        try
        {
            resourceOutputFiles = compiler.compile( config, this.sources );
        }
        catch ( NativeBuildException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        this.saveCompilerOutputFilePaths( resourceOutputFiles );

    }

    private ResourceCompiler getResourceCompiler()
        throws MojoExecutionException
    {
        ResourceCompiler rc;

        try
        {
            rc = this.manager.getResourceCompiler( this.provider );

        }
        catch ( NoSuchNativeProviderException pe )
        {
            throw new MojoExecutionException( pe.getMessage() );
        }

        return rc;
    }
}
