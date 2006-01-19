package org.codehaus.mojo.natives.javah;

import java.io.File;

public class JavahMacOSExecutable 
  extends JavahExecutable 
{
	protected File getJavaHExecutable()
	{
		return new File( System.getProperty( "java.home" ), "bin/javah" );
	}

}
