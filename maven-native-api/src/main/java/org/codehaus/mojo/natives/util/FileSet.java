package org.codehaus.mojo.natives.util;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


public class FileSet
{
    private File basedir;

    /** List of files, all relative to the basedir. */
    private File[] files;

    private static final File[] EMPTY_FILE_ARRAY = new File[0];

    public FileSet( File basedir )
    {
        this( basedir, EMPTY_FILE_ARRAY );
    }

    public FileSet( File basedir, File file )
    {
        this( basedir, new File[] { file } );
    }

    public FileSet( File basedir, String includes, String excludes ) throws IOException
    {
        this.basedir = basedir;
        
        excludes = this.trimCommaSeparateString( excludes );
        
        includes = this.trimCommaSeparateString( includes );

        files = (File[]) FileUtils.getFiles( basedir, includes, excludes ).toArray( EMPTY_FILE_ARRAY );
    }

    public FileSet( File basedir, File[] files )
    {
        if ( basedir == null )
        {
            throw new NullPointerException( "basedir must not be null" );
        }

        if ( files == null )
        {
            throw new NullPointerException( "files must not be null" );
        }

        this.basedir = basedir;
        this.files = files;
    }

    public File getBasedir()
    {
        return basedir;
    }

    public File[] getFiles()
    {
        return this.files;
    }

    public String toString() {
        return "basedir = " + basedir + "; files = " + Arrays.asList(files);
    }
    
    //temp solution until plexus-util is fix
    
    private String trimCommaSeparateString ( String in ) 
    {
    	if ( in == null || in.trim().length() == 0 ) 
    	{
    		return "";
    	}
    	
    	
    	StringBuffer out = new StringBuffer();
    	
    	String [] tokens = StringUtils.split( in, ",");
    	for ( int i = 0; i < tokens.length; ++i )
    	{
    		if ( i != 0 )
    		{
    			out.append( "," );
    		}
    		out.append( tokens[i].trim() );
    	}
    	
    	return out.toString();
    }
        
}
