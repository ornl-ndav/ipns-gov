/*
 * File: IViewControl.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.9  2005/03/20 05:29:22  millermi
 *  - Added methods getSharedKey() and setSharedKey() for use with
 *    ControlManager.
 *
 *  Revision 1.8  2005/03/09 22:32:54  millermi
 *  - Added methods getControlValue() and setControlValue(Object) for
 *    use by ControlManager.
 *  - Added generic messaging String VALUE_CHANGED for use by
 *    ControlManager.
 *
 *  Revision 1.7  2004/03/12 01:49:22  millermi
 *  - Changed package and fixed imports.
 *
 *  Revision 1.6  2004/01/30 22:11:12  millermi
 *  - Removed messaging Strings from interface and into respective
 *    implementing classes that actually send out the message.
 *
 *  Revision 1.5  2004/01/29 08:20:45  millermi
 *  - Now implements IPreserveState, thus state can now be saved for
 *    all ViewControls. Each control is responsible for detailed
 *    state information.
 *
 *  Revision 1.4  2004/01/05 18:14:06  millermi
 *  - Replaced show()/setVisible(true) with WindowShower.
 *  - Removed excess imports.
 *
 *  Revision 1.3  2003/06/13 19:44:05  serumb
 *  Added messages for the LabelCombobox and ButtonControl classes.
 *
 *  Revision 1.2  2003/05/24 17:34:39  dennis
 *  Changed action event string for ControlSlider and added the
 *  action event string for ControlCheckbox. (Mike Miller)
 *
 *  Revision 1.1  2003/05/20 19:44:46  dennis
 *  Initial version of standardized controls for viewers. (Mike Miller)
 *
 *
 */
 
 package gov.anl.ipns.ViewTools.Components.ViewControls;

 import java.awt.event.ActionListener;
 
 import gov.anl.ipns.ViewTools.Components.ObjectState;
 import gov.anl.ipns.ViewTools.Components.IPreserveState;
 
/**
 * Any class that implements this interface will be used to adjust
 * settings on the IViewComponent.
 */
public interface IViewControl extends IPreserveState
{
  // Used by LabelCombobox.java
  public static final String COMBOBOX_CHANGED  = "COMBOBOX_CHANGED";
  // Used by ButtonControl.java
  public static final String BUTTON_PRESSED  = "BUTTON_PRESSED";
  public static final String VALUE_CHANGED   = "VALUE_CHANGED";
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault );
   
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state );
  
 /**
  * Add a listener to this view control. A listener will be notified
  * when this control is modified.
  *
  *  @param  act_listener The action listener to add.
  */
  public void addActionListener( ActionListener act_listener );
  
 /**
  * Remove a specified listener from this view control.
  *
  *  @param  act_listener The action listener to remove.
  */ 
  public void removeActionListener( ActionListener act_listener );
 
 /**
  * Remove all listeners from this view control.
  */ 
  public void removeAllActionListeners();
 
 /**
  * Get title of the view control.
  */ 
  public String getTitle();
  
 /**
  * Set title of the view control.
  *
  *  @param  title Title of this control.
  */ 
  public void setTitle(String title);
  
 /**
  * Set value associated with this control.
  *
  *  @param  value Setable value for this control.
  */
  public void setControlValue(Object value);
  
 /**
  * Get value associated with this control that will change and need to be
  * updated.
  *
  *  @return Value for this control.
  */
  public Object getControlValue();
 
 /**
  * Get the category key associating this control with other controls. This
  * String will be used to "link" controls together.
  *
  *  @return Key used by ControlManager to link controls.
  */
  public String getSharedKey();
 
 /**
  * Set the category key associating this control with other controls. This
  * String will be used to "link" controls together.
  *
  *  @param  key The key used by the ControlManager to link this control
  *              with other similar controls.
  */
  public void setSharedKey(String key);
}
