/*
 * File:  floatPoint2D.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.10  2004/07/07 23:01:13  dennis
 *  Added method: distance(pt) to calculate the distance from the current
 *  floatPoint2D object to a specified floatPoint2D object.
 *
 *  Revision 1.9  2004/03/11 23:00:11  rmikk
 *  Added the correct package name to all java files
 *
 *  Revision 1.8  2004/01/06 20:26:51  dennis
 *  Added method "magnitude()" that calculates the distance from
 *  the origin to the point.
 *
 *  Revision 1.7  2004/01/03 02:12:22  millermi
 *  - Added Constructor that takes in a java.awt.Point and converts
 *    it to a floatPoint2D.
 *  - Added method toPoint() which returns a java.awt.Point form
 *    of this floatPoint2D
 *
 *  Revision 1.6  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 */

package gov.anl.ipns.Util.Numeric;

import java.awt.Point;

/*
 *   Adapted from Sun's integer Point class in java.awt
 */

/**
 * The floatPoint2D class represents a location in a
 * two-dimensional floating point coordinate space.
 */
public class floatPoint2D implements java.io.Serializable {
    /**
     * The <i>x</i> coordinate. 
     */
    public float x;

    /**
     * The <i>y</i> coordinate. 
     */
    public float y;

    /**
     * Constructs and initializes a point at the origin 
     * of the coordinate space. 
     */
    public floatPoint2D() {
	this(0.0f, 0.0f);
    }

    /**
     * Constructs and initializes a point with the same location as
     * the specified floatPoint2D object.
     * @param       p a point.
     */
    public floatPoint2D( floatPoint2D p ) {
	this(p.x, p.y);
    }

    /**
     * Constructs and initializes a point with the same location as
     * the specified Point object. This will convert integer Point.x and
     * Point.y to float values.
     * @param       p a point.
     */
    public floatPoint2D( Point p ) {
	this((float)p.x, (float)p.y);
    }

    /**
     * Constructs and initializes a point at the specified 
     * location in the coordinate space. 
     * @param       x   the x coordinate.
     * @param       y   the y coordinate.
     */
    public floatPoint2D( float x, float y ) {
	this.x = x;
	this.y = y;
    }

    /**
     * Sets the location of the point to the specificed location.
     * @param       p  a point, the new location for this point.
     */
    public void setLocation( floatPoint2D p ) {
	this.x = p.x;
        this.y = p.y;
    }	


    /**
     * Changes the point to have the specificed location.
     * @param       x  the <i>x</i> coordinate of the new location.
     * @param       y  the <i>y</i> coordinate of the new location.
     */
    public void setLocation(int x, int y) {
	this.x = x;
	this.y = y;
    }	


    /**
     *  Calculate the magnitude of this point, treated as a vector
     *  from (0,0) to it's coordinates (x,y).
     *
     * @return sqrt(x*x+y*y)
     */
    public float magnitude()
    {
      return (float)Math.sqrt( x*x + y*y ); 
    }

  
    /**
     *  Calculate the distance from the current point
     *  to the specified point.
     *
     *  @param  point2  The point whose distance from
     *                  the current point is calculated
     *  @return The euclidean distance from the current point
     *          to point2.
     */
    public float distance( floatPoint2D point2 )
    {
      float diffx = point2.x - x;
      float diffy = point2.y - y;
      return (float)Math.sqrt( diffx*diffx + diffy*diffy );
    }


    /**
     * Returns a representation of this point as a string.
     */
    public String toString() {
	return "[x=" + x + ",y=" + y + "]";
    }	

    /**
     * Converts a floatPoint2D to a java.awt.Point.
     *
     * @return Integer form of this floatPoint2D
     */
    public Point toPoint() {
	return new Point( Math.round(x), Math.round(y) );
    }
}
