/*
 * @(#) IndexColorMaker.java  1.0    1998/07/29   Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.4  2000/11/07 16:09:46  dennis
 *  Refined use of dual +/- color scales.  The zero level is no longer always
 *  mapped to black, since this did not work well with negative gray color
 *  scales.
 *
 *  Revision 1.3  2000/10/03 21:39:18  dennis
 *  Added method to construct a "Dual" logarithmic scale to handle both
 *  negative and positive values.
 *
 *  Revision 1.2  2000/07/10 22:11:51  dennis
 *  7/10/2000 version, many changes and improvements
 *
 *  Revision 1.3  2000/05/11 16:53:19  dennis
 *  Added RCS logging
 *
 */

package DataSetTools.components.image;

import java.awt.image.*;
import java.io.*;


public class IndexColorMaker implements Serializable
{
  public static final String GRAY_SCALE            = "Gray";
  public static final String NEGATIVE_GRAY_SCALE   = "Negative Gray";
  public static final String GREEN_YELLOW_SCALE    = "Green-Yellow";
  public static final String HEATED_OBJECT_SCALE   = "Heat 1";
  public static final String HEATED_OBJECT_SCALE_2 = "Heat 2";
  public static final String RAINBOW_SCALE         = "Rainbow";
  public static final String OPTIMAL_SCALE         = "Optimal";
  public static final String MULTI_SCALE           = "Multi";
  public static final String SPECTRUM_SCALE        = "Spectrum";


  public static IndexColorModel getColorModel( String scale_type,
                                               int num_colors      )
  {
    if ( num_colors > 256 )                // force valid and usable num_colors
      num_colors = 256;
    else if ( num_colors < 16 )
      num_colors = 16;

    byte red[]   = new byte [ num_colors ];
    byte green[] = new byte [ num_colors ];
    byte blue[]  = new byte [ num_colors ];

    if (scale_type == GRAY_SCALE)
      BuildGrayScale( red, green, blue );

    else if (scale_type == NEGATIVE_GRAY_SCALE )
      BuildNegativeGrayScale( red, green, blue );

    else if (scale_type == GREEN_YELLOW_SCALE )
      BuildGreenYellowScale( red, green, blue );

    else if (scale_type == HEATED_OBJECT_SCALE )
      BuildHeatedObjectScale( red, green, blue );

    else if (scale_type == HEATED_OBJECT_SCALE_2 )
      BuildHeatedObjectScale2( red, green, blue );

    else if (scale_type == RAINBOW_SCALE )
      BuildRainbowScale( red, green, blue );

    else if (scale_type == OPTIMAL_SCALE )
      BuildOptimalScale( red, green, blue );

    else if (scale_type == MULTI_SCALE )
      BuildMultiScale( red, green, blue );

    else if (scale_type == SPECTRUM_SCALE )
      BuildSpectrumScale( red, green, blue );

    else
      BuildGrayScale( red, green, blue );

    IndexColorModel colors = new IndexColorModel( 8, num_colors,
                                                     red, green, blue );
    return( colors );
  }



  public static IndexColorModel getDualColorModel( String scale_type, 
                                                   int num_colors      )
  {
    if ( num_colors > 127 )                // force valid and usable num_colors
      num_colors = 127;
    else if ( num_colors < 16 ) 
      num_colors = 16;
                                                // colors for positive values
    byte p_red[]   = new byte [ num_colors ];
    byte p_green[] = new byte [ num_colors ];
    byte p_blue[]  = new byte [ num_colors ];
                                                 // colors for negative values
    byte m_red[]   = new byte [ num_colors ];
    byte m_green[] = new byte [ num_colors ];
    byte m_blue[]  = new byte [ num_colors ];
                                                 // colors for combined +-values
    byte red[]     = new byte [ 2*num_colors+1 ];
    byte green[]   = new byte [ 2*num_colors+1 ];
    byte blue[]    = new byte [ 2*num_colors+1 ];

    if (scale_type == GRAY_SCALE)
    {
      BuildGrayScale( p_red, p_green, p_blue ); 
      BuildHeatedObjectScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type == NEGATIVE_GRAY_SCALE )
    {
      BuildNegativeGrayScale( p_red, p_green, p_blue ); 
      BuildNegativeHeatedObjectScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type == GREEN_YELLOW_SCALE ) 
    {
      BuildGreenYellowScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type == HEATED_OBJECT_SCALE ) 
    {
      BuildHeatedObjectScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type == HEATED_OBJECT_SCALE_2 ) 
    {
      BuildHeatedObjectScale2( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    } 
    else if (scale_type == RAINBOW_SCALE )
    {
      BuildRainbowScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type == OPTIMAL_SCALE )
    {
      BuildOptimalScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type == MULTI_SCALE )
    {
      BuildMultiScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type == SPECTRUM_SCALE )
    {
      BuildSpectrumScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else                           // by default, use HEATED OBJECT/GRAY SCALE
    {
      BuildHeatedObjectScale( p_red, p_green, p_blue );
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
                              // Now fit both positive and negative color 
                              // scales into red, green and blue arrays.
                              // The average of the first positive and first
                              // negative color represents "0" and is the mid 
                              // entry.
    red  [ num_colors ] = (byte)(( (int)p_red[0]   + (int)m_red[0]  ) / 2);
    green[ num_colors ] = (byte)(( (int)p_green[0] + (int)m_green[0]) / 2);
    blue [ num_colors ] = (byte)(( (int)p_blue[0]  + (int)m_blue[0] ) / 2);

    for ( int i = 0; i < num_colors; i++ ) 
    {
                                    // positive values increase from mid entry
      red  [ num_colors + i + 1] = p_red  [ i ];
      green[ num_colors + i + 1] = p_green[ i ];
      blue [ num_colors + i + 1] = p_blue [ i ];

                                    // negative values decrease from mid entry
      red  [ num_colors - i - 1 ] = m_red  [ i ];
      green[ num_colors - i - 1 ] = m_green[ i ]; 
      blue [ num_colors - i - 1 ] = m_blue [ i ];
    }

    IndexColorModel colors = new IndexColorModel( 8, 2*num_colors+1, 
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


  private static void BuildNegativeHeatedObjectScale( byte red[],
                                                      byte green[],
                                                      byte blue[] )
  {
    float base_red[]   = { 255, 255, 230, 127, 40 };
    float base_green[] = { 255, 180, 127,   0, 20 };
    float base_blue[]  = { 255,  77,   0,   0, 20 };

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
