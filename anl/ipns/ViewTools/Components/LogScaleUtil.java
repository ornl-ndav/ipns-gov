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




/**
 * This class logarithmically maps a linear interval to another linear interval,
 * especially useful for altering pixel coordinates logarithmically.
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
  * the interval destinationmin - destinationmax.
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
    source_min = smin;
    source_max = smax;
    
    // if values are backwards, swap
    if( dmin > dmax )
    {
      float temp = dmin;
      dmin = dmax;
      dmax = temp;
    }
    if(dmin <= 0){
    	dmin = Float.MIN_VALUE;
    }
    
    if(smin <= 0){
    	smin = Float.MIN_VALUE;
    }
    
    
    //if(dmin < 0)
    //{
    //	dmin = (-1)*dmin;
    //}
    // make sure interval is positive
    /*if( dmax < 0 )
    {
      float temp = dmin;
      dmin = -dmax; // when negated, the max becomes the min.
      dmax = -temp; // when negated, the min becomes the max.
    }
    // if dmax > 0, but dmin < 0
    if( dmin < 0 )
      dmin = 0;*/
    dest_min = dmin;
    dest_max = dmax;

  }
  public LogScaleUtil(float sliderPos, float xMax){
  	this.sliderPos = sliderPos;
  	this.xMax = xMax;
  }
  public LogScaleUtil(float[] data_points){
  	float tempmin = Float.MAX_VALUE;
  	float tempmax = Float.MIN_VALUE;
  	for(int i = 0; i < data_points.length; i++){
  		if((data_points[i] < tempmin) && (data_points[i] > Float.MIN_VALUE)){
  			tempmin = data_points[i];
  		}
  	}
  	for(int i = 0; i < data_points.length; i++){
  		if(data_points[i] > tempmax){
  			tempmax = data_points[i];
  		}
  	}
  	source_min = tempmin;
  	dest_min = tempmin;
  	dest_max = tempmax;
  	source_max = tempmax;
  }
 /**
  * This method is for "normal" log scaling.
  *
  *  @param  num
  *  @return source value;
  */ 
  public float toSource( float num )
  {  
    return toSource( num, (double)(source_max/source_min) );
  }

 /**
  * This method converts num to a source value within the interval.
  *
  *  @param  num
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
	//System.out.println("S = " + s);
    //System.out.println("(source_max - source_min) / Math.log(s)" + source_max + " - " + source_min + "/" + Math.log(s));
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
  *  @param  source
  *  @return destination value - the first table value that the source value
  *                              maps to.
  */ 
  public float toDest( float source )
  {
    return toDest(source, (double)(source_max/source_min) );
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


    float returnvalue = (float)( ( Math.exp( (double)(source - source_min) * Math.log(s)/
                    (source_max - source_min) ) - 1 ) *
                    ( dest_max - dest_min)/(s-1) ) + dest_min;
    //System.out.println("toDest returnValue = " + returnvalue);
    return returnvalue;
  }
public float returna(float num){
	return (dest_max - dest_min)/((float)Math.log(dest_max/dest_min));
}
public float truLogScale(float num){
	float returnvalue = 0;
	float a = (dest_max - dest_min)/((float)Math.log(dest_max/dest_min));
	returnvalue = a*(float)Math.log(num/dest_min);
	return returnvalue;
}
public float truLogCoord(float num)
{   

	float returnvalue = 0;
	float a = (dest_max - dest_min)/((float)Math.log(dest_max/dest_min));
	float power = num / a;
	returnvalue = dest_min*((float)Math.exp((double)power));
	return returnvalue;
}
  
 
  //For Test purposes only...
 
 public static void main( String argv[] )
  {
  	
    float smin   = 1f;
    float smax = 1000f;
    float dmin   = 1f;
    float dmax   = 1000f;
    LogScaleUtil testutil = new LogScaleUtil(smin,smax,dmin,dmax);
    //float dmin   = 1f;
    //float dest   = 1000f;
    //FileWriter fw = null;
   // try{
	//fw = new FileWriter("output.txt");	
   // }catch(IOException e){
    //	System.out.println(e);
   // }
	//BufferedWriter br = new BufferedWriter(fw);
    //String answer;
	float[] testarray = new float[1000];
	for(int i=1; i<1000; i++){
		testarray[i] = i;
		//testarray[i] = testutil.truLogCoord(testarray[i]);
		
	}
for (int i = 1; i < 999; i++){
	System.out.println("div = " + testarray[i+1]/testarray[i]);	
}
    /*System.out.println("Smin: " + testutil.toSource(dmin,0) );
    System.out.println("Smax: " + testutil.toSource(dmax,0) );
    System.out.println("Dmin: " + testutil.toDest(smin,0) );
    System.out.println("Dmax: " + testutil.toDest(smax,0) );*/
    
    /*for( int i = (int)(dmin*1000); i < dmax*1000; i++ )
      if( i%10 == 0 )
        System.out.println("Dest/Source: " + (float)i/1000 + "/" + 
	                    testutil.toSource((float)i/1000, .3) );
    System.out.println("");
    for( int j = (int)(smin); j < smax; j++ )
      if( j%10 == 0 )
        System.out.println("Source/Dest: " + j + "/" + 
                        testutil.toDest((float)j, .3) );
    
    //System.out.println("Extreme tests: " + testutil.toSource(1f) );*/
  }
}

