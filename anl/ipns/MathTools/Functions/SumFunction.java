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
 */

public class SumFunction extends    OneVarFunction
                         implements IOneVarParameterizedFunction,
                                    Serializable
{
  private IOneVarParameterizedFunction functions[];


   public SumFunction( IOneVarParameterizedFunction functions[] )
   {
     super( "Sum of Functions" );
     if ( functions != null ) 
       this.functions = functions;
     else
       this.functions = new IOneVarParameterizedFunction[0];
   }


  public int numParameters()
  {
    int n = 0; 
    for ( int i = 0; i < functions.length; i++ )
      n += functions[i].numParameters();
    return n;
  }


  public float[] getParameters()
  {
    int n_params = numParameters();
    float params[] = new float[n_params];

    int index = 0;
    for ( int i = 0; i < functions.length; i++ )
    {
      float current_params[] = functions[i].getParameters();
      for ( int k = 0; k < current_params.length; k++ ) 
      {
        params[index] = current_params[k];
        index++;
      }  
    }
    return params;
  }


  public void setParameters( float parameters[] )
  {
    if ( parameters == null )
      return;

    int n_params = numParameters();
    if ( n_params > parameters.length )
      n_params = parameters.length;

    int i = 0;
    int k = 0;
    float current_params[] = functions[i].getParameters();  
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
   *  Evaluate the y-value of the polynomial at the specified (double) x-value.
   *
   *  @param  x  the point at which the polynomial is to be evaluated
   *
   *  @return the value of the polynomial, at the specified point, provided 
   *          the point is in the currently specified domain.
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

      float coefficients[] = { 4, -4, 1 };
      IOneVarParameterizedFunction polynomial = new Polynomial( coefficients );
      IOneVarParameterizedFunction gaussian = new Gaussian( -2, 25, 2 ); 
      IOneVarParameterizedFunction functions[] = 
                                  new IOneVarParameterizedFunction[2];
      functions[0] = polynomial; 
      functions[1] = gaussian; 
      IOneVarParameterizedFunction sum = new SumFunction( functions );
      Data sum_data = new FunctionModel( x_scale, sum, 1 );
      ds.addData_entry( sum_data );
      ViewManager vm = new ViewManager( ds, IViewManager.IMAGE );
    }
}
