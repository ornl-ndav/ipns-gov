/*
 * File: LineRegion.java
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
 *  Revision 1.3  2003/12/20 04:14:24  millermi
 *  - Cosmetic changes, made code more user friendly.
 *
 *  Revision 1.2  2003/10/22 20:27:58  millermi
 *  - Fixed java doc error.
 *
 *  Revision 1.1  2003/08/11 23:40:41  millermi
 *  - Initial Version - Used to pass region info from a
 *    ViewComponent to the viewer. WCRegion is an unrelated
 *    class that passes info from the overlay to the
 *    ViewComponent.
 *
 */ 
package DataSetTools.components.View.Region;

import java.awt.Point;
import java.util.Vector;

import DataSetTools.util.floatPoint2D;
import DataSetTools.components.image.CoordBounds; 
import DataSetTools.components.View.Cursor.SelectionJPanel;

/**
 * This class is a specific region designated by two points. A LineRegion is
 * used to pass points selected by a line region from a view component
 * to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 */ 
public class LineRegion extends Region
{
 /**
  * Constructor - provides basic initialization for all subclasses
  *
  *  @param  dp - defining points of the line
  */ 
  public LineRegion( floatPoint2D[] dp )
  {
    super(dp);
  }
  
 /**
  * Get all of the integer points on the line. This method assumes
  * that the input points are in (x,y) where (x = col, y = row ) form.
  *
  *  @return array of points on the line.
  */
  public Point[] getSelectedPoints()
  {
    return initializeSelectedPoints();
  }
  
 /**
  * This method is here to factor out the setting of the selected points.
  * By doing this, regions can make use of the getRegionUnion() method.
  *
  *  @return array of points included within the region.
  */
  protected Point[] initializeSelectedPoints()
  { 
    Point p1 = floorImagePoint(world_to_image.MapTo(definingpoints[0]));
    Point p2 = floorImagePoint(world_to_image.MapTo(definingpoints[1]));
    // assume dx > dy
    int numsegments = Math.abs( p2.x - p1.x );
    // if dy > dx, use dy
    if( numsegments < Math.abs( p2.y - p1.y ) )
    {
      numsegments = Math.abs( p2.y - p1.y );
    }
    // numsegments counts the interval not the points, so their are
    // numsegments+1 points.
    
    // Use a vector to hold points, then build an array from this vector.
    // This will allow removal of points not on the image.
    Vector pts = new Vector();
    floatPoint2D delta_p = new floatPoint2D();
    delta_p.x = (p2.x - p1.x)/(float)numsegments;
    delta_p.y = (p2.y - p1.y)/(float)numsegments;
    
    // Get all of the points, add them to the vector if the points are on
    // the image.
    floatPoint2D actual = new floatPoint2D( p1 );
    Point temp = new Point();
    CoordBounds imagebounds = world_to_image.getDestination();
    for( int i = 0; i < numsegments + 1; i++ )
    {
      temp = floorImagePoint(actual);
      // add it to the array if the point is on the image.
      if( imagebounds.onXInterval(temp.x) && imagebounds.onYInterval(temp.y) )
        pts.add(temp);
      // increment to next point
      actual.x += delta_p.x;
      actual.y += delta_p.y;
    }
    // construct static list of points.
    selectedpoints = new Point[pts.size()];
    for( int i = 0; i < pts.size(); i++ )
      selectedpoints[i] = (Point)pts.elementAt(i);
    return selectedpoints;
  }
  
 /**
  * Display the region type with its defining points.
  *
  *  @return region type and defining points.
  */
  public String toString()
  {
    return ("Region: Line\n" +
            "Start Point: " + definingpoints[0] + "\n" +
	    "End Point: " + definingpoints[1] + "\n");
  }
   
 /**
  * This method returns the rectangle containing the line.
  *
  *  @return The bounds of the LineRegion.
  */
  protected CoordBounds getRegionBounds()
  {
    return new CoordBounds( world_to_image.MapTo(definingpoints[0]).x,
                            world_to_image.MapTo(definingpoints[0]).y, 
                            world_to_image.MapTo(definingpoints[1]).x,
			    world_to_image.MapTo(definingpoints[1]).y );
  }
}
