package org.codehaus.mojo.natives.plugin.stubs;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

/**
 * Stub MavenProject to support native-maven-plugin test harness
 */

public class ProjectStub
   extends MavenProjectStub
{    
    private Build build = new BuildStub();

 
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
    
    private Set artifacts;
    
    public void setArtifacts( Set artifacts )
    {
    	this.artifacts = artifacts;
    }

    public Set getArtifacts()
    {
    	if ( this.artifacts == null )
    	{
    		this.artifacts = new HashSet();
    	}
    	return this.artifacts;
    }
    
    public String getArtifactId()
    {
        return "someArtifactId";
    }
    
    
}