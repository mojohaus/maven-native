package org.codehaus.mojo.natives.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

public class NativeMojoUtils
{
    /**
     * Remove/trim empty or null member of a string array
     * @param args
     * @return
     */
    public static String [] trimParams( String [] args )
    {
        if ( args == null )
        {
            return new String[0];
        }

        List tokenArray = new ArrayList();

        for ( int i = 0; i < args.length; ++i )
        {           
            if ( args[i] == null || args[i].length() == 0 )
            {
                continue;
            }
            
            String [] tokens = StringUtils.split( args[i] );

            for ( int k = 0 ; k < tokens.length; ++k )
            {           
                if ( tokens[k] == null || tokens[k].trim().length() == 0 )
                {
                    continue;
                }
                
                tokenArray.add( tokens[k].trim() );             
            }
        }
        
        return (String []) tokenArray.toArray( new String[0] );
    }
    
    public static void appendFilePathsToFile( File dest, List filePaths )
        throws MojoExecutionException
    {
        FileOutputStream fs = null;
      
        StringBuffer buffer = new StringBuffer();

        try 
        {
            fs = new  FileOutputStream(dest, true );
          
            for ( int i = 0; i < filePaths.size(); ++i )
            {
                File file = (File) filePaths.get(i);
              
                buffer.append( file.getPath() );
              
                buffer.append(" ");
            }
          
            fs.write( buffer.toString().getBytes() );
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException( "Error storing object file list at " + dest.getPath() );
        }
        finally
        {
            if ( fs != null )
            {
                try 
                {
                    fs.close();
                }
                catch ( IOException ioe )
                {
                    throw new MojoExecutionException( "Error storing object file list at " + dest.getPath() );
                }
            }
        }  
    }
  
    public static List getCompilerOuputFiles ( File from )
        throws MojoExecutionException
    {
        if ( ! from.exists() )
        {
            return new ArrayList();
        }
      
        try 
        {
            String fileLists = FileUtils.fileRead( from );
          
            String [] fileNames = StringUtils.split( fileLists );
          
            List filePaths = new ArrayList( fileNames.length );
          
            for ( int i = 0; i < fileNames.length; ++i )
            {
                filePaths.add( new File( fileNames[i] ) );
            }
          
            return filePaths;
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException( "Error reading object file list at " + from.getPath() );
        }
    }    
}
