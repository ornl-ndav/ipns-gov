/* 
 * File: VirtualArray2D_Displayable.java 
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
 * Modified:
 *
 * $Log$
 * Revision 1.16  2007/08/23 21:06:31  dennis
 * Removed unused imports.
 *
 * Revision 1.15  2007/08/09 13:02:07  rmikk
 * Eliminated the use of a Display1D to store the object state and used the Object state directly
 *
 * Attempted to eliminate all persistent references to Display1D
 *
 * Revision 1.14  2007/08/08 21:30:05  oakgrovej
 * Commenting and cleanup
 *   - Note: The setLineAttribute methods and their Hashtables are usless
 *                because there is no graph view for the Display2D
 *
 * Revision 1.13  2007/08/07 21:56:20  oakgrovej
 * GetJComponent() creates a copy of the Display2D and uses the component from that.
 *
 * Revision 1.12  2007/07/27 03:42:25  dennis
 * Fixed name inconsistency between javadoc and method.
 * Some public methods still need javadocs added.
 * Some public methods need to be checked to see if they should
 * be private.
 *
 * Revision 1.11  2007/07/26 22:12:33  amoe
 * Removed debug console prints.
 *
 * Revision 1.10  2007/07/26 19:42:08  oakgrovej
 * Test data for devices
 *
 * Revision 1.9  2007/07/20 21:50:16  oakgrovej
 * Builds the view Hashtables and can set some view attributes.  
 * Has test data for all three types: Table, Contour, and Image.  
 * The setLineAttribute() methods do not yet serve any purpose.
 *
 * Revision 1.8  2007/07/20 01:54:41  dennis
 * Fixed typo (double assignment to comp).
 *
 * Revision 1.7  2007/07/18 15:12:27  rmikk
 * Added GPL and public static strings for the view type
 *
 */
package gov.anl.ipns.DisplayDevices;

import java.awt.Color;

import gov.anl.ipns.ViewTools.Components.*;
//import gov.anl.ipns.ViewTools.Components.TwoD.*;
//import gov.anl.ipns.ViewTools.Components.TwoD.Contour.*;
//import gov.anl.ipns.ViewTools.Layouts.*;
import javax.swing.*;

//import java.awt.*;
import java.util.Hashtable;

import gov.anl.ipns.ViewTools.Displays.*;

public class VirtualArray2D_Displayable  extends Displayable {

   public static final String  TABLE = "Table";
   public static final String  IMAGE = "Image";
   public static final String  CONTOUR = "Contour";
   
   IVirtualArray2D array;
   String Type ;
   Display2D comp;
   Hashtable<String, Object> viewValList;
   Hashtable<String,String> viewAttributeList;
   Hashtable<String, Object> lineValList;
   Hashtable<String,String> lineAttributeList;
   
   ObjectState Ostate;

   /**
    *  Produces a displayable
    *  
    * @param array  The virtual array of data to view
    * @param Type   The type of view. It must be ImageV2D, TableV2D,and
    *                     ContourV2D so far.
    *                 
    * NOTE:  ComponentSwapper is not implemented
    */
   public VirtualArray2D_Displayable( IVirtualArray2D array, String Type)
                  throws IllegalArgumentException {

     super();
     this.array= array;
     this.Type = Type;
     viewValList = getViewValueTable();
     viewAttributeList = getViewAttributeTable();
     //lineValList = getLineValueTable();
     //lineAttributeList = getLineAttributeTable();
     
     if( array == null)
       throw new IllegalArgumentException( " No array of values");
      
     if( Type == null)
       throw new IllegalArgumentException( " No Type for view");
      
     if( ".Image.Table.Contour.".indexOf("."+Type+".") <  0 )
       throw new IllegalArgumentException( " Improper View Type");
      
     if( Type .equals( IMAGE)) {

       comp = new Display2D( array , Display2D.IMAGE, 1 );
       Ostate = comp.getObjectState( true);
      
     }else if( Type.equals( TABLE)){
         
       comp = new Display2D( array , Display2D.TABLE, 1 );;
       Ostate = comp.getObjectState( true);
      
     }else if( Type.equals( CONTOUR )) {   
         
       comp = new Display2D( array , Display2D.CONTOUR, 1 );;
       Ostate =comp.getObjectState( true);
       
     }else
       Ostate = new ObjectState();
     
     comp.dispose();
     comp = null;
            
      //showOstate( Ostate,1);
   }

   
   public void showOstate(){
       showOstate( Ostate,1 );
   }


   public void showOstate(ObjectState Ostate,  int nspaces ){
      
      if( Ostate == null)
         return;
       
       java.util.Enumeration En = Ostate.getKeys();
       Object e = En.nextElement();
       for( ; En.hasMoreElements();  e = En.nextElement()){
          Object st = Ostate.get( e );
          if( st instanceof ObjectState){
             for( int i=0; i<nspaces +2; i++) System.out.print(" ");
             System.out.println("**"+ e+"**" );
             showOstate((ObjectState)st, nspaces+4 );
          }else {
             for( int j=0; j< nspaces ; j++)System.out.print(" ");
             System.out.println( e+":::"+ st);
          }
       }
   }

   
   /**
    *  This method sets an attribute of the displayable that pertains
    *  to the overall display, such as a background color.
    *
    *  @param  name     The name of the attribute being set.
    *  @param  value    The value to use for the attribute.
    */
   public void  setViewAttribute(String name, Object value)throws Exception
   {
      if( name == null)
         return;
          
      if( value == null)
         return;
      
      name = name.toLowerCase();
      String attrib = (String) Util.TranslateKey(viewAttributeList, name);
      
      if(value instanceof String)
      {
        setViewAttribute(name,(String)value);
        return;
      }
      
      if( attrib.contains("Field Values"))
      {
        String[] valArray = (String[])Ostate.get(attrib);
        if( name.equals("minimum value"))
        {
          valArray[0] = value.toString();
        }
        
        else if( name.equals("maximum value"))
        {
          valArray[1] = value.toString();
        }
        
        else if( name.equals("number of levels"))
        {
          valArray[2] = value.toString();
        }
        
        value = valArray;
      }
      
      try
      {
        Ostate.reset(attrib, value);
      }
      catch(Exception e)
      {
        throw e;
      }
      
      //comp.setObjectState(Ostate);
    
      /*String S =  (String)XlateAttrNames.get(name);
      
      Object DT = Ostate.get( S );
      
      Object Oval = null;
      
      if( ObjectState.INVALID_PATH ==( DT))
         DT = null;
      
      if( DT != null)
      try{
         
          Oval =Util.cvrt( DT.getClass(), value);
          
      }catch( Exception s){
         
         Oval = null;
      }
      
      if( Oval == null)
         Ostate.insert( S, Oval) ;
      
      else if(! Ostate.reset( S, Oval))
         if( !Ostate.insert( S, Oval))
          System.out.println("Could not make the change")  ;*/
   }

   /**
    * This method sets a particular View Attribute to the specified Value
    * 
    * @param name The name of the Attribute to be set
    * @param value the name of the Value to set the Attribute to
    */
   public void setViewAttribute(String name, String value)throws Exception
   {
     name = name.toLowerCase();
     value = value.toLowerCase();
     
     String attrib = (String) Util.TranslateKey(viewAttributeList, name);
     Object val = Util.TranslateKey(viewValList, value);
     
     try
     {
       Ostate.reset(attrib, val);
     }
     catch(Exception e)
     {
       throw e;
     }
     //comp.setObjectState(Ostate);
   }
   
   
   /**
    *  This method sets an attribute of the displayable that pertains
    *  to a particular portion of the display, such as one particular
    *  line. 
    *
    *  @param  index    An index identifying the part of the display
    *                   that the attribute applies to, such as a 
    *                   specific line number.
    *  @param  name     The name of the attribute being set.
    *  @param  value    The value to use for the attribute.
    */
   public void setLineAttribute(int index,  
                                String name, 
                                Object value)throws Exception
   {   
      if( name == null)
         return;

      if( value == null)
         return;

      name = name.toLowerCase();
      String attribute = (String)Util.TranslateKey(viewAttributeList, name);
    
      Ostate.reset(attribute, value);  
   }

   /**
    *  This method sets an attribute of the displayable that pertains
    *  to a particular portion of the display, such as one particular
    *  line. 
    *
    *  @param  index    An index identifying the part of the display
    *                   that the attribute applies to, such as a 
    *                   specific line number.
    *  @param  name     The name of the attribute being set.
    *  @param  val      The name of the value to use for the attribute.
    */
   public void setLineAttribute(int index, 
                                String name, 
                                String val) throws Exception
   {
     name = name.toLowerCase();
     val = val.toLowerCase();
     //Ostate = comp.getObjectState(true); 
         
     String OSAttribute = (String)Util.TranslateKey(lineAttributeList,name);
     OSAttribute = lineAttributeList.get("graph data")+index+"."+OSAttribute;
     Object OSVal = Util.TranslateKey(lineValList,val);
     try
     {
       setLineAttribute(OSAttribute, OSVal);
     }
     catch(Exception e)
     {
       throw new Exception("Cannot put "+val+" into "+name);
     }
     //comp.setObjectState(Ostate);
   }

   /**
    * This method is used by the other setLineAttribute methods after
    * they have altered the Attribute name to be ObjectState specific.
    * 
    * @param name The ObjectState specific Attribute name
    * @param val The Value to set the Attribute to
    */
   private void setLineAttribute(String name, Object val)throws Exception
   {
     try
     {
       Ostate.reset(name, val);
     }
     catch(Exception e)
     {
       throw e;
     }
     //comp.setObjectState(Ostate);
   }
   
   
   /**
    *  This method returns a JComponent that can be displayed in a Frame,
    *  printed, or saved to a file.
    *
    *  @param  with_controls   If this is false, any interactive controls
    *                          associated with the view of the data will
    *                          NOT be visible on the JComponent
    *
    *  @return A reference to a JComponent containing the configured 
    *          display.
    */
   public JComponent getJComponent( boolean with_controls)
   {
     Display2D temp = null;
     if( Type.equals(IMAGE))
     {
       if(with_controls)
         temp = new Display2D(array,Display2D.IMAGE,Display.CTRL_ALL);
       else
         temp = new Display2D(array,Display2D.IMAGE,Display.CTRL_NONE);
     }
     else if( Type.equals(TABLE) )
     {
       if(with_controls)
         temp = new Display2D(array,Display2D.TABLE,Display.CTRL_ALL);
       else
         temp = new Display2D(array,Display2D.TABLE,Display.CTRL_NONE);
     }
     else if( Type.equals(CONTOUR) )
     {
       if(with_controls)
         temp = new Display2D(array,Display2D.CONTOUR,Display.CTRL_ALL);
       else
         temp = new Display2D(array,Display2D.CONTOUR,Display.CTRL_NONE);
     }
     temp.setObjectState( Ostate);//comp.getObjectState(false));
     JComponent comp =(JComponent)temp.getContentPane();
     temp.dispose();
     temp = null;
     return comp;
      /*if( !with_controls )
         Ostate.reset( Display2D.CONTROL_OPTION, Display.CTRL_NONE);
      else
         Ostate.reset(Display2D.CONTROL_OPTION, Display.CTRL_ALL );
      
      comp.setObjectState( Ostate );
      
      if( with_controls )
         return comp.getRootPane();
      else
         return (JComponent)comp.getContentPane();//*/
   }

   /**
    * This methods creates and returns a Hashtable containing the Value
    * names along with the Object they represent
    *  
    * @return The Hashtable
    */
   public Hashtable<String,Object> getViewValueTable()
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
   
   /**
    * This methods creates and returns a Hashtable containing the View Attribute
    * names along with their ObjectState specific path
    *  
    * @return The Hashtable
    */
   public static Hashtable<String,String> getViewAttributeTable()
   {
     Hashtable<String,String> temp = new Hashtable<String,String>();
     //---------------Contour------------\\
     temp.put("background color", 
         "View Component2.Layout handler key." +
         "Control panel key.Background color");//dosn't seem to do anything
     temp.put("minimum value", 
         "View Component2.Controls handler key.Contour controls key." +
         "Uniform contour state.Field Values");
     temp.put("maximum value", 
         "View Component2.Controls handler key.Contour controls key." +
         "Uniform contour state.Field Values");
     temp.put("number of levels", 
         "View Component2.Controls handler key.Contour controls key." +
         "Uniform contour state.Field Values");
     //----------------Image--------------\\
     temp.put("preserve aspect ratio","View Component0.Preserve Aspect Ratio");
     temp.put("two sided",
         "View Component0.Two Sided");//dosn't seem to do anything
     temp.put("color control east","View Component0.Color Control East");
     temp.put("color control west","View Component0.Color Control West");
     temp.put("controls","Control Option");//dosn't seem to do anything
     temp.put("horizontal scroll",
         "View Component0.ImageJPanel.Horizontal Scroll");
     //---------------Table---------------\\
     temp.put("show row labels",
         "View Component1.TableJPanel.Show Row Labels");
     temp.put("show column labels",
         "View Component1.TableJPanel.Show Column Labels");
     return temp;
   }

 
   /**
    * @param args
    */
   public static void main( String[] args ) throws Exception{
      VirtualArray2D v2d = new VirtualArray2D( 
               new float[][]{
                        {  1,1,1,1,1,1,1,1,1},
                        {  2,2,2,2,2,2,2,2,2 },
                        {  3,3,3,3,3,3,3,3,3},
                        {  4,4,4,4,4,4,4,4,4},
                        {  5,5,5,5,5,5,5,5,5},
                        {  6,6,6,6,6,6,6,6,6}
                        
               }); 
    VirtualArray2D_Displayable disp =  
        new VirtualArray2D_Displayable( v2d, "Contour");//*/
/*    VirtualArray2D_Displayable disp =  
        new VirtualArray2D_Displayable( v2d, "Table");//*/
/*    VirtualArray2D_Displayable disp =  
        new VirtualArray2D_Displayable( v2d, "Image");//*/
      
      //System.out.println(disp.comp.getObjectState(true));
      /*int[] styles = (int[])disp.comp.getObjectState(true).get(
          "View Component2.Layout handler key.Contour panel key.Line styles");
      for( int i = 0; i<styles.length;i++)
      {
        System.out.println("index :"+i+"\n"+styles[i]);
        
      }*/
    //-------Contour
      disp.setViewAttribute( "background color", "red");//does nothing
      disp.setViewAttribute( "minimum value", 2.5);
      disp.setViewAttribute( "maximum value", 5.5);
      disp.setViewAttribute( "number of levels", 4);//*/
      
/*    //-------Image
      disp.setViewAttribute( "preserve aspect ratio", "true");
      disp.setViewAttribute("two sided", false);//dosn't do anything
      disp.setViewAttribute("color control east", "false");
      disp.setViewAttribute("color control west", true);
      //disp.setViewAttribute("horizontal scroll", "true");//*/
      
/*    //--------Table
      disp.setViewAttribute("show row labels", false);
      disp.setViewAttribute("show column labels", "false");//*/
      
//    GraphicsDevice gd = new ScreenDevice();
//    GraphicsDevice gd = new FileDevice("/home/dennis/test.jpg");
    GraphicsDevice gd = new PreviewDevice();
//    GraphicsDevice gd = new PrinterDevice("Adobe PDF");
      
      // -------------For PrinterDevice
      //gd.setDeviceAttribute("orientation", "landscape");
      //gd.setDeviceAttribute("copies", 1);
    
      gd.setRegion( 200, 100, 600, 800 );
      gd.display( disp, true );
      gd.print();      
   }
  
}
