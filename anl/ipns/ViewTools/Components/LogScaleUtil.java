/*
 * File: LogScaleUtil.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 ***************************************************************************
 * Code for this class was created in direct correlation to code found in  *
 * ImageJPanel.java written by Dennis Mikkelson.                           *
 ***************************************************************************
 * Modified:
 *
 *  $Log$
 *  Revision 1.11  2004/11/11 19:47:58  millermi
 *  - Reimplemented toDest() and toSource(), simplifying the mapping
 *    from source to destination, and back.
 *
 *  Revision 1.10  2004/11/05 22:06:00  millermi
 *  - Reimplemented toDest(float) and toSource(float) for true logscale.
 *
 *  Revision 1.9  2004/07/29 16:44:08  robertsonj
 *  added some comments and javadocs
 *
 *  Revision 1.8  2004/07/28 19:37:51  robertsonj
 *  added truLogScale which scales numbers to a logarithmic scale
 *  added truLogCoord which is the invers of truLogScale
 *
 *  Revision 1.7  2004/05/11 00:51:56  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.6  2004/03/12 00:27:17  millermi
 *  - Changed package.
 *
 *  Revision 1.5  2004/01/08 17:53:52  millermi
 *  - Changed values in main() to test new implementation.
 *
 *  Revision 1.4  2004/01/07 21:35:05  millermi
 *  - Added javadoc comments.
 *  - Removed restriction that destination interval be positive.
 *
 *  Revision 1.3  2003/10/16 05:00:03  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.2  2003/08/07 15:55:22  dennis
 *  - Added "normal" log methods toSource() and toDest()
 *    (Mike Miller)
 *
 *  Revision 1.1  2003/07/05 19:40:41  dennis
 *  Initial Version: Allows user to convert from one interval to another using
 *  logarithmic mapping.
 *
 */

package gov.anl.ipns.ViewTools.Components;

import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;


/**
 * This class logarithmically maps from a source interval
 * [source_min,source_max] to a destination interval [dest_min,dest_max].
 * Both true log and pseudo logs are supported by this class.
 * Pseudo-log uses a log scale factor to adjust the
 * amount of log mapping (used in colorscale) while tru-log maps using log
 * with no variability, thus requiring no scale factor.
 */
public class LogScaleUtil
{
  private float smin;  // source min
  private float smax;  // source max
  private float dmin;  // destination min
  private float dmax;  // destination max
  
 /**
  * Constructor - specify an interval [source_min,source_max] that will be
  * mapped logarithmically itself. All values for this interval must be
  * positive. This constructor automatically sets the destination interval
  * to the source interval. Use this constructor to logarithmically scale
  * an interval while keeping the endpoints the same.
  *
  *  @param  source_min - The interval minimum, (min > 0)
  *  @param  source_max - the interval maximum, (max > 0)
  */ 
  public LogScaleUtil( float source_min, float source_max )
  { 
    // If values are the same, increment the max by 1.
    if( source_min == source_max )
      source_max += 1f;
    // if values are backwards, swap
    if( source_min > source_max )
    {
      smin = source_max;
      smax = source_min;
    }
    else
    {
      smin = source_min;
      smax = source_max;
    }
    // If min is negative, return error message.
    if( smin < 0 )
    {
      System.out.println("Error - Invalid interval ["+smin+","+smax+"]"+
                         ". Range must be positive. Range changed to ["+
			 (smax/2f)+","+smax+"] (LogScaleUtil.java)" );
      smin = smax/2f;
    }
    dmin = smin;
    dmax = smax;
  }
  
 /**
  * Constructor - specify an interval [source_min,source_max] and interval
  * [dest_min,dest_max] that will logarithmically map values between
  * [source_min,source_max] to the interval [to_min,to_max].
  *
  *  @param  source_min - The min of the source interval getting mapped from.
  *  @param  source_max - The max of the source interval getting mapped from.
  *  @param  dest_min - The min of the destination interval getting mapped to.
  *  @param  dest_max - The max of the destination interval getting mapped to.
  */ 
  public LogScaleUtil( float source_min, float source_max,
                       float dest_min, float dest_max )
  {
    // Initialize min and max using other constructor.
    this(source_min,source_max);
    // If values are equal, increment the max by 1.
    if( dest_min == dest_max )
      dest_max += 1f;
    dmin = dest_min;
    dmax = dest_max;
  }
  
 /**
  * This method is for "normal" log mapping from the source interval to the
  * destination interval.
  *
  *  @param  num Source number on interval [source_min,source_max].
  *  @return Log(num) mapped to interval [dest_min,dest_max].
  */ 
  public float toDest( float num )
  {
    // Destination values must be positive to do the log conversion.
    // Restrict num to valid source values.
    if( num < smin )
      num = smin;
    else if( num > smax )
      num = smax;
    float a = (smax - smin)/((float)Math.log(smax/smin));
    // This will map num to the interval [0,smax-smin]
    float intermediate_value = ( a*(float)Math.log(num/smin) );
    CoordBounds intermediate = new CoordBounds( 0,0, smax-smin, 1f );
    CoordBounds dest = new CoordBounds( dmin, 0, dmax, 1f );
    CoordTransform int_to_dest = new CoordTransform( intermediate, dest );
    // Now map from [0,smax-smin] to [dmin,dmax]
    return int_to_dest.MapXTo(intermediate_value);
  }
 
 /**
  * This method is for "normal" exponential mapping from the destination
  * interval to the source interval. 
  * 
  *  @param  num Number on interval [dest_min, dest_max].
  *  @return Mapped value of num on interval [source_min, source_max].
  */ 
  public float toSource( float num )
  {
    // Restrict num to valid destination values.
    if( num < dmin )
      num = dmin;
    else if( num > dmax )
      num = dmax;
    CoordBounds intermediate = new CoordBounds( 0, 0, smax-smin, 1f );
    CoordBounds dest = new CoordBounds( dmin, 0, dmax, 1f );
    CoordTransform int_to_dest = new CoordTransform( intermediate, dest );
    // Map num to the [0,smax-smin] interval.
    num = int_to_dest.MapXFrom(num);
    
    // Convert from log to linear.
    float a = ((float)Math.log(smax/smin))/(smax - smin);
    float power = num * a;
    return ( smin*((float)Math.exp((double)power)) );
  }
  
 /**
  * This method is for "pseudo" log mapping from the source interval to the
  * destination interval.
  *
  *  @param  num Source number on interval [source_min,source_max].
  *  @param  s - logscale value
  *  @return Log(num) mapped to interval [dest_min,dest_max].
  */
  public float toDest( float num, double s )
  {
    // clamp number to the interval [min,max]
    if( num < smin )
    {
      if( -num > smin )
        num = -num;
      else
        num = smin;
    }
    if( num > smax )
      num = smax;
                                       
    if ( s > 100 )                                // clamp s to [0,100]
      s = 100;
    if ( s < 0 )
      s = 0;

    // Create scale factor.
    s = Math.exp(20 * s / 100.0) + 0.1; // map [0,100] exponentially to get 
                                        // scale change that appears more linear
    double scale = (smax - smin) / Math.log(s);
    // Map from linear to log.
    float returnvalue = (float)( smin + ( scale * Math.log( 1.0 + 
		     ((s-1.0)*(num-dmin)/(dmax - dmin)) ) ) );    
    return returnvalue;
  }
 
 /**
  * This method is for "pseudo" exponential mapping from the destination
  * interval to the source interval. 
  * 
  *  @param  num Number on interval [dest_min, dest_max].
  *  @param  s - logscale value
  *  @return Mapped value of num on interval [source_min, source_max].
  */  
  public float toSource( float num, double s )
  {
    // clamp number to the destination interval
    if( num < dmin )
    {
      if( -num > dmin )
        num = -num;
      else
        num = dmin;
    }
    if( num > dmax )
      num = dmax;
         
    if ( s < 0 )                               // clamp s to [0,100]
    {
      // negate, if -s > 100, let s = 0
      s = -s;
      if( s > 100 )
        s = 0;                              
    }
    if ( s > 100 ) 
      s = 100;

    s = Math.exp(20 * s / 100.0) + 0.1; // map [0,100] exponentially to get 
                                        // scale change that appears more linear

    // Map a log value to a linear value.
    float returnvalue = (float)( ( Math.exp( (double)(num - smin) *
                    Math.log(s) / (smax - smin) ) - 1 ) *
                    ( dmax - dmin)/(s-1) ) + dmin;
    return returnvalue;
  }
 
  //For Test purposes only...
 
  public static void main( String args[] )
  {
    float min   = 1f;
    float max   = 1000f;
    float nmin  = 50f;
    float nmax  = 50000f;
    
    // Test toDest() and toSource().
    LogScaleUtil testutil = new LogScaleUtil(min,max);
    System.out.println("Min/Max:"+min+"/"+max);
    System.out.println("1 to log: "+testutil.toDest(1f));
    System.out.println("2 to log: "+testutil.toDest(2f));
    System.out.println("3 to log: "+testutil.toDest(3f));
    System.out.println("5 to log: "+testutil.toDest(5f));
    System.out.println("10 to log: "+testutil.toDest(10f));
    System.out.println("100 to log: "+testutil.toDest(100f));
    System.out.println("1000 to log: "+testutil.toDest(1000f));
    System.out.println("log(10) to linear: "+testutil.toSource(
                                                 testutil.toDest(10f) ) );
  }
}

