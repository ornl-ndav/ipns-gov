/*
 * File:  Statistics.java    
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
 *  Revision 1.5  2004/03/11 21:57:31  dennis
 *  Changed package to java gov.anl.ipns.MathTools.Statistics
 *
 *  Revision 1.4  2002/11/27 23:15:47  pfpeterson
 *  standardized header
 *
 */

package gov.anl.ipns.MathTools;

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
  
  public static void main( String args[] )
  {
    System.out.println("Empty Main");
  }

}
