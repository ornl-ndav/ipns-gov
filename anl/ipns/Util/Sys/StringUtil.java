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
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.17  2003/06/17 15:28:58  pfpeterson
 *  Added split method for breaking a string into a string[].
 *
 *  Revision 1.16  2003/02/13 20:58:11  pfpeterson
 *  Deprecated fixSeparator and renamed the method to setFileSeparator.
 *
 *  Revision 1.15  2003/02/06 16:20:46  pfpeterson
 *  Fixed small bug in getBoolean(StringBuffer) which did not remove
 *  the parsed portion of the StringBuffer.
 *
 *  Revision 1.14  2003/02/06 15:22:04  pfpeterson
 *  Pulled out the functionality to determine the next space for StringBuffers
 *  into a private method. Also now understands tabs as spaces.
 *
 *  Revision 1.13  2003/02/05 19:30:26  pfpeterson
 *  Added methods to get a boolean value from a StringBuffer, updated
 *  some documentation, and made getFloat and getInt explicitly throw
 *  NumberFormatException (which does not need to be caught).
 *
 *  Revision 1.12  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.11  2002/09/25 16:46:58  pfpeterson
 *  Fixed a bug in getFloat(sb) and getInt(sb) where negative numbers
 *  where not considered numbers.
 *
 *  Revision 1.10  2002/08/15 18:45:09  pfpeterson
 *  Now replaces "//" in fixSeparator as well.
 *
 *  Revision 1.9  2002/08/12 20:23:59  pfpeterson
 *  Returned the fixSeparator method to its original purpose. Last
 *  version caused a bug that made runfiles not load under windows.
 *
 *  Revision 1.8  2002/08/12 18:51:47  pfpeterson
 *  fixSeparator now points at FilenameUtil.fixSeparator. Also, updated
 *  the documentation to reflect what fixSeparator actually does.
 *
 *  Revision 1.7  2002/08/06 21:22:31  pfpeterson
 *  Added methods to get fun things like floats, ints and strings
 *  out of a StringBuffer.
 *
 *  Revision 1.6  2002/04/17 21:33:10  dennis
 *  Made replace() method more robust.  Now works even if the
 *  replaced string is contained in the new string.
 *  Added method replace_token() that only makes replacements if
 *  the string is bordered by non-alphanumeric characters.
 *  Added method extract_tokens() that places separate tokens that
 *  occur in a String into an array of Strings.
 *
 */ 
package DataSetTools.util;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 *  Provide utilities for fixing file names, parsing command lines, etc.
 */

public class StringUtil
{
  /**
   *  Don't instantiate this class.
   */
  private StringUtil() {}


  /* ---------------------------- fixSeparator --------------------------- */
  /**
   *  Replace all occurrences of the possible file separators "/", "\", "\\" with the file separator needed for the local system.
   *
   *  @param  file_name  A file name string possibly containing improper
   *                     separators.
   *
   *  @return  A string containing the file name with all separators replaced 
   *           by system dependent separtator.
   *
   *  @deprecated  replaced by {@link #setFileSeparator(String)}
   */
  public static String fixSeparator( String file_name )
  {
    return setFileSeparator(file_name);
  }

  /**
   *  Replace all occurrences of the possible file separators "/",
   *  "\", "\\" with the file separator needed for the local system.
   *
   *  @param  file_name  A file name string possibly containing improper
   *                     separators.
   *
   *  @return  A string containing the file name with all separators replaced 
   *           by system dependent separtator.
   */
  public static String setFileSeparator( String file_name )
  {
      String separator=File.separator;
      String result;
      
      result = replace( file_name, "\\\\", separator);
      result = replace( result,    "\\",   separator);
      result = replace( result,    "//",   separator);
      result = replace( result,    "/",    separator);

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

    int from_index = 0; 
    while ( result.indexOf( old_chars, from_index ) >= 0 )
    {
      start = result.indexOf( old_chars, from_index );
      result = result.substring(0, start) + 
               new_chars + 
               result.substring( start + old_chars.length() );
      from_index = start + new_chars.length();
    }
    return result;
  }


  /* ---------------------------- replace_token --------------------------- */
  /**
   *  Replace all occurrences of a specified string by another string, provide
   *  the occurrence of the string is bordered by non-alphanumeric characters
   *  or by the ends of the string.  This is to allow replacing occurrence of
   *  variables such as t, t0, t1, etc. in expressions by other tokens.  If
   *  the adjacent characters are not checked, just replacing all "t"s  by "x"
   *  would damage "t0" and "t1". 
   *
   *  @param   in_string  the string in which the replacement is to be
   *                      made.
   *
   *  @param   old_chars  the string which is to be replaced.
   *
   *  @param   new_chars  the string that replaces old_chars.
   *
   *  @return  A new string in which bounded occurences of the old_chars string
   *           are replaced by the new_chars string.
   */
  public static String replace_token( String in_string,
                                      String old_chars,
                                      String new_chars )
  {
    if (in_string == null || old_chars == null || new_chars == null )
      return null;

    if ( old_chars.equals( new_chars ) )
      return in_string;

    int    start;
    char   right_border;
    char   left_border;
    String result = in_string;

    int from_index = 0; 
    while ( result.indexOf( old_chars, from_index ) >= 0 )
    {

      start        = result.indexOf( old_chars, from_index );

      if ( start > 0 )
        left_border  = result.charAt( start-1 );
      else 
        left_border = '$'; 

      if ( (start + old_chars.length()) < result.length() )
        right_border = result.charAt( start + old_chars.length() );
      else
        right_border = '$'; 

      if ( !Character.isLetterOrDigit( left_border ) && 
           !Character.isLetterOrDigit( right_border)  )
        {
          result = result.substring(0, start) +
                   new_chars +
                   result.substring( start + old_chars.length() );
          from_index = start + new_chars.length();
        }
        else
          from_index = start + old_chars.length();
    }
    return result;
  }


  /* -------------------------- extract_tokens --------------------------- */
  /**
   *  Produce an array of string "tokens" that are present in the specified
   *  string and separated by the specified delimiters.
   *
   *  @param   in_string   the string from which the tokens are to be extracted
   *
   *  @param   delimiters  the string containing the delimiters, 
   *                       eg: " ,;:\t\n\r\f"
   *
   *  @return  An array of strings containing the tokens 
   */
  public static String[] extract_tokens( String in_string,
                                         String delimiters )
  {
    StringTokenizer tokenizer = new StringTokenizer( in_string, delimiters );
    Vector tokens = new Vector();
    String token;

    while (tokenizer.hasMoreTokens())
      tokens.add( tokenizer.nextToken() );

    String list[] = new String[ tokens.size() ];
    for ( int i = 0; i < list.length; i++ )
      list[i] = (String)tokens.elementAt(i);

    return list; 
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

  /* --------------------------- commandPresent -------------------------- */
  /**
   *  Determine whether or not the specified string occurs in the array 
   *  of strings.
   *
   *  @param  args  The array of strings containing commands and arguments
   *                with space between successive command-argument pairs or
   *                individual commands without arguments
   */
  public static boolean commandPresent( String command, String args[] )
  {
    if ( args == null || args.length == 0 )
      return false;
  
    String s = args[0];
    for ( int i = 1; i < args.length; i++ )
      s += " " + args[i];

    if ( s.indexOf( command ) >= 0 )
      return true;
    else
      return false;
      
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

  /**
   * Method for spliting strings. Defaults to " " if second parameter
   * is null.
   */
  public static String[] split(String string, String spliter){
    if(string==null || string.length()<=0)
        return null;
      
    if(spliter==null) spliter=" ";
    int spliter_length=spliter.length();

    int count=1;
    int index=string.indexOf(spliter);
    while(index>0){
      count++;
      index=string.indexOf(spliter,index+spliter_length);
    }
    String[] list=new String[count];
    
    int start=0;
    int end=string.indexOf(spliter);
    if(end<=start) end=string.length();
    for( int i=0 ; i<count ; i++ ){
      list[i]=string.substring(start,end);
      start=end+spliter_length;
      end=string.indexOf(spliter,start);
      if(end<=start) end=string.length();
    }
    
    return list;
  }

  /**
   * Method analogous to String.trim().
   */
  public static void trim(StringBuffer sb){
      // don't try anything if it is an empty string
      if(sb.length()<=0) return;

      // trim leading whitespace
      while(Character.isWhitespace(sb.charAt(0))){
          sb.deleteCharAt(0);
      }
      // trim trailing whitespace
      while(Character.isWhitespace(sb.charAt(sb.length()-1))){
          sb.deleteCharAt(sb.length()-1);
      }
  }  

  /**
   * Method to get the first set of non-whitespace characters and
   * construct a float. The characters (and any separating whitespace)
   * are then removed from the StringBuffer.
   *
   * @throws NumberFormatException when there is a problem converting
   * the portion of the StringBuffer into a Float. This will not
   * remove that part from the StringBuffer.
   */
  public static float getFloat(StringBuffer sb) throws NumberFormatException{
      float val=0f;
      if(sb.length()<=0) return val; // don't bother if we have an empty string
      
      Float temp=null;
      int end=getSpace(sb);
      if(end>0){
        try{
          temp=Float.valueOf(sb.substring(0,end));
        }catch(NumberFormatException e){
          throw e;
        }
        val=temp.floatValue();
        sb.delete(0,end);
        StringUtil.trim(sb);
      }
      return val;
  }

  /**
   * Method to get the first nchar characters and construct a
   * float. The characters are then removed from the StringBuffer.
   *
   * @throws NumberFormatException when there is a problem converting
   * the portion of the StringBuffer into a Float. This will not
   * remove that part from the StringBuffer.
   */
  public static float getFloat(StringBuffer sb, int nchar)
                                                  throws NumberFormatException{
      float val=0f;

      // don't bother if we have an empty string
      if(sb.length()<nchar) return val;
      
      // just grab the next nchar characters and make a float
      String sub=sb.substring(0,nchar).trim();
      sb.delete(0,nchar);
      if(sub==null || sub.length()==0) return val;
      try{
        Float temp=Float.valueOf(sub);
        val=temp.floatValue();
      }catch(NumberFormatException e){
        throw e;
      }
      return val;
  }

  /**
   * Method to get the first set of non-whitespace characters and
   * construct an int. The characters (and any separating whitespace)
   * are then removed from the StringBuffer.
   *
   * @throws NumberFormatException when there is a problem converting
   * the portion of the StringBuffer into an Integer. This will not
   * remove that part from the StringBuffer.
   */
  public static int getInt(StringBuffer sb) throws NumberFormatException{
      int val=0;
      if(sb.length()<=0) return val; // don't bother if we have an empty string
      
      Integer temp=null;
      int end=getSpace(sb);
      
      if(end>0){
        try{
          temp=Integer.valueOf(sb.substring(0,end));
        }catch(NumberFormatException e){
          throw e;
        }
        val=temp.intValue();
        sb.delete(0,end);
        StringUtil.trim(sb);
      }
      return val;
  }

  /**
   * Method to get the first nchar characters and construct an
   * int. The characters are then removed from the StringBuffer.
   *
   * @throws NumberFormatException when there is a problem converting
   * the portion of the StringBuffer into an Integer. This will not
   * remove that part from the StringBuffer.
   */
  public static int getInt(StringBuffer sb, int nchar)
                                                  throws NumberFormatException{
      int val=0;

      // don't bother if we have an empty string
      if(sb.length()<nchar) return val;
      
      // just grab the next nchar characters and make an int
      try{
        Integer temp=Integer.valueOf(sb.substring(0,nchar));
        val=temp.intValue();
      }catch(NumberFormatException e){
        throw e;
      }
      sb.delete(0,nchar);
      return val;
  }

  /**
   * Method to get the first set of non-whitespace characters and
   * construct a boolean. The characters are then removed from the
   * StringBuffer. Only "true" and "false" will be converted and
   * everything else will create an exception. This is a case
   * insensitive method.
   *
   * @throws IllegalArgumentException when there is a problem
   * converting the portion of the StringBuffer into an boolean. This
   * will not remove that part from the StringBuffer.
   */
  public static boolean getBoolean(StringBuffer sb)
                                               throws IllegalArgumentException{
    boolean val=false;
    if(sb.length()<=0) return val; // don't bother if we have an empty string

    int end=getSpace(sb);

    String temp=null;
    if(end>0){
      temp=sb.substring(0,end);
      if(temp==null || temp.length()<=0)
        throw new IllegalArgumentException("Cannot convert empty string to "
                                           +"boolean");

      temp=temp.toLowerCase();
      if(temp.equals("true")){
        val=true;
      }else if(temp.equals("false")){
        val=false;
      }else{
        throw new IllegalArgumentException("Cannot convert string to boolean: "
                                           +temp);
      }
    }
    sb.delete(0,end);
    trim(sb);

    return val;
  }

  /**
   * Method to get the first nchar characters and construct a
   * boolean. The characters are then removed from the
   * StringBuffer. Only "true" and "false" will be converted and
   * everything else will create an exception. This is a case
   * insensitive method.
   *
   * @throws IllegalArgumentException when there is a problem
   * converting the portion of the StringBuffer into an boolean. This
   * will not remove that part from the StringBuffer.
   */
  public static boolean getBoolean(StringBuffer sb, int nchar)
                                               throws IllegalArgumentException{
    boolean val=false;

    // don't bother if we have an empty string
    if(sb.length()<nchar) return val;
      
    // just grab the next nchar characters and make an int
    String temp=sb.substring(0,nchar).trim();
    if( temp==null || temp.length()<=0 )
      throw new IllegalArgumentException("Cannot convert empty string to "
                                         +"boolean");
    temp=temp.toLowerCase();
    if(temp.equals("true")){
      val=true;
    }else if(temp.equals("false")){
      val=false;
    }else{
      throw new IllegalArgumentException("Cannot convert string to boolean: "
                                         +temp);
    }

    sb.delete(0,nchar);
    return val;
  }


  /**
   * Method to get the first set of non-whitespace characters and
   * construct a String. The characters (and any separating
   * whitespace) are then removed from the StringBuffer.
   */
  public static String getString(StringBuffer sb){
      String val=null;
      if(sb.length()<=0) return val; // don't bother if we have an empty string

      int end=getSpace(sb);
      if(end>0){
        val=sb.substring(0,end);
        sb.delete(0,end);
        StringUtil.trim(sb);
      }

      return val;
  }

  /**
   * Method to get the first nchar characters and construct a
   * String. The characters are then removed from the StringBuffer.
   */
  public static String getString(StringBuffer sb, int nchar){
      String val=null;

      // don't bother if we have an empty string
      if(sb.length()<nchar) return val;

      // just grab the next nchar characters
      val=sb.substring(0,nchar);
      sb.delete(0,nchar);
      return val;
  }

  /**
   * Determine when the next bit of whitespace occurs. This currently
   * only checks for " " and "\t".
   */
  private static int getSpace(StringBuffer sb){
    // confirm that we start with non-whitespace
    if(Character.isWhitespace(sb.charAt(0)))  return -1;

    // find all of the good information
    int end=-1;
    int space=sb.toString().indexOf(" ");
    int tab=sb.toString().indexOf("\t");

    // sort out which is the first one to appear
    if(tab>=0 && space>=0){
      if(tab<space)
        end=tab;
      else
        end=space;
    }else if(space>=0){
      end=space;
    }else if(tab>=0){
      end=tab;
    }

    // the must have both been -1 so just go for the length
    if(end<0)
      end=sb.length();

    return end;
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

                                                  // test token replacement
    String equation = "x1*x*x+x2*x+x3";
    System.out.println("equation   = " + equation );
    String equation1 = replace_token( equation, "x", " t " );
    System.out.println("equation 1 = " + equation1 );
    String equation2 = replace_token( equation1, "x1", " a " );
    System.out.println("equation 2 = " + equation2 );
    String equation3 = replace_token( equation2, "x2", " b " );
    System.out.println("equation 3 = " + equation3 );
    String equation4 = replace_token( equation3, "x3", " c " );
    System.out.println("equation 4 = " + equation4 );

    System.out.println("Tokens in: X1, x2	total1;  ");
    String tokens[] = extract_tokens( "X1, x2	total1;  ", " ,;:\t\n\r\f");
    for ( int i = 0; i < tokens.length; i++ )
      System.out.println("|"+tokens[i]+"|");
  }

}
