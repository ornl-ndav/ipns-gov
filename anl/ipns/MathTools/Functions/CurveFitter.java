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
 * Revision 1.7  2006/02/26 04:14:22  dennis
 * Moved some capabilities from the derived class, MarquardtArrayFitter,
 * into this abstract base class, so that they could be shared by other
 * derived classes in the future.  Specifically:
 * - Moved getResultsString() to base class
 * - Moved counter "n_steps" and "max_relative_change" to base class
 *   and added methods getNumStepsTaken() and getMaxParameterChange().
 *   This will allow access to this information after the fit has been
 *   calculated, so that informational prints will not be needed.
 * - Made DoFit( tolerance, max_steps ) a public method, so that the
 *   fit can be restarted from the point it reached last.
 * - Added more documentation.
 *
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

import gov.anl.ipns.Util.Numeric.*;


/**
 *  This is an abstract base class for classes that fit a 
 *  IOneVarParameterizedFunction to a set of measured points (x[i], y[i])
 *  with specified standard deviations in the y values.   
 */
abstract public class CurveFitter implements ICurveFitter
{
  protected IOneVarParameterizedFunction f;  // function being fit to 
                                             // the data

  protected double x[],                      // x,y data being fit
                   y[];

  protected double sigma[];                // list of standard deviations for
                                           // the data points.  The data points
                                           // are weighted by 1/(sigma*sigma)

  protected double weights[];

  protected int    n_steps;               // counter for number of steps taken
                                          // set by derived classes

  protected double  max_relative_change;  // largest relative change in any
                                          // parameter, set by derived classes


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


  /**
   *  Carry out additional steps of the fit method.  The iteration will stop 
   *  when a tolerance criterion is satisfied, or when the specified maximum
   *  number of steps have been taken.  The results of doing the fit are
   *  available by getting the parameters from the function and by using 
   *  other methods in the class.  Note: this method may be called multiple
   *  times, to continue iterating from the point the iteration previously
   *  terminated.
   *
   *  @param  tolerance   The tolerance bound to meet 
   *  @param  max_steps   The maximum number of steps to take 
   */
  abstract public void DoFit( double tolerance, int max_steps );


  /**
   *  Get estimate of the uncertainties in the parameters, after the last
   *  step of the fit process.
   *
   *  @return  estimate of the uncertainties in the parameters. 
   */
  abstract public double[] getParameterSigmas();


  /**
   *  Calculate the weighted sum of the squares of the errors divided by the
   *  number of free parameters, after the last step of the fit process.
   *
   *  @return  The sum of the squares of the errors in the fit, multiplied
   *           by the weights of the points after the last call to DoFit.
   */
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


  /**
   *  Calculate the weighted sum of the squares of the errors after the
   *  last step of the fit process.
   *
   *  @return  The sum of the squares of the errors in the fit, multiplied
   *           by the weights of the points after the last call to DoFit.
   */
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
   *  Get the maximum relative change in any parameter during the last step
   *  of the fit.
   *
   *  @return  The last value calculated for the maximum relative change
   *           of any paramter during the last call to the DoFit method.
   */
  public double getMaxParameterChange()
  {
    return max_relative_change;
  }


  /**
   *  Get the number of steps taken during the last attempt to do the
   *  fit.
   *
   *  @return  The last value of the step counter during the last call 
   *           to the DoFit method.
   */
  public double getNumStepsTaken()
  {
    return n_steps;
  }


  /**
   *  Convenience method to form  a formatted, multi-line String containing 
   *  the results of the fitting calculation, with error estimates on the 
   *  fitted parameters.
   *  
   *  @return a multi-line string containing a summary of the fit.
   */
  public String getResultsString()
  {
    StringBuffer result = new StringBuffer();
    String names[]    = f.getParameterNames();
    double coefs[]    = f.getParameters();
    double p_sigmas[] = getParameterSigmas();
    for ( int i = 0; i < names.length; i++ )
    {
      result.append( Format.string(names[i],17)  );
      result.append( Format.real(coefs[i],20,9) + "  +-" );
      result.append( Format.real(p_sigmas[i], 20,9) );
      result.append( "\n" );
    }
    return result.toString();
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
