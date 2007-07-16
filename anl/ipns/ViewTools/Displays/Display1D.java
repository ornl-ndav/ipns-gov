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
 * Revision 1.11  2007/07/16 22:17:20  rmikk
 * Added a View Option to determine which view is showing( table or graph)
 * Added table object states and graph object states so that their setings
 *    can be retained when switching views
 * updated the object state whenever things are changed.
 *
 * Revision 1.10  2007/07/16 14:46:31  rmikk
 * Added an object state to dictate whether the controls were shown or not
 *
 * Revision 1.9  2007/04/14 14:04:42  rmikk
 * The tickmarks and axis labels are now shown when printing the image.
 *
 * Revision 1.8  2005/05/25 20:28:44  dennis
 * Now calls convenience method WindowShower.show() to show
 * the window, instead of instantiating a WindowShower object
 * and adding it to the event queue.
 *
 * Revision 1.7  2005/03/09 23:13:02  millermi
 * - Uncommented and reimplemented dataChanged(IVirtualArrayList1D).
 *   Functionality for dataChanged() is currently untested.
 * - Edited help() to reflect new table display capabilities.
 *
 * Revision 1.6  2005/02/11 23:46:24  millermi
 * - Added private class ArrayConverter which converts an
 *   IVirtualArrayList1D to an IVirtualArray2D. This class allows
 *   graphs to be displayed in a table.
 * - Added menu items for saving and printing graphs.
 * - buildPane() now calls validate() and repaint() after rebuilding
 *   the pane.
 *
 * Revision 1.5  2004/09/15 21:58:29  millermi
 * - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *   Adding a second log required the boolean parameter to be changed
 *   to an int. These changes may affect any ObjectState saved configurations
 *   made prior to this version.
 *
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.text.html.HTMLEditorKit;

import gov.anl.ipns.ViewTools.UI.SplitPaneWithState;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.OneD.*;
import gov.anl.ipns.ViewTools.Components.TwoD.TableViewComponent;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.Util.Sys.PrintComponentActionListener;
import gov.anl.ipns.Util.Sys.SaveImageActionListener;

/**
 * Simple class to display a 1-dimensional or list of 1-dimensional arrays,
 * specified by an IVirtualArrayList1D. The two common views for this display
 * are as a graph and as a table (in progress).
 */
public class Display1D extends Display
{ 
   
   /**
    * "View Option" - This constant String is a key for referencing
    * the state information about which view component is used to display data.
    * The value this key references is of type Integer.
    */
    public static final String VIEW_OPTION           = "View Option";
    
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
  
  /**
   * "Control Option" - This constant String is a key for referencing
   * the state information about whether or not controls are displayed with
   * the view component. The value this key references is of type Integer.
   */
   public static final String CONTROL_OPTION           = "Control Option";
   
  // many of the variables are protected in the Display base class
  private static JFrame helper = null;
  private final String PROP_FILE = System.getProperty("user.home") + 
    		                   System.getProperty("file.separator") +
		                   "Display1DProps.isv";
  
  private ObjectState OState;
  
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
    OState = new ObjectState();
    addToMenubar();
    buildPane();
    JMenu file_menu= menu_bar.getMenu(0);
    PrintComponentActionListener.setUpMenuItem(file_menu,getContentPane());
    loadProps(PROP_FILE);
  }
 
  
  private void updateObjectState(){
     
     if( OState == null)
        OState = new ObjectState();
     
     if( !OState.reset( VIEW_OPTION, current_view))
        OState.insert( VIEW_OPTION, current_view);
     
     if( ivc != null)
     if( !OState.reset( VIEW_COMPONENT+current_view, ivc.getObjectState( false )));
           OState.insert( VIEW_COMPONENT+current_view, ivc.getObjectState( false ));
     
     if( ! OState.reset( CONTROL_OPTION, new Integer(add_controls)))
              OState.insert( CONTROL_OPTION, new Integer(add_controls));
     
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
  
      if( temp instanceof Integer)
         if( ((Integer)temp).intValue() != current_view)
         {    
            updateObjectState();
            removeComponentMenuItems();
            current_view = ((Integer)temp).intValue();
            buildPane();
            temp = OState.get( VIEW_COMPONENT+current_view);
            if( temp !=null)
               ivc.setObjectState( (ObjectState)temp );
            else
               OState.insert( VIEW_COMPONENT+current_view, 
                            ivc.getObjectState( false ));
            
            redraw = true;  
          }
      
     
  
    
    temp = new_state.get(VIEW_COMPONENT+current_view);
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
    
    
    temp = new_state.get(CONTROL_OPTION); 
    if( temp != null )
    {
      if( add_controls != ((Integer)temp).intValue() )
      {
        updateObjectState();
        removeComponentMenuItems();
        add_controls = ((Integer)temp).intValue();
        buildPane();
      }
      redraw = true;  
    }
    
    updateObjectState();
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
     updateObjectState();
     if( !isDefault)
        return OState;
     
    ObjectState state = new ObjectState();
    
    state.insert( VIEW_OPTION, new Integer( Display1D.GRAPH));
    
    if( ivc != null )
      state.insert( VIEW_COMPONENT+current_view, ivc.getObjectState(isDefault) );
    
    state.insert( VIEWER_SIZE, getSize() );
   
       state.insert( CONTROL_OPTION, new Integer(1) );
   
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
        	"D1D features the ability to quickly analyze a 1-D " +
		"array of data in a variety of views. Views currently " +
		"include a Graph view and a Table view.</P>" + 
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
    WindowShower.show(helper);
  }
  
 /**
  * This method takes in a virtual array and updates the image. If the array
  * is the same data array, the image is just redrawn. If the array is
  * different, the a new view component is constructed.
  * *****Functionality for dataChanged() currently untested*****
  *
  *  @param  values
  */
  public void dataChanged( IVirtualArrayList1D values )
  { 
    if( ivc instanceof FunctionViewComponent )
      ((FunctionViewComponent)ivc).dataChanged(values);
    else if( ivc instanceof TableViewComponent )
      ((TableViewComponent)ivc).dataChanged(
                                     ArrayConverter.makeInstance(values));
    
  }
  
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
    // Clear any existing views, so it can be rebuilt.
    getContentPane().removeAll();
    
    if( current_view == GRAPH )
    {
      
      // Be sure to remove any windows from other view components.
      if( ivc != null )
        ivc.kill();
      ivc = new FunctionViewComponent( (IVirtualArrayList1D)data );
      
      ObjectState st =(ObjectState)OState.get( VIEW_COMPONENT +Display1D.GRAPH);
      if( st != null)
         ivc.setObjectState( st );
    }
    else if( current_view == TABLE )
    {
       
      // Be sure to remove any windows from other view components.
      if( ivc != null )
        ivc.kill();
      ivc = new TableViewComponent( ArrayConverter.makeInstance(
                                       (IVirtualArrayList1D)data) );
      
      ObjectState st =(ObjectState)OState.get( VIEW_COMPONENT +Display1D.TABLE);
      if( st != null)
         ivc.setObjectState( st );
      
    }
    ivc.addActionListener( new ViewCompListener() );    
    
    Box view_comp_controls = buildControlPanel();
    JPanel jpHolder = new JPanel( new java.awt.GridLayout(1,1));
    jpHolder =ivc.getDisplayPanel();
    // if user wants controls, and controls exist, display them in a splitpane.
    if( add_controls == CTRL_ALL && view_comp_controls != null )
    {
      setBounds(0,0,700,485);
      pane = new SplitPaneWithState(JSplitPane.HORIZONTAL_SPLIT,
    	  			    jpHolder,
        			    view_comp_controls, .75f );
    }
    else
    {
      setBounds(0,0,500,500);
      pane = jpHolder;
    }
    getContentPane().add(pane);
    addComponentMenuItems();
    // Repaint the display, this is needed when the menu items are used
    // the switch between views.
    getContentPane().validate();
    getContentPane().repaint();
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
    Vector switch_view       = new Vector();
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
    options.add(switch_view);
      switch_view.add("View Data As...");
      switch_view.add("Graph");
      option_listeners.add( new Menu2DListener() ); // listener for user prefs
      switch_view.add("Table");
      option_listeners.add( new Menu2DListener() ); // listener for user prefs
    
    // build help menu
    help.add("Help");
    help_listeners.add( new Menu2DListener() );
    help.add( display_help );
      display_help.add("Using Display1D");
      help_listeners.add( new Menu2DListener() );  // listener for D2D helper
    menu_bar.add( MenuItemMaker.makeMenuItem(options,option_listeners) );
    menu_bar.add( MenuItemMaker.makeMenuItem(help,help_listeners) );
    
    // Add image specific menu items.
    Vector print             = new Vector();
    Vector save_graph        = new Vector();
    Vector file_listeners    = new Vector();
    
    print.add("Print Graph");
    save_graph.add("Make JPEG Graph");
    file_listeners.add( new Menu2DListener() );
   
    JMenu file_menu = menu_bar.getMenu(0);
 //   file_menu.add( MenuItemMaker.makeMenuItem( print, file_listeners ),
//		   file_menu.getItemCount() - 1 );
    
    file_menu.add( MenuItemMaker.makeMenuItem( save_graph, file_listeners ),
		   file_menu.getItemCount() - 1 );
    
    // If view is not currently an image, disable the "Print Image" and
    // "Make JPEG Image" menu items, since they are Image specific.
    if( current_view != GRAPH )
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
      String message = ae.getActionCommand();
      if( message.equals("Save User Settings") )
      {
	getObjectState(IPreserveState.DEFAULT).silentFileChooser( PROP_FILE,
	                                                          true );
      }
      else if( message.equals("Using Display1D") )
      {
        help();
      }
      else if( message.equals("Graph") )
      {
        // Check to see if current view is already graph, if not change it.
        if( current_view != GRAPH )
	{
	  // Remove the menu items of the previous view component.
	  removeComponentMenuItems();
          current_view = GRAPH;
	  // Enable the "Print Image" and "Make JPEG Image" menu items.
          JMenu file_menu = menu_bar.getMenu(0);
	  file_menu.getItem(2).setEnabled(true);
	  file_menu.getItem(3).setEnabled(true);
	  // Rebuild the display with a graph.
	  buildPane();
	}
      }
      else if( message.equals("Table") )
      {
        // Check to see if current view is already graph, if not change it.
        if( current_view != TABLE )
	{
	  // Remove the menu items of the previous view component.
	  removeComponentMenuItems();
          current_view = TABLE;
	  // Disable the "Print Image" and "Make JPEG Image" menu items.
          JMenu file_menu = menu_bar.getMenu(0);
	  file_menu.getItem(2).setEnabled(false);
	  file_menu.getItem(3).setEnabled(false);
	  // Rebuild the display with a graph.
	  buildPane();
	}
      }
      // Called when user selects "Print Graph" menu item, only enabled if
      // view is Graph.
      else if( ae.getActionCommand().equals("Print Graph") )
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
      else if( ae.getActionCommand().equals("Make JPEG Graph") )
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
  * This class will convert an IVirtualArrayList1D to an IVirtualArray2D.
  */ 
  private static class ArrayConverter extends VirtualArray2D
  {
    private IVirtualArrayList1D va1D;
    private int num_rows;
    private int num_cols;
   
   /*
    * Constructor that calls the VirtualArray2D constructor and initializes
    * the number of rows and columns.
    */ 
    private ArrayConverter( IVirtualArrayList1D oneD, int row, int col )
    {
      // Initialize super class.
      super(row,col);
      va1D = oneD;
      // Initialize values.
      num_rows = row;
      num_cols = col;
    }
    
   /*
    * Need make instance since super() needs to be called, but the number
    * of row/column first needs be calculated. Since super() must be the 
    * first call, the calculation must be done before hand.
    */ 
    public static ArrayConverter makeInstance( IVirtualArrayList1D oneD )
    {
      // Do nothing if no valid values are passed in.
      if( oneD == null )
      {
        // If invalid make a 1x1 table.
        float[] x = {0};
	float[] y = {0};	
        return new ArrayConverter(
	                 new VirtualArrayList1D( new DataArray1D(x,y) ),1,1 );
      }
      // Initialize values.
      int num_col = oneD.getNumGraphs();
      int num_row = 0;
      int temp;
      // Find the maximum number of rows from each column.
      for( int i = 0; i < num_col; i++ )
      {
        // Since number of x values >= number of y values, only need to check
	// number of x values for each graph.
        temp = oneD.getXValues(i).length;
	// find max number of rows.
	if( temp > num_row )
	  num_row = temp;
      }
      // Assume x and y values will be shown, number of columns is twice
      // as much as the number of graphs.
      num_col = num_col*2;
    
      return new ArrayConverter( oneD, num_row, num_col );
    }
    
    public String getTitle()
    {
      return va1D.getTitle();
    }
    
    public void setTitle( String title )
    {
      va1D.setTitle(title);
    }
    
    public AxisInfo getAxisInfo( int axis )
    {
      return va1D.getAxisInfo(axis);
    }
    
    public float getDataValue( int row, int col )
    {
      // If not a valid row or column, return NaN
      if( !( isValidRow(row) && isValidColumn(col) ) )
        return Float.NaN;
      // Find the graph number corresponding to the column. Since there are
      // two columns for each graph, take half of the column value.
      int graph_num = (int)Math.floor(((double)col)/2d);
      if( graph_num < 0 )
        return Float.NaN;
      float[] col_array;
      // if even, get x values, if odd, get y values.
      if( graph_num*2 == col )
        col_array = va1D.getXValues(graph_num);
      else
        col_array = va1D.getYValues(graph_num);
      // If selected column is longer than the row value asked for, give
      // the row value in the column.
      if( col_array.length > row )
        return col_array[row];
      // If the row requested was outside the length of the column, return NaN.
      return Float.NaN;
    }
    
    public float[] getColumnValues( int col, int from, int to )
    {
      float[] values = new float[Math.abs(to-from)+1];
      int row_min = from;
      int row_max = to;
      if( row_min > row_max )
      {
        int temp = row_min;
	row_min = row_max;
	row_max = temp;
      }
      for( int i = row_min; i < row_max+1; i++ )
        values[i] = getDataValue( i, col );
      return values;
    }
    
    public float[] getRowValues( int row, int from, int to )
    {
      float[] values = new float[Math.abs(to-from)+1];
      int col_min = from;
      int col_max = to;
      if( col_min > col_max )
      {
        int temp = col_min;
	col_min = col_max;
	col_max = temp;
      }
      for( int i = col_min; i < col_max+1; i++ )
        values[i] = getDataValue( row, i );
      return values;
    }
    /* -------------------------------- Stubs ------------------------------- */
    public void setDataValue( int row, int col )
    {
      ; // do nothing
    }
    
    public float[][] getErrors()
    {
      return null;
    }
    
    public float getErrorValue( int row, int col )
    {
      return Float.NaN;
    }
    
    public void setSquareRootErrors( boolean isSet )
    {
      ; // do nothing
    }
    
    public void setAllValues(float value)
    {
      ; // do nothing
    }
    
    public void setColumnValues( int col, int from, int to )
    {
      ; // do nothing
    }
    
    public void setRowValues( int row, int from, int to )
    {
      ; // do nothing
    }
    
    public void setRegionValues( float[][] values, int row, int col )
    {
      ; // do nothing
    }
    
    public void setAxisInfo( int axiscode, float min, float max,
                             String label, String units, int scale )
    {
      ; // do nothing
    }
    
    public void setAxisInfo(int axiscode, AxisInfo info)
    {
      ; // do nothing
    }
    
    private boolean isValidRow(int row)
    {
      if( row < 0 || row >= num_rows )
        return false;
      return true;
    }
    
    private boolean isValidColumn(int col)
    {
      if( col < 0 || col >= num_cols )
        return false;
      return true;
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
    		        "Angle","Radians", AxisInfo.LINEAR );
    info = va1D.getAxisInfo( AxisInfo.Y_AXIS );
    va1D.setAxisInfo( AxisInfo.Y_AXIS, info.getMin(), info.getMax(), 
    			"Length","Unit Length", AxisInfo.LINEAR );
    va1D.setTitle("Sine and Cosine Function");
    // Make instance of a Display1D frame, giving the array, the initial
    // view type, and whether or not to add controls.
    Display1D display = new Display1D(va1D,Display1D.GRAPH,Display1D.CTRL_ALL);
    
    // Class that "correctly" draws the display.
    WindowShower.show(display);
  }

}
