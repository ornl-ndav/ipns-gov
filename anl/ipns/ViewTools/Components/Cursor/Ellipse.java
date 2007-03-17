/* 
 * file: Ellipse.java
 *
 * Copyright (C) 2003, Mike Miller
 *               2007, Dennis Mikkelson
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
 *  Revision 1.1  2007/03/17 04:24:36  dennis
 *  Class for ellipse, similar to class for circle.
 *
 */

 package gov.anl.ipns.ViewTools.Components.Cursor;
 
 import java.awt.Point;
 
/** 
 *  This class identifies an elliptical region by a center and extents in
 *  in the x and y directions.  ALL VALUES ARE INTEGER, IN PIXEL COORDINATES
 *  
 */
public class Ellipse implements java.io.Serializable
{
   private int xcenter;
   private int ycenter;
   private int dx;
   private int dy;

  /**
   *  Construct a new Ellipse centered at (x,y) with x_extent, 2*dx and
   *  y_extent, 2*dy in pixel coordinates.
   * 
   *  @param  x    X-coordinate of center
   *  @param  y    Y-coordinate of center
   *  @param  dx   half of axis in the x direction
   *  @param  dy   half of axis in the y direction
   *
   */
   public Ellipse( int x, int y, int dx, int dy ) 
   {
      xcenter = x;
      ycenter = y;
      this.dx = dx;
      this.dy = dy;
   }

     
  /**
   * Get center point of ellipse.
   *
   *  @return center point
   */
   public Point getCenter()
   {
      return new Point(xcenter,ycenter);
   }


  /** 
   *  Get the distance from the center of the ellipse to the right
   *  hand edge for the ellipse.
   *
   *  @return an int with the half-length of the horizontal axis of the
   *          ellipse.
   */ 
   public int getDx()
   {
     return dx;
   } 


  /** 
   *  Get the distance from the center of the ellipse to the top 
   *  of the ellipse.
   *
   *  @return an int with the half-length of the vertical axis of the
   *          ellipse.
   */
   public int getDy()
   {
     return dy;
   }
  

  /**
   * Get upper-left-hand corner of rectangle containing this ellipse.
   * Since java method drawOval() draws a ellipse based on its bounding
   * rectangle, this method gives that corner point. 
   *
   *  @return upper left-hand corner of rectangle bounding this circle to be
   *          used when using drawOval() method.
   */
   public Point getDrawPoint()
   {
      return new Point( (int)(xcenter - dx), (int)(ycenter - dy) );
   } 
  
  /* -----------------------------------------------------------------------
   *
   * MAIN PROGRAM FOR TEST PURPOSES
   *
   */
   public static void main(String[] args)
   {
   }       
}
