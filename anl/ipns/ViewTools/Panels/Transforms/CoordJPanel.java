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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.9  2001/06/01 20:47:12  dennis
 *  Modified to work with XOR_Cursor, instead of the Rubberband class.
 *
 *  Revision 1.8  2001/05/29 15:12:25  dennis
 *  Now uses initializeWorldCoords to reset both the local and
 *  global transforms.
 *
 *  Revision 1.7  2001/05/07 21:02:40  dennis
 *  Added implementation for the method LocalTransformChanged()
 *  that was previously abstract.  This slightly simplified the
 *  GraphJPanel and ThreeD_JPanel classes.
 *
 *  Revision 1.6  2001/04/23 21:14:55  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.5  2001/03/30 19:21:10  dennis
 *  The method   setLocalWorldCoordinates( bounds )   now intersects "bounds"
 *  with the current global world coordinates to make sure that the local
 *  coordinates are contained in the global corrdinates.
 *
 *  Revision 1.4  2001/01/29 21:39:03  dennis
 *  Now uses CVS version numbers.
 *
 *  Revision 1.3  2000/07/10 22:11:46  dennis
 *  7/10/2000 version, many changes and improvements
 *
 *  Revision 1.18  2000/05/31 21:34:13  dennis
 *  Modified method that generates mouse events from key events to
 *  send a MOUSE_DRAGGED event with a MOUSE_PRESSED event so that the
 *  cursor draws/updates immediately
 *
 *  Revision 1.17  2000/05/11 16:53:19  dennis
 *  Added RCS logging
 *
 */

package DataSetTools.components.image;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

import DataSetTools.util.*;

abstract public class CoordJPanel extends    JPanel 
                                  implements Serializable
{
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
  
  protected Dimension       preferred_size = null;

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
    this_panel = this;

    set_crosshair( current_point );      // ##############
    stop_crosshair( current_point );

    set_box( current_point );            // ##############
    stop_box( current_point, false );
    CJP_handle_arrow_keys = true;
  }

 
  public void SetHorizontalScrolling( boolean scroll )
  {
    h_scroll = scroll;
    invalidate();
  }


  public Point getCurrent_pixel_point()
  {
    return new Point( current_point );
  }

  public floatPoint2D getCurrent_WC_point()
  {
    SetTransformsToWindowSize();
    return new floatPoint2D( local_transform.MapXFrom( current_point.x ),
                             local_transform.MapYFrom( current_point.y ) );
  }

  public void setCurrent_pixel_point( Point p )
  {
    current_point = new Point(p);
  }

  public void setCurrent_WC_point( floatPoint2D WC_point )
  {
    SetTransformsToWindowSize();
    current_point.x = (int)( 0.5 + local_transform.MapXTo( WC_point.x ) );
    current_point.y = (int)( 0.5 + local_transform.MapYTo( WC_point.y ) );
  }

  public CoordTransform getLocal_transform()
  {
    SetTransformsToWindowSize();
    return local_transform;
  }

  public CoordTransform getGlobal_transform()
  {
    SetTransformsToWindowSize();
    return global_transform;
  }


  public void initializeWorldCoords( CoordBounds b )
  {
    SetTransformsToWindowSize();
    global_transform.setSource( b );
    local_transform.setSource( b );
  }

  public void setGlobalWorldCoords( CoordBounds b )
  {
    SetTransformsToWindowSize();
    global_transform.setSource( b );
  }

  public CoordBounds getGlobalWorldCoords( )
  {
    SetTransformsToWindowSize();
    return( global_transform.getSource( ) );
  }


  public void setLocalWorldCoords( CoordBounds b )
  {
    SetTransformsToWindowSize();

    CoordBounds global_WC = getGlobalWorldCoords();      // keep new bounds
                                                         // within global WC
    local_transform.setSource( b.intersect( global_WC ));
  }
 

  public CoordBounds getLocalWorldCoords( )
  {
    SetTransformsToWindowSize();
    return( local_transform.getSource( ) );
  }


/* ------------------------------ showState ------------------------------ */

public void showState( String str )
{
  System.out.println( "-------------------------------------------" );
  System.out.println( str );
  System.out.println( "-------------------------------------------" );
  showBounds();
  showCurrentPoint();
}

/* ------------------------ set_crosshair_WC ------------------------------ */

public void set_crosshair_WC( floatPoint2D pt )
{
 setCurrent_WC_point( pt );
 set_crosshair( current_point );
}

/* ------------------------ set_crosshair ------------------------------ */

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
}


/* ------------------------ set_box_WC ------------------------------ */

public void set_box_WC( floatPoint2D pt )
{
 setCurrent_WC_point( pt );
 set_box( current_point );
}


/* ------------------------ set_box ------------------------------ */

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
}


/* -------------------------- stop_crosshair_WC ---------------------------- */

public void stop_crosshair_WC( floatPoint2D pt )
{
 setCurrent_WC_point( pt );
 stop_crosshair( current_point );
}


/* -------------------------- stop_crosshair ---------------------------- */

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


/* -------------------------- stop_box_WC ---------------------------- */

public void stop_box_WC( floatPoint2D pt, boolean do_zoom )
{
 setCurrent_WC_point( pt );
 stop_box( current_point, do_zoom );
}


/* -------------------------- stop_box ---------------------------- */

public void stop_box( Point current, boolean do_zoom )
{
  if ( doing_box )
  {
    current_point = current;
    rb_box.redraw( current );
    rb_box.stop( current );
    doing_box = false;

    Rectangle r = rb_box.region();
                                               // process zoom if requested
    if ( do_zoom )
      if ( r.width  > 0  &&
           r.height > 0   )
      {
         int x1 = r.x;
         int y1 = r.y;
         int x2 = x1 + r.width;
         int y2 = y1 + r.height;

         ZoomToPixelSubregion( x1, y1, x2, y2 );
         LocalTransformChanged();
      }
  }
}


/* -------------------------- isDoingCrosshair ------------------------- */

public boolean isDoingCrosshair()
{
  return doing_crosshair;
}


/* -------------------------- isDoingBox ------------------------- */

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
  SetTransformsToWindowSize();
  local_transform.setSource( global_transform.getSource() );
  LocalTransformChanged();
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
  int SNAP_REGION = 10;

  float WC_x1,
        WC_x2,
        WC_y1,
        WC_y2;

  if ( ( x1 == x2 ) || ( y1 == y2 ) )         // ignore degenerate region
    return;

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
/*
public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for CoordJPanel");
    f.setBounds(0,0,500,500);
    CoordJPanel panel = new CoordJPanel();
    f.getContentPane().add(panel);
    f.setVisible(true);
  }
*/


/* -----------------------------------------------------------------------
 *
 * UTILITY CLASSES
 *
 */


class CoordMouseAdapter extends MouseAdapter
{
  public void mouseClicked (MouseEvent e)
  {
    SetTransformsToWindowSize();
    current_point = e.getPoint();

    stop_box( current_point, false );
    stop_crosshair( current_point );

    if ( e.getClickCount() == 2 )    // reset zoom region to whole array
      resetZoom();
  }

  public void mousePressed (MouseEvent e)
  {
    SetTransformsToWindowSize();

    if ( (e.getModifiers() & InputEvent.BUTTON2_MASK) != 0  ||
          e.isShiftDown()                                         )
      set_box( e.getPoint() ); 
    else  
      set_crosshair( e.getPoint() );
  }

  public void mouseReleased(MouseEvent e)
  {
    SetTransformsToWindowSize();
    current_point = e.getPoint();
    stop_crosshair( e.getPoint() );
    stop_box( e.getPoint(), true );
  }

  public void mouseEntered (MouseEvent e)
  {
    requestFocus();                // so we can also move cursor with arrow
                                   // keys
    Cursor cursor = new Cursor( Cursor.CROSSHAIR_CURSOR );
    setCursor( cursor );
  }
};


class CoordMouseMotionAdapter extends MouseMotionAdapter
{
  public void mouseDragged(MouseEvent e)
  {

    SetTransformsToWindowSize();
    current_point = e.getPoint();

    if ( doing_box )
      set_box( e.getPoint() );

    else
      set_crosshair( e.getPoint() );
  }
}


class CoordKeyAdapter extends KeyAdapter
{
  public void keyPressed( KeyEvent e )
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

    int id = 0;                               // synthesize a mouse event and
    int modifiers = 0;                        // send it to this CoordJPanel
    int clickcount = 0;                       // to trigger the proper response

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
    LocalTransformChanged();
    current_size = size;
  }
}



}
