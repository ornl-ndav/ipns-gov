/*
 * File: HelpMenu.java
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.1  2003/06/09 14:44:04  dennis
 * Initial Version, currently able to provide help
 * information on the overlays. (Mike Miller)
 *
 */

package DataSetTools.components.View.Menu;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *  This class represents a JMenu listing available "named" pseudo color
 *  scales.
 */

public class HelpMenu extends    JMenu 
                      implements Serializable 
{
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *
  *  Construct a JMenu giving the available "named" pseudo color scales
  *  as listed in the IndexColorMaker class.
  *
  *  @param  listener  The action listener that will be added to each 
  *                    menu item. 
  *
  *  @see DataSetTools.components.image.IndexColorMaker
  *                    
  */
  public HelpMenu( ActionListener listener )
  { 
    super( "Help" );

    JMenu overlaybutton = new JMenu( "Overlays" );
    //overlaybutton.addActionListener( listener );
    add( overlaybutton );
    
    JMenuItem button = new JMenuItem( "Annotation" );
    button.addActionListener( listener );
    overlaybutton.add( button );

    button = new JMenuItem( "Axes" );
    button.addActionListener( listener );
    overlaybutton.add( button );

    button = new JMenuItem( "Selection" );
    button.addActionListener( listener );
    overlaybutton.add( button );
  }

}
