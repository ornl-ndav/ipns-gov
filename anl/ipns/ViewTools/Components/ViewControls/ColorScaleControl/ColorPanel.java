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
import gov.anl.ipns.ViewTools.Panels.Image.ImageJPanel2;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.Panels.TwoD.*;
import gov.anl.ipns.ViewTools.Components.Transparency.*;
import gov.anl.ipns.Util.Numeric.*;


/**
 *  This class provides a color scale display using an ImageJPanel2 class.
 */
public class ColorPanel extends JPanel
{
  private final int NUM_VALUES = 500;

  private ImageJPanel2   image_panel;
  private JPanel         scale_panel;
  private double         range_min; 
  private double         range_max;
  private boolean        is_log_scale = false;

  /**
   *  Construct a color panel with values in the default data range, [0,1].
   */
  public ColorPanel()
  {
    image_panel = new ImageJPanel2();
    scale_panel = new JPanel();

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
   * values from min to max.  The min value should be less the max value.
   *
   * @param min  The minimum data value in the color panel array.
   * @param max  The maximum data value in the color panel array.
   * @param rebuild_now  If true, the image will be redraw immediately.  Set
   *                     false if several other operations are to be done
   *                     before redrawing the image.
   */
  public void setDataRange( double min, double max, boolean rebuild_now )
  {
//  System.out.println("setDataRange called for min, max = " + min + ", " +max);

    if ( max < min )
    {
      double temp = min;
      min = max;
      max = temp;
    }

    range_min = min;
    range_max = max;

    float[][] values = new float[1][NUM_VALUES];
    if ( is_log_scale )                  // make exponential image values
    {
      double ratio = Math.pow( max/min, 1.0/(NUM_VALUES-1) );
      double val = min;
      for ( int i = 0; i < NUM_VALUES; i++ )
      {
        values[0][i] = (float)val;
        val *= ratio;
      }
    }
    else                                  // make linear array of image values
    {
      for ( int i = 0; i < NUM_VALUES; i++ )
        values[0][i] = (float)( i * ( max - min ) / (NUM_VALUES - 1) + min );
    }

    VirtualArray2D virtual_array = new VirtualArray2D( values );
    image_panel.setData( virtual_array, rebuild_now );
    image_panel.setDataRange( (float)min, (float)max );
/*
    System.out.println("Set data range to " + min + ", " + max );
    System.out.println("Values from " + values[0][0] + 
                       ", " + values[0][NUM_VALUES-1] );
*/
    resetCalibrations( min, max, is_log_scale );
  }


  /**
   *  Rebuild the calibrated axis across the bottom of the
   *  image display, if the panel is resized, or if the 
   *  min and/or max values are changed.
   *
   *  @param min           The minimum value on the color axis.
   *  @param max           The maximumn value on the color axis.
   *  @param is_log_scale  Flag indicating whether to make a log
   *                       or linear axis.
   */
  private void resetCalibrations(double min, double max, boolean is_log_scale)
  {
    CalibrationUtil calib = new CalibrationUtil( (float)min, (float)max );

    double[] points;

    if ( is_log_scale )
      points = Subdivide.subdivideLog( min, max );
    else
      points = Subdivide.subdivideLinear( min, max );

    scale_panel.setVisible( false );
    scale_panel.removeAll();

    int width = scale_panel.getWidth();
    int height = scale_panel.getHeight();

    TwoD_JPanel panel = new TwoD_JPanel();
    scale_panel.add( panel );

    if ( is_log_scale )
      panel.AddObject( new LogAxis( 0, 0, width, height, min, max, points ) );
    else
      panel.AddObject( new LinearAxis(0, 0, width, height, min, max, points) );

    panel.draw();
    scale_panel.setVisible( true );
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
   * @param table_min    The minimum data value for the color table.
   * @param table_max    The maximum data value for the color table.
   * @param rebuild_now  If true, the image will be redraw immediately.  Set
   *                     false if several other operations are to be done
   *                     before redrawing the image.
   */
  public void setColorTable( byte[]  table,
                             double  table_min, 
                             double  table_max, 
                             boolean is_log,
                             boolean rebuild_now )
  {
/*
    System.out.println("setColorTable called with " + table.length );
    System.out.println("min = " + table_min + "  max = " + table_max );
*/
    is_log_scale = is_log; 

    image_panel.changeColorIndexTable( table, 
                                       is_log,
                                       (float)table_min, 
                                       (float)table_max, 
                                       rebuild_now );
  }


  /**
   *  This class will update the axis whenever the component is resized or
   *  shown.
   */
  public class RedrawListener extends ComponentAdapter
  {
    public void componentResized(ComponentEvent e) 
    {
      resetCalibrations( range_min, range_max, is_log_scale );
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
    int   TABLE_SIZE = 600;
    byte  NUM_COLORS = 100;

    double bottom = 0.2;        // bottom value, normalized to [0.1];
    double top    = 0.7;        // top    value, normalized to [0.1];

    int bottom_index = (int)(bottom * TABLE_SIZE);
    int top_index    = (int)(top    * TABLE_SIZE);

    double min = 0;
    double max = 100;

    byte[] table = new byte[TABLE_SIZE];

    for ( int i = bottom_index; i < TABLE_SIZE; i++ )
      if ( i < top_index )
        table[i] = (byte)( (     i     - bottom_index ) * NUM_COLORS / 
                           ( top_index - bottom_index ) ); 
      else
        table[i] = (byte)(NUM_COLORS - 1);

                                 // now make the color panel and set the color
                                 // index table, and the color model and 
                                 // number of colors.
                                
    ColorPanel color_panel = new ColorPanel();
    double  min_data = 123;
    double  max_data = 456;
    boolean isLog    = false;
    color_panel.setColorTable( table, min_data, max_data, isLog, false );
    color_panel.setDataRange( min_data, max_data, false );
    color_panel.setColorModel( IndexColorMaker.RAINBOW_SCALE, NUM_COLORS, true);

                                 // put the panel in a frame for testing
                                 // purposes

    JFrame frame = new JFrame( "ColorPanel Test" );
    frame.add( color_panel );
    frame.setSize( 300, 80 );
    frame.setVisible( true );
  }

}
