package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.mojo.natives.javah.JavahConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MojoTest
@SuppressWarnings("deprecation")
class NativeJavahMojoTest {
    @Test
    @InjectMojo(goal = "javah", pom = "src/test/resources/javah/plugin-config.xml")
    void mojoLookup(NativeJavahMojo mojo) throws Exception {
        assertNotNull(mojo);
    }

    @Test
    @InjectMojo(goal = "javah", pom = "src/test/resources/javah/plugin-config.xml")
    void classPathBeginsWithProjectOutputDirectory(NativeJavahMojo mojo) throws Exception {

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

    @Test
    @InjectMojo(goal = "javah", pom = "src/test/resources/javah/plugin-config.xml")
    void warnsThatTheGoalIsDeprecated(NativeJavahMojo mojo) throws Exception {
        List<String> warnings = new ArrayList<>();
        mojo.setLog(new SystemStreamLog() {
            @Override
            public void warn(CharSequence content) {
                warnings.add(content.toString());
            }
        });

        mojo.execute();

        assertEquals(Collections.singletonList(NativeJavahMojo.DEPRECATION_WARNING), warnings);
        assertTrue(NativeJavahMojo.DEPRECATION_WARNING.contains("headerOutputDirectory"));
        assertNotNull(NativeJavahMojo.class.getAnnotation(Deprecated.class));
    }

    private static String getBasedir() {
        return System.getProperty("basedir", new File("").getAbsolutePath());
    }
}
