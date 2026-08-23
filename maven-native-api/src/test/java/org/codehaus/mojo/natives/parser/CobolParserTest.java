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

    /**
     * Fixed format sources may carry a sequence number in columns 1 to 6, which is skipped.
     */
    public void testSequenceAreaIsSkipped() throws IOException {
        CharArrayReader reader = new CharArrayReader("000100     COPY \"mycopy.cpy\".".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("mycopy.cpy", includes[0]);
    }

    /**
     * A comment line is skipped because the indicator character is neither whitespace, a digit, nor "C".
     */
    public void testCommentLineIsIgnored() throws IOException {
        CharArrayReader reader = new CharArrayReader("      * COPY \"mycopy.cpy\".\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        assertEquals(0, parser.getIncludes().length);
    }

    /**
     * A comment line is still a comment when it carries a sequence number.
     */
    public void testSequenceNumberedCommentLineIsIgnored() throws IOException {
        CharArrayReader reader = new CharArrayReader("000100* COPY \"mycopy.cpy\".\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        assertEquals(0, parser.getIncludes().length);
    }

    /**
     * A continuation line is not the start of a directive either.
     */
    public void testContinuationLineIsIgnored() throws IOException {
        CharArrayReader reader = new CharArrayReader("000100- COPY \"mycopy.cpy\".\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        assertEquals(0, parser.getIncludes().length);
    }

    /**
     * Skipping the sequence area must not turn a level number into the start of a directive.
     */
    public void testLevelNumberIsNotACopy() throws IOException {
        CharArrayReader reader = new CharArrayReader("       01 CUSTOMER-RECORD.\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        assertEquals(0, parser.getIncludes().length);
    }

    /**
     * A name that merely starts with COPY is indistinguishable from a COPY directive to this parser, so it yields a
     * spurious include. Harmless (it only costs a staleness lookup) but pinned so it is not rediscovered as a bug.
     */
    public void testCopyPrefixedNameYieldsSpuriousInclude() throws IOException {
        CharArrayReader reader = new CharArrayReader("       COPY-FILES.".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("-FILES", includes[0]);
    }

    /**
     * Angle brackets are C preprocessor syntax and carry no meaning in COBOL, so they are not treated as quoting
     * characters here: they are ordinary characters of an unquoted name, which still ends at the first period.
     */
    public void testAngleBracketsAreNotQuotingCharacters() throws IOException {
        CharArrayReader reader = new CharArrayReader("       COPY <mycopy>.".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("<mycopy>", includes[0]);
    }

    /**
     * An unquoted copybook name is a COBOL word and cannot contain a period, so the name ends at the first one.
     */
    public void testUnquotedNameEndsAtFirstPeriod() throws IOException {
        CharArrayReader reader = new CharArrayReader("       COPY MYCOPY.CPY.".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("MYCOPY", includes[0]);
    }

    public void testCarriageReturnLineEndings() throws IOException {
        CharArrayReader reader =
                new CharArrayReader("       COPY \"first.cpy\".\r\n       COPY SECOND.\r\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(2, includes.length);
        assertEquals("first.cpy", includes[0]);
        assertEquals("SECOND", includes[1]);
    }

    public void testParseResetsPreviousIncludes() throws IOException {
        CobolParser parser = new CobolParser();
        parser.parse(new CharArrayReader("       COPY \"first.cpy\".".toCharArray()));
        parser.parse(new CharArrayReader("       COPY \"second.cpy\".".toCharArray()));
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("second.cpy", includes[0]);
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
