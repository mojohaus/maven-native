package org.codehaus.mojo.natives.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.plexus.util.Os;


/**
 * Contains utility methods used by tests.
 */
public class TestUtils
{

    private static final String[] WINDOWS_COMMANDLINE_PARTS = {"cmd.exe", "/X", "/C"};

    private static final List<String> WINDOWS_COMMANDLINE_PARTS_ASLIST = new ArrayList<>();

    static
    {
        WINDOWS_COMMANDLINE_PARTS_ASLIST.addAll( Arrays.asList( WINDOWS_COMMANDLINE_PARTS ) );
    }


    /**
     * Adjusts commandline based on the platform.
     *
     * @param origCommandline Commandline to adjust.
     * @return Commandline adjusted for platform.
     */
    public static String[] formPlatformCommandline( String[] origCommandline )
    {

        if ( null == origCommandline )
        {
            return null;
        }

        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            return formWindowsCommandline( origCommandline );
        }
        else
        {
            return origCommandline;
        }
    }

    /**
     * Adjusts commandline for Windows platform.
     *
     * @param origCommandline Commandline to adjust.
     * @return
     */
    private static String[] formWindowsCommandline( String[] origCommandline )
    {

        if ( null == origCommandline )
        {
            return null;
        }

        List<String> origCommandlineAsList = new ArrayList<>();

        // combine origCommandline with spaces in between

        final int SIZE_ORIG_COMMANDLINE = origCommandline.length;
        int i = 1;
        if ( SIZE_ORIG_COMMANDLINE > 0 )
        {
            for ( String origCommandlineArg : origCommandline )
            {
                if ( i == 1 )
                {
                    origCommandlineAsList.add( origCommandlineArg );
                }
                else
                {
                    origCommandlineAsList.add( " " );
                    origCommandlineAsList.add( origCommandlineArg );
                }
                i++;
            }
        }

        // now generate single quoted string of origCommandline

        StringBuilder buf = new StringBuilder();
        buf.append( "\"" );
        for ( String s : origCommandlineAsList )
        {
            buf.append( s );
        }
        buf.append( "\"" );

        // prefix result with win commandline parts
        List<String> result = new ArrayList<>( WINDOWS_COMMANDLINE_PARTS_ASLIST );
        // and add origCommandline to result
        result.add( buf.toString() );

        return result.toArray( new String[0] );
    }

    /**
     * Utility class so hide constructor.
     */
    private TestUtils()
    {
    }

}
