/**
 *  @(#)StringUtil.java   0.1 2000/07/28  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.1  2000/07/28 16:16:08  dennis
 *  Utility for fixing file names for portability to different systems
 *
 *
 */ 
package DataSetTools.util;

import java.awt.*;
import java.io.*;

/**
 *  Provide utilitiy for fixing file names
 */

public class StringUtil
{
  /**
   *  Don't instantiate this class, just use the Fonts provided.
   */
  private StringUtil() {}


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

    String result = replace( file_name, "\\\\", separator );
    result = replace( result, "\\", separator );
    result = replace( result, "/", separator );

    return result;
  }


  /**
   *  Replace all occurrences of a specified string by another string
   *
   *  @param   in_string  the string in which the replacement is to be 
   *                      made. 
   *
   *  @param   old_chars  the string which is to be replaced.
   *
   *  @param   new_chars  the string that replaces old_chars.
   *
   *  @return  A new string in which all occurences of the old_chars string 
   *           are replaced by the new_chars string. 
   */
  public static String replace( String in_string, 
                                String old_chars, 
                                String new_chars )
  {
    if (in_string == null || old_chars == null || new_chars == null )
      return null;

    if ( old_chars.equals( new_chars ) )
      return in_string;

    int    start;
    String result = in_string;

    while ( result.indexOf( old_chars ) >= 0 )
    {
      start = result.indexOf( old_chars );
      result = result.substring(0, start) + 
               new_chars + 
               result.substring( start + old_chars.length() );
    }
    return result;
  }


/* ---------------------------------------------------------------------------
 *
 *  Main program for test purposes.
 *
 */
  public static void main(String[] args)
  {
    int [] ilist;
    String in_str;

    for (int i = 0; i < args.length; i++)
    {
      in_str = args[i];

      System.out.println( in_str + " translates to " + fixSeparator(in_str) );
    } 

    in_str = "C:\\junk\\junk2\\run2.run";
    System.out.println( in_str + " translates to " + fixSeparator(in_str) );

    in_str = "C:\\\\junk\\\\junk2\\\\run2.run";
    System.out.println( in_str + " translates to " + fixSeparator(in_str) );

    in_str = "C:/junk/junk2/run2.run";
    System.out.println( in_str + " translates to " + fixSeparator(in_str) );

    in_str = "C:/junk\\\\junk2\\run2.run";
    System.out.println( in_str + " translates to " + fixSeparator(in_str) );

    in_str = "\\junk\\\\junk2\\run2.run";
    System.out.println( in_str + " translates to " + fixSeparator(in_str) );

    in_str = "\\junk\\\\junk2\\run2.run\\";
    System.out.println( in_str + " translates to " + fixSeparator(in_str) );
  }
}
