/*
 * File:  BooleanPG_base.java
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
 *  Revision 1.6  2006/07/10 16:25:04  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.5  2006/06/26 20:48:06  dennis
 *  Fixed minor error in javadocs.
 *
 *  Revision 1.4  2006/06/25 02:01:10  dennis
 *  Adjusted handling of valid flag in getValue(), setValue() and
 *  hasChanged() methods, based on latest design revisions.  Specifically,
 *  getValue() will not alter the flag, since listeners will trip the valid
 *  flag false when the GUI is altered.
 *  setValue() will trip the flag false, since changing the value will require
 *  the code using the PG to determine whether or not the value is "really"
 *  valid for the current application.
 *  hasChanged() will set the flag false, if it returns true (due to a
 *  change or invalid value).  It will NOT set the flag true if no change
 *  is detected.
 *
 *  Revision 1.3  2006/06/25 01:30:24  dennis
 *  Removed updateValueFromGUI() method, since the getValue() method
 *  also will update the stored value from the value in the GUI,
 *  if the GUI is present.
 *
 *  Revision 1.2  2006/06/15 22:08:09  dennis
 *  Added try/catch blocks to the hasChanged() and updateValueFromGUI()
 *  methods, so that these will NOT throw exceptions if the widget value
 *  is invalid.  The exception handling is focused on the get/setValue()
 *  methods.
 *
 *  Revision 1.1  2006/06/12 21:52:27  dennis
 *  Initial version of new code for parameter GUIs.  While this is
 *  loosely based on the parameter GUIs developed several years ago
 *  by Peter Peterson and Chris Bouzek, the current system is much
 *  simplified, and should be easier to maintain and extend.
 *
 *
 */
package gov.anl.ipns.Parameters;


/**
 * This is the abstract base class for all ParameterGUIs that have a 
 * boolean value.  Derived classes will provide a particular GUI entry
 * widget, to allow the user to specify a value.
 *
 * @see BooleanPG
 */

public abstract class BooleanPG_base extends ParamUsesString
{
  protected  boolean  bool_value;  // just protected, so derived classes can
                                   // directly get the value to place in their
                                   // GUI when the GUI is built.

  /**
   * Creates a new BooleanPG_base object with the specified name and initial
   * value.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a boolean value.
   */
  public BooleanPG_base( String name, Object val ) 
                                             throws IllegalArgumentException
  {
    super( name, true );
    setValue( val );
  }


  /**
   * Get the value of this boolean valued PG, as a String.
   *
   * @return "true" or "false".
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI cannot be converted 
   *         to a boolean value.
   */
  public String getStringValue() throws IllegalArgumentException
  {
    getValue();                    // this will synchronize the bool_value with
                                   // the GUI widget value and throw an 
                                   // exception if the GUI widget value is bad

    return new Boolean( bool_value ).toString();
  }


  /**
   * Get the current value of this PG as follows.  First, check if there is
   * a GUI entry widget, and if so, copy its value into the value of this PG,
   * and set the valid flag.  If there is no GUI entry widget, then just
   * return the current value.  NOTE: This method is final, so derived classes
   * cannot override it.  Derived classes are only responsible for handling
   * the value displayed in the GUI entry widget, via getWidgetValue(). 
   *
   * @return A Boolean true or false.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI cannot be converted 
   *         to a boolean value.
   */
  public final Object getValue() throws IllegalArgumentException
  {
    if( hasGUI() )                        // NOTE: getWidgetValue() may throw 
      bool_value = getWidgetValue();      //       an IllegalArgumentException 
                                          //       if the widget value does 
                                          //       not represent a boolean
    return new Boolean( bool_value );
  }


  /**
   * Get the value of this PG as a primitive boolean.
   *
   * @return the primitive boolean value of this PG.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI cannot be converted 
   *         to a boolean value.
   */
  public final boolean getbooleanValue()
  {
    getValue();   // this will try to update the value from the GUI widget
                  // and may throw an IllegalArgumentException
    return bool_value;
  }


  /**
   * Set the value of this PG, its GUI entry widget, and the valid flag,
   * if the specified object can be converted to a boolean value.  Throw an
   * exception if conversion to a boolean is not possible.  NOTE: This 
   * method is final, so derived classes cannot override it.  Derived 
   * classes are only responsible for handling the value displayed in the 
   * GUI entry widget, via setWidgetValue().
   *
   * @param  obj  The new value.
   *
   * @throws IllegalArgumentException if the specific object cannot be 
   *         converted to a boolean value.
   */
  public final void setValue( Object obj ) throws IllegalArgumentException
  {
    bool_value = Conversions.get_boolean( obj );  // this could throw an
                                                  // exception
    if ( hasGUI() )
      setWidgetValue( bool_value );

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
   *          that differs from the value stored in the parameter.  This
   *          will return false if there is no entry widget, or if the
   *          value in the entry widget is not a proper value.
   */
  public boolean hasChanged()
  {
    if ( !hasGUI() )                        // GUI can't change if it's
      return false;                         // not there!

    try
    {
      boolean gui_value = getWidgetValue();
    
      if ( gui_value == bool_value )        // not change in value
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
   * Used to clear any resources allocated by the BooleanPG.  This just 
   * sets the internal value to false since there are no other resources 
   * to free.  
   */
  public void clear() 
  {
    setValue( Boolean.FALSE );
  }

  
  /**
   * Retrieves the GUI's current value.  IF the value in the widget does
   * NOT represent a valid boolean value, then the implementing method should
   * throw an IllegalArgumentException.
   * 
   * @return The value of the GUI.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present, or if the value in the widget doesn't 
   *         represent a boolean value.
   */
  protected abstract boolean getWidgetValue() throws IllegalArgumentException;


  /**
   * Sets the GUI's current value.  NOTE: When this method is called from the
   * setValue(obj) method, the validity of the argument has already been
   * checked.
   *
   * @param value  The boolean value to set into the GUI widget.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present.
   */
  protected abstract void setWidgetValue( boolean value ) 
                                           throws IllegalArgumentException;


}
