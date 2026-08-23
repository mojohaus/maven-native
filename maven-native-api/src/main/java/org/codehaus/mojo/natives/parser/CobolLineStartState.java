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

/**
 * The state at the start of a COBOL line, which matches the first letter of a COPY directive.
 * <p>
 * Leading spaces, tabs and digits are skipped so that fixed format sources carrying a sequence number in columns 1 to
 * 6 are handled alongside sources that omit it. Any other character ends the line as far as this parser is concerned,
 * which is what keeps comment lines out: the indicator in column 7 of a commented line is "*" or "/", neither of
 * which is skipped.
 */
public final class CobolLineStartState extends AbstractParserState {
    /**
     * Next state once the letter is matched.
     */
    private final AbstractParserState nextState;

    /**
     * Character to match (lower case).
     */
    private final char lowerLetter;

    /**
     * Character to match (upper case).
     */
    private final char upperLetter;

    /**
     * Constructor.
     *
     * @param parser parser
     * @param matchLetter letter to match
     * @param nextStateArg next state if a match on the letter
     */
    public CobolLineStartState(
            final AbstractParser parser, final char matchLetter, final AbstractParserState nextStateArg) {
        super(parser);
        this.lowerLetter = Character.toLowerCase(matchLetter);
        this.upperLetter = Character.toUpperCase(matchLetter);
        this.nextState = nextStateArg;
    }

    /**
     * Consumes a character and returns the next state for the parser.
     *
     * @param ch next character
     * @return the configured nextState on a match, this state while skipping the sequence area, the new line state on
     *         an end of line, or null to abandon the rest of the line
     */
    @Override
    public AbstractParserState consume(final char ch) {
        if (ch == lowerLetter || ch == upperLetter) {
            return nextState;
        }
        if (ch == ' ' || ch == '\t' || Character.isDigit(ch)) {
            return this;
        }
        if (ch == '\n') {
            return getParser().getNewLineState();
        }
        return null;
    }
}
