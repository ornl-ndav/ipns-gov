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
 *  Revision 1.11  2007/04/07 21:23:58  dennis
 *  Removed two unused variables.
 *
 *  Revision 1.10  2007/03/16 16:48:36  dennis
 *  Major refactoring.  Now overides getSelectedPoints() to deterimine
 *  which point(s) this region should indicate as selected.  Removed
 *  initializeSelectedPoints() method that is no longer needed.  Removed
 *  getRegionBounds() method, since that is now taken care of in the
 *  base class using the world coordinate bounds.  Added getRegionBoundsWC().
 *
 *  Revision 1.9  2004/05/20 20:48:26  millermi
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
 *  Revision 1.6  2004/03/12 02:10:21  rmikk
 *  changed package names
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
package gov.anl.ipns.ViewTools.Components.Region;

import java.awt.Point;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;

/**
 * This class is a specific region designated by two points. A LineRegion is
 * used to pass points selected by a line region from a view component
 * to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 */ 
public class LineRegion extends Region
{
  private float min_x = 0;
  private float max_x = 1;
  private float min_y = 0;
  private float max_y = 1;

 /**
  * Constructor - provides basic initialization for all subclasses
  *
  *  @param  dp - defining points of the line
  */ 
  public LineRegion( floatPoint2D[] dp )
  {
    super(dp);

    min_x = Math.min( dp[0].x, dp[1].x );
    max_x = Math.max( dp[0].x, dp[1].x );
    min_y = Math.min( dp[0].y, dp[1].y );
    max_y = Math.max( dp[0].y, dp[1].y );
  }


 /**
  *  Get a bounding box for the region, in World Coordinates.  The
  *  points of the region will lie in the X-interval [X1,X2] and
  *  in the Y-interval [Y1,Y2], where X1,X2,Y1 and Y2 are the values
  *  returned by the CoordBounds.getX1(), getX2(), getY1(), getY2()
  *  methods.
  * 
  *  @return a CoordBounds object containing the full extent of this
  *          region.
  */
 public CoordBounds getRegionBoundsWC()
 {
   return new CoordBounds( min_x, min_y, max_x, max_y ); 
 }


 public Point[] getSelectedPoints( CoordTransform world_to_array )
 {
   CoordBounds imagebounds = world_to_array.getDestination();

   floatPoint2D cell_0 = new floatPoint2D( 0, 0 );
   floatPoint2D cell_1 = new floatPoint2D( 1, 1 );
   cell_0 = world_to_array.MapFrom( cell_0 );
   cell_1 = world_to_array.MapFrom( cell_1 );
   floatPoint2D unit_cell = new floatPoint2D( cell_1.x - cell_0.x,
                                              cell_1.y - cell_0.y );

   Point start = floorImagePoint( world_to_array.MapTo(definingpoints[0]) );
   Point end   = floorImagePoint( world_to_array.MapTo(definingpoints[1]) );

                                                    // weed out degenerate case
                                                    // with one or 0 points
   if ( start.x - end.x == 0 && start.y - end.y == 0 )
   {
     if ( imagebounds.onXInterval(start.x) && imagebounds.onYInterval(start.y))
     {
       Point[] result = new Point[1];
       result[0] = start;
       return result;
     }
     else
       return new Point[0];
   }

   float dy    = definingpoints[1].y - definingpoints[0].y;
   float dx    = definingpoints[1].x - definingpoints[0].x;
   floatPoint2D center_1 = world_to_array.MapFrom( 
                           new floatPoint2D( start.x + 0.5f, start.y + 0.5f ));

   floatPoint2D line_pt = new floatPoint2D( definingpoints[0] );
   Point[] result;

                                                 // choose independent variable
   if ( Math.abs( end.x - start.x ) > Math.abs( end.y - start.y ) )
   { 
                                                 // step a point along the line
                                                 // starting with the WC point
     float dy_dx = dy/dx;                        // on the line, directly above 
     float delta_x = center_1.x - line_pt.x;     // or below the center point
     line_pt.x += delta_x;                       // of the first cell used.
     line_pt.y += delta_x * dy_dx;
                                                 // adjust sign of step in x
                                                 // to step from first to last
                                                 // defining point. 
     float step_in_x = unit_cell.x; 
     if ( step_in_x * (definingpoints[1].x - definingpoints[0].x) < 0 )
       step_in_x = -step_in_x;
                                                 // at each step in x, use cell
                                                 // that contains the (x,y) 
                                                 // point along the line.
     int n_points = Math.abs( end.x - start.x ) + 1;
     result = new Point[n_points];
     for ( int i = 0; i < n_points; i++ )
     {
       result[i] = floorImagePoint( world_to_array.MapTo(line_pt) );
       line_pt.x += step_in_x;
       line_pt.y += step_in_x * dy_dx;
     }
   }
   else                                         // treat x as function of y
   {
     float dx_dy = dx/dy;
     float delta_y = center_1.y - line_pt.y;

     line_pt.y += delta_y;
     line_pt.x += delta_y * dx_dy;

     float step_in_y = unit_cell.y;
     if ( step_in_y * (definingpoints[1].y - definingpoints[0].y) < 0 )
       step_in_y = -step_in_y;

     int n_points = Math.abs( end.y - start.y ) + 1;
     result = new Point[n_points];
     for ( int i = 0; i < n_points; i++ )
     {
       result[i] = floorImagePoint( world_to_array.MapTo(line_pt) );
       line_pt.y += step_in_y;
       line_pt.x += step_in_y * dx_dy;
     }
   }
   return result;
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
	    "End Point:   " + definingpoints[1] + "\n");
  }

}
