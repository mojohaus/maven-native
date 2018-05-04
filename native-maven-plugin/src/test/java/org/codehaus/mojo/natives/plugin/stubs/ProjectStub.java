package org.codehaus.mojo.natives.plugin.stubs;

import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;

/**
 * Stub MavenProject to support native-maven-plugin test harness
 */

public class ProjectStub
    extends MavenProjectStub
{
    private Build build = new BuildStub();

    private Set<Artifact> dependencyArtifacts = new HashSet<>();

    public ProjectStub()
    {
        super( (Model) null );
    }

    // kinda dangerous...
    public ProjectStub( Model model )
    {
        // super(model);
        super( (Model) null );
    }

    // kinda dangerous...
    public ProjectStub( MavenProject project )
    {
        // super(project);
        super( (Model) null );
    }

    @Override
    public Build getBuild()
    {
        return this.build;
    }

    @Override
    public Set<Artifact> getDependencyArtifacts()
    {
        return this.dependencyArtifacts;
    }

    private Set<Artifact> artifacts;

    @Override
    public void setArtifacts( Set<Artifact> artifacts )
    {
        this.artifacts = artifacts;
    }

    @Override
    public Set<Artifact> getArtifacts()
    {
        if ( this.artifacts == null )
        {
            this.artifacts = new HashSet<>();
        }
        return this.artifacts;
    }

    @Override
    public String getArtifactId()
    {
        return "someArtifactId";
    }

}
