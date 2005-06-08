/*
 * File: ControlList.java
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
 * Revision 1.3  2005/06/08 21:51:12  kramer
 * Added the method isEmpty() which is used to determine if the control's
 * list is empty.
 *
 * Revision 1.2  2005/06/06 20:18:07  kramer
 *
 * Added javadocs.
 *
 * Revision 1.1  2005/06/03 16:02:42  kramer
 *
 * This is a ViewControl that can be used to allow a user to graphically
 * enter a list of values.  Classes can listen for additions and subtractions
 * to the list or to button presses of a "finish" button.  Also, a filter
 * can be applied so that the user can only enter specific types of values
 * in the list.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.StringFilter.FloatFilter;
import gov.anl.ipns.Util.StringFilter.StringFilter;
import gov.anl.ipns.ViewTools.Components.ObjectState;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * This class is a ViewControl that allows the user to enter a list of 
 * values.  This ViewControl is useful when the user can enter any number of 
 * parameters.  Filters can also be applied to this ViewControl using the 
 * {@link #enableFilter(StringFilter) enableFilter(StringFilter)} method.  
 * This can be used to allow the user to enter only specific types of data to 
 * the list (i.e. only integers, floats, or Strings).
 */
public class ControlList extends ViewControl
{
   //Identification type for signals sent to listeners
   /**
    * "Item Added" - This is the message that is sent out to all 
    * listeners when an item has been added to this ViewControl's list.
    */
   public static final String ITEM_ADDED = "Item Added";
   /**
    * "Item Removed" - This is the message that is sent out to all 
    * listeners when an item has been removed from this ViewControl's list.
    */
   public static final String ITEM_REMOVED = "Item Removed";
   /**
    * "Submit Pressed" - This is the message that is sent out to all 
    * listeners when this ViewControl's "Submit" button is pressed.
    */
   public static final String SUBMIT_PRESSED = "Submit Pressed";
   
   //Keys for entries in an ObjectState
   /**
    * "Listed Values" - This static constant String is a key for referencing 
    * the state information for the values displayed in the list on this 
    * ViewControl.  The value that this key references is an Object[].
    */
   public static final String LIST_VALUES_KEY = "List Values";
   /**
    * "Entry Field Key" - This static constant String is a key for 
    * referencing the state information for the 
    * {@link FieldEntryControl FieldEntryControl} object on this ViewControl.  
    * This {@link FieldEntryControl FieldEntryControl} object contains one 
    * label and one text field.  This text field is the field that allows the 
    * user to enter the values to add to this ViewControl's list.  The value 
    * that this key references is an ObjectState (of a 
    * {@link FieldEntryControl FieldEntryControl} object).
    */
   public static final String ENTRY_FIELD_KEY = "Entry Field Key";
   /**
    * "Add Button Key" - This static constant String is a key for referencing 
    * the state information for the {@link ButtonControl ButtonControl} 
    * object that contains this ViewControl's "Add" button.  The value that 
    * this key references is an ObjectState (of a 
    * {@link ButtonControl ButtonControl} object).
    */
   public static final String ADD_BUTTON_KEY = "Add Button Key";
   /**
    * "Remove Button Key" - This static constant String is a key for 
    * referencing the state information for the 
    * {@link ButtonControl ButtonControl} object that contains this 
    * ViewControl's "Remove" button.  The value that this key references is an 
    * ObjectState (of a {@link ButtonControl ButtonControl} object).
    */
   public static final String REMOVE_BUTTON_KEY = "Remove Button Key";
   /**
    * "Submit Button Key" - This static constant String is a key for 
    * referencing the state information for the 
    * {@link ButtonControl ButtonControl} object that contains this 
    * ViewControl's "Submit" button.  THe value that this key references is an 
    * ObjectState (of a {@link ButtonControl ButtonControl}).
    */
   public static final String SUBMIT_BUTTON_KEY = "Submit Button Key";
   
   //Default values used in this ViewControl
   private static final String default_entry_label = "Enter new value";
   private static final String default_add_button_text = "Add";
   private static final String default_remove_button_text = "Remove";
   private static final String default_submit_button_text = "Submit";
   
   //default values for entries in the ObjectState
   /**
    * This is the default value used in an object state corresponding to the 
    * key {@link #LIST_VALUES_KEY LIST_VALUES_KEY}.
    */
   private static final Object[] default_list_values = new Object[]{};
   /**
    * This is the default value used in an object state corresponding to the 
    * key {@link #ENTRY_FIELD_KEY ENTRY_FIELD_KEY}.
    */
   private static final ObjectState default_entry_field_state = 
      (new FieldEntryControl(new String[]{default_entry_label}, 
                             new String[]{""})).getObjectState(false);
   /**
    * This is the default value used in an object state corresponding to the 
    * key {@link #ADD_BUTTON_KEY ADD_BUTTON_KEY}.
    */
   private static final ObjectState default_add_button_state = 
      (new ButtonControl(default_add_button_text)).getObjectState(false);
   /**
    * This is the default value used in an object state corresponding to the 
    * key {@link #REMOVE_BUTTON_KEY REMOVE_BUTTON_KEY}.
    */
   private static final ObjectState default_remove_button_state = 
      (new ButtonControl(default_remove_button_text)).getObjectState(false);
   /**
    * This is the default value used in an object state corresponding to the 
    * key {@link #SUBMIT_BUTTON_KEY SUBMIT_BUTTON_KEY}.
    */
   private static final ObjectState default_submit_button_state = 
      (new ButtonControl(default_submit_button_text)).getObjectState(false);
   
   //Graphical objects used in the construction of this ViewControl
   /**
    * This is the list in this ViewControl that displays the values that 
    * the user has currently entered.  This variable is saved in this 
    * classe's object state.
    */
   private JList list;
   /**
    * This contains the text field that the user can use to enter the values 
    * that he/she wants to add to {@link #list list}.  This variale is 
    * saved in this classe's object state.
    */
   private FieldEntryControl entryControl;
   /**
    * This contains the "Add" button.  When this button is clicked, the value 
    * written in the text field of the {@link #entryControl entryControl} 
    * object is added {@link #list list}.  This variable is saved in this 
    * classe's object state.
    */
   private ButtonControl addButton;
   /**
    * This contains the "Remove" button.  When this button is clicked, the 
    * currently selected items in {@link #list list} are removed from the 
    * list.  This variable is saved in this classe's object state.
    */
   private ButtonControl removeButton;
   /**
    * This contains the "Submit" button.  The user clicks this button to 
    * signal that he/she has finished entering items to the list.  This 
    * variable is saved in this classe's object state.
    */
   private ButtonControl submitButton;
   
   /**
    * Construct a ControlList with the given title such that the user can 
    * only enter values in the list if the given filter decides they are ok.
    * 
    * @param title The title of this ViewControl.
    * @param filter  The filter used to limit the type of data that 
    *                can be added to the list (i.e. only integers, float, or 
    *                Strings).
    */
   public ControlList(String title, StringFilter filter)
   {
      super(title);
      
      //Construct the graphical components of the ViewControl
      list = new JList(new DefaultListModel());
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      
      entryControl = 
         new FieldEntryControl(new String[]{default_entry_label},
                               new String[]{""});
      entryControl.removeButton();
      entryControl.enableFilter(filter,0);
      
      addButton = new ButtonControl(default_add_button_text);
      addButton.setBorderVisible(false);
      addButton.addActionListener(new AddListener());
      
      removeButton = new ButtonControl(default_remove_button_text);
      removeButton.setBorderVisible(false);
      removeButton.addActionListener(new RemoveListener());
      
      submitButton = new ButtonControl(default_submit_button_text);
      submitButton.setBorderVisible(false);
      submitButton.addActionListener(new SubmitListener());
       
      //now to construct the panel
      setLayout(new BorderLayout());
      add(this.entryControl, BorderLayout.NORTH);
      add(new JScrollPane(this.list), BorderLayout.CENTER);
       JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(submitButton);
      add(buttonPanel, BorderLayout.SOUTH);
   }
   
   /**
    * Construct a ControlList with "" as its title and a filter set such that 
    * the user can only enter values in the list if the given filter 
    * decides they are ok.
    * 
    * @param filter  The filter used to limit the type of data that 
    *                can be added to the list (i.e. only integers, float, or 
    *                Strings).
    */
   public ControlList(StringFilter filter)
   {
      this("",filter);
   }
   
   /**
    * Construct a ControlList with the given title.  No filters are applied.  
    * Thus the user can add anything he/she wants to the list.
    * 
    * @param title The title of this ViewControl.
    */
   public ControlList(String title)
   {
      this("",null);
   }
   
   /**
    * Construct a ControlList with "" as its title.  Also no filters are 
    * applied.  Thus the user can add anything he/she wants to the list.
    */
   public ControlList()
   {
      this("");
   }
   
   //-----------=[ Methods from the abstract superclass ]=-------------------//
   public void setControlValue(Object value)
   {
      if ( value != null )
      {
         if (value instanceof Vector)
         {
            Vector vec = (Vector)value;
            getMyModel().clear();
            for (int i=0; i<vec.size(); i++)
               getMyModel().addElement(vec.elementAt(i));
         }
         else if (value instanceof Object[])
         {
            Object[] arr = (Object[])value;
            getMyModel().clear();
            for (int i=0; i<arr.length; i++)
               getMyModel().addElement(arr[i]);
         }
         else
            System.out.println("Warning:  An incorrect value (A "+
                               value.getClass().getName()+
                               ") was passed to ControlList.setControlValue()");
      }
   }
   
   /**
    * Used to obtain the values that the user has entered in the list.
    * 
    * @return An Object[] containing all of the items in the list.
    */
   public Object getControlValue()
   {
      return getMyModel().toArray();
   }
   
   /**
    * Used to obtain an exact deep copy of ControlList.
    * 
    * @return A deep copy of this ControlList object.
    */
   public ViewControl copy()
   {
      ControlList copy = new ControlList(getTitle(),entryControl.getFilter(0));
      copy.setObjectState(this.getObjectState(false));
      return copy;
   }
   
   /**
    * Used to get the state variables stored for this ControlList wrapped in 
    * an ObjectState object.
    * 
    * @param isDefault <ul>
    *                    <li>
    *                      If true, the ObjectState returned will contain the 
    *                      state variables corresponding to the typical default 
    *                      configuration for this ControlList.
    *                    </li>
    *                    <li>
    *                      If false, the ObjectState containing the state 
    *                      variables corresponding to the current 
    *                      configuration for this ControlList will be 
    *                      returned.
    *                    </li>
    *                  </ul>
    * @return An ObjectState object that contains the state variables that 
    *         save the state of this ControlList.
    */
   public ObjectState getObjectState( boolean isDefault )
   {
      ObjectState state = super.getObjectState(isDefault);
      if (isDefault)
      {
         //fill the ObjectState with default values
         state.insert(LIST_VALUES_KEY, default_list_values);
         state.insert(ENTRY_FIELD_KEY, default_entry_field_state);
         state.insert(ADD_BUTTON_KEY, default_add_button_state);
         state.insert(REMOVE_BUTTON_KEY, default_remove_button_state);
         state.insert(SUBMIT_BUTTON_KEY, default_submit_button_state);
      }
      else
      {
         //fill the ObjectState with the current state of the ViewControl
         state.insert(LIST_VALUES_KEY, getControlValue());
         state.insert(ENTRY_FIELD_KEY, entryControl.getObjectState(false));
         state.insert(ADD_BUTTON_KEY, addButton.getObjectState(false));
         state.insert(REMOVE_BUTTON_KEY, removeButton.getObjectState(false));
         state.insert(SUBMIT_BUTTON_KEY, submitButton.getObjectState(false));
      }
      return state;
   }
   
   /**
    * Used to modify the way this ControlList is displayed.  For example, 
    * the ObjectState for this object contains keys that allow you to modify 
    * the text that appears on the buttons displayed on this ControlList.
    * 
    * @param state The ObjectState containing the state variables that 
    *              describe the new way this ControlList will be displayed.
    */
   public void setObjectState( ObjectState state )
   {
      if (state==null)
         return;
      
      Object curVal = state.get(LIST_VALUES_KEY);
      if (curVal!=null)
         setControlValue((Object[])curVal);
      
      curVal = state.get(ENTRY_FIELD_KEY);
      if (curVal!=null)
         entryControl.setObjectState((ObjectState)curVal);
      
      curVal = state.get(ADD_BUTTON_KEY);
      if (curVal!=null)
         addButton.setObjectState((ObjectState)curVal);
      
      curVal = state.get(REMOVE_BUTTON_KEY);
      if (curVal!=null)
         removeButton.setObjectState((ObjectState)curVal);
      
      curVal = state.get(SUBMIT_BUTTON_KEY);
      if (curVal!=null)
         submitButton.setObjectState((ObjectState)curVal);
   }
   
   //-----------------------------=[ Extra methods ]=------------------------//
   /**
    * Used to determine if this control's list of values is empty.
    * 
    * @return True if the list is empty and false if it isn't.
    */
   public boolean isEmpty()
   {
      return getMyModel().isEmpty();
   }
   
   /**
    * Used to specify which filter is used to limit the type of data the user 
    * can add to the list.
    * 
    * @param filter The filter used to limit the type of data that 
    *               can be added to the list (i.e. only integers, float, or 
    *               Strings).  Note:  If <code>filter==null</code>, this 
    *               method disables filtering.
    * @see #disableFilter()
    */
   public void enableFilter(StringFilter filter)
   {
      entryControl.enableFilter(filter,0);
   }
   
   /**
    * Used to disable filtering.  If filtering is disabled, the user can 
    * enter any type of data to the list.
    * 
    * @see #enableFilter(StringFilter)
    */
   public void disableFilter()
   {
      entryControl.disableFilter(0);
   }
   
   /**
    * Used to set the label that prompts the user to enter data to add to the 
    * list.
    * 
    * @param label The new label.  Note:  If <code>label==null</code> nothing 
    *              is done.
    */
   public void setEntryBoxLabel(String label)
   {
      if (label==null)
         return;
      entryControl.setLabel(0,label);
   }
   
   /**
    * Used to get the label that prompts the user to enter data to add to the 
    * list.
    * 
    * @return The label that tells the user to enter data to the list.
    */
   public String getEntryBoxLabel()
   {
      return entryControl.getLabel(0);
   }
   
   /**
    * Used to set the data that appears in the text field that the user uses 
    * to enter data to the list.  Note:  If a filter has been applied to 
    * limit the possible input, the value will only be set if it passes 
    * through the filter.
    * 
    * @param value The data to enter in the text field used to enter data 
    *              in the list.
    */
   public void setEntryBoxValue(String value)
   {
      StringFilter filter = entryControl.getFilter(0);
      boolean ok = true;
      if (filter!=null)
         ok = filter.isOkay(0,value,"");
      if (ok)
         entryControl.setValue(0,value);
   }
   
   /**
    * This ViewControl contains a text field that the user uses to enter 
    * data.  Then, he/she can choose to enter this data in the list.  This 
    * method returns the data currently entered in this text field.
    * 
    * @return The data that is about to be entered in the list.
    */
   public String getEntryBoxValue()
   {
      return entryControl.getStringValue(0);
   }
   
   /*
    * Note:  When any of the button's text is changed, its 
    *        corresponding actionCommand does not have to 
    *        be changed because each button has its own 
    *        listener.
    */
   /**
    * Used to set the text that appears on the "Add" button.
    * 
    * @param text The "Add" button's new text.
    */
   public void setAddButtonText(String text)
   {
      if (text!=null)
         addButton.getButton().setText(text);
   }
   
   /**
    * Used to get the text that appears on the "Add" button.
    * 
    * @return THe "Add" button's text.
    */
   public String getAddButtonText()
   {
      return addButton.getButton().getText();
   }
   
   /**
    * Used to set the text that appears on the "Remove" button.
    * 
    * @param text The "Remove" button's new text.
    */
   public void setRemoveButtonText(String text)
   {
      if (text!=null)
         removeButton.getButton().setText(text);
   }
   
   /**
    * Used to get the text that appears on the "Remove" button.
    * 
    * @return The text on the "Remove" button.
    */
   public String getRemoveButtonText()
   {
      return removeButton.getButton().getText();
   }
   
   /**
    * Used to set the text that appears on the "Submit" button.
    * 
    * @param text The "Submit" button's new text.
    */
   public void setSubmitButtonText(String text)
   {
      submitButton.getButton().setText(text);
   }
   
   /**
    * Used to get the text that appears on the "Submit" button.
    * 
    * @return The "Submit" button's text.
    */
   public String getSubmitButtonText()
   {
      return submitButton.getButton().getText();
   }
   
   /**
    * Testbed.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      JFrame frame = new JFrame("ControlList Demo 1");
        frame.setSize(200,400);
        frame.setLocation(10,10);
        ControlList list1 = new ControlList("Test Title", new FloatFilter());
        frame.getContentPane().add(list1);
      frame.setVisible(true);
      
      JFrame frame2 = new JFrame("ControlList Demo 2");
        frame2.setSize(300,400);
        frame2.setLocation(40,40);
        frame2.getContentPane().add(list1.copy());
      frame2.setVisible(true);
      
      JFrame frame3 = new JFrame("ControlList Demo 3");
        frame3.setSize(300,400);
        frame3.setLocation(50,50);
        ControlList copy = (ControlList)list1.copy();
          ObjectState copyState = copy.getObjectState(false);
            Object[] arr = new Object[3];
              arr[0] = "a";
              arr[1] = "b";
              arr[2] = "c";
            copyState.reset(LIST_VALUES_KEY, arr);
            copyState.reset(ENTRY_FIELD_KEY, 
                  (new FieldEntryControl(new String[]{"New label"}, 
                        new String[]{"12345"})).getObjectState(false));
            copyState.reset(ADD_BUTTON_KEY, 
                  (new ButtonControl("Modified Add")).getObjectState(false));
            copyState.reset(REMOVE_BUTTON_KEY, 
                  (new ButtonControl("Modified Remove")).getObjectState(false));
            copyState.reset(SUBMIT_BUTTON_KEY, 
                  (new ButtonControl("Modified Submit")).getObjectState(false));
          copy.setObjectState(copyState);
        frame3.getContentPane().add(copy);
      frame3.setVisible(true);
   }
   
   //---------------------------=[ Private methods ]=------------------------//
   /**
    * Convience method used to acquire the model used to store data in the 
    * list.
    * 
    * @return The list model that the list is using (a 
    *         {@link DefaultListModel DefaultListModel}).
    */
   private DefaultListModel getMyModel()
   {
      return (DefaultListModel)list.getModel();
   }
   
   /**
    * Convience method that sends the specified message to all of this 
    * object's listeners.
    * 
    * @param message The message to send to the listeners 
    *                ({@link #ITEM_ADDED ITEM_ADDED} is an example of a 
    *                message that could be sent).
    */
   private void sendMessage(String message)
   {
      ActionListener listener;
      for (int i=0; i<listeners.size(); i++)
      {
         listener = (ActionListener)listeners.elementAt(i);
         listener.actionPerformed(new ActionEvent(this,0,message));
      }
   }
   
   //-------=[ Classes used to listen to the graphical components ]=---------//
   /**
    * This class listens to the "Add" button.
    */
   private class AddListener implements ActionListener
   {
      /**
       * Invoked when the "Add" button is pressed.
       */
      public void actionPerformed(ActionEvent event)
      {
         if (event.getActionCommand().equals(IViewControl.BUTTON_PRESSED))
         {
            String entry = getEntryBoxValue();
            if (!entry.equals(""))
            {
               getMyModel().addElement(entry);
               sendMessage(ITEM_ADDED);
            }
         }
      }
   }
   
   /**
    * This class listens to the "Remove" button.
    */
   private class RemoveListener implements ActionListener
   {
      /**
       * This method is invoked when the "Remove" button is pressed.
       */
      public void actionPerformed(ActionEvent event)
      {
         //get the indices of the selected items in the list
         int[] indices = list.getSelectedIndices();
         //remove the elements from the list in reverse order so that 
         //the correct elements are removed.
         for (int i=indices.length-1; i>=0; i--)
         {
            getMyModel().remove(indices[i]);
            sendMessage(ITEM_REMOVED);
         }
      }
   }
   
   /**
    * This class listens to the "Submit" button.
    */
   private class SubmitListener implements ActionListener
   {
      /**
       * This method is invoked when the "Submit" button is pressed.
       */
      public void actionPerformed(ActionEvent event)
      {
         //just send a message to the ActionListeners that the submit 
         //button was pressed
         sendMessage(SUBMIT_PRESSED);
      }
   }
}
