/*
 * File:  Vector3D.java
 *
 * Copyright (C) 2001-2011, Dennis Mikkelson
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
 * $Log$
 * Revision 1.16  2006/11/12 05:31:53  dennis
 * Switched 3D vector representation to use separate fields for
 * x,y,z,w, instead of using an array to hold the four values.
 * This saves both time and space for "most" vector operations
 * since only one object (the vector) is created, rather than
 * two (the vector and the array in the vector).  Applications
 * that just frequently get all of the values out of the vector
 * may not see any improvment.
 *
 * Revision 1.15  2006/07/25 19:46:36  dennis
 * Fixed variable name in javadoc comment.
 *
 * Revision 1.14  2006/07/20 13:36:43  dennis
 * Added method cross(v) to take cross product of the current
 * vector with another vector, v.
 * Added method linear_combination() to set this vector to
 * a linear combination of a specified list of vectors, using
 * a specified list of coefficients.
 *
 * Revision 1.13  2004/07/23 13:04:42  dennis
 * Added method getCopy() to  get a copy of the array of values defining
 * this vector.  (The get() method gets a reference to the array.)
 *
 * Revision 1.12  2004/07/14 16:24:38  dennis
 * Added convenience method, distance(v), to calculate the distance from
 * the current vector to the specified vector, v.
 *
 * Revision 1.11  2004/04/02 15:14:40  dennis
 * Added constructor to make single precision Vector3D from
 * double precision Vector3D_d.
 *
 * Revision 1.10  2004/03/19 17:24:27  dennis
 * Removed unused variables
 *
 * Revision 1.9  2004/03/11 22:52:55  dennis
 * Changed to gov.anl.ipns.MathTools.Geometry package
 *
 * Revision 1.8  2003/10/16 00:01:25  dennis
 * Fixed javadocs to build cleanly with jdk 1.4.2
 *
 * Revision 1.7  2003/06/03 16:52:39  dennis
 * Added method to set value from an array of floats.
 *
 * Revision 1.6  2003/04/14 18:54:21  dennis
 * Added method .average(v[]) to set the current vector to the average
 * of a list of vectors.
 *
 * Revision 1.5  2003/02/05 21:06:13  dennis
 * Added constructor to make a Vector3D object from a Position3D object.
 *
 * Revision 1.4  2003/02/04 19:13:13  dennis
 * Added constructor that takes an array of floats.
 *
 * Revision 1.3  2002/11/27 23:15:47  pfpeterson
 * standardized header
 *
 * Revision 1.2  2002/10/29 23:43:35  dennis
 * Added methods add() and multiply() to add and multiply by a scalar.
 *
 */

package  gov.anl.ipns.MathTools.Geometry;

import java.io.Serializable;

/**
 *  This class represents a 3D vector using 4D homogeneous coordinates for
 *  use with the Tran3D class.  The use of 4D homogeneous coordinates allows
 *  Tran3D to represent translation and projection operations as well as
 *  rotation and scaling using 4x4 matrices.  Basic operations such as
 *  length, normalize, add, subtract, dot and cross products are provided.
 */

public class Vector3D implements Serializable
{
  public  static final long serialVersionUID = 1L;

  protected float x = 0f,
                  y = 0f,
                  z = 0f,
                  w = 1f;


  /*------------------------- default constructor ----------------------*/
  /**
   *  Construct the 4 dimensional homogeneous point corresponding to 
   *  { 0, 0, 0 }
   */
  public Vector3D()
  {
     // Default constructor starts with the zero vector.
  }

  /*------------------------ copy constructor ---------------------------*/
  /**
   *  Construct a copy of the given vector.
   *
   *  @param  vector  the vector to copy. 
   */
  public Vector3D( Vector3D vector )
  {
    set( vector );
  }


  /*------------- copy constructor from double precision version -----------*/
  /**
   *  Construct a copy of the given double precision vector.
   *
   *  @param  vector  the double precision vector to copy. 
   */
  public Vector3D( Vector3D_d vector )
  {
    double temp[] = vector.get();
    x = (float)temp[0];
    y = (float)temp[1];
    z = (float)temp[2];
    w = (float)temp[3];
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
  public Vector3D(float x, float y, float z )
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
   *               for the new Vector3D object. 
   */
  public Vector3D( float arr[] )
  {
    if ( arr != null )
    {
       if ( arr.length > 2 )
       {
         x = arr[0];
         y = arr[1];
         z = arr[2];
       }
       w = 1f; 
    }
  }

  /*--------------------------- constructor ----------------------------*/
  /**
   *  Construct the 4 dimensional homogeneous point corresponding to the
   *  Position3D object 
   *
   *  @param position  The Position3D object that provides the x,y,z 
   *                   values to use for this Vector3D object.
   */
  public Vector3D( Position3D position )
  {
    if ( position != null )
    {
      float pos[] = position.getCartesianCoords();
      x = pos[0];
      y = pos[1];
      z = pos[2];
    }
  }


  /* ----------------------------- equals ------------------------------- */
  /**
   *  Check whether or not the current vector has exactly the same entries as
   *  the specified vector.
   *
   *  @param  vec   The vector to compare with the current vector.
   * 
   *  @return Returns false if at least one of the x,y,z or w components 
   *  don't match, and returns true otherwise.
   */
  public boolean equals( Vector3D vec )
  {
     if ( x != vec.x || y != vec.y || z != vec.z || w != vec.w )
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
  public void set( float x, float y, float z )
  {
     this.x = x;
     this.y = y;
     this.z = z;
     this.w = 1f;
  }


  /*------------------------------- set ---------------------------------*/
  /** 
   *  Set the four components of this vector to the 4 specified values.
   *
   *  @param x    The x coordinate of the point
   *  @param y    The y coordinate of the point
   *  @param z    The z coordinate of the point
   *  @param w    The w coordinate of the point
   */
  public void set( float x, float y, float z, float w )
  {
     this.x = x;
     this.y = y;
     this.z = z;
     this.w = w;
  }


  /*------------------------------- set ---------------------------------*/
  /**
   *  Set the value for this vector to the same value as the given vector.
   *
   *  @param vector  The vector whose values are to be copied into the
   *                 current vector.
   */
  public void set( Vector3D vector )
  {
     x = vector.x;
     y = vector.y;
     z = vector.z;
     w = vector.w;
  }


  /*------------------------------- set ---------------------------------*/
  /**
   *  Set the value for this vector using values from the array.
   *  If the array has less than three values, the x, y, z values will be set 
   *  to zero and the fourth component, w, will be set to 1.  If
   *  three values are given, the first three values will be used for x, y 
   *  and z and the fourth component, w, will be set to 1.  If four or more 
   *  values are given the first four values in the array will be used for
   *  x, y, z and w, respectively.
   *
   *  @param arr  Array containing values to use for the x, y, z and possibly
   *              w components of this vector. 
   */
  public void set( float arr[] )
  {
     if ( arr == null || arr.length < 3 )
     {
       x = 0f;
       y = 0f;
       z = 0f;
       w = 1f;
     }
     else if ( arr.length == 3 )
     {
       x = arr[0];
       y = arr[1];
       z = arr[2];
       w = 1f;
     }
     else
     {
       x = arr[0];
       y = arr[1];
       z = arr[2];
       w = arr[3];
     }
  }


  /*------------------------------- get ---------------------------------*/
  /**
   *  Get a 4 dimensional array containing a copy of the x, y, z, h
   *  coordinates for this vector.
   *
   *  return a 4D array containing copies of the coordinates for this
   *         vector.
   */
  public float[] get()
  {
    float[] v = {x,y,z,w};
    return v;
  }


  /*------------------------------- getX --------------------------------*/
  /**
   *  Get the X coordinate of this vector.
   *
   *  return the X coordinate.
   */
  public float getX()
  {
    return x;
  }


  /*------------------------------- getY --------------------------------*/
  /**
   *  Get the Y coordinate of this vector.
   *
   *  return the Y coordinate.
   */
  public float getY()
  {
    return y;
  }


  /*------------------------------- getZ --------------------------------*/
  /**
   *  Get the Z coordinate of this vector.
   *
   *  return the Z coordinate.
   */
  public float getZ()
  {
    return z;
  }


  /*------------------------------- getW --------------------------------*/
  /**
   *  Get the homogeneous W coordinate of this vector.  This will usually
   *  return 1 unless the vector has justbeen multiplied by a projection
   *  matrix and not standardized.
   *
   *  return the W coordinate.
   */
  public float getW()
  {
    return w;
  }


  /*------------------------------- add ---------------------------------*/
  /**
   *  Set the value for this vector to the sum of it's current value plus 
   *  the specified vector.  It is assumed that the fourth component of 
   *  both vectors are 1. 
   *
   *  @param  vector  the vector to add to the current vector.
   */
  public void add( Vector3D vector )
  {
     x += vector.x;
     y += vector.y;
     z += vector.z;
  }

  /*----------------------------- subtract -------------------------------*/
  /**
   *  Set the value for this vector to the differenct of it's current value
   *  minus the specified vector.  It is assumed that the fourth component of
   *  both vectors are 1.
   *
   *  @param  vector  the vector to subtract from the current vector.
   */
  public void subtract( Vector3D vector )
  {
     x -= vector.x;
     y -= vector.y;
     z -= vector.z;
  }

  /*------------------------------- add ---------------------------------*/
  /**
   *  Add a scalar to the first three components of this vector.
   *
   *  @param  scalar  the scalar value to add the components of the 
   *                  current vector.
   */
  public void add( float scalar )
  {
     x += scalar;
     y += scalar;
     z += scalar;
  }

  /*----------------------------- multiply -------------------------------*/
  /**
   *  Multiply a scalar times the first three components of this vector.
   *
   *  @param  scalar  the scalar value to multiply times the components of the 
   *                  current vector.
   */
  public void multiply( float scalar )
  {
     x *= scalar;
     y *= scalar;
     z *= scalar;
  }

  /*------------------------------- average -------------------------------*/
  /**
   *  Set this vector to the average of the vectors in the given list of
   *  vectors.
   *
   *  @param  list  the list of vectors to be averaged.
   */
  public void average( Vector3D list[] )
  {
     if ( list == null || list.length == 0 )
     {
       System.out.println("WARNING: average of empty list in Vector3D.average");
       set( 0, 0, 0 );
       return;
     }

     double x_tot = 0.0,
            y_tot = 0.0,
            z_tot = 0.0;

     for ( int i = 0; i < list.length; i++ )
     {
       x_tot += list[i].x;
       y_tot += list[i].y;
       z_tot += list[i].z;
     }

     x = (float)(x_tot/list.length);
     y = (float)(y_tot/list.length);
     z = (float)(z_tot/list.length);
  }

  /*------------------------ linear_combination -------------------------*/
  /**
   *  Set this vector to the linear combination c0*V0 + c1*V1 +...+ cn*Vn
   *  of the specified coefficients c0,c1,...,cn and the specified vectors 
   *  V1,V2,...,Vn.
   *
   *  @param  coeff    The list of coeficients, c0,c1,...,cn.  The array
   *                   of coeficients must be of the same length as the
   *                   the array of vectors.
   *  @param  vectors  The list of vectors, V0,V1,...,Vn, to be combined.
   *                   The array of vectors must be of the same length as
   *                   the array of coefficients.
   */
  public void linear_combination( float coeff[], Vector3D vectors[] )
  {
    if ( coeff == null || vectors == null )
    {
      System.out.println("ERROR: null array in linear combination: " + 
                          coeff + "," + vectors );
      return;
    }

    int n_to_combine = Math.min( coeff.length, vectors.length );

    set( 0, 0, 0);
    Vector3D temp = new Vector3D();
    for ( int i = 0; i < n_to_combine; i++ )
    {
      temp.set( vectors[i] );
      temp.multiply( coeff[i] );
      add( temp );
    }
  }

  /*--------------------------- standardize ------------------------------*/
  /** 
   *  Divide the first three coordinates of this point by the fourth 
   *  component and set the fourth component to 1.
   */
  public void standardize()
  {
     x /= w;
     y /= w;
     z /= w;
     w = 1f;
  }

  /*--------------------------- length ------------------------------*/
  /** 
   *  Calculate the length of the 3D vector given by the first three 
   *  coordinates of this homogeneous point.  It is assumed that the 
   *  fourth component is 1.
   *
   *  @return   the length of this 3D vector.
   */
  public float length()
  {
    return (float)Math.sqrt( x*x + y*y + z*z );
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
  public float distance( Vector3D vec )
  {
    float dx = x - vec.x;
    float dy = y - vec.y;
    float dz = z - vec.z;
    return (float)Math.sqrt( dx * dx + dy * dy + dz * dz );
  }

  /*--------------------------- normalize ------------------------------*/
  /** 
   *  Divide the first three coordinates of this point by the length of
   *  this vector.  It is assumed that the fouth component is 1.  If the 
   *  length of the vector is zero, this function has no effect.
   */
  public void normalize()
  {
     float len = length();
     if ( len != 0.0f )
     {
       x /= len;
       y /= len;
       z /= len;
       w  = 1f;
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
  public float dot( Vector3D vector )
  {
     return ( x * vector.x +  y * vector.y +  z * vector.z );
  }

/*------------------------------- cross ---------------------------------*/
  /**
   *  Set the current vector to the cross product of itself with the
   *  given vector.  'This' is set to ( this "cross" v2 ).  It is assumed 
   *  that the 4th component of vectors 'this' vector and v is 1.
   *
   *  @param  v2  the second vector factor in the cross product
   *
   */
  public void cross( Vector3D v2 )
  {                                              // use temporary storage
                                                 // in case v2 == this
     float t0 =  y * v2.z  -  z * v2.y;
     float t1 = -x * v2.z  +  z * v2.x;
     float t2 =  x * v2.y  -  y * v2.x;

     x = t0;
     y = t1;
     z = t2;
     w = 1f;
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
  public void cross( Vector3D v1, Vector3D v2 )
  {                                              // use temporaries for v0...v2
                                                 // in case v1 or v2 == this
     float t0 =  v1.y * v2.z  -  v1.z * v2.y;
     float t1 = -v1.x * v2.z  +  v1.z * v2.x;
     float t2 =  v1.x * v2.y  -  v1.y * v2.x;

     x = t0;
     y = t1;
     z = t2;
     w = 1f;
  }

  /*------------------------------ toString ------------------------------ */
  /**
   *  Return a string form of this vector.
   */
  public String toString()
  {
    return  "{ " + x + ", " + y + ", " + z + " : " + w + " }";
  }

  /* ---------------------------- main ----------------------------------- */
  /**
   *  Main program for testing purposes only.
   */
  public static void main ( String args[] )
  {
    Vector3D  v1 = new Vector3D( 1, 2, 3);
    Vector3D  v2 = new Vector3D();
    Vector3D  v3 = new Vector3D();
    
    v2.set( 2, 3, 4 );

    System.out.println("Vectors are: " + v1 + " and " + v2 + " and " + v3 );
    System.out.println("Dot product of v1 and v2 is : " + v1.dot( v2 ) );

    v3.cross( v1, v2 );
    System.out.println( "Cross product of v1 and v2 is : " + v3 );

    System.out.println( "Length of  v3 is " + v3.length() );
    v3.normalize();
    System.out.println( "Normalized v3 is " + v3 );

    Vector3D v = new Vector3D( v1 );

    v.add( v2 );
    System.out.println( "Sum of v1 and v2  " + v  );

    v = new Vector3D( v1 );
    v.subtract( v2 );
    System.out.println( "Difference of v1 and v2  " + v  );

    v.standardize();
    System.out.println( "Standardized: " + v  );
  }

}
