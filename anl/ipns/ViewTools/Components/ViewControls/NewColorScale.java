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


  /**
   *  Construct a color scale with default values.
   *
   *  @param title  The title to appear on the border of the color scale.
   */
  public NewColorScale( String title )
  {
    super( title );

    Axis.Orientation orientation = Axis.Orientation.HORIZONTAL;
    float   max        = 100;
    float   min        = 0;
    float   prescale   = 1;
    String  cs_name    = IndexColorMaker.RAINBOW_SCALE;
    boolean two_sided  = false;
    int     num_colors = 20;
    byte[]  table      = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
    boolean is_log     = false;

    info = new ColorScaleInfo( orientation,
                               min,
                               max,
                               prescale,
                               cs_name,
                               two_sided,
                               num_colors,
                               table,
                               is_log  );
    buildPanel();
    resetCalibrations();
  }

  /**
   *  Construct a color scale with values specified by a ColorScaleInfo
   *  object.
   *
   *  @param info   The bundle of information describing this color scale.
   *  @param title  The title to appear on the border of the color scale.
   */
  public NewColorScale( String title, ColorScaleInfo info )
  {
    super( title );
    this.info = info;

    buildPanel();
    resetCalibrations();
  }

  /**
   *  Set the "value" of this control.  The object MUST be ColorScaleInfo
   *  object, or an IllegalArgumentException will be thrown.
   *
   *  @param value  The ColorScaleInfo object that holds all of the information
   *                that constitutes the value of this color scale.
   */
  public void setControlValue(Object value)
  {
    if ( !(value instanceof ColorScaleInfo) )
      throw new IllegalArgumentException("Need ColorScaleInfo parameter");

    this.info = (ColorScaleInfo)value;
    buildPanel();
    resetCalibrations();
  }
  

  /**
   *  Get the "value" of this control.  The object returned will be a
   *  ColorScaleInfo object.
   *
   *  @return  The ColorScaleInfo object that holds all of the information
   *           that constitutes the value of this color scale.
   */
  public Object getControlValue()
  {
    return info;
  }
  
 
  /** 
   *  Make a new NewColorScale control that has the same title and 
   *  information as this one.
   */
  public ViewControl copy()
  {
    return new NewColorScale( getTitle(), info );
  }


  /**
   *
   */
  private void buildPanel()
  {
    image_panel = new ImageJPanel2();
    scale_panel = new JPanel();
    if ( info.getOrientation() == Axis.Orientation.HORIZONTAL )
      scale_panel.setPreferredSize( new Dimension(100,25) );
    else
      scale_panel.setPreferredSize( new Dimension(55,100) );

    scale_panel.setLayout( new GridLayout(1,1) );

    setLayout( new BorderLayout() );
    add( image_panel, BorderLayout.CENTER );
    if ( info.getOrientation() == Axis.Orientation.HORIZONTAL )
      add( scale_panel, BorderLayout.SOUTH );
    else
      add( scale_panel, BorderLayout.WEST );

    addComponentListener( new RedrawListener() );

    VirtualArray2D va2D = buildImageArray( NUM_VALUES );
    image_panel.setData( va2D, false );
    image_panel.setDataRange( info.getTableMin(), info.getTableMax() );
    image_panel.changeColorIndexTable( info.getColorIndexTable(),
                                       info.isLog(),
                                       info.getTableMin(),
                                       info.getTableMax(),
                                       false );
  
    image_panel.setNamedColorModel( info.getColorScaleName(),
                                    info.isTwoSided(),
                                    info.getNumColors(),
                                    true );
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

    TwoD_JPanel panel = new TwoD_JPanel();
    scale_panel.add( panel );

    Axis axis;
    if ( is_log )
    {
      double[] points = Subdivide.subdivideLog( min, max );
      axis = new LogAxis( 0, 0, width, height, min, max, points,
                          info.getOrientation() );
    }
    else
    {
      double[] points = Subdivide.subdivideLinear( min, max );
      axis = new LinearAxis( 0, 0, width, height, min, max, points, 
                             info.getOrientation() );
    }
    panel.AddObject( axis );

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
    if ( info.getOrientation() == Axis.Orientation.HORIZONTAL )
      va2D = new VirtualArray2D( values );
    else
    {
      float[][] transpose = new float[num_values][1];
      for ( int i = 0; i < num_values; i++ )               // NOTE: row numbers
        transpose[i][0] = values[0][num_values - 1 - i];   // start at 0 at
      va2D = new VirtualArray2D( transpose );              // the top of the
    }                                                      // image
 
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
    Axis.Orientation orientation = Axis.Orientation.VERTICAL;

    float   prescale   = 1;
    String  cs_name    = IndexColorMaker.RAINBOW_SCALE;
    boolean two_sided  = false;
    int     num_colors = 20;
    byte[]  table      = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
    float   max        = (float)1e-4;
    float   min        = (float)1e-6;
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

//    NewColorScale scale = new NewColorScale( "My Scale" );
 
    JFrame frame = new JFrame( "NewColorScale" );
    frame.getContentPane().setLayout( new GridLayout(1,1) );
    frame.getContentPane().add( scale );
    frame.setSize( 300, 300 );
    frame.setVisible( true );
  }

}
