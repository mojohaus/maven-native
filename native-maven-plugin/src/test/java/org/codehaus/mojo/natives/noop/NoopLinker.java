package org.codehaus.mojo.natives.noop;

import javax.inject.Named;
import javax.inject.Singleton;

import java.io.File;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.Linker;
import org.codehaus.mojo.natives.linker.LinkerConfiguration;

@Named("noop")
@Singleton
public class NoopLinker implements Linker {

    @Override
    public File link(LinkerConfiguration config, List<File> compilerOutputFiles) throws NativeBuildException {
        if (!config.getOutputDirectory().exists()) {
            config.getOutputDirectory().mkdirs();
        }

        String fileName = config.getOutputFileName();

        if (config.getOutputFileExtension() != null) {
            fileName += "." + config.getOutputFileExtension();
        }
        return new File(config.getOutputDirectory(), fileName);
    }
}
