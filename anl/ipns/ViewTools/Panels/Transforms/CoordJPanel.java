package DataSetTools.components.image;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

import DataSetTools.util.*;

abstract public class CoordJPanel extends JPanel 
{
  private Rubberband      rb_box;
  private Rubberband      crosshair_cursor;
  private CoordTransform  global_transform;
          CoordTransform  local_transform;
  private Point           current_point = new Point(0,0);

  public CoordJPanel()
  { 
    CoordMouseAdapter mouse_adapter = new CoordMouseAdapter();
    addMouseListener( mouse_adapter );

    CoordMouseMotionAdapter mouse_motion_adapter = new CoordMouseMotionAdapter();
    addMouseMotionListener( mouse_motion_adapter );

    CoordComponentAdapter component_adapter = new CoordComponentAdapter();
    addComponentListener( component_adapter );

    rb_box           = new RubberbandRectangle( this );
    crosshair_cursor = new CrosshairCursor( this );

    global_transform = new CoordTransform();
    local_transform  = new CoordTransform();

    SetTransformsToWindowSize();
  }

  public Point getCurrent_pixel_point()
  {
    SetTransformsToWindowSize();
    return current_point;
  }

  public floatPoint2D getCurrent_WC_point()
  {
    SetTransformsToWindowSize();
    return new floatPoint2D( local_transform.MapXFrom( current_point.x ),
                             local_transform.MapYFrom( current_point.y ) );
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

  public void setGlobalWorldCoords( CoordBounds b )
  {
    SetTransformsToWindowSize();
    global_transform.setSource( b );
    local_transform.setSource( b );
  }

  public CoordBounds getGlobalWorldCoords( )
  {
    SetTransformsToWindowSize();
    return( global_transform.getSource( ) );
  }


  public void setLocalWorldCoords( CoordBounds b )
  {
    SetTransformsToWindowSize();
    local_transform.setSource( b );
  }
 

  public CoordBounds getLocalWorldCoords( )
  {
    SetTransformsToWindowSize();
    return( local_transform.getSource( ) );
  }

  abstract public void LocalTransformChanged();


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
    if ( e.getClickCount() == 2 )    // reset zoom region to whole array
    {
      resetZoom();
    } 
  }

  public void mousePressed (MouseEvent e)
  {
    SetTransformsToWindowSize();
    current_point = e.getPoint();
    crosshair_cursor.anchor( e.getPoint() );

    if ( (e.getModifiers() & InputEvent.BUTTON2_MASK) != 0 )
      rb_box.anchor( e.getPoint() );
  }

  public void mouseReleased(MouseEvent e)
  {
    SetTransformsToWindowSize();
    current_point = e.getPoint();
    if (  (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 )
    {
      crosshair_cursor.end( e.getPoint() ); 
      return; 
    }

    if (  (e.getModifiers() & InputEvent.BUTTON2_MASK) != 0 )
    {
      if ( !rb_box.end( e.getPoint() ) )       // ignore release, if not
        return;                                // previously pressed
          
      if ( rb_box.bounds().width  > 0  &&      // valid new region
           rb_box.bounds().height > 0   )
      {
         int x1 = rb_box.bounds().x;
         int y1 = rb_box.bounds().y;             
         int x2 = x1 + rb_box.bounds().width;
         int y2 = y1 + rb_box.bounds().height;             
         ZoomToPixelSubregion( x1, y1, x2, y2 );
         LocalTransformChanged();
      }
    }
  }

  public void mouseEntered (MouseEvent e)
  {
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
 
    if (  (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 )
    {
      crosshair_cursor.stretch( e.getPoint() ); 
    }
    else if (  (e.getModifiers() & InputEvent.BUTTON2_MASK) != 0 )
    {
      rb_box.stretch( e.getPoint() ); 
    }
  }
}

class CoordComponentAdapter extends ComponentAdapter
{
  public void componentResized( ComponentEvent c )
  {
    SetTransformsToWindowSize();
    LocalTransformChanged();
  }
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


/* ------------------------------ showState ------------------------------ */

public void showState( String str )
{
  System.out.println( "-------------------------------------------" );
  System.out.println( str );
  System.out.println( "-------------------------------------------" );
  showBounds();
  showCurrentPoint();
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

//  global_transform.setDestination( 0.1f, 0.1f, 
//                                   width-0.1f, height-0.1f );
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
}
