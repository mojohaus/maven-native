package org.codehaus.mojo.natives.plugin;

import java.io.File;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.codehaus.mojo.natives.javah.JavahConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MojoTest
public class NativeJavahMojoTest {
    @Test
    @InjectMojo(goal = "javah", pom = "src/test/resources/javah/plugin-config.xml")
    void testMojoLookup(NativeJavahMojo mojo) throws Exception {
        assertNotNull(mojo);
    }

    @Test
    @InjectMojo(goal = "javah", pom = "src/test/resources/javah/plugin-config.xml")
    void testClassPathBeginsWithProjectOutputDirectory(NativeJavahMojo mojo) throws Exception {

        mojo.execute();

        JavahConfiguration config = mojo.getJavahConfiguration();

        // only found ${project.build.outputDirectory}
        assertEquals(1, config.getClassPaths().length);

        String classesDir = config.getClassPaths()[0];

        assertEquals(mojo.getProject().getBuild().getOutputDirectory(), classesDir);

        assertEquals(new File(getBasedir(), "target/javah"), config.getOutputDirectory());

        assertEquals(new File(getBasedir()), config.getWorkingDirectory());

        // in test mode, the project starts out of 0 source root instead of 1
        assertEquals(1, mojo.getProject().getCompileSourceRoots().size());
    }

    private static String getBasedir() {
        return System.getProperty("basedir", new File("").getAbsolutePath());
    }
}
