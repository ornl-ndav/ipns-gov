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
 *           Menomonie, WI. 54751
 *           USA
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
 *  Revision 1.1  2002/06/17 22:26:04  dennis
 *  The Marquardt algorithm for curve fitting.
 *
 */

package DataSetTools.functions;

import DataSetTools.math.*;
import DataSetTools.retriever.*;
import DataSetTools.viewer.*;
import DataSetTools.dataset.*;
import DataSetTools.util.*;
import java.util.*;

public class MarquardtArrayFitter extends CurveFitter
{
  double Alpha[][];
  double u[][];

  /**
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


  public double[] getParameterSigmas()
  {
    double p_sigmas[]  = new double[ f.numParameters() ];
    double basis_vec[] = new double[ f.numParameters() ];
    
    for ( int k = 0; k < p_sigmas.length; k++ )
    {
      for ( int i = 0; i < basis_vec.length; i++ )
        basis_vec[i] = 0.0;
      basis_vec[k] = 1.0;
      LinearAlgebra.QR_solve( Alpha, u, basis_vec );
      p_sigmas[k] = basis_vec[k];
    }
    return p_sigmas;
  }

  private void do_fit( double tolerance, int max_steps )
  {
    double lamda   = 0.001;
    int    n_steps = 0;
    double delta_chisq = tolerance + 1;
    double chisq_1;
    double chisq_2;
    boolean chisq_increasing;
    double  norm_da = tolerance + 1;
    double  norm_a  = 1;

    int    n_params  = f.numParameters();
    int    n_points  = x.length;
    double a[]       = new double[n_params];          // current param values
    double a_old[]   = new double[n_params];          // old param values
    double da[]      = new double[n_params];          // change to param values
    double derivs[];                                  // dFda( xi )
    double beta[]    = new double[n_params];
    double A[][]     = new double[n_params][n_params];
    Alpha = new double[n_params][n_params];

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

      for ( int i = 0; i < n_points; i++ )            // sum over points x[i]
      {
        derivs = f.get_dFda( x[i] );
        for ( int k = 0; k < n_params; k++ )          // for each row k
        {
          beta[k] += weights[i] * ( y[i] - f.getValue(x[i]) ) * derivs[k];
          for ( int j = 0; j < n_params; j++ )
            A[k][j] += weights[i] * derivs[k] * derivs[j]; 
        }
      }

      chisq_increasing = true;
      while ( chisq_increasing && n_steps < max_steps )
      {
        for ( int k = 0; k < n_params; k++ )
          for ( int j = 0; j < n_params; j++ )
            Alpha[k][j] = A[k][j];

        for ( int k = 0; k < n_params; k++ )
          Alpha[k][k] *= (1 + lamda);

        a = f.getParameters();
        norm_a = Math.max( 1.0, LinearAlgebra.Norm(a) );
        for ( int k = 0; k < n_params; k++ )
        {
          a_old[k] = a[k];
          da[k]    = beta[k];
        }

        u = LinearAlgebra.QR_factorization( Alpha ); 
        LinearAlgebra.QR_solve( Alpha, u, da );

        norm_da = LinearAlgebra.Norm(da);

        for ( int k = 0; k < n_params; k++ )
          a[k] = a_old[k] + da[k];
        f.setParameters(a);
        
        chisq_2 = getChiSqr();
        delta_chisq = Math.abs( chisq_2 - chisq_1 );
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
//        System.out.println( n_steps + ", " + chisq_2 + ", " + lamda);
      }
    }
    System.out.println("n_steps = " + n_steps );
  }


  public static void main( String args[] )
  {
    final int SIZE = 1000;
    double x[]     = new double[SIZE];
    double y[]     = new double[SIZE];
    double sigma[] = new double[SIZE];
    Random ran = new Random( 10000 );

    for ( int i = 0; i < x.length; i++ )
    {
      x[i] = i;
      y[i] =   6.0*x[i]*x[i]*x[i] + 
               5.0*x[i]*x[i] + 
               4.0*x[i] + 
               1000.0 
              + 500*ran.nextGaussian();
      sigma[i] = Math.sqrt(y[i]); 
    }

    double coefs[] = { 0, 0, 0, 0 };
    Polynomial f = new Polynomial( coefs );

    ElapsedTime timer = new ElapsedTime();
    MarquardtArrayFitter fitter = 
         new MarquardtArrayFitter( f, x, y, sigma, 1.0e-20, 500 );
    System.out.println("Time to fit = " + timer.elapsed() );

    double p_sigmas[] = fitter.getParameterSigmas();
    String names[] = f.getParameterNames();
           coefs   = f.getParameters();
    for ( int i = 0; i < f.numParameters(); i++ )
      System.out.println(names[i] + " = " + coefs[i] + " +- " + p_sigmas[i] ); 
    System.out.println("Chi Sq = " + fitter.getChiSqr() );

    String file_name = "/home/dennis/ARGONNE_DATA/hrcs2447.run";
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
    Gaussian g1 = new Gaussian( 2725, 32000, 100 );
    Gaussian g2 = new Gaussian( 2750,   100, 70 );
//    Lorentzian l1 = new Lorentzian( 2750, 100, 70 );    
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
/*
    fitter = new MarquardtArrayFitter( g1, x, y, sigma, 1.0e-20, 500 );
    p_sigmas = fitter.getParameterSigmas();
    coefs = g1.getParameters();
    names = g1.getParameterNames();
    for ( int i = 0; i < g1.numParameters(); i++ )
      System.out.println(names[i] + " = " + coefs[i] + " +- " + p_sigmas[i] );
    model = new FunctionModel( x_scale, g1, 3 ); 
*/

    fitter = new MarquardtArrayFitter( sum, x, y, sigma, 1.0e-20, 500 );
    p_sigmas = fitter.getParameterSigmas();
    coefs = sum.getParameters();
    names = sum.getParameterNames();
    for ( int i = 0; i < sum.numParameters(); i++ )
      System.out.println(names[i] + " = " + coefs[i] + " +- " + p_sigmas[i] );
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
    coefs = g1.getParameters();
    names = g1.getParameterNames();
    for ( int i = 0; i < g1.numParameters(); i++ )
      System.out.println(names[i] + " = " + coefs[i] + " +- " + p_sigmas[i] );
    model = new FunctionModel( x_scale, g1, 3 );
    monitor_ds.addData_entry( model );
    monitor_ds.notifyIObservers( IObserver.DATA_CHANGED );
    

  }

}
