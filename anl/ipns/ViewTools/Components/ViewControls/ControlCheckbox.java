/*
 * File: ControlCheckbox.java
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
 *  Revision 1.7  2004/03/12 02:24:53  millermi
 *  - Changed package, fixed imports.
 *
 *  Revision 1.6  2004/01/30 22:11:13  millermi
 *  - Removed messaging Strings from interface and into respective
 *    implementing classes that actually send out the message.
 *
 *  Revision 1.5  2004/01/30 06:38:10  millermi
 *  - Added specific ObjectState information.
 *
 *  Revision 1.4  2004/01/05 18:14:07  millermi
 *  - Replaced show()/setVisible(true) with WindowShower.
 *  - Removed excess imports.
 *
 *  Revision 1.3  2003/12/29 04:17:25  millermi
 *  - Added doClick() method that calls the JCheckbox.doClick().
 *    This will simulate a mouse click.
 *
 *  Revision 1.2  2003/10/16 05:00:12  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.1  2003/05/24 17:37:56  dennis
 *  Initial Version of view control using a checkbox. (Mike Miller)
 *
 */
  
 package gov.anl.ipns.ViewTools.Components.ViewControls;
 
 import javax.swing.JCheckBox;
 import javax.swing.JFrame;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.GridLayout;
 import java.awt.Color;
 
 import gov.anl.ipns.Util.Sys.WindowShower;
 import gov.anl.ipns.ViewTools.Components.ObjectState;

/**
 * This class is a ViewControl (ActiveJPanel) with a generic checkbox for use 
 * by ViewComponents. It includes a hook to send out messages when the  
 * checkbox has been checked or unchecked.
 */ 
public class ControlCheckbox extends ViewControl
{
 /**
  * "Checkbox Changed" - This is a messaging String sent out when the
  * checkbox is checked or unchecked.
  */
  public static final String CHECKBOX_CHANGED  = "Checkbox Changed";
 // ------------------------ObjectState Keys------------------------------- 
 /**
  * "Selected" - This constant String is a key for referencing the state
  * information about whether or not the checkbox is checked.
  * The value that this key references is a primative boolean.
  * If the value is true, the checkbox will be checked.
  */
  public static final String SELECTED = "Selected";
 
 /**
  * "Control Text" - This constant String is a key for referencing the state
  * information about the text label on the button of this view control.
  * The value that this key references is of type String.
  */
  public static final String CONTROL_TEXT = "Control Text";
 
 /**
  * "Selected Color" - This constant String is a key for referencing the state
  * information about the color of the label on the button of this view control
  * when the checkbox is checked. The value that this key references is of
  * type Color.
  */
  public static final String SELECTED_COLOR = "Selected Color";
 
 /**
  * "Unselected Color" - This constant String is a key for referencing the state
  * information about the color of the label on the button of this view control
  * when the checkbox is NOT checked. The value that this key references is of
  * type Color.
  */
  public static final String UNSELECTED_COLOR = "Unselected Color";
  
  private JCheckBox cbox;
  private Color checkcolor;
  private Color uncheckcolor;
  private ControlCheckbox this_cbox;
  
 /**
  * Default constructor specifies no title but initializes checkbox to be
  * unchecked.
  */ 
  public ControlCheckbox()
  {  
    super("");
    this.setLayout( new GridLayout(1,1) );
    cbox = new JCheckBox();
    this.add(cbox);
    this_cbox = this;
    cbox.addActionListener( new CheckboxListener() );
    checkcolor = Color.red;
    uncheckcolor = Color.black;      
  }
 
 /**
  * Same functionality as default constructor, only this constructor allows
  * for title specification of the border.
  *
  *  @param  title
  */ 
  public ControlCheckbox(String title)
  {
    this();
    this.setTitle(title);
  }

 /**
  * Same functionality as default constructor, only this constructor allows
  * for setSelected(). Use this constructor to create a checkbox that starts
  * out checked.
  *
  *  @param  isChecked
  */ 
  public ControlCheckbox(boolean isChecked)
  {
     this();
     this.setSelected(isChecked);
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
    state.insert( SELECTED, new Boolean(isSelected()) );
    state.insert( CONTROL_TEXT, new String(getText()) );
    state.insert( SELECTED_COLOR, checkcolor );
    state.insert( UNSELECTED_COLOR, uncheckcolor );
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
    super.setObjectState( new_state );
    
    Object temp = new_state.get(SELECTED);
    if( temp != null )
    {
      setSelected(((Boolean)temp).booleanValue()); 
    }
    
    temp = new_state.get(CONTROL_TEXT);
    if( temp != null )
    {
      setText((String)temp); 
    }
    
    temp = new_state.get(SELECTED_COLOR);
    if( temp != null )
    {
      setTextCheckedColor((Color)temp); 
    }
    
    temp = new_state.get(UNSELECTED_COLOR);
    if( temp != null )
    {
      setTextUnCheckedColor((Color)temp); 
    }
  }  
  
 /**
  * isSelected() tells when the checkbox is checked, true when checked.
  *
  *  @return true if checked
  */  
  public boolean isSelected()
  {   
    return cbox.isSelected();
  }
  
 /**
  * Set the checkbox to be checked (true) or unchecked (false).
  * The constructor initializes the checkbox to be unchecked unless instructed
  * otherwise.
  *
  *  @param  isChecked
  */
  public void setSelected(boolean isChecked)
  {
    // use doClick() so action event is sent out and text colors are changed.
    // if currently not selected, but wants it to be selected.
    if( !isSelected() && isChecked )
      doClick();
    // if currently selected, but wants it unselected.
    else if( isSelected() && !isChecked )
      doClick();	 
  }

 /**
  * This method sets the text of the checkbox. setText() differs from 
  * setTitle() in that the setTitle() is the border text, while the 
  * setText() is the text following the checkbox.
  *
  *  @param  text
  */
  public void setText( String text )
  {
    cbox.setText(text);
  }

 /**
  * This method gets the text of the checkbox. getText() differs from 
  * getTitle() in that the getTitle() returns the border text, while the 
  * getText() is the text following the checkbox. Both methods can be used
  * to identify the checkbox.
  */
  public String getText()
  {
    return cbox.getText();
  }
  
 /**
  * This method sets the text of the checkbox to the color specified when
  * the checkbox is checked.
  *
  *  @param  checked - color of text when checkbox is checked
  */
  public void setTextCheckedColor( Color checked )
  {
    checkcolor = checked;
    if( cbox.isSelected() )
      cbox.setForeground( checkcolor );
  }   

 /**
  * This method sets the text of the checkbox to the color specified when
  * the checkbox is unchecked.
  *
  *  @param  unchecked - color of text when unchecked
  */
  public void setTextUnCheckedColor( Color unchecked )
  {
    uncheckcolor = unchecked;
    if( !cbox.isSelected() )
      cbox.setForeground( uncheckcolor );
  }
  
 /**
  * Acts as an artifical mouse click. Extends the capability of the
  * JCheckbox.doClick().
  */
  public void doClick()
  {
    cbox.doClick();
  }    
  
 /*
  * CheckboxListener moniters the JCheckBox private data member for the
  * ControlCheckbox class
  */
  private class CheckboxListener implements ActionListener
  { 
    public void actionPerformed( ActionEvent ae )
    {
      if( cbox.isSelected() )
        cbox.setForeground( checkcolor );
      else
        cbox.setForeground( uncheckcolor );
      this_cbox.send_message(CHECKBOX_CHANGED);
    }
  } 
  
 /*
  *  For testing purposes only
  */
  public static void main(String[] args)
  {
    ControlCheckbox check = new ControlCheckbox();
    ControlCheckbox check2 = new ControlCheckbox(true);
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout( new GridLayout(2,1) );
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setTitle("ControlCheckbox Test");
    frame.setBounds(0,0,135,120);
    frame.getContentPane().add(check);
    frame.getContentPane().add(check2);
    check.setText("myCheckbox"); 
    check2.setText("myCheckbox2");   
    check.setTextCheckedColor( Color.orange );
    check.setTextUnCheckedColor( Color.green );
    check2.doClick();
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
    ObjectState state = new ObjectState();
    state.insert( ControlCheckbox.SELECTED, new Boolean(true) );
    System.out.println("Prestate");
    check.setObjectState( state );
  }
}
