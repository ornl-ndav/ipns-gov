/*
 * File: Region.java
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
 *  Revision 1.3  2003/11/18 01:03:29  millermi
 *  - Now implement serializable to allow saving of state.
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
package DataSetTools.components.View.Region;

import java.awt.Point;
import java.util.Vector;

import DataSetTools.util.floatPoint2D;
import DataSetTools.components.image.CoordBounds;

/**
 * This class is a base class for all regions in the Region package. A Region is
 * used to pass selected regions (selected using the Selection Overlay) from a
 * view component to the viewer. Given the defining points of a region,
 * subclasses of this class can return all of the points inside the selected
 * region. 
 */ 
public abstract class Region implements java.io.Serializable
{
  protected floatPoint2D[] definingpoints;
  protected Point[] selectedpoints;
  
 /**
  * Constructor - provides basic initialization for all subclasses
  *
  *  @param  dp - defining points of the region.
  */ 
  protected Region( floatPoint2D[] dp )
  {
    definingpoints = dp;
    selectedpoints = new Point[0];
  }
  
 /**
  * Get the world coordinate points used to define the geometric shape.
  *
  *  @return array of points that define the region.
  */
  public floatPoint2D[] getDefiningPoints()
  {
    return definingpoints;
  } 
  
 /**
  * Get all of the points inside the region. The use of
  * Point was chosen over floatPoint2D because at this point we are dealing
  * with row/column coordinates, so rounding is acceptable. This method assumes
  * that the input points are in (x,y) where (x = col, y = row ) form.
  *
  *  @return array of points included within the region.
  */
  public abstract Point[] getSelectedPoints();
  
 /**
  * This method returns the selected points within a region. However, duplicate
  * points may exist in the list of points. getSelectedPoints() will call this
  * method and the getRegionUnion() method to eliminate duplicate points.
  *
  *  @return array of points included within the region.
  */
  protected abstract Point[] initializeSelectedPoints();
  
 /**
  * This method is used by the getRegionUnion() method to calculate the
  * selected points.
  *
  *  @return Bounds containing the region.
  */
  protected abstract CoordBounds getRegionBounds(); 

 /**
  * This method removes duplicate points selected by multiple regions.
  * Calling this method will combine all regions' selected points into
  * one list of points, where each point is unique.
  *
  *  @param  regions The list of regions to be unionized.
  *  @return A list of unique points for all of the regions.
  */
  public static Point[] getRegionUnion( Region[] regions )
  {
    // if no regions are passed in, return no points.
    if( regions.length == 0 )
      return new Point[0];
    CoordBounds regionbounds = regions[0].getRegionBounds();
    float rowmin = regionbounds.getX1();
    float rowmax = regionbounds.getX2();
    float colmin = regionbounds.getY1();
    float colmax = regionbounds.getY2();
    for( int i = 1; i < regions.length; i++ )
    {
      if( regions[i].getRegionBounds().getX1() < rowmin )
      {
        rowmin = regions[i].getRegionBounds().getX1();
      }
      if( regions[i].getRegionBounds().getX2() > rowmax )
      {
        rowmax = regions[i].getRegionBounds().getX2();
      }
      if( regions[i].getRegionBounds().getY1() < colmin )
      {
        colmin = regions[i].getRegionBounds().getY1();
      }
      if( regions[i].getRegionBounds().getY2() > colmax )
      {
        colmax = regions[i].getRegionBounds().getY2();
      }
    }
    // build table to keep track of selected points
    int rows = Math.round(rowmax - rowmin + 1f);
    int columns = Math.round(colmax - colmin + 1f);
    boolean[][] point_table = new boolean[rows][columns];
    //System.out.println("Row/Column: " + rows + "/" + columns );
    Vector points = new Vector();
    Point[] sel_pts;
    int irowmin = (int)rowmin;
    int icolmin = (int)colmin;
    // for each region, mark its selected points
    for( int i = 0; i < regions.length; i++ )
    {
      // use initializeSelectedPoints() since getSelectedPoints() may call
      // this method, causing an endless loop.
      sel_pts = regions[i].initializeSelectedPoints();
      for( int pt = 0; pt < sel_pts.length; pt++ )
      {
        if( !(point_table[sel_pts[pt].x-irowmin][sel_pts[pt].y-icolmin]) )
	{
          //System.out.println("Point: (" + sel_pts[pt].x + "," + 
	  //                                sel_pts[pt].y + ")");
	  points.add( new Point( sel_pts[pt] ) );
	  point_table[sel_pts[pt].x-irowmin][sel_pts[pt].y-icolmin] = true;
	}
      }
    }
    // put the vector of points into an array of points
    Point[] unionpoints = new Point[points.size()];
    for( int i = 0; i < points.size(); i++ )
      unionpoints[i] = (Point)points.elementAt(i);
    return unionpoints;
  }
}
