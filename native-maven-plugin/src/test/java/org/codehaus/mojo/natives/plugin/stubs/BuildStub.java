package org.codehaus.mojo.natives.plugin.stubs;

import org.apache.maven.model.Build;

/**
 * 
 * @author dtran
 *
 */
public class BuildStub
    extends Build
{
    public String getOutputDirectory()
    {
        return "fake-output-directory";
    }
    
    public String getFinalName()
    {
    	return "some-final-name";
    }
}
