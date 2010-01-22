/*
 * File:  LinearAlgebra.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 *
 * Modified:
 *
 *  Programmer:  Dennis Mikkelson
 *
 *  Basic linear algebra operations such as QR factorization and solution of
 *  system of linear equations using QR factorization
 * 
 *  $Log$
 *  Revision 1.29  2006/07/26 15:55:42  dennis
 *  Commented out BestFitMatrix2() method that uses the Jama package.
 *  This method was only used for performance testing of our
 *  BestFitMatrix() method.  Commenting it out, removes all
 *  dependencies on the Jama package.
 *
 *  Revision 1.28  2006/01/03 03:24:20  dennis
 *  Fixed index error in methods to multiply matrix times vector.
 *
 *  Revision 1.27  2005/12/31 02:46:01  dennis
 *  Added methods to copy matrices and multiply a matrix times
 *  a vector.
 *  Made some minor improvements to other javadocs.  Quite a few
 *  of the basic methods still need javadocs.
 *
 *  Revision 1.26  2004/08/05 17:05:18  dennis
 *  Added some additional explanation to the documentation for
 *  the BestFitMatrix() routine.
 *
 *  Revision 1.25  2004/07/16 19:05:22  dennis
 *  Fixed improper comparison with Float.NaN
 *
 *  Revision 1.24  2004/04/20 18:22:43  dennis
 *  Added some validity checks to BestFitMatrix.  Now does most
 *  calculations in a try...catch block.
 *  Minor improvements to javadocs for BestFitMatrix.
 *
 *  Revision 1.23  2004/03/17 19:01:12  dennis
 *  Removed unused variables.
 *
 *  Revision 1.22  2004/03/11 23:27:16  dennis
 *  Moved to MathTools package
 *
 *  Revision 1.21  2004/03/06 21:57:45  dennis
 *  Fixed error in calculation of inverse matrix sizes > 3x3.
 *
 *  Revision 1.20  2003/10/15 23:59:02  dennis
 *  Fixed javadocs to build cleanly with jdk 1.4.2
 *
 *  Revision 1.19  2003/07/14 22:29:57  dennis
 *  print methods now format their output in 5 columns.
 *
 *  Revision 1.18  2003/07/14 13:36:01  dennis
 *  Fixed a java doc comment.
 *
 *  Revision 1.17  2003/06/19 22:23:29  dennis
 *  Fixed java docs
 *
 *  Revision 1.16  2003/06/19 15:34:12  dennis
 *  Added float versions of routines to print arrays.
 *
 *  Revision 1.15  2003/06/18 14:59:38  dennis
 *  Added convenience method to print an array of doubles.
 *
 *  Revision 1.14  2003/06/17 13:28:09  dennis
 *  Added getTranspose( a[][] ) method, and added formatting
 *  to print( a[][] ) method.
 *
 *  Revision 1.13  2003/05/20 22:35:04  dennis
 *  Fixed bug with calculation of residual errors in the QR_solve,
 *  BestFitMatrix() and related methods.  Added method BestFitMatrix2() based
 *  on Jama package, for testing purposes.  BestFitMatrix2() is slower than
 *  our original BestFitMatrix() method and does NOT return residual
 *  errors, so BestFitMatrix() method should generally be used.
 *
 *  Revision 1.12  2003/04/30 19:57:53  pfpeterson
 *  Fixed a javadoc tag.
 *
 *  Revision 1.11  2003/04/09 16:47:51  pfpeterson
 *  Created a debug flag and moved a print statement to depend on it.
 *
 *  Revision 1.10  2003/03/19 21:33:45  dennis
 *  Added method BestFitMatrix( M, q, r ) that calculates the best
 *  least squares approximation for a matrix M mapping a list of vectors
 *  q to a list of vectors r.
 *
 *  Revision 1.9  2003/02/06 21:27:32  dennis
 *  Add code for finding inverse of 2x2 matrix.  Fixed potential problem
 *  in inverse method.
 *
 *  Revision 1.8  2003/02/04 19:43:53  pfpeterson
 *  Implemented isSquare(float[][]) and isRectangular(float[][]) without 
 *  casting.
 *
 *  Revision 1.7  2003/01/30 21:03:15  pfpeterson
 *  Added methods to convert between float[][] and double[][]. Added
 *  method to find the determinant of 2x2 and 3x3 matrices. Added
 *  methods to confirm a matrix is rectangular or square. Added method
 *  to multiply two matrices together. Renamed some methods. Added more
 *  sanity checks before finding inverse including a simple transpose
 *  if the determinant is one.
 *
 *  Revision 1.6  2003/01/08 17:12:01  dennis
 *  Added method invert() to calculate the inverse of a matrix.
 *
 *  Revision 1.5  2002/11/27 23:15:47  pfpeterson
 *  standardized header
 *
 *  Revision 1.4  2002/06/17 18:54:26  dennis
 *  Added methods QR_solve() and Norm().
 *
 */

package gov.anl.ipns.MathTools;

import gov.anl.ipns.Util.Numeric.*;
import gov.anl.ipns.Util.Sys.*;
import java.util.*;
// import Jama.*;

/**
 *  Basic linear algebra operations such as dot product, solution of
 *  system of linear equations and matrix inversion.
 */
public final class LinearAlgebra 
{
  public static final boolean DEBUG=false;
  /*
   * Don't let anyone instantiate this class.
   */
  private LinearAlgebra() {}


  /**
   *  Return a new matrix that is the transpose of the specified matrix
   *
   */
  public static float[][] getTranspose( float A[][] )
  {
    if ( !isRectangular(A) )
      return null;
   
    float A_transp[][] = new float[ A[0].length ][ A.length ];
    for ( int row = 0; row < A.length; row++ )
      for ( int col = 0; col < A[0].length; col++ )
        A_transp[col][row] = A[row][col];

    return A_transp; 
  }


  /**
   *  Return a new matrix that is the transpose of the specified matrix
   *
   */
  public static double[][] getTranspose( double A[][] )
  {
    if ( !isRectangular(A) )
      return null;
   
    double A_transp[][] = new double[ A[0].length ][ A.length ];
    for ( int row = 0; row < A.length; row++ )
      for ( int col = 0; col < A[0].length; col++ )
        A_transp[col][row] = A[row][col];

    return A_transp;
  }


  /**
   * Calculate the inverse of a square matrix A[][], of floats, if
   * possible.  This copies the matrix into a double double precision
   * array and calls the other version.
   *
   * @see #getInverse(double[][])
   */
  public static float[][] getInverse(float[][] A){
    if( ! isSquare(A) ) return null;
    return double2float(getInverse(float2double(A)));
  }


  /**
   * Calculate the inverse of a square matrix A[][], of doubles, if
   * possible.
   *
   * @param A the square matrix to invert.
   *
   * @return If successful returns the inverse matrix. If A is not
   * invertible or something goes wrong this returns null.
   */
  public static double[][] getInverse(double[][] A){
    if( A==null ) return null;

    if( ! isSquare(A) ) return null;

    int size=A.length;
    double[][] invA=new double[size][size];

    double detA=Double.NaN;

                                     // check for small matrices
    if(size==2 || size==3){          // determinants are more efficient for
      detA=determinant(A);           // 2x2 and 3x3 matrices
      if(detA==0.){ // this is not invertable
        return null;
      }

      if(size==2){                              // size must be 2 or....
        if ( ! Double.isNaN(detA) ){
          invA[0][0] =  A[1][1]/detA;
          invA[1][0] = -A[1][0]/detA;
          invA[0][1] = -A[0][1]/detA;
          invA[1][1] =  A[0][0]/detA;

          return invA;
        }
      }
                                                // otherwize size must be 3
      if( ! Double.isNaN(detA) ){
        invA[0][0]=(A[1][1]*A[2][2]-A[2][1]*A[1][2])/detA;
        invA[0][1]=(A[2][1]*A[0][2]-A[0][1]*A[2][2])/detA;
        invA[0][2]=(A[0][1]*A[1][2]-A[1][1]*A[0][2])/detA;
        invA[1][0]=(A[2][0]*A[1][2]-A[1][0]*A[2][2])/detA;
        invA[1][1]=(A[0][0]*A[2][2]-A[2][0]*A[0][2])/detA;
        invA[1][2]=(A[1][0]*A[0][2]-A[0][0]*A[1][2])/detA;
        invA[2][0]=(A[1][0]*A[2][1]-A[2][0]*A[1][1])/detA;
        invA[2][1]=(A[2][0]*A[0][1]-A[2][1]*A[0][0])/detA;
        invA[2][2]=(A[0][0]*A[1][1]-A[1][0]*A[0][1])/detA;

        return invA;
      }
    }

    double u[][] = QR_factorization( A );

    double b[] = new double[size];
    double result;                      // now calculate the inverse by 
    for ( int i = 0; i < size; i++ )    // solving the equations Ax = b where b
    {                                   // is given the columns of the identity
      for ( int k = 0; k < size; k++ )
        b[k] = 0;
      b[i] = 1;
 
      result = QR_solve( A, u, b );
      if ( Double.isNaN( result ) )
        return null;

      for ( int k = 0; k < size; k++ )
        invA[k][i] = b[k]; 
    }

    return invA;
  }


  /**
   * Replaces a square matrix A[][] with it's inverse, if
   * possible.
   *
   * @param A the square matrix to invert.
   *
   * @return Returns true and the parameter A is changed to the
   * inverse of the original matrix A, if the calculation succeeds.
   * If The matrix is not square, this returns false and the matrix A
   * is not altered.  If the calculation of the inverse fails, this
   * returns false and the values in matrix A will have been altered.
   */
  public static boolean invert(double A[][]){
    double[][] invA=getInverse(A);
    if( invA==null ) return false; // something went wrong
    int size=A.length;
    for( int i=0 ; i<size ; i++ ){
      for( int j=0 ; j<size ; j++ ){
        A[i][j]=invA[i][j];
      }
    }
    return true;
  }


  /**
   * Prints to STDOUT any nXm matrix of doubles
   */
  public static void print(double[][] a){
    for( int i=0 ; i<a.length ; i++ ){
      for( int j=0 ; j<a[i].length ; j++ ){
        System.out.print( Format.real( a[i][j], 15, 7 ) + " ");
        if ( j % 5 == 0 && j > 0 ) 
          System.out.println();
      }
      System.out.println("");
    }
  }


  /**
   * Prints to STDOUT any nXm matrix of floats
   */
  public static void print(float[][] a){
    for( int i=0 ; i<a.length ; i++ ){
      for( int j=0 ; j<a[i].length ; j++ ){
        System.out.print( Format.real( a[i][j], 15, 7 ) + " ");
        if ( j % 5 == 0 && j > 0 ) 
          System.out.println();
      }
      System.out.println("");
    }
  }


  /**
   * Prints to STDOUT any one-dimensional array of doubles
   */
  public static void print(double[] a){
    for( int j=0 ; j<a.length ; j++ )
    {
      System.out.print( Format.real( a[j], 15, 7 ) + " ");
      if ( j % 5 == 0 && j > 0 ) 
        System.out.println();
    }
    System.out.println("");
  }


  /**
   * Prints to STDOUT any one-dimensional array of floats
   */
  public static void print(float[] a){
    for( int j=0 ; j<a.length ; j++ )
    {
      System.out.print( Format.real( a[j], 15, 7 ) + " ");
      if ( j % 5 == 0 && j > 0 ) 
        System.out.println();
    }
    System.out.println("");
  }


  /**
   * Converts a one dimensional float array to a 
   * one dimensional double array
   */
  public static double[] float2double(float[] f){
    double[] d=new double[f.length];

    for( int i=0 ; i<f.length ; i++ ){
      d[i]=(double)f[i];
    }
    return d;
  }


  /**
   * Converts a one dimensional double array to a 
   * one dimensional float array
   */
  public static float[] double2float(double[] d){
    float[] f=new float[d.length];

    for( int i=0 ; i<d.length ; i++ ){
      f[i]=(float)d[i];
    }
    return f;
  }


  /**
   * Converts a rectangular float array to a rectangular double array
   */
  public static double[][] float2double(float[][] f){
    if( f==null ) return null; // make sure it isn't null
    for( int i=1; i<f.length ; i++ ){ // make sure matrix is rectangular
      if( f[i].length!=f[0].length) return null;
    }

    double[][] d=new double[f.length][f[0].length];

    for( int i=0 ; i< f.length ; i++ ){
      for( int j=0 ; j< f[0].length ; j++ ){
        d[i][j]=(double)f[i][j];
      }
    }
    return d;
  }


  /**
   * Converts a rectangular double array to a rectangular float array
   */
  public static float[][] double2float(double[][] d){
    if(!isRectangular(d)) return null;
    float[][] f=new float[d.length][d[0].length];

    for( int i=0 ; i< d.length ; i++ ){
      for( int j=0 ; j< d[0].length ; j++ ){
        f[i][j]=(float)d[i][j];
      }
    }
    return f;
  }


  /**
   * Determines whether or not the specified matrix is square by
   * comparing the lengths of the arrays.
   */
  static public boolean isSquare(float[][]a){
    if(a==null) return false;
    if(isRectangular(a)){
      return (a.length==a[0].length);
    }else{
      return false;
    }
  }


  /**
   * Determines whether or not the specified matrix is rectangular by
   * comparing the lengths of the arrays.
   */
  static public boolean isRectangular( double[][] a){
    if(a==null) return false;
    for( int i=1 ; i<a.length ; i++ ){
      if(a[i].length!=a[0].length) return false;
    }
    return true;
  }


  /**
   * Determines whether or not the specified matrix is rectangular by
   * comparing the lengths of the arrays.
   */
  static public boolean isRectangular( float[][] a){
    if(a==null) return false;
    for( int i=1 ; i<a.length ; i++ ){
      if(a[i].length!=a[0].length) return false;
    }
    return true;
  }


  /**
   * Determines whether or not the specified matrix is square by
   * comparing the lengths of the arrays.
   */
  static public boolean isSquare(double[][]a){
    if(a==null) return false;
    if(isRectangular(a)){
      return (a.length==a[0].length);
    }else{
      return false;
    }
  }


  /**
   * Find the determinant of a 3x3 or 2x2 matrix
   */
  static public double determinant( double[][] A){
    if(A==null) return Double.NaN;
    if(!isSquare(A)) return Double.NaN;

    double det=0.;
    if(A.length==2){
      det=det+(A[0][0]*A[1][1]-A[0][1]*A[1][0]);
    }else if(A.length==3){
      det=det+A[0][0]*(A[1][1]*A[2][2]-A[2][1]*A[1][2]);
      det=det-A[0][1]*(A[1][0]*A[2][2]-A[2][0]*A[1][2]);
      det=det+A[0][2]*(A[1][0]*A[2][1]-A[2][0]*A[1][1]);
    }else{
      return Double.NaN;
    }
    return det;
  }


  /**
   * Find the determinant of a 3x3 or 2x2 matrix
   */
  static public float determinant( float[][] A){
    if(A==null) return Float.NaN;
    if(!isSquare(A)) return Float.NaN;

    double[][] dA=float2double(A);
    double det = determinant( dA );

    if ( Double.isNaN(det) )
      return Float.NaN;

    return (float)det;
  }


  /**
   * Create a new matrix by multiplyin a matrix by a scalar
   */
  public static double[][] mult( double[][] A, double scale )
  {
    if ( A == null )
      return null;

    double[][] A_scaled = new double[A.length][];
    for ( int row = 0; row < A.length; row++ )
    {
      if ( A[row] == null )
        return null;

      A_scaled[row] = new double[ A[row].length ];
      for ( int col = 0; col < A[0].length; col++ )
        A_scaled[row][col] = A[row][col] * scale;
    } 

    return A_scaled;
  }


  /**
   * Create a new matrix by multiplyin a matrix by a scalar
   */
  public static float[][] mult( float[][] A, float scale )
  {
    if ( A == null ) 
      return null;

    double[][] dA = float2double(A);
    double[][] dA_scaled = mult( dA, scale );

    if ( dA_scaled == null )
      return null;

    return double2float( dA_scaled );
  }



  /**
   * Multiply two matrices together, using double arrays.
   */
  public static double[][] mult (double[][] a, double[][] b){
    if(!(isRectangular(a)&&isRectangular(b)))
      return null;

    int ax=a.length;
    int ay=a[0].length;
    int bx=b.length;
    int by=b[0].length;

    // check that this is possible
    if(ay!=bx)
      throw new ArrayIndexOutOfBoundsException("Matrices cannot be multiplied "
                                               +"due to dimensionality");

    // create the return matrix
    double[][] c=new double[ax][by];

    // do the math
    int i,j,k;
    for( i=0 ; i< ax ; i++ ){
      for( j=0 ; j<by ; j++ ){
        c[i][j]=0.;
        for( k=0 ; k<ay ; k++ ){
          c[i][j]=c[i][j]+a[i][k]*b[k][j];
        }
      }
    }
    return c;
  }


  /**
   * Multiply two matrices together, using float arrays.
   */
  public static float[][] mult(float[][] a, float[][] b){
    if( a==null || b==null ) return null;
    double[][] da=float2double(a);
    double[][] db=float2double(b);

    if(!(isRectangular(da)&&isRectangular(db))){
      return null;
    }else{
      return double2float(mult(da,db));
    }
  }


  /* ------------------------------- mult --------------------------------- */
  /**
   *  Multiply a matrix times a vector, using doubles.
   *
   *  @param  A       The matrix to multiply times the specified vector
   *  @param  vec     The vector to be multiplied
   *
   *  @return A new array containing the product vector, A*vec
   */
  public static double[] mult( double[][] A, double[] vec )
  {
    if ( ! isRectangular(A) )
      throw new IllegalArgumentException("Array A is not rectangular");

    if ( vec == null )
      throw new IllegalArgumentException("Vector is null");

    int n_rows   = A.length;
    int n_cols   = A[0].length;
    int vec_rows = vec.length;

    // check that this is possible
    if ( n_cols != vec_rows )
      throw new ArrayIndexOutOfBoundsException("Matrix cannot be multiplied "
             + " times vector due to dimensionality: " 
             +   n_rows + " X " + n_cols  
             + ", " + vec_rows ); 

    double[] result = new double[ vec_rows ];

    for( int row = 0; row < n_rows; row++ )
    {
      for( int col = 0; col < n_cols; col++ )
        result[row] += A[row][col] * vec[col];
    }
    return result;
  }
  

  /* ------------------------------- mult --------------------------------- */
  /**
   *  Multiply a matrix times a vector, using floats.
   *
   *  @param  A       The matrix to multiply times the specified vector
   *  @param  vec     The vector to be multiplied
   *
   *  @return A new array containing the product vector, A*vec
   */
  public static float[] mult( float[][] A, float[] vec )
  {
    if ( ! isRectangular(A) )
      throw new IllegalArgumentException("Array A is not rectangular");

    if ( vec == null )
      throw new IllegalArgumentException("Vector is null");

    int n_rows   = A.length;
    int n_cols   = A[0].length;
    int vec_rows = vec.length;

    // check that this is possible
    if ( n_cols != vec_rows )
      throw new ArrayIndexOutOfBoundsException("Matrix cannot be multiplied "
             + " times vector due to dimensionality: "
             +   n_rows + " X " + n_cols
             + ", " + vec_rows );

    float[] result = new float[ vec_rows ];

    for( int row = 0; row < n_rows; row++ )
    {
      for( int col = 0; col < n_cols; col++ )
        result[row] += A[row][col] * vec[col];
    }
    return result;
  }


  /* ------------------------------- copy --------------------------------- */
  /**
   *  Make a copy of a two-dimensional array of doubles
   *
   *  @param  A   The two-dimensional array to copy
   *  
   *  @return  A new array with the same dimensions as the specified array,
   *           containing the same values of the specified array.
   */
  public static double[][] copy( double A[][] )
  {
    if ( ! isRectangular(A) )
      throw new IllegalArgumentException("Array A is not rectangular");

    double M[][] = new double[ A.length ][ A[0].length ];
    for ( int i = 0; i < A.length; i++ )
      for ( int j = 0; j < A[0].length; j++ )
        M[i][j] = A[i][j];
    return M;
  }


  /* ------------------------------- copy --------------------------------- */
  /**
   *  Make a copy of a two-dimensional array of floats
   *
   *  @param  A   The two-dimensional array to copy
   *  
   *  @return  A new array with the same dimensions as the specified array,
   *           containing the same values of the specified array.
   */
  public static float[][] copy( float A[][] )
  {
    if ( ! isRectangular(A) )
      throw new IllegalArgumentException("Array A is not rectangular");

    float M[][] = new float[ A.length ][ A[0].length ];
    for ( int i = 0; i < A.length; i++ )
      for ( int j = 0; j < A[0].length; j++ )
        M[i][j] = A[i][j];
    return M;
  }


  /* ------------------------------- copy --------------------------------- */
  /**
   *  Copy one two-dimensional array of doubles into a second two-dimensional
   *  array. 
   *
   *  @param  A   The two-dimensional array to copy
   *
   *  @param  B   The two-dimensional array into which the values will be
   *              copied.  This must have the same dimensions as array A.
   */
  public static void copy( double A[][], double B[][] )
  {
    if ( ! isRectangular(A) )
      throw new IllegalArgumentException("Array A is not rectangular");

    if ( ! isRectangular(B) )
      throw new IllegalArgumentException("Array B is not rectangular");

    if ( A.length != B.length || A[0].length != B[0].length )
      throw new IllegalArgumentException("Array A and B are not the same size");

    for ( int i = 0; i < A.length; i++ )
      for ( int j = 0; j < A[0].length; j++ )
        B[i][j] = A[i][j];
  }


  /* ------------------------------- copy --------------------------------- */
  /**
   *  Copy one two-dimensional array of floats into a second two-dimensional
   *  array. 
   *
   *  @param  A   The two-dimensional array to copy
   *
   *  @param  B   The two-dimensional array into which the values will be
   *              copied.  This must have the same dimensions as array A.
   */
  public static void copy( float A[][], float B[][] )
  {
    if ( ! isRectangular(A) )
      throw new IllegalArgumentException("Array A is not rectangular");

    if ( ! isRectangular(B) )
      throw new IllegalArgumentException("Array B is not rectangular");

    if ( A.length != B.length || A[0].length != B[0].length )
      throw new IllegalArgumentException("Array A and B are not the same size");

    for ( int i = 0; i < A.length; i++ )
      for ( int j = 0; j < A[0].length; j++ )
        B[i][j] = A[i][j];
  }


  /* ------------------------------- solve -------------------------------- */
  /**
    * Solve the system of linear equations Ax = b using the QR factorization
    * of matrix A.  This is NOT the fastest way to solve a linear system, but
    * it is well behaved.  It also immediately provides the "least squares"
    * approximation to a solution of an overdetermined system of equations,
    * as is encountered when fitting a polynomial to data points.  In this case
    * the residual error is returned as the value of the function.  The 
    * solution is returned in parameter "b".  Specifically if A has N columns,
    * then the first N entries of "b" represent the solution to Ax = b if A 
    * is a square matrix.  The first N entries of "b" represent the least 
    * squares solution if A has more rows than columns.
    *
    * @param   A     Rectangular array containing a matrix "A".  This is altered
    *                to contain a matrix "R" where "R" is upper triangular and
    *                A = QR.  The number of rows of A must equal or exceed the
    *                number of columns of A.
    *
    * @param   b     One dimension array of values containing the right hand
    *                side of the linear system of equations Ax = b.  "b" must
    *                have as many rows as matrix "A".
    *
    * @return  The return value represents the residual error in the least
    *           squares approximation if A has more rows than columns.  If
    *           A is square, the return value is 0.  If A has more columns
    *           than rows, or if the system is singular, this function fails 
    *           and returns NaN.
    */

  public static double solve( double A[][], double b[] )
  { 
    if ( A == null || b == null || 
         A.length != b.length || A.length < A[0].length )
    {
      System.out.println("ERROR: invalid parameters in LinearAlgebra.solve");
      return Double.NaN;
    }

    double u[][] = QR_factorization( A );
                                          // Apply the Householder transforms
                                          // that reduced A to upper triangular
                                          // form to the right hand side, b. 
    return QR_solve( A, u, b );
  }


  /* ------------------------------- QR_solve ----------------------------- */
  /**
   *  Solve a system of linear equations, Ax = b, using the QR factored 
   *  form of A.  This is a portion of the full solution process.  To solve
   *  Ax = b, either:
   *  1. use u = QR_factorization(A) to construct u = Q, A = R and
   *  2. use QR_solve(A,u,b) to replace the components of b with the components
   *     of the solution x.
   *
   *  or just use the solve(A,b) method which combines these two steps.
   *
   *  @param   A     Rectangular array containing a matrix "A", as altered by
   *                 the method QR_factorization(A).
   *
   *  @param  u      The QR factorization of A as returned by the method
   *                 QR_factorization.
   *
   *  @param  b      The right hand side of the linear equations Ax = b
   *
   *  @return   The return value represents the residual error in the least
   *            squares approximation if u has more rows than columns.  If
   *            u is square, the return value is 0.  If u has more columns
   *            than rows, or if the system is singular, this function fails 
   *            and returns NaN.
   */

  public static double QR_solve( double A[][], double u[][], double b[] )
  {
                                          // Apply the Householder transforms
                                          // that reduced A to upper triangular
                                          // form to the right hand side, b. 
    for ( int i = 0; i < u.length; i++ )
      HouseholderTransform( u[i], b );
                                          // now back substitute  
    for ( int i = A[0].length-1; i >= 0; i-- )
    {
      if ( A[i][i] == 0 )
      {
        System.out.println("ERROR: singular system in  LinearAlgebra.solve");
        return Double.NaN;
      }
      double sum = 0.0;
      for ( int j = i+1; j < A[0].length; j++ )
        sum += b[j] * A[i][j];

      b[i] = ( b[i] - sum )/A[i][i];
    }

    double error = 0.0;
    for ( int i = A[0].length; i < A.length; i++ )
      error += b[i]*b[i];

    return Math.sqrt( error );
  }


  /* --------------------------- QR_factorization -------------------------- */
  /**
    * Produce the QR factorization of a specified matrix A.  The matrix A is
    * assumed to be a full rectangular array of values ( not a "ragged array").
    * Matrix A is altered to be the upper triangular matrix "R" obtained by
    * applying a sequence of orthogonal transformations to A.  The information
    * required to construct Q is returned in a two-dimensional array as the
    * "value" of this function.
    *
    * @param   A     rectangular array containing a matrix "A".  This is altered
    *                to contain a matrix "R" where "R" is upper triangular and
    *                A = QR.  The number of rows of A must equal or exceed the
    *                number of columns of A.
    *
    * @return  A 2D array containing the unit vectors "U" that generate 
    *          the Householder transformations that were used to reduce the
    *          matrix A to upper triangular form.  Row zero of the returned
    *          array contains the vector "U" that was used to get 0's under
    *          element A[0][0].  Row one of the returned array contains the
    *          vector "U" that was used to get 0's under element A[1][1], etc.
    *          For each i, row i contains the entries of a column vector Ui 
    *          corresponding to an orthogonal transformation 
    *          Qi = I - 2*Ui*transpose(Ui), where I is the identity matrix.
    *          A is reduced to the triangular matrix R by multiplication on 
    *          the left by the product: Qn * ... * Q2 * Q1 * Q0.  
    */

  public static double[][] QR_factorization( double A[][] )
  {
    double s;                        // s holds the sqrt of the sum of the 
                                     // squares of the last terms in a column 
    double dot_prod;                 // holds the dot product of U with a vector
    int    row,
           col;
    int    n_rows_A = A.length;
    int    n_cols_A = A[0].length;
    double U[][] = new double[n_cols_A][n_rows_A];

    for ( col = 0; col < n_cols_A; col++ )        // reduce each column of A
    {
      s = 0.0;                                    // find sum of squares of
      for ( row = col; row < n_rows_A; row++ )    // later elements in A[][col]
        s += A[row][col] * A[row][col]; 
      s = Math.sqrt( s );

      for ( row = 0; row < col; row++ )           // build U vector
        U[col][row] = 0; 

      if ( A[col][col] > 0 )
        U[col][col] = A[col][col] + s; 
      else
        U[col][col] = A[col][col] - s; 

      for ( row = col+1; row < n_rows_A; row++ )
        U[col][row] = A[row][col];

      normalize( U[col] );

      // Now multiply A by the Housholder transform corresponding to U[col].  
      // The effect of the Householder transform on any vector V is 
      // to change V to V - cU, where c is twice the dot product of V and U.
      // Since the first entries in U are non-zero, we only alter the values 
      // of A in the lower right portion of A.
                  
      for ( int j = col; j < n_cols_A; j++ )   // alter column "j"
      {
         dot_prod = 0.0;                      // find dot product of U and col j
         for ( int i = col; i < n_rows_A; i++ )       
           dot_prod += U[col][i] * A[i][j];
                                              // V = V - cU, where V is col j 
                                              // of A
         for ( int i = col; i < n_rows_A; i++ )
           A[i][j] -= 2 * dot_prod * U[col][i];
      }
    }
    return U; 
  }


  /* -------------------------- BestFitMatrix ---------------------------- */
  /**
   *  Calculate and return the matrix M that most nearly maps the vectors in
   *  the list q to the vectors in the list r.  This performs least squares
   *  fitting of the coefficients in the matrix M, as is needed when refining
   *  the orientation matrix for SCD data proccessing. Specifically, M will
   *  be calculated to be the matrix that best fits the equations  Mqi=ri
   *  for vectors qi, ri, i=0,...,k-1, where k is the number of data vectors.
   *  The vectors qi each have m components and the vectors ri each have n
   *  components.  In many cases n = m = 3, but the solution is more general.
   *  In the "simple" case, this finds the 3x3 matrix M that most nearly 
   *  maps the 3D vectors qi to the 3D vectors ri.  Parameter q holds the
   *  3D vectors qi in its ROWS and parameter r holds the 3D vectors ri in
   *  its ROWS.
   *  This version uses the QR factorization method from this package and 
   *  returns the residual errors.  The BestFitMatrix2() is based on the
   *  Jama package from NIST.  It is included for testing purposes.
   *  BestFitMatrix2() is slower than BestFitMatrix() method and does NOT 
   *  return residual errors, so BestFitMatrix() method should generally
   *  be used.
   *
   *  @param  M      The n X m matrix that maps vector q[i][*] to
   *                 vector r[i][*].  The components of M are set by this
   *                 method, but the storage for M must be properly allocated
   *                 by the calling code.
   *  @param  q      This is the list of vector q data points.  Each row of
   *                 the 2-dimensional array q is assumed to be a vector of
   *                 size m.  There must be the same number of q vectors, as
   *                 of r vectors.  Specifically, q must be of size k x m.
   *  @param  r      This is the list of vector r data points.  Each row of
   *                 the 2-dimensional array r is assumed to be a vector of
   *                 size n.  There must be the same number of r vectors, as
   *                 q vectors.  Specifically, r must be of size k x n.
   *                  
   *  @return On completion, the best fit matrix will have been entered in
   *  parameter M and the return value will be the square root of the sum of 
   *  the squares of the residual errors.  If an error is encountered, error
   *  information will be printed to the console and the return value will
   *  be set to Double.NaN.  The state of the parameters is indeterminate
   *  if an error is encountered.  NOTE: The values stored in M, q and
   *  r will be altered during the calculation, so if the calling code needs 
   *  their original values, it is responsible for saving copies of the 
   *  original data.
   */
  public static double BestFitMatrix(double M[][], double q[][], double r[][])
  {
    if ( M == null || q == null || r == null )
    {
      System.out.println("ERROR: Null parameter in BestFitMatrix");
      return Double.NaN;
    }

    if ( M.length <= 0 )
    {
      System.out.println("ERROR: Parameter M has 0 rows in BestFitMatrix");
      return Double.NaN;
    }

    int k = q.length;
    int n = M.length;
    int m = M[0].length;
    double b[] = new double[k];           // Temporary storage for right hand
                                          // side of least squares problems

    double residual = 0;                  // sum of squares of residual errors
    double error    = 0;                  // residual error from one row of M.
    double Q[][]    = null;
    int    row      = 0;
    try 
    {
      // NOTE: The entries in q form the coefficient matrix for the least
      // squares fitting.  After the call to QR_factorization, we will have
      // factored the original q matrix to  Q*q, with the altered "q" matrix
      // being the factor R. 

      Q = QR_factorization( q );
                                          // Now solve for the best fit entries
                                          // in each row of matrix M
      for ( row = 0; row < n; row++ )  
      {
        for ( int i = 0; i < k; i++ )
          b[i] = r[i][row];

        error = QR_solve( q, Q, b );
        if ( Double.isNaN( error ) )
        {
          System.out.println("Singular matrix, in LinearAlgebra.BestFitMatrix");
          return Double.NaN;
        }

        residual += error * error;
        for ( int col = 0; col < m; col++ )  // copy result to row of solution M
          M[row][col] = b[col];
      }
    }
    catch ( Exception e )    // dump info to figure out which parameter was bad
    {
      System.out.println("Exception in BestFitMatrix " + e );
      e.printStackTrace();
      System.out.println("Row = " + row );
      System.out.println("M size = " + M.length + " by " + M[0].length );
      System.out.println("r size = " + r.length + " by " + r[0].length );
      System.out.println("q size = " + q.length + " by " + q[0].length );
      System.out.println("b length = " + b.length );
      System.out.println("Q size = " + Q.length + " by " + Q[0].length );
      return Double.NaN;
    }

    return Math.sqrt(residual);
  } 


 /* -------------------------- BestFitMatrix 2---------------------------- */
 /**
  *  Calculation of best fit matrix using the QRDecomposition from the Jama
  *  package, included for testing purposes.  This is slower for small
  *  matrices and does not return the residual errors so BestFitMatrix() 
  *  should generally be used.  This method has the same signature as 
  *  BestFitMatrix. 
  */
/*
  public static double BestFitMatrix2(double M[][], double q[][], double r[][])
  {
    QRDecomposition qr_d = new QRDecomposition( new Matrix(q) );   

    Matrix b_mat = new Matrix(r);
    b_mat = qr_d.solve( b_mat );

    for ( int row = 0; row < M.length; row++ )
    for ( int col = 0; col < M[0].length; col++ ) 
        M[row][col] = b_mat.getArray()[col][row];

    return 0;
  }
*/

  /* ---------------------- HouseholderTransform --------------------------- */
  /**
   *  Calculate the effect of the Householder transform Q = I - 2 u transp(u)
   *  on a vector y, given the unit vector u.  This function replaces y by Qy. 
   *
   *  @param  u   Unit vector that determines the Householder transformation, Q.
   *  @param  y   Vector to which the Householder transformation is applied. 
   *              y is altered by the method and on completion y = Qy.
   *
   */
   public static void HouseholderTransform( double u[], double y[] )
   {
      if ( u == null || y == null || u.length != y.length )
      {
        System.out.println("ERROR: Invalid vectors in Householder Tranform");
        System.out.println("u.length = " + u.length );
        System.out.println("y.length = " + y.length );
        return;
      }

      double sum = 0.0;
      for ( int i = 0; i < y.length; i++ )
        sum += u[i] * y[i];

      double c = 2.0 * sum;
      for ( int i = 0; i < y.length; i++ )
        y[i] -= c * u[i]; 
   }


  /* -------------------------- DotProduct --------------------------------- */
  /**
   *  Calculate the dot product of two vectors.
   *
   *  @param  u   First vector
   *  @param  v   Second vector 
   *
   *  @return  The dot product  u.v  provided that both u and v have the 
   *           same number of entries.  If u and v have different numbers
   *           of entries, the shorter length is used.  If either u or v has
   *           length 0, this returns NaN. 
   */
   public static double dotProduct( double u[], double v[] )
   {
      if ( u == null || v == null || u.length == 0 || v.length == 0 )
        return Double.NaN;

      int n = u.length;
      if ( v.length < u.length )
        n = v.length;

      double sum = 0.0;
      for ( int i = 0; i < n; i++ )
        sum += u[i] * v[i];

      return sum;         
   }

  /* --------------------------- Normalize --------------------------------- */
  /**
   *  Normalize the given vector to have length 1, by dividing by it's length
   *  if it's not the zero vector.
   *
   *  @param  v    The vector to be normalized.  If v is not the zero vector,
   *               each component of v is divided by the length of v.  If v is 
   *               the zero vector, it is not changed.
   */
 
  public static void normalize( double v[] )
  {
    double norm = 0.0;

    for ( int i = 0; i < v.length; i++ )
      norm += v[i]*v[i];

    if ( norm == 0 )              // if we have a zero vector, don't change it
      return;

    norm = Math.sqrt( norm );
                   
    for ( int i = 0; i < v.length; i++ )
      v[i] /= norm;
  }


  /* ----------------------------- Norm --------------------------------- */
  /**
   *  Calculate the L2 norm of a vector.
   *
   *  @param  v    The vector whose norm is to be calculated.
   *
   *  @return  The square root of the sum of the squares of the components
   *           of the specified vector.
   */

  public static double norm( double v[] )
  {
    double norm = 0.0;

    for ( int i = 0; i < v.length; i++ )
      norm += v[i]*v[i];

    return Math.sqrt( norm );
  }


  /* ---------------------------- main -------------------------------- */
  /* main program for test purposes only 
  */
   static public void main( String[] args )
   {
     double v[] = new double[3];

     v[0] = 1;
     v[1] = 4;
     v[2] = 8;

     normalize(v);
     for ( int i = 0; i < v.length; i++ )
       System.out.println( " v[" + i + "] = " + v[i] );

     double A[][] = new double[4][3];

     A[0][0] = 1;
     A[0][1] = 1;
     A[0][2] = 1;

     A[1][0] = 1;
     A[1][1] = 2;
     A[1][2] = 4; 

     A[2][0] = 1;
     A[2][1] = 3;
     A[2][2] = 9;

     A[3][0] = 1;
     A[3][1] = 4;
     A[3][2] = 16;

     boolean test_QR = false;
     if ( test_QR )
     {
       double U[][] = QR_factorization( A );

       System.out.println("R = ");
       for ( int i = 0; i < A.length; i++ )
       {
         for ( int j = 0; j < A[i].length; j++ )
           System.out.print( " "+ A[i][j] ); 
         System.out.println();
       }

       System.out.println("U = ");
       for ( int i = 0; i < U.length; i++ )
       {
         for ( int j = 0; j < U[i].length; j++ )
           System.out.print( " "+ U[i][j] );  
         System.out.println();
       }
    }
    else
    {
      double b[] = new double[4];
      b[0] = 3;
      b[1] = 5;
      b[2] = 7;
      b[3] = 9;
      double result = solve( A, b );
      for ( int i = 0; i < b.length; i++ )
        System.out.println( "b[" + i + "] = " + b[i] );

      System.out.println("returned value = " + result );
    } 

     double matrix[][] = {{ 1, 2 }, { 3, 4 }};
     System.out.println("matrix is: " );
     System.out.println("" + matrix[0][0] + "  " + matrix[0][1] );
     System.out.println("" + matrix[1][0] + "  " + matrix[1][1] );
     System.out.println();
     matrix = getInverse(matrix);
     System.out.println("Inverse is: " );
     System.out.println("" + matrix[0][0] + "  " + matrix[0][1] );
     System.out.println("" + matrix[1][0] + "  " + matrix[1][1] );
     System.out.println();

     Random rand = new Random();
     double  mat[][] = new double[3][3];
     for ( int i = 0; i < 3; i++ )
       for ( int j = 0; j < 3; j++ )
         mat[i][j] = rand.nextDouble();

     System.out.println("\n 3x3 matrix is : " );
     LinearAlgebra.print(mat);

     mat = getInverse(mat);
     System.out.println("\n 3x3 inverse matrix is : " );
     LinearAlgebra.print(mat);


     int N = 4;
     double  matN[][] = new double[N][N];
     for ( int i = 0; i < N; i++ )
       for ( int j = 0; j < N; j++ )
         matN[i][j] = rand.nextDouble();

     System.out.println("\n N = " + N );
     System.out.println("NxN matrix is : " );
     LinearAlgebra.print(matN);

     matN = getInverse(matN);
     System.out.println("\n NxN inverse matrix is : " );
     LinearAlgebra.print(matN);

     int n_calcs = 1000000;
     ElapsedTime timer = new ElapsedTime();
     timer.reset(); 
     for ( int i = 0; i < n_calcs; i++ )
        mat = getInverse(mat);

     System.out.println("Time to do " + n_calcs + 
                        " 3x3 matrix inversions: " + timer.elapsed() );  
   }

}
