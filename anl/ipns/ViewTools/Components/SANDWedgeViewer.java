/*
 * File:  SANDWedgeViewer.java
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serializable;
import java.io.IOException;
import java.io.EOFException;
 import javax.swing.border.TitledBorder;
 import javax.swing.border.LineBorder;

import DataSetTools.components.View.TwoD.ImageViewComponent;
import DataSetTools.components.View.OneD.FunctionViewComponent;
import DataSetTools.components.View.Menu.MenuItemMaker;
import DataSetTools.components.View.Menu.ViewMenuItem;
import DataSetTools.components.image.*;
import DataSetTools.components.containers.SplitPaneWithState;
import DataSetTools.components.View.Transparency.SelectionOverlay;
import DataSetTools.components.View.Region.*;
import DataSetTools.components.View.ViewControls.PanViewControl;
import DataSetTools.util.TextFileReader;
import DataSetTools.util.RobustFileFilter;
// these imports are for putting data into a dataset, then into DataSetData.
import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.Data;
import DataSetTools.dataset.FunctionTable;
import DataSetTools.dataset.UniformXScale;
 import DataSetTools.util.FontUtil;

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
  * "FunctionViewComponent" - This constant String is a key for referencing
  * the state information about the FunctionViewComponent. Since the
  * FunctionViewComponent has its own state, this value is of type
  * ObjectState, and contains the state of the FunctionViewComponent. 
  */
  public static final String FUNCTION_VIEW_COMPONENT = "FunctionViewComponent";
  
 /**
  * "ViewerSize" - This constant String is a key for referencing
  * the state information about the size of the viewer at the time
  * the state was saved. The value this key references is of type
  * Dimension.
  */
  public static final String VIEWER_SIZE           = "ViewerSize"; 
  
 /**
  * "DataDirectory" - This constant String is a key for referencing
  * the state information about the location of the data files being
  * loaded by this viewer. The value this key references is of type
  * String.
  */
  public static final String DATA_DIRECTORY        = "DataDirectory";
  
  private static JFrame helper = null;
  
  // complete viewer, includes controls and ijp
  private transient SplitPaneWithState pane;
  private transient ImageViewComponent ivc;
  private transient FunctionViewComponent fvc;
  private transient IVirtualArray2D data;
  private transient JMenuBar menu_bar;
  private DataSet data_set;
  private String projectsDirectory = System.getProperty("user.home");
  // since box,line,point selections are not needed, don't do anything when
  // these selections are made. When selections are removed, there is no way
  // to know what kind of selection was removed, so this keeps track.
  private boolean[] validSelections = new boolean[99];

 /**
  * Construct a frame with no data to start with. This constructor will be
  * used when data is being loaded in.
  */
  public SANDWedgeViewer()
  {
    init(null);
  }

 /**
  * Construct a frame with the specified image and title
  *  
  *  @param  iva
  */
  public SANDWedgeViewer( IVirtualArray2D iva )
  {
    init(iva);
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
                          AxisInfo xinfo,
		          AxisInfo yinfo,
		          String title )
  {
    VirtualArray2D temp = new VirtualArray2D( array );
    temp.setAxisInfo( AxisInfo.X_AXIS, xinfo.copy() );
    temp.setAxisInfo( AxisInfo.Y_AXIS, yinfo.copy() );
    temp.setTitle(title);
    
    init(temp);
  }
  
  public void setObjectState( ObjectState new_state )
  {
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(IMAGE_VIEW_COMPONENT);
    if( temp != null )
    {
      ivc.setObjectState( (ObjectState)temp );
      redraw = true;  
    } 
    
    temp = new_state.get(FUNCTION_VIEW_COMPONENT);
    if( temp != null )
    {
      //fvc.setObjectState( (ObjectState)temp );
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
  
  public ObjectState getObjectState()
  {
    ObjectState state = new ObjectState();
    state.insert( IMAGE_VIEW_COMPONENT, ivc.getObjectState() );
    //state.insert( FUNCTION_VIEW_COMPONENT, fvc.getObjectState() );
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
    JTextArea text = new JTextArea("Description:\n\n");
    text.setEditable(false);
    text.setLineWrap(true);

    text.append("The SAND Wedge Viewer (SWV) in an interactive analysis tool." +
        	" SWV features the ability to make three selections: Wedge, " +
    		"Double Wedge, and Ellipse. Although other selections are " +
    		"available, they have no affect in this viewer. Once a " +
    		"selection is made on the image, the graph will display " +
		"the values per hit as a function of distance.\n\n");
    text.append("Commands for SWV\n\n");
    text.append("Note:\n" +
        	"Detailed commands can be found under the Overlay help " +
        	"menu.\n\n");
    
    JScrollPane scroll = new JScrollPane(text);
    scroll.setVerticalScrollBarPolicy(
        			    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    helper.setVisible(true);
  }
  
  public void loadData( String filename )
  {
    int NUM_ROWS = 200;
    int NUM_COLS = 200;
    float[][] array = new float[NUM_COLS][NUM_ROWS];
    float qxmin = 0;
    float qymin = 0;
    float qxmax = 0;
    float qymax = 0;
    // try to open the file
    try
    {
      int row = 0;
      int col = 0;
      // file arranged in 4 columns: Qx, Qy, Value, Error
      // Since we only care about the min and max of Qx, Qy, only the first
      // and last values in those columns are saved. The Value column is all
      // stored in the array, the Error column is ignored.
      TextFileReader reader = new TextFileReader( filename );
      // read in first line, this will set the Qx/Qy min and read in
      // the first value.
      StringTokenizer datarow = new StringTokenizer(reader.read_line());
      qxmin = (new Float( datarow.nextToken() )).floatValue();
      qymin = (new Float( datarow.nextToken() )).floatValue();
      array[col][row] = (new Float( datarow.nextToken() )).floatValue();
      col++;  // increment row since first element was read in.
      // let the exception EOF end the loop.
      while( true )
      {
	// now read in the data one row at a time, each row contains a
	// Qx, Qy, Value, and Error value. Store the Values in the array
	// by column instead of row.
        datarow = new StringTokenizer(reader.read_line());
        qxmax = (new Float( datarow.nextToken() )).floatValue();
        qymax = (new Float( datarow.nextToken() )).floatValue();
        array[col][row] = (new Float( datarow.nextToken() )).floatValue();
	//System.out.println("Row/Col: (" + row + "," + col + ")" );
	
	// increment column if at last row, reset row to start.
	// this will cause the numbers to be read in by column
	if( col == NUM_COLS - 1 )
        {
          col = 0;
	  row++;
        }
        else
          col++;
      } // end while
    }
    catch( IOException e1 )
    {
      if( e1.getMessage().equals("End of file") )
      {
        // done reading file
        VirtualArray2D va2D = new VirtualArray2D( array );
        va2D.setAxisInfo( AxisInfo.X_AXIS, qxmin, qxmax, 
    		            "Qx","X Units", true );
        va2D.setAxisInfo( AxisInfo.Y_AXIS, qymin, qymax, 
    			    "Qy","Y Units", true );
        // since datamin/max are gotten from the image,
	// the min/max are dummy values.
	va2D.setAxisInfo( AxisInfo.Z_AXIS, 0, 1, 
    			    "Qz","Z Units", true );
        va2D.setTitle("SAND Wedge Viewer");
	setData( va2D );
      }
      else
      {
        // no file to be read, display file not found on empty jpanel.
        VirtualArray2D nullarray = null;
        this.setData(nullarray);
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
  public void setData( IVirtualArray2D values )
  {
    // since data is changing, kill all windows created by ivc and fvc.
    // If they are null, no windows were made.
    if( ivc != null )
      ivc.kill();
    if( fvc != null )
      fvc.kill();
    
    // if new array is same size as old array
    if( values != null && data != null &&
        ( values.getNumRows() == data.getNumRows() &&
          values.getNumColumns() == data.getNumColumns() ) )
    {  
      data = values;
      ivc.setSelectedRegions(null);
      ivc.dataChanged(data);
      integrate(null);
    }  
    // if different sized array, remove everything and build again.
    else
    { 
      data = values;
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
  public void setData( float[][] array )
  {
    setData( new VirtualArray2D(array) );
  }
  
  public void setDataDirectory( String path )
  {
    projectsDirectory = path;
  }
  
 /*
  * The init function gathers the common functionality between the constructors
  * so that the code does not have to exist in 3 spots. This will build the
  * niceties of the viewer.
  */ 
  private void init( IVirtualArray2D iva )
  {
    setTitle("SAND Wedge Viewer");
    data = new VirtualArray2D(1,1);
    buildMenubar();
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(0,0,500,615);
    
    data_set   = new DataSet("Value per Hit vs Distance", "Sample log-info");

    data_set.setX_units("(Units)" );
    data_set.setX_label("Distance" );

    data_set.setY_units("(Units)" );
    data_set.setY_label("Value per Hit" );
    
    setData(iva);
  }

 /*
  * This method builds the content pane of the frame.
  */
  private void buildPane()
  { 
    if( data != null )
    {
      ivc = new ImageViewComponent( data );
      fvc = new FunctionViewComponent( new DataSetData( data_set ) );
      ivc.setColorControlEast(true);
      ivc.addActionListener( new WVListener() );
      ivc.addActionListener( new ImageListener() );    
      Box componentholder = new Box(BoxLayout.Y_AXIS);
      componentholder.add( ivc.getDisplayPanel() );
      componentholder.add(fvc.getDisplayPanel() );
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
      
      // get menu items from function view component and place it in a menu
      ViewMenuItem[] fmenus = fvc.getSharedMenuItems();
      for( int i = 0; i < fmenus.length; i++ )
      {
        if( ViewMenuItem.PUT_IN_FILE.toLowerCase().equals(
    	    fmenus[i].getPath().toLowerCase()) )
    	{
	  menu_bar.getMenu(0).add( fmenus[i].getItem() ); 
        }
	else if( ViewMenuItem.PUT_IN_OPTIONS.toLowerCase().equals(
    	         fmenus[i].getPath().toLowerCase()) )
    	{
	  menu_bar.getMenu(1).add( fmenus[i].getItem() );	   
        }
	else if( ViewMenuItem.PUT_IN_HELP.toLowerCase().equals(
    	         fmenus[i].getPath().toLowerCase()) )
        {
	  menu_bar.getMenu(2).add( fmenus[i].getItem() );
        }
      }
    }
    else
    {
      pane = new SplitPaneWithState(JSplitPane.HORIZONTAL_SPLIT,
                                    new JPanel(), new JPanel(), .75f );
    }	   
  }
 
 /*
  * This private method will (re)build the menubar. This is necessary since
  * the ImageViewComponent or FunctionViewComponent could add menu items to
  * the Menubar. If the file being loaded is not found, those menu items
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
    Vector save_menu = new Vector();
    Vector load_menu = new Vector();
    Vector load_data = new Vector();
    Vector swv_help  = new Vector();
    Vector file_listeners = new Vector();
    Vector option_listeners = new Vector();
    Vector help_listeners = new Vector();
    file.add("File");
    file_listeners.add( new ImageListener() ); // listener for file
    file.add(load_data);
      load_data.add("Load Data");
      file_listeners.add( new ImageListener() ); // listener for load data
    
    options.add("Options");
    option_listeners.add( new ImageListener() ); // listener for options
    options.add(save_menu);
      save_menu.add("Save State");
      option_listeners.add( new ImageListener() ); // listener for save state
    options.add(load_menu);
      load_menu.add("Load State");
      option_listeners.add( new ImageListener() ); // listener for load state
    help.add("Help");
    help_listeners.add( new ImageListener() );
    help.add( swv_help );
      swv_help.add("SAND Wedge Viewer");
      help_listeners.add( new ImageListener() );
    
    menu_bar.add( MenuItemMaker.makeMenuItem(file,file_listeners) ); 
    menu_bar.add( MenuItemMaker.makeMenuItem(options,option_listeners) );
    menu_bar.add( MenuItemMaker.makeMenuItem(help,help_listeners) );
    // since the IVC and FVC are not created unless data is available,
    // do not load state unless data is available.
    if( data == null )
    {
      JMenu option_menu = menu_bar.getMenu(1);
      option_menu.getItem(0).setEnabled(false);
      option_menu.getItem(1).setEnabled(false);
    }
    //menu_bar.add(new JMenu("Help"));
  }
  
 /*
  * build controls for both view components.
  */ 
  private Box buildControls()
  {
    Box controls = new Box(BoxLayout.Y_AXIS);
    
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
    ivc_controls.addComponentListener( new ResizedControlListener() );
    
    Box fvc_controls = new Box(BoxLayout.Y_AXIS); 
    TitledBorder fvc_border = 
    		     new TitledBorder(LineBorder.createBlackLineBorder(),
        			      "Graph Controls");
    fvc_border.setTitleFont( FontUtil.BORDER_FONT );
    fvc_controls.setBorder( fvc_border );
    JComponent[] fvc_ctrl = fvc.getPrivateControls();
    for( int i = 0; i < fvc_ctrl.length; i++ )
    {
      fvc_controls.add(fvc_ctrl[i]);
    }
    //fvc_controls.addComponentListener( new ResizedControlListener() );
    
    if( ivc_ctrl.length != 0 )
    {
      controls.add(ivc_controls);
    }
    
    JPanel spacer = new JPanel();
    spacer.setPreferredSize( new Dimension(0, 10000) );
    controls.add(spacer);
    
    if( fvc_ctrl.length != 0 )
    {
      controls.add(fvc_controls);
    }
    return controls;
  }

  private void integrate( Region region )
  {   
    int   ID      = 1;
    float start_x = 0;
    float end_x   = 0;
    int   n_xvals = 200;
    Point center = new Point(0,0);
   
    if( region == null )
    {
      return;
    }
    Point[] def_pts = region.getDefiningPoints();
    if( region instanceof WedgeRegion || 
        region instanceof DoubleWedgeRegion )
    {
     /* def_pts[0]   = center pt of circle that arc is taken from
      * def_pts[1]   = last mouse point/point at intersection of line and arc
      * def_pts[2]   = reflection of p[1]
      * def_pts[3]   = top left corner of bounding box around arc's total circle
      * def_pts[4]   = bottom right corner of bounding box around arc's circle
      * def_pts[5].x = startangle, the directional vector in degrees
      * def_pts[5].y = degrees covered by arc.
      */
      center = new Point( def_pts[0] );
      end_x = def_pts[4].x - center.x;
    }
    else if( region instanceof EllipseRegion )
    {
     /* def_pts[0]   = top left corner of bounding box around arc's total circle
      * def_pts[1]   = bottom right corner of bounding box around arc's circle
      * def_pts[2]   = center pt of circle that arc is taken from
      */
      center = new Point( def_pts[2] );
      end_x = def_pts[1].x - center.x;
    }
    // these shouldn't be used for this viewer.
    // Code in the WVListener will prevent these from ever being true.
    else if( region instanceof LineRegion )
    {
      return;
    }
    else if( region instanceof BoxRegion )
    {
      return;
    }
    else if( region instanceof PointRegion )
    {
      return;
    }

    Data          spectrum;     // data block that will hold a "spectrum"
    UniformXScale x_scale;      // "time channels" for the spectrum

    // build list of time channels
    x_scale = new UniformXScale( start_x, end_x, n_xvals );

    int hit_count[] = new int[n_xvals];
    float y_vals[] = new float[n_xvals];
    float x_vals[] = x_scale.getXs();
    Point[] selected_pts = region.getSelectedPoints();
    // this loop will...
    float x = 0;
    float y = 0;
    float dist = 0;
    int index = 0;
    for ( int i = 0; i < selected_pts.length; i++ )
    {
      x = Math.abs( selected_pts[i].x - center.x );
      x = x*x;  // square x
      y = Math.abs( selected_pts[i].y - center.y );
      y = y*y;  // square y
      dist = (float)Math.sqrt( x + y );
      index = binarySearch( x_vals, dist );
      y_vals[index] += data.getDataValue( selected_pts[i].y,
                                          selected_pts[i].x );
      hit_count[index]++;
    }
    
    for( int bindex = 0; bindex < n_xvals; bindex++ )
    {
      if( hit_count[bindex] != 0 )
        y_vals[bindex] = y_vals[bindex]/(float)hit_count[bindex];
    }
    spectrum = new FunctionTable( x_scale, y_vals, ID );
                                                      // put it into a "Data"
                                                      // object and then add
    data_set.addData_entry( spectrum );               // that data object to
                                                      // the data set
    //data_set.setPointedAtIndex( data_set.getNum_entries() - 1 );
    data_set.setSelectFlag( data_set.getNum_entries() - 1, true );
  }
  
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
  * This class is required to update the axes when the divider is moved. 
  * Without it, the image is one frame behind.
  */
  private class ImageListener implements ActionListener
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
      else if( ae.getActionCommand().equals("Save State") )
      {
        //state = ivc.getObjectState();
	getObjectState().openFileChooser(true);
      }
      else if( ae.getActionCommand().equals("Load State") )
      {
        ObjectState state = new ObjectState();
        //state = ivc.getObjectState();
	if( state.openFileChooser(false) )
	  setObjectState(state);
      }
      else if( ae.getActionCommand().equals("SAND Wedge Viewer") )
      {
        help();
      }
    }
  }
  
 /*
  * This class listeners for selections made by the selection overlay
  */ 
  private class WVListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
      // get all of the selections, we only care about the last one.
      Region[] selectedregions = ivc.getSelectedRegions();
        
      if( message.equals(SelectionOverlay.REGION_ADDED) )
      {
        Region last_region = selectedregions[selectedregions.length-1];
        if( last_region instanceof WedgeRegion ||
            last_region instanceof DoubleWedgeRegion ||
            last_region instanceof EllipseRegion )
        {
          // get points from the last selected region.
          integrate( last_region );
          fvc.dataChanged(new DataSetData(data_set));
	  validSelections[selectedregions.length-1] = true;
        }
      }
      else if( message.equals(SelectionOverlay.REGION_REMOVED) )
      {
        if( validSelections[selectedregions.length] )
	{
          data_set.removeData_entry( data_set.getNum_entries() - 1 );
          fvc.dataChanged(new DataSetData(data_set));
	  validSelections[selectedregions.length] = false;
	}
      }
      else if( message.equals(SelectionOverlay.ALL_REGIONS_REMOVED) )
      {
        data_set.removeAll_data_entries();
        fvc.dataChanged(new DataSetData(data_set));
      }
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
        height += ((JComponent)controls[ctrl]).getHeight() + 4;
      }
      if( control_box.getHeight() < height )
      {
        control_box.setSize( new Dimension( width, height ) );
        /*        
	Component temp2 = ((Container)
	    pane.getRightComponent()).getComponent(2);
	((Container)pane.getRightComponent()).remove(2);

        Component temp1 = ((Container)
	    pane.getRightComponent()).getComponent(1);
	((Container)pane.getRightComponent()).remove(1);
	
	((Container)pane.getRightComponent()).add(temp1,1);
	((Container)pane.getRightComponent()).add(temp2,2);
	*/                                                      
      }
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
  * Main program. To set a consistent projectsDirectory
  */
  public static void main( String args[] )
  {
    SANDWedgeViewer load = new SANDWedgeViewer();
    load.setVisible(true);
  }

}
