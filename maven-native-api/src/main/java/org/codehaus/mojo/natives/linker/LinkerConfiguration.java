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


/*
 * CompilerConfiguration contains inputs by the user + 
 * DependencyMangement for dependecies analysis
 */
public class LinkerConfiguration
{
	private File providerHome;
	
	private File workingDirectory;
	
	/**
	 * Must be in your path
	 */
	private String executable;
	
	/**
	 * Will be passed to compiler executable 
	 */
	private String optionsStart="";

	private String optionsMiddle="";

	private String optionsEnd="";

	private File   outputDirectory = new File ("");
	
	private String outputFileExtension;

	private String objectFileExtension;

	private String outputFileName; /* should not have extension */
	
	private File [] externalLibraries;
	
	public LinkerConfiguration() 
	{
		
	}
		
    public void setProviderHome( File providerHome )
    {
    	this.providerHome = providerHome;
    }

    public File getProviderHome()
    {
    	return this.providerHome;
    }

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

	public String getOptionsStart()
	{
		return this.optionsStart;
	}

	public void setOptionsStart( String options )
	{
		this.optionsStart = options;
	}

	public String getOptionsMiddle()
	{
		return this.optionsMiddle;
	}

	public void setOptionsMiddle( String options )
	{
		this.optionsMiddle = options;
	}

	public String getOptionsEnd()
	{
		return this.optionsEnd;
	}

	public void setOptionsEnd( String options )
	{
		this.optionsEnd = options;
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
	
	public String getObjectFileExtention()
	{
		return this.objectFileExtension;
	}

	public void setObjectFileExtention( String ext )
	{
		this.objectFileExtension = ext;
	}
	
	public String getOutputFilePath() 
	{
		File out = new File( this.outputDirectory + "/" + this.outputFileName  + "." + this.outputFileExtension );
		
		return out.getPath();
	}
	
	public File [] getExternalLibraries() 
	{
		if ( this.externalLibraries == null )
		{
			return new File[0];
		}
		
		return this.externalLibraries;
	}
	
	public void setExternalLibraries( File [] files ) 
	{
		this.externalLibraries = files;
	}
	
	
	
	
}
