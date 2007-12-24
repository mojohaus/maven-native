package org.codehaus.mojo.natives.util;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.StringUtils;

public class FileUtil
{

    /**
     * Returns a relative path for the targetFile relative to the base
     * directory.
     *
     * @param canonicalBase
     *            base directory as returned by File.getCanonicalPath()
     * @param targetFile
     *            target file
     * @return relative path of target file. Returns targetFile if there were
     *         no commonalities between the base and the target
     *
     * @author Curt Arnold
     */
    public static File getRelativeFile( File workingDirectory, File targetFile )
    {
        try
        {
            //
            //   remove trailing file separator
            //
            String canonicalBase = workingDirectory.getCanonicalFile().getAbsolutePath();

            if ( canonicalBase.charAt( canonicalBase.length() - 1 ) == File.separatorChar )
            {
                canonicalBase = canonicalBase.substring( 0, canonicalBase.length() - 1 );
            }

            //
            //   get canonical name of target and remove trailing separator
            //

            String canonicalTarget;

            if ( System.getProperty( "os.name" ).equals( "OS/400" ) )
            {
                canonicalTarget = targetFile.getPath();
            }
            else
            {
                canonicalTarget = targetFile.getCanonicalPath();
            }

            if ( canonicalTarget.charAt( canonicalTarget.length() - 1 ) == File.separatorChar )
            {
                canonicalTarget = canonicalTarget.substring( 0, canonicalTarget.length() - 1 );
            }

            if ( canonicalTarget.equals( canonicalBase ) )
            {
                return new File ( "." );
            }

            //
            //  see if the prefixes are the same
            //
            if ( canonicalBase.substring( 0, 2 ).equals( "\\\\" ) )
            {
                //
                //  UNC file name, if target file doesn't also start with same
                //      server name, don't go there
                int endPrefix = canonicalBase.indexOf( '\\', 2 );
                String prefix1 = canonicalBase.substring( 0, endPrefix );
                String prefix2 = canonicalTarget.substring( 0, endPrefix );
                if ( !prefix1.equals( prefix2 ) )
                {
                    return new File ( canonicalTarget );
                }
            }
            else
            {
                if ( canonicalBase.substring( 1, 3 ).equals( ":\\" ) )
                {
                    int endPrefix = 2;
                    String prefix1 = canonicalBase.substring( 0, endPrefix );
                    String prefix2 = canonicalTarget.substring( 0, endPrefix );
                    if ( !prefix1.equals( prefix2 ) )
                    {
                        return new File ( canonicalTarget );
                    }
                }
                else
                {
                    if ( canonicalBase.charAt( 0 ) == '/' )
                    {
                        if ( canonicalTarget.charAt( 0 ) != '/' )
                        {
                            return new File ( canonicalTarget );
                        }
                    }
                }
            }

            char separator = File.separatorChar;
            int lastSeparator = -1;
            int minLength = canonicalBase.length();

            if ( canonicalTarget.length() < minLength )
            {
                minLength = canonicalTarget.length();
            }

            int firstDifference = minLength + 1;

            //
            //  walk to the shorter of the two paths
            //      finding the last separator they have in common
            for ( int i = 0; i < minLength; i++ )
            {
                if ( canonicalTarget.charAt( i ) == canonicalBase.charAt( i ) )
                {
                    if ( canonicalTarget.charAt( i ) == separator )
                    {
                        lastSeparator = i;
                    }
                }
                else
                {
                    firstDifference = lastSeparator + 1;
                    break;
                }
            }

            StringBuffer relativePath = new StringBuffer( 50 );

            //
            //   walk from the first difference to the end of the base
            //      adding "../" for each separator encountered
            //
            if ( canonicalBase.length() > firstDifference )
            {
                relativePath.append( ".." );
                for ( int i = firstDifference; i < canonicalBase.length(); i++ )
                {
                    if ( canonicalBase.charAt( i ) == separator )
                    {
                        relativePath.append( separator );
                        relativePath.append( ".." );
                    }
                }
            }

            if ( canonicalTarget.length() > firstDifference )
            {
                //
                //    append the rest of the target
                //

                if ( relativePath.length() > 0 )
                {
                    relativePath.append( separator );
                }

                relativePath.append( canonicalTarget.substring( firstDifference ) );
            }

            return new File ( relativePath.toString() );

        }
        catch ( IOException ex )
        {
            //TODO more handling
        }

        return targetFile;

    }

    public static File[] breakPaths( String paths )
    {
        String[] tokens = StringUtils.split( paths, "," );

        File[] files = new File[tokens.length];

        for ( int i = 0; i < tokens.length; ++i )
        {
            files[i] = new File( tokens[i] );
        }

        return files;
    }

    /**
     * @param path String
     * @param baseDirectory String
     * @return relative path to a base directory if possible
     */
    public static String truncatePath( String path, String baseDirectory )
    {
        if ( path.indexOf( baseDirectory ) >= 0 )
        {
            path = path.substring( path.indexOf( baseDirectory ) + baseDirectory.length() );
            if ( path.startsWith( File.separator ) )
            {
                path = path.substring( File.separator.length() );
            }
        }
        return path;
    }
}
