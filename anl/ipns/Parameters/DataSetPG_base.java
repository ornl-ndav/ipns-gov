/*
 * File:  DataSetPG_base.java
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
 *  Revision 1.2  2006/06/26 22:36:23  dennis
 *  setValue() and constructor now accept a null value and
 *  translate that into the DataSet.EMPTY_DATA_SET.
 *  Made a few minor fixes to javadocs.
 *
 *  Revision 1.1  2006/06/26 21:01:50  dennis
 *  Abstract base class for PGs whose value is a reference to
 *  a DataSet.  Eventually this should be moved out of the "gov"
 *  hierarchy, into the DataSetTools hierarchy.  Initially, we
 *  will keep this in the "gov" hierarchy, until the code base is
 *  switched to use the new parameter GUIs.
 *
 */
package gov.anl.ipns.Parameters;  // TODO: This should be moved to the
                                  //       DataSetTools tree.

import DataSetTools.dataset.*;         

/**
 * This is the abstract base class for all ParameterGUIs that have a DataSet
 * as their value.  Derived classes will provide a particular GUI entry
 * widget, to allow the user to specify a DataSet.
 */

public abstract class DataSetPG_base extends NewParameterGUI 
{
  protected  DataSet  ds_value;  // just protected, so derived classes can
                                 // directly get the value to place in their
                                 // GUI when the GUI is built.

  /**
   * Creates a new DataSetPG_base object with the specified name and initial
   * value.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified object 
   *         is not a DataSet.
   */
  public DataSetPG_base( String name, Object val ) 
                                             throws IllegalArgumentException
  {
    super( name, true );
    setValue( val );
  }


  /**
   * Get the value of this DataSetPG as a String, by just returning the
   * result of calling DataSet.toString().
   *
   * @return String containing the name of the DataSet.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI is not a DataSet. 
   */
  public String getStringValue() throws IllegalArgumentException
  {
    getValue();                    // this will synchronize the ds_value with
                                   // the GUI widget value and throw an 
                                   // exception if the GUI widget value is bad

    return ds_value.toString();
  }


  /**
   * Get the current value of this PG as follows.  First, check if there is
   * a GUI entry widget, and if so, copy its value into the value of this PG,
   * and set the valid flag.  If there is no GUI entry widget, then just
   * return the current value.  NOTE: This method is final, so derived classes
   * cannot override it.  Derived classes are only responsible for handling
   * the value displayed in the GUI entry widget, via getWidgetValue(). 
   *
   * @return A reference to the DataSet that is the value of this PG.
   *
   * @throws IllegalArgumentException is thrown, if a GUI entry widget
   *         exists for this PG, but the value in the GUI is not a 
   *         valid reference to a DataSet.
   */
  public final Object getValue() throws IllegalArgumentException
  {
    if( hasGUI() )                        // NOTE: getWidgetValue() may throw
      ds_value = getWidgetValue();        //       an IllegalArgumentException 
                                          //       if the widget value does 
                                          //       not refer to a DataSet 
    return ds_value;
  }


  /**
   * Set the value of this PG, its GUI entry widget, and the valid flag,
   * if the specified object is a DataSet or is null.  Otherwise, throw an
   * exception.  If null is passed in, the value will be set to
   * DataSet.EMPTY_DATA_SET.
   * NOTE: This method is final, so derived classes cannot override it. 
   * Derived classes are only responsible for handling the value displayed
   * in the GUI entry widget, via setWidgetValue().
   *
   * @param  obj  The new DataSet.
   *
   * @throws IllegalArgumentException if the specific object does not 
   *         refer to a DataSet.
   */
  public final void setValue( Object obj ) throws IllegalArgumentException
  {
    if ( obj == null ) 
      ds_value = DataSet.EMPTY_DATA_SET;   

    else if ( obj instanceof DataSet )            // perhaps there should be a
      ds_value = (DataSet)obj;                    // get_DataSet method.

    else
      throw new IllegalArgumentException(
                    "obj not DataSet in DataSetPG_base.setValue()" );

    if ( hasGUI() )
      setWidgetValue( ds_value );

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
   *          that refers to a different DataSet than the value stored 
   *          in the parameter.  This will return false if there is no 
   *          entry widget, or if the value in the entry widget refers 
   *          to the same DataSet that is currently the value of this PG.
   */
  public boolean hasChanged()
  {
    if ( !hasGUI() )                        // GUI can't change if it's
      return false;                         // not there!

    try
    {
      DataSet gui_value = getWidgetValue();
    
      if ( gui_value == ds_value )          // no change in value
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
   * Used to clear any resources allocated by the DataSetPG.  This sets
   * the internal value the EMPTY_DATA_SET.  There are no other resources to
   * free.  Concrete classes may need to overide this method to be sure that 
   * lists of references to DataSets are not kept.
   */
  public void clear() 
  {
    setValue( DataSet.EMPTY_DATA_SET );
  }

  
  /**
   * Retrieves the GUI's current value.  IF the value in the widget does
   * NOT refer to a DataSet then the implementing method should
   * throw an IllegalArgumentException.
   * 
   * @return The value of the GUI.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present, or if the value in the widget doesn't 
   *         refer to a DataSet.
   */
  protected abstract DataSet getWidgetValue() throws IllegalArgumentException;


  /**
   * Sets the GUI's current value.  NOTE: When this method is called from the
   * setValue(obj) method, the validity of the argument has already been 
   * checked.
   *
   * @param value  The DataSet reference to set into the GUI widget.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present.
   */
  protected abstract void setWidgetValue( DataSet value ) 
                                          throws IllegalArgumentException;

}
