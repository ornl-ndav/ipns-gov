/*
 * File: AnnularRegion.java
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
 *  Revision 1.8  2007/03/16 16:57:56  dennis
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
 *  Revision 1.7  2004/05/20 20:48:25  millermi
 *  - Constructor now initializes world and image bounds to
 *    the bounds of the defining points.
 *
 *  Revision 1.6  2004/05/20 17:02:25  millermi
 *  - Made method getRegionBounds() public so it may be used by
 *    outside classes.
 *
 *  Revision 1.5  2004/03/15 23:53:51  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.4  2004/03/12 02:05:50  rmikk
 *  Fixed package names
 *
 *  Revision 1.3  2004/02/14 03:34:56  millermi
 *  - selectedpoints no longer includes point found off the image.
 *  - added toString() method.
 *
 *  Revision 1.2  2004/01/07 06:44:53  millermi
 *  - Added static method getRegionUnion() which removes duplicate
 *    points from one or more selections.
 *  - Added protected methods initializeSelectedPoints() and
 *    getRegionBounds(). Each is needed by getRegionUnion()
 *    to calculate a unique set of points.
 *
 *  Revision 1.1  2003/12/30 00:01:10  millermi
 *  - Initial Version - This class allows users to select regions
 *    in the shape of a ring.
 *
 */ 
package gov.anl.ipns.ViewTools.Components.Region;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;

/**
 * The AnnularRegion is used to pass points selected between two
 * elliptical regions (a ring) (in SelectionOverlay) from the view component
 * to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the ring are:
 * p[0]   = center pt of circle
 * p[1]   = top left corner of bounding box of inner circle
 * p[2]   = bottom right corner of bounding box of inner circle
 * p[3]   = top left corner of bounding box of outer circle
 * p[4]   = bottom right corner of bounding box of outer circle
 */ 
public class AnnularRegion extends RegionWithInterior
{
  float x_center;
  float y_center;
  float dx_1;
  float dy_1;
  float dx_2;
  float dy_2;

 /**
  * Constructor - uses Region's constructor to set the defining points.
  * The defining points are assumed to be in image values, where
  * the input points are in (x,y) where (x = col, y = row ) form.
  *
  *  @param  dp - world coord defining points of a ring.
  */ 
  public AnnularRegion( floatPoint2D[] dp )
  {
    super(dp);
    x_center = dp[0].x;  
    y_center = dp[0].y;  
    dx_1 = Math.abs( dp[1].x - x_center );
    dy_1 = Math.abs( dp[1].y - y_center );
    dx_2 = Math.abs( dp[3].x - x_center );
    dy_2 = Math.abs( dp[3].y - y_center );
  }
  

 /**
  *  Check whether or not the specified World Coordinate point is inside 
  *  of the Region.
  *
  *  @param x   The x-coordinate of the point, in world coordinates.
  *  @param y   The y-coordinate of the point, in world coordinates.
  *  @return true if the point is in the region and false otherwise.
  */
 public boolean isInsideWC( float x, float y )
 {
   float x_diff_sqr = (x - x_center)*(x - x_center);
   float y_diff_sqr = (y - y_center)*(y - y_center);
   if ( x_diff_sqr/(dx_1 * dx_1) + y_diff_sqr/(dy_1 * dy_1 ) >= 1  && 
        x_diff_sqr/(dx_2 * dx_2) + y_diff_sqr/(dy_2 * dy_2 ) <= 1   )
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
 {
    return new CoordBounds( x_center - dx_2, y_center - dy_2,
                            x_center + dx_2, y_center + dy_2 );
 }

  
 /**
  * Display the region type with its defining points.
  *
  *  @return region type and defining points.
  */
  public String toString()
  {
    return ("Region: Annular\n" +
            "Center: " + definingpoints[0] + "\n" +
	    "Top-left bound(inner): " + definingpoints[1] + "\n" +
	    "Bottom-right bound(inner): " + definingpoints[2] + "\n" +
	    "Top-left bound(outer): " + definingpoints[3] + "\n" +
	    "Bottom-right bound(outer): " + definingpoints[4] + "\n");
  }
   
}
