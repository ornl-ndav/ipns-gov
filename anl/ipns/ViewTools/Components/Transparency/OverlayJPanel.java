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
 *  Revision 1.9  2004/03/12 02:42:41  serumb
 *  Changed package and imports.
 *
 *  Revision 1.8  2004/01/29 08:16:27  millermi
 *  - Updated the getObjectState() to include parameter for specifying
 *    default state.
 *  - Added static variables DEFAULT and PROJECT to IPreserveState for
 *    use by getObjectState()
 *
 *  Revision 1.7  2004/01/03 04:36:13  millermi
 *  - help() now uses html tool kit to display text.
 *  - Replaced all setVisible(true) with WindowShower.
 *
 *  Revision 1.6  2003/12/20 21:37:28  millermi
 *  - implemented kill() so editor and help windows are now
 *    disposed when the kill() is called.
 *
 *  Revision 1.5  2003/10/02 23:10:18  millermi
 *  - Added java docs to constructor.
 *
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

package gov.anl.ipns.ViewTools.Components.Transparency;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;

import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.Util.Sys.WindowShower;

public abstract class OverlayJPanel extends JPanel implements IOverlay,
                                                              IPreserveState
{
 /**
  * Constructor - sets the opaqueness of the JPanel to false.
  */
  public OverlayJPanel()
  {
    this.setOpaque(false);	  // make the panel "see-through"      
  }
  
 /**
  * This method will be used to display help information about this overlay.
  * It should open it's own JFrame with information about the overlay.
  */ 
  public static void help()
  {
    JFrame helper = new JFrame("Help for Generic Overlay");
    helper.setBounds(0,0,600,400);
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Commands for Generic Overlay</H1>" +
                  "Help information not available for this overlay.";
    textpane.setText(text);
    JScrollPane scroll = new JScrollPane(textpane);
    scroll.setVerticalScrollBarPolicy(
        			    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    WindowShower shower = new WindowShower(helper);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
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
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public abstract ObjectState getObjectState( boolean isDefault );
     
 /**
  * This method is called by to inform the overlay that it is no
  * longer needed. In turn, the overlay closes all windows created
  * by it before closing.
  */ 
  public abstract void kill();
  
}
