/*
 * File:  Vector3D_d.java
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
 * Revision 1.9  2006/11/04 20:20:07  dennis
 * Minor efficiency improvement for new non-array Vector3D.
 *
 * Revision 1.8  2004/11/04 18:12:57  dennis
 * The distance() method now takes a double precision Vector3D_d
 * as a parameter, rather than a single precision Vector3D.
 *
 * Revision 1.7  2004/07/23 13:04:42  dennis
 * Added method getCopy() to  get a copy of the array of values defining
 * this vector.  (The get() method gets a reference to the array.)
 *
 * Revision 1.6  2004/07/14 16:24:38  dennis
 * Added convenience method, distance(v), to calculate the distance from
 * the current vector to the specified vector, v.
 *
 * Revision 1.5  2004/04/02 15:15:12  dennis
 * Added constructor to make double precision Vector3D_d from
 * single precision Vector3D
 *
 * Revision 1.4  2004/03/19 17:24:27  dennis
 * Removed unused variables
 *
 * Revision 1.3  2004/03/11 22:52:55  dennis
 * Changed to gov.anl.ipns.MathTools.Geometry package
 *
 * Revision 1.2  2003/10/16 00:01:25  dennis
 * Fixed javadocs to build cleanly with jdk 1.4.2
 *
 * Revision 1.1  2003/07/14 22:22:57  dennis
 * Double precision version, ported from original
 * single precision version.
 *
 */

package gov.anl.ipns.MathTools.Geometry;

/**
 *  This class represents a double precision 3D vector using 4D homogeneous
 *  coordinates for use with the Tran3D_d class.  The use of 4D homogeneous 
 *  coordinates allows Tran3D_d to represent translation and projection 
 *  operations as well as rotation and scaling using 4x4 matrices.  Basic 
 *  operations such as length, normalize, add, subtract, dot and cross 
 *  products are provided.
 */

public class Vector3D_d 
{
  protected  double v[] = { 0, 0, 0, 1 };

  /*------------------------- default constructor ----------------------*/
  /**
   *  Construct the 4 dimensional homogeneous point corresponding to 
   *  { 0, 0, 0 }
   */
  public Vector3D_d()
  {
  }

  /*------------------------ copy constructor ---------------------------*/
  /**
   *  Construct a copy of the given vector.
   *
   *  @param  vector  the vector to copy. 
   */
  public Vector3D_d( Vector3D_d vector )
  {
    set( vector );
  }


  /*------------- copy constructor from single precision version -----------*/
  /**
   *  Construct a copy of the given single precision vector.
   *
   *  @param  vector  the single precision vector to copy. 
   */
  public Vector3D_d( Vector3D vector )
  {
    float temp[] = vector.get();
    for ( int i = 0; i < 4; i++ )
      v[i] = temp[i];
  }


  /*--------------------------- constructor ----------------------------*/
  /**
   *  Construct the 4 dimensional homogeneous point corresponding to the
   *  three coordinates provided. 
   *
   *  @param x    The x coordinate of the point
   *  @param y    The y coordinate of the point
   *  @param z    The z coordinate of the point
   */
  public Vector3D_d(double x, double y, double z )
  {
    set( x, y, z );
  }

  /*--------------------------- constructor ----------------------------*/
  /**
   *  Construct the 4 dimensional homogeneous point corresponding to the
   *  first three entries in the array provided.  If there a not enough
   *  entries, 0's will be used for values not specified. 
   *
   *  @param  arr  Array whose first three values specify the x,y,z values
   *               for the new Vector3D_d object. 
   */
  public Vector3D_d( double arr[] )
  {
    if ( arr != null )
    {
      int i = 0;
      while ( i < 3 && i < arr.length )
      {
        v[i] = arr[i];
        i++;
      }
    }
  }

  /*--------------------------- constructor ----------------------------*/
  /**
   *  Construct the 4 dimensional homogeneous point corresponding to the
   *  Position3D object 
   *
   *  @param position  The Position3D object that provides the x,y,z 
   *                   values to use for this Vector3D_d object.
   */
  public Vector3D_d( Position3D_d position )
  {
    if ( position != null )
    {
      double pos[] = position.getCartesianCoords();

      for ( int i = 0; i < 3; i++ )
        v[i] = pos[i];
    }
  }


  /* ----------------------------- equals ------------------------------- */
  /**
   *  Check whether or not the current vector has exactly the same entries as
   *  the specified vector.
   *
   *  @param  vec   The vector to compare with the current vector.
   * 
   *  @return Returns true if the current vector has the same components as
   *  as the specified vector.
   */
  public boolean equals( Vector3D_d vec )
  {
    for ( int i = 0; i < 3; i++ )
      if ( v[i] != vec.v[i] )
        return false;

     return true;
  }


  /*------------------------------- set ---------------------------------*/
  /** 
   *  Set the value for this vector to the 4 dimensional homogeneous point 
   *  corresponding to the three  coordinates provided
   *
   *  @param x    The x coordinate of the point
   *  @param y    The y coordinate of the point
   *  @param z    The z coordinate of the point
   */
  public void set( double x, double y, double z )
  {
     v[0] = x;
     v[1] = y;
     v[2] = z;
     v[3] = 1.0;
  }


  /*------------------------------- set ---------------------------------*/
  /**
   *  Set the value for this vector to the same value as the given vector.
   *
   *  @param vector  The vector whose values are to be copied into the  
   *                 current vector.  
   */
  public void set( Vector3D_d vector )
  {
     v[0] = vector.v[0];
     v[1] = vector.v[1];
     v[2] = vector.v[2];
     v[3] = vector.v[3];
  }


  /*------------------------------- set ---------------------------------*/
  /**
   *  Set the value for this vector using values from the array.  If more
   *  than three values are given, the extra values are ignored.  If less
   *  than three values are given, the unspecified values will be set to 
   *  zero.
   *
   *  @param arr  Array containing values to use for the x,y,z components
   *              of this vector. 
   */
  public void set( double arr[] )
  {
     if ( arr == null || arr.length == 0 )
     {
       v[0] = 0;
       v[1] = 0;
       v[2] = 0;
       v[3] = 1;
     }
     else
     {
       int max = Math.min( arr.length, 3 );
       for ( int i = 0; i < max; i++ )
         v[i] = arr[i];
       for ( int i = max; i < 3; i++ )
         v[i] = 0;
       v[3] = 1;
     }
  }


  /*------------------------------- get ---------------------------------*/
  /**
   *  Get a reference to the 4 dimensional array containing the x, y, z, h
   *  coordinates for this vector.
   *
   *  return reference to the 4D array containing the coordinates for this
   *         vector.
   */
  public double[] get()
  {
    return v;
  }


  /*---------------------------- getCopy ---------------------------------*/
  /**
   *  Get a copy of the 4 dimensional array containing the x, y, z, h
   *  coordinates for this vector.
   *
   *  return a copy of the 4D array containing the coordinates for this
   *         vector.
   */
  public double[] getCopy()
  {
    double copy[] = new double[4];
    copy[0] = v[0];
    copy[1] = v[1];
    copy[2] = v[2];
    copy[3] = v[3];
    return copy;
  }


  /*------------------------------- add ---------------------------------*/
  /**
   *  Set the value for this vector to the sum of it's current value plus 
   *  the specified vector.  It is assumed that the fourth component of 
   *  both vectors are 1. 
   *
   *  @param  vector  the vector to add to the current vector.
   */
  public void add( Vector3D_d vector )
  {
     v[0] += vector.v[0];
     v[1] += vector.v[1];
     v[2] += vector.v[2];
  }

  /*----------------------------- subtract -------------------------------*/
  /**
   *  Set the value for this vector to the differenct of it's current value
   *  minus the specified vector.  It is assumed that the fourth component of
   *  both vectors are 1.
   *
   *  @param  vector  the vector to subtract from the current vector.
   */
  public void subtract( Vector3D_d vector )
  {
     v[0] -= vector.v[0];
     v[1] -= vector.v[1];
     v[2] -= vector.v[2];
  }

  /*------------------------------- add ---------------------------------*/
  /**
   *  Add a scalar to the first three components of this vector.
   *
   *  @param  scalar  the scalar value to add the components of the 
   *                  current vector.
   */
  public void add( double scalar )
  {
     v[0] += scalar;
     v[1] += scalar;
     v[2] += scalar;
  }

  /*----------------------------- multiply -------------------------------*/
  /**
   *  Multiply a scalar times the first three components of this vector.
   *
   *  @param  scalar  the scalar value to multiply times the components of the 
   *                  current vector.
   */
  public void multiply( double scalar )
  {
     v[0] *= scalar;
     v[1] *= scalar;
     v[2] *= scalar;
  }

  /*------------------------------- average -------------------------------*/
  /**
   *  Set the vector to the average of the vectors in the given list of
   *  vectors.
   *
   *  @param  list  the list of vectors to be averaged.
   */
  public void average( Vector3D_d list[] )
  {
     if ( list == null || list.length == 0 )
     {
       System.out.println("WARNING: average of empty list in Vector3D_d.average");
       set( 0, 0, 0 );
       return;
     }

     double x = 0.0,
            y = 0.0,
            z = 0.0;

     for ( int i = 0; i < list.length; i++ )
     {
       x += list[i].v[0];
       y += list[i].v[1];
       z += list[i].v[2];
     } 
     
     v[0] = x/list.length;
     v[1] = y/list.length;
     v[2] = z/list.length;
  }

  /*--------------------------- standardize ------------------------------*/
  /** 
   *  Divide the first three coordinates of this point by the fourth 
   *  component and set the fourth component to 1.
   */
  public void standardize()
  {
     v[0] /= v[3]; 
     v[1] /= v[3]; 
     v[2] /= v[3]; 
     v[3] = 1.0;
  }

  /*--------------------------- length ------------------------------*/
  /** 
   *  Calculate the length of the 3D vector given by the first three 
   *  coordinates of this homogeneous point.  It is assumed that the 
   *  fourth component is 1.
   *
   *  @return   the length of this 3D vector.
   */
  public double length()
  {
    return Math.sqrt( v[0]*v[0] + v[1]*v[1] + v[2]*v[2] );
  }

  /*--------------------------- distance ------------------------------*/
  /** 
   *  Calculate the distance from of this 3D vector to the specified
   *  3D vector.
   *
   *  @param    vec   The other vector 
   *
   *  @return   the distance from this 3D vector to the specified vector.
   */
  public double distance( Vector3D_d vec )
  {
    double dx = v[0] - vec.v[0];
    double dy = v[1] - vec.v[1];
    double dz = v[2] - vec.v[2];
    return Math.sqrt( dx * dx + dy * dy + dz * dz );
  }

  /*--------------------------- normalize ------------------------------*/
  /** 
   *  Divide the first three coordinates of this point by the length of
   *  this vector.  It is assumed that the fouth component is 1.  If the 
   *  length of the vector is zero, this function has no effect.
   */
  public void normalize()
  {
     double len = length();
     if ( len != 0.0 )
     {
       v[0] /= len;
       v[1] /= len;
       v[2] /= len;
       v[3]  = 1.0;
     }
  }

  /*------------------------------- dot ---------------------------------*/
  /**
   *  Calculate the dot product of the current vector with the given vector.
   *  It is assumed that the 4th component of both vectors is 1.
   *
   *  @param  vector  the vector to "dot" with the current vector
   *
   *  @return  the dot product:  (this "dot" vector)
   */
  public double dot( Vector3D_d vector )
  {
     return ( v[0] * vector.v[0] +  v[1] * vector.v[1] +  v[2] * vector.v[2] );
  }

  /*------------------------------- cross ---------------------------------*/
  /**
   *  Set the current vector to the cross product of the two given vectors.
   *  This is set to ( v1 "cross" v2 ).  It is assumed that the 4th component 
   *  of vectors v1 and v2 is 1.
   *
   *  @param  v1  the first vector factor in the cross product
   *  @param  v2  the second vector factor in the cross product
   *
   */
  public void cross( Vector3D_d v1, Vector3D_d v2 )
  {                                              // use temporaries for v0...v2
                                                 // in case v1 or v2 == this
     double t0 =  v1.v[1] * v2.v[2]  -  v1.v[2] * v2.v[1];
     double t1 = -v1.v[0] * v2.v[2]  +  v1.v[2] * v2.v[0];
     double t2 =  v1.v[0] * v2.v[1]  -  v1.v[1] * v2.v[0];

     v[0] = t0;
     v[1] = t1;
     v[2] = t2;
     v[3] = 1.0;
  }

  /*------------------------------ toString ------------------------------ */
  /**
   *  Return a string form of this vector.
   */
  public String toString()
  {
    return  "{ " + v[0] + ", " + v[1] + ", " + v[2] + " : " + v[3] + " }"; 
  }

  /* ---------------------------- main ----------------------------------- */
  /**
   *  Main program for testing purposes only.
   */
  public static void main ( String args[] )
  {
    Vector3D_d  v1 = new Vector3D_d( 1, 2, 3);
    Vector3D_d  v2 = new Vector3D_d();
    Vector3D_d  v3 = new Vector3D_d();
    
    v2.set( 2, 3, 4 );

    System.out.println("Vectors are: " + v1 + " and " + v2 + " and " + v3 );
    System.out.println("Dot product of v1 and v2 is : " + v1.dot( v2 ) );

    v3.cross( v1, v2 );
    System.out.println( "Cross product of v1 and v2 is : " + v3 );

    System.out.println( "Length of  v3 is " + v3.length() );
    v3.normalize();
    System.out.println( "Normalized v3 is " + v3 );

    Vector3D_d v = new Vector3D_d( v1 );

    v.add( v2 );
    System.out.println( "Sum of v1 and v2  " + v  );

    v = new Vector3D_d( v1 );
    v.subtract( v2 );
    System.out.println( "Difference of v1 and v2  " + v  );

    v.standardize();
    System.out.println( "Standardized: " + v  );
  }

}
