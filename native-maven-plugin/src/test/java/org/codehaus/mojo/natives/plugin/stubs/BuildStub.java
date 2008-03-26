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
    private String finalName = "some-final-name";
    
    public String getOutputDirectory()
    {
        return "fake-output-directory";
    }
    
    public String getFinalName()
    {
    	return  this.finalName;
    }
    
    public void setFinalName( String name )
    {
        this.finalName = name;
    }
    
    public String getDirectory()
    {
        return "target";
    }
}
