/*
 * File: ComponentViewManager.java
 *
 * Copyright (C) 2005, Mike Miller
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
 *  Revision 1.5  2006/11/03 19:46:12  amoe
 *  -Added:  String DATA_SET
 *                  getRawLayoutManager()
 *  -Edited:  buildPanel()  // will send a message if layout manager
 *                                          is a Dataset type, and not DataSetVirtualArray
 *  (Dominic Kramer)
 *
 *  Revision 1.4  2005/07/25 20:51:03  kramer
 *
 *  Modified the imports so that the new ContourViewComponent (from the
 *  package gov.anl.ipns.ViewTools.Components.TwoD.Contour package) is used.
 *
 *  Revision 1.3  2005/07/12 16:50:47  kramer
 *
 *  Modified the main method to use test data that can be used to test the
 *  ContourViewComponent.
 *
 *  Revision 1.2  2005/05/25 20:28:45  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.1  2005/03/28 05:54:05  millermi
 *  - Initial Version - This is a building block in the new viewer
 *    structure.
 *
 */
package gov.anl.ipns.ViewTools.Layouts;

import gov.anl.ipns.Util.Sys.PrintComponentActionListener;
import gov.anl.ipns.Util.Sys.SaveImageActionListener;
import gov.anl.ipns.Util.Sys.SharedMessages;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.ViewTools.Components.IVirtualArray;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.TwoD.Contour.ContourViewComponent;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlManager;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.UI.ActionValueEvent;
import gov.anl.ipns.ViewTools.UI.ActionValueJFrame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import gov.anl.ipns.ViewTools.Components.ComponentView.DataSetSwapper;
import gov.anl.ipns.ViewTools.Components.ComponentView.DataSetVirtualArray;


/**
 * This class displays an IComponentLayoutManager and its menu items.
 * 
 */
 public class ComponentViewManager extends ActionValueJFrame 
                                   implements IPreserveState
 {
  /* ---------------------------ObjectState Keys--------------------------*/
  /**
   * "Visible Layout" - This String key refers to the layout displayed by this
   * view manager. This ObjectState key references a value of type String.
   */
   public static final String VISIBLE_LAYOUT = "Visible Layout";
  /**
   * "Display Controls" - This String key refers to whether or not the
   * shared controls window should be displayed. This ObjectState key
   * references a value of type Boolean.
   */
   public static final String DISPLAY_SHARED_CONTROLS = "Display Controls";
  /**
   * "Link Views" - This String key refers to whether or not multiple
   * view managers should be linked. If linked, all events from one view
   * will affect every other linked view. This ObjectState key references
   * a value of type Boolean.
   */
   public static final String LINK_VIEWS = "Link Views";
  /* -----------------------------Layout Types----------------------------*/
  /**
   * "Single" - String for specifying the layout type being displayed in the
   * ComponentViewManager. This variable will specify a SingleLayoutManager.
   */
   public static final String SINGLE = "Single";

   public static final String DATA_SET = "DataSet";

   // Add new layout types to this list.
   private final String[] layout_list = new String[]
   { SINGLE, DATA_SET };

   // These static variables are shared by all ComponentViewManager instances.
   private static int cvm_counter = 0; // Keep track of # of view managers.
   private static JFrame shared_ctrls_ui; // Window displaying shared controls.
   private static final String PREFS_FILE = System.getProperty("user.home") + 
    		                         System.getProperty("file.separator") +
				         "CVMPrefs.isv";
   private IVirtualArray data = null;
   private String current_layout = null;
   private AbstractLayoutManager alm = null;
   private Hashtable layout_state = new Hashtable();
   private Hashtable layout_prefs = new Hashtable();
   private ComponentViewManager this_manager;
   private ViewMenuItem[] current_layout_menus;
   private ViewControl[] current_shared_ctrls;
   private ControlManager ctrl_mgr;
   private boolean first_pass = true;
   
  /**
   * Default Constructor - Creates a view manager with no data to display.
   */
   public ComponentViewManager()
   {
     this(null);
   }
   
  /**
   * Constructor - Creates a view manager that displays the IVirtualArray
   * in a SingleLayoutManager.
   *
   *  @param  array Data to be displayed by the view manager.
   */
   public ComponentViewManager( IVirtualArray array )
   {
     init(array,SINGLE);
   }
   
  /**
   * Constructor - Creates a view manager that displays the IVirtualArray
   * using a previously stored state. This constructor is useful when
   * additional view managers are requested.
   *
   *  @param  array Data to be displayed by the view manager.
   *  @param  state State this ComponentViewManager will be initialized with.
   */
   public ComponentViewManager( IVirtualArray array, ObjectState state )
   {
     if( state == null )
       init(array,SINGLE);
     else
     {
       String new_layout = (String)state.get(VISIBLE_LAYOUT);
       if( !isValidLayout(new_layout) )
         new_layout = SINGLE;
       init(array,new_layout);
     }
     setObjectState(state);
   }
   
  /**
   * Constructor - Create a view manager that displayes the given
   * array with the given LayoutManager.
   *
   *  @param  array IVirtualArray to be displayed by the view manager.
   *  @param  layout String key corresponding to the desired LayoutManager.
   */
   public ComponentViewManager( IVirtualArray array, String layout )
   {
     init(array,layout);
   }
   
   private void init( IVirtualArray array, String layout )
   {
     this_manager = this;
     // Initialize JFrame.
     setTitle("ComponentViewManager");
     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     setBounds(10,10,600,400);
     setLayoutManager(layout);
     // This will listen to when view managers are opened and closed.
     addWindowListener( new WindowManager() );
     // Get single instance of ControlManager.
     ctrl_mgr = ControlManager.getInstance();
     if( shared_ctrls_ui == null )
     {
       shared_ctrls_ui = new JFrame("Shared Controls");
       /*
       // Force controls to be in 1 column.
       shared_ctrls_ui.getContentPane().setLayout( new GridLayout(1,1) );
       shared_ctrls_ui.getContentPane().add(new JLabel("No Shared Controls"));
       */
       shared_ctrls_ui.getContentPane().setLayout( new GridLayout(1,1) );
       shared_ctrls_ui.getContentPane().add(ctrl_mgr.getSharedControlsUI());
       
       shared_ctrls_ui.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
       shared_ctrls_ui.setBounds(610,10,200,200);
     }
     shared_ctrls_ui.addComponentListener( new SharedControlManager() );
     current_layout_menus = null;
     current_shared_ctrls = null;
     // Counter which keeps track of the number of ComponentViewManagers.
     cvm_counter++; // Increment counter when a new view manager is created.
     // Add menubar.
     buildMenuBar();
     
     // Load any saved user preferences if the first instance of view manager.
     if( cvm_counter == 1 )
       loadPreferences();
       
     setData(array);
   }
   
  /**
   * Get the state of this view manager.
   *
   *  @param  is_default Specify either PROJECT of DEFAULT ObjectState as
   *                     defined by IPreserveState.
   *  @return Current ObjectState of the view manager.
   */
   public ObjectState getObjectState(boolean is_default)
   {
     ObjectState state = new ObjectState();
     state.insert(VISIBLE_LAYOUT,getLayoutManager());
     state.insert(DISPLAY_SHARED_CONTROLS, new Boolean(
                  getJMenuBar().getMenu(3).getItem(1).isSelected() ) );
     state.insert(LINK_VIEWS, new Boolean(
                  getJMenuBar().getMenu(3).getItem(0).isSelected() ) );
     
     // If project save, get entire state for layouts in layout_state.
     if( !is_default )
     {
       // Update the currently saved ObjectState of the current layout.
       if( alm != null )
         layout_state.put(getLayoutManager(),alm.getObjectState(PROJECT));
       
       // Go through all existing LayoutManagers and get their state.
       // These states will be "invisible" to outside users.
       Enumeration keys = layout_state.keys();
       ObjectState temp;
       while( keys.hasMoreElements() )
       {
         String layout_title = (String)keys.nextElement();
         temp = (ObjectState)layout_state.get(layout_title);
         if( temp != null )
           state.insert(layout_title,temp);
       }
     }
     // Else, if default save, get preferences for layouts in layout_prefs.
     else
     {
       // Update the currently saved ObjectState of the current layout.
       if( alm != null )
       {
         StringBuffer key = new StringBuffer(getLayoutManager());
	 key.append("Prefs");
         layout_prefs.put(key.toString(),alm.getObjectState(DEFAULT));
       }
       // Go through all existing LayoutManagers and get their state.
       // These states will be "invisible" to outside users.
       Enumeration keys = layout_prefs.keys();
       ObjectState temp;
       while( keys.hasMoreElements() )
       {
         String layout_title = (String)keys.nextElement();
         temp = (ObjectState)layout_prefs.get(layout_title);
         if( temp != null )
           state.insert(layout_title,temp);
       }
     }
     
     return state;
   }
   
  /**
   * Set/Restore the state of the view manager.
   *
   *  @param  state The new state of the view manager.
   */
   public void setObjectState( ObjectState state )
   {
     // Do nothing if state is null.
     if( state == null )
       return;
     Object value = state.get(VISIBLE_LAYOUT);
     if( value != null )
     {
       setLayoutManager((String)value);
     }
     
     value = state.get(DISPLAY_SHARED_CONTROLS);
     if( value != null )
     {
       getJMenuBar().getMenu(3).getItem(1).setSelected(
                                              ((Boolean)value).booleanValue());
     }
     
     value = state.get(LINK_VIEWS);
     if( value != null )
     {
       getJMenuBar().getMenu(3).getItem(0).setSelected(
                                              ((Boolean)value).booleanValue());
     }
     // Get preserved state for all layout managers that have state preserved.
     Object lay_state;
     for( int i = 0; i < layout_list.length; i++ )
     {
       // Get the state information for each layout.
       lay_state = state.get(layout_list[i]);
       if( lay_state != null )
       {
         // Add layout state to the Hashtable layout_state. Use the
	 // layout manager title as the key.
         layout_state.put(layout_list[i],lay_state);
       }
     }
     
     // Get preserved preferences for all layout managers that have been
     // preserved.
     StringBuffer key;
     for( int i = 0; i < layout_list.length; i++ )
     {
       key = new StringBuffer(layout_list[i]).append("Prefs");
       // Get the state information for each layout.
       lay_state = state.get(key.toString());
       if( lay_state != null )
       {
         // Add layout state to the Hashtable layout_state. Use the
	 // layout manager title as the key.
         layout_prefs.put(key.toString(),lay_state);
       }
     }
     // If current layout is not null and there is state for it, restore the
     // state.
     if( alm != null )
     {
     
       // Try to get ObjectState of current layout.
       ObjectState current_state = (ObjectState)
                                       layout_state.get(getLayoutManager());
       if( current_state != null )
         alm.setObjectState( current_state );
       else
       {
         key = new StringBuffer(getLayoutManager()).append("Prefs");
         current_state = (ObjectState)layout_prefs.get(key.toString());
       }
     }
     validate();
     repaint();
   }
   
  /**
   * Set/Change the data displayed by the ViewManager.
   *
   *  @param  iva Data to be displayed.
   */
   public void setData( IVirtualArray iva )
   {
     // If data already references iva, do nothing.
     if( data == iva )
       return;
     data = iva;
     // If no layout manager, build one.
     if( alm == null )
       buildPanel();
     // Otherwise update the display.
     else
       alm.setData(iva);
   }
   
  /**
   * Get the data being displayed by the ComponentViewManager.
   *
   *  @return Data being displayed by the ComponentViewManager.
   */
   public IVirtualArray getData()
   {
     return data;
   }
   
  /**
   * Set the layout manager that will display the data. Layouts are highly
   * specific display tools that arrange one or more view components and
   * provide visual manipulation tools.
   *
   *  @param  layout The String corresponding to the desired layout.
   */
   public void setLayoutManager( String layout )
   {
     // If layout is invalid, do nothing.
     if( !isValidLayout(layout) )
       return;
     // If the current layout is already set to the specified layout, ignore.
     if( current_layout != null && current_layout.equals(layout) )
       return;
     // Remove any menu items specific to the LayoutManager.
     removeLayoutMenuItems();
     // Remove any controls added by this layout manager.
     removeSharedControls();
     current_layout = layout;
     // Adding menu items specific to the new LayoutManager is done in
     // buildPanel().
     buildPanel();
   }
   
  /**
   * Get the layout manager currently being displayed by the
   * ComponentViewManager.
   *
   *  @return String corresponding to the current layout.
   */
   public String getLayoutManager()
   {
     return current_layout;
   }
   
   public AbstractLayoutManager getRawLayoutManager()
   {
      return this.alm;
   }
   
  /**
   * Override method from ActionValueJFrame. Perform additional operations
   * based on the event, then pass it on to listeners.
   *
   *  @param  ave The ActionValueEvent generated by a class being listened
   *              to by the ComponentViewManager.
   */
   public void valueChanged( ActionValueEvent ave )
   {
     String action = ave.getActionCommand();
     // This ActionValueEvent will act like an ActionEvent. This message
     // is sent when a ComponentSwapper changes its displayed ViewComponent.
     if( action.equals(AbstractComponentSwapper.VIEW_TYPE_CHANGED) )
     {
       // Remove elements stored in current_layout_menus.
       removeLayoutMenuItems();
       // Remove any shared controls added to the shared controls panel.
       removeSharedControls();
       // Get new layout menu items and add them to the menu bar.
       addLayoutMenuItems();
       // Add any new shared controls to the ControlManager and GUI.
       addSharedControls();
     }
     // If the "Link Views" menu item is unchecked, do not pass messages on.
     if( !getJMenuBar().getMenu(3).getItem(0).isSelected() )
       return;
     // Pass the event on to all listeners.
     super.valueChanged(ave);
   }
 
  /*
   * This method will load the user preferences that were saved in the specified
   * filename. This method assumes the file is in the user's home directory.
   *
   *  @param  filename The properties filename with path, 
   *                   ex: /home/user1/CVMPrefs.isv
   */ 
   private void loadPreferences()
   {
     // If file exists, load it into the ObjectState automatically.
     ObjectState temp = getObjectState(IPreserveState.DEFAULT);
     if( temp.silentFileChooser(PREFS_FILE,false) )
     {
       setObjectState(temp);
       SharedMessages.addmsg("Loading preferences from "+PREFS_FILE);
     }
   }
   
  /*
   * Create the GUI interface and put it into the content pane.
   */
   private void buildPanel()
   {
      // Remove everything from view.
      getContentPane().removeAll();
      if (alm != null)
      {
         // If switching views, save the state of the current layout manager.
         layout_state.put(getLayoutManager(), alm.getObjectState(PROJECT));
         // If switching views, save the preferences for current layout manager.
         StringBuffer key = new StringBuffer(getLayoutManager())
               .append("Prefs");
         layout_prefs.put(key.toString(), alm.getObjectState(DEFAULT));
         // Remove any existing menu items.
         removeLayoutMenuItems();
         // Remove any registered controls.
         removeSharedControls();
         // Since regenerating the view, remove listeners.
         removeActionValueListener(alm);
         // Since regenerating the view, remove listeners.
         alm.removeActionValueListener(this_manager);
      }
      String err_string = null;
      // If data is not valid,
      if (data == null)
      {
         err_string = "No data to display.";
      }
      else if (!isValidLayout(getLayoutManager()))
      {
         err_string = "ERROR in ComponentViewManager.buildPanel() - Layout"
               + "Manager [" + getLayoutManager() + "] unsupported.";
      }
      // If an error is found, clear the display and show the error.
      if (err_string != null)
      {
         JPanel null_display = new JPanel();
         null_display.add(new JLabel(err_string));
         // Add error label to display.
         getContentPane().add(null_display);
         validate();
         repaint();
         return;
      }

      // If state for layout exists, restore state.
      ObjectState temp_state = (ObjectState) layout_state
            .get(getLayoutManager());
      if (temp_state == null)
      {
         StringBuffer key = new StringBuffer(getLayoutManager())
               .append("Prefs");
         temp_state = (ObjectState) layout_prefs.get(key.toString());
      }

      // If SingleLayoutManager desired, create new instance and add it
      // to the layouts table.
      if (getLayoutManager().equals(SINGLE))
      {
         if (temp_state != null)
            alm = new SingleLayoutManager(data, (ObjectState) temp_state);
         else
            alm = new SingleLayoutManager(data);
      }
      else if (getLayoutManager().equals(DATA_SET))
      {
         if ( !(data instanceof DataSetVirtualArray) )
         {
            SharedMessages.addmsg("Layout ["+DATA_SET+"] is only supported " +
                                  "by ComponentViewManager.buildPanel() " +
                                  "if the ComponentViewManager was given a " +
                                  DataSetVirtualArray.class.getName());
            return;
         }
         
         alm = new SingleLayoutManager(
                      new DataSetSwapper( (DataSetVirtualArray)data ));
      }
      else
      {
         // If this message appears, a new layout manager was probably created,
         // but never added to the "if" statements above. To resolve this,
         // add an a line similar to the existing layout managers.
         SharedMessages.addmsg("Layout [" + getLayoutManager()
               + "] unsupported " + "by ComponentViewManager.buildPanel(). "
               + "Please add definition of this layout.");
         // Rebuild display after removing all components.
         validate();
         repaint();
         return;
      }
      // Allow AbstractLayoutManager to listen to ActionValueEvents
      // generated by the view manager.
      addActionValueListener(alm);
      // Allow view manager to listen to ActionValueEvents generated
      // by the LayoutManager.
      alm.addActionValueListener(this_manager);
      // Display the newly created layout manager.
      getContentPane().add(alm);
      // Add any menu items from the layout.
      addLayoutMenuItems();
      // Add any new shared controls to the ControlManager and GUI.
      addSharedControls();
      /*
       * // Load any saved user preferences if the first instance of view
       * manager. if( first_pass && cvm_counter == 1 ) { first_pass = false;
       * loadPreferences(); }
       */
      validate();
      repaint();
   }
   
  /*
   * Build the menubar for the ViewManager.
   */
   private void buildMenuBar()
   {
     // Build menu bar.
     JMenuBar menu_bar = new JMenuBar();
     menu_bar.add(new JMenu("File"));
     menu_bar.add(new JMenu("Edit"));
     menu_bar.add(new JMenu("View"));
     menu_bar.add(new JMenu("Options"));
     menu_bar.add(new JMenu("Help"));
     
     // Keyboard shortcut mask.
     KeyStroke binding;
     // Build file menu.
     JMenu file = menu_bar.getMenu(0);
     JMenuItem print_image = PrintComponentActionListener.getActiveMenuItem(
	                                "Print Display", getContentPane() );
     binding = KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.ALT_MASK);
     // Add keyboard shortcut.
     print_image.setAccelerator(binding);
     JMenuItem save_image = SaveImageActionListener.getActiveMenuItem(
	                             "Save display as JPEG", getContentPane() );
     binding = KeyStroke.getKeyStroke(KeyEvent.VK_J,InputEvent.ALT_MASK);
     // Add keyboard shortcut.
     save_image.setAccelerator(binding);
     JMenuItem close = new JMenuItem("Close");
     close.addActionListener(new MenuListener());
     file.add(print_image);
     file.add(save_image);
     file.add(close);
     
     // Build edit menu.
     JMenu edit = menu_bar.getMenu(1);
     
     // Build view menu.
     JMenu view = menu_bar.getMenu(2);
     JMenuItem new_view = new JMenuItem("Additional View");
     new_view.addActionListener( new MenuListener() );
     view.add(new_view);
     JMenuItem layout;
     // Add all layouts to the list.
     for( int i = 0; i < layout_list.length; i++ )
     {
       layout = new JMenuItem(layout_list[i]);
       layout.addActionListener( new MenuListener() );
       view.add(layout);
     }
     
     // Build options menu.
     // *****************Read before adding to Options Menu***************
     // *** If any menu items are added to the Options menu before the ***
     // *** Link Views menu item, be sure to update the code in        ***
     // *** valueChanged() since the link menu item is referenced by   ***
     // *** using an index.                                            ***
     // ******************************************************************
     JMenu options = menu_bar.getMenu(3);
     JCheckBoxMenuItem link = new JCheckBoxMenuItem("Link Views");
     link.setSelected(true); // Link views by default.
     options.add(link);
     JCheckBoxMenuItem shared_ctrls = 
                                new JCheckBoxMenuItem("Show Shared Controls");
     shared_ctrls.addActionListener( new MenuListener() );
     // If first view manager to be built, default shared controls to be true.
     if( cvm_counter == 1 )
       shared_ctrls.doClick(); // Show controls by default.
     // Otherwise match menu to whatever the controls window is.
     else
     {
       shared_ctrls.setSelected(shared_ctrls_ui.isVisible());
     }
     options.add(shared_ctrls);
     JMenuItem prefs = new JMenuItem("Save User Preferences");
     prefs.addActionListener(new MenuListener());
     options.add(prefs);
     
     // Build help menu.
     JMenu help = menu_bar.getMenu(4);
     
     // Add menu items from initial layout.
     addLayoutMenuItems();
     // Add menu bar to frame.
     setJMenuBar(menu_bar);
   }
  
  /*
   * Add the menu items for the current LayoutManager.
   */	
   private void addLayoutMenuItems()
   {
     // If current layout manager is null, do nothing.
     if( getLayoutManager() == null )
       return;
     // If layout manager has not been created, do nothing.
     if( alm == null )
       return;
     // Get the list of menu items from the layout.
     current_layout_menus = alm.getLayoutMenuItems();
     // If no menus exist to display, do nothing.
     if( current_layout_menus == null || current_layout_menus.length == 0 )
       return;
     // Get menubar for the view manager.
     JMenuBar menu_bar = getJMenuBar();
     // Make sure menu bar exists.
     if( menu_bar == null )
       return;
     // Go through list of menu items, putting them in the correct menu.
     for( int i = 0; i < current_layout_menus.length; i++ )
     {
       if( ViewMenuItem.PUT_IN_FILE.equalsIgnoreCase(
   	   current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(0).add( current_layout_menus[i].getItem() ); 
       }
       else if( ViewMenuItem.PUT_IN_EDIT.equalsIgnoreCase(
   	   current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(1).add( current_layout_menus[i].getItem() ); 
       }
       else if( ViewMenuItem.PUT_IN_VIEW.equalsIgnoreCase(
   	   current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(2).add( current_layout_menus[i].getItem() ); 
       }
       else if( ViewMenuItem.PUT_IN_OPTIONS.equalsIgnoreCase(
   		current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(3).add( current_layout_menus[i].getItem() ); 	  
       }
       else if( ViewMenuItem.PUT_IN_HELP.equalsIgnoreCase(
   		current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(4).add( current_layout_menus[i].getItem() );
       }
     }
   }
   
  /*
   * Remove any menu items from the current LayoutManager.
   */	
   private void removeLayoutMenuItems()
   {
     // If no menus exist to display, do nothing.
     if( current_layout_menus == null || current_layout_menus.length == 0 )
       return;
     // Get menubar for the view manager.
     JMenuBar menu_bar = getJMenuBar();
     // Make sure menu bar exists.
     if( menu_bar == null )
       return;
     // Go through list of menu items, removing them from each menu.
     for( int i = 0; i < current_layout_menus.length; i++ )
     {
       if( ViewMenuItem.PUT_IN_FILE.equalsIgnoreCase(
   	   current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(0).remove( current_layout_menus[i].getItem() );
       }
       else if( ViewMenuItem.PUT_IN_EDIT.equalsIgnoreCase(
   		current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(1).remove( current_layout_menus[i].getItem() );
       }
       else if( ViewMenuItem.PUT_IN_VIEW.equalsIgnoreCase(
   		current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(2).remove( current_layout_menus[i].getItem() );
       }
       else if( ViewMenuItem.PUT_IN_OPTIONS.equalsIgnoreCase(
   		current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(3).remove( current_layout_menus[i].getItem() );
       }
       else if( ViewMenuItem.PUT_IN_HELP.equalsIgnoreCase(
   		current_layout_menus[i].getPath()) )
       {
   	 menu_bar.getMenu(4).remove( current_layout_menus[i].getItem() );
       }
     }
   }
   
  /*
   * Add any shared controls to the ControlManager. If a new category is
   * created, add the first control the the shared controls GUI.
   */
   private void addSharedControls()
   {
     // Make sure layout manager is not null.
     if( alm == null )
       return;
     current_shared_ctrls = alm.getSharedControls();
     // If no controls to be shared, do nothing.
     if( current_shared_ctrls == null || current_shared_ctrls.length <= 0 )
       return;
     // Go through list of shared controls and register them with the
     // ControlManager.
     for( int i = 0; i < current_shared_ctrls.length; i++ )
     {
       ctrl_mgr.registerControl(current_shared_ctrls[i]);
     }
     // Adjust the size of the window to the size of the controls.
     shared_ctrls_ui.pack();
   }
   
  /*
   * Remove any shared controls added to the ControlManager by this
   * view manager for the current layout.
   */
   private void removeSharedControls()
   {
     // If no shared controls have been set, do nothing.
     if( current_shared_ctrls == null )
       return;
     // Remove all shared controls associated with the current layout. 
     ViewControl[] shared_ctrls = alm.getSharedControls();
     if( current_shared_ctrls != null && current_shared_ctrls.length > 0 )
     {
       // Go through list of shared controls and unregister them from the
       // ControlManager.
       for( int i = 0; i < current_shared_ctrls.length; i++ )
       {
     	 ctrl_mgr.unregisterControl(current_shared_ctrls[i]);
       }
     }
     shared_ctrls_ui.pack();
   }
  
  /*
   * Determine if layout is valid. Search through list of layouts.
   */
   private boolean isValidLayout( String layout )
   {
     // If layout is null, return invalid layout.
     if( layout == null )
       return false;
     // Go through list of layouts.
     for( int i = 0; i < layout_list.length; i++ )
     {
       // If layout matches, return valid layout.
       if( layout.equals(layout_list[i]) )
         return true;
     }
     // If layout not found, return invalid layout.
     return false;
   }
   
  /*
   * This class will listen to events generated when a menu item is pressed.
   */
   private class MenuListener implements ActionListener
   {
     public void actionPerformed( ActionEvent ae )
     {
         String action = ae.getActionCommand();
         // If layout from View menu...
         if( isValidLayout(action) ) 
         {
            // If the current layout manager is not the specified manager,
            // change the layout.
            if (!getLayoutManager().equals(action))
            {
               setLayoutManager(action);
            }
         }
         else if (action.equals("Additional View"))
         {
            ComponentViewManager cvm = new ComponentViewManager(getData(),
                  this_manager.getObjectState(PROJECT));
            // Let current view manager listen to new view manager.
            cvm.addActionValueListener(this_manager);
            // Let new view manager listen to current view manager.
            addActionValueListener(cvm);

            WindowShower.show(cvm);
            // Consider adding window listener, which will remove listener when
            // window is closing.
         }
         else if (action.equals("Show Shared Controls"))
         {
            JCheckBoxMenuItem show = (JCheckBoxMenuItem) ae.getSource();
            // If menu item is checked and shared controls exist, show them.
            if (show.isSelected()
                  && shared_ctrls_ui.getContentPane().getComponentCount() > 0)
            {
               shared_ctrls_ui.setVisible(true);
            }
            else
               shared_ctrls_ui.setVisible(false);
         }
         else if (action.equals("Save User Preferences"))
         {
            getObjectState(IPreserveState.DEFAULT).silentFileChooser(
                  PREFS_FILE, true);
            SharedMessages.addmsg("Saving preferences to " + PREFS_FILE);
         }
         // If Close, dispose of view manager.
         else if (action.equals("Close"))
         {
            // Make sure to notify listeners that this window is closing before
            // disposing of the ComponentViewManager.
            WindowEvent we = new WindowEvent(this_manager,
                  WindowEvent.WINDOW_CLOSING);
            WindowListener[] closing_listeners = this_manager
                  .getWindowListeners();
            for (int i = 0; i < closing_listeners.length; i++)
               closing_listeners[i].windowClosing(we);
            // Release all screen resources used by the ViewManager.
            dispose();
            // Run garbage collector.
            System.gc();
         }
      }
   }
   
  /*
   * This class will keep track of ComponentViewManagers. If one closes,
   * decrement the cvm_counter.
   */
   private class WindowManager extends WindowAdapter
   {
     public void windowClosing( WindowEvent we )
     {
       // Unregister and remove shared controls.
       removeSharedControls();
       cvm_counter--; // Decrement counter since view is closing.
       // If no more view managers, dispose of shared_ctrls_ui window.
       if( cvm_counter <= 0 )
         shared_ctrls_ui.dispose();
       // Call garbage collector to free up system resources.
       System.gc();
     }
   }
   
  /*
   * This class will keep track of the window containing the shared controls.
   * Since all the view managers have access to this window, listen for it to
   * become visible or hidden. Change the state of the menu item accordingly.
   */
   private class SharedControlManager extends ComponentAdapter
   {
     public void componentShown( ComponentEvent ce )
     {
       if( getJMenuBar() == null )
         return;
       // Check the "Show Shared Controls" menu item
       // when the shared_ctrls_ui window becomes visible.
       getJMenuBar().getMenu(3).getItem(1).setSelected(true);
     }
     
     public void componentHidden( ComponentEvent ce )
     {
       if( getJMenuBar() == null )
         return;
       // Uncheck the "Show Shared Controls" menu item
       // when the shared_ctrls_ui window becomes hidden.
       getJMenuBar().getMenu(3).getItem(1).setSelected(false);
     }
   }
   
  /**
   * For testing purposes only...
   *
   *  @param  args Input parameters are ignored.
   */
   public static void main( String args[] )
   {
     ComponentViewManager cvm = new ComponentViewManager();
     WindowShower.show( cvm );

     // build my 2-D data
     int row = 400;
     int col = 500;
     float test_array[][] = new float[row][col];
     for ( int i = 0; i < row; i++ )
       for ( int j = 0; j < col; j++ )
       {
         if( i%50 == 0 )
	   test_array[i][j] = 1000f;
     	 else if( j%50 == 0 )
	   test_array[i][j] = 1000f;
	 else
	   test_array[i][j] = i - j;
       }
     
     // Put 2-D data into a VirtualArray2D wrapper
     //IVirtualArray2D va2D = new VirtualArray2D( test_array );
     IVirtualArray2D va2D = ContourViewComponent.getTestData(51, 51, 3.0, 4.0);
     // Give meaningful range, labels, units, and linear or log display method.
     va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f, 
     			 "TestX","TestUnits", AxisInfo.LINEAR );
     va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f, 
     			 "TestY","TestYUnits", AxisInfo.LINEAR );
     va2D.setAxisInfo( AxisInfo.Z_AXIS, 0f, 1f, "Z", "Units",
     		       AxisInfo.PSEUDO_LOG );
     va2D.setTitle("CompViewManager Test");
     cvm.setData(va2D);
   }
 } // End of ComponentViewManager
