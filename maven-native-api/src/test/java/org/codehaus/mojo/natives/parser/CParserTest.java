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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the CParser class.
 */
public final class CParserTest extends AbstractParserTest {
    /**
     * Constructor.
     *
     * @param name String test name
     */
    public CParserTest(final String name) {
        super(name);
    }

    /**
     * Checks parsing of #include <foo.h>.
     *
     * @throws IOException test fails on IOException
     */
    @Test
    void immediateImportBracket() throws Exception {
        CharArrayReader reader = new CharArrayReader("#import <foo.h> nowhatever  ".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("foo.h", includes[0]);
    }

    /**
     * Checks parsing of #import "foo.h".
     *
     * @throws IOException test fails on IOException
     */
    @Test
    void immediateImportQuote() throws Exception {
        CharArrayReader reader = new CharArrayReader("#import \"foo.h\"   ".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("foo.h", includes[0]);
    }

    /**
     * Checks parsing of #include <foo.h>.
     *
     * @throws IOException test fails on IOException
     */
    @Test
    void immediateIncludeBracket() throws Exception {
        CharArrayReader reader = new CharArrayReader("#include      <foo.h>   ".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("foo.h", includes[0]);
    }

    /**
     * Checks parsing of #include "foo.h".
     *
     * @throws IOException test fails on IOException.
     */
    @Test
    void immediateIncludeQuote() throws Exception {
        CharArrayReader reader = new CharArrayReader("#include     \"foo.h\"   ".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("foo.h", includes[0]);
    }

    /**
     * Checks parsing of #import <foo.h.
     *
     * @throws IOException test fails on IOException
     */
    @Test
    void incompleteImmediateImportBracket() throws Exception {
        CharArrayReader reader = new CharArrayReader("#import <foo.h   ".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(0, includes.length);
    }

    /**
     * Checks parsing of #import "foo.h.
     *
     * @throws IOException test fails on IOException
     */
    @Test
    void incompleteImmediateImportQuote() throws Exception {
        CharArrayReader reader = new CharArrayReader("#import \"foo.h   ".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(0, includes.length);
    }

    /**
     * Checks parsing of #include <foo.h.
     *
     * @throws IOException test fails on IOException
     */
    @Test
    void incompleteImmediateIncludeBracket() throws Exception {
        CharArrayReader reader = new CharArrayReader("#include <foo.h   ".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(0, includes.length);
    }

    /**
     * Checks parsing of #include "foo.h.
     *
     * @throws IOException test fails on IOException
     */
    @Test
    void incompleteImmediateIncludeQuote() throws Exception {
        CharArrayReader reader = new CharArrayReader("#include     \"foo.h    ".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(0, includes.length);
    }

    /**
     * Checks parsing of #include foo.h.
     *
     * @throws IOException test fails on IOException
     */
    @Test
    void noQuoteOrBracket() throws Exception {
        CharArrayReader reader = new CharArrayReader("#include foo.h  ".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(0, includes.length);
    }

    /**
     * Checks parsing of //#include "foo.h".
     *
     * @throws IOException test fails on IOException
     */
    @Test
    void notFirstWhitespace() throws Exception {
        CharArrayReader reader = new CharArrayReader("//#include \"foo.h\"".toCharArray());
        CParser parser = new CParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(0, includes.length);
    }
}
