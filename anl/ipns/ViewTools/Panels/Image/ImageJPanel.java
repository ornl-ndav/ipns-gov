/*
 * File:  ImageJPanel.java
 *
 * Copyright (C) 1999-2003, Dennis Mikkelson
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
 *  Revision 1.17  2003/10/22 20:26:47  millermi
 *  - Fixed java doc error.
 *
 *  Revision 1.16  2003/07/10 13:37:05  dennis
 *  - Added some functionality to main() for testing purposes
 *  - Added isTwoSided as private data member to be used by the makeImage()
 *  - Now supports either one-sided or two sided color scales.
 *    (The pix array in makeImage() is now filled using zero_index in
 *     place of ZERO_COLOR_INDEX.  If the color model is two-sided,
 *     zero_index = ZERO_COLOR_INDEX, else zero_index = 0.)
 *  (Mike Miller)
 *
 *  Revision 1.15  2003/07/05 19:15:37  dennis
 *  Added methods to get min and max of data.  Added parameter to the
 *  setNamedColorMode() method to control whether the color scale is
 *  suitable for images with positive data or with both positive and
 *  negative data. (Mike Miller)
 *  Merged with previous changes.
 *
 *  Revision 1.14  2003/04/18 15:20:42  dennis
 *  Vertical scrolling is no longer automatically set true.
 *
 *  Revision 1.13  2003/02/25 22:28:12  dennis
 *  Added java docs.  Set data method now rejects ragged arrays, or
 *  degenerate or empty arrays.
 *
 *  Revision 1.12  2002/11/27 23:13:18  pfpeterson
 *  standardized header
 *
 *  Revision 1.11  2002/07/17 15:18:00  dennis
 *  Now sets valid default for the color model.
 *
 *  Revision 1.10  2002/07/15 16:55:11  pfpeterson
 *  No longer sets its own default value.
 *
 *  Revision 1.9  2002/06/19 22:43:24  dennis
 *  Added some additional checks to keep row,col values in range.
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

/**
 *    This class displays two dimensional arrays of floating point values as 
 *  pseudo-color images.  The pseudo-color scale may be specified to be 
 *  any of the named color scales from the IndexColorMaker class.  The
 *  range of values in the array is calculated and scaled logarithmically
 *  before being mapped to the pseudo-color scale.  IF the array contains
 *  negative values, one of the "dual" color models from the IndexColorMaker
 *  should be used.  The shape of the log function used for the scaling
 *  can be adjusted to control the apparent brightness of the image. 
 *    A "world coordinate" system can be applied to the image to map between
 *  row and column indices and "world coordinate" (x,y) values.  In addition
 *  cursor position information and zoom in/out is provided by methods from
 *  the base class, CoordJPanel.  Alternatively, methods to convert between
 *  image row and column values and pixel or world coordinate values are
 *  also provided.
 *
 *  @see CoordJPanel 
 *  @see IndexColorMaker
 */

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
  private float           min_data = 0;
  private float           max_data = 3;

  private IndexColorModel color_model;
  private byte[]          log_scale;
  private boolean         isTwoSided = true;

/**
 *  Construct an ImageJPanel with default values for the color scale, log
 *  scaling factor and array of values.  Most applications using this class
 *  will at least have to use the setData() method to provide the actual
 *  values to be displayed.
 */
  public ImageJPanel()
  { 
    color_model =
    IndexColorMaker.getDualColorModel( IndexColorMaker.HEATED_OBJECT_SCALE_2,
          NUM_POSITIVE_COLORS );
 
    log_scale = new byte[LOG_TABLE_SIZE];
    setLogScale( 0 );
  
    CJP_handle_arrow_keys = false;
    addKeyListener( new ImageKeyAdapter() );
  }

/* ------------------------- changeLogScale -------------------------- */
/**
 *  Change the control parameter for the logarithmic scaling applied to
 *  the image values.  If the image has negative values, the logarithmic
 *  scaling is applied to the absolute value of the values.
 *
 *  @param   s             The control parameter, s, clamped to the range
 *                         [0,100].  If s is 0, the scale is essentially 
 *                         linear.  If s is 100, the relative intensity of 
 *                         small values is greatly increased so the image 
 *                         is lightened.
 *  @param   rebuild_image Flag to determine whether the displayed image is
 *                         rebuilt with the new log scale factor, or if
 *                         rebuilding the displayed image should be delayed
 *                         since other changes will also be made before
 *                         rebuilding the image.  A value of "true" will
 *                         cause the image to be rebuilt immediately.
 */
  public void changeLogScale( double s, boolean rebuild_image )
  {                                       
    setLogScale( s );
    if ( rebuild_image )
      makeImage();
  }

/* -------------------------- setNamedColorModel --------------------------- */
/**
 *  Change the color model to used for the image.  If the data has negative
 *  values, one of the "Dual" color models should be used.
 *
 *  @param   color_scale_name  Name of the new color scale to use for the
 *                             image.  Supported color scales are listed in
 *                             the IndexColorMaker class.
 *  @param   twosided          Flag that determines whether a color scale 
 *                             that includes colors for both positive and 
 *                             negative values is used, or if only positive
 *                             values are represented.
 *  @param   rebuild_image     Flag to determine whether the displayed image is
 *                             rebuilt with the new log scale factor, or if
 *                             rebuilding the displayed image should be delayed
 *                             since other changes will also be made before 
 *                             rebuilding the image.  A value of "true" will
 *                             cause the image to be rebuilt immediately.
 * 
 *  @see IndexColorMaker
 */
  public void setNamedColorModel( String   color_scale_name,
                                  boolean  twosided,
                                  boolean  rebuild_image   )
  {
    isTwoSided = twosided;
    if( isTwoSided )
      color_model = IndexColorMaker.getDualColorModel( color_scale_name,
                                                       NUM_POSITIVE_COLORS );
    else
      color_model = IndexColorMaker.getColorModel( color_scale_name,
                                                   NUM_POSITIVE_COLORS );
    if ( rebuild_image )
    {
      makeImage();
    }
  }

/* ------------------------------- setData -------------------------------- */
/**
 *  Change the array of floats that is displayed by this ImageJPanel.
 *
 *  @param   new_data      Rectangular array of floats to display as
 *                         an image.  NOTE: if a ragged array is passed
 *                         in for new_data, the data will be ignored.
 *
 *  @param   rebuild_image Flag to determine whether the displayed image is
 *                         rebuilt with the new log scale factor, or if
 *                         rebuilding the displayed image should be delayed
 *                         since other changes will also be made before 
 *                         rebuilding the image.  A value of "true" will
 *                         cause the image to be rebuilt immediately.
 */

  public void setData( float new_data[][], boolean rebuild_image  )
  {
    int h = new_data.length;

    if ( new_data == null || new_data.length <= 0 )       // nothing to do
    {
      System.out.println("ERROR: empty new_data array in ImageJPanel.setData");
      return;
    }
                                                         // check row 0
    if ( new_data[0] == null || new_data[0].length == 0 )
    {
      System.out.println("ERROR: row 0 empty in ImageJPanel.setData" );
      return;
    }
                                                         // check later rows
    for ( int row = 1; row < new_data.length; row++ )
      if ( new_data[row] == null || new_data[row].length != new_data[0].length )
      {
        System.out.println("ERROR: row " + row + 
                           " invalid in ImageJPanel.setData." );
        return;
      }

    data = new_data;

    max_data = Float.NEGATIVE_INFINITY;
    min_data = Float.POSITIVE_INFINITY;
    for ( int row = 0; row < h; row++ )
      for ( int col = 0; col < new_data[row].length; col++ )
      {
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


/* --------------------------- getNumDataRows ----------------------------- */
/**
 *  Get the number of rows in the data for this image panel.
 *
 *  @return  The number of rows.
 */
  public int getNumDataRows()
  {
    if ( data != null )
      return data.length;

    return 0;
  }


/* --------------------------- getNumDataColumns ------------------------ */
/**
 *  Get the number of columns in the data for this image panel.
 *
 *  @return  The number of columns.
 */
  public int getNumDataColumns()
  {
    if ( data != null )
      if ( data[0] != null )
        return data[0].length;

    return 0;
  }


/* -------------------------------- update ------------------------------- */
/**
 *  Update method that just calls paint.
 */
  public void update( Graphics g )
  {
    paint(g);
  }

/* --------------------------------- paint ------------------------------- */
/**
 *  This method is invoked by swing to draw the image.  Applications must not
 *  call this directly.
 */
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
/**
 *  Get the row number in the data array of the specified pixel row.
 *
 *  @param  pix_row    Pixel "row" value, i.e. pixel y coordinate.
 *
 *  @return  the row number in the data array cooresponding to the specified
 *           pixel row.
 */
  public int ImageRow_of_PixelRow( int pix_row )
  {
    float WC_y = local_transform.MapYFrom( pix_row );
 
    return ImageRow_of_WC_y( WC_y );
  }

/* -------------------------- ImageRow_of_WC_y ----------------------- */
/**
 *  Get the row number in the data array corresponding to the specified
 *  world coordinate y value. 
 *
 *  @param  y  The world coordinate y value 
 *
 *  @return  the row number in the data array cooresponding to the specified
 *           pixel world coordinate y.
 */
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
/**
 *  Get the column number in the data array of the specified pixel col.
 *
 *  @param  pix_col    Pixel "col" value, i.e. pixel x coordinate.
 *
 *  @return  the column number in the data array cooresponding to the specified
 *           pixel column.
 */
  public int ImageCol_of_PixelCol( int pix_col )
  {
    float WC_x = local_transform.MapXFrom( pix_col );

    return  ImageCol_of_WC_x( WC_x );
  }

/* -------------------------- ImageCol_of_WC_x ----------------------- */
/**
 *  Get the column number in the data array corresponding to the specified
 *  world coordinate x value.
 *
 *  @param  x  The world coordinate x value
 *
 *  @return  the column number in the data array cooresponding to the specified
 *           pixel world coordinate x.
 */
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
/**
 *  Get the data value from the data array that cooresponds to the specified
 *  pixel position.
 *
 *  @param  pixel_pt   The 2D coordinates of the pixel
 *
 *  @return The data value drawn at that pixel location
 */
  public float ImageValue_at_Pixel( Point pixel_pt )
  {
    int row = ImageRow_of_PixelRow( pixel_pt.y );
    int col = ImageCol_of_PixelCol( pixel_pt.x );

    return data[row][col];
  }


/* -------------------------- ImageValue_at_Cursor ----------------------- */
/**
 *  Get the data value from the data array at the current cursor position
 *
 *  @return The data value drawn at the current cursor location
 */
  public float ImageValue_at_Cursor( )
  {
    return ImageValue_at_Pixel( getCurrent_pixel_point() );
  }


/* ---------------------------- getPreferredSize ------------------------- */
/**
 *  Get the preferred size of this component based on the number of rows
 *  and columns and on whether or not scrolling has been requested.
 *
 *  @see CoordJPanel
 *
 *  @return  (0,0) is returned if scrolling has not been requested.  If
 *           vertical scrolling has been requested, the actual number of 
 *           rows in the data array will be used instead of 0.  If horizontal
 *           scrolling has been requested, the actual number of columns in
 *           in the data array will be used instead of 0.
 */
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


/* -------------------------------- getDataMin --------------------------- */
/**
 *  Get the minimum value of the data represented by the image.
 *
 *  @return  min_data
 */
public float getDataMin()
{
  return min_data;
}


/* -------------------------------- getDataMax --------------------------- */
/**
 *  Get the maximum value of the data represented by the image.
 *
 *  @return  max_data
 */
public float getDataMax()
{
  return max_data;
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
    int start_row = Math.max( (int)(bounds.getY1() ), 0 );
    int end_row   = Math.min( (int)(bounds.getY2() ), data.length-1 );
    int start_col = Math.max( (int)(bounds.getX1() ), 0 );
    int end_col   = Math.min( (int)(bounds.getX2() ), data[0].length-1 );

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
    byte zero_index = 0;
    if( isTwoSided )
      zero_index = ZERO_COLOR_INDEX;
    for (int y = start_row; y <= end_row; y++)
      for (int x = start_col; x <= end_col; x++)
      {
        temp = data[y][x] * scale_factor;
        if ( temp >= 0 )
          pix[index++] = (byte)(zero_index + log_scale[(int)temp]);
        else
          pix[index++] = (byte)(zero_index - log_scale[(int)(-temp)]);
        //System.out.println("Pix " + pix[index - 1] + " " + (index - 1) );
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


/*-----------------------------------------------------------------------
 *
 *  INTERNAL CLASSES
 *
 */

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


/* -------------------------------------------------------------------------
 *
 * MAIN
 *
 */
 /* Basic main program for testing purposes only. */
  public static void main(String[] args)
  {
    float test_array[][] = new float[500][500];

    for ( int i = 0; i < 500; i++ )
      for ( int j = 0; j < 500; j++ )
      {
        if ( i % 50 == 0 )
          test_array[i][j] = 20 * i;
        else if ( j % 50 == 0 )
          test_array[i][j] = 20 * j;
        else
          test_array[i][j] = i * j;
      }
 
    JFrame f = new JFrame("Test for ImageJPanel");
    f.setBounds(0,0,500,500);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ImageJPanel panel = new ImageJPanel();
    panel.setData( test_array, true );
    panel.setNamedColorModel( IndexColorMaker.HEATED_OBJECT_SCALE_2, 
                              true,
                              true );
    f.getContentPane().add(panel);
    f.setVisible(true);
  }
}
