package DataSetTools.util;

public class arrayUtil 
{

  /**
   * Don't let anyone instantiate this class.
   */
  private arrayUtil() {}


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

  public static float[] getPortion( float arr[], int new_length )
  {
    float new_arr[];

    if ( arr.length < new_length )  
    {                                                   // too few, so 0 pad
      new_arr = new float[ new_length ];
      System.arraycopy( arr, 0, new_arr, 0, arr.length );
      for ( int i = arr.length; i < new_length; i++ )
        new_arr[i] = 0;
    } 
    else
    {                                                   // too many, truncate
      new_arr = new float[ new_length ];
      System.arraycopy( arr, 0, new_arr, 0, new_length );
    }
    return new_arr; 
  }


  public static float getMax( float arr[] )
  {
    if ( arr.length <= 0 )
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


  public static float getMin( float arr[] )
  {
    if ( arr.length <= 0 )
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
