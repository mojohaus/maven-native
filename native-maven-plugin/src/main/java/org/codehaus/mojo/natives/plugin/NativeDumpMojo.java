package org.codehaus.mojo.natives.plugin;

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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

@Mojo(name = "dump", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public final class NativeDumpMojo extends AbstractNativeMojo {

    private final class DumpDescriptor implements Serializable {

        private final File file;

        public DumpDescriptor(File file) {
            this.file = file;
        }

        public String getFileName() {
            return NativeDumpMojo.this.ignoreFileName ? null : file.getName();
        }

        public String getIdentity() {
            String result = getFileName();
            if (result != null) {
                result = result.toUpperCase().replace(".", "_");
            }
            return result;
        }

        public byte[] getContent() throws IOException {
            final InputStream is = new FileInputStream(file);
            try {
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                IOUtil.copy(is, os);
                return os.toByteArray();
            }
            finally {
                is.close();
            }
        }
    }

    /**
     * Files to be dumped in C
     *
     * @since 1.0-alpha-9
     */
    @Parameter(required = true)
    private File[] dumps;

    /**
     * Ignore filename(s) for dumped content
     *
     * @since 1.0-alpha-9
     */
    @Parameter(defaultValue = "true")
    private boolean ignoreFileName;

    /**
     * Where to place the generated source (module and header in the same
     * folder)
     *
     * @since 1.0-alpha-9
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/c", required = true)
    private File dumpOutputDirectory;

    /**
     * The module/header root name
     *
     * @since 1.0-alpha-9
     */
    @Parameter(defaultValue = "dump")
    private String fileName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(!dumpOutputDirectory.exists()) {
            dumpOutputDirectory.mkdirs();
        }

        try {
            FileUtils.cleanDirectory(dumpOutputDirectory);
            final List<DumpDescriptor> descriptors = new ArrayList<>(dumps.length);
            for (File file : dumps) {
                if (!file.exists()) {
                    throw new FileNotFoundException(file.getAbsolutePath());
                }
                descriptors.add(new DumpDescriptor(file));
            }

            {
                final STGroupFile group = new STGroupFile(new File(getClass().getResource("dump/header.stg").toURI()).getAbsolutePath());
                final ST content = group.getInstanceOf("header");
                content.add("FILE", fileName.toUpperCase());
                content.add("files", descriptors);

                final PrintWriter writer = new PrintWriter(new File(dumpOutputDirectory, fileName + ".h"));
                writer.print(content.render());
                writer.close();
                group.unload();
            }
            {
                final STGroupFile group = new STGroupFile(new File(getClass().getResource("dump/module.stg").toURI()).getAbsolutePath());
                group.registerRenderer(Byte.class, new AttributeRenderer() {
                    @Override
                    public String toString(Object o, String arg1, Locale arg2) {
                        return String.format("0x%02X", o);
                    }
                });
                final ST content = group.getInstanceOf("module");
                content.add("FILE", fileName);
                content.add("files", descriptors);

                final PrintWriter writer = new PrintWriter(new File(dumpOutputDirectory, fileName + ".c"));
                writer.print(content.render(128));
                writer.close();
                group.unload();
            }
        }
        catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

}
