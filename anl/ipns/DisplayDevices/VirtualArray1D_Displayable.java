/*
 * File: VirtualArray1D_Displayable.java 
 *  
 * Copyright (C) 2007     Ruth Mikkelson
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
 * Contact :  Ruth Mikkelson<mikkelsonr@uwstout.edu>
 *            MSCS Department
 *            Menomonie, WI. 54751
 *            (715)-235-8482
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 *
 *  $Author: eu7 $
 *  $Date: 2008-04-08 16:31:08 -0500 (Tue, 08 Apr 2008) $            
 *  $Revision: 19031 $
 *
 * Modified:
 *
 * $Log$
 * Revision 1.23  2007/09/09 23:31:04  dennis
 * Commented out an unused variable.
 * Reformated log messages to fit in 80 characters.
 *
 * Revision 1.22  2007/09/06 16:30:08  worlton
 * Modified the main routine to test laying out multiple graphs on a 
 * printed page.  This shows how it is done, but also highlights a problem 
 * with the left and right margins.  It looks like margins are set to 1" 
 * whether you want it or not.  The top and bottom margins don't seem to do 
 * that.
 *
 * The problem is particularly evident for the portrait plot.  The graphs 
 * are narrower than they need to be.  We should allow using all available 
 * space.
 *
 * Revision 1.21  2007/08/23 21:06:31  dennis
 * Removed unused imports.
 *
 * Revision 1.20  2007/08/22 15:36:36  rmikk
 * Added GPL
 * Disposed of several components
 * The main test program has changed and may not be runnable as is
 * Added Titles for picture, x axis and yaxis
 *
 * Revision 1.19  2007/08/14 20:05:49  worlton
 * Revised the main routine so that the file and printer devices will have 
 * valid locations.  Also removed a printout that failed on my system.  The 
 * saved file will now go in the user home directory and the printer device 
 * will use the default printer.  These changes should be tested on a Linux 
 * system with printers.
 *
 * Revision 1.18  2007/08/09 12:57:14  rmikk
 * Eliminated the use of a Display1D to store the object state and used 
 * the Object state directly
 *
 * Attempted to eliminate all persistent references to Display1D
 *
 * Revision 1.17  2007/08/08 20:45:55  oakgrovej
 * Commenting and clean up
 *
 * Revision 1.16  2007/08/07 21:55:22  oakgrovej
 * GetJComponent() uses the boolean with_controls.
 *
 * Revision 1.15  2007/08/07 21:21:20  oakgrovej
 * GetJComponent() now creates a copy of the Display1D
 *
 * Revision 1.14  2007/08/07 16:41:57  oakgrovej
 * Changed name of the value "line" to "solid".
 *
 * Revision 1.13  2007/08/07 16:18:30  oakgrovej
 * Added "line" option to the Value list
 *
 * Revision 1.12  2007/08/03 18:41:59  oakgrovej
 * Combined the value Hashtables
 *
 * Revision 1.11  2007/08/03 16:28:32  oakgrovej
 *  - Added setLineAttribute() method that takes in an Object for the value.
 *  - Added some more colors.
 *
 * Revision 1.10  2007/08/03 14:55:18  oakgrovej
 * - Added attributes for grid lines, grid color and legend.
 * - Made View Hashtables private fields instead of creating
 *   them locally in the method there used in
 *
 * Revision 1.9  2007/07/26 17:54:02  amoe
 * Added CVS log tag.
 *
 */
package gov.anl.ipns.DisplayDevices;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.print.*;



import gov.anl.ipns.ViewTools.Components.OneD.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Displays.*;
import gov.anl.ipns.ViewTools.Panels.Graph.GraphJPanel;

public class VirtualArray1D_Displayable extends Displayable {
   
   IVirtualArrayList1D array;
   String Type;
   
   Display1D comp ;
   ObjectState Ostate ;
   public static String GRAPH = "GraphV1D";
   public static String TABLE = "TableV1D";
   private Hashtable<String, Object> valueList; 
   private Hashtable<String,String> viewAttributeList;
   private Hashtable<String,String> graphLineAttributes;

   /**
    * Creates a Displayable of the given type  from a list of 1D arrays.
    * 
    * @param array  the list of 1D arrays to display
    * @param Type   the type of display needed.  The only values so far ar
    *                    "GraphV1D, TableV1D" 
    */
   public VirtualArray1D_Displayable( IVirtualArrayList1D array, String Type)
       throws IllegalArgumentException{

      super();
      this.array = array;
      this.Type = Type;
      if( array == null)
         throw new IllegalArgumentException
              (" Cannot make a Displayable with null data");
      
      if( Type == null )
         throw new IllegalArgumentException(
                       "The Type of the display is needed ");
      
      if( Type.equals( GRAPH ))
         
         comp = new Display1D( array, Display1D.GRAPH, Display.CTRL_ALL);
      
       else if( Type.equals( TABLE ))
          comp = new Display1D( array, Display1D.TABLE, Display.CTRL_ALL);
         
      else
         throw new IllegalArgumentException( 
                       "This view type cannot display this data ");
      
      Ostate = comp.getObjectState( true );
      valueList = getValueList();
      viewAttributeList = getViewAttributeList();
      graphLineAttributes = getGraphLineAttributeList();
      comp.removeAll();
      comp.dispose();
      comp=null;
      //System.out.println(Ostate);
   }

   /**
    * returns a the ContentPanecopy of a copy of the Display1D comp 
    * 
    * @param with_controls flag indicating if the JComponent should 
    *                      have controls.
    * @return The ContentPane of the Display1D copy 
    */
   public JComponent getJComponent( boolean with_controls ) 
   {
     Display1D temp = null;
     if( Type.equals(GRAPH))
     {
       if(with_controls)
         temp = new Display1D(array,Display1D.GRAPH,Display.CTRL_ALL);
       else
         temp = new Display1D(array,Display1D.GRAPH,Display.CTRL_NONE);
     }
     else if( Type.equals(TABLE) )
     {
       if(with_controls)
         temp = new Display1D(array,Display1D.TABLE,Display.CTRL_ALL);
       else
         temp = new Display1D(array,Display1D.TABLE,Display.CTRL_NONE);
     }
     temp.setObjectState(Ostate);
     JComponent comp =(JComponent)temp.getContentPane();
     //temp.removeAll();
     temp.dispose();
     temp = null;
     return comp;
     /*if( with_controls)
         Ostate.reset( Display1D.CONTROL_OPTION, Display.CTRL_ALL);
      else
         Ostate.reset( Display1D.CONTROL_OPTION, Display.CTRL_NONE);
      
      comp.setObjectState( Ostate );
      
      if( with_controls)
         return comp.getRootPane();
      else
         return (JComponent)comp.getContentPane();//*/
   }
   
   /**
    * This method takes in the name of a View Attribute and sets it to the
    * Value passed in.
    * 
    * @param name The name of the View Attribute to be altered
    * @param value the Value to set the Attribute to
    */
   public void setViewAttribute( String name , Object value) throws Exception
   {
     if( value instanceof String )
     {
       setViewAttribute( name, (String) value);
       return;
     }
     
     name = name.toLowerCase();
     String attribute = (String)Util.TranslateKey(viewAttributeList, name);
     
     Ostate.reset(attribute, value);
    
     comp.setObjectState(Ostate);
   }
   
   /**
    * This method takes in the name of a View Attribute and the name of the 
    * Value to set it to
    * 
    * @param name The name of the View Attribute to be altered
    * @param value The name of the Value to set the Attribute to.
    */
   public void setViewAttribute( String name , String value ) throws Exception
   {
     Object OSVal = null;
     name = name.toLowerCase();
     value = value.toLowerCase();
     //Ostate = comp.getObjectState(true); 
     
     String OSAttribute = (String)Util.TranslateKey(viewAttributeList,name);
     if( Ostate.get(OSAttribute) instanceof Dimension)
     {
       String checkedVal = "";
       Boolean hasComma = false;
       
       //--------Test value for proper format
       //--------Format will be forced if possible 
       //--------Else checkedVal will = null
       for( int i = 0; i < value.length(); i++)
       {
         
         if ( Character.isDigit(value.charAt(i)) )
         {
           checkedVal += value.charAt(i);
         }
         else if( value.charAt(i) == ',' )
         {
           if( !hasComma && checkedVal.length()>=1 )
           {
             checkedVal += value.charAt(i);
             hasComma = true;
           }
         }
       }
       if( !hasComma )
         checkedVal = null;
       else if( checkedVal.indexOf(",") == checkedVal.length()-1)
         checkedVal = null;
       
       //System.out.println(checkedVal);
       if( checkedVal != null )
       {
         int index = checkedVal.indexOf(",");
         String xStr = checkedVal.substring(0,index);
         String yStr = checkedVal.substring(index + 1);
         int xVal = Integer.parseInt(xStr);
         int yVal = Integer.parseInt(yStr);
         OSVal = new Dimension(xVal,yVal);
       }
     }
     else
       OSVal = Util.TranslateKey(valueList,value);
     
     try
     {
       Ostate.reset(OSAttribute, OSVal);
     }
     catch(Exception e)
     {
       throw e;
     }
    // comp.setObjectState(Ostate);
   }
   
   /**
    * This method sets the indicated line's Attribute to the Value
    * 
    * @param index The Index of the Line
    * @param Attribute The name of the Attribute to be altered
    * @param val The name of the Value 
    */
   private void setLineAttribute(int index, 
                                    String Attribute, 
                                    String val) throws Exception
   {
     Attribute = Attribute.toLowerCase();
     val = val.toLowerCase();
     //Ostate = comp.getObjectState(true); 
     
     String OSAttribute = (String)Util.TranslateKey(graphLineAttributes,Attribute);
     OSAttribute = graphLineAttributes.get("graph data")+index+"."+OSAttribute;
     Object OSVal = Util.TranslateKey(valueList,val);
     try
     {
       setLineAttribute(OSAttribute, OSVal);
     }
     catch(Exception e)
     {
       throw new Exception("Cannot put "+val+" into "+Attribute);
     }
     //comp.setObjectState(Ostate);
   }
   
   /**
    * This method sets the indicated line's Attribute to the Value.
    * 
    * @param index The Index of the Line
    * @param Attribute The name of the Attribute to be altered
    * @param val The Value to set to the Attribute
    */
   public void setLineAttribute(int index, 
                                 String Attribute, 
                                 Object val) throws Exception
   {
	 if(val instanceof String)
	  	setLineAttribute(index,Attribute,(String)val);
	 else{  
	   Attribute = Attribute.toLowerCase();
      // Ostate = comp.getObjectState(true);
     
       String OSAttribute = (String)Util.TranslateKey(graphLineAttributes,Attribute);
       OSAttribute = graphLineAttributes.get("graph data")+index+"."+OSAttribute;
       try
       {
         setLineAttribute(OSAttribute, val);
       }
       catch(Exception e)
       {
         throw new Exception("Cannot put "+val+" into "+Attribute);
       }
	 }
     //comp.setObjectState(Ostate);
   }

   /**
    * This method is used by the other setLineAttribute methods after
    * they have altered the Attribute name to be ObjectState specific.
    * 
    * @param Attribute The ObjectState specific Attribute name
    * @param Val The Value to set the Attribute to
    */
   private void setLineAttribute(String Attribute, 
                                 Object Val) throws Exception
   {
     try
     {
       Ostate.reset(Attribute, Val);
     }
     catch(Exception e)
     {
       throw e;
     }
   }
   
   /**
    * This methods creates and returns a Hashtable containing the View Attribute
    * names along with their ObjectState specific path
    *  
    * @return The Hashtable
    */
   public static Hashtable<String,String> getViewAttributeList()
   {
     Hashtable<String,String> temp = new Hashtable<String,String>();
     temp.put("row labels", "View Component1.TableJPanel.Show Row Labels");
     temp.put("column labels", "View Component1.TableJPanel.Show Column Labels");
     temp.put("label background", "View Component1.TableJPanel.Label Background");
     temp.put("control option","Control Option");
     temp.put("legend", "View Component0.FunctionControls.Legend Control.Selected");
     temp.put("grid lines x", "View Component0.AxisOverlay2D.Grid Display X");
     temp.put("grid lines y", "View Component0.AxisOverlay2D.Grid Display Y");
     temp.put("grid color", "View Component0.AxisOverlay2D.Grid Color");
//     temp.put("viewer size","Viewer Size"); the device will take care of size
     //temp.put();
     //temp.put();
     return temp;
   }
   
   /**
    * This methods creates and returns a Hashtable containing the Graph Line
    * Attribute names along with their ObjectState specific name
    *  
    * @return The Hashtable
    */
   public static Hashtable<String,String> getGraphLineAttributeList()
   {
     Hashtable<String,String> temp = new Hashtable<String,String>();
     temp.put("line type", "Line Type");
     temp.put("line color", "Line Color");
     temp.put("line width", "Line Width");
     temp.put("mark type", "Mark Type");
     temp.put("mark color", "Mark Color");
     temp.put("mark size", "Mark Size");
     temp.put("transparent", "Transparent");
     temp.put("graph data", 
              "View Component0.Graph JPanel.Graph Data");
     return temp;
   }
   
   /**
    * This methods creates and returns a Hashtable containing the Value
    * names along with the Object they represent
    *  
    * @return The Hashtable
    */
   public static Hashtable<String,Object> getValueList()
   {
     Hashtable<String,Object> temp = new Hashtable<String,Object>();
     temp.put("black", Color.black);
     temp.put("white", Color.white);
     temp.put("blue", Color.blue);
     temp.put("cyan", Color.cyan);
     temp.put("green", Color.green);
     temp.put("orange", Color.orange);
     temp.put("red", Color.red);
     temp.put("yellow", Color.yellow);
     temp.put("gray", Color.gray);
     temp.put("light gray", Color.lightGray);
     temp.put("dotted", GraphJPanel.DOTTED);
     temp.put("dashed", GraphJPanel.DASHED);
     temp.put("dashdot", GraphJPanel.DASHDOT);
     temp.put("none", 0);
     temp.put("off", 0);
     temp.put("on", 1);
     temp.put("true", true);
     temp.put("false", false);
     temp.put("solid", GraphJPanel.LINE);
     temp.put("dot", GraphJPanel.DOT);
     temp.put("plus", GraphJPanel.PLUS);
     temp.put("star", GraphJPanel.STAR);
     temp.put("bar", GraphJPanel.BAR);
     temp.put("cross", GraphJPanel.CROSS);
     return temp;
   }
   
   /**
    * Sets the components visibility
    * 
    * @param visible flag indicating visibility
    */
   public void setVisible(boolean visible)
   {
     comp.setVisible(visible);
   }


   /**
    * @param args
    */
   public static void main( String[] args ) throws Exception
   {
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
     VirtualArray1D_Displayable disp = 
       new VirtualArray1D_Displayable(array,GRAPH);//*/
     /*VirtualArray1D_Displayable disp = 
       new VirtualArray1D_Displayable(array,TABLE);//*/
     //System.out.println(disp.comp.getObjectState(true));
     
     //--------------graph test
     disp.setViewAttribute("legend", "true");
     disp.setViewAttribute("grid lines x", "on");
     disp.setViewAttribute("grid lines y","on");
     disp.setViewAttribute("grid color","gray");
   disp.setLineAttribute(1, "line type", "dashdot");
   //disp.setLineAttribute(1, "line type", "solid");
   disp.setLineAttribute(1, "line color", "light gray");
   disp.setLineAttribute(1, "line width", 2f);
//   disp.setLineAttribute(2, "line color", "black");
   disp.setLineAttribute(1, "mark type", "cross");
   disp.setLineAttribute(1, "mark size", 2);
   
/*  // For testing good finis
    FinishJFrame jf = new FinishJFrame("Test");
    jf.getContentPane().setLayout( new java.awt.GridLayout(1,1));
    jf.getContentPane().add(  disp.getJComponent( false ) );
    jf.setSize( 400,500);
    WindowShower.show(  jf  );
*/   
    disp.setLineAttribute(1, "mark color", "green");
    disp.setLineAttribute(3, "line type", "dashed");
    disp.setLineAttribute(3, "mark type", "plus");
    disp.setLineAttribute(3, "mark color", "cyan");
    // disp.setViewAttribute("viewer size","500,500");
     
     //------------table test
//   disp.setViewAttribute("label background","red");
//   disp.setViewAttribute("row labels", "false");
//   disp.setViewAttribute("column labels", "false");
     
//   disp.setViewAttribute("control option", "off");// doesn't do anything
     
  //  GraphicsDevice gd3 = new ScreenDevice();
   
 /*  JFileChooser jf = new JFileChooser();
      String filename = null;
      if( jf.showSaveDialog( null )== JFileChooser.APPROVE_OPTION)
         filename = jf.getSelectedFile().getPath();
      else
         System.exit(0);
   
    GraphicsDevice gd3= new FileDevice( filename );
 */
  //    GraphicsDevice gd4= new FileDevice("c:\\xx.jpg" );
 //  GraphicsDevice gd3 = new PreviewDevice();
  /*GraphicsDevice gd3 = new PrinterDevice("HP LaserJet 4 Plus");
  gd3.setDeviceAttribute( "orientation" , "landscape" );
  Vector  bounds = gd3.getBounds();
  float width = ((Float)bounds.firstElement()).floatValue();
  float height = ((Float)bounds.lastElement()).floatValue();*/
 
   // float width =576;
   // float height = 768;
  // GraphicsDevice gd3 = new ScreenDevice();
   //GraphicsDevice gd2= new FileDevice("C:/Documents and Settings/student/My Documents/My Pictures/test.jpg");
   String outfile = System.getProperty("user.home") + "/test.jpg";
   GraphicsDevice gd2 = new FileDevice(outfile);

 
   GraphicsDevice gd1 = new PreviewDevice();

   // -------------gd is PrinterDevice
   //GraphicsDevice gd = new PrinterDevice("Adobe PDF");
	PrintService defserv = PrintServiceLookup.lookupDefaultPrintService();
	String defname = defserv.getName();
	GraphicsDevice gd = new PrinterDevice(defname);
	System.out.println(" Printed on " + defname );  
     
	//test multiple plots on a page
    VirtualArray1D_Displayable disp2 = 
        new VirtualArray1D_Displayable(array,GRAPH);//*/
     gd.setDeviceAttribute("orientation", "landscape");
     //gd.setDeviceAttribute("copies", 1);
     Vector  bounds = gd.getBounds();
     int width = ((Float)bounds.firstElement()).intValue();
     int height = ((Float)bounds.lastElement()).intValue();
     System.out.println("bounds = " + width + ", " + height);
     int halfheight = height/2;
//     int halfwidth  = width/2;
     int x0 = 0;
     int y0 = 0;
//     int x1 = halfwidth;
     int y1 = halfheight;
     gd.setRegion(x0, y0, width, halfheight);
     gd.display( disp, true );
     gd.setRegion(x0, y1, width, halfheight);
     gd.display( disp2, true );
     gd.print();
     //create second device with 2x2 layout
 	 GraphicsDevice gdp2 = new PrinterDevice(defname);
     gd2.setDeviceAttribute("orientation", "portrait");
     Vector  bounds2 = gdp2.getBounds();
     int width2 = ((Float)bounds2.firstElement()).intValue();
     int height2 = ((Float)bounds2.lastElement()).intValue();
     System.out.println("bounds2 = " + width2 + ", " + height2);
     int halfheight2 = height2/2;
     int halfwidth2  = width2/2;
     int x20 = 0;
     int y20 = 0;
     int x21 = halfwidth2;
     int y21 = halfheight2;
     gdp2.setRegion(x20, y20, halfwidth2, halfheight2);
     disp.setLineAttribute(1, "line color", "green");
     gdp2.display( disp, true );
     gdp2.setRegion(x21, y20, halfwidth2, halfheight2);
     disp2.setLineAttribute(2, "line color", "orange");
     gdp2.display(disp2, true);
     gdp2.setRegion(x21, y21, halfwidth2, halfheight2);
     disp.setLineAttribute(1, "line color", "red");
     gdp2.display(disp, true);
     gdp2.print();
     
     //gd1 is preview device
     gd1.setRegion( 0, 0,700, 800 );
     gd1.display( disp, true );
     gd1.print();
     
     //gd2 is file device
     gd2.setRegion( 0, 0,700, 800 );
     gd2.display( disp, true );
     gd2.print();
     
     //gd3 was file device, now commented out
     //gd3.setRegion( 200, 0,700, 800 );
     //gd3.display( disp, true );
     //gd3.print();
   }

}
