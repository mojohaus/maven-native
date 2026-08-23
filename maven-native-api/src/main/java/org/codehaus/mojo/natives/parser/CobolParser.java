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

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

/**
 * A parser that extracts COPY statements from a COBOL Reader.
 */
public final class CobolParser extends AbstractParser implements Parser {
    private final Vector<String> includes = new Vector<>();

    private final AbstractParserState newLineState;

    public CobolParser() {
        AbstractParserState postCopy = new PostCopy(this);
        AbstractParserState y = new CaseInsensitiveLetterState(this, 'Y', postCopy, null);
        AbstractParserState p = new CaseInsensitiveLetterState(this, 'P', y, null);
        AbstractParserState o = new CaseInsensitiveLetterState(this, 'O', p, null);
        newLineState = new WhitespaceOrCaseInsensitiveLetterState(this, 'C', o);
    }

    @Override
    public void addFilename(String include) {
        if (include != null) {
            String trimmed = include.trim();
            if (!trimmed.isEmpty()) {
                includes.addElement(trimmed);
            }
        }
    }

    @Override
    public String[] getIncludes() {
        String[] retval = new String[includes.size()];
        includes.copyInto(retval);
        return retval;
    }

    @Override
    public AbstractParserState getNewLineState() {
        return newLineState;
    }

    @Override
    public void parse(Reader reader) throws IOException {
        includes.setSize(0);
        super.parse(reader);
    }
}
