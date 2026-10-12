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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class CobolParserTest {

    @Test
    void doubleQuotedCopy() throws Exception {
        CharArrayReader reader = new CharArrayReader("       COPY \"mycopy.cpy\".".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("mycopy.cpy", includes[0]);
    }

    @Test
    void singleQuotedCopy() throws Exception {
        CharArrayReader reader = new CharArrayReader("       COPY 'mycopy.cpy'.".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("mycopy.cpy", includes[0]);
    }

    @Test
    void unquotedCopy() throws Exception {
        CharArrayReader reader = new CharArrayReader("       COPY MYCOPY.".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("MYCOPY", includes[0]);
    }

    @Test
    void caseInsensitiveCopy() throws Exception {
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
    @Test
    void sequenceAreaIsSkipped() throws Exception {
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
    @Test
    void commentLineIsIgnored() throws Exception {
        CharArrayReader reader = new CharArrayReader("      * COPY \"mycopy.cpy\".\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        assertEquals(0, parser.getIncludes().length);
    }

    /**
     * A comment line is still a comment when it carries a sequence number.
     */
    @Test
    void sequenceNumberedCommentLineIsIgnored() throws Exception {
        CharArrayReader reader = new CharArrayReader("000100* COPY \"mycopy.cpy\".\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        assertEquals(0, parser.getIncludes().length);
    }

    /**
     * A continuation line is not the start of a directive either.
     */
    @Test
    void continuationLineIsIgnored() throws Exception {
        CharArrayReader reader = new CharArrayReader("000100- COPY \"mycopy.cpy\".\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        assertEquals(0, parser.getIncludes().length);
    }

    /**
     * Skipping the sequence area must not turn a level number into the start of a directive.
     */
    @Test
    void levelNumberIsNotACopy() throws Exception {
        CharArrayReader reader = new CharArrayReader("       01 CUSTOMER-RECORD.\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        assertEquals(0, parser.getIncludes().length);
    }

    /**
     * A name that merely starts with COPY is indistinguishable from a COPY directive to this parser, so it yields a
     * spurious include. Harmless (it only costs a staleness lookup) but pinned so it is not rediscovered as a bug.
     */
    @Test
    void copyPrefixedNameYieldsSpuriousInclude() throws Exception {
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
    @Test
    void angleBracketsAreNotQuotingCharacters() throws Exception {
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
    @Test
    void unquotedNameEndsAtFirstPeriod() throws Exception {
        CharArrayReader reader = new CharArrayReader("       COPY MYCOPY.CPY.".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("MYCOPY", includes[0]);
    }

    @Test
    void carriageReturnLineEndings() throws Exception {
        CharArrayReader reader =
                new CharArrayReader("       COPY \"first.cpy\".\r\n       COPY SECOND.\r\n".toCharArray());
        CobolParser parser = new CobolParser();
        parser.parse(reader);
        String[] includes = parser.getIncludes();
        assertEquals(2, includes.length);
        assertEquals("first.cpy", includes[0]);
        assertEquals("SECOND", includes[1]);
    }

    @Test
    void parseResetsPreviousIncludes() throws Exception {
        CobolParser parser = new CobolParser();
        parser.parse(new CharArrayReader("       COPY \"first.cpy\".".toCharArray()));
        parser.parse(new CharArrayReader("       COPY \"second.cpy\".".toCharArray()));
        String[] includes = parser.getIncludes();
        assertEquals(1, includes.length);
        assertEquals("second.cpy", includes[0]);
    }

    @Test
    void multipleCopies() throws Exception {
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
