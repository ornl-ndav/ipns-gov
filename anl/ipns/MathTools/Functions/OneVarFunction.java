/**
 * File: OneVarFunction.java 
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
 *  Revision 1.3  2002/06/17 22:21:39  dennis
 *  Added methods for derivatives.
 *
 *  Revision 1.2  2002/04/19 16:52:17  dennis
 *  Added more javadocs.
 *
 *  Revision 1.1  2002/04/11 20:55:55  dennis
 *  Abstract base class for functions of one variable that return 
 *  float or double values when evaluated at single points, or at i
 *  arrays of points.
 *
 *  Revision 1.1  2002/04/04 19:43:21  dennis
 *  Abstract base class for functions of one variable.
 *
 */

package DataSetTools.functions;

import DataSetTools.math.*;
import DataSetTools.util.*;

/**
 *  This is an abstract base class for functions of one variable that can
 *  provide float or double y-values at a single point or array of points.
 *  Derived classes must implement the methods that provide the y values
 *  at specified x values.
 */

abstract public class OneVarFunction implements IOneVarFunction 
{
  protected ClosedInterval domain;
  protected String         name;


  /**
   *  Construct a OneVarFunction object with the specified name and with
   *  the whole Real line as it's domain.
   *
   *  String  name  The name to use for this function. 
   */
  public OneVarFunction( String name ) 
  {
    domain = new ClosedInterval( -Float.MAX_VALUE, Float.MAX_VALUE );
    if ( name != null )
      this.name = name;
    else
      this.name = "No Name";
  }
  

  /**
   *  Evaluate this function at the specified point, as a double.  This
   *  method MUST be implemented in concrete derived classes. 
   *
   *  @param   x   The value at which the function is evaluated.
   *
   *  @return  the value of this function at the given x value. 
   */
  abstract public double getValue( double x );


  /**
   *  Evaluate this function at the specified point, as a float.  The
   *  default implementation provided here just calls the "double" version
   *  of getValue(). NOTE: For efficiency, derived classes should override 
   *  this default implementation.   
   *
   *  @param   x   The value at which the function is evaluated.
   *
   *  @return  the value of this function at the given x value.
   */
  public float getValue( float x )
  {
    return (float) getValue( (double)x );
  }


  /**
   *  Evaluate this function at the specified list of points, as floats.  The
   *  default implementation provided here just calls the "float" version
   *  of getValue(). NOTE: For efficiency, derived classes should override 
   *  this default implementation.         
   *
   *  @param   x   The list of values at which the function is evaluated.
   *
   *  @return  Array of values of this function at the given x values.
   */
  public float[] getValues( float x[] )
  {
    float[] vals = new float[ x.length ];
    for ( int i = 0; i < x.length; i++ )
      vals[i] = getValue( x[i] );
    return vals;
  }


  /**
   *  Evaluate this function at the specified list of points, as doubles.  The
   *  default implementation provided here just calls the "double" version
   *  of getValue(). NOTE: For efficiency, derived classes should override 
   *  this default implementation.
   *
   *  @param   x   The list of values at which the function is evaluated.
   *
   *  @return  Array of values of this function at the given x values.
   */
  public double[] getValues( double x[] )
  {
    double[] vals = new double[ x.length ];
    for ( int i = 0; i < x.length; i++ )
      vals[i] = getValue( x[i] );
    return vals;
  }


  /**
   *  Calculate a numerical approximation to the derivative of this function
   *  with respect to x, using the centered difference approximation.  This
   *  function should be overridden in derived classes for which exact 
   *  derivative values are available.
   *
   *  @param  x     The value at which the derivative is approximated
   *  
   *  @return The centered difference approximation to the derivative at x.
   */
  public float get_dFdx( float  x )
  {
    return (float)get_dFdx( (double)x );
  }


  /**
   *  Calculate a numerical approximation to the derivative of this function
   *  with respect to x, using the centered difference approximation.  This
   *  function should be overridden in derived classes for which exact 
   *  derivative values are available.
   *
   *  @param  x     The value at which the derivative is approximated
   *  
   *  @return The centered difference approximation to the derivative at x.
   */

  public double get_dFdx( double x )
  {
    return (getValue( x + DELTA ) - getValue( x - DELTA ))/ (2*DELTA);
  }



  /**
   *  Get the name that was specified for this function.
   *
   *  @return the name of this function. 
   */
  public String getName()
  {
    return name;
  }

  /**
   *  Set the name that to use for this function.
   *
   *  @param the new name to use for this function.
   */
  public void setName( String name)
  {
    if ( name != null )
      this.name = name;
  }


  /**
   *  Get the current domain for this function.
   *
   *  @return the closed interval that specifies the domain for this function.
   */
  public ClosedInterval getDomain()
  {
    return domain;
  }


  /**
   *  Set the current domain for this function.
   *
   *  @param the closed interval that specifies the domain for this function.
   */
  public void setDomain( ClosedInterval interval )
  {
    if ( domain != null )
      domain = interval;
  }


  /**
   *  Get a string form of this function.  In this case it just returns the
   *  name of the function.
   *
   *  @return A string containing the name of this function followed by a
   *          new line character.
   */
  public String toString()
  {
    String state = name + "\n";
    return state;
  } 

}
