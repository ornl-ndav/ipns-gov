/*
 * @(#)CurveFit.java
 *
 *  Programmer:  Dennis Mikkelson
 *
 *  Basic curve fitting operations such as least squares fitting of a 
 *  polynomial of arbitrary degree.
 * 
 *  $Log$
 *  Revision 1.2  2001/01/29 21:05:20  dennis
 *  Now uses CVS revision numbers.
 *
 *  Revision 1.1  2000/11/17 23:52:37  dennis
 *  Least squares fit of polynomial
 *
 */

package DataSetTools.math;

import DataSetTools.util.*;

public final class CurveFit 
{
  /**
   * Don't let anyone instantiate this class.
   */
  private CurveFit() {}

  /* ----------------------------- Polynomial --------------------------- */
  /**
   *  Calculate the coefficients of the polynomial that best fits the given 
   *  x and y values in the least squares sense.
   *
   *  @param   x      list of x values of the points to fit.  There should be
   *                  as many x values as y values.  If not, smaller of the
   *                  two lengths is used.
   *
   *  @param   y      list of y values of the points to fit.  There should be
   *                  as many y values as x values.  If not, smaller of the
   *                  two lengths is used.
   *                  
   *  @param   coeff  The size of this array determines the degree of the
   *                  polynomial to fit.  The array will be filled with the 
   *                  coefficients of the best fit polynomial.  The number of 
   *                  coefficients must be no more than the number of data
   *                  points, ( x[i], y[i] ).
   *
   *  @return  The total residual error, if the parameters are correct.  If
   *           the parameters don't properly specify points (x,y) or the 
   *           number of coeffients requested is improper, this returns NaN.
   */
   public static double Polynomial( double x[], double y[], double coeff[] )
   {
     if ( x == null || y == null || coeff == null )
     {
        System.out.println("ERROR: null array in CurveFit.Polynomial");
        return Double.NaN;
     }

     int n_points = x.length;
     if ( y.length < x.length )
       n_points = y.length;

     if ( coeff.length > n_points )
     {
        System.out.println(
                  "ERROR: not enough data points in CurveFit.Polynomial");
        return Double.NaN;
     }
                                            // build the linear equations Ax=b
                                            // representing the overdetermined
                                            // least squares fitting problem
     double A[][] = new double[ n_points ][ coeff.length ];
     double b[]   = new double[ n_points ];

     for ( int row = 0; row < n_points; row++ )
     {
       A[row][0] = 1.0;
       for ( int col = 1; col < coeff.length; col++ )
         A[row][col] = x[row] * A[row][col-1];

       b[row] = y[row];
     }

     double error = LinearAlgebra.solve( A, b );
     if ( error != Double.NaN )             // copy the coefficients to coeff[]
     {
       for ( int i = 0; i < coeff.length; i++ )
         coeff[i] = b[i];
     } 

     return error;
   }

  /* ---------------------------- main -------------------------------- */
  /* main program for test purposes only
  */
   static public void main( String[] args )
   {
     double x[] = { 1,  2,  3,  4 };
     double y[] = { 6, 17, 34, 57 };
     double coef[] = new double[3];

     System.out.println("Result =" + Polynomial( x, y, coef ) );
     System.out.println("Coefficients are:");
     for ( int i = 0; i < coef.length; i++ )
      System.out.println( "" + coef[i] );
   }
}
