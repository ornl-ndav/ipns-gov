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
  *  @param  dp - defining points of a ring.
  */ 
  public AnnularRegion( floatPoint2D[] dp )
  {
    super(dp); 
    // outer ellipse
    float xextent = definingpoints[0].x - definingpoints[3].x;
    float yextent = definingpoints[0].y - definingpoints[3].y; 
    // since a mapping is done with the imagejpanel, the topleft or bottomright
    // could have been mapped to the side of the image. However, at most
    // one will be affected, so take the maximum extent of the two.
    // Correct the defining points if selection made near border of image.
    // Bottomright to center
    if( (definingpoints[4].x - definingpoints[0].x) > xextent )
    {
      xextent = definingpoints[4].x - definingpoints[0].x; 
      definingpoints[3].x = definingpoints[0].x - xextent;
    }
    else if( (definingpoints[4].x - definingpoints[0].x) < xextent )
    {
      definingpoints[4].x = definingpoints[0].x + xextent;
    }
    // topleft to center
    if( (definingpoints[4].y - definingpoints[0].y) > yextent )
    {
      yextent = definingpoints[4].y - definingpoints[0].y;
      definingpoints[3].y = definingpoints[0].y - yextent;
    }
    else if( (definingpoints[4].y - definingpoints[0].y) < yextent )
    {
      definingpoints[4].y = definingpoints[0].y + yextent;
    }
    
    // inner ellipse
    float inxextent = definingpoints[0].x - definingpoints[1].x;
    float inyextent = definingpoints[0].y - definingpoints[1].y; 
    // since a mapping is done with the imagejpanel, the topleft or bottomright
    // could have been mapped to the side of the image. However, at most
    // one will be affected, so take the maximum extent of the two.
    // Correct the defining points if selection made near border of image.
    // Bottomright to center
    if( (definingpoints[2].x - definingpoints[0].x) > inxextent )
    {
      inxextent = definingpoints[2].x - definingpoints[0].x; 
      definingpoints[1].x = definingpoints[0].x - inxextent;
    }
    else if( (definingpoints[2].x - definingpoints[0].x) < inxextent )
    {
      definingpoints[2].x = definingpoints[0].x + inxextent;
    }
    // topleft to center
    if( (definingpoints[2].y - definingpoints[0].y) > inyextent )
    {
      inyextent = definingpoints[2].y - definingpoints[0].y;
      definingpoints[1].y = definingpoints[0].y - inyextent;
    }
    else if( (definingpoints[2].y - definingpoints[0].y) < inyextent )
    {
      definingpoints[2].y = definingpoints[0].y + inyextent;
    }
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
    floatPoint2D center = new floatPoint2D( definingpoints[0] );
    // inner topleft
    floatPoint2D in_tl = new floatPoint2D( definingpoints[1] );
    // inner bottomright
    floatPoint2D in_br = new floatPoint2D( definingpoints[2] );
    // outer topleft
    floatPoint2D out_tl = new floatPoint2D( definingpoints[3] );
    // outer bottomright
    floatPoint2D out_br = new floatPoint2D( definingpoints[4] );
    
    float inxextent = center.x - in_tl.x;
    float inyextent = center.y - in_tl.y;
    float outxextent = center.x - out_tl.x;
    float outyextent = center.y - out_tl.y; 
    Vector points = new Vector(); // dynamic array of points
    
    // if only one pixel is selected by the circle, for low resolution.
    if( (outxextent == 0) && (outyextent == 0) )
    { 
      points.add( center.toPoint() );
    }
    // if one pixel in x, and more than one in y is selected
    else if( outxextent == 0 )
    {
      for( int y = (int)out_tl.y; y <= out_br.y; y++ ) 
      { 
 	// make sure point is not within the inner ring.
 	if( y < in_tl.y || y > in_br.y )
 	  points.add( new Point( Math.round(out_tl.x), y ) );
      }
    }
    // if one pixel in y, and more than one in x is selected
    else if( outyextent == 0 )
    {
      for( int x = (int)out_tl.x; x <= out_br.x; x++ )
      { 
 	// make sure point is not within the inner ring.
 	if( x < in_tl.x || x > in_br.x )
 	  points.add( new Point( x, Math.round(out_tl.y) ) );
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
      for( int y = (int)out_tl.y; y <= out_br.y; y++ )
      {
 	for( int x = (int)out_tl.x; x <= out_br.x; x++ )
 	{
 	  xdiff = 0;
          ydiff = 0;
          // x/y diff represent x-h/y-k respectively
 	  xdiff = Math.abs( (double)((float)x - center.x) );
 	  ydiff = Math.abs( (double)((float)y - center.y) );
          // Subtracting 1/(xextent*4) is to account for fractional pixels.
          // This will give a smoother, more accurate selected region.
 	  // outer distance.
          outdist = Math.pow((xdiff - 1/(outxextent*4)),2)/
        		   Math.pow(outxextent,2)
        		 + Math.pow((ydiff - 1/(outyextent*4)),2)/
        		   Math.pow(outyextent,2);
 	  // inner distance.
          indist = Math.pow((xdiff - 1/(inxextent*4)),2)/
        		   Math.pow(inxextent,2)
        		 + Math.pow((ydiff - 1/(inyextent*4)),2)/
        		   Math.pow(inyextent,2);
          //System.out.println("(" + x + "," + y + ")..." + dist ); 
          // make sure point is between both ellipses.
          if( outdist <= 1 && indist >= 1 )
            points.add( new Point( x, y ) ); 
 	} 
      }
    }
    selectedpoints = new Point[points.size()];
    for( int i = 0; i < points.size(); i++ )
      selectedpoints[i] = (Point)points.elementAt(i);
    return selectedpoints;
  } 
   
 /**
  * This method returns the rectangle containing the ring.
  *
  *  @return The bounds of the AnnularRegion.
  */
  protected CoordBounds getRegionBounds()
  {
    return new CoordBounds( definingpoints[3].x,
                            definingpoints[3].y, 
                            definingpoints[4].x,
			    definingpoints[4].y );
  }
}
