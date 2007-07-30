/*
 * File: EllipseRegion.java
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
 *  Revision 1.12  2007/07/30 14:30:33  dennis
 *  The originally specified defining points are now modified to
 *  enforce symmetry.  Since the originaly defining points are
 *  typically obtained by transforming pixel locations (integer
 *  coordinates on a grid) to WorldCoordinates, symmetry conditions
 *  are often violated.  The constructor now adjusts the positions
 *  of the defining points (except for the center point) as needed
 *  to preserve symmetry.  This fixes a bug where a region would
 *  not seem to be constructed properly and would have a slightly
 *  different boundary after being drawn by a region editior.
 *
 *  Revision 1.11  2007/03/16 16:57:56  dennis
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
 *  Revision 1.10  2004/05/20 20:48:26  millermi
 *  - Constructor now initializes world and image bounds to
 *    the bounds of the defining points.
 *
 *  Revision 1.9  2004/05/20 17:02:26  millermi
 *  - Made method getRegionBounds() public so it may be used by
 *    outside classes.
 *
 *  Revision 1.8  2004/03/15 23:53:51  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.7  2004/03/12 02:09:06  rmikk
 *  Fixed package names
 *
 *  Revision 1.6  2004/02/14 03:34:56  millermi
 *  - selectedpoints no longer includes point found off the image.
 *  - added toString() method.
 *
 *  Revision 1.5  2004/01/07 06:44:53  millermi
 *  - Added static method getRegionUnion() which removes duplicate
 *    points from one or more selections.
 *  - Added protected methods initializeSelectedPoints() and
 *    getRegionBounds(). Each is needed by getRegionUnion()
 *    to calculate a unique set of points.
 *
 *  Revision 1.4  2003/12/20 07:04:48  millermi
 *  - Fixed copy/paste bug that caused an array out of bounds
 *    exception.
 *
 *  Revision 1.3  2003/12/20 05:42:25  millermi
 *  - Now corrects the topleft/bottomright defining points if
 *    they were scaled incorrectly by the image.
 *
 *  Revision 1.2  2003/12/18 22:38:00  millermi
 *  - Tweaked how x/yextent are calculated. Now must have a different
 *    of more than one to be reset.
 *
 *  Revision 1.1  2003/11/26 18:46:56  millermi
 *  - Renamed ElipseRegion.java to EllipseRegion.java
 *
 *  Revision 1.3  2003/11/26 01:57:44  millermi
 *  - Improved selection process.
 *
 *  Revision 1.2  2003/10/22 20:26:09  millermi
 *  - Fixed java doc errors.
 *
 *  Revision 1.1  2003/08/11 23:40:40  millermi
 *  - Initial Version - Used to pass region info from a
 *    ViewComponent to the viewer. WCRegion is an unrelated
 *    class that passes info from the overlay to the
 *    ViewComponent.
 *
 */ 
package gov.anl.ipns.ViewTools.Components.Region;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;

/**
 * This class is a specific region designated by two points which bound the
 * ellipse. The EllipseRegion is used to pass points selected by a
 * circle region (in SelectionOverlay) from the view component
 * to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the ellipse are the top left and bottom right points
 * of the rectangle bounding the ellipse. An ellipse must be used instead of a
 * circle, because although the selection looks circular, unless aspect ratio
 * is preserved, it may not be.
 */ 
public class EllipseRegion extends RegionWithInterior
{
  float x_center;
  float y_center;
  float dx;
  float dy;

 /**
  * Construct an EllipseRegion using the specified center and corner point.
  * The defining points are assumed to be in World Coordinates.  Only the 
  * center and top left points are used.  In order to preserve symmetry
  * the top left and bottom right points are reset to be symmetrically
  * space to the upper left and bottom right in World Coordinates.
  *
  * dp[0] = top-left corner
  * dp[1] = bottom-right corner
  * dp[2] = center
  *
  *  @param  dp - defining points of an ellipse or circle.
  */ 
  public EllipseRegion( floatPoint2D[] dp )
  {
    super(dp);

    x_center = dp[2].x;
    y_center = dp[2].y;
    dx       = Math.abs( dp[0].x - x_center );
    dy       = Math.abs( dp[0].y - y_center );
                                           
    dp[0].x = x_center - dx;   // Adjust the corner defining points, since they
    dp[0].y = y_center + dy;   // might have been off-center and/or not in
                               // the right order.
    dp[1].x = x_center + dx;
    dp[1].y = y_center - dy;
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
   if ( (x - x_center)*(x - x_center) / (dx * dx ) +
        (y - y_center)*(y - y_center) / (dy * dy )  <= 1 )
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
    return new CoordBounds( x_center - dx, y_center - dy, 
                            x_center + dx, y_center + dy );
 }

  
 /**
  * Display the region type with its defining points.
  *
  *  @return region type and defining points.
  */
  public String toString()
  {
    return ("Region: Ellipse\n" +
            "Center: " + definingpoints[2] + "\n" +
	    "Top-left bound: " + definingpoints[0] + "\n" +
	    "Bottom-right bound: " + definingpoints[1] + "\n");
  }
   
}
