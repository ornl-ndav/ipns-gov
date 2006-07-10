/*
 * File:  FloatPG_base.java
 *
  * Copyright (C) 2006, Julian Tao
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
 * Contact : Julian Tao <taoj@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.4  2006/07/10 16:25:04  dennis
 * Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 * Revision 1.3  2006/07/03 20:55:28  dennis
 * Explicityl construct object passed to set value, rather
 * than rely on autoboxing.
 *
 * Revision 1.2  2006/06/27 20:35:11  dennis
 * Removed commented out updateValueFromGUI(), since it is not
 * needed.
 *
 */
package gov.anl.ipns.Parameters;


/**
 * This is the abstract base class for all ParameterGUIs that have a 
 * float value using BooleanPG_base class as the template.  Derived
 * classes will provide a particular GUI entry widget, to allow the
 * user to specify a value.
 *
 * @see FloatPG
 */

public abstract class FloatPG_base extends ParamUsesString 
{
  protected  float  float_value;   // just protected, so derived classes can
                                   // directly get the value to place in their
                                   // GUI when the GUI is built.

  /**
   * Creates a new FloatPG_base object with the specified name and initial
   * value.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a float value.
   */
  public FloatPG_base( String name, Object val ) 
                                             throws IllegalArgumentException
  {
    super( name, true );
    setValue( val );
  }


  /**
   * Get the value of this float valued PG, as a String.
   *
   * @return "true" or "false".
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI cannot be converted 
   *         to a float value.
   */
  public String getStringValue() throws IllegalArgumentException
  {
    getValue();                    // this will synchronize the float_value with
                                   // the GUI widget value and throw an 
                                   // exception if the GUI widget value is bad

    return new Float( float_value ).toString();
  }


  /**
   * Get the current value of this PG as follows.  First, check if there is
   * a GUI entry widget, and if so, copy its value into the value of this PG,
   * and set the valid flag.  If there is no GUI entry widget, then just
   * return the current value.  NOTE: This method is final, so derived classes
   * cannot override it.  Derived classes are only responsible for handling
   * the value displayed in the GUI entry widget, via getWidgetValue(). 
   *
   * @return A Float.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI cannot be converted 
   *         to a float value.
   */
  public final Object getValue() throws IllegalArgumentException
  {
    if( hasGUI() )                        // NOTE: In some cases it will be
      float_value = getWidgetValue();     //       neccessary to check if the
                                          //       widget value is valid and
                                          //       NOT use it, but throw an
                                          //       illegal argument exception
    return new Float( float_value );
  }


  /**
   * Get the value of this PG as a primitive float.
   *
   * @return the primitive float value of this PG.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI cannot be converted 
   *         to a float value.
   */
  public final float getfloatValue()
  {
    getValue();   // this will try to update the value from the GUI widget
                  // and may throw an IllegalArgumentException
    return float_value;
  }


  /**
   * Set the value of this PG, its GUI entry widget, and the valid flag,
   * if the specified object can be converted to a float value.  Throw an
   * exception if conversion to a float is not possible.  NOTE: This 
   * method is final, so derived classes cannot override it.  Derived 
   * classes are only responsible for handling the value displayed in the 
   * GUI entry widget, via setWidgetValue().
   *
   * @param  obj  The new value.
   *
   * @throws IllegalArgumentException if the specifice object cannot be 
   *         converted to a float value.
   */
  public final void setValue( Object obj ) throws IllegalArgumentException
  {
    float_value = Conversions.get_float( obj );  // this could throw an
                                                 // exception
    if ( hasGUI() )
      setWidgetValue( float_value );
    
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
    if ( !hasGUI() ) 
      return false;

    try
    {
      float gui_value = getWidgetValue();
    
      if ( gui_value == float_value )         // no change in value
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
   * Used to clear out the FloatPG.  This sets the internal value 
   * to false.  There are no other resources to free.  
   */
  public void clear() 
  {
    setValue( new Float(0.0) );
  }

  
  /**
   * Retrieves the GUI's current value.  IF the value in the widget does
   * NOT represent a valid float value, then the implementing method should
   * throw an IllegalArgumentException.
   * 
   * @return The value of the GUI.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present, or if the value in the widget doesn't 
   *         represent a float value.
   */
  protected abstract float getWidgetValue() throws IllegalArgumentException;


  /**
   * Sets the GUI's current value.  NOTE: When this method is called from the
   * setValue(obj) method, the validity of the argument has already be checked.
   *
   * @param value  The float value to set into the GUI widget.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present.
   */
  protected abstract void setWidgetValue( float value ) 
                                           throws IllegalArgumentException;


}
