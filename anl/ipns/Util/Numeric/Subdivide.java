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
  *  @return array containing the step size, starting value, and 
  *          number of steps in positions 0, 1 and 2, respectively.
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

 /* Now express the length of the interval in the form  s_diff * 10^ipower
    where s_diff is in the interval [1., 10.) */
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

 /* Now choose step size to give a reasonable number of subdivisions
    over an interval of length b-a. */

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

 /* Now find the first grid point in the specified interval. */

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
  } /* subDivide */

 
  /**
   *  Subdivide an interval and return points whose floating point
   *  representations are strictly within the interval. 
   *  NOTE: Due to rounding errors, this may not be exactly what is
   *  needed.  In particular if the interval [0,2000] is subdivided into
   *  10 intervals of length 100, the final point is calculated as
   *  2000.0000298..., which is not in the interval.
   */
  public static float[] subdivideStrict( double xmin, double xmax )
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

    float[] result = new float[count];
    for ( i = 0; i < count; i++ )
      result[i] = (float)(start + i * step);

    System.out.println("xmin, xmax = " + xmin + ", " + xmax );
    for ( i = 0; i < count; i++ )
      System.out.println("POINT = " + result[i] );

    System.out.println("POINT = " + (start + count * step) );
 
    return result;
  }

}
