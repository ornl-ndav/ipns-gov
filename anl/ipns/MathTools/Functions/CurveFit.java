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
 *  Revision 1.4  2002/11/27 23:15:47  pfpeterson
 *  standardized header
 *
 */

package DataSetTools.math;

import DataSetTools.util.*;

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
