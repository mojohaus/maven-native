package org.codehaus.mojo.natives.plugin;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class NativeRanlibMojoTest
    extends AbstractMojoTestCase
{
    public void testMojoLookup()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/linker/plugin-config-ranlib.xml" );
        NativeRanlibMojo mojo = (NativeRanlibMojo) lookupMojo( "ranlib", pluginXml );
        assertNotNull( mojo );
        
        //simulate artifact 
        ArtifactHandler artifactHandler = new DefaultArtifactHandler();
       
        Artifact artifact = new DefaultArtifact( "test", "test", VersionRange.createFromVersion( "1.0-SNAPSHOT" ), "compile", "exe", null, artifactHandler );
        mojo.getProject().setArtifact( artifact );
        
        mojo.execute();
    }

}
