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

import gov.anl.ipns.ViewTools.UI.SplitPaneWithState;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.TwoD.*;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.Transparency.SelectionOverlay;
import gov.anl.ipns.ViewTools.Components.Region.*;
import gov.anl.ipns.ViewTools.Components.Cursor.SelectionJPanel;
import gov.anl.ipns.ViewTools.Components.ViewControls.CursorOutputControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.FieldEntryControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.PanViewControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.UI.FontUtil;
import gov.anl.ipns.Util.Sys.PrintComponentActionListener;
import gov.anl.ipns.Util.Sys.SaveImageActionListener;
import gov.anl.ipns.Util.Numeric.floatPoint2D;

/**
 * Simple class to display an image, specified by an IVirtualArray2D or a 
 * 2D array of floats, in a frame. This class adds further implementation to
 * the ImageFrame2.java class for thorough testing of the ImageViewComponent.
 */
public class Display2D extends JFrame implements IPreserveState,
                                                       Serializable
{ 
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
  
  public static final int IMAGE = 0;
  public static final int TABLE = 1;
  
  private static JFrame helper = null;
  
  // complete viewer, includes controls and ijp
  private transient Container pane;
  private transient IViewComponent2D ivc;
  private transient IVirtualArray2D data;
  private transient JMenuBar menu_bar;
  private String projectsDirectory = "";
  private transient Display2D this_viewer;
  private Vector Listeners = new Vector();
  private boolean os_region_added = false;
  private int current_view = 0;
  private boolean add_controls = true;
  
 /**
  * Construct a frame with the specified image and title
  *  
  *  @param  iva Two-dimensional virtual array.
  *  @param  view_code Code for which view component is to be used to
  *                    display the data.
  *  @param  include_ctrls If true, controls to manipulate image will be added.
  */
  public Display2D( IVirtualArray2D iva, int view_code, boolean include_ctrls )
  {
    // make sure data is not null
    if( iva == null )
    {
      System.out.println("Error in Display2D - Virtual Array is null");
      System.exit(-1);
    }
    this_viewer = this;
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Display2D");
    current_view = view_code;
    add_controls = include_ctrls;
    data = iva;    
    AxisInfo xinfo = data.getAxisInfo( AxisInfo.X_AXIS );
    AxisInfo yinfo = data.getAxisInfo( AxisInfo.Y_AXIS );
    buildMenubar();
    buildPane();
    
    // if Display2DProps.isv exists, load it into the ObjectState automatically.
    // This code will load user settings.
    String props = System.getProperty("user.home") + 
    		    System.getProperty("file.separator") +
    		    "Display2DProps.isv";
    ObjectState temp = getObjectState(IPreserveState.DEFAULT);
    temp.silentFileChooser(props,false);
    setObjectState(temp);
  }
 
 /**
  * This method sets the ObjectState of this viewer to a previously saved
  * state.
  *
  *  @param  new_state The previously saved state that this viewer will be
  *                    set to.
  */ 
  public void setObjectState( ObjectState new_state )
  {/*
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(PROJECT_FILE);
    if( temp != null )
    {
      datafile = (String)temp;
      loadData(datafile);
      redraw = true;  
    } 
    
    temp = new_state.get(IMAGE_VIEW_COMPONENT);
    if( temp != null )
    {
      Object os = ((ObjectState)temp).get(ImageViewComponent.SELECTION_OVERLAY);
      if( os != null )
      {
        os = ((ObjectState)os).get(SelectionOverlay.SELECTED_REGIONS);
	if( os != null )
          os_region_added = true;
      }
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
    
    temp = new_state.get(DATA_DIRECTORY); 
    if( temp != null )
    {
      projectsDirectory = (String)temp;
    } 
    
    if( redraw )
      repaint();*/
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
  {/*
    ObjectState state = new ObjectState();
    state.insert( IMAGE_VIEW_COMPONENT, ivc.getObjectState(isDefault) );
    state.insert( VIEWER_SIZE, getSize() );
    state.insert( DATA_DIRECTORY, new String(projectsDirectory) );
    // if they save the project, this file will automatically be loaded.
    if( !isDefault )
      state.insert( PROJECT_FILE, new String(datafile) );
    
    return state;*/
    return new ObjectState();
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
                "<P> SAVING USER PREFERENCES: Click on <B>File|Save User " +
		"Settings</B>. Your preferences will automatically be saved " +
		"in SandProps.isv in your home directory. <I>This option " +
		"will not save project specific information, such as " +
		"selections or annotations. Use <B>Options|Save Project " +
		"Settings</B> to save project specific details.</I><BR><BR>" +
		"<I>ATTENTION: Selections must be made before using the " +
		"viewing or saving results to file. </I><BR><BR> " +
		"VIEW RESULTS: The Results window will automatically appear " +
		"after a selection has been made. <B>Options|Hide Results " +
		"Window</B> will hide the window. If the window is not " +
		"visible, <B>Options|Show Results Window</B> will cause the " +
		"results window to appear.<BR>"+
		"SAVE RESULTS TO FILE: Go to <B>File|Save Results</B> " +
		"in the SWV. The new file has 3 columns: Q, Intensity, " +
		"and Error Bounds. Information about the region is listed " +
		"at the top of the file, prefixed by a pound symbol(#). " +
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
  * This method takes in a virtual array and updates the image. If the array
  * is the same data array, the image is just redrawn. If the array is
  * different, the a new view component is constructed.
  *
  *  @param  values
  */ 
  public void dataChanged( IVirtualArray2D values )
  { 
    ivc.dataChanged(data);
  }
  
 /**
  * This method takes in a 2D array and updates the image. If the array
  * is the same size as the previous data array, the image is just redrawn.
  * If the size is different, the frame is disposed and a new view component
  * is constructed.
  *
  *  @param  array
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
  * This method sets the pointed-at on this display.
  *
  *  @param  fpt
  */
  public void setPointedAt( floatPoint2D fpt )
  {
    //set the cursor position on the view component
    ivc.setPointedAt( fpt ); 
  }

 /**
  * This method gets the current floatPoint2D from the ImageJPanel and 
  * converts it to a Point.
  *
  *  @return  The current pointed-at world coordinate point as a floatPoint2D
  */
  public floatPoint2D getPointedAt()
  {
    return ivc.getPointedAt();
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
    ivc.setSelectedRegions(rgn);
  }
 
 /**
  * Get geometric regions created using the selection overlay.
  *
  *  @return selectedregions
  */ 
  public Region[] getSelectedRegions()
  {
    return ivc.getSelectedRegions();
  }

 /*
  * This method builds the content pane of the frame.
  */
  private void buildPane()
  { 
    if( current_view == IMAGE )
    {
      ivc = new ImageViewComponent( data );
      ((ImageViewComponent)ivc).setColorControlEast(true);
      ((ImageViewComponent)ivc).preserveAspectRatio(true);
    }
    if( current_view == TABLE )
    {
      System.out.println("Table view currently unavailable.");
    }
    ivc.addActionListener( new ViewCompListener() );    
    
    //Box componentholder = new Box(BoxLayout.Y_AXIS);
    //componentholder.add( ivc.getDisplayPanel() );
    Box view_comp_controls = buildControls();
    // if user wants controls, and controls exist, display them in a splitpane.
    if( add_controls && view_comp_controls != null )
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
    /* 
    // get menu items from view component and place it in a menu
    ViewMenuItem[] menus = ivc.getMenuItems();
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
    }*/
    // enable Print Image and Make Image menu items
    //menu_bar.getMenu(0).getItem(3).setEnabled(true); // print image
    //menu_bar.getMenu(0).getItem(4).setEnabled(true); // make image
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
    Vector options           = new Vector();
    Vector help              = new Vector();
    Vector save_results_menu = new Vector();
    Vector view_man  	     = new Vector();
    Vector save_menu 	     = new Vector();
    Vector load_menu 	     = new Vector();
    Vector save_default      = new Vector();
    Vector load_data         = new Vector();
    Vector swv_help          = new Vector();
    Vector print             = new Vector();
    Vector save_image        = new Vector();
    Vector exit              = new Vector();
    Vector file_listeners    = new Vector();
    Vector option_listeners  = new Vector();
    Vector help_listeners    = new Vector();
    
    // build file menu
    file.add("File");
    file_listeners.add( new MenuListener() ); // listener for file
    file.add(load_data);
      load_data.add("Load S(Qx,Qy)");
      file_listeners.add( new MenuListener() ); // listener for load data
    file.add(load_menu);
      load_menu.add("Open Project");
      file_listeners.add( new MenuListener() ); // listener for load project
    file.add(save_menu);
      save_menu.add("Save Project");
      file_listeners.add( new MenuListener() ); // listener for save project
    file.add(print);
      print.add("Print Image");
      file_listeners.add( new MenuListener() ); // listener for printing IVC
    file.add(save_image);
      save_image.add("Make Image (JPEG)");
      file_listeners.add( new MenuListener() ); // listener for saving IVC as jpg
    file.add(exit);
      exit.add("Exit");
      file_listeners.add( new MenuListener() ); // listener for exiting SWViewer
    
    // build options menu
    options.add("Options");
    option_listeners.add( new MenuListener() ); // listener for options
    options.add(view_man);
      view_man.add("Hide Results Window");
      option_listeners.add( new MenuListener() ); // listener for view results
    options.add(save_results_menu);
      save_results_menu.add("Save Results to File");
      option_listeners.add( new MenuListener() ); // listener for saving results
    options.add(save_default);
      save_default.add("Save User Settings");
      option_listeners.add( new MenuListener() ); // listener for saving results
    
    // build help menu
    help.add("Help");
    help_listeners.add( new MenuListener() );
    help.add( swv_help );
      swv_help.add("SAND Wedge Viewer");
      help_listeners.add( new MenuListener() );  // listener for SAND helper
           
    // add menus to the menu bar.
    setJMenuBar(null);
    menu_bar = new JMenuBar();
    menu_bar.add( MenuItemMaker.makeMenuItem(file,file_listeners) ); 
    menu_bar.add( MenuItemMaker.makeMenuItem(options,option_listeners) );
    menu_bar.add( MenuItemMaker.makeMenuItem(help,help_listeners) );
    
    setJMenuBar(menu_bar);
    
    // since the IVC is not created unless data is available,
    // do not load state unless data is available.
    if( data == null )
    {
      JMenu file_menu = menu_bar.getMenu(0);
      file_menu.getItem(2).setEnabled(false);   // disable Save Project Settings
      file_menu.getItem(3).setEnabled(false);   // disable Print Image
      file_menu.getItem(4).setEnabled(false);   // disable Make Image
      JMenu option_menu = menu_bar.getMenu(1);
      option_menu.getItem(0).setEnabled(false); // disable Hide Results Window
      option_menu.getItem(1).setEnabled(false); // disable Save Results
      option_menu.getItem(2).setEnabled(false); // disable Save User Settings
    }
  }
  
 /*
  * build controls for image view component.
  */ 
  private Box buildControls()
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
    if( ivc_ctrl.length == 0 )
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
  * This class is required to handle all messages within the Display2D.
  */
  private class MenuListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      if( ae.getActionCommand().equals("Save User Settings") )
      {
        String props = System.getProperty("user.home") + 
	                System.getProperty("file.separator") +
			"Display2DProps.isv";
	getObjectState(IPreserveState.DEFAULT).silentFileChooser(props,true);
      }
      else if( ae.getActionCommand().equals("Save Project") )
      {
	getObjectState(IPreserveState.PROJECT).openFileChooser(true);
      }
      else if( ae.getActionCommand().equals("Open Project") )
      {
        ObjectState state = new ObjectState();
	if( state.openFileChooser(false) )
	  setObjectState(state);
      }
      else if( ae.getActionCommand().equals("Print Image") )
      {/*
        // since the left component may change from data to data, only
	// get the component when printing has been asked for.
        JMenuItem silent_menu = PrintComponentActionListener.getActiveMenuItem(
	                                "not visible",
	                                pane.getLeftComponent() );
	silent_menu.doClick();*/
      }
      else if( ae.getActionCommand().equals("Make Image (JPEG)") )
      {/*
        // since the left component may change from data to data, only
	// get the component when saving has been asked for.
        JMenuItem silent_menu = SaveImageActionListener.getActiveMenuItem(
	                                "not visible",
	                                pane.getLeftComponent() );
	silent_menu.doClick();*/
      }
      else if( ae.getActionCommand().equals("SAND Wedge Viewer") )
      {
        help();
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
  * Tells all listeners about a new action.
  *
  *  @param  message
  */  
  private void sendMessage( String message )
  {
    for ( int i = 0; i < Listeners.size(); i++ )
    {
      ActionListener listener = (ActionListener)Listeners.elementAt(i);
      listener.actionPerformed( new ActionEvent( this, 0, message ) );
    }
  }
  
 /*
  * Main program.
  */
  public static void main( String args[] )
  {
    int row = 200;
    int col = 200;
    float test_array[][] = new float[row][col];
    for ( int i = 0; i < row; i++ )
      for ( int j = 0; j < col; j++ )
        test_array[i][j] = i - j;
    IVirtualArray2D va2D = new VirtualArray2D( test_array );
    va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f, 
    		        "TestX","TestUnits", true );
    va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f, 
    			"TestY","TestYUnits", false );
    va2D.setTitle("Display Test");
    Display2D display = new Display2D(va2D,Display2D.IMAGE,true);
    WindowShower shower = new WindowShower(display);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }

}
