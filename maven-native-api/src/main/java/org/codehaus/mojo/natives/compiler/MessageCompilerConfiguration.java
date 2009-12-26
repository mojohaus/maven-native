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

import org.codehaus.mojo.natives.ConfigurationBase;

public class MessageCompilerConfiguration
    extends ConfigurationBase
{
    /**
     * Must be in your path
     */
    private String executable = "";

    private File workingDirectory;

    private File outputDirectory;

    private File debugOutputDirectory;

    private String[] options = new String[0];
    
    public MessageCompilerConfiguration()
    {
    }

    public String getExecutable()
    {
        return this.executable;
    }

    public void setExecutable( String executable )
    {
        this.executable = executable;
    }

    public File getWorkingDirectory()
    {
        return this.workingDirectory;
    }

    public void setWorkingDirectory( File dir )
    {
        this.workingDirectory = dir;
    }

    public String[] getOptions()
    {
        return this.options;
    }

    public void setOptions( String[] options )
    {
        this.options = options;

        if ( this.options == null )
        {
            this.options = new String[0];

        }
    }

    public File getOutputDirectory()
    {
        return this.outputDirectory;
    }

    public void setOutputDirectory( File dir )
    {
        this.outputDirectory = dir;
    }

    public File getDebugOutputDirectory()
    {
        return this.debugOutputDirectory;
    }

    public void setDebugOutputDirectory( File dir )
    {
        this.debugOutputDirectory = dir;
    }
    
}
