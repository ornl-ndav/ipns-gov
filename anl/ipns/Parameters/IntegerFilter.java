/*
 * File:  IntegerFilter.java
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
 *  Revision 1.3  2006/06/30 14:25:29  dennis
 *  Removed unused variable.
 *
 *  Revision 1.2  2006/06/29 22:51:44  dennis
 *  Minor fix for javadocs.
 *
 *  Revision 1.1  2006/06/28 21:33:04  dennis
 *  This class implements IStringFilter.  The isOkay() method returns
 *  true only if the String is of a form that might be created while
 *  typing in an integer.  For example, the Strings "-", "-12" are ok
 *  but the Strings "-12." and "Fred" are not ok.
 *
 */

package gov.anl.ipns.Parameters;


/**
 *  This filter checks whether or not a String is a valid String that 
 *  could occur while entering an integer value.  
 */

public class IntegerFilter implements IStringFilter
{

  /**
   * Check if the specified String could occur while entering an integer 
   * value.  This is almost the same as checking whether or not the String 
   * represents a valid integer value, EXCEPT partial entries, like just a 
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

    if ( temp.length() == 0 )         // we allow just a leading sign while
      return true;                    // entering an integer

    else
    {
      try
      {
        Integer.parseInt( temp );
        return true;
      }
      catch ( NumberFormatException e )
      {
        return false;
      }
    }
  }
 
}
