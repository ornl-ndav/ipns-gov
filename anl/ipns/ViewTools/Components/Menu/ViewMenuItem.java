/*
 * File: ViewMenuItem.java
 *
 * Copyright (C) 2003, Mike Miller
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
 *  Revision 1.8  2005/05/25 20:28:28  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.7  2004/07/29 13:32:59  rmikk
 *  Fixed an off by one error
 *  All paths are now valid
 *
 *  Revision 1.6  2004/03/11 23:45:40  rmikk
 *  Fixed Package names
 *
 *  Revision 1.5  2004/03/11 15:03:30  millermi
 *  - Replaced equals() and toLowerCase() with equalsIgnoreCase() in
 *    validPath() and main().
 *
 *  Revision 1.4  2003/10/16 15:57:17  millermi
 *  - Added javadocs to public static final variables.
 *
 *  Revision 1.3  2003/10/16 05:00:05  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.2  2003/08/14 17:13:25  millermi
 *  - Added comments and public variable PUT_IN_HELP for help menu.
 *
 *  Revision 1.1  2003/05/22 13:02:50  dennis
 *  Menu items returned by ViewComponents to be added to the
 *  Menu bar. (Mike Miller)
 *
 */
  
 package gov.anl.ipns.ViewTools.Components.Menu;
 
 import javax.swing.*;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.Vector;
 
 import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * ViewMenuItem wraps a JMenuItem with its "path". The path specifies where
 * on the menu bar the JMenuItem should be placed. 
 */ 
public class ViewMenuItem
{
 /**
  * "File" - constant String to specify the placement into the File menu.
  */
  public static final String PUT_IN_FILE    = "File";
  
 /**
  * "Edit" - constant String to specify the placement into the Edit menu.
  */
  public static final String PUT_IN_EDIT    = "Edit";
  
 /**
  * "View" - constant String to specify the placement into the View menu.
  */
  public static final String PUT_IN_VIEW    = "View";
  
 /**
  * "Options" - constant String to specify the placement into the Options menu.
  */
  public static final String PUT_IN_OPTIONS = "Options";
  
 /**
  * "Help" - constant String to specify the placement into the Help menu.
  */
  public static final String PUT_IN_HELP    = "Help";
  
  private String location;    
  private JMenuItem item;
  private Vector listeners; 
  private static boolean isTest = false; // for testing only
  
 /**
  * Constructor takes in a menu item and initializes path to an empty string.
  * This can be used when a view component has a menu item, but doesn't care
  * where it is placed. 
  *
  *  @param  mitem - JMenuItem
  */ 
  public ViewMenuItem( JMenuItem mitem )
  {
    location = "";
    item = mitem;
    item.addActionListener(new MenuListener());
    listeners = new Vector();    
  }

 /**
  * Constructor can be used when a 
  * view component wants to recommend the placement of a menu item. 
  *
  *  @param  path
  *  @param  mitem - JMenuItem
  */ 
  public ViewMenuItem(String path, JMenuItem mitem)
  {
    this(mitem);
    setPath(path);  
  }

 /**
  * Set the "path" of the JMenuItem after verifying initial menu exists.
  *
  * Note: The format for path is <menu>.<submenu>
  *	  Both are optional, but a submenu cannot exist without a menu. 
  *	  The actual item name will be added to the path implicitly,
  *	  so it shouldn't be included in the path.
  *
  *  @param  path
  */   
  public void setPath( String path )
  {
    if( validPath(path) )
      location = path;
    else
      System.out.println("Initial path not recognized by " +
        		 "ViewMenuItem.java. Path must be of " +
        		 "form <menu>.<submenu>  Example: File.Save");
  }
 
 /**
  * Full list of menu/submenus under which this item will be placed.
  *
  * Note: The format for path is <menu>.<submenu>.<item> 
  *	  If <menu> does not exist, path is an empty string
  *
  *  @return path 
  */ 
  public String getPath()
  {
    return location;
  } 
 
 /**
  * Gives access to the menu item being added to the menu bar.
  *
  *  @return item
  */  
  public JMenuItem getItem()
  {
    return item;
  } 
   
 /**
  * Method to add a listener to this component.
  *
  *  @param  act_listener
  */
  public void addActionListener( ActionListener act_listener )
  {	     
    for ( int i = 0; i < listeners.size(); i++ )    // don't add it if it's
      if ( listeners.elementAt(i).equals( act_listener ) ) // already there
	return;

    listeners.add( act_listener ); //Otherwise add act_listener
  }    
 
 /**
  * Method to remove a listener from this component.
  *
  *  @param  act_listener
  */ 
  public void removeActionListener( ActionListener act_listener )
  {
    listeners.remove( act_listener );
  }
 
 /**
  * Method to remove all listeners from this component.
  */ 
  public void removeAllActionListeners()
  {
    listeners.removeAllElements();
  }
      
 /*
  * Tells all listeners about a new action.
  *
  *  @param  message
  */  
  private void sendMessage( String message )
  {
    for ( int i = 0; i < listeners.size(); i++ )
    {
      ActionListener listener = (ActionListener)listeners.elementAt(i);
      listener.actionPerformed( new ActionEvent( this, 0, message ) );
    }
  }
  
 /*
  * Makes sure the given path starts with one of the five menus.
  */ 
  private boolean validPath( String path )
  {
    int dot_index = path.indexOf(".");
    String menu = path;
    // get everything before first delimiter.
    if( dot_index > 0 )
      menu = path.substring(0,dot_index);
    
    if( PUT_IN_FILE.equalsIgnoreCase(menu))
      return true; 
    else if( PUT_IN_EDIT.equalsIgnoreCase(menu) )
      return true; 
    else if( PUT_IN_VIEW.equalsIgnoreCase(menu) )
      return true; 
    else if( PUT_IN_OPTIONS.equalsIgnoreCase(menu) )
      return true; 
    else if( PUT_IN_HELP.equalsIgnoreCase(menu) )
      return true; 
    return true;
  }
 
 /*
  * This class relays the message sent out by the JMenuItem and appends
  * the path to the message.
  */  
  private class MenuListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      sendMessage( location + "." + ae.getActionCommand() );
      if( isTest )
        System.out.println("Path = " + location +"."+ ae.getActionCommand() );
    }
  }
  
 /*
  * MAIN - Basic main program to test an ImageViewComponent object
  */
  public static void main( String args[] ) 
  {  
    isTest = true;
    ViewMenuItem[] item = new ViewMenuItem[4];
    item[0] = new ViewMenuItem( "File",new JMenuItem("Test1") );
    item[1] = new ViewMenuItem( "Edit",new JMenuItem("Test2") );
    item[2] = new ViewMenuItem( "Flie",new JMenuItem("Test3") ); // bad path
    item[3] = new ViewMenuItem( "File",new JMenuItem("Test4") );
    
    // construct test window
    JFrame window = new JFrame("Test Window");
    JMenuBar menu_bar = new JMenuBar();
    window.setBounds(0,0,200,60);
    window.setJMenuBar(menu_bar);	

    JMenu fileMenu    = new JMenu("File");	// Menus for the menu bar 
    JMenu editMenu    = new JMenu("Edit");
    JMenu viewMenu    = new JMenu("View");
    JMenu optionsMenu = new JMenu("Options");
  
    menu_bar.add(fileMenu); 
    menu_bar.add(editMenu); 
    menu_bar.add(viewMenu); 
    menu_bar.add(optionsMenu);
    
    for( int i = 0; i < 4; i++ )
    {
      if( PUT_IN_FILE.equalsIgnoreCase(item[i].getPath()) )
        fileMenu.add( item[i].getItem() ); 
      else if( PUT_IN_EDIT.equalsIgnoreCase(item[i].getPath()) )
          editMenu.add( item[i].getItem() );
      else if( PUT_IN_VIEW.equalsIgnoreCase(item[i].getPath()) )
          viewMenu.add( item[i].getItem() );
    //if(PUT_IN_OPTIONS.equalsIgnoreCase(item[i].getPath()))
      else
        optionsMenu.add( item[i].getItem() ); 	  
    }
    
    WindowShower.show(window);
  }
}
