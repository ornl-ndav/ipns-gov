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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 */

package DataSetTools.functions;

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

    int n_zero = 0;
    for ( int i = 0; i < x.length; i++ )
    {
      diff = f.getValue( x[i] ) - y[i];
      sum += diff * diff * weights[i];
    }
    return sum;
  }

}
