/*
 * File: LabelCombobox.java
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
 *  $Log:
 */
  
package DataSetTools.components.View.ViewControls;
 
 import javax.swing.*;
 import javax.swing.event.*;
 import java.awt.event.*;
 import java.awt.*;
 import java.lang.Object.*;
 
 import DataSetTools.viewer.ViewerState;
 import DataSetTools.util.*;

/**
 * This class is a ViewControl (ActiveJPanel) with a generic checkbox for use 
 * by ViewComponents. It includes a hook to send out messages when the  
 * checkbox has been checked or unchecked.
 */ 
public class LabelCombobox extends ViewControl
{
   public JComboBox cbox;
   private JPanel thepanel = new JPanel();
   private JLabel cboxLabel;
   private Box box1 = new Box(1);
   private Box box2 = new Box(1);
   private FlowLayout f_layout;
   public Box theBox = new Box(0);

  
 
  /**
   * constructor 
   */ 
   public LabelCombobox(String p_label, String[] fields)
   {  
      super("");

      f_layout = new FlowLayout(0);
 
      cboxLabel = new JLabel(p_label);
      thepanel.setLayout(f_layout);
      thepanel.add(cboxLabel);

      cbox = new JComboBox(fields);
      
      box1.add(thepanel);
      box2.add(cbox);

      theBox.add(box1);
      theBox.add(box2);
  
      this.add(theBox);
      cbox.addActionListener( new ComboboxListener() ); 
     
   }
  


  /**
   * Allows the combo box to be initialized to the index.
   * The constructor initializes the combobox to index 0.
   */
   public void setSelected(int index)
   {
      cbox.setSelectedIndex(index);
     
   }
 /*
   * CheckboxListener moniters the JCheckBox private data member for the
   * ControlCheckbox class
   */
   private class ComboboxListener implements ActionListener
   { 
      public void actionPerformed( ActionEvent ae )
      {
         send_message(COMBOBOX_CHANGED);
         //System.out.println("the selected item is: " + cbox.getSelectedItem());
      }
   }

  /*
   *  For testing purposes only
   */
   public static void main(String[] args)
   { 
      String[] line_type = new String[5];
      line_type[0] = "Solid"; 
      line_type[1] = "Dashed";     
      line_type[2] = "Dotted"; 
      line_type[3] = "Dash Dot Dot"; 
      line_type[4] = "Transparent";

      String[] line_width = new String[5];
      line_width[0] = "1";
      line_width[1] = "2";
      line_width[2] = "3";
      line_width[3] = "4";
      line_width[4] = "5";

      LabelCombobox check = new LabelCombobox("line_type", line_type);
      LabelCombobox check2 = new LabelCombobox("line_width", line_width);

      JFrame frame = new JFrame();
      frame.getContentPane().setLayout( new GridLayout(2,1) );
      frame.setTitle("LabelCombobox Test");
      frame.setBounds(0,0,135,120);
      frame.getContentPane().add(check);
      frame.getContentPane().add(check2);
     
      frame.show();
      

   }
}
