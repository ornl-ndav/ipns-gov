/**
 * Flie   SplitPaneWithState.java 
 *
 * Copyright (C) 1999-2001, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *  
 * $Log$
 * Revision 1.5  2002/07/15 17:00:11  dennis
 * Constructor now sets OneTouchExpandable to true.
 *
 * Revision 1.4  2001/05/29 22:32:57  dennis
 * Now basically working.  Calls validate() after setting divider
 * location to trigger the layout of the child components.  Also
 * now saves the previous width and height of the split pane so
 * that the divider location can be calculated when the split pane
 * is resized.
 *
 * Revision 1.3  2001/01/29 21:33:36  dennis
 * Now uses CVS version numbers.
 *
 * Revision 1.2  2000/07/10 22:04:44  dennis
 * Not done...partly modified for JDK 1.2.2
 *
 * Revision 1.3  2000/06/12 20:12:10  dennis
 * internal class now implements Serializable
 *
 */
package DataSetTools.components.containers;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/**
 *  This class extends the JSplitPane class with "state" information, so that
 *  if the user set a divider position that is some fraction of the way 
 *  across the split pane, that fractional position will be preserved when
 *  the entire split pane is resized.
 */
public class SplitPaneWithState extends    JSplitPane
                                implements Serializable
 {
    private   float     divider_location = 0.5f;
    private   boolean   first_time = true;
    private   float     last_width,
                        last_height;


/* ----------------------------- Constructor ----------------------------- */
/** 
 *  Construct a split pane with the specified orientation, components and
 *  fractional divider location.
 *  
 *  @param orientation       Flag indicating whether the pane is to be
 *                           split horizontally or vertically.  This should
 *                           be one of  JSplitPane.HORIZONTAL_SPLIT or
 *                           JSplitPane.VERTICAL_SPLIT 
 *  @param first_component   The component to be in the left ( or top ) part
 *                           of the split pane.
 *  @param second_component  The component to be in the right ( or botton ) part
 *                           of the split pane.
 *  @param divider location  A value between 0.0f and 1.0f giving the fraction
 *                           of the way across the split pane where the 
 *                           divider should be placed.
 */
 
    public SplitPaneWithState( int        orientation,
                               Component  first_component,
                               Component  second_component,
                               float      divider_location )
    {
      super( orientation, first_component, second_component );
      my_setDividerLocation( divider_location );
      addComponentListener( new ResizeHandler() );
      setOneTouchExpandable(true);
    }

/* -------------------------- my_setDividerLocation ----------------------- */
/**
 *  Position the divider a specified fraction of the way across the split pane.
 *
 *  @param divider location  A value between 0.0f and 1.0f giving the fraction
 *                           of the way across the split pane where the 
 *                           divider should be placed.
 */
    public void my_setDividerLocation( float divider_location )
    {
      if ( divider_location >= 0.0 && divider_location <= 1.0 )
      {
        this.divider_location = divider_location;
        PositionDivider();
      }
      else
      {
        this.divider_location = 0.5f;
        System.out.println("ERROR: invalid location in " + 
                           "SplitPaneWithState.my_setDividerLocation" );
        System.out.println("Using default of 0.5");
      }
    }


/* --------------------------------------------------------------------------
 *
 *  PRIVATE METHODS
 * 
 */

/* ---------------------------- PositionDivider --------------------------- */

    private void PositionDivider()
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

       validate();
    }

/* -------------------------------------------------------------------------
 *
 *  INTERNAL CLASSES
 * 
 */

   private class ResizeHandler extends    ComponentAdapter
                               implements Serializable
   {
      public void componentResized( ComponentEvent c )
      {
        Dimension size    = getSize();
                                                   // Don't do anything if it
        if ( !isVisible() )                        // is not visible yet.
          return;
        if ( size.width <= 0 || size.height <=0 )
          return;

        if ( first_time )                          // the first time, use the
        {                                          // preset divider_location
          first_time  = false;
          last_width  = size.width;
          last_height = size.height;
        }
        else                                       // other times, put the
        {                                          // divider the same fraction
                                                   // across the pane as before
          int orientation = getOrientation();
          int location    = getDividerLocation();

          if ( orientation == JSplitPane.HORIZONTAL_SPLIT )
            divider_location = location/last_width;
          else
            divider_location = location/last_height;

          last_width  = size.width;
          last_height = size.height;
        }          

        PositionDivider();
      }
   }

}
