/*
 * File:  IVCTester.java
 *
 * Copyright (C) 2003, Dennis Mikkelson, Mike Miller
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
 * Revision 1.25  2005/03/09 22:25:05  millermi
 * - Added capability to test ImageViewComponent with the ArrayGenerator.
 *
 * Revision 1.24  2005/01/18 23:11:00  millermi
 * - Listeners that previously listened for events from the
 *   SelectionOverlay now listen for the SELECTED_CHANGED event
 *   from the ImageViewComponent.
 *
 * Revision 1.23  2004/09/15 21:55:44  millermi
 * - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *   Adding a second log required the boolean parameter to be changed
 *   to an int. These changes may affect any ObjectState saved configurations
 *   made prior to this version.
 *
 * Revision 1.22  2004/05/11 00:50:07  millermi
 * - Removed unused variables.
 *
 * Revision 1.21  2004/04/07 01:23:04  millermi
 * - Added menu to test marker capabilities.
 *
 * Revision 1.20  2004/04/05 03:11:21  millermi
 * - Moved WindowShower code out of constructer and into main(),
 *   causing the IVCTester not to be displayed by default.
 *
 * Revision 1.19  2004/03/15 23:53:49  dennis
 * Removed unused imports, after factoring out the View components,
 * Math and other utils.
 *
 * Revision 1.18  2004/03/12 03:18:18  millermi
 * - Changed package, fixed imports.
 *
 * Revision 1.17  2004/03/10 23:37:27  millermi
 * - Changed IViewComponent interface, no longer
 *   distinguish between private and shared controls/
 *   menu items.
 * - Combined private and shared controls/menu items.
 *
 * Revision 1.16  2004/02/27 23:58:23  millermi
 * - Added "New Data" to File menu to test if data is updated
 *   when new data is passed into the ImageViewComponent
 *   via the dataChanged(new_array) method.
 *
 * Revision 1.15  2004/02/19 23:24:55  millermi
 * - Added preserveAspectRatio() to ImageViewComponent,
 *   the image dimensions are now similar to that of the selected
 *   region.
 * - Fixed SELECTED_CHANGED message problems with ImageViewComponent.
 *
 * Revision 1.14  2004/02/13 22:56:22  millermi
 * - setData() now compares array references instead of data
 *   dimensions.
 *
 * Revision 1.13  2004/02/06 22:15:30  millermi
 * - Changed test for ObjectState.
 *
 * Revision 1.12  2004/01/29 08:16:25  millermi
 * - Updated the getObjectState() to include parameter for specifying
 *   default state.
 * - Added static variables DEFAULT and PROJECT to IPreserveState for
 *   use by getObjectState()
 *
 * Revision 1.11  2004/01/07 22:35:15  millermi
 * - main() now sets negative values in the VirtualArray to
 *   allow for better testing.
 *
 * Revision 1.10  2003/12/20 03:35:41  millermi
 * - changed comments and code on how Float.NaN is checked.
 *
 * Revision 1.9  2003/12/18 22:42:12  millermi
 * - This file was involved in generalizing AxisInfo2D to
 *   AxisInfo. This change was made so that the AxisInfo
 *   class can be used for more than just 2D axes.
 *
 * Revision 1.8  2003/12/17 20:28:50  millermi
 * - Removed references to ImageViewComponent.COMPONENT_RESIZED.
 *   This was originally added to refresh the ImageViewCompoennt,
 *   but recent changes have fixed this problem without requiring
 *   this reference.
 *
 * Revision 1.7  2003/12/12 06:13:00  millermi
 * - Introduced variables for row/column sizes in main(),
 *   this makes testing various sizes less tedious.
 *
 * Revision 1.6  2003/11/21 01:26:35  millermi
 * - Commented test code out that was missed in last checkin.
 *
 * Revision 1.5  2003/11/21 00:31:31  millermi
 * - Minor improvements, working to get PanViewControl updated
 *   when divider is resized.
 *
 * Revision 1.4  2003/11/18 00:59:20  millermi
 * - Now implements Serializable, requiring many private variables
 *   to be made transient.
 * - Added Load and Save options to the file menu for testing th
 *   loading and saving of state information.
 *
 * Revision 1.3  2003/09/23 23:14:39  millermi
 * - Added getObjectState() and setObjectState() to preserve
 *   state information.
 *
 * Revision 1.2  2003/08/14 17:06:33  millermi
 * - Controls now contained in a Box instead of a JPanel.
 * - Added spacer JPanel to bottom of the Box, glue did not work well.
 *
 * Revision 1.1  2003/08/11 23:47:22  millermi
 * - Initial Version - adds additional features to the ImageFrame2.java
 *   class for thorough testing of the ImageViewComponent.
 *
 * 
 **********************************************************************
 * Revision 1.1  2003/08/07 15:58:58  dennis
 * - Further implementation of basic structure provided in
 *   ImageFrame.java.
 * - Uses an ImageViewComponent instead of ImageJPanel, which
 *   allows for controls to tweak the image.
 *   (Mike Miller)
 *
 * Revision 1.3  2003/06/05 14:35:09  dennis
 * Added method to set a new image and change the title on the frame.
 *
 * Revision 1.2  2003/01/08 20:11:37  dennis
 * Now shows blank frame and writes error message if a null image is
 * specified.
 *
 * Revision 1.1  2003/01/08 19:23:05  dennis
 * Initial version
 *
 */

package gov.anl.ipns.ViewTools.Components;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import gov.anl.ipns.ViewTools.Components.TwoD.ArrayGenerator;
import gov.anl.ipns.ViewTools.Components.TwoD.ImageViewComponent;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.UI.SplitPaneWithState;
import gov.anl.ipns.ViewTools.Components.Transparency.Marker;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlSlider;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * Simple class to display an image, specified by an IVirtualArray2D or a 
 * 2D array of floats, in a frame. This class adds further implementation to
 * the ImageFrame2.java class for thorough testing of the ImageViewComponent.
 */
public class IVCTester extends JFrame implements IPreserveState,
                                                 Serializable
{
  // complete viewer, includes controls and ijp
  private transient SplitPaneWithState pane;
  private transient ImageViewComponent ivc;
  private transient IVirtualArray2D data;
  private transient JMenuBar menu_bar;
  private ObjectState state;
  private boolean aspect = false;

 /**
  * Construct a frame with the specified image and title
  *  
  *  @param  iva
  */
  public IVCTester( IVirtualArray2D iva )
  {
    data = new VirtualArray2D(1,1);
    menu_bar = new JMenuBar();
    setJMenuBar(menu_bar);
    state = new ObjectState();
    state.setProjectsDirectory(System.getProperty("user.home"));
    // build File menu 
    Vector file = new Vector();
    Vector new_menu = new Vector();
    Vector save_menu = new Vector();
    Vector load_menu = new Vector();
    Vector new_data  = new Vector();
    Vector add_marker = new Vector();
    Vector listeners = new Vector();
    listeners.add( new ImageListener() );
    listeners.add( new ImageListener() );
    listeners.add( new ImageListener() );
    listeners.add( new ImageListener() );
    listeners.add( new ImageListener() );
    listeners.add( new ImageListener() );
    file.add("File");
    file.add(new_menu);
      new_menu.add("Toggle Aspect Ratio");
    file.add(save_menu);
      save_menu.add("Save State");
    file.add(load_menu);
      load_menu.add("Load State");
    file.add(new_data);
      new_data.add("New Data");
    file.add(add_marker);
      add_marker.add("Add Marker at Center");
    
    menu_bar.add( MenuItemMaker.makeMenuItem(file,listeners) ); 
    menu_bar.add(new JMenu("Options"));
    menu_bar.add(new JMenu("Help"));
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(0,0,700,500);
    
    setData(iva);
    ivc.addActionListener( new IVCListener() );
  }

 /**
  * Construct a frame with the specified image and title
  *  
  *  @param  array
  *  @param  xinfo
  *  @param  yinfo
  *  @param  title
  */  
  public IVCTester( float[][] array, 
                    AxisInfo xinfo,
		    AxisInfo yinfo,
		    String title )
  {
    VirtualArray2D temp = new VirtualArray2D( array );
    temp.setAxisInfo( AxisInfo.X_AXIS, xinfo.copy() );
    temp.setAxisInfo( AxisInfo.Y_AXIS, yinfo.copy() );
    temp.setTitle(title);
    
    data = new VirtualArray2D(1,1);
    
    menu_bar = new JMenuBar();
    setJMenuBar(menu_bar);   
    menu_bar.add(new JMenu("File")); 
    menu_bar.add(new JMenu("Options"));
    menu_bar.add(new JMenu("Help"));
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(0,0,700,500);
    
    setData(temp);
  }
    
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    ivc.setObjectState(new_state);
    Boolean preserve = (Boolean)
        ivc.getObjectState(false).get(ImageViewComponent.PRESERVE_ASPECT_RATIO);
    if( preserve != null )
      aspect = preserve.booleanValue();
    //repaint();
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
    return ivc.getObjectState(isDefault);
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
    // if new array is same size as old array
    if( values == data )
    { 
      ivc.dataChanged();
    }  
    // if different sized array, remove everything and build again.
    else
    {
      getContentPane().removeAll();
      data = values;
      buildPane();
      getContentPane().add(pane);
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

 /*
  * This method builds the content pane of the frame.
  */
  private void buildPane()
  { 
    if( data != null )
    { 
      setTitle( data.getTitle() );
      ivc = new ImageViewComponent( data );
      ivc.addActionListener( new ImageListener() );
      Box controls = new Box(BoxLayout.Y_AXIS);
      ViewControl[] ctrl = ivc.getControls();
      for( int i = 0; i < ctrl.length; i++ )
      {
    	controls.add(ctrl[i]);
      }
      JPanel spacer = new JPanel();
      // the value 60 is an arbitrary value for the average component height
      //spacer.setPreferredSize(new Dimension(0,(this.getHeight() - 
      //					 (ctrl.length*60) ) ) );
      spacer.setPreferredSize( new Dimension(0, 10000) );
      controls.add(spacer);
      //controls.addComponentListener( new ResizedPaneListener() );
      JPanel image_holder = new JPanel();
      image_holder.setLayout( new GridLayout(1,1) );
      image_holder.add( ivc.getDisplayPanel() );
      pane = new SplitPaneWithState(JSplitPane.HORIZONTAL_SPLIT,
    				    image_holder,
    				    controls, .75f );
      // get menu items from view component and place it in a menu
      ViewMenuItem[] menus = ivc.getMenuItems();
      for( int i = 0; i < menus.length; i++ )
      {
    	if( ViewMenuItem.PUT_IN_FILE.toLowerCase().equals(
    	    menus[i].getPath().toLowerCase()) )
    	  menu_bar.getMenu(0).add( menus[i].getItem() ); 
    	else if( ViewMenuItem.PUT_IN_OPTIONS.toLowerCase().equals(
    		 menus[i].getPath().toLowerCase()) )
    	  menu_bar.getMenu(1).add( menus[i].getItem() );     
    	else if( ViewMenuItem.PUT_IN_HELP.toLowerCase().equals(
    		 menus[i].getPath().toLowerCase()) )
    	  menu_bar.getMenu(2).add( menus[i].getItem() );
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
  * This class is required to update the axes when the divider is moved. 
  * Without it, the image is one frame behind.
  */
  private class ImageListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      if( ae.getActionCommand().equals("Toggle Aspect Ratio") )
      {
        aspect = !aspect;
        ivc.preserveAspectRatio(aspect);
      }
      else if( ae.getActionCommand().equals("Save State") )
      {
        state = ivc.getObjectState(IPreserveState.PROJECT);
	state.openFileChooser(true);
      }
      else if( ae.getActionCommand().equals("Load State") )
      {
        state = ivc.getObjectState(IPreserveState.PROJECT);
	state.openFileChooser(false);
	setObjectState(state);
      }
      else if( ae.getActionCommand().equals("New Data") )
      {
        if( data instanceof ArrayGenerator )
	{
          int rows = data.getNumRows()/10;
	  int cols = data.getNumColumns()/10;
	  IVirtualArray2D temp = data;
	  data = new ArrayGenerator(cols,rows);
	  // Transfer all meta data over to new array.
	  data.setAxisInfo(AxisInfo.X_AXIS, temp.getAxisInfo(AxisInfo.X_AXIS) );
	  data.setAxisInfo(AxisInfo.Y_AXIS, temp.getAxisInfo(AxisInfo.Y_AXIS) );
	  data.setAxisInfo(AxisInfo.Z_AXIS, temp.getAxisInfo(AxisInfo.Z_AXIS) );
	  data.setTitle(temp.getTitle());
	  temp = null;
	  ivc.dataChanged(data);
	}
	else
	{
	  int row = 200;
    	  int col = 300;
    	  float test_array[][] = new float[row][col];
    	  for ( int i = 0; i < row; i++ )
    	    for ( int j = 0; j < col; j++ )
    	      test_array[i][j] = i * j;
	  IVirtualArray2D va2D = new VirtualArray2D( test_array );
    	  va2D.setAxisInfo( AxisInfo.X_AXIS, -100f, 100f, 
    			    "New X","New Units", AxisInfo.LINEAR );
    	  va2D.setAxisInfo( AxisInfo.Y_AXIS, -150f, 150f,     
     			    "New Y","New Y Units", AxisInfo.LINEAR );
          va2D.setTitle("New IVC Test");
	  ivc.dataChanged(va2D);
        }
	repaint();
      }
      else if( ae.getActionCommand().equals("Add Marker at Center") )
      {
        floatPoint2D mark_pt = new floatPoint2D( 5000f, 750f );
        Marker center_mark = new Marker( Marker.STAR, mark_pt, Color.red,
	                                 5f, Marker.RESIZEABLE );
        ivc.addMarker( center_mark );
      }
    }
  }
  
 /*
  * This class listeners for selections made by the selection overlay.
  * Since this is a test program, this listener will MODIFY THE DATA.
  */ 
  private class IVCListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      // Since regions are so big, this calculation becomes unavailable if
      // the ArrayGenerator is used.
      if( data instanceof ArrayGenerator )
        return;
      String message = ae.getActionCommand();
      if( message.equals(ImageViewComponent.SELECTED_CHANGED) )
      {
  	Region[] selectedregions = ivc.getSelectedRegions();
	// Make sure regions exist.
	if( selectedregions.length < 1 )
	  return;
        Point[] selectedpoints = 
	          selectedregions[selectedregions.length-1].getSelectedPoints();
        //System.out.println("NumSelectedPoints: " + selectedpoints.length);
        for( int j = 0; j < selectedpoints.length; j++ )
        {
	  int row = selectedpoints[j].y;
	  int col = selectedpoints[j].x;
	  
	  if( !Float.isNaN(data.getDataValue(row,col)) )
	  {
	    data.setDataValue( row, col, data.getDataValue(row,col) * 2f );
          }
	  //System.out.println("(" + selectedpoints[j].x + "," + 
          //      	     selectedpoints[j].y + ")" );
        }
        ivc.dataChanged();
      }
    }
  }
  
 /*
  * Testing purposes only
  */
  public static void main( String args[] )
  {
    IVirtualArray2D va2D;
    if( args.length > 0 && args[0].equals("virtual") )
    {
      int row = 3000000;
      int col = 4000000;
      va2D = new ArrayGenerator( row,col );
      va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f, 
    		        "TestX","TestUnits", AxisInfo.LINEAR );
      va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f, 
    			"TestY","TestYUnits", AxisInfo.TRU_LOG );
    }
    else
    {
      int row = 200;
      int col = 200;
      float test_array[][] = new float[row][col];
      for ( int i = 0; i < row; i++ )
        for ( int j = 0; j < col; j++ )
          test_array[i][j] = i - j;
      va2D = new VirtualArray2D( test_array );
    }
    va2D.setTitle("IVCTester");
    ObjectState state = new ObjectState();
    ObjectState sliderstate = new ObjectState();
    sliderstate.insert(ControlSlider.SLIDER_VALUE, new Float(20) );
    state.insert( ImageViewComponent.LOG_SCALE_SLIDER, sliderstate );
    state.insert( ImageViewComponent.PRESERVE_ASPECT_RATIO, new Boolean(true));
    
    IVCTester im_frame = new IVCTester( va2D );
    im_frame.setObjectState(state);
    // display IVCTester
    WindowShower shower = new WindowShower(im_frame);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }

}
