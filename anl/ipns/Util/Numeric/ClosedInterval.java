/*
 * @(#)ClosedInterval.java  2000/10/19  Dennis Mikkelson
 *
 *  $Log$
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
   * Returns a representation of this interval as a string.
   */
  public String toString() 
  {
    return "["+min+","+max+"]";
  }
}
