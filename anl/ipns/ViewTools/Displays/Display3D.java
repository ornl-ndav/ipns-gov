/*
 * File:  Display3D.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.2  2005/07/19 16:48:01  cjones
 *  Display3D will now force heavyweight popup menus to allow
 *  menus to appear on top of heavyweight components.
 *
 *  Revision 1.1  2005/07/19 15:57:00  cjones
 *  Added Display3D Viewer.
 * 
 */


package gov.anl.ipns.ViewTools.Displays;

import javax.swing.*;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.text.html.HTMLEditorKit;

import gov.anl.ipns.ViewTools.UI.SplitPaneWithState;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.ThreeD.*;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.IPhysicalArray3D;

/**
 * Display 3D scene of detectors.  This class will render scattered points
 * in the shape of boxes with given position, size, and orientation.  The data
 * should be passed as an array of IPhysicalArray3Ds.
 * 
 * Side controls can be set on/off in the constructor.  Only one view component
 * is available at this time.
 */
public class Display3D extends Display
{
  private IPhysicalArray3D[] datalist;
  
 /**
  * Construct a frame with the specified data. Currently, there is only
  * one view component to render the data, which will be represented
  * using a 3D scene with colored boxes for each point.
  *  
  *  @param  iva Array of Three-dimensional physical array.
  *  @param  view_code Code for which view component is to be used to
  *                    display the data.
  *  @param  include_ctrls If true, controls to manipulate image will be added.
  */
  public Display3D( IPhysicalArray3D[] iva, int view_code, int include_ctrls )
  {
    super(iva[0], view_code, include_ctrls);
    makeHeavyWeightPopup();

    datalist = iva;
    setTitle("Display3D");
    
    addToMenubar();
    buildPane();
  }
  
 /**
  * This method sets the ObjectState of this viewer to a previously saved
  * state.
  *
  *  @param  new_state The previously saved state that this viewer will be
  *                    set to.
  */ 
  public void setObjectState( ObjectState new_state )
  {
    
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
    return new ObjectState();
  }
  
 /**
  * This method takes in a virtual array and updates the scene. 
  *
  *  @param  values
  */ 
  public void dataChanged( IPhysicalArray3D[] values )
  { 
    ((IViewComponent3D)ivc).dataChanged(values);
  }
  
  
 /*
  * This method builds the content pane of the frame.
  */
  private void buildPane()
  { 
    // Clear any existing views, so it can be rebuilt.
    getContentPane().removeAll();
    
    ivc = new SceneViewComponent((IPhysicalArray3D[])datalist);
  
    Box view_comp_controls = buildControlPanel();
    // if user wants controls, and controls exist, display them in a splitpane.
    if( add_controls == CTRL_ALL && view_comp_controls != null )
    {
      setBounds(0,0,700,510);
      pane = new SplitPaneWithState(JSplitPane.HORIZONTAL_SPLIT,
                  ivc.getDisplayPanel(),
                  view_comp_controls, .75f );;
    }
    else 
    {
      setBounds(0,0,500,500);
      pane = ivc.getDisplayPanel();
    }
    
    getContentPane().add( pane );
    addComponentMenuItems();
    
    // Repaint the display, this is needed when the menu items are used
    // the switch between views.
    validate();
    repaint();
  }
  
 /*
  * This private method will (re)build the menubar.
  * If the file being loaded is not found, those menu items
  * must be removed. To do so, rebuild the Menubar.
  * 
  *   CURRENTLY UNFINISHED
  */ 
  private void addToMenubar()
  {
    Vector options           = new Vector();
    Vector save_default      = new Vector();
    Vector help              = new Vector();
    Vector display_help      = new Vector();
    Vector option_listeners  = new Vector();
    Vector help_listeners    = new Vector();
    
    // build options menu
    options.add("Options");
    option_listeners.add( new Menu3DListener() ); // listener for options
    options.add(save_default);
      save_default.add("Save User Settings");
      option_listeners.add( new Menu3DListener() ); // listener for user prefs.
    
    // build help menu
    help.add("Help");
    help_listeners.add( new Menu3DListener() );
    help.add( display_help );
      display_help.add("Using Display3D");
      help_listeners.add( new Menu3DListener() );  // listener for D2D helper
    menu_bar.add( MenuItemMaker.makeMenuItem(options,option_listeners) );
    menu_bar.add( MenuItemMaker.makeMenuItem(help,help_listeners) );
  }

  /*
   * Force popup menus to be heavyweight.
   */
  private void makeHeavyWeightPopup()
  {
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
  	
    for(int i = 0; i < menu_bar.getMenuCount(); i++)
    {
      menu_bar.getMenu(i).
         getPopupMenu().setLightWeightPopupEnabled(false);
    }
  }
  
 /*
  * This class is required to handle all messages within the Display3D.
  */
  private class Menu3DListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      if( ae.getActionCommand().equals("Save User Settings") )
      {
      }
      // Called if user selects help option.
      else if( ae.getActionCommand().equals("Using Display3D") )
      {
      }
  
    }
  }
  
}
