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
 *  Revision 1.30  2005/05/07 00:19:48  millermi
 *  - Fixed javadoc errors.
 *
 *  Revision 1.29  2005/03/28 05:57:26  millermi
 *  - Added copy() which will make an exact copy of the ViewControl.
 *
 *  Revision 1.28  2005/03/09 22:36:04  millermi
 *  - Added methods get/setControlValue() and messaging of VALUE_CHANGED
 *    to enable controls to be linked.
 *  - Added "cm" as parameter to main() to test control with the
 *    ControlManager.
 *
 *  Revision 1.27  2005/02/03 22:07:52  millermi
 *  - No longer listens for ControlSlider messages. Only listens
 *    for messages produced by IColorScaleAddible components.
 *
 *  Revision 1.26  2004/11/12 21:20:05  millermi
 *  - Changed LogScaleUtil to PseudoLogScaleUtil since pseudo-log
 *    mapping was moved.
 *  - Constructor now initializes logscale to that of the
 *    IColorScaleAddible component passed it.
 *
 *  Revision 1.25  2004/11/11 19:48:24  millermi
 *  - Modified method calls to new version of LogScaleUtil.
 *  - Added getLocalCoordBounds() and getGlobalCoordBounds() to reflect the
 *    changes made to the IAxisAddible interface.
 *
 *  Revision 1.24  2004/11/05 22:06:52  millermi
 *  - Now implements IPseudoLogAxisAddible instead of ILogAxisAddible.
 *
 *  Revision 1.23  2004/09/15 21:55:47  millermi
 *  - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *    Adding a second log required the boolean parameter to be changed
 *    to an int. These changes may affect any ObjectState saved configurations
 *    made prior to this version.
 *
 *  Revision 1.22  2004/08/06 18:50:30  millermi
 *  - Now checks for new COLORSCALE_CHANGED message sent out by any
 *    IColorScaleAddible objects.
 *
 *  Revision 1.21  2004/03/19 18:04:37  millermi
 *  - Replaced labels and units in getAxisInformation() from ""
 *    to AxisInfo static variables.
 *
 *  Revision 1.20  2004/03/17 20:26:51  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.19  2004/03/12 03:16:46  millermi
 *  - Changed package, fixed imports.
 *
 *  Revision 1.18  2004/01/30 22:11:12  millermi
 *  - Removed messaging Strings from interface and into respective
 *    implementing classes that actually send out the message.
 *
 *  Revision 1.17  2004/01/30 06:38:10  millermi
 *  - Added specific ObjectState information.
 *
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
  
 package gov.anl.ipns.ViewTools.Components.ViewControls;
 
 import javax.swing.OverlayLayout;
 import javax.swing.JFrame;
 import javax.swing.JPanel;
 import java.awt.event.ActionListener;
 import java.awt.event.ActionEvent;
 import java.awt.*;
 
 import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
 import gov.anl.ipns.ViewTools.UI.ColorScaleImage;
 import gov.anl.ipns.ViewTools.Components.AxisInfo;
 import gov.anl.ipns.ViewTools.Components.ObjectState;
 import gov.anl.ipns.ViewTools.Components.PseudoLogScaleUtil;
 import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
 import gov.anl.ipns.ViewTools.Components.Transparency.AxisOverlay2D;
 import gov.anl.ipns.ViewTools.Components.Transparency.IPseudoLogAxisAddible;
 import gov.anl.ipns.ViewTools.Components.TwoD.ImageViewComponent;
 import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
 import gov.anl.ipns.Util.Numeric.floatPoint2D;
 import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * This class is a ViewControl (ActiveJPanel) with a color scale for use 
 * by ViewComponents. No messages are sent by this control.
 */ 
public class ControlColorScale extends ViewControl
                                         implements IPseudoLogAxisAddible 
{
 /**
  * "true" - Use this value to specify a horizontal (x-axis) calibrated
  * colorscale. This variable has no relevance when used with the basic
  * colorscale.
  */
  public static final boolean HORIZONTAL = true;
  
 /**
  * "false" - Use this value to specify a vertical (y-axis) calibrated
  * colorscale. This variable has no relevance when used with the basic
  * colorscale.
  */
  public static final boolean VERTICAL   = false;
 //--------------------------------ObjectState Keys----------------------------
 /**
  * "Orientation" - This constant String is a key for referencing the state
  * information about the orientation of the colorscale. Static boolean
  * values HORIZONTAL and VERTICAL are used to set the orientation.
  * The value that this key references is a primative boolean.
  */
  public static final String ORIENTATION  = "Orientation";
 
 /**
  * "Color Scale" - This constant String is a key for referencing the state
  * information about the color scale used for the control. The value
  * that this key references is of type String. These String constants may be
  * found in IndexColorMaker.java.
  *
  * @see gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
  */
  public static final String COLOR_SCALE  = "Color Scale";
 
 /**
  * "Two Sided" - This constant String is a key for referencing the state
  * information about one or two-sided data. The value that this key references
  * is of type boolean. Values are true if two-sided, false if one-sided.
  */
  public static final String TWO_SIDED    = "Two Sided";
 
 /**
  * "Log Scale" - This constant String is a key for referencing the state
  * information about the intensity or log scale value. This value comes from
  * the intensity slider and is of type float. Values are on the range [0,1].
  */
  public static final String LOG_SCALE    = "Log Scale";
 
 /**
  * "Marker" - This constant String is a key for referencing the state
  * information about the current pointed-at value position on the colorscale.
  */
  public static final String MARKER  	  = "Marker";
  
  private IColorScaleAddible component;
  private AxisOverlay2D axis;
  private JPanel background;
  private ColorScaleImage csi;
  private String colorscheme = IndexColorMaker.HEATED_OBJECT_SCALE_2;
  private boolean isBasic = true; // basic vs calibrated color scales
  private Font font;
  private AxisInfo value_info;
  private double logscale;
  private boolean isTwoSided;
  private boolean orientate = HORIZONTAL;
  private boolean ignore_change = true;
  
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
    logscale = component.getLogScale();
    component.addActionListener( new ColorChangedListener() );
  }
  
 /**
  * Constructor creates a basic colorscale without calibration.
  * Possible color schemes are listed in IndexColorMaker.java.
  *
  *  @param  colorscale Colorscale for this control
  *  @param  doublesided Is data double-sided?
  *  @see    gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
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
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = super.getObjectState(isDefault);
    state.insert( COLOR_SCALE, new String(colorscheme) );
    state.insert( TWO_SIDED, new Boolean(isTwoSided) );
    if( !isBasic )
    {
      state.insert( ORIENTATION, new Boolean(orientate) );
      state.insert( LOG_SCALE, new Double(logscale) );
      // only save marker for project saves.
      if( !isDefault )
        state.insert( MARKER, new Float(getMarker()) );
    }
    return state;
  }
     
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    // Do nothing if state is null.
    if( new_state == null )
      return;
    super.setObjectState( new_state );
    boolean redraw = false;
    Object temp = new_state.get(COLOR_SCALE);
    if( temp != null )
    {
      setColorScale((String)temp,isTwoSided); 
      redraw = true;  
    } 
    
    temp = new_state.get(TWO_SIDED); 
    if( temp != null )
    {
      setTwoSided( ((Boolean)temp).booleanValue() );
      redraw = true;  
    } 	
    // only do these if calibrated colorscale
    if( !isBasic )
    {
      temp = new_state.get(ORIENTATION);
      if( temp != null )
      {
        setOrientation(((Boolean)temp).booleanValue()); 
        redraw = true;  
      } 
    
      temp = new_state.get(LOG_SCALE);
      if( temp != null )
      {
        setLogScale( ((Double)temp).doubleValue() ); 
        redraw = true;  
      }  
    
      temp = new_state.get(MARKER);
      if( temp != null )
      {
        setMarker(((Float)temp).floatValue()); 
        redraw = true;  
      } 
    }
    if( redraw )
      repaint();
  }
  
 /**
  * Set the color scheme of the colorscale.
  *
  *  @param  value String representing the color scheme.
  */
  public void setControlValue(Object value)
  {
    if( value == null || !(value instanceof String) )
      return;
    ignore_change = true;
    setColorScale((String)value,isTwoSided());
    ignore_change = false;
  }
  
 /**
  * Get String value for colorscheme as defined in IndexColorMaker.
  *
  *  @return String colorscale.
  */
  public Object getControlValue()
  {
    return getColorScale();
  }
  
 /**
  * This method will make an exact copy of the control.
  *
  *  @return A new, identical instance of the control.
  */
  public ViewControl copy()
  {
    ControlColorScale ccs;
    // If basic colorscale, only need colorscale and two sided.
    if( isBasic )
    {
      ccs = new ControlColorScale( getColorScale(), isTwoSided() );
    }
    // Else calibrated color scale.
    else
    {
      ccs = new ControlColorScale( component, orientate );
    }
    ccs.setObjectState( getObjectState(PROJECT) );
    return ccs;
  }
 
 /**
  * This method allows the calibrated colorscale to always appear
  * one- or two-sided, depending on the parameter. By setting this,
  * the one/two-sided setting becomes independent of the data.
  *
  *  @param  is_two_sided If true, data always displayed 2-sided.
  *                       If false, data always displayed 1-sided.
  */ 
  public void setTwoSided( boolean is_two_sided )
  {
    if( is_two_sided != isTwoSided )
    {
      isTwoSided = is_two_sided;
      setColorScale( colorscheme, isTwoSided );
    }
  }
  
 /**
  * This method tells whether the colorscale is two-sided (positive and
  * negative values) or just one sided (positive only).
  *
  *  @return True if two-sided, false if one-sided.
  */
  public boolean isTwoSided()
  {
    return isTwoSided;
  }
 
 /**
  * This method allows calibrated color scales to trace the current point.
  *
  *  @param  marker - world coordinate value that needs marking.
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
      PseudoLogScaleUtil logger = new PseudoLogScaleUtil( axis_min, axis_max );
      double logscale = component.getLogScale(); 
      marker = logger.toDest(marker, logscale);
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
    // Make sure scheme is a valid option.
    if( !( colorscale.equals(IndexColorMaker.GRAY_SCALE) ||
           colorscale.equals(IndexColorMaker.NEGATIVE_GRAY_SCALE) ||
           colorscale.equals(IndexColorMaker.GREEN_YELLOW_SCALE) ||
	   colorscale.equals(IndexColorMaker.HEATED_OBJECT_SCALE) ||
	   colorscale.equals(IndexColorMaker.HEATED_OBJECT_SCALE_2) ||
	   colorscale.equals(IndexColorMaker.RAINBOW_SCALE) ||
	   colorscale.equals(IndexColorMaker.OPTIMAL_SCALE) ||
	   colorscale.equals(IndexColorMaker.MULTI_SCALE) ||
	   colorscale.equals(IndexColorMaker.SPECTRUM_SCALE) ) )
      return;
    colorscheme = colorscale;
    isTwoSided = doublesided;
    csi.setNamedColorModel( colorscale, isTwoSided, true );
    // This if statement will prevent VALUE_CHANGED to be sent out when
    // the setControlValue() method is called.
    if( !ignore_change )
      send_message(VALUE_CHANGED);
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
  *  @param  value Double value on interval [0,100]
  */
  public void setLogScale( double value )
  {
    // restrict value to interval [0,100]
    if( value < 0 )
      value = 0;
    if( value > 100 )
      value = 100;
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
  * This method sets the turns the axes on and off.
  *
  *  @param  showAxis Either on (true) or off (false)
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
                           value_info.getMax(), 
			   AxisInfo.NO_LABEL, 
			   AxisInfo.NO_UNITS, AxisInfo.PSEUDO_LOG );
    else
    {
      System.out.println("getAxisInfo() is not available with the " +
        		 "constructor chosen. Use constructor: " +
        		 "public ControlColorScale( " +
        		 "IColorScaleAddible icsa, boolean orientation ) " +
        		 "to enable this method." );
      return new AxisInfo( 0,1,AxisInfo.NO_LABEL,AxisInfo.NO_UNITS,
                           AxisInfo.PSEUDO_LOG );
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
  * Stub that returns the "value axis" bounds since the colorscale does not
  * allow zooming. This method is required by IAxisAddible interface.
  */
  public CoordBounds getLocalCoordBounds()
  {
    return new CoordBounds( value_info.getMin(), value_info.getMin(),
                            value_info.getMax(), value_info.getMax() );
  }
  
 /**
  * Returns the global world bounds ("value axis bounds") of the colorscale.
  * This method is required by IAxisAddible interface.
  */
  public CoordBounds getGlobalCoordBounds()
  {
    return getLocalCoordBounds();
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
    setColorScale(colorscheme,isTwoSided);
    
    // if previously created, remove all of its components
    if( background != null )
      background.removeAll();
    // otherwise make a new one
    else
      background = new JPanel( new BorderLayout() );
    background.add( csi, "Center" );
    background.add( north, "North" ); 
    background.add( east, "East" ); 
    background.add( south, "South" );
    background.add( west, "West" );
    
    // if previously created, remove all of its components
    if( axis != null )
      axis.removeAll();
    // otherwise make a new one
    else
      axis = new AxisOverlay2D( this );
    axis.setTwoSided( isTwoSided ); 
    // if true, horizontal alignment
    if( orientation )
      axis.setDisplayAxes(AxisOverlay2D.X_AXIS); 
    else
      axis.setDisplayAxes(AxisOverlay2D.Y_AXIS); 
  }
  
  private float getMarker()
  {
    floatPoint2D current = csi.getCurrent_WC_point();
    if( current.x != 0 )
      return current.x;
    return current.y;
  }
  
  private class ColorChangedListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      String message = e.getActionCommand(); 
      IColorScaleAddible temp = (IColorScaleAddible)e.getSource();
      if( message.equals( IColorScaleAddible.COLORSCALE_CHANGED ) )
      {
        // Update the logscale factor.
        setLogScale(temp.getLogScale());
	// Set the color scheme for the colorscale.
        setColorScale( temp.getColorScale(), isTwoSided );
      }
    }
  }   

 /**
  *  For testing purposes only...
  */
  public static void main(String[] args)
  {	 
    int col = 250;
    int row = 250;    
    //Make a sample 2D array
    VirtualArray2D va2D = new VirtualArray2D(row, col); 
    va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, .0001f, 
 			 "TestX","TestUnits", AxisInfo.LINEAR );
    va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, .001f, 
 			  "TestY","TestYUnits", AxisInfo.LINEAR );
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
    
    color.setTitle("myColorScale");
    // test state
    ObjectState state = new ObjectState();
    state.insert( COLOR_SCALE, new String(IndexColorMaker.MULTI_SCALE) );
    state.insert( TWO_SIDED, new Boolean(true) );
    state.insert( ORIENTATION, new Boolean(VERTICAL) );
    state.insert( LOG_SCALE, new Double(.5) );
    state.insert( MARKER, new Float(0) );
    color.setObjectState(state);
    
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.getContentPane().setLayout( new GridLayout(1,0) );
    frame.setTitle("ControlColorScale Test");
    frame.setBounds(0,0,600,300);
    frame.getContentPane().add(color);
    frame.getContentPane().add(color.copy());
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
}
