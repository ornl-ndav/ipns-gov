/*
 * File:  CoordJPanel.java
 *
 * Copyright (C) 1999-2001, Dennis Mikkelson
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.32  2004/03/15 23:53:56  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.31  2004/03/12 16:16:13  millermi
 *  - CURSOR_MOVED message now sent only when mouse is moved
 *    over the panel.
 *
 *  Revision 1.30  2004/03/12 00:42:59  millermi
 *  - Changed package and fixed imports.
 *
 *  Revision 1.29  2004/01/29 08:18:14  millermi
 *  - Updated the getObjectState() to include parameter for specifying
 *    default state.
 *  - Added static variables DEFAULT and PROJECT to IPreserveState for
 *    use by getObjectState()
 *
 *  Revision 1.28  2004/01/09 20:31:31  serumb
 *  Added a method getLocalLogWorldCoords with
 *  parameters for x min and max and y min and max.
 *
 *  Revision 1.27  2003/11/18 00:55:03  millermi
 *  - ObjectState now saves the local and global CoordBounds
 *    instead of the CoordTransforms.
 *  - Changed ObjectState keys LOCAL_TRANSFORM, GLOBAL_TRANSFORM
 *    to LOCAL_BOUNDS, GLOBAL_BOUNDS to reflect change above.
 *
 *  Revision 1.26  2003/10/17 16:05:18  millermi
 *  - Now implements IPreserveState which includes the
 *    implementation of setObjectState() and getObjectState().
 *    With these two methods, state information can
 *    be preserved.
 *
 *  Revision 1.25  2003/10/15 23:34:32  dennis
 *  Fixed javadocs to build cleanly with jdk 1.4.2
 *
 *  Revision 1.24  2003/08/07 16:27:04  dennis
 *  Improved naming of variables in method to transform
 *  local bounds by log transform. (Brent Serum)
 *
 *  Revision 1.23  2003/08/05 23:19:19  serumb
 *  Added method getLocalLogCoords to get the coord bounds that have been
 *  scaled.
 *
 *  Revision 1.22  2003/07/25 16:53:01  serumb
 *  Adjusted methods so that zoom messages are sent after
 *  the transformations have been made.
 *
 *  Revision 1.21  2003/06/25 21:34:36  serumb
 *  Added setZoom_region method.
 *
 *  Revision 1.20  2003/06/18 13:39:43  dennis
 *  (Mike Miller)
 *  - Removed isListening from CoordComponentAdapter private class.
 *
 *  Revision 1.19  2003/06/13 14:42:26  dennis
 *  - Added isListening to mouseEntered() method. (Mike Miller)
 *
 *  Revision 1.18  2003/06/10 00:02:02  dennis
 *  Merged changes from version 1.16 with Mike Miller's changes
 *  that were made on top of version 1.15.
 *
 *   *  Revision 1.17  2003/06/09 22:38:06  dennis
 *   *  - Added setEventListening() method to turn off keyboard/mouse events.
 *   *    Mouse events will still set the current point. (Mike Miller)
 *
 *  Revision 1.16  2003/04/18 15:19:50  dennis
 *  Added setVerticalScrolling() method.
 *  Added javadocs.
 *
 *  Revision 1.15  2002/11/27 23:13:18  pfpeterson
 *  standardized header
 *
 *  Revision 1.14  2002/11/26 19:28:27  dennis
 *  Added method setPreserveAspectRatio() that forces the global and
 *  local transforms to preserve aspect ratio.  Also, when the component
 *  is resized, the local transform is reset with proper aspect ratio.
 *
 *  Revision 1.13  2002/11/25 13:47:20  rmikk
 *  Eliminated the "abstract" in the class declaration.
 *  Added a method "getZoomRegion" that returns the zoom
 *     region in pixel coordinates
 *
 *  Revision 1.12  2002/07/15 19:29:35  dennis
 *  Commented out code that sets the cursor... this is an attempt to
 *  prevent crash in native code outside of VM when cursor is set on
 *  Linux.
 *
 */

package gov.anl.ipns.ViewTools.Panels.Transforms;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.UI.ActiveJPanel;
import gov.anl.ipns.ViewTools.Components.LogScaleUtil;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Panels.Cursors.*;

/**
 *  This class is a base class for panels that have a "world" coordinate
 *  system, cursors and zooming.  These capabilities are implemented here
 *  for the ImageJPanel and GraphJPanel classes in particular.  Both a
 *  full size crosshair cursor for pointing and a rubberband box for zooming
 *  are provided.
 */
public class CoordJPanel extends ActiveJPanel implements Serializable,
                                                         IPreserveState
{
 /**
  * "Zoom In" - This constant String is a messaging string sent out
  * by the CoordJPanel whenever the box cursor is used to zoom in on a region.
  */
  public static final String ZOOM_IN      = "Zoom In";
  
 /**
  * "Reset Zoom" - This constant String is a messaging string sent out
  * by the CoordJPanel whenever the image is reset to original size. 
  */
  public static final String RESET_ZOOM   = "Reset Zoom";
  
 /**
  * "Cursor Moved" - This constant String is a messaging string sent out
  * by the CoordJPanel whenever the current point changes.
  */
  public static final String CURSOR_MOVED      = "Cursor Moved";

  // these variables preserve state for the CoordJPanel
 /**
  * "Zoom Region" - This constant String is a key for referencing the state
  * information about the pixel bounds of the zoomed region. The value
  * referenced by this key is of type Rectangle.
  */
  public static final String ZOOM_REGION       = "Zoom Region";
  
 /**
  * "Horizontal Scroll" - This constant String is a key for referencing the
  * state information about the horizontal preferred size of the panel.
  * If true, this will use automatic scroll bars if the horizontal dimension
  * exceeds the window. Only set this true if the CoordJPanel is contained in
  * a JScrollPane. The value referenced by this key is a primative boolean.
  */
  public static final String HORIZONTAL_SCROLL = "Horizontal Scroll";
  
 /**
  * "Vertical Scroll" - This constant String is a key for referencing the state
  * information about the vertical preferred size of the panel.
  * If true, this will use automatic scroll bars if the vertical dimension
  * exceeds the window. Only set this true if the CoordJPanel is contained in
  * a JScrollPane. The value referenced by this key is a primative boolean.
  */
  public static final String VERTICAL_SCROLL   = "Vertical Scroll";
  
 /**
  * "Global Bounds" - This constant String is a key for referencing the
  * state information about the world and pixel coordinates for the initial
  * panel. The value referenced by this key is of type CoordBounds.
  */
  public static final String GLOBAL_BOUNDS  = "Global Bounds";
  
 /**
  * "Local Bounds" - This constant String is a key for referencing the state
  * information about the CoordTransform of the zoom region. The value
  * referenced by this key is of type CoordBounds.
  */
  public static final String LOCAL_BOUNDS   = "Local Bounds";
  
 /**
  * "Current Point" - This constant String is a key for referencing the state
  * information about the currently selected point.  The value referenced
  * by this key is of type Point.
  */
  public static final String CURRENT_POINT     = "Current Point";
  
 /**
  * "Event Listening" - This constant String is a key for referencing the state
  * information about whether this panel listens to mouse/key events. The value
  * referenced by this key is a primative boolean and is true if listening.
  */
  public static final String EVENT_LISTENING   = "Event Listening";
  
 /**
  * "Preferred Size" - This constant String is a key for referencing the state
  * information about the preferred size of this panel. Preferred size is set
  * by my_setPreferredSize(). The value referenced by this key is of type
  * Dimension.
  */
  public static final String PREFERRED_SIZE    = "Preferred Size";
  
  
  private Rectangle zoom_region   = null;     // current zoom_region
  private boolean doing_crosshair = false;    // flags used by several device
  private boolean doing_box       = false;    // adapter classes to control
                                              // zooming/crosshair cursors

  protected boolean         h_scroll = false;
  protected boolean         v_scroll = false;

  protected boolean               CJP_handle_arrow_keys;
  private   BoxCursor             rb_box;
  private   CrosshairCursor       crosshair_cursor;
  protected CoordTransform        global_transform;
  protected CoordTransform  local_transform;
  protected Point           current_point = new Point(0,0);
  protected CoordJPanel     this_panel;
  private   boolean         isListening = true; // turns listeners on/off
  private   boolean         mouse_on_panel = false; // true if mouse visibly
                                                    // on this panel
  
  protected Dimension       preferred_size = null;


  /* --------------------------- constructor -------------------------- */
  public CoordJPanel()
  { 
    CoordMouseAdapter mouse_adapter = new CoordMouseAdapter();
    addMouseListener( mouse_adapter );

    CoordMouseMotionAdapter mouse_motion_adapter =new CoordMouseMotionAdapter();
    addMouseMotionListener( mouse_motion_adapter );

    CoordKeyAdapter key_adapter = new CoordKeyAdapter();
    addKeyListener( key_adapter ); 

    CoordComponentAdapter component_adapter = new CoordComponentAdapter();
    addComponentListener( component_adapter );

    rb_box           = new BoxCursor( this );
    crosshair_cursor = new CrosshairCursor( this );

    global_transform = new CoordTransform();
    local_transform  = new CoordTransform();

    SetTransformsToWindowSize();
    SetZoomRegionToWindowSize();
    this_panel = this;

    set_crosshair( current_point );
    stop_crosshair( current_point );

    set_box( current_point );
    stop_box( current_point, false );
    CJP_handle_arrow_keys = true;
  }

  /**
   * This method will set the current state variables of the object to state
   * variables wrapped in the ObjectState passed in.
   *
   *  @param  new_state
   */
  public void setObjectState( ObjectState new_state )
  {
    boolean redraw = false;  // if any values are changed, repaint.
    Object temp = new_state.get(ZOOM_REGION);
    if( temp != null )
    {
      zoom_region = (Rectangle)temp;
      redraw = true;  
    }
    
    temp = new_state.get(HORIZONTAL_SCROLL);
    if( temp != null )
    {
      h_scroll = ((Boolean)temp).booleanValue();
      redraw = true;  
    }  
    
    temp = new_state.get(VERTICAL_SCROLL);
    if( temp != null )
    {
      v_scroll = ((Boolean)temp).booleanValue();
      redraw = true;  
    }
    
    temp = new_state.get(GLOBAL_BOUNDS);
    if( temp != null )
    {
      setGlobalWorldCoords( (CoordBounds)temp );
      redraw = true;  
    } 
    
    temp = new_state.get(LOCAL_BOUNDS); 
    if( temp != null )
    {
      setLocalWorldCoords( (CoordBounds)temp );
      redraw = true;  
    } 	
    
    temp = new_state.get(CURRENT_POINT);
    if( temp != null )
    {
      current_point = (Point)temp;
      redraw = true;  
    }   
    
    temp = new_state.get(EVENT_LISTENING);
    if( temp != null )
    {
      isListening = ((Boolean)temp).booleanValue();
      redraw = true;  
    }	
    
    temp = new_state.get(PREFERRED_SIZE);
    if( temp != null )
    {
      preferred_size = (Dimension)temp;
      redraw = true;  
    } 
    // may need changing
    if( redraw )
      repaint();
   
  } 
 
  /**
   * This method will get the current values of the state variables for this
   * object. These variables will be wrapped in an ObjectState. 
   *
   *  @param  isDefault Should selective state be returned, that used to store
   *			user preferences common from project to project?
   *  @return if true, the default state containing user preferences,
   *	      if false, the entire state, suitable for project specific saves.
   */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = new ObjectState();
    state.insert( EVENT_LISTENING, new Boolean(isListening) );
    state.insert( HORIZONTAL_SCROLL, new Boolean(h_scroll) );
    if( preferred_size != null )
      state.insert( PREFERRED_SIZE, new Dimension(preferred_size) );
    state.insert( VERTICAL_SCROLL, new Boolean(v_scroll) );
    
    // load these for project specific instances.
    if( !isDefault )
    {
      state.insert( CURRENT_POINT, new Point(current_point) );
      state.insert( GLOBAL_BOUNDS, getGlobalWorldCoords().MakeCopy() );
      state.insert( LOCAL_BOUNDS, getLocalWorldCoords().MakeCopy() );
      state.insert( ZOOM_REGION, new Rectangle( zoom_region ) );
    }
    
    return state;
  }
  
  /* ------------------------- setEventListening -------------------- */
  /**
   *  Set flag to control whether or not this component responds to events
   *
   *  @param dolisten If true, process mouse events 
   */
  public void setEventListening( boolean dolisten )
  {
     isListening = dolisten;
  }


  /* ---------------------- setPreserveAspectRatio ---------------------- */
  /**
   *  Set flag that controls whether the aspect ratio of the coordinate
   *  system should be preserved when the region is zoomed or resized.
   *
   *  @param flag  If true, the aspect ratio will be preserved.
   */
  public void setPreserveAspectRatio( boolean flag )
  {
    local_transform.setPreserveAspectRatio( flag );
    global_transform.setPreserveAspectRatio( flag );
  }
 

  /* ---------------------- setHorizontalScrolling ---------------------- */
  /**
   *  Set flag that controls whether the preferred horizontal size will
   *  be set to the total number of pixels needed in the horizontal direction
   *  for the actual image or graph.  If this is done and the panel is in
   *  JScrollPane with automatic scroll bars, setting this flag true will cause
   *  the scroll bars to be used if needed.  This should ONLY be set true if 
   *  the panel IS in a JScrollPane with an automatic horizontal scroll bar.
   *
   *  @param scroll  If true, the preferred size will be set, which will 
   *                 require scrolling if the image or graph has a large 
   *                 width.
   */
  public void setHorizontalScrolling( boolean scroll )
  {
    h_scroll = scroll;
    invalidate();
  }


  /* ----------------------- setVerticalScrolling ----------------------- */
  /**
   *  Set flag that controls whether the preferred vertical size will
   *  be set to the total number of pixels needed in the vertical direction
   *  for the actual image or graph.  If this is done and the panel is in
   *  JScrollPane with automatic scroll bars, setting this flag true will cause
   *  the scroll bars to be used if needed.  This should ONLY be set true if
   *  the panel IS in a JScrollPane with an automatic vertical scroll bar.
   *
   *  @param scroll  If true, the preferred size will be set, which will 
   *                 require scrolling if the image or graph has a large
   *                 height.
   */
  public void setVerticalScrolling( boolean scroll )
  {
    v_scroll = scroll;
    invalidate();
  }


  /* ------------------------- getZoom_region -------------------------- */
  /**
   *  Get a rectangular subregion selected by user.
   *
   *  @return a reference to the subregion (pixel Rectangle) of the panel 
   *  that was selected by the rubber band box cursor.
   */
  public Rectangle getZoom_region()
  {
    return zoom_region;
  }
  
  /* ----------------------- setZoom_region ----------------------------- */
  public void setZoom_region(float x1, float y1, float x2, float y2)
  {
    if ( ( x1 == x2 ) || ( y1 == y2 ) )         // ignore degenerate region
    {
      SetZoomRegionToWindowSize();
      return;
    }

    SetTransformsToWindowSize();

    local_transform.setSource(x1, y1, x2, y2 );
  
    send_message( ZOOM_IN );
  }

  /* ---------------------- getCurrent_pixel_point ----------------------- */
  /**
   *  Get the point, in pixel coordinates that is pointed at by the user.
   *
   *  @return   a new point, with the coordinates of the pixel that
   *  is pointed at by the crosshair cursor or corner of the rubber band
   *  box.
   */
  public Point getCurrent_pixel_point()
  {
    return new Point( current_point );
  }


  /* ------------------------ getCurrent_WC_point ----------------------- */
  /**
   *  Get the point, in world coordinates that is pointed at by the user.
   *
   *  @return   a new point, with the world coordinates of the poing that
   *  is pointed at by the crosshair cursor or corner of the rubber band
   *  box.
   */
  public floatPoint2D getCurrent_WC_point()
  {
    SetTransformsToWindowSize();
    return new floatPoint2D( local_transform.MapXFrom( current_point.x ),
                             local_transform.MapYFrom( current_point.y ) );
  }


  /* ---------------------- setCurrent_pixel_point ----------------------- */
  /**
   *  Set the coordinates of the point, in pixel coordinates where the cursor 
   *  should start.
   */
  public void setCurrent_pixel_point( Point p )
  {
    current_point = new Point(p);
  }

  /* ------------------------ setCurrent_WC_point ----------------------- */
  /**
   *  Set the coordinates of the point, in world coordinates where the cursor 
   *  should start.
   */
  public void setCurrent_WC_point( floatPoint2D WC_point )
  {
    SetTransformsToWindowSize();
    current_point.x = (int)( 0.5 + local_transform.MapXTo( WC_point.x ) );
    current_point.y = (int)( 0.5 + local_transform.MapYTo( WC_point.y ) );
  }


  /* ------------------------ getLocal_transform ----------------------- */
  /**
   *  Get a reference to the transformation between the current zoomed in
   *  region of the panel, and the pixel coordinates of the region.
   *
   *  @return the local transform for the zoom region.
   */
  public CoordTransform getLocal_transform()
  {
    SetTransformsToWindowSize();
    return local_transform;
  }


  /* ------------------------ getGlobal_transform ----------------------- */
  /**
   *  Get a reference to the transformation between the full coordinate 
   *  system covering the full panel, and the pixel coordinates of the panel.
   *
   *  @return the global transform for the panel.
   */
  public CoordTransform getGlobal_transform()
  {
    SetTransformsToWindowSize();
    return global_transform;
  }


  /* ---------------------- initializeWorldCoords ----------------------- */
  /**
   *  Set the world coordinate system for the panel to be the rectangle
   *  specified by the bounds parameter, b.  Both the local and global
   *  transforms are map from the specified region.
   *
   *  @param b The bounds that determine the world coordinate system for 
   *           the panel.
   */
  public void initializeWorldCoords( CoordBounds b )
  {
    SetTransformsToWindowSize();
    global_transform.setSource( b );
    local_transform.setSource( b );
  }


  /* ----------------------- setGlobalWorldCoords ------------------------ */
  /**
   *  Set the world coordinate system for the global transform for the
   *  panel to be the rectangle specified by the bounds parameter, b.  
   *
   *  @param b The bounds that determine the world coordinate system for 
   *           the panel.
   */
  public void setGlobalWorldCoords( CoordBounds b )
  {
    SetTransformsToWindowSize();
    global_transform.setSource( b );
  }


  /* ----------------------- getGlobalWorldCoords ------------------------ */
  /**
   *  Get the region that defines the world coordinate system for the 
   *  full panel. 
   *
   *  @return A reference to the region in world coordinates that is mapped
   *          to the full panel by the global transform.
   */
  public CoordBounds getGlobalWorldCoords( )
  {
    SetTransformsToWindowSize();
    return( global_transform.getSource( ) );
  }


  /* ----------------------- setLocalWorldCoords ------------------------ */
  /**
   *  Set the world coordinate system for the local transform for the
   *  panel to be the rectangle specified by the bounds parameter, b.  
   *  The specified bounds are restricted to a subregion of the  
   *  world coordinate region for the global transform.
   *
   *  @param b The bounds that determine the world coordinate system for
   *           the zoomed subregion of the panel.
   */
  public void setLocalWorldCoords( CoordBounds b )
  {
    SetTransformsToWindowSize();

    CoordBounds global_WC = getGlobalWorldCoords();      // keep new bounds
                                                         // within global WC
    local_transform.setSource( b.intersect( global_WC ));
  }
 

  /* ----------------------- getLocalWorldCoords ------------------------ */
  /**
   *  Get the region that defines the world coordinate system for the
   *  zoomed region of the panel.
   *
   *  @return A reference to the region in world coordinates that is 
   *          currently mapped to the full panel by the local transform.
   */
  public CoordBounds getLocalWorldCoords( )
  {
    SetTransformsToWindowSize();
    return( local_transform.getSource( ) );
  }

  /* ----------------------- getLocalLogWorldCoords ------------------------ */
  /**
   *  Get the region that defines the world coordinate system for the
   *  zoomed region of the panel.
   *
   *  @return A reference to the region in world coordinates that is 
   *          currently mapped to the full panel by the local transform.
   */
  public CoordBounds getLocalLogWorldCoords(double scale )
  {
    SetTransformsToWindowSize();
    CoordBounds b = getGlobalWorldCoords();
    CoordBounds b2 = local_transform.getSource( );
    float x1,x2,y1,y2;
    x1 = b2.getX1();
    x2 = b2.getX2();
    y1 = b2.getY1();
    y2 = b2.getY2();    
    LogScaleUtil loggerx = new LogScaleUtil(b.getX1(),b.getX2(),
                                            b.getX1(),b.getX2());
    LogScaleUtil loggery = new LogScaleUtil(b.getY1(),b.getY2(),
                                            b.getY1(),b.getY2());
    b2.setBounds(loggerx.toDest(x1, scale),
                loggery.toDest(y1, scale),
                loggerx.toDest(x2, scale),
                loggery.toDest(y2, scale));

    return( b2 );
  }

  /* ----------------------- getLocalLogWorldCoords ------------------------ */
  /**
   *  Get the region that defines the world coordinate system for the
   *  zoomed region of the panel.
   *
   *  @return A reference to the region in world coordinates that is 
   *          currently mapped to the full panel by the local transform.
   */
  public CoordBounds getLocalLogWorldCoords(double scale , float xmin,
                                            float xmax, float ymin, float ymax)
  {
    SetTransformsToWindowSize();
    CoordBounds b = getGlobalWorldCoords();
    CoordBounds b2 = local_transform.getSource( );
    float x1,x2,y1,y2;
    x1 = b2.getX1();
    x2 = b2.getX2();
    y1 = b2.getY1();
    y2 = b2.getY2();    
    LogScaleUtil loggerx = new LogScaleUtil(xmin,xmax,xmin,xmax);
    LogScaleUtil loggery = new LogScaleUtil(ymin,ymax,ymin,ymax);
    b2.setBounds(loggerx.toDest(x1, scale),
                loggery.toDest(y1, scale),
                loggerx.toDest(x2, scale),
                loggery.toDest(y2, scale));

    return( b2 );
  }

  /* ---------------------------- showState ------------------------------ */
  /**
   *  Print the current global and local coordinate bounds and the current
   *  point selected by the user.
   */
  public void showState( String str )
  {
    System.out.println( "-------------------------------------------" );
    System.out.println( str );
    System.out.println( "-------------------------------------------" );
    showBounds();
    showCurrentPoint();
  }

  /* ----------------------- set_crosshair_WC --------------------------- */
  /**
   *  Draw the full size crosshair cursor at the specified world coordinate
   *  point and notify any action listeners that the CURSOR_MOVED.
   *
   *  @param pt  The point where the crosshair should be drawn
   */
  public void set_crosshair_WC( floatPoint2D pt )
  {
   setCurrent_WC_point( pt );
   set_crosshair( current_point );
  }

  /* ------------------------ set_crosshair ------------------------------ */
  /**
   *  Draw the full size crosshair cursor at the specified pixel point
   *  point and notify any action listeners that the CURSOR_MOVED.
   *
   *  @param current  The point where the crosshair should be drawn
   */
  public void set_crosshair( Point current )
  {
    stop_box( current, false );
    current_point = current;
    if ( doing_crosshair )
      crosshair_cursor.redraw( current );
    else
    {
      crosshair_cursor.start( current );
      crosshair_cursor.redraw( current );
      doing_crosshair = true;
    }
   
    send_message( CURSOR_MOVED );
  }


  /* ------------------------ set_box_WC ------------------------------ */
  /**
   *  Move the rubber band box cursor to the specified world coordinate
   *  point and notify any action listeners that the CURSOR_MOVED.  If the
   *  rubber band box was not previously started, this will specifiy the
   *  initial point for the box.  If the rubber band box was previously
   *  started, this will specify a new location for the other corner of
   *  the box.
   *
   *  @param pt  The point where the rubber band box should be drawn
   */
  public void set_box_WC( floatPoint2D pt )
  {
   setCurrent_WC_point( pt );
   set_box( current_point );
  }


  /* ------------------------ set_box ------------------------------ */
  /**
   *  Move the rubber band box cursor to the specified pixel 
   *  point and notify any action listeners that the CURSOR_MOVED.  If the
   *  rubber band box was not previously started, this will specifiy the
   *  initial point for the box.  If the rubber band box was previously
   *  started, this will specify a new location for the other corner of
   *  the box.
   *
   *  @param current  The point where the rubber band box should be drawn
   */
  public void set_box( Point current )
  {
    stop_crosshair( current );
    current_point = current;
    if ( doing_box )
      rb_box.redraw( current );
    else
    {
      rb_box.start( current );
      rb_box.redraw( current );
      doing_box = true;
    }
  
    send_message( CURSOR_MOVED );
  }


  /* ---------------------- stop_crosshair_WC -------------------------- */
  /**
   *  Stop the crosshair cursor and set the current position to the
   *  specifed world coordinate point.
   *
   *  @param  pt  The point to record as the current point, 
   *              in world coordinates
   */
  public void stop_crosshair_WC( floatPoint2D pt )
  {
   setCurrent_WC_point( pt );
   stop_crosshair( current_point );
  }


  /* ------------------------ stop_crosshair -------------------------- */
  /**
   *  Stop the crosshair cursor and set the current position to the
   *  specifed pixel coordinate point.
   *
   *  @param  current  The point to record as the current point, 
   *                   in pixel coordinates
   */
  public void stop_crosshair( Point current )
  {
    if ( doing_crosshair )
    {
      current_point = current;
      crosshair_cursor.redraw( current );
      crosshair_cursor.stop( current );
      doing_crosshair = false;
    }
  }


  /* ------------------------- stop_box_WC ---------------------------- */
  /**
   *  Stop the rubber band box cursor and set the current position to the
   *  specifed world coordinate point.  If do_zoom is true, change the
   *  transform to map the selected region to the full panel and
   *  notify listeners of the zoom-in request.
   *
   *  @param  pt       the point to record as the current point, 
   *                   in world coordinates
   *  @param  do_zoom  flag specifying whether or not to zoom in 
   *                   on the selected region
   */
  public void stop_box_WC( floatPoint2D pt, boolean do_zoom )
  {
    setCurrent_WC_point( pt );
    stop_box( current_point, do_zoom );
  }


  /* -------------------------- stop_box ---------------------------- */
  /**
   *  Stop the rubber band box cursor and set the current position to the
   *  specifed pixel coordinate point.  If do_zoom is true, change the
   *  transform to map the selected region to the full panel and
   *  notify listeners of the zoom-in request.
   *
   *  @param  current  the point to record as the current point,
   *                   in pixel coordinates
   *  @param  do_zoom  flag specifying whether or not to zoom in 
   *                   on the selected region
   */
  public void stop_box( Point current, boolean do_zoom )
  {
    if ( doing_box )
    {
      current_point = current;
      rb_box.redraw( current );
      rb_box.stop( current );
      doing_box = false;

      zoom_region = rb_box.region();
                                               // process zoom if requested
      if ( do_zoom )
        if ( zoom_region.width  > 0  &&
             zoom_region.height > 0   )
        {
           int x1 = zoom_region.x;
           int y1 = zoom_region.y;
           int x2 = x1 + zoom_region.width;
           int y2 = y1 + zoom_region.height;

           ZoomToPixelSubregion( x1, y1, x2, y2 );
           LocalTransformChanged();
        }
    }
  }


  /* -------------------------- isDoingCrosshair ------------------------- */
  /**
   *  @return  true if the crosshair cursor is currently active
   */
  public boolean isDoingCrosshair()
  {
    return doing_crosshair;
  }


  /* -------------------------- isDoingBox ------------------------- */
  /**
   *  @return  true if the rubber band box cursor is currently active
   */
  public boolean isDoingBox()
  {
    return doing_box;
  }


/* --------------------------------------------------------------------------
 *
 *  PROTECTED METHODS
 *
 */

/* ------------------------- LocalTransformChanged ------------------------ */

protected void LocalTransformChanged()
{
  repaint();
}


/* -----------------------------------------------------------------------
 *
 * PRIVATE METHODS
 *
 */ 

/* --------------------------------- resetZoom --------------------------- */

private void resetZoom()
{
  SetZoomRegionToWindowSize();
  SetTransformsToWindowSize();
  local_transform.setSource( global_transform.getSource() );
  LocalTransformChanged();
  send_message( RESET_ZOOM );
}


/* ------------------------------ showBounds ------------------------------ */

private void showBounds( )
{
  System.out.println( "Global WC     = " + global_transform.getSource() );
  System.out.println( "Global Pix    = " + global_transform.getDestination() );
  System.out.println( "Local WC      = " + local_transform.getSource() );
  System.out.println( "Local Pix     = " + local_transform.getDestination() );
}

/* ---------------------------- showCurrentPoint -------------------------- */

private void showCurrentPoint( )
{
  System.out.println( "pixel_point = " + getCurrent_pixel_point() );
  System.out.println( "WC_point    = " + getCurrent_WC_point() );
}


/* ----------------------- SetZoomRegionToWindowSize  -------------------- */

private void SetZoomRegionToWindowSize()
{
  if ( !isVisible() )   // not yet visible, so ignore it
    return;

  Dimension total_size = this.getSize();
  zoom_region = new Rectangle( 0, 0, total_size.width, total_size.height );
}

/* ----------------------- SetTransformsToWindowSize  -------------------- */

public void SetTransformsToWindowSize()
{
  if ( !isVisible() )  // not yet visible, so ignore it
    return;

  Dimension total_size = this.getSize();

  int width  = total_size.width;
  int height = total_size.height;

  global_transform.setDestination( -0.1f, -0.1f, 
                                   width-0.9f, height-0.9f );

  local_transform.setDestination( global_transform.getDestination()); 
}


/* -------------------------- ZoomToPixelSubregion ----------------------- */
 
private void ZoomToPixelSubregion( float x1, float y1, float x2, float y2 )
{
/*
  System.out.println("ZoomToPixelSubregion called----------------------");
  System.out.println("local_transform = " );
  System.out.println( local_transform.toString() );
  System.out.println("global_transform = " );
  System.out.println( global_transform.toString() );
  System.out.println("x1, y1, x2, y2 = " + x1 + ", " + y1 + ", " +
                                           x2 + ", " + y2 );
  System.out.println();
  System.out.println("Preferred size = " + getPreferredSize() );
  System.out.println("Size =           " + getSize() );
  System.out.println();
*/

  int SNAP_REGION = 10;

  float WC_x1,
        WC_x2,
        WC_y1,
        WC_y2;

  if ( ( x1 == x2 ) || ( y1 == y2 ) )         // ignore degenerate region
  {
    SetZoomRegionToWindowSize();
    return;
  }


  SetTransformsToWindowSize();

  Dimension view_size = this.getSize();

  int width  = view_size.width;
  int height = view_size.height;


  if ( x1 < SNAP_REGION )                 // "snap" points to border if 
    x1 = -0.1f;                           // if the cursor position is
                                          // close to border already

  if ( y1 < SNAP_REGION )
    y1 = -0.1f;


  if ( x2 > width - SNAP_REGION )
    x2 = width-0.9f;

  if ( y2 > height - SNAP_REGION )
    y2 = height-0.9f;
                                              // now transform to World Coords
  WC_x1 = local_transform.MapXFrom( x1 );
  WC_x2 = local_transform.MapXFrom( x2 );
  WC_y1 = local_transform.MapYFrom( y1 );
  WC_y2 = local_transform.MapYFrom( y2 );
                                              // preserve the "right side up"
                                              // coordinate system 
  local_transform.setSource( WC_x1, WC_y1, WC_x2, WC_y2 );

  send_message( ZOOM_IN );

/*
  System.out.println("WC: x1, y1, x2, y2 = " + WC_x1 + ", " + WC_y1 + ", " +
                                               WC_x2 + ", " + WC_y2 );
  System.out.println("local_transform = " );
  System.out.println( local_transform.toString() );
  System.out.println("global_transform = " );
  System.out.println( global_transform.toString() );
  System.out.println("END OF ZoomToPixelRegion ---------------------");
  System.out.println();
*/

}

/* --------------------------- my_setPreferredSize ------------------------ */
/**
 *  Set a size that this object will return in a call to getPreferredSize.
 *  However, we don't want to override the setPreferredSize method of this 
 *  JPanel, since that has too many other uses.  If derived classes such as
 *  ImageJPanel and GraphJPanel overided the getPreferredSize method, they
 *  should check if the preferred_size variable has been set.  If so, they 
 *  should return it, otherwise, they should calculate their own preferred
 *  size.
 *
 *  @param  preferredSize   The size to return in calls to getPreferredSize. 
 *                          Pass in null to allow the derived class to 
 *                          calculate it's own preferred size.
 */
public void my_setPreferredSize( Dimension preferredSize )
{
  preferred_size = preferredSize; 
}





/* -----------------------------------------------------------------------
 *
 * MAIN PROGRAM FOR TEST PURPOSES
 *
 */

public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for CoordJPanel");
    f.setBounds(0,0,500,500);
    f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    CoordJPanel panel = new CoordJPanel();
    f.getContentPane().add(panel);
    f.setVisible(true);
    
    ObjectState os = panel.getObjectState(IPreserveState.PROJECT);
    //os.reset(PREFERRED_SIZE, new Dimension(300,100));
    os.reset(EVENT_LISTENING, new Boolean(false));
    panel.setObjectState(os);
  }



/* -----------------------------------------------------------------------
 *
 * UTILITY CLASSES
 *
 */


class CoordMouseAdapter extends MouseAdapter
{
  public void mouseClicked (MouseEvent e)
  {
    if ( isListening )
    {
      SetTransformsToWindowSize();
      current_point = e.getPoint();

      stop_box( current_point, false );
      stop_crosshair( current_point );

      if ( e.getClickCount() == 2 )    // reset zoom region to whole array
        resetZoom();
    }
    else
      current_point = e.getPoint();
  }

  public void mousePressed (MouseEvent e)
  {
    if ( isListening )
    {
      SetTransformsToWindowSize();

      if ( (e.getModifiers() & InputEvent.BUTTON2_MASK) != 0  ||
            e.isShiftDown()                                         )
        set_box( e.getPoint() ); 
      else  
        set_crosshair( e.getPoint() );
    }
    else
      current_point = e.getPoint();
  }

  public void mouseReleased(MouseEvent e)
  {
    if ( isListening )
    {
      SetTransformsToWindowSize();
      current_point = e.getPoint();
      stop_crosshair( e.getPoint() );
      stop_box( e.getPoint(), true );
    }
    else
      current_point = e.getPoint();
  }

  public void mouseEntered (MouseEvent e)
  {
    if ( isListening )
    {
      mouse_on_panel = true;
      requestFocus();                // so we can also move cursor with arrow
                                     // keys
      Cursor cursor = new Cursor( Cursor.CROSSHAIR_CURSOR );
      //###### setCursor( cursor );
    }
  }
  
  public void mouseExited( MouseEvent e )
  {
    if( isListening )
    {
      mouse_on_panel = false;
    }
  }
};


class CoordMouseMotionAdapter extends MouseMotionAdapter
{
  public void mouseDragged(MouseEvent e)
  {
    if ( isListening )
    {
      SetTransformsToWindowSize();
      current_point = e.getPoint();

      if ( doing_box )
        set_box( e.getPoint() );
      else
        if( mouse_on_panel )
          set_crosshair( e.getPoint() );
    }
    else
      current_point = e.getPoint();
  }
}


class CoordKeyAdapter extends KeyAdapter
{
  public void keyPressed( KeyEvent e )
  {
    if ( isListening )
    {
      int code = e.getKeyCode();

      boolean  is_arrow_key;
      is_arrow_key = ( code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT ||
                       code == KeyEvent.VK_UP   || code == KeyEvent.VK_DOWN   );

      Point increment = new Point(0,0);
                                                 // only process arrow keys 
                                                 // and <ENTER>, <BACKSPACE> 
      if ( !is_arrow_key && 
         code != KeyEvent.VK_ENTER && code != KeyEvent.VK_BACK_SPACE   ) 
        return;

      if ( is_arrow_key && !CJP_handle_arrow_keys )
        return;

      if ( code == KeyEvent.VK_LEFT )
        increment.x = -1;

      else if ( code == KeyEvent.VK_RIGHT )
        increment.x = 1;

      else if ( code == KeyEvent.VK_UP )
        increment.y = -1;

      else if ( e.getKeyCode() == KeyEvent.VK_DOWN )
        increment.y = 1;

      current_point.x += increment.x;
      current_point.y += increment.y;

      int id = 0;                             // synthesize a mouse event and
      int modifiers = 0;                      // send it to this CoordJPanel
      int clickcount = 0;                     // to trigger the proper response

      if ( code == KeyEvent.VK_ENTER )
        id = MouseEvent.MOUSE_RELEASED;

      else if ( code == KeyEvent.VK_BACK_SPACE )
      {
        id = MouseEvent.MOUSE_CLICKED;
        clickcount = 2; 
      } 

      else
      {
        if ( !doing_box && !doing_crosshair )
          id = MouseEvent.MOUSE_PRESSED;
        else
          id = MouseEvent.MOUSE_DRAGGED;
         
        if ( e.isShiftDown() )
          modifiers  = InputEvent.BUTTON2_MASK;
        else
          modifiers  = MouseEvent.BUTTON1_MASK;
      }

      MouseEvent mouse_e = new MouseEvent( this_panel, 
                                           id, 
                                           e.getWhen(), 
                                           modifiers, 
                                           current_point.x, 
                                           current_point.y, 
                                           clickcount,
                                           false ); 
      this_panel.dispatchEvent( mouse_e );
      if ( id == MouseEvent.MOUSE_PRESSED )      // Also send dragged event
      {
        mouse_e = new MouseEvent( this_panel,
                                  MouseEvent.MOUSE_DRAGGED,
                                  e.getWhen()+1,
                                  modifiers,
                                  current_point.x,
                                  current_point.y,
                                  clickcount,
                                  false );
        this_panel.dispatchEvent( mouse_e );
      }
    }
  }
}


class CoordComponentAdapter extends ComponentAdapter
{
  Dimension current_size = new Dimension( 0, 0 );

  public void componentResized( ComponentEvent c )
  {
    Dimension size = getSize();
    if ( size.width == 0 || size.height == 0 )
      return;

    if ( size.equals( current_size ) )       // no need to change it!
      return;

    stop_box( current_point, false );
    stop_crosshair( current_point );

    SetTransformsToWindowSize();
    local_transform.setSource( local_transform.getSource() );
    LocalTransformChanged();
    current_size = size;
  }
}


}
