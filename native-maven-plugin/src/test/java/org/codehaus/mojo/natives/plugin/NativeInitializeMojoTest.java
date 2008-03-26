package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.HashMap;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class NativeInitializeMojoTest
    extends AbstractMojoTestCase
{
    public void testMojoLookup()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/initialize/plugin-config.xml" );
        NativeInitializeMojo mojo = (NativeInitializeMojo) lookupMojo( "initialize", pluginXml );
        assertNotNull( mojo );
        
        //simulate artifact 
        ArtifactHandler artifactHandler = new DefaultArtifactHandler();
        Artifact artifact = new DefaultArtifact( "test", "test", VersionRange.createFromVersion( "1.0-SNAPSHOT" ), "compile", "exe", null, artifactHandler );
        mojo.project.setArtifact( artifact );
        mojo.setPluginContext( new HashMap() );
        
        
        mojo.execute();
        
        assertEquals( "someArtifactId", mojo.project.getBuild().getFinalName() );
    }

}
