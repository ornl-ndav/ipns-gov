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
    floatPoint2D p1 = new floatPoint2D( definingpoints[0] );
    floatPoint2D p2 = new floatPoint2D( definingpoints[1] );
    // assume dx > dy
    float numsegments = Math.abs( p2.x - p1.x );
    // if dy > dx, use dy
    if( numsegments < Math.abs( p2.y - p1.y ) )
    {
      numsegments = Math.abs( p2.y - p1.y );
    }
    // numsegments counts the interval not the points, so their are
    // numsegments+1 points.
    
    selectedpoints = new Point[Math.round(numsegments) + 1];
    floatPoint2D delta_p = new floatPoint2D();
    delta_p.x = (p2.x - p1.x)/numsegments;
    delta_p.y = (p2.y - p1.y)/numsegments;
    
    floatPoint2D actual = new floatPoint2D( p1 );
    selectedpoints[0] = actual.toPoint();
    for( int i = 1; i < numsegments; i++ )
    {
      actual.x += delta_p.x;
      actual.y += delta_p.y;
      selectedpoints[i] = actual.toPoint();
    }
    selectedpoints[Math.round(numsegments)] = p2.toPoint();
    
    return selectedpoints;
  } 
   
 /**
  * This method returns the rectangle containing the line.
  *
  *  @return The bounds of the LineRegion.
  */
  protected CoordBounds getRegionBounds()
  {
    return new CoordBounds( definingpoints[0].x,
                            definingpoints[0].y, 
                            definingpoints[1].x,
			    definingpoints[1].y );
  }
}
