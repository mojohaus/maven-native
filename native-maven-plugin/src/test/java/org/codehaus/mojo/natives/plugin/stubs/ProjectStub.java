package org.codehaus.mojo.natives.plugin.stubs;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;

/**
 * Stub MavenProject to support native-maven-plugin test harness
 * @author dtran
 *
 */

public class ProjectStub
   extends MavenProjectStub
{    
    private Build build = new Build();

 
    private Set dependencyArtifacts = new HashSet();


    public ProjectStub()
    {
        super( (Model) null );
    }

    // kinda dangerous...
    public ProjectStub( Model model )
    {
        //  super(model);
        super( (Model) null );
    }

    // kinda dangerous...
    public ProjectStub( MavenProject project )
    {
        //super(project);
        super( (Model) null );
    }
    
    public Build getBuild()
    {
        return this.build;
    }
    
    public Set getDependencyArtifacts()
    {
        return this.dependencyArtifacts;
    }
    
}