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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;

/**
 * Unpack any .inczip dependencies to be included as system include path
 *
 * @goal unzipinc
 * @phase generate-sources
 * @requiresDependencyResolution compile
 *
 * @since 1.0-alpha-4
 */
public class NativeUnZipIncMojo
    extends AbstractNativeMojo
{

    /**
     * Internal
     * @parameter default-value="${project.build.directory}/native/markers"
     * @required
     * @since 1.0-alpha-4
     */
    private File dependencyIncZipMarkerDirectory;
    
    /**
     * Internal component for archiving purposes
     * 
     * @component
     * @readonly
     * @since 1.0-alpha-4
     */
    private ArchiverManager archiverManager;
    

    public void execute()
        throws MojoExecutionException
    {
        if ( unpackIncZipDepenedencies() )
        {
            this.getPluginContext().put( AbstractNativeMojo.INCZIP_FOUND, new Boolean( "true") );
        }
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
                    unpackZipFile( incZipFile );

                    marker.delete();

                    if ( ! dependencyIncZipMarkerDirectory.exists() )
                    {
                        dependencyIncZipMarkerDirectory.mkdirs();
                    }

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

    protected void unpackZipFile( File zipFile )
        throws MojoExecutionException
    {
        System.out.println( "Unpacking: " + zipFile );

        try
        {
            if ( ! dependencyIncludeDirectory.exists() )
            {
                dependencyIncludeDirectory.mkdirs();
            }
            
            
            UnArchiver archiver = this.archiverManager.getUnArchiver( "zip" );
            archiver.setOverwrite( true );
            archiver.setDestDirectory( this.dependencyIncludeDirectory );
            archiver.setSourceFile( zipFile );
            archiver.extract();
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

        Set artifacts = this.project.getDependencyArtifacts();

        if ( artifacts != null )
        {
            for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
            {
                Artifact artifact = (Artifact) iter.next();

                //pick up only native header archive
                if ( !INCZIP_TYPE.equals( artifact.getType() ) )
                {
                    continue;
                }

                list.add( artifact );
            }
        }

        return list;
    }

}
