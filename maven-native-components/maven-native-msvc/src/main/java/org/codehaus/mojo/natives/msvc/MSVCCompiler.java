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
package org.codehaus.mojo.natives.msvc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.c.AbstractCCompiler;
import org.codehaus.mojo.natives.compiler.Compiler;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.cli.Commandline;

@Component(role = Compiler.class, hint = "msvc")
public class MSVCCompiler extends AbstractCCompiler {
    private Map<String, String> environmentVariables;

    protected void setEnvironmentVariables(Map<String, String> envs) {
        this.environmentVariables = envs;
    }

    protected Map<String, String> getEnvironmentVariables() {
        if (this.environmentVariables == null) {
            return new HashMap<>();
        }

        return this.environmentVariables;
    }

    @Override
    protected String getOutputFileOption() {
        return "/Fo";
    }

    @Override
    protected Commandline getCommandLine(File src, File dest, CompilerConfiguration config)
            throws NativeBuildException {
        if (config.getExecutable() == null || config.getExecutable().trim().length() == 0) {
            config.setExecutable("cl.exe");
        }

        Commandline cl = super.getCommandLine(src, dest, config);

        return cl;
    }
}
