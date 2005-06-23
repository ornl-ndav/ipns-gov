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
 * Revision 1.1  2005/06/23 18:29:56  kramer
 * This is a ViewControl that has a spinner and a checkbox on it.  When,
 * the checkbox is selected/deselected the checkbox is enabled/disabled.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.ObjectState;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * 
 */
public class ControlCheckboxSpinner extends ViewControl
{
   /**
    * "Checkbox state key" - This static constant String is the key used 
    * for referencing the state information about the checkbox on this 
    * control.  The value that this key references is an ObjectState.
    */
   public static final String CHECKBOX_STATE_KEY = "Checkbox state key";
   /**
    * "Spinner state key" - This static constant String is the key used 
    * for referencing the state information about the spinner on this 
    * control.  The value that this key references is an ObjectState.
    */
   public static final String SPINNER_STATE_KEY = "Spinner state key";
   
   /**
    * The checkbox that is contained on this control.  The label that 
    * can be displayed on this control is the label associated with this 
    * checkbox.
    */
   private ControlCheckbox checkbox;
   /**
    * The spinner that is contained on this control.
    */
   private SpinnerControl spinner;
   
   
   public ControlCheckboxSpinner(String con_title, 
                                 boolean isChecked, String label, 
                                 SpinnerModel model, Object initialVal, 
                                 Object defaultVal)
   {
      super(con_title);
      
      //create the spinner
      spinner = new SpinnerControl("", model, defaultVal);
      spinner.setBorderVisible(false);
      if (initialVal!=null)
         spinner.setControlValue(initialVal);
      
      //create the checkbox
      checkbox = new ControlCheckbox(isChecked);
      checkbox.setBorderVisible(false);
      if (label!=null)
         checkbox.setText(label);
      checkbox.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            if (event.getActionCommand().
                  equals(ControlCheckbox.CHECKBOX_CHANGED))
               setSpinnerEnabled(isChecked());
         }
      });
      checkbox.doClick();
      
      //add the subcontrols to this control
      setLayout(new FlowLayout(FlowLayout.LEFT));
        add(checkbox);
        add(spinner);
   }
   
   public ControlCheckboxSpinner(boolean isChecked, 
                                 SpinnerModel model, Object defaultVal)
   {
      this("", isChecked, null, model, defaultVal, defaultVal);
   }
   
   /**
    * Used to set the value of the spinner.  This value should be of 
    * whatever type the <code>SpinnerModel</code> given to the 
    * constructor of this class can handle.
    * 
    * @param value The new value of the spinner.
    */
   public void setControlValue(Object value)
   {
      spinner.setControlValue(value);
   }
   
   /**
    * Used to get the current value of the spinner.  The value returned is 
    * whatever type the <code>SpinnerModel</code> given to the constructor 
    * of this class is made to work with.
    * 
    * @return The current value of the spinner.
    */
   public Object getControlValue()
   {
      return spinner.getControlValue();
   }
   
   /**
    * Used to get a copy of this control.
    * 
    * @return A deep copy of this control.
    */
   public ViewControl copy()
   {
      ControlCheckboxSpinner copy = 
         new ControlCheckboxSpinner(getTitle(), isChecked(), getLabelText(), 
                                    getSpinner().getModel(), 
                                    getSpinnerValue(), 
                                    spinner.getDefaultValue());
      copy.setObjectState(this.getObjectState(false));
      return copy;
   }
   
   /**
    * Used to get the state of this control.
    * 
    * @param isDefault If true, the default state of this control is 
    *                  returned.  If false, the current state of this 
    *                  control is returned.
    */
   public ObjectState getObjectState(boolean isDefault)
   {
      ObjectState state = super.getObjectState(isDefault);
      
      state.insert(CHECKBOX_STATE_KEY, checkbox.getObjectState(isDefault));
      state.insert(SPINNER_STATE_KEY, spinner.getObjectState(isDefault));
      
      return state;
   }
   
   /**
    * Used to set the state of this control.
    * 
    * @param state Encapsulates the state of this control.
    */
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      Object val = state.get(CHECKBOX_STATE_KEY);
      if (val!=null)
         checkbox.setObjectState((ObjectState)val);
      
      val = state.get(SPINNER_STATE_KEY);
      if (val!=null)
         spinner.setObjectState((ObjectState)val);
   }
   
   /**
    * Overriden so that the given <code>ActionListener</code> is 
    * added to both the checkbox and spinner on this control.
    * 
    * @param listener The listener that wants to listen to the 
    *                 checkbox and spinner on this control.
    */
   public void addActionListener(ActionListener listener)
   {
      checkbox.addActionListener(listener);
      spinner.addActionListener(listener);
   }
   
   /**
    * Overriden so that the given <code>ActionListener</code> is 
    * removed from both the checkbox and spinner on this control.
    * 
    * @param listener The listener that wants to stop listening to 
    *                 the checkbox and spinner on this control.
    */
   public void removeActionListener(ActionListener listener)
   {
      checkbox.removeActionListener(listener);
      spinner.removeActionListener(listener);
   }
   
   /**
    * Used to get direct access to the spinner on this control.
    * 
    * @return The spinner on this control.
    */
   public SpinnerControl getSpinner()
   {
      return spinner;
   }
   
   /**
    * Used to get direct access to the checkbox on this control.  The 
    * checkbox also contains the this control's label.
    * 
    * @return The checkbox on this control.
    */
   public ControlCheckbox getCheckbox()
   {
      return checkbox;
   }
   
   /**
    * Used to determine if the the checkbox on this control is checked.
    * 
    * @return True if the checkbox is checked and 
    *         false it it isn't.
    */
   public boolean isChecked()
   {
      return checkbox.isSelected();
   }
   
   /**
    * Used to set if the checkbox on this control is checked or not.
    * 
    * @param checked True if the checkbox should be checked and 
    *                false if it shouldn't be.
    */
   public void setChecked(boolean checked)
   {
      checkbox.setSelected(checked);
   }
   
   /**
    * Used to get the label on this control.
    * 
    * @return The label on this control.
    */
   public String getLabelText()
   {
      return checkbox.getText();
   }
   
   /**
    * Used to set the label on this control.
    * 
    * @param label The control's new label.
    */
   public void setLabelText(String label)
   {
      checkbox.setText(label);
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
      return spinner.getControlValue();
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
      spinner.setControlValue(value);
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
      return spinner.isEditable();
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
      spinner.setEditable(editable);
   }
   
   /**
    * Used to enable/disable this entire control.  Both the spinner and 
    * checkbox will become enabled/disabled.
    * 
    * @param enabled True to enable this control and 
    *                false to disable it.
    */
   public void setEnabled(boolean enabled)
   {
      super.setEnabled(enabled);
      setCheckboxEnabled(enabled);
      setSpinnerEnabled(enabled);
   }
   
   /**
    * Used to enable/disable the checkbox on this control.
    * 
    * @param enabled True to enable the checkbox and 
    *                false to disable it.
    */
   public void setCheckboxEnabled(boolean enabled)
   {
      checkbox.setEnabled(enabled);
   }
   
   /**
    * Used to enable/disable the spinner on this control.
    * 
    * @param enabled True to enable the spinner and 
    *                false to disable it.
    */
   public void setSpinnerEnabled(boolean enabled)
   {
      spinner.setEnabled(enabled);
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
