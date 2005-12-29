package org.codehaus.mojo.natives;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

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

/**
 * Compilable list of source file in a directory
 * @author dantran@gmail.com
 * @description 
 * @version $Id$
 */
public class NativeSources
{
    private File directory;
    
    private String [] fileNames = new String [0];
    
    private boolean dependencyAnalysisParticipation = true;
    
    private boolean includePathActive = true;
    
    public NativeSources()
    {
        
    }
    
    public File getDirectory()
    {
        return this.directory;
    }
    
    public void setDirectory( File directory )
    {
        this.directory = directory;
    }
    
    public String [] getFileNames()
    {
        return this.fileNames;
    }
    
    public void setFileNames( String [] fileNames )
    {
        if ( fileNames == null )
        {
            this.fileNames = new String [0];
        }
        
        this.fileNames = fileNames;
    }
    
    public boolean getDependencyAnalysisParticipation()
    {
        return this.dependencyAnalysisParticipation;
    }
    
    public void setDependencyAnalysisParticipation( boolean flag )
    {
        this.dependencyAnalysisParticipation = flag;
    }
    
    public boolean getIncludePathActive()
    {
        return this.includePathActive;
    }
    
    public void setIncludePathActive( boolean flag )
    {
        this.includePathActive = flag;
    }
    
    /////////////////////////////////////////////////////////////////////////
    //                              HELPERS
    /////////////////////////////////////////////////////////////////////////
    private List appendSourceList( List appendList )
    {
        for ( int i = 0 ; i < this.fileNames.length; ++i )
        {
            File source = new File( this.directory.getAbsolutePath() + "/" + this.fileNames[i] );
            
            appendList.add( source );
        }
        
        return appendList;
    }

    private List appendSourceListWithExtensionTranlation( List appendList, String extension )
    {
        for ( int i = 0 ; i < this.fileNames.length; ++i )
        {
            String [] tokens = StringUtils.split( this.getFileNames()[i], "." );
            
            String fileNameWithoutExtension = "";
            
            for ( int t = 0 ; t < tokens.length - 1; ++t  )
            {
                fileNameWithoutExtension += tokens[t];
                if ( t != tokens.length - 2 )
                {
                    fileNameWithoutExtension += ".";
                }
            }
            
            File source = new File ( directory.getAbsolutePath() + "/" + fileNameWithoutExtension + "." + extension );
            
            appendList.add( source );
        }
        
        return appendList;
    }

    /**
     * Helper to get all source files in a Array of NativeSources
     * @param sources
     * @return
     */
    public static File [] getAllSourceFiles( NativeSources [] sources )
    {
        if ( sources == null ) 
        {
            return new File [0];
        }
        
        List sourceFiles = new ArrayList();
        
        for ( int i = 0 ; i < sources.length; ++i )
        {
            sources[i].appendSourceList( sourceFiles );
        }
        
        
        return (File []) sourceFiles.toArray( new File[0] );
        
    }

    /**
     * Helper to get all source files in a Array of NativeSources
     * @param sources
     * @return
     */
    public static File [] getAllSourceFilesWithExtensionTranslation( NativeSources [] sources, String extension )
    {
        if ( sources == null ) 
        {
            return new File [0];
        }
        
        List sourceFiles = new ArrayList();
        
        for ( int i = 0 ; i < sources.length; ++i )
        {
            sources[i].appendSourceListWithExtensionTranlation( sourceFiles, extension );
        }
        
        return (File []) sourceFiles.toArray( new File[0] );
        
    }
    
    public static File [] getAllDirectories( NativeSources [] sources )
    {
        File [] directories = new File[sources.length];
        
        for ( int i = 0 ; i < sources.length; ++i )
        {
            directories[i] = sources[i].getDirectory();
        }
        
        return directories;
    }
    
    public static File [] getIncludePaths( NativeSources [] sources )
    {
        if ( sources == null ) 
        {
            return new File [0];
        }
        
        List list = new ArrayList();

        for ( int i = 0 ; i < sources.length; ++i )
        {
            if ( sources[i].getIncludePathActive() && sources[i].getDependencyAnalysisParticipation() )
            {
                list.add( sources[i].getDirectory() );
            }
        }
        
        return ( File [] ) list.toArray( new File[0] ) ;
    }
            
    public static File [] getSystemIncludePaths( NativeSources [] sources )
    {
        if ( sources == null ) 
        {
            return new File [0];
        }
        
        List list = new ArrayList();

        for ( int i = 0 ; i < sources.length; ++i )
        {
            if ( sources[i].getIncludePathActive() && !sources[i].getDependencyAnalysisParticipation() )
            {
                list.add( sources[i].getDirectory() );
            }
        }
        
        return ( File [] ) list.toArray( new File[0] ) ;
    }
            
    
}
