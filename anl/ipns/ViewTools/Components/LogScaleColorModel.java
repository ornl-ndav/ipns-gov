/*
 * File:  LogScaleColorModel.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 * 
 * This work was supported by the University of Tennessee Knoxville and 
 * the Spallation Neutron Source at Oak Ridge National Laboratory under: 
 *   Support of HFIR/SNS Analysis Software Development 
 *   UT-Battelle contract #:   4000036212
 *   Date:   Oct. 1, 2004 - Sept. 30, 2006
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.2  2005/08/04 22:42:33  cjones
 *  Updated comment header and javadocs
 *
 *  Revision 1.1  2005/07/19 15:56:15  cjones
 *  Add LogScaleColorModel for Scene coloring.
 * 
 */

package gov.anl.ipns.ViewTools.Components;

import java.lang.IllegalArgumentException;
import java.awt.image.IndexColorModel;
import java.awt.Color;

import gov.anl.ipns.ViewTools.Components.PseudoLogScaleUtil;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;

/**
 *    This class maps floating point values to colors.  The pseudo-color scale 
 *  may be specified to be any of the named color scales from the IndexColorMaker 
 *  class.  The range of values in the array must be set before colors may be
 *  returned. The values are scaled logarithmically before being mapped to the 
 *  pseudo-color scale.  If the array contains negative values, one of the "dual"
 *  color models from the IndexColorMaker should be used.  The shape of the log 
 *  function used for the scaling can be adjusted to control the apparent 
 *  brightness of the color. 
 * 
 *   @see gov.anl.ipns.ViewTools.Components.PseudoLogScaleUtil
 *   @see gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
 */
public class LogScaleColorModel
{
 /**
  * "Color Model" - This constant String is a key for referencing the state
  * information about the IndexColorModel used. The value
  * referenced by this key is a String name constant from IndexColorMaker.java.
  */
  public static final String COLOR_MODEL = "Color Model";
  
 /**
  * "Log Scale" - This constant String is a key for referencing the state
  * information about the log scale table used to map data values to colors
  * within the colorscale. The value referenced by this key is an array of
  * bytes.
  */
  public static final String LOG_SCALE   = "Log Scale";
  
 /**
  * "Two Sided" - This constant String is a key for referencing the state
  * information about displaying the data. If one-sided, negative data will
  * be mapped to zero and a one-sided color model will be used, where as
  * two-sided data will remain negative and be mapped to colors on a two-sided
  * color model.
  */
  public static final String TWO_SIDED   = "Two Sided";
	
  
  private final int       LOG_TABLE_SIZE      = 60000;
  private final int       NUM_POSITIVE_COLORS = 127; 
  private final byte      ZERO_COLOR_INDEX    = (byte)NUM_POSITIVE_COLORS;
  private float           min_data = 0;
  private float           max_data = 3;
  
  private IndexColorModel color_model;
  private String          color_model_string;
  private byte[]          log_scale;
  private boolean         isTwoSided = true;
	
  /**
   * Constructor.  Sets color model to heated color scale 2 with 
   * dual color mode.  The logarithmic scale is initialized to
   * zero, i.e. linear scale.
   */
  public LogScaleColorModel( )
  {
    color_model_string = IndexColorMaker.HEATED_OBJECT_SCALE_2;
    color_model =
    IndexColorMaker.getDualColorModel( color_model_string,
          NUM_POSITIVE_COLORS );
 
    log_scale = new byte[LOG_TABLE_SIZE];
    setLogScale( 0 );
  }
  
  /**
   *  Change the control parameter for the logarithmic scaling applied to
   *  the data values.  If the data has negative values, the logarithmic
   *  scaling is applied to the absolute value of the values.
   *
   *  @param   s The control parameter, s, clamped to the range [0,100].  
   *           If s is 0, the scale is essentially linear.  If s is 100, 
   *           the relative intensity of small values is greatly increased 
   *           so the image is lightened.
   *  @throws IllegalArgumentException If parameter is outside of bounds.
   */
  public void setLogScale( double s )
  {
  	if(s > 100 || s < 0)
  	  throw new IllegalArgumentException("Log scale control out of bounds.");
  	
  	// Create log scaler and generate log scale table
    PseudoLogScaleUtil log_scaler = new PseudoLogScaleUtil(
	                                  0f, (float)LOG_TABLE_SIZE,
	              		              0f, NUM_POSITIVE_COLORS );
    
    for( int i = 0; i < LOG_TABLE_SIZE; i++ )
	  log_scale[i] = (byte)(log_scaler.toDest(i,s));
  }
  
 /**
  *  Change the color model to used for the image.  If the data has negative
  *  values, one of the "Dual" color models should be used.
  *
  *  @param   color_scale_name  Name of the new color scale to use.
  *                             Supported color scales are listed in
  *                             the IndexColorMaker class.
  *  @param   twosided          Flag that determines whether a color scale 
  *                             that includes colors for both positive and 
  *                             negative values is used, or if only positive
  *                             values are represented.
  * 
  *  @see IndexColorMaker
  */
  public void setNamedColorModel( String   color_scale_name,
                                  boolean  twosided   )
  {
    isTwoSided = twosided;
    color_model_string =  color_scale_name;
    if( isTwoSided )
      color_model = IndexColorMaker.getDualColorModel( color_model_string,
                                                       NUM_POSITIVE_COLORS );
    else
      color_model = IndexColorMaker.getColorModel( color_model_string,
                                                   NUM_POSITIVE_COLORS );
  }
  
 /**
  *  Get the name of the color model currently set.
  *
  *  @return Name of the color scale being used. Supported color 
  *          scales are listed in the IndexColorMaker class.
  *  @see IndexColorMaker
  */
  public String getNamedColorModel( )
  {
  	return color_model_string;
  }
  
 /**
  *  Determine if "dual" color model is being used.  If true, then
  *  color scale includes colors for negative and positive numbers.
  *  If false, color scale only includes colors for positive numbers.
  *
  *  @return True for two sided model, false otherwise.
  */
  public boolean isTwoSideModel( )
  {
  	return isTwoSided;
  }
  
 /**
  * This method will set the data range to [data_min,data_max]. 
  *
  *  @param  data_min The minimum data value mapped to the minimum color.
  *  @param  data_max The maximum data value to be mapped to the max color.
  */
  public void setDataRange( float data_min, float data_max )
  {
    // Make sure data_min < data_max
    if( data_min > data_max )
    {
      float swap = data_min;
      data_min = data_max;
      data_max = swap;
    }
    // Prevent data_min = data_max
    if( data_min == data_max )
      data_max = data_min + 1;
    // Set min/max_data
    min_data = data_min;
    max_data = data_max;

  } 

 /**
  *  Get the minimum value of the data represented.
  *
  *  @return  min_data
  */
  public float getDataMin()
  {
    return min_data;
  }
 
 /**
  *  Get the maximum value of the data represented.
  *
  *  @return  max_data
  */
  public float getDataMax()
  {
    return max_data;
  } 
 
 /**
  * Generates the color from given data value based on set color scale
  * with applied logarithmic scaling. If the data has negative values, 
  * the logarithmic scaling is applied to the absolute value of the values.
  * 
  * @param  data_value  The data value that gets mapped to a color.
  * @return The color for that data value.
  * @throws IllegalArgumentException If data_value is outside of value range.
  */ 
  public Color getColor(float data_value)
  {
  	// Make sure the data value is in given range
    float max_abs = 0;
    if ( Math.abs( max_data ) > Math.abs( min_data ) )
      max_abs = Math.abs( max_data );
    else
      max_abs = Math.abs( min_data );

    if(Math.abs(data_value) > max_abs)
      throw new IllegalArgumentException("Value outside of valid range. " +
    	 							   "Please use setDataRange(min, max)");
    
    // If the range is 0, then the value is scaled to 0
    // Otherwise, scale the value to a log table index
    float scale_factor = 0;
    if ( max_abs > 0 )
      scale_factor = (LOG_TABLE_SIZE - 1) / max_abs;
    else
      scale_factor = 0;
    
    byte zero_index = 0;
    if( isTwoSided )
      zero_index = ZERO_COLOR_INDEX;
    
    // Scale
    float temp = 0;
    temp = data_value * scale_factor;
	
    // Clamp to edges of Log scale table
    if( temp > LOG_TABLE_SIZE - 1 )
	  temp = LOG_TABLE_SIZE - 1;
    else if( temp < -(LOG_TABLE_SIZE - 1) )
	  temp = -(LOG_TABLE_SIZE - 1);
    
    // Find index to map into color table
  	int index = 0;
	if ( temp >= 0 )
      index = (int)(zero_index + log_scale[(int)temp]);
    else
      index = (int)(zero_index - log_scale[(int)(-temp)]);

	// Generate RGB int representing the color
	int rgb = color_model.getRGB(index);
	
	return new Color(rgb);
  }
}