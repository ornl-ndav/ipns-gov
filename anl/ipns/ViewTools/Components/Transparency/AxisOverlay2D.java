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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.43  2004/11/05 22:04:06  millermi
 *  - Added additional stability to paintTruLogX() and paintTruLogY()
 *    for painting log axes.
 *
 *  Revision 1.42  2004/09/15 21:55:46  millermi
 *  - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *    Adding a second log required the boolean parameter to be changed
 *    to an int. These changes may affect any ObjectState saved configurations
 *    made prior to this version.
 *
 *  Revision 1.41  2004/08/18 21:03:55  millermi
 *  - paint() calls paintLogX() and paintLogY() when log axes are
 *    specified. This was done because current implementations of
 *    paintTruLogX() and paintTruLogY() inconsistently display
 *    log axes.
 *
 *  Revision 1.40  2004/08/17 03:59:12  ffr
 *  Bugfix: Calling super.paint in paint() method to make sure that any
 *  lightweight descendents are shown.
 *
 *  Revision 1.39  2004/07/29 16:44:29  robertsonj
 *  added javadocs
 *
 *  Revision 1.38  2004/07/29 14:47:40  robertsonj
 *  fixed the major/minor tick marks for small orders of magnatude
 *
 *  Revision 1.37  2004/07/28 19:34:52  robertsonj
 *  added paint truLogX and paintTruLogY for painting x and y axis on the 
 *  overlay.  The functionality of these have not been completely work out
 *  as of right now
 *
 *  Revision 1.36  2004/07/10 04:49:00  millermi
 *  - Added private method removeTrailingZeros() to remove
 *    zeros after the decimal on calibrations. To view
 *    the AxisOverlay2D with the previous format, comment
 *    out the body of removeTrailingZeros().
 *
 *  Revision 1.35  2004/05/11 01:36:27  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.34  2004/04/16 19:00:33  millermi
 *  - paintLabelsAndUnits() now checks for label and units
 *    that are empty strings.
 *
 *  Revision 1.33  2004/03/19 20:16:39  millermi
 *  - Fixed log axis display of y axis. Previously, the maximum
 *    positive and negative values were switched.
 *  - Fixed y log axis display that caused end tick marks
 *    to be drawn twice.
 *
 *  Revision 1.32  2004/03/19 18:07:04  millermi
 *  - Changed how label, units, and power is displayed.
 *  - Factored out display of labels into method
 *    paintLabelsAndUnits().
 *
 *  Revision 1.31  2004/03/15 23:53:52  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.30  2004/03/12 03:16:46  millermi
 *  - Changed package, fixed imports.
 *
 *  Revision 1.29  2004/02/06 23:23:43  millermi
 *  - Changed how editor bounds were stored in the ObjectState,
 *    removed check if visible.
 *
 *  Revision 1.28  2004/01/29 08:16:27  millermi
 *  - Updated the getObjectState() to include parameter for specifying
 *    default state.
 *  - Added static variables DEFAULT and PROJECT to IPreserveState for
 *    use by getObjectState()
 *
 *  Revision 1.27  2004/01/07 22:33:29  millermi
 *  - Removed AxisOverlay2D.getPixelPoint(). Calculation of
 *    cursor value now done within the ControlColorScale's
 *    setMarker() method. ColorScaleImage now knows about
 *    WorldCoordBounds.
 *
 *  Revision 1.26  2004/01/07 21:56:37  millermi
 *  - Fixed log interval calculation, now that the destination
 *    interval can be negative, minor changes were made.
 *
 *  Revision 1.25  2004/01/03 04:36:12  millermi
 *  - help() now uses html tool kit to display text.
 *  - Replaced all setVisible(true) with WindowShower.
 *
 *  Revision 1.24  2003/12/30 03:55:31  millermi
 *  - Fixed bug that caused no log calibrations to be shown when
 *    image was very small.
 *  - Ticks associated with labels will always be shown when
 *    the label itself is visible (for log axes).
 *
 *  Revision 1.23  2003/12/23 01:58:32  millermi
 *  - Adjusted interface package locations since they
 *    were moved from the TwoD directory
 *
 *  Revision 1.22  2003/12/20 21:37:29  millermi
 *  - implemented kill() so editor and help windows are now
 *    disposed when the kill() is called.
 *
 *  Revision 1.21  2003/12/18 22:35:28  millermi
 *  - Moved LINEAR and LOG strings to AxisInfo class.
 *  - Changed string compare of NO_LABEL and NO_UNITS from
 *    !String.equals() to != so the only way the string is
 *    not shown is if it is NO_LABEL/UNITS not just "looks"
 *    like it.
 *
 *  Revision 1.20  2003/11/21 02:59:55  millermi
 *  - Now saves editor bounds before dispose() is called on
 *    the editor.
 *
 *  Revision 1.19  2003/11/18 22:32:41  millermi
 *  - Added functionality to allow cursor events to be
 *    traced by the ControlColorScale.
 *
 *  Revision 1.18  2003/11/18 01:00:17  millermi
 *  - Made non-save dependent private variables transient.
 *
 *  Revision 1.17  2003/10/20 22:46:51  millermi
 *  - Added private class NotVisibleListener to listen
 *    when the overlay is no longer visible. When not
 *    visible, any editor that is visible will be made
 *    invisible too. This will not dispose the editor,
 *    just setVisible(false).
 *
 *  Revision 1.16  2003/10/16 05:00:07  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.15  2003/10/02 04:25:44  millermi
 *  - Added java docs to public static variables
 *  - Added ObjectState constructor.
 *
 *  Revision 1.14  2003/09/24 01:32:40  millermi
 *  - Added static variables to be used as keys by ObjectState
 *  - Added methods setObjectState() and getObjectState() to adjust to
 *    changes made in OverlayJPanel.
 *  - Added componentResized() listener to set editor bounds
 *    when the editor is resized.
 *
 *  Revision 1.13  2003/08/14 21:46:32  millermi
 *  - Added toFront() to AxisEditor to display it over the viewer.
 *  - Added grid color changing abilities to AxisEditor
 *  - Added information pertaining to grid color into help()
 *  - Fixed bug that displayed grid line for special case minor tick when
 *    only drawing for major tick marks was selected.
 *
 *  Revision 1.12  2003/08/14 17:12:46  millermi
 *  - Edited help() to provide more description.
 *
 *  Revision 1.11  2003/08/06 13:55:57  dennis
 *  - Added Axis Editor to allow for the addition of grid lines.
 *  - Log calibration display changed. Previously all calibrations
 *    were shown, now a tick is shown only if it is at least 5 pixels
 *    from the last calibration drawn.
 *    (Mike Miller)
 *
 *  Revision 1.10  2003/07/25 14:42:57  dennis
 *  - Added if statement to ensure component is of type LogAxisAddible2D
 *    in paintLogX() and paintLogY(). (Mike Miller)
 *
 *  Revision 1.9  2003/07/05 19:44:21  dennis
 *  - Implemented private methods paintLogX() and paintLogY()
 *    (Mike Miller)
 *
 *  Revision 1.8  2003/06/18 22:14:28  dennis
 *  (Mike Miller)
 *  - Restructured the paint method to allow linear or log axis display.
 *    Paint method now calls private methods to paint the axis.
 *
 *  Revision 1.7  2003/06/18 13:35:25  dennis
 *  (Mike Miller)
 *  - Added method setDisplayAxes() to turn on/off x and/or y axes.
 *
 *  Revision 1.6  2003/06/17 13:22:14  dennis
 *  - Updated help menu. No functional changes made. (Mike Miller)
 *
 *  Revision 1.5  2003/06/09 22:33:33  dennis
 *  - Added static method help() to display commands via the HelpMenu.
 *    (Mike Miller)
 *
 *  Revision 1.4  2003/05/22 17:51:05  dennis
 *  Corrected problem of missing calibratons at beginning and end
 *  of y-axis and beginning of x-axis. (Mike Miller)
 *
 *  Revision 1.3  2003/05/16 14:56:12  dennis
 *  Added calibration intervals on the X-Axis when resizing. (Mike Miller)
 *
 */ 
// X axis has a constant position 50 for it's label

package gov.anl.ipns.ViewTools.Components.Transparency;

import javax.swing.*; 
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;
import gov.anl.ipns.Util.Numeric.Format;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * This class is used by view components to calibrate a JPanel. Besides
 * calibration, axis labels and graph title are set here. To add grid lines,
 * make a call to editGridLines() to view the AxisEditor.
 */
public class AxisOverlay2D extends OverlayJPanel
{
 /**
  * 0 - This constant primative integer causes neither the x nor y axes to be
  * displayed by the AxisOverlay2D class.
  */
  public static final int NO_AXES = 0;
  
 /**
  * 1 - This constant primative integer causes the x axis to be
  * displayed by the AxisOverlay2D class.
  */
  public static final int X_AXIS = 1;
  
 /**
  * 2 - This constant primative integer causes the y axis to be
  * displayed by the AxisOverlay2D class.
  */
  public static final int Y_AXIS = 2;
  
 /**
  * 3 - This constant primative integer causes both the x and y axes to be
  * displayed by the AxisOverlay2D class.
  */
  public static final int DUAL_AXES = 3;
  
  // these public variables are for preserving axis state information  
 /**
  * "Precision" - This constant String is a key for referencing the state
  * information about the precision of values for this axis overlay. The value
  * that this key references is a primative integer.
  */
  public static final String PRECISION      = "Precision";
    
 /**
  * "Font" - This constant String is a key for referencing the state
  * information about the font of the calibrations for this axis overlay.
  * The value that this key references is of type Font.
  */
  public static final String FONT	    = "Font";
    
 /**
  * "Axes Displayed" - This constant String is a key for referencing the state
  * information about which axes are to be displayed by the axis overlay.
  * The value that this key references is a primative integer. The integer
  * values are specified by public variables NO_AXES, X_AXIS, Y_AXIS, and
  * DUAL_AXES.
  */
  public static final String AXES_DISPLAYED = "Axes Displayed";
    
 /**
  * "X Scale" - This constant String is a key for referencing the state
  * information about the x axis and whether it is log or linear.
  * The value that this key references is an Integer. The
  * values are specified by public variables LINEAR, TRU_LOG, or PSEUDO_LOG,
  * all found in the AxisInfo class.
  */
  public static final String X_SCALE  = "X Scale";
    
 /**
  * "Y Scale" - This constant String is a key for referencing the state
  * information about the y axis and whether it is log or linear.
  * The value that this key references is an Integer. The
  * values are specified by public variables LINEAR, TRU_LOG, or PSEUDO_LOG,
  * all found in the AxisInfo class.
  */
  public static final String Y_SCALE  = "Y Scale";
    
 /**
  * "Two Sided" - This constant String is a key for referencing the state
  * information about the one- or two-sided data calibrated by the overlay. 
  * The value that this key references is a primative boolean. The boolean
  * values are either true for two-sided or false for one-sided.
  */
  public static final String TWO_SIDED      = "Two Sided";
    
 /**
  * "Grid Display X" - This constant String is a key for referencing the state
  * information about the x axis gridlines displayed by the overlay. 
  * The value that this key references is a primative integer. The integer
  * values are either 0 = none, 1 = major, or 2 = major/minor.
  */
  public static final String GRID_DISPLAY_X = "Grid Display X";
    
 /**
  * "Grid Display Y" - This constant String is a key for referencing the state
  * information about the y axis gridlines displayed by the overlay. 
  * The value that this key references is a primative integer. The integer
  * values are either 0 = none, 1 = major, or 2 = major/minor.
  */
  public static final String GRID_DISPLAY_Y = "Grid Display Y";
    
 /**
  * "Grid Color" - This constant String is a key for referencing the state
  * information about the color of gridlines displayed by the overlay. 
  * The value that this key references is of type Color. The Color
  * values come from Sun's Java package.
  */
  public static final String GRID_COLOR     = "Grid Color";
    
 /**
  * "Editor Bounds" - This constant String is a key for referencing the state
  * information about the size and bounds of the Axis Editor window. 
  * The value that this key references is a Rectangle. The Rectangle contains
  * the dimensions for the editor.
  */
  public static final String EDITOR_BOUNDS  = "Editor Bounds";
    
  private static JFrame helper = null;
  
  // these variables simulate the interval of values of the data
  private transient float xmin;
  private transient float xmax;
  private transient float ymin;
  private transient float ymax;
  private transient int xaxis = 0;
  private transient int yaxis = 0;
  private transient int xstart = 0;
  private transient int ystart = 0;
  private transient IAxisAddible component;
  private int precision;
  private Font f;
  private int axesdrawn;
  private int x_scale;
  private int y_scale;
  private boolean isTwoSided = true;
  private int gridxdisplay = 0;  // 0 = none, 1 = major, 2 = major/minor
  private int gridydisplay = 0;
  private transient AxisOverlay2D this_panel;
  private transient AxisEditor editor;
  private Color gridcolor = Color.black;
  private Rectangle editor_bounds = new Rectangle(0,0,400,110);
  
 /**
  * Constructor for initializing a new AxisOverlay2D
  *
  *  @param  iaa - IAxisAddible object
  */ 
  public AxisOverlay2D(IAxisAddible iaa)
  {
    super();
    addComponentListener( new NotVisibleListener() );
    component = iaa;
    f = iaa.getFont();
    xmin = component.getAxisInformation(AxisInfo.X_AXIS).getMin();
    xmax = component.getAxisInformation(AxisInfo.X_AXIS).getMax();
    ymax = component.getAxisInformation(AxisInfo.Y_AXIS).getMin();
    ymin = component.getAxisInformation(AxisInfo.Y_AXIS).getMax();
    setPrecision( iaa.getPrecision() );
    setDisplayAxes( DUAL_AXES );
    setXScale( component.getAxisInformation(AxisInfo.X_AXIS).getScale() );
    setYScale( component.getAxisInformation(AxisInfo.Y_AXIS).getScale() );
    editor = new AxisEditor();
    this_panel = this;
  }
  
 /**
  * Constructor for creating a new AxisOverlay2D with previous state settings.
  *
  *  @param  iaa - IAxisAddible2D object
  *  @param  state - previously saved state
  */ 
  public AxisOverlay2D(IAxisAddible iaa, ObjectState state)
  {
    this(iaa);
    setObjectState(state);
  }

 /**
  * Contains/Displays control information about this overlay.
  */
  public static void help()
  {
    helper = new JFrame("Help for Axis Overlay");
    helper.setBounds(0,0,600,400);
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Description:</H1>" +
                  "<P>The Axis Overlay provides calibration for data. Most " +
        	  "viewers initially have this overlay on, however, turning " +
    		  "it off provides more room for displaying data. This " +
    		  "overlay may also be used for providing grid lines.</P>" +
                  "<H2>Commands for Axis Overlay</H2>" +
                  "<P>Note:<BR>" +
        	  "- The Axis Overlay has no commands associated with it. " +
        	  "Instead, it allows the commands of the underlying image." +
        	  "<BR>- These commands will NOT work if any other overlay " +
    		  "is checked.</P>" +
                  "<H2>Commands for Underlying image <BR>" +
		  "(Without Edit button)</H2>" +
                  "<P>Click/Drag/Release MouseButton2>ZOOM IN<BR>" +
                  "Click/Drag/Release Mouse w/Shift_Key>ZOOM IN ALTERNATE<BR>" +
                  "Double Click Mouse>RESET ZOOM<BR>" +
                  "Single Click Mouse>SELECT CURRENT POINT</P>" +
		  "<H2>Commands for AxisEditor<BR>" +
		  "(Edit button under Axis Overlay)</H2>" +
                  "<P>Use drop-down box to choose GRID OPTIONS for X " +
        	  "and/or Y axis.<BR>" +
                  "Click on \"Change Grid Color\" button to " +
        	  "CHANGE GRID COLOR for both X and Y axis.</P>";
    textpane.setText(text);
    JScrollPane scroll = new JScrollPane(textpane);
    scroll.setVerticalScrollBarPolicy(
 				    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    WindowShower shower = new WindowShower(helper);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
  
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(PRECISION);
    if( temp != null )
    {
      precision = ((Integer)temp).intValue();
      redraw = true;  
    }
    
    temp = new_state.get(FONT);
    if( temp != null )
    {
      f = (Font)temp;
      redraw = true;  
    }  
    
    temp = new_state.get(AXES_DISPLAYED);
    if( temp != null )
    {
      axesdrawn = ((Integer)temp).intValue(); 
      redraw = true;  
    }  
    
    temp = new_state.get(X_SCALE);
    if( temp != null )
    {
      x_scale = ((Integer)temp).intValue(); 
      redraw = true;  
    } 
    
    temp = new_state.get(Y_SCALE); 
    if( temp != null )
    {
      y_scale = ((Integer)temp).intValue();
      redraw = true;  
    }  
    
    temp = new_state.get(TWO_SIDED);
    if( temp != null )
    {
      isTwoSided = ((Boolean)temp).booleanValue();
      redraw = true;  
    }  
    
    temp = new_state.get(GRID_DISPLAY_X);
    if( temp != null )
    {
      gridxdisplay = ((Integer)temp).intValue(); 
      redraw = true;  
    } 
    
    temp = new_state.get(GRID_DISPLAY_Y);
    if( temp != null )
    {
      gridydisplay = ((Integer)temp).intValue();
      redraw = true;  
    }  
    
    temp = new_state.get(GRID_COLOR);
    if( temp != null )
    {
      gridcolor = (Color)temp;
      redraw = true;  
    }  
    
    temp = new_state.get(EDITOR_BOUNDS);
    if( temp != null )
    {
      editor_bounds = (Rectangle)temp;
      editor.setBounds( editor_bounds );  
    }
    
    if( redraw )
      this_panel.repaint();
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
    ObjectState state = new ObjectState();
    state.insert( AXES_DISPLAYED, new Integer(axesdrawn) );
    state.insert( X_SCALE, new Integer(x_scale) );
    state.insert( Y_SCALE, new Integer(y_scale) );
    state.insert( FONT, f );
    state.insert( GRID_COLOR, gridcolor );
    state.insert( GRID_DISPLAY_X, new Integer(gridxdisplay) );
    state.insert( GRID_DISPLAY_Y, new Integer(gridydisplay) );
    state.insert( PRECISION, new Integer(precision) );
    state.insert( EDITOR_BOUNDS, editor_bounds );
    
    // load these for project specific instances.
    if( !isDefault )
    {
      state.insert( TWO_SIDED, new Boolean(isTwoSided) );
    }
    
    return state;
  }
 
 /**
  * Sets the significant digits to be displayed.
  *
  *  @param digits
  */ 
  public void setPrecision( int digits )
  {
    precision = digits;
  } 
  
 /**
  * Specify which axes should be drawn. Options are NO_AXES, X_AXIS, Y_AXIS,
  * or DUAL_AXES.
  *
  *  @param  display_scheme
  */ 
  public void setDisplayAxes( int display_scheme )
  {
    axesdrawn = display_scheme;
  }
  
 /**
  * Specify whether the data is one-sided or two-sided. This will affect how
  * logarithmic calibrations are done.
  *
  *  @param  doublesided - true if two-sided
  */ 
  public void setTwoSided( boolean doublesided )
  {
    isTwoSided = doublesided;
  }
  
 /**
  * Specify x axis as linear or logarithmic. Options are LINEAR, TRU_LOG, or
  * PSEUDO_LOG. Use the AxisInfo public variables to define the scale.
  *
  *  @param  xscale
  *  @see gov.anl.ipns.ViewTools.Components.AxisInfo
  */ 
  public void setXScale( int xscale )
  {
    x_scale = xscale;
  }   
  
 /**
  * Specify y axis as linear or logarithmic. Options are LINEAR, TRU_LOG, or
  * PSEUDO_LOG. Use the AxisInfo public variables to define the scale.
  *
  *  @param  yscale
  *  @see gov.anl.ipns.ViewTools.Components.AxisInfo
  */ 
  public void setYScale( int yscale )
  {
    y_scale = yscale;
  }   
 
 /**
  * This method displays the AxisEditor.
  */ 
  public void editGridLines()
  {
    if( editor.isVisible() )
    {
      editor.toFront();
      editor.requestFocus();
    }
    else
    { 
      editor_bounds = editor.getBounds(); 
      editor.dispose();
      editor = new AxisEditor();
      WindowShower shower = new WindowShower(editor);
      java.awt.EventQueue.invokeLater(shower);
      shower = null;
      editor.toFront();
    }
  }
  
 /**
  * This method sets the color for the grid lines. Initially set to black.
  *
  *  @param  color
  */
  public void setGridColor( Color color )
  {
    gridcolor = color;
    this_panel.repaint();
  }
     
 /**
  * This method is called by to inform the overlay that it is no
  * longer needed. In turn, the overlay closes all windows created
  * by it before closing.
  */ 
  public void kill()
  {
    editor.dispose();
    if( helper != null )
      helper.dispose();
  }
 
 /**
  * This method creates tick marks and numbers for this transparency.
  * These graphics will overlay onto a jpanel.
  *
  *  @param  g - graphics object
  */  
  public void paint(Graphics g) 
  {  
    Graphics2D g2d = (Graphics2D)g;
    g2d.setFont(f);
    FontMetrics fontdata = g2d.getFontMetrics();
    // Reset precision, make sure it is always consistent.
    setPrecision( component.getPrecision() );
    
    xmin = component.getAxisInformation(AxisInfo.X_AXIS).getMin();
    xmax = component.getAxisInformation(AxisInfo.X_AXIS).getMax();
    
    // ymin & ymax swapped to adjust for axis standard
    ymax = component.getAxisInformation(AxisInfo.Y_AXIS).getMin();
    ymin = component.getAxisInformation(AxisInfo.Y_AXIS).getMax();
    
    // get the dimension of the center panel (imagejpanel)
    xaxis = (int)( component.getRegionInfo().getWidth() );
    yaxis = (int)( component.getRegionInfo().getHeight() );
    // x and y coordinate for upper left hand corner of component
    xstart = (int)( component.getRegionInfo().getLocation().getX() );
    ystart = (int)( component.getRegionInfo().getLocation().getY() );

    // draw title on the overlay if one exists
    if( component.getTitle() != IVirtualArray2D.NO_TITLE )
      g2d.drawString( component.getTitle(), xstart + xaxis/2 -
    		   fontdata.stringWidth(component.getTitle())/2, 
    		   ystart/2 + (fontdata.getHeight())/2 );
    
    // Check to make sure the x axis should be drawn.
    if( axesdrawn == X_AXIS || axesdrawn == DUAL_AXES )
    {
      // Linear axis.
      if( x_scale == AxisInfo.LINEAR )
    	paintLinearX( g2d );
      // Tru-log axis.
      else if( x_scale == AxisInfo.TRU_LOG )
      {
    	paintTruLogX(g2d);
      }
      // Pseudo-log axis.
      else if( x_scale == AxisInfo.PSEUDO_LOG )
      {
    	paintLogX( g2d );
      }
    }
    if( axesdrawn == Y_AXIS || axesdrawn == DUAL_AXES )
    {
      // Linear Axis
      if( y_scale == AxisInfo.LINEAR )
    	paintLinearY( g2d );
      // Tru-log axis
      else if( y_scale == AxisInfo.TRU_LOG )
      {
    	paintTruLogY(g2d);
      }
      // Pseudo-log axis.
      else if( y_scale == AxisInfo.PSEUDO_LOG )
      {
    	paintLogY( g2d );
      }
    }
    super.paint(g);
  } // end of paint()
  
 /* ***********************Private Methods************************** */
 
 /*
  * This will display the labels, units, and common exponent (if not 0).
  * if the string is specifically the AxisInfo.NO_LABEL or AxisInfo.NO_UNITS
  * strings then no strings will be displayed.
  *
  *  @param num The String number with exponent, ex: 1E3
  *  @param axis Use axis codes above to determin axes, either X or Y
  */
  private void paintLabelsAndUnits( String num, int axis, 
                                    boolean log, Graphics2D g2d )
  {
    // true if "(" has been appended but not ")"
    boolean open_parenthesis = false;
    // Display will appear as such: "Label (10^x Units)"
    FontMetrics fontdata = g2d.getFontMetrics(); 
    if( axis == X_AXIS )
    {
      StringBuffer xlabel = new StringBuffer("");
      // if no label string, or empty label append nothing
      if( !component.getAxisInformation(AxisInfo.X_AXIS).getLabel().equals( 
    	  AxisInfo.NO_LABEL) &&
	  !component.getAxisInformation(AxisInfo.X_AXIS).getLabel().equals( 
	  "") )
    	xlabel.append( component.getAxisInformation(AxisInfo.X_AXIS).getLabel()
		       + "  " );
      // Since log calibrations carry their own exponent, none is required
      if( !log )
      { 
        int exp_index = num.lastIndexOf('E');       
        if( Integer.parseInt( num.substring( exp_index + 1) ) != 0 )
	{
    	  xlabel.append( "( 10^" + num.substring( exp_index + 1 ) + " ");
	  open_parenthesis =  true;
        }
      }
      if( !component.getAxisInformation(AxisInfo.X_AXIS).getUnits().equals(
    	  AxisInfo.NO_UNITS) &&
	  !component.getAxisInformation(AxisInfo.X_AXIS).getUnits().equals( 
	  "") )
      {
        if( !open_parenthesis )
	{
	  xlabel.append("( ");
	  open_parenthesis = true;
    	}
	xlabel.append( component.getAxisInformation(AxisInfo.X_AXIS).getUnits()
	               + " )");
        open_parenthesis = false;
      }
      // make sure there is always a last parenthesis
      if( open_parenthesis )
        xlabel.append(")");
      if( !xlabel.toString().equals("") )
    	g2d.drawString( xlabel.toString(), xstart + xaxis/2 -
    		  fontdata.stringWidth(xlabel.toString())/2, 
    		  yaxis + ystart + fontdata.getHeight() * 2 + 6 );
    }
    else if( axis == Y_AXIS )
    {
      open_parenthesis = false; // reset and use again.
      StringBuffer ylabel = new StringBuffer("");
      if( !component.getAxisInformation(AxisInfo.Y_AXIS).getLabel().equals( 
    	  AxisInfo.NO_LABEL) &&
	  !component.getAxisInformation(AxisInfo.Y_AXIS).getLabel().equals( 
	  "") )
    	ylabel.append( component.getAxisInformation(AxisInfo.Y_AXIS).getLabel()
		       + "  " );
      // Since log calibrations carry their own exponent, none is required
      if( !log )
      { 
        int exp_index = num.lastIndexOf('E');       
        if( Integer.parseInt( num.substring( exp_index + 1) ) != 0 )
	{
    	  ylabel.append( "( 10^" + num.substring( exp_index + 1 ) + " ");
	  open_parenthesis =  true;
        }
      }
      if( !component.getAxisInformation(AxisInfo.Y_AXIS).getUnits().equals(
    	  AxisInfo.NO_UNITS) &&
	  !component.getAxisInformation(AxisInfo.Y_AXIS).getUnits().equals( 
	  "") )
      {
        if( !open_parenthesis )
	{
	  ylabel.append("( ");
	  open_parenthesis = true;
    	}
	ylabel.append( component.getAxisInformation(AxisInfo.Y_AXIS).getUnits()
	               + " )");
        open_parenthesis = false;
      }
      // draw the label rotated along y-axis      
      if( !ylabel.toString().equals("") )
      {
    	g2d.rotate( -Math.PI/2, xstart, ystart + yaxis );    
    	g2d.drawString( ylabel.toString(), xstart + yaxis/2 -
    			fontdata.stringWidth(ylabel.toString())/2, 
    			yaxis + ystart - xstart + fontdata.getHeight() );
    	g2d.rotate( Math.PI/2, xstart, ystart + yaxis );
      }
    }
  }
  

 /*
  * This code will eliminate trailing zeros after the decimal point. If
  * the display of numbers is to be a consistent length, comment the body of
  * this method out.
  */
  private String removeTrailingZeros( String string_num )
  { 
    int decimal_index = string_num.indexOf('.');
    int char_index = string_num.length();
    // If no decimal or the last zero is not after the decimal, no trailing
    // zeroes exist.
    if( string_num.lastIndexOf('0') < decimal_index ||
   	decimal_index < 0 )
      return string_num;
    
    // If a decimal exists, trim any trailing zeros.
    while( char_index > decimal_index && 
   	   ( string_num.endsWith("0") || string_num.endsWith(".") ) )
    {
      char_index = char_index - 1;
      string_num = string_num.substring(0,char_index);
    }
    return string_num;
  }
  
 /*
  * Draw the x axis with horizontal numbers and ticks spaced linearly.
  */   
  private void paintLinearX( Graphics2D g2d )
  {
    FontMetrics fontdata = g2d.getFontMetrics();
    
    // info for putting tick marks and numbers on transparency   
    String num = "";
    int xtick_length = 5;

    CalibrationUtil util = new CalibrationUtil( xmin, xmax, precision, 
        					Format.ENGINEER );
    float[] values = util.subDivide();
    float step = values[0];
    float start = values[1];	// the power of the step
   
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
    //boolean drawn = false;
    int prepix = (int)( (float)xaxis*(start - xmin)/
        		(xmax-xmin) + xstart); 
    int skip = 0;
    // Step through and draw x axis tick marks.
    for( int steps = 0; steps < numxsteps; steps++ )
    {  
      A = (float)steps*step + start;		   
      pixel = (int)( 
              (float)xaxis*(A - xmin)/
              (xmax-xmin) + xstart);	 
  
      subpixel = (int)( 
              ( (float)xaxis*(A - xmin - step/2 ) )/
              (xmax-xmin) + xstart);	  
      // Going to be used to increment the numbers on the bottem of the graph
      // the way that they are supposed to be numbered.
      num = util.standardize( (step * (float)steps + start) );
      exp_index = num.lastIndexOf('E');        

      // determine a nice spacing for the labels.
      if( (prepix + 2 + 
           fontdata.stringWidth(num.substring(0,exp_index))/2) >
          (pixel - fontdata.stringWidth(num.substring(0,exp_index))/2) )
      {
        skip++;
      }
      // draw evenly spaced numeric labels.
      if( steps%skip == 0 )
      { 
		
        String temp_num = removeTrailingZeros( num.substring(0,exp_index) );
        g2d.drawString( temp_num, 
             pixel - fontdata.stringWidth(temp_num)/2, 
             yaxis + ystart + xtick_length + fontdata.getHeight() );
             
      
      } 	


      // draw minor tick marks (subpixel refers to minor tick mark)
      if( subpixel > xstart && subpixel < (xstart + xaxis) )
      {  

        if( gridxdisplay == 2 )
        { 
          // first draw gridlines in their color
          g2d.setColor( gridcolor );
          g2d.drawLine( subpixel, ystart, subpixel, yaxis + ystart );
          
          // then draw tick mark in black
          g2d.setColor( Color.black );
        }
        //always draw tick marks, minor tick marks
        g2d.drawLine( subpixel, yaxis + ystart, 
        	      subpixel, yaxis + ystart + xtick_length-2 );
      }
      
      // to draw grid line for major ticks
      if( gridxdisplay == 1 || gridxdisplay == 2 )
      {  
        // first draw gridlines in their color
        g2d.setColor( gridcolor );
        g2d.drawLine( pixel, ystart, pixel, yaxis + ystart );
        
        // then draw tick mark in black
        g2d.setColor( Color.black );
      }

      g2d.drawLine( pixel, yaxis + ystart, 
        	    pixel, yaxis + ystart + xtick_length );  
      //System.out.println("Y Position: " + (yaxis + ystart + 
      //				     xtick_length) );
      // if last step but another tick should be drawn
      if( steps == (numxsteps - 1) && 
          ( xaxis + xstart - pixel) > xaxis/(2*numxsteps) )
      { 
        // draw gridlines for subtick after last major tick
        if( gridxdisplay == 2 )
        {  
          // first draw gridlines in their color
          g2d.setColor( gridcolor );
          g2d.drawLine( pixel + (pixel - subpixel), ystart, 
        		pixel + (pixel - subpixel), 
        		yaxis + ystart );
          
          // then draw tick mark in black
          g2d.setColor( Color.black );
        }
	// draw major tickmarks		
        g2d.drawLine( pixel + (pixel - subpixel), yaxis + ystart, 
        	      pixel + (pixel - subpixel), 
        	      yaxis + ystart + xtick_length-2 );
        steps++;
        A = (float)steps*step + start;  	  
        pixel = (int)( (float)xaxis*(A - xmin)/(xmax-xmin) + xstart); 
        
	if( steps%skip == 0 && pixel <= (xstart + xaxis) )
        {      
          num = util.standardize( (step * (float)steps + start) );
          exp_index = num.lastIndexOf('E');
          String temp_num = removeTrailingZeros( num.substring(0,exp_index) );
          g2d.drawString( temp_num,
	       pixel - fontdata.stringWidth(temp_num)/2, 
               yaxis + ystart + xtick_length + fontdata.getHeight() );
          // to draw grid line for major ticks
          if( gridxdisplay == 1 || gridxdisplay == 2 )
          {   
            // first draw gridlines in their color
            g2d.setColor( gridcolor );
            g2d.drawLine( pixel, ystart, pixel, yaxis + ystart );
          
            // then draw tick mark in black
            g2d.setColor( Color.black );
          }
          g2d.drawLine( pixel, yaxis + ystart, 
                	pixel, yaxis + ystart + xtick_length );
        }    
      }   
    } // end of for
    paintLabelsAndUnits( num, X_AXIS, false, g2d );
  }
  
 /*
  * Draw the y axis with horizontal numbers and ticks spaced linearly.
  */
  private void paintLinearY( Graphics2D g2d )
  {
    FontMetrics fontdata = g2d.getFontMetrics();   
    String num = "";

    CalibrationUtil yutil = new CalibrationUtil( ymin, ymax, precision, 
        					 Format.ENGINEER );
    float[] values = yutil.subDivide();
    float ystep = values[0];
    float starty = values[1];
    int numysteps = (int)values[2];
    
    //   System.out.println("Y Start/Step = " + starty + "/" + ystep);
    int ytick_length = 5;     // the length of the tickmark is 5 pixels
    int ypixel = 0;	      // where to place major ticks
    int ysubpixel = 0;        // where to place minor ticks
      
    int exp_index = 0;
               
    float pmin = ystart + yaxis;
    float pmax = ystart;
    float a = 0;
    float amin = ymin - starty;
    
    // yskip is the space between calibrations: 1 = every #, 2 = every other
    
    int yskip = 1;
    while( (yaxis*yskip/numysteps) < 
           fontdata.getHeight() && yskip < numysteps)
       yskip++;
    int rem = numysteps%yskip;

    for( int ysteps = numysteps - 1; ysteps >= 0; ysteps-- )
    {	
      a = ysteps * ystep;
    
      ypixel = (int)( (pmax - pmin) * ( a - amin) /
        	      (ymax - ymin) + pmin);
  //       System.out.println("YPixel " + ypixel ); 

      //System.out.println("Ymin/Ymax " + ymin + "/" + ymax );
    
      ysubpixel = (int)( (pmax - pmin) * ( a - amin  + ystep/2 ) /
        	      (ymax - ymin) + pmin); 
    
      num = yutil.standardize(ystep * (float)ysteps + starty);
      exp_index = num.lastIndexOf('E');

      /*
      System.out.println("Ypixel/Pmin = " + ypixel + "/" + pmin );
      System.out.println("Ypixel/Pmax = " + ypixel + "/" + pmax );
      System.out.println("Num = " + num );
      */
      // if pixel is between top and bottom of imagejpanel, draw it  
      if( ypixel <= pmin && ypixel >= pmax )
      {
        if( ((float)(ysteps-rem)/(float)yskip) == ((ysteps-rem)/yskip) )
        {
	  String temp_num = removeTrailingZeros( num.substring(0,exp_index) );
          g2d.drawString( temp_num,
          	    xstart - ytick_length - 
          	    fontdata.stringWidth(temp_num),
          	    ypixel + fontdata.getHeight()/4 );
        }	       
        
        // paint gridlines for major ticks
        if( gridydisplay == 1 || gridydisplay == 2 )
        {
          // change color for grid painting
          g2d.setColor(gridcolor);
          g2d.drawLine( xstart - 1, ypixel - 1, 
                	xstart + xaxis - 1, ypixel - 1 );   
          // change color for tick marks
          g2d.setColor(Color.black);
        }
        g2d.drawLine( xstart - ytick_length, ypixel - 1, 
                      xstart - 1, ypixel - 1 );   
      }
      // if subpixel is between top and bottom of imagejpanel, draw it
      if( ysubpixel <= pmin && ysubpixel >= pmax )
      {
        // paint gridlines for minor ticks
        if( gridydisplay == 2 )
        {
          // change color for grid painting
          g2d.setColor(gridcolor);
          g2d.drawLine( xstart - 1, ysubpixel - 1, 
                	xstart + xaxis - 1, ysubpixel - 1 );   
          // change color for tick marks
          g2d.setColor(Color.black);
        }
        g2d.drawLine( xstart - (ytick_length - 2), ysubpixel - 1, 
                      xstart - 1, ysubpixel - 1 );
      }
      // if a tick mark should be drawn at the end, draw it
      // since the above "if" takes care of all subtick marks before the
      // actual numbered ticks, there may be a tick mark needed after the 
      // last tick. ( end refers to smallest y value )
      if( ysteps == 0 && 
          (pmin - ypixel) > yaxis/(2*numysteps) ) 
      {
        // paint gridlines for major ticks
        if( gridydisplay == 2 )
        {
          // change color for grid painting
          g2d.setColor(gridcolor);
          g2d.drawLine( xstart - 1, (int)(ysubpixel + 
           ( (pmin - pmax) * ystep / (ymax - ymin) ) ), xstart + xaxis - 1, 
           (int)( ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin))));  
          // change color for tick marks
          g2d.setColor(Color.black);
        }
        g2d.drawLine( xstart - (ytick_length - 2), (int)(ysubpixel + 
           ( (pmin - pmax) * ystep / (ymax - ymin) ) ), xstart - 1, 
           (int)( ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin))));
      }
    }
    paintLabelsAndUnits( num, Y_AXIS, false, g2d );
  } // end of paintLinearY()
  
 /*
  * Draw the x axis with horizontal numbers and ticks spaced logarithmically.
  * This method is for pseudo-log scaling.
  */   
  private void paintLogX( Graphics2D g2d )
  {
    if( component instanceof IPseudoLogAxisAddible )
    {
    
      IPseudoLogAxisAddible logcomponent = (IPseudoLogAxisAddible)component;
      FontMetrics fontdata = g2d.getFontMetrics(); 
      String num = "";
      int TICK_LENGTH = 5;
      int xtick_length = 0;
      int negtick_length = 0;
      //isTwoSided = false;
      CalibrationUtil util = new CalibrationUtil( xmin, xmax, precision, 
        					  Format.ENGINEER );
      util.setTwoSided(isTwoSided);
      float[] values = util.subDivideLog();
      int numxsteps = values.length;	    
  //  System.out.println("X ticks = " + numxsteps );	    
      int pixel = 0;
      float A = 0; 
      int tempprec = 3;
      if( xmax/xmin < 10 )
        tempprec = precision;
      // Draw tick marks for a one-sided color model
      if( !isTwoSided )
      {
        A = values[0];

        LogScaleUtil logger = new LogScaleUtil( 1, xaxis,xmin+1, xmax + 1);

        double logscale = logcomponent.getLogScale();
    	int division = 0;    // 0-5, divisions in the xaxis.
    	// rightmost pixel coord of last label drawn
    	int last_drawn = -xstart;
    	int last_tick = 0;    // position of last tick mark drawn  
       
    	// find division where the first label is to be drawn
    	while( (int)logger.toSource(A, logscale) >= 
    	       (int)(xaxis/5 * (division + 1) ) ){
    	  division++;}
       
       

    	for( int steps = 0; steps < numxsteps; steps++ )
        { // 
          A = values[steps];
    	

    	  pixel = xstart + (int)logger.toSource(A, logscale);

    	
          num = Format.choiceFormat( A, Format.SCIENTIFIC, tempprec );

    	  //g2d.setColor(Color.black);
          xtick_length = TICK_LENGTH;
    	  // divide axis into 5, if next tick is in the next fifth, show
    	  // the number
          if( (pixel - xstart) >= (int)(xaxis/5 * division) )
    	  {
    	    // if this label does not interfer with the label before it
            if( last_drawn < (pixel - fontdata.stringWidth(num)) )
    	    {
	      String temp_num = removeTrailingZeros(num);
	      /*System.out.println("int1 = " + 
	           (pixel - fontdata.stringWidth(temp_num)/2));
	      System.out.println("int2 = " + 
	           (yaxis+ystart+TICK_LENGTH+fontdata.getHeight()));*/
    	      g2d.drawString( temp_num,
	        pixel - fontdata.stringWidth(temp_num)/2, 
               yaxis + ystart + TICK_LENGTH + fontdata.getHeight() );
      
    	      last_drawn = pixel + fontdata.stringWidth(num)/2;
    	      if( A != 0 )
                division++;
    	      xtick_length += 3;
            }
    	  }
          // make sure ticks are at least 5 pixels apart.
    	  if( last_tick + 5 < pixel || xtick_length == TICK_LENGTH + 3 )
    	  {
    	    // paint gridlines for major ticks
            if( xtick_length == TICK_LENGTH + 3 &&
    		(gridxdisplay == 1 || gridxdisplay == 2) )
    	    {
    	      // change color for grid painting
    	      g2d.setColor(gridcolor);
    	      g2d.drawLine( pixel, ystart, 
        		    pixel, yaxis + ystart );
    	      // change back to black for tick marks
    	      g2d.setColor(Color.black);
    	    }
    	    // paint gridlines for minor ticks
            if( xtick_length == TICK_LENGTH && gridxdisplay == 2 )
    	    {
    	      // change color for grid painting
    	      g2d.setColor(gridcolor);
    	      g2d.drawLine( pixel, ystart, 
        		    pixel, yaxis + ystart );
  
        	
    	      // change back to black for tick marks
    	      g2d.setColor(Color.black);
    	    }
    	    // only paint tick marks
    	    g2d.drawLine( pixel, yaxis + ystart, 
        		  pixel, yaxis + ystart + xtick_length ); 
        
    	    last_tick = pixel; 
          }
    	  // draw xmax if no numbers are near the end of the calibration
    	  if( steps == numxsteps - 1 )
    	  {
            A = xmax;
    	    pixel = xstart + (int)logger.toSource(A, logscale);

            num = Format.choiceFormat( A, Format.SCIENTIFIC, tempprec );
            if( last_drawn < 
    	        pixel - fontdata.stringWidth(num)/2 )
    	    {
	      String temp_num = removeTrailingZeros(num);
    	      g2d.drawString( temp_num,
	        pixel - fontdata.stringWidth(temp_num)/2, 
            	yaxis + ystart + TICK_LENGTH + fontdata.getHeight() );
    	    
    	      // paint gridlines for major ticks
              if( gridxdisplay == 1 || gridxdisplay == 2 )
    	      {
    	    	// change to grid color
    	    	g2d.setColor(gridcolor);
    	    	g2d.drawLine( pixel, ystart, 
            		      pixel, yaxis + ystart );
    	    	// change back to black for tick marks
    	    	g2d.setColor(Color.black);
    	      }
    	      g2d.drawLine( pixel, yaxis + ystart, 
            		    pixel, yaxis + ystart + TICK_LENGTH + 3 );
    	    }
    	  }
    	  // debug axis divider
    	  /*g2d.setColor(Color.red);
    	  for( int i = 0; i <= 5; i++ )
    	     g2d.drawLine( xstart + xaxis*i/5, yaxis + ystart, xstart + 
    			   xaxis*i/5, yaxis + ystart + xtick_length );*/
    	} // end of for
      } // end of if( !isTwoSided )
      // draw tickmarks for a two-sided color model
      else
      {
        A = values[0];
        LogScaleUtil logger = new LogScaleUtil( 0,(int)(xaxis/2),
    					       0, xmax + 1);
        double logscale = logcomponent.getLogScale();

        int neg_pixel = 0;
    	String neg_num = "";
    	int division = 5;     // 5-10, division # in the xaxis.
    	int neg_division = 5; // 5-0 , division # in the xaxis
    	int last_drawn = 0;   // rightmost pixel coord of last label drawn
    	int last_neg_drawn = xaxis;// leftmost pixel coords of last neg. label
    	int first_drawn = 0;  // the leftmost pixel coords of first pos. label
    	int last_tick = 0;    // last tick mark drawn
    	int last_neg_tick = (int)xmax;// last negative tick mark drawn
    	// find division where the first label is to be drawn
    	while( (int)logger.toSource(A, logscale) >= 
    	       (int)(xaxis/10 * (division + 1) ) )
    	  division++;
    	// find division where the first negative label is to be drawn
    	while( (int)logger.toSource(A, logscale) >= 
    	       (int)(xaxis/10 * (10 - (neg_division-1) ) ) )
    	  neg_division--;
       
    	for( int steps = 0; steps < numxsteps; steps++ )
        { 
          A = values[steps];
           
     //System.out.println("here" + xmin + "/" + xmax + "/" + steps + "/" + A);
    	  pixel = (int)(xstart + xaxis/2 + (int)logger.toSource(A,logscale) );
          neg_pixel = (int)(xstart + xaxis/2 - 
    		      (int)logger.toSource(A, logscale) );
    	  num = Format.choiceFormat( A, Format.SCIENTIFIC, tempprec );
    	  neg_num = Format.choiceFormat( -A, Format.SCIENTIFIC, tempprec );
    	    
    	  if( A == 0 )
    	    first_drawn = pixel - fontdata.stringWidth(num)/2;    

    	  //g2d.setColor(Color.black);
    	  xtick_length = TICK_LENGTH;
    	  negtick_length = TICK_LENGTH;
          // this will handle all positive labels
    	  if( (pixel - xstart) >= (int)(xaxis/10 * division) )
    	  {
    	    // if this label does not interfer with the label before it
            if( last_drawn < 
    	        pixel - fontdata.stringWidth(num)/2 ) 
    	    {
	      String temp_num = removeTrailingZeros(num);
    	      g2d.drawString( temp_num,
	        pixel - fontdata.stringWidth(temp_num)/2, 
            	yaxis + ystart + xtick_length + fontdata.getHeight() );
            
    	      last_drawn = pixel + fontdata.stringWidth(num)/2;
    	      division++;
    	      xtick_length += 3;
    	    }
    	  }
    	  // this will handle all negative labels, if a negative label
    	  // interfers with a positive label, don't display the negative label
    	  if( (neg_pixel - xstart) <= (int)(xaxis/10 * neg_division) )
    	  {
    	    if( first_drawn > (neg_pixel + fontdata.stringWidth(neg_num)/2) &&
    	        last_neg_drawn > (neg_pixel + fontdata.stringWidth(neg_num)/2) )
    	    {
	      String temp_num = removeTrailingZeros(neg_num);
    	      g2d.drawString( temp_num,
	        neg_pixel - fontdata.stringWidth(temp_num)/2, 
                yaxis + ystart + negtick_length + fontdata.getHeight() ); 
    	    
    	      last_neg_drawn = neg_pixel - 
    	        fontdata.stringWidth(neg_num)/2;
    	      neg_division--;
    	      negtick_length += 3;
            }
    	  }
    	  // this if will "weed out" tick marks close to each other
    	  if( last_tick + 5 < pixel || xtick_length == (TICK_LENGTH + 3) )
    	  {
    	    // paint gridlines for major ticks
            if( xtick_length == TICK_LENGTH + 3 &&
    		(gridxdisplay == 1 || gridxdisplay == 2) )
    	    {
    	      // change color for grid painting
    	      g2d.setColor(gridcolor);
    	      g2d.drawLine( pixel, ystart, pixel, yaxis + ystart );
    	      // change color to black for tick marks
    	      g2d.setColor(Color.black);
    	    }
    	    // paint gridlines for minor ticks
            if( xtick_length == TICK_LENGTH && gridxdisplay == 2 )
    	    {
    	      // change color for grid painting
    	      g2d.setColor(gridcolor);
    	      g2d.drawLine( pixel, ystart, pixel, yaxis + ystart );
    	      // change color to black for tick marks
    	      g2d.setColor(Color.black);
    	    }
    	    // only paint tick marks
            g2d.drawLine( pixel, yaxis + ystart, 
        		  pixel, yaxis + ystart + xtick_length );
    	    last_tick = pixel; 
          }
    	  // this if will "weed out" neg tick marks close to each other
    	  if( last_neg_tick - 5 > neg_pixel ||
	      negtick_length == (TICK_LENGTH + 3) )
    	  {
    	    // paint gridlines for major negative ticks
            if( negtick_length == TICK_LENGTH + 3 &&
    		(gridxdisplay == 1 || gridxdisplay == 2) )
    	    {
    	      // change color for grid painting
    	      g2d.setColor(gridcolor);
    	      g2d.drawLine( neg_pixel, ystart, neg_pixel, yaxis + ystart );
    	      // change color to black for tick marks
    	      g2d.setColor(Color.black);
    	    }
    	    // paint gridlines for minor ticks
            if( negtick_length == TICK_LENGTH && gridxdisplay == 2 )
    	    {
    	      // change color for grid painting
    	      g2d.setColor(gridcolor);
    	      g2d.drawLine( neg_pixel, ystart, neg_pixel, yaxis + ystart );
    	      // change color to black for tick marks
    	      g2d.setColor(Color.black);
    	    }
    	    // only paint tick marks
    	    g2d.drawLine( neg_pixel, yaxis + ystart, 
        		  neg_pixel, yaxis + ystart + negtick_length );
    	    last_neg_tick = neg_pixel;
          }
    	  // draw xmax if no numbers are near the end of the calibration
    	  if( steps == numxsteps - 1 )
    	  {
    	    xtick_length = TICK_LENGTH;
    	    negtick_length = TICK_LENGTH;
            A = xmax;
    	    pixel = (int)(xstart + xaxis/2 + 
    	            (int)logger.toSource(A,logscale) );
            neg_pixel = (int)(xstart + xaxis/2 - 
    	        	(int)logger.toSource(A, logscale) );
    	    num = Format.choiceFormat( A, Format.SCIENTIFIC, tempprec );
    	    neg_num = Format.choiceFormat( -A, Format.SCIENTIFIC, tempprec );
            if( last_drawn < pixel - fontdata.stringWidth(num)/2 )
    	    {
	      String temp_num = removeTrailingZeros(num);
    	      g2d.drawString( temp_num,
	        pixel - fontdata.stringWidth(temp_num)/2, 
                yaxis + ystart + TICK_LENGTH + fontdata.getHeight() );
    	      xtick_length += 3;
    	    }
    	    if( last_neg_drawn > (neg_pixel +
                fontdata.stringWidth(neg_num)/2) )
    	    {
	      String temp_num = removeTrailingZeros(neg_num);
    	      g2d.drawString( temp_num, 
    	        neg_pixel - fontdata.stringWidth(temp_num)/2, 
                yaxis + ystart + negtick_length + fontdata.getHeight() ); 
              negtick_length += 3;
    	    }
    	    g2d.drawLine( pixel, yaxis + ystart, 
                	  pixel, yaxis + ystart + xtick_length );  
    	    g2d.drawLine( neg_pixel, yaxis + ystart, 
                	  neg_pixel, yaxis + ystart + negtick_length );
    	  }
          // debug axis divider
    	  /*g2d.setColor(Color.red);
    	  for( int i = 0; i <= 10; i++ )
    	     g2d.drawLine( xstart + xaxis*i/10, yaxis + ystart, xstart + 
    			   xaxis*i/10, yaxis + ystart + TICK_LENGTH );*/
    	} // end of for
      } // end of else (isTwoSided)
      paintLabelsAndUnits( num, X_AXIS, true, g2d );  
    } // end if( instanceof)
    else
      System.out.println("Instance of IPseudoLogAxisAddible needed " +
    			 "in AxisOverlay2D.java");
  }
  
 /*
  * Draw the y axis with horizontal numbers and ticks spaced logarithmically.
  * This method is used for pseudo-log axes.
  */	
  private void paintLogY( Graphics2D g2d )
  {
    if( component instanceof IPseudoLogAxisAddible )
    {
      IPseudoLogAxisAddible logcomponent = (IPseudoLogAxisAddible)component;
      FontMetrics fontdata = g2d.getFontMetrics();   
      String num = "";
      int TICK_LENGTH = 5;
      //isTwoSided = false;
      CalibrationUtil yutil = new CalibrationUtil( ymin, ymax, precision, 
						   Format.ENGINEER );
      yutil.setTwoSided(isTwoSided);
      float[] values = yutil.subDivideLog();
      int numysteps = values.length;
     
      //   System.out.println("Y Start/Step = " + starty + "/" + ystep);
      int ytick_length = 5;    // the length of the tickmark is 5 pixels
      int ypixel = 0;	       // where to place major ticks
      int tempprec = 3;
      if( xmax/xmin < 10 )
	tempprec = precision;
		
      float a = 0;
      // Draw tick marks for a one-sided color model
      if( !isTwoSided )
      {

	a = values[0];
	LogScaleUtil logger = new LogScaleUtil( 0, yaxis, ymax, ymin + 1);
	double logscale = logcomponent.getLogScale();
        int division = 0;    // 0-5, divisions in the yaxis.
        // top pixel coord of last label drawn
        int last_drawn = yaxis + ystart + fontdata.getHeight(); 
        int last_tick = last_drawn + 6;
        // find division where the first label is to be drawn
        while( (int)logger.toSource(a, logscale) >= 
               (int)(yaxis/5 * (division + 1) ) )
           division++;
  //   System.out.println("numysteps/yskip: (" + numysteps + "/" + yskip + 
  //			  ") = " + mult + "R" + rem);
        for( int ysteps = 0; ysteps < numysteps; ysteps++ )
	{ 
		
	  a = values[ysteps];  
	  //System.out.println("Logger: " + logger.toSource(a, logscale) );
	  ypixel = ystart + yaxis - (int)logger.toSource(a, logscale);
	  num = Format.choiceFormat( a, Format.SCIENTIFIC, tempprec );
 
	  ytick_length = TICK_LENGTH;
          //g2d.setColor(Color.black);
	  if( (int)logger.toSource(a, logscale) >= (int)(yaxis/5 * division) )
          {  
            // if this label does not interfer with the label before it
	    if( last_drawn > (ypixel + fontdata.getHeight()/2) ) 
            {
	      ytick_length += 3;
	      String temp_num = removeTrailingZeros(num);

	      /*System.out.println("int1 = " +
	            (xstart - ytick_length - fontdata.stringWidth(temp_num)));
	      System.out.println("int2 = " + 
	            (ypixel + fontdata.getHeight()/4));*/
    	      g2d.drawString( temp_num,
	          xstart - ytick_length - fontdata.stringWidth(temp_num),
	 	  (ypixel + fontdata.getHeight()/4) );
              last_drawn = ypixel - fontdata.getHeight()/2;
              if( a != 0 )
              //ypixel = ypixel - 2*fontdata.getHeight();
	 	division++;
            }		
          }
          // this if is to "weed out" the nearby tick marks, but always
	  // draw tickmarks for numbers.
          if( last_tick - 5 > ypixel || ytick_length == (TICK_LENGTH + 3) )
          {
            // paint gridlines for major ticks
	    if( ytick_length == TICK_LENGTH + 3 &&
                (gridydisplay == 1 || gridydisplay == 2) )
	    {
              // change color for grid painting
              g2d.setColor(gridcolor);
              g2d.drawLine( xstart, ypixel - 1, 
	        	    xstart + xaxis - 1, ypixel - 1 ); 
              // change color for tick marks
              g2d.setColor(Color.black);
            }
            // paint gridlines for minor ticks
	    if( ytick_length == TICK_LENGTH && gridydisplay == 2 )
	    {
              // change color for grid painting
              g2d.setColor(gridcolor);
	      g2d.drawLine( xstart, ypixel - 1, 
	        	    xstart + xaxis - 1, ypixel - 1 ); 
              // change color for tick marks
              g2d.setColor(Color.black);
            }
            // only paint tick marks
	    g2d.drawLine( xstart - ytick_length, ypixel - 1, 
	        	  xstart - 1, ypixel - 1 );   
	    ytick_length = TICK_LENGTH;
            last_tick = ypixel;
          }
          // draw end marker if nothing no values are near the end.
          if( ysteps == (numysteps - 1) )
          {
            a = ymin;
	    ypixel = ystart + yaxis - (int)logger.toSource(a,logscale);
         	 
	    num = Format.choiceFormat( a, Format.SCIENTIFIC, tempprec );
            if( last_drawn > (ypixel - fontdata.getHeight()/2) ) 
            {	       
              ytick_length += 3;
	      String temp_num = removeTrailingZeros(num);
    	      g2d.drawString( temp_num,
	          xstart - ytick_length - fontdata.stringWidth(temp_num),
	 	  ypixel + fontdata.getHeight()/4 );
            }
            
            // paint gridlines for major ticks
	    if( ytick_length == TICK_LENGTH + 3 &&
         	(gridydisplay == 1 || gridydisplay == 2) )
	    {
              // change color for grid painting
              g2d.setColor(gridcolor);
              g2d.drawLine( xstart, ypixel - 1, 
	 		    xstart + xaxis - 1, ypixel - 1 ); 
              // change color for tick marks
              g2d.setColor(Color.black);
            }
            // paint gridlines for minor ticks
	    if( ytick_length == TICK_LENGTH && gridydisplay == 2 )
	    {
              // change color for grid painting
              g2d.setColor(gridcolor);
	      g2d.drawLine( xstart, ypixel - 1, 
	 		    xstart + xaxis - 1, ypixel - 1 ); 
              // change color for tick marks
              g2d.setColor(Color.black);
            } 
            // only paint tick marks
            g2d.drawLine( xstart - ytick_length, ypixel - 1, 
	 		  xstart - 1, ypixel - 1 ); 
          }
           
	  // debug axis divider
          /*g2d.setColor(Color.red);
          for( int i = 0; i <= 5; i++ )
             g2d.drawLine( xstart - ytick_length, ystart + yaxis*i/5, 
	        	   xstart - 1, ystart + yaxis*i/5 );*/
	}
      }
      // if two-sided color model
      else
      {
	a = values[0];
	LogScaleUtil logger = new LogScaleUtil( 0,(int)(yaxis/2),
        					0,ymin + 1);
	double logscale = logcomponent.getLogScale();
	int negtick_length = 0;
	int neg_ypixel = 0;
        String neg_num = "";
        int division = 0;     // 0-5,  division # in the xaxis.
        int neg_division = 0; // 0-5, division # in the xaxis
        // top pixel coord of last label drawn
        int last_drawn = yaxis/2 + ystart + fontdata.getHeight();
        //bottommost pixel coords of last neg label
        int last_neg_drawn = yaxis/2 + ystart - fontdata.getHeight();
        int first_drawn = 0;  //the bottommost pixel coords of first pos label
        int last_tick = last_drawn + 6;
        int last_neg_tick = last_neg_drawn - 6;
        // find division where the first label is to be drawn
        while( (int)logger.toSource(a, logscale) >= 
               (int)(yaxis/10 * (division + 1) ) )
          division++;
        // find division where the first negative label is to be drawn
        while( (int)logger.toSource(a, logscale) >= 
               (int)(yaxis/10 * (neg_division+1) ) )
          neg_division++;

  //   System.out.println("numysteps/yskip: (" + numysteps + "/" + yskip + 
  //			  ") = " + mult + "R" + rem);
        for( int ysteps = 0; ysteps < numysteps; ysteps++ )
	{  
	  a = values[ysteps];
	  ypixel = ystart + (int)(yaxis/2) - (int)logger.toSource(a,logscale);
	  neg_ypixel = ystart + (int)(yaxis/2) + 
                		(int)logger.toSource(a,logscale);
                    
          if( ysteps == 0 )
             first_drawn = ypixel + (int)(fontdata.getHeight()/2);
          
	  num = Format.choiceFormat( a, Format.SCIENTIFIC, tempprec );
          neg_num = Format.choiceFormat( -a, Format.SCIENTIFIC, tempprec );
 
	  ytick_length = TICK_LENGTH;
          negtick_length = TICK_LENGTH;
          //g2d.setColor(Color.black);
	  if( (int)logger.toSource(a,logscale) >= (int)(yaxis/10 * division) )
          {  
            // positive number labels
	    if( last_drawn > (ypixel + fontdata.getHeight()/2) ) 
            {
	      ytick_length += 3;
	      String temp_num = removeTrailingZeros(num);
    	      g2d.drawString( temp_num, 
	  	  xstart - ytick_length - fontdata.stringWidth(temp_num),
	  	  ypixel + fontdata.getHeight()/4 );
              last_drawn = ypixel - fontdata.getHeight()/2;
              if( ysteps != 0 )
	  	division++;
            }
	  }
          if( (int)logger.toSource(a,logscale) >= 
              (int)(yaxis/10 * neg_division) &&
              first_drawn < (neg_ypixel - fontdata.getHeight()/2) )
          {  
            // negative number labels
	    if( last_neg_drawn < (neg_ypixel - fontdata.getHeight()/2) )
            {
              negtick_length += 3;
	      String temp_num = removeTrailingZeros(neg_num);
    	      g2d.drawString( temp_num,
	          xstart - ytick_length - fontdata.stringWidth(temp_num),
	  	  neg_ypixel + fontdata.getHeight()/4 + 2);
              last_neg_drawn = neg_ypixel + fontdata.getHeight()/2;
	      neg_division++;
	    }		
          }
          
          // since only draw middle once, a = 0 is a special case
          if( a == 0 )
          {   
            // paint gridlines for major ticks
	    if( ytick_length == TICK_LENGTH + 3 &&
                (gridydisplay == 1 || gridydisplay == 2) ) 
            {
              // change color for grid painting
              g2d.setColor(gridcolor);  	  
	      g2d.drawLine( xstart, ypixel, 
	        	    xstart + xaxis - 1, ypixel ); 
              // change color for tick marks
              g2d.setColor(Color.black);  
            }		  
	    g2d.drawLine( xstart - ytick_length, ypixel, 
	        	  xstart - 1, ypixel );
	    last_tick = ypixel;
            last_neg_tick = ypixel;
          }
          else // two sided, draw both negative and positive
          {
            if( last_tick - 5 > ypixel || ytick_length == (TICK_LENGTH + 3) )
            {
              // paint gridlines for major ticks
	      if( ytick_length == TICK_LENGTH + 3 &&
                  (gridydisplay == 1 || gridydisplay == 2) )
              {
                // change color for grid painting
                g2d.setColor(gridcolor);	  
                g2d.drawLine( xstart, ypixel - 1, 
	        	      xstart + xaxis - 1, ypixel - 1 ); 
                // change color for tick marks
                g2d.setColor(Color.black);  
              } 		  
              // paint gridlines for minor ticks
	      if( ytick_length == TICK_LENGTH && gridydisplay == 2 )
              {
                // change color for grid painting
                g2d.setColor(gridcolor);	  
                g2d.drawLine( xstart, ypixel - 1, 
	        	      xstart + xaxis - 1, ypixel - 1 );
                // change color for tick marks
                g2d.setColor(Color.black);  
              } 		   
              // only paint tick marks
              g2d.drawLine( xstart - ytick_length, ypixel - 1, 
	        	    xstart - 1, ypixel - 1 ); 
              last_tick = ypixel;
	    }
            
            if( last_neg_tick + 5 < neg_ypixel || 
	        negtick_length == (TICK_LENGTH + 3) )
            {
              // paint gridlines for major negative ticks
	      if( negtick_length == TICK_LENGTH + 3 &&
                  (gridydisplay == 1 || gridydisplay == 2) )
              {
                // change color for grid painting
                g2d.setColor(gridcolor);	  
                g2d.drawLine( xstart, neg_ypixel + 1, 
	        	      xstart + xaxis - 1, neg_ypixel + 1 );
                // change color for tick marks
                g2d.setColor(Color.black);
              }
              // paint gridlines for minor ticks
	      if( negtick_length == TICK_LENGTH && gridydisplay == 2 )
              {
                // change color for grid painting
                g2d.setColor(gridcolor);	  
                g2d.drawLine( xstart, neg_ypixel + 1, 
	        	      xstart + xaxis - 1, neg_ypixel + 1 );
                // change color for tick marks
                g2d.setColor(Color.black);
              }
              // only paint tick marks
              g2d.drawLine( xstart - negtick_length, neg_ypixel + 1, 
	        	    xstart - 1, neg_ypixel + 1 );
              last_neg_tick = neg_ypixel;
	    }
          }
          
          // draw end marker if no values are near the end.
          if( ysteps == (numysteps - 1) )
          {
             a = Math.abs(ymax);
	     ypixel = ystart + (int)(yaxis/2) - 
                      (int)logger.toSource(a,logscale);
	     neg_ypixel = ystart + (int)(yaxis/2) + 
                	     (int)logger.toSource(a,logscale);
                  
	     num = Format.choiceFormat( a, Format.SCIENTIFIC, tempprec );
             neg_num = Format.choiceFormat( -a, Format.SCIENTIFIC, tempprec );
	     ytick_length = TICK_LENGTH;
             negtick_length = TICK_LENGTH;
             if( last_drawn > (ypixel + fontdata.getHeight()/2) ) 
             {
               ytick_length += 3;
               negtick_length += 3;
	       String temp_num = removeTrailingZeros(num);
    	       g2d.drawString( temp_num,
	           xstart - ytick_length - fontdata.stringWidth(temp_num),
	           ypixel + fontdata.getHeight()/4 );
                   
	       
	       String temp_neg_num = removeTrailingZeros(neg_num);
    	       g2d.drawString( temp_neg_num,
	           xstart - negtick_length - fontdata.stringWidth(temp_neg_num),
	           neg_ypixel + fontdata.getHeight()/4 + 2);	
	       g2d.drawLine( xstart - ytick_length, ypixel, 
	        	     xstart - 1, ypixel );       
	       g2d.drawLine( xstart - negtick_length, neg_ypixel, 
	        	     xstart - 1, neg_ypixel );
             } 
          }
          
          // debug axis divider 
          /*g2d.setColor(Color.red);
          for( int i = 0; i <= 10; i++ )
             g2d.drawLine( xstart - ytick_length, ystart + yaxis*i/10, 
	        	   xstart - 1, ystart + yaxis*i/10 ); */
	}
      }
      paintLabelsAndUnits( num, Y_AXIS, true, g2d );
    } // end if( instanceof)
    else
       System.out.println("Instance of IPseudoLogAxisAddible needed " +
        		  "in AxisOverlay2D.java");
  }
  
  // paint the x axis so the tics and numbers align with the tru log scale
  // that has been implemented.
 /*
  * This method paints X axis' on the overlay.  These axis' use "nice" log
  * numbers.
  *
  *  @param  Graphics2D
  */
  private void paintTruLogX(Graphics2D g2d)
  {
    // Make sure component has the method getPositiveMin(), which is
    // required by the ITruLogAxisAddible interface.
    if( !(component instanceof ITruLogAxisAddible) )
    {
      System.out.println("Instance of ITruLogAxisAddible needed " +
    			 "in AxisOverlay2D.java");
      return;
    }
    FontMetrics fontdata = g2d.getFontMetrics(); 
    
    LogScaleUtil loggerx = new LogScaleUtil(xstart, xstart+xaxis, xmin, xmax);
    CalibrationUtil util = new CalibrationUtil(xmin,xmax, precision, 
        					  Format.ENGINEER );
    float lub = util.leastUpperBound();
    float glb = util.greatestLowerBound();
    float powerdiff = lub/glb;
    
    float num = 0;
    int pixel = 0;
    String string_num = "";
    float[] values = new float[3];
    boolean isLarge = false;
    
    if(powerdiff < 100)
    {
      //use linear axis numbers but paint them at there logerithmic point
      values = util.subDivide();
    }
    else
    {
      //use linear number(subdivideloglarge) numbers
      values = util.subDivideLogLarge(glb, lub);      
      isLarge = true; 
    }
    // get information from either of the subDivide function
    // to start out for loop
    float step = values[0];
    float start = values[1];
    float numsteps = values[2];
    
    //find which technique to use for the next major tickmark and label
    if(!isLarge)
    {
      for(int steps = 0; steps < numsteps; steps++)
      {
    	num = step * steps + start;
    	//find where the point will fall on the graphics coordinate system
    	pixel = (int)loggerx.toSource(num);
    	string_num = Format.choiceFormat( num, Format.SCIENTIFIC, precision );
    	//paint the pixel if it will fall onto the graph
    	if( pixel >= xstart && pixel <= (xstart + xaxis) )
    	{
    	  g2d.drawString( string_num,
        		  pixel-(fontdata.stringWidth(string_num)/2),
    			  yaxis + ystart + 25 );
    	  g2d.drawLine(pixel,yaxis+ystart, pixel, yaxis + ystart + 5);
    	}
      }
    }
    else
    {
      float major_tick = 0;
      float minor_tick = 0;
    
      for(int steps = 0; steps < numsteps; steps++)
      {
        //find the next number to be placed on the axis.
        major_tick = step * (float)Math.pow(10, steps);
        minor_tick = 0;
        string_num = Format.choiceFormat( major_tick, Format.SCIENTIFIC,
        				  precision );
        //paint minor tick marks
        for(int minorsteps = 0; minorsteps < 9; minorsteps++)
        {
          minor_tick += (int)(major_tick);
          //find where the tick mark will fall on the axis coordinate system.
          pixel = (int)loggerx.toSource(minor_tick);
          //System.out.println("Minor Tick/Pixel: "+minor_tick+"/"+pixel);
          //. If tick marks are not in the viewable range, do not draw them.
          if( pixel >= xstart && pixel <= (xstart + xaxis) )
          {
            // If 0, draw a major tick with number, else draw a minor tick.
            if( minorsteps == 0 )
            {
              //draw on axis if it actually will fit on the axis.
              g2d.drawLine(pixel,yaxis+ystart, pixel, yaxis + ystart + 5);
              g2d.drawString(string_num, 
        		     pixel-(fontdata.stringWidth(string_num)/2),
        		     yaxis + ystart + 25);
            }
            else
            {
              //draw on axis if it actually will fit on the axis.
              g2d.drawLine(pixel,yaxis+ystart, pixel, yaxis + ystart + 3);
            }
          }
        } // end for minorsteps
      } // end for steps
    } // end else large
  }
/*------------------------paintTruLogY(Graphics2D)----------------------------
 * This function paints Logrithmic axis on the overlay for the y axis using
 * logscale util and Calibration util to find nice numbers to paint.
 */
 /*
  * This method paints y axis' on the the overlay.
  *
  *  @param  Graphics2D
  */
  private void paintTruLogY(Graphics2D g2d)
  {
    // Make sure component has the method getPositiveXYMin(), which is
    // required by the ITruLogAxisAddible interface.
    if( !(component instanceof ITruLogAxisAddible) )
    {
      System.out.println("Instance of ITruLogAxisAddible needed " +
    			 "in AxisOverlay2D.java");
      return;
    }
    ITruLogAxisAddible log_comp = (ITruLogAxisAddible)component;
    // If bounds are scaled, unscale them.
    float scale_factor = 
                   log_comp.getBoundScaleFactor(ITruLogAxisAddible.Y_AXIS);
    
    CoordTransform unscaled_to_pixel = new CoordTransform(
                               new CoordBounds( 0f,ymin,1f,ymax),
			       new CoordBounds( 0f,0f,1f,
			                        (float)(yaxis)) );
    CoordBounds scaled_bounds = new CoordBounds(0f,ymin,1f,ymax);
    scaled_bounds.scaleBounds(1f,(1f/scale_factor));
    // Original ymin/ymax before scaling by component.
    float scaled_ymin = scaled_bounds.getY1();
    float scaled_ymax = scaled_bounds.getY2();
    if( scaled_ymin > scaled_ymax )
    {
      float temp = scaled_ymin;
      scaled_ymin = scaled_ymax;
      scaled_ymax = temp;
    }
    
    float yaxis_min = log_comp.getPositiveMin(ITruLogAxisAddible.Y_AXIS);
    // Make sure range is all positive. Since it is possible for ymin > ymax,
    // must check both ymin and ymax.
    float log_ymin = ymin;
    float log_ymax = ymax;
    if( log_ymin > log_ymax )
    {
      float temp = log_ymin;
      log_ymin = log_ymax;
      log_ymax = temp;
    }
    if( log_ymin <= 0 )
      log_ymin = yaxis_min;
    if( log_ymax > scaled_ymax )
      log_ymax = scaled_ymax;
    FontMetrics fontdata = g2d.getFontMetrics();
    CoordBounds log_bounds = new CoordBounds(0f, log_ymin, 1f, log_ymax);
    CoordTransform log_to_scaled = new CoordTransform(log_bounds,scaled_bounds);
    LogScaleUtil loggery = new LogScaleUtil(log_ymin, log_ymax,
                                            log_ymin, log_ymax);
    CalibrationUtil yUtil = new CalibrationUtil(ymin, ymax, 5, 
    Format.ENGINEER );
    String string_num = "";
    float num = 0;
    int pixel = 0;
    float glb = yUtil.greatestLowerBound();
    float lub = yUtil.leastUpperBound();

    int powerdiff = yUtil.powerdiff(glb, lub);
    //paint y axis using linear numbers that are scaled logrithmically
    // if they don't have enough orders of magnitude between them.
    if(powerdiff <=2)
    {
      float[] values = yUtil.subDivide();
      float start = values[1];
      float step = values[0];
      float numysteps = values[2];

      for(int steps = 0; steps < numysteps; steps++)
      {
        num = start + step*steps;
	
	//find where the point will fall on the graphics coordinate system
	pixel = ystart + yaxis-1 - (int)( unscaled_to_pixel.MapYTo(
	                         log_to_scaled.MapYTo(loggery.toSource(num))) );
	string_num = Format.choiceFormat( num, Format.SCIENTIFIC, precision );
        //paint the pixel if it will fall onto the graph
        g2d.drawString(string_num,
	               xstart - fontdata.stringWidth(string_num) - 15,
		       pixel + (fontdata.getHeight()/4) );
        g2d.drawLine(xstart - 3, pixel, xstart, pixel);
      }
    }
    //if there are more than 2 orders of magnitude between the min and the max
    else
    { 
      float[] largevalues = yUtil.subDivideLogLarge(glb, lub);
      float start = largevalues[1];
      float step = largevalues[0];
      float major_tick = 0;
      float minor_tick = 0;
      float numysteps = largevalues[2];
      for(int steps = 0; steps < numysteps; steps++)
      {
        //find the next number to be placed on the axis.
        major_tick = start* (float)Math.pow(10, steps);
	minor_tick = 0;
	string_num = Format.choiceFormat( major_tick, Format.SCIENTIFIC,
	                                  precision );

        //paint minor tick marks
        for(int minorsteps = 0; minorsteps < 9; minorsteps++)
	{
          minor_tick += (int)(major_tick);
	
	  //find where the point will fall on the graphics coordinate system
	  pixel = ystart + yaxis-1 - (int)( unscaled_to_pixel.MapYTo(
	                  log_to_scaled.MapYTo(loggery.toSource(minor_tick))) );
	  //System.out.println("Minor Tick/Pixel: "+minor_tick+"/"+pixel);
	  //. If tick marks are not in the viewable range, do not draw them.
	  if( minor_tick >= log_ymin && 
	      minor_tick <= log_ymax )
	  {
	    // If 0, draw a major tick, else draw a minor tick.
	    if( minorsteps == 0 )
	    {
              //draw on axis if it actually will fit on the axis.
              g2d.drawLine(xstart - 5, pixel, xstart, pixel);
              g2d.drawString(string_num,
	    		     xstart - fontdata.stringWidth(string_num) - 15,
	    		     pixel + (fontdata.getHeight()/4)) ;
	    }
	    else
	    {
              //draw on axis if it actually will fit on the axis.
              g2d.drawLine(xstart - 3, pixel, xstart, pixel); 
            }
	  }
        }
      }
    }
  }
  
 /*
  * This private class is a graphical tool to allow users to adjust properties
  * of the AxisOverlay.
  */ 
  private class AxisEditor extends JFrame
  {
    private AxisEditor this_editor;
    private JComboBox xbox;
    private JComboBox ybox;
    
    public AxisEditor()
    {
      super("Axis Editor");
      this_editor = this;
      this.getContentPane().setLayout( new GridLayout(2,0) );
      this.setBounds(editor_bounds);
      this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
      String[] xlist = {"X Axis (none)","Major Grid","Major/Minor Grid"};
      String[] ylist = {"Y Axis (none)","Major Grid","Major/Minor Grid"};
      xbox = new JComboBox(xlist);
      xbox.setName("XBOX");
      xbox.setSelectedIndex(gridxdisplay);
      xbox.addActionListener( new ControlListener() );
      ybox = new JComboBox(ylist);
      ybox.setName("YBOX");
      ybox.setSelectedIndex(gridydisplay);
      ybox.addActionListener( new ControlListener() );
      
      JButton gridcolor = new JButton("Change Grid Color");
      gridcolor.addActionListener( new ControlListener() );
      JButton closebutton = new JButton("Close");
      closebutton.addActionListener( new ControlListener() );
      
      // this jpanel groups all grid options into one row.
      JPanel gridoptions = new JPanel( new GridLayout() );
      gridoptions.add( new JLabel("Grid Options") );
      gridoptions.add(xbox);
      gridoptions.add(ybox);
      
      // this jpanel groups all other miscellaneous options into one row.
      JPanel miscoptions = new JPanel( new GridLayout() );
      miscoptions.add(gridcolor);
      miscoptions.add(closebutton);
      //miscoptions.add(
      this.getContentPane().add( gridoptions );
      this.getContentPane().add( miscoptions );
      this_editor.addComponentListener( new EditorListener() );
    }
    
    class ControlListener implements ActionListener
    {
      public void actionPerformed( ActionEvent e )
      {
	if( e.getSource() instanceof JComboBox )
        {
	  JComboBox temp = ((JComboBox)e.getSource());
          if( temp.getName().equals("XBOX") )
            gridxdisplay = temp.getSelectedIndex();
          else
            gridydisplay = temp.getSelectedIndex();
          this_panel.repaint();
	}
        else if( e.getSource() instanceof JButton )
        {
          String message = e.getActionCommand();
          if( message.equals("Change Grid Color") )
          {
	    Color temp =
        	JColorChooser.showDialog(this_editor, "Grid Color", gridcolor);
            if( temp != null )
            {
              gridcolor = temp;
              this_panel.repaint();
            }
          }
          else if( message.equals("Close") )
          { 
	    editor_bounds = this_editor.getBounds(); 
            this_editor.dispose();
            this_panel.repaint();
          }
          
        }
      }
    }
    
    class EditorListener extends ComponentAdapter
    {
      public void componentResized( ComponentEvent we )
      {
	editor_bounds = editor.getBounds();
      }
    }	    
  }
  
 /*
  * This class will hide the AxisEditor if the editor is visible but
  * the overlay is not.
  */
  private class NotVisibleListener extends ComponentAdapter
  {
    public void componentHidden( ComponentEvent ce )
    {
      editor.setVisible(false);
    }
  }
}
