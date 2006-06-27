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
 *  Revision 1.5  2006/06/27 21:47:49  rmikk
 *  Fixed error in comparing double to float.  Usually they are never equal
 *
 *  Revision 1.4  2006/06/27 16:32:28  taoj
 *  Added get_float() method to get a float value from a Number or String object.
 *
 *  Revision 1.3  2006/06/23 14:19:17  dennis
 *  Added initial version of method get_String() to get a String
 *  from an object.  For now, this is very straight forward and
 *  just gets a default form of the String object.  This "may" in
 *  the future be extended to get specialized String forms for
 *  special objects, such as a compact String form of an increasing
 *  sequence of integers in an int[].
 *
 *  Revision 1.2  2006/06/15 22:01:07  dennis
 *  Added get_int() method to get an integer value from an Object
 *  that is a Number or a String.
 *
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
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to a boolean value.
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


  /**
   *  Get an int value from the specified object, if possible.  
   *  Conversions from objects of class Number or String are
   *  supported.  If a String is passed in it must be a sequence of 
   *  characters representing an integer.  If a Number object is passed
   *  in, it's value must be an integer value.  For example a Float with
   *  a value 1.0f will return the integer 1, but a Float with a value of
   *  1.3f with cause an IllegalArgumentException to be thrown.   A null
   *  object will give the value 0, as a default.  All other objects will 
   *  cause an IllegalArugumentException to be thrown.
   *
   *  @param  obj   A Number or String object that can be
   *                interpreted as an integer value.
   *  
   *  @return the int value obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to an integer value.
   */

  public static int get_int( Object obj ) throws IllegalArgumentException
  {
    int int_value;

    if( obj == null )
      int_value = 0;

    else if ( obj instanceof Number )
    {
      double double_value = ((Number)obj).doubleValue();
      int_value = ((Number)obj).intValue();
      if ( int_value == double_value )
        return int_value;
      else
        throw new IllegalArgumentException(
                                "Number not an int value:" + double_value);
    }

    else if( obj instanceof String )
    {
      String temp = ((String)obj).trim();
      try
      {
        Double double_value = new Double( temp );
        int_value = double_value.intValue();     // we DO consider 1.0 to be int
        if ( int_value == double_value )
          return int_value;
        else
          throw new IllegalArgumentException(
                                "Number not an int value:" + double_value);
      }
      catch ( Exception exception )
      {
        throw new IllegalArgumentException("String not int value:" + temp);
      }
    }

    else
      throw new IllegalArgumentException("Object not int value:" + obj);

    return int_value;
  }

  /**
   *  Get a float value from the specified object, if possible.  
   *  Conversions from objects of class Number or String are
   *  supported.  If a String is passed in it must be a sequence of 
   *  characters representing a float number.  If a Number object is passed
   *  in, it's value must be a float value. A null
   *  object will give the value 0.0f, as a default.  All other objects will 
   *  cause an IllegalArugumentException to be thrown.
   *
   *  @param  obj   A Number or String object that can be
   *                interpreted as a float value.
   *  
   *  @return the float value obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to a float value.
   */  
  
  public static float get_float( Object obj ) throws IllegalArgumentException
  {
    float float_value;

    if( obj == null )
      float_value = 0.0f;

    else if ( obj instanceof Number )
    {
     
      float_value = ((Number)obj).floatValue();
      
      return float_value;
     
    }

    else if( obj instanceof String )
    {
      String temp = ((String)obj).trim();
      //Pattern regf = Pattern.compile("^[+|-]?[0-9]*(\\.[0-9]+)?([eE][+|-]?[0-9]+)?$");      
      //Matcher s2f = regf.matcher(temp); 
      //use the java static Float.valueof() method instead to convert string to float;

      try {
        float_value = Float.parseFloat(temp);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("String not a float value:" + obj);
      }
    }

    else throw new IllegalArgumentException("Object not a float value:" + obj);

    return float_value;
  }

  
  /**
   *  Get a String value from the specified object, if possible.  
   *
   *  @param  obj   An object that is to be converted to a String
   *  
   *  @return the String value obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to a String value.
   */

  public static String get_String( Object obj ) throws IllegalArgumentException
  {
    String str_value;

    if( obj == null )
      str_value = "";

    else
      str_value = "" + obj;       // use default way of converting Object to
                                  // String.  We may accomodate specific 
                                  // conversions for other data types in the
                                  // future.
    return str_value;
  }
  
}
