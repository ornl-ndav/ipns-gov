/*
 * File: ElipseRegion.java
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
 
import DataSetTools.components.View.Cursor.SelectionJPanel;

/**
 * This class is a specific region designated by two points which bound the
 * elipse. The ElipseRegion is used to pass points selected by a
 * circle region (in SelectionOverlay) from the view component
 * to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 * The defining points of the elipse are the top left and bottom right points
 * of the rectangle bounding the elipse. An elipse must be used instead of a
 * circle, because although the selection looks circular, unless aspect ratio
 * is preserved, it may not be.
 */ 
public class ElipseRegion extends Region
{
  /**
   * Constructor - uses Region's constructor to set the defining points.
   * The defining points are assumed to be in image values, where
   * the input points are in (x,y) where (x = col, y = row ) form.
   *
   *  @param  definingpoints
   */ 
   public ElipseRegion( Point[] dp )
   {
     super(dp);
   }
   
  /**
   * Get all of the points inside the eliptical region. 
   *
   *  @return array of points included within the eliptical region.
   */
   public Point[] getSelectedPoints()
   { // needs to be changed.
     Point topleft = new Point( definingpoints[0] );
     Point bottomright = new Point( definingpoints[1] );
     double xextent = (double)(bottomright.x - topleft.x)/2;
     double yextent = (double)(bottomright.y - topleft.y)/2;
     Point center = new Point( (int)(topleft.x + Math.round(xextent)),
                               (int)(topleft.y + Math.round(yextent)) );
     
     //square the extents for comparison later
     xextent = Math.pow(xextent,2);
     yextent = Math.pow(yextent,2);
     
     Vector points = new Vector();
     
     // using formula for elipse: (x-h)^2/a^2 + (y-k)^2/b^2 = 1
     // where x,y is point, (h,k) is center, and a,b are x/y extent (radius)
     for( int y = topleft.y; y <= bottomright.y; y++ )
       for( int x = topleft.x; x <= bottomright.x; x++ )
       {
         double dist = Math.pow((x - center.x),2)/xextent + 
	               Math.pow((y - center.y),2)/yextent;
	 //System.out.println("(" + x + "," + y + ")..." + dist ); 
	 if( dist <= 1 )
	   points.add( new Point( x, y ) ); 
       } 
     selectedpoints = new Point[points.size()];
     for( int i = 0; i < points.size(); i++ )
         selectedpoints[i] = (Point)points.elementAt(i);
     return selectedpoints;
   }
}
