/**
 * File: StringUtil.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2001/08/10 14:54:46  dennis
 *  Added methods to find the nth occurence of a string and to find
 *  arguments for commands of the form "-<letter><argument>" on a
 *  command line.
 *
 *  Revision 1.2  2001/04/25 22:24:45  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.1  2000/07/28 16:16:08  dennis
 *  Utility for fixing file names for portability to different systems
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


  /* ---------------------------- fixSeparator --------------------------- */
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

  /* ------------------------------ replace ------------------------------ */
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

  /* ----------------------------- getCommand ------------------------- */
  /**
   *  Find the argument following a specified occurence of a command in a
   *  string.  Eg. If the string is
   *  "-D   /home/dennis/ -Llog.txt -D/usr/data/"
   *  the, calling this method with num = 2, and command = "-D" will return
   *  "/usr/data/".
   *
   *  @param  n        The number of the occurence of the command, 1, 2, etc.
   *  @param  command  The command to look for
   *  @param  s        The string containing commands and arguments
   *                   with space between successive command-argument pairs.
   *
   *  @return The string associated with the nth occurence of the given command.
   */
  public static String getCommand( int num, String command, String s )
  {
    boolean found = false;

    int position = nth_index_of( num, command, s );
    if ( position < 0 )
      return "";

    int start = position + command.length();

    while ( start < s.length() && Character.isWhitespace( s.charAt(start) ) )
      start++;

    if ( start >= s.length() )
      return "";

    int end = start;
    while ( end < s.length() && !Character.isWhitespace( s.charAt(end) ) )
      end++;

    if ( end < s.length() )
      return s.substring( start, end ).trim();
    else
      return s.substring( start ).trim();
  }

  /* ----------------------------- getCommand ------------------------- */
  /**
   *  Find the argument following a specified occurence of a command in a
   *  list of argument strings.  The list of argument strings is concatenated
   *  and the resulting string is passed to the version of getCommand that
   *  takes a single string.
   *
   *  @param  n        The number of the occurence of the command, 1, 2, etc.
   *  @param  command  The command to look for
   *  @param  args     The array of strings containing commands and arguments
   *                   with space between successive command-argument pairs.
   *
   *  @return The string associated with the nth occurence of the given command.
   */
  public static String getCommand( int n, String command, String args[] )
  {
    if ( args == null || args.length == 0 )
      return "";
    
    String s = args[0];
    for ( int i = 1; i < args.length; i++ )
      s += " " + args[i];

    return getCommand( n, command, s );
  }


  /* ----------------------------- nth_index_of ------------------------- */
  /**
   *  Find the "nth" occurence of a string in another string.  This will return
   *  -1 if the specified occurence does not exist.
   *
   *  @param  n        The number of the occurence of the wanted string that
   *                   is being searched for
   *  @param  wanted   The string to search for
   *  @param  s        The string to search in
   *
   *  @return The index of the first character of the "nth" occurence of the 
   *          wanted string in the specified string s, if there is an "nth"
   *          occurence, -1 otherwise.
   */

  public static int nth_index_of( int n, String wanted, String s )
  {
    if( n <= 0 )
      return -1;

    if( n > s.length() - wanted.length() )
      return -1;

    int count = 0;
    int k = s.indexOf(wanted);
    while ( k >= 0 )
    {
      count++;
      if( count == n )
        return k;

       k = s.indexOf( wanted, k+1 );
     }
     return -1;
  }




/* ---------------------------------------------------------------------------
 *
 *  Main program for test purposes.
 *
 */
  public static void main(String[] args)
  {
    String s       = "-D   /home/dennis/ -Llog.txt -D/usr/data/";
    String command = "-D";
    int    n       = 2;
    System.out.println("In string " + s + " with command " + command + " " + n);
    System.out.println( getCommand( n, command, s ) );

    n       = 1;
    System.out.println("In string " + s + " with command " + command + " " + n);
    System.out.println( getCommand( n, command, s ) );

    command = "-L";
    System.out.println("In string " + s + " with command " + command + " " + n);
    System.out.println( getCommand( n, command, s ) );

    n = 0;
    System.out.println("In string " + s + " with command " + command + " " + n);
    System.out.println( getCommand( n, command, s ) );

    n = -1;
    System.out.println("In string " + s + " with command " + command + " " + n);
    System.out.println( getCommand( n, command, s ) );

    n = 2;
    System.out.println("In string " + s + " with command " + command + " " + n);
    System.out.println( getCommand( n, command, s ) );

    n = 300;
    System.out.println("In string " + s + " with command " + command + " " + n);
    System.out.println( getCommand( n, command, s ) );

    command = "-D";
    n = 2;
    System.out.println(" with command " + command + " " + n);
    System.out.println("On command line, found");
    System.out.println( getCommand( n, command, args ) );


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
