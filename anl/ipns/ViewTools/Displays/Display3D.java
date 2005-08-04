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
 *  Revision 1.4  2005/08/04 22:24:12  cjones
 *  Added help frame to give a general overiew of Display3D. Also, made fixes
 *  to the comment header.
 *
 *  Revision 1.3  2005/07/22 19:47:45  cjones
 *  Display3D can now accept IPhysicalArray3D[] or IPhysicalArray3DList[]
 *  as data input.  If the list version is given, a frame controller will
 *  be added to the controls.
 *
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
import javax.swing.text.html.HTMLEditorKit;

import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.UI.SplitPaneWithState;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.ThreeD.*;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.IPhysicalArray3D;

/**
 * This object displays a 3D scene of detectors.  The class will render scattered
 * points in the shape of boxes with given position, size, and orientation.  The 
 * data should be passed as an array of IPhysicalArray3D or IPhysicalArray3DList.
 * 
 * Side controls can be set on/off in the constructor. If the data is given
 * as IPhysicalArray3DList[], an extra control for controlling frames will
 * be given.
 */
public class Display3D extends Display
{
  private IPointList3D[] datalist;
  
  private static JFrame helper = null;
  
 /**
  * Construct a 3D display frame with the specified data. This constructor
  * is intended for data points with single values. Side panel controls can
  * be enabled or disabled.
  *  
  *  @param  iva           Array of Three-dimensional points with physical 
  *                        volume information.
  *  @param  view_code     Code for which view component is to be used to
  *                        display the data.
  *  @param  include_ctrls Code for which controls will be given to 
   *                        manipulate the scene.
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
  * Construct a 3D display frame with the specified data. This constructor
  * is intended for data points with lists values. Side panel controls can
  * be enabled or disabled.
   *  
   *  @param  iva           Array of 3D dimensional points with lists of data 
   *                        values and physical volume information.
   *  @param  view_code     Code for which view component is to be used to
   *                        display the data.
   *  @param  include_ctrls Code for which controls will be given to 
   *                        manipulate the scene.
   */
   public Display3D( IPhysicalArray3DList[] iva, int view_code, int include_ctrls )
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
  * UNFINSHED
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
  *  UNFINISHED
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */
  public ObjectState getObjectState( boolean isDefault )
  {
    return null;
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
  
 /**
  * This method takes in a virtual array and updates the scene. 
  *
  *  @param  values
  */ 
  public void dataChanged( IPhysicalArray3DList[] values )
  { 
    ((IViewComponent3D)ivc).dataChanged(values);
  }
  
  
 /*
  * This method builds the content pane of the frame.
  * 
  * It will check the type of data and pick the view component
  * accordingly.
  */
  private void buildPane()
  { 
    // Clear any existing views, so it can be rebuilt.
    getContentPane().removeAll();
    
    // Does data have frames or not.
    if(datalist instanceof IPhysicalArray3D[])
    	ivc = new SceneViewComponent((IPhysicalArray3D[])datalist);
    else if(datalist instanceof IPhysicalArray3DList[])
    	ivc = new SceneFramesViewComponent((IPhysicalArray3DList[])datalist);
    else
    	return;
  
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
  */ 
  private void addToMenubar()
  {
    Vector options           = new Vector();
    Vector save_default      = new Vector();
    Vector switch_view       = new Vector();
    Vector help              = new Vector();
    Vector display_help      = new Vector();
    Vector option_listeners  = new Vector();
    Vector help_listeners    = new Vector();
    
    menu_bar.add( new JMenu("Options") );
    
    // build help menu
    help.add("Help");
    help_listeners.add( new Menu3DListener() );
    help.add( display_help );
      display_help.add("Using Display3D");
      help_listeners.add( new Menu3DListener() );  // listener for D3D helper
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
  
  /**
   * Contains/Displays control information about this viewer.
   */
   public static void help()
   {
     helper = new JFrame("Introduction to the Display3D");
     helper.setBounds(0,0,600,400);
     JEditorPane textpane = new JEditorPane();
     textpane.setEditable(false);
     textpane.setEditorKit( new HTMLEditorKit() );
     String text = "<H1>Description:</H1> <P>" + 
        "The Display3D provides a 3D representation of data, " +
        "which allows users to quickly examine detector " +
 		"configurations and pixel values. A 3D scene is created " +
 		"to provide a physical form to the pixels, closely " +
 		"matching the detectors actual layouts. The pixels can be " +
 		"colored by value using several selectable color models and " +
 		"a variable brightness setting. The user is then capable of " +
 		"moving about the scene, examining specific pixels, or " +
 		"selecting groups of pixels for use elsewhere.</P>";
     textpane.setText(text);
     JScrollPane scroll = new JScrollPane(textpane);
     scroll.setVerticalScrollBarPolicy(
         			    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
     helper.getContentPane().add(scroll);
     WindowShower.show(helper);
   }
  
 /*
  * This class is required to handle all messages within the Display3D.
  */
  private class Menu3DListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      // Called if user selects help option.
      if( ae.getActionCommand().equals("Using Display3D") )
      {
      	help();
      }
  
    }
  }
  
}
