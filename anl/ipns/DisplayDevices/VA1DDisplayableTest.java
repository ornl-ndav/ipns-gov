/* 
 * File: VA1DDisplayableTest
 *
 * Copyright (C) 2008, Joshua Oakgrove 
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package gov.anl.ipns.DisplayDevices;

import gov.anl.ipns.ViewTools.Components.OneD.DataArray1D;
import gov.anl.ipns.ViewTools.Components.OneD.VirtualArrayList1D;

import java.util.Vector;

public class VA1DDisplayableTest
{
	public static void main(String[]args) throws Exception
	{

		//Graph
		//Table
		
		//Create data
	    //------------------------------------------------------
		float[] xvals = {0,5,10,15,20,25,30};
	    float[] yvals = {0,10,12,3,20,25,20};
	    float[] yvals2 = {0,5,10,15,20,25,30};
	    float[] yvals3 = {0,20,30,35,40,36,30};
	     
	    DataArray1D data1 = new DataArray1D(xvals,yvals);
	    DataArray1D data2 = new DataArray1D(xvals,yvals2);
	    DataArray1D data3 = new DataArray1D(xvals,yvals3);
	    Vector<DataArray1D> data = new Vector<DataArray1D>();
	    data.add(data1);
	    data.add(data2);
	    data.add(data3);
	     
	    VirtualArrayList1D array = new VirtualArrayList1D(data);
	    array.setGraphTitle("Graph 1", 0);
	    array.setGraphTitle("Graph 2", 1);
	    array.setGraphTitle("Graph 3", 2);
	    array.setTitle( "Graph" );
	    array.setAxisInfo( 0 , 0 , 35 ,"x-axis" ,"hours", 0 );
	    array.setAxisInfo( 1 , 0 , 35 ,"y-axis" ,"dollars", 0 );
		
		//Make displayables
	    //------------------------------------------------------
	    VirtualArray1D_Displayable disp1 = 
	        new VirtualArray1D_Displayable(array,"GraphV1D");//*/
	    VirtualArray1D_Displayable disp2 = 
	        new VirtualArray1D_Displayable(array,"TableV1D");//*/
		
		//Set Graph Attributes
		//------------------------------------------------------
	    
	  //------line 1
	    Displayable.setLineAttribute(disp1, 1, "line color", "red");
	    Displayable.setLineAttribute(disp1, 1, "line tYpe", "doTtEd");
	    Displayable.setLineAttribute(disp1, 1, "Mark Type", "plus");
	    //Displayable.setLineAttribute(disp1, 1,"transparent", "true");
	    Displayable.setLineAttribute(disp1, 1, "Mark color", "cyan");
	    
	    //------line 2
	    Displayable.setLineAttribute(disp1, 2, "line color", "black");
	    Displayable.setLineAttribute(disp1, 2, "line tYpe", "dashdot");
	    Displayable.setLineAttribute(disp1, 2, "Mark Type", "bar");
	    Displayable.setLineAttribute(disp1, 2, "Mark color", "red");
	    Displayable.setLineAttribute(disp1, 2, "line width", 6f);
	    Displayable.setLineAttribute(disp1, 2, "Mark size", 3);
	                                                      
	    //-----line 3
	    Displayable.setLineAttribute(disp1, 3, "line color", "cyan");
	    Displayable.setLineAttribute(disp1, 3, "line width", 5f);
	    Displayable.setLineAttribute(disp1, 3, "line tYpe", "dashed");
	    Displayable.setLineAttribute(disp1, 3, "Mark type", "cross");
	    Displayable.setLineAttribute(disp1, 3, "Mark color", "orange");
	    Displayable.setLineAttribute(disp1, 3, "Mark size", 50);
	    
	    Displayable.setViewAttribute(disp1, "grid lines x", "on");
	    Displayable.setViewAttribute(disp1, "grid color", "blue");
	    Displayable.setViewAttribute(disp1, "legend", "true");
	    
		//Set Table Attributes
		//------------------------------------------------------
	    Displayable.setViewAttribute(disp2, "row labels", "true");
	    Displayable.setViewAttribute(disp2, "column labels", "false");
	    Displayable.setViewAttribute(disp2, "Label Background", "green");
	    Displayable.setViewAttribute(disp2, "control option", "off");
	    
//      DEVICE SELECTION
	    //_______________________________________________________________________
	    //choose your device by uncommenting, then comment out the others
	    
	    
	    //        Create Scrn Device
	    //____________________________________________________
	    GraphicsDevice gd = new ScreenDevice();
	    
	    //Set Scrn Device attributes
	    //-----------------------------------------  
	    //none//*/
	    
	    /*//        Create File Device
	    //____________________________________________________
	    String filePath = 
	    "C:/Documents and Settings/student/My Documents/My Pictures/test.jpg";
	    GraphicsDevice gd = new FileDevice(filePath);
	    
	    //Set File Device attributes
	    //-----------------------------------------
	    Vector<Integer> vec = new Vector<Integer>();
	    vec.add(500);
	    vec.add(500);
	    //((FileDevice)gd).setBounds(vec);
	    //*/
	    
	    /*//        Create Printer Device
	    //____________________________________________________
	    GraphicsDevice gd = new PrinterDevice("Adobe PDF");
	    
	    //Set Printer Device Attributes
	    //-----------------------------------------
	    GraphicsDevice.setDeviceAttribute(gd, "orientation", "portrait");
	    //GraphicsDevice.setDeviceAttribute(gd, "orientation", "Landscape");
	    //GraphicsDevice.setDeviceAttribute(gd, "copies", 3);
	    GraphicsDevice.setDeviceAttribute(gd, "mediasize", "posterc");
	    //GraphicsDevice.setDeviceAttribute(gd, "mediasize", "postera");
	    //GraphicsDevice.setDeviceAttribute(gd, "mediasize", "Letter");
	    //GraphicsDevice.setDeviceAttribute(gd, "mediasize", "a");
	    //GraphicsDevice.setDeviceAttribute(gd, "mediasize", "Legal");//*/
	    
	    /*//        Create Preview Device
	    //____________________________________________________
	    GraphicsDevice gd = new PreviewDevice();
	    
	    //Set Preview Device Attributes
	    //-----------------------------------------
	    //none*/
	    
	    //Print out
	    //____________________________________________________
	    System.out.println("Bounds[X,Y]\n"+GraphicsDevice.getBounds(gd));
	    GraphicsDevice.setRegion(gd, 20, 0, 800, 750 );
	    GraphicsDevice.display(gd, disp1, true);
	    GraphicsDevice.setRegion(gd, 20, 800, 800, 150);
	    GraphicsDevice.display(gd, disp2,true);
	    GraphicsDevice.print(gd);

	}
}
