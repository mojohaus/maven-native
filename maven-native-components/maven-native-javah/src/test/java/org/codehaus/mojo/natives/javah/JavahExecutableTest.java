package org.codehaus.mojo.natives.javah;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.cli.Commandline;

import static org.junit.Assert.*;

public class JavahExecutableTest extends PlexusTestCase {
    private JavahConfiguration config;

    public void setUp() throws Exception {
        super.setUp();

        this.config = new JavahConfiguration();

        String[] classPaths = {"path1", "path2"};
        config.setClassPaths(classPaths);

        String[] classNames = {"className1", "className2"};
        config.setClassNames(classNames);

        config.setOutputDirectory(new File(getBasedir(), "target/native"));
    }

    public void testDefaultJavahExecutable() {
        // Force use of javah to avoid Java version detection
        config.setJavahPath(new File("javah"));

        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand(config);

        File outputDir = new File(getBasedir(), "target/native");

        // When javahPath is set, it uses the absolute path
        assertTrue(cl.getLiteralExecutable().endsWith("javah"));
        assertArrayEquals(
                new String[] {
                    "-d",
                    outputDir.getAbsolutePath(),
                    "-classpath",
                    "path1" + File.pathSeparator + "path2",
                    "className1",
                    "className2"
                },
                cl.getArguments());
    }

    public void testConfiguredJavahExecutable() {
        File javaBin = new File("/java/home/bin");

        JavahExecutable javah = new JavahExecutable();
        config.setJavahPath(javaBin);
        Commandline cl = javah.createJavahCommand(config);

        File outputDir = new File(getBasedir(), "target/native");

        assertEquals(javaBin.getAbsolutePath(), cl.getLiteralExecutable());
        assertArrayEquals(
                new String[] {
                    "-d",
                    outputDir.getAbsolutePath(),
                    "-classpath",
                    "path1" + File.pathSeparator + "path2",
                    "className1",
                    "className2"
                },
                cl.getArguments());
    }

    public void testJavahExecutableDashoOption() {
        // Force use of javah to avoid Java version detection
        config.setJavahPath(new File("javah"));
        config.setFileName("fileName");

        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand(config);

        File outputFile = new File(getBasedir(), "target/native/" + "fileName");
        assertArrayEquals(
                new String[] {
                    "-o",
                    outputFile.getAbsolutePath(),
                    "-classpath",
                    "path1" + File.pathSeparator + "path2",
                    "className1",
                    "className2"
                },
                cl.getArguments());
    }

    public void testWorkingDirectory() {
        // Force use of javah to avoid Java version detection
        config.setJavahPath(new File("javah"));

        JavahExecutable javah = new JavahExecutable();

        File workingDirectory = new File(getBasedir());
        config.setWorkingDirectory(workingDirectory);

        Commandline cl = javah.createJavahCommand(config);

        assertEquals(workingDirectory, cl.getWorkingDirectory());
    }

    public void testJavacExecutableWithSourceRoots() throws Exception {
        // Create test source file
        File sourceRoot = new File(getBasedir(), "target/test-sources");
        sourceRoot.mkdirs();
        File packageDir = new File(sourceRoot, "com/example");
        packageDir.mkdirs();
        File sourceFile = new File(packageDir, "TestClass.java");
        java.nio.file.Files.write(
                sourceFile.toPath(),
                "package com.example; public class TestClass { public native void test(); }".getBytes());

        // Configure to use javac
        config.setJavahPath(new File("/usr/bin/javac"));
        config.setSourceRoots(new String[] {sourceRoot.getAbsolutePath()});
        config.setClassNames(new String[] {"com.example.TestClass"});

        JavahExecutable javah = new JavahExecutable();
        Commandline cl = javah.createJavahCommand(config);

        File outputDir = new File(getBasedir(), "target/native");

        assertEquals("/usr/bin/javac", cl.getLiteralExecutable());

        String[] args = cl.getArguments();
        // Should have: -h, outputDir, -classpath, classpath, sourceFile
        assertTrue(args.length >= 5);
        assertEquals("-h", args[0]);
        assertEquals(outputDir.getAbsolutePath(), args[1]);
        assertEquals("-classpath", args[2]);
        assertTrue(args[args.length - 1].endsWith("TestClass.java"));
    }
}
