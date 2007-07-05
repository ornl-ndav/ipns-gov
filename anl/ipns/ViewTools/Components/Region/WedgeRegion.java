/*
 * File: WedgeRegion.java
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
 * Modified:
 *
 *  $Log$
 *  Revision 1.22  2007/07/05 00:17:24  dennis
 *  Fixed error in comment.
 *
 *  Revision 1.21  2007/03/16 16:57:56  dennis
 *  Major refactoring.  This class is now derived from the
 *  RegionWithInterior class.  The getSelectedPoints() method is
 *  implemented uniformly in the base class, RegionWithInterior.
 *  This is a major simplification.  The key method is the
 *  isInsideWC(x,y) that determines which world coordinate points
 *  are inside the region, and getRegionBoundsWC() that provides
 *  a bounding rectangle for this region in world coordinates.
 *  Removed initializeSelectedPoints() method that is no longer
 *  needed.
 *
 *  Revision 1.20  2005/01/18 22:59:00  millermi
 *  - Fixed gramatical error in comments.
 *
 *  Revision 1.19  2004/05/20 20:48:27  millermi
 *  - Constructor now initializes world and image bounds to
 *    the bounds of the defining points.
 *
 *  Revision 1.18  2004/05/20 17:02:27  millermi
 *  - Made method getRegionBounds() public so it may be used by
 *    outside classes.
 *
 *  Revision 1.17  2004/05/11 01:30:48  millermi
 *  - Removed unused variables.
 *  - Removed commented code which made line selections part of the
 *    wedge region.
 *
 *  Revision 1.16  2004/03/15 23:53:52  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.15  2004/03/12 02:07:45  rmikk
 *  Fixed package names
 *
 *  Revision 1.14  2004/03/09 17:23:11  millermi
 *  - Overload getDefiningPoints() from base class. This fixes
 *    bug that converted angles to image coordinates.
 *
 *  Revision 1.13  2004/02/17 01:57:52  millermi
 *  - Removed LineRegions which selected points on the boundry of the
 *    wedge. This fixes a bug that causes an index out of bounds
 *    exception found by Alok.
 *
 *  Revision 1.12  2004/02/14 03:34:57  millermi
 *  - selectedpoints no longer includes point found off the image.
 *  - added toString() method.
 *
 *  Revision 1.11  2004/01/07 06:44:53  millermi
 *  - Added static method getRegionUnion() which removes duplicate
 *    points from one or more selections.
 *  - Added protected methods initializeSelectedPoints() and
 *    getRegionBounds(). Each is needed by getRegionUnion()
 *    to calculate a unique set of points.
 *
 *  Revision 1.10  2003/12/23 18:41:53  millermi
 *  - Added adjustments to p1 and rp1 so the LineRegion is
 *    closer to the rest of the wedge.
 *  - Improved selection capability.
 *
 *  Revision 1.9  2003/12/20 05:42:25  millermi
 *  - Now corrects the topleft/bottomright defining points if
 *    they were scaled incorrectly by the image.
 *
 *  Revision 1.8  2003/12/20 04:15:59  millermi
 *  - Included an off-by-one on the pointangle so that
 *    border points are always included. This eleviates
 *    "holes" in the wedge.
 *
 *  Revision 1.7  2003/12/18 22:38:00  millermi
 *  - Tweaked how x/yextent are calculated. Now must have a different
 *    of more than one to be reset.
 *
 *  Revision 1.6  2003/12/16 01:42:24  millermi
 *  - made pointchecker protected variable so it could be
 *    used by the DoubleWedgeCursor.
 *
 *  Revision 1.5  2003/12/13 01:16:03  millermi
 *  - Lines bounding wedge now use the LineRegion class
 *    to find all points on the boundary consistently.
 *  - Fixed bug that distorted the wedge when near the
 *    border of the image.
 *
 *  Revision 1.4  2003/12/12 06:11:44  millermi
 *  - Completely renovated how the points are selected.
 *    Previously slope was used to restrict points, now
 *    the angle of the point is calculated and compared
 *    with the starting/ending angles of the arc.
 *  - This class now assumes the arc will be drawn
 *    counterclockwise, so the starting angle may be
 *    larger than the stopping angle if the 4th to
 *    1st quadrant selection is made.
 *
 *  Revision 1.3  2003/10/22 20:26:09  millermi
 *  - Fixed java doc errors.
 *
 *  Revision 1.2  2003/08/21 22:41:08  millermi
 *  - Commented out debug statements and removed out code
 *    that was commented out.
 *
 *  Revision 1.1  2003/08/21 18:21:36  millermi
 *  - Initial Version - uses equation for ellipse and slope of a line to
 *    determine if a point is in the region.
 *
 */ 
package gov.anl.ipns.ViewTools.Components.Region;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;

/**
 * This class is a specific region designated by three points.
 * The WedgeRegion is used to pass points selected by a
 * wedge region (in SelectionOverlay) from the view component
 * to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the wedge are:
 * 
 * p[0]   = center pt of circle that arc is taken from
 * p[1]   = last mouse point/point at intersection of line and arc
 * p[2]   = reflection of p[1]
 * p[3]   = top left corner of bounding box around arc's total circle
 * p[4]   = bottom right corner of bounding box around arc's circle
 * p[5].x = startangle, the directional vector in degrees
 * p[5].y = degrees covered by arc.
 *
 * The large number of defining points replaces the work of recalculating
 * information at each step of the region calculation. Like a EllipseRegion,
 * this region may appear to be circular, but may actually be elliptical.
 */ 
public class WedgeRegion extends RegionWithInterior
{
  float x_center;       // World Coordinates of center
  float y_center;
                        // NOTE: Circle could be ellipse in WC
  float dx;             // half-width of ellipse in X-direction
  float dy;             // half-height of ellipse in Y-direction

  float start_angle;    // angle of initial side, in radians 
  float included_angle; // included angle ( <= 360 ), in radians 

 /**
  * Constructor - uses Region's constructor to set the defining points.
  * The defining points are assumed to be in image values, where
  * the input points are in (x,y) where (x = col, y = row ) form.
  * The only exception is definingpoint[5] which holds angular (in degrees)
  * values.
  *
  *  @param  dp - defining points of the wedge
  */ 
  public WedgeRegion( floatPoint2D[] dp )
  {
    super(dp);
    
    x_center = dp[0].x;
    y_center = dp[0].y;

    dx = Math.abs( dp[3].x - x_center );
    dy = Math.abs( dp[3].y - y_center );

    start_angle = (float)Math.atan2( dp[1].y - y_center, dp[1].x - x_center );

    float end_angle = 
                   (float)Math.atan2( dp[2].y - y_center, dp[2].x - x_center );

    included_angle = end_angle - start_angle;

    while ( included_angle <= 0 )
      included_angle += (float)(2*Math.PI);

    while  ( included_angle >= 2*Math.PI )
      included_angle -= (float)(2*Math.PI);
  }


 /**
  *  Check whether or not the specified World Coordinate point is inside 
  *  of the Region.
  *
  *  @param x   The x-coordinate of the point, in world coordinates.
  *  @param y   The y-coordinate of the point, in world coordinates.
  *
  *  @return true if the point is in the region and false otherwise.
  */
 public boolean isInsideWC( float x, float y )
 {
   float x_diff_sqr = (x - x_center)*(x - x_center);
   float y_diff_sqr = (y - y_center)*(y - y_center);

   if ( x_diff_sqr/(dx*dx) + y_diff_sqr/(dy*dy) > 1 )
     return false;

   float angle = (float)Math.atan2( y - y_center, x - x_center );
   while ( angle < start_angle )
     angle += (float)(2*Math.PI);

   if ( angle <= start_angle + included_angle )
     return true;

   return false;
 }


 /**
  *  Get a bounding box for the region, in World Coordinates.  The
  *  points of the region will lie in the X-interval [X1,X2] and
  *  in the Y-interval [Y1,Y2], where X1,X2,Y1 and Y2 are the values
  *  returned by the CoordBounds getX1(), getX2(), getY1(), getY2()
  *  methods.
  * 
  *  @return a CoordBounds object containing the full extent of this
  *          region.
  */
 public CoordBounds getRegionBoundsWC()
 {                                      // NOTE: We could get tighter
                                        //       bound here
    return new CoordBounds( x_center - dx, y_center - dy,
                            x_center + dx, y_center + dy );
 }

  
 /**
  * Display the region type with its defining points.
  *
  *  @return A String giving the region type and defining points.
  */
  public String toString()
  {
    return ("Region: Wedge\n" +
            "Center: " + definingpoints[0] + "\n" +
	    "Arc Beginning Pt: " + definingpoints[1] + "\n" +
	    "Arc Ending Pt: " + definingpoints[2] + "\n" +
	    "Top-left bound: " + definingpoints[3] + "\n" +
	    "Bottom-right bound: " + definingpoints[4] + "\n" +
	    "Starting Angle: " + definingpoints[5].x + "\n" + 
	    "Interior Angle: " + definingpoints[5].y + "\n" );
  }
   
}
