/*
 * File:  Plane3D.java
 *
 * Copyright (C) 2003, Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.4  2004/03/19 17:24:26  dennis
 * Removed unused variables
 *
 * Revision 1.3  2004/03/11 23:37:10  dennis
 * Moved to MathTools.Geometry package
 *
 * Revision 1.2  2004/01/19 17:00:00  dennis
 * Minor improvement in javadocs.
 *
 * Revision 1.1  2003/05/15 19:18:21  dennis
 *  This class represents a Plane in 3D by a unit normal vector and the
 * distance of the plane from the orign.  Methods are provided to set
 * the plane information in various ways, including specifying three points,
 * and calculating the plane to be the plane that best approximates a set
 * of points in 3D.
 *
 */

package gov.anl.ipns.MathTools.Geometry;

import Jama.*;
import gov.anl.ipns.Util.Sys.*;

/**
 *  This class represents a Plane in 3D by a unit normal vector and the 
 *  distance of the plane from the orign.  Methods are provided to set
 *  the plane information in various ways, including specifying three points,
 *  and calculating the plane to be the plane that best approximates a set 
 *  of points in 3D.
 */

public class Plane3D 
{
  protected Vector3D normal;
  protected float    distance;

  /*------------------------- default constructor ----------------------*/
  /**
   *  Construct the x,y plane, containing the origin with normal in the 
   *  direction of the positive z axis, by default. 
   */
  public Plane3D()
  {
    normal = new Vector3D( 0, 0, 1 );
    distance = 0;
  }


  /*------------------------------- set ---------------------------------*/
  /**
   *  Set the plane normal and distance from the origin.  
   *  Specifically, the plane will be the set of points for which
   *  n[0] x + n[1] y + n[2] z = c.  The normal vector recorded will be 
   *  a unit vector in the direction specified, and parameter "c" will be
   *  scaled if needed to correspond to the unit normal.
   *
   *  @param  n   A normal to the plane
   *  @param  c   Constant that determines the distance of the plane from
   *              the origin.  If n is a unit vector then c IS the distance
   *              of the plane from the origin.
   *
   *  @return true if the vector n was of non-zero length, and so could
   *          be used to determine the direction of the normal to the plane.
   *          Return false otherwize.
   */
  public boolean set( Vector3D n, float c )
  {
    if ( !set(n) )
      return false;

    distance = c;

    float length = n.length();         // if a vector with length <> 1 was
    if ( length != 1 )                 // used, adjust the distance to the
    {                                  // origin
      normal.multiply( 1.0f/length );
      distance /= length;
    }

    return true;
  }


  /*------------------------------- set ---------------------------------*/
  /**
   *  Set this to be the plane determined by the specified three points.
   *  The three points must not be coincident, or collinear.  The normal
   *  vector is set to a unit vector in the direction of the crossproduct:
   *  (pt1-origin)X(pt2-origin).  The distance of the plane from the origin
   *  of the coordinate system is the dot product of the unit normal and the
   *  parameter "origin". 
   *
   *  @param  origin   A point on the plane.
   *  @param  pt1      A second point on the plane, different from the origin.
   *  @param  pt2      A third point on the plane, different from the origin 
   *                   and pt1 and not collinear with them.
   *
   *  @return  true if the points properly determine a plane.  If any of
   *  the vectors are null, or if edge1=(pt1-origin), or edge2=(pt2-origin) 
   *  is zero, or if the cross product of edge1 with edge2 is zero, then 
   *  the plane is not set and this method returns false.
   */
  public boolean set( Vector3D origin, Vector3D pt1, Vector3D pt2 )
  {
    if ( origin == null || pt1 == null || pt2 == null )
      return false;

    Vector3D edge1 = new Vector3D( pt1 );
    edge1.subtract( origin );
    if ( edge1.length() == 0 )
      return false;

    Vector3D edge2 = new Vector3D( pt2 );
    edge2.subtract( origin );
    if ( edge2.length() == 0 )
      return false;

    Vector3D n = new Vector3D();
    n.cross( edge1, edge2 );
    float length = n.length();
    if ( length == 0 )
      return false;

    n.multiply( 1/length );
    normal.set( n ); 

    distance = normal.dot( origin );

    return true;
  }


  /*------------------------------- set ---------------------------------*/
  /**
   *  Set the plane normal.  The specified normal is NOT a unit vector,
   *  a unit vector in the direction specified will be used.  The distance
   *  of the plane to the origin is not changed.
   *
   *  @param  n   A normal to the plane
   &
   *  @return true if the vector n was of non-zero length, and so could
   *          be used to determine the direction of the normal to the plane.
   *          Return false otherwize.
   */
  public boolean set( Vector3D n )
  {
    if ( n == null )
      return false;

    float length = n.length();
    if ( length == 0 )
      return false;

    normal.set(n);
    if ( length != 1 )
      normal.multiply( 1.0f/length );

    return true;
  }


  /*------------------------------- set ---------------------------------*/
  /**
   *  Set the distance of the plane from the origin.  
   *
   *  @param  distance  The distance of the plane from the origin.
   */
  public void set( float distance )
  {
    this.distance = distance;
  }

 
  /*------------------------------ getNormal ---------------------------- */
  /**
   *  Get the normal vector for this plane.
   *
   *  @return  a copy of the normal vector for this plane. 
   */
  public Vector3D getNormal()
  {
    return new Vector3D( normal );
  }


  /*------------------------------ getDistance ---------------------------- */
  /**
   *  Get the distance of this plane from the origin.
   *
   *  @return the distance of this plane from the origin..
   */
  public float getDistance()
  {
    return distance;
  }


  /*------------------------------ getDistance ---------------------------- */
  /**
   *  Get the distance of the specified point "above" or "below" this plane.
   *  If the point is on the side of the plane pointed to by the plane normal,
   *  the point is considered to be above the plane and the distance will
   *  be positive.  If the point is on the opposite side of the plane, the
   *  distance will be negative.  If the point is on the plane, the 
   *  distance will be 0.
   *
   *  @param  point  The point whose distance to the plane is calculated. 
   *
   *  @return the signed distance of the specified point above (or below)
   *          the plane.
   */
  public float getDistance( Vector3D point )
  {
    if ( point == null )
      return Float.NaN;

    return point.dot(normal) - distance;
  }


  /*--------------------------------- fit --------------------------------*/
  /**
   *  Fit a plane to the specified points.  The plane is set to the plane
   *  in three dimensions that minimizes the sum square distances from the
   *  specified points to the plane.  The least squares fit is calculated
   *  using the singular value decomposistion of the matrix whose rows are
   *  the specified points.
   *
   *  @param points  List of points to fit the plane to
   *
   *  @return The square root of the sum of the squares of the distances
   *          of the points from the plane.
   */
   public float fit( Vector3D points[] )
   {
     if ( points == null || points.length <= 2 )
     {
       System.out.println("ERROR: not enough points specified in Plane3D.fit");
       return Float.NaN;
     }

     int n_points = points.length;

     Vector3D average_pt = new Vector3D();
     average_pt.average( points );
     float average_coords[] = average_pt.get();

                                           // next find the least squares fit
                                           // to the points, shifted to the
                                           // origin, using the SVD
     double vals[][] = new double[n_points][3];
     float coords[];
     for ( int i = 0; i < n_points; i++ )
     {
       coords = points[i].get();
       for ( int j = 0; j < 3; j++ )
         vals[i][j] = coords[j] - average_coords[j];
     }

     Matrix A = new Matrix( vals );
     SingularValueDecomposition svd = A.svd();

     Matrix V = svd.getV();

     float temp[] = new float[4];               // now pull out solution from V
     double v_vals[][] = V.getArray();
     int last_col = v_vals[0].length - 1;
     for ( int i = 0; i < 3; i++ )
       temp[i] = (float)v_vals[i][last_col];
                                               // make unit normal 
     Vector3D n = new Vector3D( temp );
     n.normalize();    
     float c = n.dot( average_pt );            // calculate c based on average

     normal.set( n );
     distance = c;
                                               // now calculate the errors
     double err_sq = 0;
     float d;
     for ( int i = 0; i < n_points; i++ )
     {
       d = points[i].dot( normal ) - distance;
       err_sq += d * d;
     }

     return (float)(Math.sqrt(err_sq)); 
   }



  /*------------------------------ toString ------------------------------ */
  /**
   *  Return a string form of this vector.
   */
  public String toString()
  {
    return  normal.toString() + ", distance = " + distance; 
  }

  /**
   *  Main program providing basic functionality test.
   */
  public static void main( String args[] )
  {
    final int N_POINTS = 1000;
    Vector3D points[] = new Vector3D[N_POINTS];
    
    for ( int i = 0; i < N_POINTS; i++ )
      points[i] = new Vector3D( 10*(float)Math.random(), 
                                10*(float)Math.random(), 
                                10*(float)Math.random() );

    Plane3D plane = new Plane3D();

    ElapsedTime timer = new ElapsedTime();

    timer.reset();
    for ( int i = 0; i < 1000; i++ )
      plane.fit( points );
    System.out.println( "Plane: " + plane ); 
    double time = timer.elapsed();

    System.out.println("Time fit 1000 planes through 1000 points = " + time );

    plane.set(new Vector3D(1,0,0), new Vector3D(0,1,0), new Vector3D(0,0,1));
    System.out.println("Plane is : " + plane );

  }

}
