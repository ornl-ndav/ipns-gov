/*
 * File: LabelCombobox.java
 *
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
 *  Revision 1.11  2007/06/15 22:37:52  oakgrovej
 *  addItem method
 *
 *  Revision 1.10  2006/01/05 20:30:42  rmikk
 *  set the title of the combo box in the constructor
 *
 *  Revision 1.9  2005/06/22 22:22:01  kramer
 *
 *  Added the setEnabled(boolean enabled) method.
 *
 *  Revision 1.8  2005/05/25 20:28:42  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.7  2005/03/28 05:57:30  millermi
 *  - Added copy() which will make an exact copy of the ViewControl.
 *
 *  Revision 1.6  2005/03/20 05:37:00  millermi
 *  - Modified main() to reflect parameter changes to
 *    ControlManager.makeManagerTestWindow().
 *
 *  Revision 1.5  2005/03/14 19:21:44  serumb
 *  Changed public variables to private and added methods to get them.
 *
 *  Revision 1.4  2005/03/09 22:36:09  millermi
 *  - Added methods get/setControlValue() and messaging of VALUE_CHANGED
 *    to enable controls to be linked.
 *  - Added "cm" as parameter to main() to test control with the
 *    ControlManager.
 *
 *  Revision 1.3  2004/03/15 23:53:54  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.2  2004/03/12 02:24:53  millermi
 *  - Changed package, fixed imports.
 *
 */
  
package gov.anl.ipns.ViewTools.Components.ViewControls;
 
 import javax.swing.*;
 import java.awt.event.*;
 import java.awt.*;
 
 import gov.anl.ipns.Util.Sys.WindowShower;
 import gov.anl.ipns.ViewTools.Components.ObjectState;

/**
 * This class is a ViewControl (ActiveJPanel) consisting of a JLabel descriptor
 * and a JComboBox.
 */ 
public class LabelCombobox extends ViewControl
{
  /**
   * "Label" - This String key refers to the text describing the JComboBox.
   * This ObjectState key references a String.
   */
   public static final String LABEL = "Label";
  /**
   * "Fields" - This String key refers to the text that appears in the
   * JComboBox. This ObjectState key references a String[].
   */
   public static final String FIELDS = "Fields";
  /**
   * "Selected Index" - This String key refers to index of the item displayed
   * in the combo box. This ObjectState key references an Integer.
   */
   public static final String SELECTED_INDEX = "Selected Index";
  /**
   * "Display Border" - This String key refers to whether or not the titled
   * border provided by the ViewControl class is shown. This ObjectState
   * key references a Boolean.
   */
   public static final String DISPLAY_BORDER = "Display Border";
   
   private JComboBox cbox;
   private JPanel thepanel = new JPanel();
   private JLabel cboxLabel;
   private Box box1 = new Box(1);
   private Box box2 = new Box(1);
   private FlowLayout f_layout;
   private Box theBox = new Box(0);
   private String[] fields;
   private boolean ignore_change = false;
 
  /**
   * Constructor - Builds a JComboBox with a label.
   *
   *  @param  p_label The label displayed next to the JComboBox.
   *  @param  fields The String list displayed by the JComboBox.
   */ 
   public LabelCombobox(String p_label, String[] fields)
   {  
      super("");

      f_layout = new FlowLayout(0);
      this.fields = fields;
      cboxLabel = new JLabel(p_label);
      
      //setting the ViewControl title
      setTitle(p_label);     
      
      thepanel.setLayout(f_layout);
      thepanel.add(cboxLabel);

      cbox = new JComboBox(fields);
      
      box1.add(thepanel);
      box2.add(cbox);

      theBox.add(box1);
      theBox.add(box2);
  
      this.add(theBox);
      cbox.addActionListener( new ComboboxListener() ); 
   }
   
 /**
  * Get the current state of the control, either preferences (DEFAULT) or
  * the entire state (PROJECT).
  *
  *  @param  is_default The type of state information requested.
  *  @return The current state of the control.
  */
  public ObjectState getObjectState( boolean is_default )
  {
    ObjectState state = super.getObjectState(is_default);
    state.insert( LABEL, cboxLabel.getText() );
    state.insert( FIELDS, fields );
    state.insert( SELECTED_INDEX, new Integer(cbox.getSelectedIndex()) );
    boolean is_visible = true;
    if( getBorder() == null )
      is_visible = false;
    state.insert( DISPLAY_BORDER, new Boolean(is_visible) );
    return state;
  }
  
 /**
  * Set the current state of the control to the state passed in.
  *
  *  @param  new_state The new state of this control.
  */
  public void setObjectState( ObjectState new_state )
  {
    // Do nothing if state is null.
    if( new_state == null )
      return;
    Object value = new_state.get(LABEL);
    if( value != null )
    {
      cboxLabel.setText((String)value);
    }
    
    value = new_state.get(FIELDS);
    if( value != null )
    {
      setItemList( (String[])value );
    }
    
    value = new_state.get(SELECTED_INDEX);
    if( value != null )
    {
      setSelectedIndex( ((Integer)value).intValue() );
    }
    
    value = new_state.get(DISPLAY_BORDER);
    if( value != null )
    {
      setBorderVisible( ((Boolean)value).booleanValue() );
    }
  }
  
 /**
  * Set the selected index of the combo box.
  *
  *  @param  value Integer containing the selected index.
  */
  public void setControlValue(Object value)
  {
    if( value == null || !(value instanceof Integer) )
      return;
    int int_value = ((Integer)value).intValue();
    // Do nothing if int_value is not a valid index. If -1, then none are
    // selected.
    if( int_value >= cbox.getItemCount() || int_value < -1 )
      return;
    ignore_change = true;
    setSelectedIndex(int_value);
    ignore_change = false;
  }
  
 /**
  * Get selected index of the combobox as an Integer.
  *
  *  @return Returns an Integer containing the selected index.
  */
  public Object getControlValue()
  {
    return new Integer(cbox.getSelectedIndex());
  }
  
 /**
  * This method will return an exact copy of a LabelCombobox.
  *
  *  @return Copy of the label combo box.
  */
  public ViewControl copy()
  {
    LabelCombobox clone = new LabelCombobox( cboxLabel.getText(), fields );
    clone.setObjectState( getObjectState(PROJECT) );
    return clone;
  }
  
 /**
  * Use this method to reset the list of Strings displayed by the JComboBox.
  * If null is passed, all of the items in the JComboBox will be removed.
  *
  *  @param  items The items displayed by the JComboBox.
  */
  public void setItemList( String[] items )
  {
    // Do not send out messages when this method is called.
    ignore_change = true;
    cbox.removeAllItems();
    if( items == null )
      return;
    for( int i = 0; i < items.length; i++ )
    {
      cbox.addItem(items[i]);
    }
    ignore_change = false;
  }
  
  /**
   * To add an item to the combobox
   * @param item string to be added
   * @return the Index of the item added
   */
  public int addItem( String item)
  {
    cbox.addItem(item);
    return cbox.getItemCount()-1;
  }

  /**
   * Allows the combo box to be initialized to the index.
   * The constructor initializes the combobox to index 0.
   */
   public void setSelectedIndex(int index)
   {
      cbox.setSelectedIndex(index);
     
   }
   
  /**
   * Get the selected index of the JComboBox.
   *
   *  @return The selected index of the JComboBox.
   */
   public int getSelectedIndex()
   {
     return cbox.getSelectedIndex();
   }
   
  /**
   * Get the selected item in the JComboBox.
   *
   *  @return The selected item in the JComboBox.
   */
   public Object getSelectedItem()
   {
     return cbox.getSelectedItem();
   }   
   
   /**
    * Set if this control should be enabled or not.
    * 
    * @param enabled True if this control should be enabled 
    *                and false if it shouldn't be.
    */
   public void setEnabled(boolean enabled)
   {
      super.setEnabled(enabled);
      cbox.setEnabled(enabled);
   }
   
 /*
   * CheckboxListener moniters the JCheckBox private data member for the
   * ControlCheckbox class
   */
   private class ComboboxListener implements ActionListener
   { 
      public void actionPerformed( ActionEvent ae )
      {
         //System.out.println("the selected item is: " + cbox.getSelectedItem())
         // This if statement will prevent VALUE_CHANGED to be sent out when
         // the setControlValue() method is called.
         if( !ignore_change )
	 {
           send_message(COMBOBOX_CHANGED);
           send_message( VALUE_CHANGED );
         }
      }
   }

  /*
   *  For testing purposes only
   */
   public static void main(String[] args)
   {
      // If cm is passed in, test with control manager.
      if( args.length > 0 && args[0].equalsIgnoreCase("cm") )
      {
        String[] alist = {"A1","A2","A3"};
        String[] blist = {"B1","B2","B3","B4"};
        String[] clist = {"C1","C2"};
	
        ViewControl[] controls = new ViewControl[3];
        controls[0] = new LabelCombobox("Combobox1",alist);
	controls[0].setSharedKey("Combobox");
        controls[1] = new LabelCombobox("Combobox2",blist);
	controls[1].setSharedKey("Combobox");
        controls[2] = new LabelCombobox("Combobox3",clist);
	controls[2].setSharedKey("Combobox");
      
        JFrame frame = ControlManager.makeManagerTestWindow( controls );
        WindowShower.show(frame);
        return;
      }
      String[] line_type = new String[5];
      line_type[0] = "Solid"; 
      line_type[1] = "Dashed";     
      line_type[2] = "Dotted"; 
      line_type[3] = "Dash Dot Dot"; 
      line_type[4] = "Transparent";

      String[] line_width = new String[5];
      line_width[0] = "1";
      line_width[1] = "2";
      line_width[2] = "3";
      line_width[3] = "4";
      line_width[4] = "5";

      LabelCombobox check = new LabelCombobox("line_type", line_type);
      LabelCombobox check2 = new LabelCombobox("line_width", line_width);
      check.setTitle("test");
      check.setBorderVisible(false);
      check.setSelectedIndex(2);
      LabelCombobox check3 = (LabelCombobox)check.copy();
      JFrame frame = new JFrame();
      frame.getContentPane().setLayout( new GridLayout(0,1) );
      frame.setTitle("LabelCombobox Test");
      frame.setBounds(0,0,200,200);
      frame.getContentPane().add(check);
      frame.getContentPane().add(check2);
      frame.getContentPane().add(check3);
     
      WindowShower.show(frame);

      check.setBorderVisible(true);
   }
}
