/*
 * File:  Chebyshev.java
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
 *  Revision 1.2  2005/02/17 23:16:43  dennis
 *  Fixed name of parameter in javadoc.
 *
 *  Revision 1.1  2005/02/15 21:42:01  dennis
 *  Class to evaluate Chebyshev polynomials of the first kind
 *  on the interval [-1,1], using the recurrence relationship.
 *
 */

package gov.anl.ipns.MathTools.Functions;

import java.io.*;

/**
 * This class implements a Chebyshev polynomial of the first kind for
 * x in the interval [-1,1].  While the polynomial can of course be 
 * used outside of the interval [-1,1], over the interval [-1,1] the
 * Chebyshev polynomials of the first kind are orthogonal with respect
 * the a weighted inner product: 
 *
 *  <f,g>=integral[-1,1]f(x)g(x)/sqrt(1-x^2)dx
 */

public class ChebyshevPolynomial extends OneVarParameterizedFunction
                                         implements Serializable
{
  /**
   *  Construct a new ChebyshevPolynomial function object with the specified
   *  degree. 
   *
   *  @param  degree The degree of the polynomial 
   */
   public ChebyshevPolynomial( int degree )
   {
     super( "ChebyshevPolynomial", new double[1], new String[1] );
     parameter_names[0] = "Degree"; 
     parameters[0] = degree;
   }

  /**
   *  Evaluate the Chebyshev polynomial the specified (double) x-value.
   *
   *  @param  x  the point at which the Chebyshev polynomial is evaluated
   *
   *  @return the value of the polynomial, at the specified point.
   */
  public double getValue( double x )
  {
    int n = (int)parameters[0];

    if ( n == 0 )
      return 1.0;
    else if ( n == 1 )
      return x;

    double val0 = 1;
    double val1 = x;
    double val  = 2*x*val1 - val0;
    for ( int degree = 3; degree <= n; degree++ )
    {
      val0 = val1;
      val1 = val;
      val  = 2*x*val1 - val0;
    } 
    return val;
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
      System.out.println("Evaluating Chebyshev polynomial");

      ChebyshevPolynomial poly = new ChebyshevPolynomial( 10 );

      double x  = 0.5;
      float  xf = 0.5f;
      System.out.println("The double value at " +x+ " is " +poly.getValue(x) ); 
      System.out.println("The float value at " +x + " is " +poly.getValue(xf));

      double[] xs = { -1, -0.5,  0, 0.5,  1, 0.1 }; 
      double[] ys = poly.getValues( xs );
      System.out.println("Double Values are:");
      for ( int i = 0; i < xs.length; i++ )
        System.out.println("xs[i] = " + xs[i] + ", " + "ys[i] = " + ys[i] );

      float[] xfs = { -1, -0.5f, 0, 0.5f, 1 }; 
      float[] yfs = poly.getValues( xfs );
      System.out.println("Float Values are:");
      for ( int i = 0; i < xfs.length; i++ )
        System.out.println("xfs[i] = " + xfs[i] + ", " + "yfs[i] = " + yfs[i] );
    }
}
