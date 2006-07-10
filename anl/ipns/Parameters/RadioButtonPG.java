/*
 * File:  RadioButtonPG.java
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
 *  Revision 1.1  2006/07/10 16:25:06  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.2  2006/07/04 18:36:52  dennis
 *  Factored out common methods for manipulating the list of
 *  possible Strings.  Now extends the abstract base class
 *  StringListChoicePG.
 *
 *  Revision 1.1  2006/07/04 17:41:33  dennis
 *  Initial version of PG with radio buttons to select from a
 *  list of Strings.
 *
 */

package gov.anl.ipns.Parameters;

import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 * A RadioButtonPG uses radio buttons to present the users with a
 * list of of Strings to choose from. 
 */
public class RadioButtonPG extends StringListChoicePG
{
  private JPanel   panel    = null;
  private JPanel   b_panel  = null;
  private JLabel   label    = null;
  private boolean  enabled  = true;    // we store the enabled state, so the
                                       // setEnabled() method can be called
                                       // before constructing the widget.

  /**
   * Creates a new RadioButtonPG object with the specified name and initial
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
  public RadioButtonPG( String name, Object val ) 
                                        throws IllegalArgumentException
  {
    super( name, "" );  
                          // NOTE: The construction of the GUI with label
                          //       and widget is done on demand, so it does
                          //       NOT get done in the constructor.  We just
                          //       need to call the super class constructor
                          //       to record the name and default value.

    InitChoiceVector( val );
  }

  
  /**
   * Construct a copy of this StringPG object.
   *
   * @return A copy of this StringPG, with the same name and value.
   */
  public Object clone() 
  {
     RadioButtonPG copy = new RadioButtonPG( getName(), str_value );
     copy.choices = new Vector();
     for ( int i = 0; i < choices.size(); i++ )
       copy.choices.add( choices.elementAt(i) );

     copy.setValidFlag( getValidFlag() );
     return copy;
  }

  
   /**
   * Get a JPanel containing a composite entry widget for getting a value from
   * the user.  In this case, the panel contains two children, a label 
   * giving the name (i.e. prompt string) and a JPanel with a list of
   * RadioButtons.  The RadioButton's values and enabled state is set 
   * from the currently recorded values. 
   *
   * @return a JPanel containing a component to enter a value.
   */
  protected JPanel getWidget() 
  {
    if( panel == null )            // make new panel with label & RadioButtons 
    {
      label    = new JLabel( getName() );
      b_panel  = new JPanel();

      if ( choices.size() == 0 )
        choices.add( str_value );
      else
      {
        if ( !choices.contains( str_value ) )
          choices.insertElementAt( str_value, 0 );
      }

      int num_choices = choices.size();
      b_panel.setLayout( new GridLayout( num_choices, 1 ) );
      ButtonGroup b_group = new ButtonGroup();
 
      for ( int i = 0; i < choices.size(); i++ )
      {
        JRadioButton button = new JRadioButton((String)choices.elementAt(i));
        b_panel.add( button );
        if ( str_value.equals( (String)choices.elementAt(i) ) )
          button.setSelected(true);
        b_group.add( button );
        button.addActionListener( new PG_ActionListener( this ) );
      }

      panel    = new JPanel( new GridLayout( 1, 2 ) );
      panel.add( label );
      panel.add( b_panel );
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
    panel   = null;                     // null out all references to gui 
    b_panel = null;                     // components, so that they can be
    label   = null;                     // garbage collected.
  }


  /**
   * Enable or disable the RadioButtons for entering values. 
   *
   * @param  on_off  Set true to enable the RadioButtons for user input.
   */
  public void setEnabled( boolean on_off )
  {
    enabled = on_off;
    if ( panel != null )                // panel, box and label are created and
    {                                   // destroyed together, so we can just 
                                        // check that the panel is there
      label.setEnabled( on_off );
      Component components[] = b_panel.getComponents();
      for ( int i = 0; i < components.length; i++ )
        components[i].setEnabled( on_off );
    }
  }


  /**
   * Retrieves the String associated with the currently selected JRadioButton.  
   * 
   * @return The String value from the selected JRadioButton, if possible.
   *
   * @throws IllegalArgumentException is thrown if this is called without
   *         a GUI widget being present, or if the value is invalid.
   */
  protected String getWidgetValue() throws IllegalArgumentException
  {
     if ( panel == null )
      throw new IllegalArgumentException(
              "getWidgetValue() called when no StringPG widget exists");
    else
    {
      Component components[] = b_panel.getComponents();
      for ( int i = 0; i < components.length; i++ )
      {
        JRadioButton button = (JRadioButton)components[i];
        if ( button.isSelected() )
          return (String)choices.elementAt(i);
      }
    }
    return str_value;
  }


  /**
   * Sets the selected String to the specified value.
   *
   * @param value  The String value to selecte in the list of radio buttons.
   *
   * @throws IllegalArgumentException is thrown if this is called without
   *         a GUI widget being present.
   */
  protected void setWidgetValue( String value ) throws IllegalArgumentException
  {
    if ( panel == null )
      throw new IllegalArgumentException(
              "setWidgetValue() called when no BooleanPG widget exists");

    Component components[] = b_panel.getComponents();
    for ( int i = 0; i < components.length; i++ )
    {
      if ( value.equals((String)choices.elementAt(i) ) )
      {
        JRadioButton button = (JRadioButton)components[i];
        button.setSelected(true);
        return;
      }
    }
  }

}
