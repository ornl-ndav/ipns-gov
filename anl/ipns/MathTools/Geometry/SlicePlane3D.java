/*
 * File:  SlicePlane3D.java
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 * Revision 1.5  2004/03/11 23:33:26  dennis
 * Moved to package MathTools/Geometry
 *
 * Revision 1.4  2004/03/03 23:15:22  dennis
 * Added "convenience" constructor to construct a slice plane
 * given specific u,v,n local coordinate vectors.
 *
 * Revision 1.3  2004/01/26 20:41:00  dennis
 * The setOrigin() method now checks for the error of passing in
 * a null vector and returns a boolean value indicating whether
 * or not the setOrigin() method succeeded.
 *
 * Revision 1.2  2004/01/24 23:46:37  dennis
 * Added copy constructor.
 *
 * Revision 1.1  2004/01/19 23:55:17  dennis
 * Initial version of class to represent the plane of a slice through
 * reciprocal space.
 *
 */

package gov.anl.ipns.MathTools.Geometry;

/**
 *  This class represents a plane in 3D as a point that is the origin of 
 *  a coordinate system on the plane and two orthogonal basis vectors 
 *  that serve as the coordinate axes.  Methods are provided to set
 *  the plane information in various ways, including specifying three points,
 *  specifying the plane normal and the first basis vector, etc.
 */

public class SlicePlane3D 
{
  protected Vector3D origin;
  protected Vector3D u,         // the "first" basis vector 
                     v;         // the "second" basis vector, orthogonal to u

  /*------------------------- default constructor ----------------------*/
  /**
   *  Construct the x,y plane, containing the origin with normal in the 
   *  direction of the positive z axis, by default. 
   */
  public SlicePlane3D()
  {
    origin = new Vector3D( 0, 0, 0 );
    u      = new Vector3D( 1, 0, 0 );
    v      = new Vector3D( 0, 1, 0 );
  }


  /*--------------------------- copy constructor -----------------------*/
  /**
   *  Construct a new SlicePlane3D, using the values from the specified plane.
   *
   *  @param old_plane  The plane whose values are to be used in 
   *                    constructing this new plane.
   */
  public SlicePlane3D( SlicePlane3D old_plane )
  {
    origin = new Vector3D( old_plane.origin );
    u      = new Vector3D( old_plane.u      );
    v      = new Vector3D( old_plane.v      );
  }


  /*-------------------- constructor from origin, u, v ---------------------*/
  /**
   *  Construct a new SlicePlane3D, using the specified origin, and local
   *  basis vectors u and v IF POSSIBLE.  The default constructor is used,
   *  after which the origin and u and v values are set.  If u and v are
   *  invalid, the default values u = (1,0,0) and v = (0,1,0). Vector v will be
   *  adjusted to be orthogonal to u, if it is not already.
   *
   *  @param  origin  The vector to use as the origin for this 
   *                  slice plane.
   *  @param  u       non-zero vector that specifies the direction of the
   *                  first coordinate axis on the plane. 
   *  @param  v       non-zero vector that will be used to determine the 
   *                  direction of the second coordinate axis on the plane,
   *                  adjusted so that v is perpendicular to u.
   */
  public SlicePlane3D( Vector3D origin, Vector3D u, Vector3D v )
  {
    this();
    setOrigin( origin );

    if ( !setU_and_V( u, v ) )
    {
      u = new Vector3D( 1, 0, 0 );
      v = new Vector3D( 0, 1, 0 );
    }
  }

  
  /*---------------------------- getOrigin ------------------------------*/
  /**
   *  Get a copy of the origin vector for this plane.
   *
   *  @return  a new Vector3D with the same components as the origin 
   *           for this plane.
   */
  public Vector3D getOrigin()  
  {
    return new Vector3D( origin );
  }


  /*---------------------------- setOrigin ------------------------------*/
  /**
   *  Set the origin vector for this plane, keeping the same u and v vectors.
   *
   *  @param   new_origin  The new vector to use as the origin for this 
   *                       slice plane.
   *
   *  @return  true if the new_origin parameter was valid, false otherwise.
   */
  public boolean setOrigin( Vector3D new_origin )
  {
    if ( new_origin == null ) 
      return false;

    origin = new Vector3D( new_origin );
    return true;
  }


  /*------------------------------ getU ---------------------------------*/
  /**
   *  Get a copy of the first basis vector, (orthogonal to V),
   *  for the coordinate system on this plane.
   *
   *  @return  a new Vector3D with the same components as the u vector 
   *           for this plane.
   */
  public Vector3D getU()  
  {
    return new Vector3D( u );
  }

  /*------------------------------ getV ---------------------------------*/
  /**
   *  Get a copy of the second basis vector, (orthogonal to U),
   *  for the coordinate system on this plane.
   *
   *  @return  a new Vector3D with the same components as the v vector  
   *           for this plane.
   */
  public Vector3D getV()     
  {
    return new Vector3D( v );
  }

  /*---------------------------- setU_and_V -----------------------------*/
  /**
   *  Set the u and v vectors for this plane, keeping the same origin.
   *
   *  @param u   non-zero vector that specifies the direction of the
   *             first coordinate axis on the plane. 
   *  @param v   non-zero vector that will be used to determine the 
   *             direction of the second coordinate axis on the plane,
   *             adjusted so that v is perpendicular to u.
   *
   *  @return  false if the u, v vectors are null, zero, or collinear, 
   *            and return true otherwise.
   *
   */
  public boolean setU_and_V( Vector3D u, Vector3D v )
  {
    if ( u == null || v == null )
      return false;

    if ( u.length() == 0 || v.length() == 0 )
      return false;

    Vector3D n = new Vector3D();
    n.cross( u, v );
    if ( n.length() == 0 )
      return false;

    // everything is ok at this point, so we can proceed
    this.u = new Vector3D( u ); 
    this.u.normalize();

    n.normalize();
    this.v.cross( n, this.u ); 
    return true;
  }


  /*------------------------- setNormal_and_U ------------------------------*/
  /**
   *  Set this plane, keeping the same origin, by calculating a new vector
   *  u, perpendicular to the specified normal, in the plane determined by the 
   *  specified normal and u vector.  The v vector is then set to the 
   *  cross product of n and u.
   *
   *  @param n   The normal vector for this plane.  This takes precedence 
   *             since the u vector is adjusted to perpendiular to this
   *             normal.
   *
   *  @param u   non-zero vector used to calculate the direction of the
   *             first coordinate axis on the plane.  The actual u vector 
   *             used will lie in the same plane as the specified normal
   *             and u vectors, but will be adjusted to be perpendicular to
   *             the normal vector.
   *
   *  @return  false if the n or u vectors are null, zero, or collinear,
   *           and return true otherwise.
   *
   */
  public boolean setNormal_and_U( Vector3D n, Vector3D u )
  {
    if ( n == null || u == null )
      return false;

    if ( n.length() == 0 || u.length() == 0 )
      return false;

    Vector3D temp = new Vector3D();
    temp.cross( n, u );
    if ( temp.length() == 0 )
      return false;

    // everything is ok at this point, so we can proceed to make u 
    // perpendicular to the specified normal.

    n = new Vector3D(n);            // get a local copy of n and normalize it
    n.normalize();

    float n_comp = u.dot(n);        // make u perpendicular to n
    this.u = new Vector3D( u );
    temp = new Vector3D( n );
    temp.multiply( n_comp );
    this.u.subtract( temp );
    this.u.normalize();

    this.v.cross( n, this.u );
    return true;
  }

  /*--------------------------- setNormal ------------------------------*/
  /**
   *  Set this plane to be perpendicular to the specified normal vector,
   *  keeping the same origin and calculating u and v vectors 
   *  perpendicular to the normal.  The u and v vectors are determined as
   *  follows.  First, the coordinate axes are numbered 0, 1 and 2.  Next
   *  the index of the axis most nearly in the direction of the specified
   *  normal is found, as determined by the absolute value of the dot product
   *  of the normal with the coordinate axes.  Suppose that the absolute
   *  value of the dot product with axis k is the maximum.  If the dot
   *  product is positive, then axis (k+1 mod 3) will be used to determine
   *  the u direction by calling the setNormal_and_U() method.  If the dot
   *  product is negative then axis (k+2 mod 3) will be used to determine
   *  the u direction by calling the setNormal_and_U() method.
   *
   *  @param n   The normal vector for this plane.  The u and v vectors
   *             will be calculated.
   *
   *  @return  false if the n vector is null or zero.
   */
  public boolean setNormal( Vector3D n )
  {
    if ( n == null )
      return false;

    if ( n.length() == 0 )
      return false;

    Vector3D axes[] = new Vector3D[3];
    axes[0] = new Vector3D( 1, 0, 0 );
    axes[1] = new Vector3D( 0, 1, 0 );
    axes[2] = new Vector3D( 0, 0, 1 );

    float max_abs_dot = Math.abs( n.dot( axes[0] ));
    int   max_index = 0;

    for ( int i = 1; i < 3; i++ )
    {
      float abs_dot = Math.abs( n.dot( axes[i] ) );
      if ( abs_dot > max_abs_dot )
      {
         max_abs_dot = abs_dot;
         max_index = i; 
      }
    }

    float  dot = n.dot( axes[ max_index ] );
    if ( dot > 0 ) 
      return setNormal_and_U( n, axes[ (max_index+1) % 3 ] );
    else
      return setNormal_and_U( n, axes[ (max_index+2) % 3 ] );
  }


  /*------------------------------ getNormal ---------------------------- */
  /**
   *  Get the normal vector for this plane.
   *
   *  @return  a copy of the normal vector for this plane.
   */
  public Vector3D getNormal()
  {
    Vector3D normal = new Vector3D();
    normal.cross( u, v );
    normal.normalize();
    return normal;
  }


  /*------------------------------ setPlane -------------------------------*/
  /**
   *  Set this to be the plane determined by the specified three points.
   *  The three points must not be coincident, or collinear.  The u vector
   *  for this plane is set to a unit vector in the direction of 
   *  pt1-origin.  The plane is assumed to be normal to the cross product of
   *  (pt1-origin)X(pt2-origin).  The v vector for this plane is set to a 
   *  unit vector in the direction of the cross product of the plane normal
   *  with the u vector.
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
  public boolean setPlane( Vector3D origin, Vector3D pt1, Vector3D pt2 )
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

    if ( !this.setU_and_V( edge1, edge2 ) )
      return false;
     
    this.origin = new Vector3D( origin );

    return true;
  }



  /*------------------------------ toString ------------------------------ */
  /**
   *  Return a string form of this plane.
   */
  public String toString()
  {
    return  "Origin = " + origin + "\n" +
            "u = " + u + "\n" +
            "v = " + v;
  }

  /**
   *  Main program providing basic functionality test.
   */
  public static void main( String args[] )
  {
    System.out.println("====================================================" );
    SlicePlane3D plane1 = new SlicePlane3D();
    System.out.println("Default Plane is");
    System.out.println("" + plane1 );
    System.out.println("getOrigin = " + plane1.getOrigin() );
    System.out.println("getU = " + plane1.getU() );
    System.out.println("getV = " + plane1.getV() );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setOrigin( new Vector3D(1,2,3) );
    System.out.println("After setting origin to 1,2,3");
    System.out.println("" + plane1 );

    System.out.println("====================================================" );
    plane1.setU_and_V( new Vector3D( -1, 1,  2 ),
                     new Vector3D( -1, 1, -1 ) );
    System.out.println("After setting u, v to u = -1,1,2 v = -1,1,-1");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setPlane( new Vector3D( 0, 0, 1 ),
                     new Vector3D( 1, 0, 1 ),
                     new Vector3D( 0, 1, 1 ) );
    System.out.println("After setting plane = 0,0,1, u = 1,0,1 v = 0,1,1");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setPlane( new Vector3D( 1, 1, 1 ),
                     new Vector3D(-1, 1, 2 ),
                     new Vector3D(-1, 1,-1 ) );
    System.out.println("After setting plane = 1,1,1, p1 = -1,1,2 p2 = -1,1,-1");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );


    System.out.println("====================================================" );
    plane1.setNormal_and_U( new Vector3D( 0, -1, 0 ),
                            new Vector3D(-2, 0, 1 ) );
    System.out.println("After setting normal = 0,-1,0, u = -2,0,1");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal_and_U( new Vector3D( 0, -1, 0 ),
                            new Vector3D(-2, -1, 1 ) );
    System.out.println("After setting normal = 0,-1,0, u = -2,-1,1");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal( new Vector3D( 1, 0, 0 ) );
    System.out.println("After setting normal = 1,0,0 ");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal( new Vector3D( 0, 1, 0 ) );
    System.out.println("After setting normal = 0,1,0 ");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal( new Vector3D( 0, 0, 1 ) );
    System.out.println("After setting normal = 0,0,1 ");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal( new Vector3D( -1, 0, 0 ) );
    System.out.println("After setting normal = -1,0,0 ");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal( new Vector3D( 0, -1, 0 ) );
    System.out.println("After setting normal = 0,-1,0 ");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal( new Vector3D( 0, 0, -1 ) );
    System.out.println("After setting normal = 0,0,-1 ");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal( new Vector3D( 1, 1, 0 ) );
    System.out.println("After setting normal = 1,1,0 ");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal( new Vector3D( -1, -1, 0 ) );
    System.out.println("After setting normal = -1,-1,0 ");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );

    System.out.println("====================================================" );
    plane1.setNormal( new Vector3D( 1, 2, 3 ) );
    System.out.println("After setting normal = 1,2,3 ");
    System.out.println("" + plane1 );
    System.out.println("getNormal = " + plane1.getNormal() );
  }

}
