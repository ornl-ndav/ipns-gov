/*
 * @(#)floatPoint2D.java  1999/01/10  Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.2  2000/07/10 22:52:01  dennis
 *  Standard fonts for labels and borders, etc.
 *
 *  Revision 1.3  2000/06/08 19:07:37  dennis
 *  Fixed DOS text problem
 *
 *  Revision 1.2  2000/05/11 16:18:22  dennis
 *  Added RCS logging
 *
 *
 */

package DataSetTools.util;

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
     * @param       x   the x coordinate.
     * @param       y   the y coordinate.
     */
    public floatPoint2D() {
	this(0.0f, 0.0f);
    }

    /**
     * Constructs and initializes a point with the same location as
     * the specified Point object.
     * @param       p a point.
     */
    public floatPoint2D( floatPoint2D p ) {
	this(p.x, p.y);
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
     * Returns a representation of this point as a string.
     */
    public String toString() {
	return "[x=" + x + ",y=" + y + "]";
    }
}
