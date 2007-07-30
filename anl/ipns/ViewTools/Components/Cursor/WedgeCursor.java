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
 *  $Log$
 *  Revision 1.10  2007/07/30 14:31:41  dennis
 *  Minor javadoc fix.
 *
 *  Revision 1.9  2007/04/07 21:27:44  dennis
 *  Clean up of logic for selecting points that define the region.
 *
 *  Revision 1.8  2007/03/23 20:24:54  dennis
 *  Now calculates symmetrically placed points across axis of wedge
 *  using vectors, instead of slopes of lines that could become
 *  undefined.
 *
 *  Revision 1.7  2004/05/11 00:59:27  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.6  2004/03/12 01:33:23  millermi
 *  - Changed package and imports.
 *
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

 package gov.anl.ipns.ViewTools.Components.Cursor;

 import javax.swing.JPanel;
 import java.io.Serializable;
 import java.awt.Point;
 import java.awt.Graphics;
 import gov.anl.ipns.Util.Numeric.floatPoint2D;

/** 
 *  This class implements a Rubberband Wedge cursor for selecting regions.
 *  The wedge can range from a pie slice to a pie without a slice.
 *
 *  @see  XOR_Cursor3pt
 */

public class WedgeCursor extends  XOR_Cursor3pt 
                                  implements Serializable
{
  private Point topleft = new Point(0,0);     // top left corner of box
                                              // bounding the circle that the
                                              // arc comes from
  private Point bottomright = new Point(0,0); // bottom right corner of 
                                              // same box
  private Point last_p3= new Point(0,0);      // last value of p3
  private Point p4     = new Point(0,0);      // reflection of p3
  private Point angles = new Point(0,0);      // start angle and arcangle

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
  *  @param  graphics   The graphics context that the circle will be drawn in.
  *  @param  p1         Vertex of wedge, the first point the user selects.
  *  @param  p2         Point on the axis of symmetry for the wedge 
  *  @param  p3         Point on the outer edge of the wedge.  This point is
  *                     reflected across the axis of symmetry to form the
  *                     full wedge. 
  */
  public void draw( Graphics graphics, Point p1, Point p2, Point p3 )
  {
    // if second and third points are the same, or first and second points are 
    // the same, then draw only the line (or point) between p1 and p2.

    if( !( p2.x == p3.x && p2.y == p3.y ) && !( p2.x == p1.x && p2.y == p1.y ))
    { 
                                          // first draw line from p1 to p3
                                          // then find and draw the reflection
                                          // of this line across the axis of
                                          // the wedge.
      graphics.drawLine( p1.x, p1.y, p3.x, p3.y );
      
      floatPoint2D diff_vec = new floatPoint2D( p2.x - p1.x, p2.y - p1.y );
      floatPoint2D u_vec = new floatPoint2D( diff_vec );
      u_vec.normalize();
      floatPoint2D v_vec = new floatPoint2D( -u_vec.y, u_vec.x ); 

      floatPoint2D p3_float = new floatPoint2D( p3.x, p3.y );
      diff_vec = new floatPoint2D( p3.x - p1.x, p3.y - p1.y );
      float perp_comp = diff_vec.dot( v_vec );
      v_vec.scale( -2 * perp_comp );
      floatPoint2D p4_float = new floatPoint2D( p3_float );
      p4_float.add( v_vec ); 

      p4.x = Math.round( p4_float.x );
      p4.y = Math.round( p4_float.y );
                                            // draw reflection of line passing 
                                            // through p1 and p3.
      graphics.drawLine( p1.x, p1.y, p4.x, p4.y );
      		
      double angle_3   = -Math.atan2(p3.y - p1.y, p3.x - p1.x);
      double angle_4   = -Math.atan2(p4.y - p1.y, p4.x - p1.x);
      double axisangle = -Math.atan2(p2.y - p1.y, p2.x - p1.x);

      double half_angle   = 0;             // this is half the positive angle
                                           // at the vertex of the wedge

                                           // we may need to swap points p3&p4
                                           // if p3 is not located clockwise
                                           // along the edge of the wedge, from
                                           // the wedge axis.
      boolean swap_points = false;
      if ( Math.abs( angle_3 - axisangle ) < Math.abs( angle_4 - axisangle ))
      {
        half_angle = Math.abs(angle_3 - axisangle);
        if ( angle_3 - axisangle  > 0 )
          swap_points = true;
      }
      else
      {
        half_angle = Math.abs(angle_4 - axisangle);
        if ( angle_4 - axisangle < 0 )
          swap_points = true;
      }
      
      if ( swap_points )                   // rearrange our copies of p3&p4 
      {                                    // so the wedge can start from p3
        last_p3 = new Point( p4 );         // an extend in the clockwise 
        p4      = new Point( p3 );         // direction.
      }
      else
        last_p3 = new Point( p3 );

      
      // create rectangle with p1 at its center that bounds the arc's circle
      // find radius of arc
      double xsquared = Math.pow( p1.x - p3.x, 2 );
      double ysquared = Math.pow( p1.y - p3.y, 2 );
      double radius = Math.sqrt( xsquared + ysquared );
      topleft = new Point( p1.x - (int)radius, p1.y - (int)radius );
      bottomright = new Point( topleft.x + (int)(2*radius),
                               topleft.y + (int)(2*radius) );
      
      // put angles in point for passing to overlay.
      angles.x = (int)Math.round(Math.toDegrees( axisangle - half_angle ));
      angles.y = (int)Math.round(Math.toDegrees( 2 * half_angle ));
      if ( angles.y > 0 )
      {
        graphics.drawArc( topleft.x, 
                          topleft.y,
                          (int)(2*radius),
                          (int)(2*radius),
                          angles.x,          // integer form of angle_3 
                          angles.y  );       // integer form of arcangle 
      }
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

    array[1] = last_p3;
    array[2] = p4;

    array[3] = topleft;
    array[4] = bottomright;
    array[5] = angles;

    return array;
  }

}
