package org.codehaus.mojo.natives.plugin;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.mojo.natives.javah.JavahConfiguration;

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

        JavahConfiguration conf = mojo.createProviderConfiguration();
        String outputDirectory = conf.getClassPaths()[0];
        assertEquals( mojo.getProject().getBuild().getOutputDirectory(), outputDirectory );
    }
}
