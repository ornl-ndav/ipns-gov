package DataSetTools.components.image;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;


public class ImageJPanel extends  CoordJPanel 
{
  private final int       LOG_TABLE_SIZE    = 60000;
  private final int       NUM_PSEUDO_COLORS = 128;
  private Image           image;
  private Image           rescaled_image;
  private float           data[][] = { {0,1}, {2,3} };
  private float           min_data = 0;;
  private float           max_data = 3;

  private IndexColorModel color_model;
  private byte[]          log_scale;

  private boolean         v_scroll = false;
  private boolean         h_scroll = false;

  public ImageJPanel()
  { 
    color_model = 
       IndexColorMaker.getColorModel( IndexColorMaker.HEATED_OBJECT_SCALE_2,
                                      128 );
    log_scale = new byte[LOG_TABLE_SIZE];
    setLogScale( 0 );
  
    h_scroll     = false;
    v_scroll     = true;
  }

/* ------------------------- changeLogScale -------------------------- */

  public void changeLogScale( double s, boolean rebuild_image )
  {                                       
    setLogScale( s );
    if ( rebuild_image )
      makeImage();
  }

/* ----------------------------- setColorModel --------------------------- */

  public void setColorModel( IndexColorModel new_color_model )
  {
    color_model = new_color_model;
  }

/* ------------------------------- setData -------------------------------- */

  public void setData( float new_data[][], boolean rebuild_image  )
  {
    int h = new_data.length;
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

    max_data = Float.NEGATIVE_INFINITY;
    min_data = Float.POSITIVE_INFINITY;
    for ( int row = 0; row < h; row++ )
      for ( int col = 0; col < new_data[row].length; col++ )
      {
        data[row][col] = new_data[row][col];
        if ( data[row][col] > max_data )
          max_data = data[row][col]; 
        if ( data[row][col] < min_data )
          min_data = data[row][col]; 
      }
    if ( min_data == max_data )    // avoid division by 0 when scaling data
      max_data = min_data + 1;

    if ( rebuild_image )
      makeImage();
  }

/* -------------------------------- update ------------------------------- */

  public void update( Graphics g )
  {
    paint(g);
  }

/* --------------------------------- paint ------------------------------- */
  public void paint( Graphics g )
  {
    if ( rescaled_image != null )
      g.drawImage( rescaled_image, 0, 0, this ); 
  }

/* -------------------------- ImageRow_of_PixelRow ----------------------- */

  public int ImageRow_of_PixelRow( int pix_row )
  {
    CoordTransform world_to_image = getWorldToImageTransform(); 
    
    float WC_y = local_transform.MapYFrom( pix_row );
    int   row = (int)( world_to_image.MapYTo( WC_y ) );
    if ( row < 0 )
      row = 0;
    else if ( row > data.length - 1 )
      row = data.length - 1;
    return row;
  }

/* -------------------------- ImageCol_of_PixelCol ----------------------- */

  public int ImageCol_of_PixelCol( int pix_col )
  {
    CoordTransform world_to_image = getWorldToImageTransform(); 
    
    float WC_x = local_transform.MapXFrom( pix_col );
    int col = (int)( world_to_image.MapXTo( WC_x ) );
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

public void LocalTransformChanged()
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
*/
    byte pix[] = new byte[h*w];
    int index = 0;
    scale_factor = (LOG_TABLE_SIZE - 1) / (max_data - min_data);
    shift        = - min_data * scale_factor;
    for (int y = start_row; y <= end_row; y++)
      for (int x = start_col; x <= end_col; x++)
      {
        temp = data[y][x] * scale_factor + shift;
        pix[index++] = log_scale[(int)temp];
      }
    image = createImage(new MemoryImageSource(w, h, color_model, pix, 0, w));

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
    int rows, cols;

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

    double scale = NUM_PSEUDO_COLORS / Math.log(s);

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
}
