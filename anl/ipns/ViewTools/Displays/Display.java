/*
 * File:  Display.java
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.10  2004/08/13 03:38:04  millermi
 * - Added removeComponentMenuItems() to remove a component's menu
 *   items from the JMenuBar.
 * - Moved image specific menu items to Display2D
 *
 * Revision 1.9  2004/07/30 17:43:12  millermi
 * - Fixed null pointer exception, now checks to see if controls
 *   list is null before checking if length = 0.
 *
 * Revision 1.8  2004/05/20 03:33:37  millermi
 * - Removed unused variables.
 *
 * Revision 1.7  2004/05/11 01:55:30  millermi
 * - Updated the class description in the javadocs.
 *
 * Revision 1.6  2004/04/29 06:20:25  millermi
 * - Added window listener to listen when the display window
 *   is closed. Upon closing, the kill() method is called on
 *   the ViewComponent to close any of its remaining windows.
 *
 * Revision 1.5  2004/04/02 20:59:46  millermi
 * - Fixed javadoc errors
 *
 * Revision 1.4  2004/03/19 21:30:06  millermi
 * - Changed controls parameter to int instead of boolean.
 * - Added CTRL_ALL and CTRL_NONE to Display as constants for
 *   control parameter.
 * - Moved Options menu from Display to Display2D.
 * - Added method loadProps() to load the properties file.
 *
 * Revision 1.3  2004/03/15 23:53:54  dennis
 * Removed unused imports, after factoring out the View components,
 * Math and other utils.
 *
 * Revision 1.2  2004/03/13 07:42:05  millermi
 * - Removed unused imports.
 * - Finished factoring out Display from Display2D.
 * - ObjectState now implemented, but needs IViewComponet to
 *   extend IPreserveState before completion.
 * - Wrote meaningful help dialogue.
 *
 * Revision 1.1  2004/03/12 23:22:43  millermi
 * - Initial Version - Factored out common functionality between
 *   into displays this abstract base class.
 *
 */

package gov.anl.ipns.ViewTools.Displays;

import javax.swing.*;
import java.util.Vector;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.io.Serializable;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

import gov.anl.ipns.ViewTools.UI.SplitPaneWithState;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.UI.FontUtil;

/**
 * This class acts as a base class for all display classes. Displays are
 * flexible, portable viewers that enable users to quickly visualize data.
 * Displays are not intended to manipulate or make calculations with data,
 * only to visualize the data in a graphical manner. Displays include the
 * graph (Display1D), image (Display2D), and soon the table (both 1D and 2D).
 */
abstract public class Display extends JFrame implements IPreserveState,
                                                       Serializable
{ 
 /**
  * 0 - This static int tells the Display class to display no controls.
  */
  public static final int CTRL_NONE = 0;
  
 /**
  * 1 - This static int tells the Display class to display all of the controls.
  */
  public static final int CTRL_ALL  = 1; 
  // complete viewer, includes controls and ijp
  protected transient Container pane;
  protected transient IViewComponent ivc;
  protected transient IVirtualArray data;
  protected transient JMenuBar menu_bar;
  protected transient Display this_viewer;
  protected Vector Listeners = new Vector();
  protected int current_view = 0;
  protected int add_controls = CTRL_NONE;
  
 /**
  * Construct a frame with the specified image and title
  *  
  *  @param  iva Two-dimensional virtual array.
  *  @param  view_code Code for which view component is to be used to
  *                    display the data.
  *  @param  ctrl_option If true, controls to manipulate image will be added.
  */
  protected Display( IVirtualArray iva, int view_code, int ctrl_option )
  {
    // make sure data is not null
    if( iva == null )
    {
      System.out.println("Error in Display - Virtual Array is null");
      System.exit(-1);
    }
    addWindowListener( new ClosingListener() );
    this_viewer = this;
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    current_view = view_code;
    add_controls = ctrl_option;
    data = iva;    
    buildMenubar();
  }
 
 /**
  * This method sets the ObjectState of this viewer to a previously saved
  * state.
  *
  *  @param  new_state The previously saved state that this viewer will be
  *                    set to.
  */ 
  abstract public void setObjectState( ObjectState new_state );
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */
  abstract public ObjectState getObjectState( boolean isDefault );
  
 /**
  * This method updates the view component when changes are made to the
  * existing array.
  */ 
  public void dataChanged()
  {
    ivc.dataChanged();
  }
  
 /**
  * Method to add a listener to this component.
  *
  *  @param  act_listener
  */
  public void addActionListener( ActionListener act_listener )
  {	     
    for ( int i = 0; i < Listeners.size(); i++ )    // don't add it if it's
      if ( Listeners.elementAt(i).equals( act_listener ) ) // already there
        return;

    Listeners.add( act_listener ); //Otherwise add act_listener
  }
  
 /**
  * Add the menu items from the ViewComponent.
  */   
  protected void addComponentMenuItems()
  {
    // get menu items from view component and place it in a menu
    ViewMenuItem[] menus = ivc.getMenuItems();
    if( menus == null || menus.length == 0 )
      return;
    for( int i = 0; i < menus.length; i++ )
    {
      if( ViewMenuItem.PUT_IN_FILE.equalsIgnoreCase(
          menus[i].getPath()) )
      {
        menu_bar.getMenu(0).add( menus[i].getItem() ); 
      }
      else if( ViewMenuItem.PUT_IN_OPTIONS.equalsIgnoreCase(
               menus[i].getPath()) )
      {
        menu_bar.getMenu(1).add( menus[i].getItem() );  	 
      }
      else if( ViewMenuItem.PUT_IN_HELP.equalsIgnoreCase(
               menus[i].getPath()) )
      {
        menu_bar.getMenu(2).add( menus[i].getItem() );
      }
    }
  }
  
 /**
  * Remove any menu items from the ViewComponent.
  */   
  protected void removeComponentMenuItems()
  {
    // get menu items from view component and place it in a menu
    ViewMenuItem[] menus = ivc.getMenuItems();
    if( menus == null || menus.length == 0 )
      return;
    for( int i = 0; i < menus.length; i++ )
    {
      if( ViewMenuItem.PUT_IN_FILE.equalsIgnoreCase(
          menus[i].getPath()) )
      {
        menu_bar.getMenu(0).remove( menus[i].getItem() ); 
      }
      else if( ViewMenuItem.PUT_IN_OPTIONS.equalsIgnoreCase(
               menus[i].getPath()) )
      {
        menu_bar.getMenu(1).remove( menus[i].getItem() );  	 
      }
      else if( ViewMenuItem.PUT_IN_HELP.equalsIgnoreCase(
               menus[i].getPath()) )
      {
        menu_bar.getMenu(2).remove( menus[i].getItem() );
      }
    }
  }
 
 /**
  * This method will load the user preferences that were saved in the specified
  * filename. This method assumes the file is in the user's home directory.
  *
  *  @param  filename The properties filename with path, 
                      ex: /home/user1/DisplayProps.isv
  */ 
  protected void loadProps( String filename )
  {
    // if file exists, load it into the ObjectState automatically.
    // This code will load user settings.
    ObjectState temp = getObjectState(IPreserveState.DEFAULT);
    temp.silentFileChooser(filename,false);
    setObjectState(temp);
  }
 
 /*
  * This private method will (re)build the menubar. This is necessary since
  * the ImageViewComponent could add menu items to the Menubar.
  * If the file being loaded is not found, those menu items
  * must be removed. To do so, rebuild the Menubar.
  */ 
  private void buildMenubar()
  { 
    Vector file              = new Vector();
    Vector save_menu 	     = new Vector();
    Vector open_menu 	     = new Vector();
    Vector exit              = new Vector();
    Vector file_listeners    = new Vector();
    
    // build file menu
    file.add("File");
    file_listeners.add( new MenuListener() ); // listener for file
    file.add(open_menu);
      open_menu.add("Open Project");
      file_listeners.add( new MenuListener() ); // listener for load project
    file.add(save_menu);
      save_menu.add("Save Project");
      file_listeners.add( new MenuListener() ); // listener for save project
    file.add(exit);
      exit.add("Exit");
      file_listeners.add( new MenuListener() ); // listener for exiting Display
           
    // add menus to the menu bar.
    setJMenuBar(null);
    menu_bar = new JMenuBar();
    menu_bar.add( MenuItemMaker.makeMenuItem(file,file_listeners) ); 
    
    setJMenuBar(menu_bar);
    // Add keyboard shortcuts
    KeyStroke binding = 
                  KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.ALT_MASK);
    JMenu file_menu = menu_bar.getMenu(0);
    file_menu.getItem(0).setAccelerator(binding);   // Open Project
    binding = KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.ALT_MASK);
    file_menu.getItem(1).setAccelerator(binding);   // Save Project
    binding = KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK);
    file_menu.getItem(2).setAccelerator(binding);   // Exit
  }
  
 /**
  * Put the controls for view component in a Box and return them as one entity.
  *
  *  @return Box containing all of the controls.
  */ 
  protected Box buildControlPanel()
  {
    // add viewcomponent controls
    Box ivc_controls = new Box(BoxLayout.Y_AXIS);
    TitledBorder ivc_border = 
    		     new TitledBorder(LineBorder.createBlackLineBorder(),
        			      "View Controls");
    ivc_border.setTitleFont( FontUtil.BORDER_FONT ); 
    ivc_controls.setBorder( ivc_border );
    ViewControl[] ivc_ctrl = ivc.getControls();
    // if no controls, return null.
    if( ivc_ctrl == null || ivc_ctrl.length == 0 )
      return null;
    for( int i = 0; i < ivc_ctrl.length; i++ )
    {
      ivc_controls.add(ivc_ctrl[i]);
    }
    // if resized, adjust container size for the pan view control.
    // THIS WAS COMMENTED OUT BECAUSE IT SLOWED THE VIEWER DOWN.
    //ivc_controls.addComponentListener( new ResizedControlListener() );
    
    // add spacer between ivc controls
    JPanel spacer = new JPanel();
    spacer.setPreferredSize( new Dimension(0, 10000) );
    ivc_controls.add(spacer);
    return ivc_controls;
  }
  
 /**
  * Tells all listeners about a new action.
  *
  *  @param  message
  */  
  protected void sendMessage( String message )
  {
    for ( int i = 0; i < Listeners.size(); i++ )
    {
      ActionListener listener = (ActionListener)Listeners.elementAt(i);
      listener.actionPerformed( new ActionEvent( this, 0, message ) );
    }
  }
  
 /*
  * This class is required to handle all messages within the Display.
  */
  private class MenuListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      if( ae.getActionCommand().equals("Save Project") )
      {
	getObjectState(IPreserveState.PROJECT).openFileChooser(true);
      }
      else if( ae.getActionCommand().equals("Open Project") )
      {
        ObjectState state = new ObjectState();
	if( state.openFileChooser(false) )
	  setObjectState(state);
      }
      else if( ae.getActionCommand().equals("Exit") )
      {
	this_viewer.dispose();
	System.gc();
	System.exit(0);
      }
    }
  }
  
 /*
  * This class will make sure all other windows are closed when the
  * display pane is closed.
  */
  private class ClosingListener extends WindowAdapter
  {
    public void windowClosing( WindowEvent we )
    {
      ivc.kill();
    }
  }
}
