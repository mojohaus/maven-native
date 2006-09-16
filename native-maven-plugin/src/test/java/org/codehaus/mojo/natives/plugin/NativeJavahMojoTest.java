package org.codehaus.mojo.natives.plugin;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class NativeJavahMojoTest
    extends AbstractMojoTestCase
{
    public void testMojoLookup()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/javah/plugin-config.xml" );
        NativeJavahMojo mojo = (NativeJavahMojo) lookupMojo( "javah", pluginXml );
        assertNotNull( mojo );
    }

    public void testClassPathBeginsWithProjectOutputDirectory()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/javah/plugin-config.xml" );
        NativeJavahMojo mojo = (NativeJavahMojo) lookupMojo( "javah", pluginXml );

        String outputDirectory = mojo.buildConfiguration().getClassPaths()[0];
        assertEquals( mojo.getProject().getBuild().getOutputDirectory(), outputDirectory );
    }
}
