/*
 * $Log$
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



import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.FunctionTable;
import DataSetTools.dataset.UniformXScale;
import DataSetTools.viewer.IViewManager;
import DataSetTools.viewer.ViewManager;
import gov.anl.ipns.ViewTools.Components.OneD.*;
import gov.anl.ipns.ViewTools.Components.TwoD.Contour.ContourViewComponent;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Displays.*;
import gov.anl.ipns.ViewTools.Panels.Graph.GraphJPanel;
import java.util.*;
import javax.swing.*;

public class VirtualArray1D_Displayable extends Displayable {
   
   IVirtualArrayList1D array;
   String Type;
   
   Display1D comp ;
   ObjectState Ostate ;
   public static String GRAPH = "GraphV1D";
   public static String TABLE = "TableV1D";
   private Hashtable<String, Object> viewValueList; 
   private Hashtable<String,String> viewAttributeList;

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
      viewValueList = getViewValueList();
      viewAttributeList = getViewAttributeList();
      //System.out.println(Ostate);
   }


   public JComponent getJComponent( boolean with_controls ) {
   
      if( with_controls)
         Ostate.reset( Display1D.CONTROL_OPTION, Display.CTRL_ALL);
      else
         Ostate.reset( Display1D.CONTROL_OPTION, Display.CTRL_NONE);
      
      comp.setObjectState( Ostate );
      
      if( with_controls)
         return comp.getRootPane();
      else
         return (JComponent)comp.getContentPane();
   }

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

   public void setViewAttribute( String name , String value ) throws Exception
   {
     Object OSVal = null;
     name = name.toLowerCase();
     value = value.toLowerCase();
     Ostate = comp.getObjectState(true); 
     
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
       OSVal = Util.TranslateKey(viewValueList,value);
     
     try
     {
       Ostate.reset(OSAttribute, OSVal);
     }
     catch(Exception e)
     {
       throw e;
     }
     comp.setObjectState(Ostate);
   }
   

   public void setLineAttribute(int index, 
                                    String Attribute, 
                                    String val) throws Exception
   {
     Attribute = Attribute.toLowerCase();
     val = val.toLowerCase();
     Ostate = comp.getObjectState(true); 
     Hashtable<String, Object> values = getGraphLineValueList();
     Hashtable<String,String> selectedGraphDataTable = 
       getGraphLineAttributeList();
     
     String OSAttribute = (String)Util.TranslateKey(selectedGraphDataTable,Attribute);
     OSAttribute = selectedGraphDataTable.get("graph data")+index+"."+OSAttribute;
     Object OSVal = Util.TranslateKey(values,val);
     try
     {
       setLineAttribute(OSAttribute, OSVal);
     }
     catch(Exception e)
     {
       throw new Exception("Cannot put "+val+" into "+Attribute);
     }
     comp.setObjectState(Ostate);
   }
   
   public void setLineAttribute(int index, 
                                 String Attribute, 
                                 Object val) throws Exception
   {
     Attribute = Attribute.toLowerCase();
     Ostate = comp.getObjectState(true);
     Hashtable<String, Object> values = getGraphLineValueList();
     Hashtable<String,String> selectedGraphDataTable = 
       getGraphLineAttributeList();
     
     String OSAttribute = (String)Util.TranslateKey(selectedGraphDataTable,Attribute);
     OSAttribute = selectedGraphDataTable.get("graph data")+index+"."+OSAttribute;
     try
     {
       setLineAttribute(OSAttribute, val);
     }
     catch(Exception e)
     {
       throw new Exception("Cannot put "+val+" into "+Attribute);
     }
     comp.setObjectState(Ostate);
   }

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
   
   public static Hashtable<String,Object> getViewValueList()
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
     temp.put("true", true);
     temp.put("false", false);
     temp.put("off", 0);
     temp.put("on", 1);
     return temp;
   }
   
   public static Hashtable<String,Object> getGraphLineValueList()
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
     temp.put("true", true);
     temp.put("false", false);
     temp.put("dot", GraphJPanel.DOT);
     temp.put("plus", GraphJPanel.PLUS);
     temp.put("star", GraphJPanel.STAR);
     temp.put("bar", GraphJPanel.BAR);
     temp.put("cross", GraphJPanel.CROSS);
     return temp;
   }
   
   public void setVisible(boolean visible)
   {
     comp.setVisible(true);
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
     Vector<DataArray1D> data = new Vector();
     data.add(data1);
     data.add(data2);
     data.add(data3);
     
     VirtualArrayList1D array = new VirtualArrayList1D(data);
     array.setGraphTitle("Graph 1", 0);
     array.setGraphTitle("Graph 2", 1);
     array.setGraphTitle("Graph 3", 2);
     VirtualArray1D_Displayable disp = 
       new VirtualArray1D_Displayable(array,GRAPH);//*/
     /*VirtualArray1D_Displayable disp = 
       new VirtualArray1D_Displayable(array,TABLE);//*/
     System.out.println(disp.comp.getObjectState(true));
     
     //--------------graph test
     disp.setViewAttribute("legend", "true");
     disp.setViewAttribute("grid lines x", "on");
     disp.setViewAttribute("grid lines y","on");
     disp.setViewAttribute("grid color","red");
//   disp.setLineAttribute(1, "line type", "dashdot");
   disp.setLineAttribute(1, "line color", "red");
   disp.setLineAttribute(1, "line width", 2f);
//   disp.setLineAttribute(2, "line color", "black");
   disp.setLineAttribute(1, "mark type", "cross");
   disp.setLineAttribute(1, "mark size", 2);
//   disp.setLineAttribute(1, "mark color", "green");
//   disp.setLineAttribute(3, "line type", "dashed");
//   disp.setLineAttribute(3, "mark type", "plus");
//   disp.setLineAttribute(3, "mark color", "cyan");
//   disp.setViewAttribute("viewer size","500,500");
     
     //------------table test
//   disp.setViewAttribute("label background","red");
//   disp.setViewAttribute("row labels", "false");
//   disp.setViewAttribute("column labels", "false");
     
//   disp.setViewAttribute("control option", "off");// doesn't do anything
     
//   GraphicsDevice gd = new ScreenDevice();
//   GraphicsDevice gd = new FileDevice("/home/dennis/test.jpg");
//   GraphicsDevice gd = new PreviewDevice();
   GraphicsDevice gd = new PrinterDevice("Adobe PDF");
     
     // -------------For PrinterDevice
     //gd.setDeviceAttribute("orientation", "landscape");
     //gd.setDeviceAttribute("copies", 1);
   
     gd.setRegion( 0, 0,701, 800 );
     gd.display( disp, true );
     gd.print();
   }
}
