/*
 * File:  SANDWedgeViewer.java
 *
 * Copyright (C) 2003-2004, Mike Miller
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
 * Revision 1.18  2004/01/24 02:55:26  millermi
 * - Added File|Save Results menu item. This allows the
 *   results from the SWV to be neatly written out to file.
 * - Results window is now has toFront() called on it when
 *   a new selection is added.
 * - Moved field labels out of SANDEditor and into whole
 *   class. This allows them to be used when writing out
 *   the file.
 *
 * Revision 1.17  2004/01/23 22:59:12  millermi
 * - Results window now automatically popped up when a selection
 *   is made. The menu item previously used to show the results
 *   now will read "Hide Results Window" when the window is
 *   visible and "Show Results Window" when the window is not.
 * - Changed how the distance was calculated. Now goes from center of
 *   image, not center of Region.
 * - Removed all references to the FunctionViewComponent, now housed
 *   in the ViewManager.
 * - Changed label from "Width of Angle" to "Interior Angle" for
 *   Wedge and DoubleWedge field labels (in SANDEditor).
 *
 * Revision 1.16  2004/01/20 00:59:59  millermi
 * - Since Save is not yet implemented, added information in
 *   the help menu to help users find the save option.
 *
 * Revision 1.15  2004/01/19 23:38:43  millermi
 * - Fixed bug that caused file to be read into the array incorrectly.
 *   Now the first element is read into array[NUM_ROWS-1][0].
 * - Now checks if data exceeds array bounds, this prevents
 *   ARRAY_OUT_OF_BOUNDS exception.
 *
 * Revision 1.14  2004/01/09 21:08:17  dennis
 * Added cacluation of error estimates.
 *
 * Revision 1.13  2004/01/08 22:26:03  millermi
 * - Separated off the FunctionViewComponent. Now use "View Results"
 *   under the options menu to view a ViewManager with all of the
 *   old views, including the graph and table view.
 * - The ViewManager is always initialized with the graph view
 *   displaying the results.
 * - More testing needs to be done on this version...Please test and
 *   give feedback.
 *
 * Revision 1.12  2004/01/08 21:07:33  millermi
 * - Fixed bug introduced when world coord conversion took place.
 *
 * Revision 1.11  2004/01/08 20:14:46  millermi
 * - Made viewing selection info available.
 * - Selection defining points stored in attributes now
 *   converted to world coords.
 * - Expanded bounds of the SAND Editor.
 * - Known bug: Graphs corresponding to selections no longer
 *   disappear when the selection is removed.
 *
 * Revision 1.10  2004/01/07 06:47:39  millermi
 * - New float Region parameters have been updated.
 *
 * Revision 1.9  2004/01/06 20:28:16  dennis
 * Fixed some problems with labels.
 * Now displays intensity vs Q graph.
 * Some problems remain with the graph region and log axes.
 *
 * Revision 1.8  2004/01/03 04:40:23  millermi
 * - help() now uses html toolkit
 * - replaced setVisible(true) with WindowShower.
 * - Added code for world-to-image transform, however transform
 *   is not currently being used.
 *
 * Revision 1.7  2003/12/30 00:39:37  millermi
 * - Added Annular selection capabilities.
 * - Changed SelectionJPanel.CIRCLE to SelectionJPanel.ELLIPSE
 *
 * Revision 1.6  2003/12/29 07:54:31  millermi
 * - Added editor which enables user to make a selection
 *   by entering defining characteristics.
 * - ***Editing still unavailable*** This editor will
 *   only work for creating new selections. The next
 *   version should contain the ability to edit selections.
 *
 * Revision 1.5  2003/12/23 02:21:36  millermi
 * - Added methods and functionality to allow enabling/disabling
 *   of selections.
 * - Fixed interface package changes where applicable.
 *
 * Revision 1.4  2003/12/20 22:18:49  millermi
 * - Orphaned windows are now disposed when setData() is called.
 * - Added simple help() for new users. More detail needed.
 *
 * Revision 1.3  2003/12/20 11:11:52  millermi
 * - Introduced DataSet concept. Coordinated the
 *   ImageViewComponent with the FunctionViewComponent.
 *   THE VIEWER IS NOW FUNCTIONABLE!!!
 * - Known bug: PanViewControl causes layout issues because
 *   it calculated preferredSize on the fly.
 *
 * Revision 1.2  2003/12/20 03:55:43  millermi
 * - Fixed javadocs error.
 *
 * Revision 1.1  2003/12/19 02:20:09  millermi
 * - Initial Version - Adds specialized functionality to the
 *   IVCTester class. This class allows users to specifically
 *   view SAND data files.
 * - Currently, the FunctionViewComponent is unavailable.
 *   Once this is added, calculations on selected regions will
 *   be possible.
 *
 */

package DataSetTools.components.View;

import javax.swing.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.io.IOException;
import java.io.EOFException;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLEditorKit;

import DataSetTools.components.image.*;
import DataSetTools.components.containers.SplitPaneWithState;
import DataSetTools.components.View.TwoD.ImageViewComponent;
import DataSetTools.components.View.Menu.MenuItemMaker;
import DataSetTools.components.View.Menu.ViewMenuItem;
import DataSetTools.components.View.Transparency.SelectionOverlay;
import DataSetTools.components.View.Region.*;
import DataSetTools.components.View.Cursor.SelectionJPanel;
import DataSetTools.components.View.ViewControls.CursorOutputControl;
import DataSetTools.components.View.ViewControls.FieldEntryControl;
import DataSetTools.components.View.ViewControls.PanViewControl;
import DataSetTools.util.TextFileReader;
import DataSetTools.util.RobustFileFilter;
import DataSetTools.util.floatPoint2D;
import DataSetTools.util.SharedData;
import DataSetTools.util.WindowShower;
import DataSetTools.util.Format;
import DataSetTools.util.IObserver;
import DataSetTools.util.FontUtil;
import DataSetTools.viewer.IViewManager;
import DataSetTools.viewer.ViewManager;
import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.Data;
import DataSetTools.dataset.FunctionTable;
import DataSetTools.dataset.UniformXScale;
import DataSetTools.dataset.Float1DAttribute;

/**
 * Simple class to display an image, specified by an IVirtualArray2D or a 
 * 2D array of floats, in a frame. This class adds further implementation to
 * the ImageFrame2.java class for thorough testing of the ImageViewComponent.
 */
public class SANDWedgeViewer extends JFrame implements IPreserveState,
                                                       Serializable
{
 /**
  * "ImageViewComponent" - This constant String is a key for referencing
  * the state information about the ImageViewComponent. Since the
  * ImageViewComponent has its own state, this value is of type ObjectState,
  * and contains the state of the ImageViewComponent. 
  */
  public static final String IMAGE_VIEW_COMPONENT    = "ImageViewComponent";
  
 /**
  * "ViewerSize" - This constant String is a key for referencing
  * the state information about the size of the viewer at the time
  * the state was saved. The value this key references is of type
  * Dimension.
  */
  public static final String VIEWER_SIZE           = "Viewer Size"; 
  
 /**
  * "Data Directory" - This constant String is a key for referencing
  * the state information about the location of the data files being
  * loaded by this viewer. The value this key references is of type
  * String.
  */
  public static final String DATA_DIRECTORY        = "Data Directory";
  
  private static JFrame helper = null;
  
  // complete viewer, includes controls and ijp
  private transient SplitPaneWithState pane;
  private transient ImageViewComponent ivc;
  private transient IVirtualArray2D data;
  private transient float[][]       errors;      // error estimates in the data
  private transient JMenuBar menu_bar;
  private transient DataSet data_set;
  private String projectsDirectory = SharedData.getProperty("Data_Directory");
  private transient SANDEditor editor;
  private transient CoordTransform image_to_world_tran = new CoordTransform();
  private transient ViewManager oldview;
  private transient SANDWedgeViewer this_viewer;
  private String[] ellipselabels;
  private String[] wedgelabels;
  private String[] ringlabels;

 /**
  * Construct a frame with no data to start with. This constructor will be
  * used when data is being loaded in.
  */
  public SANDWedgeViewer()
  {
    init(null,null);
  }

 /**
  * Construct a frame with the specified image and title
  *  
  *  @param  iva
  */
  public SANDWedgeViewer( IVirtualArray2D iva, float[][] err_array )
  {
    init(iva, err_array);
  }

 /**
  * Construct a frame with the specified image and title
  *  
  *  @param  array
  *  @param  xinfo
  *  @param  yinfo
  *  @param  title
  */  
  public SANDWedgeViewer( float[][] array, 
                          float[][] err_array,
                          AxisInfo xinfo,
		          AxisInfo yinfo,
		          String title )
  {
    VirtualArray2D temp = new VirtualArray2D( array );
    temp.setAxisInfo( AxisInfo.X_AXIS, xinfo.copy() );
    temp.setAxisInfo( AxisInfo.Y_AXIS, yinfo.copy() );
    temp.setTitle(title);
    
    init(temp, err_array);
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
    Object temp = new_state.get(IMAGE_VIEW_COMPONENT);
    if( temp != null )
    {
      ivc.setObjectState( (ObjectState)temp );
      redraw = true;  
    } 
    
    temp = new_state.get(VIEWER_SIZE); 
    if( temp != null )
    {
      setSize( (Dimension)temp );
      redraw = true;  
    } 
    
    temp = new_state.get(DATA_DIRECTORY); 
    if( temp != null )
    {
      projectsDirectory = (String)temp;
    } 
    
    if( redraw )
      repaint();
  }
 
 /**
  * This method returns the current ObjectState of this viewer.
  *
  *  @return The current ObjectState of this viewer.
  */ 
  public ObjectState getObjectState()
  {
    ObjectState state = new ObjectState();
    state.insert( IMAGE_VIEW_COMPONENT, ivc.getObjectState() );
    state.insert( VIEWER_SIZE, getSize() );
    state.insert( DATA_DIRECTORY, new String(projectsDirectory) );
    
    return state;
  }

 /**
  * Contains/Displays control information about this viewer.
  */
  public static void help()
  {
    helper = new JFrame("Introduction to the SAND Wedge Viewer");
    helper.setBounds(0,0,600,400);
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Description:</H1> <P>" + 
                "The SAND Wedge Viewer (SWV) in an interactive analysis tool." +
        	" SWV features the ability to make four selections: Wedge, " +
    		"Double Wedge, Annular, and Ellipse. Once a " +
    		"selection is made on the image, the graph will display " +
		"the intensity values per hit as a function of distance " +
		"in Q. Selections can be made in two ways: <BR>" +
		"1. Graphically using the SelectionOverlay<BR>" +
		"2. Entering defining information by pressing the Manual " +
		"Selection button.</P>" + 
		"<H2>Commands for SWV</H2>" +
                "<P> <I>ATTENTION: Selections must be made before using the " +
		"viewing or saving results to file. </I><BR><BR> " +
		"VIEW RESULTS: The Results window will automatically appear " +
		"after a selection has been made. <B>Options|Hide Results " +
		"Window</B> will hide the window. If the window is not " +
		"visible, <B>Options|Show Results Window</B> will cause the " +
		"results window to appear.<BR>"+
		"SAVE RESULTS TO FILE: Go to <B>File|Save Results</B> " +
		"in the SWV. The new file has 3 columns: Q, Intensity, " +
		"and Error Bounds. Information about the region is listed " +
		"at the top of the file, precluded by a pound symbol(#). " +
		"<I>If multiple selections are made, only the last " +
		"selection can be written to file.</I><BR>" +
		"<BR>Note:<BR>" +
        	"Detailed commands for each overlay can be found under " +
		"<B>Help|Overlays</B> after a data file has been loaded.</P>";
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
  * This method loads a 200 x 200 data array from the file specified.
  *
  *  @param  filename Filename of the data file being loaded.
  */ 
  public void loadData( String filename )
  {
    int NUM_ROWS = 200;
    int NUM_COLS = 200;
    float[][] array     = new float[NUM_ROWS][NUM_COLS];
    float[][] err_array = new float[NUM_ROWS][NUM_COLS];
    float qxmin = 0;
    float qymin = 0;
    float qxmax = 0;
    float qymax = 0;
    // try to open the file
    try
    {
      int row = NUM_ROWS - 1;
      int col = 0;
      // file arranged in 4 columns: Qx, Qy, Value, Error
      // Since we only care about the min and max of Qx, Qy, only the first
      // and last values in those columns are saved. The Value and Error columns
      // are each stored in a separate 2-D array.
      TextFileReader reader = new TextFileReader( filename );
      // read in first line, this will set the Qx/Qy min and read in
      // the first value.
      StringTokenizer datarow = new StringTokenizer(reader.read_line());
      qxmin = (new Float( datarow.nextToken() )).floatValue();
      qymin = (new Float( datarow.nextToken() )).floatValue();
      array[row][col]     = (new Float( datarow.nextToken() )).floatValue();
      err_array[row][col] = (new Float( datarow.nextToken() )).floatValue();
    
      row--;  // decrement row since first element was read in.
      // let the exception EOF end the loop.
      while( true )
      {
	// now read in the data one row at a time, each row contains a
	// Qx, Qy, Value, and Error value. Store the Values in the array
	// by column instead of row, starting at lower left-hand corner of
	// array, and ending in upper right-hand corner.
        datarow = new StringTokenizer(reader.read_line());
        qxmax = (new Float( datarow.nextToken() )).floatValue();
        qymax = (new Float( datarow.nextToken() )).floatValue();
        array[row][col] = (new Float( datarow.nextToken() )).floatValue();
        err_array[row][col] = (new Float( datarow.nextToken() )).floatValue();
	//System.out.println("Row/Col: (" + row + "," + col + ")" );
	
	// increment column if at last row, reset row to start.
	// this will cause the numbers to be read in by column
	if( row == 0 )
        {
          row = NUM_ROWS - 1;
	  col++;
        }
	// increment rows so data is read in by column
        else
          row--;
	// if file is larger than 200x200, artificially throw the EOF exception
        if( col == NUM_COLS )
	  throw new IOException("End of file");
      } // end while
    }
    // either end of file or no file found
    catch( IOException e1 )
    {
      // done reading file
      if( e1.getMessage().equals("End of file") )
      {
	// set the source of the image_to_world transform
        // y min/max are swapped since IVC swaps them.
        image_to_world_tran.setDestination( qxmin, qymax, qxmax, qymin );
	image_to_world_tran.setSource( 0.001f, 0.001f, array[0].length-0.001f,
				       array.length-0.001f );
        VirtualArray2D va2D = new VirtualArray2D( array );
        va2D.setAxisInfo( AxisInfo.X_AXIS, qxmin, qxmax, 
    		            "Qx","(Inverse Angstroms)", true );
        va2D.setAxisInfo( AxisInfo.Y_AXIS, qymin, qymax, 
    			    "Qy","(Inverse Angstroms)", true );
        // since datamin/max are gotten from the image,
	// the min/max are dummy values.
	va2D.setAxisInfo( AxisInfo.Z_AXIS, 0, 1, 
    			    "","Intensity", true );
        va2D.setTitle("SAND Wedge Viewer");
	setData( va2D, err_array );
      }
      // no file to be read, display file not found on empty jpanel.
      else
      {
        VirtualArray2D nullarray = null;
        this.setData(nullarray,null);
        ((JComponent)pane.getLeftComponent()).add( 
	                                       new JLabel("File Not Found") );
        validate();
        repaint();
      }
    }
  }
  
 /**
  * This method takes in a virtual array and updates the image. If the array
  * is the same size as the previous data array, the image is just redrawn.
  * If the size is different, the frame is disposed and a new view component
  * is constructed.
  *
  *  @param  values
  */ 
  public void setData( IVirtualArray2D values, float[][] err_array )
  {
    // since data is changing, kill all windows created by ivc.
    // If they are null, no windows were made.
    if( ivc != null )
      ivc.kill();
    
    // if new array is same size as old array
    if( values != null && data != null &&
        ( values.getNumRows() == data.getNumRows() &&
          values.getNumColumns() == data.getNumColumns() ) )
    {  
      data   = values;
      errors = err_array;
      ivc.setSelectedRegions(null);
      ivc.dataChanged(data);
      integrate(null);
    }  
    // if different sized array, remove everything and build again.
    else
    { 
      data   = values;
      errors = err_array;
      getContentPane().removeAll();
      buildMenubar();
      buildPane();
      getContentPane().add(pane);
      validate();
      repaint();
    }
  }
  
 /**
  * This method takes in a 2D array and updates the image. If the array
  * is the same size as the previous data array, the image is just redrawn.
  * If the size is different, the frame is disposed and a new view component
  * is constructed.
  *
  *  @param  array
  */ 
  public void setData( float[][] array, float[][] err_array )
  {
    setData( new VirtualArray2D(array), err_array );
  }
 
 /**
  * This method sets the directory where data files can be found.
  */ 
  public void setDataDirectory( String path )
  {
    projectsDirectory = path;
  }
  
 /*
  * The init function gathers the common functionality between the constructors
  * so that the code does not have to exist in 3 spots. This will build the
  * niceties of the viewer.
  */ 
  private void init( IVirtualArray2D iva, float[][] err_array )
  {
    this_viewer = this;
    setTitle("SAND Wedge Viewer");
    ellipselabels = new String[]{"X Center", "Y Center",
                                 "X Radius", "Y Radius"};
    wedgelabels = new String[]{"X Center", "Y Center", "Radius",
                   "Wedge Axis Angle", "Interior Angle"};
    ringlabels = new String[]{"X Center", "Y Center",
                             "Inner Radius", "Outer Radius"};
    editor = new SANDEditor();
    if( iva != null )
    {
      AxisInfo xinfo = iva.getAxisInfo( AxisInfo.X_AXIS );
      AxisInfo yinfo = iva.getAxisInfo( AxisInfo.Y_AXIS );
      // y min/max are swapped since IVC swaps them.
      image_to_world_tran.setDestination( new CoordBounds( xinfo.getMin(),
						           yinfo.getMax(),      
						           xinfo.getMax(),
						           yinfo.getMin() ) );
      image_to_world_tran.setSource( 0.001f, 0.001f,
                                     iva.getNumColumns()-0.001f,
				     iva.getNumRows()-0.001f );
    }
    data = new VirtualArray2D(1,1);
    buildMenubar();
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(0,0,700,485);
    data_set   = new DataSet("Intensity vs Q in Region", 
                             "Calculated Intensity vs Q in Region");

    data_set.setX_units("(Inverse Angstroms)" );
    data_set.setX_label("Q" );

    data_set.setY_units("" );
    data_set.setY_label("Relative Intensity" );
    if( data_set.getNum_entries() > 0 )
      data_set.setSelectFlag( data_set.getNum_entries() - 1, true );
    setData(iva, err_array);
  }

 /*
  * This method builds the content pane of the frame.
  */
  private void buildPane()
  { 
    if( data != null )
    {
      ivc = new ImageViewComponent( data );
      // since box, point, and line selections don't apply, disable them.
      String[] disSelect = { SelectionJPanel.BOX,
                             SelectionJPanel.POINT,
			     SelectionJPanel.LINE };
      ivc.disableSelection( disSelect );
      ivc.setColorControlEast(true);
      //ivc.preserveAspectRatio(true);
      ivc.addActionListener( new ImageListener() );    
      Box componentholder = new Box(BoxLayout.Y_AXIS);
      componentholder.add( ivc.getDisplayPanel() );
      pane = new SplitPaneWithState(JSplitPane.HORIZONTAL_SPLIT,
    	  			    componentholder,
        			    buildControls(), .75f );
      // get menu items from view component and place it in a menu
      ViewMenuItem[] menus = ivc.getSharedMenuItems();
      for( int i = 0; i < menus.length; i++ )
      {
        if( ViewMenuItem.PUT_IN_FILE.toLowerCase().equals(
    	    menus[i].getPath().toLowerCase()) )
    	{
	  menu_bar.getMenu(0).add( menus[i].getItem() ); 
        }
	else if( ViewMenuItem.PUT_IN_OPTIONS.toLowerCase().equals(
    	         menus[i].getPath().toLowerCase()) )
    	{
	  menu_bar.getMenu(1).add( menus[i].getItem() );	   
        }
	else if( ViewMenuItem.PUT_IN_HELP.toLowerCase().equals(
    	         menus[i].getPath().toLowerCase()) )
        {
	  menu_bar.getMenu(2).add( menus[i].getItem() );
        }
      }
    }
    // no data, build an empty split pane.
    else
    {
      pane = new SplitPaneWithState(JSplitPane.HORIZONTAL_SPLIT,
                                    new JPanel(), new JPanel(), .75f );
    }	   
  }
 
 /*
  * This private method will (re)build the menubar. This is necessary since
  * the ImageViewComponent could add menu items to the Menubar.
  * If the file being loaded is not found, those menu items
  * must be removed. To do so, rebuild the Menubar.
  */ 
  private void buildMenubar()
  {    
    setJMenuBar(null);
    menu_bar = new JMenuBar();
    setJMenuBar(menu_bar);
    
    Vector file      = new Vector();
    Vector options   = new Vector();
    Vector help      = new Vector();
    Vector save_results_menu = new Vector();
    Vector view_man  = new Vector();
    Vector save_menu = new Vector();
    Vector load_menu = new Vector();
    Vector load_data = new Vector();
    Vector swv_help  = new Vector();
    Vector file_listeners = new Vector();
    Vector option_listeners = new Vector();
    Vector help_listeners = new Vector();
    file.add("File");
    file_listeners.add( new WVListener() ); // listener for file
    file.add(load_data);
      load_data.add("Load Data");
      file_listeners.add( new WVListener() ); // listener for load data
    file.add(save_results_menu);
      save_results_menu.add("Save Results");
      file_listeners.add( new WVListener() ); // listener for saving results
    
    options.add("Options");
    option_listeners.add( new WVListener() ); // listener for options
    options.add(view_man);
      view_man.add("Hide Results Window");
      option_listeners.add( new WVListener() ); // listener for view results
    options.add(save_menu);
      save_menu.add("Save State");
      option_listeners.add( new WVListener() ); // listener for save state
    options.add(load_menu);
      load_menu.add("Load State");
      option_listeners.add( new WVListener() ); // listener for load state
    help.add("Help");
    help_listeners.add( new WVListener() );
    help.add( swv_help );
      swv_help.add("SAND Wedge Viewer");
      help_listeners.add( new WVListener() );  // listener for SAND helper
    
    menu_bar.add( MenuItemMaker.makeMenuItem(file,file_listeners) ); 
    menu_bar.add( MenuItemMaker.makeMenuItem(options,option_listeners) );
    menu_bar.add( MenuItemMaker.makeMenuItem(help,help_listeners) );
    // since the IVC is not created unless data is available,
    // do not load state unless data is available.
    if( data == null )
    {
      JMenu option_menu = menu_bar.getMenu(1);
      option_menu.getItem(0).setEnabled(false);
      option_menu.getItem(1).setEnabled(false);
      option_menu.getItem(2).setEnabled(false);
    }
    // if ViewManager not visible, disable "Hide Results Window" button
    if( oldview == null || !oldview.isVisible() )
    {
      menu_bar.getMenu(1).getItem(0).setEnabled(false);
    }
  }
  
 /*
  * build controls for both view components.
  */ 
  private Box buildControls()
  {
    // box that contains all of the controls.
    Box controls = new Box(BoxLayout.Y_AXIS);
    // add button to open sand editor
    JPanel sand_controls = new JPanel(); 
    TitledBorder sand_border = 
    		     new TitledBorder(LineBorder.createBlackLineBorder(),
        			      "SAND Selection Controls");
    sand_border.setTitleFont( FontUtil.BORDER_FONT );
    sand_controls.setBorder( sand_border );
    JButton createbutton = new JButton("Manual Selection");
    createbutton.addActionListener( new WVListener() );
    sand_controls.add(createbutton);
    controls.add(sand_controls);

    // add imageviewcomponent controls
    Box ivc_controls = new Box(BoxLayout.Y_AXIS);
    TitledBorder ivc_border = 
    		     new TitledBorder(LineBorder.createBlackLineBorder(),
        			      "Image Controls");
    ivc_border.setTitleFont( FontUtil.BORDER_FONT ); 
    ivc_controls.setBorder( ivc_border );
    JComponent[] ivc_ctrl = ivc.getSharedControls();
    for( int i = 0; i < ivc_ctrl.length; i++ )
    {
      ivc_controls.add(ivc_ctrl[i]);
    }
    // if resized, adjust container size for the pan view control.
    ivc_controls.addComponentListener( new ResizedControlListener() );
    if( ivc_ctrl.length != 0 )
    {
      controls.add(ivc_controls);
    }
    
    // add spacer between ivc controls
    JPanel spacer = new JPanel();
    spacer.setPreferredSize( new Dimension(0, 10000) );
    controls.add(spacer);
    return controls;
  }

 /*
  *                 **************Integrate*******************
  * This method does the calculations that produce the graph.
  */
  private void integrate( Region region )
  {   
    int   ID      = 1;
    float start_x = 0;
    float end_x   = 0;
    floatPoint2D center = new floatPoint2D(); 
    floatPoint2D  start_point = null,
                  end_point   = null;
    int n_xvals;
    String attribute_name = "";
    float[] attributes;
    // Attributes for wedge/double wedge.
    // 0. Type of selection (Static ints at top of this file)
    // 1. X axis Center position, in world coordinates
    // 2. Y axis Center position, in world coordinates
    // 3. Radius of wedge, in world coordinates
    // 4. Angle of the wedge axis, in degrees.
    // 5. Width of wedge, in degrees.
    // *** for Ellipse, Att. 3 is x radius, Att. 4 is y radius.***
    if( region == null )
    {
      return;
    }
    floatPoint2D[] def_pts = region.getDefiningPoints();
    if( region instanceof WedgeRegion )
    {
     /* def_pts[0]   = center pt of circle that arc is taken from
      * def_pts[1]   = last mouse point/point at intersection of line and arc
      * def_pts[2]   = reflection of p[1]
      * def_pts[3]   = top left corner of bounding box around arc's total circle
      * def_pts[4]   = bottom right corner of bounding box around arc's circle
      * def_pts[5].x = startangle, the directional vector in degrees
      * def_pts[5].y = degrees covered by arc.
      */
      attributes = new float[5];
      center = new floatPoint2D( def_pts[0] );
      end_x = def_pts[4].x - center.x;
      // build attributes list
      attribute_name = SelectionJPanel.WEDGE;
      float axisangle = def_pts[5].x + def_pts[5].y/2f;
      if( axisangle >= 360 )
        axisangle -= 360;
      // tranform from image to world coords
      floatPoint2D wc_center = image_to_world_tran.MapTo(center);
      float radius = image_to_world_tran.MapXTo(def_pts[4].x) - wc_center.x;
      // keep radius positive.
      radius = Math.abs(radius);
      // round number to 5 digits.
      attributes[0] = (float)Format.round((double)wc_center.x, 5);
      attributes[1] = (float)Format.round((double)wc_center.y, 5);
      attributes[2] = (float)Format.round((double)radius, 5);
      attributes[3] = axisangle;
      attributes[4] = def_pts[5].y;
      end_point = new floatPoint2D( def_pts[1].x, def_pts[1].y );
    }
    else if( region instanceof DoubleWedgeRegion )
    {
     /* def_pts[0]   = center pt of circle that arc is taken from
      * def_pts[1]   = last mouse point/point at intersection of line and arc
      * def_pts[2]   = reflection of p[1]
      * def_pts[3]   = top left corner of bounding box around arc's total circle
      * def_pts[4]   = bottom right corner of bounding box around arc's circle
      * def_pts[5].x = startangle, the directional vector in degrees
      * def_pts[5].y = degrees covered by arc.
      */
      attributes = new float[5];
      center = new floatPoint2D( def_pts[0] );
      end_x = def_pts[4].x - center.x;
      // build attributes list
      attribute_name = SelectionJPanel.DOUBLE_WEDGE;
      float axisangle = def_pts[5].x + def_pts[5].y/2f;
      if( axisangle >= 360 )
        axisangle -= 360;
      // tranform from image to world coords
      floatPoint2D wc_center = image_to_world_tran.MapTo(center);
      float radius = image_to_world_tran.MapXTo(def_pts[4].x) - wc_center.x;
      // keep radius positive.
      radius = Math.abs(radius);
      // round number to 5 digits.
      attributes[0] = (float)Format.round((double)wc_center.x, 5);
      attributes[1] = (float)Format.round((double)wc_center.y, 5);
      attributes[2] = (float)Format.round((double)radius, 5);
      attributes[3] = axisangle;
      attributes[4] = def_pts[5].y;
      end_point = new floatPoint2D( def_pts[1].x, def_pts[1].y );
    }
    else if( region instanceof EllipseRegion )
    {
     /* def_pts[0]   = top left corner of bounding box around ellipse
      * def_pts[1]   = bottom right corner of bounding box around ellipse
      * def_pts[2]   = center pt of ellipse
      */
      attributes = new float[4];
      center = new floatPoint2D( def_pts[2] );
      end_x = def_pts[1].x - center.x;
      // build attributes list
      attribute_name = SelectionJPanel.ELLIPSE;
      // tranform from image to world coords
      floatPoint2D wc_center = image_to_world_tran.MapTo(center);
      float major_radius = image_to_world_tran.MapXTo(def_pts[1].x) - 
                           wc_center.x;
      float minor_radius = image_to_world_tran.MapYTo(def_pts[1].y) - 
                           wc_center.y;
      // keep radii positive.
      major_radius = Math.abs(major_radius);
      minor_radius = Math.abs(minor_radius);
      // round number to 5 digits.
      attributes[0] = (float)Format.round((double)wc_center.x, 5);
      attributes[1] = (float)Format.round((double)wc_center.y, 5);
      attributes[2] = (float)Format.round((double)major_radius, 5);
      attributes[3] = (float)Format.round((double)minor_radius, 5);
      float image_radius = def_pts[1].x - center.x;
                                      // The following end_point calculation
                                      // may need to be changed ###############
      end_point = new floatPoint2D( center.x + image_radius, center.y );
    }
    else if( region instanceof AnnularRegion )
    {
     /*
      * def_pts[0]   = center pt of circle that arc is taken from
      * def_pts[1]   = top left corner of bounding box of inner circle
      * def_pts[2]   = bottom right corner of bounding box of inner circle
      * def_pts[3]   = top left corner of bounding box of outer circle
      * def_pts[4]   = bottom right corner of bounding box of outer circle
      */
      attributes = new float[4];
      center = new floatPoint2D( def_pts[0] );
      end_x = def_pts[4].x - center.x;
      // build attributes list
      attribute_name = SelectionJPanel.RING;
      // tranform from image to world coords
      floatPoint2D wc_center = image_to_world_tran.MapTo(center);
      float inner_radius = image_to_world_tran.MapXTo(def_pts[2].x) -
                           wc_center.x;
      float outer_radius = image_to_world_tran.MapYTo(def_pts[4].x) -
                           wc_center.x;
      // keep radii positive.
      inner_radius = Math.abs(inner_radius);
      outer_radius = Math.abs(outer_radius);
      // round number to 5 digits.
      attributes[0] = (float)Format.round((double)wc_center.x, 5);
      attributes[1] = (float)Format.round((double)wc_center.y, 5);
      attributes[2] = (float)Format.round((double)inner_radius, 5);
      attributes[3] = (float)Format.round((double)outer_radius, 5);
      float image_radius = def_pts[4].x - center.x;
                                      // The following end_point calculation
                                      // needs to be changed ###############
      end_point = new floatPoint2D( center.x + image_radius, center.y );
    }
    // should never get to this else, we have an invalid Region.
    else
    {
      return;
    }

    UniformXScale x_scale;      // "time channels" for the spectrum

    // build list of Q bin centers, with one bin for each "pixel" in the radius
    n_xvals = Math.round(end_x);
    x_scale = new UniformXScale( start_x, end_x, n_xvals );

    int hit_count[]  = new int[n_xvals];
    float y_vals[]   = new float[n_xvals];
    float err_vals[] = new float[n_xvals];
    float err;
    float x_vals[] = x_scale.getXs();
    Point[] selected_pts = region.getSelectedPoints();
    // this loop will sum up values at the same distance and count the number
    // of hits at each distance.
    // NOTE: The distance calculation is done in image row/col values, not
    // in world coord values.
    // Map world coord origin to image origin for magnitude calculation
    floatPoint2D image_origin = 
                        image_to_world_tran.MapFrom( new floatPoint2D(0,0) );
    float x = 0;
    float y = 0;
    float dist = 0;
    int index = 0;
    for ( int i = 0; i < selected_pts.length; i++ )
    {
      x = Math.abs( selected_pts[i].x - image_origin.x );
      y = Math.abs( selected_pts[i].y - image_origin.y );
      dist = (float)Math.sqrt( x*x + y*y );
      index = binarySearch( x_vals, dist );
      y_vals[index] += data.getDataValue( selected_pts[i].y,
                                          selected_pts[i].x );
      err = errors[selected_pts[i].y][selected_pts[i].x];
      err_vals[index] += err*err; 
      hit_count[index]++;
    }
    
    // find average value per hit.
    for( int bindex = 0; bindex < n_xvals; bindex++ )
    {
      if( hit_count[bindex] != 0 )
      {
        y_vals[bindex] = y_vals[bindex]/(float)hit_count[bindex];
        err_vals[bindex] = (float)Math.sqrt(err_vals[bindex]) /
                                  (float)hit_count[bindex];
      }
    }

    // Convert the spectrum into a spectrum relative to "Q".  Also, discard the
    // the first bin, at the central vertex of the wedge, since the counts
    // there are usually 0.  ( 0 causes problems with the log-log display.)
    //
    // NOTE: The calculation in "pixel space" added the intensity to y_vals[k]
    //       provided the point's distance from the center was closest to
    //       x_vals[k], so the x_vals[] are actually bin centers.
   
    
    start_point = new floatPoint2D( center.x, center.y );
    start_point = image_to_world_tran.MapTo( start_point ); 
    end_point   = image_to_world_tran.MapTo( end_point ); 

    float new_start_x = start_point.magnitude(); 
    float new_end_x   = end_point.magnitude(); 
    // increment to avoid first bin.
    if ( n_xvals > 1 )
    {
      float step = (new_end_x - new_start_x) / (n_xvals-1);
      new_start_x += step;
    }

    System.out.println("Using Q between " + new_start_x + 
                              " and " + new_end_x );
    // first bin gone, so n_xvals-1 values
    UniformXScale new_x_scale = new UniformXScale( new_start_x, 
                                                   new_end_x, 
                                                   n_xvals-1 );
    float new_y_vals[]   = new float[ y_vals.length - 1 ];
    float new_err_vals[] = new float[ y_vals.length - 1 ];
    for ( int i = 0; i < new_y_vals.length; i++ )
    {
      new_y_vals[i]   = y_vals[i+1];
      new_err_vals[i] = err_vals[i+1];
    }
  
    // put it into a "Data" object and then add it to the dataset
    Data new_spectrum = new FunctionTable( new_x_scale, 
                                           new_y_vals, 
                                           new_err_vals,  
                                           ID );
    new_spectrum.setAttribute( new Float1DAttribute( attribute_name, 
                               attributes ) );

    data_set.addData_entry( new_spectrum ); 
    data_set.setSelectFlag( data_set.getNum_entries() - 1, true );
  }
 
 /*
  * This method efficiently finds what bin to put data in.
  */ 
  private int binarySearch(  float[] x_values, float dist )
  { 
    float start = x_values[0];
    float end = x_values[x_values.length - 1];
    int bin_low = 0;
    int bin_high = x_values.length - 1;
    int bin = Math.round( (float)(bin_high-bin_low)/2f );
    float half_increment = (x_values[1] - start)/2f;
    while( !( dist <= (x_values[bin] + half_increment) &&
              dist >= (x_values[bin] - half_increment) ) &&
	    !( bin == bin_low || bin == bin_high ) )
    {
      //System.out.println("Dist/X_val/bin: " + dist + "/" + x_values[bin] +
      //                   "/" + bin );
      if( dist < (x_values[bin] - half_increment) )
      {
        bin_high = bin;
	bin = bin_low + Math.round((float)(bin_high-bin_low)/2f);
      }
      else
      {
        bin_low = bin;
	bin = bin_low + Math.round((float)(bin_high-bin_low)/2f);
      }
    }
    return bin;
  }
  
 /*
  * This class is required to handle all messages within the SANDWedgeViewer.
  */
  private class WVListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      if( ae.getActionCommand().equals("Load Data") )
      {
        JFileChooser fc = new JFileChooser(projectsDirectory);
        fc.setFileFilter( new DataFileFilter() );
        int result = fc.showDialog(new JFrame(),"Load Data");
     
        if( result == JFileChooser.APPROVE_OPTION )
        {
          String filename = fc.getSelectedFile().toString();
	  projectsDirectory = fc.getCurrentDirectory().toString();
          loadData(filename);
        }
      } // end else if load data
      else if( ae.getActionCommand().equals("Save Results") )
      {
        JFileChooser fc = new JFileChooser(projectsDirectory);
        fc.setFileFilter( new DataFileFilter() );
        int result = fc.showDialog(new JFrame(),"Save Results");
     
        if( result == JFileChooser.APPROVE_OPTION )
        {
          String filename = fc.getSelectedFile().toString();
	  filename = new DataFileFilter().appendExtension(filename);
	  projectsDirectory = fc.getCurrentDirectory().toString();
	  String[] descriptors;  // labels displayed in the SANDEditor.
          Data tempdata = data_set.getData_entry(data_set.getNum_entries() - 1);
          Float1DAttribute fat;
	  // figure out which type of region was selected.
	  if( tempdata.getAttribute(SelectionJPanel.WEDGE) != null )
	  {
	    fat = (Float1DAttribute)
                       tempdata.getAttribute(SelectionJPanel.WEDGE);
	    descriptors = wedgelabels;
	  }
	  else if( tempdata.getAttribute(SelectionJPanel.DOUBLE_WEDGE) != null )
	  {
	    fat = (Float1DAttribute)
                       tempdata.getAttribute(SelectionJPanel.DOUBLE_WEDGE);
	    descriptors = wedgelabels;
	  }
	  else if( tempdata.getAttribute(SelectionJPanel.ELLIPSE) != null )
	  {
	    fat = (Float1DAttribute)
                       tempdata.getAttribute(SelectionJPanel.ELLIPSE);
	    descriptors = ellipselabels;
	  }
	  else  // ring selection
	  {
	    fat = (Float1DAttribute)
                       tempdata.getAttribute(SelectionJPanel.RING);
	    descriptors = ringlabels;
	  }
	  
	  float[] vals = fat.getFloatValue();
	  String at_name = fat.getName();
	  // This portion will put the data attributes at the top of the
	  // file, each line preceeded with a pound (#) symbol.
	  StringBuffer header = new StringBuffer("# Selection Type: ");
	  header.append(at_name).append('\n');
	  // make sure there are the same number of values as descriptors.
	  int length = vals.length;
	  if( length > descriptors.length )
	    length = descriptors.length;
	  for( int i = 0; i < length; i++ )
	  {
	    header.append("# ").append(descriptors[i]);
	    header.append(": ").append(vals[i]).append('\n');
	  }
	  SANDFileWriter.makeFile(filename,header.toString(),tempdata);
        }
      } // end else if load data
      else if( ae.getActionCommand().equals("Hide Results Window") )
      {
        oldview.setVisible(false);
      }
      else if( ae.getActionCommand().equals("Show Results Window") )
      {
	if( !oldview.isVisible() )
	{
          WindowShower shower = new WindowShower(oldview);
          java.awt.EventQueue.invokeLater(shower);
          shower = null;
	}
	else
	  oldview.toFront();
      }
      else if( ae.getActionCommand().equals("Save State") )
      {
	getObjectState().openFileChooser(true);
      }
      else if( ae.getActionCommand().equals("Load State") )
      {
        ObjectState state = new ObjectState();
	if( state.openFileChooser(false) )
	  setObjectState(state);
      }
      else if( ae.getActionCommand().equals("SAND Wedge Viewer") )
      {
        help();
      }
      else if( ae.getActionCommand().equals("Manual Selection") )
      {
        editor.setCurrentPoint( ivc.getPointedAt() );
	WindowShower shower = new WindowShower(editor);
        java.awt.EventQueue.invokeLater(shower);
	shower = null;
      }
    }
  }
  
 /*
  * This class listeners for all messages sent by the ImageViewComponent,
  * including selections made by the selection overlay.
  */ 
  private class ImageListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
      // get all of the selections, we only care about the last one.
      Region[] selectedregions = ivc.getSelectedRegions();
        
      if( message.equals(SelectionOverlay.REGION_ADDED) )
      {
        // get points from the last selected region.
        integrate( selectedregions[selectedregions.length-1] );
	if( oldview == null )
	{
          oldview = new ViewManager( data_set, IViewManager.SELECTED_GRAPHS );
          oldview.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
	  oldview.addComponentListener( new VisibleListener() );
	  oldview.addWindowListener( new ClosingListener() );
	}
	else
	{
	  oldview.update(data_set, IObserver.DATA_CHANGED);
	  if( !oldview.isVisible() )
	  {
            WindowShower shower = new WindowShower(oldview);
            java.awt.EventQueue.invokeLater(shower);
            shower = null;
	  }
	  else
	    oldview.toFront();
	}
	editor.selectionChanged();
      }
      else if( message.equals(SelectionOverlay.REGION_REMOVED) )
      {
        data_set.removeData_entry( data_set.getNum_entries() - 1 );
	if( oldview != null )
	{
	  oldview.update(data_set, IObserver.DATA_CHANGED);
	}
	editor.selectionChanged();
      }
      else if( message.equals(SelectionOverlay.ALL_REGIONS_REMOVED) )
      {
        data_set.removeAll_data_entries();
	if( oldview != null )
	{
	  oldview.update(data_set, IObserver.DATA_CHANGED);
	}
	editor.selectionChanged();
      }
      else if( message.equals(IViewComponent.POINTED_AT_CHANGED) )
      {
        editor.setCurrentPoint( ivc.getPointedAt() );
	//System.out.println("Pointed At Changed " + 
	//                   ivc.getPointedAt().toString() );
      }
    }
  }
  
 /*
  * If ViewManager is visible, make button read "Hide Results Window",
  * otherwise have it read "Show Results Window"
  */
  private class VisibleListener extends ComponentAdapter
  {
    public void componentHidden( ComponentEvent e )
    {
      menu_bar.getMenu(1).getItem(0).setText("Show Results Window");
      menu_bar.validate();
    }  
    
    public void componentShown( ComponentEvent e )
    {
      // since menu item starts out disabled, make sure it is enabled.
      if( !menu_bar.getMenu(1).getItem(0).isEnabled() )
        menu_bar.getMenu(1).getItem(0).setEnabled(true);
      menu_bar.getMenu(1).getItem(0).setText("Hide Results Window");
    }
  } 
 
 /*
  * This class is needed if to check of the user hides the ViewManager by
  * the close button on the frame. This was needed to extend the functionality
  * of the VisibleListener class.
  */ 
  private class ClosingListener extends WindowAdapter
  {
    public void windowClosing( WindowEvent we )
    {
      menu_bar.getMenu(1).getItem(0).setText("Show Results Window");
      menu_bar.validate();
    }
  }
  
 /*
  * This class is needed to reajust the size of the PanViewControl. Since
  * the PanViewControl needs to resize itself once the width is known,
  * the initial bounding box restricts the size of the control.
  */
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
  } 
 
 /*
  * File filter for .dat files being loaded for data analysis.
  */ 
  private class DataFileFilter extends RobustFileFilter
  {
   /*
    *  Default constructor.  Calls the super constructor,
    *  sets the description, and sets the file extensions.
    */
    public DataFileFilter()
    {
      super();
      super.setDescription("Data File (*.dat)");
      super.addExtension(".dat");
    } 
  }

 /*
  * This class is called by the Manual Selection button. It allows users to
  * enter/edit selections via field entries instead of using the GUI.
  */
  private class SANDEditor extends JFrame
  {
    private FieldEntryControl radiofec = new FieldEntryControl(5);
    private CursorOutputControl coc;
    private JComboBox selectlist;
    private Box pane;
    private Box leftpane;
    private Box rightpane;
    private SANDEditor this_editor;
    
    protected SANDEditor()
    {
      this_editor = this;
      this_editor.setTitle("SAND Wedge Editor");
      this_editor.setBounds(0,0,430,300);
      this_editor.getContentPane().setLayout( new GridLayout(1,1) );
      this_editor.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
      
      pane = new Box( BoxLayout.X_AXIS );
      leftpane = new Box( BoxLayout.Y_AXIS );
      // give left pane 5/8 of the window.
      leftpane.setPreferredSize( new Dimension(
                                     (int)(5*this_editor.getWidth()/8),0) );
      rightpane = new Box( BoxLayout.Y_AXIS );
      rightpane.setPreferredSize( new Dimension(
                                      (int)(3*this_editor.getWidth()/8),0) );
      String[] cursorlabels = {"X","Y"};
      
      radiofec.setLabelWidth(10);
      radiofec.setFieldWidth(10);
      radiofec.addRadioChoice(SelectionJPanel.ELLIPSE,ellipselabels);
      radiofec.addRadioChoice(SelectionJPanel.WEDGE,wedgelabels);
      radiofec.addRadioChoice(SelectionJPanel.DOUBLE_WEDGE,
                              SelectionJPanel.WEDGE); // use wedge labels
      radiofec.addRadioChoice(SelectionJPanel.RING,ringlabels);
      radiofec.setButtonText("Enter Values");
      radiofec.addActionListener( new EditorListener() );
      
      coc = new CursorOutputControl(cursorlabels);
      coc.setTitle("Current Pointed At");
      setCurrentPoint( new floatPoint2D(0,0) );
      
      leftpane.add(radiofec);
      
      selectlist = new JComboBox();
     /* *******************CHANGE HERE FOR ADDED FEATURES**********************
      * Uncomment line below, and remove setEnabled(false) to allow for 
      * editing of selections. This cannot be done until the IVC passes
      * world coords to this viewer.
      */
      selectlist.addActionListener( new EditorListener() );
      //selectlist.setEnabled(false);
      
      buildComboBox();
      rightpane.add( selectlist );
      rightpane.add(coc);
      JPanel spacer = new JPanel();
      spacer.setPreferredSize( new Dimension( 0, 250 ) );
      rightpane.add(spacer);
      JButton closebutton = new JButton("Close");
      closebutton.addActionListener(new EditorListener() );
      rightpane.add( closebutton );
      pane.add(leftpane);
      pane.add(rightpane);
      this_editor.getContentPane().add(pane);
      
    }
   
   /*
    * This method will set the current world coord point, displayed by the
    * x[] y[] cursor readout.
    */ 
    protected void setCurrentPoint( floatPoint2D current_pt )
    {
      coc.setValue( 0, current_pt.x );
      coc.setValue( 1, current_pt.y );
      this_editor.repaint();
    }
    
   /*
    * called when a selection is added/removed
    */ 
    protected void selectionChanged()
    {
      radiofec.clearAllValues();
      buildComboBox();
      rightpane.validate();
      this_editor.repaint();
    }
   
   /*
    * This will rebuild the combobox each time a selection is added/removed
    */ 
    private void buildComboBox()
    {  
      if( ivc != null )
      {
        Region[] regions = ivc.getSelectedRegions();
        String[] sel_names = new String[regions.length + 1];
        String temp = "";
	String temp_name = "";
	selectlist.removeAllItems();
	for( int reg = 0; reg < regions.length; reg++ )
        {
	  if( regions[reg] instanceof EllipseRegion )
	    temp = SelectionJPanel.ELLIPSE;
	  else if( regions[reg] instanceof WedgeRegion )
	    temp = SelectionJPanel.WEDGE;
	  else if( regions[reg] instanceof DoubleWedgeRegion )
	    temp = SelectionJPanel.DOUBLE_WEDGE;
	  else if( regions[reg] instanceof AnnularRegion )
	    temp = SelectionJPanel.RING;
          
	  temp_name = (reg+1) + " - " + temp;
          selectlist.addItem(temp_name);
	}
	selectlist.addItem("New Selection");
	// set selected region to be the last region added.
	if( regions.length > 0 )
	  selectlist.setSelectedIndex( regions.length - 1 );
      }
      else
      {
	selectlist.addItem("New Selection");
      }
    }
  
   /*
    * This class listeners for messages send by the editor.
    */ 
    private class EditorListener implements ActionListener
    {
      public void actionPerformed( ActionEvent ae )
      {
        String message = ae.getActionCommand();
        
        if( message.equals(FieldEntryControl.BUTTON_PRESSED) )
        {
	  float[] values = radiofec.getAllFloatValues();
	  // make sure no values are invalid
	  for( int i = 0; i < values.length; i++ )
	  {
	    if( Float.isNaN(values[i]) )
	      return;
	  }
     /* ***********************Defining Points for Ellipse**********************
      * def_pts[0]   = top left corner of bounding box around arc's total circle
      * def_pts[1]   = bottom right corner of bounding box around arc's circle
      * def_pts[2]   = center pt of circle that arc is taken from
      **************************************************************************
      */
	  if( radiofec.getSelected().equals(SelectionJPanel.ELLIPSE) )
	  {
	    // since ivc y values are swapped, y values for topleft and
	    // bottomright are also swapped.
	    floatPoint2D[] wc_pts = new floatPoint2D[3];
	    wc_pts[0] = new floatPoint2D( (values[0] - values[2]),
	                                  (values[1] + values[3]) );
	    wc_pts[1] = new floatPoint2D( (values[0] + values[2]),
	                                  (values[1] - values[3]) );
	    wc_pts[2] = new floatPoint2D( values[0], values[1] );
	    ivc.addSelection( new WCRegion( SelectionJPanel.ELLIPSE, wc_pts ) );
	  }
	  
     /* ***********************Defining Points for Wedge************************
      * def_pts[0]   = center pt of circle that arc is taken from
      * def_pts[1]   = last mouse point/point at intersection of line and arc
      * def_pts[2]   = reflection of p[1]
      * def_pts[3]   = top left corner of bounding box around arc's total circle
      * def_pts[4]   = bottom right corner of bounding box around arc's circle
      * def_pts[5].x = startangle, the directional vector in degrees
      * def_pts[5].y = degrees covered by arc.
      **************************************************************************
      */
	  else if( radiofec.getSelected().equals(SelectionJPanel.WEDGE) )
	  {
	    // since ivc y values are swapped, y values for topleft and
	    // bottomright are also swapped.
	    floatPoint2D[] wc_pts = new floatPoint2D[6];
	    wc_pts[0] = new floatPoint2D( values[0], values[1] );
	    wc_pts[3] = new floatPoint2D( (values[0] - values[2]),
	                                  (values[1] + values[2]) );
	    wc_pts[4] = new floatPoint2D( (values[0] + values[2]),
	                                  (values[1] - values[2]) );
	    // use trig to find p1 (see WedgeCursor)
	    double theta_p1 = (double)(values[3] - values[4]/2);
	    // convert theta from degrees to radians
	    theta_p1 = theta_p1 * Math.PI / 180;
	    wc_pts[1] = new floatPoint2D( values[0] +
	                                  values[2]*(float)Math.cos(theta_p1),
					  values[1] +
	                                  values[2]*(float)Math.sin(theta_p1));
	    // use trig to find rp1 (see WedgeCursor)
	    double theta_rp1 = (double)(values[3] + values[4]/2);
	    // convert theta from degrees to radians
	    theta_rp1 = theta_rp1 * Math.PI / 180;
	    wc_pts[2] = new floatPoint2D( values[0] +
	                                  values[2]*(float)Math.cos(theta_rp1),
					  values[1] +
	                                  values[2]*(float)Math.sin(theta_rp1));
	    // define angles so WedgeRegion can use them.
	    float arcangle = values[4];
	    // make sure values are on interval [0,360)
	    while( arcangle < 0 )
	      arcangle = -arcangle;
	    while( arcangle >= 360 )
	      arcangle -= 360;
	    
	    float startangle = values[3] - arcangle/2;
	    while( startangle >= 360 )
	      startangle -= 360;
	    while( startangle < 0 )
	      startangle += 360;
	    wc_pts[5] = new floatPoint2D( startangle, arcangle );
	    ivc.addSelection( new WCRegion( SelectionJPanel.WEDGE, wc_pts ) );
	  }
	  
     /* *******************Defining Points for Double Wedge*********************
      * def_pts[0]   = center pt of circle that arc is taken from
      * def_pts[1]   = last mouse point/point at intersection of line and arc
      * def_pts[2]   = reflection of p[1]
      * def_pts[3]   = top left corner of bounding box around arc's total circle
      * def_pts[4]   = bottom right corner of bounding box around arc's circle
      * def_pts[5].x = startangle, the directional vector in degrees
      * def_pts[5].y = degrees covered by arc.
      **************************************************************************
      */
	  else if( radiofec.getSelected().equals(SelectionJPanel.DOUBLE_WEDGE) )
	  {
	    // since ivc y values are swapped, y values for topleft and
	    // bottomright are also swapped.
	    floatPoint2D[] wc_pts = new floatPoint2D[6];
	    wc_pts[0] = new floatPoint2D( values[0], values[1] );
	    wc_pts[3] = new floatPoint2D( (values[0] - values[2]),
	                                  (values[1] + values[2]) );
	    wc_pts[4] = new floatPoint2D( (values[0] + values[2]),
	                                  (values[1] - values[2]) );
	    // use trig to find p1 (see WedgeCursor)
	    double theta_p1 = (double)(values[3] - values[4]/2);
	    // convert theta from degrees to radians
	    theta_p1 = theta_p1 * Math.PI / 180;
	    wc_pts[1] = new floatPoint2D( values[0] +
	                                  values[2]*(float)Math.cos(theta_p1),
					  values[1] +
	                                  values[2]*(float)Math.sin(theta_p1));
	    // use trig to find rp1 (see WedgeCursor)
	    double theta_rp1 = (double)(values[3] + values[4]/2);
	    // convert theta from degrees to radians
	    theta_rp1 = theta_rp1 * Math.PI / 180;
	    wc_pts[2] = new floatPoint2D( values[0] +
	                                  values[2]*(float)Math.cos(theta_rp1),
					  values[1] +
	                                  values[2]*(float)Math.sin(theta_rp1));
	    // define angles so WedgeRegion can use them.
	    float arcangle = values[4];
	    // make sure values are on interval [0,360)
	    while( arcangle < 0 )
	      arcangle = -arcangle;
	    while( arcangle > 180 )
	      arcangle = 360 - arcangle;
	    
	    float startangle = values[3] - arcangle/2;
	    while( startangle >= 360 )
	      startangle -= 360;
	    while( startangle < 0 )
	      startangle += 360;
	    wc_pts[5] = new floatPoint2D( startangle, arcangle );
	    ivc.addSelection( new WCRegion( SelectionJPanel.DOUBLE_WEDGE,
	                                    wc_pts ) );
	  }
	  
     /* ***********************Defining Points for Ring*************************
      * def_pts[0]   = center pt of circle that arc is taken from
      * def_pts[1]   = top left corner of bounding box of inner circle
      * def_pts[2]   = bottom right corner of bounding box of inner circle
      * def_pts[3]   = top left corner of bounding box of outer circle
      * def_pts[4]   = bottom right corner of bounding box of outer circle
      **************************************************************************
      */
	  else if( radiofec.getSelected().equals(SelectionJPanel.RING) )
	  {
	    // if inner radius larger than outer radius, swap them
	    if( values[2] > values[3] )
	    {
	      float temp = values[2];
	      values[2] = values[3];
	      values[3] = temp;
	    }
	    // since ivc y values are swapped, y values for topleft and
	    // bottomright are also swapped.
	    floatPoint2D[] wc_pts = new floatPoint2D[5];
	    wc_pts[0] = new floatPoint2D( values[0], values[1] );
	    // inner topleft and bottomright
	    wc_pts[1] = new floatPoint2D( (values[0] - values[2]),
	                                  (values[1] + values[2]) );
	    wc_pts[2] = new floatPoint2D( (values[0] + values[2]),
	                                  (values[1] - values[2]) );
	    // outer topleft and bottomright
	    wc_pts[3] = new floatPoint2D( (values[0] - values[3]),
	                                  (values[1] + values[3]) );
	    wc_pts[4] = new floatPoint2D( (values[0] + values[3]),
	                                  (values[1] - values[3]) );
	    ivc.addSelection( new WCRegion( SelectionJPanel.RING, wc_pts ) );
	  }	  
        } // end if (BUTTON_PRESSED)
        else if( message.equals("Close") )
        {
	  this_editor.setVisible(false);
        }
	else if( message.equals("comboBoxChanged") )
	{
	  int index = selectlist.getSelectedIndex();
	  if( selectlist.getSelectedItem() != null &&
	      !((String)selectlist.getSelectedItem()).equals("New Selection") )
	  {
	    Data data = data_set.getData_entry(index);
	    
	    if( ((String)selectlist.getSelectedItem()).indexOf(
	                                        SelectionJPanel.ELLIPSE) >= 0 )
	    {
	      radiofec.setSelected(SelectionJPanel.ELLIPSE);
	      Float1DAttribute att =
	           (Float1DAttribute)data.getAttribute(SelectionJPanel.ELLIPSE);
	      float[] attlist = att.getFloatValue();
	      for( int i = 0; i < attlist.length; i++ )
	        radiofec.setValue( i,attlist[i] );
	    }
	    else if( ((String)selectlist.getSelectedItem()).indexOf(
	                                   SelectionJPanel.DOUBLE_WEDGE) >= 0 )
	    {
	      radiofec.setSelected(SelectionJPanel.DOUBLE_WEDGE);
	      Float1DAttribute att = (Float1DAttribute)
	            data.getAttribute(SelectionJPanel.DOUBLE_WEDGE);
	      float[] attlist = att.getFloatValue();
	      for( int i = 0; i < attlist.length; i++ )
	        radiofec.setValue( i,attlist[i] );
	    }
	    else if( ((String)selectlist.getSelectedItem()).indexOf(
	                                   SelectionJPanel.WEDGE) >= 0 )
	    {
	      radiofec.setSelected(SelectionJPanel.WEDGE);
	      Float1DAttribute att =
	             (Float1DAttribute)data.getAttribute(SelectionJPanel.WEDGE);
	      float[] attlist = att.getFloatValue();
	      for( int i = 0; i < attlist.length; i++ )
	        radiofec.setValue( i,attlist[i] );
	    }
	    else if( ((String)selectlist.getSelectedItem()).indexOf(
	                                   SelectionJPanel.RING) >= 0 )
	    {
	      radiofec.setSelected(SelectionJPanel.RING);
	      Float1DAttribute att =
	             (Float1DAttribute)data.getAttribute(SelectionJPanel.RING);
	      float[] attlist = att.getFloatValue();
	      for( int i = 0; i < attlist.length; i++ )
	        radiofec.setValue( i,attlist[i] );
	    }
	  }
	  this_editor.validate();
	  this_editor.repaint();
	  // this will repaint the image when a selection is made.
	  if( this_viewer != null )
	  {
	    this_viewer.validate();
	    this_viewer.repaint();
	  }
	}
      }
    }
  
  } // end of SANDEditor
  
 /*
  * Main program.
  */
  public static void main( String args[] )
  {
    SANDWedgeViewer wedgeviewer = new SANDWedgeViewer();
    if( args.length > 0 )
      wedgeviewer.loadData( args[0] );
    WindowShower shower = new WindowShower(wedgeviewer);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }

}
