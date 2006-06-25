package org.codehaus.mojo.natives.plugin;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id$
 */

public abstract class AbstractNativeMojo
    extends AbstractMojo
{
	public static final String LINKER_INPUT_LIST_NAME = "NativeLinkerInputListName";
		
    protected static final List EMPTY_FILE_LIST = new ArrayList();
	
	
    /**
     * POM
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
	
	
    /**
     * Where to place the final packaging and compiler object files
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    protected File outputDirectory;


    /**
     * Specifies a fully qualified class name implementing the 
     * org.codehaus.mojo.natives.EnvFactory interface. The class creates 
     * a set environment variables to be used with the command line.
     * @parameter
     */
    protected String envFactoryName;   
    
    
    protected static String [] removeEmptyOptions( String [] args )
    {
    	return NativeMojoUtils.trimParams ( args );
    }
    
    protected List getAllCompilersOutputFileList( )
    {
        List list = (List) this.getPluginContext().get( AbstractNativeMojo.LINKER_INPUT_LIST_NAME );
        
        if ( list == null ) 
        {
        	list = new ArrayList();
        	
        	this.getPluginContext().put( AbstractNativeMojo.LINKER_INPUT_LIST_NAME, list );
        }
        
        return list;

    }
    
    protected void saveCompilerOutputFilePaths( List filePaths )
       throws MojoExecutionException
    {
    	List allCompilerOutputFileList = getAllCompilersOutputFileList();
    	
        for ( int i = 0; i < filePaths.size(); ++i )
        {
    	    File file = (File) filePaths.get(i);
    	    allCompilerOutputFileList.add( file );
        }
    }
}
    
