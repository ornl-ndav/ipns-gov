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

import DataSetTools.util.floatPoint2D;
import DataSetTools.util.FontUtil;
import DataSetTools.components.image.*;
import DataSetTools.components.View.Cursor.SelectionJPanel;
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
	                                   IPreserveState
{
 /**
  * "COMPONENT_RESIZED" - This constant String is a messaging string sent out
  * by the ImageViewComponent when the component has been resized.
  */
  public static final String COMPONENT_RESIZED   = "COMPONENT_RESIZED";
  
  // these variables preserve the state of the ImageViewComponent
 /**
  * "Selected Regions" - This constant String is a key for referencing the state
  * information about the selected regions of this view component. The value
  * that this key references is an array of Regions.
  */
  public static final String SELECTED_REGIONS	 = "Selected Regions";
 
 /**
  * "Listeners" - This constant String is a key for referencing the state
  * information about the action listeners listening to this components. The
  * value that this key references is a Vector of action listeners. 
  */
  public static final String LISTENERS  	 = "Listeners";
 
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
  * "Two Sided" - This constant String is a key for referencing the state
  * information about one or two-sided data. The value that this key references
  * is of type boolean. Values are true if two-sided, false if one-sided.
  */
  public static final String TWO_SIDED  	 = "Two Sided";
 
 /**
  * "Log Scale" - This constant String is a key for referencing the state
  * information about the intensity or log scale value. This value comes from
  * the intensity slider and is of type float. Values are on the range [0,1].
  */
  public static final String LOG_SCALE  	 = "Log Scale";
 
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
  * state information about the annotation on/off control. Of type boolean,
  * if true, the Annotation Overlay control will be turned on, thus turning
  * on the Annotation Overlay itself.
  */
  public static final String ANNOTATION_CONTROL  = "Annotation Control";

 /**
  * "Axis Control" - This constant String is a key for referencing the state
  * information about the axis on/off control. Of type boolean, if true, the
  * Axis Overlay control will be turned on, thus turning on the Axis Overlay
  * itself.
  */
  public static final String AXIS_CONTROL	 = "Axis Control";

 /**
  * "Selection Control" - This constant String is a key for referencing the
  * state information about the selection on/off control. Of type boolean,
  * if true, the Selection Overlay control will be turned on, thus turning
  * on the Selection Overlay itself.
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
  private IVirtualArray2D Varray2D;  //An object containing our array of data
  private Stack dynamicregionlist = new Stack(); // dynamic list of regions.
  private Region[] selectedregions = new Region[0];   
  private Vector Listeners = null;   
  private JPanel big_picture = new JPanel();  
  private JPanel background = new JPanel(new BorderLayout());  
  private ImageJPanel ijp;
  private Rectangle regioninfo;
  private CoordBounds local_bounds;
  private CoordBounds global_bounds;
  private Vector transparencies = new Vector();
  private int precision;
  private Font font;
  private ViewControl[] controls = new ViewControl[5];
  private ViewMenuItem[] menus = new ViewMenuItem[2];
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
    
    AxisInfo2D xinfo = varr.getAxisInfoVA(AxisInfo2D.XAXIS);
    AxisInfo2D yinfo = varr.getAxisInfoVA(AxisInfo2D.YAXIS);
    
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
   
    temp = new_state.get(LISTENERS);
    if( temp != null )
    {
      Listeners = ((Vector)temp);; 
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
    
    temp = new_state.get(TWO_SIDED); 
    if( temp != null )
    {
      isTwoSided = ((Boolean)temp).booleanValue();
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
    
    temp = new_state.get(LOG_SCALE);
    if( temp != null )
    {
      logscale = ((Double)temp).doubleValue(); 
      redraw = true;  
    } 
    
    temp = new_state.get(IMAGEJPANEL);
    if( temp != null )
    {
      //ijp.setObjectState( (ObjectState)temp );
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
      ((ControlCheckboxButton)controls[4]).setSelected( 
                                 ((Boolean)temp).booleanValue() );
      redraw = true;  
    }  	
    
    temp = new_state.get(AXIS_CONTROL);
    if( temp != null )
    {
      ((ControlCheckboxButton)controls[2]).setSelected( 
                                 ((Boolean)temp).booleanValue() );
      redraw = true;  
    }  	
    
    temp = new_state.get(SELECTION_CONTROL);
    if( temp != null )
    {
      ((ControlCheckboxButton)controls[3]).setSelected( 
                                 ((Boolean)temp).booleanValue() );
      redraw = true;  
    }    
  

   
    if( redraw )
      reInit();
  }
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState. Keys will be
  * put in alphabetic order.
  */ 
  public ObjectState getObjectState()
  {
    ObjectState state = new ObjectState();
    state.insert( ANNOTATION_CONTROL, new Boolean( 
                         ((ControlCheckboxButton)controls[4]).isSelected() ) );
    state.insert( ANNOTATION_OVERLAY, 
	       ((OverlayJPanel)transparencies.elementAt(0)).getObjectState() );
    state.insert( AXIS_CONTROL, new Boolean( 
                         ((ControlCheckboxButton)controls[2]).isSelected() ) );
    state.insert( AXIS_OVERLAY_2D,  
	       ((OverlayJPanel)transparencies.elementAt(2)).getObjectState() );
    state.insert( COLOR_CONTROL, new Boolean(addColorControl) );
    state.insert( COLOR_CONTROL_EAST, new Boolean(addColorControlEast) );
    state.insert( COLOR_CONTROL_SOUTH, new Boolean(addColorControlSouth) );
    state.insert( COLOR_SCALE, colorscale );
    state.insert( FONT, font );
    //state.insert( IMAGEJPANEL, ijp.getState() );
    state.insert( LISTENERS, Listeners );
    state.insert( LOG_SCALE, new Double(logscale) );
    state.insert( PRECISION, new Integer(precision) );
    state.insert( SELECTED_REGIONS, selectedregions );
    state.insert( SELECTION_CONTROL, new Boolean( 
                         ((ControlCheckboxButton)controls[3]).isSelected() ) );
    state.insert( SELECTION_OVERLAY,  
	       ((OverlayJPanel)transparencies.elementAt(1)).getObjectState() );
    state.insert( TWO_SIDED, new Boolean(isTwoSided) );
    
    return state;
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
  * This method will get the current data minimum from the imagejpanel.
  *
  *  @return minimum data value
  */
  public float getDataMin()
  {
     return ijp.getDataMin();
  }
  
 /**
  * This method will get the current data maximum from the imagejpanel.
  *
  *  @return maximum data value
  */ 
  public float getDataMax()
  {
     return ijp.getDataMax();
  }
  
 // The following methods are required because this component implements 
 // IColorScaleAddible which extends ILogAxisAddible2D which extends 
 // IAxisAddible2D
 /**
  * This method returns the info about the specified axis. Currently, axes
  * for the image can only be viewed in linear form. If log axes are required,
  * FunctionViewComponent does this and may provide code to implement this.
  * The getAxisInfo method will need to call getLocalLogWorldCoords() in
  * CoordJPanel.java if the log axes are needed.
  * 
  *  @param  isX
  *  @return If isX = true, return info about x axis.
  *	     If isX = false, return info about y axis.
  */
  public AxisInfo2D getAxisInfo( boolean isX )
  {
     // if true, return x info
     if( isX )
     {
	return new AxisInfo2D( ijp.getLocalWorldCoords().getX1(),
        	      ijp.getLocalWorldCoords().getX2(),
        	      Varray2D.getAxisInfoVA(AxisInfo2D.XAXIS).getLabel(),
        	      Varray2D.getAxisInfoVA(AxisInfo2D.XAXIS).getUnits(),
        	      AxisOverlay2D.LINEAR );
     }
     // if false return y info
     return new AxisInfo2D( ijp.getLocalWorldCoords().getY1(),
        	      ijp.getLocalWorldCoords().getY2(),
        	      Varray2D.getAxisInfoVA(AxisInfo2D.YAXIS).getLabel(),
        	      Varray2D.getAxisInfoVA(AxisInfo2D.YAXIS).getUnits(),
        	      AxisOverlay2D.LINEAR );
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
     //return ijp.getLocalWorldCoords().MakeCopy();
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
     //return ijp.getGlobalWorldCoords().MakeCopy();
  }  
  
 /**
  * This method will get the current log scale value for the imagejpanel.
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
  *  @param  pt
  */
  public void setPointedAt( Point pt )
  {
     //Type cast Point pt  into  floatPoint2D fpt
     floatPoint2D fpt = new floatPoint2D( (float)pt.x, (float)pt.y );
     
     //set the cursor position on ImageJPanel
     ijp.setCurrent_WC_point( fpt ); 
  }

 /**
  * This method gets the current floatPoint2D from the ImageJPanel and 
  * converts it to a Point.
  *
  *  @return pt - The current pointed-at point
  */
  public Point getPointedAt()
  {
    floatPoint2D fpt = new floatPoint2D();
    fpt = ijp.getCurrent_WC_point();
    
    Point pt = new Point((int)fpt.x, (int)fpt.y);
    
    return pt;
  }
 
 /**
  * This method creates a selected region to be displayed over the imagejpanel
  * by the selection overlay. Currently, this will replace any previously
  * selected regions. To prevent this, add..., remove..., and clear... could
  * be added to allow for appending selections. A stack could be added to
  * allow for undo and redo. 
  *
  *  @param  rgn - array of selected Regions
  */ 
  public void setSelectedRegions( Region[] rgn ) 
  {
    selectedregions = rgn;
    dynamicregionlist.clear();
    //dynamicpointlist.clear();
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
     if( pin_Varray.getNumRows() == Varray2D.getNumRows() &&
	 pin_Varray.getNumColumns() == Varray2D.getNumColumns() )
     {
       Varray2D.setRegionValues(f_array,0,0);
       Varray2D.setAxisInfoVA( AxisInfo2D.XAXIS,
        		       pin_Varray.getAxisInfoVA( AxisInfo2D.XAXIS ) );
       Varray2D.setAxisInfoVA( AxisInfo2D.YAXIS,
        		       pin_Varray.getAxisInfoVA( AxisInfo2D.YAXIS ) );
       Varray2D.setTitle( pin_Varray.getTitle() );
     }
     else
     {
       Varray2D = new VirtualArray2D( f_array );
     }
     ijp.setData(Varray2D.getRegionValues( 0, MAXDATASIZE, 
					   0, MAXDATASIZE ), true);
     paintComponents( big_picture.getGraphics() );  
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
  * This method allows the user to place a VERTICAL color control in the
  * east panel of the view component.
  *
  *  @param isOn - true if calibrated color scale appears to the right
  *                of the image.
  */
  public void setColorControlEast( boolean isOn )
  {
    ((ControlColorScale)controls[1]).setVisible(false);
    addColorControlEast = isOn;
    buildViewComponent();
  }
  
 /**
  * This method allows the user to place a HORIZONTAL color control in the
  * south panel of the view component.
  *
  *  @param isOn - true if calibrated color scale appears below the image.
  */   
  public void setColorControlSouth( boolean isOn )
  {
    ((ControlColorScale)controls[1]).setVisible(false);
    addColorControlSouth = isOn;
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
  
  private void paintComponents( Graphics g )
  {
    if( g != null )
    {
      for( int i = big_picture.getComponentCount(); i > 0; i-- )
      {
	if( big_picture.getComponent( i - 1 ).isVisible() )
          big_picture.getComponent( i - 1 ).update(g);
      }
    }
    Component temppainter = big_picture;
    while( temppainter.getParent() != null )
      temppainter = temppainter.getParent();
    temppainter.repaint();
    //big_picture.getParent().getParent().getParent().getParent().repaint();
  }
  
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
  
  private void reInit()  
  {
    ijp.setNamedColorModel(colorscale, isTwoSided, false); 
  
    buildViewComponent();    // initializes big_picture to jpanel containing
			     // the background and transparencies
    ((ControlSlider)controls[0]).setValue((float)logscale);
    // this control is an uncalibrated colorscale
    ((ControlColorScale)controls[1]).setColorScale( colorscale, isTwoSided );
    if( addColorControl )
      ((ControlColorScale)controls[1]).setVisible(true);
    else
      ((ControlColorScale)controls[1]).setVisible(false);
      
    ControlCheckboxButton control = (ControlCheckboxButton)controls[2];
    // initialize axis overlay control  
    int bpsize = big_picture.getComponentCount(); 
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
      
    // initialize selection overlay control
    control = (ControlCheckboxButton)controls[3];
    SelectionOverlay select = (SelectionOverlay)transparencies.elementAt(1); 
    if( !control.isSelected() )
    {
      select.setVisible(false);
    }
    else
    {
      select.setVisible(true); 
      select.getFocus();
    }
    
    // initialize annotation overlay control
    control = (ControlCheckboxButton)controls[4];
    AnnotationOverlay note = (AnnotationOverlay)transparencies.elementAt(0); 
    if( !control.isSelected() )
    {
      note.setVisible(false);
    }
    else
    {
      note.setVisible(true);
      note.getFocus();
    } 
    
    //buildViewMenuItems(); 
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
    
    JPanel north = new JPanel();
    north.setPreferredSize(new Dimension( 0, 25 ) );
    JPanel east; 
    JPanel south;
    if( addColorControlEast )
    {
      east = new ControlColorScale( this, ControlColorScale.VERTICAL );
      east.setPreferredSize( new Dimension( 90, 0 ) );
      ((ControlColorScale)east).setTitle("Y Axis Color Scale");
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
      ccs.setTitle("X Axis Color Scale");
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
     // must be incremented.
     controls[0] = new ControlSlider();
     controls[0].setTitle("Intensity Slider");
     ((ControlSlider)controls[0]).setValue((float)logscale);
     //logscale = ((ControlSlider)controls[0]).getValue();		   
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
        sendMessage(COMPONENT_RESIZED);
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
      //System.out.println("Image sent message " + message );

      if (message == CoordJPanel.CURSOR_MOVED)
      {
	//System.out.println("Sending POINTED_AT_CHANGED" );
	sendMessage(POINTED_AT_CHANGED);
      }
      else if (message == CoordJPanel.ZOOM_IN)
      {
	//System.out.println("Sending SELECTED_CHANGED " + regioninfo );
	ImageJPanel center = (ImageJPanel)ae.getSource();
	local_bounds = center.getLocalWorldCoords().MakeCopy();
	global_bounds = center.getGlobalWorldCoords().MakeCopy();
	//for(int next = 0; next < transparencies.size(); next++ )
	//   ((OverlayJPanel)transparencies.elementAt(next)).repaint();
	paintComponents( big_picture.getGraphics() ); 
	sendMessage(SELECTED_CHANGED);
      }
      else if (message == CoordJPanel.RESET_ZOOM)
      {
	//System.out.println("Sending SELECTED_CHANGED" );
	ImageJPanel center = (ImageJPanel)ae.getSource();
	local_bounds = center.getLocalWorldCoords().MakeCopy();
	global_bounds = center.getGlobalWorldCoords().MakeCopy();
	//for(int next = 0; next < transparencies.size(); next++ )
	//   ((OverlayJPanel)transparencies.elementAt(next)).repaint();
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
	if ( message == IViewControl.SLIDER_CHANGED )
	{
           ControlSlider control = (ControlSlider)ae.getSource();
           logscale = control.getValue();				       
           ijp.changeLogScale( logscale, true );
           ((ControlColorScale)controls[1]).setLogScale( logscale );
	} 
	else if ( message == IViewControl.CHECKBOX_CHANGED )
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
               {						// axis overlay
        	((AxisOverlay2D)transparencies.elementAt(2)).setVisible(false);
        	 back.getComponent(1).setVisible(false);	// north
        	 back.getComponent(2).setVisible(false);	// west
        	 back.getComponent(3).setVisible(false);	// south
        	 back.getComponent(4).setVisible(false);	// east
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
        		      big_picture.getComponent(
        		      big_picture.getComponentCount() - 4 ); 
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
        		    big_picture.getComponent(
        		    big_picture.getComponentCount() - 3 ); 
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
        else if( message.equals( IViewControl.BUTTON_PRESSED ) )
        {
          if( ae.getSource() instanceof ControlCheckboxButton )
          {
            ControlCheckboxButton ccb = (ControlCheckboxButton)ae.getSource();
            if( ccb.getTitle().equals("Axis Overlay") )
            {
              AxisOverlay2D axis = (AxisOverlay2D)big_picture.getComponent(
        			   big_picture.getComponentCount() - 2 );
              axis.editGridLines();
            }
            else if( ccb.getTitle().equals("Annotation Overlay") )
            {
              AnnotationOverlay note = (AnnotationOverlay)
        			big_picture.getComponent(
        			big_picture.getComponentCount() - 4 ); 
              note.editAnnotation();
              note.getFocus();
            }
            else if( ccb.getTitle().equals("Selection Overlay") )
            {
              SelectionOverlay select = (SelectionOverlay)
        		      big_picture.getComponent(
        		      big_picture.getComponentCount() - 3 ); 
              select.editSelection();
              select.getFocus();
            }
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
        big_picture.setVisible(false);
        big_picture.setVisible(true);
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
        big_picture.setVisible(false);
        big_picture.setVisible(true);
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
        big_picture.setVisible(false);
        big_picture.setVisible(true);
      }
      // else its a color scale choice.
      else
      {
	colorscale = message;
	ijp.setNamedColorModel( colorscale, isTwoSided, true );
	((ControlColorScale)controls[1]).setColorScale( colorscale, 
							isTwoSided );
      }
      sendMessage( message );
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
  private class SelectedRegionListener implements ActionListener
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
        Point[] imagecolrow = new Point[wcp.length];
        for( int i = 0; i < imagecolrow.length; i++ )
        {
          imagecolrow[i] = new Point( ijp.ImageCol_of_WC_x( wcp[i].x ),
        			      ijp.ImageRow_of_WC_y( wcp[i].y ) );
          /*System.out.println("ImageCoords: " + 
        		   ijp.ImageCol_of_WC_x( wcp[i].x ) + "/" +
        		   ijp.ImageRow_of_WC_y( wcp[i].y ) );
          System.out.println("WorldCoords: " + 
        		   wcp[i].x + "/" +
        		   wcp[i].y );*/
        }
        Region selregion;
        
        if( regiontype.equals(SelectionJPanel.BOX) )
          selregion = new BoxRegion( imagecolrow );
        else if( regiontype.equals(SelectionJPanel.CIRCLE) )
          selregion = new ElipseRegion( imagecolrow );
        else if( regiontype.equals(SelectionJPanel.LINE) )
          selregion = new LineRegion( imagecolrow );
        else if( regiontype.equals(SelectionJPanel.POINT) )
          selregion = new PointRegion( imagecolrow );
        else if( regiontype.equals(SelectionJPanel.WEDGE) )
        {
          int size = imagecolrow.length - 1;
          imagecolrow[size] = new Point( (int)wcp[size].x, (int)wcp[size].y );
          selregion = new WedgeRegion( imagecolrow );
        }
        else //if( regiontype.equals(SelectionJPanel.DOUBLE_WEDGE) )
        {
          int size = imagecolrow.length - 1;
          imagecolrow[size] = new Point( (int)wcp[size].x, (int)wcp[size].y );
          selregion = new DoubleWedgeRegion( imagecolrow );
        }
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
    va2D.setAxisInfoVA( AxisInfo2D.XAXIS, 0f, 10000f, 
        	       "TestX","TestUnits", true );
    va2D.setAxisInfoVA( AxisInfo2D.YAXIS, 0f, 1500f, 
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
    /* instead of using ViewerSim, use IVCTester, which is specific to IVC
    //Construct an ImageViewComponent with array2D
    ImageViewComponent ivc = new ImageViewComponent(va2D);
    ivc.setColorControlEast(true);
    ivc.setColorControlSouth(true);
    ViewerSim viewer = new ViewerSim(ivc);
    viewer.show();*/  
  }
}
