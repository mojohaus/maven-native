package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class NativeLinkerMojoTest
    extends AbstractMojoTestCase
{
    public void testMojoLookup()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/linker/plugin-config.xml" );
        NativeLinkMojo mojo = (NativeLinkMojo) lookupMojo( "link", pluginXml );
        assertNotNull( mojo );
    }

    public void NotestExecute()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/linker/plugin-config.xml" );
        NativeLinkMojo mojo = (NativeLinkMojo) lookupMojo( "link", pluginXml );
        assertNotNull( mojo );
        
        //must init this
        mojo.setPluginContext( new HashMap() );
        
        //simulate object files
        List objectList = new ArrayList();
        objectList.add( new File( "o1.o" ) );
        objectList.add( new File( "o2.o" ) );
        mojo.saveCompilerOutputFilePaths( objectList );
        
        //simulate artifact 
        ArtifactHandler artifactHandler = new DefaultArtifactHandler();
        Artifact artifact = new DefaultArtifact( "test", "test", VersionRange.createFromVersion( "1.0-SNAPSHOT" ), "compile", "exe", null, artifactHandler );
        mojo.getProject().setArtifact( artifact );

        //simulate artifacts 
        mojo.getProject().setArtifacts( new HashSet() ); //no extern libs for now
        
        mojo.execute();
         
    }
}
