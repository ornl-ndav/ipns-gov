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
 *  Revision 1.8  2003/08/26 03:40:24  millermi
 *  - Added functionality for double wedge, including panel button.
 *
 *  Revision 1.7  2003/08/21 18:18:59  millermi
 *  - Added ability to handle XOR_Cursor3pt class.
 *
 *  Revision 1.6  2003/08/18 20:53:19  millermi
 *  - Added JButton controls to simulate a keyboard event, making the
 *    selection process more user friendly.
 *  - Added getControls() so outside classes can access the new controls.
 *
 *  Revision 1.5  2003/08/07 17:55:56  millermi
 *  - Replaced elipse selection framework with line selection capabilities.
 *
 *  Revision 1.4  2003/06/06 14:37:38  dennis
 *  Temporarily commented out call to setFocusable() so that
 *  it will compile under jdk 1.3.1.  The call to setFocusable()
 *  should be re-enabled when we start using jdk 1.4.x.
 *
 *  Revision 1.3  2003/06/05 22:13:57  dennis
 *   - Fixed keyboard listener problems by adding setFocusable()
 *     in Constructor.  (Mike Miller)
 *
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DataSetTools.components.ui.ActiveJPanel;
import DataSetTools.components.image.XOR_Cursor;
import DataSetTools.components.image.BoxCursor;

/**
 * This class allows for the drawing of rubber band box/circle/point cursors.
 * It is used by the SelectionOverlay to select regions of the underlying 
 * JPanel.
 */
public class SelectionJPanel extends ActiveJPanel
{
   public static final String RESET_SELECTED = "RESET_SELECTED";
   public static final String RESET_LAST_SELECTED = "RESET_LAST_SELECTED";
   public static final String REGION_SELECTED = "REGION_SELECTED";

   public static final String BOX = "box";
   public static final String CIRCLE = "circle";
   public static final String LINE = "line";
   public static final String POINT = "point";
   public static final String WEDGE = "wedge";
   public static final String DOUBLE_WEDGE = "double_wedge";
   
   private BoxCursor box;
   private CircleCursor circle;
   private PointCursor point;
   private LineCursor line;
   private WedgeCursor wedge;
   private DoubleWedgeCursor dblwedge;
   private boolean isAdown;   // true if A is pressed (for RESET_SELECTED)
   private boolean isBdown;   // true if B is pressed (for box selection)
   private boolean isCdown;   // true if C is pressed (for circle selection)
   private boolean isDdown;   // true if D is pressed (for dbl wedge selection)
   private boolean isLdown;   // true if L is pressed (for line selection)
   private boolean isPdown;   // true if P is pressed (for point selection)
   private boolean isWdown;   // true if W is pressed (for wedge selection)
   private boolean isAnydown; // true if any of the keys above is down
   private boolean doing_box;      // true if box selection started
   private boolean doing_circle;   // true if circle selection started
   private boolean doing_dblwedge; // true if wedge selection started
   private boolean doing_line;     // true if line selection started
   private boolean doing_point;    // true if point selection started
   private boolean doing_wedge;    // true if wedge selection started
   private boolean firstRun;       // true if 3pt cursor hasn't drawn midpoint
   
  /**
   * Constructor adds listeners to this SelectionJPanel. All boolean values
   * are set to false. This JPanel is also contructed to receive keyboard
   * events.
   */ 
   public SelectionJPanel()
   { 
      isAdown = false;
      isBdown = false;
      isCdown = false;
      isDdown = false;
      isLdown = false;
      isPdown = false;
      isWdown = false;
      isAnydown = false;
      
      doing_box = false;
      doing_circle = false;
      doing_line = false;
      doing_point = false;
      doing_wedge = false;
      doing_dblwedge = false;
      
      firstRun = true;
   
      box = new BoxCursor(this);
      circle = new CircleCursor(this);
      line = new LineCursor(this);
      point = new PointCursor(this);
      wedge = new WedgeCursor(this);
      dblwedge = new DoubleWedgeCursor(this);
      
      requestFocus();
      
      addMouseListener( new SelectMouseAdapter() );
      addMouseMotionListener( new SelectMouseMotionAdapter() );
      addKeyListener( new SelectKeyAdapter() );
   }

  /* ------------------------ set_cursor ------------------------------ */
  /**
   *  Move the rubber band cursor to the specified pixel point. If the
   *  rubber band region was not previously started, this will specifiy the
   *  initial point for that region.  If the rubber band region was previously
   *  started, this will specify a new location for the ending point of the
   *  region.
   *
   *  @param cursor  The type of cursor that was selected. 
   *  @param pt  The point where the rubber band region should be drawn
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
      else if( cursor instanceof LineCursor )
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

  /* --------------- set_cursor for XOR_Cursor3pt --------------------- */
  /**
   *  Move the rubber band cursor to the specified pixel point. If the
   *  rubber band region was not previously started, this will specifiy the
   *  initial point for that region.  If the rubber band region was previously
   *  started, this will specify a new location for the ending point of the
   *  region.
   *
   *  @param  cursor  The type of 3 pt cursor that was selected. 
   *  @param  pt  The point where the rubber band region should be drawn
   */
   public void set_cursor( XOR_Cursor3pt cursor, Point current )
   {
     if( cursor instanceof WedgeCursor )
     {
       if ( doing_wedge )
         cursor.redraw( current );
       else
       {
         cursor.start( current );
         cursor.redraw( current );
         doing_wedge = true;
       }
     }
     else if( cursor instanceof DoubleWedgeCursor )
     {
       if ( doing_dblwedge )
         cursor.redraw( current );
       else
       {
         cursor.start( current );
         cursor.redraw( current );
         doing_dblwedge = true;
       }
     }
   }

  /* -------------------------- stop_cursor ---------------------------- */
  /**
   *  Stop the rubber band cursor and set the current position to the
   *  specifed pixel coordinate point. 
   *
   *  @param  cursor   the type of cursor being used.
   *  @param  current  the point to record as the current point,
   *                   in pixel coordinates
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
      else if( cursor instanceof LineCursor )
      {   
         if ( doing_line )
         {
            cursor.redraw( current );
            cursor.stop( current );
            doing_line = false;
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

  /* -------------------------- stop_cursor ---------------------------- */
  /**
   *  Stop the rubber band cursor and set the current position to the
   *  specifed pixel coordinate point. 
   *
   *  @param  cursor   the type of cursor being used.
   *  @param  current  the point to record as the current point,
   *                   in pixel coordinates
   */
   public void stop_cursor( XOR_Cursor3pt cursor, Point current )
   {
     if( cursor instanceof WedgeCursor )
     {   
       if ( doing_wedge )
       {
         if( firstRun )
	 {
	   cursor.redraw( current );
	   cursor.midpoint( current );
	 }
	 else
	 {
           cursor.redraw( current );
           cursor.stop( current );
           doing_wedge = false;
	 }
       }
     } 
     else if( cursor instanceof DoubleWedgeCursor )
     {   
       if ( doing_dblwedge )
       {
         if( firstRun )
	 {
	   cursor.redraw( current );
	   cursor.midpoint( current );
	 }
	 else
	 {
           cursor.redraw( current );
           cursor.stop( current );
           doing_dblwedge = false;
	 }
       }
     }   
   }
   
  /**
   * Since there are 4 types of xor cursors, this method gives us
   * which cursor was used.
   *
   *  @param  cursor - string label
   *  @return cursor - actual XOR_Cursor corresponding to the string label.
   */ 
   public XOR_Cursor getCursor( String cursor )
   {
     if( cursor.equals(BOX) )
       return box;
     else if( cursor.equals(CIRCLE) )
       return circle;
     else if( cursor.equals(LINE) )
       return line;	
     else //( cursor.equals(POINT) )
       return point;
   }
   
  /**
   * Since there are other types of cursors, this method gives us
   * the xor 3pt cursor that was used.
   *
   *  @param  cursor - string label
   *  @return cursor - actual XOR_Cursor corresponding to the string label.
   */ 
   public XOR_Cursor3pt get3ptCursor( String cursor )
   {
     if( cursor.equals(WEDGE) )
       return wedge;
     else //if( cursor.equals(DOUBLE_WEDGE) )
       return dblwedge;
   }
  
  /**
   * This method returns controls to be used as an alternative to key events
   * when making selections. Also included is a "Clear All" button.
   *
   *  @return array of JButtons, each button corresponding to a region.
   */ 
   public JButton[] getControls()
   {
     JButton[] controls = new JButton[7];
     controls[0] = new JButton("Box");
     controls[0].addActionListener( new ButtonListener() );
     controls[1] = new JButton("Circle");
     controls[1].addActionListener( new ButtonListener() );
     controls[2] = new JButton("Double Wedge");
     controls[2].addActionListener( new ButtonListener() );
     controls[3] = new JButton("Line");
     controls[3].addActionListener( new ButtonListener() );
     controls[4] = new JButton("Point");
     controls[4].addActionListener( new ButtonListener() );
     controls[5] = new JButton("Wedge");
     controls[5].addActionListener( new ButtonListener() );
     controls[6] = new JButton("Clear All");
     controls[6].addActionListener( new ButtonListener() );
     
     return controls;
   }

  /*
   * Tells the SelectMouseAdapter is any keys are pressed.
   */
   private class SelectKeyAdapter extends KeyAdapter
   {
     public void keyPressed( KeyEvent e )
     {
       //System.out.println("here in keypressed");
       int code = e.getKeyCode();
       // only make a selection if no other selections are in progress
       if( !isAnydown )
       {
         if( code == KeyEvent.VK_A )
         {
           isAdown = true; 
	   isAnydown = true;     
         }
         else if( code == KeyEvent.VK_B )
         {
           isBdown = true; 
	   isAnydown = true;    
         }
         else if( code == KeyEvent.VK_C )
         {
           isCdown = true; 
	   isAnydown = true;    
         }
         else if( code == KeyEvent.VK_D )
         {
           isDdown = true; 
	   isAnydown = true;    
         }
         else if( code == KeyEvent.VK_L )
         {
           isLdown = true;
	   isAnydown = true;     
         }
         else if( code == KeyEvent.VK_P )
         {
           isPdown = true; 
	   isAnydown = true;    
         }
         else if( code == KeyEvent.VK_W )
         {
           isWdown = true; 
	   isAnydown = true;    
         }
       }			       
     }
     
     public void keyReleased( KeyEvent ke )
     {
       //System.out.println("here in keyreleased");

       int code = ke.getKeyCode();

       if( code == KeyEvent.VK_A )
       {
         isAdown = false;      
       }    
       else if( code == KeyEvent.VK_B )
       {
         isBdown = false;    
       }
       else if( code == KeyEvent.VK_C )
       {
         isCdown = false;     
       }
       else if( code == KeyEvent.VK_L )
       {
         isLdown = false;     
       }
       else if( code == KeyEvent.VK_P )
       {
         isPdown = false;     
       } 
       //else if( code == KeyEvent.VK_W )
       //  isWdown = false;
       
       // if no one is down, then set isAnydown = false		 
       if( !isAdown && !isBdown && !isCdown && !isDdown &&
           !isLdown && ! isPdown && !isWdown )
	 isAnydown = false; 
     }
   }
   
  /*
   * This class receives button events and substitutes them for key events.
   */ 
   private class ButtonListener implements ActionListener
   {
     public void actionPerformed( ActionEvent ae )
     {
       String message = ae.getActionCommand();
       
       // only make a selection if no other selections are in progress
       if( !isAnydown )
       {
         if( message.equals("Box") )
         {
           isBdown = true;
	   isAnydown = true;
         }
         else if( message.equals("Circle") )
         {
           isCdown = true;
	   isAnydown = true;
         }
         else if( message.equals("Double Wedge") )
         {
           isDdown = true;
	   isAnydown = true;
         }
         else if( message.equals("Line") )
         {
           isLdown = true;
	   isAnydown = true;
         }
         else if( message.equals("Point") )
         {
           isPdown = true;
	   isAnydown = true;
         }
         else if( message.equals("Wedge") )
         {
           isWdown = true;
	   isAnydown = true;
         }
         else if( message.equals("Clear All") )
         {
           send_message(RESET_SELECTED);
         }
       } // end if !isAnydown
     }
   }

  /*
   * This class used flags set by the SelectKeyAdapter class to determine
   * an action.
   */
   private class SelectMouseAdapter extends MouseAdapter
   {
     public void mouseClicked (MouseEvent e)
     {
       if ( e.getClickCount() == 2 ) 
          send_message(RESET_LAST_SELECTED);
     }

     public void mousePressed (MouseEvent e)
     {
       //System.out.println("here in mousepressed");
       // if A is pressed, delete all selections.      
       if( isAdown )
         send_message(RESET_SELECTED);
       else if( isBdown )
         set_cursor( box, e.getPoint() );
       else if( isCdown )
         set_cursor( circle, e.getPoint() );
       else if( isDdown )
         set_cursor( dblwedge, e.getPoint() );
       else if( isLdown )
         set_cursor( line, e.getPoint() ); 
       else if( isPdown )
         set_cursor( point, e.getPoint() );
       else if( isWdown )
         set_cursor( wedge, e.getPoint() );
     }

     public void mouseReleased(MouseEvent e)
     {
       //System.out.println("here in mousereleased");

       if( doing_box )
       {
         stop_cursor( box, e.getPoint() );
         isBdown = false;
	 isAnydown = false;
         send_message(REGION_SELECTED + ">" + BOX);
       }
       else if( doing_circle )
       {
         stop_cursor( circle, e.getPoint() );
         isCdown = false;
	 isAnydown = false;
         send_message(REGION_SELECTED + ">" + CIRCLE);
       }
       else if( doing_line )
       {
         stop_cursor( line, e.getPoint() );
         isLdown = false;
	 isAnydown = false;
         send_message(REGION_SELECTED + ">" + LINE);
       }
       else if( doing_point )
       {
         stop_cursor( point, e.getPoint() ); 
         isPdown = false;
	 isAnydown = false;
         send_message(REGION_SELECTED + ">" + POINT); 
       }  
       else if( doing_wedge )
       {
         stop_cursor( wedge, e.getPoint() ); 
	 if( firstRun )
	 {
           isWdown = true;
	   isAnydown = true;
	 }
	 else
	 {
	   isWdown = false;
	   isAnydown = false;
           send_message(REGION_SELECTED + ">" + WEDGE);
	 }
	 firstRun = !firstRun; 
       }   
       else if( doing_dblwedge )
       {
         stop_cursor( dblwedge, e.getPoint() ); 
	 if( firstRun )
	 {
           isDdown = true;
	   isAnydown = true;
	 }
	 else
	 {
	   isDdown = false;
	   isAnydown = false;
           send_message(REGION_SELECTED + ">" + DOUBLE_WEDGE);
	 }
	 firstRun = !firstRun; 
       }          
     }
   } 
   
  /*
   * Redraw the specified cursor, giving the rubber band stretch effect.
   */ 
   class SelectMouseMotionAdapter extends MouseMotionAdapter
   {
     public void mouseDragged(MouseEvent e)
     {
       //System.out.println("here in mousedragged");

       //System.out.println("Point: " + e.getPoint() );
       if( doing_box )
         set_cursor( box, e.getPoint() );
       else if( doing_circle )
         set_cursor( circle, e.getPoint() );
       else if( doing_dblwedge )
         set_cursor( dblwedge, e.getPoint() );
       else if( doing_line )
         set_cursor( line, e.getPoint() );
       else if( doing_point )
         set_cursor( point, e.getPoint() );
       else if( doing_wedge )
         set_cursor( wedge, e.getPoint() );
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
