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
 *  Revision 1.9  2003/04/11 14:23:31  dennis
 *  Added methods to format numbers for display as axis labels.(Mike Miller)
 *  doubleEng() uses engineering units (powers are multiples of 3).
 *  choiceFormat() allows selection of format controlled by parameter.
 *  round() rounds values to a specified number of significant digits.
 *  setE() formats number to have a specified exponent.
 *
 *  Revision 1.8  2002/12/03 21:42:41  pfpeterson
 *  Fixed bug where Format.string did not accept null values.
 *
 *  Revision 1.7  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.6  2002/07/25 22:12:21  pfpeterson
 *  Added methods to print 'real' numbers w/o a minimum number
 *  digits past the decimal.
 *
 *  Revision 1.5  2002/07/17 16:53:17  pfpeterson
 *  Changed the MinimumFractionDigits to be equal to the
 *  MaximumFractionDigits.
 *
 *  Revision 1.4  2002/07/16 22:42:16  dennis
 *  Added methods to format values using scientific notation.
 *
 *  Revision 1.3  2002/07/16 22:15:55  pfpeterson
 *  Added new formatting methods for Strings and StringBuffers.
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
  public static final String AUTO       = "0";
  public static final String DECIMAL    = "1";
  public static final String SCIENTIFIC = "2";
  public static final String ENGINEER   = "3";
  
  static NumberFormat  f          =  NumberFormat.getInstance();
  static DecimalFormat sciFormatf = new DecimalFormat( "0.######E0" );
  static DecimalFormat sciFormatd = new DecimalFormat( "0.###############E0" );
  static DecimalFormat engFormatd = new DecimalFormat( "##0.#############E00" );
  /*
   * Don't instantiate this class, just use the methods provided.
   */
  private Format() {}

  /**
   *  Format a real number into a string using engineering notation,
   *  with thirteen digits after the decimal, appropriate for double precision
   *  values.
   *
   *  @param  num          The number to format
   *  @param  field_width  The total number of spaces to be used for the number.   *                       If the formatted number does not occupy all of the
   *                       spaces, leading blanks will be prepended.
   *                       If more spaces are needed, they will be used.
   *
   *  @return A string containing the formatted number with at least the
   *          specified number of characters.
   */ 
   static public String doubleEng( double   num,
                                   int      field_width )
   {
      String result = engFormatd.format( num );
      return string( result, field_width, true );
   }

  /**
   *  Format a real number into a string.
   *
   *  @param  num           The number to format
   *  @param  form          The specified format for displaying num
   *  @param  sig_digits    Significant digits to round and display number.
   *
   *  @return A string containing the formatted number with at least the 
   *          specified number of characters.  
   */ 
   static public String choiceFormat( double num, String form, 
                                      int sig_digits )
   {
      num = round(num,sig_digits); 
      
      if( form == AUTO )
      {
         if( Math.abs(num) >= 10000 || Math.abs(num) < .001 )
	    return doubleEng( num, sig_digits );
         else 
	    return real(num, sig_digits);
      }
      if( form == DECIMAL )
         return real(num, sig_digits);
      if( form == SCIENTIFIC )
         return doubleExp( num, sig_digits );
      return doubleEng( num, sig_digits );      
   }
  
  /**
   *  Format a real number into a string. 
   *  Calls the choiceFormat( double, String, int ) with sig_digits = 4
   *
   *  @param  num           The number to format
   *  @param  form          The specified format for displaying num
   *
   *  @return A string containing the formatted number with at least the 
   *          specified number of characters.  
   */
   static public String choiceFormat( double num, String form )
   {
      return choiceFormat(num, form, 4);
   }

  /**
   *  Format a real number into a string. 
   *  Calls the choiceFormat( double, String, int ) 
   *  with form = AUTO and sig_digits = 4.
   *
   *  @param  num           The number to format
   *
   *  @return A string containing the formatted number with at least the 
   *          specified number of characters.  
   */
   static public String choiceFormat( double num )
   {
      return choiceFormat( num, AUTO, 4 );
   }
  
  /**
   *  Round a real number to the significant digits. This round() method
   *  will preserve decimal values. 
   *  NOTE: Because a double is returned, if the number is rounded to an
   *        integer, the number will still contain a decimal and zero.
   *        EX. round(100.5,3) = 100.0
   *
   *  @param  num           The number to format
   *  @param  sig_dig       The significant digits to round the number
   *
   *  @return A double rounded to the specified length.  
   */ 
   static public double round( double num, int sig_dig )
   {
      int numex = 0;
      // figure out the degree of num
      while ( Math.abs(num) >= 10.0 )
      { 
	 num = num / 10.0f;
	 numex = numex + 1;
      }
      while ( Math.abs(num) < 1.0 && num != 0 )
      {
	 num = num * 10.0f;
	 numex = numex - 1;
      }
      num = num*Math.pow(10.0,sig_dig - 1);
      num = Math.round(num);
      num = num/Math.pow(10.0,sig_dig - 1 - numex);
      	  
      return num;
   }

  /**
   *  Format a real number into a string number with a specified exponent. 
   *
   *  @param  num           The number to format
   *  @param  at_exp        The exponent the number will be converted to.
   *  @param  sig_dig       Significant digits to round the number.
   *
   *  @return A string containing the formatted number with at least the 
   *          specified number of characters.  
   */   
   static public String setE( double num, int at_exp, int sig_dig )
   {
      int numex = 0;
      // figure out the degree of num
      while ( Math.abs(num) >= 10.0 )
      { 
	 num = num / 10.0;
	 numex = numex + 1;
      }
      while ( Math.abs(num) < 1.0 && num != 0 )
      {
	 num = num * 10.0;
	 numex = numex - 1;
      }
      
      // if numex != at_exp, change until it is.
      int exp = numex;
      while( exp != at_exp )
      {
         if( exp < at_exp )
	 {
	    exp++;
	    num = num/10;
	 }
	 if(exp > at_exp )
	 {
	    exp--;
	    num = num * 10;
	 }   
      }
      num = round(num, sig_dig);
      String snum = Double.toString(num);
      // String must be at least as long as the number of significant digits.
      // The "+2" takes into account negative sign and decimal.
      while( snum.length() < sig_dig + 2 )
         snum = snum + "0";
      int predecimal = 1 + (numex - at_exp);
      if( predecimal > sig_dig )
      {
         SharedData.addmsg("WARNING -- Specified significant digits not " + 
     			   "sufficient. Number exceeds significant digits.");
         sig_dig = predecimal;
      }
      // take into account the negative sign
      if( num < 0 )
         sig_dig++;
      if( predecimal - sig_dig != 0 )
         snum = snum.substring(0, sig_dig + 1);
      else
         snum = snum.substring(0, sig_dig);      
      snum = snum + "E" + Integer.toString(at_exp);
      return snum;
   }

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
    f.setMinimumFractionDigits( num_digits );
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
   *  Format a real number into a string.
   *
   *  @param num           The number to format
   *  @param field_width   The total number of spaces to be used for the
   *                       number.  If the formatted number does not
   *                       occupy all of the spaces, leading blanks
   *                       will be prepended.  If more spaces are
   *                       needed, they will be used.
   *
   *  @return  A string containing the formatted number with at least the 
   *           specified number of characters.  
   */

  static public String real( double   num, int field_width ){
      f.setMinimumFractionDigits( 0 );
      int index=Double.toString(num).indexOf(".");
      f.setMaximumFractionDigits( field_width-index-1 );
      f.setGroupingUsed( false );

      return string(f.format(num),field_width,true);
  }

  /**
   *  Format a real number into a string using scientific notation,
   *  with six digits after the decimal, appropriate for single precision
   *  values. 
   *
   *  @param num           The number to format
   *  @param field_width   The total number of spaces to be used for the number.   
   *                       If the formatted number does not occupy all of the
   *                       spaces, leading blanks will be prepended.
   *                       If more spaces are needed, they will be used.
   *
   *  @return  A string containing the formatted number with at least the
   *           specified number of characters.
   */

  static public String singleExp( double   num,
                                  int      field_width )
  {
    String result = sciFormatf.format( num );
    return string( result, field_width, true );
  }


  /**
   *  Format a real number into a string using scientific notation,
   *  with fifteen digits after the decimal, appropriate for double precision
   *  values.
   *
   *  @param num           The number to format
   *  @param field_width   The total number of spaces to be used for the number.   *                       If the formatted number does not occupy all of the
   *                       spaces, leading blanks will be prepended.
   *                       If more spaces are needed, they will be used.
   *
   *  @return  A string containing the formatted number with at least the
   *           specified number of characters.
   */

  static public String doubleExp( double   num,
                                  int      field_width )
  {
    String result = sciFormatd.format( num );
    return string( result, field_width, true );
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
      if(val==null) val=new StringBuffer("");
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
    if(val==null)
      return string(new StringBuffer(""),field_width,pad_left);
    else
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
    if(val==null)
      return string(new StringBuffer(""),field_width,true);
    else
      return string(new StringBuffer(val),field_width,true);
  }

  /**
   *  Main program for testing purposes only.
   */
  public static void main( String argv[] )
  {
    System.out.println("Real Formatted 12345.678 = "+ 
                       Format.real( 12345.678, 12, 1, true ) );
    System.out.println("Integer Formatted 12345 = "+ 
                       Format.integer( 12345, 12, false ) );                        
    System.out.println("Engineer Format: .001234567 = "+ 
                        Format.choiceFormat( .001234567, ENGINEER, 6) );
    System.out.println("Decimal Format: 1234567800 = "+ 
                        Format.choiceFormat( 1234567800, DECIMAL, 4 ) );       
    System.out.println("setE: 1234567800 = "+ 
                        Format.setE( 1234567800, 8, 5 ) );
    System.out.println("setE: 1234567800 = "+ 
                        Format.setE( 1234567800, 6, 6 ) );
  }

}
