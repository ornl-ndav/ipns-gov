/*
 * File: AnnularRegion.java
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
 *  Revision 1.3  2004/02/14 03:34:56  millermi
 *  - selectedpoints no longer includes point found off the image.
 *  - added toString() method.
 *
 *  Revision 1.2  2004/01/07 06:44:53  millermi
 *  - Added static method getRegionUnion() which removes duplicate
 *    points from one or more selections.
 *  - Added protected methods initializeSelectedPoints() and
 *    getRegionBounds(). Each is needed by getRegionUnion()
 *    to calculate a unique set of points.
 *
 *  Revision 1.1  2003/12/30 00:01:10  millermi
 *  - Initial Version - This class allows users to select regions
 *    in the shape of a ring.
 *
 */ 
package DataSetTools.components.View.Region;

import java.awt.Point;
import java.util.Vector;

import DataSetTools.util.floatPoint2D;
import DataSetTools.components.image.CoordBounds;
import DataSetTools.components.View.Cursor.SelectionJPanel;

/**
 * The AnnularRegion is used to pass points selected between two
 * circle regions (a ring) (in SelectionOverlay) from the view component
 * to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the ring are:
 * p[0]   = center pt of circle
 * p[1]   = top left corner of bounding box of inner circle
 * p[2]   = bottom right corner of bounding box of inner circle
 * p[3]   = top left corner of bounding box of outer circle
 * p[4]   = bottom right corner of bounding box of outer circle
 */ 
public class AnnularRegion extends Region
{
 /**
  * Constructor - uses Region's constructor to set the defining points.
  * The defining points are assumed to be in image values, where
  * the input points are in (x,y) where (x = col, y = row ) form.
  *
  *  @param  dp - world coord defining points of a ring.
  */ 
  public AnnularRegion( floatPoint2D[] dp )
  {
    super(dp); 
  }
  
 /**
  * Get all of the points inside the annular (ring) region. 
  *
  *  @return array of points included within the elliptical region.
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
    // Convert definingpoints to image coords.
    Point center = floorImagePoint(
                             world_to_image.MapTo(definingpoints[0]));
    // inner topleft
    Point in_tl = floorImagePoint(
                             world_to_image.MapTo(definingpoints[1]));
    // inner bottomright
    Point in_br = floorImagePoint(
                             world_to_image.MapTo(definingpoints[2]));
    // outer topleft
    Point out_tl = floorImagePoint(
                             world_to_image.MapTo(definingpoints[3]));
    // outer bottomright
    Point out_br = floorImagePoint(
                             world_to_image.MapTo(definingpoints[4]));
    
    int inxextent = center.x - in_tl.x;
    int inyextent = center.y - in_tl.y;
    int outxextent = center.x - out_tl.x;
    int outyextent = center.y - out_tl.y; 
    Vector points = new Vector(); // dynamic array of points
    
    // if only one pixel is selected by the circle, for low resolution.
    if( (outxextent == 0) && (outyextent == 0) )
    { 
      points.add( center );
    }
    // if one pixel in x, and more than one in y is selected
    else if( outxextent == 0 )
    {
      for( int y = out_tl.y; y <= out_br.y; y++ ) 
      { 
 	// make sure point is not within the inner ring.
 	if( y < in_tl.y || y > in_br.y )
 	  points.add( new Point( out_tl.x, y ) );
      }
    }
    // if one pixel in y, and more than one in x is selected
    else if( outyextent == 0 )
    {
      for( int x = out_tl.x; x <= out_br.x; x++ )
      { 
 	// make sure point is not within the inner ring.
 	if( x < in_tl.x || x > in_br.x )
 	  points.add( new Point( x, out_tl.y ) );
      }
    }
    // large region, more than one pixel in both x and y
    else
    {
      double outdist = 0;
      double indist = 0;
      double xdiff = 0;
      double ydiff = 0;
      // using formula for ellipse: (x-h)^2/a^2 + (y-k)^2/b^2 = 1
      // where x,y is point, (h,k) is center, and a,b are x/y extent (radius)
      for( int y = out_tl.y; y <= out_br.y; y++ )
      {
 	for( int x = out_tl.x; x <= out_br.x; x++ )
 	{
 	  xdiff = 0;
          ydiff = 0;
          // x/y diff represent x-h/y-k respectively
 	  xdiff = Math.abs( (double)(x - center.x) );
 	  ydiff = Math.abs( (double)(y - center.y) );
          // Subtracting 1/(xextent*4) is to account for fractional pixels.
          // This will give a smoother, more accurate selected region.
 	  // outer distance.
          outdist = Math.pow((xdiff - 1/(double)(outxextent*4)),2)/
        		   Math.pow(outxextent,2)
        		 + Math.pow((ydiff - 1/(double)(outyextent*4)),2)/
        		   Math.pow(outyextent,2);
 	  // inner distance.
          indist = Math.pow((xdiff - 1/(double)(inxextent*4)),2)/
        		   Math.pow(inxextent,2)
        		 + Math.pow((ydiff - 1/(double)(inyextent*4)),2)/
        		   Math.pow(inyextent,2);
          //System.out.println("(" + x + "," + y + ")..." + dist ); 
          // make sure point is between both ellipses.
          if( outdist < 1 && indist >= 1 )
	  {
	    // make sure point is on the image.
	    CoordBounds imagebounds = world_to_image.getDestination();
	    if( imagebounds.onXInterval((float)x) && 
	        imagebounds.onYInterval((float)y) )
              points.add( new Point( x, y ) );
	  } 
 	} 
      }
    }
    selectedpoints = new Point[points.size()];
    for( int i = 0; i < points.size(); i++ )
      selectedpoints[i] = (Point)points.elementAt(i);
    return selectedpoints;
  } 
  
 /**
  * Display the region type with its defining points.
  *
  *  @return region type and defining points.
  */
  public String toString()
  {
    return ("Region: Annular\n" +
            "Center: " + definingpoints[0] + "\n" +
	    "Top-left bound(inner): " + definingpoints[1] + "\n" +
	    "Bottom-right bound(inner): " + definingpoints[2] + "\n" +
	    "Top-left bound(outer): " + definingpoints[3] + "\n" +
	    "Bottom-right bound(outer): " + definingpoints[4] + "\n");
  }
   
 /**
  * This method returns the image bounds for the ring.
  *
  *  @return The bounds of the AnnularRegion.
  */
  protected CoordBounds getRegionBounds()
  {
    return new CoordBounds( world_to_image.MapTo(definingpoints[3]).x,
                            world_to_image.MapTo(definingpoints[3]).y, 
                            world_to_image.MapTo(definingpoints[4]).x,
			    world_to_image.MapTo(definingpoints[4]).y );
  }
}
