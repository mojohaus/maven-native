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
package org.codehaus.mojo.natives.javah;

import java.io.File;
import java.io.IOException;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.util.CommandLineUtil;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Sun's javah compatible implementation
 */
public class JavahExecutable extends AbstractJavah {
    public JavahExecutable() {}

    public void compile(JavahConfiguration config) throws NativeBuildException {
        Commandline cl = this.createJavahCommand(config);

        CommandLineUtil.execute(cl, this.getLogger());
    }

    protected Commandline createJavahCommand(JavahConfiguration config) throws NativeBuildException {
        this.validateConfiguration(config);

        Commandline cl = new Commandline();

        if (config.getWorkingDirectory() != null) {
            cl.setWorkingDirectory(config.getWorkingDirectory().getPath());
        }

        String executable = this.getJavaHExecutable(config);
        cl.setExecutable(executable);

        boolean usingJavac = executable.endsWith("javac") || executable.equals("javac");

        if (usingJavac) {
            // For javac, use -h for header generation
            cl.createArg().setValue("-h");
            cl.createArg().setFile(config.getOutputDirectory());

            // Note: javac -o is not the same as javah -o
            // javac generates one header per class, not a combined file
            if (config.getFileName() != null && config.getFileName().length() > 0) {
                getLogger()
                        .warn("The -o option (fileName) is not supported when using javac for header generation. "
                                + "Each class will generate its own header file.");
            }
        } else {
            // Original javah behavior
            if (config.getFileName() != null && config.getFileName().length() > 0) {
                File outputFile = new File(config.getOutputDirectory(), config.getFileName());
                cl.createArg().setValue("-o");
                cl.createArg().setFile(outputFile);
            } else {
                if (config.getOutputDirectory() != null) {
                    cl.createArg().setValue("-d");
                    cl.createArg().setFile(config.getOutputDirectory());
                }
            }
        }

        String[] classPaths = config.getClassPaths();

        StringBuilder classPathBuffer = new StringBuilder();

        for (int i = 0; i < classPaths.length; ++i) {
            classPathBuffer.append(classPaths[i]);
            if (i != classPaths.length - 1) {
                classPathBuffer.append(File.pathSeparatorChar);
            }
        }

        if (config.getUseEnvClasspath()) {
            cl.addEnvironment("CLASSPATH", classPathBuffer.toString());
        } else {
            cl.createArg().setValue("-classpath");

            cl.createArg().setValue(classPathBuffer.toString());
        }

        if (config.getVerbose()) {
            cl.createArg().setValue("-verbose");
        }

        if (usingJavac) {
            // javac requires source files, not just class names
            // We need to find the source files corresponding to the class names
            String[] sourceFiles = findSourceFiles(config);
            if (sourceFiles.length == 0) {
                throw new NativeBuildException("Cannot find source files for classes: "
                        + String.join(", ", config.getClassNames())
                        + ". When using Java 10+, either ensure javah is installed separately, "
                        + "or source files must be available for javac -h to generate headers.");
            }
            cl.addArguments(sourceFiles);
        } else {
            cl.addArguments(config.getClassNames());
        }

        return cl;
    }

    /**
     * Find source files corresponding to class names
     */
    private String[] findSourceFiles(JavahConfiguration config) {
        String[] classNames = config.getClassNames();
        String[] sourceRoots = config.getSourceRoots();

        if (sourceRoots == null || sourceRoots.length == 0) {
            return new String[0];
        }

        java.util.List<String> sourceFiles = new java.util.ArrayList<>();

        for (String className : classNames) {
            // Convert class name to file path: com.example.MyClass -> com/example/MyClass.java
            String relativePath = className.replace('.', File.separatorChar) + ".java";

            // Search in all source roots
            for (String sourceRoot : sourceRoots) {
                File sourceFile = new File(sourceRoot, relativePath);
                if (sourceFile.exists() && sourceFile.isFile()) {
                    sourceFiles.add(sourceFile.getAbsolutePath());
                    break;
                }
            }
        }

        return sourceFiles.toArray(new String[0]);
    }

    private void validateConfiguration(JavahConfiguration config) throws NativeBuildException {
        if (config.getClassPaths() == null || config.getClassPaths().length == 0) {
            throw new NativeBuildException("javah classpaths can not be empty.");
        }

        if (config.getOutputDirectory() == null) {
            throw new NativeBuildException("javah destDir can not be empty.");
        }

        if (!config.getOutputDirectory().exists()) {
            config.getOutputDirectory().mkdirs();
        }

        if (config.getClassNames() == null || config.getClassNames().length == 0) {
            throw new NativeBuildException("javah: java classes can not be empty.");
        }
    }

    /**
     * @return
     */
    protected String getJavaHExecutable(JavahConfiguration config) {
        String path = "javah";

        if (config.getJavahPath() != null) {
            path = config.getJavahPath().getAbsolutePath();
        } else if (isJava10OrLater() && !isJavahAvailable()) {
            // Java 10+ removed javah, use javac instead
            path = "javac";
        }

        return path;
    }

    /**
     * Check if running on Java 10 or later
     */
    private boolean isJava10OrLater() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            // Java 8 or earlier (1.8, 1.7, etc.)
            return false;
        }
        // Java 9+ uses version numbers like "9", "10", "11", etc.
        String majorVersion = version.split("\\.")[0];
        try {
            return Integer.parseInt(majorVersion) >= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if javah executable is available
     */
    private boolean isJavahAvailable() {
        try {
            Process process = Runtime.getRuntime().exec(new String[] {"javah", "-version"});
            process.waitFor();
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
