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
 *  Programmer:  Dennis Mikkelson
 *
 *  Basic linear algebra operations such as QR factorization and solution of
 *  system of linear equations using QR factorization
 * 
 *  $Log$
 *  Revision 1.3  2001/04/25 20:56:33  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.2  2001/01/29 21:05:42  dennis
 *  Now uses CVS revision numbers.
 *
 *  Revision 1.1  2000/11/17 23:51:52  dennis
 *  Basic linear algebra functions, QR factorization and linear
 *  system solution.
 *
 */

package DataSetTools.math;

import DataSetTools.util.*;

/**
 *  Basic linear algebra operations such as dot product, and solution of
 *  system of linear equations.
 */
public final class LinearAlgebra 
{
  /*
   * Don't let anyone instantiate this class.
   */
  private LinearAlgebra() {}

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
    * @ return  The return value represents the residual error in the least
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
      error = b[i]*b[i];

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

    double norm;
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

      Normalize( U[col] );

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
   public static double DotProduct( double u[], double v[] )
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
 
  public static void Normalize( double v[] )
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


  /* ---------------------------- main -------------------------------- */
  /* main program for test purposes only 
  */
   static public void main( String[] args )
   {
     double v[] = new double[3];

     v[0] = 1;
     v[1] = 4;
     v[2] = 8;

     Normalize(v);
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
   }

}
