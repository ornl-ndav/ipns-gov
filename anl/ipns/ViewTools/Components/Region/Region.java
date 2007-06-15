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
 *  Revision 1.18  2007/06/15 22:33:32  oakgrovej
 *  Added getRegionInstance methods
 *
 *  Revision 1.17  2007/05/10 20:54:41  dennis
 *  Added method ClampPointsToArray() that is used to restrict the selected
 *  points returned by regions such as LineRegion and PointRegion, that are
 *  not derived from the RegionWithInterior class.
 *
 *  Revision 1.16  2007/04/28 19:46:40  dennis
 *  The getRegionBounds() method now clamps the lower bounds to be
 *  at least zero, and the upperbounds to be no more than maximum
 *  row or column number.
 *  Expanded java docs.
 *
 *  Revision 1.15  2007/04/11 21:52:33  dennis
 *  Simplified getRegionUnion() method by using the RegionOpList class.
 *
 *  Revision 1.14  2007/03/16 16:38:12  dennis
 *  Major refactoring of Region concept.  Regions are defined in world
 *  coordinates.  Any information about the mapping from world coordinates
 *  to array (col,row) coordinates is no longer kept by the regions.
 *  The world_to_array transformation is passed in as a parameter to
 *  the methods that require it.  This avoids any problems with keeping
 *  the mapping up to date.  Also, a region can be easily used with different
 *  mappings, say from world to an array of data or from world to pixels
 *  on a display, just by providing the correct mapping when calling the
 *  methods.  All methods for getting and setting mapping information were
 *  removed.  Also, methods specific to regions with an interior (such as box)
 *  were placed in a subclass RegionsWithInterior.
 *
 *  Revision 1.13  2007/03/11 04:37:16  dennis
 *  Added methods to setWorldToArrayTran() and getWorldToArrayTran().
 *
 *  Revision 1.12  2005/01/18 22:59:47  millermi
 *  - Added getWorldBounds() and getImageBounds()
 *
 *  Revision 1.11  2004/07/02 16:40:52  millermi
 *  - Added comments and println message to getRegionUnion() notifying
 *    them that TableRegions may not work with this method.
 *
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

import javax.swing.JPanel;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.*;
import gov.anl.ipns.ViewTools.Components.Transparency.SelectionOverlay;
import gov.anl.ipns.ViewTools.Panels.Cursors.*;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;

/**
 * This class is a base class for all regions in the Region package. A Region is
 * used to pass selected regions (selected using the Selection Overlay) from a
 * view component to the viewer. Given the defining points of a region,
 * subclasses of this class can return all of the points inside the selected
 * region. The defining points are saved in the world coordinate system.
 * Classes that use regions are responsible for constructing and maintaining
 * a mapping from world coordinates to array or pixel coordinates, if they
 * such a discrete coordinate system.
 */ 
public abstract class Region implements java.io.Serializable
{
  protected floatPoint2D[] definingpoints;  // saved in world coords.
  
 /**
  * Constructor - provides basic initialization for all subclasses
  *
  *  @param  dp - world coordinate defining points of the region.
  */ 
  protected Region( floatPoint2D[] dp )
  {
    definingpoints = dp;
  }


 /**
  * Get the world coordinate points used to define the geometric shape.
  *
  * @return a reference to the array of points that define the region.
  */
  public floatPoint2D[] getDefiningPoints()
  {
    return definingpoints;
  }

  
 /**
  * Compare two regions. This will return true only if the two intances
  * are of the same class and have identical defining points. 
  * Be aware that it is difficult to compare
  * float values reliably, so even identical regions will return false if
  * one of the values is off by a small amount. This will reliably return true
  * if the references of the two regions match. 
  *
  *  @param  reg2 The region to be compared.
  *  @return true if equal, false if not.
  */
  public boolean equals1( Region reg2 )
  {
    // if references are the same, the region is the same.
    if( this == reg2 )
      return true;

    // check if instances of the same class
    if( !this.getClass().getName().equals(reg2.getClass().getName()) )
      return false;

    // make sure there exist the same number of defining points
    floatPoint2D[] thisdp = this.definingpoints;
    floatPoint2D[] reg2dp = reg2.definingpoints;
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
  *  Get a bounding box for the region, in World Coordinates.  The
  *  points of the region will lie in the X-interval [X1,X2] and
  *  in the Y-interval [Y1,Y2], where X1,X2,Y1 and Y2 are the values
  *  returned by the CoordBounds.getX1(), getX2(), getY1(), getY2()
  *  methods.
  * 
  *  @return a CoordBounds object containing the full extent of this
  *          region.
  */
 abstract public CoordBounds getRegionBoundsWC();

  
 /**
  * Get the discrete points that lie within this region, based on the
  * specified mapping from world to array (col,row) coordinates.
  *
  * @param world_to_array  The transformation from world coordinates to
  *                        array coordinates.  NOTE: The destination bounds
  *                        for this mapping MUST correspond to the array
  *                        size.  The destination CoordBounds object is used
  *                        to get the array size!!!
  * 
  *  @return array of points included within the region.
  */
  abstract public Point[] getSelectedPoints( CoordTransform world_to_array );
  

 /**
  * Displays the Region type and its defining points.
  */
  abstract public String toString();


 /**
  *  This method gets a rectangular bounding box in array (col,row) coordinates
  *  that contains the region, based on the bounds returned by the
  *  getRegionBoundsWC() method.  The Math.floor and Math.ceil functions are
  *  applied to the results of mapping the world coordinate bounds to array
  *  coordinates, to guarantee that the integer bounds produced contain
  *  the region.
  *
  *  @param world_to_array  The transformation from world coordinates to 
  *                         array coordinates.  NOTE: The destination bounds
  *                         for this mapping MUST correspond to the array
  *                         size.  The destination CoordBounds object is used
  *                         to get the array size!!!
  *
  *  @return Rectangular bounds containing the region.
  */
  public CoordBounds getRegionBounds( CoordTransform world_to_array )
  {
    CoordBounds bounds = getRegionBoundsWC();

    floatPoint2D min_point = new floatPoint2D( bounds.getX1(), bounds.getY1() );
    floatPoint2D max_point = new floatPoint2D( bounds.getX2(), bounds.getY2() );

    min_point = world_to_array.MapTo( min_point );
    max_point = world_to_array.MapTo( max_point );

                                      // Switch the "min" and "max"
                                      // incase the ordering is reversed in
                                      // the bounds (eg. "upside down" coords
                                      // on the screen.)
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
                                       // get lower and upper bounds as 
                                       // integer values
    min_point.x = (float)Math.floor( min_point.x );
    min_point.y = (float)Math.floor( min_point.y );

    max_point.x = (float)Math.ceil( max_point.x );
    max_point.y = (float)Math.ceil( max_point.y );

                                       // "clamp" the computed bounds to lie
                                       // inside the array
    CoordBounds array_size = world_to_array.getDestination();
    int n_cols = Math.round(Math.max( array_size.getX1(), array_size.getX2()));
    int n_rows = Math.round(Math.max( array_size.getY1(), array_size.getY2()));

    max_point.x = Math.min( max_point.x, n_cols - 1 );
    min_point.x = Math.max( min_point.x, 0 );

    max_point.y = Math.min( max_point.y, n_rows - 1 );
    min_point.y = Math.max( min_point.y, 0 );

    return new CoordBounds( min_point.x, min_point.y,
                            max_point.x, max_point.y );
  }


 /**
  *  This method clamps the specified list of Points to lie in the bounding
  *  box (in array coordinates) for this Region.  The bounding box is the 
  *  intersection of the bounds returned by the getRegionBounds() method and
  *  the destination region of the world_to_array transformation.  
  *
  *  @param world_to_array  The transformation from world coordinates to 
  *                         array coordinates.  NOTE: The destination bounds
  *                         for this mapping MUST correspond to the array
  *                         size.  The destination CoordBounds object is used
  *                         to get the array size!!!
  *
  *  @param points          The list of Points in array coordinates that are
  *                         to be restricted to lie in the data array.
  *
  *  @return a new list of Points, containing only those points in the
  *          original list, that lie in the data array.
  */
  public Point[] ClampPointsToArray( CoordTransform world_to_array,
                                     Point[]        points )
  {

                                 // To deal with the possibility that the
                                 // line extended of the edge of the array,
                                 // we only keep points that are inside
                                 // the array.
    CoordBounds bounds = getRegionBounds( world_to_array );

    int min_x = (int)bounds.getX1();
    int max_x = (int)bounds.getX2();

    int min_y = (int)bounds.getY1();
    int max_y = (int)bounds.getY2();

    Vector bounded_points = new Vector( points.length );
    int row,
        col;
    for ( int i = 0; i < points.length; i++ )
    {
      row = points[i].y;
      col = points[i].x;
      if ( row >= min_y && row <= max_y && col >= min_x && col <= max_x )
        bounded_points.add( points[i] );
    }

    points = new Point[ bounded_points.size() ];
    for ( int i = 0; i < points.length; i++ )
      points[i] = (Point)bounded_points.elementAt(i);

    return points;
  }

  
 /**
  * Since image row/column values are integers, the mapping from world to
  * image coordinates must be converted from image float values to integers.
  * Any float coordinates with column number in the half-open interval
  * [col,col+1) and row number in [row,row+1) will be mapped to the point
  * with array coordinates [col,row] by this method. 
  *
  *  @param  imagept   Floating point (possibly fractions) (col,row)
  *                    coordinates.
  *  @return           The corresponding integer image row/column values.
  */
  public Point floorImagePoint( floatPoint2D imagept )
  {
    int x = (int)Math.floor(imagept.x);
    int y = (int)Math.floor(imagept.y);
    return new Point( x, y );
  }


 /**
  * This method removes duplicate points selected by multiple regions.
  * Calling this method will combine all regions' selected points into
  * one list of points, where each point is unique.
  *
  *  @param regions         The list of regions to be unionized.
  *
  *  @param world_to_array  The transformation from world coordinates to
  *                         array coordinates.  NOTE: The destination bounds
  *                         for this mapping MUST correspond to the array
  *                         size.  The destination CoordBounds object is used
  *                         to get the array size!!!
  *
  *  @return A list of unique points for all of the regions.
  */
  public static Point[] getRegionUnion( Region[]       regions, 
                                        CoordTransform world_to_array  )
  {
     RegionOpList op_list = new RegionOpList();
     for ( int i = 0; i < regions.length; i++ )
       op_list.add( new RegionOp( regions[i], RegionOp.Operation.UNION ) );

     return op_list.getSelectedPoints( world_to_array );
  }
  
  public static Region getInstanceRegion(XOR_Cursor3pt cursor)
  {
    return null;
  }
  
  public static Region getInstanceRegion(XOR_Cursor cursor,CoordTransform trans)
  {
    Region newRegion;
    if (cursor instanceof EllipseCursor)
    {
      //use transformation to change points of the cursor
      Ellipse ellipseRegion = ((EllipseCursor)cursor).region();
      Point p1 =ellipseRegion.getDrawPoint();
      Point center = ellipseRegion.getCenter();
      Point p2 =new Point(ellipseRegion.getDx()+center.x,
                          ellipseRegion.getDy()+center.y);
      floatPoint2D[] points = new floatPoint2D[3];
      if (trans!=null)
      {
        //System.out.println("Mapping trans");
        trans.MapTo(new floatPoint2D(p1));
        points[1] = trans.MapTo(new floatPoint2D(p1));
        points[2] = trans.MapTo(new floatPoint2D(p2));
      }
      else
      {
        //System.out.println("setting points\n"+p1+"\n"+p2);
        points[1] = new floatPoint2D(p1);
        points[2] = new floatPoint2D(p2);
      }
      //create a region with these new world pts
      newRegion = new EllipseRegion(points);
    }
    else
    {
      newRegion = null;
      //System.out.println("null region");
    }
    return newRegion;
  }
  
  public static Region getInstanceRegion(XOR_PanCursor cursor,
                                         CoordTransform trans)
  {
    Region newRegion;
    if (cursor instanceof BoxPanCursor)
    {
      //use transformation to change points of the cursor
      Point p1 =((BoxPanCursor)cursor).getP1();
      Point p2 =((BoxPanCursor)cursor).getP2();
      floatPoint2D[] points = new floatPoint2D[2];
      if (trans!=null)
      {
        //System.out.println("Mapping trans");
        trans.MapTo(new floatPoint2D(p1));
        points[0] = trans.MapTo(new floatPoint2D(p1));
        points[1] = trans.MapTo(new floatPoint2D(p2));
      }
      else
      {
        //System.out.println("setting points\n"+p1+"\n"+p2);
        points[0] = new floatPoint2D(p1);
        points[1] = new floatPoint2D(p2);
      }
      //create a region with these new world pts
      newRegion = new BoxRegion(points);
    }
    else
    {
      newRegion = null;
      //System.out.println("null region");
    }
    return newRegion;
  }
  
  public static Region getInstanceRegion(CursorTag cursor,floatPoint2D[] points)
  {
    if( cursor instanceof XOR_Cursor3pt)
      return getInstanceRegion((XOR_Cursor3pt)cursor,points);
    else if(cursor instanceof XOR_Cursor)
      return getInstanceRegion((XOR_Cursor)cursor,points);
    else if(cursor instanceof BoxPanCursor)
    {
      return getInstanceRegion((XOR_PanCursor)cursor,points);
    }
    else
      return null;
  }
  
  private static Region getInstanceRegion(XOR_Cursor3pt cursor,
                                         floatPoint2D[] points)
  {
    Region newRegion = null;
        
    if(cursor instanceof WedgeCursor)
    {
      JPanel drawPanel = cursor.getPanel();
      if( drawPanel instanceof SelectionOverlay)
      {
        Point[] regPoints = ((WedgeCursor)cursor).region();
        floatPoint2D[] tempwcp = new floatPoint2D[6];
        for (int i = 0; i < regPoints.length - 1; i++) 
        {
          //System.out.println(regPoints[i]+"");
          tempwcp[i] = ((SelectionOverlay) drawPanel).convertToWorldPoint(regPoints[i]);
        }
        tempwcp[0]=points[0];
        //System.out.println(points[0]+"");
        if (regPoints.length > 0) 
        {
          tempwcp[regPoints.length - 1] = new floatPoint2D(
              (float) regPoints[regPoints.length - 1].x,
              (float) regPoints[regPoints.length - 1].y);
        }
        if(cursor instanceof DoubleWedgeCursor)
          newRegion = new DoubleWedgeRegion(tempwcp);
        else
          newRegion = new WedgeRegion(tempwcp);
        
      }
      
    }
    else if(cursor instanceof AnnularCursor)
    {
      floatPoint2D[] regPoints = new floatPoint2D[5];
      regPoints[0]=points[0];
      //top left inner circle
      float x = Math.abs( points[0].x - points[1].x );
      float y = Math.abs( points[0].y - points[1].y );
      float r = (float)Math.sqrt( Math.pow(x,2) + Math.pow(y,2) );
      regPoints[1]= new floatPoint2D(points[0].x-r,points[0].y+r);
      //bottom right inner
      regPoints[2]=new floatPoint2D(points[0].x+r,points[0].y-r);
      
//    top left outer circle
      x = Math.abs( points[0].x - points[2].x );
      y = Math.abs( points[0].y - points[2].y );
      r = (float)Math.sqrt( Math.pow(x,2) + Math.pow(y,2) );
      regPoints[3]= new floatPoint2D(points[0].x-r,points[0].y+r);
      //bottom right outer
      regPoints[4]=new floatPoint2D(points[0].x+r,points[0].y-r);
      
      
      newRegion = new AnnularRegion(regPoints);
    }
    return newRegion;
  }
  
  private static Region getInstanceRegion(XOR_Cursor cursor,
                                         floatPoint2D[] points)
  {
    Region newRegion = null;
    //System.out.println("making XOR_Cursor region");
    if (cursor instanceof EllipseCursor)
    {
      //System.out.println("making Ellipse region");
      newRegion = new EllipseRegion(points);
    }
    
    else if(cursor instanceof PointCursor)
    {
      //System.out.println("making Point region");
      points[((PointCursor)cursor).region().x] = points[points.length-1];
      floatPoint2D[] newPoints = new floatPoint2D[points.length-1];
      for (int i=0;i<newPoints.length;i++)
        newPoints[i] = points[i];
      newRegion = new PointRegion(newPoints);
    }
    
    else if(cursor instanceof LineCursor)
    {
      //System.out.println("making line Region");
      newRegion = new LineRegion(points);
    }
    return newRegion;
  }
  
  private static Region getInstanceRegion(XOR_PanCursor cursor,
                                         floatPoint2D[] points)
  {
    Region newRegion = new BoxRegion(points);
    return newRegion;
  }
}
