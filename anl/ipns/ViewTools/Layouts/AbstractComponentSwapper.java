/*
 * File: AbstractComponentSwapper.java
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
 *  Revision 1.1  2005/03/28 05:54:03  millermi
 *  - Initial Version - This is a building block in the new viewer
 *    structure.
 *
 */
 package gov.anl.ipns.ViewTools.Layouts;
 
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Vector;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JMenu;
 import javax.swing.JMenuBar;
 import javax.swing.JMenuItem;
 import javax.swing.JPanel;
 
 import gov.anl.ipns.Util.Sys.SharedMessages;
 import gov.anl.ipns.Util.Sys.WindowShower;
 import gov.anl.ipns.ViewTools.Components.IViewComponent;
 import gov.anl.ipns.ViewTools.Components.IVirtualArray;
 import gov.anl.ipns.ViewTools.Components.ObjectState;
 import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
 import gov.anl.ipns.ViewTools.Components.IViewComponent;
 import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
 import gov.anl.ipns.ViewTools.UI.ActionValueJPanel;
 import gov.anl.ipns.ViewTools.UI.ActionValueEvent;
 import gov.anl.ipns.ViewTools.UI.SplitPaneWithState;
 
/**
 * This abstract class implements all methods defined by IComponentSwapper
 * and handles all swapping for extending classes. Extending classes
 * must define constructors that take in instances of IVirtualArray and
 * must also implement setData(), linkComponents(), and buildComponent().
 * These three methods are specific the the component swapper and the
 * data accepted by it. A test program is provided by testFrame()
 * to allow easy testing of swappers.
 */
 public abstract class AbstractComponentSwapper extends ActionValueJPanel
                                                implements IComponentSwapper
 {
  /**
   * "View Type Changed" - This static String is a message sent out when
   * the view component changes and the menu items for that component
   * need to be removed from the menu bar.
   */
   public static final String VIEW_TYPE_CHANGED = "View Type Changed";
  // -------------------------ObjectState keys---------------------------//
  /**
   * "Visible View" - This static String key saves the ObjectState
   * information for the currently visible view component. This key references
   * a String view type defined by the IComponentSwapper interface.
   */
   public static final String VISIBLE_VIEW = "Visible View";
   
  //------------------------------Data Members------------------------------//
  /**
   * The data being displayed by the ComponentSwapper.
   */ 
   protected IVirtualArray data = null;
   protected ComponentTracker current_tracker;
   private Hashtable viewstate;       // Hashtable containing ObjectStates.
   private Hashtable viewprefs;       // Hashtable containing ObjectStates.
   private String visible_view;       // The currently displayed view.
   private String[] available_views;  // The available views for this swapper.
   private transient Box ctrl_panel;  // GUI container for all visible controls.
   
  /**
   * Default Contructor - Initializes list of possible view that can be
   * displayed by this ComponentSwapper.
   *
   *  @param  possible_views String[] list of view types specified by the
   *                         IComponentSwapper.
   */
   protected AbstractComponentSwapper( String[] possible_views,
                                       IVirtualArray iva,
				       String view_type )
   {
     super();
     setLayout( new GridLayout(1,1) );
     // If no views are specified, do nothing.
     if( possible_views == null || possible_views.length <= 0 )
       return;
     // Check and make sure views passed in are valid.
     // Use Vector to remove repetitive entries.
     Vector valid_possible_views = new Vector();
     int counter = 0;
     for( int i = 0; i < possible_views.length; i++ )
     {
       if( isValidViewType(possible_views[i]) &&
           !valid_possible_views.contains(possible_views[i]) )
       {
         valid_possible_views.add(possible_views[i]);
       }
       else
         counter++;
     }
     // If any views were invalid, "weed them out".
     if( counter > 0 )
     {
       String[] new_possible_views = (String[])valid_possible_views.toArray();
       // Make sure valid views exist.
       if( new_possible_views.length <= 0 )
         return;
       available_views = new_possible_views;
     }
     // Else all views were valid.
     else
       available_views = possible_views;
     
     // Initialize viewsstate Hashtable that will store state information.
     viewstate = new Hashtable(available_views.length);
     // Initialize viewprefs Hashtable that will store user preferences.
     viewprefs = new Hashtable(available_views.length);
     // If an available view, set this to be the visible view.
     if( isAvailableViewType(view_type) )
       visible_view = view_type;
     // If invalid view, set visible view to be first available view.
     else
       visible_view = available_views[0];
     // Initialize container for controls.
     ctrl_panel = new Box(BoxLayout.Y_AXIS);
     // Set data is implemented by extending classes. This will either
     // initialize the view component if not yet created, or call
     // some dataChanged() on an existing view component.
     setData(iva);
   }
  
  /**
   * Get the current state of this ComponentSwapper.
   *
   *  @param  is_default Is desired state for preferences (default=true) or 
   *		         a project (session=false) save? Use IPreserveState
   *		         variables for parameter.
   *  @return The current ObjectState of this ComponentSwapper.
   */
   public ObjectState getObjectState( boolean is_default )
   {
     ObjectState state = new ObjectState();
     // If PROJECT save, get the project states of the view components.
     if( !is_default )
     {
       // Update state in viewstate hashtable for current tracker, if it exists.
       if( current_tracker != null )
         viewstate.put(visible_view, current_tracker.getObjectState(PROJECT));
       // Save the state of all ComponentTrackers behind the scenes.
       Enumeration keys = viewstate.keys();
       String key;
       while( keys.hasMoreElements() )
       {
         key = (String)keys.nextElement();
         // Save each ObjectState under the view type.
         state.insert(key, viewstate.get(key));
       }
     }
     // If default save, get preferences for each view component.
     else
     {
       // Update state in viewprefs hashtable for current tracker, if it exists.
       if( current_tracker != null )
       {
         // Let key be ViewTypePrefs.
         StringBuffer key = new StringBuffer(visible_view).append("Prefs");
         viewprefs.put(key.toString(), current_tracker.getObjectState(DEFAULT));
       }
       // Save the state of all ComponentTrackers behind the scenes.
       Enumeration keys = viewprefs.keys();
       String key;
       ObjectState temp_prefs;
       while( keys.hasMoreElements() )
       {
         key = (String)keys.nextElement();
         // Save each ObjectState under the view type.
	 temp_prefs = (ObjectState)viewprefs.get(key);
	 if( temp_prefs != null )
           state.insert(key, temp_prefs);
       }
     }
     
     state.insert( VISIBLE_VIEW, visible_view );
     
     return state;
   }
   
  /**
   * Set the state of the ComponentSwapper to new_state.
   *
   *  @param  new_state The new state of the ComponentSwapper.
   */ 
   public void setObjectState( ObjectState new_state )
   {
     // Do nothing if state is null.
     if( new_state == null )
       return;
     boolean rebuild_and_repaint = false;
     
     Object value = new_state.get(VISIBLE_VIEW);
     if( value != null )
     {
       setVisibleViewType((String)value);
       rebuild_and_repaint = true;
     }
     
     // Set state for all ComponentTrackers. This is done without user's
     // knowledge. No static variables are provided to get this state.
     for( int i = 0; i < available_views.length; i++ )
     {
       value = new_state.get(available_views[i]);
       if( value != null )
       {
         // Replace any existing value in the ObjectState hashtable.
         viewstate.put(available_views[i], value);
         // If the visible ViewComponent has ObjectState saved, restore it.
         if( available_views[i] == getVisibleViewType() )
           current_tracker.setObjectState((ObjectState)value);
         rebuild_and_repaint = true;
       }
     }
     
     // Set preferences for all ComponentTrackers. This is done without user's
     // knowledge. No static variables are provided to get this state.
     StringBuffer key;
     for( int i = 0; i < available_views.length; i++ )
     {
       key = new StringBuffer(available_views[i]).append("Prefs");
       value = new_state.get(key.toString());
       if( value != null )
       {
         // Replace any existing value in the ObjectState hashtable.
         viewprefs.put(key.toString(), value);
	 // If the visible ViewComponent has ObjectState saved, restore it.
         if( available_views[i].equals(getVisibleViewType()) )
	 {
           current_tracker.setObjectState((ObjectState)value);
         }
	 rebuild_and_repaint = true;
       }
     }
     
     // Rebuild and repaint GUI if necessary.
     if( rebuild_and_repaint )
     {
       validate();
       repaint();
     }
   }
   
  /**
   * Get the list of control indices hidden by this swapper for the visible
   * IViewComponent. The indices are consistent with the array of
   * ViewControls returned by the IViewComponent.
   *
   *  @return int[] of indices corresponding to controls in the list of
   *          ViewControls returned by IViewComponent.getControls(). If
   *          no controls exist, an empty array (int[0]) is returned. If
   *          no visible ViewComponent, null is returned.
   */ 
   public int[] getHiddenControls()
   {
     if( current_tracker == null )
       return null;
     return current_tracker.getHiddenControls();
   }
   
  /**
   * Get the list of control indices shown by this swapper for the visible
   * IViewComponent. The indices are consistent with the array of
   * ViewControls returned by the IViewComponent.
   *
   *  @return int[] of indices corresponding to controls in the list of
   *          ViewControls returned by IViewComponent.getControls(). If
   *          no controls exist, an empty array (int[0]) is returned. If
   *          no visible ViewComponent, null is returned.
   */ 
   public int[] getVisibleControls()
   {
     if( current_tracker == null )
       return null;
     return current_tracker.getVisibleControls();
   }
   
  /**
   * Set the visible controls of the visible IViewComponent based on
   * a predetermined scheme.
   *
   *  @param  display_scheme Predefined method for choosing visible controls. 
   */ 
   public void setVisibleControls( int display_scheme )
   {
     // If either view type or control scheme is invalid, do nothing.
     if( !isValidControlScheme(display_scheme) )
       return;
     
     if( current_tracker == null )
       return;
     // By default, any of these options will display all of the controls.
     if( display_scheme == SHOW_ALL || display_scheme == USER_DEFINED ||
         display_scheme == SHOW_DEFAULT )
     {
       ViewControl[] ctrl_list = current_tracker.getViewComp().getControls();
       int[] visible_list = new int[ctrl_list.length];
       // Build list of indices for all controls.
       for( int i = 0; i < visible_list.length; i++ )
         visible_list[i] = i;
       setVisibleControls(visible_list);
     }
     else if( display_scheme == SHOW_NONE )
     {
       setVisibleControls(new int[0]);
     }
     // Since the other setVisibleControls() method set the display scheme
     // to USER_DEFINED, restore it to the actual value.
     current_tracker.setControlScheme(display_scheme);
   }
   
  /**
   * Set the visible controls of the visible IViewComponent based on
   * a list of control indices. Control indices not included will be
   * hidden. The indices are consistent with the array of ViewControls
   * returned by the IViewComponent. Order of the indices is important and
   * can be used to change the order of control layout.
   *
   *  @param  control_indices List of indices for choosing visible controls. 
   */ 
   public void setVisibleControls( int[] control_indices )
   {
     // If no view has been specified, no visible controls can be set.
     if( current_tracker == null )
       return;
     current_tracker.setVisibleControls(control_indices);
     current_tracker.setControlScheme(USER_DEFINED);
     // If SHOW_NONE, change container from SplitPane to JPanel.
     if( control_indices == null || control_indices.length == 0 )
       buildPanel();
     // Else just rebuild control panel.
     else
       buildControlPanel();
     validate();
     repaint();
   }
   
  /**
   * This method gets the visible IViewComponent. Use this method to get
   * a complete list of component controls and component menu items. Also,
   * specialized settings for each IViewComponent can be set by upper levels.
   *
   *  @return IViewComponent specified by view_type. If view_type is invalid,
   *          null is returned.
   */ 
   public IViewComponent getViewComponent()
   {
     // If tracker has not yet been initialized, do nothing.
     if( current_tracker == null )
     {
       return null;
     }
     return current_tracker.getViewComp();
   }
   
  /**
   * Get ViewMenuItems generated by the class implementing IComponentSwapper.
   * This list should include a ViewMenuItem to swap between IViewComponents.
   *
   *  @return List of ViewMenuItems unique to this IComponentSwapper.
   */ 
   public ViewMenuItem[] getSwapperMenuItems()
   {
     JMenu swap_menu = new JMenu("Display as...");
     JMenuItem temp = new JMenuItem();
     boolean valid_menu;
     for( int i = 0; i < available_views.length; i++ )
     {
       if( isAvailableViewType(available_views[i]) )
       {
         temp = new JMenuItem(available_views[i]);
         // Set the action command to the view type.
         temp.setActionCommand( available_views[i] );
         temp.addActionListener( new SwapListener() );
         swap_menu.add(temp);
       }
       else
       {
         SharedMessages.addmsg("ERROR in AbstractComponentSwapper."+
	                       "getSwapperMenuItems() - Unsupported "+
			       "view type code ["+available_views[i]+
			       "]. Please add definition to this method.");
       }
     }
     
     ViewMenuItem swap_views = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS,
                                                 swap_menu );
     ViewMenuItem[] vcm = getViewComponent().getMenuItems();
     ViewMenuItem[] menu_items = new ViewMenuItem[vcm.length+1];
     menu_items[0] = swap_views;
     // Copy array of view component menu items to swapper menu items.
     System.arraycopy(vcm,0,menu_items,1,vcm.length);
     return menu_items;
   }
   
  /**
   * Get all available IViewComponent types displayable by this swapper. Types
   * are specified by String keys defined by the IComponentSwapper interface.
   *
   *  @return List of available IViewComponent types that can be displayed
   *          by this swapper.
   */
   public String[] getViewTypes() { return available_views; }
   
  /**
   * Get the currently visible IViewComponent type displayed by this swapper.
   * Types are specified by String keys defined by the IComponentSwapper
   * interface.
   *
   *  @return Current IViewComponent type that is being displayed
   *          by this swapper.
   */
   public String getVisibleViewType() { return visible_view; }
   
  /**
   * Set the currently visible IViewComponent type displayed by this swapper.
   * Types are specified by String keys defined by the IComponentSwapper
   * interface.
   *
   *  @param  view_type Current IViewComponent type that is to be displayed
   *                    by this swapper.
   */
   public void setVisibleViewType( String view_type )
   {
     // If visible view is already the specified view_type, do nothing.
     if( visible_view.equals(view_type) )
       return;
     // If no available views, do nothing.
     if( isAvailableViewType(view_type) )
     {
       // If view has already been created, keep state information about
       // the current view component soon to be switched.
       if( current_tracker != null )
       {
         viewstate.put(visible_view, current_tracker.getObjectState(PROJECT));
         // Add Prefs to end of visible view type.
         StringBuffer key = new StringBuffer(visible_view).append("Prefs");
         viewprefs.put(key.toString(), current_tracker.getObjectState(DEFAULT));
       }
       String previous_view = new String(visible_view);
       visible_view = view_type;
       buildComponent(visible_view);
       // If state for this view type has been saved, restore the state.
       ObjectState state = (ObjectState)viewstate.get(visible_view);
       if( state != null )
         current_tracker.setObjectState(state);
       // Else if preferences have been saved, restore them.
       else
       {
         StringBuffer key = new StringBuffer(visible_view).append("Prefs");
         state = (ObjectState)viewprefs.get(key.toString());
         if( state != null )
	 {
           current_tracker.setObjectState(state);
         }
       }
       // Reconstruct the display.
       buildPanel();
       // This method is defined by extending classes. It will pass any
       // information from the old view component to the newly visible
       // view component.
       linkComponents();
       // Tell listeners that the menu list has changed. Give the old list
       // and the new list.
       sendActionValue( new ActionValueEvent( this, VIEW_TYPE_CHANGED,
					      previous_view, visible_view ) );
       validate();
       repaint();
       return;
     }
     // If view_type not an available view, print error message.
     SharedMessages.addmsg("ERROR - Invalid view type ["+view_type+
                           "] in ComponentSwapper.setVisibleViewType()" );
   }
  
  /**
   * Check to see if value passed in is a valid view for this ComponentSwapper.
   *
   *  @param  view_type View type defined by IComponentSwapper.
   *  @return True if this ComponentSwapper supports the view specified,
   *          False if view is unsupported.
   */
   public boolean isAvailableViewType( String view_type )
   {
     if( available_views == null || view_type == null )
       return false;
     // Search through all available views.
     for( int i = 0; i < available_views.length; i++ )
     {
       if( view_type.equals(available_views[i]) )
         return true;
     }
     return false;
   }
  
  /*
   * Check to see if value passed in is a view defined by IComponentSwapper.
   *
   *  @param  view_type View type defined by IComponentSwapper.
   *  @return True if this ComponentSwapper supports the view specified,
   *          False if view is unsupported.
   */
   private boolean isValidViewType( String view_type )
   {
     // Search through all available views defined by IComponentSwapper.
     for( int i = 0; i < total_views.length; i++ )
     {
       if( view_type.equals(total_views[i]) )
         return true;
     }
     // If not found in list, return false.
     return false;
   }
  
  /**
   * Check to see if value passed in is a valid option for laying out controls.
   *
   *  @param  control_scheme Method for showing controls as defined by
   *                         IComponentSwapper.
   *  @return True if this ComponentSwapper supports the method,
   *          False if method is unsupported.
   */
   public boolean isValidControlScheme( int control_scheme )
   {
     if( control_scheme >= SHOW_ALL && control_scheme < USER_DEFINED )
       return true;
     return false;
   }
  
  /**
   * This method sets the data to be displayed by the ComponentSwapper.
   * Since data specific knowledge is needed, the extending ComponentSwapper
   * must provide the implementation.
   *
   *  @param  iva The data to be displayed by the swapper.
   */ 
   abstract public void setData(IVirtualArray iva);
  
  /**
   * This method creates a ComponentTracker containing an IViewComponent
   * associated with view_type. Since association of IViewComponents to
   * view types is specific to the ComponentSwapper, classes extending
   * AbstractComponentSwapper must provide the implementation.
   *
   *  @param  view_type The view_type that needs to be added to the table.
   */ 
   abstract protected void buildComponent(String view_type);
  
  /**
   * This method passes information from the previous view component
   * to the new visible view component. Information such as pointed at and
   * colorscale should be set here.
   */ 
   abstract protected void linkComponents();
  
  /*
   * This method builds the control panel containing all visible controls
   * for the swapper display. This method should only be called by classes
   * extending AbstractComponentSwapper.
   */ 
   private void buildControlPanel()
   {
     if( current_tracker == null )
       return;
     ViewControl[] ctrls = current_tracker.getViewComp().getControls();
     int[] visible_list = current_tracker.getVisibleControls();
     // Remove everything from the control panel.
     ctrl_panel.removeAll();
     for( int i = 0; i < visible_list.length; i++ )
     {
       // Make sure index in visible_list is valid.
       if( visible_list[i] < ctrls.length )
         ctrl_panel.add(ctrls[visible_list[i]]);
     }
     JPanel spacer = new JPanel();
     spacer.setPreferredSize(new java.awt.Dimension(0,1000));
     ctrl_panel.add(spacer);
   }
  
  /**
   * This method builds the full panel, including the view component and
   * any visible controls in a JSplitPane. If no controls are visible, the
   * view component itself is added to the swapper. This method should only
   * be called by classes extending AbstractComponentSwapper.
   */ 
   protected void buildPanel()
   {
     buildControlPanel(); // Rebuild the controls panel for this swapper.
     this.removeAll(); // Remove all graphical components from the panel.
     if( data == null )
     {
       JPanel no_data_panel = new JPanel();
       no_data_panel.add( new JLabel("No data to display.") );
       add( no_data_panel );
       return;
     }
     IViewComponent ivc = getViewComponent();
     
     // If no controls is specified or no controls are available for display,
     // add the view component directly to the panel. 
     if( current_tracker.getControlScheme() == SHOW_NONE ||
         getVisibleControls().length == 0 )
     {
       this.add(ivc.getDisplayPanel());
     }
     // if user wants controls, and controls exist, display them in a splitpane.
     else
     {
       this.add( new SplitPaneWithState( SplitPaneWithState.HORIZONTAL_SPLIT,
    	  			         ivc.getDisplayPanel(),
        			         ctrl_panel, .8f ) );
     }
     
     // Add preferences state if it doesn't exist.
     if( !viewprefs.containsKey(visible_view) )
     {
       // Let key be ViewTypePrefs.
       StringBuffer key = new StringBuffer(visible_view).append("Prefs");
       viewprefs.put(key.toString(), current_tracker.getObjectState(DEFAULT));
     }
   }
  
  /**
   * This class groups together a view component and its shared controls.
   * Since a swapper can have multiple view components, this is a convenience
   * class for keeping track of components.
   */
   protected class ComponentTracker implements java.io.Serializable
   {
     private IViewComponent ivc;
     private int[] vis_ctrls;
     private int ctrl_scheme;
     protected ComponentTracker(IViewComponent vc)
     {
       ivc = vc;
       // By default, let all controls be shown.
       if( ivc != null )
       {
         vis_ctrls = new int[ivc.getControls().length];
	 for( int i = 0; i < vis_ctrls.length; i++ )
	   vis_ctrls[i] = i;
       }
       ctrl_scheme = SHOW_ALL;
     }
     
     protected ObjectState getObjectState( boolean is_default )
     {
       ObjectState state = new ObjectState();
       state.insert("Visible Controls",getVisibleControls());
       state.insert("Control Scheme",new Integer(getControlScheme()));
       state.insert("ViewComponent", ivc.getObjectState(is_default));
       return state;
     }
     
     protected void setObjectState( ObjectState new_state )
     {
       // If no state, do nothing.
       if( new_state == null )
         return;
       Object value = new_state.get("Visible Controls");
       if( value != null )
       {
         setVisibleControls((int[])value);
       }
       
       value = new_state.get("Control Scheme");
       if( value != null )
       {
         setControlScheme(((Integer)value).intValue());
       }
       
       value = new_state.get("ViewComponent");
       if( value != null )
       {
         if( ivc != null )
	   ivc.setObjectState((ObjectState)value);
       }
     }
    
    /*
     * Get the IViewComponent.
     */
     protected IViewComponent getViewComp(){ return ivc; }
    
    /*
     * Get the visible controls for an associated IViewComponent.
     */
     protected int[] getVisibleControls(){ return vis_ctrls; }
    
    /*
     * Set the visible controls for an associated IViewComponent.
     */
     protected void setVisibleControls( int[] ctrl_indices )
     {
       if( ctrl_indices == null )
         vis_ctrls = new int[0];
       else
         vis_ctrls = ctrl_indices;
     }
     
    /*
     * Set how controls should be displayed, as defined by IComponentSwapper.
     */
     protected void setControlScheme( int scheme ){ ctrl_scheme = scheme; }
     
    /*
     * Get how controls should be displayed, as defined by IComponentSwapper.
     */
     protected int getControlScheme(){ return ctrl_scheme; }
    
    /*
     * Set the hidden controls for an associated IViewComponent.
     */
     protected int[] getHiddenControls()
     {
       if( ivc == null )
         return new int[0];
       // Create boolean table same size as controls, mark true if visible.
       boolean[] visible = new boolean[ivc.getControls().length];
       int counter = 0;
       for( int i = 0; i < vis_ctrls.length; i++ )
       {
         // Make sure index is valid.
         if( vis_ctrls[i] >= 0 && vis_ctrls[i] < visible.length )
         {
	   // If index appears twice, do not count it a second time.
	   if( !visible[vis_ctrls[i]] )
             counter++;
	   // Mark control index as visible.
           visible[vis_ctrls[i]] = true;
         }
       }
       int[] hidden_ctrls = new int[visible.length-counter];
       int index = 0;
       for( int i = 0; i < visible.length; i++ )
       {
         // If not marked as visible, add index to hidden controls array.
         if( !visible[i] )
         {
           hidden_ctrls[index++] = i;
         }
       }
       return hidden_ctrls;
     }
   } // End ComponentTracker
  
  /*
   * This class is responsible for swapping between view components when the
   * swapping menu item is used.
   */
   private class SwapListener implements ActionListener
   {
     public void actionPerformed( ActionEvent ae )
     {
       setVisibleViewType(ae.getActionCommand());
     }
   }
  
  /**
   * Test JFrame that constructs a menu bar with File,Edit,Options, and Help
   * JMenus. This will test the ability to swap between views.
   *
   *  @param  ics A class extending AbstractComponentSwapper that has
   *              already been initialized with an IVirtualArray.
   */ 
   protected static void testFrame( AbstractComponentSwapper ics )
   {
     // Build Menubar.
     JMenuBar menu_bar = new JMenuBar();
     menu_bar.add( new JMenu("File") );
     menu_bar.add( new JMenu("Edit") );
     menu_bar.add( new JMenu("Options") );
     menu_bar.add( new JMenu("Help") );
     ViewMenuItem[] items = ics.getSwapperMenuItems();
     for( int i = 0; i < items.length; i++ )
     {
       if( items[i].getPath().equals(ViewMenuItem.PUT_IN_FILE) )
       {
         menu_bar.getMenu(0).add(items[i].getItem());
       }
       else if( items[i].getPath().equals(ViewMenuItem.PUT_IN_EDIT) )
       {
         menu_bar.getMenu(1).add(items[i].getItem());
       }
       else if( items[i].getPath().equals(ViewMenuItem.PUT_IN_OPTIONS) )
       {
         menu_bar.getMenu(2).add(items[i].getItem());
       }
       else if( items[i].getPath().equals(ViewMenuItem.PUT_IN_HELP) )
       {
         menu_bar.getMenu(3).add(items[i].getItem());
       }
     }
     JFrame frame = new JFrame("ComponentSwapperTest");
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setBounds(0,0,800,600);
     frame.setJMenuBar(menu_bar);
     frame.getContentPane().add(ics);
     // Class that "correctly" draws the display.
     WindowShower shower = new WindowShower(frame);
     java.awt.EventQueue.invokeLater(shower);
     shower = null;
   }
 }
