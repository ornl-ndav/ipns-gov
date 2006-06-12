/*
 * File:  Conversions.java
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
 *  Revision 1.1  2006/06/12 21:52:28  dennis
 *  Initial version of new code for parameter GUIs.  While this is
 *  loosely based on the parameter GUIs developed several years ago
 *  by Peter Peterson and Chris Bouzek, the current system is much
 *  simplified, and should be easier to maintain and extend.
 *
 */
package gov.anl.ipns.Parameters;

/**
 *  This class has static methods to get particular data type values from
 *  a generic object, when possible.  When this is not possible, the methods
 *  should throw an IllegalArugmentException.
 */
public class Conversions
{

  private Conversions()
  {
    // private constructor, since this class should just have static methods
  }


  /**
   *  Get a boolean value from the specified object, if possible.  
   *  Conversions from objects of class Boolean, String or Integer are
   *  supported.  If a String is passed in it must have either "true" or
   *  "false" as its only non-blank characters, ignoring case.  If an
   *  Integer is passed in, all non-zero values will be considered to be
   *  true.  A null object is considered to be false.  All other cases
   *  will cause an IllegalArugumentException to be thrown.
   *
   *  @param  obj   A Boolean, String or Integer object that can be
   *                interpreted as containing a boolean value.
   *  
   *  @return the boolean value obtained from the specified object.
   *
   * @throws IllegalArgumentException if the object cannot be converted
   *         to a boolean value.
   */

  public static boolean get_boolean( Object obj ) 
                                            throws IllegalArgumentException
  {
    boolean bool_value;

    if( obj == null )
      bool_value = false;
   
    else if ( obj instanceof Boolean )
      bool_value = ((Boolean)obj).booleanValue();
    
    else if( obj instanceof String )
    {
      String temp = ((String)obj).trim();
      if ( temp.equalsIgnoreCase( "true" ) )
        bool_value = true;
      else if ( temp.equalsIgnoreCase( "false" ) )
        bool_value = false;
      else
        throw new IllegalArgumentException("String not boolean value:" + temp);
    }

    else if ( obj instanceof Integer )
    {
      int intval = ((Integer)obj).intValue();

      if ( intval == 0 )
        bool_value = Boolean.FALSE;
      else
        bool_value = Boolean.TRUE;
    }

    else
      throw new IllegalArgumentException("Object not boolean value:" + obj);
    
    return bool_value;
  }

}
