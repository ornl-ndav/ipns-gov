/*
 * File: CalibrationUtil.java
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
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2003/05/16 14:57:09  dennis
 *  Added acknowledgement of NSF funding.
 *
 */

package DataSetTools.components.View.Transparency;

import DataSetTools.util.*;

public class CalibrationUtil 
{
   private float xmin;
   private float xmax;
   private int sig_dig;          // significant digits
   private int baseE;            // the exponent all numbers will be based from
   private String format;
   
   public CalibrationUtil( float interval_min, float interval_max, 
                           int signif_dig, String formattee )
   {
      xmin = interval_min;
      xmax = interval_max;
      
      if( xmax < xmin )
      {
         float temp = xmax;
	 xmax = xmin;
	 xmin = temp;
      }
      if( xmax == xmin )
      {
         SharedData.addmsg("ERROR -- endpoints of interval " + 
     			    "are equal. Interval has been adjusted.");
         xmax = xmax + 1;
      }
      
      sig_dig = signif_dig;
      baseE = 0;
      
      // if either of the bounds triggers engineer format, all numbers
      // will be represented in engineer format
      if( formattee == Format.AUTO && 
            ( Math.abs(xmax) >= 10000 || Math.abs(xmin) < .001 ) )
         formattee = Format.ENGINEER;	  
      format = formattee;
   }
   
   public CalibrationUtil( float interval_min, float interval_max )
   {
      this( interval_min, interval_max, 4, Format.AUTO );
   }   
   
  /* ------------------------------- subDivide ------------------------*
   * Method taken from Subdivide.c by Dennis Mikkelson
   * Given an interval [a,b] find a "rounded" step size "step" and a
   * "rounded" starting point "start" in [a,b], so that start+k*step
   * for k = 0,1,... gives a reasonable subdivision of [a,b].
   * However, a and b are not changed.
   */   
   public float[] subDivide()
   {
      float s_diff = 0;
      int   i_power = 0;
      float start = 0;
      float step = 0;
      float[] values = new float[3];
  
      s_diff = xmax - xmin;
      
      //System.out.println("Diff = " + s_diff );
  /* Now express the length of the interval in the form  s_diff * 10^ipower
     where s_diff is in the interval [1., 10.) */
      i_power = 0;
      while ( s_diff >= 10.0 )
      {
	 s_diff = s_diff / 10.0f;
	 i_power = i_power + 1;
      }
      while ( s_diff < 1.0 )
      {
	 s_diff = s_diff * 10.0f;
	 i_power = i_power - 1;
      }
      
      baseE = i_power - 1;
  /* Now choose step size to give a reasonable number of subdivisions
     over an interval of length b-a. */

      if ( s_diff <= 1.2 )
	 step = .1f * (float)Math.pow(10.0, i_power );
      else if ( s_diff <= 2.0 )
	 step = .2f * (float)Math.pow( 10.0, i_power );
      else if ( s_diff <= 2.5 )
	 step = .25f * (float)Math.pow( 10.0, i_power );
      else if ( s_diff <= 5.0 )
	 step = .5f * (float)Math.pow( 10.0, i_power );
      else
	 step = 1.0f * (float)Math.pow( 10.0, i_power );

  /* Now find the first grid point in the specified interval. */

      start = xmin;
      if ( start >= 0.0 )
	 start = start - ((start%step ) - step );
      else
      {
	 start = -start;
	 start = start - (start%step);
	 start = -start;
      }
      
   // return the number of steps
      float sum = start; 
      int numstep = 0;     
      while( sum <= xmax )
      {
         sum = sum + step;
	 numstep++;
      }
      /*
      System.out.println("Step = " + step );
      System.out.println("Degree = " + i_power );
      System.out.println("Start = " + start );
      System.out.println("NumStep = " + numstep );
      */   
      values[0] = step;
      values[1] = start;
      values[2] = numstep;
      
      return values;
   } /* Subdivide */
   
      
   public String standardize( float num )
   {
      
      if( Math.abs(num) < 10000 && Math.abs(num) >= .001 
          && format == Format.AUTO )         	  
         return Format.choiceFormat( (double)num, format );    
      if( format == Format.DECIMAL )
         return Format.choiceFormat( (double)num, format ); 
      
      if( format == Format.ENGINEER || format == Format.AUTO )
      {
         baseE = (int)(baseE/3);
	 baseE = baseE*3;
      } 
      
      return Format.setE( (double)num, baseE, sig_dig);
   }
   
   public String getFormat()
   {
      if( format == Format.AUTO )
         return "Automatic (Decimal or Engineering) Format";
      if( format == Format.DECIMAL )
         return "Decimal Format";
      if( format == Format.SCIENTIFIC )
         return "Scientific Format";
      return "Engineering Format";
   }
   
   public static void main( String argv[] )
   {
      CalibrationUtil testcal = new CalibrationUtil( 0, 10 );
      
      float[] testvalue = testcal.subDivide();
      System.out.println("TestStep = " + testvalue[0] );
      System.out.println("TestStart = " + testvalue[1] );  
      
      testcal = new CalibrationUtil( 60000f, 432000f );
      
      testvalue = testcal.subDivide();
      
      System.out.println("Standardize 4007000: " + 
                           testcal.standardize( 4007000 ) );
      System.out.println("Standardize 400700: " + 
                           testcal.standardize( 400700 ) );
      System.out.println("Standardize 40070: " + 
                           testcal.standardize( 40070 ) );
      
      System.out.println("TestStep = " + testvalue[0] );
      System.out.println("TestStart = " + testvalue[1] );  
      
      testcal = new CalibrationUtil( .001f, .1f );
      
      testvalue = testcal.subDivide();
      
      System.out.println("TestStep = " + testvalue[0] );
      System.out.println("TestStart = " + testvalue[1] );  
      
      System.out.println("Format = " + testcal.getFormat() );          		 
   }
}
   
   
   
