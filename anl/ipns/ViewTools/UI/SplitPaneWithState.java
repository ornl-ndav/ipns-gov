package DataSetTools.components.containers;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/**
 *  
 *  
 */
public class SplitPaneWithState extends    JSplitPane
                                         implements Serializable
 {
    private   float  divider_location = 0.5f;

    public SplitPaneWithState( int        orientation,
                               Component  first_component,
                               Component  second_component,
                               float      divider_location )
    {
      super( orientation, first_component, second_component );
      my_setDividerLocation( divider_location );
      addComponentListener( new ResizeHandler() );
    }

    public void my_setDividerLocation( float location )
    {
      if ( location >= 0.0 && location <= 1.0 )
      {
        this.divider_location = location;
        PositionDivider();
      }
      else
        System.out.println("ERROR: invalid location in " + 
                           "SplitPaneWithState.my_setDividerLocation" );
    }

    public void PositionDivider()
    {
       Dimension size    = getSize();
                                                   // Don't do anything if it
       if ( !isVisible() )                         // is not visible yet.
         return;
       if ( size.width <= 0 || size.height <=0 ) 
         return;

       int orientation   = getOrientation();
       int pix_location;

       if ( orientation == JSplitPane.HORIZONTAL_SPLIT )
         pix_location = (int)(divider_location * size.width);
       else
         pix_location = (int)(divider_location * size.height);

       setDividerLocation( pix_location );
      // repaint();
    }


   class ResizeHandler extends ComponentAdapter
   {
      public void componentResized( ComponentEvent c )
      {
/*
         int       pix_location  =  getDividerLocation();
         Dimension size          =  getSize();
         int       orientation   =  getOrientation();
         float     new_location  =  0;

         if ( orientation == JSplitPane.HORIZONTAL_SPLIT )
           new_location = ((float)pix_location)/((float)size.width);
         else
           new_location = ((float)pix_location)/((float)size.height);

         System.out.println("Resized called ....." + new_location);
         if ( Math.abs( new_location - divider_location ) > 0.01 )
         {
           divider_location = new_location;
           PositionDivider();
         }
*/
           PositionDivider();
      }
   }


}
