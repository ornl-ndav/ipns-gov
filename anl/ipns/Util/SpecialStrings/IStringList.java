/*
 * File:  IStringList.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2001/04/25 22:24:33  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.1  2000/11/07 16:29:10  dennis
 *  Interface to a list of strings. Will be implemented by subclasses of
 *  SpecialString to pass lists of choices to automatically generated
 *  GUI components.
 *
 */

package DataSetTools.util;

import java.io.*;

/**
 * The StringList interface provides an interface that represents an ordered
 * list of strings.
 *
 */

public interface IStringList
{

  /* --------------------------- num_strings ------------------------------ */
  /**
   *  Get the number of Strings contained in this list of Strings.
   *
   *  @return  the number of Strings in the list of Strings.
   */

  public int num_strings();


  /* ----------------------------- getString ----------------------------- */
  /**
   *  Get a copy of the String in the specified position in this list 
   *  of Strings.
   *
   *  @param   position  The position in the list from which the string is to
   *                     be obtained. 
   *
   *  @return  A copy of the String in the given position in the list, 
   *           if the position is valid, or null of the position is not valid.
   */

  public String getString( int position );

}
