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
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.mojo.natives.NativeSources;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * Prepare include file bundle to be attached to maven for deployment purpose
 * @goal inczip
 * @phase package
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id: NativeInitializeMojo.java 6588 2008-03-28 12:22:57Z bentmann $
 */

public class NativeBundleIncludeFilesMojo
    extends AbstractNativeMojo
{

    /**
     * Array of NativeSources containing include directories and source files. 
     * @parameter 
     * @optional
     */
    private NativeSources[] sources = new NativeSources[0];

    /**
     * Archive file to bundle enable NativeSources
     * @parameter default-value="${project.build.directory}/${project.build.finalName}-${project.version}.inczip"
     */
    private File inZipFile;

    /**
     * Option to skip include source bundle deployment
     * @parameter expression="${project.build.directory}/${project.build.finalName}-${project.version}-include.inczip" default-value="false"
     */
    private boolean skipIncludeDeployment;

    /**
     * Maven ProjectHelper.
     * 
     * @component
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    public void execute()
        throws MojoExecutionException
    {
        if ( skipIncludeDeployment )
        {
            return;
        }

        if ( this.sources.length != 0 )
        {
            try
            {
                ZipArchiver archiver = new ZipArchiver();
                
                boolean zipIt = false;
                for ( int i = 0; i < sources.length; ++i )
                {
                    if ( sources[i].isDeployable() )
                    {
                        DefaultFileSet fileSet = new DefaultFileSet();
                        
                        fileSet.setDirectory( sources[i].getDirectory() );
                        archiver.addFileSet( fileSet );
                        zipIt = true;
                    }
                }

                if ( zipIt )
                {
                    archiver.setDestFile( this.inZipFile );
                    archiver.createArchive();

                    projectHelper.attachArtifact( this.project, "inczip", null, this.inZipFile );
                }
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( e.getMessage(), e );
            }
        }
    }

}
