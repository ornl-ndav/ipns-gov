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
 * This class linearly maps the log(destination_value) to
 * a value in the source interval. Both true log and pseudo logs are supported.
 * Pseudo-log uses a log scale factor to map from destination to source
 * (used in colorscale) while tru-log maps the log of a destination value to
 * the source with no scale factor.
 */
public class LogScaleUtil
{
  private float source_min;
  private float source_max;
  private float dest_min;
  private float dest_max;
  private float sliderPos;
  private float xMax;
  
 /**
  * Constructor - construct intervals sourcemin - sourcemax, and map it to
  * the interval destinationmin - destinationmax. The source interval is
  * assumed to be the logarithmic interval, and the destination interval
  * is assumed to be the linear interval. Thus, if source_min = 1 and
  * source_max = 100, a value = 10 would appear half way inbetween while the
  * same value on the destination interval would appear 1/10 of the way.
  *
  *  @param  smin - the source minimum
  *  @param  smax - the source maximum
  *  @param  dmin - the destination minimum
  *  @param  dmax - the destination maximum
  */ 
  public LogScaleUtil( float smin, float smax, float dmin, float dmax )
  { 
  	//System.out.println("in LogscaleUtil");
    // if values are backwards, swap
    if( smin > smax )
    {
      float temp = smin;
      smin = smax;
      smax = temp;
    }
    // Source interval may contain negatives since log(dest_value) is linearly
    // mapped to this interval.
    source_min = smin;
    source_max = smax;
    
    // if values are backwards, swap
    if( dmin > dmax )
    {
      float temp = dmin;
      dmin = dmax;
      dmax = temp;
    }
    
    // make sure interval is positive
    if( dmax < 0 )
    {
      float temp = dmin;
      dmin = -dmax; // when negated, the max becomes the min.
      dmax = -temp; // when negated, the min becomes the max.
    }
    // if dmax > 0, but dmin < 0, set dmin = 1/2 dmax
    else if( dmin < 0 )
      dmin = dmax/2f;
    dest_min = dmin;
    dest_max = dmax;

  }
  
 /**
  * This method is for "normal" log scaling.
  *
  *  @param  num
  *  @return source value;
  */ 
  public float toSource( float num )
  {
    // Destination values must be positive to do the log conversion.
    if( num < 0 )
      return Float.NaN;
    float a = (dest_max - dest_min)/
              ((float)Math.log(dest_max/dest_min));
    // This will map num to the interval [0,dest_max-dest_min]
    float intermediate_value = ( a*(float)Math.log(num/dest_min) );
    CoordBounds intermediate = new CoordBounds( 0,0,dest_max-dest_min,
                                                dest_max-dest_min );
    CoordBounds source = new CoordBounds( source_min,source_min,
                                          source_max, source_max );
    CoordTransform dest_to_source = new CoordTransform( intermediate,
                                                        source );
    // Now map from [0,dest_max-dest_min] to [source_min,source_max]
    return dest_to_source.MapXTo(intermediate_value);
  }

 /**
  * This method converts num to a source value within the interval.
  *
  *  @param  num - Number on the destination interval.
  *  @param  s - logscale value
  *  @return source value;
  */ 
  public float toSource( float num, double s )
  {
    //System.out.println("toSource num = " + num);
    //System.out.println("toSource s = " + s);
    //System.out.println("toSource dest_max = " + dest_max);
    // clamp number to the destination interval
    if( num < dest_min )
    {
      if( -num > dest_min )
        num = -num;
      else
        num = dest_min;
    }
    if( num > dest_max )
      num = dest_max;
                                       
    if ( s > 100 )                                // clamp s to [0,100]
      s = 100;
    if ( s < 0 )
      s = 0;

    //System.out.println("dest_min= " + dest_min);
    //stem.out.println("dest_max= " + dest_max);
	//System.out.println("Source_max = " + source_max);
    s =Math.exp(20 * s / 100.0) + 0.1; // map [0,100] exponentially to get 
                                       // scale change that appears more linear
    double scale = (source_max - source_min) / Math.log(s);
    //System.out.println("scale = " + scale);
    //System.out.println("source_min = " + source_min);
    float returnvalue = (float)( source_min + ( scale * Math.log( 1.0 + 
		     ((s-1.0)*(num-dest_min)/(dest_max - dest_min)) ) ) );
		     //System.out.println("returnvalue = " + returnvalue);     
	return returnvalue;
  } 
 
 /**
  * This method finds the destination value for "normal" log scaling. 
  * 
  *  @param  num
  *  @return destination value - the first table value that the source value
  *                              maps to.
  */ 
  public float toDest( float num )
  {
    // Restrict num to valid source values.
    if( num < source_min )
      num = source_min;
    else if( num > source_max )
      num = source_max;
    CoordBounds intermediate = new CoordBounds( 0,0,dest_max-dest_min,
                                                dest_max-dest_min );
    CoordBounds source = new CoordBounds( source_min,source_min,
                                          source_max, source_max );
    CoordTransform dest_to_source = new CoordTransform( intermediate,
                                                        source );
    // Map num to the [0,dest_max-dest_min] interval.
    num = dest_to_source.MapXFrom(num);
    
    float a = ((float)Math.log(dest_max/dest_min))/(dest_max - dest_min);
    float power = num * a;
    return ( dest_min*((float)Math.exp((double)power)) );
  }
 
 /**
  * This method finds the equivalent destination value for the source value
  * given. 
  * 
  *  @param  source
  *  @param  s - logscale value
  *  @return destination value - the first table value that the source value
  *                              maps to.
  */ 
  public float toDest( float source, double s )
  {
    //System.out.println("toDest source = " + source);
    //System.out.println("toDest s = " + s);
    // clamp number to the destination interval
    if( source < source_min )
    {
      if( -source > source_min )
        source = -source;
      else
        source = source_min;
    }
    if( source > source_max )
      source = source_max;
         
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


    float returnvalue = (float)( ( Math.exp( (double)(source - source_min) *
                    Math.log(s) / (source_max - source_min) ) - 1 ) *
                    ( dest_max - dest_min)/(s-1) ) + dest_min;
    //System.out.println("toDest returnValue = " + returnvalue);
    return returnvalue;
  }
 
  //For Test purposes only...
 
  public static void main( String args[] )
  {
    float smin   = 20f;
    float smax   = 30f;
    float dmin   = 1f;
    float dmax   = 10f;
    
    LogScaleUtil testutil = new LogScaleUtil(smin,smax,dmin,dmax);
    System.out.println("Source Min/Max:"+smin+"/"+smax);
    System.out.println("Dest Min/Max:"+dmin+"/"+dmax);
    System.out.println("1 to log: "+testutil.toSource(1f));
    System.out.println("2 to log: "+testutil.toSource(2f));
    System.out.println("3 to log: "+testutil.toSource(3f));
    System.out.println("5 to log: "+testutil.toSource(5f));
    System.out.println("10 to log: "+testutil.toSource(10f));
  }
}

