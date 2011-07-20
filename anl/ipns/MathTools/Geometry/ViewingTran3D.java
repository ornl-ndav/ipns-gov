/*
 * File:  ViewingTran3D.java
 *
 * Copyright (C) 2011, Dennis Mikkelson
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
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 */

package gov.anl.ipns.MathTools.Geometry;

import gov.anl.ipns.Util.Sys.*;
import gov.anl.ipns.MathTools.*;

/**
 *  This class represents a transformation of Vector3D objects that 
 *  corresponds to the 3D viewing process.  This includes rotation and
 *  translation to put the Center Of Projection (i.e. observer) at the origin 
 *  with the View Reference Point (point the observer looks towards) along
 *  the -z axis, and a simple axis aligned perspective transformation.  After
 *  applying this view transfromation to a Vector3D object, the projection
 *  still must be completed by calling "standardize" on the transformed
 *  Vector3D object. 
 */
public class ViewingTran3D extends    Tran3D
                           implements java.io.Serializable
// NOTE: THIS SHOULD NOT EXTEND Tran3D, but it has to to retain compatibility
//       with the code in DataSetTools/components/ui/Peaks/View3D.java
//       which uses a single Tran3D for viewing and moving objects in 3D.

{
  public static final long serialVersionUID = 1L;

/*                                            // for now this extends Tran3D
  private  float a[][] = { { 1, 0, 0, 0 },    // so we use the array from there
                           { 0, 1, 0, 0 },
                           { 0, 0, 1, 0 },
                           { 0, 0, 0, 1 } };
*/

  private  Vector3D  vrp;
  private  Vector3D  cop;
  private  Vector3D  vuv;

  private  Vector3D  u;
  private  Vector3D  v;
  private  Vector3D  n;

  private  boolean   perspective;


  /*--------------------------- constructor ------------------------------*/
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
  public ViewingTran3D( Vector3D cop,
                        Vector3D vrp,
                        Vector3D vuv,
                        boolean  perspective )
  {
    setViewMatrix( cop, vrp, vuv, perspective );
  }
  

  /*------------------------ copy constructor ---------------------------*/
  /**
   *  Construct a copy of the given transformation.
   *
   *  @param  tran  the transformation to copy.
   */
  public ViewingTran3D( ViewingTran3D tran )
  {
    set( tran );
  }


  /* ----------------------------- equals ------------------------------- */
  /**
   *  Check whether or not the current viewing transform is exactly the same
   *  transformation as the specified viewing transform.
   *
   *  @param  tran   The transform object to compare with the current transform
   *                 object.
   * 
   *  @return Returns true if the current transform object has the same matrix
   *  as the specified transform object.
   */ 
  public boolean equals( ViewingTran3D tran )
  {
    for ( int row = 0; row < 3; row++ )
      for ( int col = 0; col < 3; col++ )
        if ( a[row][col] != tran.a[row][col] )
          return false;

     return true;
  }

  /*------------------------------- get ---------------------------------*/
  /**
   *  Get a copy of the 4x4 array containing the matrix for this
   *  transformation.
   *
   *  return copy of the 4x4 array containing the matrix.
   */
  public float[][] get()
  {
    float[][] copy = new float[4][4];
    for ( int row = 0; row < 4; row++ )
      for ( int col = 0; col < 4; col++ )
        copy[row][col] = a[row][col];
 
    return copy;
  }


  /*------------------------------ set --------------------------------- */
  /**
   *  Set this viewing transformation to the specified viewing transformation.
   *
   *  @param tran  the viewing transformation that the current transform is
   *               to be set from.
   */
  public void set( ViewingTran3D tran )
  {
    this.vrp = new Vector3D( tran.vrp );
    this.cop = new Vector3D( tran.cop );
    this.vuv = new Vector3D( tran.vuv );

    this.u   = new Vector3D( tran.u );
    this.v   = new Vector3D( tran.v );
    this.n   = new Vector3D( tran.n );

    this.perspective = tran.perspective;

    for ( int row = 0; row < 4; row++ )
      for ( int col = 0; col < 4; col++ )
        a[row][col] = tran.a[row][col];
  }


  public Vector3D getCOP()
  {
    return new Vector3D( cop );
  }

  public Vector3D getVRP()
  {
    return new Vector3D( vrp );
  }

  public Vector3D getVUV()
  {
    return new Vector3D( vuv );
  }

  public Vector3D getU()
  {
    return new Vector3D( u );
  }

  public Vector3D getV()
  {
    return new Vector3D( v );
  }

  public Vector3D getN()
  {
    return new Vector3D( n );
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
     Vector3D temp_u = new Vector3D();       // these are set to an orthonormal
     Vector3D temp_v = new Vector3D();       // coordinate system at the cop
     Vector3D temp_n = new Vector3D( cop );
                                             // set n to a unit vector pointing
     temp_n.subtract( vrp );                 // from the observer in the
     temp_n.normalize();                     // direction they are looking

     if ( temp_n.length() < 0.0001 )         // cop and vrp cooincide
     {
       System.out.println("Error: cop and vrp cooincide in " +
                          "Tran3D.setViewMatrix()");
       return;
     } 

     temp_u.cross( vuv, temp_n );            // set u to the local "x" axis
     temp_u.normalize();  

     if ( temp_u.length() < 0.0001 )
     {
       System.out.println("Error: view direction and vuv cooincide in " +
                          "Tran3D.setViewMatrix()");
       return;
     }
                                             // at this point we should be OK
     this.u = temp_u;
     this.n = temp_n;
     this.v = new Vector3D();
     this.v.cross( n, u );                   // set v to the local "y" axis

     this.perspective = perspective;

     this.cop = new Vector3D( cop );
     this.vrp = new Vector3D( vrp );
     this.vuv = new Vector3D( vuv );

     a[0][3] = -u.dot( vrp );               
     a[1][3] = -v.dot( vrp );
     a[2][3] = -n.dot( vrp );
     a[3][3] =  1;
     
     a[0][0] = u.x;
     a[1][0] = v.x;
     a[2][0] = n.x;
     a[3][0] = 0;

     a[0][1] = u.y;
     a[1][1] = v.y;
     a[2][1] = n.y;
     a[3][1] = 0;

     a[0][2] = u.z;
     a[1][2] = v.z;
     a[2][2] = n.z;
     a[3][2] = 0;
                     
     if ( perspective )                   // multiply on the left by the
     {                                    // perspective proj matrix
       Tran3D   perspec  = new Tran3D();
       Vector3D distance = new Vector3D( cop );
        
       distance.subtract( vrp );
       perspec.a[3][2] = -1/distance.length();
       
       perspec.multiply_by( new Tran3D(a) );
       this.a = perspec.get();
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
    if ( v1 == null || v2 == null )
    {
      System.out.println("Error in Tran3D.apply_to: vector is null" );
      return;
    }

    int   row;

    float[] temp = new float[4];
    for ( row = 0; row < 4; row++ )
    {
      temp[row] = a[row][0] * v1.x +
                  a[row][1] * v1.y +
                  a[row][2] * v1.z +
                  a[row][3] * v1.w;
    }
    v2.set(temp);
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

    for ( int k = 0; k < v1.length; k++ )
      apply_to( v1[k], v2[k] );
  }


  /*------------------------------ toString ------------------------------ */
  /**
   *  Return a string form of this matrix.
   */
  public String toString()
  {
    return "" + cop + "\n" +
                vrp + "\n" +
                vuv + "\n" +
                u   + "\n" +
                v   + "\n" +
                n   + "\n" +
                perspective + "\n" +
           "{ "+a[0][0]+", "+a[0][1]+", "+a[0][2]+", "+a[0][3]+ "\n" +
           "  "+a[1][0]+", "+a[1][1]+", "+a[1][2]+", "+a[1][3]+ "\n" +
           "  "+a[2][0]+", "+a[2][1]+", "+a[2][2]+", "+a[2][3]+ "\n" +
           "  "+a[3][0]+", "+a[3][1]+", "+a[3][2]+", "+a[3][3]+ " }";
  }


  public static void main( String args[] )
  {
    Vector3D cop = new Vector3D(  1,  2, 3 );
    Vector3D vrp = new Vector3D( -3,  5, 2 );
    Vector3D vuv = new Vector3D(  3, -1, 4 );
    ViewingTran3D tran = new ViewingTran3D( cop, vrp, vuv, true );
    System.out.println("Viewing tran is: \n" + tran );
  }

}
