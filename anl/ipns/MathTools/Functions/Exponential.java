/*
 * File:  Exponential.java
 *
 * Copyright (C) 2002, Dennis Mikkelson
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
 *  Revision 1.4  2004/03/12 02:12:58  dennis
 *  Moved to package gov.anl.ipns.MathTools.Functions;
 *
 *  Revision 1.3  2002/11/27 23:14:24  pfpeterson
 *  standardized header
 *
 *  Revision 1.2  2002/06/17 22:11:19  dennis
 *  Now uses doubles for calculation.
 *
 *  Revision 1.1  2002/04/17 21:43:13  dennis
 *  Exponential growth/decay function a*exp(b*x)
 *
 */

package gov.anl.ipns.MathTools.Functions;

import java.io.*;

/**
 * This class implements an exponential growth or decay model, a exp(kx)  as a 
 * parameterized function of one variable 
 */

public class Exponential extends OneVarParameterizedFunction
                                 implements Serializable
{
  /**
   *  Construct a new Exponential model function object with the specified
   *  characteristics 
   *
   *  @param  a  the initial value  
   *  @param  k  the rate of growth (k>0) or decay (k<0)
   */
   public Exponential( double a, double k )
   {
     super( "Exponential", new double[2], new String[2] );
     parameter_names[0] = "Initial Value"; 
     parameter_names[1] = "Growth Rate";
     parameters[0] = a;
     parameters[1] = k; 
   }

  /**
   *  Evaluate the y-value of the exponential at the specified (double) x-value.
   *
   *  @param  x  the point at which the exponential is to be evaluated
   *
   *  @return the value of the exponential function, at the specified point, 
   *          provided the point is in the currently specified domain.
   */
  public double getValue( double x )
  {
    if ( domain.contains( (float)x ) )
      return parameters[0] * Math.exp( parameters[1] * x );
    else
      return 0; 
  }


  /**
   *  Evaluate the exponential function at the specified list of 
   *  (double) x-values.
   *
   *  @param  x  the list of points at which the exponential is to be evaluated
   *
   *  @return an array containing the values of the enxponential at the 
   *          specified points.  If a point is outside of the currently 
   *          specified domain, the value is taken to be 0 at that point.
   */
  public double[] getValues( double x[] )
  {
    double vals[] = new double[x.length];
    double x_min  = domain.getStart_x();
    double x_max  = domain.getEnd_x();
    double a      = parameters[0];
    double k      = parameters[1];

    for ( int i = 0; i < x.length; i++ )
    { 
      if ( x[i] >= x_min && x[i] <= x_max )
        vals[i] = a * Math.exp( k * x[i] );
      else
        vals[i] = 0.0;
    }
    return vals;
  }


  /**
   *  Evaluate the y-value of the exponential at the specified (float) x-value.
   *
   *  @param  x  the point at which the exponential is to be evaluated
   *
   *  @return the value of the exponential function, at the specified point, 
   *          provided the point is in the currently specified domain.
   */
  public float getValue( float x )
  {
    if ( domain.contains( x ) )
      return (float)(parameters[0] * Math.exp( parameters[1] * x ));
    else
      return 0;
  }


  /**
   *  Evaluate the exponential function at the specified list of 
   *  (double) x-values.
   *
   *  @param  x  the list of points at which the exponential is to be evaluated
   *
   *  @return an array containing the values of the enxponential at the 
   *          specified points.  If a point is outside of the currently 
   *          specified domain, the value is taken to be 0 at that point.
   */
  public float[] getValues( float x[] )
  {
    float vals[] = new float[x.length];
    float x_min  = domain.getStart_x();
    float x_max  = domain.getEnd_x();
    double a     = parameters[0];
    double k     = parameters[1]; 

    for ( int i = 0; i < x.length; i++ )
    {
      if ( x[i] >= x_min && x[i] <= x_max )
        vals[i] = (float)(a * Math.exp( k * x[i] ));
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
      System.out.println("With standard normal distribution");

      Exponential f = new Exponential( 100, -0.1f );

      double x  = 0.5;
      float  xf = 0.5f;
      System.out.println("The double value at " +x+ " is " +f.getValue(x) ); 
      System.out.println("The float value at " +x + " is " +f.getValue(xf));

      double[] xs = { -1, -0.5,  0, 0.5,  1 }; 
      double[] ys = f.getValues( xs );
      System.out.println("Double Values are:");
      for ( int i = 0; i < xs.length; i++ )
        System.out.println("xs[i] = " + xs[i] + ", " + "ys[i] = " + ys[i] );

      float[] xfs = { -1, -0.5f, 0, 0.5f, 1 }; 
      float[] yfs = f.getValues( xfs );
      System.out.println("Float Values are:");
      for ( int i = 0; i < xfs.length; i++ )
        System.out.println("xfs[i] = " + xfs[i] + ", " + "yfs[i] = " + yfs[i] );
    }
}
