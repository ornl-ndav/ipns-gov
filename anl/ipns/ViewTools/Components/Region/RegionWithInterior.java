/*
 * File: RegionWithInterior.java
 *
 * Copyright (C) 2007, Dennis Mikklelson 
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2007/03/16 16:31:48  dennis
 *  New subclass of Region to deal with regions like box, wedge, circle,
 *  etc. that have an interior, as opposed to regions like line and point
 *  that don't have an interior.  All regions with interior are now handled
 *  uniformly using a method to determine whether or not a point is in
 *  the region, in world coordinates.
 *
 */ 
package gov.anl.ipns.ViewTools.Components.Region;

import java.awt.Point;
import java.util.Vector;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;

/**
 * This class is a base class for Regions that have an interior, such as
 * boxes, circles, etc.  These regions must have a method, isInsideWC(x,y)
 * to determine whether or not a point, specified in world coordinates,
 * is inside the region.  This allows finding which discrete array (or pixel)
 * elements are inside the region by stepping over all disrete elements in
 * a bounding box for the region, mapping the centers of the discrete elements 
 * to world coordinates, and checking whether or not the centers fall in 
 * the region.
 * such a discrete coordinate system.
 */ 
public abstract class RegionWithInterior extends    Region
                                         implements java.io.Serializable
{
  
 /**
  * Constructor 
  *
  *  @param  dp - world coordinate defining points of the region.
  */ 
  protected RegionWithInterior( floatPoint2D[] dp )
  {
    super(dp);
    definingpoints = dp;
  }


 /**
  *  Check whether or not the specified World Coordinate point is inside 
  *  of the Region.  This method must be overridden in a meaningful way 
  *  by derived classes, such as BoxRegion, that have a non-degenerate
  *  interior.  For such regions, this method is used by the base class
  *  implementation of getSelectedPoints(), so such derived classes should
  *  NOT need to override the getSelectedPoints() method.  On the other 
  *  hand, derived classes like PointRegion or LineRegion that have 
  *  an empty interior MUST override getSelectedPoints().
  *  It is the responsibility of concrete derived classes to determine 
  *  which points are selected, by either overriding isInsideWC() and
  *  getRegionBoundsWC(), OR overriding getSelectedPoints().
  *
  *  @param x   The x-coordinate of the point, in world coordinates.
  *  @param y   The y-coordinate of the point, in world coordinates.
  *  @return true if the point is in the region and false otherwise.
  */
 abstract public boolean isInsideWC( float x, float y );


 /**
  * Get all of the image points inside the region. The use of
  * Point was chosen over floatPoint2D because at this point we are dealing
  * with row/column coordinates, so rounding is acceptable. This method assumes
  * that the input points are in (x,y) where (x = col, y = row ) form.
  *
  *  @return array of points included within the region.
  */
  public Point[] getSelectedPoints( CoordTransform world_to_array )
  {
    CoordBounds bounds = getRegionBoundsWC();

    floatPoint2D min_point = new floatPoint2D( bounds.getX1(), bounds.getY1() );
    floatPoint2D max_point = new floatPoint2D( bounds.getX2(), bounds.getY2() );

    min_point = world_to_array.MapTo( min_point );
    max_point = world_to_array.MapTo( max_point );

    if ( min_point.x > max_point.x )
    {
      float temp = min_point.x;
      min_point.x = max_point.x;
      max_point.x = temp;
    }

    if ( min_point.y > max_point.y )
    {
      float temp = min_point.y;
      min_point.y = max_point.y;
      max_point.y = temp;
    }

    min_point.x = (float)Math.floor( min_point.x );
    min_point.y = (float)Math.floor( min_point.y );

    max_point.x = (float)Math.ceil( max_point.x );
    max_point.y = (float)Math.ceil( max_point.y );

                                           // Step through box rowwise, getting
                                           // points that are in the region.
    Vector pts = new Vector();
    floatPoint2D temp_point = new floatPoint2D();
    for( int row = (int)min_point.y; row <= (int)max_point.y; row++ )
    {
      for( int col = (int)min_point.x; col <= (int)max_point.x; col++ )
      {
        temp_point.x = col + 0.5f;
        temp_point.y = row + 0.5f;
        temp_point = world_to_array.MapFrom( temp_point );
        if ( isInsideWC( temp_point.x, temp_point.y ) )
          pts.add(new Point(col,row));
      }
    }
                                            // construct final list of points.
    Point[] selectedpoints = new Point[pts.size()];
    for( int i = 0; i < pts.size(); i++ )
      selectedpoints[i] = (Point)pts.elementAt(i);

    return selectedpoints;
  }
  
}
