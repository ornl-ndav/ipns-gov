/*
 * File: BoxRegion.java
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
 *  Revision 1.10  2007/03/11 04:37:16  dennis
 *  Added methods to setWorldToArrayTran() and getWorldToArrayTran().
 *
 *  Revision 1.9  2004/05/20 20:48:26  millermi
 *  - Constructor now initializes world and image bounds to
 *    the bounds of the defining points.
 *
 *  Revision 1.8  2004/05/20 17:02:25  millermi
 *  - Made method getRegionBounds() public so it may be used by
 *    outside classes.
 *
 *  Revision 1.7  2004/05/11 01:04:13  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.6  2004/03/15 23:53:51  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.5  2004/03/12 02:03:42  rmikk
 *  Fixed package names
 *
 *  Revision 1.4  2004/02/14 03:34:56  millermi
 *  - selectedpoints no longer includes point found off the image.
 *  - added toString() method.
 *
 *  Revision 1.3  2004/01/07 06:44:53  millermi
 *  - Added static method getRegionUnion() which removes duplicate
 *    points from one or more selections.
 *  - Added protected methods initializeSelectedPoints() and
 *    getRegionBounds(). Each is needed by getRegionUnion()
 *    to calculate a unique set of points.
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
 * This class is a specific region designated by two points. A BoxRegion is
 * used to pass points selected by a rectangular region from the view
 * component to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the box are:
 * p[0] = one corner
 * p[1] = diagonally opposite corner
 */ 
public class BoxRegion extends RegionWithInterior
{
  private float min_x = 0;
  private float max_x = 1;
  private float min_y = 0;
  private float max_y = 1;

 /**
  * Constructor - provides basic initialization for all subclasses
  *
  *  @param  dp - defining points of the box
  */ 
  public BoxRegion( floatPoint2D[] dp )
  {
    super(dp);

    min_x = Math.min( definingpoints[0].x, definingpoints[1].x );
    max_x = Math.max( definingpoints[0].x, definingpoints[1].x );
    min_y = Math.min( definingpoints[0].y, definingpoints[1].y );
    max_y = Math.max( definingpoints[0].y, definingpoints[1].y );
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
   if ( x >= min_x && x <= max_x &&
        y >= min_y && y <= max_y  )
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
    return new CoordBounds( min_x, min_y, max_x, max_y );
 }
  
  
 /**
  * Display the region type with its defining points.
  *
  *  @return region type and defining points.
  */
  public String toString()
  {
    return ("Region: Box\n" +
            "Top-left Corner:     " + definingpoints[0] + "\n" +
	    "Bottom-right Corner: " + definingpoints[1] + "\n");
  }
   
}
