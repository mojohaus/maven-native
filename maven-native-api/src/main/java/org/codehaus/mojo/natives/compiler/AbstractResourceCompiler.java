package org.codehaus.mojo.natives.compiler;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.NativeSources;
import org.codehaus.mojo.natives.SourceDependencyAnalyzer;
import org.codehaus.mojo.natives.parser.CParser;
import org.codehaus.mojo.natives.parser.Parser;
import org.codehaus.mojo.natives.util.CommandLineUtil;
import org.codehaus.mojo.natives.util.EnvUtil;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.cli.Commandline;


public abstract class AbstractResourceCompiler
    extends AbstractLogEnabled
    implements ResourceCompiler
{

    protected abstract Commandline getCommandLine( ResourceCompilerConfiguration config, File source )
        throws NativeBuildException;

    public List compile( ResourceCompilerConfiguration config, NativeSources[] sources )
        throws NativeBuildException
    {
        File[] sourceFiles = NativeSources.getAllSourceFiles( sources );

        config.setIncludePaths( NativeSources.getIncludePaths( sources ) );

        config.setSystemIncludePaths( NativeSources.getSystemIncludePaths( sources ) );

        List compilerOutputFiles = new ArrayList( sourceFiles.length );

        for ( int i = 0; i < sourceFiles.length; ++i )
        {
            File src = sourceFiles[i];

            File outputFile = config.getOutputFile( src );

            compilerOutputFiles.add( outputFile );

            if ( isResourceFileStaled( src, outputFile, config.getIncludePaths() ) )
            {
                Commandline cl = getCommandLine( config, src );

                EnvUtil.setupCommandlineEnv( cl, config.getEnvFactory() );

                CommandLineUtil.execute( cl, this.getLogger() );
            }
        }

        return compilerOutputFiles;

    }

    private boolean isResourceFileStaled( File src, File dest, File[] includePaths )
        throws NativeBuildException
    {
        Parser parser = new CParser();

        try
        {
            if ( !SourceDependencyAnalyzer.isStaled( src, dest, parser, includePaths ) )
            {
                this.getLogger().info( src.getPath() + " is up to date." );
                return false;
            }
        }
        catch ( NativeBuildException ioe )
        {
            throw new NativeBuildException( "Error analyzing " + src.getPath() + " dependencies.", ioe );
        }

        return true;
    }

}
