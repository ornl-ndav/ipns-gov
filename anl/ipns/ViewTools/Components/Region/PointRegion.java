/*
 * File: PointRegion.java
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
 *  Revision 1.10  2007/03/16 16:48:35  dennis
 *  Major refactoring.  Now overides getSelectedPoints() to deterimine
 *  which point(s) this region should indicate as selected.  Removed
 *  initializeSelectedPoints() method that is no longer needed.  Removed
 *  getRegionBounds() method, since that is now taken care of in the
 *  base class using the world coordinate bounds.  Added getRegionBoundsWC().
 *
 *  Revision 1.9  2004/05/20 20:48:27  millermi
 *  - Constructor now initializes world and image bounds to
 *    the bounds of the defining points.
 *
 *  Revision 1.8  2004/05/20 17:02:26  millermi
 *  - Made method getRegionBounds() public so it may be used by
 *    outside classes.
 *
 *  Revision 1.7  2004/03/15 23:53:52  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.6  2004/03/12 02:11:15  rmikk
 *  Fixed package names
 *
 *  Revision 1.5  2004/02/14 03:34:57  millermi
 *  - selectedpoints no longer includes point found off the image.
 *  - added toString() method.
 *
 *  Revision 1.4  2004/01/07 06:44:53  millermi
 *  - Added static method getRegionUnion() which removes duplicate
 *    points from one or more selections.
 *  - Added protected methods initializeSelectedPoints() and
 *    getRegionBounds(). Each is needed by getRegionUnion()
 *    to calculate a unique set of points.
 *
 *  Revision 1.3  2003/10/29 20:30:11  millermi
 *  - Fixed java docs.
 *
 *  Revision 1.2  2003/10/22 20:26:09  millermi
 *  - Fixed java doc errors.
 *
 *  Revision 1.1  2003/08/11 23:40:41  millermi
 *  - Initial Version - Used to pass region info from a
 *    ViewComponent to the viewer. WCRegion is an unrelated
 *    class that passes info from the overlay to the
 *    ViewComponent.
 *
 */ 
package gov.anl.ipns.ViewTools.Components.Region;

import java.awt.Point;
import java.util.Vector;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;

/**
 * This class passes one or more selected points. Most of the functionality
 * provided by the base class works for the case of a point region. Thus,
 * only the constructor is needed.
 */ 
public class PointRegion extends Region
{
  float min_x;
  float max_x;
  float min_y;
  float max_y;

 /**
  * Constructor takes in an array of Points, each defining a point region.
  *
  *  @param  dp - defining point regions
  */ 
  public PointRegion( floatPoint2D[] dp )
  {
    super(dp);
    
    // Give the image and world bounds meaningful values.
    min_x = definingpoints[0].x;
    max_x = min_x; 
    min_y = definingpoints[0].y;
    max_y = min_y;
    for( int i = 1; i < definingpoints.length; i++ )
    {
      if( definingpoints[i].x < min_x )
        min_x = definingpoints[i].x;
      else if( definingpoints[i].x > max_x )
        max_x = definingpoints[i].x;

      if( definingpoints[i].y < min_y )
        min_y = definingpoints[i].y;
      else if( definingpoints[i].y > max_y )
        max_y = definingpoints[i].y;
    }
  }
  

 /**
  *  Get a bounding box for the region, in World Coordinates.  The
  *  points of the region will lie in the X-interval [X1,X2] and
  *  in the Y-interval [Y1,Y2], where X1,X2,Y1 and Y2 are the values
  *  returned by the CoordBounds.getX1(), getX2(), getY1(), getY2()
  *  methods.
  * 
  *  @return a CoordBounds object containing the full extent of this
  *          list of points.
  */
 public CoordBounds getRegionBoundsWC()
 {
    return new CoordBounds( min_x, min_y, max_x, max_y );
 }
  

 /**
  * Get the array elements currently selected, based on the world coordinate
  * points and the mapping from world to array coordinates.
  *
  *  @param  world_to_array  The transformation from world coordinates to
  *                          array coordinates.
  *
  *  @return array of Points, each of which contains one or more of
  *          the selected points.
  */
  public Point[] getSelectedPoints( CoordTransform world_to_array )
  {
    Vector pts = new Vector();
    CoordBounds imagebounds = world_to_array.getDestination();
    Point temp;

               // Convert defining points from world coords to image coords and
               // add it to the array if the point is on the image.
    for( int i = 0; i < definingpoints.length; i++ )
    {
      temp = floorImagePoint(world_to_array.MapTo(definingpoints[i]));
      if( imagebounds.onXInterval(temp.x) && imagebounds.onYInterval(temp.y) )
        pts.add(temp);
    }

                                                  // construct array of points.
    Point[] selectedpoints = new Point[pts.size()];
    for( int i = 0; i < pts.size(); i++ )
      selectedpoints[i] = (Point)pts.elementAt(i);
    return selectedpoints;
  } 
  

 /**
  * Display the region type with its defining points. For a PointRegion,
  * all points will be displayed.
  *
  *  @return region type and defining points.
  */
  public String toString()
  {
    StringBuffer regstring = new StringBuffer("Region: Point\n");
    // add all points to be displayed.
    for( int i = 0; i < definingpoints.length; i++ )
    {
      regstring.append("Point " + (i+1) + ": " + definingpoints[i] + "\n");
    }
    return regstring.toString();
  } 

}
