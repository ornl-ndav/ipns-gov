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
 * Contact : Alok Chatterjee <achatterjee@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.7  2005/02/02 21:50:18  dennis
 *  Cleaned up formatting.
 *
 *  Revision 1.6  2004/03/12 17:54:03  rmikk
 *  Fixed package names.
 *  Fixed JMenuBar "File" Jmenu search algorithm
 *
 *  Revision 1.5  2004/03/12 17:21:14  hammonds
 *  Moved from DataSetTools.viewer to gov.anl.ipns.Util.Sys
 *
 *  Revision 1.4  2004/03/10 16:22:52  millermi
 *  - Added static method getActiveMenuItem() that takes in
 *    a String text and a component, and returns a JMenuItem.
 *    The menu item returned will invoke the java print option when
 *    clicked.
 *
 *  Revision 1.3  2002/11/27 23:24:18  pfpeterson
 *  standardized header
 *
 *  Revision 1.2  2002/05/30 23:05:05  chatterjee
 *  Closed the comment lines.
 *
 *  Revision 1.1  2002/05/30 22:55:44  chatterjee
 *  ActionListener for print requests from the viewers
 *
*/

package gov.anl.ipns.Util.Sys;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
/*import IsawGUI.*;*/


/**
  * This class instantiates the PrintUtilities class and calls its print
  * method. The component that is to be printed is passed as a parameter
  * to this class.
  */

public class PrintComponentActionListener  implements ActionListener
{
  JMenu jm;
  Component comp;

  public PrintComponentActionListener( Component comp )
  {   
    this.comp= comp;
  }

  public void actionPerformed( ActionEvent evt )
  {
    PrintUtilities printHelper = new PrintUtilities(comp);
    //new ViewManager( ds, IViewManager.IMAGE ));
    printHelper.print();
  }

  public static void setUpMenuItem( JMenuBar jmb, Component comp )
  {
    if( jmb == null)
      return;
    
     for( int i=0; i< jmb.getMenuCount();i++){
        JMenu jm = jmb.getMenu(i);
        if( jm.getText().equals("File")){
           setUpMenuItem( jm, comp);
           return;
         }
     }
     JMenu jm = new JMenu("File");
     jmb.add(jm);
     setUpMenuItem( jm, comp);
  }

  /**
   * This class sets up the menubar on a component with the "print" menuitem
   */
  public static void setUpMenuItem( JMenu jm, Component comp )
  {
    JMenuItem jmi= new JMenuItem("Print");
    int nitems= jm.getItemCount();
    if( nitems < 0) 
      nitems= 0;
    jm.add(jmi, nitems );
    jmi.addActionListener(new PrintComponentActionListener( comp));
  }
   
  /**
   * This method will return a JMenuItem with the provided text name. The
   * menu item will have a print listener added to it. If clicked on,
   * this menu item will print out the component passed in.
   *
   *  @param  menu_text Display text on the JMenuItem
   *  @param  comp Component to be printed.
   *  @return menu item with listener to initiate printing routine.
   */
  public static JMenuItem getActiveMenuItem( String menu_text, Component comp )
  {
    JMenuItem jmi = new JMenuItem(menu_text);
    jmi.addActionListener( new PrintComponentActionListener(comp) );
    return jmi;
  }

}
