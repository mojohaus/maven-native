package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.HashMap;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MojoTest
public class NativeInitializeMojoTest {
    @Test
    @InjectMojo(goal = "initialize", pom = "src/test/resources/initialize/plugin-config.xml")
    void testMojoLookup(NativeInitializeMojo mojo) throws Exception {
        assertNotNull(mojo);

        // simulate artifact
        ArtifactHandler artifactHandler = new DefaultArtifactHandler();
        Artifact artifact = new DefaultArtifact(
                "test",
                "test",
                VersionRange.createFromVersion("1.0-SNAPSHOT"),
                "compile",
                "exe",
                null,
                artifactHandler);
        mojo.project.setArtifact(artifact);
        mojo.setPluginContext(new HashMap<>());

        mojo.execute();

        assertEquals("someArtifactId", mojo.project.getBuild().getFinalName());
    }

    @Test
    @InjectMojo(goal = "initialize", pom = "src/test/resources/initialize/plugin-config-skip.xml")
    void testSkip(NativeInitializeMojo mojo) throws Exception {
        assertNotNull(mojo);

        // simulate artifact
        ArtifactHandler artifactHandler = new DefaultArtifactHandler();
        Artifact artifact = new DefaultArtifact(
                "test",
                "test",
                VersionRange.createFromVersion("1.0-SNAPSHOT"),
                "compile",
                "exe",
                null,
                artifactHandler);
        mojo.project.setArtifact(artifact);
        mojo.setPluginContext(new HashMap<>());

        String originalFinalName = mojo.project.getBuild().getFinalName();

        // Execute the mojo with skip=true
        mojo.execute();

        // When skip is true, the final name should not be changed
        assertEquals(originalFinalName, mojo.project.getBuild().getFinalName());
    }

    private static String getBasedir() {
        return System.getProperty("basedir", new File("").getAbsolutePath());
    }
}
