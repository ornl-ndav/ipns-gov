/*
 * File: ColorScaleMenu.java
 *
 * Copyright (C) 2001 Dennis Mikkelson
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
 * Revision 1.5  2004/04/02 20:59:46  millermi
 * - Fixed javadoc errors
 *
 * Revision 1.4  2004/03/15 23:53:58  dennis
 * Removed unused imports, after factoring out the View components,
 * Math and other utils.
 *
 * Revision 1.3  2004/03/12 02:01:13  serumb
 * Changed package and imports.
 *
 * Revision 1.2  2002/11/27 23:13:34  pfpeterson
 * standardized header
 *
 */

package gov.anl.ipns.ViewTools.UI;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import gov.anl.ipns.ViewTools.Panels.Image.*;

/**
 *  This class represents a JMenu listing available "named" pseudo color
 *  scales.
 */

public class ColorScaleMenu extends    JMenu 
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
  *  @see gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
  *                    
  */
  public ColorScaleMenu( ActionListener listener )
  { 
    super( "Color Scale..." );

    JMenuItem button = new JMenuItem( IndexColorMaker.HEATED_OBJECT_SCALE );
    button.addActionListener( listener );
    add( button );

    button = new JMenuItem( IndexColorMaker.HEATED_OBJECT_SCALE_2 );
    button.addActionListener( listener );
    add( button );

    button = new JMenuItem( IndexColorMaker.GRAY_SCALE );
    button.addActionListener( listener );
    add( button );

    button = new JMenuItem( IndexColorMaker.NEGATIVE_GRAY_SCALE );
    button.addActionListener( listener );
    add( button );

    button = new JMenuItem( IndexColorMaker.GREEN_YELLOW_SCALE );
    button.addActionListener( listener );
    add( button );

    button = new JMenuItem( IndexColorMaker.RAINBOW_SCALE );
    button.addActionListener( listener ); 
    add( button );

    button = new JMenuItem( IndexColorMaker.OPTIMAL_SCALE );
    button.addActionListener( listener );
    add( button );

    button = new JMenuItem( IndexColorMaker.MULTI_SCALE );
    button.addActionListener( listener );
    add( button );

    button = new JMenuItem( IndexColorMaker.SPECTRUM_SCALE );
    button.addActionListener( listener );
    add( button );
  }

}
