/*
 * File:  ChoiceListPG.java
 *
 * Copyright (C) 2006, Dennis Mikkelson
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2006/06/30 16:13:16  dennis
 *  This PG uses a JComboBox to present a list of choices.
 *  A few basic methods related to maintaining the Vector
 *  of choices ( clear(), addItem() ) should be factored out
 *  so that they can be shared with the RadioButtonPG
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 * A ChoiceListPG uses a combo box to present the users with a list of
 * of Strings to choose from. 
 */
public class ChoiceListPG extends StringPG_base
{
  private Vector     choices  = null;
  private JComboBox  combobox = null;
  private JPanel     panel    = null;
  private JLabel     label    = null;
  private boolean    enabled  = true;  // we store the enabled state, so the
                                       // setEnabled() method can be called
                                       // before constructing the widget.

  /**
   * Creates a new ChoiceListPG object with the specified name and initial
   * value.  If the object passed in is a Vector of Strings, the vector of
   * Strings will determine the list of possible choices, and the initially
   * selected choice as follows.  If the last String in the Vector is NOT
   * equal to one of the earlier Strings, the list of Strings will be used
   * as the choices, and the first String will be the default value.  If
   * the last String in the Vector is a repeat of an earlier String, then
   * the list of choices will be all but the last String, and the last 
   * String will designate which choice is the default value.
   * If the object passed in is NOT a Vector of Strings, the object will
   * be converted to a String, if possible, and used as the value of this
   * PG.  Additional items may be added to the list of choices using the
   * addItem() method, but these will not have an immediate effect if the
   * combo box is currently displayed.
   *
   * @param  name   The name (i.e. prompt string) for this PG.
   * @param  val    String or Vector of Strings giving the value 
   *                for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a String value.
   */
  public ChoiceListPG( String name, Object val ) 
                                        throws IllegalArgumentException
  {
    super( name, "" );  
                          // NOTE: The construction of the GUI with label
                          //       and widget is done on demand, so it does
                          //       NOT get done in the constructor.  We just
                          //       need to call the super class constructor
                          //       to record the name and default value.

    choices = new Vector();
    if ( val == null )
    {
       str_value = "NO CHOICES";
       return;
    }

    if ( val instanceof Vector )                // get an array of Strings from
    {                                           // the Vector
      String list[] = new String[ ((Vector)val).size() ];
      for ( int i = 0; i < list.length; i++ )
        list[i] = Conversions.get_String( ((Vector)val).elementAt(i) );

      boolean default_specified = false;
      String last = list[ list.length - 1 ];
      for ( int i = 0; i < list.length - 1; i++ )
        if ( list[i].equals( last ) )
          default_specified = true;

      if ( default_specified )
      {
        str_value = last;
        for ( int i = 0; i < list.length - 1; i++ )
          addItem( list[i] );
      }
      else
      {
        str_value = list[0];
        for ( int i = 0; i < list.length; i++ )
          addItem( list[i] );
      }
    }
    else                                        // set value from simple object
      str_value = Conversions.get_String( val );
  }

  
  /**
   * Construct a copy of this StringPG object.
   *
   * @return A copy of this StringPG, with the same name and value.
   */
  public Object getCopy() 
  {
     ChoiceListPG copy = new ChoiceListPG( getName(), str_value );
     copy.choices = new Vector();
     for ( int i = 0; i < choices.size(); i++ )
       copy.choices.add( choices.elementAt(i) );

     copy.setValidFlag( getValidFlag() );
     return copy;
  }

  
   /**
   * Get a JPanel containing a composite entry widget for getting a value from
   * the user.  In this case, the panel contains two children, a label 
   * giving the name (i.e. prompt string) and a JComboBox. 
   * The JComboBox's value and enabled state is set from the currently
   * recorded values. 
   *
   * @return a JPanel containing a component to enter a value.
   */
  protected JPanel getWidget() 
  {
    if( panel == null )                // make new panel with label & ComboBox 
    {
      panel    = new JPanel( new GridLayout( 1, 2 ) );
      combobox = new JComboBox();
      label    = new JLabel( getName() );

      if ( choices.size() == 0 )
        combobox.addItem( str_value );      // one choice, the current value
      else
        for ( int i = 0; i < choices.size(); i++ )
          combobox.addItem( choices.elementAt(i) );

      panel.add( label );
      panel.add( combobox );
 
      combobox.addActionListener( new PG_ActionListener( this ) );
    }

    setEnabled( enabled );                 // set widget state from
    setWidgetValue( str_value );           // current information

    return panel;
  }


  /**
   * Set all internal references to the JPanel and entry components to NULL,
   * so that it can be garbage collected.  Subsequent calls to getPGWidget()
   * will return a new JPanel and entry components.
   */
  protected void destroyWidget()
  {
    panel    = null;                     // null out all references to gui 
    combobox = null;                     // components, so that they can be
    label    = null;                     // garbage collected.
  }


  /**
   *  Add a new item to the list of choices to use, the next time that
   *  the widget is created.  NOTE: This does not have an effect on a 
   *  widget that is currently displayed.
   *
   *  @param item  A String to add to the list of items to choose from.
   */
  public void addItem( String item )
  {
    choices.add( item ); 
  }


  /**
   * Used to clear the list of available choices, by setting the Vector
   * of choices to a new empty Vector.
   */
  public void clear()
  {
    super.clear();
    choices = new Vector();
  }


  /**
   * Enable or disable the JTextField for entering values. 
   *
   * @param  on_off  Set true to enable the JTextField for user input.
   */
  public void setEnabled( boolean on_off )
  {
    enabled = on_off;
    if ( panel != null )                // panel, box and label are created and
    {                                   // destroyed together, so we can just 
      combobox.setEnabled( on_off );    // check that the panel is there
      label.setEnabled( on_off );
    }
  }


  /**
   * Retrieves the JTextField's current value.  
   * 
   * @return The String value from the JTextField, if possible.
   *
   * @throws IllegalArgumentException is thrown if this is called without
   *         a GUI widget being present, or if the value is invalid.
   */
  protected String getWidgetValue() throws IllegalArgumentException
  {
    String widget_value = "";

    if ( combobox == null )
      throw new IllegalArgumentException(
              "getWidgetValue() called when no StringPG widget exists");

    else
      widget_value = Conversions.get_String( combobox.getSelectedItem() );      

    return widget_value;
  }


  /**
   * Sets the value displayed in the JTextField to the specified value.
   *
   * @param value  The String value to record in the JTextField.
   *
   * @throws IllegalArgumentException is thrown if this is called without
   *         a GUI widget being present.
   */
  protected void setWidgetValue( String value ) throws IllegalArgumentException
  {
    if ( combobox == null )
      throw new IllegalArgumentException(
              "setWidgetValue() called when no BooleanPG widget exists");

    combobox.setSelectedItem( value );
  }

}
