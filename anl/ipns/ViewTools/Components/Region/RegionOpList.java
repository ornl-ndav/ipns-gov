/* 
 * File: RegionOpList.java
 *  
 * Copyright (C) 2007  Johnathan Morck, Chad Diller
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797.
 *
 * Modified:
 * $Log$
 * Revision 1.3  2007/04/28 03:34:52  dennis
 * Added method removeAll() to clear the list of RegionOps.
 * Null regions are now skipped when the maximum size is found.
 *
 * Revision 1.2  2007/04/27 03:52:27  dennis
 * Cleaned up and expanded the javadocs.
 *
 * Revision 1.1  2007/04/11 21:51:10  dennis
 * New class for dealing with unions, intersections and complements
 * of selected regions.
 *
 */

package gov.anl.ipns.ViewTools.Components.Region;

import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;
import java.util.Vector;
import java.awt.Point;

/**
 * This class Holds a list of RegionOp objects and contains methods 
 * to carry out the specified operations on the specified regions.
 * The regions are specified in a "world coordinate systme", such
 * as the size in cenimeters of an area detector.  The range of 
 * world coordinates is assumed to correspond to an underlying two
 * dimensional data array, such as the array of data values from a
 * pixelated detector.  The method getSelectedPoints will return
 * the list of (col,row) pairs corresponding to the selected elements
 * in the underlying data array.  The selected points are the
 * result of applying the operations and corresponding operations
 * in the order that they appear in the list.  For example, the 
 * first pair is typically a region with the operation "UNION". This 
 * will set the selected points to the array elemets corresponding
 * to the specified region.  If a second pair is the operation 
 * INTERSECT and another region, when this pair is processed, the
 * selected points will be set to the array elements that are in
 * the intersection of the previously selected points AND the 
 * points in the newly specified region.
 */
public class RegionOpList {

   private Vector<RegionOp> regionOpList = new Vector<RegionOp>();
   private boolean[][]    regionMask;

 
 /**
  * Adds a RegionOp to the end of the list
  *
  * @param regionOp The RegionOp object to add to the list.
  */
  public void add( RegionOp regionOp ){
    regionOpList.add( regionOp );
  }

 
 /**
  * Removes the RegionOp at the specified position from the list
  *
  * @param pos  The position from which to remove the RegionOp
  */
  public void remove( int pos ){
    regionOpList.remove( pos );
  }


 /**
  * Remove all region ops from the list
  */
  public void removeAll(){
    regionOpList.clear();
  }

 
 /**
  * Get a refereence to the whole RegionOpList
  *
  * @return a reference to the Vector of RegionOp objects.
  */
  public Vector<RegionOp> getList(){
    return regionOpList;
  }

 
 /**
  * Get the list of array coordinates in this compound selection.
  * This RegionOpList contains a (possibly) empty list of selected
  * region and operation pairs.  This method goes through the list
  * of region and operation pairs, and accumulates the net result
  * of the sequence of operations applied using the specified 
  * regions.  
  *
  * @param world_to_array The transformation between floating point 
  *                       "world coordinates" and the column & row
  *                       numbers of the underlying data array. 
  *
  * @return An array of points containing the (col,row) coordinates
  *         of each selected underlying data array element.
  */
  public Point[] getSelectedPoints( CoordTransform world_to_array ){
    RegionOp.Operation op;
    Point size = getSize( world_to_array );
    regionMask = new boolean[size.x][size.y];
  
    for ( RegionOp regionOp:regionOpList ){
      op = regionOp.getOp();
   
      if ( op == RegionOp.Operation.UNION ){
        doUnion( regionOp.getRegion(), world_to_array );
      }
      else if ( op == RegionOp.Operation.INTERSECT ){
        doIntersect( regionOp.getRegion(), world_to_array );
      }
      else if ( op == RegionOp.Operation.INTERSECT_COMPLEMENT ){
        doIntersectComplement( regionOp.getRegion(), world_to_array );
      }
      else if ( op == RegionOp.Operation.COMPLEMENT ){
        doComplement();
      }
    }
  
    //get the number of points selected in the region
    int numPoints = 0;
    for ( int x = 0; x < regionMask.length; x++ ){
      for ( int y = 0; y < regionMask[x].length; y++){
        if ( regionMask[x][y] ){
          numPoints++;
        }
      }
    }
  
    //create Point[] of size numPoints and fill
    Point points[] = new Point[numPoints];
    int pointsIndex = 0;

    if ( numPoints > 0 )
      for ( int y = 0; y < regionMask[0].length; y++ ){
        for ( int x = 0; x < regionMask.length; x++ ){
          if ( regionMask[x][y] ){
            points[pointsIndex] = new Point(x,y);
            pointsIndex++;
          }
        }
      }
  
    //explicitly set to null for gc
    op = null;
    size = null;
    regionMask = null;
  
    return points;
 }

 
 /**
  *  Scan across all of the specified regions to obtain the maximum 
  *  row and column indices used.
  */
 private Point getSize( CoordTransform world_to_array ){
   int x = -1;
   int y = -1;
   Point[] points;
  
   // for every region, get the selected points.
   // for every point, if its x is max set to x
   // for every point, if its y is max set to y
   for ( RegionOp regionOp:regionOpList ){
     if ( regionOp.getRegion() != null ){
       points = regionOp.getRegion().getSelectedPoints( world_to_array );
       for ( Point p:points ){
         if ( p.x > x ){
           x = p.x;
         }
         if ( p.y > y ){
           y = p.y;
         }
       }
     }
   }
   return new Point( x+1, y+1 );    // required size is one more than the
                                    // largest index.
 }

 
 private boolean[][] getThisRegionMask( Region         region, 
                                        CoordTransform world_to_array )
 {
   Point points[] = region.getSelectedPoints( world_to_array );
   boolean[][] mask = new boolean[ regionMask.length ][ regionMask[0].length ];
   for ( Point p:points )
     mask[p.x][p.y] = true; 
   return mask;
 }

 
 /**
  * Union the specified region with the current cummulative selection.
  *
  * @param region         Region to union with the
  *                       current cummulative selection.
  * 
  * @param world_to_array The tranformation from the world coordinates
  *                       in which the regions are specified, to the
  *                       (col,row) coordinates of the underlying data array.
  */
 private void doUnion( Region region, CoordTransform world_to_array ){
   Point points[] = region.getSelectedPoints( world_to_array );

   //if the point in the mask is in the selected points,
   //set it to true. otherwise leave it alone.
   for ( Point p:points ){
     regionMask[p.x][p.y] = true;
   }
 }

 
 /**
  * Intersects the specified region with the current cumulative selection.
  *
  * @param region         Region to be intersected with the
  *                       current cummulative selection.
  * 
  * @param world_to_array The tranformation from the world coordinates
  *                       in which the regions are specified, to the
  *                       (col,row) coordinates of the underlying data array.
  */
 private void doIntersect( Region region, CoordTransform world_to_array ){
   boolean[][] mask = getThisRegionMask( region, world_to_array );
   for ( int x = 0; x < regionMask.length; x++ ){
     for ( int y = 0; y < regionMask[x].length; y++ ){
       regionMask[x][y] = regionMask[x][y] && mask[x][y];
     }
   }
 }

 
 /**
  * Intersects the complement of the specified region with the current 
  * cummulative selection.
  *
  * @param region         Region whose complement is intersected with the
  *                       current cummulative selection.
  * 
  * @param world_to_array The tranformation from the world coordinates
  *                       in which the regions are specified, to the
  *                       (col,row) coordinates of the underlying data array.
  */
 private void doIntersectComplement( Region         region, 
                                     CoordTransform world_to_array ){
   boolean[][] mask = getThisRegionMask( region, world_to_array );
   for ( int x = 0; x < regionMask.length; x++ ){
     for ( int y = 0; y < regionMask[x].length; y++ ){
       regionMask[x][y] = regionMask[x][y] && !mask[x][y];
     }
   }
 }
 

 /**
  * Complement the current cummulative selection
  */
 private void doComplement(){
   //If Point is true in mask set it to false
   for ( int x = 0; x < regionMask.length; x++ ){ 
     for( int y = 0; y < regionMask[x].length; y++ ){ 
       regionMask[x][y] = !regionMask[x][y]; 
     } 
   }
 }
 

}
