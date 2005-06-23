/*
 * File: ControlCheckboxSpinner.java
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
 * Revision 1.2  2005/06/23 20:58:18  kramer
 * Made this class extend CheckedControl which does most of this class's
 * work now.
 *
 * Revision 1.1  2005/06/23 18:29:56  kramer
 *
 * This is a ViewControl that has a spinner and a checkbox on it.  When,
 * the checkbox is selected/deselected the checkbox is enabled/disabled.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.Sys.WindowShower;

import javax.swing.JFrame;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * 
 */
public class ControlCheckboxSpinner extends CheckedControl
{
   public ControlCheckboxSpinner(String con_title, 
                                 boolean isChecked, String label, 
                                 SpinnerModel model, Object defaultVal, 
                                 Object initialVal)
   {
      super(con_title, isChecked, label, 
            generateSpinner(model, defaultVal, initialVal));
   }
   
   public ControlCheckboxSpinner(boolean isChecked, 
                                 SpinnerModel model, Object defaultVal, 
                                 Object initialVal)
   {
      super(isChecked, generateSpinner(model, defaultVal, initialVal));
   }
   
   private static SpinnerControl generateSpinner(SpinnerModel model, 
                                                 Object defaultVal, 
                                                 Object initialVal)
   {
      SpinnerControl spinner = new SpinnerControl("", model, defaultVal);
      spinner.setBorderVisible(false);
      if (initialVal!=null)
         spinner.setControlValue(initialVal);
      
      return spinner;
   }
   
   /**
    * Used to get direct access to the spinner on this control.
    * 
    * @return The spinner on this control.
    */
   public SpinnerControl getSpinner()
   {
      return (SpinnerControl)getSubControl();
   }
   
   /**
    * Used to get the spinner's value.
    * 
    * @return The spinner's value.
    * 
    * @see #getControlValue()
    */
   public Object getSpinnerValue()
   {
      return getSpinner().getControlValue();
   }
   
   /**
    * Used to set the spinner's value.
    * 
    * @param value The spiner's new value.
    * 
    * @see #setControlValue(Object)
    */
   public void setSpinnerValue(Object value)
   {
      getSpinner().setControlValue(value);
   }
   
   /**
    * Used to determine if the textfield showing the spinner's 
    * value is directly editable by the user.
    * 
    * @return True if the spinner's value is directly editable by 
    *         the user and false if it isn't.
    */
   public boolean isSpinnerEditable()
   {
      return getSpinner().isEditable();
   }
   
   /**
    * The spinner has a textfield that shows its current value.  This method 
    * is used to set if the user should be able to directly modify this 
    * textfield.
    * 
    * @param editable True if the user should be directly able to modify the 
    *                 spinner's value and false if he/she shouldn't be.
    */
   public void setSpinnerEditable(boolean editable)
   {
      getSpinner().setEditable(editable);
   }
   
   /**
    * Used to enable/disable the spinner on this control.
    * 
    * @param enabled True to enable the spinner and 
    *                false to disable it.
    */
   public void setSpinnerEnabled(boolean enabled)
   {
      getSpinner().setEnabled(enabled);
   }
   
   /**
    * Testbed.  Opens a <code>JFrame</code> displaying this control.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      Integer defaultSpinVal = new Integer(0);
      
      ControlCheckboxSpinner spinner = 
         new ControlCheckboxSpinner("Title", true, "Some label", 
               new SpinnerNumberModel(defaultSpinVal.intValue(), 0, 100, 1), 
               defaultSpinVal, defaultSpinVal);
      
      JFrame frame = new JFrame("ControlCheckboxSpinner Demo");
        frame.getContentPane().add(spinner);
        frame.pack();
      WindowShower.show(frame);
   }
}
