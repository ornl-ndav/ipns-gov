/*
 * File: AxisOverlay2D.java
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 */
 
//******************************Mental Reminders*******************************
// Y axis doesn't display end ranges
// ImageJPanel currently draws over the overlay.
// X axis has a constant position 50 for it's label
//*****************************************************************************

package DataSetTools.components.View.Transparency;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.util.*; 
import DataSetTools.components.image.*; //ImageJPanel & CoordJPanel
import DataSetTools.components.View.*;
import DataSetTools.components.View.TwoD.*;
import DataSetTools.util.*; 
import java.lang.Math;

public class AxisOverlay2D extends OverlayJPanel 
{
   // these variables simulate the interval of values of the data
   private float xmin;
   private float xmax;
   private float ymin;
   private float ymax;
   private IAxisAddible2D component;
   private int precision;
   private Font f;
   
   public AxisOverlay2D(IAxisAddible2D iaa)
   {
      super();
      component = iaa; 
      precision = iaa.getPrecision();
      f = iaa.getFont();
      xmin = 0;
      xmax = 1;
      ymin = 0;
      ymax = 1;       
   }
   
   public void setPrecision( int digits )
   {
      precision = digits;
   }  
  
  /*
   * This method creates tick marks and numbers for this transparency.
   * These graphics will overlay onto an imagejpanel.
   */  
   public void paint(Graphics g) 
   {  
      Graphics2D g2d = (Graphics2D)g; 
           
      g2d.setFont(f);
      FontMetrics fontdata = g2d.getFontMetrics();
      System.out.println("Precision = " + precision);
      
      xmin = component.getAxisInfo(true).getMin();
      xmax = component.getAxisInfo(true).getMax();
      // ymin & ymax swapped to adjust for axis standard
      ymax = component.getAxisInfo(false).getMin();
      ymin = component.getAxisInfo(false).getMax();
      System.out.println("Ymin/Ymax " + ymin + "/" + ymax );
      // get the dimension of the center panel (imagejpanel)
      // all of these values are returned as floats, losing precision!!!
      int xaxis = (int)( component.getRegionInfo().getWidth() );
      int yaxis = (int)( component.getRegionInfo().getHeight() );
      // x and y coordinate for upper left hand corner of component
      int xstart = (int)( component.getRegionInfo().getLocation().getX() );
      int ystart = (int)( component.getRegionInfo().getLocation().getY() );
           
      System.out.println("X,Y axis = " + xaxis + ", " + yaxis );
      System.out.println("X,Y start = " +  xstart + ", " + ystart );  
      
      // draw title on the overlay if one exists
      if( component.getTitle() != IVirtualArray2D.NO_TITLE )
         g2d.drawString( component.getTitle(), xstart + xaxis/2 -
                      fontdata.stringWidth(component.getTitle())/2, 
     	              ystart - fontdata.getHeight() );
          
      // info for putting tick marks and numbers on transparency       	      
      String num = "";
      int xtick_length = 5;
     /*
      * Draw the x axis with horizontal numbers and adjusting ticks
      */
      //xmin = -83;
      //xmax = 139;
      CalibrationUtil util = new CalibrationUtil( xmin, xmax, precision, 
                                                  Format.ENGINEER );
      float[] values = util.subDivide();
      float step = values[0];
      float start = values[1];    // the power of the step
      int numxsteps = (int)values[2];    
      	 
      int pixel = 0;
      int subpixel = 0;
      /* xaxis represents Pmax - Pmin
      float Pmin = start;
      float Pmax = start + xaxis;
      float Amin = xmin;
      */
      float A = 0;	
      int exp_index = 0;
      
      for( int steps = 0; steps < numxsteps; steps++ )
      {  
         A = (float)steps*step + start;		 
     	 pixel = (int)( 
     		 (float)xaxis*(A - xmin)/
     		 (xmax-xmin) + xstart);    	 
     	 //System.out.println("Pixel " + pixel );
	 //System.out.println("Xmin/Xmax " + xmin + "/" + xmax ); 	 
       	 subpixel = (int)( 
     		 ( (float)xaxis*(A - xmin - step/2 ) )/
     		 (xmax-xmin) + xstart);      
 
	 num = util.standardize( (step * (float)steps + start) );
	 exp_index = num.indexOf('E');	 
	 
	 g2d.drawString( num.substring(0,exp_index), 
	            pixel - fontdata.stringWidth(num.substring(0,exp_index))/2, 
     	            yaxis + ystart + xtick_length + fontdata.getHeight() );	      

         //System.out.println("Subpixel/XStart " + subpixel + "/" + xstart );
     	 if( subpixel > xstart && subpixel < (xstart + xaxis) )
     	 {
     	    g2d.drawLine( subpixel, yaxis + ystart, 
     		       subpixel, yaxis + ystart + xtick_length-2 );
     	 }
	 
     	 if( steps == (numxsteps - 1) && 
     	     ( xaxis + xstart - pixel) > xaxis/(2*numxsteps) )
     	 { 
	       g2d.drawLine( pixel + (pixel - subpixel), yaxis + ystart, 
     			     pixel + (pixel - subpixel), 
     			     yaxis + ystart + xtick_length-2 );
     	 }
     	 g2d.drawLine( pixel, yaxis + ystart, 
     		       pixel, yaxis + ystart + xtick_length );  		    
      }
     /*
      * This will display the x label, x units, and common exponent (if not 0).
      */
      String xlabel = "";
      if( component.getAxisInfo(true).getLabel() != IVirtualArray2D.NO_XLABEL )
         xlabel = xlabel + component.getAxisInfo(true).getLabel();
      if( component.getAxisInfo(true).getUnits() != IVirtualArray2D.NO_XUNITS )
         xlabel = xlabel + "  " + component.getAxisInfo(true).getUnits();
      if( Integer.parseInt( num.substring( exp_index + 1) ) != 0 )
         xlabel = xlabel + "  " + num.substring( exp_index );
      if( xlabel != "" )
         g2d.drawString( xlabel, xstart + xaxis/2 -
                      fontdata.stringWidth(xlabel)/2, 
     	              yaxis + ystart + 50 - fontdata.getHeight()/2 );
      
     /*
      * Draw y axis with horizontal numbers and adjusting ticks
      */
      CalibrationUtil yutil = new CalibrationUtil( ymin, ymax, precision, 
                                                   Format.ENGINEER );
      values = yutil.subDivide();
      float ystep = values[0];
      float starty = values[1];    // the power of the interval
      int numysteps = (int)values[2];

      //System.out.println("NumYSteps = " + numysteps);
      int ytick_length = 5;
      // draw ticks for the y-axis, starting from origin, 
      // and backtracking up
      int ypixel = 0;
      int ysubpixel = 0;
     		    
      float pmin = ystart + yaxis;
      float pmax = ystart;
      float a = 0;
      float amin = ymin - starty;
      // given ysteps in world coordinates, put ticks on y axis starting
      // from top and moving down to the origin
      for( int ysteps = numysteps - 1; ysteps >= 0; ysteps-- )
      {   
     	 a = ysteps * ystep;
     	 
     	 ypixel = (int)( (pmax - pmin) * ( a - amin) /
     			 (ymax - ymin) + pmin);
     	 //System.out.println("YPixel " + ypixel ); 
	 
	 //System.out.println("Ymin/Ymax " + ymin + "/" + ymax );
     	 
     	 ysubpixel = (int)( (pmax - pmin) * ( a - amin  + ystep/2 ) /
     			 (ymax - ymin) + pmin); 
     
	 num = yutil.standardize(ystep * (float)ysteps + starty);
	 exp_index = num.indexOf('E');
	 
	 System.out.println("Ypixel/Pmin = " + ypixel + "/" + pmin );
	 System.out.println("Ypixel/Pmax = " + ypixel + "/" + pmax );
	 System.out.println("Num = " + num );
	 // if pixel is between top and bottom of imagejpanel, draw it 		     	       
     	 if( ypixel <= pmin && ypixel >= pmax )
     	 {
	    g2d.drawString( num.substring(0,exp_index), 
	           xstart - ytick_length - 
		      fontdata.stringWidth(num.substring(0,exp_index)),
	           ypixel + fontdata.getHeight()/4 );	      

     	    g2d.drawLine( xstart - ytick_length, ypixel - 1, 
     	                  xstart - 1, ypixel - 1 );   
     	 }
	 // if subpixel is between top and bottom of imagejpanel, draw it 		     	       
     	 if( ysubpixel < pmin && ysubpixel > pmax )
     	 {
     	    g2d.drawLine( xstart - (ytick_length - 2), ysubpixel - 1, 
     			  xstart - 1, ysubpixel - 1 );
     	 }
	 // if a tick mark should be drawn at the end, draw it
	 // since the above "if" takes care of all subtick marks before the
	 // actual numbered ticks, there may be a tick mark needed after the 
	 // last tick. 
     	 if( ysteps == 0 && 
     	     (pmin - ypixel) > yaxis/(2*numysteps) ) 
     	 {
     	    g2d.drawLine( xstart - (ytick_length - 2), 
     	          (int)(ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin) ) ), 
		  xstart - 1, 
     		  (int)( ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin)))); 		
     	 }
      }
     /*
      * This will display the y label, y units, and common exponent (if not 0).
      */
      String ylabel = "";
      if( component.getAxisInfo(false).getLabel() != IVirtualArray2D.NO_YLABEL )
         ylabel = ylabel + component.getAxisInfo(false).getLabel();
      if( component.getAxisInfo(false).getUnits() != IVirtualArray2D.NO_YUNITS )
         ylabel = ylabel + "  " + component.getAxisInfo(false).getUnits();
      if( Integer.parseInt( num.substring( exp_index + 1) ) != 0 )
         ylabel = ylabel + "  " + num.substring( exp_index );
      if( ylabel != "" )
      {
         g2d.rotate( -Math.PI/2, xstart, ystart + yaxis );	 
         g2d.drawString( ylabel, xstart + yaxis/2 -
                      fontdata.stringWidth(ylabel)/2, 
     	              yaxis + ystart - xstart + fontdata.getHeight() );
         g2d.rotate( Math.PI/2, xstart, ystart + yaxis );
      }	
   } // end of paint()
}
