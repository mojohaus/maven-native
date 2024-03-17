package org.codehaus.mojo.natives.msvc;

import java.io.File;
import java.io.IOException;

public abstract class AbstractMSVC2019EnvFactory extends AbstractMSVCEnvFactory {
    private AbstractMSVC2017CircaEnvFactoryHelper helper = new AbstractMSVC2017CircaEnvFactoryHelper();

    @Override
    protected File createEnvWrapperFile(File vsInstallDir, String platform) throws IOException {
        return helper.createEnvWrapperFile(vsInstallDir, platform);
    }
}
