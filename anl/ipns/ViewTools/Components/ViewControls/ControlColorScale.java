/*
 * File: ControlColorScale.java
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
 *  Revision 1.16  2004/01/07 22:33:29  millermi
 *  - Removed AxisOverlay2D.getPixelPoint(). Calculation of
 *    cursor value now done within the ControlColorScale's
 *    setMarker() method. ColorScaleImage now knows about
 *    WorldCoordBounds.
 *
 *  Revision 1.15  2003/12/23 01:58:32  millermi
 *  - Adjusted interface package locations since they
 *    were moved from the TwoD directory
 *
 *  Revision 1.14  2003/12/20 20:36:25  millermi
 *  - setLogScale() now bounds the logscale value in the
 *    interval [0,1]. Previously, it just assumed it true.
 *
 *  Revision 1.13  2003/12/20 20:08:20  millermi
 *  - Now uses getValueAxisInfo() to get data min/max.
 *
 *  Revision 1.12  2003/12/18 22:42:13  millermi
 *  - This file was involved in generalizing AxisInfo2D to
 *    AxisInfo. This change was made so that the AxisInfo
 *    class can be used for more than just 2D axes.
 *
 *  Revision 1.11  2003/11/18 22:32:42  millermi
 *  - Added functionality to allow cursor events to be
 *    traced by the ControlColorScale.
 *
 *  Revision 1.10  2003/10/24 02:49:54  millermi
 *  - Added vertical preferred size to the basic color scale.
 *
 *  Revision 1.9  2003/10/16 05:00:14  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.8  2003/09/19 03:42:20  millermi
 *  - Added/fixed java docs.
 *
 *  Revision 1.7  2003/07/25 14:44:14  dennis
 *  - Removed method implementation of getLocalCoordBounds() and
 *    getGlobalCoordBounds() since implementation of ILogAxisAddible2D
 *    no longer requires implementing these two methods.
 *    (Mike Miller)
 *
 *  Revision 1.6  2003/07/10 13:39:35  dennis
 *  - Made One-sided color models available
 *  (Mike Miller)
 *
 *  Revision 1.5  2003/07/05 19:51:49  dennis
 *  - Now implements ILogAxisAddible2D, which includes adding
 *    method getLogScale().
 *  - Added capability for one- or two-sided color models. Currently,
 *    only two-sided color models are allowed. Alter line 128 to enable
 *    this feature.
 *  (Mike Miller)
 *
 *  Revision 1.4  2003/06/18 22:15:33  dennis
 *  (Mike Miller)
 *  - Fixed calibration update on zoom. Previously, the interval
 *    on the color scale would adjust when the image was zoomed.
 *    Now it remains constant.
 *
 *  Revision 1.3  2003/06/18 13:30:03  dennis
 *  - Restructured to enable calibrations. Added constructor for
 *    calibration color scale.
 *  - Added implementation of IAxisAddible2D.
 *  - FIX NEEDED - Vertical scale, if too small,
 *    will not display all of the color scale.
 *  - Added listener to color and log scale changes.
 *
 *  Revision 1.2  2003/06/09 22:36:54  dennis
 *  - Added setEventListening(false) method call for ColorScaleImage to
 *    ignore keyboard/mouse events. (Mike Miller)
 *
 *  Revision 1.1  2003/05/29 14:25:27  dennis
 *  Initial version, displays the possible colors of the ImageViewComponent.
 *  (Mike Miller)
 *
 */
  
 package DataSetTools.components.View.ViewControls;
 
 import javax.swing.OverlayLayout;
 import javax.swing.JFrame;
 import javax.swing.JPanel;
 import java.awt.event.ActionListener;
 import java.awt.event.ActionEvent;
 import java.awt.*;
 
 import DataSetTools.components.image.IndexColorMaker;
 import DataSetTools.components.ui.ColorScaleImage;
 import DataSetTools.components.View.TwoD.ImageViewComponent;
 import DataSetTools.components.View.VirtualArray2D;
 import DataSetTools.components.View.AxisInfo;
 import DataSetTools.components.View.LogScaleUtil;
 import DataSetTools.components.View.Transparency.AxisOverlay2D;
 import DataSetTools.components.View.Transparency.ILogAxisAddible;
 import DataSetTools.components.image.CoordBounds;
 import DataSetTools.util.floatPoint2D;
 import DataSetTools.util.WindowShower;

/**
 * This class is a ViewControl (ActiveJPanel) with a color scale for use 
 * by ViewComponents. No messages are sent by this control.
 */ 
public class ControlColorScale extends ViewControl
                                         implements ILogAxisAddible 
{
  public static final boolean HORIZONTAL = true;
  public static final boolean VERTICAL   = false;
  
  private IColorScaleAddible component;
  private AxisOverlay2D axis;
  private JPanel background;
  private ColorScaleImage csi;
  private String colorscheme;
  private boolean isBasic = true; // basic vs calibrated color scales
  private Font font;
  private AxisInfo value_info;
  private double logscale;
  private boolean isTwoSided;
  private boolean orientate = true;
  
 /* Possible color schemes as designated by 
  * DataSetTools/components/image/IndexColorMaker.java
  *
  * GRAY_SCALE  	
  * NEGATIVE_GRAY_SCALE   
  * GREEN_YELLOW_SCALE  
  * HEATED_OBJECT_SCALE   
  * HEATED_OBJECT_SCALE_2 
  * RAINBOW_SCALE	
  * OPTIMAL_SCALE	
  * MULTI_SCALE 	
  * SPECTRUM_SCALE
  */	
  
 /**
  * Constructor used for calibrated color scale.
  *
  *  @param  icsa - IColorScaleAddible component
  *  @param  orientation
  */ 
  public ControlColorScale( IColorScaleAddible icsa, boolean orientation )
  {  
    super("");
    component = icsa; 
    value_info = component.getValueAxisInfo();
    isBasic = false; 
    if( value_info.getMin() < 0 )
      isTwoSided = true;
    else
      isTwoSided = false;
    setOrientation(orientation); 
    this.setLayout( new OverlayLayout(this) );  
    this.add( axis );
    this.add(background); 
    colorscheme = component.getColorScale(); 
    // initialize to a two-sided model
    setColorScale( colorscheme, isTwoSided ); 
    setAxisVisible(true); 
    font = component.getFont();
    logscale = 0;
    component.addActionListener( new ColorChangedListener() );
  }
  
 /**
  * Constructor creates a basic colorscale without calibration.
  * Possible color schemes are listed in IndexColorMaker.java.
  *
  *  @param  colorscale Colorscale for this control
  *  @param  doublesided Is data double-sided?
  */ 
  public ControlColorScale( String colorscale, boolean doublesided )
  {  
    super("");
    this.setLayout( new GridLayout(1,1) );
    isBasic = true;
    isTwoSided = doublesided;
    if( isTwoSided )
      csi = new ColorScaleImage(ColorScaleImage.HORIZONTAL_DUAL);
    else
      csi = new ColorScaleImage(ColorScaleImage.HORIZONTAL_SINGLE);
    csi.setEventListening(false);
    this.add(csi);
    colorscheme = colorscale; 
    setColorScale( colorscale, isTwoSided ); 
    setPreferredSize( new Dimension(0,35) );  
  }
 
 /**
  * This method allows calibrated color scales to trace the current point.
  *
  *  @param  marker - value that needs marking.
  */ 
  public void setMarker( float marker )
  {
    if( !isBasic )
    {
      floatPoint2D wc_mark;
      boolean negate = false;   // true if marker is negative
      //System.out.println("Marker value1: " + marker );
      // keep marker positive
      if( marker < 0 )
      {
        marker = -marker;
	negate = true;
      }
      // range will be 0 - axis_max for two-sided data, if marker < 0, negate
      // it since calibrations are always symmetric.
      float axis_min = value_info.getMin();
      float axis_max = value_info.getMax();
      if( isTwoSided || (axis_min < 0) )
      {
	axis_min = 0;
      }
      LogScaleUtil logger = new LogScaleUtil( axis_min, axis_max,
                                              axis_min, axis_max );
      double logscale = component.getLogScale(); 
      marker = logger.toSource(marker, logscale);
      if( negate && isTwoSided )
        marker = -marker;
      // horizontal, zero out vertical factor
      if( orientate )
      {
	wc_mark = new floatPoint2D( marker, 0 );
      }
      // vertical, zero out horizontal factor
      else
      {
        wc_mark = new floatPoint2D( 0, marker );
      }
      csi.set_crosshair_WC(wc_mark);
    }
  }
 
 /**
  * This method sets the color scheme of the ColorScaleImage.
  * Possible color schemes are listed in IndexColorMaker.java.
  *
  *  @param  colorscale Colorscale for this control
  *  @param  doublesided Is data double-sided?
  */
  public void setColorScale( String colorscale, boolean doublesided )
  {
    colorscheme = colorscale;
    isTwoSided = doublesided;
    csi.setNamedColorModel( colorscale, isTwoSided, true );
  } 
  
 /**
  * This method gets the color scheme of the ColorScaleImage.
  * The scheme will be one listed in IndexColorMaker.java.
  *
  *  @return colorscheme
  */
  public String getColorScale()
  {
    if( !isBasic )
      colorscheme = component.getColorScale();
    return colorscheme;
  }  
 
 /**
  * This method sets the logscale of this control. The logscale
  * is only used for calibrated colorscales.
  *
  *  @param  value Double value on interval [0,1]
  */
  public void setLogScale( double value )
  {
    // restrict value to interval [0,1]
    if( value < 0 )
      value = 0;
    if( value > 1 )
      value = 1;
    logscale = value;
  }
  
 /**
  * This method will get the current log scale value for the slider this
  * control is listening to.
  *
  *  @return logscale
  */ 
  public double getLogScale()
  {
    return logscale;
  }	
  
 /**
  * This method sets the orientation of the ColorScaleImage to either
  * horizontal or vertical.
  *
  *  @param  showAxis Either HORIZONTAL or VERTICAL
  */
  public void setAxisVisible( boolean showAxis)
  {
    if( !isBasic )
    {
      if( showAxis )
      {
        background.getComponent(1).setVisible( true );
        background.getComponent(2).setVisible( true );
        background.getComponent(3).setVisible( true );
        background.getComponent(4).setVisible( true );
        axis.setVisible( true );
      }
      else
      {
        background.getComponent(1).setVisible( false );
        background.getComponent(2).setVisible( false );
        background.getComponent(3).setVisible( false );
        background.getComponent(4).setVisible( false );
        axis.setVisible( false );
      }
    }
    else
    {
      System.out.println("setAxisVisible() is not available with the " +
        		 "constructor chosen. Use constructor: " +
        		 "public ControlColorScale( " +
        		 "IColorScaleAddible icsa, boolean orientation ) " +
        		 "to enable this method." );
    }     
  }	       

 /**
  * The integer, from AxisInfo integer codes, will determine which axis
  * to get information for. The information is wrapped in an AxisInfo object.
  * This method also tells the axis overlay to display the data in log form.
  *
  *  @param  axiscode
  *  @return value axisinfo of the component, always in log form.
  *	     If this is a basic color scale, a dumby value is returned.
  */
  public AxisInfo getAxisInformation(int axiscode)
  {
    if( !isBasic )
      return new AxisInfo( value_info.getMin(),
                           value_info.getMax(), "", "", false );
    else
    {
      System.out.println("getAxisInfo() is not available with the " +
        		 "constructor chosen. Use constructor: " +
        		 "public ControlColorScale( " +
        		 "IColorScaleAddible icsa, boolean orientation ) " +
        		 "to enable this method." );
      return new AxisInfo( 0,1,"","",false );
    }
  }
  
 /**
  * This method will return a rectangle with pixel coordinates corresponding
  * to the desired region.
  *
  *  @return bounds of the center image.
  */ 
  public Rectangle getRegionInfo()
  {
    return csi.getBounds();
  }

 /**
  * This method will return the precision specified by the user. Precision
  * will be assumed to be 4 if not specified. The axis overlays will call
  * this method to determine the precision.
  *
  *  @return precision of the component
  *	     If a basic color scale, -1 is returned.
  */
  public int getPrecision()
  {
    if( !isBasic )
      return component.getPrecision();
    else
    {
      System.out.println("getPrecision() is not available with the " +
        		 "constructor chosen. Use constructor: " +
        		 "public ControlColorScale( " +
        		 "IColorScaleAddible icsa, boolean orientation ) " +
        		 "to enable this method." );
      return -1;
    }
  } 
  
 /**
  * This method will return the font used on by the overlays. The axis overlay
  * will call this to determine what font to use.
  *
  *  @return font of component
  * 
  * Note: If a basic (non-calibrated) color scale, 
  *	  the view control font is returned.
  */
  public Font getFont()
  {
    if( !isBasic )
      return font;
    else
      return super.getFont();
  }
  
 /**
  * This method overloads the ViewControls getTitle() so that no title is
  * passed. Because the axis overlay displays the title, we need to set it
  * to an empty string. The title is already displayed by the titled border.
  *
  *  @return empty_string
  */
  public String getTitle()
  {
    return "";
  } 
  
 /**
  * This private method sets the orientation of the ColorScaleImage to either
  * horizontal or vertical and adjusts the structure accordingly.
  *
  *  @param  orientation
  */
  private void setOrientation( boolean orientation)
  {
    orientate = orientation;
    JPanel north = new JPanel();
    JPanel east = new JPanel(); 
    JPanel south = new JPanel(); 
    JPanel west = new JPanel(); 
    float axis_min = value_info.getMin();
    float axis_max = value_info.getMax();
    // if true, horizontal alignment
    if( orientation )
    {
      north.setPreferredSize(new Dimension( 0, 0 ) );
      east.setPreferredSize(new Dimension( 25, 0 ) ); 
      south.setPreferredSize(new Dimension( 0, 35 ) );
      west.setPreferredSize(new Dimension( 25, 0 ) );
      if( isTwoSided )
      { 
        csi = new ColorScaleImage(ColorScaleImage.HORIZONTAL_DUAL);
        csi.initializeWorldCoords( new CoordBounds(-axis_max, 0, axis_max, 1) );
      }
      else
      {
        csi = new ColorScaleImage(ColorScaleImage.HORIZONTAL_SINGLE);
        csi.initializeWorldCoords( new CoordBounds(axis_min, 0, axis_max, 1) );
      }
    }
    // else vertical alignment
    else
    { 
      north.setPreferredSize(new Dimension( 0, 10 ) );
      east.setPreferredSize(new Dimension( 0, 0 ) ); 
      south.setPreferredSize(new Dimension( 0, 10 ) );
      west.setPreferredSize(new Dimension( 60, 0 ) ); 
      if( isTwoSided ) 
      {
        csi = new ColorScaleImage(ColorScaleImage.VERTICAL_DUAL);
        csi.initializeWorldCoords( new CoordBounds(0, axis_max, 1, -axis_max) );
      }
      else
      {
        csi = new ColorScaleImage(ColorScaleImage.VERTICAL_SINGLE);
        csi.initializeWorldCoords( new CoordBounds(0, axis_max, 1, axis_min) );
      }
    }
    csi.setEventListening(false);
    
    background = new JPanel( new BorderLayout() );
    background.add( csi, "Center" );
    background.add( north, "North" ); 
    background.add( east, "East" ); 
    background.add( south, "South" );
    background.add( west, "West" );
    
    axis = new AxisOverlay2D( this );
    axis.setTwoSided( isTwoSided ); 
    // if true, horizontal alignment
    if( orientation )
      axis.setDisplayAxes(AxisOverlay2D.X_AXIS); 
    else
      axis.setDisplayAxes(AxisOverlay2D.Y_AXIS); 
  }
  
  private class ColorChangedListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      String message = e.getActionCommand();
      if( message.equals( component.getColorScale() ) )
        setColorScale( message, isTwoSided );
      else if ( message == IViewControl.SLIDER_CHANGED )
      { 
        IColorScaleAddible temp = (IColorScaleAddible)e.getSource();
        logscale = temp.getLogScale();
      }
    }
  }   

 /*
  *  For testing purposes only
  */
  public static void main(String[] args)
  {	 
    int col = 250;
    int row = 250;    
    //Make a sample 2D array
    VirtualArray2D va2D = new VirtualArray2D(row, col); 
    va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, .0001f, 
 			 "TestX","TestUnits", true );
    va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, .001f, 
 			  "TestY","TestYUnits", true );
    va2D.setTitle("Main Test");
    //Fill the 2D array with the function x*y
    float ftemp;
    for(int i = 0; i < row; i++)
    {
      for(int j = 0; j < col; j++)
      {
        ftemp = i*j;
        if ( i % 25 == 0 )
          va2D.setDataValue(i, j, i*col); //put float into va2D
        else if ( j % 25 == 0 )
          va2D.setDataValue(i, j, j*row); //put float into va2D
        else
          va2D.setDataValue(i, j, ftemp); //put float into va2D
      }
    }
    ImageViewComponent ivc = new ImageViewComponent(va2D);
    ControlColorScale color = 
 		new ControlColorScale(ivc, ControlColorScale.HORIZONTAL);
    /*ControlColorScale color = 
 		new ControlColorScale(IndexColorMaker.GRAY_SCALE, true);*/ 
    
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.getContentPane().setLayout( new GridLayout(1,1) );
    frame.setTitle("ControlColorScale Test");
    frame.setBounds(0,0,300,300);
    frame.getContentPane().add(color);
    color.setTitle("myColorScale");
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
}
