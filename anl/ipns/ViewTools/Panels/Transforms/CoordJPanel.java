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

package DataSetTools.components.image;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

import DataSetTools.util.*;
import DataSetTools.components.ui.*;
import DataSetTools.components.View.LogScaleUtil;

/**
 *  This class is a base class for panels that have a "world" coordinate
 *  system, cursors and zooming.  These capabilities are implemented here
 *  for the ImageJPanel and GraphJPanel classes in particular.  Both a
 *  full size crosshair cursor for pointing and a rubberband box for zooming
 *  are provided.
 */
public class CoordJPanel extends    ActiveJPanel 
                                  implements Serializable
{
  public static final String ZOOM_IN      = "Zoom In";
  public static final String RESET_ZOOM   = "Reset Zoom";
  public static final String CURSOR_MOVED = "Cursor Moved";

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

    set_crosshair( current_point );      // ##############
    stop_crosshair( current_point );

    set_box( current_point );            // ##############
    stop_box( current_point, false );
    CJP_handle_arrow_keys = true;
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
    LogScaleUtil loggerx = new LogScaleUtil(b.getX1(),b.getX2(),b.getX1(),b.getX2());
    LogScaleUtil loggery = new LogScaleUtil(b.getY1(),b.getY2(),b.getY1(),b.getY2());
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
   *  initial point for the box.  If the rebber band box was previously
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
   *  initial point for the box.  If the rebber band box was previously
   *  started, this will specify a new location for the other corner of
   *  the box.
   *
   *  @param pt  The point where the rubber band box should be drawn
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
   *  @param  pt  The point to record as the current point, 
   *              in pixel coordinates
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
   *  Stop the ruber band box cursor and set the current position to the
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
   *  Stop the ruber band box cursor and set the current position to the
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
    CoordJPanel panel = new CoordJPanel();
    f.getContentPane().add(panel);
    f.setVisible(true);
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
      requestFocus();                // so we can also move cursor with arrow
                                     // keys
      Cursor cursor = new Cursor( Cursor.CROSSHAIR_CURSOR );
      //###### setCursor( cursor );
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
