/*
 * File:  Lorentzian.java
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2002/06/17 22:24:08  dennis
 *  First version of Lorentzian distribution.  The amplitude & FWHM are
 *  "coupled".
 *
 *
 */

package  DataSetTools.functions;

import java.io.*;
import DataSetTools.math.*;
import DataSetTools.dataset.*;

/**
 * This class implements a Lorentzian distribution model of a peak as a 
 * parameterized function of one variable 
 */

public class Lorentzian extends    OneVarParameterizedFunction
                        implements Serializable
{
  /**
   *  Construct a new Lorentzian function object with the specified
   *  characteristics 
   *
   *  @param  position  The position of the peak
   *  @param  amplitude The amplitude of the peak
   *  @param  fwhm      The full width at half max of the peak
   */
   public Lorentzian( double position, double amplitude, double fwhm )
   {
     super( "Lorentzian", new double[3], new String[3] );
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
    if ( domain.contains( (float)x ) )
    {
      double gamma_2 = parameters[2]/2.0;
      double offset  = x - parameters[0];

      return (parameters[1] * gamma_2 / ( offset*offset + gamma_2*gamma_2 ));
    }
    else
      return 0.0; 
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
    if ( domain.contains( (float)x ) )
    {
      double gamma_2 = parameters[2]/2.0;
      double offset  = x - parameters[0];

      return (float)(parameters[1]*gamma_2/(offset*offset + gamma_2*gamma_2));
    }
    else
      return 0.0f;
  }



 /* -------------------------------------------------------------------------
  *
  * MAIN  ( Basic main program for testing purposes only. )
  *
  */
    public static void main(String[] args)
    {
      System.out.println("With standard normal distribution");

      Lorentzian peak = new Lorentzian( 0, 1, 1 );

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
