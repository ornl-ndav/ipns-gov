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
 * Class RegionOpList - Holds a list of RegionOp objects and 
 * contains methods to componds all the objects into one boolean[][]
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
  * @param pos  The position from which to remove the RegionOp
  */
  public void remove( int pos ){
    regionOpList.remove( pos );
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
  * Compounds the list of RegionOps into a boolean[][] of included values
  * @return
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

 
 private Point getSize( CoordTransform world_to_array ){
   int x = -1;
   int y = -1;
   Point[] points;
  
   // for every region, get the selected points.
   // for every point, if its x is max set to x
   // for every point, if its y is max set to y
   for ( RegionOp regionOp:regionOpList ){
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
  * Adds region to current selection
  * @param region region to add
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
  * Intersects region to current selection
  * @param region region to interect
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
  * Subtracts region to current selection
  * @param region region to subtract
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
  * Reverses current cummulative selection
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
