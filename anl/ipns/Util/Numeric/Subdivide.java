/* 
 * File: Subdivide.java
 *
 * Copyright (C) 2008, Dennis Mikkelson
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
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package gov.anl.ipns.Util.Numeric;

import java.util.Vector;

public class Subdivide
{

 /* ------------------------------- subDivide ------------------------*
 /** 
  * Given an interval [a,b] find a "rounded" step size "step" and a
  * "rounded" starting point "start" in [a,b], so that start+k*step
  * for k = 0,1,... gives a reasonable subdivision of [a,b].
  * NOTE: Due to possible problems with rounding, the returned 
  * starting value may be slightly less than a and the right hand end
  * point, a + (n_steps-1) * step may be slightly more than b.  To
  * get an array of subdivision points that are guaranteed to be
  * contained in [a,b] use the method subdivideStrict(a,b);
  *
  * @param  xmin   The left  hand endpoint of the interval [a,b] 
  * @param  xmax   The right hand endpoint of the interval [a,b] 
  *
  * @return array containing the step size, starting value, and 
  *         number of steps in positions 0, 1 and 2, respectively.
  */
  public static double[] subdivide( double xmin, double xmax )
  {
    final int  MAX_STEPS = 100;

    double   s_diff  = 0;
    int      i_power = 0;
    double   start   = 0;
    double   step    = 0;
    double[] values  = new double[3];

    s_diff = xmax - xmin;

 /*
  *  Now express the length of the interval in the form  s_diff * 10^ipower
  *  where s_diff is in the interval [1., 10.) 
  */

    i_power = 0;
    while ( s_diff >= 10.0 )
    {
      s_diff = s_diff / 10.0;
      i_power = i_power + 1;
    }
    while ( s_diff < 1.0 )
    {
      s_diff = s_diff * 10.0;
      i_power = i_power - 1;
    }

 /*
  *  Now choose step size to give a reasonable number of subdivisions
  *  over an interval of length b-a. 
  */

    if ( s_diff <= 1.2 )
      step = .1 * Math.pow(10.0, i_power );
    else if ( s_diff <= 2.0 )
      step = .2 * Math.pow( 10.0, i_power );
    else if ( s_diff <= 2.5 )
      step = .25 * Math.pow( 10.0, i_power );
    else if ( s_diff <= 5.0 )
      step = .5 * Math.pow( 10.0, i_power );
    else
      step = Math.pow( 10.0, i_power );

 /*
  * Now find the first grid point in the specified interval. 
  */

    start = step * Math.floor( xmin / step );
    if ( start < xmin - step/1000 )
      start += step;

  // NOTE: The following can fail due to rounding errors in the floating point
  //       calculation.  If step is so small that it is below the "noise level"
  //       of floating point arithmetic, it can happen that sum+step is
  //       no different than sum.  To avoid this, we break out of the loop
  //       when too many iterations have occurred, and just return a default 
  //       subdivision.

    double sum     = start;
    int    numstep = 0;
    while( sum <= xmax + step/1000 && numstep < MAX_STEPS )
    {
      sum = sum + step;
      numstep++;
    }

    //System.out.println("Step = " + step );
    //System.out.println("Degree = " + i_power );
    //System.out.println("Start = " + start );
    //System.out.println("NumStep = " + numstep );

    if ( numstep < MAX_STEPS )                // the subdivision succeeded
    {
      values[0] = step;
      values[1] = start;
      values[2] = numstep;
    }
    else                                      // we failed to subdivide, so 
    {                                         // just use the endpoints.
      values[0] = xmax - xmin;
      values[1] = xmin; 
      values[2] = 2;
    }

    return values;
  }

 
 /**
  * Subdivide an interval and return points whose floating point
  * representations are strictly within the interval. 
  * NOTE: Due to rounding errors, this may not be exactly what is
  * needed.  If the interval would divide perfectly with exact arithmetic
  * it may happen that one or both endpoints might be missed due to
  * rounding error in the calculation of the division points.
  *
  * @param  xmin   The left  hand endpoint of the interval [a,b] 
  * @param  xmax   The right hand endpoint of the interval [a,b] 
  *
  * @return array containing the step size, starting value, and 
  *         number of steps in positions 0, 1 and 2, respectively.
  */
  public static double[] subdivideStrict( double xmin, double xmax )
  {
    double[] info = subdivide( xmin, xmax );
    double step  = info[0];
    double start = info[1];

    int i = 0;    
    while ( start + i * step < xmin )
      i++;

    start = start + i * step;

    int count = 0;
    while ( start + count * step <= xmax )
      count++;

    double[] result = new double[count];
    for ( i = 0; i < count; i++ )
      result[i] = start + i * step;

/*
    System.out.println("subdivideStrict: xmin, xmax = " + xmin + ", " + xmax );
    for ( i = 0; i < count; i++ )
      System.out.println("POINT = " + result[i] );

    System.out.println("EXTRA POINT = " + (start + count * step) );
*/
 
    return result;
  }


 /* --------------------------- subDivideLinear -------------------------*
 /** 
  * Given an interval [a,b] return a list of evenly spaced points with
  * "nice" values that subdivide the interval.
  * NOTE: Due to possible problems with rounding, the returned 
  * starting value may be slightly less than a and the right hand end
  * point may be slightly more than b.  To get an array of subdivision 
  * points that are guaranteed to be contained in [a,b] use the method 
  * subdivideStrict(a,b);
  *
  * @param  xmin   The left  hand endpoint of the interval [a,b] 
  * @param  xmax   The right hand endpoint of the interval [a,b] 
  *
  * @return array containing a list of "nice" subdivision points for the
  *         interval.
  */

  public static double[] subdivideLinear( double xmin, double xmax )
  {
    double[] info  = subdivide( xmin, xmax );
    double step    = info[0];
    double start   = info[1];
    int    n_steps = (int)info[2];

    double[] result = new double[n_steps];

    for ( int i = 0; i < n_steps; i++ )
      result[i] = start + i * step;
/*
    System.out.println("subdivideLinear: xmin, xmax = " + xmin + ", " + xmax );
    for ( int i = 0; i < n_steps; i++ )
      System.out.println("POINT = " + result[i] );
*/
    return result;
  }


 /* ---------------------------- subDivideLog ----------------------------*
 /** 
  * Given an interval [a,b] with a > 0 and b > a, return a list of points 
  * with that subdivide the interval.  If the ratio of b/a is less than 10, 
  * this method will juat return a linear subdivision.  Over one order of 
  * magnitude the difference between linear and log division points is not 
  * critical.  If b/a is at least 10, this method will return a list of 
  * points of the form v * 10^N "in" [a,b] with v in {1,2,3,4,5,6,7,8,9}. 
  *
  * NOTE: Due to possible problems with rounding, the first point returned 
  * may be slightly less than a and the last point returned may be slightly 
  * more than b.  
  *
  * @param  xmin   The left  hand endpoint of the interval [a,b].  This
  *                MUST be more than 0.
  * @param  xmax   The right hand endpoint of the interval [a,b].  This
  *                MUST be more than xmin. 
  *
  * @return array containing a list of "nice" subdivision points for the
  *         interval.
  */

  public static double[] subdivideLog( double xmin, double xmax )
  {
    if ( xmin <= 0 )
      throw new IllegalArgumentException( "xmin is <= 0 in subdivideLog " 
                                         + xmin );
    if ( xmax <= xmin )
      throw new IllegalArgumentException( "xmin <= xmin in subdivideLog, " +
                                          "xmin= " + xmin + " xmax = " + xmax);

                                              // if the interval does not even
    if ( xmax / xmin < 10 )                   // span one order of magnitude,
      return subdivideLinear( xmin, xmax );   // just use linear case

//  double[] factors = { 2, 5, 10 }; 
    double[] factors = { 2, 3, 4, 5, 6, 7, 8, 9, 10 }; 
    int exponent = (int)Math.floor( Math.log10( xmin ) ); 

    Vector values = new Vector();

    int i = 0;
    int n_factors = factors.length;
    double value = Math.pow( 10, exponent );
    while ( value < xmin * 0.9999999 )
    {
      value = factors[i] * Math.pow( 10, exponent );
      i = (i + 1) % n_factors;               // i points to the next factor
                                             // to use
      if ( i == 0 )
        exponent++;
    }   

    while ( value <= xmax * 1.0000001 )
    {
      values.add( new Double(value) );
      value = factors[i] * Math.pow( 10, exponent );
      i = (i + 1) % n_factors;               // i points to the next factor
                                             // to use
      if ( i == 0 )
        exponent++;
    }

    double[] result = new double[ values.size() ];
    for ( i = 0; i < result.length; i++ )
      result[i] = (Double)(values.elementAt(i));
 
    return result;
  }

}
