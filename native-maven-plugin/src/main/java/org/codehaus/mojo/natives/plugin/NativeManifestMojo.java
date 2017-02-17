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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.Manifest;
import org.codehaus.mojo.natives.linker.ManifestConfiguration;
import org.codehaus.mojo.natives.manager.ManifestManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;

/**
 * Embeds a Visual Studio manifest file into a generated executable
 * @since 1.0-alpha4
 */
@Mojo(name = "manifest", defaultPhase = LifecyclePhase.PACKAGE)
public class NativeManifestMojo
    extends AbstractNativeMojo
{
    /**
     * Manifest Provider.
     * @since 1.0-alpha4
     */
    @Parameter(defaultValue = "msvc", required = true)
    private String provider;

    /**
     * Manifest extension
     * @since 1.0-alpha-4
     */
    @Parameter(defaultValue = "manifest", required = true)
    private String manifestExtension;

    /**
     * Enable this option to speed up linkage for large project with no dependencies changes
     * @since 1.0-alpha-8
     */
    @Parameter(defaultValue = "false")
    private boolean checkStaleLinkage;

    /**
     * Internal - To look up manifest implementation
     * @since 1.0-alpha-4
     */
    @Component
    private ManifestManager manager;

    public void execute()
        throws MojoExecutionException
    {
        File linkerOutputFile = (File) this.getPluginContext().get( LINKER_OUTPUT_PATH );

        if ( !linkerOutputFile.exists() )
        {
            // @TODO ERROR?
            return;
        }

        File linkerManifestFile = new File( linkerOutputFile.getAbsolutePath() + "." + manifestExtension );

        if ( !linkerManifestFile.exists() )
        {
            // no need to inject manifest file into the executable
            return;
        }

        // @Todo check for stale output, and skip if needed
        try
        {
            ManifestConfiguration config = new ManifestConfiguration();

            config.setEnvFactory( this.getEnvFactory() );
            config.setWorkingDirectory( this.workingDirectory );
            config.setInputFile( linkerOutputFile );
            config.setManifestFile( linkerManifestFile );

            Manifest Manifest = this.getManifest();

            Manifest.run( config );
        }
        catch ( NativeBuildException e )
        {
            throw new MojoExecutionException( "Error executing Manifest.", e );
        }

    }

    private Manifest getManifest()
        throws MojoExecutionException
    {
        Manifest Manifest;

        try
        {
            Manifest = this.manager.getManifest( this.provider );
        }
        catch ( NoSuchNativeProviderException pe )
        {
            throw new MojoExecutionException( pe.getMessage() );
        }

        return Manifest;
    }

}
