/*
 * @(#)ClosedInterval.java  2000/10/19  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.3  2001/03/30 19:16:40  dennis
 *  Added method   intersect( interval )
 *
 *  Revision 1.2  2000/11/17 23:38:18  dennis
 *  Minor change to format of output in toString() method.
 *
 *  Revision 1.1  2000/11/07 16:31:25  dennis
 *  Specify a closed interval [a,b] that can degenerate to one point.
 *
 *
 */

package DataSetTools.util;

/**
 *  Objects of this class represent a closed interval of floating point values,
 *  [min,max].   
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
