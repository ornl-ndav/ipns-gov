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
 * Revision 1.1  2005/06/03 16:02:42  kramer
 * This is a ViewControl that can be used to allow a user to graphically
 * enter a list of values.  Classes can listen for additions and subtractions
 * to the list or to button presses of a "finish" button.  Also, a filter
 * can be applied so that the user can only enter specific types of values
 * in the list.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.StringFilter.IntegerFilter;
import gov.anl.ipns.Util.StringFilter.StringFilter;
import gov.anl.ipns.ViewTools.Components.ObjectState;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 
 */
public class ControlList extends ViewControl
{
   //Identification type for signals sent to listeners
   public static final String ITEM_ADDED = "Item Added";
   public static final String ITEM_REMOVED = "Item Removed";
   public static final String SUBMIT_PRESSED = "Submit Pressed";
   
   //Keys for entries in an ObjectState
   public static final String LIST_VALUES_KEY = "List Values";
   public static final String ENTRY_FIELD_KEY = "Entry Field Key";
   public static final String ERROR_MESSAGE_KEY = "Error Message";
   public static final String ADD_BUTTON_KEY = "Add Button Key";
   public static final String REMOVE_BUTTON_KEY = "Remove Button Key";
   public static final String SUBMIT_BUTTON_KEY = "Submit Button Key";
   
   //Default values used in this ViewControl
   private static final String default_entry_label = "Enter new value";
   private static final String default_error_message = "";
   private static final String default_add_button_text = "Add";
   private static final String default_remove_button_text = "Remove";
   private static final String default_submit_button_text = "Submit";
   
   //default values for entries in the ObjectState
   private static final Object[] DEFAULT_LIST_VALUES = new Object[]{};
   private static final ObjectState DEFAULT_ENTRY_FIELD_STATE = 
      (new FieldEntryControl(new String[]{default_entry_label}, 
                             new String[]{""})).getObjectState(false);
   private static final String DEFAULT_ERROR_MESSAGE = "";
   private static final ObjectState DEFAULT_ADD_BUTTON_STATE = 
      (new ButtonControl(default_add_button_text)).getObjectState(false);
   private static final ObjectState DEFAULT_REMOVE_BUTTON_STATE = 
      (new ButtonControl(default_remove_button_text)).getObjectState(false);
   private static final ObjectState DEFAULT_SUBMIT_BUTTON_STATE = 
      (new ButtonControl(default_submit_button_text)).getObjectState(false);
   
   //Graphical objects used in the construction of this ViewControl
   private JList list;
   private FieldEntryControl entryControl;
   private JLabel errorMessage;
   private ButtonControl addButton;
   private ButtonControl removeButton;
   private ButtonControl submitButton;
   
   //Extra objects used in this ViewControls
   private StringFilter filter;
   private String filterErrMsg;
   
   /**
    * @param title
    */
   public ControlList(String title, StringFilter filter, String filterErrMsg)
   {
      super(title);
      
      //Construct the graphical components of the ViewControl
      this.list = new JList(new DefaultListModel());
      this.entryControl = 
        new FieldEntryControl(new String[]{default_entry_label},
                              new String[]{""});
      this.errorMessage = new JLabel(default_error_message);
       this.errorMessage.setBackground(Color.RED);
      this.addButton = new ButtonControl(default_add_button_text);
       this.addButton.addActionListener(new AddListener());
      this.removeButton = new ButtonControl(default_remove_button_text);
       this.removeButton.addActionListener(new RemoveListener());
      this.submitButton = new ButtonControl(default_submit_button_text);
       this.submitButton.addActionListener(new SubmitListener());
       
      //Construct the extra objects needed
      this.filter = filter;
      this.filterErrMsg = filterErrMsg;
      
      //now to construct the panel
      setLayout(new BorderLayout());
       JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(this.entryControl, BorderLayout.CENTER);
        topPanel.add(this.errorMessage, BorderLayout.SOUTH);
      add(topPanel, BorderLayout.NORTH);
      add(new JScrollPane(this.list), BorderLayout.CENTER);
       JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(submitButton);
      add(buttonPanel, BorderLayout.SOUTH);
   }
   
   public ControlList(StringFilter filter, String filterErrMsg)
   {
      this("",filter,filterErrMsg);
   }
   
   public ControlList(String title)
   {
      this("",null,"");
   }
   
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

   public Object getControlValue()
   {
      return getMyModel().toArray();
   }

   public ViewControl copy()
   {
      ControlList copy = new ControlList(getTitle(),getFilter(),filterErrMsg);
      copy.setObjectState(this.getObjectState(false));
      return copy;
      
      /*
       //copy the label and value of the entry field
        copy.setEntryBoxLabel(this.getEntryBoxLabel());
        copy.setEntryBoxValue(this.getEntryBoxValue());
       //copy the values in the list
        copy.setControlValue(this.getControlValue());
       //copy the buttons' text
        copy.setAddButtonText(this.getAddButtonText());
        copy.setRemoveButtonText(this.getRemoveButtonText());
        copy.setSubmitButtonText(this.getSubmitButtonText());
      return copy;
      */
   }
   
   public ObjectState getObjectState( boolean isDefault )
   {
      ObjectState state = super.getObjectState(isDefault);
      if (isDefault)
      {
         //fill the ObjectState with default values
         state.insert(LIST_VALUES_KEY, DEFAULT_LIST_VALUES);
         state.insert(ENTRY_FIELD_KEY, DEFAULT_ENTRY_FIELD_STATE);
         state.insert(ERROR_MESSAGE_KEY, DEFAULT_ERROR_MESSAGE);
         state.insert(ADD_BUTTON_KEY, DEFAULT_ADD_BUTTON_STATE);
         state.insert(REMOVE_BUTTON_KEY, DEFAULT_REMOVE_BUTTON_STATE);
         state.insert(SUBMIT_BUTTON_KEY, DEFAULT_SUBMIT_BUTTON_STATE);
      }
      else
      {
         //fill the ObjectState with the current state of the ViewControl
         state.insert(LIST_VALUES_KEY, getControlValue());
         state.insert(ENTRY_FIELD_KEY, entryControl.getObjectState(false));
         state.insert(ERROR_MESSAGE_KEY, errorMessage.getText());
         state.insert(ADD_BUTTON_KEY, addButton.getObjectState(false));
         state.insert(REMOVE_BUTTON_KEY, removeButton.getObjectState(false));
         state.insert(SUBMIT_BUTTON_KEY, submitButton.getObjectState(false));
      }
      return state;
   }
   
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
      
      curVal = state.get(ERROR_MESSAGE_KEY);
      if (curVal!=null)
         errorMessage.setText((String)curVal);
      
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
   public DefaultListModel getMyModel()
   {
      return (DefaultListModel)list.getModel();
   }
   
   public void setFilter(StringFilter filter)
   {
      this.filter = filter;
   }
   
   public StringFilter getFilter()
   {
      return filter;
   }
   
   public void setEntryBoxLabel(String label)
   {
      if (label==null)
         return;
      entryControl.setLabel(0,label);
   }
   
   public String getEntryBoxLabel()
   {
      return entryControl.getLabel(0);
   }
   
   public void setEntryBoxValue(String value)
   {
      boolean ok = true;
      if (filter!=null)
         ok = filter.isOkay(0,value,"");
      
      if (ok)
         entryControl.setValue(0,value);
   }
   
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
   
   public void setAddButtonText(String text)
   {
      if (text!=null)
         addButton.getButton().setText(text);
   }
   
   public String getAddButtonText()
   {
      return addButton.getButton().getText();
   }
   
   public void setRemoveButtonText(String text)
   {
      if (text!=null)
         removeButton.getButton().setText(text);
   }
   
   public String getRemoveButtonText()
   {
      return removeButton.getButton().getText();
   }
   
   public void setSubmitButtonText(String text)
   {
      submitButton.getButton().setText(text);
   }
   
   public String getSubmitButtonText()
   {
      return submitButton.getButton().getText();
   }
   
   public static void main(String[] args)
   {
      JFrame frame = new JFrame("ControlList Demo");
       frame.setSize(200,400);
       frame.setLocation(10,10);
       frame.getContentPane().add(new ControlList("Test Title",
                                                  new IntegerFilter(),
                                                  "You must enter an integer"));
      frame.setVisible(true);
   }
   
   //---------------------------=[ Private methods ]=------------------------//
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
   private class AddListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         boolean textOk = true;
         if (filter!=null)
            textOk = filter.isOkay(0,getEntryBoxValue(),"");
         if (textOk)
         {
            getMyModel().addElement(getEntryBoxValue());
            errorMessage.setText("");
            sendMessage(ITEM_ADDED);
         }
         else
            errorMessage.setText(filterErrMsg);
      }
   }
   
   private class RemoveListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         //get the indices of the selected items in the list
         int[] indices = list.getSelectedIndices();
         //remove the elements from the list in reverse order so that 
         //the correct elements are removed.
         for (int i=indices.length-1; i>=0; i--)
         {
            getMyModel().remove(i);
            sendMessage(ITEM_REMOVED);
         }
      }
   }
   
   private class SubmitListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         //just send a message to the ActionListeners that the submit 
         //button was pressed
         sendMessage(SUBMIT_PRESSED);
      }
   }
}
