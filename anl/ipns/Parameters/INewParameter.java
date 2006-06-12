/*
 * File:  INewParameter.java
 *
 * Copyright (C) 2003, Peter Peterson, 2006, Dennis Mikkelson 
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
 * This is an interface to be implemented by all parameters.  A basic 
 * parameter just maintains a name and value pair, describing the prompt
 * for the parameter value and the parameter value itself.   The derived
 * interface, IParameterGUI, adds methods for handling a GUI component to
 * get input from the user.
 */

public interface INewParameter {

  /**
   * Change the name (i.e. prompt string) for the parameter.
   *
   * @param  name  The name of this IParameter.
   */
  void setName( String name );


  /**
   * Get the current name for the paraemter.
   *
   * @return The name of the parameter. 
   */
  String getName();


  /**
   * Sets the value of the parameter, from the specified object.  
   * Implementing classes are expected to support setting the
   * value from a String form of the parameter value.  Implementing classes
   * may also support various type conversions, such as setting a Float
   * value from an Integer value.  NOTE: if the correct data type cannot be 
   * obtained from the specified object, an IllegalArgument exception 
   * should be thrown.
   *
   * @param value  This object is used to set the value of this parameter.
   * 
   */
  void setValue( Object value );


  /**
   * Get the value of this IParameter, as an object.
   *
   * @return An object containing the value of the parameter.   Implementing
   *         classes are responsible for returning an expected type.  That is,
   *         if FloatParameter implements this interface, it should return
   *         an object of type Float.
   */
  Object getValue();


  /**
   * Get the String that identifies the type of this parameter object.
   *
   * @return the string used in scripts to denote the particular parameter.
   */
  String getType(  );


  /**
   * Construct a copy of this IParameter object.
   *
   * @return A copy of this IParameter, with the same name and value.
   */
  public Object getCopy();

}
