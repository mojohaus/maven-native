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
package org.codehaus.mojo.natives.linker;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.util.CommandLineUtil;
import org.codehaus.mojo.natives.util.EnvUtil;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.cli.Commandline;

public abstract class AbstractLinker extends AbstractLogEnabled implements Linker {

    protected abstract Commandline createLinkerCommandLine(List<File> objectFiles, LinkerConfiguration config)
            throws NativeBuildException;

    /**
     * Transform linker options for GCC 4.6+ compatibility.
     * Options starting with "--" are linker-specific and must be prefixed with "-Wl,"
     * when using gcc/g++ as the linker driver.
     *
     * @param options the original options array
     * @return transformed options array with "-Wl," prefix added to linker-specific options
     */
    protected String[] transformOptionsForGcc(String[] options) {
        if (options == null) {
            return null;
        }

        String[] transformedOptions = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            if (option != null && option.startsWith("--")) {
                transformedOptions[i] = "-Wl," + option;
            } else {
                transformedOptions[i] = option;
            }
        }
        return transformedOptions;
    }

    @Override
    public File link(LinkerConfiguration config, List<File> compilerOutputFiles)
            throws NativeBuildException, IOException {
        if (!config.getOutputDirectory().exists()) {
            config.getOutputDirectory().mkdirs();
        }

        if (isStaled(config, compilerOutputFiles)) {
            // TODO validate config to make sure required fields are available
            Commandline cl = this.createLinkerCommandLine(compilerOutputFiles, config);
            EnvUtil.setupCommandlineEnv(cl, config.getEnvFactory());
            CommandLineUtil.execute(cl, this.getLogger());
        }

        return config.getOutputFile();
    }

    private boolean isStaled(LinkerConfiguration config, List<File> compilerOutputFiles) {
        if (!config.isCheckStaleLinkage()) {
            // user dont care
            return true;
        }

        File previousDestination = config.getOutputFile();

        if (!previousDestination.exists()) {
            return true;
        }

        if (previousDestination.exists()) {
            for (File compilerOutputFile : compilerOutputFiles) {
                if (previousDestination.lastModified() < compilerOutputFile.lastModified()) {
                    if (this.getLogger().isDebugEnabled()) {
                        getLogger()
                                .debug("Stale relative to compilerOutputFiles: "
                                        + compilerOutputFile.getAbsolutePath());
                    }

                    return true;
                }
            }

            for (int i = 0; i < config.getExternalLibFileNames().size(); ++i) {
                File extLib = new File(
                        config.getExternalLibDirectory(),
                        config.getExternalLibFileNames().get(i));

                if (previousDestination.lastModified() < extLib.lastModified()) {
                    if (this.getLogger().isDebugEnabled()) {
                        getLogger().debug("Stale relative to extLib: " + extLib.getAbsolutePath());
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
