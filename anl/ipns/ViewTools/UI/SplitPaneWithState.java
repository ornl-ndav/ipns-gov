/**
 * @(#)SplitPaneWithState.java 
 *
 * Programmer: Dennis Mikkelson
 *  
 * $Log$
 * Revision 1.3  2001/01/29 21:33:36  dennis
 * Now uses CVS version numbers.
 *
 * Revision 1.2  2000/07/10 22:04:44  dennis
 * Not done...partly modified for JDK 1.2.2
 *
 * Revision 1.3  2000/06/12 20:12:10  dennis
 * internal class now implements Serializable
 *
 *
 */
package DataSetTools.components.containers;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

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
/*
       int orientation   = getOrientation();
       int pix_location;

       if ( orientation == JSplitPane.HORIZONTAL_SPLIT )
         pix_location = (int)(divider_location * size.width);
       else
         pix_location = (int)(divider_location * size.height);

       setDividerLocation( pix_location );
*/
       setDividerLocation( (double)divider_location );
    }


   class ResizeHandler extends    ComponentAdapter
                       implements Serializable
   {
      public void componentResized( ComponentEvent c )
      {
        PositionDivider();
      }
   }


}
