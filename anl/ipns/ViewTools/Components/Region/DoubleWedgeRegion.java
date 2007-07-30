/*
 * File: DoubleWedgeRegion.java
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
 *  Revision 1.16  2007/07/30 14:25:32  dennis
 *  Updated javadocs.
 *
 *  Revision 1.15  2007/03/16 16:57:56  dennis
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
 *  Revision 1.14  2004/12/05 05:32:34  millermi
 *  - Fixed Eclipse warnings.
 *
 *  Revision 1.13  2004/05/20 20:48:26  millermi
 *  - Constructor now initializes world and image bounds to
 *    the bounds of the defining points.
 *
 *  Revision 1.12  2004/05/20 17:02:25  millermi
 *  - Made method getRegionBounds() public so it may be used by
 *    outside classes.
 *
 *  Revision 1.11  2004/05/11 01:06:44  millermi
 *  - Removed unused variables
 *
 *  Revision 1.10  2004/03/15 23:53:51  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.9  2004/03/12 02:08:03  rmikk
 *  Fixed package names
 *
 *  Revision 1.8  2004/03/09 17:23:11  millermi
 *  - Overload getDefiningPoints() from base class. This fixes
 *    bug that converted angles to image coordinates.
 *
 *  Revision 1.7  2004/02/14 03:34:56  millermi
 *  - selectedpoints no longer includes point found off the image.
 *  - added toString() method.
 *
 *  Revision 1.6  2004/01/07 06:44:53  millermi
 *  - Added static method getRegionUnion() which removes duplicate
 *    points from one or more selections.
 *  - Added protected methods initializeSelectedPoints() and
 *    getRegionBounds(). Each is needed by getRegionUnion()
 *    to calculate a unique set of points.
 *
 *  Revision 1.5  2003/12/23 18:39:22  millermi
 *  - Improved how region was calculated.
 *
 *  Revision 1.4  2003/12/20 05:42:26  millermi
 *  - Now corrects the topleft/bottomright defining points if
 *    they were scaled incorrectly by the image.
 *
 *  Revision 1.3  2003/12/16 01:45:08  millermi
 *  - Changed way selected points are calculated, now uses
 *    two WedgeRegion instances to calculate the points
 *    in the region.
 *
 *  Revision 1.2  2003/10/22 20:26:09  millermi
 *  - Fixed java doc errors.
 *
 *  Revision 1.1  2003/08/26 03:38:19  millermi
 *  - Initial Version - Allows double wedge selected regions to be passed to
 *    the viewer. Restricts wedge angle to 180 degrees.
 *
 */ 
package gov.anl.ipns.ViewTools.Components.Region;

import gov.anl.ipns.Util.Numeric.floatPoint2D;

/**
 * This class is a specific region designated by three points.
 * The DoubleWedgeRegion is used to pass points selected by a
 * symmetric double wedge region (in SelectionOverlay) from the view
 * component to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the double wedge are same as the wedge:
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
 * information at each step of the region calculation. Like a ElipseRegion,
 * this region may appear to be circular, but may actually be eliptical.
 */ 
public class DoubleWedgeRegion extends WedgeRegion
{

 /**
  * Construct a DoubleWedgRegion in WorldCoordinates using the spcified
  * center, corner point, axis angle and included angle.  The
  * defining points are assumed to be in WorldCoordinates.  Only the
  * center, top left corner point and angles are used to determine the
  * region.  The other values are adjusted to preserve symmetry.
  * NOTE: definingpoint[5] holds angular values in degrees.
  *
  *  @param  dp - defining points of the wedge
  */
  public DoubleWedgeRegion( floatPoint2D[] dp )
  {
    super(dp);
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
                                   // A point will be within the double wedge
                                   // provided it is in the wedge, or its
                                   // reflection across the center is in the
                                   // wedge
   if ( super.isInsideWC( x, y ) )
     return true;

   if ( super.isInsideWC( 2 * x_center - x, 2 * y_center - y ) )
     return true;

   return false;
 }

  
 /**
  * Display the region type with its defining points.
  *
  *  @return region type and defining points.
  */
  public String toString()
  {
    return ("Region: Double Wedge\n" +
            "Center: " + definingpoints[0] + "\n" +
	    "Arc Beginning Pt: " + definingpoints[1] + "\n" +
	    "Arc Ending Pt: " + definingpoints[2] + "\n" +
	    "Top-left bound: " + definingpoints[3] + "\n" +
	    "Bottom-right bound: " + definingpoints[4] + "\n" +
	    "Starting Angle: " + definingpoints[5].x + "\n" + 
	    "Interior Angle: " + definingpoints[5].y + "\n" );
  }
   
}
