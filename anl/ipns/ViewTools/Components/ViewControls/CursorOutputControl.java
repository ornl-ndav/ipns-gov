
/*
 * File: CursorOutputControl.java
 *
 * Copyright (C) 2003 Brent Serum
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2003/10/16 17:06:00  millermi
 *  - Restructured this control to utilize the ViewControl
 *    base class.
 *
 */

package DataSetTools.components.View.ViewControls;

import javax.swing.Box;
import javax.swing.JFrame;

import DataSetTools.viewer.ViewerState;
import DataSetTools.components.ui.TextValueUI;

/**
  * This class is a ViewControl (ActiveJPanel) with text fields set up for
  * cursor output.
  */
public class CursorOutputControl extends ViewControl
{
   private TextValueUI[] TextField;
   private ViewControl cursorPanel;
   private Box vert_box = new Box(1);

/**
  *  The Constructor initilizes the values for the cursor output,
  *  and sets the heddings and the border.
  *
  * @param name An array of strings which are the names of the text fields
  *             which display the cursor output.
  */
   public CursorOutputControl( String[] name)
   {
     super("");
     cursorPanel = this;
     if (name.length<1) return;

     TextField = new TextValueUI[name.length];
     for(int i = 0; i < name.length; i++)
     { 
       TextField[i] = new TextValueUI(name[i], Float.NaN);
       TextField[i].setEditable(false);
       vert_box.add(TextField[i]);
     }
     setTitle("Cursor");
     add(vert_box);


   }  
  /**
   * This function sets the border title.
   *
   * @param title The string which holds the title of the border.
   *
   */
   public void setBorderTitle(String  title)
   {
     setTitle(title);
   }

  /**
   * This function sets the value to be displayed in the text area.
   *
   * @param index The int which represents the text area for the value to
   *               be displayed in.
   *
   * @param value The floating point value to be dispyaled.
   */
   public void setValue(int index, float value)
   {
      TextField[index].setValue(value);
   }
   
  /**
   * Test purposes only...
   */
   public static void main( String args[] ) 
   {
     JFrame tester = new JFrame("CursorOutputControl Test");
     tester.setBounds(0,0,100,200);
     tester.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
     String[] menu = {"menu1","Menu2","Menu3","Menu4","Menu5"};
     CursorOutputControl coc = new CursorOutputControl( menu );
     coc.setBorderTitle("Border");
     tester.getContentPane().add(coc);
     tester.setVisible(true);
   }
} 
