package org.codehaus.mojo.natives.c;

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

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.AbstractLinker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Generic C/CPP linker with "-o " as its output option 
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */

public class CLinker
    extends AbstractLinker
{

    /**
     * @return Commandline of a linker base on its configuration and object files
     */
    protected Commandline createLinkerCommandLine( List objectFiles, LinkerConfiguration config )
        throws NativeBuildException
    {
        if ( config.getExecutable() == null )
        {
            config.setExecutable( "gcc" );
        }

        Commandline cl = new Commandline();

        cl.setWorkingDirectory( config.getWorkingDirectory().getPath() );

        cl.setExecutable( config.getExecutable() );

        if ( config.getStartOptions() != null )
        {
            cl.addArguments( config.getStartOptions() );
        }

        String linkerOutputOption = this.getLinkerOutputOption();
        if ( linkerOutputOption.endsWith( " " ) )
        {
            cl.createArgument().setValue( linkerOutputOption.substring( 0, linkerOutputOption.length() - 1 ) );
            cl.createArgument().setFile( config.getOutputFile() );
        }
        else
        {
            cl.createArgument().setValue( linkerOutputOption + config.getOutputFile() );
        }

        for ( int i = 0; i < objectFiles.size(); ++i )
        {
            File objFile = (File) objectFiles.get( i );
            String objFilePath = truncatePath( objFile.getPath(), config.getWorkingDirectory().getPath() );
            cl.createArgument().setValue( objFilePath );
        }

        if ( config.getMiddleOptions() != null )
        {
            cl.addArguments( config.getMiddleOptions() );
        }

        setCommandLineForExternalLibraries( cl, config );

        if ( config.getEndOptions() != null )
        {
            cl.addArguments( config.getEndOptions() );
        }

        return cl;

    }
    
    /**
     * 
     * @return output option flag of a generic C linker
     */
    protected String getLinkerOutputOption()
    {
        return "-o ";
    }

    /**
     * Setup Commandline to handle external library depending on extention type
     * @param cl Commandline
     * @param config LinkerConfiguration
     * @throws NativeBuildException
     */
    protected void setCommandLineForExternalLibraries( Commandline cl, LinkerConfiguration config )
        throws NativeBuildException
    {
        if ( config.getExternalLibFileNames().size() == 0 )
        {
            return;
        }

        boolean hasUnixLinkage = false;

        for ( Iterator iter = config.getExternalLibFileNames().iterator(); iter.hasNext(); )
        {
            String libFileName = (String) iter.next();

            String ext = FileUtils.getExtension( libFileName );

            if ( "o".equals( ext ) || "obj".equals( ext ) || "lib".equals( ext ) || "dylib".equals( ext ) )
            {
                File libFile = new File( config.getExternalLibDirectory(), libFileName );
                String relativeLibFile = truncatePath( libFile.getPath(), config.getWorkingDirectory().getPath() );
                cl.createArgument().setValue( relativeLibFile );
            }
            else if ( "a".equals( ext ) || "so".equals( ext ) || "sl".equals( ext ) )
            {
                hasUnixLinkage = true;
            }
        }

        if ( hasUnixLinkage )
        {
            cl.createArgument().setValue( "-L" + config.getExternalLibDirectory() );
        }

        for ( Iterator iter = config.getExternalLibFileNames().iterator(); iter.hasNext(); )
        {
            String libFileName = (String) iter.next();

            String ext = FileUtils.getExtension( libFileName );

            if ( "a".equals( ext ) || "so".equals( ext ) || "sl".equals( ext ) )
            {
                String libName = FileUtils.removeExtension( libFileName );

                if ( libFileName.startsWith( "lib" ) )
                {
                    libName = libName.substring( "lib".length() );
                }

                cl.createArgument().setValue( "-l" + libName );
            }
        }
    }

    /**
     * @param path String
     * @param baseDirectory String
     * @return relative path to a base directory if possible
     */
    private String truncatePath( String path, String baseDirectory )
    {
        if ( path.indexOf( baseDirectory ) >= 0 )
        {
            path = path.substring( path.indexOf( baseDirectory ) + baseDirectory.length() );
            if ( path.startsWith( File.separator ) )
            {
                path = path.substring( File.separator.length() );
            }
        }
        return path;
    }
}
