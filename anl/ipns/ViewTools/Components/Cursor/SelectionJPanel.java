/*
 * File:  SelectionJPanel.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2003/05/29 14:13:22  dennis
 *  Made three changes (Mike Miller)
 *    -extends ActiveJPanel not CoordJPanel
 *    -added messages for actionListeners
 *    -added double click feature to remove last/all selected
 *
 *  Revision 1.1  2003/05/24 17:32:20  dennis
 *  Initial version of cursor selection. (Mike Miller)
 *
 */

package DataSetTools.components.View.Cursor;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

import DataSetTools.util.*;
import DataSetTools.components.ui.*;
import DataSetTools.components.image.*;

/**
 *
 */
public class SelectionJPanel extends ActiveJPanel
{
   public static final String RESET_SELECTED = "RESET_SELECTED";
   public static final String RESET_LAST_SELECTED = "RESET_LAST_SELECTED";
   public static final String REGION_SELECTED = "REGION_SELECTED";

   public static final String BOX = "box";
   public static final String CIRCLE = "circle";
   public static final String ELIPSE = "elipse";
   public static final String POINT = "point";
   private BoxCursor box;
   private CircleCursor circle;
   private PointCursor point;
   private boolean isAdown;   // true if A is pressed (for RESET_SELECTED)
   private boolean isBdown;   // true if B is pressed (for box selection)
   private boolean isCdown;   // true if C is pressed (for circle selection)
   private boolean isEdown;   // true if E is pressed (for eliptical selection)
   private boolean isPdown;   // true if P is pressed (for point selection)
   private boolean doing_box;     // true if box selection started
   private boolean doing_circle;  // true if circle selection started
   private boolean doing_point;   // true if point selection started
   private boolean doing_elipse;  // true if elipse selection started
   
   public SelectionJPanel()
   { 
      isAdown = false;
      isBdown = false;
      isCdown = false;
      isEdown = false;
      isPdown = false;
      
      doing_box = false;
      doing_circle = false;
      doing_point = false;
      doing_elipse = false;
      
  //*********Testing - Remove after KeyListeners are fixed*******************//
      doing_circle = true;
  //*************************************************************************//
      
      box = new BoxCursor(this);
      circle = new CircleCursor(this);
      point = new PointCursor(this);
      
      addMouseListener( new SelectMouseAdapter() );
      addMouseMotionListener( new SelectMouseMotionAdapter() );
      addKeyListener( new SelectKeyAdapter() );
   }

  /* ------------------------ set_box ------------------------------ */
  /**
   *  Move the rubber band box cursor to the specified pixel 
   *  point and notify any action listeners that the CURSOR_MOVED.  If the
   *  rubber band box was not previously started, this will specifiy the
   *  initial point for the box.  If the rebber band box was previously
   *  started, this will specify a new location for the other corner of
   *  the box.
   *
   *  @param pt  The point where the rubber band box should be drawn
   */
   public void set_cursor( XOR_Cursor cursor, Point current )
   {
      if( cursor instanceof BoxCursor )
      {
         if ( doing_box )
            cursor.redraw( current );
         else
         {
            cursor.start( current );
            cursor.redraw( current );
            doing_box = true;
         }
      }
      else if( cursor instanceof CircleCursor )
      {
         if ( doing_circle )
            cursor.redraw( current );
         else
         {
            cursor.start( current );
            cursor.redraw( current );
            doing_circle = true;
         }
      }      
      else if( cursor instanceof PointCursor )
      {
         if ( doing_point )
            cursor.redraw( current );
         else
         {
            cursor.start( current );
            cursor.redraw( current );
            doing_point = true;
         }
      }  
  }

  /* -------------------------- stop_cursor ---------------------------- */
  /**
   *  Stop the ruber band box cursor and set the current position to the
   *  specifed pixel coordinate point.  If do_zoom is true, change the
   *  transform to map the selected region to the full panel and
   *  notify listeners of the zoom-in request.
   *
   *  @param  current  the point to record as the current point,
   *                   in pixel coordinates
   *  @param  do_zoom  flag specifying whether or not to zoom in 
   *                   on the selected region
   */
   public void stop_cursor( XOR_Cursor cursor, Point current )
   {
      if( cursor instanceof BoxCursor )
      {   
         if ( doing_box )
         {
            cursor.redraw( current );
            cursor.stop( current );
            doing_box = false;
         }
      }
      else if( cursor instanceof CircleCursor )
      {   
         if ( doing_circle )
         {
            cursor.redraw( current );
            cursor.stop( current );
            doing_circle = false;
         }
      }      
      else if( cursor instanceof PointCursor )
      {   
         if ( doing_point )
         {
            cursor.redraw( current );
            cursor.stop( current );
            doing_point = false;
         }
      }      
   }
   
   public XOR_Cursor getCursor( String cursor )
   {
   
      if( cursor.equals(BOX) )
         return box;
      else if( cursor.equals(CIRCLE) )
         return circle;
    //else if( cursor.equals(ELIPSE) )          // elipse not implemented
    //   return elipse; 	 
      else //( cursor.equals(POINT) )
         return point;
   }

   private class SelectKeyAdapter extends KeyAdapter
   {
      public void keyPressed( KeyEvent e )
      {
         System.out.println("here in keypressed");
         int code = e.getKeyCode();

	 if( code == KeyEvent.VK_A )
	    isAdown = true;	 
	 if( code == KeyEvent.VK_B )
	    isBdown = true;
	 if( code == KeyEvent.VK_C )
	    isCdown = true;
	 if( code == KeyEvent.VK_E )
	    isEdown = true;
	 if( code == KeyEvent.VK_P )
	    isPdown = true;		 	 
      }
      
      public void keyReleased( KeyEvent ke )
      {
         //System.out.println("here in keyreleased");
	 
         int code = ke.getKeyCode();

	 if( code == KeyEvent.VK_A )
	    isBdown = false;	 
	 if( code == KeyEvent.VK_B )
	    isBdown = false;
	 if( code == KeyEvent.VK_C )
	    isCdown = false;
	 if( code == KeyEvent.VK_E )
	    isEdown = false;
	 if( code == KeyEvent.VK_P )
	    isPdown = false;              
      }
   }

   private class SelectMouseAdapter extends MouseAdapter
   {
      public void mouseClicked (MouseEvent e)
      {
         if ( e.getClickCount() == 2 ) 
	 {
	    if( isAdown )
	       send_message(RESET_SELECTED);	
	    else
	       send_message(RESET_LAST_SELECTED);
	 }	        	    
      }

      public void mousePressed (MouseEvent e)
      {
         System.out.println("here in mousepressed");

         if( isBdown || isCdown || isEdown )
	 {
	 // turn off crosshairs in CoordJPanel, 
	 // then do one of the following	    
            if( isBdown )
	       set_cursor( box, e.getPoint() );
	    if( isCdown )
	       set_cursor( circle, e.getPoint() );
            if( isEdown )
	       System.out.println("Eliptical Selection");	    	    	    
         }
	 if( isPdown )
	    set_cursor( point, e.getPoint() );	       
      }

      public void mouseReleased(MouseEvent e)
      {
         System.out.println("here in mousereleased");

         if( doing_box )
	 {
	    stop_cursor( box, e.getPoint() );
	    send_message(REGION_SELECTED + ">" + BOX);
         }
	 if( doing_circle )
	 {
	    stop_cursor( circle, e.getPoint() );
	    send_message(REGION_SELECTED + ">" + CIRCLE);
         }
	 if( doing_elipse )
	 {
	    System.out.println("Eliptical Selection Complete");
	    send_message(REGION_SELECTED + ">" + ELIPSE);
         }
	 if( doing_point )
	 {
	    stop_cursor( point, e.getPoint() ); 
	    send_message(REGION_SELECTED + ">" + POINT); 
	 }    
      }
   } 
   
   class SelectMouseMotionAdapter extends MouseMotionAdapter
   {
      public void mouseDragged(MouseEvent e)
      {
         //System.out.println("here in mousedragged");

	 //System.out.println("Point: " + e.getPoint() );
         if( doing_box )
            set_cursor( box, e.getPoint() );
         if( doing_circle )
	    set_cursor( circle, e.getPoint() );
         if( doing_elipse )
	    System.out.println("Eliptical Selection");
         if( doing_point )
	    set_cursor( point, e.getPoint() );
      }
   }    
   
/* -----------------------------------------------------------------------
 *
 * MAIN PROGRAM FOR TEST PURPOSES
 *
 */

  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for SelectionJPanel");
    f.setBounds(0,0,500,500);
    SelectionJPanel panel = new SelectionJPanel();
    f.getContentPane().add(panel);
    f.setVisible(true);
  }  
}
