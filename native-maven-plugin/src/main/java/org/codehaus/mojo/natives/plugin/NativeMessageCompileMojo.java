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
import org.codehaus.mojo.natives.compiler.MessageCompiler;
import org.codehaus.mojo.natives.compiler.MessageCompilerConfiguration;
import org.codehaus.mojo.natives.manager.MessageCompilerManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;

import java.io.File;
import java.util.List;

/**
 * Compile Windows message files
 * @goal compile-message
 * @phase generate-sources
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 */

public class NativeMessageCompileMojo
    extends AbstractNativeMojo
{

    /**
     * @parameter default-value="msvc"
     * @required
     * @description Compiler Provider Type
     */
    private String provider;

    /**
     * Use this field to override provider specific message compiler executable
     * @parameter 
     * @optional
     */
    private String messageCompilerExecutable;

    /**
     * @description Compiler options
     * @parameter 
     */
    private List messageCompilerOptions;

    /**
     * @parameter 
     * @required
     */
    protected File[] messageFiles;

    /**
     * @parameter expression="${component.org.codehaus.mojo.natives.manager.MessageCompilerManager}"
     * @required
     */
    private MessageCompilerManager manager;

    /**
     * Specifies a fully qualified class name implementing the 
     * org.codehaus.mojo.natives.EnvFactory interface. The class creates 
     * a set environment variables to be used with the command line.
     * @parameter
     */
    protected String envFactoryName;

    public void execute()
        throws MojoExecutionException
    {

        if ( !this.outputDirectory.exists() )
        {
            this.outputDirectory.mkdirs();
        }

        MessageCompiler compiler = this.getMessageCompiler();

        MessageCompilerConfiguration config = new MessageCompilerConfiguration();

        config.setExecutable( this.messageCompilerExecutable );
        config.setWorkingDirectory( this.project.getBasedir() );
        config.setOutputDirectory( this.outputDirectory );
        config.setOptions( NativeMojoUtils.trimParams( this.messageCompilerOptions ) );
        config.setEnvFactoryName( this.envFactoryName );

        try
        {
            compiler.compile( config, this.messageFiles );
        }
        catch ( NativeBuildException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        this.project.addCompileSourceRoot( this.outputDirectory.getAbsolutePath() );

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
