/*
 * File:  AnnotationJPanel.java
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
 *  Revision 1.7  2004/03/15 23:53:50  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.6  2004/03/12 01:33:21  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.5  2003/10/16 05:00:04  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.4  2003/08/14 17:08:32  millermi
 *  - Use "Shift" instead of "N" to draw lines. This prevents problems that
 *    happen if the user releases the mouse button before the key.
 *
 *  Revision 1.3  2003/08/11 23:48:07  millermi
 *  - Changed remove all from double click with A down, to single click
 *    with A down
 *
 *  Revision 1.2  2003/06/06 14:37:38  dennis
 *  Temporarily commented out call to setFocusable() so that
 *  it will compile under jdk 1.3.1.  The call to setFocusable()
 *  should be re-enabled when we start using jdk 1.4.x.
 *
 *  Revision 1.1  2003/06/05 22:12:50  dennis
 *   - Initial Version, used by AnnotationOverlay to create arrow
 *     to the note. (Mike Miller)
 *
 *
 */

package gov.anl.ipns.ViewTools.Components.Cursor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gov.anl.ipns.ViewTools.UI.ActiveJPanel;

/**
 * This class is used by the AnnotationOverlay to create an arrow from the 
 * region of interest to the annotation. 
 */
public class AnnotationJPanel extends ActiveJPanel
{
   public static final String RESET_NOTE = "RESET_NOTE";
   public static final String RESET_LAST_NOTE = "RESET_LAST_NOTE";
   public static final String NOTE_REQUESTED = "NOTE_REQUESTED";

   private LineCursor cursor;
   private boolean isAdown;        // true if A is pressed
   private boolean isShiftdown;        // true if N is pressed
   private boolean doing_line;     // true if line started
   
  /**
   * Constructor adds listeners to this AnnotationJPanel and allows for keyboard
   * focus. All boolean values are set to false.
   */ 
   public AnnotationJPanel()
   { 
      isAdown = false;
      isShiftdown = false;
      
      doing_line = false;
      
      cursor = new LineCursor(this);
      
      addMouseListener( new SelectMouseAdapter() );
      addMouseMotionListener( new SelectMouseMotionAdapter() );
      addKeyListener( new SelectKeyAdapter() );
      
      //setFocusable(true);
   }

  /* ------------------------ set_cursor ------------------------------ */
  /**
   *  Move the rubber band line cursor to the specified pixel 
   *  point.  If the rubber band line was not previously started, 
   *  this will specifiy the initial point for the line.  If the rubber 
   *  band line was previously started, this will specify a new location 
   *  for the ending point of the line.
   *
   *  @param  current  The point where the rubber band line should be drawn
   */
   public void set_cursor( Point current )
   {
      if ( doing_line )
         cursor.redraw( current );
      else
      {
         cursor.start( current );
         cursor.redraw( current );
         doing_line = true;
      }
  }

  /* -------------------------- stop_cursor ---------------------------- */
  /**
   *  Stop the rubber band line cursor and set the current position to the
   *  specifed pixel coordinate point. 
   *
   *  @param  current  the point to record as the current point,
   *                   in pixel coordinates
   */
   public void stop_cursor( Point current )
   {  
      if ( doing_line )
      {
         cursor.redraw( current );
         cursor.stop( current );
         doing_line = false;
      }
   }
   
  /**
   * Method used by annotation overlay to get the region selected by the 
   * line cursor.
   *
   *  @return cursor
   */ 
   public LineCursor getLineCursor()
   {
      return cursor;
   }

  /*
   * This class is used the tell the SelectMouseAdapter 
   * if a key is currently down.
   */
   private class SelectKeyAdapter extends KeyAdapter
   {
      public void keyPressed( KeyEvent e )
      {
         //System.out.println("here in keypressed");
         int code = e.getKeyCode();

	 if( code == KeyEvent.VK_A )
	    isAdown = true; 
	 if( e.isShiftDown() )
	    isShiftdown = true;	 
      }
      
      public void keyReleased( KeyEvent ke )
      {
         //System.out.println("here in keyreleased");
	 
         int code = ke.getKeyCode();

	 if( code == KeyEvent.VK_A )
	    isAdown = false; 
	 //if( ke.isShiftDown() )
	    isShiftdown = false;    
      }
   }
  
  /*
   * If N is pressed, a line cursor will be drawn when a mouse is pressed.
   * On double click, RESET_LAST_NOTE message is sent. If A is pressed during
   * a double click, RESET_NOTE message will be sent.
   */
   private class SelectMouseAdapter extends MouseAdapter
   {
      public void mouseClicked (MouseEvent e)
      {
         if ( e.getClickCount() == 2 ) 
	 {
	    send_message(RESET_LAST_NOTE);
	 }	        	    
      }

      public void mousePressed (MouseEvent e)
      {
         //System.out.println("here in mousepressed");

	 if( isAdown )
	    send_message(RESET_NOTE);
         if( isShiftdown )
	    set_cursor( e.getPoint() );	       
      }

      public void mouseReleased(MouseEvent e)
      {
         //System.out.println("here in mousereleased");

         if( doing_line )
	 {
	    stop_cursor( e.getPoint() );
	    send_message(NOTE_REQUESTED);
         }
      }
   } 
   
  /*
   * This class allows the rubber band line cursor to be redrawn as it is
   * stretched.
   */ 
   class SelectMouseMotionAdapter extends MouseMotionAdapter
   {
      public void mouseDragged(MouseEvent e)
      {
         //System.out.println("here in mousedragged");

	 //System.out.println("Point: " + e.getPoint() );
         if( doing_line )
            set_cursor( e.getPoint() );
      }
   }    
   
/* -----------------------------------------------------------------------
 *
 * MAIN PROGRAM FOR TEST PURPOSES
 *
 */

  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for AnnotationJPanel");
    f.setBounds(0,0,500,500);
    AnnotationJPanel panel = new AnnotationJPanel();
    f.getContentPane().add(panel);
    f.setVisible(true);
  }  
}
