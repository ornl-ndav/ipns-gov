/*
 * File: ButtonControl.java
 *
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
 *  Revision 1.3  2004/03/12 02:24:52  millermi
 *  - Changed package, fixed imports.
 *
 */
  
package gov.anl.ipns.ViewTools.Components.ViewControls;
 
 import javax.swing.*;
 import javax.swing.event.*;
 import java.awt.event.*;
 import java.awt.GridLayout;
 //import java.lang.Object.*;
 
 //import DataSetTools.util.*;

/**
 * This class is a ViewControl (ActiveJPanel) with a generic button for use 
 * by ViewComponents. It includes a hook to send out messages when the  
 * button has been pressed.
 */ 
public class ButtonControl extends ViewControl
{
   public JButton button;

  /**
   *  constructor 
   */ 
   public ButtonControl(String buttonName)
   {  
      super("");
      button = new JButton(buttonName);
      this.add(button);
      button.addActionListener( new ButtonListener() ); 
     
   }
  



 /*
   * CheckboxListener moniters the JCheckBox private data member for the
   * ControlCheckbox class
   */
   private class ButtonListener implements ActionListener
   { 
      public void actionPerformed( ActionEvent ae )
      {
         send_message(BUTTON_PRESSED);
         //System.out.println("the button was pressed");
      }
   }

  /*
   *  For testing purposes only
   */
   public static void main(String[] args)
   { 
      String name = "button";


      ButtonControl button = new ButtonControl(name);
      

      JFrame frame = new JFrame();
      frame.getContentPane().setLayout( new GridLayout(2,1) );
      frame.setTitle("LabelCombobox Test");
      frame.setBounds(0,0,135,120);
      frame.getContentPane().add(button);

      frame.show();
      

   }
}
