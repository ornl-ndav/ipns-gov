/*
 * File:  InstNamePG.java
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
 *  Revision 1.4  2006/07/10 16:25:05  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.3  2006/07/04 02:41:21  dennis
 *  Moved getCopy() method from abstract base class,
 *  FilteredStringPG, to concrete derived class.
 *
 *  Revision 1.2  2006/06/30 14:24:40  dennis
 *  Removed unused imports.
 *
 *  Revision 1.1  2006/06/29 22:49:38  dennis
 *  PG for specifying instrument names.  Currently, this just get's its
 *  initial value from the system properties, and does not do any
 *  filtering of the names.
 *
 */

package gov.anl.ipns.Parameters;

import DataSetTools.util.SharedData;

/**
 *  An InstNamePG uses a JTextField component to let the user specify an
 *  instrument name.  If no name is specified, this PG gets the instrument
 *  name from the properties.
 */
public class InstNamePG extends FilteredStringPG
{

  /**
   * Creates a new InstNamePG object with the specified name and initial
   * value.  An InstNamePG will let the user enter an arbitrary String.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a String value.
   */
  public InstNamePG( String name, Object val ) throws IllegalArgumentException
  {
    super( name, Conversions.get_String( val ), new AllPassFilter() );  

    String inst_name = Conversions.get_String( val );

    if ( val == null || inst_name.length() == 0 )
    {
      inst_name = SharedData.getProperty( "Default_Instrument" );
      str_value = inst_name;
    }
  }


  /**
   * Construct a copy of this InstNamePG object.
   *
   * @return A copy of this InstNamePG, with the same name and value.
   */
  public Object clone() 
  {
     InstNamePG copy = new InstNamePG( getName(), str_value );
     copy.setValidFlag( getValidFlag() );
     return copy;
  }

}
