/*
 * File: FormatControl.java
 *
 * Copyright (C) 2004, Mike Miller
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
 *  Revision 1.3  2004/12/05 05:48:57  millermi
 *  - Fixed Eclipse warnings.
 *
 *  Revision 1.2  2004/08/17 01:25:21  millermi
 *  - Made getFormat() and setFormat() public methods.
 *  - Fixed bug that caused state to be saved incorrectly.
 *
 *  Revision 1.1  2004/08/04 18:51:18  millermi
 *  - Initial Version - This control consists of a series of combo boxes
 *    to set multiple format options.
 *
 */
  
 package gov.anl.ipns.ViewTools.Components.ViewControls;
 
 import javax.swing.JComboBox;
 import javax.swing.JFrame;
 import java.util.Vector;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.GridLayout;
 
 import gov.anl.ipns.Util.Sys.WindowShower;
 import gov.anl.ipns.ViewTools.Components.IPreserveState;
 import gov.anl.ipns.ViewTools.Components.ObjectState;

/**
 * This class is a ViewControl (ActiveJPanel) with a ...
 */ 
public class FormatControl extends ViewControl
{
 /**
  * "Format Changed" - This is a messaging String sent out when any
  * combo box is changed.
  */
  public static final String FORMAT_CHANGED  = "Format Changed";
 
 // ------------------------ObjectState Keys------------------------------- 
 /**
  * "Format List" - This constant String is a key for referencing the state
  * information about all of the combo boxes for this control.
  * The value that this key references is a Vector containing an array of
  * Objects. Each Object[] represents a combo box.
  */
  public static final String FORMAT_LIST = "Format List";
  
 /**
  * "Selected List" - This constant String is a key for referencing the state
  * information about the selected index of each combo box at the time the save
  * occurred. The value that this key references is an int[] matching the
  * number of Object[] in the FORMAT_LIST Vector.
  */ 
  public static final String SELECTED_LIST = "Selected List";
  
 /**
  * "Tool Tip List" - This constant String is a key for referencing the state
  * information about the tool tips of each combo box at the time the save
  * occurred. The value that this key references is a String[] matching the
  * number of Object[] in the FORMAT_LIST Vector.
  */ 
  public static final String TOOL_TIP_LIST = "Tool Tip List";
  
  private JComboBox[] formats;
  private int changed_index;
  
 /**
  * Default constructor - specifies no title, with one JComboBox.
  */ 
  public FormatControl()
  {  
    super("");
    formats = new JComboBox[1];
    init();
  }
 
 /**
  * Same functionality as default constructor, only this constructor allows
  * for title specification of the border.
  *
  *  @param  title
  */ 
  public FormatControl(String title)
  {
    this();
    this.setTitle(title);
  }
 
 /**
  * Use this constructor to specify a number JComboBoxes, each for a different
  * format.
  *
  *  @param  num_format_parameters
  */ 
  public FormatControl( int num_format_parameters )
  {
    super("");
    formats = new JComboBox[num_format_parameters];
    init();
    
  }
 
 /**
  * Use this constructor to specify a number JComboBoxes by providing a list
  * of Object[]. Each Object[] contains the list of format options.
  *
  *  @param  format_lists A vector of Object[].
  */ 
  public FormatControl( Vector format_lists )
  {
    super("");
    setFormats(format_lists);
  }
  
  private void init()
  {
    removeAll();
    changed_index = -1;
    
    int row = formats.length;
    int col = 1;
    if( formats.length == 0 )
      row = 1;
    setLayout( new GridLayout(row,col) );
    for( int i = 0; i < formats.length; i++ )
      add( formats[i] );
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
    
    if( !isDefault )
    {
      state.insert( FORMAT_LIST, getFormats() );
      state.insert( SELECTED_LIST, getSelectedIndexArray() );
      state.insert( TOOL_TIP_LIST, getToolTipArray() );
    }
    
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
    boolean redraw = false;
    Object temp = new_state.get(FORMAT_LIST);
    if( temp != null )
    {
      setFormats((Vector)temp);
      redraw = true;
    }
    
    temp = new_state.get(SELECTED_LIST);
    if( temp != null )
    {
      setSelectedIndexArray( (int[])temp );
      redraw = true;
    }
    
    temp = new_state.get(TOOL_TIP_LIST);
    if( temp != null )
    {
      setToolTipArray( (String[])temp );
      redraw = true;
    }
    
    if( redraw )
      repaint();
  }
  
 /**
  * Get the index of the COMBO BOX that was most recently changed. If this
  * method is called before a combo box has been changed, -1 is returned.
  *
  *  @return The index of the last combo box to be changed. Negative one (-1)
  *          is returned if no combo boxes have been changed.
  */ 
  public int getLastChangedIndex()
  {
    return changed_index;
  }
  
 /**
  * Get the index of the SELECTED ITEM from the combo box that was most
  * recently changed. If this method is called before a combo box has been
  * changed, -1 is returned.
  *
  *  @return The index of the selected item for the last combo box to be
  *          changed. Negative one (-1) is returned if no combo boxes have
  *          been changed.
  */ 
  public int getLastChangedSelectedIndex()
  {
    return getSelectedIndex(changed_index);
  }
  
 /**
  * Given the array index, get the index of the currently selected object
  * in the combo box at combo_box_index.
  *
  *  @param  combo_box_index The index of the combo box where the selected
  *                          index is being found.
  *  @return The index of the selected Object displayed in the combo box
  *          at combo_box_index.
  */ 
  public int getSelectedIndex( int combo_box_index )
  {
    // If index is invalid, return -1
    if( combo_box_index < 0 || combo_box_index >= formats.length )
      return -1;
    changed_index = combo_box_index;
    return formats[combo_box_index].getSelectedIndex();
  }
  
 /**
  * Given the array index, set the selected object in the combo box at
  * combo_box_index.
  *
  *  @param  combo_box_index The index of the combo box where the selected
  *                          index is being found.
  *  @param  select_index The index of the desired item to be selected
  *                       for combo box found at combo_box_index.
  */ 
  public void setSelectedIndex( int combo_box_index, int select_index )
  {
    // If index is invalid combo box index, do nothing.
    if( combo_box_index < 0 || combo_box_index >= formats.length )
      return;
    // If index is invalid combo box entry index, do nothing.
    if( select_index < 0 ||
        select_index >= formats[combo_box_index].getItemCount() )
      return;
    formats[combo_box_index].setSelectedIndex(select_index);
  }
  
 /**
  * Get the tool tips for the combo box at combo_box_index.
  *
  *  @param  combo_box_index The combo box with tool tips.
  *  @return The String tool tip or null if invalid index.
  */
  public String getToolTipText( int combo_box_index )
  {
    // If index is invalid combo box index, do nothing.
    if( combo_box_index < 0 || combo_box_index >= formats.length )
      return null;
    return formats[combo_box_index].getToolTipText();
  }
  
 /**
  * Set the tool tips for the combo box at combo_box_index.
  *
  *  @param  combo_box_index The combo box needing tool tips.
  *  @param  text The tool tip text being added to the combo box.
  */
  public void setToolTipText( int combo_box_index, String text )
  {
    // If index is invalid combo box index, do nothing.
    if( combo_box_index < 0 || combo_box_index >= formats.length )
      return;
    formats[combo_box_index].setToolTipText(text);
  }
 
 /**
  * Get the labels of the combo boxes in the form of a Vector containing a
  * list of Object[], with each array corresponding to a combo box.
  *
  *  @return Vector containing multiple Object[], each corresponding to a
  *          combo box.
  */ 
  public Vector getFormats()
  {
    Vector temp = new Vector();
    Object[] item_list;
    for( int i = 0; i < formats.length; i++ )
    {
      item_list = new Object[formats[i].getItemCount()];
      for( int item = 0; item < formats[i].getItemCount(); item++ )
      {
        item_list[item] = formats[i].getItemAt(item);
      }
      temp.add(item_list);
    }
    return temp;
  }
 
 /**
  * This method will create the combo boxes and add listeners to each.
  * Calling this method will clear/remove any previously existing combo boxes.
  *
  *  @param  format_lists Vector containing list of Object[], each corresponding
  *          to a combo box.
  */ 
  public void setFormats( Vector format_lists )
  {
    if( format_lists == null )
    {
      formats = new JComboBox[0];
      return;
    }
    formats = new JComboBox[format_lists.size()];
    Object temp;
    for( int i = 0; i < formats.length; i++ )
    {
      temp = format_lists.elementAt(i);
      if( temp instanceof Object[] )
      {
        formats[i] = new JComboBox( (Object[])temp );
	((JComboBox)formats[i]).addActionListener( new ComboBoxChanged() );
      }
      // If invalid, create a combobox with an error message in it's list.
      else
      {
        Object[] err_temp = new Object[2];
	err_temp[0] = new String("Invalid List");
	err_temp[1] = new String("Must Be Object[]");
        formats[i] = new JComboBox(err_temp);
      }
    }
    // This will remove any previous combo boxes are replace them with the
    // newly created combo boxes.
    init();
  }
 
 /*
  * Find the array index of the combo box that is passed in.
  */ 
  private int findIndex( JComboBox cbox )
  {
    int index = 0;
    while( index < formats.length && cbox != formats[index] )
      index++;
    // If combo box not found, return -1
    if( index == formats.length )
      return -1;
    // Otherwise combo box is at the current value of index.
    return index;
  }
  
 /*
  * Get the selected index of each combo box, all combined into one int[].
  */ 
  private int[] getSelectedIndexArray()
  {
    int[] indices = new int[formats.length];
    for( int i = 0; i < indices.length; i++ )
    {
      indices[i] = getSelectedIndex(i);
    }
    return indices;
  }
 
 /*
  * Set the selected indices of all combo boxes, each index corresponds to a
  * combo box.
  */ 
  private void setSelectedIndexArray( int[] indices )
  {
    if( indices == null || indices.length != formats.length )
      return;
    for( int i = 0; i < indices.length; i++ )
    {
      setSelectedIndex(i,indices[i]);
    }
  }
  
 /*
  * Get all of the tool tips for each combo box.
  */
  private String[] getToolTipArray()
  {
    String[] indices = new String[formats.length];
    for( int i = 0; i < indices.length; i++ )
    {
      indices[i] = getToolTipText(i);
    }
    return indices;
  }
  
 /*
  * Set all of the tool tips for each combo box.
  */
  private void setToolTipArray( String[] tips )
  {
    if( tips == null || tips.length != formats.length )
      return;
    for( int i = 0; i < tips.length; i++ )
    {
      setToolTipText(i,tips[i]);
    }
  }
  
 /*
  * This listener will keep track of the last combo box to be changed and
  * will notify all listeners of this control that a combo box was changed.
  */ 
  private class ComboBoxChanged implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      // Record the last changed combo box.
      changed_index = findIndex( (JComboBox)ae.getSource() );
      send_message( FORMAT_CHANGED );
    }
  }
  
 /*
  *  For testing purposes only
  */
  public static void main(String[] args)
  {
    JFrame frame = new JFrame();
    //frame.getContentPane().setLayout( new GridLayout(2,1) );
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setTitle("FormatControl Test");
    frame.setBounds(0,0,135,120);
    Object[] listA = {"ListA0","ListA1","ListA2"};
    Object[] listB = {"ListB0","ListB1","ListB2","ListB3"};
    Object[] listC = {"ListC0","ListC1","ListC2","ListC3","ListC4"};
    Vector list = new Vector();
    list.add(listA);
    list.add(listB);
    list.add(listC);
    FormatControl fc = new FormatControl(list);
    fc.addActionListener( new ActionListener()
      {
        public void actionPerformed( ActionEvent ae )
	{
	  FormatControl temp = (FormatControl)ae.getSource();
	  System.out.println("Last Changed " + temp.getLastChangedIndex() );
	  System.out.println("Index At Last Changed " + 
	                     temp.getLastChangedSelectedIndex() );
	}
      });
    // Test setSelectedIndex()
    fc.setSelectedIndex(0,1);
    fc.setSelectedIndex(1,2);
    fc.setSelectedIndex(2,3);
    // Test setToolTipText()
    fc.setToolTipText(0,"List1");
    fc.setToolTipText(1,"List2");
    fc.setToolTipText(2,"List3");
    
    Object[] listD = {"ListD0","ListD1","ListD2"};
    Object[] listE = {"ListE0","ListE1","ListE2","ListE3"};
    Object[] listF = {"ListC0","ListF1","ListF2","ListF3","ListF4"};
    
    ObjectState state = fc.getObjectState(IPreserveState.PROJECT);
    Vector new_list = new Vector();
    new_list.add(listD);
    new_list.add(listE);
    new_list.add(listF);
    fc.setFormats(new_list);
    // These should have no effect since setting the ObjectState should restore
    // the set indices above.
    fc.setSelectedIndex(0,0);
    fc.setSelectedIndex(1,0);
    fc.setSelectedIndex(2,0);
    fc.setToolTipText(0,"This Should Not Appear");
    fc.setToolTipText(1,"This Should Not Appear");
    fc.setToolTipText(2,"This Should Not Appear");
    fc.setObjectState(state);
    
    frame.getContentPane().add(fc);
    
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
}
