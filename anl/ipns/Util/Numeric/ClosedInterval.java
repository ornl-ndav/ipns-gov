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
   *          with the specified interval, or null if the intersection is empty.
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
  }

}
