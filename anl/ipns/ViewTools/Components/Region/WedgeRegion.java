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
 *  Revision 1.3  2003/10/22 20:26:09  millermi
 *  - Fixed java doc errors.
 *
 *  Revision 1.2  2003/08/21 22:41:08  millermi
 *  - Commented out debug statements and removed out code
 *    that was commented out.
 *
 *  Revision 1.1  2003/08/21 18:21:36  millermi
 *  - Initial Version - uses equation for elipse and slope of a line to
 *    determine if a point is in the region.
 *
 */ 
package DataSetTools.components.View.Region;

import java.awt.Point;
import java.util.Vector;
 
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
 * information at each step of the region calculation. Like a ElipseRegion,
 * this region may appear to be circular, but may actually be eliptical.
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
   public WedgeRegion( Point[] dp )
   {
     super(dp);
   }
   
  /**
   * Get all of the points inside the wedge region. This method first determines
   * if the point is in the elipse, then uses slope to find if the point is
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
     Point rp1 = new Point( definingpoints[2] );
     Point topleft = new Point( definingpoints[3] );
     Point bottomright = new Point( definingpoints[4] );
     double xextent = (double)(bottomright.x - topleft.x)/2;
     double yextent = (double)(bottomright.y - topleft.y)/2;
     // p1 will always be before rp1 when tracing unit circle counterclockwise
     // if degrees covered by arc are negative, circle is missing a wedge. 
     boolean counterclockwise = true;
     if( definingpoints[5].y < 0 )
       counterclockwise = false;
     //square the extents for comparison later
     xextent = Math.pow(xextent,2);
     yextent = Math.pow(yextent,2);
     
     Vector points = new Vector();
     int pointangle = 0;
     int stopangle = definingpoints[5].x + definingpoints[5].y;
     if( stopangle < 0 )
       stopangle = 360 + stopangle;
     else if( stopangle > 360 )
       stopangle = stopangle - 360;
     
     // upper and lower slope is determined by p1 & rp1, midslope is per point
     float upperslope = 0;
     float lowerslope = 0;
     float midslope   = 0;
     
     int p1quad = 0;  // the quadrant p1 is in, normal unit circle quadrants.
     if( p1.x > center.x )
     {
       if( p1.y <= center.y )
         p1quad = 1;
       else
         p1quad = 4;
     }
     else
     {
       if( p1.y < center.y )
         p1quad = 2;
       else
         p1quad = 3;
     }
     
     int rp1quad = 0; // quadrant rp1 is in
     if( rp1.x > center.x )
     {
       if( rp1.y <= center.y )
         rp1quad = 1;
       else
         rp1quad = 4;
     }
     else
     {
       if( rp1.y < center.y )
         rp1quad = 2;
       else
         rp1quad = 3;
     }
     
     //System.out.println("p1/rp1 Quad: " + p1quad + "/" + rp1quad );
     
     // this code will avoid slope of 0 or +/- infinity
     float numerator = p1.y - center.y;
     if( numerator == 0 )
       numerator = .01f;
     float denominator = p1.x - center.x;
     if( denominator == 0 )
       denominator = -.01f;
     lowerslope = numerator/denominator;
     
     numerator = rp1.y - center.y;
     if( numerator == 0 )
       numerator = .01f;
     denominator = rp1.x - center.x;
     if( denominator == 0 )
       denominator = -.01f;
     upperslope = numerator/denominator;
     
     //lowerslope = (float)(p1.y - center.y)/(float)(p1.x - center.x);
     //upperslope = (float)(rp1.y - center.y)/(float)(rp1.x - center.x);
    //System.out.println("Lower/Upper Slope: " + lowerslope + "/" + upperslope);
     // using formula for elipse: (x-h)^2/a^2 + (y-k)^2/b^2 = 1
     // where x,y is point, (h,k) is center, and a,b are x/y extent (radius)
     int quadnum = 0;
     // these values are specific to each quadrant, once quadrant is know, set
     // these values.
     int ystart = 0;
     int ystop = 0;
     int xstart = 0;
     int xstop = 0;
     if( counterclockwise )
     {
       if( rp1quad < p1quad )
         rp1quad += 4;
       for( int quadcount = p1quad; quadcount <= rp1quad; quadcount++ )
       {
         if(quadcount > 4 )
           quadnum = quadcount - 4;
         else
	   quadnum = quadcount;
	 
	 if( quadnum == 1 )
	 {
	   ystart = topleft.y;
	   ystop  = center.y;
	   xstart = center.x + 1;
	   xstop  = bottomright.x;
	 }
	 else if( quadnum == 2 )
	 {
	   ystart = topleft.y;
	   ystop  = center.y - 1;
	   xstart = topleft.x;
	   xstop  = center.x;
	 }
	 else if( quadnum == 3 )
	 {
	   ystart = center.y;
	   ystop  = bottomright.y ;
	   xstart = topleft.x;
	   xstop  = center.x;
	 }
	 else if( quadnum == 4 )
	 {
	   ystart = center.y + 1;
	   ystop  = bottomright.y;
	   xstart = center.x + 1;
	   xstop  = bottomright.x;
	 }
	   
         //System.out.println("Current quad(c): " + quadnum );

     	 for( int y = ystart; y <= ystop; y++ )
         {
     	   for( int x = xstart; x <= xstop; x++ )
     	   {
     	     double dist = Math.pow((x - center.x),2)/xextent + 
                	   Math.pow((y - center.y),2)/yextent;
             //System.out.println("(" + x + "," + y + ")..." + dist ); 
             if( dist <= 1 )
             {
	       if( p1quad != quadnum && rp1quad != quadnum && 
	           (rp1quad - 4) != quadnum )
	       {
	         points.add( new Point( x, y ) );
	       }
	       else
	       {
	       //midslope = (float)(y - center.y)/(float)( x - center.x );
	         
                 numerator = y - center.y;
                 if( numerator == 0 )
                   numerator = .01f;
                 denominator = x - center.x;
                 if( denominator == 0 )
                   denominator = -.01f;
     	         midslope = numerator/denominator;
	         
                 if( rp1quad != quadnum )
                 {
                   if( midslope <= lowerslope )
     	             points.add( new Point( x, y ) );
                 }
                 else if( p1quad != quadnum )
                 {
                   if( midslope >= upperslope )
     	             points.add( new Point( x, y ) );
                 }
                 else
                 {
                   if( midslope <= lowerslope &&
     	               midslope >= upperslope )
     	             points.add( new Point( x, y ) );
                 }
               }
             }
           } // end for x
         } // end for y
       } // for quad
     } // if counterclockwise
     // *********************else its clockwise**********************
     else
     {
       if( rp1quad >= p1quad )
         p1quad += 4;
       int quadenteredcount = 0;
       for( int quadcount = p1quad; quadcount >= rp1quad; quadcount-- )
       {
         if(quadcount > 4 )
           quadnum = quadcount - 4;
	 else
	   quadnum = quadcount;
	 
	 // set starting and ending points of the for loop depending on the quad
	 if( quadnum == 1 )
	 {
	   ystart = topleft.y;
	   ystop  = center.y;
	   xstart = center.x + 1;
	   xstop  = bottomright.x;
	 }
	 else if( quadnum == 2 )
	 {
	   ystart = topleft.y;
	   ystop  = center.y - 1;
	   xstart = topleft.x;
	   xstop  = center.x;
	 }
	 else if( quadnum == 3 )
	 {
	   ystart = center.y;
	   ystop  = bottomright.y ;
	   xstart = topleft.x;
	   xstop  = center.x;
	 }
	 else if( quadnum == 4 )
	 {
	   ystart = center.y + 1;
	   ystop  = bottomright.y;
	   xstart = center.x + 1;
	   xstop  = bottomright.x;
	 }
       
         //System.out.println("Current quad: " + quadnum );
	 
         if( quadenteredcount < 4 )
         { 
	   quadenteredcount++;
     	   for( int y = ystart; y <= ystop; y++ )
           {
     	     for( int x = xstart; x <= xstop; x++ )
     	     {
     	       double dist = Math.pow((x - center.x),2)/xextent + 
        		     Math.pow((y - center.y),2)/yextent;
               //System.out.println("(" + x + "," + y + ")..." + dist ); 
               if( dist <= 1 )
               {
	         if( p1quad != quadnum && (p1quad - 4) != quadnum && 
		     rp1quad != quadnum )
		 {
		   points.add( new Point( x, y ) );
		 }
		 else
		 {
		   //midslope = (float)(y - center.y)/(float)( x - center.x );
		   
                   numerator = y - center.y;
                   if( numerator == 0 )
                     numerator = .01f;
                   denominator = x - center.x;
                   if( denominator == 0 )
                     denominator = -.01f;
     	           midslope = numerator/denominator;
		   
                   if( rp1quad != quadnum )
                   {
                     if( midslope >= lowerslope )
     	               points.add( new Point( x, y ) );
                   }
                   else if( p1quad != quadnum && (p1quad - 4) != quadnum )
                   {
                     if( midslope <= upperslope )
     	               points.add( new Point( x, y ) );
		   }
                   else
                   {
                     if( midslope >= lowerslope ||
     	                 midslope <= upperslope )
     	               points.add( new Point( x, y ) );
                   }
                 }
	       }
             } // end for x
           } // end for y
         } // end if quadenteredcount
       } // for quadcount
     } // end else clockwise
     
     // put the vector of points into an array of points
     selectedpoints = new Point[points.size()];
     for( int i = 0; i < points.size(); i++ )
         selectedpoints[i] = (Point)points.elementAt(i);
     return selectedpoints;     
   }
}
