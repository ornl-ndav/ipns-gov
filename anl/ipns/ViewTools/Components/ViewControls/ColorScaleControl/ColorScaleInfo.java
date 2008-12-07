/* 
 * File: ColorScaleInfo.java
 *
 * Copyright (C) 2008, Dennis Mikkelson
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
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */


package gov.anl.ipns.ViewTools.Components.ViewControls.ColorScaleControl;

import gov.anl.ipns.ViewTools.Panels.TwoD.*;

/**
 *  This class records the information about color scales that is
 *  needed by users of the color scale editor.
 */
public class ColorScaleInfo 
{
  private Axis.Orientation  orientation; // scale orientation

  private float   min;        // minimum data value corresponding to the first
                              // color table entry.
  private float   max;        // maximum data value corresponding to the last 
                              // color table entry.

  private float   prescale;   // prescale factor that should be multiplied 
                              // times the data values before the color map
                              // is used/ 

  private String  cs_name;    // the name of the color model to use 
  private boolean two_sided;  // flag indicting wheter a two-sided color model
                              // is to be used
  private int     num_colors; // the number of color values to use in the 
                              // color model

  private byte[]  table;      // the table of color indexes
  private boolean is_log;     // flag indicating whether or not the color
                              // table has been built with indices increasing
                              // logarithmically

  /*
   *  Construct a ColorScaleInfo object from the specified information,
   *  with orientation set to HORIZONTAL by default.
   *  
   *  @param orientation  Specifies whether to scale should be HORIZONTAL or
   *                      VERTICAL.
   *  @param min          The minimum value corresponding to the start of 
   *                      the table.
   *  @param max          The maximum value corresponding to the start of 
   *                      the table.
   *  @param prescale     Prescale value to be applied to image values.
   *  @param cs_name      The name of the color model.
   *  @param two_sided    Flag indicating whether or not the color scale is
   *                      symmetric around 0.
   *  @param num_colors   The number of color indices used in the color scale.
   *  @param table        Table of psuedo color indices.
   *  @param is_log       Flag indicating whether the color table is 
   *                      logarithmic.
   */
  public ColorScaleInfo( float    min,
                         float    max,
                         float    prescale,
                         String   cs_name,
                         boolean  two_sided,
                         int      num_colors,
                         byte[]   table,
                         boolean  is_log )
  {
    this( Axis.Orientation.HORIZONTAL, 
          min, 
          max, 
          prescale, 
          cs_name, 
          two_sided,
          num_colors,
          table,
          is_log );
  }


  /*
   *  Construct a ColorScaleInfo object from the specified information.
   *  
   *  @param orientation  Specifies whether to scale should be HORIZONTAL or
   *                      VERTICAL.
   *  @param min          The minimum value corresponding to the start of 
   *                      the table.
   *  @param max          The maximum value corresponding to the start of 
   *                      the table.
   *  @param prescale     Prescale value to be applied to image values.
   *  @param cs_name      The name of the color model.
   *  @param two_sided    Flag indicating whether or not the color scale is
   *                      symmetric around 0.
   *  @param num_colors   The number of color indices used in the color scale.
   *  @param table        Table of psuedo color indices.
   *  @param is_log       Flag indicating whether the color table is 
   *                      logarithmic.
   */
  public ColorScaleInfo( Axis.Orientation orientation,
                         float       min,
                         float       max,
                         float       prescale,
                         String      cs_name,
                         boolean     two_sided,
                         int         num_colors,
                         byte[]      table,
                         boolean     is_log )
  {
    this.orientation = orientation;
    this.min         = min;
    this.max         = max;
    this.prescale    = prescale;
    this.cs_name     = cs_name;
    this.two_sided   = two_sided;
    this.num_colors  = num_colors;
    this.table       = table;
    this.is_log      = is_log;
  }


  /**
   *  Construct a new color scale info object with a new orientation value
   *  using the values from the current object for all other fields.
   *
   *  @param  new_orientation The new orientation to be set in the new
   *                          object.
   *
   *  @return a new ColorInfoObject with the orientation updated.
   */
  public ColorScaleInfo  newOrientation( Axis.Orientation new_orientation )
  {
    return new ColorScaleInfo( new_orientation,
                               min,
                               max,
                               prescale,
                               cs_name,
                               two_sided,
                               num_colors,
                               table,
                               is_log
                             ); 
  }


  /**
   *  Construct a new color scale info object with a new color scale name 
   *  using the values from the current object for all other fields.
   *
   *  @param  new_color_scale The new color scale name to be set in the new
   *                          object.
   *
   *  @return a new ColorInfoObject with the color scale updated.
   */
  public ColorScaleInfo  newColorScale( String new_color_scale )
  {
    return new ColorScaleInfo( orientation,
                               min,
                               max,
                               prescale,
                               new_color_scale,
                               two_sided,
                               num_colors,
                               table,
                               is_log
                             );
  }

  
  /**
   *  Get the orientation value.
   *
   *  @return the orientation.
   */
  public Axis.Orientation getOrientation()
  {
    return orientation;
  }


  /**
   *  Get the data value associated with the first entry in the color table. 
   *
   *  @return the data value associated with position 0 in the color table.
   */
  public float getTableMin()
  {
    return min;
  }


  /**
   *  Get the data value associated with the last entry in the color table. 
   *
   *  @return the data value associated with the last entry in the color table.
   */
  public float getTableMax()
  {
    return max;
  }


  /**
   *  Get the currently set pre-scale value that will be applied to image
   *  values BEFORE they are mapped to a color. 
   *
   *  @return the pre-scale value.
   */
  public float getPrescale()
  {
    return prescale;
  }


  /**
   *  Get the name of the color scale to be used.
   *
   *  @return the color scale name.
   */
  public String getColorScaleName()
  {
    return cs_name;
  }


  /**
   *  Get the value of the flag that indicates whether or not the color
   *  scale should be two-sided with a different set of colors used for
   *  any negative values.
   *
   *  @return the value of the "two-sided" flag.
   */
  public boolean isTwoSided()
  {
    return two_sided;
  }


  /**
   *  Get the number of color values to be constructed in the color table.
   *
   *  @return the number of colors.
   */
  public int getNumColors()
  {
    return num_colors;
  }


  /**
   *  Get a reference to the color index look up table.
   *
   *  @return reference to the color look up table.
   */
  public byte[] getColorIndexTable()
  {
    byte[] table_copy = new byte[ table.length ];
    System.arraycopy( table, 0, table_copy, 0, table.length );
    return table_copy; 
  }


  /**
   *  Get the value of the flag that indicates whether or not the color
   *  scale was constructed as a logarithmic scale.
   *
   *  @return the value of the "is_log" flag.
   */
  public boolean isLog()
  {
    return is_log; 
  }

}

