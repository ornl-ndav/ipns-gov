/*
 * File:  VectorPG_base.java
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
 *  Revision 1.1  2006/06/26 22:37:54  dennis
 *  Initial version of abstract base class for PGs whose value
 *  is a Vector.  setValue() and constructor will accept a null
 *  value and translate that to an new (empty) Vector.
 *
 *
 */
package gov.anl.ipns.Parameters;  

import java.util.*;
import gov.anl.ipns.Util.Sys.*;


/**
 * This is the abstract base class for all ParameterGUIs that have a Vector
 * of Objects as their value.  Derived classes will provide a particular GUI 
 * entry widget, to allow the user to specify a Vector, possibly with a 
 * particular type of entries.
 */

public abstract class VectorPG_base extends NewParameterGUI 
{
  protected  Vector  vec_value;  // just protected, so derived classes can
                                 // directly get the value to place in their
                                 // GUI when the GUI is built.

  /**
   * Creates a new VectorPG_base object with the specified name and initial
   * value.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified object 
   *         is not a Vector.
   */
  public VectorPG_base( String name, Object val ) 
                                             throws IllegalArgumentException
  {
    super( name, true );
    setValue( val );
  }


  /**
   * Get the value of this VectorPG as a String, by returning the
   * result of calling gov.anl.ipns.Util.Sys.StringUtil.toString().
   *
   * @return String containing a String form of the contents of this
   *         vector.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI is not a Vector. 
   */
  public String getStringValue() throws IllegalArgumentException
  {
    getValue();                    // this will synchronize the vec_value with
                                   // the GUI widget value and throw an 
                                   // exception if the GUI widget value is bad

    return StringUtil.toString( vec_value );
  }


  /**
   * Get the current value of this PG as follows.  First, check if there is
   * a GUI entry widget, and if so, copy its value into the value of this PG,
   * and set the valid flag.  If there is no GUI entry widget, then just
   * return the current value.  NOTE: This method is final, so derived classes
   * cannot override it.  Derived classes are only responsible for handling
   * the value displayed in the GUI entry widget, via getWidgetValue(). 
   *
   * @return A reference to the Vector that is the value of this PG.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI is not a 
   *         valid reference to a Vector.
   */
  public final Object getValue() throws IllegalArgumentException
  {
    if( hasGUI() )                        // NOTE: getWidgetValue() may throw
      vec_value = getWidgetValue();       //       an IllegalArgumentException 
                                          //       if the widget value does 
                                          //       not refer to a Vector 
    return vec_value;
  }


  /**
   * Set the value of this PG, its GUI entry widget, and the valid flag,
   * if the specified object is a Vector or is null.  Otherwise, throw an 
   * exception.  If null is passed in, an empty Vector will be created as
   * the value of the VectorPG.
   * NOTE: This method is final, so derived classes cannot override it. 
   * Derived classes are only responsible for handling the value displayed
   * in the GUI entry widget, via setWidgetValue().
   *
   * @param  obj  The new Vector.
   *
   * @throws IllegalArgumentException if the specific object does not 
   *         refer to a Vector. 
   */
  public final void setValue( Object obj ) throws IllegalArgumentException
  {
    if ( obj == null ) 
      vec_value = new Vector();

    else if ( obj instanceof Vector )
      vec_value = (Vector)obj; 

    else
      throw new IllegalArgumentException(
                    "obj not Vector in VectorPG_base.setValue()" );

    if ( hasGUI() )
      setWidgetValue( vec_value );

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
   *          that refers to a different Vector than the Vector stored 
   *          in the parameter.  This will return false if there is no 
   *          entry widget, or if the value in the entry refers to the 
   *          same Vector that is currently the value of this PG.
   */
  public boolean hasChanged()
  {
    if ( !hasGUI() )                        // GUI can't change if it's
      return false;                         // not there!

    try
    {
      Vector gui_value = getWidgetValue();
    
      if ( gui_value == vec_value )         // no change in value
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
   * Used to clear any resources allocated by the VectorPG.  This sets
   * the internal value to a new emtpy Vector.  There are no other resources to
   * free.  Concrete classes may need to overide this method to be sure that 
   * large data structures are not kept in their local variables.
   */
  public void clear() 
  {
    setValue( new Vector() );
  }

  
  /**
   * Retrieves the GUI's current value.  IF the value in the widget does
   * NOT refer to a Vector then the implementing method should
   * throw an IllegalArgumentException.
   * 
   * @return The value of the GUI.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present, or if the value in the widget doesn't 
   *         refer to a Vector.
   */
  protected abstract Vector getWidgetValue() throws IllegalArgumentException;


  /**
   * Sets the GUI's current value.  NOTE: When this method is called from the
   * setValue(obj) method, the validity of the argument has already been 
   * checked.
   *
   * @param value  The Vector reference to set into the GUI widget.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present.
   */
  protected abstract void setWidgetValue( Vector value ) 
                                          throws IllegalArgumentException;

}
