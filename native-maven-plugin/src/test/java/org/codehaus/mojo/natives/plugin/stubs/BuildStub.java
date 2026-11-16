package org.codehaus.mojo.natives.plugin.stubs;

import org.apache.maven.model.Build;

public class BuildStub extends Build {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String finalName = "some-final-name";

    @Override
    public String getOutputDirectory() {
        return "fake-output-directory";
    }

    @Override
    public String getFinalName() {
        return this.finalName;
    }

    @Override
    public void setFinalName(String name) {
        this.finalName = name;
    }

    @Override
    public String getDirectory() {
        return "target";
    }
}
