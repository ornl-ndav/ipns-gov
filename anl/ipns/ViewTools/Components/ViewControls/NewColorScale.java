/*
 * File: NewColorScale.java
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
import gov.anl.ipns.Util.Numeric.*;
import gov.anl.ipns.ViewTools.Components.ViewControls.ColorScaleControl.*;
import gov.anl.ipns.ViewTools.Panels.TwoD.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Panels.Image.*;
 
/**
 */
public class NewColorScale extends ViewControl
{
  private int NUM_VALUES = 256;

  private ColorScaleInfo info; 
  private ImageJPanel2   image_panel;
  private JPanel         scale_panel;


  public NewColorScale( String title, ColorScaleInfo info )
  {
    super( title );
    this.info = info;

    build_panel();
  }

  public void setControlValue(Object value)
  {
    if ( !(value instanceof ColorScaleInfo) )
      throw new IllegalArgumentException("Need ColorScaleInfo parameter");

    this.info = (ColorScaleInfo)value;
    build_panel();
  }
  
  public Object getControlValue()
  {
    return info;
  }
  
  public ViewControl copy()
  {
    return new NewColorScale( getTitle(), info );
  }


  private void build_panel()
  {
    System.out.println( "IN build_panel()" );

    image_panel = new ImageJPanel2();
    scale_panel = new JPanel();
    scale_panel.setPreferredSize( new Dimension(100,30) );
    scale_panel.setLayout( new GridLayout(1,1) );

    setLayout( new BorderLayout() );
    add( image_panel, BorderLayout.CENTER );
    add( scale_panel, BorderLayout.SOUTH );
    addComponentListener( new RedrawListener() );

    VirtualArray2D va2D = buildImageArray( NUM_VALUES );
    image_panel.setData( va2D, true );
  }

  private void resetCalibrations()
  {
    float min      = info.getTableMin();
    float max      = info.getTableMax();
    boolean is_log = info.isLog();

    scale_panel.setVisible( false );
    scale_panel.removeAll();

    int width  = scale_panel.getWidth();
    int height = scale_panel.getHeight();

    System.out.println( "IN resetCalibrations()" );
    System.out.println("width, height = " + width + ", " + height );
    System.out.println("min, max = " + min + ", " + max );

    TwoD_JPanel panel = new TwoD_JPanel();
    scale_panel.add( panel );

    IDrawable axis;
    if ( is_log )
    {
      double[] points = Subdivide.subdivideLog( min, max );
      panel.AddObject( new LogAxis( 0, 0, width, height, min, max, points ) );
    }
    else
    {
      double[] points = Subdivide.subdivideLinear( min, max );
      panel.AddObject( new LinearAxis(0, 0, width, height, min, max, points) );
    }

    panel.draw();
    scale_panel.setVisible( true );
  }


  private VirtualArray2D buildImageArray( int num_values )
  {
    boolean is_log = info.isLog();
    double  min    = info.getTableMin();
    double  max    = info.getTableMax();

    float[][] values = new float[1][num_values];
    if ( is_log )                          // make exponential image values
    {
      double ratio = Math.pow( max/min, 1.0/(num_values-1) );
      double val = min;
      for ( int i = 0; i < num_values; i++ )
      {
        values[0][i] = (float)val;
        val *= ratio;
      }
    }
    else                                  // make linear array of image values
    {
      for ( int i = 0; i < num_values; i++ )
        values[0][i] = (float)( i * ( max - min ) / (num_values - 1) + min );
    }

    VirtualArray2D va2D;
    if ( info.getOrientation() == ColorScaleInfo.Orientation.HORIZONTAL )
      va2D = new VirtualArray2D( values );
    else
    {
      float[][] transpose = new float[num_values][1];
      for ( int i = 0; i < num_values; i++ )
        transpose[i][0] = values[0][i];
      va2D = new VirtualArray2D( transpose );
    }
 
    return va2D;
  }


  /**
   *  This class will update the axis whenever the component is resized or
   *  shown.
   */
  public class RedrawListener extends ComponentAdapter
  {
    public void componentResized(ComponentEvent e)
    {
      resetCalibrations();
    }
  };


  public static void main( String args[] )
  {
    ColorScaleInfo.Orientation orientation;
    orientation = ColorScaleInfo.Orientation.HORIZONTAL;

    float   min        = 1;
    float   max        = 100;
    float   prescale   = 1;
    String  cs_name    = IndexColorMaker.RAINBOW_SCALE;
    boolean two_sided  = false;
    int     num_colors = 20;
    byte[]  table      = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
    boolean is_log     = true;

    ColorScaleInfo info = new ColorScaleInfo( orientation,
                                              min,
                                              max,
                                              prescale,
                                              cs_name,
                                              two_sided,
                                              num_colors,
                                              table,
                                              is_log  );

    NewColorScale scale = new NewColorScale( "My Scale", info );
 
    JFrame frame = new JFrame( "ColorPanel Test" );
    frame.getContentPane().setLayout( new GridLayout(1,1) );
    frame.getContentPane().add( scale );
    frame.setSize( 300, 80 );
    frame.setVisible( true );
  }

}
