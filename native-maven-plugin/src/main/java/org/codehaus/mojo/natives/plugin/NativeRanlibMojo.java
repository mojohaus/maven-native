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

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.Ranlib;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.mojo.natives.manager.RanlibManager;

/**
 * ranlib a Unix linker output file
 * @goal ranlib
 * @phase package
 */
public class NativeRanlibMojo
    extends AbstractNativeMojo
{
    
    /**
     * Where to place the final packaging
     * @parameter default-value="${project.build.directory}"
     * @required
     * @since 1.0-alpha-2
     */
    protected File ranlibOutputDirectory;
    
    /**
     * Ranlib Provider. 
     * @parameter default-value="default"
     * @required
     * @since 1.0-alpha-2
     */
    private String provider;

    /**
     * To look up ranlib implementation
     * @component
     * @readonly
     * @since 1.0-alpha-2
     */

    private RanlibManager manager;

    public void execute()
        throws MojoExecutionException
    {
        
        try
        {
            String finalName = this.project.getBuild().getFinalName();

            String fileExt = this.project.getArtifact().getArtifactHandler().getExtension();

            File outputFile = new File( this.ranlibOutputDirectory.getAbsolutePath() + "/" + finalName + "." + fileExt );

            Ranlib ranlib = this.getRanlib();
            
            ranlib.run( outputFile );
        }
        catch ( NativeBuildException e )
        {
            throw new MojoExecutionException( "Error executing ranlib.", e );
        }
    }

    private Ranlib getRanlib()
        throws MojoExecutionException
    {
        Ranlib ranlib;

        try
        {
            ranlib = this.manager.getRanlib( this.provider );

        }
        catch ( NoSuchNativeProviderException pe )
        {
            throw new MojoExecutionException( pe.getMessage() );
        }

        return ranlib;
    }

}
