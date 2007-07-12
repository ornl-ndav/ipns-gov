package gov.anl.ipns.DisplayDevices;

import javax.swing.JComponent;
import gov.anl.ipns.ViewTools.Components.OneD.*;
import gov.anl.ipns.ViewTools.Components.*;

public class VirtualArray1D_Displayable extends Displayable {
   
   IVirtualArrayList1D array;
   String Type;
   
   IViewComponent1D comp ;
   ObjectState Ostate ;
   public static String FUNCTION_DISPLAY = "FunctionV1D";
   public static String DIFFERENCE_DISPLAY = "DifferenceV1D";

   /**
    * Creates a Displayable of the given type  from a list of 1D arrays.
    * 
    * @param array  the list of 1D arrays to display
    * @param Type   the type of display needed.  The only values so far ar
    *                    "FunctionV1D" and "DifferenceV1D"
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
      
      if( Type.equals( FUNCTION_DISPLAY )){
         
         comp = new FunctionViewComponent( array );
      
      }else if( Type.equals( DIFFERENCE_DISPLAY)){
         
         comp = new DifferenceViewComponent( array );
         
      }else
         throw new IllegalArgumentException( 
                       "This view type cannot display this data ");
      
      Ostate = comp.getObjectState( true );
   }


   public JComponent getJComponent( boolean with_controls ) {
   
      comp.setObjectState( Ostate );
      // TODO Auto-generated method stub
      return comp.getDisplayPanel();
   }


   public void setViewAttribute( String name , Object value ) {

      // TODO Auto-generated method stub

   }


   public void setLineAttribute( int index , String name , Object value ) {

      // TODO Auto-generated method stub

   }


   /**
    * @param args
    */
   public static void main( String[] args ) {

      // TODO Auto-generated method stub

   }

}
