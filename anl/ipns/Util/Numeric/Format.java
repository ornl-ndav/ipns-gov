/**
 *  @(#)Format.java   2000/07/13  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.1  2000/07/13 22:25:22  dennis
 *  Convenient utilities for formatting numbers with a specific string length
 *
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

  /**
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
    f.setMaximumFractionDigits( num_digits );
    f.setGroupingUsed( use_grouping );

    String result = f.format( num );

    while ( result.length() < field_width )
      result = " " + result;

    return result;
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
