
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
 *  Revision 1.1  2003/12/24 10:19:29  millermi
 *  - Initial Version - This class contains an array of editable
 *    text fields with a label for each. A special feature is to
 *    allows a user to use radio buttons to change how textfields
 *    are labeled.
 *
 */

package DataSetTools.components.View.ViewControls;

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
  * "Enter Pressed" - This static final String is a message sent out when the
  * enter button has been pressed.
  */
  public static final String ENTER_PRESSED = "Enter Pressed";
  
  private JTextField[][] text;
  private Box all_fields = new Box( BoxLayout.Y_AXIS );
  private Box all_radios = new Box( BoxLayout.Y_AXIS );
  private ButtonGroup radioChoices = new ButtonGroup();
  private Hashtable radiotable = new Hashtable();
  private boolean radio_added = false;

 /**
  * This constructor will be used to create a control with labels but no
  * default values.
  *
  *  @param  name An array of String labels, one for each text area.
  */
  public FieldEntryControl( String[] name )
  {
    super("Field Entries");
    if (name.length < 1) return;
    setLayout( new GridLayout(1,1) );
    text = new JTextField[name.length][2];
    for(int i = 0; i < name.length; i++)
    { 
      text[i][0] = new JTextField(name[i],10);
      text[i][0].setEditable(false);
      text[i][1] = new JTextField("",10);
      Box fieldholder = new Box(BoxLayout.X_AXIS);
      fieldholder.add(text[i][0]);
      fieldholder.add(text[i][1]);
      all_fields.add(fieldholder);
    }
    JButton enter = new JButton("Enter");
    enter.addActionListener( new EnterListener() );
    all_fields.add(enter);
    add(all_fields);
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
    if (name.length < 1) return;
    setLayout( new GridLayout(1,1) );
    text = new JTextField[name.length][2];
    for(int i = 0; i < name.length; i++)
    { 
      text[i][0] = new JTextField(name[i],10);
      text[i][0].setEditable(false);
      if( values.length > i )
        text[i][1] = new JTextField(new Integer(values[i]).toString(),10);
      else
        text[i][1] = new JTextField("",10);
      Box fieldholder = new Box(BoxLayout.X_AXIS);
      fieldholder.add(text[i][0]);
      fieldholder.add(text[i][1]);
      all_fields.add(fieldholder);
    }
    JButton enter = new JButton("Enter");
    enter.addActionListener( new EnterListener() );
    all_fields.add(enter);
    add(all_fields);
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
    if (name.length < 1) return;
    setLayout( new GridLayout(1,1) );
    text = new JTextField[name.length][2];
    for(int i = 0; i < name.length; i++)
    { 
      text[i][0] = new JTextField(name[i],10);
      text[i][0].setEditable(false);
      if( values.length > i )
        text[i][1] = new JTextField(new Float(values[i]).toString(),10);
      else
        text[i][1] = new JTextField("",10);
      Box fieldholder = new Box(BoxLayout.X_AXIS);
      fieldholder.add(text[i][0]);
      fieldholder.add(text[i][1]);
      all_fields.add(fieldholder);
    }
    JButton enter = new JButton("Enter");
    enter.addActionListener( new EnterListener() );
    all_fields.add(enter);
    add(all_fields);
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
    if (name.length < 1) return;
    setLayout( new GridLayout(1,1) );
    text = new JTextField[name.length][2];
    for(int i = 0; i < name.length; i++)
    { 
      text[i][0] = new JTextField(name[i],10);
      text[i][0].setEditable(false);
      if( values.length > i )
        text[i][1] = new JTextField(new Double(values[i]).toString(),10);
      else
        text[i][1] = new JTextField("",10);
      Box fieldholder = new Box(BoxLayout.X_AXIS);
      fieldholder.add(text[i][0]);
      fieldholder.add(text[i][1]);
      all_fields.add(fieldholder);
    }
    JButton enter = new JButton("Enter");
    enter.addActionListener( new EnterListener() );
    all_fields.add(enter);
    add(all_fields);
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
    if (name.length < 1) return;
    setLayout( new GridLayout(1,1) );
    text = new JTextField[name.length][2];
    for(int i = 0; i < name.length; i++)
    { 
      text[i][0] = new JTextField(name[i],10);
      text[i][0].setEditable(false);
      if( values.length > i )
        text[i][1] = new JTextField(values[i],10);
      else
        text[i][1] = new JTextField("",10);
      Box fieldholder = new Box(BoxLayout.X_AXIS);
      fieldholder.add(text[i][0]);
      fieldholder.add(text[i][1]);
      all_fields.add(fieldholder);
    }
    JButton enter = new JButton("Enter");
    enter.addActionListener( new EnterListener() );
    all_fields.add(enter);
    add(all_fields);
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
    float[] values = new float[text.length];
    for( int index = 0; index < text.length; index++ )
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
    String[] values = new String[text.length];
    for( int index = 0; index < text.length; index++ )
    {
      values[index] = text[index][1].getText();
    }    
    return values;
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
  *  @return The label of the selected radio button.
  */ 
  public String getSelected()
  {
    if( radio_added )
      return radioChoices.getSelection().getActionCommand();
    return "Radio Button Needed";
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
    if( radiotable.get(same_labels) != null )
    {
      radiotable.put( radiolabel, same_labels );
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
  *  @param  new_labels A list of new labels for the text fields. If the list
  *                     is shorter than the amount set by the constructor, the
  *                     remaining fields will be disabled. If the list is too
  *                     long, the exceeding values will be ignored.
  */ 
  public void removeRadioChoice( String radiolabel )
  {
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
    }
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
      // if a String, then this is a key to another array.
      while( temp_key instanceof String )
        temp_key = radiotable.get(temp_key);
      String[] labels = (String[])temp_key;
      // if true, use text.length, ignore extra labels
      if( labels.length > text.length )
      {
        for( int i = 0; i < text.length; i++ )
	{
	  setLabel(i,labels[i]);
	  text[i][1].setEditable(true);
	}
      }
      else
      {
        for( int i = 0; i < labels.length; i++ )
	{
	  setLabel(i,labels[i]);
	  text[i][1].setEditable(true);
	}
	// if true, set labels to "" and disable.
	if( labels.length < text.length )
	{
	  for( int dis = labels.length; dis < text.length; dis++ )
	  {
	    setLabel(dis,"");
	    setValue(dis,"");
	    text[dis][1].setEditable(false);
	  }
	}
      }
    }
  }
  
 /*
  * This class sends out a message when the enter button is pressed.
  */
  private class EnterListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      if( ae.getActionCommand().equals("Enter") )
      {
        send_message(ENTER_PRESSED);
      }
    }
  }
  
 /**
  * Test purposes only...
  */
  public static void main( String args[] ) 
  {
    JFrame tester = new JFrame("FieldEntryControl Test");
    tester.setBounds(0,0,150,250);
    tester.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    String[] menu = {"MenuOne","MenuTwo","MenuThree","MenuFour","MenuFive"};
    String[] menu2 = {"Menu2","Menu4","Menu6","Menu8","Menu10"};
    String[] menu3 = {"Menu1","Menu3","Menu5","Menu7","Menu9","Menu11"};
    String[] menu4 = {"Menu5","Menu10","Menu15"};
    //int[] values = {0,2,4,6,8};
    //FieldEntryControl fec = new FieldEntryControl( menu, values );
    FieldEntryControl fec = new FieldEntryControl( menu );
    fec.setTitle("Field Entry");
    fec.addRadioChoice( "Even", menu2 );
    fec.addRadioChoice( "Odd" , menu3 );
    fec.addRadioChoice( "Every Fifth", menu4, 0 );
    fec.addRadioChoice( "Even2", "Even" );
    fec.addRadioChoice( "Even3", "Even2");
    
    fec.removeRadioChoice( "Even2" );
    fec.removeRadioChoice( "Even" );
    fec.setSelected( "Every Fifth" );
    tester.getContentPane().add(fec);
    tester.setVisible(true);
    System.out.println("Selected: " + fec.getSelected() );
    fec.setLabel( 2, "Menu20" );
  }
} 
