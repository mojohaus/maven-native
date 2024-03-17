package org.codehaus.mojo.natives.noop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.compiler.Compiler;
import org.codehaus.mojo.natives.compiler.CompilerConfiguration;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.FileUtils;

/**
 * Helper class to test native-maven-plugin
 */
@Component(role = Compiler.class, hint = "noop")
public class NoopCompiler implements Compiler {

    @Override
    public List<File> compile(CompilerConfiguration config, File[] sourceFiles) throws NativeBuildException {
        List<File> compilerOutputFiles = new ArrayList<>(sourceFiles.length);

        for (File sourceFile : sourceFiles) {
            File source = new File(sourceFile.toString());

            File objectFile = this.getObjectFile(source, config);

            compilerOutputFiles.add(objectFile);
        }

        return compilerOutputFiles;
    }

    /**
     * @return
     */
    protected String getObjectFileExtension() {
        // no need to test system specific extension
        return "o";
    }

    /**
     * Figure out the object file path from a given source file
     *
     * @param sourceFile
     * @return
     */
    private File getObjectFile(File sourceFile, CompilerConfiguration config) {
        String srcPath = sourceFile.getPath();

        String destPath = config.getOutputDirectory().getPath() + "/" + FileUtils.basename(srcPath)
                + this.getObjectFileExtension();

        return new File(destPath);
    }
}
