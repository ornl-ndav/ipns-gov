/*
 * File:  SumFunction.java
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
 *  Revision 1.3  2002/06/17 22:15:13  dennis
 *  Now uses doubles for calculation.
 *
 *  Revision 1.2  2002/04/19 16:53:48  dennis
 *  Added more javadocs.  Also added methods numFunctions() and
 *  getFunction() to allow access to the individual component
 *  functions.  This will be useful for changing the domains
 *  of the component functions, after constructing the sum function.
 *
 *  Revision 1.1  2002/04/11 21:00:55  dennis
 *  Class for parameterized functions of one variable that are
 *  sums of other functios of one variable.
 *
 */

package  DataSetTools.functions;

import java.io.*;
import DataSetTools.math.*;
import DataSetTools.dataset.*;
import DataSetTools.viewer.*;

/**
 *  This class represents new OneVarParameterizedFunctions defined as the
 *  sum of component OneVarParameterizedFunctions.  
 */

public class SumFunction extends    OneVarParameterizedFunction
                         implements Serializable
{
  private IOneVarParameterizedFunction functions[];


  /**
   *  Construct a new parameterized function as the sum of a collection of
   *  individual parameterized functions.  
   *
   *  @param functions   Array containing the component functions that will
   *                     form this sum function.
   */
   public SumFunction( IOneVarParameterizedFunction functions[] )
   {
     super( "Sum of Functions", null, null );
     if ( functions != null ) 
       this.functions = functions;
     else
       this.functions = new IOneVarParameterizedFunction[0];
   }


  /**
   *  Get the number of component functions that make up this SumFunction.
   *
   *  @return the number of component functions for this function. 
   */
  public int numFunctions()
  {
    if ( functions == null )
      return 0;

    return functions.length;
  }


  /**
   *  Get a reference to one of the component functions that make up this 
   *  SumFunction.
   *
   *  @param  index  The position of the requested component function in the
   *                 list of components specified when this sum function was
   *                 constructed. 
   *
   *  @return A reference to the specified component function, if it exists,
   *          or null if it does not exist. 
   */
  public IOneVarParameterizedFunction getFunction( int index )
  {
    if ( functions == null )
      return null;

    if ( index < 0 || index >= functions.length )
      return null;
 
    return functions[index];
  }


  /**
   *  Get the total number of parameters for all components of this function.
   *
   *  @return the sum of the number of parameters for the components of
   *          this function.
   */
  public int numParameters()
  {
    int n = 0; 
    for ( int i = 0; i < functions.length; i++ )
      n += functions[i].numParameters();
    return n;
  }


  /**
   *  Get a list of the current parameter values for this function by 
   *  combining the lists of values for the component functions, in order. 
   *
   *  @return  Array containing the combined list of parameter values for
   *           all component functions of this sum function.
   */
  public double[] getParameters()
  {
    int    n_params = numParameters();
    double params[] = new double[n_params];

    int index = 0;
    for ( int i = 0; i < functions.length; i++ )
    {
      double current_params[] = functions[i].getParameters();
      for ( int k = 0; k < current_params.length; k++ ) 
      {
        params[index] = current_params[k];
        index++;
      }  
    }
    return params;
  }

  /**
   *  Set new values for the list of parameters for this function.  The 
   *  specified values will be copied into the parameters for the component
   *  functions in order.  For example, if there are two component functions,
   *  f1 and f2 with 3 and 4 parameters respectively, then the parameters
   *  array should have 7 values.  In that case, the first 3 will specify
   *  paramter values for f1 and the next 4 will specify parameter values for  
   *  f2.  If there are not enough parameters in the array, the specified 
   *  parameters will still be used in order to set the corresponding 
   *  parameter values. 
   *
   *  @param  parameters  Array containing values to copy into the list of
   *                      parameter values for this function.
   */
  public void setParameters( double parameters[] )
  {
    if ( parameters == null )
      return;

    int n_params = numParameters();
    if ( n_params > parameters.length )
      n_params = parameters.length;

    int i = 0;
    int k = 0;
    double current_params[] = functions[i].getParameters();  
    for ( int index = 0; index < n_params; index++ )
    {
      if ( k >= current_params.length )
      {    
        k = 0;
        i++;
        current_params = functions[i].getParameters();  
      }
      current_params[k] = parameters[index];
      k++;
    }
  }


  /**
   *  Get a reference to the list of parameter names for this function by
   *  concatenating the lists of parameter names for each of the component
   *  functions.
   *
   *  @return  Reference to the combined lists of parameter names for this 
   *           function.
   */
  public String[] getParameterNames()
  {
    int n_params = numParameters();
    String names[] = new String[n_params];

    int index = 0;
    for ( int i = 0; i < functions.length; i++ )
    {
      String current_names[] = functions[i].getParameterNames();
      for ( int k = 0; k < current_names.length; k++ )
      {
        names[index] = current_names[k];
        index++;
      }
    }
    return names;
  }


  /**
   *  Evaluate the y-value of this sum function at the specified (double) i
   *  x-value.
   *
   *  @param  x  the point at which the sum is to be evaluated
   *
   *  @return the sum of the values of the components of this sum function, 
   *          at the specified point.
   */
  public double getValue( double x )
  {
    double sum = 0;
    for ( int i = 0; i < functions.length; i++ )
      sum += functions[i].getValue( x );     
    return sum;
  }


 /* -------------------------------------------------------------------------
  *
  * MAIN  ( Basic main program for testing purposes only. )
  *
  */
    public static void main(String[] args)
    {
      DataSet ds = new DataSet( "Sample Sum", "Initial Version" );

      XScale x_scale = new UniformXScale( -5, 5, 500 );

      double coefficients[] = { 4, -4, 1 };
      IOneVarParameterizedFunction polynomial = new Polynomial( coefficients );
      IOneVarParameterizedFunction gaussian = 
                               new Gaussian(-2, 25, Gaussian.SIGMA_TO_FWHM_D);
      IOneVarParameterizedFunction functions[] = 
                                  new IOneVarParameterizedFunction[2];
      functions[0] = polynomial; 
      functions[1] = gaussian; 
      IOneVarParameterizedFunction sum = new SumFunction( functions );
      Data sum_data = new FunctionModel( x_scale, sum, 1 );
      ds.addData_entry( sum_data );
      ViewManager vm = new ViewManager( ds, IViewManager.IMAGE );

      double x = 1;
      String p_names[] = sum.getParameterNames();
      double p_vals[]  = sum.getParameters();
      double p_ders[]  = sum.get_dFda( x );
      System.out.println("names length = " + p_names.length );
      System.out.println("ders  length = " + p_ders.length );
      System.out.println("Function Name: " + sum.getName() ); 
      System.out.println("Parameter Names, values, derivatives at" + x + " :");
      for ( int i = 0; i < sum.numParameters(); i++ )
        System.out.println( p_names[i] + ",    " + 
                            p_vals [i] + ",    " + 
                            p_ders [i] );

      System.out.println();
      System.out.println("Evaluating: ");
      for ( int i = -3; i < 4; i++ )
      {
        x = i;
        System.out.println("       x = " + x + 
                           "    f(x) = " + sum.getValue(x) +
                           "   f'(x) = " + sum.get_dFdx(x) ); 
      } 
    }
}
