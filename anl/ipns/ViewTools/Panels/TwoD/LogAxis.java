/*
 * File: LogAxis.java 
 *
 * Copyright (C) 2008, Dennis Mikkelson
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
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package gov.anl.ipns.ViewTools.Panels.TwoD;


import java.awt.*;
import javax.swing.*;

import gov.anl.ipns.ViewTools.Components.Transparency.CalibrationUtil;

/**
 * This class is a Drawable that draws a calibrated logarithmic axis with the
 * specified points marked.
 * 
 * @author Dennis Mikkelson
 *
 */
public class LogAxis extends Drawable
{
  private int     width,
                  height;
  private double  min,
                  max;
  private double  log_min,
                  log_max;
  private float   real_height = 1;  // We'll work on a virtual rectangle
                                    // [min,max] X [0,1] to draw the axis.
  private float[] points;

  private TextDrawable[] labels;
  
  /**
   * Construct a log axis of the specified dimensions, with
   * the specified calibrations.
   * 
   * @param width   The width of the axis in pixels 
   * @param height  The height of the axis in pixels 
   * @param min     The real number associated with the
   *                left hand end point of the axis
   * @param max     The real number associated with the
   *                right hand end point of the axis
   * @param points  The points to mark along the axis
   */
  public LogAxis( int     width, 
                  int     height, 
                  float   min, 
                  float   max, 
                  float[] points )
  {
    this.width   = width;
    this.height  = height;
    this.min     = min;
    this.max     = max;
    this.log_min = Math.log10( min );
    this.log_max = Math.log10( max );
    this.points  = points;
    
    Font font = new Font("SansSerif", Font.PLAIN, 9 );

    labels = new TextDrawable[points.length];
    for ( int i = 0; i < points.length; i++ )
    {
       String text = Format( points[i] );
       TextDrawable label = new TextDrawable( text.trim() );

       if ( i == 0 )
         label.setAlignment( TextDrawable.Horizontal.LEFT, 
                             TextDrawable.Vertical.TOP  );
       else if ( i == points.length - 1 )
         label.setAlignment( TextDrawable.Horizontal.RIGHT, 
                             TextDrawable.Vertical.TOP  );
       else
         label.setAlignment( TextDrawable.Horizontal.CENTER, 
                             TextDrawable.Vertical.TOP  );

       Point position = WorldToPixel( points[i], 0.45 );
       label.setPosition( position );
       label.setFont( font );
       labels[i] = label;
    }
  }


  /**
   *  Attempt to format the point in a "nice" compact String form, without
   *  excessive trailing zeros.
   *
   *  @param  point   The value to format
   *  @return A String form of the value.
   */
  private String Format( float point )
  {
    String text;

    if ( point < 0.00001f )
      text = String.format("%4.1E", point );
    else if ( point < 0.0001f )
      text = String.format("%4.5f", point );
    else if ( point < 0.001f )
      text = String.format("%4.4f", point );
    else if ( point < 0.01f )
      text = String.format("%4.3f", point );
    else if ( point < 0.1f )
      text = String.format("%4.2f", point );
    else if ( point < 1 )
      text = String.format("%4.1f", point );
    else if ( point < 1000000 )
      text = String.format("%2.0f", point );
    else
      text = String.format("%4.0E", point );

    return text;
  }
  
  
  /**
   * Draw this axis using the specified graphics context, 
   * with any specified color, position and rotation angle.
   * 
   * @param graphics  The graphics context for drawing this axis.
   */
  public void draw(Graphics2D graphics)
  {
    graphics = setAttributes( graphics );     // Use super class method 
                                              // to set up the color, etc. and
                                              // get a new graphics context
                                              // with those attributes set.

    Point left = WorldToPixel( min, 0.99f );
    Point right = WorldToPixel( max, 0.99f );
    graphics.drawLine( left.x, left.y, right.x, right.y ); 
    for ( int i = 0; i < points.length; i++ )
    {
      Point top    = WorldToPixel( points[i], 0.99 );
      Point bottom;
      if ( isPowerOfTen( points[i] ) ) 
        bottom = WorldToPixel( points[i], 0.70 );
      else
        bottom = WorldToPixel( points[i], 0.85 );

      graphics.drawLine( top.x, top.y, bottom.x, bottom.y );
    }

    for ( int i = 0; i < labels.length; i++ )
      if ( i % 3 == 0 )
        labels[i].draw( graphics );

    graphics.dispose();                       // get rid of the new 
                                              // graphics context 
  }

 
  /**
   * Check whether the specified value is a power of ten, by checking
   * whether its log is an integer, to within a tolerance of 0.00001.
   *
   * @param  x  The number to check.
   *
   * @return true if the parameter is essentially a power of ten.
   */
  private boolean isPowerOfTen( double x )
  {
    double exponent = Math.log10( x );
    if ( Math.abs((int)exponent - exponent) < 0.00001 )
      return true;
    else
      return false;
  }


  /**
   *  Calculate the point in pixel coordinates that corresponds to the
   *  specified x, y in "log world coordinates".
   *
   *  @param  x   The horizontal position along the line from the min to max 
   *              scale value.
   *  @param  y   The vertical position between 0 and 1.
   *
   *  @param the pixel point in the panel corresponding to the specified (x,y).
   */
  private Point WorldToPixel( double x, double y )
  {
    double log_x = Math.log10( x );
    int pix_x = (int)(( log_x - log_min ) * (width-1) / ( log_max - log_min ));
    int pix_y = (int)( y * (height-1) );
    return new Point( pix_x, pix_y );
  }


  /**
   *  Basic functionality test.
   */
  public static void main( String args[] )
  {                              
    int WIDTH  = 500;
    int HEIGHT = 50; 
    JFrame frame = new JFrame("Axis Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(WIDTH,HEIGHT));
                                                  // make a TwoD_JPanel to use
    TwoD_JPanel panel = new TwoD_JPanel();        // for displaying IDrawables
    frame.add( panel );
    frame.setVisible( true );
    
    int     width  = frame.getContentPane().getWidth();
    int     height = frame.getContentPane().getHeight();
 
/*
    float[] points = {1, 3, 10, 30, 100, 300, 1000, 3000, 10000};

    for ( int i = 0; i < points.length; i++ )
      points[i] *= 100;

    float   min = points[0];
    float   max = points[points.length-1]; 
*/
    float   min = 1f;
    float   max = 1.1f; 

    CalibrationUtil calib = new CalibrationUtil( min, max );
    float[] points = calib.subDivideLog();
    for ( int i = 0; i < points.length; i++ )
      System.out.printf( "%3d  %4.5f\n", i, points[i] );

    LogAxis axis = new LogAxis( width, height, min, max, points );

    panel.AddObject( axis );
    panel.draw();
  }

}
