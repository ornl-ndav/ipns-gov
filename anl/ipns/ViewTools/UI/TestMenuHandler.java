/*
 * File:  TestMenuHandler.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2001/04/23 21:50:26  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.2  2001/01/29 21:43:26  dennis
 *  Now uses CVS version numbers.
 *
 *  Revision 1.1  2000/11/10 22:55:46  dennis
 *  Trivial ActionListener to test menus.
 *
 */
package DataSetTools.components.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


/* ------------------------- TestMenuHandler ------------------------------ */
/**
 *  Trivial menu handler class only for testing a menu.
 */
  public class TestMenuHandler implements ActionListener,
                                           Serializable
  {
    public void actionPerformed( ActionEvent e )
    {
      System.out.println( e.getActionCommand() );
    }
  }

