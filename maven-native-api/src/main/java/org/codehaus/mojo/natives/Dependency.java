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
package org.codehaus.mojo.natives;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.codehaus.mojo.natives.parser.Parser;

/**
 * Dependency analizer of a native source file
 *
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */

public class Dependency
{
    /**
     * Field source
     */
    private String source;

    /**
     * Field lastModified
     */
    private long lastModified = 0;

    /**
     * Field dependencies
     */
    private java.util.List<Dependency> dependencies;

    private Parser parser;

    private File[] includePaths;

    Dependency parent;

    public Dependency( Dependency parent, File source, Parser parser, File[] includePaths )
    {
        init( parent, source, parser, includePaths );
    }

    public Dependency( File source, Parser parser, File[] includePaths )
    {
        init( null, source, parser, includePaths );
    }

    private void init( Dependency parent, File source, Parser parser, File[] includePaths )
    {
        this.parent = parent;

        this.source = source.getPath();

        this.lastModified = source.lastModified();

        this.parser = parser;

        if ( includePaths == null )
        {
            this.includePaths = new File[0];
        }
        else
        {
            this.includePaths = includePaths;
        }
    }

    public void analyze()
        throws IOException
    {
        String[] includeNames = getIncludeNames();

        File[] resolvedIncludeFiles = resolveIncludeNames( includeNames );

        for ( File fileName : resolvedIncludeFiles )
        {
            Dependency depend = new Dependency( this, fileName, this.parser, this.includePaths );

            if ( !this.getRoot().contains( depend ) )
            {
                this.addDependency( depend );
            }
        }

        for ( int i = 0; i < this.getDependencies().size(); ++i )
        {
            Dependency depend = this.getDependencies().get( i );
            depend.analyze();
        }

    }

    private Dependency getRoot()
    {
        Dependency root = this;

        while ( root.getParent() != null )
        {
            root = root.getParent();
        }

        return root;
    }

    public Dependency getParent()
    {
        return this.parent;
    }

    public long getCompositeLastModified()
    {
        long currentLastModify = this.lastModified;

        for ( Dependency dependency : this.getDependencies() )
        {
            long lastModified = dependency.getCompositeLastModified();

            if ( lastModified > currentLastModify )
            {
                currentLastModify = lastModified;
            }
        }

        return currentLastModify;
    }

    private String[] getIncludeNames()
        throws IOException
    {
        try ( Reader reader = new BufferedReader( new FileReader( this.source ) ) )
        {
            parser.parse( reader );
            return parser.getIncludes();
        }
    }

    /**
     * @param includeNames
     * @return
     * @throws IOException
     */
    private File[] resolveIncludeNames( String[] includeNames )
        throws IOException
    {
        ArrayList<File> resolvedIncludeFiles = new ArrayList<>( includeNames.length );

        for ( String includeName : includeNames )
        {
            File resolvedFile = resolveSingleIncludeName( includeName );

            if ( resolvedFile != null )
            {
                resolvedIncludeFiles.add( resolvedFile );
            }
        }

        File[] arrayResolvedIncludeFiles = new File[resolvedIncludeFiles.size()];

        for ( int j = 0; j < arrayResolvedIncludeFiles.length; ++j )
        {
            arrayResolvedIncludeFiles[j] = resolvedIncludeFiles.get( j );
        }

        return arrayResolvedIncludeFiles;
    }

    /**
     * Search for file that matches an include name with all available include paths
     *
     * @param includeName
     * @return an file or null when it is not found in user include path
     * @throws IOException
     */

    private File resolveSingleIncludeName( String includeName )
        throws IOException
    {
        File includeFile = null;

        File[] sourcePath = new File[1];

        sourcePath[0] = new File( new File( this.source ).getParent() ); // TODO

        includeFile = this.resolveSingleIncludeNameFromPaths( includeName, sourcePath );

        if ( includeFile == null )
        {
            includeFile = this.resolveSingleIncludeNameFromPaths( includeName, this.includePaths );
        }

        return includeFile;
    }

    /**
     * Translate an include file
     *
     * @param includeName
     * @param includePath
     * @return
     */
    private File resolveSingleIncludeNameFromPaths( String includeName, File[] includePath )
    {
        File includeFile = null;

        for ( File file : includePath )
        {
            File tmpFile = new File( file, includeName );

            // make sure we dont pickup directory like STL which has no extension
            if ( tmpFile.exists() && tmpFile.isFile() )
            {
                includeFile = tmpFile;

                break;
            }
        }

        return includeFile;
    }

    /**
     * Method addDependency
     *
     * @param dependency
     */
    public void addDependency( Dependency dependency )
    {
        getDependencies().add( dependency );
    }

    /**
     * Method getDependencies
     */
    public java.util.List<Dependency> getDependencies()
    {
        if ( this.dependencies == null )
        {
            this.dependencies = new java.util.ArrayList<>();
        }

        return this.dependencies;
    }

    /**
     * Method getLastModified
     */
    public long getLastModified()
    {
        return this.lastModified;
    }

    /**
     * Method getSource
     */
    public String getSource()
    {
        return this.source;
    }

    // helper for testing only
    boolean contains( Dependency dependent )
    {
        if ( this.source.equals( dependent.getSource() ) )
        {
            return true;
        }

        for ( int i = 0; i < this.getDependencies().size(); ++i )
        {
            Dependency node = this.getDependencies().get( i );
            if ( node.contains( dependent ) )
            {
                return true;
            }
        }

        return false;
    }

    int getDeepDependencyCount()
    {
        int ret = this.getDependencies().size();
        for ( int i = 0; i < this.getDependencies().size(); ++i )
        {
            Dependency node = this.getDependencies().get( i );
            ret += node.getDeepDependencyCount();
        }

        return ret;
    }
}
