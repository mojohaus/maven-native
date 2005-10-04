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
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="dantran@gmail.com">Dan T. Tran</a>
 * @version $Id:$
 */
public abstract class AbstractNativeMojo
    extends AbstractMojo
{
    protected static final List EMPTY_FILE_LIST = new ArrayList();
	
    /**
     * Some compiler can take advantage of this setting to add 
     * additional environments ( ex msvc, bcc, etc)
     * @parameter 
     * @optional
     */
    protected File providerHome;
	
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
	
	
    /**
     * @description where to place the final packaging
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    protected File outputDirectory;

    /**
     * Internal readonly property
     * @parameter expression="${basedir}
     * @required
     * @readonly
     */
	protected File basedir;

    /**
     * Temporary file to store all linkable input file paths by other mojos
     * @parameter expression="${project.build.directory}/object-file-list.txt
     * @required
     * @readonly
     */
    protected File compilerOutputListFile;
 
    
    protected static String [] removeEmptyOptions( String [] args )
    {
    	return NativeMojoUtils.trimParams ( args );
    }
    
}
