/**
 * File:  Format.java
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
 *  Revision 1.3  2002/07/16 22:15:55  pfpeterson
 *  Added new formatting methods for Strings and StringBuffers.
 *
 *  Revision 1.2  2001/04/25 22:24:22  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.1  2000/07/13 22:25:22  dennis
 *  Convenient utilities for formatting numbers with a specific string length
 *
 */ 

package DataSetTools.util;

import java.text.*;

/**
 *  Provide convenient text formatting for numeric values that includes string
 *  length control.
 */

public class Format 
{
  static NumberFormat f =  NumberFormat.getInstance();

  /*
   * Don't instantiate this class, just use the methods provided.
   */
  private Format() {}


  /**
   *  Format a real number into a string.
   *
   *  @param num           The number to format
   *  @param field_width   The total number of spaces to be used for the number.
   *                       If the formatted number does not occupy all of the
   *                       spaces, leading blanks will be prepended.
   *                       If more spaces are needed, they will be used.
   *  @param num_digits    The number of digits to use after the decimal point.
   *  @param use_grouping  Flag indicating whether or not a grouping symbol is
   *                       used between groups of three digits. 
   *
   *  @return  A string containing the formatted number with at least the 
   *           specified number of characters.  
   */

  static public String real( double   num, 
                             int      field_width, 
                             int      num_digits, 
                             boolean  use_grouping )
  {
    if(num_digits>0)
        f.setMinimumFractionDigits( 1 );
    else
        f.setMinimumFractionDigits( 0 );
    f.setMaximumFractionDigits( num_digits );
    f.setGroupingUsed( use_grouping );

    return string(f.format(num),field_width,true);
  }

  /**
   *  Format a real number into a string with no grouping symbol.
   *
   *  @param num           The number to format
   *  @param field_width   The total number of spaces to be used for the number.   *                       If the formatted number does not occupy all of the
   *                       spaces, leading blanks will be prepended.
   *                       If more spaces are needed, they will be used.
   *  @param num_digits    The number of digits to use after the decimal point.
   *
   *  @return  A string containing the formatted number with at least the
   *           specified number of characters.
   */

  static public String real( double   num,
                             int      field_width,
                             int      num_digits )
  {
    return real( num, field_width, num_digits, false );    
  }


  /**
   *  Format an integer into a string.
   *
   *  @param num           The number to format
   *  @param field_width   The total number of spaces to be used for the number.   *                       If the formatted number does not occupy all of the
   *                       spaces, leading blanks will be prepended.
   *                       If more spaces are needed, they will be used.
   *  @param use_grouping  Flag indicating whether or not a grouping symbol is
   *                       used between groups of three digits.
   *
   *  @return  A string containing the formatted number with at least the 
   *           specified number of characters.  
   */

  public static String integer( double   num, 
                                int      field_width, 
                                boolean  use_grouping )
  { 
    return real( num, field_width, 0, use_grouping );    
  }

  /** 
   *  Format an integer into a string with no grouping symbols
   *  
   *  @param num           The number to format
   *  @param field_width   The total number of spaces to be used for the number.   *                       If the formatted number does not occupy all of the
   *                       spaces, leading blanks will be prepended.
   *                       If more spaces are needed, they will be used.
   *  
   *  @return  A string containing the formatted number with at least the
   *           specified number of characters.
   */

  public static String integer( double   num,
                                int      field_width )
  {
    return real( num, field_width, 0, false );   
  }

  /**
   * Pad a StringBuffer on the appropriate side.
   *
   * @param val         The StringBuffer to format.
   * @param field_width The total number of spaces to be used for
   *                    the StringBuffer. If the formatted
   *                    StringBuffer is larger than the number of
   *                    spaces provided then the will be used.
   * @param pad_left    Whether to pad with spaces on the left or
   *                    right.
   *
   * @return A string containing at least the specified number of
   * characters.
   */
  public static String string(StringBuffer val, int field_width,
                              boolean pad_left){
      while( val.length()<field_width ){
          if(pad_left)
              val.insert(0," ");
          else
              val.append(" ");
      }
      
      return val.toString();
  }

  /**
   * Pad a String on the appropriate side.
   *
   * @param val         The String to format.
   * @param field_width The total number of spaces to be used for
   *                    the String. If the formatted String is larger
   *                    than the number of spaces provided then the
   *                    will be used.
   * @param pad_left    Whether to pad with spaces on the left or
   *                    right.
   *
   * @return A string containing at least the specified number of
   * characters.
   */
  public static String string(String val, int field_width, boolean pad_left){
      return string(new StringBuffer(val),field_width,pad_left);
  }

  /**
   * Pad a StringBuffer on the appropriate side.
   *
   * @param val         The StringBuffer to format.
   * @param field_width The total number of spaces to be used for
   *                    the StringBuffer. If the string is too short,
   *                    then more spaces will be prepended. If the
   *                    formatted StringBuffer is larger than the
   *                    number of spaces provided then the will be
   *                    used.
   *
   * @return A string containing at least the specified number of
   * characters.
   */
  public static String string(StringBuffer val, int field_width ){
      return string(val,field_width,true);
  }

  /**
   * Pad a String on the appropriate side.
   *
   * @param val         The String to format.
   * @param field_width The total number of spaces to be used for
   *                    the String. If the string is too short, then
   *                    more spaces will be prepended. If the
   *                    formatted String is larger than the number of
   *                    spaces provided then the will be used.
   *
   * @return A string containing at least the specified number of
   * characters.
   */
  public static String string(String val, int field_width ){
      return string(new StringBuffer(val),field_width,true);
  }

  /**
   *  Main program for testing purposes only.
   */
  public static void main( String argv[] )
  {
    System.out.println("Real Formmated 12345.678 = "+ 
                       Format.real( 12345.678, 12, 1, true ) );
    System.out.println("Integer Formmated 12345 = "+ 
                       Format.integer( 12345, 12, false ) );
  }

}
