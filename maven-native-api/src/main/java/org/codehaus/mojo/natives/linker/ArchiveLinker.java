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
package org.codehaus.mojo.natives.linker;

import java.io.File;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Commandline;

public class ArchiveLinker
    extends AbstractLinker
{

    public static final String EXECUTABLE = "ar";

    public ArchiveLinker()
    {
    }

    @Override
    protected Commandline createLinkerCommandLine( List<File> objectFiles, LinkerConfiguration config )
    {
        Commandline cl = new Commandline();

        cl.setWorkingDirectory( config.getWorkingDirectory().getPath() );

        String executable = EXECUTABLE;

        if ( !StringUtils.isBlank( config.getExecutable() ) )
        {
            executable = config.getExecutable();
        }

        cl.setExecutable( executable );

        for ( int i = 0; i < config.getStartOptions().length; ++i )
        {
            cl.createArg().setValue( config.getStartOptions()[i] );
        }

        // the next 2 are for completeness, the start options should be good enough
        for ( int i = 0; i < config.getMiddleOptions().length; ++i )
        {
            cl.createArg().setValue( config.getMiddleOptions()[i] );
        }

        for ( int i = 0; i < config.getEndOptions().length; ++i )
        {
            cl.createArg().setValue( config.getEndOptions()[i] );
        }

        cl.createArg().setFile( config.getOutputFile() );

        for ( File objFile : objectFiles )
        {
            cl.createArg().setValue( objFile.getPath() );
        }

        return cl;

    }
}
