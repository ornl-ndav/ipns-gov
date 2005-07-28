/*
 * File: CheckedControl.java
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
 * Revision 1.4  2005/07/28 15:43:11  kramer
 * Added some needed imports.
 *
 * Revision 1.3  2005/07/19 19:00:43  kramer
 *
 * Added javadocs and modified the main method to test the
 * set/getObjectState() methods.
 *
 * Revision 1.2  2005/07/12 16:54:46  kramer
 *
 * Now the setObjectState() method calls super.setObjectState() to set the
 * state that the superclass maintains.
 *
 * Revision 1.1  2005/06/23 21:07:28  kramer
 *
 * This is a generic type of control that has a checkbox and a ViewControl
 * placed side by side.  When the checkbox is selected/deselected the
 * ViewControl becomes enabled/disabled.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.ObjectState;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * This is a <code>ViewControl</code> that has a checkbox, label, and 
 * another "sub" <code>ViewControl</code> placed side-by-side.  When, the 
 * checkbox is checked/unchecked the sub <code>ViewControl</code> is 
 * enabled/disabled.
 * <p>
 * This control is designed to be used by components that have certain 
 * functionality that is only available if the user selects to have the 
 * functionality available.  In this case, the user would check the 
 * checkbox, and the <code>ViewControl</code> to control the component's 
 * functionality would be enabled.  Otherwise, the <code>ViewControl</code> 
 * would be disabled because it would not be applicable.
 */
public class CheckedControl extends ViewControl
{
   /**
    * "Checkbox state key" - This static constant String is the key used 
    * for referencing the state information about the checkbox on this 
    * control.  The value that this key references is an ObjectState.
    */
   public static final String CHECKBOX_STATE_KEY = "Checkbox state key";
   /**
    * "Subcontrol state key" - This static constant String is the key used 
    * for referencing the state information about the sub-ViewControl 
    * placed on this control.  The value that this key references 
    * is an ObjectState.
    */
   public static final String SUBCONTROL_STATE_KEY = "Subcontrol state key";
   
   /**
    * The checkbox that is contained on this control.  The label that 
    * can be displayed on this control is the label associated with this 
    * checkbox.
    */
   private ControlCheckbox checkbox;
   /**
    * This is the other ViewControl placed on the gui next to the 
    * checkbox.
    */
   private ViewControl subControl;
   
   /**
    * Creates this ViewControl with the given title.  The layout of the 
    * control is that it has a checkbox, a label, and another ViewControl 
    * all lined up horizontally.
    * 
    * @param con_title The control's title.
    * @param isChecked True if the checkbox on this control should be 
    *                  checked and false if it shouldn't be.
    * @param label The text of the label that is on this control.  If this 
    *              value is <code>null</code>, the label is not placed on 
    *              the control.
    * @param subControl The other ViewControl to place on this control.
    */
   public CheckedControl(String con_title, boolean isChecked, String label, 
                         ViewControl subControl)
   {
      super(con_title);
      
      //store the subcontrol
      this.subControl = subControl;
      this.subControl.setBorderVisible(false);
      
      //create the checkbox
      //for now tell the checkbox to in the opposite state that the 
      //user wants because the doClick() method will be called to 
      //initialize the checkbox and it will reverse checkbox's state
      this.checkbox = new ControlCheckbox(!isChecked);
      this.checkbox.setBorderVisible(false);
      if (label!=null)
         this.checkbox.setText(label);
      this.checkbox.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            if (event.getActionCommand().
                  equals(ControlCheckbox.CHECKBOX_CHANGED))
               setSubControlEnabled(isChecked());
         }
      });
      this.checkbox.doClick();
      
      //add the subcontrols to this control
      setLayout(new BorderLayout());
        add(this.checkbox, BorderLayout.WEST);
        add(this.subControl, BorderLayout.CENTER);
   }
   
   /**
    * Creates this ViewControl with an empty title.  The layout of the 
    * control is that it has a checkbox another ViewControl lined up 
    * horizontally.  The control has "" set as its title and doesn't have 
    * a label.
    * 
    * @param isChecked True if the checkbox on this control should be 
    *                  checked and false if it shouldn't be.
    * @param subControl The other ViewControl to place on this control.
    */
   public CheckedControl(boolean isChecked, ViewControl subControl)
   {
      this("", isChecked, null, subControl);
   }
   
   /**
    * Used to set the value of the other ViewControl placed on this 
    * control.
    * 
    * @param value The value to give to the other ViewControl on this 
    *              control.
    */
   public void setControlValue(Object value)
   {
      subControl.setControlValue(value);
   }
   
   /**
    * Used to get the value of the other ViewControl on this control.
    * 
    * @return The value of the other ViewControl on this control.
    */
   public Object getControlValue()
   {
      return subControl.getControlValue();
   }
   
   /**
    * Used to get a copy of this control.
    * 
    * @return A deep copy of this control.
    */
   public ViewControl copy()
   {
      CheckedControl copy = 
         new CheckedControl(getTitle(), isChecked(), getLabelText(), 
                            getSubControl());
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
      state.insert(SUBCONTROL_STATE_KEY, subControl.getObjectState(isDefault));
      
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
      
      //set the state that the superclass maintains
      super.setObjectState(state);
      
      Object val = state.get(CHECKBOX_STATE_KEY);
      if (val!=null)
         checkbox.setObjectState((ObjectState)val);
      
      val = state.get(SUBCONTROL_STATE_KEY);
      if (val!=null)
         subControl.setObjectState((ObjectState)val);
   }
   
   /**
    * Overriden so that the given <code>ActionListener</code> is 
    * added to both the checkbox and other control on this control.
    * 
    * @param listener The listener that wants to listen to the 
    *                 checkbox and other control on this control.
    */
   public void addActionListener(ActionListener listener)
   {
      checkbox.addActionListener(listener);
      subControl.addActionListener(listener);
   }
   
   /**
    * Overriden so that the given <code>ActionListener</code> is 
    * removed from both the checkbox and other control on this control.
    * 
    * @param listener The listener that wants to stop listening to 
    *                 the checkbox and other control on this control.
    */
   public void removeActionListener(ActionListener listener)
   {
      checkbox.removeActionListener(listener);
      subControl.removeActionListener(listener);
   }
   
   /**
    * Used to get direct access to the other control on this control.
    * 
    * @return The other control on this control.
    */
   public ViewControl getSubControl()
   {
      return subControl;
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
    * Used to enable/disable this entire control.  Both the other
    * control and the checkbox will become enabled/disabled.
    * 
    * @param enabled True to enable this control and 
    *                false to disable it.
    */
   public void setEnabled(boolean enabled)
   {
      super.setEnabled(enabled);
      setCheckboxEnabled(enabled);
      setSubControlEnabled(enabled);
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
    * Used to enable/disable the other control on this control.
    * 
    * @param enabled True to enable the other control and 
    *                false to disable it.
    */
   public void setSubControlEnabled(boolean enabled)
   {
      subControl.setEnabled(enabled);
   }
   
   /**
    * Testbed.  Opens a <code>JFrame</code> displaying this control.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      final String STORE_TEXT = "Store ObjectState";
      final String LOAD_TEXT = "Load ObjectState";
      final Integer defaultSpinVal = new Integer(0);
      
      final ViewControl subControl = 
         new FieldEntryControl(new String[] {"a", "b", "c", "d", "e"});
      //   new ColorControl("", Color.BLUE, ColorSelector.TABBED);
      //   new SpinnerControl("", 
      //      new SpinnerNumberModel(defaultSpinVal.intValue(),0,10,1), 
      //         defaultSpinVal);
      
      final CheckedControl control = 
         new CheckedControl("Title", true, "Some label", subControl);
      
      ActionListener stateListener = new ActionListener()
      {
         ObjectState state = new ObjectState();
         
         public void actionPerformed(ActionEvent event)
         {
            String actionCommand = event.getActionCommand();
            
            if (actionCommand.equals(STORE_TEXT))
               state = control.getObjectState(false);
            else if (actionCommand.equals(LOAD_TEXT))
               control.setObjectState(state);
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
      
      JFrame frame = new JFrame("CheckedControl Demo");
        frame.getContentPane().add(control);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
      WindowShower.show(frame);
   }
}
