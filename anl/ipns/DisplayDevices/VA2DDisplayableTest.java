/* 
 * File: VA2DDisplayableTest
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

import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.OneD.DataArray1D;
import gov.anl.ipns.ViewTools.Components.OneD.VirtualArrayList1D;

import java.util.Vector;

public class VA2DDisplayableTest
{
	public static void main(String[]args)throws Exception
	{
		//Table
		//Image
		//Contour
		
		//Create data
	    //------------------------------------------------------
		VirtualArray2D v2d = new VirtualArray2D( 
	               new float[][]{
	                        {  1,1,1,1,1,1,1,1,1},
	                        {  2,2,2,2,2,2,2,2,2 },
	                        {  3,3,3,3,3,3,3,3,3},
	                        {  4,4,4,4,4,4,4,4,4},
	                        {  5,5,5,5,5,5,5,5,5},
	                        {  6,6,6,6,6,6,6,6,6},
	                        
	                        {0,5,10,15,20,25,30,10,12},
	                	    {0,10,12,3,20,25,20,25,30},
	                	    {0,5,10,15,20,25,30,36,30},
	                	    {0,20,30,35,40,36,30,25,30}
	                        
	              }); 
		
		
		//Make displayables
	    //------------------------------------------------------
		 VirtualArray2D_Displayable disp1 =  
		        new VirtualArray2D_Displayable( v2d, "Contour");//*/
		 VirtualArray2D_Displayable disp2 =  
		        new VirtualArray2D_Displayable( v2d, "Table");//*/
		 VirtualArray2D_Displayable disp3 =  
		        new VirtualArray2D_Displayable( v2d, "Image");//*/
		
		//Set Contour Attributes   note: these don't seem to do anything important
		//------------------------------------------------------
		 
		 //Displayable.setViewAttribute(disp1, "background color","green"); //these don't work
		 //Displayable.setViewAttribute(disp1, "minimum value",);       //possible problem
		 //Displayable.setViewAttribute(disp1, "maximum value",);     //setting the controls
		 //Displayable.setViewAttribute(disp1, "number of levels",20);//from object state

	     
		//Set Table Attributes
		//------------------------------------------------------
	    
		Displayable.setViewAttribute(disp2, "row labels", "true");
		Displayable.setViewAttribute(disp2,"column labels", "false");
		Displayable.setViewAttribute(disp2,"Label Background", "green");
		Displayable.setViewAttribute(disp2,"control option", "off");
	    
		
		//Set Image Attributes
		//-------------------------------------------------------
			    
	    Displayable.setViewAttribute(disp3, "preserve aspect ratio","true");
	    Displayable.setViewAttribute(disp3, "two sided","false");
	    Displayable.setViewAttribute(disp3, "color control east","true");
	    Displayable.setViewAttribute(disp3, "color control west","true");
	    Displayable.setViewAttribute(disp3, "control option","off");
		
//      DEVICE SELECTION
	    //_______________________________________________________________________
	    //choose your device by uncommenting, then comment out the others
	    
	    
	    /*//        Create Scrn Device
	    //____________________________________________________
	    GraphicsDevice gd = new ScreenDevice();
	    
	    //Set Scrn Device attributes
	    //-----------------------------------------  
	    //none//*/
	    
	    /*//        Create File Device
	    //____________________________________________________
	    String filePath = 
	    "C:/Documents and Settings/student/My Documents/My Pictures/test2.jpg";
	    GraphicsDevice gd = new FileDevice(filePath);
	    
	    //Set File Device attributes
	    //-----------------------------------------
	    Vector<Integer> vec = new Vector<Integer>();
	    vec.add(500);
	    vec.add(500);
	    //((FileDevice)gd).setBounds(vec);
	    //*/
	    
	    //        Create Printer Device
	    //____________________________________________________
	    GraphicsDevice gd = new PrinterDevice("Adobe PDF");
	    
	    //Set Printer Device Attributes
	    //-----------------------------------------
	    GraphicsDevice.setDeviceAttribute(gd,"orientation", "portrait");
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
	    System.out.println("Bounds[X,Y]\n"+gd.getBounds());
	    GraphicsDevice.setRegion(gd, 700, 400, 400, 400);
	    GraphicsDevice.display(gd, disp1, true );
	    GraphicsDevice.setRegion(gd, 700, 0, 400, 400);
	    GraphicsDevice.display(gd, disp2,true);
	    //System.out.print(disp3.Ostate);
	    GraphicsDevice.setRegion(gd, 0, 0, 700, 750 );
	    GraphicsDevice.display(gd, disp3,true);
	    GraphicsDevice.print(gd);		
	}
}
