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
 *  Revision 1.1  2006/06/29 22:54:49  dennis
 *  This IStringFilter class will only allow Strings that specify
 *  materials in a form like "H_2,O".
 *
 *
 */

package gov.anl.ipns.Parameters;


/**
 *  This filter checks whether or not a String is a valid String that 
 *  could occur while entering a material, using a simple syntax of the
 *  form: C,H,F_3,O_3,S.
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

    String parts[] = temp.split(",");             // split into element groups

    for ( int i = 0; i < parts.length; i++ )
    {
      String comp[] = parts[i].split("_");        // split into element, number
      if ( comp.length > 2 || comp.length < 1 )
        return false;

      String element = comp[0];
      if ( element.length() < 1 )
        return false;

      if ( !Character.isUpperCase(element.charAt(0)) )
        return false;

      for ( int k = 1; k < element.length(); k++ )
        if ( !Character.isLowerCase(element.charAt(k)) )
          return false;

      if ( comp.length > 1 )                      // the value must be a number
      try
      {
        float val = Float.parseFloat( comp[1] );
      }
      catch ( NumberFormatException e )
      {
        return false;
      }
    }

    return true;                                 // we made it through all
  }                                              // element groups
 
}
