/*
 * File: Region.java
 *
 * Copyright (C) 2003-2004, Mike Miller
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
 *  Revision 1.10  2004/05/20 17:02:26  millermi
 *  - Made method getRegionBounds() public so it may be used by
 *    outside classes.
 *
 *  Revision 1.9  2004/05/11 01:08:08  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.8  2004/03/24 03:05:31  millermi
 *  - convertFloatPoint() now converts image row/column values to
 *    world coord values corresponding to the center of the pixel.
 *
 *  Revision 1.7  2004/03/12 02:00:35  rmikk
 *  Fixed package Names
 *
 *  Revision 1.6  2004/02/15 21:42:01  millermi
 *  - Revised javadocs. Commented out world/image_coords_set
 *    variables since they are not being used.
 *
 *  Revision 1.5  2004/02/14 03:33:02  millermi
 *  - revised getRegionUnion()
 *  - added setImageBounds(), setWorldBounds(), convertFloatPoint(),
 *    equals(), and toString() methods
 *  - Introduced transformation for converting from world to image
 *  - getDefiningPoints() now takes a parameter to determine if
 *    world or image coords are desired.
 *  - added floorImagePoint() to round float image coordinates to
 *    the greatest integer row/column value less than the float.
 *
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
package gov.anl.ipns.ViewTools.Components.Region;

import java.awt.Point;
import java.util.Vector;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;

/**
 * This class is a base class for all regions in the Region package. A Region is
 * used to pass selected regions (selected using the Selection Overlay) from a
 * view component to the viewer. Given the defining points of a region,
 * subclasses of this class can return all of the points inside the selected
 * region. The defining points are saved in the world coordinate system, so
 * all subclasses must map the defining points to image values before using.
 * ALL CLASSES THAT USE THE REGION CLASS SHOULD CALL setWorldBounds() AND
 * setImageBounds() IMMEDIATELY AFTER CONSTRUCTING A REGION INSTANCE.
 */ 
public abstract class Region implements java.io.Serializable
{
 /**
  * "0" - This primative integer defines the coordinate system in
  * world coordinates.
  */
  public static final int WORLD = 0;
  
 /**
  * "1" - This primative integer defines the coordinate system in
  * image row/column coordinates.
  */
  public static final int IMAGE = 1;
  
  protected floatPoint2D[] definingpoints;  // saved in world coords.
  protected Point[] selectedpoints;
  protected CoordTransform world_to_image;
  //protected boolean world_coords_set = false;  // use these flags to tell if
  //protected boolean image_coords_set = false;  // coordtransform is complete.
  
 /**
  * Constructor - provides basic initialization for all subclasses
  *
  *  @param  dp - world coordinate defining points of the region.
  */ 
  protected Region( floatPoint2D[] dp )
  {
    definingpoints = dp;
    selectedpoints = new Point[0];
    world_to_image = new CoordTransform();
  }
  
 /**
  * Set the world coordinate system. This must be done immediately following
  * the construction of the Region since the selected area is dependent on
  * the image and world bounds.
  *
  *  @param  wb The world bounds.
  */
  public void setWorldBounds( CoordBounds wb )
  {
    world_to_image.setSource(wb);
    //world_coords_set = true;
  }
  
 /**
  * Set the image coordinate system. This must be done immediately following
  * the construction of the Region since the selected area is dependent on
  * the image and world bounds.
  *
  *  @param  ib The image bounds.
  */
  public void setImageBounds( CoordBounds ib )
  {
    world_to_image.setDestination(ib);
    //image_coords_set = true;
  }
  
 /**
  * This method converts a floatPoint2D from one coordinate system to the
  * coordinate system specified. It is assumed that the floatPoint2D lives
  * in either the image or world coordinate system. If image coordinates
  * are converted to world coords, the world coordinate values at the center
  * of the pixel are returned.
  *
  *  @param  fpt floatPoint2D that is going to be mapped to the other coord
  *              system.
  *  @param  to_coordsystem The coordinate system the point will be mapped TO.
  *  @return the mapped floatPoint2D.
  */
  public floatPoint2D convertFloatPoint( floatPoint2D fpt, int to_coordsystem )
  {
    // convert to world coords
    // Since image coords are whole numbers, map world coords to pixel centers.
    if( to_coordsystem == WORLD )
    {
      // the transform goes from upper left-hand corner
      floatPoint2D map_to_center = world_to_image.MapFrom(fpt);
      floatPoint2D wcpt1 = world_to_image.MapFrom(new floatPoint2D(0,0));
      floatPoint2D wcpt2 = world_to_image.MapFrom(new floatPoint2D(1,1));
      float x_offset = (wcpt2.x - wcpt1.x)/2f;
      float y_offset = (wcpt2.y - wcpt1.y)/2f;
      // Since offset takes care of increasing vs decreasing bounds, only
      // need to use one case.
      // This will adjust the bounds by half a pixel in world coordinates
      // so the coordinates are given at pixel centers.
      map_to_center.x += x_offset;
      map_to_center.y += y_offset;
      return map_to_center;
    }
    // convert to image coords
    if( to_coordsystem == IMAGE )
      return world_to_image.MapTo(fpt);
    // if invalid number
    return null;
  }
  
 /**
  * Get the world coordinate points used to define the geometric shape.
  *
  *  @param  coordsystem The coordinate system from which the defining
  *                      points are taken from, either WORLD or IMAGE.
  *  @return array of points that define the region.
  */
  public floatPoint2D[] getDefiningPoints( int coordsystem )
  {
    // world coords
    if( coordsystem == WORLD )
      return definingpoints;
    // image coords
    if( coordsystem == IMAGE )
    {
      // convert image def. pts to world def. pts.
      floatPoint2D[] imagedp = new floatPoint2D[definingpoints.length];
      for( int i = 0; i < definingpoints.length; i++ )
        imagedp[i] = world_to_image.MapTo(definingpoints[i]);
      return imagedp;
    }
    // if invalid number
    return null;
  }
  
 /**
  * Compare two regions. This will return true only if the two intances
  * are of the same class, have the same world_to_image transform, and have
  * identical defining points. Be aware that it is difficult to compare
  * float values reliably, so even identical regions will return false if
  * one of the values is off by a small amount. This will reliably return true
  * if the references of the two regions match. 
  *
  *  @param  reg2 The region to be compared.
  *  @return true if equal, false if not.
  */
  public boolean equals( Region reg2 )
  {
    // if references are the same, the region is the same.
    if( this == reg2 )
      return true;
    // check if instances of the same class
    if( !this.getClass().getName().equals(reg2.getClass().getName()) )
      return false;
    // make sure working on same coordinate systems
    if( !world_to_image.equals(reg2.world_to_image) )
      return false;
    // make sure there exist the same number of defining points
    floatPoint2D[] thisdp = this.getDefiningPoints(WORLD);
    floatPoint2D[] reg2dp = reg2.getDefiningPoints(WORLD);
    if( thisdp.length != reg2dp.length )
      return false;
    // check each individual element, be aware that float values are hard
    // to compare reliably.
    for( int i = 0; i < thisdp.length; i++ )
    {
      if( thisdp[i] != reg2dp[i] )
        return false;
    }
    // else, must be same region.
    return true;
  }
  
 /**
  * Get all of the image points inside the region. The use of
  * Point was chosen over floatPoint2D because at this point we are dealing
  * with row/column coordinates, so rounding is acceptable. This method assumes
  * that the input points are in (x,y) where (x = col, y = row ) form.
  *
  *  @return array of points included within the region.
  */
  public abstract Point[] getSelectedPoints();
  
 /**
  * Displays the Region type and its defining points.
  */
  public abstract String toString();
   
 /**
  * This method returns the selected points within a region. However, duplicate
  * points may exist in the list of points. getSelectedPoints() will call this
  * method and the getRegionUnion() method to eliminate duplicate points.
  *
  *  @return array of points included within the region.
  */
  protected abstract Point[] initializeSelectedPoints();
  
 /**
  * This method is defines a rectangular bounds that the region is contained in.
  *
  *  @return Rectangular bounds containing the region.
  */
  public abstract CoordBounds getRegionBounds();
  
 /**
  * Since image row/column values are integers, the mapping from world to
  * image coordinates must be converted from image float values to integers.
  * To display properly, the decimal portion of the float values must be
  * truncated instead of rounded. Off-by-one errors will occur if image decimal
  * is >= .5.
  *
  *  @param  imagept Float image row/column values.
  *  @return The corresponding integer image row/column values.
  */
  protected Point floorImagePoint( floatPoint2D imagept )
  {
    float x = (float)Math.floor((double)imagept.x);
    float y = (float)Math.floor((double)imagept.y);
    return new Point(Math.round(x), Math.round(y));
  }

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
    // this transform will map image bounds to an integer grid from
    // [0,#rows-1] x [0,#col-1]
    CoordTransform image_to_array = new CoordTransform();
    // if no regions are passed in, return no points.
    if( regions.length == 0 )
      return new Point[0];
    // region bounds are in image coordinates.
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
    // create a nice integer-like interval that will nicely map to
    // the array.
    rowmin = (float)Math.floor((double)rowmin);
    colmin = (float)Math.floor((double)colmin);
    rowmax = (float)Math.ceil((double)rowmax);
    colmax = (float)Math.ceil((double)colmax);
    // set image bounds
    image_to_array.setSource( new CoordBounds(rowmin,colmin,rowmax,colmax) );
    // build table to keep track of selected points
    int rows = Math.round(rowmax - rowmin) + 1;
    int columns = Math.round(colmax - colmin) + 1;
    // set array bounds
    image_to_array.setDestination( new CoordBounds(0,0,
                                                   (float)(rows-1),
						   (float)(columns-1) ) );
    boolean[][] point_table = new boolean[rows][columns];
    //System.out.println("Row/Column: " + rows + "/" + columns );
    Vector points = new Vector();
    Point[] sel_pts;
    Point temp = new Point();
    // for each region, mark its selected points
    for( int i = 0; i < regions.length; i++ )
    {
      // use initializeSelectedPoints() since getSelectedPoints() may call
      // this method, causing an endless loop.
      sel_pts = regions[i].initializeSelectedPoints();
      for( int pt = 0; pt < sel_pts.length; pt++ )
      { 
	// map image points to array points
	temp = image_to_array.MapTo(new floatPoint2D(sel_pts[pt])).toPoint();
        /*
	System.out.println(image_to_array);
	System.out.println("row min/max: " + rowmin + "/" + rowmax );
	System.out.println("col min/max: " + colmin + "/" + colmax );
	System.out.println("Point: " + temp );
	*/
	if( !(point_table[temp.x][temp.y]) )
	{
          //System.out.println("Point: (" + sel_pts[pt].x + "," + 
	  //                                sel_pts[pt].y + ")");
	  points.add( new Point( sel_pts[pt] ) );
	  point_table[temp.x][temp.y] = true;
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
