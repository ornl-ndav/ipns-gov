/*
 * File:  CurveFit.java
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
 * Modified:
 * 
 *  $Log$
 *  Revision 1.9  2005/02/17 03:24:37  dennis
 *  Added method to compute coefficients c0, c1, c2, ... for the
 *  Chebyshev expansion of a sampled function f(x) on the interval
 *  [-1,1].  f(x) ~ c0 T0(x) + c1 T1(x) + c2 T2(x) + ... where
 *  T0, T1, T2,... are the Chebyshev polynomials of the first kind.
 *
 *
 *  Revision 1.8  2004/07/16 19:10:52  dennis
 *  Fixed improper comparison with Float.NaN
 *
 *  Revision 1.7  2004/03/12 02:15:48  dennis
 *  Moved to package gov.anl.ipns.MathTools.Functions;
 *
 *  Revision 1.6  2003/10/15 23:59:02  dennis
 *  Fixed javadocs to build cleanly with jdk 1.4.2
 *
 *  Revision 1.5  2003/07/22 22:14:40  dennis
 *  Added option to use "statistical weighting" by 1/sqrt(|y[i]|)
 *  in the method to fit a polynomial.
 *  Added convenience method to efficiently evaluate a polynomial,
 *  defined by an array of coefficients, at a list of x values.
 *
 *  Revision 1.4  2002/11/27 23:15:47  pfpeterson
 *  standardized header
 *
 */

package gov.anl.ipns.MathTools.Functions;

import gov.anl.ipns.MathTools.*;


/**
 *  Basic curve fitting operations such as least squares fitting of a 
 *  polynomial of arbitrary degree.
 */
public final class CurveFit 
{
  /*
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
   *  @param  stat_w  If true, this will do a weighted least squares
   *                  approximation, where the weights are 
   *                  Wi=1/sqrt(|y[i]|) if
   *                  yi is not zero, and is 1 if yi is zero.
   *
   *  @return  The total residual error, if the parameters are correct.  If
   *           the parameters don't properly specify points (x,y) or the 
   *           number of coeffients requested is improper, this returns NaN.
   */
   public static double Polynomial( double  x[],
                                    double  y[], 
                                    double  coeff[],
                                    boolean stat_w   )
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
     double w[]   = new double[ n_points ];

     for ( int i = 0; i < n_points; i++ )
     {
       if ( stat_w && y[i] != 0 )
         w[i] = 1/Math.sqrt(Math.abs(y[i]));
       else
         w[i] = 1;  
     }

     for ( int row = 0; row < n_points; row++ )
     {
       A[row][0] = 1.0;
       for ( int col = 1; col < coeff.length; col++ )
         A[row][col] = x[row] * A[row][col-1];

       for ( int col = 0; col < coeff.length; col++ )
         A[row][col] *= w[row];

       b[row] = y[row] * w[row];
     }

     double error = LinearAlgebra.solve( A, b );
     if ( !Double.isNaN( error ) )          // copy the coefficients to coeff[]
     {
       for ( int i = 0; i < coeff.length; i++ )
         coeff[i] = b[i];
     } 

     return error;
   }


  /**
   *  Convenience method for evaluating a polynomial, specified by it's
   *  array of coefficients, at a list of x values.
   *
   *  @param  x     Array of x values where the polynomial is to be evaluated.
   *  @param  y     Array in which the values will be stored; this must have
   *                 at least as many entries as x.
   *  @param  coef  The list of coefficents A0, A1, A2, ... etc.
   */
  static public void eval( double x[], double y[], double coef[] )
  {
     double sum;
     int    n = coef.length - 1;

     for ( int i = 0; i < x.length; i++ )
     {
       sum = 0;
       for ( int k = n; k >= 0; k-- )    // Horner's rule
         sum = sum * x[i] + coef[k];
       y[i] = sum;
     }
  }


  /* ----------------------------- Chebyshev --------------------------- */
  /**
   *  Calculate the coefficients of the Chebyshev sum polynomial that 
   *  best fits the given x and y values in the weighted least squares sense.
   *  The array of x values must range from -1 to 1, in order.  
   *
   *  @param   x      list of x values of the points to fit.  There must be
   *                  as many x values as y values.  The first x value must
   *                  be -1, the last x value must be 1.
   *
   *  @param   y      list of y values of the points to fit.  There must be
   *                  at least as many y values as x values.  If there are 
   *                  more y values than x values, the extra y values will 
   *                  be ignored. 
   *                  
   *  @param   coeff  The size of this array determines the degree of the
   *                  polynomial to fit.  The array will be filled with the 
   *                  coefficients ci for the ith Chebyshev polynomial. 
   */
   public static void Chebyshev( double  x[],
                                 double  y[],
                                 double  coeff[] )
   {
     if ( x == null || y == null || coeff == null )
     {
       System.out.println("ERROR: null array in CurveFit.Chebyshev");
       return;
     }

     if ( x.length == 0 || y.length == 0 || coeff.length == 0 )
     {
       System.out.println("ERROR: zero length array in CurveFit.Chebyshev");
       return;
     }

     if ( x[0] != -1 || x[x.length-1] != 1 )
     {
       System.out.println("ERROR: interval not [-1,1] in CurveFit.Chebyshev");
       System.out.println(" inteval = " + x[0] + ", " + x[x.length] );
       return;
     }  

     if ( y.length < x.length )
     {
       System.out.println("ERROR: fewer y's than x's in CurveFit.Chebyshev");
       return;
     }

     for ( int degree = 0; degree < coeff.length; degree++ )
       coeff[ degree ] = ChebyshevCoeff( x, y, degree );
   }


  /* -------------------------- ChebyshevCoeff --------------------------- */
  /**
   *  Calculate the weighted inner product of the Nth degree Chebyshev 
   *  polynomial and the specfied sampled data for a function, f, over 
   *  the interval [-1,1].  The inner product is computed using the
   *  trapezoidal rule.
   *
   *  @param   x      list of x values in [-1,1] of the data points. There must
   *                  be as many x values as y values.  If not, smaller of the
   *                  two lengths is used.  The first x value MUST be -1 and
   *                  the last x value MUST be 1.
   *
   *  @param   y      list of y values of the points to fit.  There should be
   *                  as many y values as x values.  If not, smaller of the
   *                  two lengths is used.
   *                  
   *  @param   n      The degree of the Chebyshev polynomial, Tn, that is used.
   *
   *  @return  The normalized inner product <f,Tn> defined as the integral 
   *           from -1 to 1 of  f(x)Tn(x)/sqrt(1-x^2).  The Chebyshev 
   *           polynomials are orthogonal with respect to this inner product, 
   *           so this "essentially" gives the coefficient on Tn in the 
   *           expansion of f() as a sum of Chebyshev polynomials.   The 
   *           normalization factor is 1/PI for T0, and 2/PI
   *           for Tn, n>0.
   */
   public static double ChebyshevCoeff( double  x[],
                                        double  y[],
                                        int     n  )
   {
     if ( x == null || y == null )
     {
        System.out.println("ERROR: null array in CurveFit.ChebyshevCoeff");
        return Double.NaN;
     }

     int n_points = x.length;
     if ( y.length < x.length )
       n_points = y.length;

     ChebyshevPolynomial cheb = new ChebyshevPolynomial(n);
     
     // Now approximate the inner product of the tabulated function, with the
     // nth degree Chebyshev polynomial.  The integral is an improper integral
     // so we can't use the values at the endpoints directly.  We use a 
     // trapezoidal rule to approximate the integral, numerically.  This is
     // straightforward, except for the first and last interval, which would
     // require evaluating 1/sqrt(1-x^2) where x is 1 or -1.  These two 
     // trapezoids are evaluated separately, by evaluating the improper 
     // integral of a linear function divided by the weight function.  The
     // x,y values are linearly interpolated over the interval to obtain 
     // the linear function to integrate.
     //
                                          // use "ordinary" trapezoidal rule
     double sum = 0;                      // over the interior intervals
     double f0,
            f1;
     f0 = (y[1] * cheb.getValue(x[1])) / Math.sqrt(1-x[1]*x[1]);
     for ( int i = 1; i < n_points-2; i++ )
     {
       f1 = (y[i+1] * cheb.getValue(x[i+1])) / Math.sqrt(1-x[i+1]*x[i+1]);
       sum += (f0 + f1) / 2.0 * (x[i+1] - x[i]);
       f0 = f1;
     }
                                          // add exact value for integral of
                                          // linear interpolation polynomial
     double x0  = -1;                     // over the first subinterval.
     double x1  = x[1];
     f0  = y[0] * cheb.getValue(x0);
     f1  = y[1] * cheb.getValue(x1);

     double m = (f1 - f0) / (x1 - x0);
     double b = (f0 * x1 - x0 * f1) / (x1 - x0);
     sum += -m * Math.sqrt( 1 - x1 * x1 ) + b * (Math.PI - Math.acos(x1)); 

                                          // add exact value for integral of
                                          // linear interpolation polynomial
     x0  = x[n_points-2];                 // over the last subinterval.
     x1  = 1;
     f0  = y[n_points-2] * cheb.getValue(x0);
     f1  = y[n_points-1] * cheb.getValue(x1);

     m = (f1 - f0) / (x1 - x0);
     b = (f0 * x1 - x0 * f1) / (x1 - x0);
     sum += m * Math.sqrt( 1 - x0 * x0 ) + b * Math.acos(x0);   
     
     if ( n == 0 )
       return sum * ( 1 / Math.PI );
     else 
       return sum * ( 2 / Math.PI );
   }


  /* ---------------------------- main -------------------------------- */
  /* 
   * main program for test purposes only
   *
   */
   static public void main( String[] args )
   {
     final double DELTA = 1e-5;
                                                      // some sample data
     double x[] = { 1,  2,  3,  4,  5,  6 };
     double y[] = { 2,  1, 10, 15, 26, 35 };
     double coef[] = new double[3];
     double val[] = new double[ x.length ];
                                                      // test UNWEIGHTED fit
                                                      // option
     System.out.println("Result =" + Polynomial( x, y, coef, false ) );
     System.out.println("Unweighted Coefficients are:");
     for ( int i = 0; i < coef.length; i++ )
      System.out.println( "" + coef[i] );
                                                       // show the values
     System.out.println("Unweighted approximation is" );
     eval( x, val, coef ); 
     for ( int i = 0; i < x.length; i++ )
       System.out.println("x = " + x[i] + " y = " + y[i] + " val = " + val[i] );
                                                      
                                                       // calculate chisq with
                                                       // these and with nearby
     double chi_sq = 0;                                // values 
     for ( int i = 0; i < x.length; i++ )
       chi_sq += (y[i] - val[i]) * (y[i] - val[i]);
     System.out.println("Optimal: chi_sq      = " + chi_sq );

     for ( int i = 0; i < coef.length; i++ )
       coef[i] += DELTA;
     eval( x, val, coef ); 
     chi_sq = 0;
     for ( int i = 0; i < x.length; i++ )
       chi_sq += (y[i] - val[i]) * (y[i] - val[i]);
     System.out.println("Shifted UP: chi_sq   = " + chi_sq );

     for ( int i = 0; i < coef.length; i++ )
       coef[i] -= 2*DELTA;
     eval( x, val, coef ); 
     chi_sq = 0;
     for ( int i = 0; i < x.length; i++ )
       chi_sq += (y[i] - val[i]) * (y[i] - val[i]);
     System.out.println("Shifted DOWN: chi_sq = " + chi_sq );

                                                      // test WEIGHTED fit
                                                      // option
     System.out.println("Result =" + Polynomial( x, y, coef, true ) );
     System.out.println("Weighted Coefficients are:");
     for ( int i = 0; i < coef.length; i++ )
      System.out.println( "" + coef[i] ); 

                                                       // show the values
     System.out.println("Weighted approximation is" );
     eval( x, val, coef ); 
     for ( int i = 0; i < x.length; i++ )
       System.out.println("x = " + x[i] + " y = " + y[i] + " val = " + val[i] );

                                                       // calculate chisq with
                                                       // these and with nearby
     chi_sq = 0;                                       // values
     for ( int i = 0; i < x.length; i++ )
       chi_sq += (y[i] - val[i]) * (y[i] - val[i]) / y[i];
     System.out.println("Optimal: chi_sq      = " + chi_sq );

     for ( int i = 0; i < coef.length; i++ )
       coef[i] += DELTA;
     eval( x, val, coef ); 
     chi_sq = 0;
     for ( int i = 0; i < x.length; i++ )
       chi_sq += (y[i] - val[i]) * (y[i] - val[i]) / y[i];
     System.out.println("Shifted UP: chi_sq   = " + chi_sq );

     for ( int i = 0; i < coef.length; i++ )
       coef[i] -= 2*DELTA;
     eval( x, val, coef ); 
     chi_sq = 0;
     for ( int i = 0; i < x.length; i++ )
       chi_sq += (y[i] - val[i]) * (y[i] - val[i]) / y[i];
     System.out.println("Shifted DOWN: chi_sq = " + chi_sq );

                                                       // test the Chebyshev fit
     x = new double[ 101 ];
     y = new double[ 101 ];
     double coeff[] = new double[ 10 ];
     ChebyshevPolynomial test = new ChebyshevPolynomial( 3 );

     for ( int i = 0; i < x.length; i++ )
     {
       x[i] = -1 + 2 * i/(double)(x.length-1);
       y[i] = test.getValue( x[i] ); 
     }

     Chebyshev( x, y, coeff );
     for ( int i = 0; i < coeff.length; i++ )
       System.out.println("i = " + i + " coeff = " + coeff[i] );
   }
}
