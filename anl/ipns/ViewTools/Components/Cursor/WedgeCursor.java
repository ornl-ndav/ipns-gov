/* 
 * file: WedgeCursor.java
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
 *  Revision 1.5  2003/12/12 20:04:40  millermi
 *  - Fixed bug that caused entire circle to be drawn.
 *
 *  Revision 1.4  2003/12/12 06:08:28  millermi
 *  - Reworked how arc was calculated.
 *  - Now progressing from start to stop of angle is always
 *    counterclockwise. This simplifies the work necessary
 *    for calculated the points selected by the region.
 *
 *  Revision 1.3  2003/12/11 17:11:23  millermi
 *  - Minor change so line is not redrawn while cursor stretches.
 *
 *  Revision 1.2  2003/09/02 21:08:10  millermi
 *  - Removed bounding rectangle from cursor.
 *  - Directional vector new is redrawn when the wedge is being
 *    drawn, thus causing the vector to disappear.
 *
 *  Revision 1.1  2003/08/21 18:19:46  millermi
 *  - Initial Version, allows for "pie slice" selection.
 *
 */

 package DataSetTools.components.View.Cursor;

 import javax.swing.JPanel;
 import java.io.Serializable;
 import java.awt.Point;
 import java.awt.Graphics;

/** 
 *  This class implements a Rubberband Wedge cursor for selecting regions.
 *  The wedge can range from a pie slice to a pie without a slice.
 *
 *  @see  XOR_Cursor3pt
 */

public class WedgeCursor extends  XOR_Cursor3pt 
                                implements Serializable
{
  private Point topleft = new Point(0,0);     // top left corner of box bounding
                                              // the circle that the arc comes
					      // from
  private Point bottomright = new Point(0,0); // bottom right corner of same box
  private Point p4 = new Point(0,0);          // reflection of p3
  private Point angles = new Point(0,0);      // start angle and arcangle
  private boolean swap = false;               // were p3 and p4 swapped?
 /**
  *  Construct a new WedgeCursor to be used on a JPanel.
  *
  *  @param panel The JPanel for this cursor.
  *
  */
  public WedgeCursor ( JPanel panel ) 
  {
    super( panel );
  }

 /**
  *  This method draws a wedge or pie slice. First a line is drawn, then the
  *  wedge is formed.
  *
  *  @param  graphics	The graphics context that the circle will be drawn in.
  *  @param  p1 	Vertex of wedge.
  *  @param  p2 	Radius point of the wedge.
  *  @param  p3 	Size of wedge arc.
  */
  public void draw( Graphics graphics, Point p1, Point p2, Point p3 )
  {
    // if second and third points are the same, draw only the line
    if( !( p2.x == p3.x && p2.y == p3.y ) )
    { 
      // draw line from "if" again to make it disappear using XOR
      //graphics.drawLine( p1.x, p1.y, p2.x, p2.y );
      // draw line passing through p1 and p3
      graphics.drawLine( p1.x, p1.y, p3.x, p3.y );
      
      // draw reflection of line passing through p1 and p3 about the line 
      // passing through p1 and p2.
      
      // this will prevent slope of zero or +/- infinity
      if( p1.x == p2.x )
        p2.x += 1;
      if( p1.y == p2.y )
        p2.y += 1;
      float slope = (float)(p2.y - p1.y)/(float)(p2.x - p1.x);
      float perpendicular_slope = -1/slope;
      float perp_y_intercept = p3.y - (perpendicular_slope * p3.x);
      // int is the intersection point of the two lines.
      float intx = ( p3.y - p1.y - perpendicular_slope * p3.x + slope * p1.x )/
                  ( slope - perpendicular_slope );
      float inty = slope * (intx - p1.x) + p1.y;
      // p4 is the reflection of p3 across the line passing through p1 and p2.
      p4.x = (int)(2*intx - p3.x);
      p4.y = (int)(2*inty - p3.y);
      // reflection of line passing through p1 and p3.
      graphics.drawLine( p1.x, p1.y, p4.x, p4.y );
      		
      int startangle = -Math.round( (float)Math.toDegrees(Math.atan2(
                                                       (double)(p3.y - p1.y),
                                                       (double)(p3.x - p1.x))));
      int stopangle  = -Math.round( (float)Math.toDegrees(Math.atan2( 
                                                       (double)(p4.y - p1.y),
                                                       (double)(p4.x - p1.x))));
      int initangle  = -Math.round( (float)Math.toDegrees(Math.atan2( 
                                                       (double)(p2.y - p1.y),
                                                       (double)(p2.x - p1.x))));
      // put everything from 0-360
      if( startangle < 0 )
        startangle = 360 + startangle;
      if( stopangle < 0 )
        stopangle = 360 + stopangle;
      if( initangle < 0 )
        initangle = 360 + initangle;      
      
      // make sure startangle is always less than stop angle
      if( startangle > stopangle )
      { // swap them and their corresponding points
        int temp = startangle;
	startangle = stopangle;
	stopangle = temp;
	swap = true;
      }
      else
        swap = false;
      
      int arcangle = stopangle - startangle;
      // if initangle isn't between start and stop, then the angle includes
      // the point where the unit circle goes from 359 to 0. Swap the start
      // and stop angle so that the arcangle is always positive and the
      // drawing always occurs in a counterclockwise direction.
      // The initangle+1 and initangle-1 are adjustments to compensate for
      // rounding.  
      if( !( initangle+1 >= startangle && initangle-1 <= stopangle) )
      {
        int invertStop = 360 - stopangle;
	arcangle = startangle + invertStop;
	
        int temp = startangle;
	startangle = stopangle;
	stopangle = temp;
	swap = !swap;
      }
      // create rectangle with p1 at its center that bounds the arc's circle
      // find radius of arc
      double xsquared = Math.pow( (double)(p1.x - p3.x), 2 );
      double ysquared = Math.pow( (double)(p1.y - p3.y), 2 );
      double radius = Math.sqrt( xsquared + ysquared );
      topleft = new Point( p1.x - (int)radius, p1.y - (int)radius );
      bottomright = new Point( topleft.x + (int)(2*radius),
                               topleft.y + (int)(2*radius) );
     //System.out.println("Start/Stop angle: " + startangle + "/" + stopangle );
      //System.out.println("StartPt: (" + topleft.x + "," + topleft.y +")" );
      
      graphics.drawArc(topleft.x,topleft.y,(int)(2*radius),(int)(2*radius),
                       startangle,arcangle);
      // put angles in point for passing to overlay.
      angles.x = startangle;
      angles.y = arcangle;
    }
    // this line is the directional vector
    graphics.drawLine( p1.x, p1.y, p2.x, p2.y );
  }

 /**
  *  This method returns an array of points used to define a wedge.
  *
  *  p[0]   = center pt of circle that arc is taken from
  *  p[1]   = last mouse point/point at intersection of line and arc
  *  p[2]   = reflection of p[1]
  *  p[3]   = top left corner of bounding box around arc's total circle
  *  p[4]   = bottom right corner of bounding box around arc's circle
  *  p[5].x = startangle, the directional vector in degrees
  *  p[5].y = degrees covered by arc.
  *
  *  p[0-2] are used for drawing lines
  *  p[3-4] are needed in case wedge is from an ellipse
  *  p[5] is needed to determine which area was selected, wedge or all but wedge
  *
  *  @return  array of Points used to define a wedge
  */

  public Point[] region() 
  {
    Point[] array = new Point[6];
    array[0] = first_pt;
    // swap points here instead of in draw because otherwise swapping affects
    // the XOR cursor redraw, which erases the last drawn line.
    if( !swap )
    {
      array[1] = last_pt;
      array[2] = p4;
    }
    else
    {
      array[1] = p4;
      array[2] = last_pt;
    }
    array[3] = topleft;
    array[4] = bottomright;
    array[5] = angles;

    return array;
  }
}
