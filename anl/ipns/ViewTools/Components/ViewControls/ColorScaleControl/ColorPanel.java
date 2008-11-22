/* 
 * File: ColorPanel.java
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

package gov.anl.ipns.ViewTools.Components.ViewControls.ColorScaleControl;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.Panels.TwoD.*;
import gov.anl.ipns.ViewTools.Components.Transparency.*;


/**
 *  This class provides a color scale display using an ImageJPanel2 class.
 */
public class ColorPanel extends JPanel
{
  private final int NUM_VALUES = 1000;

  private ImageJPanel2   image_panel;
  private JPanel         scale_panel;
  private float          range_min;
  private float          range_max;

  /**
   *  Construct a color panel with values in the default data range, [0,1].
   */
  public ColorPanel()
  {
    image_panel = new ImageJPanel2();
    scale_panel = new JPanel();

//    scale_panel.setMaximumSize( new Dimension(1000,30) );
    scale_panel.setPreferredSize( new Dimension(100,30) );
    scale_panel.setLayout( new GridLayout(1,1) );
    
    setLayout( new BorderLayout() );
    add( image_panel, BorderLayout.CENTER );
    add( scale_panel, BorderLayout.SOUTH );

    addComponentListener( new RedrawListener() );
    setDataRange( 0, 1, false );
  }


  /**
   * Set the data range for the displayed color panel to the range of
   * values from min to max.  Both min and max should be at least 0, and
   * min should be less the max.
   *
   * @param min  The minimum data value in the color panel array.
   * @param max  The maximum data value in the color panel array.
   * @param rebuild_now  If true, the image will be redraw immediately.  Set
   *                     false if several other operations are to be done
   *                     before redrawing the image.
   */
  public void setDataRange( float min, float max, boolean rebuild_now )
  {
    System.out.println("setDataRange called for min, max = " + min + ", " +max);

    if ( max < min )
    {
      System.out.println("Swapping max & min");
      float temp = min;
      min = max;
      max = temp;
    }

    range_min = min;
    range_max = max;

    CalibrationUtil calib = new CalibrationUtil( min, max );
    float[] info = calib.subDivide();
    float first = info[1];
    float step  = info[0];
    int   n_steps = (int) info[2];
    float[] points = new float[n_steps];
    for ( int i = 0; i < n_steps; i++ )
      points[i] = first + i * step;

    System.out.println("\nLinear calibrations: ");
    for ( int i = 0; i < points.length; i++ )
      System.out.printf( "%3d  %4.2f\n", i, points[i] );

/*
    System.out.println("\nLog calibrations: ");
    points = calib.subDivideLog();
    for ( int i = 0; i < points.length; i++ )
      System.out.printf( "%3d  %4.2f\n", i, points[i] );
*/
    scale_panel.setVisible( false );
    scale_panel.removeAll();

    int width = scale_panel.getWidth();
    int height = scale_panel.getHeight();

    System.out.println("Current Size = " + width + ", " + height );

    TwoD_JPanel panel = new TwoD_JPanel();
    scale_panel.add( panel );
    panel.AddObject( new LinearAxis( width, height, min, max, points ) );
    panel.draw();
    scale_panel.setVisible( true );

    float[][] values = new float[1][NUM_VALUES];
    for ( int i = 0; i < NUM_VALUES; i++ )
      values[0][i] =  i * ( max - min ) / (NUM_VALUES - 1) + min;

    VirtualArray2D virtual_array = new VirtualArray2D( values );
    image_panel.setData( virtual_array, rebuild_now );
    image_panel.setDataRange( min, max );
  }


  /**
   * Set the color model and number of colors to use for the color scale.
   * Supported color scales are listed in the IndexColorMaker class.
   *
   * @param name         The name of the color scale, such as "Spectrum".
   * @param num_colors   The number of colors to be used in the color scale.
   *                     Currently, this must be at least 16.
   * @param rebuild_now  If true, the image will be redraw immediately.  Set
   *                     false if several other operations are to be done
   *                     before redrawing the image.
   */
  public void setColorModel( String name, int num_colors, boolean rebuild_now )
  {
    boolean twosided = false;
    image_panel.setNamedColorModel( name, twosided, num_colors, rebuild_now );
  }


  /**
   * Set the color index table to use for mapping data values to color
   * indices. 
   *
   * @param table        Table with color index entries, from zero to one less
   *                     than the number of colors used.
   * @param min          The minimum data value for the color table.
   * @param max          The maximum data value for the color table.
   * @param rebuild_now  If true, the image will be redraw immediately.  Set
   *                     false if several other operations are to be done
   *                     before redrawing the image.
   */
  public void setColorTable( byte[]  table,
                             float   min, 
                             float   max, 
                             boolean rebuild_now )
  {
    System.out.println("setColorTable called with " + table.length );
    System.out.println("min = " + min + "  max = " + max );
    image_panel.setDataRange( min, max );
    image_panel.changeColorIndexTable( table, rebuild_now );
  }


  /**
   *  This class will update the axis whenever the component is resized or
   *  shown.
   */
  public class RedrawListener extends ComponentAdapter
  {
    public void componentResized(ComponentEvent e) 
    {
      setDataRange( range_min, range_max, true );
    }
    public void componentShown(ComponentEvent e) 
    {
      setDataRange( range_min, range_max, true );
    }
  };
  

  /**
   *  The "main program" provides a simple example of the use of this class
   *  and a basic test of its functionality.
   */
  public static void main( String args[] )
  {
                                // make a color index table, containing byte
                                // values between 0 and NUM_COLORS - 1.
    int   TABLE_SIZE = 60000;
    byte  NUM_COLORS = 100;

    float bottom = 0.2f;        // bottom value, normalized to [0.1];
    float top    = 0.7f;        // top    value, normalized to [0.1];

    int bottom_index = (int)(bottom * TABLE_SIZE);
    int top_index    = (int)(top    * TABLE_SIZE);

    float min = 0;
    float max = 100;

    byte[] table = new byte[TABLE_SIZE];
/*
    for ( int i = bottom_index; i < TABLE_SIZE; i++ )
      if ( i < top_index )
        table[i] = (byte)( (     i     - bottom_index ) * NUM_COLORS / 
                           ( top_index - bottom_index ) ); 
      else
        table[i] = (byte)(NUM_COLORS - 1);
*/

    for ( int i = 0; i < TABLE_SIZE; i++ )
    {
      float x = i * max / (TABLE_SIZE-1);
      if ( x > 1 )
        x = (float)Math.log10(x);
      else
        x = 0;

      table[i] = (byte)((NUM_COLORS-1) * x/Math.log10(max));
    }

                                 // now make the color panel and set the color
                                 // index table, and the color model and 
                                 // number of colors.
                                
    ColorPanel color_panel = new ColorPanel();
    float min_data =   0;
    float max_data = 345;
    color_panel.setDataRange( min_data, max_data, false );
    color_panel.setColorTable( table, min_data, max_data, false );
    color_panel.setColorModel( IndexColorMaker.RAINBOW_SCALE, NUM_COLORS, true);

                                 // put the panel in a frame for testing
                                 // purposes

    JFrame frame = new JFrame( "ColorPanel Test" );
    frame.add( color_panel );
    frame.setSize( 300, 60 );
    frame.setVisible( true );
  }

}
