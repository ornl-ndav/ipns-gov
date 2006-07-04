/*
 * File:  ParameterGUI.java
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
 *  Revision 1.3  2006/07/04 02:27:23  dennis
 *  Revised sequence in getGUIPanel(), so that the lower level
 *  construction of the GUI widget is done before constructing
 *  the overall GUIPanel().
 *
 *  Revision 1.2  2006/06/13 16:16:02  dennis
 *  Added methods notifyChanged() and notifyChanging() that are
 *  to be called by low-level listeners, when the PG's entry widget
 *  is changed.  Currently, these methods just trip the valid flag
 *  to false (pending verification of the new value).  Eventually,
 *  these methods could also pass the change notificiation on.
 *
 *  Revision 1.1  2006/06/12 21:52:29  dennis
 *  Initial version of new code for parameter GUIs.  While this is
 *  loosely based on the parameter GUIs developed several years ago
 *  by Peter Peterson and Chris Bouzek, the current system is much
 *  simplified, and should be easier to maintain and extend.
 *
 */
package gov.anl.ipns.Parameters;

import java.awt.*;
import javax.swing.*;
import java.io.*;

/**
 *  This is the abstract base class for parameters with GUI components.
 *  This class implements those capabilities that are common to parameters
 *  with GUIs of all different data types.  There should be a derived 
 *  abstract class for each data type supported.  That is, there is a class
 *  that deals with common methods for any parameter that has a String data
 *  type, regardless of whether the string represents a file name, directory
 *  name, title, etc.
 */

public abstract class NewParameterGUI implements INewParameterGUI, 
                                                 Serializable 
{
  private   JPanel    gui_panel  = null;
  private   JCheckBox validCheck = null;
  
  private   String    name;
  private   boolean   valid;
  

  /**
   * While this class is abstract, the constructor is called by concrete
   * derived classes, to set the name and initial state of the valid flag. 
   *
   * @param  name    The name of this ParameterGUI.
   * @param  valid   Whether this VectorPG should be valid or 
   *                 not (initially).
   */
  public NewParameterGUI( String name, boolean valid ) 
  {
    this.name  = name;
    this.valid = valid;
  }


  /**
   * Set the name of the parameter.
   *
   * @param name The new name.
   */
  public void setName( String name ) 
  {
    this.name = name;
  }


  /**
   * @return The name of the parameter. This is normally used as the 
   *         title of the parameter.
   */
  public String getName() 
  {
    return name;
  }


  /**
   * Get a reference to a JPanel containing the GUI component for this
   * ParameterGUI.  If the panel and component don't already exist, they
   * will be constructed.  If they already exist, the existing panel will
   * be returned.
   * The value of the parameter "include_valid_box", will be ignored on 
   * subsequent calls to this method.  If the GUI Panel is to be reconfigured 
   * in a different state, destroyGUIPanel() must be called first, so that an 
   * entirely new Panel will be created on the next call to getGUIPanel().
   * 
   * @param include_valid_box  boolean determining if a "valid check box"
   *                           component is to be included in the GUI Panel.
   *                           This parameter is only used the first time
   *                           a GUI Panel is created after constructing
   *                           the ParameterGUI, and the first time after a
   *                           call to destroyGUIPanel().
   * 
   * @return The GUI panel in which the entry widget is drawn.
   */
  public final JPanel getGUIPanel( boolean include_valid_box ) 
  {
    if ( !hasGUI() )                      // there is no GUI so make a new one.
    {
      if( !include_valid_box )            // no valid checkbox so just use the 
        gui_panel = getWidget();          // GUI entry widget

      else                                // make a new panel with the GUI 
      {                                   // entry widget and a valid checkbox
        validCheck = new JCheckBox();
        validCheck.setSelected( valid );
        validCheck.setEnabled( false );

        JPanel widget = getWidget();      // NOTE: Do any calls to getWidget()
                                          // BEFORE making the gui_panel non-
                                          // null, since the gui_panel == null
                                          // is used to check the existence of
                                          // the GUI, and the GUI can't really
                                          // exist until getWidget() finishes.
        gui_panel = new JPanel( new BorderLayout() );
        gui_panel.add( widget, BorderLayout.CENTER );
        gui_panel.add( validCheck,  BorderLayout.EAST );
      }
    }

    return gui_panel;
  }


  /**
   * Destroys the GUI Panel, Component, and Valid CheckBox(if present).  
   * Calling this method will set the references to all GUI components to 
   * null so that they can be garbage collected.  After calling
   * destroyGUIPanel(), a new GUI Panel will be constructed when getGUIPanel()
   * is called.
   */
  public final void destroyGUIPanel() 
  {
    gui_panel  = null;
    validCheck = null;
    
    destroyWidget();
  }


  /**
   * Set the valid state of the parameter.
   *
   * @param  valid  boolean indicating whether or not this ParameterGUI 
   *                        should be considered valid.
   */
  public void setValidFlag( boolean valid ) 
  {
    this.valid = valid;
    if ( validCheck != null )
      validCheck.setSelected( valid );
  }


  /**
   * @return Whether or not this ParameterGUI is valid.
   */
  public boolean getValidFlag() 
  {
    return this.valid;
  }


  /**
   * Retrieves a String giving the type word to use when specifying this
   * ParameterGUI in a script.  By default the type word is the class name
   * with the suffix PG removed.  If different behaviour is required, this
   * method can be overridden in a derived class.
   * 
   * @return The Type of the parameter GUI.
   */
  public String getType()
  {
    String type      = this.getClass().toString();
    int    dot_index = type.lastIndexOf( "." );
    type = type.substring( dot_index + 1 );    

    int    pg_index = type.lastIndexOf( "PG" );
    if ( pg_index >= 0 )
      type = type.substring( 0, pg_index );

    return type;
  }


  /**
   * @return A String representation of this ParameterGUI consisting 
   *         of its type, name, valid, and validity.
   */
  public String toString() 
  {
    String result = "";
    try
    {
      result = this.getType() + ": \"" + this.getName()  + "\" "  + 
                                         this.getValue() +
                                " "    + this.getValidFlag();
    }
    catch ( Exception e )
    {
      result = this.getType() + ": \"" + this.getName()  + "\" "  + 
                                " Exception getting value "  + e  +
                                " "    + this.getValidFlag();
    }
    return result;
  }


  /**
   *  This method should be called by concrete derived classes when their
   *  value is in the process of being changed.  This will cause the valid
   *  flag to be tripped false.  Future versions may also send a message 
   *  indicating that the value is being changed, using an
   *  "IParameterMessageSender" that has been added to this parameter.
   */
  protected final void notifyChanging()
  {
    setValidFlag( false );
  } 


  /**
   *  This method should be called by concrete derived classes when their
   *  value has been changed.  This will cause the valid flag to be tripped 
   *  false.  Future versions may also send a message indicating that the 
   *  value was changed, using an "IParameterMessageSender" that has been 
   *  added to this parameter.
   */
  protected final void notifyChanged()
  {
    setValidFlag( false );
  }


  /**
   *  Check whether or not the GUI panel currently exists for this 
   *  object.  This method is protected, since there should be no need 
   *  for code using a ParameterGUI to know whether or not the GUI 
   *  component is present.
   *
   *  @return false if the gui_panel has been set to null.
   */
  protected final boolean hasGUI()
  {
    if ( gui_panel == null )
      return false;

    return true;
  }

 
  /**
   * Returns the bottom level GUI component in a JPanel.  If it does not 
   * exist it will be created.
   * 
   * @return The GUI panel containing the parameterGUI.
   */
  protected abstract JPanel getWidget();


  /**
   * Set internal references to the lower level GUI entry widget to
   * null so that it can be garbage collected.  Subsequent calls to 
   * getWidget() will create a new widget.
   */
  protected abstract void destroyWidget();


}
