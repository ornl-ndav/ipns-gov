/*
 * File: Region.java
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
 *  Revision 1.3  2003/11/18 01:03:29  millermi
 *  - Now implement serializable to allow saving of state.
 *
 *  Revision 1.2  2003/10/22 20:26:09  millermi
 *  - Fixed java doc errors.
 *
 *  Revision 1.1  2003/08/11 23:40:41  millermi
 *  - Initial Version - Used to pass region info from a
 *    ViewComponent to the viewer. WCRegion is an unrelated
 *    class that passes info from the overlay to the
 *    ViewComponent.
 *
 */ 
package DataSetTools.components.View.Region;

import java.awt.Point;

/**
 * This class is a base class for all regions in the Region package. A Region is
 * used to pass selected regions (selected using the Selection Overlay) from a
 * view component to the viewer. Given the defining points of a region,
 * subclasses of this class can return all of the points inside the selected
 * region. 
 */ 
public abstract class Region implements java.io.Serializable
{
   protected Point[] definingpoints;
   protected Point[] selectedpoints;
   protected String region;
   
  /**
   * Constructor - provides basic initialization for all subclasses
   *
   *  @param  dp - defining points of the region.
   */ 
   protected Region( Point[] dp )
   {
     definingpoints = dp;
     selectedpoints = new Point[0];
   }
   
  /**
   * Get the world coordinate points used to define the geometric shape.
   *
   *  @return array of points that define the region.
   */
   public Point[] getDefiningPoints()
   {
     return definingpoints;
   } 
   
  /**
   * Get all of the points inside the region. The use of
   * Point was chosen over floatPoint2D because at this point we are dealing
   * with row/column coordinates, so rounding is acceptable. This method assumes
   * that the input points are in (x,y) where (x = col, y = row ) form.
   *
   *  @return array of points included within the region.
   */
   public Point[] getSelectedPoints()
   {
     selectedpoints = definingpoints;
     return selectedpoints;
   }
}
