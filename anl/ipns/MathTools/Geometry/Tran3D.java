/*
 * File:  Tran3D.java
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.4  2002/10/04 20:17:35  dennis
 * Fixed typo in javadoc comment.
 *
 * Revision 1.3  2001/06/29 18:33:19  dennis
 * Added setOrientation() method to create a matrix that
 * moves and object from the origin to a specified point
 * in 3D with it's "base" and "up" vectors in specified
 * directions..
 *
 * Revision 1.2  2001/05/23 17:26:57  dennis
 * Added get() method.
 *
 * Revision 1.1  2001/05/07 21:35:09  dennis
 * Basic transformations of 3D homogeneous vectors, such as translate,
 * rotate, scale and perspective projection.
 *
 * 
 */

package DataSetTools.math;

import DataSetTools.util.*;

/**
 *  This class represents basic transformations of Vector3D objects, such
 *  as translation, rotation, scaling, 3D viewing & projection, as well as
 *  arbitrary 4x4 linear transformations.  Methods to apply the transformation
 *  to a single Vector3D or an array of Vector3D objects are also provided.
 */
public class Tran3D
{
  public static final float Identity[][] = { { 1, 0, 0, 0 },
                                             { 0, 1, 0, 0 },
                                             { 0, 0, 1, 0 },
                                             { 0, 0, 0, 1 } };
  protected  float a[][] = { { 1, 0, 0, 0 },
                             { 0, 1, 0, 0 },
                             { 0, 0, 1, 0 },
                             { 0, 0, 0, 1 } };

  /*------------------------- default constructor ----------------------*/
  /**
   *  Construct the 3D identity transform as a 4x4 matrix.
   */
  public Tran3D()
  {
  }

  /*------------------------ copy constructor ---------------------------*/
  /**
   *  Construct a copy of the given transformation.
   *
   *  @param  tran  the transformation to copy.
   */
  public Tran3D( Tran3D tran )
  {
    set( tran );
  }

  /*---------------------------- constructor -------------------------- */
  /**
   *  Construct this transformation using the specified matrix.  The matrix 
   *  must have at least four rows and each of them must have at least four 
   *  columns.
   *
   *  @param matrix the matrix that is to be used for the transform. 
   */
  public Tran3D( float matrix[][] )
  {
    set( matrix );
  }

  /* ----------------------------- equals ------------------------------- */
  /**
   *  Check whether or not the current transform is exactly the same
   *  transformation as the specified transform.
   *
   *  @param  tran   The transform object to compare with the current transform
   *                 object.
   * 
   *  @return Returns true if the current transform object has the same matrix
   *  as the specified transform object.
   */ 
  public boolean equals( Tran3D tran )
  {
    boolean equal = true;

    for ( int row = 0; row < 3; row++ )
      for ( int col = 0; col < 3; col++ )
        if ( a[row][col] != tran.a[row][col] )
          return false;

     return true;
  }

  /*------------------------------- get ---------------------------------*/
  /**
   *  Set a reference to the 4x4 array containing the matrix for this
   *  transformation.
   *
   *  return reference to the 4x4 array containing the matrix.
   */
  public float[][] get()
  {
    return a;
  }


  /*------------------------------ set --------------------------------- */
  /**
   *  Set this transformation to the specified transformation.
   *
   *  @param tran  the transformation that the current transform is to be
   *               set from.
   */
  public void set( Tran3D tran )
  {
    for ( int row = 0; row < 4; row++ )
      for ( int col = 0; col < 4; col++ )
        a[row][col] = tran.a[row][col];
  }

  /*------------------------------ set --------------------------------- */
  /**
   *  Set this transformation to the specified matrix.  The matrix must 
   *  have at least four rows and each of them must have at least four 
   *  columns.
   *
   *  @param matrix the matrix that is to be used for the transform.
   */
  public void set( float matrix[][] )
  {
    if ( matrix.length < 4 )
    {
      System.out.println("ERROR: too few rows in matrix in Tran3D.set()" );
      return;
    }
    for ( int row = 0; row < 4; row++ )
      if ( matrix[row].length < 4 )
      {
        System.out.println("ERROR: too few columns in Tran3D.set()" );
        return;
      }

    for ( int row = 0; row < 4; row++ )
      for ( int col = 0; col < 4; col++ )
        a[row][col] = matrix[row][col];
  }


  /*--------------------------- setIdentity -----------------------------*/
  /** 
   *  Resets this transform to the identity transform.
   */
  public void setIdentity( )
  {
    set( Identity );
  }

  /*--------------------------- setTranslation ---------------------------*/
  /**
   *  Sets this transform to the transform representing a translation in 
   *  3 Dimensions through the homogeneous vector v.  It is assumed that v
   *  has been "Standardized" so that the 4th component is 1. 
   *
   *  @param   v      the homogenous representation of the translation vector 
   *                  { x, y, z, 1 }
   */
  public void setTranslation( Vector3D v )
  {
    set( Identity );
    a[0][3] = v.v[0];
    a[1][3] = v.v[1];
    a[2][3] = v.v[2];
  }


  /*----------------------------- setScale -------------------------------*/
  /**
   *  Set this transform to the transform representing scaling in 3 
   *  Dimensions by the x, y, z components of the homogeneous vector v.  
   *  It is assumed that v has been "Standardized" so that the 4th 
   *  component is 1.
   *
   *  @param   v      the homogenous representation of the scaling vector 
   *                  { x, y, z, 1 }
   */
  public void setScale( Vector3D v )
  {
    set( Identity );
    a[0][0] = v.v[0];
    a[1][1] = v.v[1];
    a[2][2] = v.v[2];
  }


  /*--------------------------- setRotation ------------------------------*/
  /**
   *  Set this transform to the transform representing rotation in 3
   *  Dimensions through a specified angle about and axis given by the 
   *  x, y, z components of the homogeneous vector v.  It is assumed that 
   *  v has been "Standardized" so that the 4th component is 1.  This 
   *  function was adapted from "matrix.c" that is part of the Mesa OpenGL
   *  code, copyright Brian Paul, 1999-2000.  It was contributed to Mesa by
   *  Erich Boleyn (erich@uruk.org) and is freely redistributable.
   *
   *  @param   angle  The angle to rotate by, specified in degrees.
   *  @param   v      The homogenous representation of the axis vector
   *                  { x, y, z, 1 }
   */
  public void setRotation( float angle, Vector3D v )
  {
    double radians = angle * Math.PI / 180.0;
    double s       = Math.sin( radians ); 
    double c       = Math.cos( radians ); 
    double mag     = Math.sqrt( v.v[0]*v.v[0] + v.v[1]*v.v[1] + v.v[2]*v.v[2] );

    if ( mag < 1.0e-10 )                       // axis is essentially zero
    {
      System.out.println("Error in Tran3D.setRotation: axis is essentially 0");
      set( Identity );
      return;
    }
   
    double x = v.v[0] / mag;
    double y = v.v[1] / mag;
    double z = v.v[2] / mag;

    double xx    = x * x;
    double yy    = y * y;
    double zz    = z * z;
    double xy    = x * y;
    double yz    = y * z;
    double zx    = z * x;
    double xs    = x * s;
    double ys    = y * s;
    double zs    = z * s;
    double one_c = 1.0F - c;

    a[0][0] = (float)((one_c * xx) + c);
    a[0][1] = (float)((one_c * xy) - zs);
    a[0][2] = (float)((one_c * zx) + ys);
    a[0][3] = 0.0F;

    a[1][0] = (float)((one_c * xy) + zs);
    a[1][1] = (float)((one_c * yy) + c);
    a[1][2] = (float)((one_c * yz) - xs);
    a[1][3] = 0.0F;

    a[2][0] = (float)((one_c * zx) - ys);
    a[2][1] = (float)((one_c * yz) + xs);
    a[2][2] = (float)((one_c * zz) + c);
    a[2][3] = 0.0F;

    a[3][0] = 0.0F;
    a[3][1] = 0.0F;
    a[3][2] = 0.0F;
    a[3][3] = 1.0F;
  }


  /*------------------------- setOrientation -----------------------------*/
  /**
   *  Set this transform to position and orient an object in 3D.
   *  An object is assumed to be initially centered at the origin
   *  with it's "base" direction in the x direction and it's "up"
   *  direction in the y direction.  The matrix created by this method
   *  will first orient the object so that it's base and up directions
   *  are in the directions given by the base and up parameters, and
   *  then translate the object to the specified point.
   *
   *  @param   base   The direction the x-axis is mapped to.
   *  @param   up     The direction the y-axis is mapped to.
   *  @param   point  The point the origin is transformed to.
   */
  public void setOrientation( Vector3D base, Vector3D up, Vector3D point )
  {
    setTranslation( point );
                                        // build the orientation matrix
    Vector3D n = new Vector3D(); 
    n.cross( base, up );

    Tran3D orient = new Tran3D();   
    for ( int i = 0; i < 3; i++ )
      orient.a[i][0] = base.v[i];

    for ( int i = 0; i < 3; i++ )
      orient.a[i][1] = up.v[i];

    for ( int i = 0; i < 3; i++ )
      orient.a[i][2] = n.v[i];

    multiply_by( orient );             // combine orientation with tranlation
  }

  /*--------------------------- setViewMatrix ------------------------------*/
  /**
   *  Set this transform to the transform representing the "view transformation"
   *  cooresponding to a specified observers position, orientation and 
   *  view direction.
   *
   *  @param   cop    The "center of projection", ie. the position of the
   *                  observer.
   *  @param   vrp    The "view reference point", ie. the point that the
   *                  observer is looking at.
   *  @param   vuv    The "view up vector", ie. the direction that is "up"
   *                  for the observer.
   *
   *  @param   perspective  Flag indicating whether or not to make a
   *                        perspective projection.  If false, the projection
   *                        will be an orthographic projection.
   */
  public void setViewMatrix( Vector3D cop, 
                             Vector3D vrp, 
                             Vector3D vuv, 
                             boolean  perspective )
  {
     Vector3D  u = new Vector3D();           // these are set to an orthonormal
     Vector3D  v = new Vector3D();           // coordinate system at the cop
     Vector3D  n = new Vector3D( cop );
                                             // set n to a unit vector pointing
     n.subtract( vrp );                      // from the observer in the
     n.normalize();                          // direction they are looking

     if ( n.length() < 0.0001 )              // cop and vrp cooincide
     {
       System.out.println("Error: cop and vrp cooincide in " +
                          "Tran3D.setViewMatrix()");
       setIdentity();
       a[3][2] = 1;
       return;
     } 

     u.cross( vuv, n );                      // set u to the local "x" axis
     u.normalize();  

     if ( u.length() < 0.0001 )
     {
       System.out.println("Error: view direction and vuv cooincide in " +
                          "Tran3D.setViewMatrix()");
       setIdentity();
       return;
     }

     v.cross( n, u );                       // set v to the local "y" axis

     setIdentity();
     a[0][3] = -u.dot( vrp );               
     a[1][3] = -v.dot( vrp );
     a[2][3] = -n.dot( vrp );
     
     for ( int i = 0; i < 3; i++ )
     {
       a[0][i] = u.v[i];
       a[1][i] = v.v[i];
       a[2][i] = n.v[i];
     }
                     
     if ( perspective )                   // multiply on the left by the
     {                                    // perspective proj matrix
       Tran3D   perspec  = new Tran3D();
       Vector3D distance = new Vector3D( cop );
        
       distance.subtract( vrp );
       perspec.a[3][2] = -1/distance.length();
       
       perspec.multiply_by( this );
       this.set( perspec );
     }
  }


  /*-------------------------- multiply_by -------------------------------*/
  /**
   *  Sets this transform to the transform representing the product of
   *  itself and the specified transform.  Specifically, this = this*tran,
   *  so conceptually, the specified transform acts first, followed by the
   *  current value of this transform, to get the resulting value for this
   *  transform.
   *
   *  @param  tran  the transform that the current transform is to be 
   *                multiplied by.
   */
  public void multiply_by( Tran3D tran )
  {
    float temp[] = new float[4];   // temporary storage for one row of product
    float sum;
    int   i, j, k;                 

    for ( i = 0; i < 4; i++ )
    {
      for ( j = 0; j < 4; j++ )
      {
        sum = 0.0f;                              // find product of row i
        for ( k = 0; k < 4; k++ )                // with the columns of "tran".
          sum = sum + a[i][k] * tran.a[k][j];    // This uses all row i entries.
        temp[j] = sum;
      }

      for ( j = 0; j < 4; j++ )                  // now that we are done with
        a[i][j] = temp[j];                       // row i, we can copy the temp
                                                 // row into row i.
    }
  }

  /*-------------------------- apply_to -------------------------------*/
  /**
   *  Multiply the transformation times vector v1, placing the result in
   *  vector v2.
   *
   *  @param  v1    The vector being transformed ( i.e. multiplied by this 
   *                transform.)
   *  @param  v2    Set to the result of transforming v1.
   */
  public void apply_to( Vector3D v1, Vector3D v2 )
  {
    float sum;
    int   row, 
          col;

    if ( !v1.equals( v2 ) )
      for ( row = 0; row < 4; row++ )
      {
        sum = 0.0f;
        for ( col = 0; col < 4; col++ )
          sum += a[row][col] * v1.v[col];

        v2.v[row] = sum;
      }       
    else
    {
      float temp[] = new float[4];          // if v1==v2, we must create the
                                            // product in a temporary variable
      for ( row = 0; row < 4; row++ )       // to avoid altering the vector
      {                                     // while we still need the original
        sum = 0.0f;
        for ( col = 0; col < 4; col++ )
          sum += a[row][col] * v1.v[col];

        temp[row] = sum;
      }      

      for ( row = 0; row < 4; row++ )
        v2.v[row] = temp[row];
    }
  }

  /*-------------------------- apply_to -------------------------------*/
  /**
   *  Multiply the transformation times each vector in the array v1, 
   *  placing the result in the corresponding vector in the array v2.
   *
   *  @param  v1    The array of vectors being transformed 
   *                ( i.e. multiplied by this transform.)
   *  @param  v2    Set to the result of transforming the vectors in v1.
   */
  public void apply_to( Vector3D v1[], Vector3D v2[] )
  {
    if ( v1 == null || v2 == null || v1.length > v2.length )
    {
      System.out.println("Error in Tran3D.apply_to: invalid arrays" );
      return;
    }

    float sum;
    int   row,
          col;
    float temp[] = new float[4];
 
    for ( int k = 0; k < v1.length; k++ )
      if ( !v1[k].equals( v2[k] ) ) 
        for ( row = 0; row < 4; row++ )
        { 
          sum = 0.0f; 
          for ( col = 0; col < 4; col++ )
            sum += a[row][col] * v1[k].v[col];
        
          v2[k].v[row] = sum;
        } 
      else
      { 
        for ( row = 0; row < 4; row++ )  
        { 
          sum = 0.0f; 
          for ( col = 0; col < 4; col++ )
            sum += a[row][col] * v1[k].v[col];
        
          temp[row] = sum;
        }
      
        for ( row = 0; row < 4; row++ )
          v2[k].v[row] = temp[row];
      }
  }


  /*------------------------------ toString ------------------------------ */
  /**
   *  Return a string form of this matrix.
   */
  public String toString()
  {
    return "{ "+a[0][0]+", "+a[0][1]+", "+a[0][2]+", "+a[0][3]+ "\n" +
           "  "+a[1][0]+", "+a[1][1]+", "+a[1][2]+", "+a[1][3]+ "\n" +
           "  "+a[2][0]+", "+a[2][1]+", "+a[2][2]+", "+a[2][3]+ "\n" +
           "  "+a[3][0]+", "+a[3][1]+", "+a[3][2]+", "+a[3][3]+ " }";
  }


  /* ----------------------------- main ---------------------------------- */
  /*
   *  main program for testing purposes only.
   *
   */
 
  public static void main ( String args[] )
  {
    float   test_1[][] =  { { 1, 2, 3, 4 },
                            { 2, 1, 3, 1 },
                            { 3, 2, 1, 5 },
                            { 2, 3, 4, 1 } };
 
    float   test_2[][] =  { { 4, 5, 1, 2 },
                            { 2, 3, 2, 1 },
                            { 5, 1, 2, 4 },
                            { 3, 4, 1, 5 } };

    float   test_3[][] =  { { 4, 5, 1, 1 },
                            { 2, 3, 2, 1 },
                            { 5, 1, 2, 1 },
                            { 0, 0, 0, 1 } };

    Tran3D  tran = new Tran3D();
    Tran3D  tran_1 = new Tran3D( test_1 );
    Tran3D  tran_2 = new Tran3D( test_2 );

    Tran3D  tran_3 = new Tran3D();
    tran_3.setScale( new Vector3D(2,3,4) );

    Tran3D  tran_4 = new Tran3D();
    tran_4.setTranslation( new Vector3D(2,3,4) );

    Tran3D  tran_5 = new Tran3D( test_1 );
    tran_5.setIdentity();

    Tran3D  tran_6 = new Tran3D( tran_2 );

    System.out.println( "tran   = \n" + tran ); 
    System.out.println( "tran_1 = \n" + tran_1 ); 
    System.out.println( "tran_2 = \n" + tran_2 ); 
    System.out.println( "tran_3 = \n" + tran_3 ); 
    System.out.println( "tran_4 = \n" + tran_4 ); 
    System.out.println( "tran_5 = \n" + tran_5 ); 
    System.out.println( "tran_6 = \n" + tran_6 ); 

    tran_1.multiply_by( tran_2 );
    System.out.println( "Product tran_1 * tran_2 = \n" + tran_1 );

    Vector3D v  = new Vector3D();
    Vector3D v1 = new Vector3D( -2, 3, 1 );
    tran_1.set( test_3 );
    tran_1.apply_to( v1, v );
    System.out.println( "Applying " );
    System.out.println( tran_1 );
    System.out.println( "to    " + v1 );
    System.out.println( "gives " + v );

    tran_1.setTranslation( new Vector3D( 1, 2, 3 ) );
    tran_1.apply_to( v1, v );
    System.out.println( "Applying " );
    System.out.println( tran_1 );
    System.out.println( "to    " + v1 );
    System.out.println( "gives " + v );

    tran_1.setScale( new Vector3D( 2, 3, 4 ) );

    int N_POINTS = 20000;
    ElapsedTime timer = new ElapsedTime();
    for ( int i = 0; i < N_POINTS; i++ )
      tran_1.apply_to( v1, v );
    float time = timer.elapsed();
    System.out.println( "Time for "+ N_POINTS +" transforms is " + time );

    Vector3D v1_list[] = new Vector3D[ N_POINTS ];
    Vector3D v2_list[] = new Vector3D[ N_POINTS ];
    for ( int i = 0; i < N_POINTS; i++ )
    {
      v1_list[i] = new Vector3D( 1, 2, 3 );
      v2_list[i] = new Vector3D( 1, 2, 3 );
    }
    timer.reset();
      tran_1.apply_to( v1_list, v2_list );
    time = timer.elapsed();
    System.out.println( "Time for "+N_POINTS+" transforms using array:"+time );

    System.out.println( "Applying " );
    System.out.println( tran_1 );
    System.out.println( "to    " + v1 );
    System.out.println( "gives " + v );

    tran_1.setRotation( 45, new Vector3D( 1, 0, 0 ) );
    System.out.println( "Rotation by 45 deg about x-axis : \n" + tran_1 );

    tran_1.setRotation( 45, new Vector3D( 0, 1, 0 ) );
    System.out.println( "Rotation by 45 deg about y-axis : \n" + tran_1 );

    tran_1.setRotation( 45, new Vector3D( 0, 0, 1 ) );
    System.out.println( "Rotation by 45 deg about z-axis : \n" + tran_1 );

    tran_1.setViewMatrix( new Vector3D( 0, 0, 1 ),
                          new Vector3D( 0, 0, 0 ),
                          new Vector3D( 0, 1, 0 ),
                          true );
    System.out.println( "View matrix is : \n" + tran_1 );
  }

}
