/*
 * File:  NumericalAnalysis.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 *  Basic numerical analysis operations, such as integration, calculation 
 *  of first and second moments, etc.   
 * 
 *  $Log$
 *  Revision 1.8  2002/10/08 15:41:28  dennis
 *  Added BisectionMethod() for solving f(x)=0 on interval where f()
 *  changes sign.
 *
 *  Revision 1.7  2002/09/25 22:16:22  pfpeterson
 *  Added a method to integrate function-table data using trapezoid method.
 *
 *  Revision 1.6  2001/04/25 20:56:36  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.5  2001/01/29 21:05:44  dennis
 *  Now uses CVS revision numbers.
 *
 *  Revision 1.4  2000/08/02 20:12:35  dennis
 *  Added routines to use trapezoidal rule to numerically integrate
 *  a function, and calculate moments of functions given by tablulated
 *  data points over an interval [a,b]
 *
 *  Revision 1.3  2000/07/13 14:27:27  dennis
 *  Removed extra ;
 *
 *  Revision 1.2  2000/07/10 22:25:13  dennis
 *  Now Using CVS 
 *
 *  Revision 1.7  2000/06/13 16:07:26  dennis
 *  fixed error message in HistogramMoment()
 *
 *  Revision 1.6  2000/06/12 14:46:10  dennis
 *  Fixed Integration and moment calculation for histograms.  Originally the
 *  histogram values were treated like ordinary function values and the
 *  integral was calculated as  SUM( y * dx ).  Now the integration is just
 *  done as SUM( y ) since the histogram values already give the total counts
 *  on an interval, not a count rate.
 *
 *  Revision 1.5  2000/05/11 16:08:13  dennis
 *  Added RCS logging
 *
 */

package DataSetTools.math;

import DataSetTools.util.*;

/**
 *  Basic numerical analysis operations, such as integration, calculation 
 *  of first and second moments, etc.   
 */
public final class NumericalAnalysis 
{
  /*
   * Don't let anyone instantiate this class.
   */
  private NumericalAnalysis() {}

  /**
    * Do numerical integration of histogram data on the interval [a,b].  This
    * is done by just summing the histogram bins (or portions there of) that
    * overlap the intervalr [a,b].
    *
    * @param   x_vals    Array of bin boundaries for the histogram bins.  There
    *                    MUST be one more bin boundary than the number of y
    *                    values provided in the parameter y_vals.
    *
    * @param   y_vals    Array of counts in histogram bins.
    *
    * @param   a         The left hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   b         The right hand endpoint of the interval over which the
    *                    data is integrated.  
    *
    * @return  An approximate value for the definite integral of the function
    *          represented by this histogram on the interval [a,b].
    */

  public static float IntegrateHistogram( float[] x_vals, 
                                          float[] y_vals, 
                                          float   a, 
                                          float   b   )
  {
    if ( !ValidHistogram( x_vals, y_vals, "IntegrateHistogram" )  ||
         !ValidInterval( a, b, "IntegrateHistogram" )             )
      return 0;

    if ( a >= x_vals[ x_vals.length - 1 ]  ||    
         b <= x_vals[ 0 ]                   )    // interval misses histogram
      return 0;

    double sum = 0.0;

    int   i = 0;                                 // find the first bin and
    while ( a > x_vals[i] )
      i++;

    if ( i == 0 )                                 // add part of that bin to the
      sum = 0.0;                                  // integral value as needed
    else if ( b <= x_vals[i] )
    {
      sum = y_vals[i-1] * ( b - a ) /             // a & b are in the same bin 
                   (x_vals[i] - x_vals[i-1]);     // so we are done.
      return (float)sum; 
    }
    else
      sum = y_vals[i-1] * (x_vals[i] - a) /
                          (x_vals[i] - x_vals[i-1]); 

    i++;                                          // advance to the next sub-
    while ( i < x_vals.length && x_vals[i] <= b ) // interval and integrate
    {                                             // to the last point before b.
      sum += y_vals[i-1];
      i++;
    }
                                                  // now take care of the last
    if ( i < x_vals.length )                      // partial bin, if needed
    {
      sum += y_vals[i-1] * ( b - x_vals[i-1] ) /
                    (x_vals[i] - x_vals[i-1]);
    }

    return (float)sum;
  }


  /**
    * Do numerical integration of function-table data on the interval
    * [a,b].  This is done using the trapezoidal method of
    * integration.
    *
    * @param x_vals Array of x values that the function was evaluated
    * at. There must be the same number of x values as y values.
    *
    * @param   y_vals    Array of counts at x values
    *
    * @param   a         The left hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   b         The right hand endpoint of the interval over which the
    *                    data is integrated.  
    *
    * @return  An approximate value for the definite integral of the function
    *          represented by this histogram on the interval [a,b].
    */
  public static float IntegrateFunctionTable( float[] x_vals, float[] y_vals, 
                                              float   a,      float   b   ){
      if ( (x_vals.length!=y_vals.length) || (a>x_vals[x_vals.length-1]) 
                         || (b<x_vals[0]) || (a>=b) ) return 0;
      
      double sum = 0.0;
      
      for( int i=1 ; i<x_vals.length ; i++ ){
          if(x_vals[i]>=a){
              if(x_vals[i]>b) break;
              sum+=0.5*(y_vals[i]+y_vals[i-1])*(x_vals[i]-x_vals[i-1]);
          }
      }

      return (float)sum;
  }

  /**
    * Calculate 1st, 2nd, 3rd, etc. moments for the given histogram about the
    * specified center point.  The Nth moment is calculated by summing the 
    * terms:
    *             y[i] * (bincenter[i] - center)**N  
    *
    * @param   x_vals    Array of bin boundaries for the histogram bins.  There
    *                    MUST be one more bin boundary than the number of y
    *                    values provided in the parameter y_vals.
    *
    * @param   y_vals    Array of counts in histogram bins.
    *
    * @param   a         The left hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   b         The right hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   center    The center point about which the moment is calculated.
    *  
    * @param   moment    Integer specifying which moment is to be calculated.
    *                    This must be a positive integer.
    *
    * @return  An approximate value for the Nth moment of the function 
    *          represented by this histogram on the interval [a,b], about the
    *          specified center point.
    */

  public static float HistogramMoment( float[] x_vals,
                                       float[] y_vals,
                                       float   a,
                                       float   b,
                                       float   center,
                                       int     moment   )
  {
    if ( moment < 0 )
    {
      System.out.println("ERROR: moment <= 0 in IntegrateHistogram");
      return 0;
    }

    if ( !ValidHistogram( x_vals, y_vals, "HistogramMoment" )  ||
         !ValidInterval( a, b, "HistogramMoment" )             )
      return 0;

    if ( a >= x_vals[ x_vals.length - 1 ]  ||
         b <= x_vals[ 0 ]                   )    // interval misses histogram
      return 0;

    double sum = 0.0;
    double x;

    int   i = 0;                                 // find the first bin and
    while ( a > x_vals[i] )
      i++;

    if ( i == 0 )                                 // add part of that bin to the
      sum = 0;                                    // integral value as needed
    else if ( b <= x_vals[i] )
    {                                             // a & b are in the same bin
      x   = ( a + b ) / 2.0;                      // so we are done.
      sum = y_vals[i-1] * ( b - a ) /
                   (x_vals[i] - x_vals[i-1]) * Math.pow(x-center,moment); 
      return (float)sum;            
    }
    else
    {
      x   = ( x_vals[i] + a )/2.0;
      sum = y_vals[i-1] * (x_vals[i] - a) /
                          (x_vals[i] - x_vals[i-1]) * Math.pow(x-center,moment);
    }

    i++;                                          // advance to the next sub-
    while ( i < x_vals.length && x_vals[i] <= b ) // interval and integrate
    {                                             // to the last point before b.
      x    = ( x_vals[i] + x_vals[i-1] ) / 2.0;
      sum += y_vals[i-1] * Math.pow(x-center,moment);
      i++;
    }
                                                  // now take care of the last
    if ( i < x_vals.length )                      // partial bin, if needed
    {
      x    = ( b + x_vals[i-1] ) / 2.0;
      sum += y_vals[i-1] * ( b - x_vals[i-1] ) /
                    (x_vals[i] - x_vals[i-1]) * Math.pow(x-center,moment);
    }

    return (float)sum;
  }


  /**
    * Do numerical integration of a list of (x,y) points, using the trapezoidal
    * rule.  The x-values are NOT assumed to be evenly spaced, but MUST at least
    * be ordered.  There must be the same number of x values as y values. 
    *
    * @param   x_vals  Array of x-coordinates for the list of (x,y) points.
    *                  There MUST be the same number of x values and y values.
    * 
    * @param   y_vals  Array of y-coordinates for the list of (x,y) points.
    *                  There MUST be the same number of x values and y values.
    *
    * @return  An approximate value for the definite integral of the function
    *          represented by this collection of (x,y) coordinates.
    */

  public static float TrapIntegrate( float[] x_vals, float[] y_vals )
  {
    if ( x_vals.length != y_vals.length )
    {
      System.out.println("ERROR:array length(s) invalid in TrapIntegrate");
      return 0;
    }

    if ( x_vals.length <= 1 )
      return 0;
 
    double sum = 0;
    for ( int i = 0; i < x_vals.length - 1; i++ )
      sum += ( x_vals[i+1] - x_vals[i] ) * ( y_vals[i+1] + y_vals[i] ) / 2;

    return (float)sum; 
  }


  /**
    * Do numerical integration of a list of (x,y) points, using the trapezoidal
    * rule, over a specified interval [a,b].  The x-values are NOT assumed 
    * to be evenly spaced, but MUST at least be ordered.  There must be the 
    * same number of x values as y values.
    *
    * @param   x_vals  Array of x-coordinates for the list of (x,y) points.
    *                  There MUST be the same number of x values and y values.
    *
    * @param   y_vals  Array of y-coordinates for the list of (x,y) points.
    *                  There MUST be the same number of x values and y values.
    *
    * @param   a       The left hand end point of the interval over which
    *                  the function is integrated.
    *
    * @param   b       The right hand end point of the interval over which
    *                  the function is integrated
    *
    * @return  An approximate value for the definite integral of the function
    *          represented by this collection of (x,y) coordinates over the
    *          interval [a,b].
    */

  public static float TrapIntegrate( float[] x_vals, 
                                     float[] y_vals,
                                     float   a,
                                     float   b       )
  {
    if ( x_vals.length != y_vals.length )
    {
      System.out.println("ERROR:array length(s) invalid in TrapIntegrate");
      return 0;
    }
   
    if (x_vals.length <= 1 || b <= x_vals[0]  || a >= x_vals[x_vals.length-1])
      return 0;

    if ( a < x_vals[0] )                 // restrict a, b to tabulated interval
      a = x_vals[0];

    if ( b > x_vals[x_vals.length-1] )
      b = x_vals[x_vals.length-1];

                                          // get interval containing a and
                                          // evaluate y at a
    int start_i = arrayUtil.get_index_of( a, x_vals );   // a <= x_vals[start]
    if ( a > x_vals[start_i] )
      start_i++;

    float y_at_a;
    if ( a == x_vals[start_i] )
      y_at_a = y_vals[start_i];
    else
      y_at_a = y_vals[start_i-1] +  ( y_vals[start_i] - y_vals[start_i-1] )
                                 *  ( a               - x_vals[start_i-1] ) /
                                    ( x_vals[start_i] - x_vals[start_i-1] );


                                          // get interval containing b and
                                          // and evaluate y at b
    int end_i = arrayUtil.get_index_of( b, x_vals );     // x_vals[end] <= b
    if ( b < x_vals[end_i] )
      end_i--;

    float y_at_b;
    if ( x_vals[end_i] == b )
      y_at_b = x_vals[end_i];
    else
      y_at_b = y_vals[end_i] + ( y_vals[end_i + 1] - y_vals[end_i] )
                             * ( b                 - x_vals[end_i] ) /
                               ( x_vals[end_i + 1] - x_vals[end_i] );

                                           // now do the integration, treating
                                           // the first and last partial 
                                           // intervals separately 
    double sum = ( x_vals[start_i] - a ) * ( y_at_a + y_vals[start_i] ) / 2;

    for ( int i = start_i; i < end_i - 1; i++ )
      sum += ( x_vals[i+1] - x_vals[i] ) * ( y_vals[i+1] + y_vals[i] ) / 2;

    sum += ( b - x_vals[end_i] ) * ( y_vals[end_i] + y_at_b ) / 2; 
    
    return (float)sum;
  }


  /**
    * Calculate 1st, 2nd, 3rd, etc. moments for the given tabulated function 
    * about the specified center point, using the trapezoidal rule.  The Nth 
    * moment is calculated by numerically integrating:
    *
    *             f(x) * (x - center)**N
    *
    * The x-values are NOT assumed to be evenly spaced, but MUST at least be 
    * ordered.  There must be the same number of x values as y values.
    *
    * @param   x_vals  Array of x-coordinates for the list of (x,y) points.
    *                  There MUST be the same number of x values and y values.
    *
    * @param   y_vals  Array of y-coordinates for the list of (x,y) points.
    *                  There MUST be the same number of x values and y values.
    *
    * @param   a       The left hand end point of the interval over which
    *                  the function is integrated.
    *
    * @param   b       The right hand end point of the interval over which
    *                  the function is integrated
    * @param   center    The center point about which the moment is calculated.
    *
    * @param   moment    Integer specifying which moment is to be calculated.
    *                    This must be a positive integer.
    *
    * @return  An approximate value for the specified moment of the function
    *          represented by this collection of (x,y) coordinates over the
    *          interval [a,b].
    */

  public static float TrapMoment( float[] x_vals,
                                  float[] y_vals,
                                  float   a,
                                  float   b,
                                  float   center,
                                  float   moment       )
  {
    if ( x_vals.length != y_vals.length )
    {
      System.out.println("ERROR:array length(s) invalid in TrapIntegrate");
      return 0;
    }

    if (x_vals.length <= 1 || b <= x_vals[0]  || a >= x_vals[x_vals.length-1])
      return 0;

    if ( a < x_vals[0] )                 // restrict a, b to tabulated interval
      a = x_vals[0];

    if ( b > x_vals[x_vals.length-1] )
      b = x_vals[x_vals.length-1];

                                          // get interval containing a and
                                          // evaluate y at a
    int start_i = arrayUtil.get_index_of( a, x_vals );   // a <= x_vals[start]
    if ( a > x_vals[start_i] )
      start_i++;

    float y_at_a;
    if ( a == x_vals[start_i] )
      y_at_a = y_vals[start_i];
    else
      y_at_a = y_vals[start_i-1] +  ( y_vals[start_i] - y_vals[start_i-1] )
                                 *  ( a               - x_vals[start_i-1] ) /
                                    ( x_vals[start_i] - x_vals[start_i-1] );


                                          // get interval containing b and
                                          // and evaluate y at b
    int end_i = arrayUtil.get_index_of( b, x_vals );     // x_vals[end] <= b
    if ( b < x_vals[end_i] )
      end_i--;

    float y_at_b;
    if ( x_vals[end_i] == b )
      y_at_b = x_vals[end_i];
    else
      y_at_b = y_vals[end_i] + ( y_vals[end_i + 1] - y_vals[end_i] )
                             * ( b                 - x_vals[end_i] ) /
                               ( x_vals[end_i + 1] - x_vals[end_i] );

                                           // now do the integration, treating
                                           // the first and last partial
                                           // intervals separately

    float  x   = ( x_vals[start_i] + a ) / 2;
    double sum = ( x_vals[start_i] - a ) * (( y_at_a + y_vals[start_i] ) / 2)
                                         *  Math.pow(x-center,moment);

    for ( int i = start_i; i < end_i - 1; i++ )
    {
      x = ( x_vals[i+1] + x_vals[i] ) / 2;
      sum += ( x_vals[i+1] - x_vals[i] ) * (( y_vals[i+1] + y_vals[i] ) / 2) 
                                         *  Math.pow(x-center,moment);
    }

    x    = ( b + x_vals[end_i] ) / 2;
    sum += ( b - x_vals[end_i] ) * (( y_vals[end_i] + y_at_b ) / 2)
                                 *  Math.pow(x-center,moment);

    return (float)sum;
  }


  /**
    * Do numerical integration of a one variable function using a simple  
    * trapezoidal rule integration.
    *
    * @param  f          The function being integrated.  This must provide 
    *                    precision values, so that single precision accuracy is
    *                    easier to achieve.
    * @param  a, b       The endpoints of the interval [a,b] on which f is 
    *                    being integrated.
    * @param  n_grid_pts The number of grid points to be used
    *
    * @return The trapezoidal rule approximation to the integral of f on [a,b]
    *         based on the specified number of grid points.
    */

  public static double TrapIntegrate( IOneVariableFunction f, 
                                      double               a, 
                                      double               b,
                                      int                  n_grid_pts )
  {
    if ( n_grid_pts < 2 )
    {
      System.out.println("ERROR: TrapIntegrate called with too few points");
      return 0;
    }
    double h   = (b-a)/(n_grid_pts-1);
    double x0  = a;
    double x1  = a + h;

    double sum = (f.getValue(a) + f.getValue(b)) / 2.0;
    for ( int i = 1; i < n_grid_pts-1; i++ )
      sum += f.getValue( a+i*h );

    return h*sum;
  }

  /**
    * Do numerical integration of a one variable function using Romberg
    * integration.  Integration is repeated until the relative error is 
    * reduced the to the specified tolerance value, or more than MAX_STEPS
    * have been taken.  Integration begins using 8 grid points and the number
    * of grid points is doubled at each step.
    *
    * @param  f            The function being integrated.  This must provide  
    *                      double precision values, so that single precision
    *                      accuracy is easier to achieve.
    * @param  a, b         The endpoints of the interval [a,b] on which f is
    *                      being integrated.
    * @param  tolerance    The relative error tolerance desired.  Since the 
    *                      result is returned in a float, tolerances of < 1.0E-7
    *                      should be used.
    * @param  MAX_DOUBLES  The maximum number of times the number of intervals
    *                      used should be doubled.  Since the integration 
    *                      begins with 2, a MAX_DOUBLES value of 10 will use
    *                      up to 2048 points, while a MAX_DOUBLES value of
    *                      20 will use up to about 2 million points.   
    *
    * @return An approximation to the integral of f on [a,b]
    */

  public static float RombergIntegrate( IOneVariableFunction f, 
                                        float                a, 
                                        float                b, 
                                        float                tolerance,
                                        int                  MAX_STEPS  )
  {
    int    n_grid_pts = 2; 
    double table[][] = new double[MAX_STEPS+1][MAX_STEPS+1];
    double power,
           factor;
    double approx_1,
           approx_2;

    table[0][0] = TrapIntegrate( f, a, b, n_grid_pts );
    approx_1 = table[0][0];
    approx_2 = approx_1;
    for ( int i = 1; i < MAX_STEPS; i++ )
    {
      n_grid_pts *= 2;
      table[i][0] = TrapIntegrate( f, a, b, n_grid_pts ); 
      power = 1.0;
      for ( int j = 1; j <= i; j++ )
      {
        power *= 4.0;
        factor = power - 1.0;        
        table[i][j] = table[i][j-1] + (table[i][j-1] - table[i-1][j-1])/factor;
      }
      approx_2 = table[i][i];
      if ( i > 3 &&                             // use at least 16 points 
           Math.abs(approx_2 - approx_1) < tolerance * Math.abs(approx_2) )
        return (float)approx_2;

      approx_1 = approx_2; 
    }
    return (float)approx_2;
  }


  /**
    * Calculate 1st, 2nd, 3rd, etc. moments for the given function about the
    * specified center point.  The Nth moment is calculated by integrating:
    *
    *             f(x) * (x - center)**N
    *
    * @param  f          The function being integrated.  This must provide
    *                    double precision values, so that single precision
    *                    accuracy is easier to achieve.
    *
    * @param   a         The left hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   b         The right hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   center    The center point about which the moment is calculated.
    *
    * @param   moment    Integer specifying which moment is to be calculated.
    *                    This must be a positive integer.
    *
    * @return  An approximate value for the moment of the function about the
    *          specified center on [a,b].
    */

  public static float FunctionMoment( IOneVariableFunction f,
                                      float   a,
                                      float   b,
                                      float   center,
                                      int     moment   )
   {
     XminusCtoNtimesF moment_f = new XminusCtoNtimesF( center, moment, f );
     return RombergIntegrate( moment_f, a, b, 0.0000001f, 15 );
   } 


  /**
    * Verify that the parameters given represent valid histgram data.
    *
    * @param   x_vals    Array of bin boundaries for the histogram bins.  There
    *                    MUST be one more bin boundary than the number of y
    *                    values provided in the parameter y_vals.
    *
    * @param   y_vals    Array of counts in histogram bins.
    *
    * @param   method_name  Used in error message if the data is not valid.
    *
    * @return  Returns true if the data represents a valid histogram and
    *          returns false otherwise.
    */

  public static boolean ValidHistogram( float[] x_vals,
                                        float[] y_vals,
                                        String  method_name   )
  {
    if ( y_vals.length != x_vals.length - 1 )         // not a proper histogram
    { 
       System.out.println("ERROR: Data is not HISTOGRAM data in "
                                + method_name );
       return false;
    }

    if ( x_vals.length < 2 )                     // not even one full bin
       return false;

    for ( int i = 0; i < x_vals.length - 1; i++ )
      if ( x_vals[i] >= x_vals[i+1] )
      {
        System.out.println("ERROR: x_vals are NOT increasing in " 
                                 + method_name );
        return false;
      }

    return true;
  }

  /**
    * Verify that the interval [a, b] is a valid interval with a < b. 
    *
    * @param   a         The left hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   b         The right hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   method_name  Used in error message if the interval is not valid.
    *
    * @return  Returns true if the interval satisfies a < b and 
    *          returns false otherwise.
    */

  public static boolean ValidInterval( float a, float b, String  method_name )
  {
    if ( b <= a )                                // not a proper interval
    {
      System.out.println("ERROR: Invalid interval in " + method_name );
      return false;
    }

    return true;
  }

  /**
    * Solve f(x)=0 in an interval [a, b] on which the function f() changes
    * sign, using the bisection method.
    *
    * @param   f         Function specifying the equation being solved.
    * @param   a         The left hand endpoint of the interval on which the
    *                    equation is solved.
    * @param   b         The right hand endpoint of the interval on which the
    *                    equation is solved.
    * @param   n_steps   The number of steps used for the bisction method. 
    *                    Since the interval is divided in two equal length
    *                    pieces at each step, the solution is known to within
    *                    (b-a)/2**n after n steps.
    *
    * @return  Returns a solution to f(x) = 0 IF there is a change of sign 
    *          on the interval and returns Double.NaN if there is no change
    *          of sign.
    */

  public static double BisectionMethod( IOneVariableFunction f, 
                                        double               a, 
                                        double               b, 
                                        int                  max_tries )
  {
    double midpoint;
    int    n_tries;
                                        // we can't find the zero if the
                                        // function doesn't change sign
    double f_a = f.getValue(a);
    double f_b = f.getValue(b);
    if ( f_a * f_b >= 0 )  
      return Double.NaN;

    n_tries = 0;                        // set initial conditions for the loop
    midpoint = ( a + b ) / 2.0;
    double f_mid = f.getValue(midpoint);

    while ( n_tries < max_tries && f_mid != 0.0 )
    {
      if ( f_a * f_mid > 0 )            // same sign at a and at midpoint
      {                                 // so change to the right half interval
         a = midpoint;
         f_a = f.getValue(a);
      }
      else
        b = midpoint;                 // else change to the left half interval 

      midpoint = ( a + b ) / 2.0;       // recalculate midpoint for the new
      f_mid = f.getValue(midpoint);     // interval

      n_tries++;
    }

    return midpoint;                    // the midpoint should approximate
                                        // the solution to f(x) = 0;
  }

}
