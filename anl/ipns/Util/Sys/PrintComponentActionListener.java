/*
 * File:  PrintComponentActionListener.java
 *
 * Copyright (C) 2002, Ruth Mikkelson, Alok Chatterjee
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
 * Contact : Alok Chatterjee achatterjee@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 S. Cass Avenue, Bldg 360
 *           Argonne, IL 60440
 *           USA
 *
 * 
 *
 * For further information, see http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2002/05/30 22:55:44  chatterjee
 *  ActionListener for print requests from the viewers
 *


package DataSetTools.viewer;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import IsawGUI.*;

/**
  * This class instantiates the PrintUtilities class and calls its print
  * method. The component that is to be printed is passed as a parameter
  * to this class.
  */

public class PrintComponentActionListener  implements ActionListener
{JMenu jm;
 Component comp;

public PrintComponentActionListener(  Component comp)
  {   this.comp= comp;
  }
public void actionPerformed( ActionEvent evt)
  {PrintUtilities printHelper = new PrintUtilities(comp);
//new ViewManager( ds, IViewManager.IMAGE ));
printHelper.print();
}
public static void setUpMenuItem(JMenuBar jmb, Component comp )
{setUpMenuItem(jmb.getMenu( DataSetTools.viewer.DataSetViewer.FILE_MENU_ID ),comp);
}

/**
  * This class sets up the menubar on a component with the "print" menuitem
  */
public static void setUpMenuItem(JMenu jm, Component comp )
  {JMenuItem jmi= new JMenuItem("Print");
  int nitems= jm.getItemCount();
  if( nitems < 0) nitems= 0;
  jm.add(jmi, nitems );
  jmi.addActionListener(new PrintComponentActionListener( comp));

   }


}
