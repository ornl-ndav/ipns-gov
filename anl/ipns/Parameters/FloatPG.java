/*
 * File:  FloatPG.java
 *
 * Copyright (C) 2006, Julian Tao
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
 * Contact : Julian Tao <taoj@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.5  2006/06/30 14:24:40  dennis
 * Removed unused imports.
 *
 * Revision 1.4  2006/06/29 15:47:55  dennis
 * Modified to use the FilteredPG_TextField.
 *
 * Revision 1.3  2006/06/28 22:32:53  dennis
 * Modified to use the PG_DocumentFilter with a FloatFilter to
 * prevent the user from entering some invalid values for a float.
 *
 * Revision 1.2  2006/06/27 20:36:03  dennis
 * Removed commented out actionPerformed() method.
 * Added tag to include log messages.
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.*;
import javax.swing.*;


/**
 *  A BooleanPG uses a JCheckBox component to let the user specify a value
 *  of true (box checked) or false (box unchecked).
 */
public class FloatPG extends FloatPG_base
{
                                         // Use a text field that can filter
                                         // Strings to just keep valid ones
  private FilteredPG_TextField field = null;

  private JPanel     panel   = null;
  private JLabel     label   = null;
  private boolean    enabled = true;   // we store the enabled state, so the
                                       // setEnabled() method can be called
                                       // before constructing the widget.

  /**
   * Creates a new FloatPG object with the specified name and initial
   * value.
   *
   * @param  name  The name (i.e. prompt string) for this PG.
   * @param  val   The initial value for this PG.
   *
   * @throws IllegalArgumentException is thrown, if the specified value
   *         cannot be converted to a boolean value.
   */
  public FloatPG( String name, Object val ) throws IllegalArgumentException
  {
    super( name, val );   // NOTE: The construction of the GUI with label
                          //       and widget is done on demand, so it does
                          //       NOT get done in the constructor.  We just
                          //       need to call the super class constructor
                          //       to record the name and default value.
  }

  
  /**
   * Construct a copy of this FloatPG object.
   *
   * @return A copy of this FloatPG, with the same name and value.
   */
  public Object getCopy() 
  {
     FloatPG copy = new FloatPG( getName(), new Float( float_value ) );
     copy.setValidFlag( getValidFlag() );
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
      field = new FilteredPG_TextField( this, new FloatFilter() );
      label = new JLabel( getName() );

      panel.add( label );
      panel.add( field );

      field.addActionListener( new PG_ActionListener( this ) );
    }

    setEnabled( enabled );                         // set widget state from
    setWidgetValue( float_value );                  // current information

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
    field = null;                     // components, so that they can be
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
      field.setEnabled( on_off );       // check that the panel is there
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
  protected float getWidgetValue() throws IllegalArgumentException
  {
    float widget_value = 0.0f;
    
    if ( field == null )
      throw new IllegalArgumentException(
              "getWidgetValue() called when no FloatPG widget exists");

    else widget_value = Conversions.get_float( field.getText() );
    
    return widget_value;
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
  protected void setWidgetValue(float value) throws IllegalArgumentException
  {
    if ( field == null )
      throw new IllegalArgumentException(
              "setWidgetValue() called when no BooleanPG widget exists");

    field.setText( (new Float(value)).toString() );
  }

}
