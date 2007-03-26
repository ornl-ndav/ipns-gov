/*
 * File:  Sine.java
 *
 * Copyright (C) 2007, Julian Tao
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
 * Contact : Julian Tao <taoj@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 */

package gov.anl.ipns.MathTools.Functions;

import java.io.*;

/**
 * This class implements a sine function
 */

public class Sine extends    OneVarParameterizedFunction
                      implements Serializable
{
  /**
   *  Construct a new Gaussian function object with the specified
   *  characteristics 
   *
   *  @param  amplitude
   *  @param  period
   *  @param  phase
   *  @param  background
   */
  
   public Sine( double amplitude, double period, double phase, double background )
   {
     super( "Sin", new double[4], new String[4] );
     parameter_names[0] = "Amplitude";
     parameter_names[1] = "Period";
     parameter_names[2] = "Phase";
     parameter_names[3] = "Background";
     parameters[0] = amplitude;
     parameters[1] = period;
     parameters[2] = phase; 
     parameters[3] = background; 
   }

  /**
   *  Evaluate the y-value of the peak at the specified (double) x-value.
   *
   *  @param  x  the point at which the peak is to be evaluated
   *
   *  @return the value of the peak, at the specified point, provided the point
   *          is in the currently specified domain.
   */
  public double getValue( double x )
  {    
    if ( domain.contains( (float)x ) )
      return parameters[0] * Math.sin(2*Math.PI * x / parameters[1] + parameters[2]) + parameters[3];
    else
      return 0; 
  }


  /**
   *  Evaluate the peak at the specified list of (double) x-values.
   *
   *  @param  x  the list of points at which the peak is to be evaluated
   *
   *  @return an array containing the values of the peak, at the specified
   *  points.  If a point is outside of the currently specified domain, the
   *  value is taken to be 0 at that point.
   */
  public double[] getValues( double x[] )
  {
    double vals[] = new double[x.length];
    double x_min  = domain.getStart_x();
    double x_max  = domain.getEnd_x();

    for ( int i = 0; i < x.length; i++ )
    { 
      if ( x[i] >= x_min && x[i] <= x_max )
      {
        vals[i] = parameters[0] * Math.sin(2*Math.PI * x[i] / parameters[1] + parameters[2]) + parameters[3];
      }
      else
        vals[i] = 0;
    }
    return vals;
  }


  /**
   *  Evaluate the y-value of the peak at the specified (float) x-value.
   *
   *  @param  x  the point at which the peak is to be evaluated
   *
   *  @return the value of the peak, at the specified point, provided the point
   *          is in the currently specified domain.
   */
  public float getValue( float x )
  {
   
    if ( domain.contains( x ) )
      return (float)( parameters[0] * Math.sin(2*Math.PI * x / parameters[1] + parameters[2]) + parameters[3] );
    else
      return 0;
  }


  /**
   *  Evaluate the peak at the specified list of (float) x-values.
   *
   *  @param  x  the list of points at which the peak is to be evaluated
   *
   *  @return an array containing the values of the peak, at the specified
   *  points.  If a point is outside of the currently specified domain, the
   *  value is taken to be 0 at that point.
   */
  public float[] getValues( float x[] )
  {
    float  vals[] = new float[x.length];
    float  x_min  = domain.getStart_x();
    float  x_max  = domain.getEnd_x();

    for ( int i = 0; i < x.length; i++ )
    {
      if ( x[i] >= x_min && x[i] <= x_max )
      {
        vals[i] = (float)(parameters[0] * Math.sin(2*Math.PI * x[i] / parameters[1] + parameters[2]) + parameters[3]);
      }
      else
        vals[i] = 0.0f;
    }
    return vals;
  }


 /* -------------------------------------------------------------------------
  *
  * MAIN  ( Basic main program for testing purposes only. )
  *
  */
    public static void main(String[] args)
    {
      System.out.println("sin():");

      Sine wave = new Sine( 1, 2, 3, 4 );

      double x  = 0.5;
      float  xf = 0.5f;
      System.out.println("The double value at " +x+ " is " +wave.getValue(x) ); 
      System.out.println("The float value at " +x + " is " +wave.getValue(xf));

      double[] xs = { -1, -0.5,  0, 0.5,  1 }; 
      double[] ys = wave.getValues( xs );
      System.out.println("Double Values are:");
      for ( int i = 0; i < xs.length; i++ )
        System.out.println("xs[i] = " + xs[i] + ", " + "ys[i] = " + ys[i] + ", " + "ycalc = " + 
            (Math.sin(Math.PI*xs[i]+3)+4) );

      float[] xfs = { -1, -0.5f, 0, 0.5f, 1 }; 
      float[] yfs = wave.getValues( xfs );
      System.out.println("Float Values are:");
      for ( int i = 0; i < xfs.length; i++ )
        System.out.println("xfs[i] = " + xfs[i] + ", " + "yfs[i] = " + yfs[i] );
    }
}
