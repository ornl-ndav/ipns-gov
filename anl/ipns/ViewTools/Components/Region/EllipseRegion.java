/*
 * File: EllipseRegion.java
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
 *  Revision 1.8  2004/03/15 23:53:51  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.7  2004/03/12 02:09:06  rmikk
 *  Fixed package names
 *
 *  Revision 1.6  2004/02/14 03:34:56  millermi
 *  - selectedpoints no longer includes point found off the image.
 *  - added toString() method.
 *
 *  Revision 1.5  2004/01/07 06:44:53  millermi
 *  - Added static method getRegionUnion() which removes duplicate
 *    points from one or more selections.
 *  - Added protected methods initializeSelectedPoints() and
 *    getRegionBounds(). Each is needed by getRegionUnion()
 *    to calculate a unique set of points.
 *
 *  Revision 1.4  2003/12/20 07:04:48  millermi
 *  - Fixed copy/paste bug that caused an array out of bounds
 *    exception.
 *
 *  Revision 1.3  2003/12/20 05:42:25  millermi
 *  - Now corrects the topleft/bottomright defining points if
 *    they were scaled incorrectly by the image.
 *
 *  Revision 1.2  2003/12/18 22:38:00  millermi
 *  - Tweaked how x/yextent are calculated. Now must have a different
 *    of more than one to be reset.
 *
 *  Revision 1.1  2003/11/26 18:46:56  millermi
 *  - Renamed ElipseRegion.java to EllipseRegion.java
 *
 *  Revision 1.3  2003/11/26 01:57:44  millermi
 *  - Improved selection process.
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
package gov.anl.ipns.ViewTools.Components.Region;

import java.awt.Point;
import java.util.Vector;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;

/**
 * This class is a specific region designated by two points which bound the
 * ellipse. The EllipseRegion is used to pass points selected by a
 * circle region (in SelectionOverlay) from the view component
 * to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the ellipse are the top left and bottom right points
 * of the rectangle bounding the ellipse. An ellipse must be used instead of a
 * circle, because although the selection looks circular, unless aspect ratio
 * is preserved, it may not be.
 */ 
public class EllipseRegion extends Region
{
 /**
  * Constructor - uses Region's constructor to set the defining points.
  * The defining points are assumed to be in image values, where
  * the input points are in (x,y) where (x = col, y = row ) form.
  *
  *  @param  dp - defining points of an ellipse or circle.
  */ 
  public EllipseRegion( floatPoint2D[] dp )
  {
    super(dp);
  }
  
 /**
  * Get all of the points inside the elliptical region. 
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
    Point topleft = floorImagePoint(world_to_image.MapTo(definingpoints[0]));
    Point bottomright = floorImagePoint(
                               world_to_image.MapTo(definingpoints[1]) );
    Point center = floorImagePoint(world_to_image.MapTo(definingpoints[2]) );
    float xextent = (float)(center.x - topleft.x);
    float yextent = (float)(center.y - topleft.y); 
    Vector points = new Vector();  // dynamic array of points
    
    // if only one pixel is selected by the circle, for low resolution.
    if( xextent == 0 && yextent == 0 )
      points.add( center );
    // if one pixel in x, and more than one in y is selected
    else if( xextent == 0 )
    {
      for( int y = topleft.y; y <= bottomright.y; y++ ) 
      {
	// make sure point is on the image.
	CoordBounds imagebounds = world_to_image.getDestination();
	if( imagebounds.onXInterval((float)topleft.x) && 
	    imagebounds.onYInterval((float)y) )
	  points.add( new Point( topleft.x, y ) );
      }
    }
    // if one pixel in y, and more than one in x is selected
    else if( yextent == 0 )
    {
      for( int x = topleft.x; x <= bottomright.x; x++ )
      {
	// make sure point is on the image.
	CoordBounds imagebounds = world_to_image.getDestination();
	if( imagebounds.onXInterval((float)x) && 
	    imagebounds.onYInterval((float)topleft.y) )
	  points.add( new Point( x, topleft.y ) );
      }
    }
    // large region, more than one pixel in both x and y
    else
    {
      double dist = 0;
      float xdiff = 0;
      float ydiff = 0;
      // using formula for ellipse: (x-h)^2/a^2 + (y-k)^2/b^2 = 1
      // where x,y is point, (h,k) is center, and a,b are x/y extent (radius)
      for( int y = topleft.y; y <= bottomright.y; y++ )
      {
	for( int x = topleft.x; x <= bottomright.x; x++ )
	{
	  xdiff = 0;
          ydiff = 0;
          // x/y diff represent x-h/y-k respectively
	  xdiff = Math.abs( (float)(x - center.x) );
	  ydiff = Math.abs( (float)(y - center.y) );
          // Subtracting 1/(xextent*4) is to account for fractional pixels.
          // This will give a smoother, more accurate selected region.
	  dist = Math.pow((double)(xdiff - 1/(xextent*4)),2) / 
        	 Math.pow((double)xextent,2) + 
        	   Math.pow((double)(ydiff - 1/(yextent*4)),2) /
        	   Math.pow((double)yextent,2);
          //System.out.println("(" + x + "," + y + ")..." + dist ); 
          if( dist < 1 )
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
    return ("Region: Ellipse\n" +
            "Center: " + definingpoints[2] + "\n" +
	    "Top-left bound: " + definingpoints[0] + "\n" +
	    "Bottom-right bound: " + definingpoints[1] + "\n");
  }
   
 /**
  * This method returns the rectangle containing the ellipse.
  *
  *  @return The bounds of the EllipseRegion.
  */
  protected CoordBounds getRegionBounds()
  {
    return new CoordBounds( world_to_image.MapTo(definingpoints[0]).x,
                            world_to_image.MapTo(definingpoints[0]).y, 
                            world_to_image.MapTo(definingpoints[1]).x,
			    world_to_image.MapTo(definingpoints[1]).y );
  }
}
