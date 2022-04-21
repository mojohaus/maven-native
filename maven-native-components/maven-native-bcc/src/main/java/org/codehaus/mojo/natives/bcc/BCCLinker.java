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
package org.codehaus.mojo.natives.bcc;

import java.io.File;
import java.util.List;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.c.CLinker;
import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.Commandline;

@Component(role = Linker.class, hint = "bcc", instantiationStrategy = "per-lookup")
public class BCCLinker
    extends CLinker
{

    public static final String DEFAULT_EXECUTABLE = "ilink32";

    @Override
    protected Commandline createLinkerCommandLine( List<File> objectFiles, LinkerConfiguration config )
        throws NativeBuildException
    {
        Commandline cl = new Commandline();

        cl.setWorkingDirectory( config.getWorkingDirectory().getPath() );

        String executable = DEFAULT_EXECUTABLE;

        /**
         * Turbo Incremental Link 5.68 Copyright (c) 1997-2005 Borland Syntax: ILINK32 options objfiles, exefile,
         * mapfile, libfiles, deffile, resfiles
         */
        if ( config.getExecutable() != null && config.getExecutable().trim().length() != 0 )
        {
            executable = config.getExecutable();
        }

        cl.createArg().setValue( executable );

        cl.addArguments( config.getStartOptions() );

        // objfiles
        for ( File objFile : objectFiles )
        {
            cl.createArg().setValue( objFile.getPath() );
        }

        for ( String fileName : config.getExternalLibFileNames() )
        {
            if ( !FileUtils.getExtension( fileName ).equalsIgnoreCase( "res" ) )
            {
                cl.createArg().setFile( new File( config.getExternalLibDirectory(), fileName ) );
            }
        }

        // ouput file
        cl.createArg().setValue( "," + config.getOutputFile() );

        // map files + system lib, and def file to be given by user in middle options
        // a comma is required between map, lib, and def
        cl.createArg().setValue( "," );
        cl.addArguments( config.getMiddleOptions() );

        // res file
        cl.createArg().setValue( "," );
        for ( String fileName : config.getExternalLibFileNames() )
        {
            if ( FileUtils.getExtension( fileName ).equalsIgnoreCase( "res" ) )
            {
                cl.createArg().setFile( new File( config.getExternalLibDirectory(), fileName ) );
            }
        }

        return cl;
    }
}
