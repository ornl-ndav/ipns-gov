/*
 * @(#) ImageJPanel.java  1.0    1998/07/29   Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.5  2000/12/07 22:32:32  dennis
 *  Added some debug prints
 *
 *  Revision 1.4  2000/10/03 21:48:27  dennis
 *  Modified to work with "Dual" color scales for both positive and negative
 *  values.
 *  Modified paint() routine to verify that the all data has been initialized.
 *  This was needed on the Mac to avoid null pointer problems.
 *
 *  Revision 1.3  2000/07/10 22:17:00  dennis
 *  minor format change to documentation
 *
 *  Revision 1.2  2000/07/10 22:11:50  dennis
 *  7/10/2000 version, many changes and improvements
 *
 *  Revision 1.13  2000/05/31 21:32:53  dennis
 *  Modified method that generates mouse events from key events to
 *  send a MOUSE_DRAGGED event with a MOUSE_PRESSED event so that the
 *  cursor draws/updates immediately
 *
 *  Revision 1.12  2000/05/16 22:28:40  dennis
 *  modified it to not copy the data array that is passed in.  This means
 *  that it will no longer work for "ragged" arrays, but it will be somewhat
 *  more efficient.
 *
 *  Revision 1.11  2000/05/11 16:53:19  dennis
 *  Added RCS logging
 *
 */

package DataSetTools.components.image;

import java.awt.*;
import java.io.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;

import DataSetTools.util.*;


public class ImageJPanel extends    CoordJPanel 
                         implements Serializable
{
  private final int       LOG_TABLE_SIZE      = 60000;
  private final int       NUM_POSITIVE_COLORS = 127; 
  private final int       NUM_PSEUDO_COLORS   = 2 * NUM_POSITIVE_COLORS + 1;
  private final byte      ZERO_COLOR_INDEX    = (byte)NUM_POSITIVE_COLORS; 
  private Image           image;
  private Image           rescaled_image = null;
  private float           data[][] = { {0,1}, {2,3} };
  private float           min_data = 0;;
  private float           max_data = 3;

  private IndexColorModel color_model;
  private byte[]          log_scale;

  public ImageJPanel()
  { 
    color_model = 
      IndexColorMaker.getDualColorModel( IndexColorMaker.HEATED_OBJECT_SCALE_2,
                                         NUM_POSITIVE_COLORS );
    log_scale = new byte[LOG_TABLE_SIZE];
    setLogScale( 0 );
  
    h_scroll     = false;
    v_scroll     = true;

    CJP_handle_arrow_keys = false;
    addKeyListener( new ImageKeyAdapter() );
  }

/* ------------------------- changeLogScale -------------------------- */

  public void changeLogScale( double s, boolean rebuild_image )
  {                                       
    setLogScale( s );
    if ( rebuild_image )
    {
      makeImage();
    }
  }

/* ----------------------------- setColorModel --------------------------- */

  public void setNamedColorModel( String   color_scale_name,
                                  boolean  rebuild_image   )
  {
    color_model = IndexColorMaker.getDualColorModel( color_scale_name,
                                                     NUM_POSITIVE_COLORS );
    if ( rebuild_image )
    {
      makeImage();
    }
  }

/* ------------------------------- setData -------------------------------- */

  public void setData( float new_data[][], boolean rebuild_image  )
  {
    int h = new_data.length;

    data = new_data;  //###### Technically, since we may have "ragged" arrays,
                      //       we should allocate a new rectangular array and
                      //       fill it out as is done in the commented out 
                      //       code below.  However, since our ISAW app only 
                      //       passes in rectangular arrays, we will omit this
                      //       copy step for now. 
/*  #########
    int max_row_length = -1;

    for (int i = 0; i < new_data.length; i++ )
    {
      if ( new_data[i].length > max_row_length )
        max_row_length = new_data[i].length;
    }  
   
    if ( max_row_length <= 0 )
    {
      System.out.println("ERROR: max row length <= 0 in ImageView.setData");
      return;
    }
    data  = new float[h][max_row_length];
*/

    max_data = Float.NEGATIVE_INFINITY;
    min_data = Float.POSITIVE_INFINITY;
    for ( int row = 0; row < h; row++ )
      for ( int col = 0; col < new_data[row].length; col++ )
      {
//######        data[row][col] = new_data[row][col];
        if ( data[row][col] > max_data )
          max_data = data[row][col]; 
        if ( data[row][col] < min_data )
          min_data = data[row][col]; 
      }
    if ( min_data == max_data )    // avoid division by 0 when scaling data
      max_data = min_data + 1;

    if ( rebuild_image )
    {
      makeImage();
    }
  }

/* -------------------------------- update ------------------------------- */

  public void update( Graphics g )
  {
    paint(g);
  }

/* --------------------------------- paint ------------------------------- */
  public void paint( Graphics g )
  {
    stop_box( current_point, false );   // if the system redraws this without
    stop_crosshair( current_point );    // our knowlege, we've got to get rid
                                        // of the cursors, or the old position
                                        // will be drawn rather than erased 
                                        // when the user moves the cursor (due
                                        // to XOR drawing). 

    if ( rescaled_image == null )       // the component might not have been
      makeImage();                      // visible when makeImage was called

    if ( rescaled_image != null )       // the component must still not be 
    {                                   // visible
      prepareImage( rescaled_image, this );
      g.drawImage( rescaled_image, 0, 0, this ); 
    }
  }

/* -------------------------- ImageRow_of_PixelRow ----------------------- */

  public int ImageRow_of_PixelRow( int pix_row )
  {
    float WC_y = local_transform.MapYFrom( pix_row );
 
    return ImageRow_of_WC_y( WC_y );
  }

/* -------------------------- ImageRow_of_WC_y ----------------------- */

  public int ImageRow_of_WC_y( float y )
  {
    CoordTransform world_to_image = getWorldToImageTransform();
   
    int   row = (int)( world_to_image.MapYTo( y ) );
    if ( row < 0 )
      row = 0;
    else if ( row > data.length - 1 )
      row = data.length - 1;
    return row;
  }

/* -------------------------- ImageCol_of_PixelCol ----------------------- */

  public int ImageCol_of_PixelCol( int pix_col )
  {
    float WC_x = local_transform.MapXFrom( pix_col );

    return  ImageCol_of_WC_x( WC_x );
  }

/* -------------------------- ImageCol_of_WC_x ----------------------- */

  public int ImageCol_of_WC_x( float x )
  {
    CoordTransform world_to_image = getWorldToImageTransform();
   
    int col = (int)( world_to_image.MapXTo( x ) );
    if ( col < 0 )
      col = 0;
    else if ( col > data[0].length - 1 )
      col = data[0].length - 1;

    return col;
  }


/* -------------------------- ImageValue_at_Pixel ----------------------- */

  public float ImageValue_at_Pixel( Point pixel_pt )
  {
    int row = ImageRow_of_PixelRow( pixel_pt.y );
    int col = ImageCol_of_PixelCol( pixel_pt.x );

    return data[row][col];
  }


/* -------------------------- ImageValue_at_Cursor ----------------------- */

  public float ImageValue_at_Cursor( )
  {
    return ImageValue_at_Pixel( getCurrent_pixel_point() );
  }


/* ---------------------- LocalTransformChanged -------------------------- */

protected void LocalTransformChanged()
{
  makeImage();
}

/* -----------------------------------------------------------------------
 *
 * PRIVATE METHODS
 *
 */

/* ----------------------- getWorldToImageTransform ---------------------- */

  private CoordTransform getWorldToImageTransform()
  {
    CoordBounds     world_bounds;
    CoordBounds     image_bounds;

    SetTransformsToWindowSize();
    image_bounds = new CoordBounds( 0.001f, 0.001f, 
                                    data[0].length-0.001f, data.length-0.001f );
    world_bounds = getGlobal_transform().getSource();
    return( new CoordTransform( world_bounds, image_bounds ) );   
  }


/* ---------------------------------- makeImage --------------------------- */

  private void makeImage()
  {
    float       temp;
    float       scale_factor,
                shift;

    if ( ! isVisible() )              // don't do it yet if it's not visible
      return;

    SetTransformsToWindowSize();
    CoordTransform world_to_image = getWorldToImageTransform(); 
    CoordBounds    bounds         = local_transform.getSource();

    bounds = world_to_image.MapTo( bounds );
    int start_row = (int)(bounds.getY1());
    int end_row   = (int)(bounds.getY2());
    int start_col = (int)(bounds.getX1());
    int end_col   = (int)(bounds.getX2());

    CoordBounds new_bounds = new CoordBounds( start_col+.001f, start_row+0.001f,
                                              end_col+0.999f, end_row+0.999f );
    new_bounds = world_to_image.MapFrom( new_bounds );
    setLocalWorldCoords( new_bounds );

    int h = end_row - start_row + 1;
    int w = end_col - start_col + 1; 
/*
    System.out.println("makeImage..." );
    System.out.println("rows: " + start_row + " to " + end_row );
    System.out.println("cols: " + start_col + " to " + end_col );
    System.out.println("Panel Size = " + getSize() );
    System.out.println("num rows = " + data.length );
    for ( int i = 0; i < data.length; i++ )
      System.out.println("row " + i +" length = " + data[i].length );
*/
    byte pix[] = new byte[h*w];
    int index = 0;

    float max_abs = 0;
    if ( Math.abs( max_data ) > Math.abs( min_data ) )
      max_abs = Math.abs( max_data );
    else
      max_abs = Math.abs( min_data );

    if ( max_abs > 0 )
      scale_factor = (LOG_TABLE_SIZE - 1) / max_abs;
    else
      scale_factor = 0;

    for (int y = start_row; y <= end_row; y++)
      for (int x = start_col; x <= end_col; x++)
      {
        temp = data[y][x] * scale_factor;
        if ( temp >= 0 )
          pix[index++] = (byte)(ZERO_COLOR_INDEX + log_scale[(int)temp]);
        else
          pix[index++] = (byte)(ZERO_COLOR_INDEX - log_scale[(int)(-temp)]);
      }

    image = createImage(new MemoryImageSource(w, h, color_model, pix, 0, w));

    stop_box( current_point, false );
    stop_crosshair( current_point );

    rescaleImage();
    repaint();
  }

/* ---------------------------- rescaleImage -------------------------- */

  private void rescaleImage()
  {
    int       new_width,
              new_height;

    SetTransformsToWindowSize();

//    Rectangle rect = this.getVisibleRect();
//    new_width  = rect.width;
//    new_height = rect.height;

    Dimension size = this.getSize();
    new_width  = size.width;
    new_height = size.height;

    if ( new_width == 0 || new_height == 0 )   // region not yet sized properly
      return;

    if ( image != null )
    {
      if ( v_scroll && new_height < data.length )
        new_height = data.length;

      if ( h_scroll && new_width < data[0].length )
        new_width = data[0].length;

      rescaled_image = image.getScaledInstance(new_width, new_height, 
                                                 Image.SCALE_DEFAULT );
    }
  }

/* ---------------------------- getPreferredSize ------------------------- */

public Dimension getPreferredSize()
{
    if ( preferred_size != null )     // if someone has specified a preferred
      return preferred_size;          // size, just use it.

    int rows, cols;                   // otherwise calculate the preferred
                                      // width based on the data dimensions
                                      // if scrolling is to be used. 
    if ( v_scroll )
      rows = data.length;
    else
      rows = 0;

    if ( h_scroll )
      cols = data[0].length;
    else
      cols = 0;

    return new Dimension( cols, rows );
}

/* ----------------------------- setLogScale -------------------------- */

  private void setLogScale( double s )
  {                                       
    if ( s > 100 )                                // clamp s to [0,100]
      s = 100;
    if ( s < 0 )
      s = 0;

    s = Math.exp(20 * s / 100.0) + 0.1; // map [0,100] exponentially to get 
                                        // scale change that appears more linear

    double scale = NUM_POSITIVE_COLORS / Math.log(s);

    for ( int i = 0; i < LOG_TABLE_SIZE; i++ )
      log_scale[i] = (byte)
                     (scale * Math.log(1.0+((s-1.0)*i)/LOG_TABLE_SIZE));
  }


/* -------------------------------------------------------------------------
 *
 * MAIN
 *
 */
    /* Basic main program for testing purposes only. */
    public static void main(String[] args)
    {
        JFrame f = new JFrame("Test for ImageJPanel");
        f.setBounds(0,0,500,500);
        ImageJPanel panel = new ImageJPanel();
        f.getContentPane().add(panel);
        f.setVisible(true);
    }


class ImageKeyAdapter extends KeyAdapter
{
  public void keyPressed( KeyEvent e )
  {
    int code = e.getKeyCode();

    boolean  is_arrow_key;
    is_arrow_key = ( code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT ||
                     code == KeyEvent.VK_UP   || code == KeyEvent.VK_DOWN   );
                                                 // only process arrow keys
    if ( !is_arrow_key )
      return;

    CoordTransform world_to_image = getWorldToImageTransform();
    floatPoint2D cur_WC_point = getCurrent_WC_point();
    floatPoint2D cur_image_pix = world_to_image.MapTo( cur_WC_point );

    cur_image_pix.x = ImageCol_of_PixelCol( current_point.x ) + 0.5f;
    cur_image_pix.y = ImageRow_of_PixelRow( current_point.y ) + 0.5f;

    if ( code == KeyEvent.VK_UP )
    {
      if ( cur_image_pix.y > 1 )
        cur_image_pix.y = cur_image_pix.y - 1;
    }
    else if ( code == KeyEvent.VK_DOWN )
    {
      if ( cur_image_pix.y < data.length - 1 )
        cur_image_pix.y = cur_image_pix.y + 1;
    }
    else if ( code == KeyEvent.VK_LEFT )
    {
      if ( cur_image_pix.x > 1 )
        cur_image_pix.x = cur_image_pix.x - 1;
    }
    else if ( code == KeyEvent.VK_RIGHT )
    { 
      if ( cur_image_pix.x < data[0].length - 1 )
        cur_image_pix.x = cur_image_pix.x + 1;
    }

    Point old_screen_pix_pt = getCurrent_pixel_point();
    cur_WC_point = world_to_image.MapFrom( cur_image_pix );
    setCurrent_WC_point( cur_WC_point );
    Point new_screen_pix_pt = getCurrent_pixel_point();

    if ( (new_screen_pix_pt.x == old_screen_pix_pt.x) &&
         (new_screen_pix_pt.y == old_screen_pix_pt.y)   )
    {
      if ( code == KeyEvent.VK_UP )
        new_screen_pix_pt.y--;
      else if ( code == KeyEvent.VK_DOWN )
        new_screen_pix_pt.y++;
      else if ( code == KeyEvent.VK_LEFT )
        new_screen_pix_pt.x--;
      else if ( code == KeyEvent.VK_RIGHT )
        new_screen_pix_pt.x++;
    }
    setCurrent_pixel_point( new_screen_pix_pt );

    int id = 0;                               // synthesize a mouse event and
    int modifiers = 0;                        // send it to this CoordJPanel
    int clickcount = 0;                       // to trigger the proper response

    if ( !isDoingBox() && !isDoingCrosshair() )
      id = MouseEvent.MOUSE_PRESSED;
    else
      id = MouseEvent.MOUSE_DRAGGED;

    if ( e.isShiftDown() )
      modifiers  = InputEvent.BUTTON2_MASK;
    else
      modifiers  = MouseEvent.BUTTON1_MASK;

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
