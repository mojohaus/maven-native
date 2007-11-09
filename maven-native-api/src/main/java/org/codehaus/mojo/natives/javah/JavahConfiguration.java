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

import java.io.File;

public class JavahConfiguration
{
    /**
     * Working directory where javah command will run
     */
    private File workingDirectory;

    /**
     *  Directory to save generate files, must either be fullpath 
     *  or relative to relative workingDirectory
     */
    private File outputDirectory;

    /**
     * Option to combine all generated include files into one file ${outputDirectory}/${fileName}
     * Support javah -o option
     */
    private String fileName;

    /**
     * Location for the actual binary. This may be <code>nulL</code>
     */
    private File javahPath;

    /*
     *  the fully-qualified class name
     */
    private String[] classNames;

    /**
     * ClassPaths to locate classNames
     */
    private String[] classPaths;

    private boolean verbose = false;

    public void setOutputDirectory( File dir )
    {
        this.outputDirectory = dir;
    }

    public File getOutputDirectory()
    {
        return this.outputDirectory;
    }

    public String[] getClassPaths()
    {
        return this.classPaths;
    }

    public void setJavahPath( File javahPath )
    {
        this.javahPath = javahPath;
    }

    public void setClassPaths( String[] paths )
    {
        this.classPaths = paths;
    }

    public void setVerbose( boolean flag )
    {
        this.verbose = flag;
    }

    public File getJavahPath()
    {
        return this.javahPath;
    }

    public boolean getVerbose()
    {
        return this.verbose;
    }

    public void setClassNames( String[] names )
    {
        this.classNames = names;
    }

    public String[] getClassNames()
    {
        return this.classNames;
    }

    public void setFileName( String name )
    {
        this.fileName = name;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public File getWorkingDirectory()
    {
        return this.workingDirectory;
    }

    public void setWorkingDirectory( File dir )
    {
        this.workingDirectory = dir;
    }
}
