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
 *  Revision 1.1  2003/05/20 19:44:46  dennis
 *  Initial version of standardized controls for viewers. (Mike Miller)
 *
 *
 */
 
 package DataSetTools.components.View.ViewControls;

 import java.awt.event.*;
 import javax.swing.border.*;
 
 import DataSetTools.components.ui.ActiveJPanel;
 import DataSetTools.util.*;
 
/**
 * Any class that implements this interface will be used to adjust
 * settings on the IViewComponent.
 */
public abstract class ViewControl extends ActiveJPanel implements IViewControl
{
  /* **********************************************
   *  Messaging Strings used by action listeners.
   * **********************************************
   *  Sender                  Message
   * **********************************************
   *  ControlSlider           IS_CHANGING
   *                          IS_CHANGED
   * **********************************************
   * Method data:
   */
   private String title;
   
   public ViewControl(String con_title)
   {
      if( con_title.equals("") )
         con_title = "No Title";
      this.setTitle(con_title);      
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
   *  @param  title of control
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
