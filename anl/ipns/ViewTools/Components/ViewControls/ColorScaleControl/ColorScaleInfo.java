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


/**
 *  This class records the information about color scales that is
 *  needed by users of the color scale editor.
 */
public class ColorScaleInfo 
{
  public enum  Orientation { HORIZONTAL, VERTICAL };

  private Orientation  orientation; // scale orientation

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
    this( Orientation.HORIZONTAL, 
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
  public ColorScaleInfo( Orientation orientation,
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


  public void setOrientation( Orientation orientation )
  {
    this.orientation = orientation;
  }

  public Orientation getOrientation()
  {
    return orientation;
  }

  public float getTableMin()
  {
    return min;
  }

  public float getTableMax()
  {
    return max;
  }

  public float getPrescale()
  {
    return prescale;
  }

  public String getColorScaleName()
  {
    return cs_name;
  }

  public boolean isTwoSided()
  {
    return two_sided;
  }

  public int getNumColors()
  {
    return num_colors;
  }

  public byte[] getColorIndexTable()
  {
    byte[] table_copy = new byte[ table.length ];
    System.arraycopy( table, 0, table_copy, 0, table.length );
    return table_copy; 
  }

  public boolean isLog()
  {
    return is_log; 
  }

}

