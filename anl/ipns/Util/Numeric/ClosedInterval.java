/*
 * File: ClosedInterval.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 *  Revision 1.7  2003/05/02 19:19:17  dennis
 *  Added method niceGrid(n) to calculate a list of roughly n values in the
 *  interval, that are multiples of powers of .1, .2, .25 or .5.  These
 *  values are useful for axis labels, contour levels, etc.
 *
 *  Revision 1.6  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.5  2002/07/23 18:16:53  dennis
 *  Added comment that ClosedInterval should be immutable.
 *
 */

package DataSetTools.util;

/**
 *  Objects of this class represent a closed interval of floating point values,
 *  [min,max].  ClosedInterval objects are immutable.
 */

public class ClosedInterval implements java.io.Serializable {

  private float min;      // The endpoints of the interval, min <= max
  private float max;

  /**
   * Constructs and the unit interval [0,1].
   *
   */
  public ClosedInterval ( )
  {
    this(0.0f, 1.0f);
  }


  /**
   * Constructs the interval [val_1, val_2] if val_1 <= val_2, or the interval
   * [val_2, val_1] if val_1 > val_2. 
   *
   * @param       val_1 The min value for the interval.  If val_1 > val_2, the
   *                    values will be interchanged.
   *
   * @param       val_2 The max value for the interval.  If val_1 > val_2, the
   *                    values will be interchanged..
   */
  public ClosedInterval ( float val_1, float val_2 )
  {
    if ( val_1 <= val_2 )
    {
      min = val_1;
      max = val_2;
    }
    else
    {
      min = val_2;
      max = val_1;
    }
  }

  /**
   * Copy constructor to construct a new interval from a given interval.
   *
   * @param       interval  the original interval.
   *
   */
   public ClosedInterval( ClosedInterval interval ) 
   {
     this( interval.min, interval.max );
   }

  /**
   *  Get the left endpoint of the interval.
   *
   *  @return The left endpoint, min, of the interval [min,max] 
   */
  public float getStart_x()
  {
    return min;
  }

  /**
   *  Get the right endpoint of the interval.
   *
   *  @return The right endpoint, max, of the interval [min,max]
   */
  public float getEnd_x()
  {
    return max;
  }

  /**
   *  Determine whether or not the specified value is in the closed interval. 
   *
   *  @return The left endpoint, min, of the interval [min,max]
   */
  public boolean contains( float x )
  {
    if ( min <= x && x <= max )
      return true;
    else
      return false;
  }

  /**
   * Intersect two ClosedIntervals to obtain a new ClosedInterval, or
   * null if the intervals don't intersect. 
   *
   * @param       interval  The ClosedInterval to intersect with this
   *                        ClosedInterval.
   *
   * @return  A closed interval representing the intersection of this interval
   *          with the specified interval, or null if the intersection is 
   *          empty.
   */
   public ClosedInterval intersect( ClosedInterval interval )
   {
     if ( max < interval.min || min > interval.max )
       return null;

     float intersection_min,
           intersection_max;
     
     intersection_max = Math.min( max, interval.max );
     intersection_min = Math.max( min, interval.min );

     return new ClosedInterval( intersection_min, intersection_max );
   }


  /**
   *  Get a list of grid points that are essentially contained in the
   *  given interval and have a "nice" decimal representation.  The values 
   *  returned are multiples of .1, .2, .25, .5 or 1 and are intended to be
   *  used to calibrate axes, find suitable values for calculating contour
   *  levels, etc.  NOTE: The grid points returned may be beyond the 
   *  endpoints of the interval by as much as 0.01% of the size of the 
   *  interval.
   *
   *  @param min_grids  The minimun number of grids points that should be 
   *                    used.  This can be set to 0 if the default number of
   *                    grid points is ok.  If more grid points are requested
   *                    the step sizes will be repeatedly reduced until the
   *                    number of grid points is at least the requested value.
   *                    Due to rounding errors, the first or last grid point
   *                    may be omitted, so this condition may not be exactly
   *                    met.  
   *
   * @return a list uniformly spaced floats that are "essentially" in this
   *         interval and have a "nice" decimal representation.
   */
   public float[] niceGrid( int min_grids)
   {
      if ( min >= max )     // degenerate case, return just one point
      {
        float[] values = {min};
        return values;
      }

      final double TOL = 0.0001;  // grid points will be included if they are
                                  // within this fraction of the total inteval
                                  // length of either of the endpoints.

      double xmin = min;          // use doubles for calculation
      double xmax = max;
      double s_diff;
      int    i_power;
      double start;
      double step;

      s_diff = xmax - xmin;      // s_diff is guaranteed to be positive

   // express the length of the interval in the form  s_diff * 10^ipower
   // where s_diff is in the interval [1., 10.)
      i_power = 0;
      while ( s_diff >= 10.0 )
      {
         s_diff /= 10.0;
         i_power++;
      }
      while ( s_diff < 1.0 )
      {
         s_diff *= 10.0;
         i_power--;
      }

   // choose a step size to give a "reasonable" number of subdivisions
   // over an interval of length b-a.

      if ( s_diff <= 1.2 )
         step = .1;
      else if ( s_diff <= 2.0 )
         step = .2;
      else if ( s_diff <= 2.5 )
         step = .25;
      else if ( s_diff <= 5.0 )
         step = .5;
      else
         step = 1.0;

      while ( s_diff / step < min_grids )      // reduce step size, if needed
      {                                        // to get enough grid points.
        double normalized_step = step;         // get a normalized step size
        int    power_of_10 = 0;                // between .1 and 1.
        while ( normalized_step < .1 )
        {
          normalized_step *= 10;
          power_of_10++;
        }
        if ( Math.abs( normalized_step - .25 ) > .01 )
          step /= 2;                           // if we aren't dealing with
                                               // a multiple of 0.25, just
                                               // divide step size by 2.
        else                                   // else use multiple of .1
          step = .1 / Math.pow( 10, power_of_10 );
      }

      step *= Math.pow( 10.0, i_power );

   // find the first grid point in the specified interval.

      start = xmin;
      long n = Math.round( start/step );
      if ( Math.abs(n*step-start) < (xmax-xmin)*TOL )    // xmin is basically
                                                         // a multiple of step
        start  = n * step;                               // so just use nearest
                                                         // multiple of step.
      else
      {
        n = (long)Math.floor( start/step );
        start = (n+1) * step;
      }

   // calculate and return the actual division points
   // use points out to "essentially" the endpoint, xmax

      int num_points = 1 + (int)Math.round( (xmax - start)/step );

                                             // discard the last point if it's
                                             // too far out of the interval
      if ( start + (num_points-1)*step - xmax > (xmax-xmin)*TOL )
        num_points--;

      float[] values = new float[num_points];
      for ( int i = 0; i < values.length; i++ )
        values[i] = (float)(start + i * step);

      return values;
   }


 
  /**
   * Returns a representation of this interval as a string.
   */
  public String toString() 
  {
    return "["+min+","+max+"]";
  }

  /* ----------------------------- main --------------------------------- */
  /*
   *  main program for testing purposes.
   */

  public static void main( String argv[] )
  {
    ClosedInterval  i1 = new ClosedInterval( 10, 20 );
    ClosedInterval  i2 = new ClosedInterval( 15, 25 );
    ClosedInterval  i3 = new ClosedInterval( 21, 30 );

    System.out.println("i1 intersect i2 = " + i1.intersect( i2 ));
    System.out.println("i2 intersect i1 = " + i2.intersect( i1 ));
    System.out.println("i2 intersect i3 = " + i2.intersect( i3 ));
    System.out.println("i3 intersect i2 = " + i3.intersect( i2 ));

    if ( i1.intersect( i3 ) == null )
      System.out.println("Empty intersection is null");

    if ( i3.intersect( i1 ) == null )
      System.out.println("Empty intersection is null");

    float nice_pts[];

    System.out.println("Nice Grids on " + i3 );
    nice_pts = i3.niceGrid( 25 );
    for ( int i = 0; i < nice_pts.length; i++ )
      System.out.println(""+nice_pts[i] );
  }

}
