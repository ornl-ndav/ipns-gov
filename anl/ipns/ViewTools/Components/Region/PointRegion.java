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

/**
 * This class passes one or more selected points. Most of the functionality
 * provided by the base class works for the case of a point region. Thus,
 * only the constructor is needed.
 */ 
public class PointRegion extends Region
{
 /**
  * Constructor takes in an array of Points, each defining a point region.
  *
  *  @param  dp - defining point regions
  */ 
  public PointRegion( floatPoint2D[] dp )
  {
    super(dp);
    
    // Give the image and world bounds meaningful values.
    float xmin = definingpoints[0].x;
    float xmax = definingpoints[0].x;
    float ymin = definingpoints[0].y;
    float ymax = definingpoints[0].y;
    for( int i = 1; i < definingpoints.length; i++ )
    {
      if( definingpoints[i].x < xmin )
        xmin = definingpoints[i].x;
      if( definingpoints[i].x > xmax )
        xmax = definingpoints[i].x;
      if( definingpoints[i].y < ymin )
        ymin = definingpoints[i].y;
      if( definingpoints[i].y > ymax )
        ymax = definingpoints[i].y;
    }
    setWorldBounds( new CoordBounds( xmin, ymin, xmax, ymax ) );
    setImageBounds( new CoordBounds( xmin, ymin, xmax, ymax ) );
  }
  
 /**
  * The PointRegion returns the defining points as an array of Points.
  *
  *  @return array of points.
  */
  public Point[] getSelectedPoints()
  { 
    initializeSelectedPoints();
    Region[] points = {this};
    selectedpoints = getRegionUnion( points );
    return selectedpoints;
  }
  
 /**
  * This method is here to factor out the setting of the selected points.
  * By doing this, regions can make use of the getRegionUnion() method.
  *
  *  @return array of points included within the region.
  */
  protected Point[] initializeSelectedPoints()
  {
    Vector pts = new Vector();
    CoordBounds imagebounds = world_to_image.getDestination();
    Point temp;
    // Convert defining points from world coords to image coords and
    // add it to the array if the point is on the image.
    for( int i = 0; i < definingpoints.length; i++ )
    {
      temp = floorImagePoint(world_to_image.MapTo(definingpoints[i]));
      if( imagebounds.onXInterval(temp.x) && imagebounds.onYInterval(temp.y) )
        pts.add(temp);
    }
    // construct static list of points.
    selectedpoints = new Point[pts.size()];
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
   
 /**
  * This method returns the extent of the Points selected.
  *
  *  @return The bounds of the PointRegion.
  */
  public CoordBounds getRegionBounds()
  {
    float xmin = world_to_image.MapTo(definingpoints[0]).x;
    float xmax = world_to_image.MapTo(definingpoints[0]).x;
    float ymin = world_to_image.MapTo(definingpoints[0]).y;
    float ymax = world_to_image.MapTo(definingpoints[0]).y;
    for( int i = 1; i < definingpoints.length; i++ )
    {
      if( world_to_image.MapTo(definingpoints[i]).x < xmin )
        xmin = world_to_image.MapTo(definingpoints[i]).x;
      if( world_to_image.MapTo(definingpoints[i]).x > xmax )
        xmax = world_to_image.MapTo(definingpoints[i]).x;
      if( world_to_image.MapTo(definingpoints[i]).y < ymin )
        ymin = world_to_image.MapTo(definingpoints[i]).y;
      if( world_to_image.MapTo(definingpoints[i]).y > ymax )
        ymax = world_to_image.MapTo(definingpoints[i]).y;
    }
    return new CoordBounds( xmin, ymin, xmax, ymax );
  }
}
