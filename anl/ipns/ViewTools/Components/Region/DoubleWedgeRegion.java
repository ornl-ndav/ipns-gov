/*
 * File: DoubleWedgeRegion.java
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
 *  Revision 1.1  2003/08/26 03:38:19  millermi
 *  - Initial Version - Allows double wedge selected regions to be passed to
 *    the viewer. Restricts wedge angle to 180 degrees.
 *
 */ 
package DataSetTools.components.View.Region;

import java.awt.Point;
import java.util.Vector;
 
import DataSetTools.components.View.Cursor.SelectionJPanel;

/**
 * This class is a specific region designated by three points.
 * The DoubleWedgeRegion is used to pass points selected by a
 * symmetric double wedge region (in SelectionOverlay) from the view
 * component to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the double wedge are same as the wedge:
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
public class DoubleWedgeRegion extends Region
{
  /**
   * Constructor - uses Region's constructor to set the defining points.
   * The defining points are assumed to be in image values, where
   * the input points are in (x,y) where (x = col, y = row ) form.
   * The only exception is definingpoint[5] which holds angular (in degrees)
   * values.
   *
   *  @param  definingpoints
   */ 
   public DoubleWedgeRegion( Point[] dp )
   {
     super(dp);
   }
   
  /**
   * Get all of the points inside the double wedge region. 
   *
   *  @return array of points included within the double wedge region.
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
     *
     * Although this method uses quadrants similar to the unit circle,
     * be aware that the slope in these quadrants does not behave in the same
     * fashion. Quad II & III have positive slope while Quad I & IV have neg.
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
     int arcangle = definingpoints[5].y;
	     
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
        
     // this code will avoid slope of 0 or +/- infinity
     float numerator = p1.y - center.y;
     float denominator = p1.x - center.x;
     if( denominator == 0 )
       denominator = -.01f;
     lowerslope = numerator/denominator;
     //System.out.println("Num/Denom: " + numerator + "/" + denominator);
     
     numerator = rp1.y - center.y;
     denominator = rp1.x - center.x;
     if( denominator == 0 )
       denominator = -.01f;
     if( Math.abs(numerator) == 0 )
     {
       if( lowerslope < 0 && denominator > 0 )
         numerator = -0;
       else
         numerator = 0;
     }
     upperslope = numerator/denominator;
     
     // If slope > 800, it was infinity, however, +/- infinity are next to each
     // other. For convenience, if number is infinity (>800), match the signs
     if( lowerslope > 800 && upperslope < 0 )
       lowerslope = -lowerslope; 
     else if( lowerslope < -800 && upperslope > 0 )
       lowerslope = -lowerslope; 
      
     if( upperslope > 800 && lowerslope < 0 )
       upperslope = -upperslope; 
     else if( upperslope < -800 && lowerslope > 0 )
       upperslope = -upperslope; 
     
     //System.out.println("Low/Mid/Upper " + lowerslope + "/" + midslope +
     //                   "/" + upperslope ); 
     
     if( lowerslope > upperslope )
     {
       float temp = lowerslope;
       lowerslope = upperslope;
       upperslope = temp;
     }
     
     if( counterclockwise )
     {
       //System.out.println("CounterClockwise");
       // using formula for elipse: (x-h)^2/a^2 + (y-k)^2/b^2 = 1
       // where x,y is point, (h,k) is center, and a,b are x/y extent (radius)
       for( int y = topleft.y; y <= bottomright.y; y++ )
         for( int x = topleft.x; x <= bottomright.x; x++ )
         {
           double dist = Math.pow((x - center.x),2)/xextent + 
	                 Math.pow((y - center.y),2)/yextent;
	   //System.out.println("(" + x + "," + y + ")..." + dist ); 
	   if( dist <= 1 )
	   {
             numerator = y - center.y;
             if( numerator == 0 )
               numerator = .01f;
             denominator = x - center.x;
             if( denominator == 0 )
               denominator = -.01f;
     	     midslope = numerator/denominator;
	  // System.out.println("Low/Mid/Upper " + lowerslope + "/" + midslope +
	  //                    "/" + upperslope ); 
	     if( Math.abs(arcangle) > 90 &&
	         (lowerslope*upperslope >= 0 ) )
	     {
	       if( (midslope <= lowerslope) ||
	           (midslope >= upperslope) )
	         points.add( new Point( x, y ) );
	     } 
	     // if upper and lower are opposite sign
	     else if( (lowerslope*upperslope < 0 ) )
	     { 
	       // if wedge starts in quad 2 and ends in quad 3
	       // rp1quad == 1 && p1quad == 4 taken care of by clockwise code.
	       if( p1quad == 2 && rp1quad == 3 )
	       {
	         if( midslope >= lowerslope &&
	             midslope <= upperslope )
	           points.add( new Point( x, y ) );
	       }
	       else
	       {
	         if( midslope <= lowerslope ||
	             midslope >= upperslope )
	           points.add( new Point( x, y ) );
	       }
	     }
	     // most generic case
	     else if( midslope >= lowerslope &&
	         midslope <= upperslope )
	       points.add( new Point( x, y ) );
	      
	   }
         } 
     }
     else //( !counterclockwise )
     {
       //System.out.println("Clockwise");
       // using formula for elipse: (x-h)^2/a^2 + (y-k)^2/b^2 = 1
       // where x,y is point, (h,k) is center, and a,b are x/y extent (radius)
       for( int y = topleft.y; y <= bottomright.y; y++ )
         for( int x = topleft.x; x <= bottomright.x; x++ )
         {
           double dist = Math.pow((x - center.x),2)/xextent + 
	                 Math.pow((y - center.y),2)/yextent;
	   //System.out.println("(" + x + "," + y + ")..." + dist );
	   if( dist <= 1 )
	   {
             numerator = y - center.y;
             if( numerator == 0 )
               numerator = .01f;
             denominator = x - center.x;
             if( denominator == 0 )
               denominator = -.01f;
     	     midslope = numerator/denominator;
	     
	     // if arc > 90 degrees and both lower and upper are pos or neg. 
	     if( Math.abs(arcangle) > 90 &&
	         (lowerslope*upperslope >= 0 ) )
	     {
	       if( (midslope <= lowerslope) ||
	           (midslope >= upperslope) )
	         points.add( new Point( x, y ) );
	     }     
	     else if( ( lowerslope > 0 && upperslope < 0 ) ||
	         ( lowerslope < 0 && upperslope > 0 ) )
	     { 
	       if( (midslope >= lowerslope) &&
	           (midslope <= upperslope) )
	         points.add( new Point( x, y ) );
	     }
	     else if( midslope >= lowerslope &&
	         midslope <= upperslope )
	       points.add( new Point( x, y ) );
	      
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
