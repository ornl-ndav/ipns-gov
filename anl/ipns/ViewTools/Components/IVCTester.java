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

package DataSetTools.components.View;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import DataSetTools.components.View.TwoD.ImageViewComponent;
import DataSetTools.components.View.Menu.ViewMenuItem;
import DataSetTools.components.image.*;
import DataSetTools.components.containers.SplitPaneWithState;
import DataSetTools.components.View.Transparency.SelectionOverlay;
import DataSetTools.components.View.Region.Region;

/**
 * Simple class to display an image, specified by an IVirtualArray2D or a 
 * 2D array of floats, in a frame. This class adds further implementation to
 * the ImageFrame2.java class for thorough testing of the ImageViewComponent.
 */
public class IVCTester extends JFrame
{
  private SplitPaneWithState pane; // complete viewer, includes controls and ijp
  private ImageViewComponent ivc;
  private IVirtualArray2D data;
  private JMenuBar menu_bar;

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
    menu_bar.add(new JMenu("File")); 
    menu_bar.add(new JMenu("Options"));
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(0,0,700,500);
    
    setData(iva);
    ivc.addActionListener( new IVCListener() );
    setVisible(true);
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
                      AxisInfo2D xinfo,
		      AxisInfo2D yinfo,
		      String title )
  {
    VirtualArray2D temp = new VirtualArray2D( array );
    temp.setAxisInfoVA( AxisInfo2D.XAXIS, xinfo.copy() );
    temp.setAxisInfoVA( AxisInfo2D.YAXIS, yinfo.copy() );
    temp.setTitle(title);
    
    data = new VirtualArray2D(1,1);
    
    menu_bar = new JMenuBar();
    setJMenuBar(menu_bar);   
    menu_bar.add(new JMenu("File")); 
    menu_bar.add(new JMenu("Options"));
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(0,0,700,500);
    
    setData(temp);
    setVisible(true);
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
    if( values.getNumRows() == data.getNumRows() &&
        values.getNumColumns() == data.getNumColumns() )
    {  
      data = values;
      ivc.dataChanged(data);
    }  
    // if different sized array, remove everything and build again.
    else
    {
      dispose();
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
    setTitle( data.getTitle() );
    ivc = new ImageViewComponent( data );
    ivc.setColorControlSouth(true);
    ivc.addActionListener( new ImageListener() );
    JPanel controls = new JPanel();
    controls.setLayout( new BoxLayout( controls, BoxLayout.Y_AXIS) );
    JComponent[] ctrl = ivc.getSharedControls();
    for( int i = 0; i < ctrl.length; i++ )
      controls.add(ctrl[i]);
    pane = new SplitPaneWithState(JSplitPane.HORIZONTAL_SPLIT,
                                  ivc.getDisplayPanel(),
			          controls, .75f );
    
    // get menu items from view component and place it in a menu
    ViewMenuItem[] menus = ivc.getSharedMenuItems();
    
    for( int i = 0; i < menus.length; i++ )
    {
       if( ViewMenuItem.PUT_IN_FILE.toLowerCase().equals(
    		menus[i].getPath().toLowerCase()) )
    	  menu_bar.getMenu(0).add( menus[i].getItem() ); 
       else // put in options menu
    	  menu_bar.getMenu(1).add( menus[i].getItem() );	   
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
      if( ae.getActionCommand().equals(ImageViewComponent.COMPONENT_RESIZED) )
        repaint();
    }
  }
  
 /*
  * This class listeners for selections made by the selection overlay
  */ 
  private class IVCListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
      if( message.equals(SelectionOverlay.REGION_ADDED) )
      {
  	Region[] selectedregions = ivc.getSelectedRegions();
        Point[] selectedpoints = 
	          selectedregions[selectedregions.length-1].getSelectedPoints();
        //System.out.println("NumSelectedPoints: " + selectedpoints.length);
        for( int j = 0; j < selectedpoints.length; j++ )
        {
	  int row = selectedpoints[j].y;
	  int col = selectedpoints[j].x;
	  
	  data.setDataValue( row, col, data.getDataValue(row,col) + 100f );
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
    float test_array[][] = new float[500][500];
    for ( int i = 0; i < 500; i++ )
      for ( int j = 0; j < 500; j++ )
        test_array[i][j] = i + j;
    VirtualArray2D va2D = new VirtualArray2D( test_array );
    va2D.setAxisInfoVA( AxisInfo2D.XAXIS, 0f, 10000f, 
    		        "TestX","TestUnits", true );
    va2D.setAxisInfoVA( AxisInfo2D.YAXIS, 0f, 1500f, 
    			"TestY","TestYUnits", false );
    va2D.setTitle("ImageFrame Test");
    IVCTester im_frame = new IVCTester( va2D );
    /*
    // test setData() 10 times
    for( int x = 0; x < 20; x++ )
    {
      for ( int i = 0; i < 500; i++ )
        for ( int j = 0; j < 500; j++ )
          test_array[i][j] = i*j;
      va2D = new VirtualArray2D( test_array );
      im_frame.setData(va2D);
    }
    
    IVCTester im_frame2 = new IVCTester( test_array,
                                             new AxisInfo2D( 0f, 10000f,"TestX",
			                                    "TestUnits", true ),
                                             new AxisInfo2D( 0f, 1500f,"TestY",
			                                   "TestYUnits", true ),
					     "ImageFrame Alternate Test" );
    im_frame2.setData( test_array );*/
  }

}
