/*
 * File: WedgeRegion.java
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
 *  Revision 1.13  2004/02/17 01:57:52  millermi
 *  - Removed LineRegions which selected points on the boundry of the
 *    wedge. This fixes a bug that causes an index out of bounds
 *    exception found by Alok.
 *
 *  Revision 1.12  2004/02/14 03:34:57  millermi
 *  - selectedpoints no longer includes point found off the image.
 *  - added toString() method.
 *
 *  Revision 1.11  2004/01/07 06:44:53  millermi
 *  - Added static method getRegionUnion() which removes duplicate
 *    points from one or more selections.
 *  - Added protected methods initializeSelectedPoints() and
 *    getRegionBounds(). Each is needed by getRegionUnion()
 *    to calculate a unique set of points.
 *
 *  Revision 1.10  2003/12/23 18:41:53  millermi
 *  - Added adjustments to p1 and rp1 so the LineRegion is
 *    closer to the rest of the wedge.
 *  - Improved selection capability.
 *
 *  Revision 1.9  2003/12/20 05:42:25  millermi
 *  - Now corrects the topleft/bottomright defining points if
 *    they were scaled incorrectly by the image.
 *
 *  Revision 1.8  2003/12/20 04:15:59  millermi
 *  - Included an off-by-one on the pointangle so that
 *    border points are always included. This eleviates
 *    "holes" in the wedge.
 *
 *  Revision 1.7  2003/12/18 22:38:00  millermi
 *  - Tweaked how x/yextent are calculated. Now must have a different
 *    of more than one to be reset.
 *
 *  Revision 1.6  2003/12/16 01:42:24  millermi
 *  - made pointchecker protected variable so it could be
 *    used by the DoubleWedgeCursor.
 *
 *  Revision 1.5  2003/12/13 01:16:03  millermi
 *  - Lines bounding wedge now use the LineRegion class
 *    to find all points on the boundary consistently.
 *  - Fixed bug that distorted the wedge when near the
 *    border of the image.
 *
 *  Revision 1.4  2003/12/12 06:11:44  millermi
 *  - Completely renovated how the points are selected.
 *    Previously slope was used to restrict points, now
 *    the angle of the point is calculated and compared
 *    with the starting/ending angles of the arc.
 *  - This class now assumes the arc will be drawn
 *    counterclockwise, so the starting angle may be
 *    larger than the stopping angle if the 4th to
 *    1st quadrant selection is made.
 *
 *  Revision 1.3  2003/10/22 20:26:09  millermi
 *  - Fixed java doc errors.
 *
 *  Revision 1.2  2003/08/21 22:41:08  millermi
 *  - Commented out debug statements and removed out code
 *    that was commented out.
 *
 *  Revision 1.1  2003/08/21 18:21:36  millermi
 *  - Initial Version - uses equation for ellipse and slope of a line to
 *    determine if a point is in the region.
 *
 */ 
package DataSetTools.components.View.Region;

import java.awt.Point;
import java.util.Vector;
 
import DataSetTools.util.floatPoint2D;
import DataSetTools.components.image.CoordBounds;
import DataSetTools.components.View.Cursor.SelectionJPanel;

/**
 * This class is a specific region designated by three points.
 * The WedgeRegion is used to pass points selected by a
 * wedge region (in SelectionOverlay) from the view component
 * to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the wedge are:
 * 
 * p[0]   = center pt of circle that arc is taken from
 * p[1]   = last mouse point/point at intersection of line and arc
 * p[2]   = reflection of p[1]
 * p[3]   = top left corner of bounding box around arc's total circle
 * p[4]   = bottom right corner of bounding box around arc's circle
 * p[5].x = startangle, the directional vector in degrees
 * p[5].y = degrees covered by arc.
 *
 * The large number of defining points replaces the work of recalculating
 * information at each step of the region calculation. Like a EllipseRegion,
 * this region may appear to be circular, but may actually be elliptical.
 */ 
public class WedgeRegion extends Region
{
 /**
  * Constructor - uses Region's constructor to set the defining points.
  * The defining points are assumed to be in image values, where
  * the input points are in (x,y) where (x = col, y = row ) form.
  * The only exception is definingpoint[5] which holds angular (in degrees)
  * values.
  *
  *  @param  dp - defining points of the wedge
  */ 
  public WedgeRegion( floatPoint2D[] dp )
  {
    super(dp);
  }
  
 /**
  * Get all of the points inside the wedge region. This method first determines
  * if the point is in the ellipse, then uses angles to find if the points
  * in the wedge region. 
  *
  *  @return array of points included within the wedge region.
  */
  public Point[] getSelectedPoints()
  { 
    initializeSelectedPoints();
    Region[] wedge = {this};
    // get rid of duplicate points.
    selectedpoints = getRegionUnion( wedge );
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
   /* p[0]   = center pt of circle that arc is taken from
    * p[1]   = last mouse point/point at intersection of line and arc
    * p[2]   = reflection of p[1]
    * p[3]   = top left corner of bounding box around arc's total circle
    * p[4]   = bottom right corner of bounding box around arc's circle
    * p[5].x = startangle, the directional vector in degrees
    * p[5].y = degrees covered by arc.
    */
    Point center = floorImagePoint(
                                world_to_image.MapTo(definingpoints[0]));
    Point p1 = floorImagePoint(
                                world_to_image.MapTo(definingpoints[1]));
    // rp1 is reflection of p1
    Point rp1 = floorImagePoint(
                                world_to_image.MapTo(definingpoints[2]));
    Point topleft = floorImagePoint(
                                world_to_image.MapTo(definingpoints[3]));
    Point bottomright = floorImagePoint(
                                world_to_image.MapTo(definingpoints[4]));
    
    int xextent = center.x - topleft.x;
    int yextent = center.y - topleft.y; 
    
    Vector points = new Vector();  // dynamic array of points
    float startangle = definingpoints[5].x;
    float totalangle = definingpoints[5].y + startangle;
    float stopangle = totalangle;
    int p1quad = 0; 
    // these adjustments are used when for the LineRegion selection near
    // the end.
    float p1xadjust = 0;
    float p1yadjust = 0;
    float rp1xadjust = 0;
    float rp1yadjust = 0; 
    // using the startangle, find the quadrant of p1
    if( startangle <= 180 )
    {
      if( startangle <= 90 )
      {
	p1quad =  1;
        p1xadjust = -0.5f;
        p1yadjust = -0.5f;
      }
      else
      {
	p1quad =  2;
        p1xadjust = -0.5f;
        p1yadjust = 0.5f;
      }
    }
    else
    {
      if( startangle < 270 )
      {
	p1quad =  3;
        p1xadjust = 0.5f;
        p1yadjust = 0.5f;
      }
      else
      {
	p1quad =  4;
        p1xadjust = 0.5f;
        p1yadjust = -0.5f;
      }
    } 
    // make sure angle is between 0 and 360  
    if( totalangle >= 360 )
      totalangle -= 360f;
    //System.out.println("Total: " + totalangle );
    int rp1quad = 0;
    // using the startangle + arcangle, find quadrant of reflection of p1
    if( totalangle <= 180 )
    {
      if( totalangle <= 90 )
      {
	rp1quad =  1;
        rp1xadjust = 0.5f;
        rp1yadjust = 0.5f;
      }
      else
      {
	rp1quad =  2;
        rp1xadjust = 0.5f;
        rp1yadjust = -0.5f;
      }
    }
    else
    {
      if( totalangle < 270 )
      {
	rp1quad =  3;
        rp1xadjust = -0.5f;
        rp1yadjust = -0.5f;
      }
      else
      {
	rp1quad =  4;
        rp1xadjust = -0.5f;
        rp1yadjust = 0.5f;
      }
    }
    
    // using formula for ellipse: (x-h)^2/a^2 + (y-k)^2/b^2 = 1
    // where x,y is point, (h,k) is center, and a,b are x/y extent (radius)
    int quadnum = 0;
    // these values are specific to each quadrant, once quadrant is know, set
    // these values.
    int ystart = 0;
    int ystop = 0;
    int xstart = 0;
    int xstop = 0;
    
    // if rp1quad < p1quad, angle goes from 4th quad to 1st quad. so
    // adjust rp1quad so it works as a ending bound for the for loop.
    if( rp1quad < p1quad )
      rp1quad += 4;
    // if the two are in the same quad, but the total angle is greater than 90,
    // the shape must be a pie with a slice removed.
    else if( rp1quad == p1quad && definingpoints[5].y > 90 )
      rp1quad += 3;

    // Step through each quadrant involved in the selection.
    for( int quadcount = p1quad; quadcount <= rp1quad; quadcount++ )
    {
      // since rp1quad could be > 4, restrict the quadnum to max of 4.
      if( quadcount > 4 )
	quadnum = quadcount - 4;
      else
	quadnum = quadcount;
      
      // Depending on which quadrant, set the bounds for the two for loops
      // below which will step through all the points in that quadrant
      // and add ones contained in the selection.
      // Since y values go top to bottom, the ystart and stop are switched
      // and since quad 2 & 3 x values should go right to left, those xstart
      // and stop are switched.
      if( quadnum == 1 )
      {
	ystart = center.y;
	ystop  = topleft.y;
	xstart = center.x;
	xstop  = bottomright.x;
      }
      else if( quadnum == 2 )
      {
	ystart = center.y;
	ystop  = topleft.y;
	xstart = topleft.x;
	xstop  = center.x - 1;
      }
      else if( quadnum == 3 )
      {
	ystart = bottomright.y;
	ystop  = center.y + 1;
	xstart = topleft.x;
	xstop  = center.x - 1;
      }
      else if( quadnum == 4 )
      {
	ystart = bottomright.y;
	ystop  = center.y + 1;
	xstart = center.x;
	xstop  = bottomright.x;
      }
      //System.out.println("Current quad(c): " + quadnum );
      double dist = 0;
      float xdiff = 0;
      float ydiff = 0;
      // These two loops will check every point within the given quadrant
      // and test to see if the point is in the ellipse from which the
      // arc comes from.
      for( int y = ystart; y >= ystop; y-- )
      {
	for( int x = xstart; x <= xstop; x++ )
	{
	  xdiff = 0;
	  ydiff = 0;
	  // x/y diff represent x-h/y-k respectively
	  xdiff = Math.abs( (float)(x - center.x) );
	  ydiff = Math.abs( (float)(y - center.y) );
	  // Subtracting 1/(xextent*4) is to account for fractional pixels.
	  // This will give a smoother, more accurate selected region.
	  dist = Math.pow((double)(xdiff - 1/(double)(xextent*4)),2) /
        	 Math.pow((double)xextent,2) + 
		   Math.pow((double)(ydiff - 1/(double)(yextent*4)),2) /
        	   Math.pow((double)yextent,2);
	  //System.out.println("(" + x + "," + y + ")..." + dist ); 
	  // Using ellipse equation, the distance must be < 1 in order to
          // be contained within the ellipse.
          if( dist <= 1 )
	  {
            int pointangle  = -Math.round( (float)Math.toDegrees(Math.atan2( 
						   (double)(y - center.y),
						   (double)(x - center.x))));
	    // put everything from 0-360
	    if( pointangle < 0 )
	      pointangle = 360 + pointangle;
	    
	   //System.out.println("Point/Stop: " + pointangle + "/" + stopangle);
            // if stopangle >= 360, the angle goes from 4th to 1st quadrant,
            // thus start - 360, and 0 - stop becomes the interval.
            if( stopangle >= 360 )
            {
              if( pointangle >= startangle || 
        	  pointangle <= stopangle - 360f )
              {
	        // make sure point is on the image.
	        CoordBounds imagebounds = world_to_image.getDestination();
	        if( imagebounds.onXInterval((float)x) && 
	            imagebounds.onYInterval((float)y) )
		  points.add( new Point( x, y ) );
              }
            }
            // otherwise the angle must be between the start and stop angle.
            else
            {
              if( pointangle >= startangle && pointangle <= stopangle )
              {
	        // make sure point is on the image.
	        CoordBounds imagebounds = world_to_image.getDestination();
	        if( imagebounds.onXInterval((float)x) && 
	            imagebounds.onYInterval((float)y) )
		  points.add( new Point( x, y ) );
              }
            }
	  } // if( dist < 1 )
	} // end for x
      } // end for y
    } // for quad
    
    //System.out.println("Center: (" + (center.x - topleft.x) + "," +
    //  		 (center.y - topleft.y) + ")");
    /*
    // this code uses line regions to select the points along the bounding
    // lines of the wedge
    floatPoint2D p1temp = new floatPoint2D( definingpoints[1].x + p1xadjust,
					    definingpoints[1].y + p1yadjust );
    // map defining points back to world coords.
    floatPoint2D[] p1pts = {definingpoints[0],p1temp};
    LineRegion p1line = new LineRegion( p1pts );
    p1line.setWorldBounds(world_to_image.getSource());
    p1line.setImageBounds(world_to_image.getDestination());
    Point[] p1select = p1line.initializeSelectedPoints();
    for( int i = 0; i < p1select.length; i++ )
    {
      // make sure point is on the image.
      CoordBounds imagebounds = world_to_image.getDestination();
      if( imagebounds.onXInterval((float)p1select[i].x) && 
          imagebounds.onYInterval((float)p1select[i].y) )
        points.add( new Point( p1select[i] ) );
    }
    floatPoint2D rp1temp = new floatPoint2D( definingpoints[2].x + rp1xadjust,
					     definingpoints[2].y + rp1yadjust );
    // map defining points back to world coords.
    floatPoint2D[] rp1pts = {definingpoints[0],rp1temp};
    LineRegion rp1line = new LineRegion( rp1pts );
    rp1line.setWorldBounds(world_to_image.getSource());
    rp1line.setImageBounds(world_to_image.getDestination());
    Point[] rp1select = rp1line.initializeSelectedPoints();
    for( int i = 0; i < rp1pts.length; i++ )
    {
      // make sure point is on the image.
      CoordBounds imagebounds = world_to_image.getDestination();
      if( imagebounds.onXInterval((float)rp1select[i].x) && 
          imagebounds.onYInterval((float)rp1select[i].y) )
        points.add( new Point( rp1select[i] ) );
    }*/
    
    // put the vector of points into an array of points
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
    return ("Region: Wedge\n" +
            "Center: " + definingpoints[0] + "\n" +
	    "Arc Beginning Pt: " + definingpoints[1] + "\n" +
	    "Arc Ending Pt: " + definingpoints[2] + "\n" +
	    "Top-left bound: " + definingpoints[3] + "\n" +
	    "Bottom-right bound: " + definingpoints[4] + "\n" +
	    "Starting Angle: " + definingpoints[5].x + "\n" + 
	    "Interior Angle: " + definingpoints[5].y + "\n" );
  }
   
 /**
  * This method returns the rectangle containing the ellipse from which the
  * wedge is taken from.
  *
  *  @return The bounds of the WedgeRegion.
  */
  protected CoordBounds getRegionBounds()
  {
    return new CoordBounds( world_to_image.MapTo(definingpoints[3]).x,
                            world_to_image.MapTo(definingpoints[3]).y, 
                            world_to_image.MapTo(definingpoints[4]).x,
			    world_to_image.MapTo(definingpoints[4]).y );
  }
}
