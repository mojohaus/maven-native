package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NativeUnZipIncMojoTest extends AbstractMojoTestCase {

    @Test
    void transitiveIncZipDependencies() throws Exception {
        File pluginXml = new File(getBasedir(), "src/test/resources/unzipinc/plugin-config.xml");
        NativeUnZipIncMojo mojo = (NativeUnZipIncMojo) lookupMojo("unzipinc", pluginXml);
        assertNotNull(mojo);

        // Set up plugin context
        mojo.setPluginContext(new HashMap<>());

        // Create direct and transitive inczip artifacts
        ArtifactHandler inczipHandler = new DefaultArtifactHandler("inczip");

        // Direct dependency
        Artifact directInczip = new DefaultArtifact(
                "com.example",
                "direct-headers",
                VersionRange.createFromVersion("1.0"),
                "compile",
                "inczip",
                null,
                inczipHandler);

        // Transitive dependency
        Artifact transitiveInczip = new DefaultArtifact(
                "com.example",
                "transitive-headers",
                VersionRange.createFromVersion("2.0"),
                "compile",
                "inczip",
                null,
                inczipHandler);

        // Regular JAR dependency (should be ignored)
        ArtifactHandler jarHandler = new DefaultArtifactHandler("jar");
        Artifact jarArtifact = new DefaultArtifact(
                "com.example", "some-jar", VersionRange.createFromVersion("1.0"), "compile", "jar", null, jarHandler);

        // Set up dependency artifacts (direct dependencies only)
        Set<Artifact> directDependencies = new HashSet<>();
        directDependencies.add(directInczip);
        directDependencies.add(jarArtifact);
        mojo.project.setDependencyArtifacts(directDependencies);

        // Set up all artifacts (direct + transitive dependencies)
        Set<Artifact> allArtifacts = new HashSet<>();
        allArtifacts.add(directInczip);
        allArtifacts.add(transitiveInczip);
        allArtifacts.add(jarArtifact);
        mojo.project.setArtifacts(allArtifacts);

        // Use reflection to access the private getIncZipDependencies method
        java.lang.reflect.Method method = NativeUnZipIncMojo.class.getDeclaredMethod("getIncZipDependencies");
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<Artifact> incZipDeps = (java.util.List<Artifact>) method.invoke(mojo);

        // Verify that both direct and transitive inczip dependencies are included
        assertNotNull(incZipDeps);
        assertEquals(2, incZipDeps.size(), "Should include both direct and transitive inczip dependencies");

        // Verify that jar artifact is not included
        boolean hasDirectInczip = false;
        boolean hasTransitiveInczip = false;
        boolean hasJar = false;

        for (Artifact artifact : incZipDeps) {
            if (artifact.getArtifactId().equals("direct-headers")) {
                hasDirectInczip = true;
            } else if (artifact.getArtifactId().equals("transitive-headers")) {
                hasTransitiveInczip = true;
            } else if (artifact.getArtifactId().equals("some-jar")) {
                hasJar = true;
            }
        }

        assertTrue(hasDirectInczip, "Should include direct inczip dependency");
        assertTrue(hasTransitiveInczip, "Should include transitive inczip dependency");
        assertFalse(hasJar, "Should not include jar dependency");
    }
}
