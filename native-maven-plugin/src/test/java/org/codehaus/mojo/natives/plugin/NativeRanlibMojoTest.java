package org.codehaus.mojo.natives.plugin;

import java.io.File;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@MojoTest
class NativeRanlibMojoTest {
    @Test
    @InjectMojo(goal = "ranlib", pom = "src/test/resources/linker/plugin-config-ranlib.xml")
    void mojoLookup(NativeRanlibMojo mojo) throws Exception {
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
        mojo.getProject().setArtifact(artifact);

        mojo.execute();
    }

    private static String getBasedir() {
        return System.getProperty("basedir", new File("").getAbsolutePath());
    }
}
