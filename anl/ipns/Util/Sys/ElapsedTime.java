/**
 *  ElapsedTime.java 
 *
 *
 *  $Log$
 *  Revision 1.1  2001/04/20 20:18:09  dennis
 *  Simple "stopwatch" object for performance testing.
 *
 *
 */ 

package DataSetTools.util;

import java.util.*;

/**
 *  Simple timer object for performance testing.
 */

public class ElapsedTime
{
  long      base_time;

  /**
   *  Construct an ElapsedTime object, and start measuring elapsed time from 
   *  the time it was constructed.
   */
  public ElapsedTime() 
  {
    base_time = System.currentTimeMillis();
  }

  /**
   *  Get the elapsed time since this timer was constructed, or was last
   *  reset.
   *
   *  @return   The elapsed time in seconds.
   */
  public float elapsed()
  {
    return ( System.currentTimeMillis() - base_time ) / 1000.0f;
  }

  /**
   *  Reset the elapsed time to zero.
   */
  public void reset()
  {
    base_time = System.currentTimeMillis();
  }

  /*
   *   Main program for testing purposes only
   */
  public static void main( String args[] )
  {
    float        time;
    ElapsedTime  timer = new ElapsedTime();

    timer.reset();
    time = timer.elapsed();
    System.out.println("Initially, elapsed time = " + time );

    timer.reset();
    double x = 0;
    for ( int i = 0; i < 10000000; i++ )
      x = Math.cos(x);

    time = timer.elapsed();
    System.out.println("After ten million double precision cosines, " +
                       "elapsed time = " + time );

    timer.reset();
    time = timer.elapsed();
    System.out.println("After reset, elapsed time = " + time );

  }
}
