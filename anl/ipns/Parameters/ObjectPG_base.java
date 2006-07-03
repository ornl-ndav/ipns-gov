/*
 * File:  ObjectPG_base.java
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
 *  Revision 1.2  2006/07/03 20:54:33  dennis
 *  Removed unused import.
 *
 *  Revision 1.1  2006/06/27 22:29:53  dennis
 *  Abstract base class for PGs that have an object as their value,
 *  such as PlaceholderPG and RealArrayPG.
 *
 *
 */
package gov.anl.ipns.Parameters;  

import gov.anl.ipns.Util.Sys.*;


/**
 * This is the abstract base class for all ParameterGUIs that have an Object
 * as their value.  Derived classes will provide a particular GUI 
 * entry widget, to allow the user to specify an Object of some specific 
 * type.
 */

public abstract class ObjectPG_base extends NewParameterGUI 
{
  protected  Object  obj_value;  // just protected, so derived classes can
                                 // directly get the value to place in their
                                 // GUI when the GUI is built.

  /**
   * Creates a new ObjectPG_base object with the specified name and initial
   * value.  The object will be passed to the (abstract) getObjectValue() 
   * method of the concrete derived class, to extract a valid object for
   * this PG.  The definition of a valid value will differ based on 
   * the specific requirements of the concrete derived class.  If a proper
   * value for this PG can't be obtained from the specified object, an
   * exception will be thrown.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if it is not possible to
   *         extract an object of the correct type from the specified object.
   */
  public ObjectPG_base( String name, Object val ) 
                                             throws IllegalArgumentException
  {
    super( name, true );
    setValue( val );
  }


  /**
   * Get the value of this ObjectPG as a String, if possible.
   *
   * @return String containing a String form of the contents of this
   *         object.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI cannot be converted
   *         to an object of the correct type. 
   */
  public String getStringValue() throws IllegalArgumentException
  {
    getValue();                    // this will synchronize the obj_value with
                                   // the GUI widget value and throw an 
                                   // exception if the GUI widget value is bad

    return StringUtil.toString( obj_value );
  }


  /**
   * Get the current value of this PG as follows.  First, check if there is
   * a GUI entry widget, and if so, copy its value into the value of this PG,
   * and set the valid flag.  If there is no GUI entry widget, then just
   * return the current value.  NOTE: This method is final, so derived classes
   * cannot override it.  Derived classes are only responsible for handling
   * the value displayed in the GUI entry widget, via getWidgetValue(). 
   *
   * @return A reference to the Object that is the value of this PG.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI cannot be  
   *         converted to an object of the correct type.
   */
  public final Object getValue() throws IllegalArgumentException
  {
    if( hasGUI() )                        // NOTE: getWidgetValue() may throw
      obj_value = getWidgetValue();       //       an IllegalArgumentException 
                                          //       if the widget value can't 
                                          //       be converted to the correct
                                          //       type of object. 
    return obj_value;
  }


  /**
   * Set the value of this PG, its GUI entry widget, and the valid flag,
   * from the specified object.  Objects are passed to the getObjectValue() 
   * method of the concrete derived class, to extract a valid value for
   * this PG.  The definition of a valid value will differ based on 
   * the specific requirements of the concrete derived class.  If a proper
   * value for this PG can't be obtained from the specified object, an
   * exception will be thrown.
   * NOTE: This method is final, so derived classes cannot override it. 
   * Derived classes are responsible for handling the value displayed
   * in the GUI entry widget, via setWidgetValue(), and for "extracting"
   * an appropriate object from a specified object, via the getObjectValue()
   * method.
   *
   * @param  obj  The Object specifying the value for this PG.
   *
   * @throws IllegalArgumentException if an Object of the required type
   *         cannot be extracted from the specified object. 
   */
  public final void setValue( Object obj ) throws IllegalArgumentException
  {
    obj_value = getObjectValue( obj );     // this may throw an exception

    if ( hasGUI() )
      setWidgetValue( obj_value );

    setValidFlag( false );
  }


  /**
   *  Check whether the value stored in the parameter matches the value
   *  from the GUI entry widget, WITHOUT changing either value.  Classes
   *  implementing this method must be careful to implement it in a way
   *  that does not alter the value stored in the parameter, or the value
   *  stored in the GUI entry widget, if it exists.  If the entry widget
   *  does not exist, this method will just return false.
   *   
   *  This method will also set the valid flag to false, if it returns
   *  true.
   *
   *  @return true if there is an entry widget with a meaningful value
   *          that refers to a different object than the object stored 
   *          in the parameter.  This will return false if there is no 
   *          entry widget, or if the value in the entry refers to the 
   *          same Object that is currently the value of this PG.
   */
  public boolean hasChanged()
  {
    if ( !hasGUI() )                        // GUI can't change if it's
      return false;                         // not there!

    try
    {
      Object gui_value = getWidgetValue();
    
      if ( gui_value == obj_value )         // no change in value
        return false;

      setValidFlag(false);                  // GUI val doesn't match old val
      return true;
    }
    catch ( Exception exception )
    {
      setValidFlag( false );                // illegal value entered by user
      return true;                          // is considered a change
    }
  }


  /**
   * Retrieves the GUI's current value.  IF the value in the widget does
   * NOT refer to an object of the proper form then the implementation 
   * should throw an IllegalArgumentException.
   * 
   * @return The value of the GUI.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present, or if the value in the widget doesn't 
   *         refer to an object of the proper type.
   */
  protected abstract Object getWidgetValue() throws IllegalArgumentException;


  /**
   * Sets the GUI's current value.  NOTE: When this method is called from the
   * setValue(obj) method, the validity of the argument has already been 
   * checked.
   *
   * @param value  The Object specifying the value for this PG.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present.
   */
  protected abstract void setWidgetValue( Object value ) 
                                          throws IllegalArgumentException;


  /**
   * Extract an object of the type required by the concrete subclass, from
   * the specified object.  In (special) cases, some attempt may be made to
   * extract an object with the correct contents from different types of
   * objects.  The object types that are supported will depend on the
   * concrete PG class, derived from this class, and should be described
   * in the documentation for the concrete PG class.  If a proper value
   * for this PG can't be obtained from the specified object, an
   * exception will be thrown.
   *
   * @param  obj  The Object specifying the value for this PG.
   *
   * @throws IllegalArgumentException if an object of the required type
   *         cannot be extracted from the specified object. 
   */
  protected abstract Object getObjectValue( Object obj )
                                          throws IllegalArgumentException;

}
