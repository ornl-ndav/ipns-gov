/* 
 * file: Circle.java
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
 *  Revision 1.4  2004/03/12 01:33:22  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.3  2003/11/18 01:03:29  millermi
 *  - Now implement serializable to allow saving of state.
 *
 *  Revision 1.2  2003/10/16 05:00:04  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.1  2003/05/24 17:32:19  dennis
 *  Initial version of cursor selection. (Mike Miller)
 *
 */

 package gov.anl.ipns.ViewTools.Components.Cursor;
 
 import java.awt.Point;
 
/** 
 *  This class identifies a circular region by a center and radius.
 *  It is analogous to a rectangle.
 */
public class Circle implements java.io.Serializable
{
   private int xcenter;
   private int ycenter;
   private float radius;

  /**
   *  Construct a new Circle centered at (x,y) with radius r.
   * 
   *  @param  x
   *  @param  y
   *  @param  r - radius
   *
   */
   public Circle( int x, int y, float r ) 
   {
      xcenter = x;
      ycenter = y;
      radius = r;
   }

  /**
   *  Construct a new Circle centered at a point with radius r.
   * 
   *  @param  p - center point
   *  @param  r - radius
   *
   */
   public Circle( Point p, float r ) 
   {
      xcenter = p.x;
      ycenter = p.y;
      radius = r;
   }
     
  /**
   * Get center point of circle.
   *
   *  @return center point
   */
   public Point getCenter()
   {
      return new Point(xcenter,ycenter);
   }
  
  /**
   * Get radius of circle
   *
   *  @return radius with float precision
   */
   public float getRadius()
   {
      return radius;
   }
   
  /**
   * Get upper-left-hand corner of rectangle containing this circle.
   * Since java method drawOval() draws a circle based on its bounding
   * rectangle, this method gives that corner point. The dimension of the 
   * rectangle is just (2*r,2*r) 
   *
   *  @return upper left-hand corner of rectangle bounding this circle to be
   *          used when using drawOval() method.
   */
   public Point getDrawPoint()
   {
      return new Point( (int)(xcenter - radius), (int)(ycenter - radius) );
   } 
  
  /**
   * Check to see if this circle contains a point.
   *
   *  @param  p - point being checked
   *  @return true if point is in the circle ( including boundary )
   */
   public boolean contains( Point p)
   {
      return contains( p.x, p.y );
   }

  /**
   * Check to see if this circle contains a point specified by (x,y).
   *
   *  @param  x
   *  @param  y
   *  @return true if point is in the circle ( including boundary )
   */
   public boolean contains( int x, int y)
   {
      if( ( Math.pow( (x - xcenter) ,2) + Math.pow( (y - ycenter) ,2) ) 
          <= Math.pow(radius,2) )
         return true;
      return false;
   }

  /**
   * Change size of circle by adjusting the radius.
   *
   *  @param  r - new radius
   */
   public void reSize( float r )
   {
      radius = r;
   }  
   
  /**
   * Recenter circle at point specified. This method acts to translate or
   * move the circle to the specified point.
   *
   *  @param  p - new center point
   */
   public void reCenter( Point p )
   {
      xcenter = p.x;
      ycenter = p.y;
   } 
   
  /**
   * Recenter circle at point specified by (x,y) This method acts to translate
   * or move the circle to the specified point.
   *
   *  @param  x
   *  @param  y
   */
   public void reCenter( int x, int y )
   {
      xcenter = x;
      ycenter = y;
   }  
   
  /* -----------------------------------------------------------------------
   *
   * MAIN PROGRAM FOR TEST PURPOSES
   *
   */
   public static void main(String[] args)
   {
      Circle mycircle = new Circle( 3, 5, 2 );
      System.out.println("Circle (" + mycircle.getCenter().x + "," +
                       mycircle.getCenter().y + "," + mycircle.getRadius() +
		       ")" );
      System.out.println("Is (3,7) in the circle? " + mycircle.contains(3,7) );
      System.out.println("Is (3,8) in the circle? " + mycircle.contains(3,8) );
      mycircle.reSize(3);
      System.out.println("Circle (" + mycircle.getCenter().x + "," +
                       mycircle.getCenter().y + "," + mycircle.getRadius() +
		       ")" );      
      System.out.println("Is (3,8) in the circle? " + mycircle.contains(3,8) );
   }       
}
