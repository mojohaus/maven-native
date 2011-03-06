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
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.natives.ConfigurationBase;


/*
 * CompilerConfiguration contains inputs by the user + 
 * DependencyMangement for dependecies analysis
 */
public class LinkerConfiguration
    extends ConfigurationBase
{
	
	private File workingDirectory;
	
	/**
	 * Must be in your path
	 */
	private String executable;
	
	/**
	 * Will be passed to linker executable 
	 */
	private String [] startOptions;

	private String [] middleOptions;

	private String [] endOptions;

	private File   outputDirectory;
	
	private String outputFileExtension;

	private String outputFileName; /* should not have extension */
	
    /**
     * Single location that the client must place library files to be linked with
     */
    private File externalLibDirectory;

    /**
     * Library file names in externalLibDirectory
     */
    private List externalLibFileNames;
    
    /**
     * For project with lots of object files on windows, turn this flag to resolve Windows command line length limit
     */
    private boolean usingLinkerResponseFile;
		
	public File getOutputDirectory()
	{
		return this.outputDirectory;
	}
	
	public void setOutputDirectory( File dir ) 
	{
		this.outputDirectory = dir;
	}
	
	public String getOutputFileExtension() 
	{
		return this.outputFileExtension;
	}
	
	public void setOutputFileExtension ( String ext ) 
	{
		this.outputFileExtension = ext;
	}
	
	public File getWorkingDirectory()
	{
		return this.workingDirectory;
	}
	
	public void setWorkingDirectory( File dir ) 
	{
		this.workingDirectory = dir;
	}

	public String [] getStartOptions()
	{
		return this.startOptions;
	}

	public void setStartOptions( String [] options )
	{
		this.startOptions = options;
	}

	public String [] getMiddleOptions()
	{
		return this.middleOptions;
	}

	public void setMiddleOptions( String [] options )
	{
		this.middleOptions = options;
	}

	public String [] getEndOptions()
	{
		return this.endOptions;
	}

	public void setEndOptions( String [] options )
	{
		this.endOptions = options;
	}
	
	public String getExecutable()
	{
		return this.executable;
	}
	
	public void setExecutable( String executable )
	{
		this.executable = executable;
	}
	
	public String getOutputFileName()
	{
		return this.outputFileName;
	}
	
	public void setOutputFileName ( String name ) 
	{
		this.outputFileName = name; 
	}
	
    /**
     * convenient method to get linker output file
     * @return
     */
	public File getOutputFile() 
	{
		File out = new File( this.outputDirectory , this.outputFileName  + "." + this.outputFileExtension );
		
		return out;
	}
	
    public List getExternalLibFileNames() 
    {
        if ( this.externalLibFileNames == null )
        {
            return new ArrayList(0);
        }
        
        return this.externalLibFileNames;
    }
    
    public void setExternalLibFileNames ( List list )
    {
        this.externalLibFileNames = list;
    }
    
    public void setExternalLibDirectory ( File dir )
    {
        this.externalLibDirectory = dir;
    }
	
    public File getExternalLibDirectory()
    {
        return this.externalLibDirectory;
    }
    
    public boolean isUsingLinkerResponseFile()
    {
        return usingLinkerResponseFile;
    }

    public void setUsingLinkerResponseFile( boolean useObjectsFile )
    {
        this.usingLinkerResponseFile = useObjectsFile;
    }
    
}
