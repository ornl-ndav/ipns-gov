
/*
 * File: FieldEntryControl.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
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
 *
 *  $Log$
 *  Revision 1.17  2005/05/25 20:28:40  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.16  2005/03/28 05:57:29  millermi
 *  - Added copy() which will make an exact copy of the ViewControl.
 *
 *  Revision 1.15  2005/03/20 05:36:59  millermi
 *  - Modified main() to reflect parameter changes to
 *    ControlManager.makeManagerTestWindow().
 *
 *  Revision 1.14  2005/03/11 20:34:48  millermi
 *  - set/getControlValue() now includes radio button selection as
 *    first entry of String[].
 *  - getSelection() now returns null if radio button not found.
 *
 *  Revision 1.13  2005/03/09 22:36:06  millermi
 *  - Added methods get/setControlValue() and messaging of VALUE_CHANGED
 *    to enable controls to be linked.
 *  - Added "cm" as parameter to main() to test control with the
 *    ControlManager.
 *
 *  Revision 1.12  2005/02/13 21:02:50  millermi
 *  - Factored out existing code into new method resetLabels()
 *    so it may be used by outside classes.
 *
 *  Revision 1.11  2004/05/20 03:29:56  millermi
 *  - Removed unused imports.
 *
 *  Revision 1.10  2004/05/02 22:45:24  millermi
 *  - Created init() method to factor out common functionality
 *    between the constructors.
 *  - Added method call setFocusable(false) to all textfields
 *    that are uneditable.
 *
 *  Revision 1.9  2004/03/12 02:24:53  millermi
 *  - Changed package, fixed imports.
 *
 *  Revision 1.8  2004/02/06 20:07:38  millermi
 *  - Each key now references a String[] of labels. Previously,
 *    a key could reference another key.
 *  - Added ObjectState capabilities.
 *
 *  Revision 1.7  2004/01/08 19:37:09  millermi
 *  - Added method clearAllValues() to easily clear values in the
 *    text fields.
 *
 *  Revision 1.6  2004/01/07 17:54:33  millermi
 *  - Fixed javadoc errors
 *
 *  Revision 1.5  2004/01/05 18:14:06  millermi
 *  - Replaced show()/setVisible(true) with WindowShower.
 *  - Removed excess imports.
 *
 *  Revision 1.4  2004/01/03 03:01:13  millermi
 *  - getAll*Values() now returns an array with the same number of
 *    elements as there are labels. Previously, the array size was
 *    always the largest number of labels.
 *
 *  Revision 1.3  2003/12/29 20:43:13  millermi
 *  - Added setButtonText() so button message could be personalized.
 *  - Changed message string from ENTER_PRESSED to BUTTON_PRESSED
 *
 *  Revision 1.2  2003/12/29 00:32:44  millermi
 *  - Added contructor that takes in an integer to define the size
 *    of the control.
 *  - Added method getLabel() to compliment setLabel().
 *
 *  Revision 1.1  2003/12/24 10:19:29  millermi
 *  - Initial Version - This class contains an array of editable
 *    text fields with a label for each. A special feature is to
 *    allows a user to use radio buttons to change how textfields
 *    are labeled.
 *
 */

package gov.anl.ipns.ViewTools.Components.ViewControls;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Enumeration;

import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.ObjectState;

/**
 * This class is a ViewControl (ActiveJPanel) with labels and input text fields
 * set up for the user to input values. Currently, input for this control is
 * limited to either ALL NUMERIC or ALL STRING. Since the radio button labels
 * are used as keys, no setLabel() method is available. Instead, a
 * removeRadioChoice() and addRadioChoice() must be done.
 */
public class FieldEntryControl extends ViewControl
{
 /**
  * "Button Pressed" - This static final String is a message sent out when the
  * Enter or Submit button has been pressed.
  */
  public static final String BUTTON_PRESSED = "Button Pressed";
 // ---------------------ObjectState Keys---------------------------------
 /**
  * "Label Width" - This constant String is a key for referencing the state
  * information about the width allocated for the label.
  * The value that this key references is of type String.
  */
  public static final String LABEL_WIDTH = "Label Width";
  
 /**
  * "Field Width" - This constant String is a key for referencing the state
  * information about the width allocated for the textfield.
  * The value that this key references is of type String.
  */
  public static final String FIELD_WIDTH = "Field Width";
  
 /**
  * "Radio Choice" - This constant String is a key for referencing the state
  * information about the various radio button choices added to the control.
  * Each radio choice has a list of labels associated with it.
  * The value that this key references is of type Hashtable.
  */
  public static final String RADIO_CHOICE = "Radio Choice";
  
 /**
  * "Field Values" - This constant String is a key for referencing the state
  * information about the text displayed in the textfield.
  * The value that this key references a 1-D array of Strings.
  */
  public static final String FIELD_VALUES = "Field Values";
  
 /**
  * "Button Text" - This constant String is a key for referencing the state
  * information about the text displayed on the button at the bottom of the
  * control. The value that this key references is of type String.
  */
  public static final String BUTTON_TEXT = "Button Text";
  
 /**
  * "Selected Radio Choice" - This constant String is a key for referencing
  * the state information about which radio choice is selected. This
  * corresponds to which set of labels should be displayed. The value
  * that this key references is of type String, matching the radio choice
  * label.
  */
  public static final String SELECTED_RADIO_CHOICE = "Selected Radio Choice";
  
  private JTextField[][] text;
  private transient Box all_fields = new Box( BoxLayout.Y_AXIS );
  private transient Box all_radios = new Box( BoxLayout.Y_AXIS );
  private transient ButtonGroup radioChoices = new ButtonGroup();
  private Hashtable radiotable = new Hashtable();
  private transient boolean radio_added = false;
  private transient JButton enter = new JButton("Enter");
  private transient boolean ignore_change = false;

 /**
  * This constructor will be used to create a control with labels but no
  * default values.
  *
  *  @param  num_entries Specifies the number of field entries for this control.
  */
  public FieldEntryControl( int num_entries )
  {
    super("Field Entries");
    init( new String[num_entries], new String[num_entries] );
  }  

 /**
  * This constructor will be used to create a control with labels but no
  * default values.
  *
  *  @param  name An array of String labels, one for each text area.
  */
  public FieldEntryControl( String[] name )
  {
    super("Field Entries");
    init( name, new String[name.length] );
  }  
  
 /**
  * This constructor will be used to create a control with labels and
  * an array of all integer default values. There does not have to be
  * a value for every label, but values will always be assigned from the
  * beginning.
  *
  *  @param  name An array of String labels, one for each text area.
  *  @param  values An array of integer default values.
  */
  public FieldEntryControl( String[] name, int[] values )
  {
    super("Field Entries");
    String[] int_to_string = new String[values.length];
    for( int i = 0; i < values.length; i++ )
      int_to_string[i] = new Integer(values[i]).toString();
    init( name, int_to_string );
  }  
  
 /**
  * This constructor will be used to create a control with labels and
  * an array of all float default values. There does not have to be
  * a value for every label, but values will always be assigned from the
  * beginning.
  *
  *  @param  name An array of String labels, one for each text area.
  *  @param  values An array of float default values.
  */
  public FieldEntryControl( String[] name, float[] values )
  {
    super("Field Entries");
    String[] float_to_string = new String[values.length];
    for( int i = 0; i < values.length; i++ )
      float_to_string[i] = new Float(values[i]).toString();
    init( name, float_to_string );
  }  
  
 /**
  * This constructor will be used to create a control with labels and
  * an array of all double default values. There does not have to be
  * a value for every label, but values will always be assigned from the
  * beginning.
  *
  *  @param  name An array of String labels, one for each text area.
  *  @param  values An array of double default values.
  */
  public FieldEntryControl( String[] name, double[] values )
  {
    super("Field Entries");
    String[] double_to_string = new String[values.length];
    for( int i = 0; i < values.length; i++ )
      double_to_string[i] = new Double(values[i]).toString();
    init( name, double_to_string );
  } 
  
 /**
  * This constructor will be used to create a control with labels and
  * an array of some or all String default values. There does not have to be
  * a value for every label, but values will always be assigned from the
  * beginning. This constructor should be used if defaults are desired but
  * some fields are numeric and others are non-numeric.
  *
  *  @param  name An array of String labels, one for each text area.
  *  @param  values An array of String default values.
  */
  public FieldEntryControl( String[] name, String[] values )
  {
    super("Field Entries");
    init( name, values );
  }
  
 /*
  * Common functionality among the constructors.
  */ 
  private void init( String[] name, String[] values )
  {
    // If no names in the list, do nothing.
    if (name.length < 1) return;
    setLayout( new GridLayout(1,1) );
    text = new JTextField[name.length][2];
    // For each name, add two JTextFields, the first acts as a label, the
    // second acts as a place to enter values. 
    for(int i = 0; i < name.length; i++)
    { 
      text[i][0] = new JTextField(name[i],10);
      text[i][0].setEditable(false);
      text[i][0].setFocusable(false);
      if( values.length > i )
        text[i][1] = new JTextField(values[i],10);
      else
        text[i][1] = new JTextField("",10);
      Box fieldholder = new Box(BoxLayout.X_AXIS);
      fieldholder.add(text[i][0]);
      fieldholder.add(text[i][1]);
      all_fields.add(fieldholder);
    }
    enter.addActionListener( new EnterListener() );
    all_fields.add(enter);
    add(all_fields);
  }
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = super.getObjectState(isDefault);
    state.insert( LABEL_WIDTH, new Integer(text[0][0].getColumns()) );
    state.insert( FIELD_WIDTH, new Integer(text[0][1].getColumns()) );
    state.insert( RADIO_CHOICE, radiotable );
    state.insert( BUTTON_TEXT, new String(enter.getText()) );
    state.insert( SELECTED_RADIO_CHOICE, getSelected() );
    // only save values in the editable textfields if a project save.
    if( !isDefault )
      state.insert( FIELD_VALUES, getAllStringValues() );
    return state;
  }
     
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    // Do nothing if state is null.
    if( new_state == null )
      return;
    // call setObjectState of ViewControl, sets title if one exists.
    super.setObjectState( new_state );
    Object temp = new_state.get(RADIO_CHOICE);
    if( temp != null )
    {
      // clear old hashtable
      radiotable.clear();
      // restore the table with the new hashtable
      Hashtable labels = (Hashtable)temp;
      Enumeration keys = labels.keys();
      String tempkey = "";
      while( keys.hasMoreElements() )
      {
        tempkey = (String)keys.nextElement();
        addRadioChoice( tempkey, (String[])labels.get(tempkey) );
      }
    }
    
    temp = new_state.get(LABEL_WIDTH);
    if( temp != null )
    {
      setLabelWidth( ((Integer)temp).intValue() );
    }
    
    temp = new_state.get(FIELD_WIDTH);
    if( temp != null )
    {
      setFieldWidth( ((Integer)temp).intValue() );
    }
    
    temp = new_state.get(SELECTED_RADIO_CHOICE);
    if( temp != null )
    {
      setSelected( (String)temp );
    }
    
    temp = new_state.get(BUTTON_TEXT);
    if( temp != null )
    {
      setButtonText( (String)temp );
    }
    
    temp = new_state.get(FIELD_VALUES);
    if( temp != null )
    {
      String[] values = (String[])temp;
      // can blindly call setValue() since setValue() checks for invalid
      // indices.
      for( int i = 0; i < values.length; i++ )
        setValue(i,values[i]);
    }
  }
  
 /**
  * This method will set all the values in the field entry control given an
  * an array of Strings. Since values can be parsed later, use strings for
  * generic purposes. The first entry of the String[] must refer to the
  * radio button that is selected. If no radio buttons have been added
  * to the control, pass in null for the first array value.
  *
  *  @param  value String[] of field entry values.
  */
  public void setControlValue(Object value)
  {
    if( value == null || !(value instanceof String[]) )
      return;
    String[] values = (String[])value;
    // First value is the selected radio button.
    // Make sure radiolabel exists.
    if( !radiotable.containsKey( values[0] ) )
      return;
    setSelected(values[0]);
    int min_length = values.length;
    // Let min_length be the smallest value between the String[] and number of
    // field entries.
    if( min_length > text.length + 1 )
      min_length = text.length;
    ignore_change = true;
    // Start at 1 since first value is radio button selection.
    for( int i = 1; i < min_length; i++ )
      setValue(i-1,values[i]);
    // Submit values.
    enter.doClick();
    ignore_change = false;
  }
  
 /**
  * Get String[] containing the selected radio button and all values in
  * the field entries. Parsing the value strings is done
  * within this control to determine if the strings are numeric values.
  * If no radio buttons have been added to the control, null is the first
  * element of the array.
  *
  *  @return Array containing the selected radio button and all values in
  *          the field entries.
  */
  public Object getControlValue()
  {
    String[] values = getAllStringValues();
    String[] return_values = new String[values.length+1];
    return_values[0] = getSelected();
    for( int i = 0; i < values.length; i++ )
      return_values[i+1] = values[i];
    return return_values;
  }
  
 /**
  * This method will return an exact copy of a FieldEntryControl.
  *
  *  @return Copy of the FieldEntryControl.
  */
  public ViewControl copy()
  {
    FieldEntryControl clone = new FieldEntryControl(text.length);
    clone.setObjectState( getObjectState(PROJECT) );
    return clone;
  }
 
 /**
  * This method will set the column size on all of the non-editable textfields
  * in the first column.
  *
  *  @param  num Number of columns wide the label will be.
  */ 
  public void setLabelWidth( int num )
  {
    for(int i = 0; i < text.length; i++)
    { 
      text[i][0].setColumns(num);
    }
  }
 
 /**
  * This method will set the column size on all of the editable textfields
  * in the second column.
  *
  *  @param  num Number of columns wide the field will be.
  */ 
  public void setFieldWidth( int num )
  {
    for(int i = 0; i < text.length; i++)
    { 
      text[i][1].setColumns(num);
    }
  }

 /**
  * This function sets the text of the label to be displayed in the text area.
  *
  * @param  index The int which represents the text area for the value to
  *		  be displayed in. Index interval is [0,n-1].
  * @param  value The text label for the field.
  */
  public void setLabel(int index, String value)
  {
    // make sure index is valid.
    if( index < text.length && index >= 0 )
    {
      // temporary change, on screen
      text[index][0].setText(value);
      // only do this if radio buttons have been added
      if( radio_added )
      {
        // need to edit hashtable value for selected radio button.
        Object temp_key = radiotable.get(getSelected());
        // if a String, then this is a key to another array.
        while( temp_key instanceof String )
          temp_key = radiotable.get(temp_key);
        String[] labels = (String[])temp_key;
        // only make absolute change if index is smaller than the current
        // number of enabled text fields.
        if( index < labels.length )
        {
          labels[index] = new String(value);
        }
      }
    }
  }

 /**
  * This function sets the text of the label to be displayed in the text area.
  *
  * @param  index The int which represents the text area for the value to
  *		  be displayed in. Index interval is [0,n-1].
  * @return String label for textfield at index. Returns null if index is
  *         invalid or label is null.
  */
  public String getLabel( int index )
  {
    // make sure index is valid.
    if( index < text.length && index >= 0 )
    {
      // temporary change, on screen
      return text[index][0].getText();
    }
    return null;
  }

 /**
  * This function sets an integer value to be displayed in the text area.
  *
  * @param  index The int which represents the text area for the value to
  *		  be displayed in. Index interval is [0,n-1].
  * @param  value The integer value to be displayed.
  */
  public void setValue(int index, int value)
  {
    // make sure index is valid.
    if( index < text.length && index >= 0 )
      text[index][1].setText(new Integer(value).toString());
  }

 /**
  * This function sets a float value to be displayed in the text area.
  *
  * @param  index The int which represents the text area for the value to
  *		  be displayed in. Index interval is [0,n-1].
  * @param  value The floating point value to be displayed.
  */
  public void setValue(int index, float value)
  {
    // make sure index is valid.
    if( index < text.length && index >= 0 )
      text[index][1].setText(new Float(value).toString());
  }

 /**
  * This function sets a double value to be displayed in the text area.
  *
  * @param  index The int which represents the text area for the value to
  *		  be displayed in. Index interval is [0,n-1].
  * @param  value The double value to be displayed.
  */
  public void setValue(int index, double value)
  {
    // make sure index is valid.
    if( index < text.length && index >= 0 )
      text[index][1].setText(new Double(value).toString());
  }

 /**
  * This function sets a string value to be displayed in the text area.
  *
  * @param  index The int which represents the text area for the value to
  *		  be displayed in. Index interval is [0,n-1].
  * @param  value The String value to be displayed.
  */
  public void setValue(int index, String value)
  {
    // make sure index is valid.
    if( index < text.length && index >= 0 )
      text[index][1].setText(value);
  }

 /**
  * This function gets the float value of the text area at the index. Use
  * this to get any numeric values from the text fields.
  *
  * @param  index The int which represents the text area for the value to
  *		  be displayed in. Index interval is [0,n-1].
  * @return The float value of the number at the index.
  */
  public float getFloatValue( int index )
  {
    // make sure index is valid.
    if( index < text.length && index >= 0 )
    {
      // make sure text in JTextField is numeric.
      try
      {
        return Float.parseFloat(text[index][1].getText());
      }
      catch( java.lang.NumberFormatException e )
      {
        return Float.NaN;
      }
    }
    // if an invalid index is entered, return Float.NaN
    return Float.NaN;
  }

 /**
  * This function gets the String value of the text area at the index.
  *
  * @param  index The int which represents the text area for the value to
  *		  be displayed in. Index interval is [0,n-1].
  * @return The String value of the value at the index.
  */
  public String getStringValue( int index )
  {
    // make sure index is valid.
    if( index < text.length && index >= 0 )
    {
      return text[index][1].getText();
    }
    // if an invalid index is entered, return Float.NaN
    return "Invalid Index";
  }

 /**
  * This function gets all of the float values of all the text areas. Use
  * this method only if ALL of the values are numeric.
  *
  * @return An array of float values, ordered the same as the textfields.
  *         If a value in not numeric, Float.NaN is returned in its place.
  */
  public float[] getAllFloatValues()
  {
    // this will reduce the array size to the number of labels.
    // if a String, then this is a key to another array.
    Object temp_key = radiotable.get( getSelected() );
    while( temp_key instanceof String )
      temp_key = radiotable.get(temp_key);
    String[] labellist = (String[])temp_key;
    float[] values;
    if( labellist != null )
    {
      values = new float[labellist.length];
    }
    else
    {
      values = new float[text.length];
    }
    // build the array of values
    for( int index = 0; index < values.length; index++ )
    {
      // make sure text in JTextField is numeric.
      try
      {
        values[index] = Float.parseFloat(text[index][1].getText());
      }
      catch( java.lang.NumberFormatException e )
      {
        values[index] = Float.NaN;
      }
    }    
    return values;
  }

 /**
  * This function gets all of the String values of all the text areas.
  * Use this if some or all of the text areas contain non-numeric text.
  *
  * @return An array of String values, ordered the same as the textfields.
  */
  public String[] getAllStringValues()
  {
    // this will reduce the array size to the number of labels.
    // if a String, then this is a key to another array.
    Object temp_key = radiotable.get( getSelected() );
    while( temp_key instanceof String )
      temp_key = radiotable.get(temp_key);
    String[] labellist = (String[])temp_key;
    String[] values;
    if( labellist != null )
    {
      values = new String[labellist.length];
    }
    else
    {
      values = new String[text.length];
    }
    
    // build the array of values
    for( int index = 0; index < values.length; index++ )
    {
      values[index] = text[index][1].getText();
    }    
    return values;
  }
  
 /**
  * Use this method to reset the labels. If the number of labels exceeds
  * the number of text fields, the extra labels are ignored. If there are
  * not enough labels to fill the text fields, the unfilled text fields are
  * disabled and cleared.
  *
  * @param  labels The new labels for the text fields.
  */ 
  public void resetLabels( String[] labels )
  {
    // if true, use text.length, ignore extra labels
    if( labels.length > text.length )
    {
      for( int i = 0; i < text.length; i++ )
      {
        setLabel(i,labels[i]);
        text[i][1].setEditable(true);
        text[i][1].setFocusable(true);
      }
    }
    else
    {
      for( int i = 0; i < labels.length; i++ )
      {
        setLabel(i,labels[i]);
        text[i][1].setEditable(true);
        text[i][1].setFocusable(true);
      }
      // if true, set labels to "" and disable.
      if( labels.length < text.length )
      {
        for( int dis = labels.length; dis < text.length; dis++ )
        {
          setLabel(dis,"");
          setValue(dis,"");
          text[dis][1].setEditable(false);
          text[dis][1].setFocusable(false);
        }
      }
    }
  }
 
 /**
  * Use this method to clear all of the values in the control.
  */ 
  public void clearAllValues()
  {
    for( int i = 0; i < text.length; i++ )
      setValue(i,"");
  }
 
 /**
  * This method will select the radio button with the given label. This
  * will also update the textfield labels to the new selection.
  *
  *  @param radiolabel The label of the radio button to be selected.
  */ 
  public void setSelected( String radiolabel )
  {
    // make sure radio button label exists
    if( radiotable.containsKey( radiolabel ) && radio_added )
    {
      Enumeration buttons = radioChoices.getElements();
      JRadioButton nextbutton = (JRadioButton)buttons.nextElement();
      String label = nextbutton.getText();
      // find the button with radiolabel as its label.
      while( !label.equals(radiolabel) && buttons.hasMoreElements() )
      {
        nextbutton = (JRadioButton)buttons.nextElement();
        label = nextbutton.getText();
      }
      // set the selected radio button and fire the action listener to
      // update the text field labels.
      radioChoices.setSelected(nextbutton.getModel(),true);
      nextbutton.doClick();
    }
  }
 
 /**
  * This method will return the label of the selected JRadioButton.
  *
  *  @return The label of the selected radio button. Returns null if no
  *          radio buttons have been added to the control.
  */ 
  public String getSelected()
  {
    if( radio_added )
      return radioChoices.getSelection().getActionCommand();
    return null;
  }
  
 /**
  * This method is used to add a radio button which will provide alternate
  * labels to the fields. Use this method if the labels for the new radio
  * button are unique from any other radio button.
  *
  *  @param  radiolabel The label on the radio button.
  *  @param  new_labels A list of new labels for the text fields. If the list
  *                     is shorter than the amount set by the constructor, the
  *                     remaining fields will be disabled. If the list is too
  *                     long, the exceeding values will be ignored.
  */ 
  public void addRadioChoice( String radiolabel, String[] new_labels )
  {
    // make sure there are new labels to apply.
    if( new_labels.length > 0 )
    {
      radiotable.put( radiolabel, new_labels );
      JRadioButton rad = new JRadioButton(radiolabel);
      rad.addActionListener( new RadioListener() );
      radioChoices.add(rad);
      rad.getModel().setGroup(radioChoices);
      rad.getModel().setActionCommand(radiolabel);
      all_radios.add(rad);
      if( radio_added )
      {
        all_radios.validate();
      }
      else
      {
        all_fields.add(all_radios,0);
        radioChoices.setSelected(rad.getModel(),true);
	rad.doClick();
        validate();
        radio_added = true;
      }
    }
  }
  
 /**
  * This method is used to add a radio button which will provide alternate
  * labels to the fields. Use this method if the labels for the new radio
  * button are unique from any other radio button.
  *
  *  @param  radiolabel The label on the radio button.
  *  @param  new_labels A list of new labels for the text fields. If the list
  *                     is shorter than the amount set by the constructor, the
  *                     remaining fields will be disabled. If the list is too
  *                     long, the exceeding values will be ignored.
  *  @param  index      The index where this radio button should be inserted.
  */ 
  public void addRadioChoice( String radiolabel, 
                              String[] new_labels, 
			      int index )
  {
    // make sure there are new labels to apply.
    if( new_labels.length > 0 )
    {
      radiotable.put( radiolabel, new_labels );
      JRadioButton rad = new JRadioButton(radiolabel);
      rad.addActionListener( new RadioListener() );
      radioChoices.add(rad);
      rad.getModel().setGroup(radioChoices);
      rad.getModel().setActionCommand(radiolabel);
      if( all_radios.getComponentCount() <= index )
        index = all_radios.getComponentCount() - 1;
      all_radios.add(rad, index);
      if( radio_added )
      {
        all_radios.validate();
      }
      else
      {
        all_fields.add(all_radios,0);
        radioChoices.setSelected(rad.getModel(),true);
	rad.doClick();
        validate();
        radio_added = true;
      }
    }
  }
  
 /**
  * This method is used to add a radio button which will provide alternate
  * labels to the fields. Use this method if the new radio button has the
  * same labels as another already existing radio button. 
  *
  *  @param  radiolabel The label on the radio button.
  *  @param  same_labels A String label of an already existing radio button.
  *                      This will use the new labels of that radio button.
  */ 
  public void addRadioChoice( String radiolabel, String same_labels )
  {
    Object labels = radiotable.get(same_labels);
    if( labels != null )
    {
      // make sure labels is a String[]
      while( !(labels instanceof String[]) )
      {
        labels = radiotable.get(labels);
	// if null, something went wrong, don't add radio button.
	if( labels == null )
	  return;
      }
      radiotable.put( radiolabel, labels );
      JRadioButton rad = new JRadioButton(radiolabel);
      rad.addActionListener( new RadioListener() );
      radioChoices.add(rad);
      rad.getModel().setGroup(radioChoices);
      rad.getModel().setActionCommand(radiolabel);
      all_radios.add(rad);
      all_radios.validate();
    }
  }
  
 /**
  * This method is used to remove a radio button choice.
  *
  *  @param  radiolabel The label on the radio button.
  */ 
  public void removeRadioChoice( String radiolabel )
  {
    /*
     * since all keys should reference a String[], this should no longer
     * need to be done.
    // if this key is the value of another key, find that key and replace
    // its value with the value of this key.
    // This check is required since addRadioChoice(String,String) is available.
    if( radiotable.containsValue( radiolabel ) )
    {
      Enumeration radiokeys = radiotable.keys();
      Object nextkey;
      Object temp;
      // find which keys have radiolabel as their value.
      while( radiokeys.hasMoreElements() )
      {
        nextkey = radiokeys.nextElement();
	temp = radiotable.get(nextkey);
        if( temp instanceof String )
	{
	  // replace the old value in nextkey with the value in radiolabel.
	  if( ((String)temp).equals( radiolabel ) )
	  {
	    Object value = radiotable.get(radiolabel);
	    if( value instanceof String )
	      radiotable.put( nextkey, new String((String)value) );
	    else
	    {
	      String[] templist = (String[])value;
	      String[] list = new String[templist.length];
	      for( int copy = 0; copy < templist.length; copy++ )
	        list[copy] = new String(templist[copy]);
	      radiotable.put( nextkey, list );
	    }
	  } // end if( temp is radiolabel )
	} // end if( temp is string )
      } // end of while
    }*/
    // make sure radiolabel is in hashtable
    if( radiotable.remove( radiolabel ) != null )
    {  
      Enumeration buttons = radioChoices.getElements();
      JRadioButton nextbutton = (JRadioButton)buttons.nextElement();
      String label = nextbutton.getText();
      // find the button with radiolabel as its label.
      while( !label.equals(radiolabel) && buttons.hasMoreElements() )
      {
        nextbutton = (JRadioButton)buttons.nextElement();
        label = nextbutton.getText();
      }
      // If this button was selected, set the first button to be selected.
      // Then, delete this button from the ButtonGroup and from the control.
      if( nextbutton.isSelected() )
      {
        radioChoices.remove(nextbutton);
        all_radios.remove(nextbutton);
	JRadioButton firstbutton = 
	               ((JRadioButton)radioChoices.getElements().nextElement());
	radioChoices.setSelected(firstbutton.getModel(),true);
	firstbutton.doClick();
      }
      // Delete this button from the ButtonGroup and from the control.
      else
      {
        radioChoices.remove(nextbutton);
        all_radios.remove(nextbutton);
      }
    }
  }
  
 /**
  * This method sets the label on the Enter or Submit button.
  *
  *  @param text Desired text for this button.
  */ 
  public void setButtonText( String text )
  {
    enter.setText(text);
    all_fields.validate();
  }
  
 /*
  * This class listens for a radiobutton selection. Once the selection is
  * made, change the labels to the corresponding radio button.
  */
  private class RadioListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String radio_key = ae.getActionCommand();
      Object temp_key = radiotable.get(radio_key);
      /*
       * This should no longer be needed since every key now references
       * a String[]
      // if a String, then this is a key to another array.
      while( temp_key instanceof String )
        temp_key = radiotable.get(temp_key);
      */
      resetLabels( (String[])temp_key );
    }
  }
  
 /*
  * This class sends out a message when the enter button is pressed.
  */
  private class EnterListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      if( ae.getActionCommand().equals(enter.getText()) )
      {
        send_message(BUTTON_PRESSED);
        // This if statement will prevent VALUE_CHANGED to be sent out when
        // the setControlValue() method is called.
        if( !ignore_change )
	  send_message(VALUE_CHANGED);
      }
    }
  }
  
 /**
  *  Test purposes only... If "cm" is passed as an argument, the
  *  ControlManager will link controls.
  */
  public static void main( String args[] ) 
  {
    // If cm is passed in, test with control manager.
    if( args.length > 0 && args[0].equalsIgnoreCase("cm") )
    {
      String[] namelist1 = {"A1","A2","A3"};
      String[] namelist2 = {"B1","B2","B3"};
      String[] namelist3 = {"C1","C2","C3"};
      String[] namelist4 = {"D1","D2","D3"};
      String[] namelist5 = {"E1","E2","E3"};
      
      FieldEntryControl[] controls = new FieldEntryControl[3];
      controls[0] = new FieldEntryControl(3);
      controls[0].setTitle("FieldEntry1");
      controls[0].addRadioChoice("A",namelist1);
      controls[0].addRadioChoice("B",namelist2);
      controls[0].addRadioChoice("C",namelist3);
      controls[0].setSharedKey("FieldEntry");
      controls[1] = new FieldEntryControl(3);
      controls[1].setTitle("FieldEntry2");
      controls[1].addRadioChoice("A",namelist1);
      controls[1].addRadioChoice("B",namelist2);
      controls[1].addRadioChoice("C",namelist3);
      controls[1].setSharedKey("FieldEntry");
      controls[2] = new FieldEntryControl(3);
      controls[2].setTitle("FieldEntry3");
      controls[2].addRadioChoice("C",namelist3);
      controls[2].addRadioChoice("D",namelist4);
      controls[2].addRadioChoice("E",namelist5);
      controls[2].setSharedKey("FieldEntry");
      
      JFrame frame = ControlManager.makeManagerTestWindow( controls );
      frame.setBounds(0,0,300,300);
      WindowShower.show(frame);
      return;
    }
    JFrame tester = new JFrame("FieldEntryControl Test");
    tester.setBounds(0,0,250,250);
    tester.getContentPane().setLayout( new java.awt.GridLayout(0,2) );
    tester.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    String[] menu = {"MenuOne","MenuTwo","MenuThree","MenuFour","MenuFive"};
    String[] menu2 = {"Menu2","Menu4","Menu6","Menu8","Menu10"};
    String[] menu3 = {"Menu1","Menu3","Menu5","Menu7","Menu9","Menu11"};
    String[] menu4 = {"Menu5","Menu10","Menu15"};
    int[] values = {0,2,4,6,8};
    FieldEntryControl fec = new FieldEntryControl( menu, values );
    //FieldEntryControl fec = new FieldEntryControl( menu );
    //FieldEntryControl fec = new FieldEntryControl( 5 );
    /*fec.setTitle("Field Entry");
    System.out.println("Label of entry 2: " + fec.getLabel(1) );
    fec.addRadioChoice( "Even", menu2 );
    fec.addRadioChoice( "Odd" , menu3 );
    fec.addRadioChoice( "Every Fifth", menu4, 0 );
    fec.addRadioChoice( "Even2", "Even" );
    fec.addRadioChoice( "Even3", "Even2");
    
    fec.removeRadioChoice( "Even2" );
    fec.removeRadioChoice( "Even" );
    fec.setSelected( "Every Fifth" );
    fec.setButtonText("Button Text Test");*/
    Hashtable testtable = new Hashtable();
    testtable.put("Radio1", menu);
    testtable.put("Radio2", menu2);
    testtable.put("Radio3", menu3);
    testtable.put("Radio4", menu4);
    // test ObjectState
    String[] fieldvalues = {"1.5","HI","test","2"};
    ObjectState state = new ObjectState();
    state.insert( LABEL_WIDTH, new Integer(10) );
    state.insert( FIELD_WIDTH, new Integer(10) );
    state.insert( RADIO_CHOICE, testtable );
    state.insert( BUTTON_TEXT, "State Test" );
    state.insert( SELECTED_RADIO_CHOICE, "Radio3" );
    state.insert( FIELD_VALUES, fieldvalues );
    fec.setObjectState(state);
    fec.removeRadioChoice("Radio3");
    tester.getContentPane().add(fec);
    tester.getContentPane().add(fec.copy());
    WindowShower.show(tester);

    System.out.println("Selected: " + fec.getSelected() );
    fec.setLabel( 2, "Menu20" );
    //fec.clearAllValues();
  }
} 
