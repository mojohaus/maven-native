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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.mojo.natives.EnvFactory;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.util.EnvUtil;

/**
 * BCC Environment Setup
 */

public class BCCEnvFactory
    implements EnvFactory
{
    private static final String BCC_INSTALL_ENV_KEY = "BCC_INSTALL_DIR";

    private static final String DEFAULT_BCC_INSTALL_DIR = "C:/Borland/BCC";

    private static Map<String, String> envs;

    @Override
    public synchronized Map<String, String> getEnvironmentVariables()
        throws NativeBuildException
    {
        if ( envs == null )
        {
            envs = createEnvs();
        }

        return envs;
    }

    private Map<String, String> createEnvs()
        throws NativeBuildException
    {
        File bccDir = new File( EnvUtil.getEnv( BCC_INSTALL_ENV_KEY, BCC_INSTALL_ENV_KEY, DEFAULT_BCC_INSTALL_DIR ) );

        if ( !bccDir.isDirectory() )
        {
            throw new NativeBuildException( bccDir.getPath() + " is not a directory." );
        }

        Map<String, String> envs = new HashMap<>();

        // setup new PATH
        String currentPath = System.getProperty( "java.library.path" );

        String newPath = bccDir.getPath() + "\\BIN;" + currentPath;

        envs.put( "PATH", newPath );

        return envs;

    }

}
