package gov.anl.ipns.DisplayDevices;

import javax.swing.JComponent;
import gov.anl.ipns.ViewTools.Components.OneD.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Displays.*;
import java.util.*;
import javax.swing.*;

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
      if(!Ostate.reset( name, value))
         Ostate.insert( name, value );

   }


   public void setLineAttribute( int index , String name , Object value ) {

      // TODO Auto-generated method stub

   }


   /**
    * @param args
    */
   public static void main( String[] args ) {
     
      float[] x1Vals ={1.0f, 2.1f, 3.2f, 6.8f, 10.2f };
      float[] x2Vals ={1.2f, 2.3f, 3.5f, 6f, 8f };
      float[] y1Vals ={1.0f, 2.0f, 3.0f, 4.0f, 5.0f };
      float[] y2Vals ={2.0f, 3f, 4f,5f, 6f };
      Vector V = new Vector( );
      V.add( new DataArray1D( x1Vals,y1Vals));
      V.add( new DataArray1D( x2Vals,y2Vals));
      VirtualArrayList1D vlist = new VirtualArrayList1D( V);
      
      VirtualArray1D_Displayable v1d = new VirtualArray1D_Displayable( vlist, GRAPH_DISPLAY);
      
      v1d.setViewAttribute( "View Component0.Graph JPanel.Graph Data1.Line Color", java.awt.Color.red);
      v1d.setViewAttribute( "View Component0.Graph JPanel.Graph Data2.Line Color", java.awt.Color.green);
      
      JFrame jf = new JFrame();
      jf.getContentPane().setLayout( new java.awt.GridLayout(1,1));
      jf.getContentPane().add( v1d.getJComponent( true ));
      jf.setSize( 300,400);
      jf.show();
      
                
      
     
      // TODO Auto-generated method stub

   }

}
