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

// component changes

import java.awt.*;
import java.awt.Rectangle;
import java.awt.event.*;

// Component location and resizing within the big_picture
import java.io.Serializable;
import java.util.*;
import javax.swing.*;

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
  
  private transient IVirtualArrayList1D Varray1D;  //An object containing our
                                               // array of data
  private Point[] selectedset;  //To be returned by getSelectedSet()   
  private transient Vector Listeners   = null;
  private transient JPanel big_picture = new JPanel();
  private transient JPanel background = new JPanel(new BorderLayout());
  private GraphJPanel gjp;
  private final int MAX_GRAPHS = 20;
 
  // for component size and location adjustments
  //private ComponentAltered comp_listener;
  private transient Rectangle regioninfo;
  private transient CoordBounds local_bounds;
  private transient CoordBounds global_bounds;
  private transient Vector transparencies = new Vector(  );
  private int precision;
  private Font font;
  private transient LinkedList controls = new LinkedList(  );
  private int linewidth      = 1;
  private FunctionControls mainControls;
  private boolean draw_pointed_at = true;
  private ControlCheckbox control_box = new ControlCheckbox(false);

  /**
   * Constructor that takes in a virtual array and creates an graphjpanel
   * to be viewed in a border layout.
   *
   *  @param varr The IVirtual array containing data for producing the graph.
   */
  public FunctionViewComponent( IVirtualArrayList1D varr ) {

    Varray1D    = varr;  // Get reference to varr
    precision   = 4;
    font        = FontUtil.LABEL_FONT2;
    gjp         = new GraphJPanel(  );
   
    //initialize GraphJPanel with the virtual array
    int num_lines = varr.getNumSelectedGraphs(  );
    for( int i = 1; i < num_lines+1; i++ ) {
       float x[] = Varray1D.getXValues(i-1);
       float y[] = Varray1D.getYValues(i-1);
       gjp.setData( x, y, i, false );     
    }

    gjp.setBackground( Color.white );
/*    // set initial line styles
    if( varr.getNumSelectedGraphs(  ) > 1 ) {
      gjp.setColor( Color.blue, 2, true );
      gjp.setStroke( gjp.strokeType( gjp.LINE, 2 ), 2, true );
      gjp.setLineWidth( linewidth, 2, false );
    }

    if( varr.getNumSelectedGraphs(  ) > 2 ) {
      gjp.setColor( Color.green, 3, false );
      gjp.setStroke( gjp.strokeType( gjp.LINE, 3 ), 3, true );
      gjp.setLineWidth( linewidth, 3, false );
    }
*/
    setAxisInfo();
    ImageListener gjp_listener = new ImageListener(  );

    gjp.addActionListener( gjp_listener );

    ComponentAltered comp_listener = new ComponentAltered(  );

    gjp.addComponentListener( comp_listener );

    regioninfo      = new Rectangle( gjp.getBounds(  ) );
    local_bounds    = gjp.getLocalWorldCoords(  ).MakeCopy(  );
    global_bounds   = gjp.getGlobalWorldCoords(  ).MakeCopy(  );

    Listeners = new Vector(  );
    
    if( varr.getNumGraphs(  ) > 0 )
    {
      buildViewComponent();  // initializes big_picture to jpanel containing
     				  // the background and transparencies
      // create transparencies
      AnnotationOverlay top = new AnnotationOverlay( this );
      top.setVisible( false );  // initialize this overlay to off.

      AxisOverlay2D bottom = new AxisOverlay2D( this );

      LegendOverlay leg_overlay = new LegendOverlay( this );
   
      transparencies.add( leg_overlay ); 
      transparencies.add( top );
      transparencies.add( bottom );  // add the transparency to the vector
      OverlayLayout overlay = new OverlayLayout( big_picture );

      big_picture.setLayout( overlay );

      for( int trans = 0; trans < transparencies.size(  ); trans++ ) {
     	big_picture.add( ( OverlayJPanel )transparencies.elementAt( trans ) );
      }
      big_picture.add( background );
      
      DrawSelectedGraphs();
      if(draw_pointed_at)
      DrawPointedAtGraph();
      
      mainControls = new FunctionControls(varr, gjp, getDisplayPanel(),this);
      mainControls.get_frame().addWindowListener( new FrameListener() );
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

 // setState() and getState() are required by IPreserveState interface   
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    boolean redraw = false;  // if any values change redraw.
   
    Object temp = new_state.get(GRAPHJPANEL);
    if ( temp != null){
      gjp = (GraphJPanel)temp;
      redraw = true;
    }

    temp = new_state.get(PRECISION);
    if ( temp != null){
      precision = ((Integer)temp).intValue();
      redraw = true;
    }
    
    temp = new_state.get(FONT);
    if( temp != null ){
      font = (Font)temp;
      redraw = true;
    }

    temp = new_state.get(POINTED_AT_CONTROL);
    if( temp != null ){
      draw_pointed_at = ((Boolean)temp).booleanValue();
      redraw = true;
    }

    temp = new_state.get(CONTROL_BOX);
    if( temp != null ){
      control_box.setSelected( ((Boolean)temp).booleanValue() );
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
  public ObjectState getObjectState(boolean isDefult)
  {
    ObjectState state = new ObjectState();
    state.insert( CONTROL_BOX, new Boolean( control_box.isSelected() ));
    state.insert( FONT, font);
    state.insert( GRAPHJPANEL, gjp);
    state.insert( POINTED_AT_CONTROL, new Boolean(draw_pointed_at) );
    state.insert( PRECISION, new Integer(precision) );
    
    if(! isDefult){
    }

    return state;
  } 


  // getAxisInfo(), getRegionInfo(), getTitle(), getPrecision(), getFont() 
  // all required since this component implements IAxisAddible2D

  /**
   * This method initializes the world coords.
   */
  public void setAxisInfo() {/*
  if(gjp.getLogScaleX() == true && gjp.getLogScaleY() == true ) 
  {
      gjp.setLocalWorldCoords( gjp.getLocalLogWorldCoords() );
  }
  else if (gjp.getLogScaleX() == true && gjp.getLogScaleY() == false)
  {
      gjp.setLocalWorldCoords( new CoordBounds(
                               gjp.getLocalLogWorldCoords().getX1(),
                               gjp.getLocalWorldCoords().getY1(),
			       gjp.getLocalLogWorldCoords().getX2(),
                               gjp.getLocalWorldCoords().getY2() ) );
  }                                    
  else if (gjp.getLogScaleX() == false && gjp.getLogScaleY() == true)
  {
      gjp.setLocalWorldCoords( new CoordBounds(
                               gjp.getLocalWorldCoords().getX1(),
                               gjp.getLocalLogWorldCoords().getY1(),
			       gjp.getLocalWorldCoords().getX2(),
                               gjp.getLocalLogWorldCoords().getY2() ) );
  }
  else
  {*/
    gjp.initializeWorldCoords( 
        new CoordBounds( 
          gjp.getXmin(), gjp.getYmin(),
          gjp.getXmax(), gjp.getYmax() ) );
  //}
 
   // AxisInfo xinfo = getAxisInformation( AxisInfo.X_AXIS );
   // AxisInfo yinfo = getAxisInformation( AxisInfo.Y_AXIS );

  }
 
  public AxisInfo getAxisInformation( int axis ) {
    /*float xmin,xmax,ymin,ymax;
       xmin = gjp.getXmin();
       xmax = gjp.getXmax();
       ymin = gjp.getYmin();
       ymax = gjp.getYmax();*/

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
  public Font getFont(  ) {
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

     if ( gjp != null && gjp.graphs != null && graph > 0 && graph < gjp.graphs.size() )
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
   * This method will return the local coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
  public CoordBounds getLocalCoordBounds(  ) {
    //return local_bounds;
    return gjp.getLocalWorldCoords(  ).MakeCopy(  );
  }

  /**
   * This method will return the global coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
  public CoordBounds getGlobalCoordBounds(  ) {
    //return global_bounds;
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
    // Invalid axis, return zero.
    return 1f;
  }
  
  /**
   * This method adjusts the crosshairs on the graphjpanel.
   * setPointedAt is called from the viewer when another component
   * changes the selected point.
   *
   *  @param  pt
   */
  public void setPointedAt( Point pt ) {
    //System.out.println( "Entering: void setPointedAt( Point pt )" );
    //System.out.println( "X value = " + pt.getX(  ) );
    //System.out.println( "Y value = " + pt.getY(  ) );

    //Type cast Point pt  into  floatPoint2D fpt
    floatPoint2D fpt = new floatPoint2D( ( float )pt.x, ( float )pt.y );

    //set the cursor position on GraphJPanel
    gjp.setCurrent_WC_point( fpt );

    //System.out.println( "" );
  }

  /**
   * This method creates a selected region to be displayed over the graphjpanel
   * by an overlay.
   *
   *  @param  pts
   */
  public void setSelectedSet( Point[] pts ) {
    // implement after selection overlay has been created
    //System.out.println( "Entering: void setSelectedSet( Point[] coords )" );
    //System.out.println( "" );
  }

  /**
   * This method will be called to notify this component of a change in data.
   */
  public void dataChanged(  ) {

       if(draw_pointed_at) 
       DrawPointedAtGraph();

       DrawSelectedGraphs(); 
       paintComponents(big_picture.getGraphics());
       sendMessage("Reset Zoom");
       
    }
  
  /**
   * This method takes in a new array of data and redraws the graph accordingly.
   */
  public void dataChanged( IVirtualArrayList1D pin_varray ) //pin == "passed in"
   {
    if (Varray1D != pin_varray){

    if (Varray1D.getNumSelectedGraphs() > pin_varray.getNumSelectedGraphs()){
       gjp.clearData();
       float[] reset = {0,0.0001f};
       gjp.setData(reset,reset, 0, false);
    }
    Varray1D = pin_varray;
                              // rebuild controls for the new data IN THE 
                              // SAME FRAME, so that the frame doesn't move.
    mainControls = new FunctionControls(pin_varray, gjp, getDisplayPanel(),
                                        this, mainControls.get_frame() );
    } 
    dataChanged();
    transparencies.set(0 ,  new LegendOverlay(this));
    big_picture.removeAll();
      for( int trans = 0; trans < transparencies.size(  ); trans++ ) {
     	big_picture.add( ( OverlayJPanel )transparencies.elementAt( trans ) );
      }
      big_picture.add( background );
    
    //      System.out.println("Value of first element: " + x_array[0] +
    //							y_array[0] );
    //System.out.println( "Thank you for notifying us" );
    //System.out.println( "" );
  }

  /**
   * Get selected set specified by setSelectedSet. The selection overlay
   * will need to use this method.
   *
   *  @return selectedset
   */
  public Point[] getSelectedSet(  )  //keep the same (for now)
   {
    //System.out.println( "Entering: Point[] getSelectedSet()" );
    //System.out.println( "" );

    return selectedset;
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
 /*  // if no 
   if( Varray1D.getNumGraphs(  ) < 1 )
     return new ViewControl[0];
    //System.out.println("");
   ViewControl[] Res = new ViewControl [2];
   /
   JPanel test_p = new JPanel();
   JLabel test_l = new JLabel("Graph View");
   test_p.add(test_l);
   Res[0] = test_p;
   /
   Res[0] = control_box;
   ((ControlCheckbox)Res[0]).setText("Function Controls");
   ((ControlCheckbox)Res[0]).addActionListener( new ControlListener() );
    
   Res[1] = new ControlCheckbox(true);
   ((ControlCheckbox)Res[1]).setText("Show Pointed At");
   ((ControlCheckbox)Res[1]).addActionListener( new ControlListener() );
   
   // Res[0]   =  mainControls.get_panel().getPanel();

    return Res;
*/
     return new ViewControl[0];
  }

  public ViewMenuItem[] getMenuItems(  ) {
   
   if( Varray1D.getNumGraphs(  ) < 1 )
     return new ViewMenuItem[0];
   
   ViewMenuItem[] Res = new ViewMenuItem [2];
   
   Res[0] = new ViewMenuItem(ViewMenuItem.PUT_IN_OPTIONS, new JCheckBoxMenuItem("Function Controls"));
   (Res[0]).addActionListener( new ControlListener() );

   Res[1] = new ViewMenuItem (ViewMenuItem.PUT_IN_OPTIONS, new JCheckBoxMenuItem("Show Pointed At"));
   ((JCheckBoxMenuItem)Res[1].getItem()).setState(true);
   (Res[1]).addActionListener( new ControlListener() );

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
  public Point getPointedAt(  ) {
    floatPoint2D fpt = new floatPoint2D(  );

    fpt = gjp.getCurrent_WC_point(  );

    Point pt = new Point( ( int )fpt.x, ( int )fpt.y );

    return pt;
  }
 
 public void paintComponents()
 {
   paintComponents(big_picture.getGraphics()); 
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
    if(pointed_at_line >= 0) {
      Draw_GJP(pointed_at_line, 0, false);
      return true;
    }
    return false;
  }
  private void reInit(){
    mainControls = new FunctionControls(Varray1D, gjp, getDisplayPanel(),
                                        this, mainControls.get_frame() );
    buildViewComponent();
  }  
    
    

  private int DrawSelectedGraphs() {
    int draw_count = 0;

    int num_graphs = Varray1D.getNumGraphs();

      for(int i=0; i < num_graphs && draw_count < MAX_GRAPHS; i++) {
    
        if( Varray1D.isSelected(i) ) {
            draw_count++;
            Draw_GJP( i, draw_count, false );
        }
      }

   // if( DrawPointedAtGraph() )
   //   draw_count++;
    
    return draw_count;
  }

  private void Draw_GJP( int index, int graph_num, boolean pointed_at )
  {
       float x[] = Varray1D.getXValues(index);
       float y[] = Varray1D.getYValues(index);
     // gjp.setColor( Color.black, graph_num, false );
       gjp.setData( x, y, graph_num, false );     
     //  gjp.setErrors( Varray1D.getErrorVals_ofIndex( index ), 0, 
     //                                        graph_num, true);
       if(!draw_pointed_at)
         gjp.setTransparent(true, 0, true);    

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

    JPanel north = new JPanel( new FlowLayout(  ) );

    north.setPreferredSize( new Dimension( 0, 25 ) );

    JPanel east = new JPanel( new FlowLayout(  ) );

    east.setPreferredSize( new Dimension( 50, 0 ) );

    JPanel south = new JPanel( new FlowLayout(  ) );

    south.setPreferredSize( new Dimension( 0, southwidth ) );

    JPanel west = new JPanel( new FlowLayout(  ) );

    west.setPreferredSize( new Dimension( westwidth, 0 ) );
    //Construct the background JPanel
    background.add( gjp, "Center" );
    background.add( north, "North" );
    background.add( west, "West" );
    background.add( south, "South" );
    background.add( east, "East" );
  }

  /*
   * MAIN - Basic main program to test a FunctionViewComponent object
   */
  public static void main( String[] args ) {
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
     // System.out.println("Component Resized");
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
   * ImageListener monitors if the graphjpanel has sent any messages.
   * If so, process the message and relay it to the viewer.
   */
  private class ImageListener implements ActionListener {
    //~ Methods ****************************************************************

    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );


      //System.out.println("Graph sent message " + message );
      if( message == CoordJPanel.CURSOR_MOVED ) {
        //System.out.println("Sending POINTED_AT_CHANGED" );
        sendMessage( POINTED_AT_CHANGED );
      }

      if( message == CoordJPanel.ZOOM_IN ) {
        //System.out.println("Sending SELECTED_CHANGED " + regioninfo );
        paintComponents();
        sendMessage( SELECTED_CHANGED );
      }

      if( message == CoordJPanel.RESET_ZOOM ) {
        //System.out.println("Sending SELECTED_CHANGED" );
        paintComponents();
        sendMessage( SELECTED_CHANGED );
      }
    }
  }
  private class ControlListener implements ActionListener {
    //~ Methods ****************************************************************

    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );

      System.out.println( "action command: " + message );
      System.out.println( "action event: " + ae );
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


