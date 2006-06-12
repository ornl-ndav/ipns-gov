/*
 * File:  BooleanPG.java
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
 *  Revision 1.1  2006/06/12 21:52:27  dennis
 *  Initial version of new code for parameter GUIs.  While this is
 *  loosely based on the parameter GUIs developed several years ago
 *  by Peter Peterson and Chris Bouzek, the current system is much
 *  simplified, and should be easier to maintain and extend.
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.*;
import javax.swing.*;

/**
 *  A BooleanPG uses a JCheckBox component to let the user specify a value
 *  of true (box checked) or false (box unchecked).
 */
public class BooleanPG extends BooleanPG_base
{
  private JPanel    panel   = null;
  private JCheckBox box     = null;
  private JLabel    label   = null;
  private boolean   enabled = true;    // we store the enabled state, so the
                                       // setEnabled() method can be called
                                       // before constructing the widget.

  /**
   * Creates a new BooleanPG object with the specified name and initial
   * value.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a boolean value.
   */
  public BooleanPG( String name, Object val ) throws IllegalArgumentException
  {
    super( name, val );   // NOTE: The construction of the GUI with label
                          //       and widget is done on demand, so it does
                          //       NOT get done in the constructor.  We just
                          //       need to call the super class constructor
                          //       to record the name and default value.
  }

  
  /**
   * Construct a copy of this BooleanPG object.
   *
   * @return A copy of this BooleanPG, with the same name and value.
   */
  public Object getCopy() 
  {
     BooleanPG copy = new BooleanPG( getName(), getValue() );
     return copy;
  }

  
   /**
   * Get a JPanel containing a composite entry widget for getting a value from
   * the user.  In this case, the panel contains two children, a label 
   * giving the name (i.e. prompt string) and a JCheckBox. 
   * The JCheckBox's value and enabled state is set from the currently
   * recorded values. 
   *
   * @return a JPanel containing a component to enter a value.
   */
  protected JPanel getWidget() 
  {
    if( panel == null )                // make new panel with label & checkbox
    {
      panel = new JPanel( new GridLayout( 1, 2 ) );
      box   = new JCheckBox();
      label = new JLabel( getName() );

      panel.add( label );
      panel.add( box );
    }

    setEnabled( enabled );                         // set widget state from
    setWidgetValue( bool_value );                  // current information

    return panel;
  }


  /**
   * Set all internal references to the JPanel and entry components to NULL,
   * so that it can be garbage collected.  Subsequent calls to getPGWidget()
   * will return a new JPanel and entry components.
   */
  protected void destroyWidget()
  {
    panel = null;                     // null out all references to gui 
    box   = null;                     // components, so that they can be
    label = null;                     // garbage collected.
  }


  /**
   * Enable or disable the JCheckBox for entering values. 
   *
   * @param  on_off  Set true to enable the JCheckBox for user input.
   */
  public void setEnabled( boolean on_off )
  {
    enabled = on_off;
    if ( panel != null )              // panel, box and label are created and
    {                                 // destroyed together, so we can just 
      box.setEnabled( on_off );       // check that the panel is there
      label.setEnabled( on_off );
    }
  }


  /**
   * Retrieves the JCheckBox's current value.  
   * 
   * @return The value of isSelected() from the JCheckBox.
   *
   * @throws IllegalArgumentException is thrown if this is called without
   *         a GUI widget being present.
   */
  protected boolean getWidgetValue() throws IllegalArgumentException
  {
    if ( box == null )
      throw new IllegalArgumentException(
              "getWidgetValue() called when no BooleanPG widget exists");

    return box.isSelected();
  }


  /**
   * Sets the value displayed in the JCheckBox to the specified value.
   *
   * @param value  The value to pass to the setSelected() method of the
   *               JCheckbox. 
   *
   * @throws IllegalArgumentException is thrown if this is called without
   *         a GUI widget being present.
   */
  protected void setWidgetValue(boolean value) throws IllegalArgumentException
  {
    if ( box == null )
      throw new IllegalArgumentException(
              "setWidgetValue() called when no BooleanPG widget exists");

    box.setSelected( value );
  }


}
