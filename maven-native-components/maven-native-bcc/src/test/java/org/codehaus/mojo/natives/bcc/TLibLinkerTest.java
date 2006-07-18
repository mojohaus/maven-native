package org.codehaus.mojo.natives.bcc;

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

import junit.framework.TestCase;

import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class TLibLinkerTest 
    extends TestCase 
{
	private static String FS = File.separator;

	public void testCommandLine()
        throws Exception
	{
		TLibLinker linker = new TLibLinker();
		
		LinkerConfiguration config = new LinkerConfiguration();
		
		config.setWorkingDirectory( new File( "." ) );
		
		String [] options = {"/C"};
		config.setStartOptions ( options );
		config.setOutputFileName("tlib");
		config.setOutputFileExtension("lib");
		config.setOutputDirectory( new File ( "target" ) );
		
		List objectFiles = new ArrayList();
		objectFiles.add( new File ( "target/a.obj" ) );
		objectFiles.add( new File ( "target/b.obj" ) );
		objectFiles.add( new File ( "target/c.obj") );
		
		Commandline cl = linker.createLinkerCommandLine(objectFiles, config);
		
		String expectedCl = "tlib target" + FS + "tlib.lib" + " /C " + 
		                    "+target" + FS + "a.obj " +
		                    "+target" + FS + "b.obj " + 
		                    "+target" + FS + "c.obj" ; 
		
        //TODO do to a bug in commandline.tostring, it is no longer possible to contruct the right string
        //assertEquals( expectedCl, cl.toString() );
		
	}

	
}
