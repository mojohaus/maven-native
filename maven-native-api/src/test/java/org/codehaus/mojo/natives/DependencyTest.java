package org.codehaus.mojo.natives;

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
import java.io.IOException;

import org.codehaus.mojo.natives.Dependency;
import org.codehaus.mojo.natives.parser.CParser;
import org.codehaus.mojo.natives.parser.Parser;


public class DependencyTest 
    extends AbstractDependencyTest 
{
	
	public DependencyTest( String name )
	{
		super( name );
	}

	/**
	 * Source has includes, but include path is not given 
	 * 
	 **/
	public void testNoneParticipateDepedencyAnalysisInclude()
	  throws IOException, InterruptedException
	{
		String testSrcDir = "target/test/testNoneParticipateDepedencyAnalysisInclude/c/";
		String testSource = testSrcDir + "test1.c";
		
		this.rmDir(testSrcDir);
		this.mkDir(testSrcDir);
		
		this.writeFile( testSource, "#include \"test1.h\"\n \"test2.h\"\n" );
		
        File [] includePaths = new File[0];

	    File srcFile = new File( testSource );

        Parser parser = new CParser();

	    Dependency dependency = new Dependency( srcFile, parser, includePaths );
	    
	    dependency.analyze();
	    
	    assertEquals ( 0, dependency.getDependencies().size() );

	}

	public void testCyclicOnTheSameSource()
	  throws IOException, InterruptedException
	{
		String testIncDir = "target/test/testCyclicOnTheSameSource/h/";
		String testSource = testIncDir + "test1.h";
		
		this.rmDir(testIncDir);
		this.mkDir(testIncDir);
		
		this.writeFile( testSource, "#include \"test1.h\"" );
		
        File [] includePaths = new File[0];

	    File srcFile = new File( testSource );
        
        Parser parser = new CParser();

	    Dependency dependency = new Dependency( srcFile, parser, includePaths );
	    
	    dependency.analyze();
	    
	    assertEquals ( 0, dependency.getDependencies().size() );

 	}
	
	/*
	 * Makesure depedencies tree does not contain duplicate node
	 */
	public void testCyclicDependencyAnalysis()
	  throws IOException, InterruptedException
	{
		String testSrcDir = "target/test/testCyclicDependencyAnalysis/c/";
		String testIncDir = "target/test/testCyclicDependencyAnalysis/h/";
		
		this.rmDir( testSrcDir );
		this.rmDir( testIncDir );
		
		this.mkDir(testSrcDir);
		this.mkDir(testIncDir);
		
		this.writeFile( testSrcDir + "test1.h",
                         "#include \"test2.h\"" );

		Thread.sleep(100);

		//force a cyclic condiction
		this.writeFile( testIncDir + "test2.h",
                         "#include \"test1.h\"" );

		this.writeFile( testIncDir + "test3.h",
                         "#include \"test1.h\"" );
		
		Thread.sleep(100);

		this.writeFile( testSrcDir + "test1.c",
                                     "#include \"test1.h\"\n#include \"test3.h\"" );

	    File srcFile = new File( testSrcDir + "test1.c" );
	    
	    File [] includePaths = new File[2];
	    
	    includePaths[0] = new File( testSrcDir ); 
	    includePaths[1] = new File( testIncDir );
	    
	    Parser parser = new CParser();
	    
	    Dependency dependency = new Dependency( srcFile, parser, includePaths  );
	    
	    dependency.analyze();
	    
	    assertEquals ( 2, dependency.getDependencies().size() );

	    assertEquals ( 3, dependency.getDeepDependencyCount() );

	    assertTrue ( srcFile.lastModified() == dependency.getCompositeLastModified() );

	}
	
}
