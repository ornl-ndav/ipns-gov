/*
 * File: BoxRegion.java
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
 
import DataSetTools.components.View.Cursor.SelectionJPanel;

/**
 * This class is a specific region designated by two points. A BoxRegion is
 * used to pass points selected by a rectangular region from the view
 * component to the viewer. Given the defining points of a region,
 * this class can return all of the points inside the selected region. 
 */ 
public class BoxRegion extends Region
{
  /**
   * Constructor - provides basic initialization for all subclasses
   *
   *  @param  dp - defining points of the box
   */ 
   public BoxRegion( Point[] dp )
   {
     super(dp);
   }
   
  /**
   * Get all of the points inside the region. This method assumes
   * that the input points are in (x,y) where (x = col, y = row ) form.
   * The points are entered into the array row by row.
   *
   *  @return array of points included within the region.
   */
   public Point[] getSelectedPoints()
   {
     Point topleft = new Point( definingpoints[0] );
     Point bottomright = new Point( definingpoints[1] );
     int w = bottomright.x - topleft.x + 1;
     int h = bottomright.y - topleft.y + 1;
     
     selectedpoints = new Point[w*h];
     if( selectedpoints.length > 0 )
     {
       int index = 0;
       for( int col = topleft.x; col <= bottomright.x; col++ )
         for( int row = topleft.y; row <= bottomright.y; row++ )
	 {
           selectedpoints[index] = new Point( col, row );
	   index++;
	 }
     }
     else
     {
       selectedpoints = new Point[1];
       selectedpoints[0] = topleft;
     }
     return selectedpoints;
   }
}
