/*
 * File:  Gaussian.java
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
 *  Revision 1.4  2004/03/12 01:59:00  dennis
 *  Moved to package gov.anl.ipns.MathTools.Functions
 *
 *  Revision 1.3  2002/11/27 23:14:24  pfpeterson
 *  standardized header
 *
 *  Revision 1.2  2002/06/17 22:12:53  dennis
 *  Now uses doubles for calculation.
 *
 *  Revision 1.1  2002/04/11 20:59:09  dennis
 *  Class for Gaussian distribution function of one variable
 *  controlled by parameters giving the peak position, amplitude
 *  and full width at half max.
 *
 */

package gov.anl.ipns.MathTools.Functions;

import java.io.*;

/**
 * This class implements a Gaussian distribution model of a peak as a 
 * parameterized function of one variable 
 */

public class Gaussian extends    OneVarParameterizedFunction
                      implements Serializable
{
                            // conversion constant to switch between the 
                            // standard deviation, sigma, and the full width,
                            // half max. ( fwhm = sigma * SIGMA_TO_FWHM )     
  public static final double SIGMA_TO_FWHM_D=(2.0*Math.sqrt(2.0*Math.log(2.0)));
  public static final float SIGMA_TO_FWHM = 
                                     (float)(2.0*Math.sqrt(2.0*Math.log(2.0)));

  /**
   *  Construct a new Gaussian function object with the specified
   *  characteristics 
   *
   *  @param  position  The position of the peak
   *  @param  amplitude The amplitude of the peak
   *  @param  fwhm      The full width at half max of the peak
   */
   public Gaussian( double position, double amplitude, double fwhm )
   {
     super( "Gaussian", new double[3], new String[3] );
     parameter_names[0] = "Position"; 
     parameter_names[1] = "Amplitude";
     parameter_names[2] = "FWHM";
     parameters[0] = position;
     parameters[1] = amplitude; 
     parameters[2] = fwhm; 
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
    double sigma = parameters[2] / SIGMA_TO_FWHM_D;
    
    if ( domain.contains( (float)x ) )
      return parameters[1] *
             Math.exp(-(x-parameters[0])*(x-parameters[0])/(2.0*sigma*sigma));
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
    double sigma  = parameters[2] / SIGMA_TO_FWHM_D;
    double temp;
    double x_min  = domain.getStart_x();
    double x_max  = domain.getEnd_x();

    for ( int i = 0; i < x.length; i++ )
    { 
      if ( x[i] >= x_min && x[i] <= x_max )
      {
        temp = (x[i]-parameters[0])/sigma;
        vals[i] = parameters[1] * Math.exp(-(temp*temp)/2.0);
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
    double sigma = parameters[2] / SIGMA_TO_FWHM;
   
    if ( domain.contains( x ) )
      return (float)( parameters[1] *
             Math.exp(-(x-parameters[0])*(x-parameters[0])/(2.0*sigma*sigma)) );
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
    double sigma  = parameters[2] / SIGMA_TO_FWHM;
    double temp;
    float  x_min  = domain.getStart_x();
    float  x_max  = domain.getEnd_x();

    for ( int i = 0; i < x.length; i++ )
    {
      if ( x[i] >= x_min && x[i] <= x_max )
      {
        temp = (x[i]-parameters[0])/sigma;
        vals[i] = (float)(parameters[1] * Math.exp(-(temp*temp)/2.0));
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
      System.out.println("With standard normal distribution");

      Gaussian peak = new Gaussian( 0, 1, 1 );

      double x  = 0.5;
      float  xf = 0.5f;
      System.out.println("The double value at " +x+ " is " +peak.getValue(x) ); 
      System.out.println("The float value at " +x + " is " +peak.getValue(xf));

      double[] xs = { -1, -0.5,  0, 0.5,  1 }; 
      double[] ys = peak.getValues( xs );
      System.out.println("Double Values are:");
      for ( int i = 0; i < xs.length; i++ )
        System.out.println("xs[i] = " + xs[i] + ", " + "ys[i] = " + ys[i] );

      float[] xfs = { -1, -0.5f, 0, 0.5f, 1 }; 
      float[] yfs = peak.getValues( xfs );
      System.out.println("Float Values are:");
      for ( int i = 0; i < xfs.length; i++ )
        System.out.println("xfs[i] = " + xfs[i] + ", " + "yfs[i] = " + yfs[i] );
    }
}
