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
 *  Revision 1.1  2006/07/10 16:25:05  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.3  2006/07/04 02:41:20  dennis
 *  Moved getCopy() method from abstract base class,
 *  FilteredStringPG, to concrete derived class.
 *
 *  Revision 1.2  2006/06/30 14:24:40  dennis
 *  Removed unused imports.
 *
 *  Revision 1.1  2006/06/29 20:11:34  dennis
 *  This PG extends FilteredStringPG using a filter that only allows
 *  users to specify an increasing sequence of integers, separated
 *  by "," and ":".
 */

package gov.anl.ipns.Parameters;


/**
 *  This abstract class serves as the super class for those
 *  PGs whose value can be meaningfully set with a String.
 *  This concept is used by IsawLite, and has been added to
 *  to the new simpler PGs, for compatibility with IsawLite.
 */
public abstract class ParamUsesString extends ParameterGUI 
{

  /**
   * Constructor that just passes the information on up to the
   * super class.
   *
   * @param  name    The name (i.e. prompt string) for this PG.
   * @param  valid   Whether this PG should be valid or 
   *                 not (initially).
   */
  public ParamUsesString( String name, boolean valid )
  {
    super( name, valid );  
  }

  /**
   * Set the value of this PG, its GUI entry widget, and the valid flag,
   * from the specified String value.
   *
   * @param  str  The new value.
   *
   * @throws IllegalArgumentException if the GUI is active but the
   *         specific String cannot be set as the value of the GUI,
   *         or if the specified str cannot be converted into a 
   *         value of the required type.
   */
  public final void setStringValue( String str ) throws IllegalArgumentException
  {
    setValue( str );
  }

}
