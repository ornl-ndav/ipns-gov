/*
 * File:  GSASTOFProfileFunction1.java
 *
 * Copyright (C) 2006, Julian Tao
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 *
 *
 */

package gov.anl.ipns.MathTools.Functions;

import java.io.Serializable;
import gov.anl.ipns.MathTools.LinearAlgebra;
import gov.anl.ipns.Util.Numeric.ClosedInterval;


/**
 * This class implements the GSAS neutron TOF profile function 1, for details see
 * "GSAS manual, pp. 147". The profile function is a convolution of two back-to-back
 * exponentials with a gaussian function. The actual implmentation is through JNI
 * calling of the single precision GSAS Fortran subroutines and functions with C 
 * wrapper code in between.
 */

public class GSASTOFProfileFunction1 extends    OneVarParameterizedFunction
                      implements Serializable
{  
  private GSASFunctions gsasfuns;
  private float expgaus1args[];
//SUBROUTINE EXPGAUS1(DT,ALP,BET,SIG,PRFUNC,DPRDT,ALPART,BEPART,SGPART)
  
  /**
   *  Construct a new profile function object with the specified
   *  characteristics 
   *
   *  @param  scalef The scale factor of the peak
   *  @param  position  The position of the peak 
   *  @param  alpha     The rise coefficient for the expondentials
   *  @param  beta      The decay coefficient
   *  @param  sigmasqr  The Gaussian variance
   *  @param  m         The slope of the linear background
   *  @param  y0        The linear background: m*(x-x_min)+y0;
   */
   public GSASTOFProfileFunction1( double scalef, double position, 
                    double alpha, double beta, double sigmasqr,
                    double m, double y0 )
   {
     super( "GSASTOFPF1", new double[7], new String[7] );
     parameter_names[0] = "Scale Factor"; 
     parameter_names[1] = "Position";
     parameter_names[2] = "alpha";
     parameter_names[3] = "beta";
     parameter_names[4] = "sigmasqr";
     parameter_names[5] = "m";
     parameter_names[6] = "y0";
     parameters[0] = scalef;
     parameters[1] = position; 
     parameters[2] = alpha;
     parameters[3] = beta;
     parameters[4] = sigmasqr;
     parameters[5] = m;
     parameters[6] = y0;
     gsasfuns = new GSASFunctions();
     expgaus1args = new float[] {0.0f, (float)alpha, (float)beta, (float)sigmasqr,
                            0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
   }
   
//Overwritting the superclass method to make sure that values in 
//parameters[] and expgaus1args[] are synchronized;
  public void setParameters( double parameters[] )
  {
    if ( parameters != null )
    { 
      int num_params = Math.min( this.parameters.length, parameters.length );
      for ( int i = 0; i < num_params; i++ )
        this.parameters[i] = parameters[i];
      for ( int i = 2; i <= 4; i++)
      //updating 3 parameter arguments sent to the GSAS function as well;  
        expgaus1args[i-1] = (float)this.parameters[i];
    }          
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
    if ( domain.contains( (float)x ) ) {
      double dtof = x - parameters[1];
      double x_min  = domain.getStart_x();

      expgaus1args[0] = (float)dtof;
      if (gsasfuns.expgaus1(expgaus1args) != 0) //"0" for normal exit;
        throw new RuntimeException("!!!!!!Failed to call the GSAS Fortran subroutine expguas1().!!!!!!");

      return parameters[0] * expgaus1args[4]
           + parameters[5] * (x-x_min) + parameters[6];
    }
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
    double x0 = parameters[1], h = parameters[0];
    double m = parameters[5], y0 = parameters[6];
    double x_min  = domain.getStart_x();
    double x_max  = domain.getEnd_x();

    for ( int i = 0; i < x.length; i++ )
    { 
      if ( x[i] >= x_min && x[i] <= x_max )
      {
        expgaus1args[0] = (float)(x[i]-x0);
        if (gsasfuns.expgaus1(expgaus1args) != 0) //"0" for normal exit;
            throw new RuntimeException("!!!!!!Failed to call the GSAS Fortran subroutine expgaus1().!!!!!!");

        vals[i] = h * expgaus1args[4] + m * (x[i]-x_min) + y0;
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
    return (float)getValue((double) x);
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
    return LinearAlgebra.double2float(getValues(LinearAlgebra.float2double(x)));   
  }

  public float get_dFdai( float  x, int i )
  {
    return (float)get_dFdai( (double)x, i );
  }

//Overwritting the superclass method to use the numeric derivatives returned by
//the GSAS subroutine;
  public double get_dFdai( double  x, int i )
  {
    if ( i < 0 || i >= numParameters() )
      return 0;
    
    if ( domain.contains( (float)x ) ) {

      expgaus1args[0] = (float)(x - parameters[1]);
      if (gsasfuns.expgaus1(expgaus1args) != 0) //"0" for normal exit;
          throw new RuntimeException("!!!!!!Failed to call the GSAS Fortran subroutine expgaus1().!!!!!!");

      if (i == 0)
        return expgaus1args[4]; //diff(f, scale_factor);
      else if (i < 5)
        return parameters[0] * expgaus1args[i+4];
      else if (i == 5)
        return x-(double)domain.getStart_x();
      else
        return 1.0;
    }
    else
      return 0; 
  }
  
  public float[] get_dFdai( float  x[], int i )
  {
    double x_d[] = LinearAlgebra.float2double( x );
    double derivs[] = get_dFdai( x_d, i );
    return LinearAlgebra.double2float( derivs ); 
  }
  
  public double[] get_dFdai( double x[], int i )
    {
      double derivs[] = new double[ x.length ];
      double x_min = domain.getStart_x();

      if ( i < 6 )      
        for ( int k = 0; k < x.length; k++ ) 
          derivs[k] = get_dFdai(x[k], i); 
      else if ( i == 6 )      
        for ( int k = 0; k < x.length; k++ ) 
          derivs[k] = x[k] - x_min;
      else
        for ( int k = 0; k < x.length; k++ ) 
          derivs[k] = 1.0; 

      return derivs;
    }
    
  public float[]  get_dFda( float  x )
  {
    float result[] = new float[ numParameters() ];
    for ( int i = 0; i < result.length; i++ )
      result[i] = (float)get_dFdai( (double)x, i );

    return result;
  }
  
  public double[] get_dFda( double x )
  {
    double result[] = new double[ numParameters() ];
    for ( int i = 0; i < result.length; i++ )
      result[i] = get_dFdai( x, i );

    return result;
  }

 /* -------------------------------------------------------------------------
  *
  * MAIN  ( Basic main program for testing purposes only. )
  *
  */
    public static void main(String[] args)
    {
      System.out.println("a GSAS TOF profile function 1 peak:");

      double scalef = 97164,
            tof0 = 20958.25,
            alpha = 0.3,
            beta = 0.03,
            sigmasqr = 1030.73,
            m = 0,
            y0 = 0;

      float tof_min = 20600, tof_max = 21400;
      int nbins = 200;
      GSASTOFProfileFunction1 peak = new GSASTOFProfileFunction1( scalef, tof0, alpha, beta, sigmasqr, m, y0 );
      ClosedInterval interval = new ClosedInterval(tof_min, tof_max);
      peak.setDomain(interval);

      for (int i = 0; i < peak.numParameters(); i++) {
        System.out.println(peak.getParameterNames()[i]+": "+peak.getParameters()[i]);
      }
      double x  = 0.5;
      float  xf = 0.5f;
      System.out.println("The double value at " +x+ " is " +peak.getValue(x) ); 
      System.out.println("The float value at " +x + " is " +peak.getValue(xf));

      double[] xs = { -1, -0.5,  0, 0.5,  1 }; 
      double[] ys = peak.getValues( xs );      
      System.out.println("Double Values are:");
      for ( int i = 0; i < xs.length; i++ )
        System.out.println("xs[i] = " + xs[i] + ", " + "ys[i] = " + ys[i]
        +", "+"dFda0 = " + peak.get_dFdai (xs[i],0)
        +", "+"dFda1 = " + peak.get_dFdai (xs[i],1)
        +", "+"dFda2 = " + peak.get_dFdai (xs[i],2)
        +", "+"dFda3 = " + peak.get_dFdai (xs[i],3)
        +", "+"dFda4 = " + peak.get_dFdai (xs[i],4)
        +", "+"dFda5 = " + peak.get_dFdai (xs[i],5)
        );

      float[] xfs = { -1, -0.5f, 0, 0.5f, 1 }; 
      float[] yfs = peak.getValues( xfs );
      System.out.println("Float Values are:");
      for ( int i = 0; i < xfs.length; i++ )
        System.out.println("xfs[i] = " + xfs[i] + ", " + "yfs[i] = " + yfs[i] );
      
/*      
      xfs = new float[nbins+1];
      yfs = new float[nbins+1];
      float dbin = (tof_max-tof_min)/nbins;
      StringBuffer output = new StringBuffer();  
      for (int i = 0; i <= nbins; i++) {
        xfs[i] = tof_min + dbin * i;
        yfs[i] = peak.getValue(xfs[i]);
        System.out.println("xfs[i] = " + xfs[i] + ", " + "yfs[i] = " + yfs[i]
                +", "+"dFda0 = " + peak.get_dFdai (xfs[i],0)
                +", "+"dFda1 = " + peak.get_dFdai (xfs[i],1)
                +", "+"dFda2 = " + peak.get_dFdai (xfs[i],2)
                +", "+"dFda3 = " + peak.get_dFdai (xfs[i],3)
                +", "+"dFda4 = " + peak.get_dFdai (xfs[i],4)
                +", "+"dFda5 = " + peak.get_dFdai (xfs[i],5)
                +", "+"dFda6 = " + peak.get_dFdai (xfs[i],6)
                +", "+"dFda7 = " + peak.get_dFdai (xfs[i],7)
                );
        output.append(" ["+xfs[i]+", "+yfs[i]+"],");
      }
      System.out.println(output); 
*/
      double xds[] = new double[nbins+1];
      double yds[] = new double[nbins+1];
      float dbin = (tof_max-tof_min)/nbins;
      StringBuffer output = new StringBuffer();  
      for (int i = 0; i <= nbins; i++) {
        xds[i] = tof_min + dbin * i;
        yds[i] = peak.getValue(xds[i]);
        System.out.println("xds[i] = " + xds[i] + ", " + "yds[i] = " + yds[i]
                +", "+"dFda0 = " + peak.get_dFdai (xds[i],0)
                +", "+"dFda1 = " + peak.get_dFdai (xds[i],1)
                +", "+"dFda2 = " + peak.get_dFdai (xds[i],2)
                +", "+"dFda3 = " + peak.get_dFdai (xds[i],3)
                +", "+"dFda4 = " + peak.get_dFdai (xds[i],4)
                +", "+"dFda5 = " + peak.get_dFdai (xds[i],5)
                +", "+"dFda6 = " + peak.get_dFdai (xds[i],6)
                );
        output.append(" ["+xds[i]+", "+yds[i]+"],");
      }
      System.out.println(output);         
    }
}
