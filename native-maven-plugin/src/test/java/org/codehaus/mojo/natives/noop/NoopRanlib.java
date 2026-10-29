package org.codehaus.mojo.natives.noop;

import javax.inject.Named;
import javax.inject.Singleton;

import java.io.File;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.linker.Ranlib;

/**
 * Helper class to test native-maven-plugin' javah mojo
 */
@Named("noop")
@Singleton
public class NoopRanlib implements Ranlib {

    public void run(File file) throws NativeBuildException {}
}
