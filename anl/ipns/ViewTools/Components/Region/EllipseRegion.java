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
package DataSetTools.components.View.Region;

import java.awt.Point;
import java.util.Vector;

import DataSetTools.util.floatPoint2D;
import DataSetTools.components.View.Cursor.SelectionJPanel;

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
   public EllipseRegion( Point[] dp )
   {
     super(dp);
   }
   
  /**
   * Get all of the points inside the elliptical region. 
   *
   *  @return array of points included within the elliptical region.
   */
   public Point[] getSelectedPoints()
   { // needs to be changed.
     Point topleft = new Point( definingpoints[0] );
     Point bottomright = new Point( definingpoints[1] );
     Point pcenter = new Point( definingpoints[2] );
     double xextent = (double)(pcenter.x - topleft.x);
     double yextent = (double)(pcenter.y - topleft.y); 
     // since a mapping is done with the imagejpanel, the topleft or bottomright
     // could have been mapped to the side of the image. However, at most
     // one will be affected, so take the maximum extent of the two.
     // Correct the defining points if selection made near border of image.
     if( (bottomright.x - pcenter.x)-1 > xextent )
     {
       xextent = bottomright.x - pcenter.x; 
       topleft.x = (int)(pcenter.x - xextent); 
       definingpoints[0].x = topleft.x;
     }
     else
     {
       bottomright.x = (int)(pcenter.x + xextent);
       definingpoints[1].x = bottomright.x;
     }
     if( (bottomright.y - pcenter.y)-1 > yextent )
     {
       yextent = bottomright.y - pcenter.y;
       topleft.y = (int)(pcenter.y - yextent);
       definingpoints[0].y = topleft.y;
     }
     else
     {
       bottomright.y = (int)(pcenter.y + yextent);
       definingpoints[1].y = bottomright.y;
     }
     
     floatPoint2D center = new floatPoint2D( (float)(topleft.x + xextent),
                               (float)(topleft.y + yextent) );
     
     Vector points = new Vector();
     
     // if only one pixel is selected by the circle, for low resolution.
     if( xextent == 0 && yextent == 0 )
       points.add( new Point( (int)(center.x + .5), (int)(center.y + .5) ) );
     // if one pixel in x, and more than one in y is selected
     else if( xextent == 0 )
     {
       for( int y = topleft.y; y <= bottomright.y; y++ ) 
         points.add( new Point( topleft.x, y ) );
     }
     // if one pixel in y, and more than one in x is selected
     else if( yextent == 0 )
     {
       for( int x = topleft.x; x <= bottomright.x; x++ ) 
         points.add( new Point( x, topleft.y ) );
       
     }
     // large region, more than one pixel in both x and y
     else
     {
       double dist = 0;
       double xdiff = 0;
       double ydiff = 0;
       // using formula for ellipse: (x-h)^2/a^2 + (y-k)^2/b^2 = 1
       // where x,y is point, (h,k) is center, and a,b are x/y extent (radius)
       for( int y = topleft.y; y <= bottomright.y; y++ )
       {
         for( int x = topleft.x; x <= bottomright.x; x++ )
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
	   if( dist <= 1 )
	     points.add( new Point( x, y ) ); 
         } 
       }
     }
     selectedpoints = new Point[points.size()];
     for( int i = 0; i < points.size(); i++ )
         selectedpoints[i] = (Point)points.elementAt(i);
     return selectedpoints;
   }
}
