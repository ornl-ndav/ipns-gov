/*
 * File: OverlayJPanel.java
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
 *  Revision 1.4  2003/09/24 01:34:32  millermi
 *  - Now implements IPreserveState, all classes extending OverlayJPanel
 *    will now preserve state.
 *
 *  Revision 1.3  2003/06/09 22:35:06  dennis
 *  - Added methods help() and getFocus() with implementation.
 *    (Mike Miller)
 *
 *  Revision 1.2  2003/05/16 14:57:09  dennis
 *  Added acknowledgement of NSF funding.
 *
 */

package DataSetTools.components.View.Transparency;

import java.awt.*;
import javax.swing.*;

import DataSetTools.components.View.ObjectState;
import DataSetTools.components.View.IPreserveState;

public abstract class OverlayJPanel extends JPanel implements IOverlay,
                                                              IPreserveState
{
   public OverlayJPanel()
   {
      this.setOpaque(false);        // make the panel "see-through"	
   }
   
  /**
   * This method will be used to display help information about this overlay.
   * It should open it's own JFrame with information about the overlay.
   */ 
   public static void help()
   {
      JFrame helper = new JFrame("Help for Generic Overlay");
      helper.setBounds(0,0,600,400);
      JTextArea text = new JTextArea("Commands for Generic Overlay\n\n");
      helper.getContentPane().add(text);
      text.setEditable(false);
      text.setLineWrap(true);
      text.append("Help information not available for this overlay.\n\n");
      
      helper.setVisible(true);
   }
  
  /**
   * This method requests window focus for the overlay. If focus is wanted
   * by a private data member of an overlay, this method should be overloaded
   * to have the data member itself call requestFocus(). 
   */ 
   public void getFocus()
   {
      this.requestFocus();
   } 
   
  /**
   * This method will set the current state variables of the object to state
   * variables wrapped in the ObjectState passed in.
   *
   *  @param new_state
   */
   public abstract void setObjectState( ObjectState new_state );
  
  /**
   * This method will get the current values of the state variables for this
   * object. These variables will be wrapped in an ObjectState. Keys will be
   * put in alphabetic order.
   */ 
   public abstract ObjectState getObjectState();
   
}
