package org.codehaus.mojo.natives.javah;

import java.io.File;

/**
 * @deprecated use the JavahExecutable instead since its invokes javah on system path
 *
 */
public class JavahMacOSExecutable 
  extends JavahExecutable 
{
	protected String getJavaHExecutable()
	{
		return new File( System.getProperty( "java.home" ), "bin/javah" ).getAbsolutePath();
	}

}
