package DataSetTools.components.image;

import java.awt.image.*;

public class IndexColorMaker
{
  public static final int GRAY_SCALE            = 0;
  public static final int NEGATIVE_GRAY_SCALE   = 1;
  public static final int GREEN_YELLOW_SCALE    = 2;
  public static final int HEATED_OBJECT_SCALE   = 3;
  public static final int HEATED_OBJECT_SCALE_2 = 4;
  public static final int RAINBOW_SCALE         = 5;
  public static final int OPTIMAL_SCALE         = 6;
  public static final int MULTI_SCALE           = 7;
  public static final int SPECTRUM_SCALE        = 8;
  public static final int NUM_SCALES            = 9;

  public static IndexColorModel getColorModel( int color_scale_id, 
                                               int num_colors      )
  {
    if ( color_scale_id < 0 )                  // force valid color_scale_id 
      color_scale_id = 0;
    else if ( color_scale_id >= NUM_SCALES )
      color_scale_id = SPECTRUM_SCALE;

    if ( num_colors > 256 )                // force valid and usable num_colors
      num_colors = 256;
    else if ( num_colors < 16 ) 
      num_colors = 16;

    byte red[]   = new byte [ num_colors ];
    byte green[] = new byte [ num_colors ];
    byte blue[]  = new byte [ num_colors ];

    switch ( color_scale_id )
    {
    case GRAY_SCALE           : BuildGrayScale( red, green, blue ); 
                                break;
    case NEGATIVE_GRAY_SCALE  : BuildNegativeGrayScale( red, green, blue ); 
                                break;
    case GREEN_YELLOW_SCALE   : BuildGreenYellowScale( red, green, blue ); 
                                break;
    case HEATED_OBJECT_SCALE  : BuildHeatedObjectScale( red, green, blue ); 
                                break;
    case HEATED_OBJECT_SCALE_2: BuildHeatedObjectScale2( red, green, blue ); 
                                break;
    case RAINBOW_SCALE        : BuildRainbowScale( red, green, blue ); 
                                break;
    case OPTIMAL_SCALE        : BuildOptimalScale( red, green, blue ); 
                                break;
    case MULTI_SCALE          : BuildMultiScale( red, green, blue ); 
                                break;
    case SPECTRUM_SCALE       : BuildSpectrumScale( red, green, blue ); 
                                break;
    default        : BuildGrayScale( red, green, blue );
    }
    IndexColorModel colors = new IndexColorModel( 8, num_colors, 
                                                     red, green, blue );
    return( colors );
  } 

  private static void BuildGrayScale( byte red[], 
                                      byte green[], 
                                      byte blue[] )
  {
    float base_red[]   = { 30 , 255 };
    float base_green[] = { 30 , 255 };
    float base_blue[]  = { 30 , 255 };

    InterpolateColorScale( base_red, base_green, base_blue, red, green, blue );
  } 

  private static void BuildNegativeGrayScale( byte red[], 
                                              byte green[], 
                                              byte blue[] )
  {
    float base_red[]   = { 255, 30 };
    float base_green[] = { 255, 30 };
    float base_blue[]  = { 255, 30 };

    InterpolateColorScale( base_red, base_green, base_blue, red, green, blue );
  }

  private static void BuildGreenYellowScale( byte red[], 
                                             byte green[], 
                                             byte blue[] )
  {
    float base_red[]   = { 40, 255 };
    float base_green[] = { 80, 255 };
    float base_blue[]  = {  0,   0 };

    InterpolateColorScale( base_red, base_green, base_blue, red, green, blue );
  }

  private static void BuildHeatedObjectScale( byte red[], 
                                              byte green[], 
                                              byte blue[] )
  {
    float base_red[]   = { 40, 127, 230, 255, 255 };
    float base_green[] = { 20,   0, 127, 180, 255 };
    float base_blue[]  = { 20,   0,   0,  77, 255 };

    InterpolateColorScale( base_red, base_green, base_blue, red, green, blue );
  }

  private static void BuildHeatedObjectScale2( byte red[],
                                               byte green[],
                                               byte blue[] )
  {
    float base_red[]   = { 30, 127, 255, 255, 255 };
    float base_green[] = { 30,  30, 127, 255, 255 };
    float base_blue[]  = { 30,  30,  30,  77, 255 };

    InterpolateColorScale( base_red, base_green, base_blue, red, green, blue );
  }


  private static void BuildRainbowScale( byte red[],
                                         byte green[],
                                         byte blue[] )
  {
    float base_red[]   = {  0,   0,   0, 153, 255, 255, 255 };
    float base_green[] = {  0,   0, 255, 255, 255, 153,   0 };
    float base_blue[]  = { 77, 204, 255,  77,   0,   0,   0 };

    InterpolateColorScale( base_red, base_green, base_blue, red, green, blue );
  }


  private static void BuildOptimalScale( byte red[],
                                         byte green[],
                                         byte blue[] )
  {
    float base_red[]   = { 30, 200, 230,  30, 255 };
    float base_green[] = { 30,  30, 230,  30, 255 };
    float base_blue[]  = { 30,  30,  30, 255, 255 };

    InterpolateColorScale( base_red, base_green, base_blue, red, green, blue );
  }

  private static void BuildMultiScale( byte red[],
                                              byte green[],
                                              byte blue[] )
  {
    float base_red[]   = { 30,  30,  30, 230, 245, 255 };
    float base_green[] = { 30,  30, 200,  30, 245, 255 };
    float base_blue[]  = { 30, 200,  30,  30,  30, 255 };

    InterpolateColorScale( base_red, base_green, base_blue, red, green, blue );
  }


  private static void BuildSpectrumScale( byte red[],
                                          byte green[],
                                          byte blue[] )
  {
    float base_red[]   = { 100, 235,   0, 130 };
    float base_green[] = {   0, 255, 235,   0 };
    float base_blue[]  = {   0,   0, 255, 130 };

    InterpolateColorScale( base_red, base_green, base_blue, red, green, blue );
  }


  // 
  //  Build a color table by interpolating between a base set of colors.
  //  The "base" color arrays must all be of the same length ( the length
  //  being the number of base colors given.  The base color values must
  //  be between 0 and 255. 
  //  The calling routine must provide red, green and blue arrays, each 
  //  of the same length ( less than 257) to hold the color table being 
  //  constructed.  The number of colors being constructed must exceed 
  //  the number of base colors given;
  //  
  private static void InterpolateColorScale( float base_red[],
                                             float base_green[],
                                             float base_blue[],
                                             byte  red[],
                                             byte  green[],
                                             byte  blue[]  )
  {
    int   n_ranges   = base_red.length - 1;
    int   out_length = red.length; 
    float range_size = ( out_length - 0.999f) / (float)n_ranges;
  
    float t;
    int   range     = 0;
    float step_base = 0;
    for ( int i = 0; i < out_length; i++ )
    {
      if ( i > (step_base + range_size) )
      {
        range     = range + 1;
        step_base = step_base + range_size;
      }
      t = ( i - step_base ) / range_size;
      red[i]   = (byte) ( (1.0-t) * base_red[range]+ 
                                t * base_red[range+1] );
      green[i] = (byte) ( (1.0-t) * base_green[range]+ 
                                t * base_green[range+1] );
      blue[i]  = (byte) ( (1.0-t) * base_blue[range]+ 
                                t * base_blue[range+1] );
    } 
  }
}
