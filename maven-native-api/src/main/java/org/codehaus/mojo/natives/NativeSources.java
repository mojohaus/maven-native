package org.codehaus.mojo.natives;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.DirectoryScanner;

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

/**
 * Compilable list of source file in a directory
 * @author dantran@gmail.com
 * @description 
 * @version $Id$
 */
public class NativeSources
{
    private File directory;

    private String[] fileNames = new String[0];

    private boolean dependencyAnalysisParticipation = true;

    /**
     * Hint Maven to bundle up all file under 'directory' and deploy
     */
    private boolean deployable = false;

    /**
     * ANT expression to get all compilable source files
     */
    private String[] includes;

    /**
     * ANT expression for source exclusions
     */
    private String[] excludes;

    public NativeSources()
    {

    }

    /**
     * 
     * @return
     */
    public File getDirectory()
    {
        return this.directory;
    }

    /**
     * 
     * @param directory
     */
    public void setDirectory( File directory )
    {
        this.directory = directory;
    }

    /**
     * 
     * @return
     */
    public String[] getFileNames()
    {
        return this.fileNames;
    }

    /**
     * 
     * @param fileNames
     */
    public void setFileNames( String[] fileNames )
    {
        if ( fileNames == null )
        {
            this.fileNames = new String[0];
        }

        this.fileNames = fileNames;
    }

    /**
     * 
     * @return
     */
    public String[] getIncludes()
    {
        return this.includes;
    }

    /**
     * 
     * @param includes
     */
    public void setIncludes( String[] includes )
    {
        this.includes = includes;
    }

    /**
     * 
     * @return
     */
    public String[] getExcludes()
    {
        return this.excludes;
    }

    /**
     * 
     * @param excludes
     */
    public void setExcludes( String[] excludes )
    {
        this.excludes = excludes;
    }

    /**
     * 
     * @return
     */
    public boolean getDependencyAnalysisParticipation()
    {
        return this.dependencyAnalysisParticipation;
    }

    /**
     * 
     * @param flag
     */
    public void setDependencyAnalysisParticipation( boolean flag )
    {
        this.dependencyAnalysisParticipation = flag;
    }

    public boolean isDeployable()
    {
        return deployable;
    }

    public void setDeployable( boolean deployable )
    {
        this.deployable = deployable;
    }

    ////////////////////////////////////////////////////////////////////////////
    
    public List getFiles()
    {
        String[] filePaths = new String[0];

        if ( includes != null || excludes != null )
        {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir( this.directory );
            scanner.setIncludes( includes );
            scanner.setExcludes( excludes );
            scanner.addDefaultExcludes();

            scanner.scan();

            filePaths = scanner.getIncludedFiles();
        }

        List files = new ArrayList( filePaths.length + this.fileNames.length );
        for ( int i = 0; i < filePaths.length; ++i )
        {
            files.add( new File( this.directory, filePaths[i] ) );
        }

        //remove duplicate files 
        for ( int i = 0; i < this.fileNames.length; ++i )
        {
            File file = new File( this.directory, this.fileNames[i] );

            boolean found = false;

            for ( int k = 0; k < filePaths.length; ++k )
            {
                if ( files.get( k ).equals( file ) )
                {
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                files.add( file );
            }
        }

        return files;

    }

    /////////////////////////////////////////////////////////////////////////
    //                              HELPERS
    /////////////////////////////////////////////////////////////////////////

    /**
     * Helper to get all source files in a Array of NativeSources
     * @param sources
     * @return
     */
    public static File[] getAllSourceFiles( NativeSources[] sources )
    {
        if ( sources == null )
        {
            return new File[0];
        }

        List sourceFiles = new ArrayList();

        for ( int i = 0; i < sources.length; ++i )
        {
            sourceFiles.addAll( sources[i].getFiles() );
        }

        return (File[]) sourceFiles.toArray( new File[sourceFiles.size()] );
    }

    public static File[] getIncludePaths( NativeSources[] sources )
    {
        if ( sources == null )
        {
            return new File[0];
        }

        List list = new ArrayList();

        for ( int i = 0; i < sources.length; ++i )
        {
            if ( sources[i].getDependencyAnalysisParticipation() )
            {
                list.add( sources[i].getDirectory() );
            }
        }

        return (File[]) list.toArray( new File[0] );
    }

    public static File[] getSystemIncludePaths( NativeSources[] sources )
    {
        if ( sources == null )
        {
            return new File[0];
        }

        List list = new ArrayList();

        for ( int i = 0; i < sources.length; ++i )
        {
            if ( !sources[i].getDependencyAnalysisParticipation() )
            {
                list.add( sources[i].getDirectory() );
            }
        }

        return (File[]) list.toArray( new File[0] );
    }

}
