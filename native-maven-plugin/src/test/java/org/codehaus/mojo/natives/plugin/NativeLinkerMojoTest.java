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
import org.codehaus.mojo.natives.linker.LinkerConfiguration;

public class NativeLinkerMojoTest
    extends AbstractMojoTestCase
{
    private NativeLinkMojo getMojo() throws Exception {
        File pluginXml = new File( getBasedir(), "src/test/resources/linker/plugin-config.xml" );
        NativeLinkMojo mojo = (NativeLinkMojo) lookupMojo( "link", pluginXml );
        assertNotNull( mojo );

        // must init this
        mojo.setPluginContext( new HashMap() );
        return mojo;
    }

    public void testExecute() throws Exception
    {
        NativeLinkMojo mojo = getMojo();

        // simulate object files
        List objectList = new ArrayList();
        objectList.add( new File( "o1.o" ) );
        objectList.add( new File( "o2.o" ) );
        mojo.saveCompilerOutputFilePaths( objectList );

        // simulate artifact
        ArtifactHandler artifactHandler = new DefaultArtifactHandler();

        Artifact artifact = new DefaultArtifact( "test", "test", VersionRange.createFromVersion( "1.0-SNAPSHOT" ), "compile", "exe", null, artifactHandler);
        mojo.getProject().setArtifact( artifact );

        // simulate artifacts
        mojo.getProject().setArtifacts( new HashSet() ); // no extern libs for now

        String linkerFinalName = "some-final-name";
        setVariableValueToObject( mojo, "linkerFinalName", linkerFinalName );

        mojo.execute();

        LinkerConfiguration conf = mojo.getLgetLinkerConfiguration();

        // "target is set in the stub
        assertEquals( new File( "target" ), conf.getOutputDirectory() );
        assertEquals( linkerFinalName, conf.getOutputFileName() );
        assertNull(conf.getOutputFileExtension() );
        // current artifactHandler mocking return null extension name
        assertEquals( new File( "target/some-final-name.null" ), conf.getOutputFile() );

    }

    public void testExecuteWithFinalNameExtension() throws Exception
    {
        NativeLinkMojo mojo = getMojo();

        // simulate object files
        List objectList = new ArrayList();
        objectList.add( new File( "o1.o" ) );
        objectList.add( new File( "o2.o" ) );
        mojo.saveCompilerOutputFilePaths( objectList );

        // simulate artifact
        ArtifactHandler artifactHandler = new DefaultArtifactHandler();

        Artifact artifact = new DefaultArtifact( "test", "test", VersionRange.createFromVersion( "1.0-SNAPSHOT" ), "compile", "exe", null, artifactHandler );
        mojo.getProject().setArtifact( artifact );

        // simulate artifacts
        mojo.getProject().setArtifacts( new HashSet() ); // no extern libs for now

        String linkerFinalName = "some-final-name";
        setVariableValueToObject( mojo, "linkerFinalName", linkerFinalName );
        String linkerFinalNameExt = "some-extension";
        setVariableValueToObject( mojo, "linkerFinalNameExt", linkerFinalNameExt );

        mojo.execute();

        LinkerConfiguration conf = mojo.getLgetLinkerConfiguration();

        // "target is set in the stub
        assertEquals( new File( "target" ), conf.getOutputDirectory() );
        assertEquals( linkerFinalName, conf.getOutputFileName() );
        assertEquals( linkerFinalNameExt, conf.getOutputFileExtension() );
        assertEquals( new File( "target/some-final-name.some-extension" ), conf.getOutputFile() );

    }
}
