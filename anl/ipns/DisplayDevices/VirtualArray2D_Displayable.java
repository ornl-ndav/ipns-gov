package gov.anl.ipns.DisplayDevices;

import java.awt.Color;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.TwoD.*;
import gov.anl.ipns.ViewTools.Components.TwoD.Contour.*;
import gov.anl.ipns.ViewTools.Layouts.*;
import javax.swing.*;
import java.awt.*;

public class VirtualArray2D_Displayable  extends Displayable {

   
   IVirtualArray2D array;
   String Type ;
   IViewComponent2D comp;
   java.util.Hashtable XlateAttrNames;
   
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
      XlateAttrNames = new java.util.Hashtable();
      if( array == null)
         throw new IllegalArgumentException( " No array of values");
      
      if( Type == null)
         throw new IllegalArgumentException( " No Type for view");
      
      if( ".ImageV2D.TableV2D.ContourV2D.".indexOf("."+Type+".")< 0)
         throw new IllegalArgumentException( " Improper View Type");
      
      if( Type .equals( "ImageV2D")) {
         
         comp = new ImageViewComponent( array );
         Ostate =((ImageViewComponent)comp).getObjectState( true);
         
         XlateAttrNames.put( "ColorModel", "Color Scale" );
         XlateAttrNames.put( "Axes Displayed" , "AxisOverlay2D.Axes Displayed");
      
      }else if( Type.equals( "TableV2D")){
         
         comp = new TableViewComponent( array );
         Ostate =((TableViewComponent)comp).getObjectState( true);
         //XlateAttrNames =
      
      }else if( Type.equals("ContourV2D")) {   
         
         comp = new ContourViewComponent( array );
         Ostate =((ContourViewComponent)comp).getObjectState( true);
         //XlateAttrNames =
      }else
         Ostate = new ObjectState();
      
      
      showOstate( Ostate,1);
    
      
   }
   
   public void showOstate(){
       showOstate( Ostate, 1);
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
             System.out.println( e );
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
   public void  setViewAttribute(String name, Object value){
      
      if( name == null)
         return;

      //if( name.toUpperCase().indexOf("Color")>=0)
      //    value =Convert2Color( value );   
      
      if( value == null)
         return;
      
      if(! Ostate.reset( XlateAttrNames.get(name), value))
         if( !Ostate.insert( XlateAttrNames.get(name), value))
          System.out.println("Could not make the change")  ;
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
   public void setLineAttribute(int index,  String name, Object value){
      
      
      if( name == null)
         return;

      if( name.toUpperCase().indexOf("Color")>=0)
          value =Convert2Color( value );   
      
      if( value == null)
         return;
      
      String S ;
      if( index >=10){
         
         S = (String) XlateAttrNames.get("Line_"+name+"__"+7);
         int k = S.indexOf("7");
         S = S.substring(0,k)+index+S.substring(k+1);
         
      }else
         S =(String) XlateAttrNames.get("Line_"+name+"__"+index);
      
      if(!Ostate.reset( S , value))
         Ostate.insert( S, value );
      
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
   public JComponent getJComponent( boolean live){
      
      comp.setObjectState( Ostate );
      return comp.getDisplayPanel();
   }
   
   public static Color Convert2Color( Object value){
      
      if( value == null )
         return null;
      
      if( value instanceof java.awt.Color )
         return (Color)value;
      
      if( !(value instanceof String ) )
         return null;
      
     String Col = ((String)value).toUpperCase().trim();
     
     if( Col.equals( "BLACK"))
        return Color.black;
     else if( Col.equals( "BLue"))
        return Color.blue;
     else      if( Col.equals( "RED"))
        return Color.red;
     else      if( Col.equals( "GREEN"))
        return Color.green;
     else      if( Col.equals( "GRAY"))
        return Color.GRAY;
     else      if( Col.equals( "CYAN"))
        return Color.CYAN;
     else      if( Col.equals( "ORANGE"))
        return Color.ORANGE;
     else      if( Col.equals( "MAGENTA"))
        return Color.MAGENTA;
     else if( Col.equals( "DARK GRAY"))
        return Color.DARK_GRAY;
     else if( Col.equals( "LIGHT GRAY"))
        return Color.LIGHT_GRAY;
     else if( Col.equals( "PINK"))
        return Color.PINK;
     else if( Col.equals( "YELLOW"))
        return Color.YELLOW;
     else 
        return null;
     
   }

   /**
    * @param args
    */
   public static void main( String[] args ) {
      String Type = "ImageV2D";
      VirtualArray2D v2d = new VirtualArray2D( 
               new float[][]{
                        {  1,1,1,1,1,1,1,1,1},
                        {  2,2,2,2,2,2,2,2,2 },
                        {  3,3,3,3,3,3,3,3,3},
                        {  4,4,4,4,4,4,4,4,4},
                        {  5,5,5,5,5,5,5,5,5},
                        {  6,6,6,6,6,6,6,6,6}
                        
               });
      VirtualArray2D_Displayable disp =  new VirtualArray2D_Displayable( v2d, "ImageV2D");
      
      disp.setViewAttribute("ColorModel", "Rainbow");
      disp.setViewAttribute("Axes Displayed", new Integer(2));
      JFrame jf = new JFrame("test");
      jf.getContentPane().setLayout( new GridLayout(1,1));
      jf.setSize( 400,400);
      jf.getContentPane().add( disp.getJComponent( false ));
      jf.show();
      System.out.println("============================");
      disp.showOstate();

   }

}
