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
 *  Revision 1.45  2004/03/10 15:49:13  serumb
 *  Implemented the kill method to dispose of the control panel.
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
package DataSetTools.components.View.OneD;

import DataSetTools.components.ParametersGUI.*;
import DataSetTools.components.View.*;  // IVirtualArray1D
import DataSetTools.components.View.TwoD.*;
import DataSetTools.components.View.Transparency.*;  //Axis Overlays
import DataSetTools.components.View.ViewControls.*;
import DataSetTools.components.View.TwoD.*;
import DataSetTools.components.image.*;  //GraphJPanel & CoordJPanel
import DataSetTools.dataset.*;
import DataSetTools.math.*;
import DataSetTools.util.*;  //floatPoint2D FloatFilter
import DataSetTools.components.View.Menu.*;

// component changes

import java.applet.Applet;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.event.*;

// Component location and resizing within the big_picture
import java.awt.event.ComponentAdapter.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.io.Serializable;
import java.lang.*;
import java.lang.Object.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.*;

/**
 * This class allows the user to view data in the form of an image. Meaning
 * is given to the data by way of overlays, which add calibration, selection,
 * and annotation abilities.
 */
public class FunctionViewComponent implements IViewComponent1D,
                                              ActionListener, 
                                              IZoomTextAddible, 
                                              IAxisAddible,
                                              ILogAxisAddible,
                                              IPreserveState,
                                              Serializable 
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
  
  private transient IVirtualArray1D Varray1D;  //An object containing our
                                               // array of data
  private Point[] selectedset;  //To be returned by getSelectedSet()   
  private transient Vector Listeners   = null;
  private transient JPanel big_picture = new JPanel();
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
  public FunctionViewComponent( IVirtualArray1D varr ) {

    Varray1D    = varr;  // Get reference to varr
    precision   = 4;
    font        = FontUtil.LABEL_FONT2;
    gjp         = new GraphJPanel(  );
   

    //initialize the pointed at graph to 0
       float x1[] = {0};
       float y1[] = {0};
       gjp.setData( x1, y1, 0, false );     
     

    //initialize GraphJPanel with the virtual array
    int num_lines = varr.getNumlines(  );
    for( int i = 1; i < num_lines+1; i++ ) {
       float x[] = Varray1D.getXVals_ofIndex(i-1);
       float y[] = Varray1D.getYVals_ofIndex(i-1);
       gjp.setData( x, y, i, false );     
    }

    gjp.setBackground( Color.white );
/*    // set initial line styles
    if( varr.getNumlines(  ) > 1 ) {
      gjp.setColor( Color.blue, 2, true );
      gjp.setStroke( gjp.strokeType( gjp.LINE, 2 ), 2, true );
      gjp.setLineWidth( linewidth, 2, false );
    }

    if( varr.getNumlines(  ) > 2 ) {
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

    buildViewComponent( gjp );  // initializes big_picture to jpanel containing
                                // the background and transparencies
    DrawSelectedGraphs();
    if(draw_pointed_at)
    DrawPointedAtGraph();

    mainControls = new FunctionControls(varr, gjp, getDisplayPanel(),this);
    mainControls.get_frame().addWindowListener( new FrameListener() );
  }

  /**
   * Constructor that takes in a virtual array and creates an graphjpanel
   * to be viewed in a border layout.
   *
   *  @param varr  The IVirtual array containing data for producing the graph.
   *  @param state The state of a previous Function View Component.
   */
  public FunctionViewComponent( IVirtualArray1D varr, ObjectState state ) {

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
  public void setAxisInfo() {

    float xmin,xmax,ymin,ymax;
       xmin = gjp.getXmin();
       xmax = gjp.getXmax();
       ymin = gjp.getYmin();
       ymax = gjp.getYmax();

  if(gjp.getLogScaleX() == true && gjp.getLogScaleY() == true ) 
  {
      gjp.setLocalWorldCoords(new CoordBounds(
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                          xmin,xmax,ymin,ymax )
                                         .getX1(  ) ,
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                          xmin,xmax,ymin,ymax )
                                          .getY1(  ) , 
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                          xmin,xmax,ymin,ymax )
                                          .getX2(  ) ,
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                          xmin,xmax,ymin,ymax )
                                          .getY2(  ) ));
  }
  else if (gjp.getLogScaleX() == true && gjp.getLogScaleY() == false)
  {
      gjp.setLocalWorldCoords(new CoordBounds(
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                          xmin,xmax,ymin,ymax )
                                         .getX1(  ) ,
      ymin,
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                          xmin,xmax,ymin,ymax )
                                          .getX2(  ) ,
      ymax));
  }                                    
  else if (gjp.getLogScaleX() == true && gjp.getLogScaleY() == false)
  {
      gjp.setLocalWorldCoords(new CoordBounds(
      xmin,
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                          xmin,xmax,ymin,ymax )
                                          .getY1(  ) , 
      xmax,
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                          xmin,xmax,ymin,ymax )
                                          .getY2(  ) ));
  }
  else
  {
    gjp.initializeWorldCoords( 
        new CoordBounds( 
          xmin, ymin,
          xmax, ymax) );
  }
 
   // AxisInfo xinfo = getAxisInformation( AxisInfo.X_AXIS );
   // AxisInfo yinfo = getAxisInformation( AxisInfo.Y_AXIS );

  }
 
  public AxisInfo getAxisInformation( int axis ) {
    // if true, return x info
    float xmin,xmax,ymin,ymax;
       xmin = gjp.getXmin();
       xmax = gjp.getXmax();
       ymin = gjp.getYmin();
       ymax = gjp.getYmax();
   

    if( axis == AxisInfo.X_AXIS) {
      if(gjp.getLogScaleX() == true) { 
        return new AxisInfo( 
        gjp.getLocalLogWorldCoords(gjp.getScale(),
                                              xmin,xmax,ymin,ymax )
                                              .getX1(  ),
        gjp.getLocalLogWorldCoords(gjp.getScale(),
                                              xmin,xmax,ymin,ymax)
                                              .getX2(  ),
        Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getLabel(  ),
        Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getUnits(  ),
        Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getIsLinear(  ) );
      }
      else
      {

      return new AxisInfo( 
        gjp.getLocalWorldCoords(  ).getX1(  ),
        gjp.getLocalWorldCoords(  ).getX2(  ),
        Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getLabel(  ),
        Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getUnits(  ),
        Varray1D.getAxisInfo( AxisInfo.X_AXIS ).getIsLinear(  ) );
       }
    }

    // if false return y info
    if(gjp.getLogScaleY() == true) {
      return new AxisInfo( 
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                             xmin,xmax,ymin,ymax )
                                              .getY1(  ),
      gjp.getLocalLogWorldCoords(gjp.getScale(),
                                              xmin,xmax,ymin,ymax )
                                              .getY2(  ),
      Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getLabel(  ),
      Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getUnits(  ),
      Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getIsLinear(  ) );
    }
    else
    {
      return new AxisInfo( 
        gjp.getLocalWorldCoords(  ).getY1(  ),
        gjp.getLocalWorldCoords(  ).getY2(  ),
        Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getLabel(  ),
        Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getUnits(  ),
        Varray1D.getAxisInfo( AxisInfo.Y_AXIS ).getIsLinear(  ) );
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
  public IVirtualArray1D getArray(  ) {
    return Varray1D;
  }

  /**
   * This method will return the graph JPanel. 
   */
  public GraphJPanel GaphJPanel(  ) {
    return gjp;
  }
  
  public double getLogScale()
  {
    return mainControls.getLogScale();
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
   * To be continued...
   */
  public void dataChanged( IVirtualArray1D pin_varray )  // pin == "passed in"
   {
    if (Varray1D != pin_varray){

    if (Varray1D.getNumlines() > pin_varray.getNumlines()){
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

  public JComponent[] getSharedControls(  ) {
    JComponent[] jcontrols = new JComponent[controls.size(  )];

    for( int i = 0; i < controls.size(  ); i++ ) {
      jcontrols[i] = ( JComponent )controls.get( i );
    }

    return jcontrols;
  }

  public JComponent[] getPrivateControls(  ) {
    //System.out.println("Entering: JComponent[] getPrivateControls()");
    //System.out.println("");
   JPanel[] Res = new JPanel[3];

   JPanel test_p = new JPanel();
   JLabel test_l = new JLabel("Graph View");
   test_p.add(test_l);
   Res[0] = test_p;

   Res[1] = control_box;
   ((ControlCheckbox)Res[1]).setText("Function Controls");
   ((ControlCheckbox)Res[1]).addActionListener( new ControlListener() );
    
   Res[2] = new ControlCheckbox(true);
   ((ControlCheckbox)Res[2]).setText("Show Pointed At");
   ((ControlCheckbox)Res[2]).addActionListener( new ControlListener() );
   
   // Res[0]   =  mainControls.get_panel().getPanel();

    return Res;

    // return new JComponent[0];
  }

  public ViewMenuItem[] getSharedMenuItems(  ) {
    //System.out.println( "Entering: ViewMenuItem[] getSharedMenuItems()" );
   // System.out.println( "" );

    return new ViewMenuItem[0];
  }

  public ViewMenuItem[] getPrivateMenuItems(  ) {
    //System.out.println( "Entering: ViewMenuItem[] getPrivateMenuItems()" );
    //System.out.println( "" );

    return new ViewMenuItem[0];
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
    buildViewComponent(gjp);
  }  
    
    

  private int DrawSelectedGraphs() {
    int draw_count = 0;

    int num_graphs = Varray1D.getNumGraphs();

      for(int i=0; i < num_graphs && 
                  Varray1D.getNumlines() < MAX_GRAPHS; i++) {
    
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
       float x[] = Varray1D.getXVals_ofIndex(index);
       float y[] = Varray1D.getYVals_ofIndex(index);
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
   mainControls.get_frame().dispose();
   };
   
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
  private void buildViewComponent( GraphJPanel panel ) {
    setAxisInfo();
    int westwidth  = ( font.getSize(  ) * precision ) + 22;
    int southwidth = ( font.getSize(  ) * 3 ) + 22;
    // this will be the background for the master panel
    JPanel background = new JPanel( new BorderLayout(  ) );

    JPanel north = new JPanel( new FlowLayout(  ) );

    north.setPreferredSize( new Dimension( 0, 25 ) );

    JPanel east = new JPanel( new FlowLayout(  ) );

    east.setPreferredSize( new Dimension( 50, 0 ) );

    JPanel south = new JPanel( new FlowLayout(  ) );

    south.setPreferredSize( new Dimension( 0, southwidth ) );

    JPanel west = new JPanel( new FlowLayout(  ) );

    west.setPreferredSize( new Dimension( westwidth, 0 ) );
    //Construct the background JPanel
    background.add( panel, "Center" );
    background.add( north, "North" );
    background.add( west, "West" );
    background.add( south, "South" );
    background.add( east, "East" );

    AnnotationOverlay top = new AnnotationOverlay( this );
    top.setVisible( false );  // initialize this overlay to off.

    AxisOverlay2D bottom = new AxisOverlay2D( this );

    transparencies.add( top );
    transparencies.add( bottom );  // add the transparency to the vector

    JPanel master         = new JPanel(  );
    OverlayLayout overlay = new OverlayLayout( master );

    master.setLayout( overlay );

    for( int trans = 0; trans < transparencies.size(  ); trans++ ) {
      master.add( ( OverlayJPanel )transparencies.elementAt( trans ) );
    }
    master.add( background );

    big_picture = master;
  }

  /*
   * MAIN - Basic main program to test a FunctionViewComponent object
   */
  public static void main( String[] args ) {
    if( args == null ) {
      System.exit( 0 );
    }

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

    JComponent[] controls = fvc.getSharedControls(  );

    for( int i = 0; i < controls.length; i++ ) {
      cpain.add( controls[i] );
    }

    f2.setBounds( 0, 0, 200, ( 100 * controls.length ) );
    cpain.validate(  );
    f2.show(  );  //display the frame
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
        for( int next = 0; next < transparencies.size(  ); next++ ) {
          ( ( OverlayJPanel )transparencies.elementAt( next ) ).repaint(  );
        }

        sendMessage( SELECTED_CHANGED );
      }

      if( message == CoordJPanel.RESET_ZOOM ) {
        //System.out.println("Sending SELECTED_CHANGED" );
        for( int next = 0; next < transparencies.size(  ); next++ ) {
          ( ( OverlayJPanel )transparencies.elementAt( next ) ).repaint(  );
        }

        sendMessage( SELECTED_CHANGED );
      }
    }
  }
  private class ControlListener implements ActionListener {
    //~ Methods ****************************************************************

    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );

     // System.out.println( "action command: " + message );
     // System.out.println( "action event: " + ae );
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
    }
  }
    private class FrameListener extends WindowAdapter  {
    //~ Methods ****************************************************************
      public void windowClosing(WindowEvent e) {
        control_box.setSelected(false);
      }
   }
 
}


