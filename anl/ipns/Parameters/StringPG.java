/*
 * File:  StringPG.java
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
 *  Revision 1.5  2006/07/10 16:25:06  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.4  2006/07/04 02:41:19  dennis
 *  Moved getCopy() method from abstract base class,
 *  FilteredStringPG, to concrete derived class.
 *
 *  Revision 1.3  2006/06/30 14:24:39  dennis
 *  Removed unused imports.
 *
 *  Revision 1.2  2006/06/29 20:13:21  dennis
 *  Modified to do virtually all of the work in the new base class
 *  FilteredStringPG.  Now just extends FilteredStringPG and uses
 *  the AllPassFilter as it's filter, so that any String is accepted.
 *
 *  Revision 1.1  2006/06/23 14:14:45  dennis
 *  Concrete class for entering a string from a TextField.
 *
 */

package gov.anl.ipns.Parameters;


/**
 *  A StringPG uses a JTextField component to let the user specify a String.
 */
public class StringPG extends FilteredStringPG
{

  /**
   * Creates a new StringPG object with the specified name and initial
   * value.  A StringPG will let the user enter an arbitrary String.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a String value.
   */
  public StringPG( String name, Object val ) throws IllegalArgumentException
  {
    super( name, Conversions.get_String( val ), new AllPassFilter() );  
  }


  /**
   * Construct a copy of this StringPG object.
   *
   * @return A copy of this StringPG, with the same name and value.
   */
  public Object clone() 
  {
     StringPG copy = new StringPG( getName(), str_value );
     copy.setValidFlag( getValidFlag() );
     return copy;
  }

}
