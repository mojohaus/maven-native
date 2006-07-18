package org.codehaus.mojo.natives;

import java.io.File;
import java.io.IOException;

import org.codehaus.mojo.natives.parser.Parser;

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

/**
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Id$
 */

public class SourceDependencyAnalyzer 
{
	public static boolean isStaled ( File source, File dest, Parser parser, File [] includePaths )
    	throws NativeBuildException
	{
	    if ( ! source.exists() )
	    {
	    	throw new NativeBuildException ( source.getPath() + " not found." );
	    }
		
		//quick compare  with the source where the user likely to change first
		if ( ( !dest.exists() ) || 
			 ( dest.lastModified() < source.lastModified() ) )
		{
			return true;
		}

		//analyze the depenencies of the source file to detect any new changes
		Dependency dependency = new Dependency( null, source, parser, includePaths );

		try 
		{
			dependency.analyze();
		}
		catch ( IOException ioe )
		{
			throw new NativeBuildException( "Error analysing " + source.getPath() + ". Reason: " + ioe.getMessage() );
		}
		
		return dest.lastModified() < dependency.getCompositeLastModified();
		
	}
	
}
