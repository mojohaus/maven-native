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
package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.natives.EnvFactory;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.manager.EnvFactoryManager;

public abstract class AbstractNativeMojo
    extends AbstractMojo
{
    public static final String LINKER_INPUT_LIST_NAME = "NativeLinkerInputListName";

    public static final String LINKER_OUTPUT_PATH = "NativeLinkerOutputPath";

    public static final String INCZIP_FOUND = "IncZipFound";

    public static final String INCZIP_TYPE = "inczip";

    protected static final List<File> EMPTY_FILE_LIST = new ArrayList<>();

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * user directory when external tools( ie compiler/linker ) are invoked
     */
    @Parameter(defaultValue = "${basedir}", required = true)
    protected File workingDirectory;

    /**
     * Specifies a fully qualified class name implementing the org.codehaus.mojo.natives.EnvFactory interface. The class
     * creates a set environment variables to be used with the command line.
     */
    @Parameter
    private String envFactoryName;

    @Component
    protected EnvFactoryManager envFactoryManager;

    /**
     * Directory to unpack .inczip dependency files to be included as system include path
     */
    @Parameter(defaultValue = "${project.build.directory}/native/include")
    protected File dependencyIncludeDirectory;

    protected static String[] removeEmptyOptions( List<String> args )
    {
        return NativeMojoUtils.trimParams( args );
    }

    protected List<File> getAllCompilersOutputFileList()
    {
        @SuppressWarnings("unchecked")
        List<File> list = (List<File>) this.getPluginContext().get( AbstractNativeMojo.LINKER_INPUT_LIST_NAME );

        if ( list == null )
        {
            list = new ArrayList<>();

            @SuppressWarnings({ "unchecked", "unused" })
            Object unchecked = this.getPluginContext().put( AbstractNativeMojo.LINKER_INPUT_LIST_NAME, list );
        }

        return list;

    }

    protected void saveCompilerOutputFilePaths( List<File> filePaths )
        throws MojoExecutionException
    {
        List<File> allCompilerOutputFileList = getAllCompilersOutputFileList();
        allCompilerOutputFileList.addAll( filePaths );
    }

    /**
     * Internal for unit test only
     */

    protected MavenProject getProject()
    {
        return this.project;
    }

    private EnvFactory envFactory = null;

    protected EnvFactory getEnvFactory()
        throws MojoExecutionException
    {
        if ( envFactory == null && envFactoryName != null )
        {
            try
            {
                envFactory = this.envFactoryManager.getEnvFactory( this.envFactoryName );
            }
            catch ( NativeBuildException e )
            {
                throw new MojoExecutionException( e.getMessage(), e );
            }
        }

        return envFactory;
    }

}
