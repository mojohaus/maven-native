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
package org.codehaus.mojo.natives.bcc;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.plexus.util.cli.Commandline;

import junit.framework.TestCase;

public class TLibLinkerTest
    extends TestCase
{

    public void testCommandLine()
    {
        TLibLinker linker = new TLibLinker();

        LinkerConfiguration config = new LinkerConfiguration();

        config.setWorkingDirectory( new File( "." ) );

        String[] options = { "/C" };
        config.setStartOptions( options );
        config.setOutputFileName( "tlib" );
        config.setOutputFileExtension( "lib" );
        config.setOutputDirectory( new File( "target" ) );

        List<File> objectFiles = new ArrayList<>();
        objectFiles.add( new File( "target" + File.separator + "a.obj" ) );
        objectFiles.add( new File( "target" + File.separator + "b.obj" ) );
        objectFiles.add( new File( "target" + File.separator + "c.obj" ) );

        Commandline cl = linker.createLinkerCommandLine( objectFiles, config );

        assertArrayEquals( new String[] { "tlib", "\"target" + File.separator + "tlib.lib\"", "/C",
                "+\"target" + File.separator + "a.obj\"", "+\"target" + File.separator + "b.obj\"",
                "+\"target" + File.separator + "c.obj\"" }, cl.getArguments() );

    }

}
