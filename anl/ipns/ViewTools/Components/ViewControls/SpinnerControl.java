/*
 * File: SpinnerControl.java
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
 * Revision 1.3  2005/07/12 16:54:46  kramer
 * Now the setObjectState() method calls super.setObjectState() to set the
 * state that the superclass maintains.
 *
 * Revision 1.2  2005/06/23 20:56:00  kramer
 *
 * Added the methods isEditable() and getDefaultValue() used to see if the
 * spinner on the control is editable and to get its default value.
 *
 * Revision 1.1  2005/06/22 22:32:31  kramer
 *
 * This is a type of ViewControl that has a JLabel and JSpinner on it.  It
 * can be used to restrict the user to enter a value from a specified
 * range.  Any object (such as a Date or an integer) can be used as the
 * value and the ability for the user to manually modify the value can be
 * enabled/disabled.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.ObjectState;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class is a ViewControl that allows the user to specify a value 
 * from a range of values using a <code>JSpinner</code>.  The possible 
 * types of values displayed on this spinner are not restricted and 
 * depend on the <code>SpinnerModel</code> given when constructing this 
 * control.  Also, the spinner has a textfield next to it that displays 
 * the spinner's current value.  Using the 
 * {@link #setEditable(boolean) setEditable(boolean)} method, the 
 * editability of this textfield can be modified.  If it is uneditable, 
 * the user cannot enter arbitrary bad values.
 */
public class SpinnerControl extends ViewControl implements ChangeListener
{
   //--------------=[ Signals sent to listeners ]=----------------------------
   /**
    * "Spinner changed" - This is the signal that is sent to all 
    * listeners when the spinner's value has changed.
    */
   public static final String SPINNER_CHANGED = "Spinner changed";
   
   //------------------=[ ObjectState keys ]=---------------------------------
   /**
    * "Spinner Value" - This static constant String is the key used for 
    * referencing the state information describing the current value of 
    * this control's spinner.  The value that this key references is 
    * an Object.  The value's exact type depends on the 
    * <code>SpinnerModel</code> used when constructing this ViewControl.
    */
   public static final String SPINNER_VALUE = "Spinner value";
   
   //-----------=[ Objects used in constructing the control ]=----------------
   /** This is the spinner that is located on the control. */
   private JSpinner spinner;
   /**
    * This is the spinner's default value.  This value is context 
    * specific and depends on the SpinnerModel being used with 
    * the spinner.  Thus, this value is set in the constructor.  
    */
   private final Object DEFAULT_SPINNER_VALUE;
   /** This is the label that is placed in front of the spinner. */
   private JLabel label;
   
   /**
    * Constructs this ViewControl with the given title and a label with the 
    * given text in front of the spinner.  The spinner's value is initialized 
    * to be <code>defaultVal</code>.  The spinner has a textfield next to 
    * it that displays the spinner's value.  By default, this textfield is 
    * not editable.
    * 
    * @param con_title  The title of this control.
    * @param labelText  The text that is displayed in the label in front 
    *                   of the spinner on this control.
    * @param model      Specifies the type of data that will be displayed 
    *                   on the spinner. 
    * @param defaultVal The spinner's default value.
    */
   public SpinnerControl(String con_title, String labelText, 
                            SpinnerModel model, Object defaultVal)
   {
      super(con_title);
      
      //Create the spinner
      spinner = new JSpinner(model);
        //make the spinner's text field uneditable so the user cannot enter 
        //invalid values
        setEditable(false);
        
        //store and set the default values
        DEFAULT_SPINNER_VALUE = defaultVal;
        spinner.setValue(DEFAULT_SPINNER_VALUE);
        //listen for changes to the spinner
        spinner.addChangeListener(this);
        
      //create the label
        label = new JLabel(labelText);
        
      setLayout(new BorderLayout());
        add(label, BorderLayout.WEST);
        add(spinner, BorderLayout.CENTER);
   }
   
   /**
    * Constructs this ViewControl with the given title and a spinner.  
    * The spinner's value is initialized to be <code>defaultVal</code>.  
    * Also, the label in front of the spinner is not visible.  The spinner 
    * has a textfield next to it that displays the spinner's value.  By 
    * default, this textfield is not editable.
    * 
    * @param con_title  The title of this control.
    * @param model      Specifies the type of data that will be displayed 
    *                   on the spinner. 
    * @param defaultVal The spinner's default value.
    */
   public SpinnerControl(String con_title, SpinnerModel model, 
                            Object defaultVal)
   {
      this(con_title, "", model, defaultVal);
   }

   /**
    * Used to set the value displayed on the spinner.
    * 
    * @param value The value to place on the spinner.  Note:  This Object 
    *              should be of whatever type is accepted by the 
    *              <code>SpinnerModel</code> given to the constructor of 
    *              this class  (This <code>SpinnerModel</code> can be 
    *              accessed through the {@link #getModel() getModel()()} 
    *              method).
    * 
    * @see SpinnerModel#setValue(java.lang.Object)
    */
   public void setControlValue(Object value)
   {
      if (value!=null)
         spinner.setValue(value);
   }

   /**
    * Used to get the current value displayed on the spinner.
    * 
    * @return The spinner's current value.  The Object returned is of the 
    *         same type as the Objects returned from the 
    *         <code>SpinnerModel</code> given to the constructor of this 
    *         class (This <code>SpinnerModel</code> can be accessed 
    *         through the {@link #getModel() getModel()} method.
    * 
    * @see SpinnerModel#getValue()
    */
   public Object getControlValue()
   {
      return spinner.getValue();
   }
   
   /**
    * Used to get the current state of this control.  The state 
    * encapsulates the spinner's value.
    * 
    * @param isDefault True if the default state for this control should 
    *                  be returned.
    */
   public ObjectState getObjectState(boolean isDefault)
   {
      ObjectState state = super.getObjectState(isDefault);
      if (isDefault)
         state.insert(SPINNER_VALUE, DEFAULT_SPINNER_VALUE);
      else
         state.insert(SPINNER_VALUE, getControlValue());
      return state;
   }
   
   /**
    * Used to set the state of this control.
    * 
    * @param state Encapsulates the state information for this control.  
    */
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      //set the state that the superclass maintains
      super.setObjectState(state);
      
      Object val = state.get(SPINNER_VALUE);
      if (val!=null)
         setControlValue(val);
   }

   /**
    * Used to get an exact copy of this control.
    * 
    * @return A deep copy of this ViewControl.
    */
   public ViewControl copy()
   {
      SpinnerControl copy = new SpinnerControl(getTitle(), 
                                               getModel(), 
                                               DEFAULT_SPINNER_VALUE);
      copy.setObjectState(this.getObjectState(false));
      return copy;
   }
   
   //-----------------=[ Extra public methods ]=------------------------------
   /**
    * Used to get the spinner's model.  Access to the model gives access 
    * to the spinner's current value, previous value, and next value.  Also, 
    * the spinner's current value can be set. 
    * 
    * @return The spinner's model.
    */
   public SpinnerModel getModel()
   {
      return spinner.getModel();
   }
   
   /**
    * This control's spinner has a textfield next to it that displays the 
    * spinner's current value.  This method is used to set if this 
    * textfield is directly editable by the user.
    * 
    * @param editable True if the textfield should be editable and 
    *                 false if it shouldn't be.
    */
   public void setEditable(boolean editable)
   {
      ((JSpinner.DefaultEditor)spinner.getEditor()).
                                  getTextField().setEditable(editable);
   }
   
   /**
    * This control's spinner has a textfield next to it that displays the 
    * spinner's current value.  This method is used to see if this 
    * textfield is directly editable by the user.
    * 
    * @return True if this control's textfield can be directly edited by the 
    *         user and false if it can't be.
    */
   public boolean isEditable()
   {
      return ((JSpinner.DefaultEditor)spinner.getEditor()).
                                         getTextField().isEditable();
   }
   
   /**
    * Used to get the text of the label on this ViewControl.
    * 
    * @return This control's label's text.  If the label is not visible, 
    *         the empty string ("") is returned.
    */
   public String getLabelText()
   {
      return label.getText();
   }
   
   /**
    * Used to set the text of the label on this ViewControl.
    * 
    * @param text The label's new text.  If this is the empty string (""), 
    *             it will make the label invisible.
    */
   public void setLabelText(String text)
   {
      if (text==null)
         return;
      label.setText(text);
   }
   
   /**
    * Used to make this control's label invisible.
    */
   public void disableLabel()
   {
      setLabelText("");
   }
   
   /**
    * Used to make this control's label visible.
    * 
    * @param text The text to show on the now visible label.  If this is 
    *             the empty string (""), the label will still be invisible 
    *             after this method is invoked.
    */
   public void enableLabel(String text)
   {
      setLabelText(text);
   }
   
   /**
    * Used to test if this control's label is visible.
    * 
    * @return True if this control's label is visible and 
    *         false if it is invisible.
    */
   public boolean isLabelEnabled()
   {
      return getLabelText().equals("");
   }
   
   /**
    * This method provides a way to programmatically 
    * cause the spinner to think that its value was 
    * changed.
    */
   public void doClick()
   {
      //It is okay to use 'null' as an argument 
      //because the stateChange(....) method does 
      //not use its parameters
      stateChanged(null);
   }
   
   /**
    * Used to set if this spinner is enabled or not.
    * 
    * @param enable True if the spinner should be enabled and 
    *               false if it shouldn't be enabled.
    */
   public void setEnabled(boolean enable)
   {
      super.setEnabled(enable);
      
      label.setEnabled(enable);
      spinner.setEnabled(enable);
   }
   
   /**
    * Used to get the spinner's default value.
    * 
    * @return The spinner's default value.
    */
   public Object getDefaultValue()
   {
      return DEFAULT_SPINNER_VALUE;
   }
   
   //----=[ Implemented for the ChangeListener interface ]=--------
   /**
    * Invoked when the spinner is modified.  This method sends a message 
    * to all listeners with the message being <code>SPINNER_CHANGED</code>.
    */
   public void stateChanged(ChangeEvent e)
   {
      send_message(SPINNER_CHANGED);
   }
   
   /**
    * Testbed.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      JFrame frame = new JFrame("SpinnerControl Demo");
        frame.getContentPane().add(
              new SpinnerControl("Title", 
                                 "Some label ", 
                                 new SpinnerNumberModel(5,0,10,1), 
                                 new Integer(8)));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
      WindowShower.show(frame);
   }
}
