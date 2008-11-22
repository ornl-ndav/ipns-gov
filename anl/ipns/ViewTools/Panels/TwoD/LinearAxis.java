/*
 * File: LinearAxis.java 
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

/**
 * This class is a Drawable that draws a calibrated axis with the
 * specified points marked.
 * 
 * @author Dennis Mikkelson
 *
 */
public class LinearAxis extends Drawable
{
  private int     width,
                  height;
  private float   min,
                  max;
  private float   real_height = 1;  // We'll work on a virtual rectangle
                                    // [min,max] X [0,1] to draw the axis.
  private float[] points;

  private TextDrawable[] labels;
  
  /**
   * Construct an axis of the specified dimensions, with
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
  public LinearAxis( int     width, 
                     int     height, 
                     float   min, 
                     float   max, 
                     float[] points )
  {
    this.width  = width;
    this.height = height;
    this.min    = min;
    this.max    = max;
    this.points = points;
    
    Font font = new Font("SansSerif", Font.PLAIN, 9 );

    labels = new TextDrawable[points.length];
    for ( int i = 0; i < points.length; i++ )
    {
       String text = String.format("%4.0f", points[i] );
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

       Point position = WorldToPixel( points[i], 0.5f );
       label.setPosition( position );
       label.setFont( font );
       labels[i] = label;
    }
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
      Point top    = WorldToPixel( points[i], 0.99f );
      Point bottom;
      if ( i % 2 == 0 )
        bottom = WorldToPixel( points[i], 0.75f );
      else
        bottom = WorldToPixel( points[i], 0.85f );

      graphics.drawLine( top.x, top.y, bottom.x, bottom.y );
    }

    for ( int i = 0; i < labels.length; i++ )
      if ( i % 2 == 0 )
        labels[i].draw( graphics );

    graphics.dispose();                       // get rid of the new 
                                              // graphics context 
  }

  private Point WorldToPixel( float x, float y )
  {
    int pix_x = (int)( ( x - min ) * (width-1) / ( max - min ) );
    int pix_y = (int)( y * (height-1) );
    return new Point( pix_x, pix_y );
  }

  public static void main( String args[] )
  {                              
    int WIDTH  = 300;
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
    float   min = -10;
    float   max = 110;
    float[] points = {0, 25, 50, 75, 100};
    LinearAxis axis = new LinearAxis( width, height, min, max, points );

    panel.AddObject( axis );
    panel.draw();
  }

}
