/*
 * @(#)Statistics.java    
 *
 *  Programmer:  Dennis Mikkelson
 * 
 *  $Log$
 *  Revision 1.2  2001/01/29 21:05:52  dennis
 *  Now uses CVS revision numbers.
 *
 *  Revision 1.1  2000/07/10 22:26:14  dennis
 *  July 10, 2000 version... many changes
 *
 *  Revision 1.2  2000/05/11 16:08:13  dennis
 *  Added RCS logging
 */

package DataSetTools.math;

/**
 *  Class that provides basic statistics operations as static methods. 
 *  Currently just provides a least squares fit for a line.
 */

public final class Statistics 
{
  /**
   * Don't let anyone instantiate this class.
   */
  private Statistics() {}

  /**
   *  Find the least squares line that fits the specified data points.
   *  
   *  @param  x_vals   Array of x values, there must be at least 2 values
   *  @param  y_vals   Array of y values, there must be at least 2 values
   *
   *  @return   Returns an array with two entries, the slope and y-intercept
   *            for the best fit line. 
   */

  public static float[] FitLine( float x_vals[], float y_vals[] )
  {
    float parameters[] = new float[2];
    parameters[0] = 0;
    parameters[1] = 0;

    int n_points = Math.min( x_vals.length, y_vals.length );
    if ( n_points < 2 )
    {
      System.out.println("ERROR: FitLine called with less than 2 points"); 
      return parameters;
    }
   
    double sum_x  = 0;
    double sum_x2 = 0;
    double sum_y  = 0;
    double sum_xy = 0;

    for ( int i = 0; i < n_points; i++ )
    {
      sum_x  += x_vals[i];
      sum_x2 += x_vals[i] * x_vals[i];
      sum_y  += y_vals[i];
      sum_xy += x_vals[i] * y_vals[i];
    }

    double determinant = n_points * sum_x2 - sum_x * sum_x;
    if ( determinant == 0 )
    { 
      System.out.println("ERROR: zero determinant in FitLine");
      return parameters;
    }

    parameters[0] = (float)((n_points * sum_xy - sum_x * sum_y) / determinant);
    parameters[1] = (float)((sum_x2 * sum_y - sum_x * sum_xy) / determinant);
    return parameters;
  }
  
}
