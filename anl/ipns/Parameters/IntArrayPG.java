/*
 * File:  IntArrayPG.java
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
 *  Revision 1.1  2006/06/29 20:11:34  dennis
 *  This PG extends FilteredStringPG using a filter that only allows
 *  users to specify an increasing sequence of integers, separated
 *  by "," and ":".
 *
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.*;
import javax.swing.*;

/**
 *  An IntArrayPG uses a JTextField component to let the user specify a 
 *  in a form represents an increasing sequence of integers, as determined
 *  by the IntList class.  Individual integers or ranges are separated by 
 *  commas and ranges are indicated like "3:5".
 */
public class IntArrayPG extends FilteredStringPG
{

  /**
   * Creates a new IntArrayPG object with the specified name and initial
   * value.  An IntArrayPG will let the user enter a String representing 
   * an increasing sequence of integers.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a an increasing sequence of integers.
   */
  public IntArrayPG( String name, Object val ) throws IllegalArgumentException
  {
    super( name, Conversions.get_String( val ), new IntListFilter() );  
  }

}
