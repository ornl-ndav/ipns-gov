/*
 * File:  ChebyshevSum.java
 *
 * Copyright (C) 2005, Dennis Mikkelson
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
 *  Revision 1.1  2005/02/15 22:12:24  dennis
 *  Class representing a sum of Chebyshev polynomials of the
 *  first kind, c0 T0(x) + c1 T1(x) + c2 T2(x) + ....
 *  given the coefficient c0, c1, c2,...
 *
 *
 */

package gov.anl.ipns.MathTools.Functions;

import java.io.*;

/**
 * This class implements a sum of multiples of Chebyshev polynomials,
 * c0 T0(x) + c1 T1(x) + c2 T2(x) + ....  given the coefficients 
 * c0, c1, c2, ... as parameters.
 */

public class ChebyshevSum extends    OneVarParameterizedFunction
                          implements Serializable
{
  /**
   *  Construct a new sum of Chebyshev polynomials, given the coefficients
   *  c0, c1, c2,...
   *
   *  @param coefficients  Array containing the coefficients c0, c1, c2,...
   *                       for the sum c0 T0(x) + c1 T1(x) + c2 T2(x)...
   */
   public ChebyshevSum( double coefficients[] )
   {
     super( "ChebyshevSum", coefficients, new String[coefficients.length] );
     for ( int i = 0; i < parameter_names.length; i++ )
       parameter_names[i] = "c"+i;
   }


  /**
   *  Evaluate the sum polynomial at the specified (double) x-value.
   *
   *  @param  x  the point at which the sum polynomial is to be evaluated
   *
   *  @return the value of the sum polynomial, at the specified point, provided 
   *          the point is in the currently specified domain.
   */
  public double getValue( double x )
  {
    if ( domain.contains( (float)x ) )
    {
      int    n   = parameters.length - 1;

      if ( n == 0 )
        return parameters[0];

      double sum = parameters[0] + x * parameters[1];
      if ( n == 1 )
        return sum;

      double val0 = 1;
      double val1 = x;
      double val  = 2*x*val1 - val0;
      sum = sum + parameters[2] * val;
      for ( int degree = 3; degree <= n; degree++ )
      {
        val0 = val1;
        val1 = val;
        val  = 2*x*val1 - val0;
        sum  = sum + parameters[degree] * val;
      }
      return sum;
    }
    else
      return 0; 
  }

  /**
   *  Evaluate the Chebyshev polynomial the specified (float) x-value.
   *
   *  @param  x  the point at which the Chebyshev polynomial is evaluated
   *
   *  @return the value of the polynomial, at the specified point.
   */
  public float getValue( float x )
  {
    return (float)getValue( (double)x );
  }


 /* -------------------------------------------------------------------------
  *
  * MAIN  ( Basic main program for testing purposes only. )
  *
  */
    public static void main(String[] args)
    {
      OneVarParameterizedFunction cheb_sum; 

      double coef[] = { 2, 3, 4, 5 };
      cheb_sum = new ChebyshevSum( coef );

      String par_name[] = cheb_sum.getParameterNames();

      System.out.println( "ChebyshevSum: " );
      for ( int i = 0; i < par_name.length; i++ )
        System.out.println(par_name[i] + " = " + coef[i] );

      double x;
      for ( int i = 0; i <= 4; i++ )
      {
        x = i/2.0 - 1;
        System.out.println( "x        = " + x + 
                            ", p(x)   = " + cheb_sum.getValue(x) +
                            ", p'(x)  = " + cheb_sum.get_dFdx(x) +
                            ", fp'(x) = " + cheb_sum.get_dFdx((float)x) );
      }

      x = 3;
      double derivs[] = cheb_sum.get_dFda( x );
      System.out.println( "At " + x + 
                          ", the derivatives relative to the coeffs are:");
      for ( int i = 0; i < par_name.length; i++ )
        System.out.println("deriv WRT " + par_name[i] + " = " + derivs[i]);

    }
}
