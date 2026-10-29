package org.codehaus.mojo.natives.noop;

import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.javah.Javah;
import org.codehaus.mojo.natives.javah.JavahConfiguration;

/**
 * Helper class to test native-maven-plugin' javah mojo
 */
@Named("noop")
@Singleton
public class NoopJavah implements Javah {

    public void compile(JavahConfiguration config) throws NativeBuildException {}
}
