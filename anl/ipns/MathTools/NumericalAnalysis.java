/*
 * @(#)NumericalAnalysis.java        1.00 99/07/25  Dennis Mikkelson
 *
 *  Basic numerical analysis operations, such as integration, calculation 
 *  of first and second moments, etc.   
 * 
 */

package DataSetTools.math;

public final class NumericalAnalysis 
{
  /**
   * Don't let anyone instantiate this class.
   */
  private NumericalAnalysis() {}

  /**
    * Do numerical integration of histogram data on the interval [a,b].
    *
    * @param   x_vals    Array of bin boundaries for the histogram bins.  There
    *                    MUST be one more bin boundary than the number of y
    *                    values provided in the parameter y_vals.
    *
    * @param   y_vals    Array of counts in histogram bins.
    *
    * @param   a         The left hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   b         The right hand endpoint of the interval over which the
    *                    data is integrated.  
    *
    * @return  An approximate value for the definite integral of the function
    *          represented by this histogram on the interval [a,b].
    */

  public static float IntegrateHistogram( float[] x_vals, 
                                          float[] y_vals, 
                                          float   a, 
                                          float   b   )
  {
    if ( !ValidHistogram( x_vals, y_vals, "IntegrateHistogram" )  ||
         !ValidInterval( a, b, "IntegrateHistogram" )             )
      return 0;

    if ( a >= x_vals[ x_vals.length - 1 ]  ||    
         b <= x_vals[ 0 ]                   )    // interval misses histogram
      return 0;

    double sum = 0;

    int   i = 0;                                 // find the first bin and
    while ( a > x_vals[i] )
      i++;

    if ( i == 0 )                                 // add part of that bin to the
      sum = 0.0;                                  // integral value as needed
    else if ( b <= x_vals[i] )
    {
      sum = y_vals[i-1] * ( b - a );              // a & b are in the same bin 
      return (float)sum;                          // so we are done.
    }
    else
      sum = y_vals[i-1] * (x_vals[i] - a); 

    i++;                                          // advance to the next sub-
    while ( i < x_vals.length && x_vals[i] <= b ) // interval and integrate
    {                                             // to the last point before b.
      sum += y_vals[i-1] * ( x_vals[i] - x_vals[i-1] );
      i++;
    }
                                                  // now take care of the last
    if ( i < x_vals.length )                      // partial bin, if needed
    {
      sum += y_vals[i-1] * ( b - x_vals[i-1] );
    }

    return (float)sum;
  }




  /**
    * Calculate 1st, 2nd, 3rd, etc. moments for the given histogram.  
    *
    * @param   x_vals    Array of bin boundaries for the histogram bins.  There
    *                    MUST be one more bin boundary than the number of y
    *                    values provided in the parameter y_vals.
    *
    * @param   y_vals    Array of counts in histogram bins.
    *
    * @param   a         The left hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   b         The right hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   moment    Integer specifying which moment is to be calculated.
    *                    This must be a positive integer.
    *
    * @return  An approximate value for the definite integral of the function
    *          represented by this histogram on the interval [a,b].
    */

  public static float HistogramMoment( float[] x_vals,
                                       float[] y_vals,
                                       float   a,
                                       float   b,
                                       int     moment   )
  {
    if ( moment <= 0 )
    {
      System.out.println("ERROR: moment <= 0 in IntegrateHistogram");
      return 0;
    }

    if ( !ValidHistogram( x_vals, y_vals, "IntegrateHistogram" )  ||
         !ValidInterval( a, b, "IntegrateHistogram" )             )
      return 0;

    if ( a >= x_vals[ x_vals.length - 1 ]  ||
         b <= x_vals[ 0 ]                   )    // interval misses histogram
      return 0;

    double sum = 0;
    double x;

    int   i = 0;                                 // find the first bin and
    while ( a > x_vals[i] )
      i++;

    if ( i == 0 )                                 // add part of that bin to the
      sum = 0;                                    // integral value as needed
    else if ( b <= x_vals[i] )
    {                                             // a & b are in the same bin
      x   = ( a + b ) / 2.0;                      // so we are done.
      sum = y_vals[i-1] * ( b - a ) * Math.pow(x,moment); 
      return (float)sum;            
    }
    else
    {
      x   = ( x_vals[i] + a )/2.0;
      sum = y_vals[i-1] * (x_vals[i] - a) * Math.pow(x,moment);
    }

    i++;                                          // advance to the next sub-
    while ( i < x_vals.length && x_vals[i] <= b ) // interval and integrate
    {                                             // to the last point before b.
      x    = ( x_vals[i] + x_vals[i-1] ) / 2.0;
      sum += y_vals[i-1] * ( x_vals[i] - x_vals[i-1] ) * Math.pow(x,moment);
      i++;
    }
                                                  // now take care of the last
    if ( i < x_vals.length )                      // partial bin, if needed
    {
      x    = ( b + x_vals[i-1] ) / 2.0;
      sum += y_vals[i-1] * ( b - x_vals[i-1] ) * Math.pow(x,moment);
    }

    return (float)sum;
  }



  /**
    * Verify that the parameters given represent valid histgram data.
    *
    * @param   x_vals    Array of bin boundaries for the histogram bins.  There
    *                    MUST be one more bin boundary than the number of y
    *                    values provided in the parameter y_vals.
    *
    * @param   y_vals    Array of counts in histogram bins.
    *
    * @param   method_name  Used in error message if the data is not valid.
    *
    * @return  Returns true if the data represents a valid histogram and
    *          returns false otherwise.
    */

  public static boolean ValidHistogram( float[] x_vals,
                                        float[] y_vals,
                                        String  method_name   )
  {
    if ( y_vals.length != x_vals.length - 1 )         // not a proper histogram
    { 
       System.out.println("ERROR: Data is not HISTOGRAM data in "
                                + method_name );
       return false;
    }

    if ( x_vals.length < 2 )                     // not even one full bin
       return false;

    for ( int i = 0; i < x_vals.length - 1; i++ )
      if ( x_vals[i] >= x_vals[i+1] )
      {
        System.out.println("ERROR: x_vals are NOT increasing in " 
                                 + method_name );
        return false;
      }

    return true;
  }

  /**
    * Verify that the interval [a, b] is a valid interval with a < b. 
    *
    * @param   a         The left hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   b         The right hand endpoint of the interval over which the
    *                    data is integrated.
    *
    * @param   method_name  Used in error message if the interval is not valid.
    *
    * @return  Returns true if the interval satisfies a < b and 
    *          returns false otherwise.
    */

  public static boolean ValidInterval( float a, float b, String  method_name )
  {
    if ( b <= a )                                // not a proper interval
    {
      System.out.println("Invalid inteval in " + method_name );
      return false;
    }

    return true;
  }

}
