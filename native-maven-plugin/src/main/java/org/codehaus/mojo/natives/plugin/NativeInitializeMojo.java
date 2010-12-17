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
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Initialize build lifecycle
 * @goal initialize
 * @phase initialize
 */

public class NativeInitializeMojo
    extends AbstractMojo
{

    /**
     * Internal
     * @parameter expression="${project}"
     * @since 1.0-alpha-2
     */
    protected MavenProject project;
        
    public void execute()
        throws MojoExecutionException
    {
        File buildDirectory = new File( this.project.getBuild().getDirectory() );
        
        if ( ! buildDirectory.exists() )
        {
            buildDirectory.mkdirs();
        }
        
        //strip version from finalName since  and
        // disallow user from changing the final name since many
        //  final linker output depending heavily on the name without any associated version
        String finalName = project.getArtifactId();
        
        project.getBuild().setFinalName( finalName );

        //we need to clear out object files list since it possible that compile phase gets called mulitple times
        // and produce duplicate objects, and therefore will fail at link phase
        List objList = (List) this.getPluginContext().get( AbstractNativeMojo.LINKER_INPUT_LIST_NAME );
        if ( objList != null ) {
            objList.clear();
        }
        
                
    }

}
