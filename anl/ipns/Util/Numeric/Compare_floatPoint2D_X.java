/*
 *  @(#)arrayUtil.java  2000/09/26   Dennis Mikkelson
 *
 *  Comparator function to allow sorting of 2D points
 *
 *  $Log$
 *  Revision 1.1  2000/10/03 22:33:03  dennis
 *  Comparator function for sorting points
 *
 *
 */
package DataSetTools.util;

import java.util.*;

/**
 *  This class implements the Comparator interface for objects of type
 *  floatPoint2D, to allow for sorting based on the X-coordinate.
 *
 */

public class Compare_floatPoint2D_X extends    Object
                                    implements Comparator
{

  /**
   *  Compare two floatPoint2D objects, based on their X-coordianates.
   *
   *  @param  o1   the first floatPoint2D object
   *  @param  o2   the second floatPoint2D object
   *
   *  @returns   Return -1 if o1.x < o2.x, +1 if o1.x > o2.x, and zero
   *             otherwise.
   */
  public int compare( Object o1, Object o2 )
  {
    floatPoint2D  point_1 = (floatPoint2D) o1;
    floatPoint2D  point_2 = (floatPoint2D) o2;

    if ( point_1.x < point_2.x )
      return -1;
    
    if ( point_1.x > point_2.x )
      return 1;
    
    return 0;
  }
  
}
