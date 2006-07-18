package org.codehaus.mojo.natives.linker;

import java.io.File;

import org.codehaus.mojo.natives.NativeBuildException;

public interface Ranlib
{
    String ROLE = Ranlib.class.getName();
    
    void run( File file ) throws NativeBuildException;
}
