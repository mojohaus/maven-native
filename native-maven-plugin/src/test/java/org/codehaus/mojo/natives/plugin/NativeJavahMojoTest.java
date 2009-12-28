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
        NativeJavahMojo mojo = (NativeJavahMojo) this.lookupMojo( "javah", pluginXml );
        
        mojo.execute();
        
        JavahConfiguration config = mojo.getJavahConfiguration();
        
        //only found ${project.build.outputDirectory}
        assertEquals( 1, config.getClassPaths().length );
        
        String classesDir = config.getClassPaths()[0];
        
        assertEquals( mojo.getProject().getBuild().getOutputDirectory(), classesDir );
        
        assertEquals( new File( "target/javah"), config.getOutputDirectory() );
        
        assertEquals( new File( getBasedir() ), config.getWorkingDirectory() );
        
        //in test mode, the project starts out of 0 source root instead of 1
        assertEquals( 1, mojo.getProject().getCompileSourceRoots().size() );
                
    }
}
