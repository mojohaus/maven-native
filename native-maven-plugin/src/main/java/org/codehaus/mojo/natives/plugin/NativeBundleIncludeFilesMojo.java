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
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.mojo.natives.NativeSources;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * Prepare include file bundle to be attached to maven for deployment purpose
 * @since 1.0-alpha-4
 */
@Mojo(name = "inczip", defaultPhase = LifecyclePhase.PACKAGE)
public class NativeBundleIncludeFilesMojo
    extends AbstractNativeMojo
{

    /**
     * Array of NativeSources containing include directories and source files.
     * @since 1.0-alpha-4
     */
    @Parameter
    private NativeSources[] sources = new NativeSources[0];

    /**
     * Archive file to bundle all enable NativeSources
     * @since 1.0-alpha-4
     */
    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}.inczip", required = true)
    private File incZipFile;

    /**
     * Option to skip include source bundle deployment
     * @since 1.0-alpha-4
     */
    @Parameter(defaultValue = "false")
    private boolean skipIncludeDeployment;

    /**
     * Maven ProjectHelper.
     * @since 1.0-alpha-4
     */
    @Component
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
                        fileSet.setUsingDefaultExcludes( true );
                        fileSet.setDirectory( sources[i].getDirectory() );
                        fileSet.setIncludes( sources[i].getIncludes() );
                        fileSet.setExcludes( sources[i].getExcludes() );
                        archiver.addFileSet( fileSet );
                        zipIt = true;
                    }
                }

                if ( zipIt )
                {
                    archiver.setDestFile( this.incZipFile );
                    archiver.createArchive();
                    projectHelper.attachArtifact( this.project, INCZIP_TYPE, null, this.incZipFile );
                }
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( e.getMessage(), e );
            }
        }
    }

}
