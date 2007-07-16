package gov.anl.ipns.DisplayDevices;

import javax.swing.JComponent;
import gov.anl.ipns.ViewTools.Components.OneD.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Displays.*;

public class VirtualArray1D_Displayable extends Displayable {
   
   IVirtualArrayList1D array;
   String Type;
   
   Display1D comp ;
   ObjectState Ostate ;
   public static String GRAPH_DISPLAY = "GraphV1D";
   public static String TABLE_DISPLAY = "TableV1D";

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
      
      if( Type.equals( GRAPH_DISPLAY ))
         
         comp = new Display1D( array, Display1D.GRAPH, Display.CTRL_ALL);
      
       else if( Type.equals( TABLE_DISPLAY ))
          comp = new Display1D( array, Display1D.TABLE, Display.CTRL_ALL);
         
      else
         throw new IllegalArgumentException( 
                       "This view type cannot display this data ");
      
      Ostate = comp.getObjectState( true );
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
