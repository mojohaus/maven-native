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
package org.codehaus.mojo.natives.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.mojo.natives.compiler.Compiler;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

@Component(role = CompilerManager.class, hint = "native-compiler-provider-manager")
public class DefaultCompilerManager
    extends AbstractLogEnabled
    implements CompilerManager, Initializable
{
    @Requirement(role = Compiler.class)
    private Map<String, Compiler> providers;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    @Override
    public void initialize()
    {
        if ( providers == null )
        {
            providers = new HashMap<>();
        }

        if ( providers.size() == 0 )
        {
            getLogger().warn( "No compiler providers configured." );
        }
    }

    @Override
    public Compiler getCompiler( String providerType )
        throws NoSuchNativeProviderException
    {
        Compiler provider = providers.get( providerType );

        if ( provider == null )
        {
            throw new NoSuchNativeProviderException( providerType );
        }

        return provider;
    }
}
