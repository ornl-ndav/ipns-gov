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
  protected boolean[][] pointchecker = new boolean[0][0];
  /**
   * Constructor - uses Region's constructor to set the defining points.
   * The defining points are assumed to be in image values, where
   * the input points are in (x,y) where (x = col, y = row ) form.
   * The only exception is definingpoint[5] which holds angular (in degrees)
   * values.
   *
   *  @param  dp - defining points of the wedge
   */ 
   public WedgeRegion( Point[] dp )
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
    /* p[0]   = center pt of circle that arc is taken from
     * p[1]   = last mouse point/point at intersection of line and arc
     * p[2]   = reflection of p[1]
     * p[3]   = top left corner of bounding box around arc's total circle
     * p[4]   = bottom right corner of bounding box around arc's circle
     * p[5].x = startangle, the directional vector in degrees
     * p[5].y = degrees covered by arc.
     */
     Point center = new Point( definingpoints[0] );
     Point p1 = new Point( definingpoints[1] );
     Point rp1 = new Point( definingpoints[2] );   // reflection of p1
     Point topleft = new Point( definingpoints[3] );
     Point bottomright = new Point( definingpoints[4] );
     
     double xextent = (double)(center.x - topleft.x);
     double yextent = (double)(center.y - topleft.y); 
     // since a mapping is done with the imagejpanel, the topleft or bottomright
     // could have been mapped to the side of the image. However, at most
     // one will be affected, so take the maximum extent of the two.
     // Correct the defining points if selection made near border of image.
     if( (bottomright.x - center.x) > xextent )
     {
       xextent = bottomright.x - center.x; 
       topleft.x = (int)(center.x - xextent);
       definingpoints[3].x = topleft.x;
     }
     else if( (bottomright.x - center.x) < xextent )
     {
       bottomright.x = (int)(center.x + xextent);
       definingpoints[4].x = bottomright.x;
     }
     
     if( (bottomright.y - center.y) > yextent )
     {
       yextent = bottomright.y - center.y;
       topleft.y = (int)(center.y - yextent);
       definingpoints[3].y = topleft.y;
     }
     else if( (bottomright.y - center.y) < yextent )
     {
       bottomright.y = (int)(center.y + yextent);
       definingpoints[4].y = bottomright.y;
     }
     
     Vector points = new Vector();
     // use this 2-d array to mark points that have been selected.
     // First, all points inside the wedge are selected. To maintain a
     // consistent way of selecting regions, all boundry points are also
     // included. To do this, LineRegions are used to find the inclusive points
     // on each line bounding the wedge. This pointchecker acts as a board
     // to mark which points have already been added. When a point is added,
     // the corresponding boolean value is changed to true. If true, the point
     // will not be added.
     // Since this value is now a protected variable, if it is set correctly
     // by an outside source, use the array given.
     int xdist = bottomright.x - topleft.x + 1;
     int ydist = bottomright.y - topleft.y + 1;
     if( !( pointchecker.length == xdist && pointchecker[0].length == ydist) )
     {
       pointchecker = new boolean[xdist][ydist];
     }
     int startangle = definingpoints[5].x;
     int totalangle = definingpoints[5].y + startangle;
     int stopangle = totalangle;
     int p1quad = 0; 
     // these adjustments are used when for the LineRegion selection near
     // the end.
     int p1xadjust = 0;
     int p1yadjust = 0;
     int rp1xadjust = 0;
     int rp1yadjust = 0; 
     // using the startangle, find the quadrant of p1
     if( startangle <= 180 )
     {
       if( startangle <= 90 )
       {
         p1quad =  1;
	 p1xadjust = -1;
	 p1yadjust = 1;
       }
       else
       {
         p1quad =  2;
	 p1xadjust = 1;
	 p1yadjust = 1;
       }
     }
     else
     {
       if( startangle < 270 )
       {
         p1quad =  3;
	 p1xadjust = 1;
	 p1yadjust = -1;
       }
       else
       {
         p1quad =  4;
	 p1xadjust = -1;
	 p1yadjust = -1;
       }
     } 
     // make sure angle is between 0 and 360  
     if( totalangle >= 360 )
       totalangle -= 360;
     //System.out.println("Total: " + totalangle );
     int rp1quad = 0;
     // using the startangle + arcangle, find quadrant of reflection of p1
     if( totalangle <= 180 )
     {
       if( totalangle <= 90 )
       {
         rp1quad =  1;
	 rp1xadjust = -1;
	 rp1yadjust = 1;
       }
       else
       {
         rp1quad =  2;
	 rp1xadjust = 1;
	 rp1yadjust = 1;
       }
     }
     else
     {
       if( totalangle < 270 )
       {
         rp1quad =  3;
	 rp1xadjust = 1;
	 rp1yadjust = -1;
       }
       else
       {
         rp1quad =  4;
	 rp1xadjust = -1;
	 rp1yadjust = -1;
       }
     }
     //System.out.println("p1/rp1 Quad: " + p1quad + "/" + rp1quad );
     
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

     //System.out.println("p1quad/rp1quad: " + p1quad + "/" + rp1quad );
     // Step through each quadrant involved in the selection.
     for( int quadcount = p1quad; quadcount <= rp1quad; quadcount++ )
     {
       // since rp1quad could be > 4, restrict the quadnum to max of 4.
       if(quadcount > 4 )
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
       double xdiff = 0;
       double ydiff = 0;
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
     	   xdiff = Math.abs( (double)x - center.x );
     	   ydiff = Math.abs( (double)y - center.y );
           // Subtracting 1/(xextent*4) is to account for fractional pixels.
           // This will give a smoother, more accurate selected region.
     	   dist = Math.pow((xdiff - 1/(xextent*4)),2)/Math.pow(xextent,2) + 
        	  Math.pow((ydiff - 1/(yextent*4)),2)/Math.pow(yextent,2);
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
	     // Add/Subtract 1 to pointangle to include border points.
	     if( stopangle >= 360 )
	     {
	       if( pointangle >= startangle || 
	           pointangle <= stopangle - 360 )
	       {
        	 points.add( new Point( x, y ) );
		 // add one to the index as a lower cushion, prevents index=-1
		 pointchecker[x-topleft.x][y-topleft.y] = true;
	       }
	     }
	     // otherwise the angle must be between the start and stop angle.
	     // Add/subtract one so pointangle = 0 or 360 not a problem
	     else
	     {
	       if( pointangle >= startangle && pointangle <= stopangle )
	       {
        	 points.add( new Point( x, y ) );
		 // add one to the index as a lower cushion, prevents index=-1
		 pointchecker[x-topleft.x][y-topleft.y] = true;
	       }
	     }
     	   } // if( dist < 1 )
     	 } // end for x
       } // end for y
     } // for quad
     
     //System.out.println("Center: (" + (center.x - topleft.x) + "," +
     //                   (center.y - topleft.y) + ")");
     
     // this code uses line regions to select the points along the bounding
     // lines of the wedge
     Point p1temp = new Point( p1.x + p1xadjust, p1.y + p1yadjust );
     Point[] p1pts = {center,p1temp};
     LineRegion p1line = new LineRegion( p1pts );
     p1pts = p1line.getSelectedPoints();
     int tempx = -1;
     int tempy = -1;
     for( int i = 0; i < p1pts.length; i++ )
     {
       if( !(p1pts[i].x < topleft.x || p1pts[i].y < topleft.y) &&
           !(p1pts[i].x > bottomright.x || p1pts[i].y > bottomright.y) )
       {
         tempx = p1pts[i].x-topleft.x;
         tempy = p1pts[i].y-topleft.y;
	 //System.out.println("(" + tempx + "," + tempy + ") " + 
	 //                   pointchecker[tempx][tempy] );
         if( !pointchecker[tempx][tempy] )
         {
           points.add( new Point( p1pts[i].x, p1pts[i].y ) );
           pointchecker[p1pts[i].x-topleft.x][p1pts[i].y-topleft.y] = true;
         }
       }
     }
     Point rp1temp = new Point( rp1.x + rp1xadjust, rp1.y + rp1yadjust );
     Point[] rp1pts = {center,rp1temp};
     LineRegion rp1line = new LineRegion( rp1pts );
     rp1pts = rp1line.getSelectedPoints();
     for( int i = 0; i < rp1pts.length; i++ )
     {
       if( !(rp1pts[i].x < topleft.x || rp1pts[i].y < topleft.y) &&
           !(rp1pts[i].x > bottomright.x || rp1pts[i].y > bottomright.y) )
       {
         tempx = rp1pts[i].x-topleft.x;
         tempy = rp1pts[i].y-topleft.y;
	 //System.out.println("(" + tempx + "," + tempy + ") " + 
	 //                   pointchecker[tempx][tempy] );
         if( !pointchecker[tempx][tempy] )
         {
           points.add( new Point( rp1pts[i].x, rp1pts[i].y ) );
           pointchecker[rp1pts[i].x-topleft.x][rp1pts[i].y-topleft.y] = true;
         }
       }
     }
     
     // put the vector of points into an array of points
     selectedpoints = new Point[points.size()];
     for( int i = 0; i < points.size(); i++ )
       selectedpoints[i] = (Point)points.elementAt(i);
     return selectedpoints;     
   }
}
