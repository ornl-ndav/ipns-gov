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
 *  Revision 1.5  2003/12/23 18:39:22  millermi
 *  - Improved how region was calculated.
 *
 *  Revision 1.4  2003/12/20 05:42:26  millermi
 *  - Now corrects the topleft/bottomright defining points if
 *    they were scaled incorrectly by the image.
 *
 *  Revision 1.3  2003/12/16 01:45:08  millermi
 *  - Changed way selected points are calculated, now uses
 *    two WedgeRegion instances to calculate the points
 *    in the region.
 *
 *  Revision 1.2  2003/10/22 20:26:09  millermi
 *  - Fixed java doc errors.
 *
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
   *  @param  dp - defining points of the DoubleWedge
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
     double xextent = (double)(center.x - topleft.x);
     double yextent = (double)(center.y - topleft.y); 
     // since a mapping is done with the imagejpanel, the topleft or bottomright
     // could have been mapped to the side of the image. However, at most
     // one will be affected, so take the maximum extent of the two.
     // Correct the defining points if selection made near border of image.
     if( (bottomright.x - center.x) - 1 > xextent )
     {
       xextent = bottomright.x - center.x; 
       topleft.x = (int)(center.x - xextent); 
       definingpoints[3].x = topleft.x;
     }
     else if( (bottomright.x - center.x) + 1 < xextent )
     {
       bottomright.x = (int)(center.x + xextent);
       definingpoints[4].x = bottomright.x;
     }
     
     if( (bottomright.y - center.y) - 1 > yextent )
     {
       yextent = bottomright.y - center.y;
       topleft.y = (int)(center.y - yextent);
       definingpoints[3].y = topleft.y;
     }
     else if( (bottomright.y - center.y) + 1 < yextent )
     {
       bottomright.y = (int)(center.y + yextent);
       definingpoints[4].y = bottomright.y;
     }
     
     //System.out.println(bottomright.toString() + " " + topleft.toString() );
     
     WedgeRegion wedge1 = new WedgeRegion(definingpoints);
     Point[] selected_pts_wedge1 = wedge1.getSelectedPoints();
     
     Point[] defpt2 = new Point[definingpoints.length];
     defpt2[0] = new Point( definingpoints[0] );
     defpt2[1] = new Point( 2*center.x - p1.x, 2*center.y - p1.y );
     defpt2[2] = new Point( 2*center.x - rp1.x, 2*center.y - rp1.y );
     defpt2[3] = new Point( definingpoints[3] );
     defpt2[4] = new Point( definingpoints[4] );
     
     // since defpt2[1] & defpt2[2] are calculated, it is possible for them
     // to fall outside of the bounds. This will make sure they don't.
     // bound defpt2[1].x
     if( defpt2[1].x > bottomright.x )
       defpt2[1].x = bottomright.x;
     else if( defpt2[1].x < topleft.x )
       defpt2[1].x = topleft.x;
     
     // bound defpt2[1].y
     if( defpt2[1].y > bottomright.y )
       defpt2[1].y = bottomright.y;
     else if( defpt2[1].y < topleft.y )
       defpt2[1].y = topleft.y;
     
     // bound defpt2[2].x
     if( defpt2[2].x > bottomright.x )
       defpt2[2].x = bottomright.x;
     else if( defpt2[2].x < topleft.x )
       defpt2[2].x = topleft.x;
     
     // bound defpt2[2].y
     if( defpt2[2].y > bottomright.y )
       defpt2[2].y = bottomright.y;
     else if( defpt2[2].y < topleft.y )
       defpt2[2].y = topleft.y;
     
     if( (definingpoints[5].x + 180) >= 360 )
       defpt2[5] = new Point( definingpoints[5].x - 180, definingpoints[5].y );
     else
       defpt2[5] = new Point( definingpoints[5].x + 180, definingpoints[5].y );
     
     WedgeRegion wedge2 = new WedgeRegion(defpt2);
     //wedge1.pointchecker[center.x-topleft.x][center.y-topleft.y] = true;
     wedge2.pointchecker = wedge1.pointchecker;
     Point[] selected_pts_wedge2 = wedge2.getSelectedPoints();
     int total_num_pts = selected_pts_wedge1.length +
                         selected_pts_wedge2.length;
     
     selectedpoints = new Point[total_num_pts];
     for( int i = 0; i < selected_pts_wedge1.length; i++ )
       selectedpoints[i] = new Point(selected_pts_wedge1[i]);
     Point temp;
     // this increment value is used to avoid gaps in the array when
     // duplicate points are bypassed and not added to the selectedpoints array.
     int inc = 0;
     for( int j = 0; j < selected_pts_wedge2.length; j++ )
       selectedpoints[selected_pts_wedge1.length + j] = 
                                 new Point(selected_pts_wedge2[j]);
     
     return selectedpoints;
   }
}
