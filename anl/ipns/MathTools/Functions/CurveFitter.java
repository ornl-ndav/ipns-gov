/**
 * File: CurveFitter.java
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
 * $Log$
 * Revision 1.6  2006/02/20 04:41:40  dennis
 * Added method: MaxRelativeParameterChange( da[], a[] ) to calculate
 * the maximum relative absolute change in a parameter during the
 * curve fitting process.  This allows setting a more meaningful
 * stopping criterion...stop when the maximum change in any parameter
 * during an optimization step is less than a specified tolerance.
 *
 * Revision 1.5  2004/03/19 17:24:25  dennis
 * Removed unused variables
 *
 * Revision 1.4  2004/03/12 02:14:41  dennis
 * Moved to package gov.anl.ipns.MathTools.Functions;
 *
 * Revision 1.3  2002/11/27 23:14:24  pfpeterson
 * standardized header
 *
 */

package gov.anl.ipns.MathTools.Functions;


abstract public class CurveFitter implements ICurveFitter
{
  protected IOneVarParameterizedFunction f;

  protected double x[],
                   y[],
                   sigma[],
                   weights[];

  /**
   *  Construct a curve fitter object to adjust the parameters for the
   *  specified function.
   *
   *  @param function  The function whose parameters are to be adjusted to
   *                   fit data.  
   */
  protected CurveFitter( IOneVarParameterizedFunction function, 
                         double x[],
                         double y[],
                         double sigma[] )
  {
    f          = function;
    this.x     = x;
    this.y     = y;
    this.sigma = sigma;
    weights    = new double[sigma.length];

    for ( int i = 0; i < sigma.length; i++ )
      if ( sigma[i] <= 0 )
        weights[i] = 0;                               // skip points with 
      else                                            // invalid sigma value
        weights[i] = 1.0/(sigma[i]*sigma[i]);
  } 


  /**
   *  Get the function whose parameters were adjusted by this CurveFitter
   *  object.
   *
   *  @return a reference to the function that was adjusted to fit the data.
   */
  public IOneVarParameterizedFunction getFitFunction()
  {
    return f;
  }


  abstract public double[] getParameterSigmas();


  public double getWeightedChiSqr()
  {
    double diff;
    double sum = 0.0;

    int n_zero = 0;
    for ( int i = 0; i < x.length; i++ )
    {
      if ( weights[i] == 0 )
        n_zero++;
      else
      {
        diff = f.getValue( x[i] ) - y[i];
        sum += diff * diff * weights[i];
      }
    } 
    int n_free = x.length - f.numParameters() - n_zero;

    return sum/n_free;
  }


  public double getChiSqr()
  {
    double diff;
    double sum = 0.0;

    for ( int i = 0; i < x.length; i++ )
    {
      diff = f.getValue( x[i] ) - y[i];
      sum += diff * diff * weights[i];
    }
    return sum;
  }


  /**
   *  Calculate the maximum relative change in the parameters:
   *  max( |da[i]/a[i]| ).  If a[i] is zero, |da[i]| is used.
   *  
   *  @param da   array of changes in the parameter values
   *  @param a    array of parameter values
   *  
   *  @return  The maximum of the quantities |da[i]/a[i]| or |da[i]| if
   *  a[i] is zero.  If the arrays are null, of zero length or of unequal
   *  length, a -1 is returned.
   */
  public static double MaxRelativeParameterChange( double da[], double a[] )
  {
    double max_change = -1;
    if ( da == null || a == null ||
         da.length < 1 || da.length != a.length )
      return max_change;

    double change;
    double mag_param;
    double mag_delta;
    for ( int i = 0; i < a.length; i++ )
    {
      mag_param = Math.abs(  a[i] );
      mag_delta = Math.abs( da[i] );
      if ( mag_param == 0 )
        change = mag_delta;
      else 
        change = mag_delta/mag_param;

      if ( change > max_change )
        max_change = change;
    } 
    return max_change;
  }

}
