/**
 * File: DateUtil.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *  Revision 1.2  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 */ 
package DataSetTools.util;

import java.io.*;
import java.util.*;

/**
 *  Provide utilities for handling Dates 
 */

public class DateUtil
{
  /**
   *  Don't instantiate this class
   */
  private DateUtil() {}


 /* ---------------------------- default_string ------------------------- */
 /**
  *  Get the default form of the current date and time as a string.
  *
  *  @return String with the current date and time.
  */
  public static String default_string()
  {
    Date date = new Date( System.currentTimeMillis() );
    return date.toString();
  }

}
