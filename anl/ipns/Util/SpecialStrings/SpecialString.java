/*
 * File: SpecialString.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 *  Revision 1.7  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 */

package DataSetTools.util;

import java.io.*;

/**
 * The SpecialString class is an abstract base class for special purpose
 * strings whose class is used to distinguish their functionality.  For 
 * example, ErrorString and AttributeString are derived from this SpecialString
 * class and used for passing special types of parameters and return values
 * to/from DataSetOperators.
 */
public abstract class SpecialString  implements  Serializable 
{
  private String message;

    /**
     * Constructs a special string with the specified message.
     *
     * @param   message   The string that serves as the value of this 
     *                    SpecialString object.
     */
    public SpecialString( String message )
    {
      this.message = message;
    }


    /**
     * Set the string value of a special string to the specified message.
     *
     * @param   message   The string that to be set as the value of this
     *                    SpecialString object.
     */
    public void setString( String message )
    {
      this.message = message;
    }

    /**
     * Get the String value of this SpecialString object
     *
     * @return The string value for this object
     *
     */
    public String toString()
    {
      return message;
    }
}
