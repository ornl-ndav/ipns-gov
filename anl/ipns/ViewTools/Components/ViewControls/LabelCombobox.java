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
 *  $Log$
 *  Revision 1.5  2005/03/14 19:21:44  serumb
 *  Changed public variables to private and added methods to get them.
 *
 *  Revision 1.4  2005/03/09 22:36:09  millermi
 *  - Added methods get/setControlValue() and messaging of VALUE_CHANGED
 *    to enable controls to be linked.
 *  - Added "cm" as parameter to main() to test control with the
 *    ControlManager.
 *
 *  Revision 1.3  2004/03/15 23:53:54  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.2  2004/03/12 02:24:53  millermi
 *  - Changed package, fixed imports.
 *
 */
  
package gov.anl.ipns.ViewTools.Components.ViewControls;
 
 import javax.swing.*;
 import java.awt.event.*;
 import java.awt.*;
 
 import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * This class is a ViewControl (ActiveJPanel) with a generic checkbox for use 
 * by ViewComponents. It includes a hook to send out messages when the  
 * checkbox has been checked or unchecked.
 */ 
public class LabelCombobox extends ViewControl
{
   private JComboBox cbox;
   private JPanel thepanel = new JPanel();
   private JLabel cboxLabel;
   private Box box1 = new Box(1);
   private Box box2 = new Box(1);
   private FlowLayout f_layout;
   private Box theBox = new Box(0);
   private boolean ignore_change = false;
 
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
  * Set the selected index of the combo box.
  *
  *  @param  value Integer containing the selected index.
  */
  public void setControlValue(Object value)
  {
    if( value == null || !(value instanceof Integer) )
      return;
    int int_value = ((Integer)value).intValue();
    // Do nothing if int_value is not a valid index. If -1, then none are
    // selected.
    if( int_value >= cbox.getItemCount() || int_value < -1 )
      return;
    ignore_change = true;
    setSelected(int_value);
    ignore_change = false;
  }
  
 /**
  * Get selected index of the combobox as an Integer.
  *
  *  @return Returns an Integer containing the selected index.
  */
  public Object getControlValue()
  {
    return new Integer(cbox.getSelectedIndex());
  }

  /**
   * Allows the combo box to be initialized to the index.
   * The constructor initializes the combobox to index 0.
   */
   public void setSelected(int index)
   {
      cbox.setSelectedIndex(index);
     
   }
  
  /**
   * Gets the box that the control is put in.
   *
   * @return Returns and object of type Box.
   */ 
   public Box getBox()
   {
     return theBox;
   }  
  
  /**
   * Gets the Combobox.
   *
   * @return returns an object of type JComboBox.
   */
   public JComboBox getCBox()
   {
     return cbox;
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
         //System.out.println("the selected item is: " + cbox.getSelectedItem())
         // This if statement will prevent VALUE_CHANGED to be sent out when
         // the setControlValue() method is called.
         if( !ignore_change )
           send_message( VALUE_CHANGED );
      }
   }

  /*
   *  For testing purposes only
   */
   public static void main(String[] args)
   {
      // If cm is passed in, test with control manager.
      if( args.length > 0 && args[0].equalsIgnoreCase("cm") )
      {
        String[] alist = {"A1","A2","A3"};
        String[] blist = {"B1","B2","B3","B4"};
        String[] clist = {"C1","C2"};
	
        ViewControl[] controls = new ViewControl[3];
        controls[0] = new LabelCombobox("Combobox1",alist);
        controls[1] = new LabelCombobox("Combobox2",blist);
        controls[2] = new LabelCombobox("Combobox3",clist);
      
        String[] keys = new String[3];
        keys[0] = "Combobox";
        keys[1] = "Combobox";
        keys[2] = "Combobox";
      
        JFrame frame = ControlManager.makeManagerTestWindow( controls, keys );
        WindowShower shower = new WindowShower(frame);
        java.awt.EventQueue.invokeLater(shower);
        shower = null;
        return;
      }
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
