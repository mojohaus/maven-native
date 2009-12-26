package org.codehaus.mojo.natives.c;

import java.io.File;
import org.codehaus.plexus.util.cli.Commandline;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.compiler.AbstractCompiler;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.mojo.natives.parser.Parser;
import org.codehaus.mojo.natives.parser.CParser;

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


public abstract class AbstractCCompiler
    extends AbstractCompiler
{
    /**
     * resuable parser in one Compilation session
     */

    private Parser parser = new CParser();

    protected abstract String getOutputFileOption();

    protected Parser getParser()
    {
        return this.parser;
    }


    /**
     * Setup Compiler Command line
     */
    protected Commandline getCommandLine( File srcFile, File destFile, CompilerConfiguration config )
        throws NativeBuildException
    {

        if ( config.getExecutable() == null )
        {
            config.setExecutable( "gcc" );
        }
        
        Commandline cl = new Commandline();

        cl.setExecutable( config.getExecutable() );

        if ( config.getWorkingDirectory() != null )
        {
            cl.setWorkingDirectory( config.getWorkingDirectory().getPath() );
        }

        this.setStartOptions( cl, config );

        this.setIncludePaths( cl, config.getIncludePaths() );

        this.setIncludePaths( cl, config.getSystemIncludePaths() );

        this.setMiddleOptions( cl, config );

        this.setOutputArgs( cl, destFile );
        
        this.setSourceArgs( cl, srcFile ) ;

        this.setEndOptions( cl, config );

        return cl;
    }

    private void setOptions( Commandline cl, String[] options )
    {
        if ( options != null )
        {
            for ( int i = 0; i < options.length; ++i )
            {
                cl.createArg().setValue( options[i] );
            }
        }
    }

    private void setStartOptions( Commandline cl, CompilerConfiguration config )
    {
        this.setOptions( cl, config.getStartOptions() );
    }

    private void setMiddleOptions( Commandline cl, CompilerConfiguration config )
    {
        this.setOptions( cl, config.getMiddleOptions() );
    }

    private void setEndOptions( Commandline cl, CompilerConfiguration config )
    {
        this.setOptions( cl, config.getEndOptions() );
    }

    private void setIncludePaths( Commandline cl, File [] includePaths )
    {
        if ( includePaths != null )
        {
            for ( int i = 0; i < includePaths.length; ++i )
            {
                cl.createArg().setValue( "-I" + includePaths[i].getPath() );
            }
        }
    }
    
    private void setOutputArgs( Commandline cl, File outputFile )
    {
        String outputFileOption = this.getOutputFileOption();

        if ( outputFileOption.endsWith( " " ) )
        {
            cl.createArg().setValue( outputFileOption.trim() );
            cl.createArg().setValue( outputFile.getPath() );
        }
        else
        {
            cl.createArg().setValue( outputFileOption + outputFile.getPath() );
        }    
    }
    
    private void setSourceArgs( Commandline cl, File srcFile )
    {
        cl.createArg().setValue( "-c" );
        cl.createArg().setValue( srcFile.getPath() );
    }    
}
