/*
 * File:  FloatFilter.java
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
 *  Revision 1.5  2006/06/30 16:48:25  dennis
 *  Minor format clean up.
 *
 *  Revision 1.4  2006/06/30 16:42:18  rmikk
 *  Fixed error that allowed +.e3 to be accepted
 *
 *  Revision 1.3  2006/06/30 14:25:29  dennis
 *  Removed unused variable.
 *
 *  Revision 1.2  2006/06/29 22:51:44  dennis
 *  Minor fix for javadocs.
 *
 *  Revision 1.1  2006/06/28 22:31:42  dennis
 *  This calss implements IStringFilter.  The isOkay() method returns
 *  true only if hte String is a form that might be created while
 *  typing in a float.  For example, the Strings "-", "+1.2E-" are ok
 *  but the Strings "+-" and "1.2easy" are not.
 *
 */

package gov.anl.ipns.Parameters;


/**
 *  This filter checks whether or not a String is a valid String that 
 *  could occur while entering a float value.  
 */

public class FloatFilter implements IStringFilter
{
  /**
   * Check if the specified String could occur while entering a float 
   * value.  This is almost the same as checking whether or not the String 
   * represents a valid float value, EXCEPT partial entries, like just a 
   * leading "-" are accepted.
   *
   * @param  str  The String to check 
   *
   * @return true if the String is ok, false otherwise
   */
  public boolean isOkay( String str )
  {
    String temp = str.trim();

    if ( temp.startsWith( "+" ) || temp.startsWith( "-" ) )
      temp = temp.substring(1);

    if ( temp.length() == 0 )                 // we allow just a leading sign
      return true;                            // while entering a float 

    if ( temp.startsWith( "+" ) || temp.startsWith( "-" ) )  // only one sign
      return false;                                          // char allowed

    if ( temp.endsWith( "." ) )               // we allow just a leading 
      temp =  temp+ "0";                      // decimal point while entering
                                              // a float

    if ( temp.endsWith( "+" ) || temp.endsWith( "-" ) ||    // might end with
         temp.endsWith( "e" ) || temp.endsWith( "E" )  )    // E-  or +, etc
      temp = temp + "0";                                    // while entering
    
    try
    { 
       Float.parseFloat( temp );
      return true;
    }
    catch ( NumberFormatException e )
    {
      return false;
    }
  }
 
}
