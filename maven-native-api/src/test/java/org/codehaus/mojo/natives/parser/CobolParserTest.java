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
package org.codehaus.mojo.natives.parser;

import java.io.CharArrayReader;
import java.io.IOException;

public final class CobolParserTest extends AbstractParserTest {

    public CobolParserTest(final String name) {
        super(name);
    }

    public void testDoubleQuotedCopy() throws IOException {
        CharArrayReader reader = new CharArrayReader("       COPY \"mycopy.cpy\".".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("mycopy.cpy", includes[0]);
    }

    public void testSingleQuotedCopy() throws IOException {
        CharArrayReader reader = new CharArrayReader("       COPY 'mycopy.cpy'.".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("mycopy.cpy", includes[0]);
    }

    public void testUnquotedCopy() throws IOException {
        CharArrayReader reader = new CharArrayReader("       COPY MYCOPY.".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("MYCOPY", includes[0]);
    }

    public void testCaseInsensitiveCopy() throws IOException {
        CharArrayReader reader = new CharArrayReader("   copy \"header.cpy\"\n   CoPy 'footer.cpy'  ".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(2, includes.length);
        assertEquals("header.cpy", includes[0]);
        assertEquals("footer.cpy", includes[1]);
    }

    public void testMultipleCopies() throws IOException {
        String source = "       IDENTIFICATION DIVISION.\n"
                + "       PROGRAM-ID. TESTPROG.\n"
                + "       ENVIRONMENT DIVISION.\n"
                + "       DATA DIVISION.\n"
                + "       WORKING-STORAGE SECTION.\n"
                + "       COPY \"REC1.CPY\".\n"
                + "       COPY REC2.\n"
                + "       PROCEDURE DIVISION.\n"
                + "           DISPLAY \"HELLO\".\n"
                + "           STOP RUN.\n";
        CharArrayReader reader = new CharArrayReader(source.toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(2, includes.length);
        assertEquals("REC1.CPY", includes[0]);
        assertEquals("REC2", includes[1]);
    }
}
