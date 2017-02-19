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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.compiler.MessageCompiler;
import org.codehaus.mojo.natives.compiler.MessageCompilerConfiguration;
import org.codehaus.mojo.natives.manager.MessageCompilerManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;

import java.io.File;
import java.util.List;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Compile Windows message files
 */
@Mojo(name = "compile-message", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class NativeMessageCompileMojo
    extends AbstractNativeMojo
{

    /**
     * Compiler Provider Type
     * @since 1.0-alpha-2
     */
    @Parameter(defaultValue = "msvc", required = true)
    private String provider;

    /**
     * Use this field to override provider specific message compiler executable
     * @since 1.0-alpha-2
     */
    @Parameter
    private String messageCompilerExecutable;

    /**
     * Additional Compiler options
     * @since 1.0-alpha-2
     */
    @Parameter
    private List messageCompilerOptions;

    /**
     * List of message files to compile
     * @since 1.0-alpha-2
     */
    @Parameter(required = true)
    protected File[] messageFiles;

    /**
     * Where to place the compiler object files
     * @since 1.0-alpha-2
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    protected File messageCompilerOutputDirectory;

    /**
     * Internal
     * @since 1.0-alpha-2
     */
    @Component
    private MessageCompilerManager manager;

    public void execute()
        throws MojoExecutionException
    {

        if ( !this.messageCompilerOutputDirectory.exists() )
        {
            this.messageCompilerOutputDirectory.mkdirs();
        }

        MessageCompiler compiler = this.getMessageCompiler();
        MessageCompilerConfiguration config = new MessageCompilerConfiguration();

        config.setExecutable( this.messageCompilerExecutable );
        config.setWorkingDirectory( this.workingDirectory );
        config.setOutputDirectory( this.messageCompilerOutputDirectory );
        config.setOptions( NativeMojoUtils.trimParams( this.messageCompilerOptions ) );
        config.setEnvFactory( this.getEnvFactory() );

        try
        {
            compiler.compile( config, this.messageFiles );
        }
        catch ( NativeBuildException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        this.project.addCompileSourceRoot( this.messageCompilerOutputDirectory.getAbsolutePath() );

    }

    private MessageCompiler getMessageCompiler()
        throws MojoExecutionException
    {
        MessageCompiler mc;

        try
        {
            mc = this.manager.getMessageCompiler( this.provider );

        }
        catch ( NoSuchNativeProviderException pe )
        {
            throw new MojoExecutionException( pe.getMessage() );
        }

        return mc;
    }
}
