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
 *  $Log$
 *  Revision 1.1  2002/04/11 21:00:04  dennis
 *  Class for polynomials of one variable controlled by
 *  parameters giving the coefficients of the polynomial.
 *
 */

package  DataSetTools.functions;

import java.io.*;
import DataSetTools.math.*;
import DataSetTools.dataset.*;
import DataSetTools.viewer.*;

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
   public Polynomial( float coefficients[] )
   {
     super( "Polynomial", coefficients, new String[coefficients.length] );
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
        sum += x * sum + parameters[i];
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
      DataSet ds = new DataSet( "Sample Polynomials", "Initial Version" );

      XScale x_scale = new UniformXScale( -5, 5, 500 );
      OneVarFunction polynomial; 
      Data polynomial_data; 
      for ( int i = 0; i < 100; i++ )
      {
        float a = -5+i/10.0f;
        float coefficients[] = { a*a, -2*a, 1 };
        polynomial      = new Polynomial( coefficients );
        polynomial_data = new FunctionModel( x_scale, polynomial, i );
        ds.addData_entry( polynomial_data );
      }
      ViewManager vm = new ViewManager( ds, IViewManager.IMAGE );
    }
}
