/*
 * File:  StateFileFilter.java
 *
 * Copyright (C) 2003, Chris M. Bouzek
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
 *           Chris Bouzek <coldfusion78@yahoo.com>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.1  2003/11/18 00:57:31  millermi
 * - Initial Version, created by Chris Bouzek
 * - Added package association.
 *
 *
 */
package DataSetTools.components.View;

import DataSetTools.util.RobustFileFilter;

/**
 * Filters out .isv (State) files.
 */
public class StateFileFilter extends RobustFileFilter
{
  /**
   *  Default constructor.  Calls the super constructor,
   *  sets the description, and sets the file extensions.
   */
  public StateFileFilter()
  {
    super();
    super.setDescription("State File (*.isv)");
    super.addExtension(".isv");
  }
}
