/*
 * @(#)arrayUtil.java  1999/01/10   Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.2  2000/07/10 22:52:01  dennis
 *  Standard fonts for labels and borders, etc.
 *
 *  Revision 1.5  2000/06/13 14:39:25  dennis
 *  Added dcoumentation and simplified the method to get a portion of an array.
 *
 *  Revision 1.4  2000/06/08 19:07:37  dennis
 *  Fixed DOS text problem
 *
 *  Revision 1.3  2000/05/11 16:18:22  dennis
 *  Added RCS logging
 *
 *
 */ 
package DataSetTools.util;

/**
 *
 *  This class provides static methods for some basic array operations  
 *  such as reversing, finding values and extracting a portion of
 *  an array of floats. 
 *  
 */
public class arrayUtil 
{

  /**
   * Don't let anyone instantiate this class.
   */
  private arrayUtil() {}


  /**
   *  Reorder the elements of the specified array in reverse order.
   *
   *  @param  arr   The array whose elements are reversed.
   */
  public static void Reverse( float arr[] )
  {
    float temp;
    int   length = arr.length;
    int   length_m_1 = length-1;
    int   k;

    for ( int i = 0; i < length/2; i++ )
    {
      k = length_m_1 - i;
      temp = arr[i];
      arr[i] = arr[k];
      arr[k] = temp;
    }
  }


  /**
   *  Find a value in an array of floats that is IN INCREASING ORDER, by using
   *  a binary search.
   *
   *  @param  x       The value to find
   *  @parma  x_vals  The array of x values IN INCREASING ORDER.
   *
   *  @return The last index, i, for which  x_vals[i] <= x.  If x_vals[i] > x
   *          for all indices, or if the array is empty, this returns -1.
   */
   
   public static int get_index_of( float x, float x_vals[] )
   {
     if ( x_vals == null || x_vals.length <= 0 )
       return -1;

     if ( x < x_vals[0] )                           // x to left of all values
       return -1;

     else if ( x > x_vals[ x_vals.length-1 ] )      // x to right of all values
       return x_vals.length-1;

                                             // do binary search to find value  
     int     first = 0;          
     int     last  = x_vals.length-1;  
     int     mid   = 0;
     boolean found = false;

     while ( !found && first <= last )
     {
       mid   = (first + last) / 2;
       if ( x == x_vals[mid] )
         found = true;
       else if ( x < x_vals[mid] )
         last = mid - 1;
       else if ( x > x_vals[mid] )
         first = mid + 1;
     }

    if ( found )                          // if exact value is in list, return
      return mid;                         // the position where it occurred.
    else
      return last;                        // first & last have crossed
   }


  /**
   *  Extract a sequence of elements from the given array to form a new
   *  array.  If a longer array is needed, the unused positions are filled
   *  with zeros.
   *
   *  @param   arr        the source array from which elements are extracted.
   *  @param   new_length the size of the new array to be created.
   *
   *  @return  An new array filled with elements from the given array and
   *           zero padded if necessary. 
   */
  public static float[] getPortion( float arr[], int new_length )
  {
    float new_arr[] = new float[ new_length ];

    if ( arr.length < new_length )  
    {                                                   // too few, so 0 pad
      System.arraycopy( arr, 0, new_arr, 0, arr.length );
      for ( int i = arr.length; i < new_length; i++ )
        new_arr[i] = 0;
    } 
    else                                                // too many, truncate
      System.arraycopy( arr, 0, new_arr, 0, new_length );

    return new_arr; 
  }


  /**
   *  Get the largest value in an array.  
   *
   *  @param  arr  The array in which the largest value is to be found
   *
   *  @return  The largest value in the array.  If the array is empty, 
   *           return Float.NEGATIVE_INFINITY
   */
  public static float getMax( float arr[] )
  {
    if ( arr == null || arr.length <= 0 )
      return Float.NEGATIVE_INFINITY;
    else
    {
      float max = arr[0];
      for ( int i = 1; i < arr.length; i++ )
        if ( max < arr[i] )
          max = arr[i]; 
      return max;
    }
  }

  /**
   *  Get the smallest value in an array.  
   *
   *  @param  arr  The array in which the smallest value is to be found
   *
   *  @return  The smallest value in the array.  If the array is empty, 
   *           return Float.POSITIVE_INFINITY
   */
  public static float getMin( float arr[] )
  {
    if ( arr == null || arr.length <= 0 )
      return Float.POSITIVE_INFINITY;
    else
    {
      float min = arr[0];
      for ( int i = 1; i < arr.length; i++ )
        if ( min > arr[i] )
          min = arr[i];
      return min;
    }
  }


  /**
   *  Main program for testing purposes only.
   */
  public static void main( String argv[] )
  {
    float test[] = { 1, 2, 3, 4, 5, 6, 7, 8 };

    System.out.println( "Original:");
    for ( int i = 0; i < test.length; i++ )
      System.out.println( test[i] );

    arrayUtil.Reverse( test );
    System.out.println( "Reversed:");
    for ( int i = 0; i < test.length; i++ )
      System.out.println( test[i] );

    float new_test[] = arrayUtil.getPortion( test, 5 );
    System.out.println( "Truncated to 5 entries:");
    for ( int i = 0; i < new_test.length; i++ )
      System.out.println( new_test[i] );

    new_test = arrayUtil.getPortion( test, 10 );
    System.out.println( "Expanded to 10 entries:");
    for ( int i = 0; i < new_test.length; i++ )
      System.out.println( new_test[i] );

  }
}
