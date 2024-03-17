/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.NativeSources;
import org.codehaus.mojo.natives.compiler.ResourceCompiler;
import org.codehaus.mojo.natives.compiler.ResourceCompilerConfiguration;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.mojo.natives.manager.ResourceCompilerManager;
import org.codehaus.plexus.util.FileUtils;

/**
 * Compile Windows resource files
 */
@Mojo(name = "resource-compile", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class NativeResourceCompileMojo extends AbstractNativeMojo {

    /**
     * Compiler Provider Type
     *
     * @since 1.0-alpha-2
     */
    @Parameter(defaultValue = "msvc", required = true)
    private String provider;

    /**
     * Use this field to override provider specific resource compiler executable
     *
     * @since 1.0-alpha-2
     */
    @Parameter
    private String resourceCompilerExecutable;

    /**
     * Resource compiler options
     *
     * @since 1.0-alpha-2
     */
    @Parameter
    private List<String> resourceCompilerOptions;

    /**
     * Array of NativeSources containing include directories and source files
     *
     * @since 1.0-alpha-8
     */
    @Parameter
    private NativeSources[] resources;

    /**
     * @since 1.0-alpha-2
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    protected File resourceCompilerOutputDirectory;

    /**
     * Internal
     *
     * @since 1.0-alpha-2
     */
    @Component
    private ResourceCompilerManager manager;

    @Override
    public void execute() throws MojoExecutionException {

        if (!this.resourceCompilerOutputDirectory.exists()) {
            this.resourceCompilerOutputDirectory.mkdirs();
        }

        FileUtils.mkdir(project.getBuild().getDirectory());

        ResourceCompiler compiler = this.getResourceCompiler();

        ResourceCompilerConfiguration config = new ResourceCompilerConfiguration();
        config.setExecutable(this.resourceCompilerExecutable);
        config.setWorkingDirectory(this.workingDirectory);
        config.setOptions(NativeMojoUtils.trimParams(this.resourceCompilerOptions));
        config.setOutputDirectory(this.resourceCompilerOutputDirectory);
        config.setEnvFactory(this.getEnvFactory());

        try {
            List<File> resourceOutputFiles;
            resourceOutputFiles = compiler.compile(config, this.resources);

            this.saveCompilerOutputFilePaths(resourceOutputFiles);
        } catch (NativeBuildException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private ResourceCompiler getResourceCompiler() throws MojoExecutionException {
        ResourceCompiler rc;

        try {
            rc = this.manager.getResourceCompiler(this.provider);

        } catch (NoSuchNativeProviderException pe) {
            throw new MojoExecutionException(pe.getMessage());
        }

        return rc;
    }
}
