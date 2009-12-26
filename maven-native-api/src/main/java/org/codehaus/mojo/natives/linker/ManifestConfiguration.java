package org.codehaus.mojo.natives.linker;

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


/*
 * ManifestConfiguration contains inputs by the user
 */
public class ManifestConfiguration
    extends ConfigurationBase
{

    private File workingDirectory;
    
    private File inputFile;
    
    private File manifestFile;

    public File getInputFile()
    {
        return inputFile;
    }

    public void setInputFile( File inputFile )
    {
        this.inputFile = inputFile;
    }

    public File getManifestFile()
    {
        return manifestFile;
    }

    public void setManifestFile( File manifestFile )
    {
        this.manifestFile = manifestFile;
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
