/*
 * File:  Display1D.java
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
 * Revision 1.4  2004/05/20 03:35:50  millermi
 * - Removed unused variables and imports.
 *
 * Revision 1.3  2004/05/11 01:47:38  millermi
 * - Updated javadocs for class description.
 *
 * Revision 1.2  2004/04/21 02:39:53  millermi
 * - main() now has two functions.
 *
 * Revision 1.1  2004/04/20 05:32:19  millermi
 * - Initial Version - Allow users to view data as a graph. Be aware
 *   that some functionality with the FunctionViewComponent still
 *   needs to be worked out.
 *
 */

package gov.anl.ipns.ViewTools.Displays;

import javax.swing.*;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.text.html.HTMLEditorKit;

import gov.anl.ipns.ViewTools.UI.SplitPaneWithState;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.OneD.*;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * Simple class to display a 1-dimensional or list of 1-dimensional arrays,
 * specified by an IVirtualArrayList1D. The two common views for this display
 * are as a graph and as a table (in progress).
 */
public class Display1D extends Display
{ 
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
  * 0 - Use this int to specify display using the FunctionViewComponent.
  */ 
  public static final int GRAPH = 0;
 
 /**
  * 1 - Use this int to specify display using the TableViewComponent.
  */
  public static final int TABLE = 1;
  // many of the variables are protected in the Display base class
  private static JFrame helper = null;
  private final String PROP_FILE = System.getProperty("user.home") + 
    		                   System.getProperty("file.separator") +
		                   "Display1DProps.isv";
  
 /**
  * Construct a frame with the specified image and title
  *  
  *  @param  iva One-dimensional virtual array list.
  *  @param  view_code Code for which view component is to be used to
  *                    display the data.
  *  @param  include_ctrls If true, controls to manipulate image will be added.
  */
  public Display1D( IVirtualArrayList1D iva, int view_code, int include_ctrls )
  {
    super(iva,view_code,include_ctrls);
    setTitle("Display1D");
    addToMenubar();
    buildPane();
    loadProps(PROP_FILE);
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
    Object temp = new_state.get(VIEW_COMPONENT);
    if( temp != null )
    {
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
    if( ivc != null )
      state.insert( VIEW_COMPONENT, ivc.getObjectState(isDefault) );
    state.insert( VIEWER_SIZE, getSize() );
    return state;
  }

 /**
  * Contains/Displays control information about this viewer.
  */
  public static void help()
  {
    helper = new JFrame("Introduction to the Display1D");
    helper.setBounds(0,0,600,400);
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Description:</H1> <P>" + 
                "The Display1D (D1D) in an interactive analysis tool. " +
        	"D1D features the ability to quickly analyze a 2-D " +
		"array of data in a variety of views. Views that are " +
		"included are currently restricted to Graphs, but additional " +
		"support for a Table view will be included soon.</P>" + 
		"<H2>Commands for D1D</H2>" +
                "<P> SAVING USER PREFERENCES: Click on <B>Options|Save User " +
		"Settings</B>. Your preferences will automatically be saved " +
		"in Display1DProps.isv in your home directory. <I>This " +
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
  
 /*
  * This method takes in a virtual array and updates the image. If the array
  * is the same data array, the image is just redrawn. If the array is
  * different, the a new view component is constructed.
  *
  *  @param  values
  * 
  public void dataChanged( IVirtualArrayList1D values )
  { 
    ((IViewComponent1D)ivc).dataChanged(values);
  }*/
  
 /*
  * This method sets the pointed-at on this display. This may need to be changed
  * to allow for "pointed-at graphs" concept.
  *
  *  @param  fpt
  *
  public void setPointedAt( floatPoint2D fpt )
  {
    //set the cursor position on the view component
    ((IViewComponent1D)ivc).setPointedAt( fpt ); 
  }*/

 /*
  * This method gets the current floatPoint2D from the ViewComponent.
  *
  *  @return  The current pointed-at world coordinate point as a floatPoint2D
  *
  public floatPoint2D getPointedAt()
  {
    return ((IViewComponent1D)ivc).getPointedAt();
  }*/
 
 /* ### currently not applicable ###
  * This method creates a selected region to be displayed over the imagejpanel
  * by the selection overlay. Any previously selected regions will be erased
  * by using this method. To maintain previous selections, use
  * addSelectedRegion(). A stack could be added to allow for undo and redo
  * of selections. 
  * If null is passed as a parameter, the selections will be cleared.
  *
  *  @param  rgn - array of selected Regions
  * 
  public void setSelectedRegions( Region[] rgn ) 
  {
    ((IViewComponent2D)ivc).setSelectedRegions(rgn);
  }*/
 
 /* ### currently not applicable ###
  * Get geometric regions created using the selection overlay.
  *
  *  @return selectedregions
  *
  public Region[] getSelectedRegions()
  {
    return ((IViewComponent2D)ivc).getSelectedRegions();
  }*/

 /*
  * This method builds the content pane of the frame.
  */
  private void buildPane()
  { 
    if( current_view == GRAPH )
    {
      ivc = new FunctionViewComponent( (IVirtualArrayList1D)data );
    }
    if( current_view == TABLE )
    {
      System.out.println("Table view currently unavailable.");
    }
    ivc.addActionListener( new ViewCompListener() );    
    
    Box view_comp_controls = buildControlPanel();
    // if user wants controls, and controls exist, display them in a splitpane.
    if( add_controls == CTRL_ALL && view_comp_controls != null )
    {
      setBounds(0,0,700,485);
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
    
    // build help menu
    help.add("Help");
    help_listeners.add( new Menu2DListener() );
    help.add( display_help );
      display_help.add("Using Display1D");
      help_listeners.add( new Menu2DListener() );  // listener for D2D helper
    menu_bar.add( MenuItemMaker.makeMenuItem(options,option_listeners) );
    menu_bar.add( MenuItemMaker.makeMenuItem(help,help_listeners) );
    
    // Add keyboard shortcuts
    JMenu option_menu = menu_bar.getMenu(1);
    KeyStroke binding = 
               KeyStroke.getKeyStroke(KeyEvent.VK_U,InputEvent.ALT_MASK);
    option_menu.getItem(0).setAccelerator(binding); // Save User Settings
    JMenu help_menu = menu_bar.getMenu(2);
    binding = KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.ALT_MASK);
    help_menu.getItem(0).setAccelerator(binding);   // Help Menu
  }
  
 /*
  * This class listens to the view component.
  */
  private class ViewCompListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      sendMessage( ae.getActionCommand() );
    }
  }
  
 /*
  * This class is required to handle all messages within the Display1D.
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
      else if( ae.getActionCommand().equals("Using Display1D") )
      {
        help();
      }
    }
  } 
  
 /*
  * Main program.
  */
  public static void main( String args[] )
  {
    // build my 2-D data
    int num_x_vals = 360;
    float sin_x[] = new float[num_x_vals];
    float sin_y[] = new float[num_x_vals];
    float sin_err[] = new float[num_x_vals];
    float cos_x[] = new float[num_x_vals];
    float cos_y[] = new float[num_x_vals];
    float cos_err[] = new float[num_x_vals];
    for ( int i = 0; i < num_x_vals; i++ )
    {
      // contruct sine function
      sin_x[i] = ((float)i)*(float)Math.PI/180f;
      sin_y[i] = (float)Math.sin((double)sin_x[i]);
      sin_err[i] = ((float)i)*.001f;
      // construct cosine function
      cos_x[i] = ((float)i)*(float)Math.PI/180f;
      cos_y[i] = (float)Math.cos((double)cos_x[i]);
      cos_err[i] = sin_err[i];
    }
    DataArray1D sine_graph = new DataArray1D( sin_x, sin_y, sin_err );
    sine_graph.setTitle("Sine Function");
    DataArray1D cosine_graph = new DataArray1D( cos_x, cos_y, cos_err );
    cosine_graph.setTitle("Cosine Function");
    Vector trig = new Vector();
    trig.add(sine_graph);
    trig.add(cosine_graph);
    // Put data array into a VirtualArrayList1D wrapper
    IVirtualArrayList1D va1D = new VirtualArrayList1D( trig );
    // Give meaningful range, labels, units, and linear or log display method.
    AxisInfo info = va1D.getAxisInfo( AxisInfo.X_AXIS );
    va1D.setAxisInfo( AxisInfo.X_AXIS, info.getMin(), info.getMax(), 
    		        "Angle","Radians", true );
    info = va1D.getAxisInfo( AxisInfo.Y_AXIS );
    va1D.setAxisInfo( AxisInfo.Y_AXIS, info.getMin(), info.getMax(), 
    			"Length","Unit Length", true );
    va1D.setTitle("Sine and Cosine Function");
    // Make instance of a Display1D frame, giving the array, the initial
    // view type, and whether or not to add controls.
    Display1D display = new Display1D(va1D,Display1D.GRAPH,Display1D.CTRL_ALL);
    
    // Class that "correctly" draws the display.
    WindowShower shower = new WindowShower(display);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }

}
