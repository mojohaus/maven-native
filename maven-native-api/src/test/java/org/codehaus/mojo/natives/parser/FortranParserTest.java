/*
 *
 * Copyright 2002-2004 The Ant-Contrib project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.codehaus.mojo.natives.parser;

import java.io.CharArrayReader;
import java.io.IOException;

public final class FortranParserTest extends AbstractParserTest {
    /**
     * Constructor.
     *
     * @param name String test name
     */
    public FortranParserTest(final String name) {
        super(name);
    }

    /**
     * Checks parsing of INCLUDE 'foo.inc'.
     *
     * @throws IOException test fails on IOException
     */
    public void testINCLUDE() throws IOException {
        CharArrayReader reader = new CharArrayReader("INCLUDE 'foo.inc' nowhatever  ".toCharArray());
        FortranParser parser = new FortranParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(includes.length, 1);
        assertEquals("foo.inc", includes[0]);
    }

    /**
     * Checks parsing of InClUdE 'foo.inc'.
     *
     * @throws IOException test fails on IOException
     */
    public void testInClUdE() throws IOException {
        CharArrayReader reader = new CharArrayReader("InClUdE 'foo.inc'  ".toCharArray());
        FortranParser parser = new FortranParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(includes.length, 1);
        assertEquals("foo.inc", includes[0]);
    }

    /**
     * Checks parsing of InClUdE 'foo.inc'.
     *
     * @throws IOException test fails on IOException
     */
    public void testMultipleInClUdE() throws IOException {
        CharArrayReader reader = new CharArrayReader("InClUdE 'foo.inc'\ninclude 'bar.inc'  ".toCharArray());
        FortranParser parser = new FortranParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(includes.length, 2);
        assertEquals("foo.inc", includes[0]);
        assertEquals("bar.inc", includes[1]);
    }
}
