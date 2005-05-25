/*
 * File: ColorSelector.java
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
 *  Revision 1.3  2005/05/25 20:28:26  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.2  2005/01/10 16:13:16  dennis
 *  Removed unused imports.
 *
 *  Revision 1.1  2004/04/23 16:58:05  millermi
 *  - Initial Version - This class allows programmers to easily
 *    add a color chooser to the GUI application without
 *    the use of a dialog box.
 *
 */
package gov.anl.ipns.Util.Sys;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import gov.anl.ipns.ViewTools.UI.ActiveJPanel;
import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * This class is a simplified version of the JColorChooser. The advantage
 * of the ColorSelector is the ability to add one or all of the color
 * selectors to a GUI component. Also, the selector uses ActionListeners
 * instead of ChangeListeners to notify listeners of a colorchange.
 */
public class ColorSelector extends ActiveJPanel
{
 /**
  * "Color Changed" - This message String is sent to all listeners of this
  * class whenever the selected color is changed.
  */
  public static String COLOR_CHANGED = "Color Changed";
 
 /**
  * 0 - Use this variable to specify the color chooser with swatches. This
  * chooser is ideal when a variety of colors is desired. Using tabbed display,
  * this chooser is under the Swatches tab.
  */ 
  public static int SWATCH = 0;
 
 /**
  * 1 - Use this variable to specify the color chooser with a blended
  * display of colors. This chooser is ideal when a variety of shades of
  * one color is desired. Using tabbed display, this chooser is under the
  * HSB tab.
  */ 
  public static int BLEND = 1;
 
 /**
  * 2 - Use this variable to specify the color chooser that uses sliders
  * to determine amounts of red, green, and blue in a color. 
  * This chooser is ideal when a variety of shades of a variety of
  * colors is desired. Using tabbed display, this chooser is under the
  * RGB tab.
  */ 
  public static int SLIDER = 2;
  
 /**
  * 3 - Use this variable to have all of the color choosers displayed in
  * a tabbed layout.
  */
  public static int TABBED = 3;
  
  private JColorChooser colorchooser;
  private Color current_color;
  
 /**
  * Constructor - Specify the color model and get a JPanel containing that
  * chooser.
  *
  *  @param  chooser_model The chooser model desired. Static String constants
  *                        have been provided by this class to provide options.
  */
  public ColorSelector( int chooser_model )
  { 
    colorchooser = new JColorChooser(Color.black);
    current_color = Color.black;
    setModel(chooser_model); 
  }
  
 /**
  * Get the currently selected color of the current color chooser.
  *
  *  @return The currently selected color.
  */ 
  public Color getSelectedColor()
  {
    return current_color;
  }
  
 /**
  * This method will set the model that will be displayed. This method
  * does not need to be called unless the chooser model type needs to be
  * changed. If the model type needs to change often, consider using the
  * TABBED chooser type.
  *
  *  @param  chooser_model The chooser model desired. Static String constants
  *                        have been provided by this class to provide options.
  */ 
  public void setModel( int chooser_model )
  {
    // remove all components on this color selector.
    removeAll();
    // get the color selection part of the colorchooser
    AbstractColorChooserPanel[] acc = colorchooser.getChooserPanels();
    colorchooser.getSelectionModel().addChangeListener(
    					new ColorChangedListener() );
    // add one color chooser
    if( !(chooser_model < 0) && chooser_model < 3 )
      add(acc[chooser_model]);
    // add tabbed pane with all color models.
    else if( chooser_model == 3 )
    {
      JTabbedPane selectors = new JTabbedPane();
      selectors.add( "Swatches", acc[0] );
      selectors.add( "HSB", acc[1] );
      selectors.add( "RGB", acc[2] );
      add(selectors);
    }
  }
 
 /*
  * This class converts ChangeListener events to ActionListeners and notifies
  * all listeners when the color has changed.
  */   
  private class ColorChangedListener implements ChangeListener
  {
    public void stateChanged( ChangeEvent ce )
    {
      current_color = colorchooser.getSelectionModel().getSelectedColor();
      send_message( COLOR_CHANGED );
    }
  }
 
 /**
  * For testing purposes only.
  */ 
  public static void main( String args[] )
  {    
    JFrame display = new JFrame("ColorSelector Test");
    ColorSelector cs = new ColorSelector(ColorSelector.TABBED);
    cs.addActionListener( new ColorListener() );
    display.getContentPane().add( cs );
    display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    display.setBounds(0,0,450,300);
    
    // show the display tester
    WindowShower.show(display);
  } // end main
  
 /*
  * This class is only here to assist main() for testing purposes.
  */ 
  private static class ColorListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      if(ae.getActionCommand().equals(ColorSelector.COLOR_CHANGED) )
      {
        ColorSelector selector = (ColorSelector)ae.getSource();
  	System.out.println("Color: " + selector.getSelectedColor() );
      }  
    }
  }
  
}
