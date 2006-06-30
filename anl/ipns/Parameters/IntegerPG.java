/*
 * File:  IntegerPG.java
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
 *  Revision 1.4  2006/06/30 14:24:40  dennis
 *  Removed unused imports.
 *
 *  Revision 1.3  2006/06/29 15:47:56  dennis
 *  Modified to use the FilteredPG_TextField.
 *
 *  Revision 1.2  2006/06/28 21:39:16  dennis
 *  Now uses a PG_DocumentFilter, with an IntegerFilter to restrict
 *  the characters that can be entered in the IntegerPG.
 *
 *  Revision 1.1  2006/06/15 23:35:43  dennis
 *  Concrete base class for classes that will allow the user to
 *  enter an integer value.
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.*;
import javax.swing.*;

/**
 *  An IntegerPG uses a JTextField component to let the user specify an
 *  integer value.
 */
public class IntegerPG extends IntegerPG_base
{
                                         // Use a text field that can filter
                                         // Strings to just keep valid ones
  private FilteredPG_TextField text_field = null;

  private JPanel     panel      = null;
  private JLabel     label      = null;
  private boolean    enabled    = true;  // we store the enabled state, so the
                                         // setEnabled() method can be called
                                         // before constructing the widget.

  /**
   * Creates a new IntegerPG object with the specified name and initial
   * value.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a integer value.
   */
  public IntegerPG( String name, Object val ) throws IllegalArgumentException
  {
    super( name, val );   // NOTE: The construction of the GUI with label
                          //       and widget is done on demand, so it does
                          //       NOT get done in the constructor.  We just
                          //       need to call the super class constructor
                          //       to record the name and default value.
  }

  
  /**
   * Construct a copy of this IntegerPG object.
   *
   * @return A copy of this IntegerPG, with the same name and value.
   */
  public Object getCopy() 
  {
     IntegerPG copy = new IntegerPG( getName(), new Integer( int_value ) );
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
      text_field = new FilteredPG_TextField( this, new IntegerFilter() );
      label      = new JLabel( getName() );

      panel.add( label );
      panel.add( text_field );
 
      text_field.addActionListener( new PG_ActionListener( this ) );
    }

    setEnabled( enabled );                 // set widget state from
    setWidgetValue( int_value );           // current information

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
   * @return The integer value from the JTextField, if possible.
   *
   * @throws IllegalArgumentException is thrown if this is called without
   *         a GUI widget being present, or if the value is invalid.
   */
  protected int getWidgetValue() throws IllegalArgumentException
  {
    int widget_value = 0;

    if ( text_field == null )
      throw new IllegalArgumentException(
              "getWidgetValue() called when no IntegerPG widget exists");

    else
      widget_value = Conversions.get_int( text_field.getText() );      

    return widget_value;
  }


  /**
   * Sets the value displayed in the JTextField to the specified value.
   *
   * @param value  The integer value to record in the JTextField.
   *
   * @throws IllegalArgumentException is thrown if this is called without
   *         a GUI widget being present.
   */
  protected void setWidgetValue( int value ) throws IllegalArgumentException
  {
    if ( text_field == null )
      throw new IllegalArgumentException(
              "setWidgetValue() called when no BooleanPG widget exists");

    text_field.setText( ""+value );
  }


}
