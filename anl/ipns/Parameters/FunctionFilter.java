/*
 * File:  FunctionFilter.java
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
 *  Revision 1.1  2006/06/29 20:15:53  dennis
 *  This filter is intended to assist in preventing errors while
 *  entering a String describing a mathematical expression.  Currently
 *  this filter always returns true.  It is essentially just a stub
 *  to be filled in later.
 *
 *
 */

package gov.anl.ipns.Parameters;


/**
 *  This filter checks whether or not a String is a valid String that 
 *  could occur while entering a function.  NOTE: Currently this just
 *  always returns true.
 */

public class FunctionFilter implements IStringFilter
{

  /**
   * Check if the specified String could occur while entering a function. 
   *
   * @param  str  The string to check 
   */
  public boolean isOkay( String str )
  {
    return true;
  }

}
