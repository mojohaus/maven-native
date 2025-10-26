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

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

/**
 * A parser that extracts #include statements from a Reader.
 *
 * @author Adam Murdoch
 * @author Curt Arnold
 */
public final class CobolParser extends AbstractParser implements Parser {
    private final Vector<String> includes = new Vector<>();

    private AbstractParserState newLineState;

    public CobolParser() {
        AbstractParserState quote = new FilenameState(this, new char[] {'"'});
        AbstractParserState bracket = new FilenameState(this, new char[] {'>'});
        AbstractParserState postE = new PostE(this, bracket, quote);
        //
        // opy
        //
        AbstractParserState y = new LetterState(this, 'y', postE, null);
        AbstractParserState p = new LetterState(this, 'p', y, null);
        AbstractParserState o = new LetterState(this, 'o', p, null);
        newLineState = new LetterState(this, 'o', o, null);
    }

    @Override
    public void addFilename(String include) {
        includes.addElement(include);
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
