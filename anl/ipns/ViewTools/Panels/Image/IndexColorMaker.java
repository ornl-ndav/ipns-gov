/*
 * File:  IndexColorMaker.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 *  Revision 1.13  2002/11/27 23:13:18  pfpeterson
 *  standardized header
 *
 *  Revision 1.12  2002/07/17 15:18:38  dennis
 *  Removed informational print.
 *
 *  Revision 1.11  2002/07/15 16:54:18  pfpeterson
 *  Uses 'Heat 1' as default.
 *
 */

package DataSetTools.components.image;

import java.awt.*;
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


  /**
   *  Get an indexed color model for a pseudo color scale to be used with an
   *  image.  The pseudo color scales have been choosen to work well with 
   *  non-negative values in the interval  [ 0, num_colors-1 ].
   *
   *  @param scale_type   Specifies the type of pseudo color scale to be 
   *                      constructed.  This must be one of the string values
   *                      defined by this class, GRAY_SCALE...SPECTRUM_SCALE.
   *
   *  @param num_colors   The number of pseudo colors to use in the range
   *                      16 to 256.
   *
   *  @return  Returns an IndexColorModel to be used to map indices in the
   *           range 0 to num_colors-1 to pseudo colors in the color model.
   */
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

    if (scale_type.equalsIgnoreCase( GRAY_SCALE ))
      BuildGrayScale( red, green, blue );

    else if (scale_type.equalsIgnoreCase( NEGATIVE_GRAY_SCALE ))
      BuildNegativeGrayScale( red, green, blue );

    else if (scale_type.equalsIgnoreCase( GREEN_YELLOW_SCALE ))
      BuildGreenYellowScale( red, green, blue );

    else if (scale_type.equalsIgnoreCase( HEATED_OBJECT_SCALE ))
      BuildHeatedObjectScale( red, green, blue );

    else if (scale_type.equalsIgnoreCase( HEATED_OBJECT_SCALE_2 ))
      BuildHeatedObjectScale2( red, green, blue );

    else if (scale_type.equalsIgnoreCase( RAINBOW_SCALE ))
      BuildRainbowScale( red, green, blue );

    else if (scale_type.equalsIgnoreCase( OPTIMAL_SCALE ))
      BuildOptimalScale( red, green, blue );

    else if (scale_type.equalsIgnoreCase( MULTI_SCALE ))
      BuildMultiScale( red, green, blue );

    else if (scale_type.equalsIgnoreCase( SPECTRUM_SCALE ))
      BuildSpectrumScale( red, green, blue );

    else
      BuildHeatedObjectScale( red, green, blue );

    IndexColorModel colors = new IndexColorModel( 8, num_colors,
                                                     red, green, blue );
    return( colors );
  }

  /**
   *  Get an indexed color model for a pseudo color scale to be used with an
   *  image.  The pseudo color scales have been choosen to work well with 
   *  signed values in the interval  [ -num_colors, num_colors ].
   *
   *  @param scale_type   Specifies the type of pseudo color scale to be
   *                      constructed.  This must be one of the string values
   *                      defined by this class, GRAY_SCALE...SPECTRUM_SCALE.
   *
   *  @param num_colors   The number of pseudo colors to use for positive 
   *                      values.  This must be in the range 16 to 127.
   *
   *  @return  Returns an IndexColorModel to be used to map indices in the
   *           range 0 to 2*num_colors to pseudo colors in the color model.
   *           The colors are stored in the color model starting with the
   *           color that corresponds to the most negative value in position 
   *           0, then increasing towards the color that corresponds to 0 
   *           and ending with the color that corresponds to the most positive
   *           value.  Specifically, the colors:
   *
   *           most_negative...zero...most_positive
   *
   *           are arranged sequentially in positions:
   *
   *           0,...,num_colors,...,2*num_colors.
   */

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

    if (scale_type.equalsIgnoreCase( GRAY_SCALE ))
    {
      BuildGrayScale( p_red, p_green, p_blue ); 
      BuildHeatedObjectScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type.equalsIgnoreCase( NEGATIVE_GRAY_SCALE ))
    {
      BuildNegativeGrayScale( p_red, p_green, p_blue ); 
      BuildNegativeHeatedObjectScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type.equalsIgnoreCase( GREEN_YELLOW_SCALE )) 
    {
      BuildGreenYellowScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type.equalsIgnoreCase( HEATED_OBJECT_SCALE )) 
    {
      BuildHeatedObjectScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type.equalsIgnoreCase( HEATED_OBJECT_SCALE_2 )) 
    {
      BuildHeatedObjectScale2( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    } 
    else if (scale_type.equalsIgnoreCase( RAINBOW_SCALE ))
    {
      BuildRainbowScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type.equalsIgnoreCase( OPTIMAL_SCALE ))
    {
      BuildOptimalScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type.equalsIgnoreCase( MULTI_SCALE ))
    {
      BuildMultiScale( p_red, p_green, p_blue ); 
      BuildGrayScale( m_red, m_green, m_blue ); 
    }
    else if (scale_type.equalsIgnoreCase( SPECTRUM_SCALE ))
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


  /**
   *  Get a table of Colors for a pseudo color scale.  The pseudo color 
   *  scales have been choosen to work well with non-negative values in 
   *  the interval  [ 0, num_colors-1 ].
   *
   *  @param scale_type   Specifies the type of pseudo color scale to be
   *                      constructed.  This must be one of the string values
   *                      defined by this class, GRAY_SCALE...SPECTRUM_SCALE.
   *
   *  @param num_colors   The number of pseudo colors to use in the range
   *                      16 to 256.
   *
   *  @return  Returns a table of Color objects to be used to map indices 
   *           in the range 0 to num_colors-1 to pseudo colors.
   */
  public static Color[] getColorTable( String scale_type, int num_colors )
  {
     IndexColorModel model = getColorModel( scale_type, num_colors );
     return getColorTable( model );
  }

  /**
   *  Get a table of Colors for a pseudo color scale.  The pseudo color 
   *  scales have been choosen to work well with signed values in the 
   *  interval  [ -num_colors, num_colors ].
   *
   *  @param scale_type   Specifies the type of pseudo color scale to be
   *                      constructed.  This must be one of the string values
   *                      defined by this class, GRAY_SCALE...SPECTRUM_SCALE.
   *
   *  @param num_colors   The number of pseudo colors to use for positive
   *                      values.  This must be in the range 16 to 127.
   *
   *  @return  Returns a table of Color objects to be used to map indices
   *           in the range 0 to 2*num_colors to pseudo colors.
   *           The colors are stored in the color table starting with the
   *           color that corresponds to the most negative value in position
   *           0, then increasing towards the color that corresponds to 0
   *           and ending with the color that corresponds to the most positive
   *           value.  Specifically, the colors:
   *
   *           most_negative...zero...most_positive
   *
   *           are arranged sequentially in positions:
   *
   *           0,...,num_colors,...,2*num_colors.
   */

  public static Color[] getDualColorTable( String scale_type, int num_colors )
  {
     IndexColorModel model = getDualColorModel( scale_type, num_colors );
     return getColorTable( model );
  }


  /**
   *  Get the table of Colors from an IndexColorModel object.
   *
   *  @param model   The IndexColorModel object whose colors are to be
   *                 extracted.
   *
   *  @return  Returns the table of Color objects corresponding to the colors
   *           in the specified IndexColorModel.
   */
  public static Color[] getColorTable( IndexColorModel model )
  {
     int  num_vals =  model.getMapSize();
     byte reds[]   = new byte[ num_vals ];
     byte greens[] = new byte[ num_vals ];
     byte blues[]  = new byte[ num_vals ];

     model.getReds  ( reds );
     model.getGreens( greens );
     model.getBlues ( blues );

     float red,
           green,
           blue;

     Color table[] = new Color[ num_vals ];
     for ( int i = 0; i < num_vals; i++ )
     {
       if ( reds[i] < 0 )
         red = reds[i] + 256;
       else
         red = reds[i];

       if ( greens[i] < 0 )
         green = greens[i] + 256;
       else
         green = greens[i];

       if ( blues[i] < 0 )
         blue = blues[i] + 256;
       else
         blue = blues[i];

       table[i] = new Color( red/255, green/255, blue/255 ); 
     }
     return table;
  }





/* -------------------------------------------------------------------------
 * 
 *  PRIVATE METHODS
 *
 */

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
