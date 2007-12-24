package org.codehaus.mojo.natives.compiler;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.SourceDependencyAnalyzer;
import org.codehaus.mojo.natives.parser.Parser;
import org.codehaus.mojo.natives.util.CommandLineUtil;
import org.codehaus.mojo.natives.util.EnvUtil;
import org.codehaus.mojo.natives.util.FileUtil;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */

public abstract class AbstractCompiler
    extends AbstractLogEnabled
    implements Compiler
{

    protected abstract Parser getParser();

    protected abstract Commandline getCommandLine( File src, File dest, CompilerConfiguration config )
        throws NativeBuildException;

    public List compile( CompilerConfiguration config, File[] sourceFiles )
        throws NativeBuildException
    {
        List compilerOutputFiles = new ArrayList( sourceFiles.length );

        for ( int i = 0; i < sourceFiles.length; ++i )
        {
            File source = sourceFiles[i];

            File objectFile = getObjectFile( source, config.getWorkingDirectory(), config.getOutputDirectory(), config
                .getObjectFileExtension() );

            compilerOutputFiles.add( objectFile );

            Parser parser = this.getParser();

            if ( SourceDependencyAnalyzer.isStaled( source, objectFile, parser, config.getIncludePaths() ) )
            {
                Commandline cl = getCommandLine( source, objectFile, config );

                EnvUtil.setupCommandlineEnv( cl, config.getEnvFactoryName() );

                CommandLineUtil.execute( cl, this.getLogger() );

            }
            else
            {
                this.getLogger().debug( ( objectFile + " is up to date." ) );
            }
        }

        return compilerOutputFiles;
    }

    /**
     * return "obj" or "o" when file extension is not given based on current platform
     * @return
     */
    protected static String getObjectFileExtension( String fileExtension )
    {
        if ( fileExtension != null )
        {
            return fileExtension;
        }
        else
        {
            if ( Os.isFamily( "windows" ) )
            {
                return "obj";
            }
            else
            {
                return "o";
            }
        }
    }

    /**
     * Figure out the object file relative path from a given source file
     * @param sourceFile
     * @param workingDirectory
     * @param outputDirectory
     * @param config
     * @return
     */
    protected static File getObjectFile( File sourceFile, File workingDirectory, File outputDirectory,
                                         String objectFileExtension )
        throws NativeBuildException
    {
        String objectFileName;

        try
        {
            objectFileExtension = AbstractCompiler.getObjectFileExtension( objectFileExtension );

            //plexus-util requires that we remove all ".." in the the file source, so getCanonicalPath is required
            // other filename with .. and no extension will throw StringIndexOutOfBoundsException

            objectFileName = FileUtils.basename( sourceFile.getCanonicalPath() );

            if ( objectFileName.charAt( objectFileName.length() - 1 ) != '.' )
            {
                objectFileName += "." + objectFileExtension;
            }
            else
            {
                objectFileName += objectFileExtension;
            }
        }
        catch ( IOException e )
        {
            throw new NativeBuildException( e.getMessage() );
        }

        File objectFile = new File( outputDirectory, objectFileName );

        return FileUtil.getRelativeFile( workingDirectory, objectFile );

    }

}
