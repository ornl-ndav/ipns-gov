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
package DataSetTools.components.View.Region;

import java.awt.Point;
import java.util.Vector;

import DataSetTools.util.floatPoint2D;
import DataSetTools.components.image.CoordBounds;
import DataSetTools.components.View.Cursor.SelectionJPanel;

/**
 * This class is a specific region designated by two points. A BoxRegion is
 * used to pass points selected by a rectangular region from the view
 * component to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 */ 
public class BoxRegion extends Region
{
 /**
  * Constructor - provides basic initialization for all subclasses
  *
  *  @param  dp - defining points of the box
  */ 
  public BoxRegion( floatPoint2D[] dp )
  {
    super(dp);
  }
  
 /**
  * Get all of the points inside the region. This method assumes
  * that the input points are in (x,y) where (x = col, y = row ) form.
  * The points are entered into the array row by row.
  *
  *  @return array of points included within the region.
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
    Point topleft = floorImagePoint(world_to_image.MapTo(definingpoints[0]));
    Point bottomright = floorImagePoint(
                           world_to_image.MapTo(definingpoints[1]) );
    int w = bottomright.x - topleft.x + 1;
    int h = bottomright.y - topleft.y + 1;
    
    Vector pts = new Vector();
    CoordBounds imagebounds = world_to_image.getDestination();
    // Set through box rowwise, getting points that are on the image.
    for( int row = topleft.x; row <= bottomright.x; row++ )
    {
      for( int col = topleft.y; col <= bottomright.y; col++ )
      {
        // add it to the array if the point is on the image.
        if( imagebounds.onXInterval(row) && imagebounds.onYInterval(col) )
          pts.add(new Point(row,col));
      }
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
    return ("Region: Box\n" +
            "Top-left Corner: " + definingpoints[0] + "\n" +
	    "Bottom-right Corner: " + definingpoints[1] + "\n");
  }
   
 /**
  * This method returns the rectangle representing the box.
  *
  *  @return The bounds of the BoxRegion.
  */
  protected CoordBounds getRegionBounds()
  {
    return new CoordBounds( world_to_image.MapTo(definingpoints[0]).x,
                            world_to_image.MapTo(definingpoints[0]).y, 
                            world_to_image.MapTo(definingpoints[1]).x,
			    world_to_image.MapTo(definingpoints[1]).y );
  }
}
