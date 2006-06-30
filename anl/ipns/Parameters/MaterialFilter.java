/*
 * File:  MaterialFilter.java
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
 *  Revision 1.2  2006/06/30 14:21:17  dennis
 *  Now traps a few more problems like starting with ',' or '_',
 *  having repeated separator characters, and using exponential notation
 *  or floating point suffixes like 'F' or 'D'.
 *
 *  Revision 1.1  2006/06/29 22:54:49  dennis
 *  This IStringFilter class will only allow Strings that specify
 *  materials in a form like "H_2,O".
 */

package gov.anl.ipns.Parameters;


/**
 *  This filter checks whether or not a String is a valid String that 
 *  could occur while entering a material, using a simple syntax of the
 *  form: C,H,F_3,O_3,S.  It is not absolutely effective, but just traps 
 *  some basic mistakes.
 */

public class MaterialFilter implements IStringFilter
{

  /**
   * Check if the specified String could occur while entering a material.
   *
   * @param  str  The String to check 
   *
   * @return true if the String is ok, false otherwise
   */
  public boolean isOkay( String str )
  {
    String temp = str.trim();

    if ( temp.length() == 0 )                     // we allow an empty string
      return true;

    for ( int i = 0; i < temp.length() - 1; i++ ) // don't allow repeated ,_
    {
      char ch      = temp.charAt(i);
      char next_ch = temp.charAt(i+1);
      if ( ch == ',' || ch == '_' )
        if ( next_ch == ',' || next_ch == '_' )
          return false;

      if ( i == 0 && (ch == ',' || ch == '_') )   // can't start with , or _
        return false;
    }

    String parts[] = temp.split(",");             // split into element groups

    for ( int i = 0; i < parts.length; i++ )
    {
      String comp[] = parts[i].split("_");        // split into element, number
      if ( comp.length > 2 || comp.length < 1 )
        return false;

      String element = comp[0];   
      if ( element.length() < 1 )
        return false;

      if ( !Character.isUpperCase(element.charAt(0)) )   // first letter must
        return false;                                    // be upper case

      for ( int k = 1; k < element.length(); k++ )
        if ( !Character.isLowerCase(element.charAt(k)) ) // later letters must
          return false;                                  // be lower case

      if ( comp.length > 1 )                      // next value must be a number
      {
        String number = comp[1];
        try
        {
          Float.parseFloat( number );
        }
        catch ( NumberFormatException e )
        {
          return false;
        }

        for ( int k = 1; k < number.length(); k++ )     // don't allow letters
        {                                               // in number(eg. E,F,D)
          char ch = number.charAt(k);
          if ( !Character.isDigit( ch ) && ( ch != '.' ) )
            return false; 
        }
      }
    }

    return true;                                 // we made it through all
  }                                              // element groups
 
}
