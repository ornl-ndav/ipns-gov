/*
 * File:  Polynomial.java
 *
 * Copyright (C) 2002, Dennis Mikkelson
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
 *  Revision 1.5  2004/03/19 17:24:26  dennis
 *  Removed unused variables
 *
 *  Revision 1.4  2004/03/12 02:00:53  dennis
 *  Moved to package gov.anl.ipns.MathTools.Functions
 *
 *  Revision 1.3  2002/11/27 23:14:24  pfpeterson
 *  standardized header
 *
 *  Revision 1.2  2002/06/17 22:16:32  dennis
 *  Now uses doubles for calculation and includes calculation of derivative.
 *
 *  Revision 1.1  2002/04/11 21:00:04  dennis
 *  Class for polynomials of one variable controlled by
 *  parameters giving the coefficients of the polynomial.
 *
 */

package gov.anl.ipns.MathTools.Functions;

import java.io.*;

/**
 * This class implements a polynomial as a parameterized function of one 
 * variable 
 */

public class Polynomial extends    OneVarParameterizedFunction
                        implements Serializable
{
  /**
   *  Construct a new polynomial function object with the specified
   *  characteristics 
   */
   public Polynomial( double coefficients[] )
   {
     super( "Polynomial", coefficients, new String[coefficients.length] );
     for ( int i = 0; i < parameter_names.length; i++ )
       parameter_names[i] = "a"+i;
   }


  /**
   *  Evaluate the y-value of the polynomial at the specified (double) x-value.
   *
   *  @param  x  the point at which the polynomial is to be evaluated
   *
   *  @return the value of the polynomial, at the specified point, provided 
   *          the point is in the currently specified domain.
   */
  public double getValue( double x )
  {
    if ( domain.contains( (float)x ) )
    {
      double sum = 0.0;  
      for ( int i = parameters.length - 1; i >= 0; i-- )
        sum = x * sum + parameters[i];
      return sum;   
    }
    else
      return 0; 
  }

  /**
   *  Get the derivative of this polynomial with respect to x.
   *
   *  @param  x     The value at which the derivative is evaluated. 
   *
   *  @return  the derivative at x.
   */
  public double get_dFdx( double x )
  {
    if ( domain.contains( (float)x ) )
    {
      double sum = 0.0;
      for ( int i = parameters.length - 2; i >= 0; i-- )
        sum = x * sum + (i+1) * parameters[i+1];
      return sum;
    }
    else
      return 0;
  }


 /* -------------------------------------------------------------------------
  *
  * MAIN  ( Basic main program for testing purposes only. )
  *
  */
    public static void main(String[] args)
    {
      OneVarParameterizedFunction polynomial; 

      double coef[] = { 1, 2, 3, 4 };
      polynomial = new Polynomial( coef );

      String par_name[] = polynomial.getParameterNames();

      System.out.println( "Polynomial: " );
      for ( int i = 0; i < par_name.length; i++ )
        System.out.println(par_name[i] + " = " + coef[i] );

      double x;
      for ( int i = 0; i <= 10; i++ )
      {
        x = i;
        System.out.println( "x        = " + x + 
                            ", p(x)   = " + polynomial.getValue(x) +
                            ", p'(x)  = " + polynomial.get_dFdx(x) +
                            ", fp'(x) = " + polynomial.get_dFdx((float)x) );
      }

      x = 3;
      double derivs[] = polynomial.get_dFda( x );
      System.out.println( "At " + x + 
                          ", the derivatives relative to the coeffs are:");
      for ( int i = 0; i < par_name.length; i++ )
        System.out.println("deriv WRT " + par_name[i] + " = " + derivs[i]);

/*
      
      DataSet ds = new DataSet( "Sample Polynomials", "Initial Version" );

      XScale x_scale = new UniformXScale( -5, 5, 500 );
      Data polynomial_data; 
      for ( int i = 0; i < 100; i++ )
      {
        double a = -5+i/10.0f;
        double coefficients[] = { a*a, -2*a, 1 };
        polynomial      = new Polynomial( coefficients );
        polynomial_data = new FunctionModel( x_scale, polynomial, i );
        ds.addData_entry( polynomial_data );
      }
      ViewManager vm = new ViewManager( ds, IViewManager.IMAGE );
*/
    }
}
