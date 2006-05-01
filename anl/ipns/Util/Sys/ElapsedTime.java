/**
 * File:  ElapsedTime.java 
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *  Revision 1.6  2006/05/01 19:24:29  dennis
 *  Now uses the newer high resolution timer, System.nanoTime(),
 *  to get a timing resolution of 20 microseconds or better,
 *  rather than the older System.currentTimeMillis() which had
 *  a timing resolution of at best 1 millisecond on Linux,
 *  and often had lower resolution on various Windwows systems.
 *
 *  Revision 1.5  2004/03/15 23:53:49  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.4  2004/03/11 22:13:14  millermi
 *  - Changed package names and replaced SharedData with
 *    SharedMessages class.
 *
 *  Revision 1.3  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 */ 

package gov.anl.ipns.Util.Sys;

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
    base_time = System.nanoTime();
  }

  /**
   *  Get the elapsed time since this timer was constructed, or was last
   *  reset.
   *
   *  @return   The elapsed time in seconds.
   */
  public float elapsed()
  {
    return ( System.nanoTime() - base_time ) / 1.0e9f;
  }

  /**
   *  Reset the elapsed time to zero.
   */
  public void reset()
  {
    base_time = System.nanoTime();
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
    for ( int i = 0; i < 10000; i++ )
      x = Math.cos(x);

    time = timer.elapsed();
    System.out.println("After ten thousand double precision cosines, " +
                       "elapsed time = " + time );

    timer.reset();
    time = timer.elapsed();
    System.out.println("After reset, elapsed time = " + time );

  }
}
