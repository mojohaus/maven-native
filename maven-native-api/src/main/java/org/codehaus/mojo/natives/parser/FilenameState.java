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

public class FilenameState
    extends AbstractParserState
{
    private final StringBuffer buf = new StringBuffer();

    private final char[] terminators;

    public FilenameState( AbstractParser parser, char[] terminators )
    {
        super( parser );
        this.terminators = terminators.clone();
    }

    public AbstractParserState consume( char ch )
    {
        for ( char terminator : terminators )
        {
            if ( ch == terminator )
            {
                getParser().addFilename( buf.toString() );
                buf.setLength( 0 );
                return null;
            }
        }
        if ( ch == '\n' )
        {
            buf.setLength( 0 );
            return getParser().getNewLineState();
        }
        else
        {
            buf.append( ch );
        }
        return this;
    }
}
