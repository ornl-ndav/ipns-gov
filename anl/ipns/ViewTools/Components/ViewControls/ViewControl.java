/*
 * File: ViewControl.java
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
 *  Revision 1.6  2004/01/29 08:20:45  millermi
 *  - Now implements IPreserveState, thus state can now be saved for
 *    all ViewControls. Each control is responsible for detailed
 *    state information.
 *
 *  Revision 1.5  2004/01/05 18:15:59  millermi
 *  - Removed excess imports.
 *
 *  Revision 1.4  2003/10/16 05:00:14  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.3  2003/08/06 13:51:18  dennis
 *  - Abstract class layout now initialized to GridLayout(1,1)
 *    (Mike Miller)
 *
 *  Revision 1.2  2003/05/24 17:36:10  dennis
 *  Changed comments to maintain consistency between ViewControl and
 *  IViewControl action event strings.
 *  Changed constructor so it no longer initializes titled border
 *  to "No Title" when no title is passed.  (Mike Miller)
 *
 *  Revision 1.1  2003/05/20 19:44:46  dennis
 *  Initial version of standardized controls for viewers. (Mike Miller)
 *
 *
 */
 
 package DataSetTools.components.View.ViewControls;

 import java.awt.GridLayout;
 import javax.swing.border.TitledBorder;
 import javax.swing.border.LineBorder;
 
 import DataSetTools.components.ui.ActiveJPanel;
 import DataSetTools.util.FontUtil;
 import DataSetTools.components.View.ObjectState;
 
/**
 * Any class that implements this interface will be used to adjust
 * settings on the IViewComponent.
 */
public abstract class ViewControl extends ActiveJPanel implements IViewControl
{
 /* **********************************************
  *  Messaging Strings used by action listeners.
  * **********************************************
  *  Sender		     Message
  * **********************************************
  *  ControlSlider	     SLIDER_CHANGED
  *  ControlCheckbox	     CHECKBOX_CHANGED
  * **********************************************
  * Method data:
  */
  private String title;
 
 /**
  * "Title" - This constant String is a key for referencing the state
  * information about the title of this view control. This title is usually
  * displayed via a titled border. The value that this key references is of
  * type String.
  */
  public static final String TITLE = "Title";
  
  public ViewControl(String con_title)
  {
    this.setTitle(con_title); 
    this.setLayout( new GridLayout(1,1) );
  }
     
 /* *************** Methods implemented by ActiveJPanel *************** */ 
 /* void addActionListener()
  * void removeActionListener()
  * void removeAllActionListeners()
  * void send_message()
  */
      
 /* *************** Methods included from IViewControl  *************** */ 
 /* Although the methods addActionListener(),removeActionListener(), and
  * removeAllActionListeners() are included in the IViewControl,
  * these methods are implemented by ActiveJPanel, which ViewControl
  * implements.
  */
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = new ObjectState();
    state.insert( TITLE, new String(title) );
    return state;
  }
     
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    Object temp = new_state.get(TITLE);
    if( temp != null )
    {
      title = (String)title;
    }
  }
 
 /**
  * Get title of the view control.
  *
  *  @return title
  */
  public String getTitle()
  {
    return title;
  }
  
 /**
  * Set title of the view control.
  *
  *  @param  control_title - title of control
  */ 
  public void setTitle(String control_title)
  {
    title = control_title;
    
    TitledBorder border = 
	  new TitledBorder(LineBorder.createBlackLineBorder(),title);
    border.setTitleFont( FontUtil.BORDER_FONT ); 
    this.setBorder( border ); 
  }  
}
