/*
 * File:  IStringFilter.java
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
 *  Revision 1.2  2006/06/29 22:51:44  dennis
 *  Minor fix for javadocs.
 *
 *  Revision 1.1  2006/06/28 21:30:31  dennis
 *  Interface for classes that check whether or not a String
 *  has a particular form.  There is only one method, isOkay()
 *  that returns true if the String has the correct form
 *  and false otherwise.
 *
 */

package gov.anl.ipns.Parameters;


/**
 *  This is the interface that must be implemented by classes that 
 *  check whether or not a String has a valid form for a particular type
 *  of data entry.
 */

public interface IStringFilter 
{

  /**
   *  Check whether or not the specified string is a valid String for entering
   *  data of a particular type.
   *
   *  @param  str  The String to check 
   *
   * @return true if the String is ok, false otherwise
   */
  public boolean isOkay( String str );
 
}
