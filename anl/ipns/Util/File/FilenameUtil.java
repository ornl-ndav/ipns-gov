/**
 *  @(#)FilenameUtil.java   0.1 2000/08/03  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.1  2000/08/03 21:41:14  dennis
 *  Now includes FilenameUtil.fixCase() to check for case errors in basic
 *  filename
 *
 *
 *
 */ 
package DataSetTools.util;

import java.awt.*;
import java.io.*;
import DataSetTools.instruments.*;

/**
 *  Provide utilitiy for fixing file names
 */

public class FilenameUtil
{
  /**
   *  Don't instantiate this class, just use the Fonts provided.
   */
  private FilenameUtil() {}


  /**
   *  Replace all occurrences of the possible file separators "/" "\" "\\"
   *  with the file separator needed for the local system.
   *
   *  @param  file_name  A file name string possibly containing improper
   *                     separators.
   *
   *  @return  A string containing the file name with all separators replaced 
   *           by the system dependent separtator needed on the local system. 
   */
  public static String fixSeparator( String file_name )
  {
    String separator = File.separator;

    String result = StringUtil.replace( file_name, "\\\\", separator );
    result = StringUtil.replace( result, "\\", separator );
    result = StringUtil.replace( result, "/", separator );

    return result;
  }


  /**
   *  Adjust the case of a file name if the specified file does not exist
   *
   *  @param  file_name  A file name string possibly with the wrong case. 
   *
   *  @return  A string containing the file name with the actual file name
   *           ( excluding the path ) changed to a different case, if the
   *           original file did not exist, but the file with the changed
   *           case does exist.  If the file can't be found this returns null.
   */
  public static String fixCase( String file_name )
  {
    boolean opened_ok = true;

    File file = new File ( file_name );
    if ( file.exists() )
      return file_name;

    String name = InstrumentType.getFileName( file_name );
    String path = InstrumentType.getPath( file_name );
    char   first_char = name.charAt(0);
    if ( Character.isUpperCase( first_char ) )
      file_name = path + name.toLowerCase();
    else
      file_name = path + name.toUpperCase();

    file = new File ( file_name );
    if ( file.exists() )
      return file_name;

    return null;
  }


}
