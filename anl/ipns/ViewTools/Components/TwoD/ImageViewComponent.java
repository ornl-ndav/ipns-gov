/*
 * File: ImageViewComponent.java
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
 *  Revision 1.55  2004/02/06 23:46:07  millermi
 *  - Updated ObjectState and reInit() so shared values
 *    maintain consistency.
 *  - KNOWN BUG: If default state is saved with AxisOverlay
 *    control unchecked, the border does not disappear like it is
 *    supposed to.
 *
 *  Revision 1.54  2004/02/03 21:44:48  millermi
 *  - Updated javadocs
 *
 *  Revision 1.53  2004/01/30 22:16:12  millermi
 *  - Changed references of messaging Strings from the IViewControl
 *    to the respective control that sent the message.
 *
 *  Revision 1.52  2004/01/29 23:43:58  millermi
 *  - Added Dennis's changes to dataChanged(), which fixed refresh
 *    bug.
 *  - dataChanged(v2d) now checks to see if new virtual array
 *    references current virtual array. Removed check for arrays
 *    of the same size.
 *
 *  Revision 1.51  2004/01/29 08:16:25  millermi
 *  - Updated the getObjectState() to include parameter for specifying
 *    default state.
 *  - Added static variables DEFAULT and PROJECT to IPreserveState for
 *    use by getObjectState()
 *
 *  Revision 1.50  2004/01/07 06:47:39  millermi
 *  - New float Region parameters have been updated.
 *
 *  Revision 1.49  2003/12/30 00:39:40  millermi
 *  - Added Annular selection capabilities.
 *  - Changed SelectionJPanel.CIRCLE to SelectionJPanel.ELLIPSE
 *
 *  Revision 1.48  2003/12/29 06:32:58  millermi
 *  - Added method addSelection() to add selections through
 *    command as opposed to just via the GUI
 *  - Loading state now makes use of the doClick() method
 *    which was added to the ControlCheckboxButton class.
 *    This reduces the repetative code required to manually
 *    set events that should take place when a mouse click
 *    occurs.
 *
 *  Revision 1.47  2003/12/29 02:41:26  millermi
 *  - get/setPointedAt() now uses floatPoint2D to pass information
 *    about the current pointed at instead of java.awt.Point.
 *
 *  Revision 1.46  2003/12/23 02:21:37  millermi
 *  - Added methods and functionality to allow enabling/disabling
 *    of selections.
 *  - Fixed interface package changes where applicable.
 *
 *  Revision 1.45  2003/12/20 22:15:18  millermi
 *  - Now implements kill();
 *
 *  Revision 1.44  2003/12/20 20:08:55  millermi
 *  - Added ability to clear selections.
 *  - Added axis information for z axis.
 *  - replaced getDataMin() and getDataMax() with getValueAxisInfo()
 *    which returns an AxisInfo object containing the min, max, and more.
 *
 *  Revision 1.43  2003/12/18 22:36:29  millermi
 *  - Now allows selections made on border of image.
 *  - Now uses new AxisInfo class.
 *
 *  Revision 1.42  2003/12/17 20:31:06  millermi
 *  - Removed private static final COMPONENT_RESIZED since
 *    recent changes make this variable obsolete.
 *  - Changed PanViewControl.refreshData() to PanViewControl.repaint()
 *    to stay consistent with changes in the PanViewControl.
 *
 *  Revision 1.41  2003/11/26 18:49:45  millermi
 *  - Changed ElipseRegion reference to EllipseRegion.
 *
 *  Revision 1.40  2003/11/21 02:51:08  millermi
 *  - improved efficiency of paintComponents() and other aspects
 *    that repaint the IVC.
 *
 *  Revision 1.39  2003/11/21 00:48:14  millermi
 *  - Changed how the IVC updated the PanViewControl,
 *    now uses method refreshData().
 *
 *  Revision 1.38  2003/11/18 22:32:42  millermi
 *  - Added functionality to allow cursor events to be
 *    traced by the ControlColorScale.
 *
 *  Revision 1.37  2003/11/18 00:58:43  millermi
 *  - Now implements Serializable, requiring many private variables
 *    to be made transient.
 *  - Added method calls to PanViewControl.setLogScale() to update
 *    the PanViewControl image.
 *
 *  Revision 1.36  2003/10/29 20:32:57  millermi
 *  -Added further support for the PanViewControl, including colorscale
 *   and logscale response.
 *
 *  Revision 1.35  2003/10/27 08:49:33  millermi
 *  - Added the PanViewControl so users can now pan
 *    the image if it is larger than the viewport.
 *
 *  Revision 1.34  2003/10/23 05:49:51  millermi
 *  - Uncommented code that makes use of the ObjectState
 *    information from the ImageJPanel. Prior to now,
 *    the ImageJPanel did not preserve ObjectState.
 *
 *  Revision 1.33  2003/10/21 00:48:40  millermi
 *  - Added kill() to keep consistent with new IViewComponent.
 *
 *  Revision 1.32  2003/10/17 16:09:36  millermi
 *  - getObjectState() now returns a copy of the colorscale string.
 *
 *  Revision 1.31  2003/10/16 16:00:08  millermi
 *  - Changed getCurrentPoint() to getPointedAt() to
 *    maintain method name consistency.
 *
 *  Revision 1.30  2003/10/16 05:00:10  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.29  2003/10/02 03:32:26  millermi
 *  - Added string values to java docs for public static strings.
 *
 *  Revision 1.28  2003/09/24 00:09:46  millermi
 *  - Added static variables to be used as keys by ObjectState
 *  - Added IPreserveState to implementing interfaces along with methods
 *    setObjectState() and getObjectState() required by this interface.
 *  - Added constructor with ObjectState parameter for creating a new
 *    ImageViewComponent with previous state information.
 *
 *  Revision 1.27  2003/09/11 06:28:30  millermi
 *  - Changed menu to coincide with changed to MenuItemMaker.
 *  - Overlay help menu now created using getOverlayMenu().
 *
 *  Revision 1.26  2003/08/29 23:12:09  millermi
 *  - dataChanged() now sets the title and AxisInfo if the array
 *    is of the same size.
 *
 *  Revision 1.25  2003/08/26 03:41:48  millermi
 *  - Added functionality to private class SelectedRegionListener to
 *    handle double wedge selections.
 *
 *  Revision 1.24  2003/08/21 18:22:57  millermi
 *  - Added capabilities for wedge selection
 *
 *  Revision 1.23  2003/08/14 21:49:00  millermi
 *  - Replaced "for" loop to repaint transparencies with paintComponents() in
 *    private class ImageListener. This updates grid lines in a desirable way.
 *  - Edited Revision 1.22 comments, one comment was left unfinished.
 *
 *  Revision 1.22  2003/08/14 17:07:27  millermi
 *  - Changed control of Selection Overlay from ControlCheckbox to
 *    ControlCheckboxButton.
 *  - Implementing dataChanged(IVArray) to handle different sized arrays.
 *  - Changed menus from specialized class instances to an instance of
 *    MenuItemMaker.java.
 *  - Moved construction of big_picture from buildViewComponent to constructor.
 *    buildViewComponent now only builds the background JPanel. This is useful
 *    when calling buildViewComponent multiple times.
 *  - Added returnFocus() to improve focus transition between overlays. When
 *    overlays are checked and unchecked, sometimes the focus is lost. This
 *    fixes most instances.
 *  - Added menu option to switch between color scales. Now south/east/control
 *    color scales can be controlled by Options>Color Scale>Display Position
 *
 *  Revision 1.21  2003/08/11 23:42:48  millermi
 *  - Changed getSelectedSet() to getSelectedRegions() which now
 *    returns an array of Regions.
 *  - Changed setSelectedSet() to setSelectedRegions() which now
 *    takes parameter of type Region.
 *  - Added MAXDATASIZE so it is no longer a hardcoded value.
 *  - Added select.getFocus() if annotation overlay gets unchecked while the
 *    selection overlay is visible. This ensures key events for the
 *    selection overlay are listened to.
 *
 *  Revision 1.20  2003/08/08 00:19:22  millermi
 *  - Moved initialization of local_bounds and global_bounds
 *  after ijp.initializeWorldCoords() in constructor. Fixes
 *  bug that assigned incorrect world coordinates to
 *  selections and annotations prior to zooming.
 *
 *  Revision 1.19  2003/08/07 15:53:20  dennis
 *  - Removed debug statements, added comments to getAxisInfo() for
 *    dealing with log axes.
 *  - getAxisInfo() no longer allows log scaling for image axes.
 *  - Uncommented code in constructor for overlays, removed code
 *    from buildViewComponent().
 *  - Added static variable COMPONENT_RESIZED
 *  - Added sendMessage(COMPONENT_RESIZED) to inform listeners when
 *    the imagejpanel is resized.
 *    (Mike Miller)
 *
 *  Revision 1.18  2003/08/06 13:54:49  dennis
 *  - Control for AxisOverlay2D changed from ControlCheckbox to a
 *    ControlCheckboxButton.
 *  - Removed creation of overlays from buildViewComponent(),
 *    now in constructor.
 *  - Transparencies vector now is built in constructor.
 *  - Combined checkbox and button controls for annotation overlay
 *    into one ControlCheckboxButton control.
 *  - Uncommented code that adjusts the selection overlay color
 *    depending on the color scale used to view the image.
 *    (Mike Miller)
 *
 *  Revision 1.17  2003/07/25 14:37:55  dennis
 *  - Now also implements IZoomTextAddible so that selection and annotation
 *  overlays may be added. (Mike Miller)
 *
 *  Revision 1.15  2003/07/05 19:47:34  dennis
 *  - Added methods getDataMin() and getDataMax().
 *  - Added capability for one- or two-sided color models. Currently,
 *    only two-sided color models are allowed. Alter line 187 to
 *    enable this feature.  (Mike Miller)
 *
 *  Revision 1.14  2003/06/18 22:16:42  dennis
 *  (Mike Miller)
 *  - Changed how horizontal color scale was added to the view
 *    component, now less wasted space.
 *  - Reduced size of vertical color scale.
 *
 *  Revision 1.13  2003/06/18 13:33:50  dennis
 *  (Mike Miller)
 *  - Now implements IColorScaleAddible, which required the addition of
 *    getColorScale() and getLogScale().
 *  - Added methods setColorControlEast() and setColorControlSouth() to
 *    allow user to add the vertical calibrated color scale to the east
 *    panel, or the horizontal calibrated color scale to the south panel.
 *
 *  Revision 1.12  2003/06/13 14:43:53  dennis
 *  - Added methods and implementation for getLocalCoordBounds() and
 *  getGlobalCoordBounds() to allow selection and annotation overlays to adjust
 *  when a zoom occurs. (Mike Miller)
 *
 *  Revision 1.11  2003/06/09 14:46:34  dennis
 *  Added functional HelpMenu under Options. (Mike Miller)
 *
 *  Revision 1.10  2003/06/06 18:51:00  dennis
 *  Added control for editing annotations. (Mike Miller)
 *
 *  Revision 1.9  2003/06/05 17:15:00  dennis
 *   - Added getFocus() call when Selection/AnnotationOverlay checkbox
 *     is selected. (Mike Miller)
 *
 *  Revision 1.8  2003/05/29 14:34:32  dennis
 *  Three changes: (Mike Miller)
 *   -added SelectionOverlay and its on/off control
 *   -added ControlColorScale to controls
 *   -added AnnotationOverlay and its on/off control
 *
 *  Revision 1.7  2003/05/24 17:33:25  dennis
 *  Added on/off control for Axis Overlay. (Mike Miller)
 * 
 *  Revision 1.6  2003/05/22 13:05:58  dennis
 *  Now returns menu items to place in menu bar.
 *
 *  Revision 1.5  2003/05/20 19:46:16  dennis
 *  Now creates a brightness control slider. (Mike Miller)
 *
 *  Revision 1.4  2003/05/16 15:25:12  dennis
 *  Implemented dataChanged() method.
 *  Added grid lines to test image to aid in testing.
 *
 *  Revision 1.3  2003/05/16 14:59:11  dennis
 *  Calculates space needed for labels, and adjusts space as the component
 *  is resized.  (Mike Miller)
 *
 */
 
package DataSetTools.components.View.TwoD;

import javax.swing.*; 
import javax.swing.event.*;
import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector; 
import java.util.Stack; 
import java.io.Serializable;

import DataSetTools.util.floatPoint2D;
import DataSetTools.util.FontUtil;
import DataSetTools.components.image.*;
import DataSetTools.components.View.Cursor.SelectionJPanel;
import DataSetTools.components.View.Cursor.TranslationJPanel;
import DataSetTools.components.View.Transparency.*;
import DataSetTools.components.View.*;
import DataSetTools.components.View.ViewControls.*;
import DataSetTools.components.View.Menu.*;
import DataSetTools.components.View.Region.*;
import DataSetTools.components.ui.ColorScaleMenu;

/**
 * This class allows the user to view data in the form of an image. Meaning
 * is given to the data by way of overlays, which add calibration, selection,
 * and annotation abilities.
 */
public class ImageViewComponent implements IViewComponent2D, 
                                           ActionListener,
           /*for IAxisAddible2D*/          IColorScaleAddible,
           /*for Selection/Annotation*/    IZoomTextAddible,
	                                   IPreserveState,
					   Serializable
{  
  // these variables preserve the state of the ImageViewComponent
 /**
  * "Selected Regions" - This constant String is a key for referencing the state
  * information about the selected regions of this view component. The value
  * that this key references is an array of Regions.
  */
  public static final String SELECTED_REGIONS	 = "Selected Regions";
 
 /**
  * "Precision" - This constant String is a key for referencing the state
  * information about the precision this view component will have. Precision
  * affects the significant digits displayed on the Axis Overlay, among other
  * things. The value that this key references is a primative integer, with
  * value > 0.
  */
  public static final String PRECISION  	 = "Precision";
 
 /**
  * "Font" - This constant String is a key for referencing the state information
  * about the font used by this view component. This font will also be
  * passed on to overlays, such as the Axis, Annotation, and Selection.
  * The value that this key references is of type Font.
  */
  public static final String FONT		 = "Font";
 
 /**
  * "Color Scale" - This constant String is a key for referencing the state
  * information about the color scale used by this view component. The value
  * that this key references is of type String. These string constants may be
  * found in IndexColorMaker.java.
  *
  * @see DataSetTools.components.image.IndexColorMaker
  */
  public static final String COLOR_SCALE	 = "Color Scale";
 
 /**
  * "Log Scale Slider" - This constant String is a key for referencing the state
  * information about the intensity or log scale slider. The value this
  * key references is of type ObjectState.
  */
  public static final String LOG_SCALE_SLIDER  	 = "Log Scale Slider";
 
 /**
  * "Color Control" - This constant String is a key for referencing the state
  * information about the preference of a color scale in the control panel. Of
  * type boolean, if true, an uncalibrated color scale will appear in the
  * control panel.
  */
  public static final String COLOR_CONTROL       = "Color Control";
 
 /**
  * "Color Control East" - This constant String is a key for referencing the
  * state information about the preference of a color scale placed below the
  * image. Of type boolean, if true, a calibrated color scale will appear below
  * the image.
  */
  public static final String COLOR_CONTROL_EAST  = "Color Control East";

 /**
  * "Color Control West" - This constant String is a key for referencing the
  * state information about the preference of a color scale placed to the right
  * of the image. Of type boolean, if true, a calibrated color scale will
  * appear to the right of the image.
  */
  public static final String COLOR_CONTROL_SOUTH = "Color Control West";
 
 /**
  * "Annotation Control" - This constant String is a key for referencing the
  * state information about the control that operates the annotation overlay.
  * The value this key references is of type ObjectState.
  */
  public static final String ANNOTATION_CONTROL  = "Annotation Control";

 /**
  * "Axis Control" - This constant String is a key for referencing the state
  * information about the control that operates the axis overlay. The value this
  * key references is of type ObjectState.
  */
  public static final String AXIS_CONTROL	 = "Axis Control";

 /**
  * "Selection Control" - This constant String is a key for referencing the
  * state information about the control that operates the selection overlay.
  * The value this key references is of type ObjectState.
  */
  public static final String SELECTION_CONTROL   = "Selection Control";

 /**
  * "ImageJPanel" - This constant String is a key for referencing the state
  * information about the ImageJPanel. Since the ImageJPanel has its own state,
  * this value is of type ObjectState, and contains the state of the
  * ImageJPanel. 
  */
  public static final String IMAGEJPANEL	 = "ImageJPanel";

 /**
  * "AnnotationOverlay" - This constant String is a key for referencing the
  * state information about the Annotation Overlay. Since the overlay has its
  * own state, this value is of type ObjectState, and contains the state of
  * the overlay. 
  */
  public static final String ANNOTATION_OVERLAY  = "AnnotationOverlay";

 /**
  * "AxisOverlay2D" - This constant String is a key for referencing the state
  * information about the Axis Overlay. Since the overlay has its own state,
  * this value is of type ObjectState, and contains the state of the
  * overlay.
  */
  public static final String AXIS_OVERLAY_2D	 = "AxisOverlay2D";

 /**
  * "SelectionOverlay" - This constant String is a key for referencing the
  * state information about the Selection Overlay. Since the overlay has
  * its own state, this value is of type ObjectState, and contains the state
  * of the overlay.
  */
  public static final String SELECTION_OVERLAY   = "SelectionOverlay";
  
  // this variable controls the max size of the virtual array to be analyzed.
  private static final int MAXDATASIZE = 1000000000;
  //An object containing our array of data
  private transient IVirtualArray2D Varray2D;
  // dynamic list of regions.
  private transient Stack dynamicregionlist = new Stack();
  private Region[] selectedregions = new Region[0];   
  private transient Vector Listeners = null;   
  private transient JPanel big_picture = new JPanel();  
  private transient JPanel background = new JPanel(new BorderLayout());  
  private transient ImageJPanel ijp;
  private transient Rectangle regioninfo;
  private transient CoordBounds local_bounds;
  private transient CoordBounds global_bounds;
  private transient Vector transparencies = new Vector();
  private int precision;
  private Font font;
  private transient ViewControl[] controls = new ViewControl[6];
  private transient ViewMenuItem[] menus = new ViewMenuItem[2];
  private String colorscale;
  private boolean isTwoSided = true;
  private double logscale = 0;
  private boolean addColorControlEast = false;   // add calibrated color scale
  private boolean addColorControlSouth = false;  // add calibrated color scale
  private boolean addColorControl = true;  // add color scale with controls
  
 /**
  * Constructor that takes in a virtual array and creates an imagejpanel
  * to be viewed in a border layout.
  *
  *  @param  varr IVirtualArray2D containing data for image creation
  */
  public ImageViewComponent( IVirtualArray2D varr )  
  {
    Varray2D = varr; // Get reference to varr
    precision = 4;
    font = FontUtil.LABEL_FONT2;
    ijp = new ImageJPanel();
    //Make ijp correspond to the data in f_array
    ijp.setData(varr.getRegionValues(0, MAXDATASIZE, 0, MAXDATASIZE), true); 
    ImageListener ijp_listener = new ImageListener();
    ijp.addActionListener( ijp_listener );
		
    ComponentAltered comp_listener = new ComponentAltered();   
    ijp.addComponentListener( comp_listener );
    
    regioninfo = new Rectangle( ijp.getBounds() );
    
    AxisInfo xinfo = varr.getAxisInfo(AxisInfo.X_AXIS);
    AxisInfo yinfo = varr.getAxisInfo(AxisInfo.Y_AXIS);
    
    ijp.initializeWorldCoords( new CoordBounds( xinfo.getMin(),
						yinfo.getMax(),      
						xinfo.getMax(),
						yinfo.getMin() ) ); 
    
    local_bounds = ijp.getLocalWorldCoords().MakeCopy();
    global_bounds = ijp.getGlobalWorldCoords().MakeCopy();
    
    colorscale = IndexColorMaker.HEATED_OBJECT_SCALE_2;
    // two-sided model
    if( ijp.getDataMin() < 0 )
       isTwoSided = true;
    // one-sided model
    else
       isTwoSided = false;
    ijp.setNamedColorModel(colorscale, isTwoSided, false); 
    
    //create transparencies
    AnnotationOverlay top = new AnnotationOverlay(this);
    top.setVisible(false);	// initialize this overlay to off.
    SelectionOverlay nextup = new SelectionOverlay(this);
    nextup.setVisible(false);	// initialize this overlay to off.
    nextup.setRegionColor(Color.magenta);
    nextup.addActionListener( new SelectedRegionListener() );
    AxisOverlay2D bottom_overlay = new AxisOverlay2D(this);
    
    // add the transparencies to the transparencies vector
    transparencies.clear();
    transparencies.add(top);
    transparencies.add(nextup);
    transparencies.add(bottom_overlay); 
    
    OverlayLayout overlay = new OverlayLayout(big_picture);
    big_picture.setLayout(overlay);
    for( int trans = 0; trans < transparencies.size(); trans++ )
       big_picture.add((OverlayJPanel)transparencies.elementAt(trans));
    big_picture.add(background);
    
    Listeners = new Vector();
    buildViewComponent();    // initializes big_picture to jpanel containing
			     // the background and transparencies
    buildViewControls(); 
    buildViewMenuItems(); 
  } 
  
 /**
  * Constructor that takes in a virtual array and a previous state to create
  * an imagejpanel to be viewed in a border layout.
  *
  *  @param  varr IVirtualArray2D containing data for image creation
  *  @param  state The state of a previous ImageViewComponent.
  */
  public ImageViewComponent( IVirtualArray2D varr, ObjectState state )  
  {
    this(varr);
    setObjectState(state);
  } 
  
 // setState() and getState() are required by IPreserveState interface   
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(SELECTED_REGIONS);
    if( temp != null )
    {
      selectedregions = (Region[])temp;
      redraw = true;  
    }
    
    temp = new_state.get(FONT);
    if( temp != null )
    {
      font = (Font)temp;
      redraw = true;  
    }  
    
    temp = new_state.get(PRECISION);
    if( temp != null )
    {
      precision = ((Integer)temp).intValue();
      redraw = true;  
    }
    
    temp = new_state.get(COLOR_SCALE);
    if( temp != null )
    {
      colorscale = (String)temp; 
      redraw = true;  
    } 
    
    temp = new_state.get(COLOR_CONTROL);
    if( temp != null )
    {
      addColorControl = ((Boolean)temp).booleanValue();
      redraw = true;  
    }   
    
    temp = new_state.get(COLOR_CONTROL_EAST);
    if( temp != null )
    {
      addColorControlEast = ((Boolean)temp).booleanValue();
      redraw = true;  
    }	
    
    temp = new_state.get(COLOR_CONTROL_SOUTH);
    if( temp != null )
    {
      addColorControlSouth = ((Boolean)temp).booleanValue();
      redraw = true;  
    }  
    
    temp = new_state.get(LOG_SCALE_SLIDER);
    if( temp != null )
    {
      ((ControlSlider)controls[0]).setObjectState( (ObjectState)temp );
      // by doing this, the value in the slider will be used to set all
      // other values. This will keep the logscale values consistent.
      logscale = ((ControlSlider)controls[0]).getValue(); 
      redraw = true;  
    } 
    
    temp = new_state.get(IMAGEJPANEL);
    if( temp != null )
    {
      ijp.setObjectState( (ObjectState)temp );
      redraw = true;  
    } 
    
    temp = new_state.get(ANNOTATION_OVERLAY);
    if( temp != null )
    {
      ((OverlayJPanel)transparencies.elementAt(0)).setObjectState( 
					      (ObjectState)temp );
      redraw = true;  
    }  
    
    temp = new_state.get(AXIS_OVERLAY_2D);
    if( temp != null )
    {
      ((OverlayJPanel)transparencies.elementAt(2)).setObjectState( 
					      (ObjectState)temp );
      redraw = true;  
    }  
    
    temp = new_state.get(SELECTION_OVERLAY);
    if( temp != null )
    {
      ((OverlayJPanel)transparencies.elementAt(1)).setObjectState( 
					      (ObjectState)temp );
      redraw = true;  
    }	
    
    temp = new_state.get(ANNOTATION_CONTROL);
    if( temp != null )
    {
      ((ControlCheckboxButton)controls[4]).setObjectState((ObjectState)temp);
      redraw = true;  
    }  	
    
    temp = new_state.get(AXIS_CONTROL);
    if( temp != null )
    {
      ((ControlCheckboxButton)controls[2]).setObjectState((ObjectState)temp);
      redraw = true;  
    }
    
    temp = new_state.get(SELECTION_CONTROL);
    if( temp != null )
    {
      ((ControlCheckboxButton)controls[3]).setObjectState((ObjectState)temp);
      redraw = true;  
    }
   
    if( redraw )
      reInit();
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
    state.insert( ANNOTATION_CONTROL,
              ((ControlCheckboxButton)controls[4]).getObjectState(isDefault) );
    state.insert( ANNOTATION_OVERLAY, 
      ((OverlayJPanel)transparencies.elementAt(0)).getObjectState(isDefault) );
    state.insert( AXIS_CONTROL, 
              ((ControlCheckboxButton)controls[2]).getObjectState(isDefault) );
    state.insert( AXIS_OVERLAY_2D,  
      ((OverlayJPanel)transparencies.elementAt(2)).getObjectState(isDefault) );
    state.insert( COLOR_CONTROL, new Boolean(addColorControl) );
    state.insert( COLOR_CONTROL_EAST, new Boolean(addColorControlEast) );
    state.insert( COLOR_CONTROL_SOUTH, new Boolean(addColorControlSouth) );
    state.insert( COLOR_SCALE, new String(colorscale) );
    state.insert( FONT, font );
    state.insert( IMAGEJPANEL, ijp.getObjectState(isDefault) );
    state.insert( LOG_SCALE_SLIDER, 
      ((ControlSlider)controls[0]).getObjectState(isDefault) );
    state.insert( PRECISION, new Integer(precision) );
    state.insert( SELECTION_CONTROL,
              ((ControlCheckboxButton)controls[3]).getObjectState(isDefault) );
    state.insert( SELECTION_OVERLAY,  
      ((OverlayJPanel)transparencies.elementAt(1)).getObjectState(isDefault) );
    
    // load these for project specific instances.
    if( !isDefault )
    {
      state.insert( SELECTED_REGIONS, selectedregions );
    }
    
    return state;
  }
  /*
  public void preserveAspectRatio( boolean doPreserve )
  {
    ijp.setPreserveAspectRatio(doPreserve);
  }
  */    
 /**
  * This method will disable the selections included in the names
  * list. Names are defined by static Strings in the SelectionJPanel class.
  *
  *  @param  names List of selection names defined by SelectionJPanel class.
  *  @see DataSetTools.components.View.Cursor.SelectionJPanel
  */
  public void disableSelection(String[] names)
  {
    ((SelectionOverlay)(transparencies.elementAt(1))).disableSelection( names );
  }
     
 /**
  * This method will enable the selections included in the names
  * list. Names are defined by static Strings in the SelectionJPanel class.
  *
  *  @param  names List of selection names defined by SelectionJPanel class.
  *  @see DataSetTools.components.View.Cursor.SelectionJPanel
  */
  public void enableSelection(String[] names)
  {
    ((SelectionOverlay)(transparencies.elementAt(1))).enableSelection( names );
  }
  
 /**
  * This method allows users to add a selection without using the GUI.
  *
  *  @param  world_coord_region The region to be added, with defining points
  *                             in world coord points.
  *  @see    DataSetTools.components.View.Transparency.SelectionOverlay
  */
  public void addSelection( WCRegion world_coord_region )
  {
    ((SelectionOverlay)
        (transparencies.elementAt(1))).addSelectedRegion( world_coord_region );
    // if selection control is unchecked, turn it on.
    if( !((ControlCheckboxButton)controls[3]).isSelected() )
      ((ControlCheckboxButton)controls[3]).doClick();
    returnFocus();
  } 
  
 // These method are required because this component implements 
 // IColorScaleAddible
 /**
  * This method returns the color scale used by the imagejpanel.
  *
  *  @return colorscale
  */
  public String getColorScale()
  {
    return colorscale;
  }
  
 /**
  * This method will get the AxisInfo for the value axis. Use this for
  * finding the datamin, datamax, units, and labels for the data.
  *
  *  @return axisinfo about the data being analyzed.
  */
  public AxisInfo getValueAxisInfo()
  {
    return getAxisInformation( AxisInfo.Z_AXIS );
  }
  
 // The following methods are required by IAxisAddible and ILogAxisAddible
 // and must be implemented because this component implements 
 // IColorScaleAddible which extends ILogAxisAddible which extends 
 // IAxisAddible.
 /**
  * This method returns the info about the specified axis. Currently, axes
  * for the image can only be viewed in linear form. If log axes are required,
  * FunctionViewComponent does this and may provide code to implement this.
  * The getAxisInfo() method will need to call getLocalLogWorldCoords() in
  * CoordJPanel.java if the log axes are needed.
  * 
  *  @param  axiscode Use AxisInfo integer codes.
  *  @return If axiscode = AxisInfo.X_AXIS, return info about x axis.
  *	     If axiscode = AxisInfo.Y_AXIS, return info about y axis.
  *	     If axiscode = AxisInfo.Z_AXIS, return info about "value" axis.
  */
  public AxisInfo getAxisInformation( int axiscode )
  {
    // if true, return x info
    if( axiscode == AxisInfo.X_AXIS )
    {
       return new AxisInfo( ijp.getLocalWorldCoords().getX1(),
    			    ijp.getLocalWorldCoords().getX2(),
    			    Varray2D.getAxisInfo(AxisInfo.X_AXIS).getLabel(),
    			    Varray2D.getAxisInfo(AxisInfo.X_AXIS).getUnits(),
    			    AxisInfo.LINEAR );
    }
    // if true, return y info
    if( axiscode == AxisInfo.Y_AXIS )
    {
      return new AxisInfo( ijp.getLocalWorldCoords().getY1(),
    			   ijp.getLocalWorldCoords().getY2(),
    			   Varray2D.getAxisInfo(AxisInfo.Y_AXIS).getLabel(),
    			   Varray2D.getAxisInfo(AxisInfo.Y_AXIS).getUnits(),
    			   AxisInfo.LINEAR );
    }
    // else return z info
    return new AxisInfo( ijp.getDataMin(),
        		 ijp.getDataMax(),
        		 Varray2D.getAxisInfo(AxisInfo.Z_AXIS).getLabel(),
        		 Varray2D.getAxisInfo(AxisInfo.Z_AXIS).getUnits(),
        		 AxisInfo.LINEAR );
  }
  
 /**
  * This method returns a rectangle containing the location and size
  * of the imagejpanel.
  *
  *  @return The region info about the imagejpanel
  */ 
  public Rectangle getRegionInfo()
  {
    return regioninfo;
  }    
 
 /**
  * This method will return the title given to the image as specified by
  * the Virtual Array
  *
  *  @return title stored in Virtual Array
  */
  public String getTitle()
  {
    return Varray2D.getTitle();
  }
  
 /**
  * This method will return the precision specified by the user. Precision
  * will be assumed to be 4 if not specified. The overlays will call
  * this method to determine the precision.
  *
  *  @return precision of displayed values
  */
  public int getPrecision() 
  {
    return precision;
  }  
  
 /**
  * This method will return the font used on by the overlays. The axis overlay
  * will call this to determine what font to use.
  *
  *  @return font of displayed values
  */
  public Font getFont()
  {
    return font;
  }
  
 /**
  * This method will return the local coordinate bounds of the center
  * jpanel. To be implemented, the center may have to be a coordjpanel.
  *
  *  @return local coordinate bounds of ImageJPanel
  */
  public CoordBounds getLocalCoordBounds()
  {
    return local_bounds;
  }
     
 /**
  * This method will return the global coordinate bounds of the center
  * jpanel. To be implemented, the center may have to be a coordjpanel.
  *
  *  @return global coordinate bounds of ImageJPanel
  */
  public CoordBounds getGlobalCoordBounds()
  {
    return global_bounds;
  }  
  
 /**
  * This method will get the current log scale value for the imagejpanel.
  * This value is on the interval of [0,100].
  *
  *  @return logscale value from the intensity slider
  */ 
  public double getLogScale()
  {
    return logscale;
  }
  
 //****************************************************************************

 // Methods required since this component implements IViewComponent2D
 /**
  * This method adjusts the crosshairs on the imagejpanel.
  * setPointedAt is called from the viewer when another component
  * changes the selected point.
  *
  *  @param  fpt
  */
  public void setPointedAt( floatPoint2D fpt )
  {
    //set the cursor position on ImageJPanel
    ijp.setCurrent_WC_point( fpt ); 
  }

 /**
  * This method gets the current floatPoint2D from the ImageJPanel and 
  * converts it to a Point.
  *
  *  @return  The current pointed-at world coordinate point as a floatPoint2D
  */
  public floatPoint2D getPointedAt()
  {
    return new floatPoint2D(ijp.getCurrent_WC_point());
  }
 
 /**
  * This method creates a selected region to be displayed over the imagejpanel
  * by the selection overlay. Currently, this will replace any previously
  * selected regions. To prevent this, add..., remove..., and clear... could
  * be added to allow for appending selections. A stack could be added to
  * allow for undo and redo. 
  * If null is passed as a parameter, the selections will be cleared.
  *
  *  @param  rgn - array of selected Regions
  */ 
  public void setSelectedRegions( Region[] rgn ) 
  {
    selectedregions = rgn;
    dynamicregionlist.clear();
    //dynamicpointlist.clear();
    if( selectedregions != null )
    {
      for( int i = 0; i < selectedregions.length; i++ )
      {/*
        // if multiple points, this combines them.
        if( selectedregions[i] instanceof PointRegion )
        {
          for( int i = 0; i < selectedregions.length; i++ )
            dynamicpointlist.add(selectedregions[i]);	    
        }
        else
	  dynamicregionlist.add( selectedregions[i] );*/
        dynamicregionlist.push( selectedregions[i] );
      }
    }
    else
    {
      ((SelectionOverlay)(transparencies.elementAt(1))).clearSelectedRegions();
    }
  }
 
 /**
  * Get geometric regions created using the selection overlay.
  *
  *  @return selectedregions
  */ 
  public Region[] getSelectedRegions() //keep the same (for now)
  {
    /*
    // Since the points are grouped together, if any exist, put them all
    // in a pointregion. Because no point regions are in the dynamicregionlist,
    // the PointRegion must be added now.
    if( dynamicpointlist.size() > 0 )
    {
      floatPoint2D[] fplist = new floatPoint2D[dynamicpointlist.size()];
      for( int i = 0; i < dynamicpointlist.size(); i++ )
	fplist[((floatPoint2D)dynamicpointlist.elementAt(i))];
      PointRegion allpoints = new PointRegion(fplist);
      dynamicregionlist.add(allpoints);
    }*/
    selectedregions = new Region[dynamicregionlist.size()];
    for( int i = 0; i < dynamicregionlist.size(); i++ )
      selectedregions[i] = ((Region)dynamicregionlist.elementAt(i));
    return selectedregions;
  } 
 
 /**
  * This method will be called to notify this component of a change in data.
  */
  public void dataChanged()  
  {
    float[][] f_array = Varray2D.getRegionValues( 0, MAXDATASIZE, 
        					  0, MAXDATASIZE );
    ijp.setData(f_array, true);

    AxisInfo xinfo = Varray2D.getAxisInfo(AxisInfo.X_AXIS);
    AxisInfo yinfo = Varray2D.getAxisInfo(AxisInfo.Y_AXIS);

    ijp.initializeWorldCoords( new CoordBounds( xinfo.getMin(),
    					       yinfo.getMax(),
    					       xinfo.getMax(),
    					       yinfo.getMin() ) );

    local_bounds = ijp.getLocalWorldCoords().MakeCopy();
    global_bounds = ijp.getGlobalWorldCoords().MakeCopy();
    
    // since data has changed, remove all selections and annotations.
    ((AnnotationOverlay)(transparencies.elementAt(0))).clearAnnotations();
    ((SelectionOverlay)(transparencies.elementAt(1))).clearSelectedRegions();

    ((PanViewControl)controls[5]).repaint();
    paintComponents( big_picture.getGraphics() );
  }
 
 /**
  * This method will be called to notify this component of a change in data 
  * and an entirely new VirtualArray is used.
  *
  *  @param  pin_Varray - passed in array
  */ 
  public void dataChanged( IVirtualArray2D pin_Varray ) // pin == "passed in"
  {
    //get the complete 2D array of floats from pin_Varray
    float[][] f_array = pin_Varray.getRegionValues( 0, MAXDATASIZE, 
        					    0, MAXDATASIZE );
    // compare references, if not the same, reinitialize the virtual array.
    if( pin_Varray != Varray2D )
    {
      // let Varray2D reference pin_Varray
      if( pin_Varray instanceof VirtualArray2D )
        Varray2D = pin_Varray;
      else
      {
        Varray2D.setRegionValues(f_array,0,0);
        Varray2D.setAxisInfo( AxisInfo.X_AXIS,
    			      pin_Varray.getAxisInfo( AxisInfo.X_AXIS ) );
        Varray2D.setAxisInfo( AxisInfo.Y_AXIS,
    			      pin_Varray.getAxisInfo( AxisInfo.Y_AXIS ) );
        Varray2D.setTitle( pin_Varray.getTitle() );
      }
    }
    dataChanged();   // call other dataChanged() method to reset the ijp and 
                     // redraw the display.
  }
  
 /**
  * Method to add a listener to this component.
  *
  *  @param  act_listener
  */
  public void addActionListener( ActionListener act_listener )
  {	     
    for ( int i = 0; i < Listeners.size(); i++ )    // don't add it if it's
      if ( Listeners.elementAt(i).equals( act_listener ) ) // already there
        return;

    Listeners.add( act_listener ); //Otherwise add act_listener
  }
 
 /**
  * Method to remove a listener from this component.
  *
  *  @param  act_listener
  */ 
  public void removeActionListener( ActionListener act_listener )
  {
    Listeners.remove( act_listener );
  }
 
 /**
  * Method to remove all listeners from this component.
  */ 
  public void removeAllActionListeners()
  {
    Listeners.removeAllElements();
  }
 
 /**
  * Returns all of the controls needed by this view component
  *
  *  @return controls
  */ 
  public JComponent[] getSharedControls()
  {    
    return controls;
  }
 
 /**
  * This method is here to fulfill the implementation required by the
  * IViewComponent2D interface.
  */ 
  public JComponent[] getPrivateControls()
  {
    System.out.println("***Currently unimplemented***");
     
    return new JComponent[0];
  }
 
 /**
  * Returns all of the menu items needed by this view component
  *
  *  @return menus;
  */ 
  public ViewMenuItem[] getSharedMenuItems()
  {
    return menus;
  }
  
  public ViewMenuItem[] getPrivateMenuItems()
  {
    System.out.println("***Currently unimplemented***");
     
    return new ViewMenuItem[0];
  }
  
 /**
  * Return the "background" or "master" panel
  *
  *  @return JPanel containing imagejpanel in the center of a borderlayout.  
  */
  public JPanel getDisplayPanel()
  {
    return big_picture;   
  }
  
   
 /**
  * This method is called by the viewer to inform the view component
  * it is no longer needed. In turn, the view component closes all windows
  * created by it before closing.
  */
  public void kill()
  {    
    for( int trans = 0; trans < transparencies.size(); trans++ )
      ((OverlayJPanel)transparencies.elementAt(trans)).kill();
  }
  
 /**
  * This method allows the user to place a VERTICAL color control in the
  * east panel of the view component. If this method is called, it is assumed
  * that the simple color scale in the controls is no longer wanted, so
  * that color scale will disappear.
  *
  *  @param isOn - true if calibrated color scale appears to the right
  *                of the image.
  */
  public void setColorControlEast( boolean isOn )
  {
    ((ControlColorScale)controls[1]).setVisible(false);
    addColorControlEast = isOn;
    // if calibrated colorscale is requested, turn off control colorscale
    if( addColorControlEast )
    {
      addColorControl = false;
      ((ControlColorScale)controls[1]).setVisible(false);
    }
    buildViewComponent();
  }
  
 /**
  * This method allows the user to place a HORIZONTAL color control in the
  * south panel of the view component. If this method is called, it is assumed
  * that the simple color scale in the controls is no longer wanted, so
  * that color scale will disappear.
  *
  *  @param isOn - true if calibrated color scale appears below the image.
  */   
  public void setColorControlSouth( boolean isOn )
  {
    ((ControlColorScale)controls[1]).setVisible(false);
    addColorControlSouth = isOn;
    // if calibrated colorscale is requested, turn off control colorscale
    if( addColorControlSouth )
    {
      addColorControl = false;
      ((ControlColorScale)controls[1]).setVisible(false);
    }
    buildViewComponent();
  }
 
 // required since implementing ActionListener
 /**
  * This method sends out a message if the pointed-at point is changed.
  *
  *  @param  e - action event
  */ 
  public void actionPerformed( ActionEvent e )
  {
    //get POINTED_AT_CHANGED or SELECTED_CHANGED message from e 
    String message = e.getActionCommand();     
    
    //Send message to listeners 
    if ( message.equals(POINTED_AT_CHANGED) )
      sendMessage(POINTED_AT_CHANGED);
  }
  
 /*
  * Tells all listeners about a new action.
  *
  *  @param  message
  */  
  private void sendMessage( String message )
  {
    for ( int i = 0; i < Listeners.size(); i++ )
    {
      ActionListener listener = (ActionListener)Listeners.elementAt(i);
      listener.actionPerformed( new ActionEvent( this, 0, message ) );
    }
  }
 
 /*
  * This method repaints the ImageViewComponent correctly
  */ 
  private void paintComponents( Graphics g )
  {
    if( g != null )
    {
      big_picture.update(g);
    }
    Component temppainter = big_picture;
    while( temppainter.getParent() != null )
      temppainter = temppainter.getParent();
    temppainter.repaint();
  }
 
 /*
  * This method is needed so that keyboard events from the overlays
  * are recognized after controls are given focus.
  */ 
  private void returnFocus()
  {	       
    AnnotationOverlay note = (AnnotationOverlay)transparencies.elementAt(0); 
    SelectionOverlay select = (SelectionOverlay)transparencies.elementAt(1); 
    AxisOverlay2D axis = (AxisOverlay2D)transparencies.elementAt(2);
    
    if( note.isVisible() )
      note.getFocus(); 
    else if(select.isVisible() )
      select.getFocus();
    else if(axis.isVisible() )
      axis.getFocus();   
  }
 
 /*
  * This method is used by the setObjectState() to set all saved state.
  */ 
  private void reInit()  
  {
    ijp.setNamedColorModel(colorscale, isTwoSided, false); 
    local_bounds = ijp.getLocalWorldCoords().MakeCopy();
    global_bounds = ijp.getGlobalWorldCoords().MakeCopy();
    
    // make sure logscale and two-sided are consistent
    ((AxisOverlay2D)transparencies.elementAt(2)).setTwoSided(isTwoSided);
    ijp.changeLogScale(logscale,true);
    // since flags have already been set, this will put the color scales
    // where they need to be.
    buildViewComponent();    // builds the background jpanel containing
			     // the image and possibly a calibrated color scale
    // this control is an uncalibrated colorscale
    if( addColorControl )
    {
      ((ControlColorScale)controls[1]).setColorScale( colorscale, isTwoSided );
      ((ControlColorScale)controls[1]).setVisible(true);
    }
    else
      ((ControlColorScale)controls[1]).setVisible(false);
    
    // give focus to the top overlay
    returnFocus();
    
    ((PanViewControl)controls[5]).setGlobalBounds(global_bounds);
    ((PanViewControl)controls[5]).setLocalBounds(local_bounds);
    ((PanViewControl)controls[5]).repaint();
  } 
  
 /*
  * This method takes in an imagejpanel and puts it into a borderlayout.
  * Overlays are added to allow for calibration, selection, and annotation.
  */
  private void buildViewComponent()
  {   
    int westwidth = font.getSize() * precision + 22;
    int southwidth = font.getSize() * 3 + 9;
    // this will be the background for the master panel
    background.removeAll();
    String title = getValueAxisInfo().getLabel() + " (" + 
                   getValueAxisInfo().getUnits() + ")";
    JPanel north = new JPanel();
    north.setPreferredSize(new Dimension( 0, 25 ) );
    JPanel east; 
    JPanel south;
    if( addColorControlEast )
    {
      east = new ControlColorScale( this, ControlColorScale.VERTICAL );
      east.setPreferredSize( new Dimension( 90, 0 ) );
      //((ControlColorScale)east).setTwoSided(isTwoSided);
      //((ControlColorScale)east).setLogScale(logscale);
      ((ControlColorScale)east).setTitle(title);
    }
    else
    {
      east = new JPanel();
      east.setPreferredSize(new Dimension( 50, 0 ) );
    }
    if( addColorControlSouth )
    {
      south = new JPanel( new BorderLayout() );
      JPanel mininorth = new JPanel();
      mininorth.setPreferredSize( new Dimension( 0, southwidth ) );
      south.add( mininorth, "North" );
      ControlColorScale ccs = new ControlColorScale(
				    this,ControlColorScale.HORIZONTAL);
      ccs.setTitle(title);
      //ccs.setTwoSided(isTwoSided);
      //ccs.setLogScale(logscale);
      south.add( ccs, "Center" );
      south.setPreferredSize( new Dimension( 0, southwidth + 75) );
    }
    else
    {
      south = new JPanel();    
      south.setPreferredSize(new Dimension( 0, southwidth ) );
    }
 
    JPanel west = new JPanel();
    west.setPreferredSize(new Dimension( westwidth, 0 ) );
    
    //Construct the background JPanel

    background.add(ijp, "Center");
    background.add(north, "North");
    background.add(west, "West");
    background.add(south, "South");
    background.add(east, "East" ); 
  }
  
 /*
  * This method constructs the controls required by the ImageViewComponent
  */
  private void buildViewControls()
  {
    // Note: If controls are added here, the size of the array controls[]
    // must be incremented in the private data members.
    controls[0] = new ControlSlider();
    controls[0].setTitle("Intensity Slider");
    ((ControlSlider)controls[0]).setValue((float)logscale);		  
    controls[0].addActionListener( new ControlListener() );
               
    controls[1] = new ControlColorScale(colorscale, isTwoSided );
    controls[1].setTitle("Color Scale");
    
    controls[2] = new ControlCheckboxButton(true);
    ((ControlCheckboxButton)controls[2]).setTitle("Axis Overlay");
    controls[2].addActionListener( new ControlListener() );
   
    controls[3] = new ControlCheckboxButton();  // initially unchecked
    ((ControlCheckboxButton)controls[3]).setTitle("Selection Overlay");
    controls[3].addActionListener( new ControlListener() );
    
    controls[4] = new ControlCheckboxButton();  // initially unchecked
    ((ControlCheckboxButton)controls[4]).setTitle("Annotation Overlay");
    controls[4].addActionListener( new ControlListener() );
    // panviewcontrol
    controls[5] = new PanViewControl(ijp);
    controls[5].addActionListener( new ControlListener() );     
  }
  
 /*
  * This method constructs the menu items required by the ImageViewComponent
  */   
  private void buildViewMenuItems()
  {  
    Vector colorscale = new Vector();
    Vector position = new Vector();
    Vector choices = new Vector();
    Vector cs_listener = new Vector(); 
    colorscale.add("Color Scale");
    cs_listener.add( new ColorListener() );
    colorscale.add(choices);
     choices.add("Scales");
      cs_listener.add( new ColorListener() );
      choices.add(IndexColorMaker.HEATED_OBJECT_SCALE);
      choices.add(IndexColorMaker.HEATED_OBJECT_SCALE_2);
      choices.add(IndexColorMaker.GRAY_SCALE);
      choices.add(IndexColorMaker.NEGATIVE_GRAY_SCALE);
      choices.add(IndexColorMaker.GREEN_YELLOW_SCALE);
      choices.add(IndexColorMaker.RAINBOW_SCALE);
      choices.add(IndexColorMaker.OPTIMAL_SCALE);
      choices.add(IndexColorMaker.MULTI_SCALE);
      choices.add(IndexColorMaker.SPECTRUM_SCALE);
    colorscale.add(position);
     position.add("Display Position");
      cs_listener.add( new ColorListener() );
      position.add("Control Panel");
      position.add("Below Image (calibrated)");
      position.add("Right of Image (calibrated)");
      position.add("None");
    
    JMenuItem scalemenu = MenuItemMaker.makeMenuItem( colorscale,cs_listener );
    menus[0] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS, scalemenu ); 

    JMenuItem helpmenu = MenuItemMaker.getOverlayMenu( new HelpListener() );
    menus[1] = new ViewMenuItem(ViewMenuItem.PUT_IN_HELP, helpmenu );
  }
  
 //***************************Assistance Classes******************************
 /*
  * ComponentAltered monitors if the imagejpanel has been resized. If so,
  * the regioninfo is updated.
  */
  private class ComponentAltered extends ComponentAdapter
  {
    public void componentResized( ComponentEvent e )
    {
      //System.out.println("Component Resized");
      Component center = e.getComponent();
      regioninfo = new Rectangle( center.getLocation(), center.getSize() );
      /*
      System.out.println("Location = " + center.getLocation() );
      System.out.println("Size = " + center.getSize() );
      System.out.println("class is " + center.getClass() );  
      */
    }
  }

 /*
  * ImageListener monitors if the imagejpanel has sent any messages.
  * If so, process the message and relay it to the viewer.
  */
  private class ImageListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();

      if (message == CoordJPanel.CURSOR_MOVED)
      {
        // If calibrated color scales are included, this will cause the
	// current point to be traced by those color scales.
        if( addColorControlSouth )
	{
	  JPanel south = (JPanel)background.getComponent(3);
	  ControlColorScale cs = (ControlColorScale)south.getComponent(1);
	  cs.setMarker( ijp.ImageValue_at_Cursor() );
	}
        if( addColorControlEast )
	{
	  ControlColorScale east = 
	           (ControlColorScale)background.getComponent(4);
	  east.setMarker( ijp.ImageValue_at_Cursor() );
	}
	
	sendMessage(POINTED_AT_CHANGED);
      }
      else if (message == CoordJPanel.ZOOM_IN)
      {
	ImageJPanel center = (ImageJPanel)ae.getSource();
	local_bounds = center.getLocalWorldCoords().MakeCopy();
	global_bounds = center.getGlobalWorldCoords().MakeCopy();
	((PanViewControl)controls[5]).setGlobalBounds(global_bounds);
	((PanViewControl)controls[5]).setLocalBounds(local_bounds);
	paintComponents( big_picture.getGraphics() ); 
	sendMessage(SELECTED_CHANGED);
      }
      else if (message == CoordJPanel.RESET_ZOOM)
      {
	ImageJPanel center = (ImageJPanel)ae.getSource();
	local_bounds = center.getLocalWorldCoords().MakeCopy();
	global_bounds = center.getGlobalWorldCoords().MakeCopy();
	((PanViewControl)controls[5]).setGlobalBounds(global_bounds);
	((PanViewControl)controls[5]).setLocalBounds(local_bounds);
	paintComponents( big_picture.getGraphics() ); 
	sendMessage(SELECTED_CHANGED);
      }
    }	  
  }
  
 /*
  * ControlListener moniters activities of all controls 
  * of the ImageViewComponent.
  */
  private class ControlListener implements ActionListener
  { 
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
        		   // set image log scale when slider stops moving
      if ( message == ControlSlider.SLIDER_CHANGED )
      {
        ControlSlider control = (ControlSlider)ae.getSource();
        logscale = control.getValue();
        ijp.changeLogScale( logscale, true );
        ((ControlColorScale)controls[1]).setLogScale( logscale );
	((PanViewControl)controls[5]).repaint();
      } 
      else if ( message == ControlCheckboxButton.CHECKBOX_CHANGED )
      { 
        int bpsize = big_picture.getComponentCount();
        
        if( ae.getSource() instanceof ControlCheckboxButton )
        {
          ControlCheckboxButton control = 
                		      (ControlCheckboxButton)ae.getSource();
          // if this control turns on/off the axis overlay...
          if( control.getTitle().equals("Axis Overlay") )
          {	
            JPanel back = (JPanel)big_picture.getComponent( bpsize - 1 );
            if( !control.isSelected() )
            {						     // axis overlay
              ((AxisOverlay2D)transparencies.elementAt(2)).setVisible(false);
              back.getComponent(1).setVisible(false);	     // north
              back.getComponent(2).setVisible(false);	     // west
              back.getComponent(3).setVisible(false);	     // south
              back.getComponent(4).setVisible(false);	     // east
              //System.out.println("visible..." + 
               //((AxisOverlay2D)transparencies.elementAt(2)).isVisible() );
            }
            else
            {		   
              back.getComponent(1).setVisible(true);
              back.getComponent(2).setVisible(true);
              back.getComponent(3).setVisible(true);
              back.getComponent(4).setVisible(true);
              ((AxisOverlay2D)transparencies.elementAt(2)).setVisible(true);
            }
          }// end of if( axis overlay control ) 
          else if( control.getTitle().equals("Annotation Overlay") )
          {
            AnnotationOverlay note = (AnnotationOverlay)
                	             transparencies.elementAt(0); 
            if( !control.isSelected() )
            {
              note.setVisible(false);
            }
            else
            {
              note.setVisible(true);
              note.getFocus();
            }		  
          }
          else if( control.getTitle().equals("Selection Overlay") )
          {
            // if this control turns on/off the selection overlay...
            SelectionOverlay select = (SelectionOverlay)
                	              transparencies.elementAt(1); 
            if( !control.isSelected() )
            {
              select.setVisible(false);
            }
            else
            {
              select.setVisible(true); 
              select.getFocus();
            }
          }
        }		
      } // end if checkbox
      else if( message.equals( ControlCheckboxButton.BUTTON_PRESSED ) )
      {
        if( ae.getSource() instanceof ControlCheckboxButton )
        {
          ControlCheckboxButton ccb = (ControlCheckboxButton)ae.getSource();
          if( ccb.getTitle().equals("Axis Overlay") )
          {
            AxisOverlay2D axis = (AxisOverlay2D)transparencies.elementAt(2);
            axis.editGridLines();
          }
          else if( ccb.getTitle().equals("Annotation Overlay") )
          {
            AnnotationOverlay note = (AnnotationOverlay)
	                             transparencies.elementAt(0); 
            note.editAnnotation();
            note.getFocus();
          }
          else if( ccb.getTitle().equals("Selection Overlay") )
          {
            SelectionOverlay select = (SelectionOverlay)
			              transparencies.elementAt(1); 
            select.editSelection();
            select.getFocus();
          }
        }	
      }
      // This message is sent by the pan view control when the viewable
      // subregion changes.
      else if( message.equals( TranslationJPanel.BOUNDS_CHANGED ) )
      {
        if( ae.getSource() instanceof PanViewControl )
        {
          PanViewControl pvc = (PanViewControl)ae.getSource();
          // since the pan view control has a CoordJPanel in it with the
          // same bounds, set its local bounds to the image local bounds.
	//System.out.println("IVCLocal: " + local_bounds.toString() );
	  local_bounds = pvc.getLocalBounds();
	//System.out.println("IVCLocal2: " + local_bounds.toString() );
          ijp.setLocalWorldCoords( local_bounds );
          // this method is only here to repaint the image
          ijp.changeLogScale( logscale, true );
          sendMessage(SELECTED_CHANGED);
        }
      }
      //repaints overlays accurately 
      sendMessage( message );
      returnFocus();
      paintComponents( big_picture.getGraphics() ); 
    }
  } 

 /*
  * This class relays the message sent out by the ColorScaleMenu
  */  
  private class ColorListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
      // these determine which color scale is to be viewed.
      if( message.equals("Control Panel") )
      {
	((ControlColorScale)controls[1]).setVisible(true);
        addColorControlEast = false;
        addColorControlSouth = false;
	addColorControl = true;
        buildViewComponent();
        
        // this control turns on/off the axis overlay...
        ControlCheckboxButton control = (ControlCheckboxButton)controls[2];
	int bpsize = big_picture.getComponentCount();
        JPanel back = (JPanel)big_picture.getComponent( bpsize - 1 );
	if( !control.isSelected() )
        {						 // axis overlay
         ((AxisOverlay2D)transparencies.elementAt(2)).setVisible(false);
          back.getComponent(1).setVisible(false);	 // north
          back.getComponent(2).setVisible(false);	 // west
          back.getComponent(3).setVisible(false);	 // south
          back.getComponent(4).setVisible(false);	 // east
        }
      }
      else if( message.equals("Below Image (calibrated)") )
      {
	((ControlColorScale)controls[1]).setVisible(false);
        addColorControlEast = false;
        addColorControlSouth = true;
	addColorControl = false;
        buildViewComponent();
        ((ControlCheckboxButton)controls[2]).setSelected(true);
        ((OverlayJPanel)transparencies.elementAt(2)).setVisible(true);
      }
      else if( message.equals("Right of Image (calibrated)") )
      {
	((ControlColorScale)controls[1]).setVisible(false);
        addColorControlEast = true;
        addColorControlSouth = false;
	addColorControl = false;
        buildViewComponent();
        ((ControlCheckboxButton)controls[2]).setSelected(true);
        ((OverlayJPanel)transparencies.elementAt(2)).setVisible(true);
      }
      else if( message.equals("None") )
      {
	((ControlColorScale)controls[1]).setVisible(false);
        addColorControlEast = false;
        addColorControlSouth = false;
	addColorControl = false;
        buildViewComponent();
        ((ControlCheckboxButton)controls[2]).setSelected(true);
        ((OverlayJPanel)transparencies.elementAt(2)).setVisible(true);
      }
      // else change color scale.
      else
      {
	colorscale = message;
	ijp.setNamedColorModel( colorscale, isTwoSided, true );
	((ControlColorScale)controls[1]).setColorScale( colorscale, 
							isTwoSided );        
	((PanViewControl)controls[5]).repaint();
      }
      sendMessage( message );
      background.validate();
      paintComponents( big_picture.getGraphics() );
    }
  }

 /*
  * This class relays the message sent out by the HelpMenu
  */  
  private class HelpListener implements ActionListener
  {
     public void actionPerformed( ActionEvent ae )
     {
	String button = ae.getActionCommand();
        if( button.equals("Annotation") )
        {
           //System.out.println("AnnotationHelpMenu");
           AnnotationOverlay.help();
        }
        else if( button.equals("Axis") )
        {
           //System.out.println("AxisHelpMenu");
           AxisOverlay2D.help();
        }
        else if( button.equals("Selection") )
        {
           //System.out.println("SelectionHelpMenu");
           SelectionOverlay.help();  
        }	
     }
  } 

 /*
  * This class relays messages to listeners and repackages WCRegions into
  * Regions whenever the SelectionOverlay sends a message that
  * a selected region is added or removed.
  */  
  private class SelectedRegionListener implements ActionListener, Serializable
  {
    public void actionPerformed( ActionEvent ae )
    {
      // must set the selected region here
      Vector regions = 
	 ((SelectionOverlay)transparencies.elementAt(1)).getSelectedRegions();
      
      // if region was added, add the defining points of the last region.
      if( ae.getActionCommand().equals( SelectionOverlay.REGION_ADDED ) )
      {
	WCRegion lastregion = (WCRegion)regions.lastElement();
        String regiontype = lastregion.getRegionType();
	floatPoint2D[] wcp = lastregion.getWorldCoordPoints();
        floatPoint2D[] imagecolrow = new floatPoint2D[wcp.length];
	
	// wcp[0] must be on image, but wcp[1] may not always be.
	// If wcp[1] isn't on the image, move endpoint to edge of image.
        if( regiontype.equals(SelectionJPanel.LINE) )
	{
	  float wcxmin = ijp.getGlobalWorldCoords().getX1();
	  float wcxmax = ijp.getGlobalWorldCoords().getX2();
	  // since y max and min were entered in opposite order, switch them.
	  float wcymax = ijp.getGlobalWorldCoords().getY1();
	  float wcymin = ijp.getGlobalWorldCoords().getY2();
	  float slope = (wcp[1].y - wcp[0].y)/(wcp[1].x - wcp[0].x);
	  // if less than xmin, set wcp1.x = xmin, calculate wcp1.y
	  if( wcp[1].x < wcxmin ) 
	  {
	    // solve equation used to find "slope" for wcp[1].y since all
	    // other elements are known.
	    wcp[1].x = wcxmin;
	    wcp[1].y = slope*(wcp[1].x - wcp[0].x) + wcp[0].y;
	    
	  }
	  // if greater than xmax, set wcp1.x = xmax, calculate wcp1.y
	  else if( wcp[1].x > wcxmax )
	  {
	    // solve equation used to find "slope" for wcp[1].y since all
	    // other elements are known.
	    wcp[1].x = wcxmax;
	    wcp[1].y = slope*(wcp[1].x - wcp[0].x) + wcp[0].y;
	  }
	  
	  // if less than ymin, set wcp1.y = ymin, calculate wcp1.x
	  if( wcp[1].y < wcymin )
	  {
	    // solve equation used to find "slope" for wcp[1].x since all
	    // other elements are known.
	    wcp[1].y = wcymin;
	    wcp[1].x = (wcp[1].y - wcp[0].y)/slope + wcp[0].x;
	  }
	  // if greater than ymax, set wcp1.y = ymax, calculate wcp1.x
	  else if( wcp[1].y > wcymax )
	  {
	    // solve equation used to find "slope" for wcp[1].x since all
	    // other elements are known.
	    wcp[1].y = wcymax;
	    wcp[1].x = (wcp[1].y - wcp[0].y)/slope + wcp[0].x;
	  }
	    
          imagecolrow[0] = new floatPoint2D( new Point( 
	                              ijp.ImageCol_of_WC_x( wcp[0].x ),
                		      ijp.ImageRow_of_WC_y( wcp[0].y ) ) );
          imagecolrow[1] = new floatPoint2D( new Point( 
	                              ijp.ImageCol_of_WC_x( wcp[1].x ),
                		      ijp.ImageRow_of_WC_y( wcp[1].y ) ) );
	}
	else
	{
          for( int i = 0; i < imagecolrow.length; i++ )
          {
	    imagecolrow[i] = new floatPoint2D( new Point( 
	                                ijp.ImageCol_of_WC_x( wcp[i].x ),
        			        ijp.ImageRow_of_WC_y( wcp[i].y ) ) );
	  /*System.out.println("ImageCoords: " + 
        		   ijp.ImageCol_of_WC_x( wcp[i].x ) + "/" +
        		   ijp.ImageRow_of_WC_y( wcp[i].y ) );
          System.out.println("WorldCoords: " + 
        		   wcp[i].x + "/" +
        		   wcp[i].y );*/
          }
	}
        Region selregion;
        
        if( regiontype.equals(SelectionJPanel.BOX) )
          selregion = new BoxRegion( imagecolrow );
        else if( regiontype.equals(SelectionJPanel.ELLIPSE) )
          selregion = new EllipseRegion( imagecolrow );
        else if( regiontype.equals(SelectionJPanel.LINE) )
          selregion = new LineRegion( imagecolrow );
        else if( regiontype.equals(SelectionJPanel.POINT) )
          selregion = new PointRegion( imagecolrow );
        else if( regiontype.equals(SelectionJPanel.WEDGE) )
        {
          int size = imagecolrow.length - 1;
          imagecolrow[size] = new floatPoint2D( wcp[size].x, wcp[size].y );
          selregion = new WedgeRegion( imagecolrow );
        }
        else if( regiontype.equals(SelectionJPanel.DOUBLE_WEDGE) )
        {
          int size = imagecolrow.length - 1;
          imagecolrow[size] = new floatPoint2D( wcp[size].x, wcp[size].y );
          selregion = new DoubleWedgeRegion( imagecolrow );
        }
        else if( regiontype.equals(SelectionJPanel.RING) )
        {
          selregion = new AnnularRegion( imagecolrow );
        }
	// else its an invalid region.
	else
	  return;
        dynamicregionlist.push(selregion);
      //System.out.println("WCP[0]: " + wcp[0].x + wcp[0].y );
      } // end if( regionadded )
      else if( ae.getActionCommand().equals(SelectionOverlay.REGION_REMOVED) )
      {
	if( dynamicregionlist.size() != 0 )
	  dynamicregionlist.pop();
      }
      else if( ae.getActionCommand().equals(
	       SelectionOverlay.ALL_REGIONS_REMOVED) )
	dynamicregionlist.clear();
      sendMessage( ae.getActionCommand() );
    }
  }
     
 /*
  * MAIN - Basic main program to test an ImageViewComponent object
  */
  public static void main( String args[] ) 
  {
    int col = 200;
    int row = 200;

    //Make a sample 2D array
    VirtualArray2D va2D = new VirtualArray2D(row, col); 
    va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f, 
        	       "TestX","TestUnits", true );
    va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f, 
        		"TestY","TestYUnits", false );
    va2D.setTitle("Main Test");
    //Fill the 2D array with the function x*y
    for(int i = 0; i < row; i++)
    {
      for(int j = 0; j < col; j++)
      {
        // adds vertical and horizontal test lines every 25th pixel
        if ( i % 25 == 0 )
          va2D.setDataValue(i, j, i*col); //put float into va2D
        else if ( j % 25 == 0 )
          va2D.setDataValue(i, j, j*row); //put float into va2D
        else
          va2D.setDataValue(i, j, i*j); //put float into va2D
      }
    }
    
    IVCTester test = new IVCTester( va2D );
  }
}
