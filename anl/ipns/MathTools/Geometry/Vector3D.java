/*
 * File:  Vector3D.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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

package DataSetTools.math;

/**
 *  This class represents a 3D vector using 4D homogeneous coordinates for
 *  use with the Tran3D class.  The use of 4D homogeneous coordinates allows
 *  Tran3D to represent translation and projection operations as well as
 *  rotation and scaling using 4x4 matrices.  Basic operations such as
 *  length, normalize, add, subtract, dot and cross products are provided.
 */

public class Vector3D 
{
  protected  float v[] = { 0f, 0f, 0f, 1f };

  /*------------------------- default constructor ----------------------*/
  /**
   *  Construct the 4 dimensional homogeneous point corresponding to 
   *  { 0, 0, 0 }
   */
  public Vector3D()
  {
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
   *                   values to use for this Vector3D object.
   */
  public Vector3D( Position3D position )
  {
    if ( position != null )
    {
      float pos[] = position.getCartesianCoords();

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
  public boolean equals( Vector3D vec )
  {
    boolean equal = true;

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
  public void set( float x, float y, float z )
  {
     v[0] = x;
     v[1] = y;
     v[2] = z;
     v[3] = 1.0f;
  }


  /*------------------------------- set ---------------------------------*/
  /**
   *  Set the value for this vector to the same value as the given vector.
   *
   *  @param x    The x coordinate of the point
   *  @param y    The y coordinate of the point
   *  @param z    The z coordinate of the point
   */
  public void set( Vector3D vector )
  {
     v[0] = vector.v[0];
     v[1] = vector.v[1];
     v[2] = vector.v[2];
     v[3] = vector.v[3];
  }


  /*------------------------------- get ---------------------------------*/
  /**
   *  Set a reference to the 4 dimensional array containing the x, y, z, h
   *  coordinates for this vector.
   *
   *  return reference to the 4D array containing the coordinates for this
   *         vector.
   */
  public float[] get()
  {
    return v;
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
  public void subtract( Vector3D vector )
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
  public void add( float scalar )
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
  public void multiply( float scalar )
  {
     v[0] *= scalar;
     v[1] *= scalar;
     v[2] *= scalar;
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
     v[3] = 1.0f;
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
    return (float)Math.sqrt( v[0]*v[0] + v[1]*v[1] + v[2]*v[2] );
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
       v[0] /= len;
       v[1] /= len;
       v[2] /= len;
       v[3]  = 1.0f;
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
  public void cross( Vector3D v1, Vector3D v2 )
  {                                              // use temporaries for v0...v2
                                                 // in case v1 or v2 == this
     float t0 =  v1.v[1] * v2.v[2]  -  v1.v[2] * v2.v[1];
     float t1 = -v1.v[0] * v2.v[2]  +  v1.v[2] * v2.v[0];
     float t2 =  v1.v[0] * v2.v[1]  -  v1.v[1] * v2.v[0];

     v[0] = t0;
     v[1] = t1;
     v[2] = t2;
     v[3] = 1.0f;
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
