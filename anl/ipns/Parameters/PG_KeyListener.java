/*
 * File:  PG_KeyListener.java
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
 *  Revision 1.2  2006/07/10 16:25:05  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.1  2006/06/15 23:34:07  dennis
 *  Listeners for low-level events, for use with concrete parameter
 *  GUIs.  When an event occurs, the notifiyChanged() or
 *  notifyChanging() methods are called on the parameter GUI base
 *  class to trip the valid flag to false.
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.event.*;

/**
 *  This class is a KeyListener that can be added to an appropriate 
 *  concrete class derived from ParameterGUI, to call the notifyChanging()
 *  method to notify the PG that the item's value is being changed, and to
 *  trip the the valid Checkbox to false.
 */

public class PG_KeyListener implements KeyListener
{
  private ParameterGUI my_pg = null;// this is the PG whose notifyChanging()
                                       // method should be called.

  /**
   *  Construct a KeyListener to notify the specified 
   *  ParameterGUI object that the widget's value is being changed.
   *
   *  @param  pg  The ParameterGUI object to be notified.
   */
  public PG_KeyListener( ParameterGUI pg )
  {
    my_pg = pg;
  }

  
  /**
   *  This method is called when a key is typed in a component, 
   *  (e.g. a character is typed in a JTextField.)  It just calls 
   *  the PG's notifyChanging() method.
   *
   *  @param  event  The KeyEvent indicating that a character was typed.
   */
  public void keyTyped( KeyEvent event )
  {
    my_pg.notifyChanging();
  }


  /**
   *  This method is called when a key is pressed down in a component.
   *  Currently this has no effect.
   *
   *  @param  event  The KeyEvent indicating that a key was pressed.
   */
  public void keyPressed( KeyEvent event )
  {
    // NO-OP, we don't care about KeyPress, just KeyTyped
  }


  /**
   *  This method is called when a key is released in a component.
   *  Currently this has no effect.
   *
   *  @param  event  The KeyEvent indicating that a key was released.
   */
  public void keyReleased( KeyEvent event )
  {
    // NO-OP, we don't care about KeyRelease, just KeyTyped
  }


}
