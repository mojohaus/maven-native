package org.codehaus.mojo.natives.javah;

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

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.util.CommandLineUtil;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;

/**
 * Sun's javah compatible implementation
 */

public class JavahExecutable
    extends AbstractJavah
{
    public JavahExecutable()
    {
    }

    public void compile( JavahConfiguration config )
        throws NativeBuildException
    {
        Commandline cl = this.createJavahCommand( config );

        CommandLineUtil.execute( cl, this.getLogger() );
    }

    protected Commandline createJavahCommand( JavahConfiguration config )
        throws NativeBuildException
    {
        this.validateConfiguration( config );

        Commandline cl = new Commandline();

        if ( config.getWorkingDirectory() != null )
        {
            cl.setWorkingDirectory( config.getWorkingDirectory().getPath() );
        }

        cl.setExecutable( this.getJavaHExecutable( config ) );

        if ( config.getFileName() != null && config.getFileName().length() > 0 )
        {
            File outputFile = new File( config.getOutputDirectory(), config.getFileName() );
            cl.createArg().setValue( "-o" );
            cl.createArg().setFile( outputFile );
        }
        else
        {
            if ( config.getOutputDirectory() != null )
            {
                cl.createArg().setValue( "-d" );
                cl.createArg().setFile( config.getOutputDirectory() );
            }
        }

        String[] classPaths = config.getClassPaths();

        StringBuffer classPathBuffer = new StringBuffer();

        for ( int i = 0; i < classPaths.length; ++i )
        {
            classPathBuffer.append( classPaths[i] );
            if ( i != classPaths.length - 1 )
            {
                classPathBuffer.append( File.pathSeparatorChar );
            }
        }

        cl.createArg().setValue( "-classpath" );

        cl.createArg().setValue( classPathBuffer.toString() );

        if ( config.getVerbose() )
        {
            cl.createArg().setValue( "-verbose" );
        }

        cl.addArguments( config.getClassNames() );

        return cl;
    }

    private void validateConfiguration( JavahConfiguration config )
        throws NativeBuildException
    {
        if ( config.getClassPaths() == null || config.getClassPaths().length == 0 )
        {
            throw new NativeBuildException( "javah classpaths can not be empty." );
        }

        if ( config.getOutputDirectory() == null )
        {
            throw new NativeBuildException( "javah destDir can not be empty." );
        }

        if ( !config.getOutputDirectory().exists() )
        {
            config.getOutputDirectory().mkdirs();
        }

        if ( config.getClassNames() == null || config.getClassNames().length == 0 )
        {
            throw new NativeBuildException( "javah: java classes can not be empty." );
        }

    }

    /**
     * @return
     */
    protected String getJavaHExecutable( JavahConfiguration config )
    {
        String path = "javah";

        if ( config.getJavahPath() != null )
        {
            path = config.getJavahPath().getAbsolutePath();
        }

        return path;
    }

}
