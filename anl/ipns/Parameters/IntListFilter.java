/*
 * File:  IntListFilter.java
 *
 * Copyright (C) 2006, Dennis Mikkelson
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2006/06/30 14:21:58  dennis
 *  Removed unused variable.
 *
 *  Revision 1.2  2006/06/29 22:51:44  dennis
 *  Minor fix for javadocs.
 *
 *  Revision 1.1  2006/06/29 20:09:25  dennis
 *  This filter only accepts Strings needed while specifying an
 *  increasing sequence of integers, separated by "," and ":".
 *
 *
 */
package gov.anl.ipns.Parameters;

/**
 *  This filter checks whether or not a String is a valid String that 
 *  could occur while entering an IntListString.
 */

public class IntListFilter implements IStringFilter
{
  /**
   * Check if the specified String could occur while entering an IntListString. 
   *
   * @param  str  The String to check 
   *
   * @return true if the String is ok, false otherwise

   */
  public boolean isOkay( String str )
  {
    String temp = str.trim();

    if ( temp.length() == 0 )                            // empty string OK
      return true;

    if ( temp.length() == 1 )                            // allow single + or -
    {
      char first_char = temp.charAt( 0 ); 
      if ( first_char == '+' || first_char == '-' )
        return true; 
    }
                                                         // discard leading 
                                                         // delimiter
    if ( temp.startsWith(":") || temp.startsWith(",") )
      return false; 

                                                         // make sure we don't
                                                         // have adjacent : & ,
    if ( temp.length() > 1 )
    {
      for ( int i = 0; i < temp.length() - 1; i++ )
      {
        char ch      = temp.charAt(i);
        char next_ch = temp.charAt(i+1);
        if ( ch == ':' || ch == ',' )                   
          if ( next_ch == ':' || next_ch == ',' )        // two delims in a row
            return false; 
      }
    }

                                                         // make sure we don't
                                                         // have two colons  
    boolean colon_found = false;                         // without a comma 
    for ( int i = 0; i < temp.length(); i++ )            // between
    {
      char ch = temp.charAt(i);
      if ( ch == ':' )
      {
        if ( colon_found )                               // two succesive ":"
          return false;   
        colon_found = true;
      }
      else if ( ch == ',' )
        colon_found = false;
    }

                                                         // delete trailing sign
    char last_char = temp.charAt( temp.length()-1 ); 
    if ( last_char == '+' || last_char == '-' )
    {
      temp = temp.substring( 0, temp.length()-1 );
      last_char = temp.charAt( temp.length()-1 );
    }
                                                         // make sure that all
                                                         // non-tokens are ints
                                                         // and the ints are
                                                         // increasing
    boolean ends_with_delimiter = false;
    if ( last_char == ':' || last_char == ',' )
      ends_with_delimiter = true;

    int last_val = Integer.MIN_VALUE;
    String parts[] = temp.split( "[,:]" );
    for ( int i = 0; i < parts.length; i++ )
    {
      try
      {
        int val = Integer.parseInt( parts[i] );
 
        if ( i < parts.length - 1 || ends_with_delimiter ) // The last int may
        {                                                  // NOT be complete 
          if ( val < last_val )                        
            return false;
        }
        last_val = val;
      }
      catch ( NumberFormatException e )
      {
        return false;                                    // some part was
      }                                                  // not an integer
    }

    return true;
  }

}
