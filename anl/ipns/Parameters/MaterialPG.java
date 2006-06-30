/*
 * File:  MaterialPG.java
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
 *  Revision 1.2  2006/06/30 14:24:39  dennis
 *  Removed unused imports.
 *
 *  Revision 1.1  2006/06/29 22:55:53  dennis
 *  This PG uses a MaterialFilter to restrict entries to specifying
 *  materials in a form like "H_2,O"
 *
 *
 */

package gov.anl.ipns.Parameters;


/**
 *  A MaterialPG uses a JTextField component to let the user specify 
 *  a material in the form C,O_2.
 */
public class MaterialPG extends FilteredStringPG
{

  /**
   * Creates a new MaterialPG object with the specified name and initial
   * value.  A MaterialPG will let the user enter a material formula in
   * a simple form with element groups separated by commas.  Each element
   * group consists of an element symbol and possibly an underscore follwed
   * by the number of that element.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a String value.
   */
  public MaterialPG( String name, Object val ) throws IllegalArgumentException
  {
    super( name, Conversions.get_String( val ), new MaterialFilter() );  
  }

}
