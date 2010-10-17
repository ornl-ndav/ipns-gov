/*
 * File FunctionViewComponent.java
 *
 * Copyright (C) 2003 Brent Serum
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
 * 
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USAB
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
 *  Revision 1.96  2007/09/17 02:50:12  dennis
 *  Some code cleanup and additions working towards fixing the problem
 *  with specified axis values not having an effect.  This is ultimately
 *  due to the code always auto-scaling to fit the data, regardless of
 *  whether or not Axis information was specified for the VirtualArray1D.
 *  Additional information is needed by the FunctionViewComponent to
 *  determine whether it should auto-scale or not.  So:
 *  1. Added another constructor that takes an additional boolean flag,
 *     autoscale.  If passed in as false, this should use data from the
 *     AxisInfo objects that are part of the virtual array, instead of
 *     autoscaling to the data values.  This is NOT FULLY IMPLEMENTED
 *     YET.
 *  2. Removed methods and messaging related to selection.  The
 *     methods were not yet used, and were only partly implemented or were
 *     just "stubs".  Since the selection process has been updated for the
 *     ImageViewComponent, the selection related methods in
 *     FunctionViewComponent need to be entirely redone.  These "stubs"
 *     were therefore useless and could cause problems if people tried to
 *     use them.
 *  3. Removed some commented out code that would initialize line colors
 *     and types, since this is now done elsewhere.
 *  4. Renamed setAxisInfo() method to setMaximumDisplayableWCRegion(),
 *     since that name is more descriptive.  This method did nothing with
 *     the AxisInfo objects, but just initializes the gjp's global world
 *     coordinate system to just fit the range covered by the data.
 *     This is yet another autoscaling operation.  However, when this
 *     is called in the constructor, the region set by this method is
 *     later changed when the RangeControl is built.  This method is
 *     also called by the dataChanged() method.
 *     This method should take into account whether the AxisInfo that
 *     was specified should be used.
 *
 *  Revision 1.95  2007/09/09 23:28:45  dennis
 *  Now only sends POINTED_AT_CHANGED message in response to a
 *  CURSOR_MOVED message, if this component did NOT itself request
 *  the move.  This fixes a problem with an infinite loop of
 *  POINTED_AT_CHANGED messages and a cursor update problem.
 *  Some cleanup:
 *    -removed unused local variable
 *    -removed some old code that was commented out.
 *
 *  Revision 1.94  2007/06/08 20:29:48  dennis
 *  setAxisInfo() now inverts the bounds to be consistent with
 *  other parts of the system.
 *  Also removed some unused code in setAxisInfo()
 *
 *  Revision 1.93  2007/03/12 15:00:53  amoe
 *  Added public method isDrawingPointedAtGraph() so external
 *  classes could determine if the FunctionViewComponent's
 *  pointedAtGraph is on.
 *
 *  Revision 1.92  2006/10/20 05:33:44  amoe
 *  - Added code to setPointedAt(), so the crosshairs would be updated.
 *  - Removed getDataPanel().
 *
 *  Revision 1.91  2006/08/09 19:16:28  amoe
 *  Added getDataPanel().  It returns the GraphJPanel gjp.
 *  (Dominic Kramer, Andrew Moe)
 *
 *  Revision 1.90  2006/07/25 20:43:27  amoe
 *  - Changed width from 50 to 65 on west white border pane.
 *  - Fixed javadoc.
 *
 *  Revision 1.89  2006/07/25 16:23:16  amoe
 *  - Changed vector transparancies to protected.
 *  - Made FunctionViewComponent self-referencing variable fvc.
 *  - Created protected initTransparancies() for the purpose of
 *    overloading it in DifferenceViewComponent.
 *
 *  Revision 1.88  2006/07/19 17:39:03  dennis
 *  Changed get/setPointedAt() methods to work with floatPoint2D,
 *  rather than java.awt.Point.  This change is needed so that
 *  the 1D view component deals with the PointedAt concept in
 *  the same coordinate system as the other view components.
 *
 *  Revision 1.87  2006/07/18 21:57:32  amoe
 *  Changed default precision from 4 to 6.
 *
 *  Revision 1.86  2006/07/14 21:42:15  amoe
 *  Changed GraphJPanel gjp border color to black.
 *
 *  Revision 1.85  2006/07/11 16:22:33  dennis
 *  Now does update of Axis info in dataChanged(). (Jim Kohl)
 *
 *  Revision 1.84  2006/07/10 21:01:10  amoe
 *  *** empty log message ***
 *
 *  Revision 1.84  2006/07/04 20:00:12  amoe
 *  Changed ControlCheckbox control_box to JMenuItem control_box.  
 *  Then re-instantiated it in getMenuItems().  This will allow the 
 *  existing code to correctly unselect the control_box when the 
 *  FunctionControls window is closed.
 *
 *  Revision 1.83  2006/06/22 19:39:58  amoe
 *  -changed 'private transient GraphJPanel gjp' to protected, for
 *  DifferenceViewComponent
 *  -changed 'private FunctionControls mainControls' to protected, for
 *  DifferenceViewComponent
 *  -updated javadoc entry for dataChanged(...)
 *
 *  Revision 1.82  2006/05/24 17:13:38  dennis
 *  Made dataChanged( VirtualArrayList1D ) always reconstruct the list
 *  of graphs. (Jim Kohl)
 *  Also, now always clear the graphs from the graph JPanel, removed
 *  a couple of unused variables and did some minor format cleanup.
 *
 *  Revision 1.81  2006/04/21 22:15:45  amoe
 *  -In buildViewComponent(), the surrounding panels around the 
 *   SelectedGraphView are now white on default.
 *
 *  -In FunctionViewComponent(), the SelectedGraphView is now outlined by a 
 *   gray line border on default.
 *
 *  Revision 1.80  2006/03/14 22:08:16  dennis
 *  Undid the default setting of Shift to "Diagonal", since most uses of
 *  this component require the graphs be "Overlaid".  (Tom Worlton)
 *
 *
 *  Revision 1.79  2006/02/05 23:53:39  amoe
 *
 *  Previous 1.78 is log incorrect.  Here is the correct log:
 *  -Added code to dataChanged() so that the legend would refresh when 
 *   new spectra are selected.
 *  -Edited code that sets the Shift ViewControl to "Diagonal", to use a 
 *   public static final int variable in FunctionControls when muliple 
 *   spectra are selected.
 *
 *  Revision 1.78  2006/02/05 20:20:21  amoe
 *  -Added code to dataChanged() so that the legend would refresh when new
 *   spectra are selected.
 *  -Modified control_list initialization to use static final int variables 
 *   instead of int numbers.
 *
 *  Revision 1.77  2006/01/05 20:34:43  rmikk
 *  Initialized the SHIFT value on the SHIFT control
 *
 *  Revision 1.76  2005/12/02 16:39:59  serumb
 *  Set "pointed at" graph color to black, so the default blue is not used
 *  when a graph is cleared.
 *
 *  Revision 1.75  2005/11/22 21:09:54  dennis
 *  Modified to point tick marks inward by default.
 *
 *  Revision 1.74  2005/11/11 20:29:12  serumb
 *  Set pointed at graph color to black, and set the graphs to be offset
 *  when two or more are selected.
 *
 *  Revision 1.73  2005/05/24 17:32:04  serumb
 *  Removed unneeded print statement.
 *
 *  Revision 1.72  2005/05/20 16:41:09  serumb
 *  Only draws the pointed at graph if no graphs are selected.
 *
 *  Revision 1.71  2005/05/19 21:47:35  serumb
 *  Fixed index out of bounds exception in the reInit() Method and
 *  changed the pointed at to not be displayed initially.
 *
 *  Revision 1.70  2005/03/28 05:58:52  serumb
 *  Now uses new methods from function controls for data changed and for
 *  initializing the controls with the ObjectState.
 *
 *  Revision 1.69  2005/03/11 19:47:30  serumb
 *  Added Object State and fixed eclipse warnings.
 *
 *  Revision 1.68  2005/02/04 22:51:16  millermi
 *  - Added sendMessage(POINTED_AT_CHANGED) to dataChanged() and
 *    sendMessage(SELECTED_CHANGED) to dataChanged(iva) so
 *    FunctionControls can be updated when dataChanged() is called.
 *
 *  Revision 1.67  2005/02/04 18:49:33  serumb
 *  Fixed zoom/pointed at problem when a selected line is cleared.
 *
 *  Revision 1.66  2005/02/01 03:14:20  millermi
 *  - Fixed pointed_at messages resetting zoom.
 *
 *  Revision 1.65  2005/01/11 15:34:09  dennis
 *  Commented out debug print.
 *
 *  Revision 1.64  2005/01/10 16:16:00  dennis
 *  Added check for existence of GraphData object before using it.
 *
 *  Revision 1.63  2004/11/12 03:36:19  millermi
 *  - Since the min/max of getAxisInformation() are no longer used
 *    to determine the zoom region by the AxisOverlay2D, the AxisInfo
 *    returned by getAxisInformation() now reflects global coordinates
 *    instead of local coordinates.
 *
 *  Revision 1.62  2004/11/05 22:01:43  millermi
 *  - Edited getAxisInformation() so getPositiveXmin() and getPositiveYmin()
 *    are called if the axis is specified as log.
 *  - Removed GaphJPanel().
 *  - Now implements ITruLogAxisAddible instead of ILogAxisAddible.
 *  - Removed getLogScale(), added getPositiveMin() to reflect change from
 *    ILogAxisAddible to ITruLogAxisAddible interface.
 *
 *  Revision 1.61  2004/09/29 20:41:42  serumb
 *  Removed unnecessary call to request focus.
 *
 *  Revision 1.60  2004/09/15 21:55:46  millermi
 *  - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *    Adding a second log required the boolean parameter to be changed
 *    to an int. These changes may affect any ObjectState saved configurations
 *    made prior to this version.
 *
 *  Revision 1.59  2004/09/13 22:30:46  serumb
 *  Added path to the view menu items, so the Display1DExample
 *  puts them in the options menu.
 *
 *  Revision 1.58  2004/07/15 19:29:25  dennis
 *  Fixed serious inefficiency problem when constructing a selected
 *  graph view from a DataSet with many spectra. (Ruth)
 *
 *  Revision 1.57  2004/07/02 19:24:36  serumb
 *  Moved the Function Controls and Pointed At Checkboxes to the options menu.
 *
 *  Revision 1.56  2004/06/17 16:45:17  serumb
 *  Repainted the components after zoom.
 *
 *  Revision 1.55  2004/06/16 22:09:10  serumb
 *  Repainted the transparencies after the components are rebuilt.
 *
 *  Revision 1.54  2004/06/10 23:27:09  serumb
 *  Now implements ILegendAddible.
 *
 *  Revision 1.53  2004/04/21 02:33:45  millermi
 *  - Removed call to setAxisInfo() in buildViewComponent().
 *
 *  Revision 1.52  2004/04/20 05:35:24  millermi
 *  - The construction of the big_picture was taken out of
 *    buildViewComponent and put into the constructor, so it is done
 *    only once.
 *
 *  Revision 1.51  2004/04/16 20:24:53  millermi
 *  - Now uses new methods from IVirtualArrayList1D.
 *
 *  Revision 1.50  2004/03/15 23:53:51  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.49  2004/03/12 22:56:31  serumb
 *  Now uses IVirtualArrayList1D in place of IVirtualArray1D.
 *
 *  Revision 1.48  2004/03/12 03:28:59  dennis
 *  Moved to package gov.anl.pns.ViewTools.Components.OneD
 *  (NOT COMPLETE!)
 *
 *  Revision 1.47  2004/03/10 23:37:28  millermi
 *  - Changed IViewComponent interface, no longer
 *    distinguish between private and shared controls/
 *    menu items.
 *  - Combined private and shared controls/menu items.
 *
 *  Revision 1.46  2004/03/10 19:37:28  serumb
 *  Added commands to remove overlay controls in the kill method.
 *
 *  Revision 1.44  2004/03/09 22:20:41  millermi
 *  - Added setData() after data was cleared in dataChanged()
 *    to reset the pointed at graph.
 *
 *  Revision 1.43  2004/03/09 20:56:38  serumb
 *  Initilized the pointed at graph to zero.
 *
 *  Revision 1.41  2004/03/09 17:28:37  serumb
 *  Fixed the problem that had to do with drawing selected graphs.
 *
 *  Revision 1.40  2004/02/27 20:24:41  serumb
 *  Removed unecessary print statments.
 *
 *  Revision 1.39  2004/02/20 19:11:57  serumb
 *  Fixed the way the axes info was being set.
 *
 *  Revision 1.36  2004/01/30 18:12:27  serumb
 *  Initilized the GraphJPanel with the Virtual Array and
 *  added object state data.
 *
 *  Revision 1.35  2004/01/09 20:33:31  serumb
 *  Utilize getLocalLogWorldCoords to correct log
 *  transformations.
 *
 *  Revision 1.34  2004/01/08 22:53:49  serumb
 *  Undo bad Y axis fix.
 *
 *  Revision 1.33  2004/01/08 20:45:23  serumb
 *  Fixed the y-axis decreasing on start-up problem.
 *
 *  Revision 1.32  2004/01/06 17:17:54  serumb
 *  The Function View Component now maintains graph data
 *   after a new line is selected.
 *
 *  Revision 1.31  2003/12/20 07:40:47  millermi
 *  - Changed paintComponents() to newer version in
 *    ImageViewComponent that is more efficient and
 *    consistent.
 *  - Added paintComponents() method to
 *    dataChanged(IVirtualArray1D) so the axes are updated
 *    when the data is changed.
 *
 *  Revision 1.30  2003/12/18 22:42:13  millermi
 *  - This file was involved in generalizing AxisInfo2D to
 *    AxisInfo. This change was made so that the AxisInfo
 *    class can be used for more than just 2D axes.
 *
 *  Revision 1.29  2003/10/31 18:13:19  dennis
 *  Now when the selected data is changed, the
 *  view controls are rebuilt in the same frame,
 *  so the control frame does not move around.
 *
 *  Revision 1.28  2003/10/21 22:01:49  serumb
 *  Implements all methods from IViewComponent.
 *
 *  Revision 1.27  2003/10/21 00:43:13  serumb
 *  Changed return types of methods getShared/PrivateMenuItems from
 *  JMenuItem to JViewMenuItem.
 *
 *  Revision 1.24  2003/08/08 21:08:08  serumb
 *  Now un-checks the Function Controls box when Function Controls window
 *  is closed.
 *
 *  Revision 1.22  2003/08/08 18:28:17  serumb
 *  Sends message to function controls letting it know to update x and y
 *  range info.
 *
 *  Revision 1.19  2003/08/06 16:26:13  serumb
 *  Function controls will not close when pointed at changes.
 *
 *  Revision 1.18  2003/08/05 23:24:31  serumb
 *  Now check for linear and log axes.
 *
 *  Revision 1.17  2003/07/31 16:50:34  serumb
 *  Draw the pointed at line to the correct zoomed region.
 *
 *  Revision 1.16  2003/07/30 20:58:44  serumb
 *  Implement LogAxisAddible2D.
 *
 *  Revision 1.15  2003/07/25 14:48:36  dennis
 *  Now implements IZoomTextAddible. (Needed to work with new heirarchy
 *  of interfaces for overlays.)
 *
 *  Revision 1.14  2003/07/17 20:38:54  serumb
 *  Implemented the dataChanged methods, and added methods for
 *  drawing the graphs.
 *
 *  Revision 1.13  2003/07/10 21:51:01  serumb
 *  Added control to split pane for showing the function controls
 *  and took out all all references to data sets.
 *
 *  Revision 1.12  2003/07/08 16:34:48  serumb
 *  Took out the controls and moved them to DataSetTools/
 *  components/View/ViewControls/FunctionControls.java.
 *
 *  Revision 1.10  2003/07/02 17:21:22  serumb
 *  Added a control for line shift change, added code comments,
 *  and moved the controls variables to buildViewControls().
 * 
 *
 */
package gov.anl.ipns.ViewTools.Components.OneD;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.Transparency.*;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;
import gov.anl.ipns.ViewTools.Components.Menu.*;
import gov.anl.ipns.ViewTools.Panels.Graph.*;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;
import gov.anl.ipns.ViewTools.UI.*;
import gov.anl.ipns.Util.Numeric.*;
import gov.anl.ipns.Util.Sys.WindowShower;

// component changes
import java.awt.*;
import java.awt.event.*;

// Component location and resizing within the big_picture
import java.io.Serializable;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class allows the user to view data in the form of an image. Meaning
 * is given to the data by way of overlays, which add calibration, selection,
 * and annotation abilities.
 */
public class FunctionViewComponent implements IViewComponent1D,
                                              ActionListener, 
                                              IZoomTextAddible,
                                              ITruLogAxisAddible,
                                              IPreserveState,
                                              Serializable,
                                              ILegendAddible
{
 /**
  * "Precision" - This constant String is a key for referencing the state
  *  information about the precision this view component will have. Precision
  *  affects the significant digits displayed on the Axis Overlay, among other
  *  things. The value that this key references is a primative integer, with
  *  value > 0.
  */
  public static final String PRECISION           = "Precision";

 /**
  * "Font" - This constant String is a key for referencing the state information
  *  about the font used by this view component. This font will also be
  *  passed on to overlays, such as the Axis, Annotation, and Selection.
  *  The value that this key references is of type Font.
  */
  public static final String FONT                = "Font";

 /**
  * "Pointed At Control" - This constant String is a key for referencing the 
  *  state information about wether or not to display the pointer at graph.
  */

  public static final String POINTED_AT_CONTROL  = "Pointed At Control";

 /**
  *  "Control Box" - This constant String is a key for referencing the state
  *   Information about wether or not to display the function controls box.
  */
  public static final String CONTROL_BOX         = "Control Box";

 /**
  * "Graph JPanel" - This constant String is a key for refferencing the
  * state information about the graph jpanel.
  */
  public static final String GRAPHJPANEL        = "Graph JPanel";
    
 /**
  * "AnnotationOverlay" - This constant String is a key for referencing the
  * state information about the Annotation Overlay. Since the overlay has its
  * own state, this value is of type ObjectState, and contains the state of
  * the overlay.
  **/
    public static final String ANNOTATION_OVERLAY  = "AnnotationOverlay";
    
 /**
  * "AxisOverlay2D" - This constant String is a key for referencing the state
  * information about the Axis Overlay. Since the overlay has its own state,
  * this value is of type ObjectState, and contains the state of the
  * overlay.
  */
      public static final String AXIS_OVERLAY_2D     = "AxisOverlay2D";
   
 /**
  * "LegendOverlay" - This constant String is a key for referencing the state
  * information about the Legend Overlay. Since the overlay has its own state,
  * this value is of type ObjectState, and contains the state of the
  * overlay
  */
      public static final String LEGEND_OVERLAY      = "LegendOverlay";
 /**
  * "FunctionControls" - This constant String is a key for referencing the 
  * state information about the FunctionControls. Since the control has its
  * own state, this value is of type ObjectState, and contains the state of
  * the controls.
  */
     public static final String FUNCTION_CONTROLS = "FunctionControls";       
      

  private boolean   autoscale;                     // Set true if the maximum displayable 
                                                   // region is determined by the actual
                                                   // data. Set false if the values passed
                                                   // in as AxisInfo in the VirtualArray
                                                   // are to be used.
  private transient IVirtualArrayList1D Varray1D;  //An object containing our
                                                   // array of data
  private transient Vector Listeners   = null;
  private transient JPanel big_picture = new JPanel();
  private transient JPanel background = new JPanel(new BorderLayout());
  protected transient GraphJPanel gjp;
  private final int MAX_GRAPHS = 20;

  private transient boolean ignore_pointed_at = false;
 
  // for component size and location adjustments
  private transient Rectangle regioninfo;
  protected transient Vector transparencies = new Vector(  );
  private int precision;
  private Font font;
  protected FunctionControls mainControls;
  private boolean draw_pointed_at = false;
  private JMenuItem control_box = new JMenuItem();
  protected FunctionViewComponent fvc;

  /**
   * Constructor that just takes in a virtual array.  The graph
   * region will be automatically scaled to fit the data.  Any 
   * information regarding the range of data in the AxisInfo 
   * objects stored in the virtual array is ignored.  
   *
   * @param varr The IVirtual array containing data for producing the graph.
   */
  public FunctionViewComponent( IVirtualArrayList1D varr ) {
    this( varr, true );
  }
  
  
  /**
   * Constructor that takes in a virtual array and a boolean flag.
   * The flag indicates whether the view should be auto scaled to
   * fit the data, or if the region specified by the AxisInfo objects
   * in the virtual array should be used to specifiy the region used.
   *
   * @param varr       The IVirtual array containing data for 
   *                   producing the graph.
   * @param autoscale  Flag indicating whether to autoscale or use
   *                   the AxisInfo from the virtual array to 
   *                   specifiy the region covered by the graph.
   *                   If this parameter is true, the region will
   *                   be automatically determined based on the 
   *                   region covered by the data.
   */
  public FunctionViewComponent( IVirtualArrayList1D varr, boolean autoscale ) {

    this.autoscale =  autoscale;    // record autoscaling preference
    
    Varray1D    = varr;  // Get reference to varr
    precision   = 6;
    font        = FontUtil.LABEL_FONT2;
    gjp         = new GraphJPanel(  );
    
    fvc = this;
   
    //initialize selected graphs
    int num_lines = varr.getNumSelectedGraphs(  );
    float x[];
    float y[];
    for( int i = 0; i < num_lines; i++ ) {
       x = Varray1D.getXValues(i);
       y = Varray1D.getYValues(i);
       gjp.setData( x, y, i+1, false );     // graph0 reserved for pointed at
    }
    gjp.setBackground( Color.white );
    gjp.setBorder(new LineBorder(Color.black));
    
    setMaximumDisplayableWCRegion();
    GraphListener gjp_listener = new GraphListener(  );

    gjp.addActionListener( gjp_listener );

    ComponentAltered comp_listener = new ComponentAltered(  );

    gjp.addComponentListener( comp_listener );

    regioninfo = new Rectangle( gjp.getBounds(  ) );

    Listeners = new Vector(  );
    
    if( varr.getNumGraphs(  ) > 0 )
    {
      buildViewComponent();     // initializes big_picture to jpanel containing
                                // the background and transparencies
      initTransparancies();
      OverlayLayout overlay = new OverlayLayout( big_picture );
      
      big_picture.setLayout( overlay );

      for( int trans = 0; trans < transparencies.size(  ); trans++ ) {
     	big_picture.add( ( OverlayJPanel )transparencies.elementAt( trans ) );
      }
      big_picture.add( background );
   
      if(Varray1D.getNumSelectedGraphs() > 0)
      {
        draw_pointed_at = false;
      }
      else
      {
        draw_pointed_at = true;
      }

      DrawSelectedGraphs();
      if(draw_pointed_at)
        DrawPointedAtGraph();
      
    //initialize pointed_at graph
    int pointed_at_index = Varray1D.getPointedAtGraph();
    gjp.setColor(Color.black, 0, false);
    Draw_GJP( pointed_at_index, 0 );
    
      mainControls = new FunctionControls(varr, gjp, getDisplayPanel(),this);
      mainControls.get_frame().addWindowListener( new FrameListener() );

      if(Varray1D.getNumSelectedGraphs() > 1)
      {
    	  //Retrieving viewcontrol list 
    	  //ViewControl[] vcontrol = mainControls.getControlList();
    	  
          // Comment out the switch to diagonal shift of spectra
          //vcontrol[FunctionControls.VC_SHIFT].setControlValue(new Integer(2));
    	  
    	  /*System.out.println("TITLE\tCONT-VALUE");    	  
    	  for(int a = 0;a<vcontrol.length;a++)
    	  {
            System.out.println("["+a+"] ." + vcontrol[a].getTitle() + 
                               ".\t" + vcontrol[a].getControlValue());
    	  }*/     	  
    	  
    	  gjp.setMultiplotOffsets((int)(20 ),
                                (int)( 20 ));
    	  gjp.repaint(); 
       }
    }
    else
    {
      JPanel no_graph = new JPanel();
      no_graph.add( new JLabel("No Graphs to Display") );
      big_picture.add( no_graph );
    }
  }


  /**
   * Constructor that takes in a virtual array and creates an graphjpanel
   * to be viewed in a border layout.
   *
   *  @param varr  The IVirtual array containing data for producing the graph.
   *  @param state The state of a previous Function View Component.
   */
  public FunctionViewComponent( IVirtualArrayList1D varr, ObjectState state ) {

    this(varr);
    setObjectState(state);
  }

/**
 * Creates a simple instance of a FunctionViewComponent with the given
 * parameters
 * 
 * @param x_values    The x values to display
 * @param y_values    The y values to display
 * @param errors      The errors or null
 * @param Title       The Title 
 * @param x_units     The units for the x values( or null)
 * @param y_units     The units for the y values( or null)
 * @param x_label     The label for the x values( or null)
 * @param y_label     The label for the x values( or null)
 * @return A FucntionViewComponent with the appropriate information set or
 *         null if an error occurs
 * 
 * NOTE: The axis information, overlays settings, etc. can be subsequently
 *    changed via handles from this FunctionViewComponent.
 *    @see #getControlList() For the list of controls
 *    @see #dataChanged(IVirtualArrayList1D) To change the data
 *    @see FunctionControls#get_frame()    To display Function controls 
 */
  public static FunctionViewComponent getInstance( float[]x_values, 
                                                   float[]y_values, 
                                                   float[]errors, 
                                                   String Title,
                                                   String x_units,
                                                   String y_units,
                                                   String x_label,
                                                   String y_label)
  {
    if(x_values == null || y_values == null)
       return null;
    
    if( x_values.length !=y_values.length && x_values.length != y_values.length +1 )
       return null;
    
    try
    {
     DataArray1D data;
     if( errors == null)
        data = new DataArray1D( x_values, y_values);
     else
        data = new DataArray1D( x_values, y_values, errors);
     
     VirtualArrayList1D  vList = new VirtualArrayList1D( data);
     
     if( Title != null)
          vList.setTitle(  Title );
     
     setAxis( AxisInfo.X_AXIS, vList, x_units,x_label);
     setAxis( AxisInfo.Y_AXIS, vList, y_units,y_label);
     
     return new FunctionViewComponent( vList, true) ;
     
    }catch( Exception s)
    {
       JOptionPane.showMessageDialog( null , "Could Not create Graph. Error= "+s );
       return null;
    }
    
  }
  
  
  /**
   * Creates a simple instance of a FunctionViewComponent with the given
   * parameters
   * 
   * @param x_values    The x values to display
   * @param y_values    The y values to display
   * @param errors      The errors or null
   * @param Title       The Title for titled border if not null
   * @param x_units     The units for the x values( or null)
   * @param y_units     The units for the y values( or null)
   * @param x_label     The label for the x values( or null)
   * @param y_label     The label for the x values( or null)
   * @return  JPanel with one Graph with given information
   * 
   * NOTE: To change to logarithmic axes, change line colors/styles,turn on 
   *      other overlays, see the getInstance method
   *      @see #getInstance(float[], float[], float[], String, String, String, String, String)
   */
  public static JPanel ShowGraphWithAxes( float[]x_values, 
                                float[]y_values, 
                                float[]errors, 
                                String Title,
                                String x_units,
                                String y_units,
                                String x_label,
                                String y_label)
  {
     String prop_str = System.getProperty("ShowWCToolTip");
     System.setProperty("ShowWCToolTip","true");
     FunctionViewComponent Fcomp = FunctionViewComponent.getInstance( x_values,
                                                                     y_values,
                                                                     errors,
                                                                     Title,
                                                                     x_units,
                                                                     y_units,
                                                                     x_label,
                                                                     y_label);
     if(Fcomp == null)
        return null;
    
    JPanel panel = Fcomp.getDisplayPanel();
    
    if(prop_str == null)
       System.clearProperty( "ShowWCToolTip" );
    else
       System.setProperty(  "ShowWCToolTip" , prop_str );
    
    return panel;
    
    
  }

  private static void setAxis( int                 axis, 
                               IVirtualArrayList1D vList, 
                               String              units, 
                               String              label)
  {
     if( units == null && label == null )
         return;

      AxisInfo X = vList.getAxisInfo( axis );
      
      if( units == null )
         units = X.getUnits();
      
      if( label == null )
         label = X.getLabel();
      
      vList.setAxisInfo( axis , X.getMin() , X.getMax() , label , units , 1 );
    
  }

 /**
    * This method will set the current state variables of the object to state
    * variables wrapped in the ObjectState passed in.
    * setState() and getState() are required by IPreserveState interface
    * 
    * @param new_state
    */
  public void setObjectState( ObjectState new_state )
  {
    Object temp = new_state.get(GRAPHJPANEL);
    if ( temp != null){
      gjp.setObjectState( (ObjectState)temp );
    }

    temp = new_state.get(PRECISION);
    if ( temp != null){
      precision = ((Integer)temp).intValue();
    }
    
    temp = new_state.get(FONT);
    if( temp != null ){
      font = (Font)temp;
    }

    temp = new_state.get(POINTED_AT_CONTROL);
    if( temp != null ){
      draw_pointed_at = ((Boolean)temp).booleanValue();
    }

    temp = new_state.get(CONTROL_BOX);
    if( temp != null ){
      control_box.setSelected( ((Boolean)temp).booleanValue() );
    }
    
    temp = new_state.get(LEGEND_OVERLAY);
    if (temp != null)
    {
       ((OverlayJPanel)transparencies.elementAt(0)).setObjectState(
						(ObjectState)temp);
    }
    
    temp = new_state.get(ANNOTATION_OVERLAY);
    if (temp != null)
    {
       ((OverlayJPanel)transparencies.elementAt(1)).setObjectState(
						(ObjectState)temp);
    }       
    
    temp = new_state.get(AXIS_OVERLAY_2D);
    if (temp != null)
    {
       ((OverlayJPanel)transparencies.elementAt(2)).setObjectState(
						(ObjectState)temp);
    }
    
    temp = new_state.get(FUNCTION_CONTROLS);
    if (temp != null)
    {
       mainControls.setObjectState((ObjectState)temp);
    }       
  } 


  /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState. Keys will be
  * put in alphabetic order.
  */
  public ObjectState getObjectState(boolean isDefault)
  {
    ObjectState state = new ObjectState();
    state.insert( ANNOTATION_OVERLAY,
      ((OverlayJPanel)transparencies.elementAt(1)).getObjectState(isDefault) );
    state.insert( AXIS_OVERLAY_2D,
      ((OverlayJPanel)transparencies.elementAt(2)).getObjectState(isDefault) );
    state.insert( CONTROL_BOX, new Boolean( control_box.isSelected() ));
    state.insert( FONT, font);
    state.insert( FUNCTION_CONTROLS, mainControls.getObjectState(isDefault) );
    state.insert( GRAPHJPANEL, gjp.getObjectState(isDefault) );
    state.insert( LEGEND_OVERLAY, 
      ((OverlayJPanel)transparencies.elementAt(0)).getObjectState(isDefault) );
    state.insert( POINTED_AT_CONTROL, new Boolean(draw_pointed_at) );
    state.insert( PRECISION, new Integer(precision) );
    
    if(! isDefault){
    }

    return state;
  } 


  // getAxisInfo(), getRegionInfo(), getTitle(), getPrecision(), getFont() 
  // all required since this component implements IAxisAddible2D

  /**
   * This method initializes the world coords.
   */
  private void setMaximumDisplayableWCRegion() {
    CoordBounds bounds = new CoordBounds( gjp.getXmin(), gjp.getYmin(),
                                          gjp.getXmax(), gjp.getYmax() );
    bounds.scaleBounds(1, gjp.getScaleFactor() );
    bounds.invertBounds();
    gjp.initializeWorldCoords( bounds );
  }

 
  public AxisInfo getAxisInformation( int axis ) {

    // if true, return x info
    if( axis == AxisInfo.X_AXIS) {
      if(gjp.getLogScaleX() == true) {
        return new AxisInfo( 
	        gjp.getPositiveXmin(),
            gjp.getXmax(),
            Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getLabel(  ),
            Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getUnits(  ),
            AxisInfo.TRU_LOG );
      }
      else
      {
        return new AxisInfo( 
            gjp.getXmin(),
            gjp.getXmax(),
            Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getLabel(  ),
            Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getUnits(  ),
            AxisInfo.LINEAR );
       }
    }

    // if false return y info
    if(gjp.getLogScaleY() == true) {
      return new AxisInfo(
          gjp.getPositiveYmin(),
          gjp.getYmax(),
     	  Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getLabel(  ),
     	  Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getUnits(  ),
     	  AxisInfo.TRU_LOG );
    }
    else
    {
      return new AxisInfo( 
     	  gjp.getYmin(),
     	  gjp.getYmax(),
     	  Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getLabel(  ),
     	  Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getUnits(  ),
     	  AxisInfo.LINEAR );
    }
  }

   
  /**
   * This method returns a rectangle containing the location and size
   * of the graphjpanel.
   *
   *  @return The region info about the graphjpanel
   */
  public Rectangle getRegionInfo(  ) {
    return regioninfo;
  }


  /**
   * This method will return the title given to the image as specified by
   * the Virtual Array
   *
   *  @return title stored in Virtual Array
   *
   */
  public String getTitle(  ) {
    return Varray1D.getTitle(  );
  }


  /**
   * This method will return the precision specified by the user. Precision
   * will be assumed to be 4 if not specified. The overlays will call
   * this method to determine the precision.
   *
   *  @return precision of displayed values
   */
  public int getPrecision(  ) {
    return precision;
  }


  /**
   * This method will return the font used on by the overlays. The axis overlay
   * will call this to determine what font to use.
   *
   *  @return font of displayed values
   */
  public Font getFont() {
    return font;
  }


 // methods required by legend interface

  /**
   *  This method will return the graphic for the legend.
   *
   *  @return graphic to be displayed
   */
   public GraphData getGraphData(int graph){
     GraphData data = null;

     if ( gjp != null        && 
          gjp.graphs != null && 
          graph > 0          && 
          graph < gjp.graphs.size() )
       data = ( GraphData )gjp.graphs.elementAt( graph );
    
     return data; //test 
   }


  /**
   *  This method will return the text for the legend.
   *
   *  @return text to be displayed
   */
   public String getText(int graph){
   return Varray1D.getGraphTitle( graph );
   }


  /**
   *  This method will return the selected graphs for the legend.
   *
   *  @return the selected graphs
   */
   public int[] getSelectedGraphs(){
   return Varray1D.getSelectedIndexes();
   }


  /**
   * This function will return an array of 17 ViewControls 
   *  which are used by the Function View Component. 
   *  <table border ="1" ><tr><th>Index</th><th>Function</th><th>get/set ControlValues</th></tr>
   *<tr><td>  [0]</td><td> Control to choose a selected line.<BR> (Line Selected)</td><td></td></tr>
   *<tr><td> [1] </td><td>Control to choose the style of the <BR>chosen line. (Line Style)</td>
   *                                    <td>0,1..4 for Solid,Dashed,<BR>Dotted,Dash Dot Dot, Transparent<BR></td></tr>
   *<tr><td>  [2]</td><td> Control to choose the thickness of the <BR>chosen line. (Line Width)</td>
   *                                   <td>0,1,..4 for widths<BR> 1,2,3,4,and 5</td></tr>
   *<tr><td>  [3] </td><td> Control to display point markers for<BR> the chosen line. (Point Marker)</td>
   *                                    <td>0(DOT),1(PLUS),2(STAR),3(BOX),<BR>4(CROSS),5(BAR),6(No Mark)</td></tr>
   *<tr><td>  [4] </td><td> Control to choose the size of the point<BR> markers for the line. (Point Marker Size)</td>
   *                                   <td> 0,1,..4 for sizes 1,2,<BR>3,4, and 5</td></tr>
   *<tr><td>  [5] </td><td> Control to display error bars for the<BR> chosen line. (Error Bars)</td>
   *                                     <td>0(None),1(At Points),2(At top)</td></tr>
   * <tr><td> [6] </td><td> Button Control to select the color of <BR>the chosen line. (Line Color)</td>
   *                                    <td>java.awt.Color</td></tr>
   *<tr><td>  [7] </td><td> Button Control to select the color of <BR>the point markers. (Point Marker Color)</td>
   *                                    <td>java.awt.Color</td></tr>
   *<tr><td>  [8]</td><td> Button Control to select the color of <BR>the error bars. (Error Bar Color)</td>
   *                                   <td>java.awt.Color</td></tr>
   *<tr><td>  [9] </td><td> Control to offset the selected lines. <BR>(Shift)</td>
   *                                  <td>0(Diag),1(Vert),2(Overlaid)</td></tr>
   * <tr><td> [10]</td><td> Control to set a shift factor to offset<BR> the selected lines by. (Shift Factor)</td>
   *                                  <td>0,1,or 2 for <BR> factors 1,1.5, and 2</td></tr>
   *<tr><td>  [11]</td><td> Control to select the axis overlay. <BR>(Axis Checkbox)</td>
   *                                 <td>Boolean</td></tr>
   *<tr><td>  [12]</td><td> Control to select the annotation <BR>overlay. (Annotation Checkbox)</td>
   *                                  <td>Boolean</td></tr>
   *<tr><td>  [13]</td><td> Control to select the legend overlay. <BR>(Legend Checkbox)</td>
   *                                  <td>Boolean</td></tr>
   *<tr><td>  [14]</td><td> Control to select a range for the <BR>graph to display. (Graph Range)</td>
   *                                   <td>Vector<float[2]> each float[2]=[min,max]</td></tr>
   * <tr><td> [15]</td><td> Control to show the location of the <BR>cursor. (Cursor)</td><td></td></tr>
   * <tr><td> [16]</td><td> Control to display logarithmic axes.<BR> (Logarith Axes)</td>
   *                         <td>0(Neither),1(X-axis Only)<BR>,2(Y axis Only), 3(Both Axes)</td></tr>
   *</table>
   *  @return ViewControl[] the array of view controls
   *  
   *  Note: An outside user should be able to programmatically change these values by using
   *  the set and getControlValues with the given values.
   *  
   *  
   */
   public ViewControl[] getControlList()
   {
     return mainControls.getControlList();
   } 


  /**
   * This method will return the local coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
  public CoordBounds getLocalCoordBounds(  ) {
    return gjp.getLocalWorldCoords(  ).MakeCopy(  );
  }


  /**
   * This method will return the global coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
  public CoordBounds getGlobalCoordBounds(  ) {
    return gjp.getGlobalWorldCoords(  ).MakeCopy(  );
  }


  /**
   * This method will return the virtual array. 
   */
  public IVirtualArrayList1D getArray(  ) {
    return Varray1D;
  }

  
 /**
  * This method will get the minimum positive value for the x and y axis.
  * The value returned will be greater than zero if a valid axis is passed..
  *
  *  @param  axis The axis to get the positive minimum. Use the static ints
  *               provided by the ITruLogAxisAddible interface.
  *  @return The smallest positive float value for the specified axis.
  */ 
  public float getPositiveMin( int axis )
  {
    // If x axis, return smallest positive x.
    if( axis == ITruLogAxisAddible.X_AXIS )
      return gjp.getPositiveXmin();
    // If y axis, return smallest positive y.
    if( axis == ITruLogAxisAddible.Y_AXIS )
      return gjp.getPositiveYmin();
    // Invalid axis, return zero.
    return 0;
  }


 /**
  * This method will get the scale factor used, if any, to alter the size of
  * the bounds. A scale factor of 1.05 will increase the bounds by 5% while
  * a scale factor of .8 will decrease the bounds by 20%.
  */
  public float getBoundScaleFactor( int axis )
  {
    // If y axis, return smallest positive y.
    if( axis == ITruLogAxisAddible.Y_AXIS )
      return gjp.getScaleFactor();

    return 1f;
  }

  public boolean isDrawingPointedAtGraph()
  {
    return draw_pointed_at;
  }
  
  /**
   * This method adjusts the crosshairs on the graphjpanel.
   * setPointedAt is called from the viewer when another component
   * changes the selected point.
   *
   *  @param  pt
   */
  public void setPointedAt( floatPoint2D pt ) {
    //System.out.println( "Entering: void setPointedAt( Point pt )" );
    //System.out.println( "X value = " + pt.getX(  ) );
    //System.out.println( "Y value = " + pt.getY(  ) );
    //set the cursor position on GraphJPanel

    floatPoint2D graph_pt = new floatPoint2D(pt.x, gjp.getY_value(pt.x,0));
    gjp.setCurrent_WC_point( graph_pt );
    gjp.set_crosshair_WC(new floatPoint2D( graph_pt.x, graph_pt.y ) );
    ignore_pointed_at = true;
  }


  /**
   * This method will be called to notify this component of a change in data.
   */
  public void dataChanged(  ) {
	  
       if(draw_pointed_at)
       {
         DrawPointedAtGraph();         
       }
       paintComponents(big_picture.getGraphics());
       //System.out.println("FVC.datachanged().gjp.isDoingBox()");

       sendMessage(POINTED_AT_CHANGED); 
       //System.out.println( "FunctionViewComponent.dataChanged()" );
    }
  

  /**
   * This method changes the array of data being displayed and 
   * updates the display accordingly.  The title and x and y axis labels
   * and units are all retained from the current graph.  If this information
   * is being changed as well, it would be best to construct a whole new
   * function view component. 
   * 
   * @param pin_varray The IVirtualArrayList1D containing the new data. 
   * 
   */
  public void dataChanged( IVirtualArrayList1D pin_varray ) //pin == "passed in"
  {
    gjp.clearData();          // since any of the graphs might have changed, we
                              // clear out all stored copies and start over.

                              // The 0th graph in the gjp will be used for the
                              // pointed at graph.  By default, use the 0th
                              // entry in the virtual array.
    //System.out.println( "FunctionViewComponent.dataChanged(..)" );
    float[] x_vals = pin_varray.getXValues(0);
    float[] y_vals = pin_varray.getYValues(0);
    gjp.setData(x_vals,y_vals, 0, false);
    setMaximumDisplayableWCRegion();         // force update of Axis (Jim Kohl)
 
                                             // get the name, units and title
                                             // from the old virtual array list
    /*//Why?? not needed if all data changes
     *  AxisInfo x_info = pin_varray.getAxisInfo(AxisInfo.X_AXIS); 
    AxisInfo y_info = pin_varray.getAxisInfo(AxisInfo.Y_AXIS); 
    setAxis( AxisInfo.X_AXIS, pin_varray, 
                              x_info.getUnits(), x_info.getLabel() );
    setAxis( AxisInfo.Y_AXIS, pin_varray, 
                              y_info.getUnits(), y_info.getLabel() );
    //pin_varray.setTitle( Varray1D.getTitle() );
 
     */
    Varray1D = pin_varray;
                              // rebuild controls for the new data IN THE 
                              // SAME FRAME, so that the frame doesn't move.
    mainControls.dataChanged(Varray1D);
    DrawSelectedGraphs();
    dataChanged();
    transparencies.set( 0,  new LegendOverlay(this) );
    big_picture.removeAll();
      for( int trans = 0; trans < transparencies.size(  ); trans++ ) {
        big_picture.add( ( OverlayJPanel )transparencies.elementAt( trans ) );
      }
      big_picture.add( background );

    //      System.out.println("Value of first element: " + x_array[0] +
    //							y_array[0] );
    //System.out.println( "Thank you for notifying us" );
    //System.out.println( "" );
    sendMessage(SELECTED_CHANGED);
    
    //check if the Legend is on.  If it is, refresh it.
    ViewControl[] vcontrol = mainControls.getControlList();  
  	
    if( Boolean.TRUE.equals(
             vcontrol[FunctionControls.VC_LEGEND_CHECKBOX].getControlValue()) )
    {
      ((LegendOverlay)transparencies.elementAt(0)).setVisible(true);    	
    }
  }


  /**
   * Method to add a listener to this component.
   *
   *  @param act_listener
   */
  public void addActionListener( ActionListener act_listener ) {
    //System.out.print( "Entering: void " );
    //System.out.println( "addActionListener( ActionListener act_listener )" );

    for( int i = 0; i < Listeners.size(  ); i++ )  // don't add it if it's

     {
      if( Listeners.elementAt( i ).equals( act_listener ) ) {  // already there

        return;
      }
    }

    Listeners.add( act_listener );  //Otherwise add act_listener
    //System.out.println( "" );
  }


  /**
   * Method to remove a listener from this component.
   *
   *  @param act_listener
   */
  public void removeActionListener( ActionListener act_listener ) {
    Listeners.remove( act_listener );
  }


  /**
   * Method to remove all listeners from this component.
   */
  public void removeAllActionListeners(  ) {
    Listeners.removeAllElements(  );
  }


  public ViewControl[] getControls(  ) {
     return new ViewControl[0];
  }


  public ViewMenuItem[] getMenuItems(  ) {
   
   if( Varray1D.getNumGraphs(  ) < 1 )
     return new ViewMenuItem[0];
   
   ViewMenuItem[] Res = new ViewMenuItem [2];
   
   Res[0] = new ViewMenuItem(ViewMenuItem.PUT_IN_OPTIONS, 
		   new JCheckBoxMenuItem("Function Controls"));
   (Res[0]).addActionListener( new ControlListener() );

   Res[1] = new ViewMenuItem (ViewMenuItem.PUT_IN_OPTIONS, 
		   new JCheckBoxMenuItem("Show Pointed At"));
   ((JCheckBoxMenuItem)Res[1].getItem()).setState(draw_pointed_at);
   (Res[1]).addActionListener( new ControlListener() );
   
   //setting the function controls menu item
   control_box = Res[0].getItem();

   return Res;

    //return new ViewMenuItem[0];
  }


  /**
   * Return the "background" or "master" panel
   *
   *  @return JPanel containing graphjpanel in the center of a borderlayout.
   */
  public JPanel getDisplayPanel(  ) {
    return big_picture;
  }


  /*
   *  Gets the current point
   */
  public floatPoint2D getPointedAt(  ) {
    floatPoint2D fpt = new floatPoint2D(  );

    fpt = gjp.getCurrent_WC_point(  );

     return fpt;
  }

 
 public void paintComponents()
 {
   paintComponents(big_picture.getGraphics()); 
 }
 
 protected void initTransparancies()
 {
	 //create transparencies
     AnnotationOverlay top = new AnnotationOverlay( this );
     top.setVisible( false );    // initialize this overlay to off.

                                 // Default ticks inward for this component
     AxisOverlay2D bottom = new AxisOverlay2D( this, true );

     LegendOverlay leg_overlay = new LegendOverlay( this );
  
     transparencies.add( leg_overlay ); 
     transparencies.add( top );
     transparencies.add( bottom );  // add the transparency to the vector     
 }
 
  /*
   * Tells all listeners about a new action.
   *
   *  @param  message
   */
  private void sendMessage( String message ) {

    for( int i = 0; i < Listeners.size(  ); i++ ) {
      ActionListener listener = ( ActionListener )Listeners.elementAt( i );
      listener.actionPerformed( new ActionEvent( this, 0, message ) );
    }

  }


  private void paintComponents( Graphics g ) {
	  //System.out.println("paintComponents()");
    if( g != null )
    {
      big_picture.update(g);
    }
    Component temppainter = big_picture;
    while( temppainter.getParent() != null )
      temppainter = temppainter.getParent();
    temppainter.repaint();
    for( int next = 0; next < transparencies.size(  ); next++ ) {
       ( ( OverlayJPanel )transparencies.elementAt( next ) ).repaint(  );
    }
  }


  private boolean DrawPointedAtGraph() {
    int pointed_at_line = Varray1D.getPointedAtGraph();
    float[] x_vals = Varray1D.getXValues(pointed_at_line);
    float[] y_vals = Varray1D.getYValues(pointed_at_line);
    gjp.setColor( Color.black, 0, false );
    if(pointed_at_line >= 0) {
      gjp.updatePointedAtGraph(x_vals,y_vals);
      return true;
    }
    return false;
  }

/*
  private void reInit(){

    if( Varray1D.getNumGraphs(  ) > 0 )
    {
      if( Varray1D.getNumSelectedGraphs(  ) > 0 )
      {
        buildViewComponent();  // initializes big_picture to jpanel containing
                                  // the background and transparencies
        DrawSelectedGraphs();
        if(draw_pointed_at)
          DrawPointedAtGraph();

        mainControls.reInit();
      }
    }
    else
    {
      JPanel no_graph = new JPanel();
      no_graph.add( new JLabel("No Graphs to Display") );
      big_picture.add( no_graph );
    }
  }  
*/  

  private int DrawSelectedGraphs() {
    int draw_count = 0;

    int num_graphs = Varray1D.getNumGraphs();

      for(int i=0; i < num_graphs && draw_count < MAX_GRAPHS; i++) {
    
        if( Varray1D.isSelected(i) ) {
            draw_count++;
            Draw_GJP( i, draw_count);
        }
      }

   // if( DrawPointedAtGraph() )
   //   draw_count++;
    
    return draw_count;
  }


  private void Draw_GJP( int index, int graph_num )
  {
       float x[] = Varray1D.getXValues(index);
       float y[] = Varray1D.getYValues(index);
     // gjp.setColor( Color.black, graph_num, false );
       gjp.setData( x, y, graph_num, false );     
     //  gjp.setErrors( Varray1D.getErrorVals_ofIndex( index ), 0, 
     //                                        graph_num, true);
       if(!draw_pointed_at)
         gjp.setTransparent(true, 0, true);    
       else 
         gjp.setColor( Color.black, 0, true );
  }

   
  /**
   * To be continued.
   **/
   public void kill(){
     // only call methods on mainControls if they have been constructed.
     if( mainControls != null )
     {
       mainControls.kill();
       mainControls.get_frame().dispose();
     }
     // only call kill on transparencies if they have been initialized.
     if( transparencies == null )
       return;
     for( int trans = 0; trans < transparencies.size(); trans++ )
      ((OverlayJPanel)transparencies.elementAt(trans)).kill();
   }
   
  // required since implementing ActionListener


  /**
   * To be continued...
   */
  public void actionPerformed( ActionEvent e ) {

    //get POINTED_AT_CHANGED or SELECTED_CHANGED message from e 
    String message = e.getActionCommand(  );

    //Send message to tester 
    if( message.equals(POINTED_AT_CHANGED) ) {
      sendMessage( POINTED_AT_CHANGED );
    }
    else if( message.equals(SELECTED_CHANGED) ) {
      sendMessage (SELECTED_CHANGED);
    }
  }


  /*
   * This method takes in an graphjpanel and puts it into a borderlayout.
   * Overlays are added to allow for calibration, selection, and annotation.
   */
  private void buildViewComponent() {/*
    if( gjp.getXmin() == gjp.getXmax() || gjp.getYmin() == gjp.getYmax() )
    {
      JPanel no_graph = new JPanel();
      no_graph.add( new JLabel("No Graphs to Display") );
      background.add( no_graph, "Center" );
    }*/
    //setAxisInfo();
    int westwidth  = ( font.getSize(  ) * precision ) + 22;
    int southwidth = ( font.getSize(  ) * 3 ) + 22;
    background.removeAll();

    JPanel north = new JPanel( new FlowLayout(  ) );

    north.setPreferredSize( new Dimension( 0, 25 ) );
    north.setBackground(Color.white);
    
    JPanel east = new JPanel( new FlowLayout(  ) );

    east.setPreferredSize( new Dimension( 65, 0 ) );
    east.setBackground(Color.white);

    JPanel south = new JPanel( new FlowLayout(  ) );

    south.setPreferredSize( new Dimension( 0, southwidth ) );
    south.setBackground(Color.white);

    JPanel west = new JPanel( new FlowLayout(  ) );

    west.setPreferredSize( new Dimension( westwidth, 0 ) );
    west.setBackground(Color.white);
    //Construct the background JPanel
    background.add( gjp, "Center" );
    background.add( north, "North" );
    background.add( west, "West" );
    background.add( south, "South" );
    background.add( east, "East" );
  }

 /* 
  //Test program for ShowGraphWithAxes
  public static void main( String[] args)
  {
     float[]x={1f,2f,3f,4f,5f,6f};
     float[]y ={1.1f,1.1f,2.2f,2.2f,3.3f};
     JFrame jf = new JFrame("Test");
     jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
     jf.setSize( 400,400 );
     jf.getContentPane().add( FunctionViewComponent.ShowGraphWithAxes(x,y,null,
              null,null,"ghi","lmn","opq"));
     WindowShower.show(jf);
     
     
  }
  */
  /*
   * MAIN - Basic main program to test a FunctionViewComponent object
   */
  public static void main1( String[] args ) {
    if( args == null ) {
      System.exit( 0 );
    }
   
    
/*
    DataSet[] DSS = ( new IsawGUI.Util(  ) ).loadRunfile( args[0] );

    int k = DSS.length - 1;

    DSS[k].setSelectFlag( 0, true );
    DSS[k].setSelectFlag( 3, true );

    DataSetData ArrayHandler = new DataSetData( DSS[k] );

    AxisInfo xaxis = ArrayHandler.getAxisInfo( AxisInfo.X_AXIS );
    AxisInfo yaxis = ArrayHandler.getAxisInfo( AxisInfo.Y_AXIS );

    System.out.println( 
      "ArrayHandler info" + xaxis.getMax(  ) + "," + xaxis.getMin(  ) + "," +
      yaxis.getMax(  ) + "," + yaxis.getMin(  ) );

    if( java.lang.Float.isNaN( xaxis.getMax(  ) ) ) {
      try {
        int c = System.in.read(  );
      } catch( Exception sss ) {}
    }
    FunctionViewComponent fvc = new FunctionViewComponent( ArrayHandler );

    //A tester frame to throw the bottom and top JPanel into **********
    JFrame f = new JFrame( "ISAW FunctionViewComponent" );

    f.setBounds( 0, 0, 500, 500 );

    Container c = f.getContentPane(  );

    c.add( fvc.getDisplayPanel(  ) );

    f.show(  );  //display the frame

    JFrame f2       = new JFrame( "ISAW GraphViewControls" );
    Container cpain = f2.getContentPane(  );

    cpain.setLayout( new BoxLayout( cpain, BoxLayout.Y_AXIS ) );

    JComponent[] controls = fvc.getControls(  );

    for( int i = 0; i < controls.length; i++ ) {
      cpain.add( controls[i] );
    }

    f2.setBounds( 0, 0, 200, ( 100 * controls.length ) );
    cpain.validate(  );
    f2.show(  );  //display the frame
*/
  }

  //***************************Assistance Classes******************************

  /*
   * ComponentAltered monitors if the graphjpanel has been resized. If so,
   * the regioninfo is updated.
   */
  private class ComponentAltered extends ComponentAdapter {
    //~ Methods ****************************************************************

    public void componentResized( ComponentEvent e ) {
     //System.out.println("Component Resized");
      Component center = e.getComponent(  );
     
      regioninfo = new Rectangle( center.getLocation(  ), center.getSize(  ) );
      paintComponents(big_picture.getGraphics()); 
      /*
         System.out.println("Location = " + center.getLocation() );
         System.out.println("Size = " + center.getSize() );
         System.out.println("class is " + center.getClass() );
       */
    }
  }

  /*
   * GraphListener monitors if the graphjpanel has sent any messages.
   * If so, process the message and relay it to the viewer.
   */
  private class GraphListener implements ActionListener {
    //~ Methods ****************************************************************

    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );
      
      //System.out.println("FunctionViewComponent...isDoingBox1? "+gjp.isDoingBox());
      //System.out.println("Graph sent message " + message );

      if( message == CoordJPanel.CURSOR_MOVED ) {
        //System.out.println("FunctionViewComponent$GraphListener - CURSOR_MOVED" );
        if ( ignore_pointed_at ) 
          ignore_pointed_at = false;           // ignore one pointed at message echoed back
        else                                   // from the GraphJPanel
          sendMessage( POINTED_AT_CHANGED );
      }

      if( message == CoordJPanel.ZOOM_IN ) {
        //System.out.println("FunctionViewComponent$GraphListener - ZOOM_IN" + regioninfo );
        paintComponents();
        sendMessage( SELECTED_CHANGED );
      }

      if( message == CoordJPanel.RESET_ZOOM ) {
        //System.out.println("FunctionViewComponent$GraphListener - RESET_ZOOM" );
        paintComponents();
        sendMessage( SELECTED_CHANGED );
      }      	
      //System.out.println("FunctionViewComponent...isDoingBox2? "+gjp.isDoingBox());
    }
  }

  private class ControlListener implements ActionListener {
    //~ Methods ****************************************************************

    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );
      
//    System.out.println( "action command: " + message );
//    System.out.println( "action event: " + ae );
      if( message.equals( ControlCheckbox.CHECKBOX_CHANGED ) ) {
        ControlCheckbox control = ( ControlCheckbox )ae.getSource(  );
        
        if( control.getText(  ).equals( "Function Controls" ) ) {
          if( control.isSelected(  ) ) {
            mainControls.display_controls();
          } else {
            mainControls.close_frame();
            control_box.setSelected(false);
          }
        }
        
        else if( control.getText(  ).equals( "Show Pointed At" ) ) {
          if( control.isSelected(  ) ) {
            draw_pointed_at = true;
          gjp.setTransparent(false, 0, true);
          } else {
              gjp.setTransparent(true, 0, true);
            draw_pointed_at = false;
          }
          paintComponents(big_picture.getGraphics()); 
        }
      }
      
      else if( message.equals("Options.Function Controls"))
      {  
         JMenuItem theItem = ((ViewMenuItem)ae.getSource()).getItem();
         if (((JCheckBoxMenuItem)theItem).getState())
           mainControls.display_controls();
         else{
            mainControls.close_frame();
            control_box.setSelected(false);
         }  
      }  
      else if( message.equals("Options.Show Pointed At"))
      {
         JMenuItem theItem = ((ViewMenuItem)ae.getSource()).getItem();
         if (((JCheckBoxMenuItem)theItem).getState()){
           draw_pointed_at = true;
           gjp.setTransparent(false, 0, true);
         }
         else{
         gjp.setTransparent(true, 0, true);
            draw_pointed_at = false;
         }
         paintComponents(big_picture.getGraphics());  
      }  
    }
  }
    private class FrameListener extends WindowAdapter  {
    //~ Methods ****************************************************************
      public void windowClosing(WindowEvent e) {
        control_box.setSelected(false);
      }
   }
 
}
