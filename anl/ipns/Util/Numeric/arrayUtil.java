/*
 * File:  arrayUtil.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 *  Revision 1.17  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.16  2002/04/11 21:17:06  dennis
 *  Fixed documentation for get_index_of() method.
 *  interpolate() method now includes the endpoints in the
 *  special cases that are filtered out.
 *
 *  Revision 1.15  2002/03/18 21:15:49  dennis
 *  The reverse() method now just returns if the array is null.
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
    if ( arr == null )              // nothing to do
      return;

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
   *  @return The last index, i, for which  x_vals[i] <= x, provided that
   *          x is between x_vals[0] and x_vals[x_vals.length-1].  
   *          If the array is empty or if x is outside of the interval from
   *          x_vals[0] to x_vals[x_vals.length-1], this returns -1.
   */
   
   public static int get_index_of( float x, float x_vals[] )
   {
     if ( x_vals == null || x_vals.length <= 0 )
       return -1;

     if ( x < x_vals[0] )                           // x to left of all values
       return -1;

     else if ( x > x_vals[ x_vals.length-1 ] )      // x to right of all values
       return -1;

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
       else
         first = mid + 1;
     }

    if ( found )                          // if exact value is in list, return
      return mid;                         // the position where it occurred.
    else
      return last;                        // first & last have crossed
   }


  /**
   *  Find a value in an array of ints that is IN INCREASING ORDER, by using
   *  a binary search.
   *
   *  @param  x       The value to find
   *  @parma  x_vals  The array of x values IN INCREASING ORDER.
   *  @param  first   The first position to be considered in the search.
   *  @param  last    The last position to be considered in the search.
   *
   *  @return  The index at which x was found in the array, or -1 if x was
   *           not found.
   */

   public static int get_index_of( int  x, 
                                   int  x_vals[], 
                                   int  first, 
                                   int  last )
   {
     if ( x_vals == null || x_vals.length <= 0 )
       return -1;

     if ( x < x_vals[0] )                           // x to left of all values
       return -1;

     else if ( x > x_vals[ last ] )                 // x to right of all values
       return -1;

     if ( first > last )
       return -1;

                                             // do binary search to find value
     if ( first < 0 )
       first = 0;

     if ( last > x_vals.length - 1 )
       last = x_vals.length - 1;

     int     mid   = 0;
     boolean found = false;

     while ( !found && first <= last )
     {
       mid   = (first + last) / 2;
       if ( x == x_vals[mid] )
         found = true;
       else if ( x < x_vals[mid] )
         last = mid - 1;
       else 
         first = mid + 1;
     }

    if ( found )                          // if exact value is in list, return
      return mid;                         // the position where it occurred.
    else
      return -1;                          // first & last have crossed, so
                                          // not in list
   }


/**
 *  Obtain an approximate value for y(x) by interpolating using tables of 
 *  values for x and y.  If the x_value is outside of the interval of x values
 *  in the table, this returns the first or last y value.  The table of 
 *  x values must be in order and should all be distinct.
 *
 *  @param  x_value      the x value for which the corresponding y value is to
 *                       be interpolated
 *  @param  x[]          array of x values
 *  @param  y[]          array of corresponding y values.  There must be
 *                       at least as many y values as x values.  
 *
 *  @return interpolated y value at the specified x value
 */
public static float interpolate( float x_value, float x[], float y[] )
{
  if ( x_value <= x[0] )
    return y[0];

  if ( x_value >= x[x.length-1] )
    if ( x.length <= y.length )
      return y[ x.length - 1 ];
    else
      return y[ y.length - 1 ];         // take the closest value we have

  int index = arrayUtil.get_index_of( x_value, x );

  if ( x_value == x[index] )            // if exact value is in list, return
    return y[index];                    // the corresponding y value.

  float x1 = x[index];                  // x_value between two listed x_vals
  float x2 = x[index + 1];

  if ( x1 == x2 )                       // duplicate x values 
    return y[index];  

  float y1 = y[index];                  // otherwise, interpolate
  float y2 = y[index+1];
  return y1 + ( x_value - x1 )*( y2 - y1 ) / ( x2 - x1 );
}


/**
 *  Calculate the "signed absolute" total value for an array of numbers.
 *  If all the numbers have the same sign, this is just the ordinary sum.
 *  If the numbers have mixed sign, this will be the sum of the absolute
 *  values of the given numbers.
 *
 *  @param  x   The array of numbers being "summed".
 *
 *  @return  The sum if all the numbers have the same sign, the sum 
 *           of the absolute values otherwise.
 */
public static float SignedAbsSum( float x[] )
{
  if ( x == null || x.length == 0 )
  {
    System.out.println("ERROR: invalid array in arrayUtil.SignedAbsSum()" );
    return Float.NaN;
  }

  boolean all_LE_0 = true;
  boolean all_GE_0 = true;

  for ( int i = 0; i < x.length; i++ )
  {
    if ( x[i] < 0 )
      all_GE_0 = false;  
 
    if ( x[i] > 0 )
      all_LE_0 = false;    
  }

  float sum = 0;
  if ( all_LE_0 || all_GE_0 )
    for ( int i = 0; i < x.length; i++ )         // for same sign, just use sum
      sum += x[i];
  else
    for ( int i = 0; i < x.length; i++ )         // for mixed sign, use abs()
      sum += Math.abs(x[i]);
  
  return sum;
}


/**
 *  Sort an array of 2D points based on the x coordinates of the points.
 *  (Not implemented in Java 1.1 version. )
 *
 *  @param  points   The array of 2D points to sort.
 *
 *  @return interpolated y value at the specified x value
 */
/* ###################
public static void SortOnX( floatPoint2D points[] )
{
  Compare_floatPoint2D_X comp = new Compare_floatPoint2D_X();

  java.util.Arrays.sort( points, comp );
}
*/

/**
 *  Sort an array of integers to place them in increasing order
 *
 *  @param  values  the array of integers to sort
 */
public static void sort( int values[] )
{
  q_sort( values, 0, values.length-1 );
}

/**
 *  Interchange two integers in an array of integers, used by q_sort_ints.
 * 
 *  @param  list  the array holding the elements to interchange.
 *  @param  i     the index of the first element to interchange.
 *  @param  j     the index of the second element to interchange.
 */
public static void swap( int list[], int i, int j )
{
   int temp = list[i];
   list[i]  = list[j];
   list[j]  = temp;
}


/**
 *  Do a "quick" sort of a portion of an array of integers.  This 
 *  implementation of the quick sort algorithm uses the middle element 
 *  of a sub-list as the key to split the sub-list.  While the worst 
 *  case behavior is still O(n**2), it should only occur in rare cases.  
 *  The standard quick sort is O(n**2) for ordered lists, which is a very
 *  common occurence.
 *
 *  @param  list    the array holding the list to sort.
 *  @param  start   the index of the start of the list to sort in the array.
 *  @param  end     the index of the end of the list to sort in the array.
 */
public static void q_sort( int list[], int start, int end )
{
   int i = start;
   int j = end;
   int key;

   if ( i >= j )                      // at most one element, so we're
     return;                          // done with this sublist

   swap( list, start, (i+j)/2 );

   key = list[ start ];
   while ( i < j )
   {
     while ( list[i] <= key && i < end )
       i++;
     while ( list[j] > key )
       j--;
     if ( i < j )
       swap( list, i, j );
   }
   swap( list, start, j );

   q_sort( list, start, j-1 );
   q_sort( list, j+1, end );
}


/**
 *  Determine whether or not an array of integers contains a strictly 
 *  increasing sequence of distinct integer values.
 *
 *  @param  list  The array of integers to check.
 *
 *  @return  True if the list is empty, or if the list contains an increasing
 *           sequence of distinct integers.  Return false of if there are 
 *           duplicate values, or if the list is not in increasing order.
 */
public static boolean increasing( int list[] )
{
  if ( list == null || list.length <= 0 )
    return true;

  boolean is_increasing = true;
  for ( int i = 0; i < list.length - 1; i++ )
    if ( list[i] >= list[i+1] )
      is_increasing = false;
   
  return is_increasing;
}


/**
 *  Make a list of the distinct integers occurring in an array and place them
 *  in increasing order.
 *
 *  @param  list  The array of integers to use to build the increasing list.
 *
 *  @return  A new empty array if the list is null or empty.  If the list is
 *           non-empty, the new array will be non-empty and will contain all of
 *           the distinct integer values, sorted in increasing order.
 */
public static int[] make_increasing( int list[] )
{
  if ( list == null || list.length <= 0 )
    return new int[0];

  if (increasing( list ))
  {
    int new_list[] = new int[ list.length ];
    System.arraycopy( list, 0, new_list, 0, list.length );
    return new_list;
  }
                                             // put in order 
  boolean in_order = true;
  for ( int i = 0; i < list.length - 1; i++ )
    if ( list[i] > list[i+1] )
      in_order = false;

  int temp[];                   
  if ( in_order )
    temp = list;
  else
  { 
    temp = new int[list.length];
    System.arraycopy( list, 0, temp, 0, list.length );
    sort( temp );
  }
                                            // filter out duplicates 
  int temp2[] = new int[temp.length];
  temp2[0]    = temp[0];
  int n_used = 1;
  for ( int i = 1; i < temp.length; i++ )
  if ( temp[i] != temp2[n_used-1] )
  {
    temp2[n_used] = temp[i];
    n_used++;
  }
                                            // return distinct values 
  if ( n_used == temp2.length )
    return temp2;
  else
  {
    int temp3[] = new int[ n_used ];
    System.arraycopy( temp2, 0, temp3, 0, n_used );
    return temp3;
  }

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
   *  Get the largest difference between successive values in an array.
   *
   *  @param  arr  The array in which the largest step is to be found
   *
   *  @return  The largest difference between successive values in the array.
   *           If the array is empty, or if there is only one element in 
   *           the array, return 0.
   */
  public static float getMaxStep( float arr[] )
  {
    if ( arr == null || arr.length <= 1 )
      return 0;
    else
    {
      float step;
      float max_step = Math.abs( arr[1] - arr[0] );
      for ( int i = 2; i < arr.length; i++ )
      {
        step = Math.abs( arr[i] - arr[i-1] );
        if ( max_step < step )
          max_step = step;
      }
      return max_step;
    }
  }


  /**
   *  Get the smallest difference between successive values in an array.
   *
   *  @param  arr  The array in which the smallest step is to be found
   *
   *  @return  The smallest difference between successive values in the array.  
   *           If the array is empty, or if there is only one element in 
   *           the array, return 0.
   */
  public static float getMinStep( float arr[] )
  {
    if ( arr == null || arr.length <= 1 )
      return 0;
    else
    {
      float step;
      float min_step = Math.abs( arr[1] - arr[0] );
      for ( int i = 2; i < arr.length; i++ )
      {
        step = Math.abs( arr[i] - arr[i-1] );
        if ( min_step > step )
          min_step = step;
      }
      return min_step;
    }
  }


  /**
   *  Check whether or not the values in an array are essentially evenly
   *  spaced to within a specified percentage.  It is assumed that the
   *  values in the array are either increasing or decreasing.  If max and
   *  min are the largest and smallest absolute differences between successive
   *  values, this function returns true if 100 * (max-min)/max < tol.
   *
   *  @param  arr  The array to be checked for uniform step sizes.
   *  @param  tol  The tolerance for the percentage difference in step sizes. 
   *
   *  @return  True if there is a non-zero step size and the step sizes are
   *           with tol% of the largest step size.  Return false otherwise.
   */
  public static boolean isUniformlySpaced( float arr[], float tol )
  {
    if ( arr == null || arr.length <= 1 )
      return false;
    else
    {
      float max_step = getMaxStep( arr );
      if ( max_step <= 0 )
        return false;

      float min_step = getMinStep( arr );
      if ( 100 * (max_step - min_step )/max_step > tol )
        return false;

      return true;
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

   floatPoint2D points[] = new floatPoint2D[5];
   points[0] = new floatPoint2D( 7, 30 );
   points[1] = new floatPoint2D( 4, 25 );
   points[2] = new floatPoint2D( 9, 35 );
   points[3] = new floatPoint2D( 1, 10 );
   points[4] = new floatPoint2D( 4, 26 );

//   arrayUtil.SortOnX( points );
   for ( int i = 0; i < 5; i++ )
     System.out.println( points[i] );
  }

}
