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
 * Revision 1.1  2005/06/23 21:10:33  kramer
 * This is a special type of control that has a combobox and a checkbox.
 * When the checkbox is selected/deselected the combobox is enabled/
 * disabled.  This was originally designed to be used in the
 * ContourViewComponent to select line styles.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.Sys.WindowShower;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * 
 */
public class ControlCheckboxCombobox extends CheckedControl
{

   /**
    * @param con_title
    * @param isChecked
    * @param label
    */
   public ControlCheckboxCombobox(String con_title, boolean isChecked,
                                  String label, String[] fields)
   {
      super(con_title, isChecked, label, generateCombobox(fields));
   }

   /**
    * @param isChecked
    */
   public ControlCheckboxCombobox(boolean isChecked, String[] fields)
   {
      super(isChecked, generateCombobox(fields));
   }
   
   private static LabelCombobox generateCombobox(String[] fields)
   {
      return new LabelCombobox("", fields);
   }
   
   public LabelCombobox getComboBox()
   {
      return (LabelCombobox)getSubControl();
   }
   
   public int getSelectedIndex()
   {
      return getComboBox().getSelectedIndex();
   }
   
   public void setSelectedIndex(int index)
   {
      getComboBox().setSelectedIndex(index);
   }
   
   public String getSelectedString()
   {
      return (String)getComboBox().getSelectedItem();
   }

   public static void main(String[] args)
   {
      ControlCheckboxCombobox combobox = 
         new ControlCheckboxCombobox("Title", true, "line style", 
                                     new String[] {"Solid", 
                                                   "Dashed", 
                                                   "Dashed-Dotted", 
                                                   "Dotted"});
      JFrame frame = new JFrame("ControlCheckboxCombobox Demo");
        frame.getContentPane().add(combobox);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
      WindowShower.show(frame);
   }
}
