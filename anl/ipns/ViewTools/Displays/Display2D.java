/*
 * File:  Display2D.java
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
 * Revision 1.10  2004/12/05 05:55:40  millermi
 * - Fixed Eclipse warnings.
 *
 * Revision 1.9  2004/09/15 21:58:29  millermi
 * - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *   Adding a second log required the boolean parameter to be changed
 *   to an int. These changes may affect any ObjectState saved configurations
 *   made prior to this version.
 *
 * Revision 1.8  2004/08/17 01:22:05  millermi
 * - Added colorscale reset to ViewCompListener. This fixed bug
 *   that incorrectly saved the colorscale.
 *
 * Revision 1.7  2004/08/13 03:39:13  millermi
 * - Added TableViewComponent capabilities.
 * - Moved image specific menu items from Display to this class.
 * - Colorscale is now saved when switching between view components
 * - ***KNOWN BUG*** ObjectState currently does not correctly save
 *   all attributes.
 *
 * Revision 1.6  2004/05/11 01:49:49  millermi
 * - Updated javadocs for class description.
 *
 * Revision 1.5  2004/03/19 21:30:06  millermi
 * - Changed controls parameter to int instead of boolean.
 * - Added CTRL_ALL and CTRL_NONE to Display as constants for
 *   control parameter.
 * - Moved Options menu from Display to Display2D.
 * - Added method loadProps() to load the properties file.
 *
 * Revision 1.4  2004/03/15 23:53:54  dennis
 * Removed unused imports, after factoring out the View components,
 * Math and other utils.
 *
 * Revision 1.3  2004/03/13 07:42:06  millermi
 * - Removed unused imports.
 * - Finished factoring out Display from Display2D.
 * - ObjectState now implemented, but needs IViewComponet to
 *   extend IPreserveState before completion.
 * - Wrote meaningful help dialogue.
 *
 * Revision 1.2  2004/03/12 23:23:24  millermi
 * - Factored out common functionality, now extends Display.
 *
 * Revision 1.1  2004/03/12 21:00:30  millermi
 * - Added to CVS, stripped down version of SANDWedgeViewer
 *   that is independent of DataSets
 * - NOTE: STILL AT DEMO QUALITY, MENU ITEMS CURRENTLY NOT
 *   LINKED PROPERLY.
 *
 *
 */

package gov.anl.ipns.ViewTools.Displays;

import javax.swing.*;
import java.util.Vector;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.text.html.HTMLEditorKit;

import gov.anl.ipns.ViewTools.UI.SplitPaneWithState;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.TwoD.*;
import gov.anl.ipns.ViewTools.Components.Transparency.SelectionOverlay;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.PrintComponentActionListener;
import gov.anl.ipns.Util.Sys.SaveImageActionListener;

/**
 * Simple class to display an 2-dimensional array of data, specified by an
 * IVirtualArray2D. Two common views for this display are as an image and
 * as a table (in progress).
 */
public class Display2D extends Display
{
 /**
  * "View Option" - This constant String is a key for referencing
  * the state information about which view component is used to display data.
  * The value this key references is of type Integer.
  */
  public static final String VIEW_OPTION           = "View Option";
  
 /**
  * "Control Option" - This constant String is a key for referencing
  * the state information about whether or not controls are displayed with
  * the view component. The value this key references is of type Integer.
  */
  public static final String CONTROL_OPTION           = "Control Option";
  
 /**
  * "ViewerSize" - This constant String is a key for referencing
  * the state information about the size of the viewer at the time
  * the state was saved. The value this key references is of type
  * Dimension.
  */
  public static final String VIEWER_SIZE           = "Viewer Size";
  
 /**
  * "View Component" - This constant String is a key for referencing
  * the state information about the ViewComponent. The value this key
  * references is of type ObjectState.
  */
  public static final String VIEW_COMPONENT        = "View Component";
  
 /**
  * "Color Scale" - This constant String is a key for referencing
  * the state information about the colorscale used to display the
  * ViewComponents. The value this key references is of type String.
  */
  public static final String COLOR_SCALE           = "Color Scale";
  
 /**
  * 0 - Use this int to specify display using the ImageViewComponent.
  */ 
  public static final int IMAGE = 0;
 
 /**
  * 1 - Use this int to specify display using the TableViewComponent.
  */
  public static final int TABLE = 1;
  
  // many of the variables are protected in the Display base class
  private static JFrame helper = null;
  private final String PROP_FILE = System.getProperty("user.home") + 
    		                   System.getProperty("file.separator") +
		                   "Display2DProps.isv";
  private boolean os_region_added = false;
  private String colorscale;
  
 /**
  * Construct a frame with the specified image and title
  *  
  *  @param  iva Two-dimensional virtual array.
  *  @param  view_code Code for which view component is to be used to
  *                    display the data.
  *  @param  include_ctrls If true, controls to manipulate image will be added.
  */
  public Display2D( IVirtualArray2D iva, int view_code, int include_ctrls )
  {
    super(iva,view_code,include_ctrls);
    setTitle("Display2D");
    addToMenubar();
    buildPane();
    loadProps(PROP_FILE);
    saveColorScale();
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
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(VIEW_OPTION); 
    if( temp != null )
    {
      // If this view is different than the saved view, rebuild with new view.
      if( current_view != ((Integer)temp).intValue() )
      {
        removeComponentMenuItems();
        current_view = ((Integer)temp).intValue();
        buildPane();
      }
      redraw = true;  
    }
    
    temp = new_state.get(CONTROL_OPTION); 
    if( temp != null )
    {
      if( add_controls != ((Integer)temp).intValue() )
      {
        removeComponentMenuItems();
        add_controls = ((Integer)temp).intValue();
        buildPane();
      }
      redraw = true;  
    }
    
    temp = new_state.get(VIEW_COMPONENT);
    if( temp != null )
    {
      if( current_view == IMAGE )
      {
        Object os = ((ObjectState)temp).get(
	                        ImageViewComponent.SELECTION_OVERLAY);
	if( os != null )
        {
          os = ((ObjectState)os).get(SelectionOverlay.SELECTED_REGIONS);
	  if( os != null )
            os_region_added = true;
        }
      }
      // set the object state of the view component.
      if( ivc != null )
        ivc.setObjectState( (ObjectState)temp );
      redraw = true;  
    }
    
    temp = new_state.get(VIEWER_SIZE); 
    if( temp != null )
    {
      setSize( (Dimension)temp );
      redraw = true;  
    }
    
    temp = new_state.get(COLOR_SCALE); 
    if( temp != null )
    {
      colorscale = (String)temp;
      if( current_view == IMAGE )
        ((ImageViewComponent)ivc).setColorScale(colorscale);
      else if( current_view == TABLE )
        ((TableViewComponent)ivc).setThumbnailColorScale(colorscale);
      redraw = true;  
    }
    
    if( redraw )
      repaint();
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
    ObjectState state = new ObjectState();
    state.insert( VIEW_OPTION, new Integer(current_view) );
    state.insert( CONTROL_OPTION, new Integer(add_controls) );
    if( ivc != null )
      state.insert( VIEW_COMPONENT, ivc.getObjectState(isDefault) );
    state.insert( VIEWER_SIZE, getSize() );
    if( colorscale != null )
      state.insert( COLOR_SCALE, colorscale );
    return state;
  }

 /**
  * Contains/Displays control information about this viewer.
  */
  public static void help()
  {
    helper = new JFrame("Introduction to the Display2D");
    helper.setBounds(0,0,600,400);
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Description:</H1> <P>" + 
                "The Display2D (D2D) in an interactive analysis tool. " +
        	"D2D features the ability to quickly analyze a 2-D " +
		"array of data in a variety of views. Views that are " +
		"included are currently restricted to Images, but new " +
		"views will include Table and Contour.</P>" + 
		"<H2>Commands for D2D</H2>" +
                "<P> SAVING USER PREFERENCES: Click on <B>Options|Save User " +
		"Settings</B>. Your preferences will automatically be saved " +
		"in Display2DProps.isv in your home directory. <I>This " +
		"option will not save project specific information, such as " +
		"selections or annotations. Use <B>File|Save Project</B> " +
		"to save project specific details.</I><BR><BR>" +
		"<BR>Note:<BR>" +
        	"Detailed commands for each overlay can be found under " +
		"<B>Help|Overlays</B>.</P>";
    textpane.setText(text);
    JScrollPane scroll = new JScrollPane(textpane);
    scroll.setVerticalScrollBarPolicy(
        			    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    WindowShower shower = new WindowShower(helper);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
  
 /**
  * This method takes in a virtual array and updates the image. If the array
  * is the same data array, the image is just redrawn. If the array is
  * different, the a new view component is constructed.
  *
  *  @param  values
  */ 
  public void dataChanged( IVirtualArray2D values )
  { 
    ((IViewComponent2D)ivc).dataChanged(values);
  }
  
 /**
  * This method sets the pointed-at on this display.
  *
  *  @param  fpt
  */
  public void setPointedAt( floatPoint2D fpt )
  {
    //set the cursor position on the view component
    ((IViewComponent2D)ivc).setPointedAt( fpt ); 
  }

 /**
  * This method gets the current floatPoint2D from the ImageJPanel and 
  * converts it to a Point.
  *
  *  @return  The current pointed-at world coordinate point as a floatPoint2D
  */
  public floatPoint2D getPointedAt()
  {
    return ((IViewComponent2D)ivc).getPointedAt();
  }
 
 /**
  * This method creates a selected region to be displayed over the imagejpanel
  * by the selection overlay. Any previously selected regions will be erased
  * by using this method. To maintain previous selections, use
  * addSelectedRegion(). A stack could be added to allow for undo and redo
  * of selections. 
  * If null is passed as a parameter, the selections will be cleared.
  *
  *  @param  rgn - array of selected Regions
  */ 
  public void setSelectedRegions( Region[] rgn ) 
  {
    ((IViewComponent2D)ivc).setSelectedRegions(rgn);
  }
 
 /**
  * Get geometric regions created using the selection overlay.
  *
  *  @return selectedregions
  */ 
  public Region[] getSelectedRegions()
  {
    return ((IViewComponent2D)ivc).getSelectedRegions();
  }

 /*
  * This method builds the content pane of the frame.
  */
  private void buildPane()
  { 
    // Clear any existing views, so it can be rebuilt.
    getContentPane().removeAll();
    
    if( current_view == IMAGE )
    {
      ivc = new ImageViewComponent( (IVirtualArray2D)data );
      ((ImageViewComponent)ivc).setColorControlEast(true);
      ((ImageViewComponent)ivc).preserveAspectRatio(true);
      if( colorscale != null )
        ((ImageViewComponent)ivc).setColorScale(colorscale);
    }
    if( current_view == TABLE )
    {
      ivc = new TableViewComponent( (IVirtualArray2D)data );
      if( colorscale != null )
        ((TableViewComponent)ivc).setThumbnailColorScale(colorscale);
    }
    ivc.addActionListener( new ViewCompListener() );    
    
    //Box componentholder = new Box(BoxLayout.Y_AXIS);
    //componentholder.add( ivc.getDisplayPanel() );
    Box view_comp_controls = buildControlPanel();
    // if user wants controls, and controls exist, display them in a splitpane.
    if( add_controls == CTRL_ALL && view_comp_controls != null )
    {
      setBounds(0,0,700,510);
      pane = new SplitPaneWithState(JSplitPane.HORIZONTAL_SPLIT,
    	  			    ivc.getDisplayPanel(),
        			    view_comp_controls, .75f );
    }
    else
    {
      setBounds(0,0,500,500);
      pane = ivc.getDisplayPanel();
    }
    getContentPane().add(pane);
    addComponentMenuItems();
    // Repaint the display, this is needed when the menu items are used
    // the switch between views.
    validate();
    repaint();
  }
 
 /*
  * This private method will (re)build the menubar. This is necessary since
  * the ImageViewComponent could add menu items to the Menubar.
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
    
    // build options menu
    options.add("Options");
    option_listeners.add( new Menu2DListener() ); // listener for options
    options.add(save_default);
      save_default.add("Save User Settings");
      option_listeners.add( new Menu2DListener() ); // listener for user prefs.
    options.add(switch_view);
      switch_view.add("View Data As...");
      switch_view.add("Image");
      option_listeners.add( new Menu2DListener() ); // listener for user prefs
      switch_view.add("Table");
      option_listeners.add( new Menu2DListener() ); // listener for user prefs
    
    // build help menu
    help.add("Help");
    help_listeners.add( new Menu2DListener() );
    help.add( display_help );
      display_help.add("Using Display2D");
      help_listeners.add( new Menu2DListener() );  // listener for D2D helper
    menu_bar.add( MenuItemMaker.makeMenuItem(options,option_listeners) );
    menu_bar.add( MenuItemMaker.makeMenuItem(help,help_listeners) );
    
    // Add image specific menu items.
    Vector print             = new Vector();
    Vector save_image        = new Vector();
    Vector file_listeners    = new Vector();
    
    print.add("Print Image");
    save_image.add("Make JPEG Image");
    file_listeners.add( new Menu2DListener() );
    JMenu file_menu = menu_bar.getMenu(0);
    file_menu.add( MenuItemMaker.makeMenuItem( print, file_listeners ),
		   file_menu.getItemCount() - 1 );
    file_menu.add( MenuItemMaker.makeMenuItem( save_image, file_listeners ),
		   file_menu.getItemCount() - 1 );
    
    // If view is not currently an image, disable the "Print Image" and
    // "Make JPEG Image" menu items, since they are Image specific.
    if( current_view != IMAGE )
    {
      file_menu.getItem(2).setEnabled(false);
      file_menu.getItem(3).setEnabled(false);
    }
    
    // Add keyboard shortcuts
    KeyStroke binding = 
               KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.ALT_MASK);
    file_menu.getItem(2).setAccelerator(binding);   // Print Image
    binding = KeyStroke.getKeyStroke(KeyEvent.VK_J,InputEvent.ALT_MASK);
    file_menu.getItem(3).setAccelerator(binding);   // Make JPEG Image
    JMenu option_menu = menu_bar.getMenu(1);
    binding = KeyStroke.getKeyStroke(KeyEvent.VK_U,InputEvent.ALT_MASK);
    option_menu.getItem(0).setAccelerator(binding); // Save User Settings
    JMenu help_menu = menu_bar.getMenu(2);
    binding = KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.ALT_MASK);
    help_menu.getItem(0).setAccelerator(binding);   // Help Menu
    
  }
  
 /*
  * This method saves the colorscale of the current component so it may
  * be applied to other view components, should the view be switched.
  */ 
  private void saveColorScale()
  {
    if( current_view == IMAGE )
    {
      colorscale = ((ImageViewComponent)ivc).getColorScale();
    }
    else if( current_view == TABLE )
    {
      colorscale = ((TableViewComponent)ivc).getThumbnailColorScale();
    }
  }
  
 /*
  * This class listens to the view component.
  */
  private class ViewCompListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
      sendMessage( message );
      if( message.equals( ImageViewComponent.COLORSCALE_CHANGED ) )
      {
        colorscale = ((ImageViewComponent)ivc).getColorScale();
      }
      else if(message.equals(TableViewComponent.THUMBNAIL_COLOR_SCALE_CHANGED))
      {
        colorscale = ((TableViewComponent)ivc).getThumbnailColorScale();
      }      
    }
  }
  
 /*
  * This class is required to handle all messages within the Display2D.
  */
  private class Menu2DListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      if( ae.getActionCommand().equals("Save User Settings") )
      {
	getObjectState(IPreserveState.DEFAULT).silentFileChooser( PROP_FILE,
	                                                          true );
      }
      // Called if user selects help option.
      else if( ae.getActionCommand().equals("Using Display2D") )
      {
        help();
      }
      // This is called if the user switches the view to an image.
      else if( ae.getActionCommand().equals("Image") )
      {
        // Check to see if current view is already image, if not change it.
        if( current_view != IMAGE )
	{
	  // Remove the menu items of the previous view component.
	  removeComponentMenuItems();
	  // Save colorscale of previous view component so it may be
	  // applied to the image.
	  saveColorScale();
          current_view = IMAGE;
	  // Enable the "Print Image" and "Make JPEG Image" menu items.
          JMenu file_menu = menu_bar.getMenu(0);
	  file_menu.getItem(2).setEnabled(true);
	  file_menu.getItem(3).setEnabled(true);
	  // Rebuild the display with an image.
	  buildPane();
	}
      }
      // This is called if the user switches the view to a table.
      else if( ae.getActionCommand().equals("Table") )
      {
        // Check to see if current view is already a table, if not change it.
        if( current_view != TABLE )
	{
	  // Remove the menu items of the previous view component.
	  removeComponentMenuItems();
	  // Save colorscale of previous view component so it may be
	  // applied to the thumbnail image of the table.
	  saveColorScale();
          current_view = TABLE;
	  // Disable the "Print Image" and "Make JPEG Image" menu items.
          JMenu file_menu = menu_bar.getMenu(0);
	  file_menu.getItem(2).setEnabled(false);
	  file_menu.getItem(3).setEnabled(false);
	  // Rebuild the display with a table.
	  buildPane();
	}
      }
      // Called when user selects "Print Image" menu item, only enabled if
      // view is Image.
      else if( ae.getActionCommand().equals("Print Image") )
      {
        // Since pane could be one of two things, determine which one
	// it is, then determine the image accordingly.
	Component image;
	if( pane instanceof SplitPaneWithState )
	  image = ((SplitPaneWithState)pane).getLeftComponent();
        else
	  image = pane;
	JMenuItem silent_menu = PrintComponentActionListener.getActiveMenuItem(
	                                "not visible", image );
	silent_menu.doClick();
      }
      // Called when user selects "Make JPEG Image" menu item, only enabled if
      // view is Image.
      else if( ae.getActionCommand().equals("Make JPEG Image") )
      {
        // Since pane could be one of two things, determine which one
	// it is, then determine the image accordingly.
	Component image;
	if( pane instanceof SplitPaneWithState )
	  image = ((SplitPaneWithState)pane).getLeftComponent();
        else
	  image = pane;
        JMenuItem silent_menu = SaveImageActionListener.getActiveMenuItem(
	                                "not visible", image );
	silent_menu.doClick();
      }
    }
  }
  
 /*
  * This class is needed to reajust the size of the PanViewControl. Since
  * the PanViewControl needs to resize itself once the width is known,
  * the initial bounding box restricts the size of the control.
  *
  private class ResizedControlListener extends ComponentAdapter
  {
    public void componentResized( ComponentEvent e )
    {
      Box control_box = (Box)e.getComponent();
      int height = 0;
      int width = control_box.getWidth();
      Component[] controls = control_box.getComponents();
      for( int ctrl = 0; ctrl < controls.length; ctrl++ )
      {
        height += ((JComponent)controls[ctrl]).getHeight();
      }
      height += 20; // this is to adjust for spaces in between components.
      if( control_box.getHeight() < height )
      {
        control_box.setSize( new Dimension( width, height ) );     
      }
      control_box.validate();
    }  
  }*/ 
  
 /*
  * Main program.
  */
  public static void main( String args[] )
  {
    // build my 2-D data
    int row = 200;
    int col = 180;
    float test_array[][] = new float[row][col];
    for ( int i = 0; i < row; i++ )
      for ( int j = 0; j < col; j++ )
        test_array[i][j] = i - j;
    
    // Put 2-D data into a VirtualArray2D wrapper
    IVirtualArray2D va2D = new VirtualArray2D( test_array );
    // Give meaningful range, labels, units, and linear or log display method.
    va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f, 
    		        "TestX","TestUnits", AxisInfo.LINEAR );
    va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f, 
    			"TestY","TestYUnits", AxisInfo.LINEAR );
    va2D.setAxisInfo( AxisInfo.Z_AXIS, 0f, 1f, "Z", "Units",
                      AxisInfo.PSEUDO_LOG );
    va2D.setTitle("Display2D Test");
    // Make instance of a Display2D frame, giving the array, the initial
    // view type, and whether or not to add controls.
    Display2D display = new Display2D(va2D,Display2D.TABLE,Display2D.CTRL_ALL);
    
    // Class that "correctly" draws the display.
    WindowShower shower = new WindowShower(display);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }

}
