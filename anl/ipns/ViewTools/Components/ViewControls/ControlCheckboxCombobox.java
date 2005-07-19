/*
 * File: ControlCheckboxCombobox.java
 *
 * Copyright (C) 2005, Dominic Kramer
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
 * Primary   Dominic Kramer <kramerd@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
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
 * $Log$
 * Revision 1.2  2005/07/19 19:00:42  kramer
 * Added javadocs and modified the main method to test the
 * set/getObjectState() methods.
 *
 * Revision 1.1  2005/06/23 21:10:33  kramer
 *
 * This is a special type of control that has a combobox and a checkbox.
 * When the checkbox is selected/deselected the combobox is enabled/
 * disabled.  This was originally designed to be used in the
 * ContourViewComponent to select line styles.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.ObjectState;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * This is control that has a checkbox with a combobox next to it.  When 
 * the checkbox is checked/unchecked the combobox is enabled/disabled.
 */
public class ControlCheckboxCombobox extends CheckedControl
{
   /**
    * Used to construct the control with the given parameters.
    * 
    * @param con_title The title for this control.
    * @param isChecked True if the checkbox should be initially checked and 
    *                  false if it shouldn't be.
    * @param label The label displayed next to the checkbox on this control.
    * @param fields The values displayed in the combobox.
    */
   public ControlCheckboxCombobox(String con_title, boolean isChecked,
                                  String label, String[] fields)
   {
      super(con_title, isChecked, label, generateCombobox(fields));
   }

   /**
    * Used to construct the control without a title or label.
    * 
    * @param isChecked True if the checkbox should be initially checked and 
    *                  false if it shouldn't be.
    * @param fields The values displayed in the combobox.
    */
   public ControlCheckboxCombobox(boolean isChecked, String[] fields)
   {
      super(isChecked, generateCombobox(fields));
   }
   
   /**
    * Used to create a <code>LabelCombobox</code> with the specified values.
    * 
    * @param fields The values displayed in the combobox.
    * @return A <code>LabelCombobox</code> holding the specified values.
    */
   private static LabelCombobox generateCombobox(String[] fields)
   {
      return new LabelCombobox("", fields);
   }
   
   /**
    * Used to get the combobox displayed on this control.
    * 
    * @return The combobox displayed on this control.
    */
   public LabelCombobox getComboBox()
   {
      return (LabelCombobox)getSubControl();
   }
   
   /**
    * Used to get the index of the selected item in the combobox on this 
    * control.
    * 
    * @return The index of this control's combobox's selected item.
    */
   public int getSelectedIndex()
   {
      return getComboBox().getSelectedIndex();
   }
   
   /**
    * Used to get String that is selected on this control's combobox.
    * 
    * @return This control's combobox's selected String.
    */
   public String getSelectedString()
   {
      return (String)getComboBox().getSelectedItem();
   }
   
   /**
    * Used to set the index of the selected item in the combobox on this 
    * control.
    * 
    * @param index The new selected index of this control's combobox.
    */
   public void setSelectedIndex(int index)
   {
      getComboBox().setSelectedIndex(index);
   }
   
   /**
    * Displays a frame showing this control.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      final String STORE_TEXT = "Store ObjectState";
      final String LOAD_TEXT = "Load ObjectState";
      
      final ControlCheckboxCombobox combobox = 
         new ControlCheckboxCombobox("Title", true, "line style", 
                                     new String[] {"Solid", 
                                                   "Dashed", 
                                                   "Dashed-Dotted", 
                                                   "Dotted"});
      
      ActionListener stateListener = new ActionListener()
      {
         ObjectState state = new ObjectState();
         
         public void actionPerformed(ActionEvent event)
         {
            String actionCommand = event.getActionCommand();
            
            if (actionCommand.equals(STORE_TEXT))
               state = combobox.getObjectState(false);
            else if (actionCommand.equals(LOAD_TEXT))
               combobox.setObjectState(state);
            else
               System.out.println("Unrecognized ActionCommand:  "+
                                   actionCommand);
         }
      };
      
      JFrame stateFrame = new JFrame();
        stateFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container pane = stateFrame.getContentPane();
          pane.setLayout(new FlowLayout(FlowLayout.LEFT));
          JButton storeButton = new JButton(STORE_TEXT);
            storeButton.addActionListener(stateListener);
        pane.add(storeButton);
          JButton loadButton = new JButton(LOAD_TEXT);
            loadButton.addActionListener(stateListener);
        pane.add(loadButton);
      stateFrame.pack();
      stateFrame.setVisible(true);
      
      JFrame frame = new JFrame("ControlCheckboxCombobox Demo");
        frame.getContentPane().add(combobox);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
      WindowShower.show(frame);
   }
}
