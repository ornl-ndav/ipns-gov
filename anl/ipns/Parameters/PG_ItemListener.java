/*
 * File:  PG_ItemListener.java
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
 *  Revision 1.2  2006/06/13 16:13:20  dennis
 *  Corrected this to call notifyChanged() when an event occurs,
 *  NOT hasChanged().
 *
 *  Revision 1.1  2006/06/13 16:04:20  dennis
 *  Item listener for handling low-level change events on an PG, that
 *  uses a check box, such as the BooleanPG.  When the itemStateChanged()
 *  method is called, this listener will just pass the event along by
 *  calling the hasChanged() method of the ParameterGUI base class.
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.event.*;


/**
 *  This class is an item listener that can be added to an appropriate 
 *  concrete class derived from NewParameterGUI, to call the notifyChanged()
 *  method to notify the PG that the item's value changed, and to trip the
 *  the valid Checkbox to false.
 */
public class PG_ItemListener implements ItemListener
{
  private NewParameterGUI my_pg = null;  // this is the PG whose haaChanged()
                                          // method should be called.

  /**
   *  Construct an item listener to notify the specified NewParameterGUI
   *  object that the item's value was changed.
   *
   *  @param  pg  The NewParameterGUI object to be notified.
   */
  public PG_ItemListener( NewParameterGUI pg )
  {
    my_pg = pg;
  }

  
  /**
   *  This method is called when the item's state is changed (e.g. BooleanPG
   *  checkbox is checked or unchecked.)  It just calls the PG's 
   *  notifyChanged() method.
   *
   *  @param  event  The item event indicating that the state was changed.
   */
  public void itemStateChanged( ItemEvent event )
  {
    my_pg.notifyChanged();
  }


}
