/* 
 * file: AnnularCursor.java
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * (This follows the format of BoxCursor.java created by Dennis Mikkelson,
 * only this class uses three points.)
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2004/03/12 01:33:22  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.2  2004/01/08 21:55:38  millermi
 *  - Added crosshairs to center of ring.
 *
 *  Revision 1.1  2003/12/30 00:00:12  millermi
 *  - Initial Version - This cursor allows users to select rings.
 *
 */

 package gov.anl.ipns.ViewTools.Components.Cursor;

 import javax.swing.JPanel;
 import java.io.Serializable;
 import java.awt.Point;
 import java.awt.Graphics;

/** 
 *  This class implements two Rubberband circle cursors for selecting regions.
 *  The annular selection includes the points between the two ellipses drawn.
 *
 *  @see  XOR_Cursor3pt
 */

public class AnnularCursor extends  XOR_Cursor3pt 
                           implements Serializable
{
 /**
  *  Construct a new AnnularCursor to be used on a JPanel.
  *
  *  @param panel The JPanel for this cursor.
  *
  */
  public AnnularCursor ( JPanel panel ) 
  {
    super( panel );
  }

 /**
  *  This method draws a ring consisting of two circles centered at the same
  *  point.
  *
  *  @param  graphics	The graphics context that the circle will be drawn in.
  *  @param  p1 	Center of ring.
  *  @param  p2 	Radius point of inner circle.
  *  @param  p3 	Radius point of outer circle.
  */
  public void draw( Graphics graphics, Point p1, Point p2, Point p3 )
  {
    // if second and third points are different, draw outer circle
    if( !( p2.x == p3.x && p2.y == p3.y ) )
    { 
      int x = Math.abs( p1.x - p3.x );
      int y = Math.abs( p1.y - p3.y );
      int r = (int)Math.sqrt( Math.pow(x,2) + Math.pow(y,2) ); 
      
      // draw inner circle of ring  	 
      graphics.drawOval( (p1.x - r), (p1.y - r), 2*r, 2*r );
    }
    // if second and third points are the same, draw only inner circle
    else
    {
      int x = Math.abs( p1.x - p2.x );
      int y = Math.abs( p1.y - p2.y );
      int r = (int)Math.sqrt( Math.pow(x,2) + Math.pow(y,2) ); 
      
      // draw inner circle of ring  	 
      graphics.drawOval( (p1.x - r), (p1.y - r), 2*r, 2*r );
      // draw crosshair at center of ring
      graphics.drawLine( p1.x - 2, p1.y, p1.x + 2, p1.y );
      graphics.drawLine( p1.x, p1.y - 2, p1.x, p1.y + 2 );
    }
  }

 /**
  *  This method returns an array of Points used to define a ring.
  *
  *  p[0]   = center pt of the ring
  *  p[1].x = inner radius
  *  p[1].y = outer radius
  *
  *  @return  array of Points used to define a ring
  */
  public Point[] region() 
  {
    Point[] array = new Point[2];
    array[0] = new Point( first_pt.x, first_pt.y );
    array[1] = new Point(0,0);
    
    int x1 = Math.abs( first_pt.x - mid_pt.x );
    int y1 = Math.abs( first_pt.y - mid_pt.y );
    double temp = Math.sqrt( Math.pow(x1,2) + Math.pow(y1,2) );
    array[1].x = (int)Math.round(temp);

    int x2 = Math.abs( first_pt.x - last_pt.x );
    int y2 = Math.abs( first_pt.y - last_pt.y );
    temp = Math.sqrt( Math.pow(x2,2) + Math.pow(y2,2) ); 
    
    // make sure array[1].x < array[1].y
    if( temp < array[1].x )
    {
      array[1].y = array[1].x;
      array[1].x = (int)Math.round(temp);
    }
    else
      array[1].y = (int)Math.round(temp);
    
    return array;
  }
}
