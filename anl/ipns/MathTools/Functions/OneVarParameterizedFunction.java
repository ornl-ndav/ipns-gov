/**
 * File: OneVarParameterizedFunction.java 
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
 *  Revision 1.9  2003/07/14 14:02:05  dennis
 *  Changed the offset used for calculation of numerical approximation
 *  to the derivative.  Now set to DELTA = 1.0e-5 if it was set less
 *  than DELTA.
 *
 *  Revision 1.8  2003/06/30 22:36:17  dennis
 *  Made calculation of derivatives with respect to parameters
 *  at a collection of x-values, more efficient.
 *  Fixed error in calculation of derivative with respect to
 *  paramters at one x-value.
 *
 *  Revision 1.7  2003/06/19 22:21:08  dennis
 *  Added methods to evaluate the derivative with respect to
 *  one parameter at a list of x values.
 *
 *  Revision 1.6  2003/06/17 23:05:36  dennis
 *  The step size for calculating numerical derivatives is now
 *  interpreted as a fractional change, rather than as an
 *  absolute step size.
 *
 *  Revision 1.5  2003/06/10 22:20:24  dennis
 *  Made constructor public, so that it can be used by classes
 *  outside of its package.
 *
 *  Revision 1.4  2002/11/27 23:14:24  pfpeterson
 *  standardized header
 *
 *  Revision 1.3  2002/06/17 22:21:01  dennis
 *  Added methods for derivatives and made the parameters 'double'.
 *
 *  Revision 1.2  2002/04/19 16:52:21  dennis
 *  Added more javadocs.
 *
 *  Revision 1.1  2002/04/11 20:57:27  dennis
 *  Abstract base class for functions of one variable that are controlled
 *  by an array of parameters and that return float or double
 *  values when evaluated at single points, or at arrays of points.
 *
 *  Revision 1.1  2002/04/04 19:44:52  dennis
 *  Abstract base class for functions of one variable that are controlled
 *  by a list of parameters.
 */

package DataSetTools.functions;

import DataSetTools.math.*;
import DataSetTools.util.*;

/**
 *  This is an abstract base class for parameterized functions of one 
 *  variable.
 *  Derived classes must implement the methods that provide the y values
 *  at specified x values.
 */

abstract public class OneVarParameterizedFunction extends OneVarFunction 
                                  implements IOneVarParameterizedFunction
{
  protected double parameters[];
  protected String parameter_names[];

  /**
   *  Construct a function with the specified name and parameters.
   *
   *  @param  name             The name to use for this function
   *  @param  parameters       Array of parameter values for this function.
   *                           The length of this array determines the number
   *                           of parameters for this function.
   *                           If no parameters are used, this can be null.
   *  @param  parameter_names  Array of names of parameters for this function.
   *                           If no names, or not enough names are specified,
   *                           default names P0, P1, P2, etc. will be generated.
   */
  public OneVarParameterizedFunction( String name, 
                                      double parameters[], 
                                      String parameter_names[] )
  {
    super( name );
                                
    if ( parameters != null )                 // make a valid parameter list,
      this.parameters = parameters;      
    else
      this.parameters = new double[0];
                                              // copy any names that were given
                                              // into a new list of names, and
                                              // synthesize the rest. 
    this.parameter_names = new String[ this.parameters.length ]; 
    if ( parameter_names != null )           
    {
      for ( int i = 0; i < parameter_names.length; i++ )
        this.parameter_names[i] = parameter_names[i]; 

      for ( int i = parameter_names.length; i < parameters.length; i++ )
        this.parameter_names[i] = "P"+i; 
    }
    else
      for ( int i = 0; i < this.parameters.length; i++ )
        parameter_names[i] = "P"+i; 
  }
  
  /**
   *  Get the number of parameters that this function has.
   *
   *  @return the number of parameters for this function.
   */
  public int numParameters() 
  {
    return parameters.length;
  }


  /**
   *  Get a reference to the list of parameter names for this function.
   *
   *  @return  Reference to the parameter names for this function.
   */
  public String[] getParameterNames()
  {
    return parameter_names;
  }


  /**
   *  Get a reference to the list of parameters for this function.
   *
   *  @return  Reference to the parameters of this function.
   */
  public double[] getParameters()
  {
    return parameters;
  }


  /**
   *  Set the new values for the list of parameters for this function.
   *
   *  @param  parameters  Array containing values to copy into the list of
   *                      parameter values for this function.
   */
  public void setParameters( double parameters[] )
  {
    if ( parameters != null )
    { 
      int num_params = Math.min( this.parameters.length, parameters.length );
      for ( int i = 0; i < num_params; i++ )
        this.parameters[i] = parameters[i];
    }
  }

  
  public float get_dFdai( float  x, int i )
  {
    return (float)get_dFdai( (double)x, i );
  }
  

  public double get_dFdai( double x, int i )
  {
    if ( i < 0 || i >= numParameters() )
      return 0;

    // NOTE: While not as efficient as accessing the parameters list directly,
    // using the get/setParameters() method here will allow this to also work 
    // in more complicated cases where the function calculates state 
    // information when it's paramters are set.  For efficiency in those cases
    // the calling code should use the method  get_dFdai( x[], i ), to allow
    // the state information to be set only once, and used to evaluate the
    // derivative at an array of x[] values.
 
    double parameters_copy[] = getParameters();
    double old_a_val   = parameters_copy[i];

    double da = Math.abs( DELTA * old_a_val );
    if ( da < DELTA )
      da = DELTA;

    parameters_copy[i] = old_a_val + da;
    setParameters( parameters_copy );
    double f1 = getValue( x );

    parameters_copy[i] = old_a_val - da;
    setParameters( parameters_copy );
    double f0 = getValue( x );

    parameters_copy[i] = old_a_val;
    setParameters( parameters_copy );

    return (f1 - f0) / ( 2*da );
  }

  // derivatives with respect to
  // ith parameter at list of x 
  public float[] get_dFdai( float  x[], int i )
  {
    double x_d[] = LinearAlgebra.float2double( x );
    double derivs[] = get_dFdai( x_d, i );
    return LinearAlgebra.double2float( derivs ); 
  }

  public double[] get_dFdai( double x[], int i )
  {
    double derivs[] = new double[ x.length ];

    if ( i < 0 || i >= numParameters() )                 // return all 0's
    {
      for ( int k = 0; k < x.length; k++ )
        derivs[k] = 0;
      return derivs;
    }

    double val_1[] = new double[ x.length ];
    double parameters_copy[] = getParameters();
    double old_a_val   = parameters_copy[i];

    double da = Math.abs( DELTA * old_a_val );
    if ( da < DELTA )
      da = DELTA;

    parameters_copy[i] = old_a_val + da;
    setParameters( parameters_copy );

    for ( int k = 0; k < x.length; k++ )
      val_1[k] = getValue( x[k] );

    parameters_copy[i] = old_a_val - da;
    setParameters( parameters_copy );

    for ( int k = 0; k < x.length; k++ )
      derivs[k] = (val_1[k] - getValue( x[k] )) / (2*da);
   
    parameters_copy[i] = old_a_val;
    setParameters( parameters_copy );

    return derivs;
  }


  public float[]  get_dFda( float  x )
  {
    float result[] = new float[ numParameters() ];
    for ( int i = 0; i < result.length; i++ )
      result[i] = (float)get_dFdai( (double)x, i );

    return result;
  }


  public double[] get_dFda( double x )
  {
    double result[] = new double[ numParameters() ];
    for ( int i = 0; i < result.length; i++ )
      result[i] = get_dFdai( x, i );

    return result;
  }



  /**
   *  Get a string showing the function name and list of parameter names
   *  and parameter values for this function.
   *
   *  @return  A multi-line string containg the name and parameter information
   *           for this function. 
   */
  public String toString()
  {
    String state = super.toString();
    for ( int i = 0; i < parameters.length; i++ )
      state += parameter_names[i] + ": " + parameters[i] + "\n";
    return state;
  } 
}
