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
package org.codehaus.mojo.natives.gnucobol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.AbstractLinker;
import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;
import org.codehaus.mojo.natives.util.FileUtil;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * GNU COBOL linker with "-o " as its output option.
 * <p>
 * No build-mode flag is passed, so cobc applies its own default of "-m", which produces a dynamically loadable
 * module. To produce an executable instead, pass "-x" through the linker start options.
 */
@Component(
        role = Linker.class,
        hint = "gnucobol",
        instantiationStrategy = "per-lookup",
        description = "GNU Cobol linker")
public class GNUCOBOLLinker extends AbstractLinker {

    @Override
    protected Commandline createLinkerCommandLine(List<File> objectFiles, LinkerConfiguration config)
            throws NativeBuildException {
        if (config.getExecutable() == null) {
            config.setExecutable("cobc");
        }

        Commandline cl = new Commandline();

        cl.setWorkingDirectory(config.getWorkingDirectory().getPath());

        cl.setExecutable(config.getExecutable());

        if (config.getStartOptions() != null) {
            cl.addArguments(config.getStartOptions());
        }

        String linkerOutputOption = this.getLinkerOutputOption();
        if (linkerOutputOption.endsWith(" ")) {
            cl.createArg().setValue(linkerOutputOption.substring(0, linkerOutputOption.length() - 1));
            cl.createArg().setFile(config.getOutputFile());
        } else {
            cl.createArg().setValue(linkerOutputOption + config.getOutputFile());
        }

        // On Windows to avoid command lines being too long, support response files
        if (config.isUsingLinkerResponseFile()) {
            try {
                File linkerFile = new File(config.getWorkingDirectory(), "objectsFile");
                try (FileWriter linkerFileWriter = new FileWriter(linkerFile, false)) {
                    for (File objFile : objectFiles) {
                        linkerFileWriter.write(objFile.getPath() + "\n");
                    }
                }
            } catch (IOException error) {
                throw new NativeBuildException("Error creating linker response file", error);
            }

            cl.createArg().setValue("@objectsFile");
        } else {
            for (File objFile : objectFiles) {
                String objFilePath = FileUtil.truncatePath(
                        objFile.getPath(), config.getWorkingDirectory().getPath());
                cl.createArg().setValue(objFilePath);
            }
        }

        if (config.getMiddleOptions() != null) {
            cl.addArguments(config.getMiddleOptions());
        }

        setCommandLineForExternalLibraries(cl, config);

        if (config.getEndOptions() != null) {
            cl.addArguments(config.getEndOptions());
        }

        return cl;
    }

    protected String getLinkerOutputOption() {
        return "-o ";
    }

    protected void setCommandLineForExternalLibraries(Commandline cl, LinkerConfiguration config)
            throws NativeBuildException {
        if (config.getExternalLibFileNames().isEmpty()) {
            return;
        }

        boolean hasUnixLinkage = false;

        for (String libFileName : config.getExternalLibFileNames()) {
            String ext = FileUtils.getExtension(libFileName);

            if ("o".equals(ext) || "obj".equals(ext) || "lib".equals(ext) || "dylib".equals(ext)) {
                File libFile = new File(config.getExternalLibDirectory(), libFileName);
                String relativeLibFile = FileUtil.truncatePath(
                        libFile.getPath(), config.getWorkingDirectory().getPath());
                cl.createArg().setValue(relativeLibFile);
            } else if ("a".equals(ext) || "so".equals(ext) || "sl".equals(ext)) {
                hasUnixLinkage = true;
            }
        }

        if (hasUnixLinkage) {
            cl.createArg().setValue("-L" + config.getExternalLibDirectory());
        }

        for (String libFileName : config.getExternalLibFileNames()) {
            String ext = FileUtils.getExtension(libFileName);

            if ("a".equals(ext) || "so".equals(ext) || "sl".equals(ext)) {
                String libName = FileUtils.removeExtension(libFileName);

                if (libFileName.startsWith("lib")) {
                    libName = libName.substring("lib".length());
                }

                cl.createArg().setValue("-l" + libName);
            }
        }
    }
}
