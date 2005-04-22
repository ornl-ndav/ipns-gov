/**
 * File: MarquardtArrayFitter.java
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
 * NOTE: The algorithm is from P.Bevington & D.Robinson, "Data Reduction and
 *       Error Analysis for the Physical Sciences", 2nd Ed., McGraw-Hill, 1992,
 *       with some implementation ideas from The Java Analysis Studio class
 *       LeastSquaresFit.java. 
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.15  2005/04/22 16:51:25  dennis
 *  Moved three remaining debug prints into "if (debug)" statement.
 *
 *  Revision 1.14  2004/03/19 17:24:26  dennis
 *  Removed unused variables
 *
 *  Revision 1.13  2004/03/12 02:19:44  dennis
 *  Moved to package gov.anl.ipns.MathToolsr.Functions
 *
 *  Revision 1.12  2003/07/29 22:28:57  dennis
 *  Added convenience method: getResultsString() to form a multi-line
 *  string with the list of fitted parameters and error estimates.
 *
 *  Revision 1.11  2003/07/29 16:08:26  dennis
 *  Put printing of A[k][k], alpha[k][k] and u[k][k],
 *  and sqrt(A[k][k]) into an "if (debug)" block.
 *
 *  Revision 1.10  2003/07/28 22:19:20  dennis
 *  Improved second method of estimating the standard deviations for
 *  the parameters.
 *
 *  Revision 1.9  2003/07/16 22:27:47  dennis
 *  Now calculates numerical derivatives with a smaller step size,
 *  but with a minumum step size of 1e-8.  Also now uses "weighted"
 *  ChiSqr rather than un-weighted ChiSqr, when weights that are
 *  not identically = 1 are specified.
 *
 *  Revision 1.8  2003/07/14 22:38:32  dennis
 *  Added some debugging code and debug flag.
 *
 *  Revision 1.7  2003/07/14 13:43:55  dennis
 *  Fixed sign error in alternate way of estimating the standard
 *  deviations of the parameters.
 *
 *  Revision 1.6  2003/06/19 22:20:02  dennis
 *  Now uses the methods that evaluate the function and derivatives
 *  at a list of x values, so that the evaluation can be done more
 *  efficiently.
 *
 *  Revision 1.5  2003/06/19 20:51:32  dennis
 *  Pulled evaluation of f(x) out one level to make it
 *  more efficient for functions where evaluating f(x) is
 *  "expensive".
 *
 *  Revision 1.4  2003/01/30 21:03:50  pfpeterson
 *  Works with new method names in DataSetTools.math.LinearAlgebra.
 *
 *  Revision 1.3  2002/11/27 23:14:24  pfpeterson
 *  standardized header
 *
 *  Revision 1.2  2002/06/19 22:51:13  dennis
 *  Added methods getParameterSigmas() and getParameterSigmas_2()
 *  that estimate errors in the fitting parameters (in two ways).
 *  Also, now scales rows and columns of the matrix "Alpha" by the
 *  square root of the diagonal elements to keep the diagonal
 *  elements at the same magnitude.
 *
 *  Revision 1.1  2002/06/17 22:26:04  dennis
 *  The Marquardt algorithm for curve fitting.
 *
 */

package gov.anl.ipns.MathTools.Functions;

import gov.anl.ipns.MathTools.*;
import gov.anl.ipns.Util.Sys.*;
import gov.anl.ipns.Util.Numeric.*;

/*
import DataSetTools.math.*;
import DataSetTools.retriever.*;
import DataSetTools.viewer.*;
import DataSetTools.dataset.*;
import DataSetTools.util.*;
*/
import java.util.*;

public class MarquardtArrayFitter extends CurveFitter
{
  double Alpha[][];
  double u[][];
  double root_diag[];

  /**
   *  Construct the curve fitter to fit the specified function to the
   *  specified data.
   *
   *  @param function   The function whose parameters will be adjusted to
   *                    match the data. NOTE: If the function evaluation is
   *                    "expensive", it may be helpful to override the methods
   *                    getValues(double x[]) and get_dFdai(double x[], int i)
   *                    and implement them as efficiently as possible, since
   *                    these are the methods that the fitter uses to evaluate
   *                    the function and the derivatives relative to its
   *                    parameters.
   *  @param x          The list of x values
   *  @param y          The list of y values
   *  @param sigma      The list of standard deviations for the data points.
   *                    The data points are weighted by 1/(sigma*sigma).
   *  @param tolerance  When (norm_da/norm_a) < tolerance, the iteration will
   *                    stop.
   *  @param max_steps  When the number of iterations hits max_steps, the 
   *                    interation will stop. 
   *
   */
  public MarquardtArrayFitter( IOneVarParameterizedFunction function, 
                               double x[],
                               double y[],
                               double sigma[],
                               double tolerance,
                               int    max_steps ) 
  {
    super( function, x, y, sigma );
    do_fit( tolerance, max_steps );
  } 


  /**
   *  Return a formatted, multi-line String containing the results of the 
   *  fitting calculation, with error estimates on the fitted parameters.
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
   *  Get estimates of the standard deviations of the parameters, as
   *  1/sqrt( A[k][k] ).
   *
   * @return the array of sigma values. 
   */
  public double[] getParameterSigmas()
  {
    double p_sigmas[]  = new double[ f.numParameters() ];

    for ( int k = 0; k < p_sigmas.length; k++ )
      p_sigmas[k] = 1.0 / root_diag[k];

    return p_sigmas;
  }


  /**
   *  Get estimates of the standard deviations of the parameters, by 
   *  approximating chi sq by a quadratic polynomial through three points
   *  and finding the change in the parameter that would cause a change
   *  of 1 in chi sq.  (See Bevington, 2nd ed., pg 147, eqn: 8.13 )  
   *  In this version, we calculate a sequence of approximations for
   *  each parameter, with delta ranging over 10 orders of magnitude
   *  and keep the value in the sequence with the smallest relative change. 
   *  While it gives the same results as the method getParameterSigmas()
   *  in simple cases, it is not entirely reliable.
   *
   * @return the array of sigma values. 
   */
  public double[] getParameterSigmas_2()
  {
    final int MAX_STEPS = 10;        // evaluate approximation using deltas
                                     // ranging over 10 orders of magnitudue
    double START_DELTA = 1.0e-2;     // start with change of 1%
    int    n_approx;
    double approx[] = new double[ MAX_STEPS ];   // save list of approximations

    double p_sigmas[]  = new double[ f.numParameters() ];
    double a_save;
    double delta;
    for ( int k = 0; k < p_sigmas.length; k++ )
    {
      double a[] = f.getParameters();
      double diff = 0.0;

      a_save = a[k];

      if ( a_save < 1.0e-8 )             // if parameter essentially 0, use
        delta = 1.0e-8;                  // a "small" step
      else
        delta = START_DELTA * a_save;

      n_approx = 0;
      for ( int count = 0; count < MAX_STEPS; count++ ) 
      {
        a[k] = a_save + delta;
        f.setParameters(a);
        double chi_3 = getChiSqr();

        a[k] = a_save - delta;
        f.setParameters(a);
        double chi_1 = getChiSqr();

        a[k] = a_save;
        f.setParameters(a);
        double chi_2 = getChiSqr();

        diff = chi_1-2*chi_2+chi_3;
        if ( diff > 0 )
        {
          approx[n_approx] = Math.abs(delta) * 
                             Math.sqrt(2.0/Math.abs(diff));
          n_approx++;
        }
        delta = delta / 10;
      }
      if ( n_approx == 0 )
        p_sigmas[k] = Double.POSITIVE_INFINITY;    // no reasonable value

      else if ( n_approx == 1 )
        p_sigmas[k] = approx[0];                   // only one possible value

      else                                        // use one with smallest diff
      {
        double min_diff = Double.POSITIVE_INFINITY;
        for ( int i = 0; i < n_approx-1; i++ )
        {
          diff = Math.abs( (approx[i+1]-approx[i])/approx[i] );
          if ( diff < min_diff )
          {
            p_sigmas[k] = approx[i+1];
            min_diff = diff;
          }
        }
      }
    }    
    return p_sigmas;
  }

  /*
   *  Carry out Marquardt's method to do the fit.
   */

  private void do_fit( double tolerance, int max_steps )
  {
    boolean debug = false;                          // set true for some debug
                                                    // messages.
    double lamda   = 0.001;
    int    n_steps = 0;
    double chisq_1 = 0;
    double chisq_2 = 0;
    boolean chisq_increasing;
    double  norm_da = tolerance + 1;
    double  norm_a  = 1;
    double  w_diff_i;                                // weighted difference at
                                                     // at the ith data point
    int    n_params  = f.numParameters();
    int    n_points  = x.length;
    double a[]       = new double[n_params];          // current param values
    double a_old[]   = new double[n_params];          // old param values
    double da[]      = new double[n_params];          // change to param values
    double derivs[][];                                // dFda( xi )
    double vals[];
    double beta[]    = new double[n_params];
    double A[][]     = new double[n_params][n_params];
    Alpha            = new double[n_params][n_params];
    root_diag        = new double[n_params];

    ClosedInterval domain = f.getDomain();
    float x_min = domain.getStart_x();
    float x_max = domain.getEnd_x();
    for ( int i = 0; i < n_points; i++ )
      if ( sigma[i] <= 0 || x[i] < x_min || x[i] > x_max )  // use domain and
        weights[i] = 0;                               // skip points with 
      else                                            // invalid sigma value
        weights[i] = 1.0/(sigma[i]*sigma[i]);

    chisq_1 = getChiSqr();
    while ( n_steps < max_steps && norm_da/norm_a > tolerance )
    {
                                                      // calculate vector beta
      for ( int k = 0; k < n_params; k++ )            // and matrix A
      {
        beta[k] = 0;
        for ( int j = 0; j < n_params; j++ )
          A[k][j] = 0;
      }       

      vals = f.getValues( x );             // function values at all "x" points

      derivs = new double[n_params][];
      for ( int k = 0; k < n_params; k++ )
      {
        derivs[k] = f.get_dFdai(x,k);      // get derivatives at all "x" points
                                           // with respect to kth parameter
        if ( debug )
        {
          boolean all_zero = true;
          int i = 0;
          while (all_zero && i < n_points )
          { 
            if ( derivs[k][i] != 0 )
              all_zero = false;
            i++;
          }
          if ( all_zero )
          {
            System.out.println( "ERROR: all derivs 0 WRT parameter #" + k );
            System.out.println( "a, a_old, da = " + a[k] + ", " 
                                                  + a_old[k] + ", " 
                                                  + da[k] );
          }
        }
      }

      for ( int i = 0; i < n_points; i++ )            // sum over points x[i]
      {
        w_diff_i = weights[i] * ( y[i] - vals[i] );
        for ( int k = 0; k < n_params; k++ )          // for each row k
        {
          beta[k] += w_diff_i * derivs[k][i];
          for ( int j = 0; j < n_params; j++ )
            A[k][j] += weights[i] * derivs[k][i] * derivs[j][i]; 
        }
      }

      for ( int k = 0; k < n_params; k++ )
        root_diag[k] = Math.sqrt( A[k][k] );

      chisq_increasing = true;
      while ( chisq_increasing && n_steps < max_steps )
      {
        a = f.getParameters();
        norm_a = Math.max( 1.0, LinearAlgebra.norm(a) );
        for ( int k = 0; k < n_params; k++ )
        {
          a_old[k] = a[k];
          da[k]    = beta[k]/root_diag[k];
        }

        for ( int k = 0; k < n_params; k++ )
        {
          for ( int j = 0; j < n_params; j++ )
            Alpha[k][j] = A[k][j] / (root_diag[k] * root_diag[j]);
        }

        for ( int k = 0; k < n_params; k++ )
          Alpha[k][k] *= (1 + lamda);

        u = LinearAlgebra.QR_factorization( Alpha ); 
        LinearAlgebra.QR_solve( Alpha, u, da );

        for ( int k = 0; k < n_params; k++ )
          da[k] /= root_diag[k];

        norm_da = LinearAlgebra.norm(da);

        for ( int k = 0; k < n_params; k++ )
          a[k] = a_old[k] + da[k];
        f.setParameters(a);
        
        chisq_2 = getChiSqr();
        if ( chisq_2 > chisq_1 )
        {
          lamda *= 10;
          for ( int i = 0; i < n_params; i++ )
            a[i] = a_old[i];
          f.setParameters(a);
        }
        else
        {
          lamda /= 10;
          chisq_increasing = false;
          chisq_1 = chisq_2;
        }        
        n_steps++;
      }
      if ( debug )
        System.out.println("n,chisq2,lamda=  "+ n_steps + ", " + 
                                                chisq_2 + ", " + 
                                                lamda );
    }

    if ( debug )
    {
      System.out.println("After fit ..............................");
      System.out.println("n_steps = " + n_steps );
      System.out.println("lamda = " + lamda );
      System.out.println("........................................");
      System.out.println("A[k][k],    Alpha[k][k],   u[k][k] =");
      for ( int k = 0; k < n_params; k++ )
        System.out.println(""+A[k][k]+", " + Alpha[k][k] +", " + u[k][k] );
      System.out.println("root_diag = " );
      for ( int k = 0; k < n_params; k++ )
        System.out.print(" "+root_diag[k]);
      System.out.println();
    }
  }


  /** 
   *  Main progrm for test purposes only.
   */
  public static void main( String args[] )
  {
    final int SIZE = 1000;
    double x[]     = new double[SIZE];
    double y[]     = new double[SIZE];
    double sigma[] = new double[SIZE];
    Random ran = new Random( 10000 );

    for ( int i = 0; i < x.length; i++ )
    {
      x[i] =   i;
      y[i] =   6.0*x[i]*x[i]*x[i] + 
               5.0*x[i]*x[i] + 
               4.0*x[i] + 
               1000.0   +
              + 999*ran.nextGaussian();

      sigma[i] = Math.sqrt(y[i]); 
    }

    double coefs[] = { 0, 0, 0, 0 };
    Polynomial f = new Polynomial( coefs );

    ElapsedTime timer = new ElapsedTime();
    MarquardtArrayFitter fitter = 
         new MarquardtArrayFitter( f, x, y, sigma, 1.0e-20, 1000 );
    System.out.println("Time to fit = " + timer.elapsed() );

    double p_sigmas[] = fitter.getParameterSigmas();
    double p_sigmas_2[] = fitter.getParameterSigmas_2();
    String names[] = f.getParameterNames();
           coefs   = f.getParameters();
    for ( int i = 0; i < f.numParameters(); i++ )
      System.out.println(names[i] + " = " + coefs[i] + 
                         " +- " + p_sigmas[i] +
                         " +- " + p_sigmas_2[i] );
    System.out.println("Chi Sq = " + fitter.getChiSqr() );
/*
    String file_name = "/usr/local/ARGONNE_DATA/hrcs2447.run";
    RunfileRetriever rr = new RunfileRetriever(file_name); 
    DataSet monitor_ds = rr.getDataSet( 0 );
    ViewManager view_manager = new ViewManager(monitor_ds, IViewManager.IMAGE);

    Data m1 = monitor_ds.getData_entry(0);
    m1 = new FunctionTable( m1, false, 2 );

    float xf[] = m1.getX_scale().getXs();
    float yf[] = m1.getY_values();
    float sigmaf[] = m1.getErrors();
 
    x = new double[xf.length];
    y = new double[xf.length];
    sigma = new double[xf.length];
    for ( int i = 0; i < xf.length; i++ )
    {
      x[i] = xf[i];
      y[i] = yf[i];
      sigma[i] = sigmaf[i];
    }
   
    FunctionModel model;
    XScale x_scale = m1.getX_scale();
    Gaussian g1 = new Gaussian( 2725, 32000, 13 );
    Gaussian g2 = new Gaussian( 2746,   100, 30 );
//    Lorentzian l1 = new Lorentzian( 2750, 2000, 70 );    
    Lorentzian l1 = new Lorentzian( 2746, 3391, 30 );    
//    Lorentzian l1 = new Lorentzian( 2725, 1000, 100 );    

    String p_names[] = { "A", "x0", "k" };
    double p_vals[]  = { 1000, 2725, 0.05 };
    Expression e1 = new Expression( "A*exp(-(x-x0)*k)", "x", p_names, p_vals );
    ClosedInterval interval = new ClosedInterval( 2725, 2900 );
    e1.setDomain( interval );

    IOneVarParameterizedFunction funs[] = new IOneVarParameterizedFunction[2];
    funs[0] = g1;
//    funs[1] = g2;
//    funs[1] = e1;
    funs[1] = l1;

    SumFunction sum = new SumFunction( funs );
    interval = new ClosedInterval( 2500, 2900 );
    sum.setDomain( interval );
*/
/*
    fitter = new MarquardtArrayFitter( g1, x, y, sigma, 1.0e-20, 500 );
    p_sigmas = fitter.getParameterSigmas();
    p_sigmas_2 = fitter.getParameterSigmas_2();
    coefs = g1.getParameters();
    names = g1.getParameterNames();
    for ( int i = 0; i < g1.numParameters(); i++ )
      System.out.println(names[i] + " = " + coefs[i] + 
                         " +- " + p_sigmas[i] +
                         " +- " + p_sigmas_2[i] );
    model = new FunctionModel( x_scale, g1, 3 ); 
*/
/*
    fitter = new MarquardtArrayFitter( sum, x, y, sigma, 1.0e-20, 500 );
    p_sigmas = fitter.getParameterSigmas();
    p_sigmas_2 = fitter.getParameterSigmas_2();
    coefs = sum.getParameters();
    names = sum.getParameterNames();
    for ( int i = 0; i < sum.numParameters(); i++ )
      System.out.println(names[i] + " = " + coefs[i] + 
                         " +- " + p_sigmas[i] +
                         " +- " + p_sigmas_2[i] );

    model = new FunctionModel( x_scale, sum, 3 ); 
    System.out.println("Chi Sq = " + fitter.getChiSqr() );

    monitor_ds.addData_entry( model );
    monitor_ds.notifyIObservers( IObserver.DATA_CHANGED );

    Data m2 = monitor_ds.getData_entry(1);
    m2 = new FunctionTable( m2, false, 3 );

    xf = m2.getX_scale().getXs();
    yf = m2.getY_values();
    sigmaf = m2.getErrors();
    x_scale = m2.getX_scale();
    for ( int i = 0; i < xf.length; i++ )
    {
      x[i] = xf[i];
      y[i] = yf[i];
      sigma[i] = sigmaf[i];
    }

    g1 = new Gaussian( 3800, 1000, 140 );
    g1.setDomain( new ClosedInterval(3700, 4220 ));
    fitter = new MarquardtArrayFitter( g1, x, y, sigma, 1.0e-20, 500 );
    p_sigmas = fitter.getParameterSigmas();
    p_sigmas_2 = fitter.getParameterSigmas_2();
    coefs = g1.getParameters();
    names = g1.getParameterNames();
    for ( int i = 0; i < g1.numParameters(); i++ )
      System.out.println(names[i] + " = " + coefs[i] + 
                         " +- " + p_sigmas[i] +
                         " +- " + p_sigmas_2[i] );
    model = new FunctionModel( x_scale, g1, 3 );
    monitor_ds.addData_entry( model );
    monitor_ds.notifyIObservers( IObserver.DATA_CHANGED );
*/
  }

}
