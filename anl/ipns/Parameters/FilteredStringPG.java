/*
 * File:  FilteredStringPG.java
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
 *  Revision 1.2  2006/06/30 14:24:41  dennis
 *  Removed unused imports.
 *
 *  Revision 1.1  2006/06/29 20:03:38  dennis
 *  This is an abstract base class for PGs whose value is a String
 *  subject to input restrictions.  The input restrictions are
 *  determined by specifying a particular filter.  Derived classes
 *  for different types of restrictions just need a constructor that
 *  calls the constructor for this class, with the correct filter.
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.*;
import javax.swing.*;


/**
 * FilteredStringPG is an abstract base class for all String valued PGs
 * that use a filter to limit the form of the Strings that can be entered.
 */
public abstract class FilteredStringPG extends StringPG_base
{
  private FilteredPG_TextField text_field = null;
  private IStringFilter        my_filter  = null;

  private JPanel     panel      = null;
  private JLabel     label      = null;
  private boolean    enabled    = true;  // we store the enabled state, so the
                                         // setEnabled() method can be called
                                         // before constructing the widget.

  /**
   * Creates a new FilteredStringPG object with the specified name and initial
   * value.
   *
   * @param  name   The name (i.e. prompt string) for this PG.
   * @param  val    The initial value for this PG.
   * @param  filter The filter to apply to Strings as characters are being
   *                typed in.  If null is passed in for the filter, all
   *                Strings will be accepted.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a String value.
   */
  public FilteredStringPG( String name, Object val, IStringFilter filter ) 
                                        throws IllegalArgumentException
  {
    super( name, Conversions.get_String( val ) );  
                          // NOTE: The construction of the GUI with label
                          //       and widget is done on demand, so it does
                          //       NOT get done in the constructor.  We just
                          //       need to call the super class constructor
                          //       to record the name and default value.

    if ( filter != null )
      my_filter = filter;
    else
      my_filter = new AllPassFilter();
  }

  
  /**
   * Construct a copy of this StringPG object.
   *
   * @return A copy of this StringPG, with the same name and value.
   */
  public Object getCopy() 
  {
     StringPG copy = new StringPG( getName(), str_value );
     copy.setValidFlag( getValidFlag() );
     return copy;
  }

  
   /**
   * Get a JPanel containing a composite entry widget for getting a value from
   * the user.  In this case, the panel contains two children, a label 
   * giving the name (i.e. prompt string) and a JTextField. 
   * The JTextField's value and enabled state is set from the currently
   * recorded values. 
   *
   * @return a JPanel containing a component to enter a value.
   */
  protected JPanel getWidget() 
  {
    if( panel == null )                // make new panel with label & TextField 
    {
      panel      = new JPanel( new GridLayout( 1, 2 ) );
      text_field = new FilteredPG_TextField( this, my_filter );
      label      = new JLabel( getName() );

      panel.add( label );
      panel.add( text_field );
 
      text_field.addActionListener( new PG_ActionListener( this ) );
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
    panel      = null;                     // null out all references to gui 
    text_field = null;                     // components, so that they can be
    label      = null;                     // garbage collected.
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
      text_field.setEnabled( on_off );  // check that the panel is there
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

    if ( text_field == null )
      throw new IllegalArgumentException(
              "getWidgetValue() called when no StringPG widget exists");

    else
      widget_value = Conversions.get_String( text_field.getText() );      

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
    if ( text_field == null )
      throw new IllegalArgumentException(
              "setWidgetValue() called when no BooleanPG widget exists");

    text_field.setText( ""+value );
  }

}
