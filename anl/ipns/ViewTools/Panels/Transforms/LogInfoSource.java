/*
 * File:LogInfoSource.java 
 *  
 * Copyright (C) 2007 Andrew Moe
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *            
 * Modified:
 *
 * $Log$
 */
package gov.anl.ipns.ViewTools.Panels.Transforms;

import gov.anl.ipns.ViewTools.Components.LogScaleUtil;

/**
 * This class is used to make a information string with a logarithmic point 
 * from a linear point.
 */
public class LogInfoSource implements ICoordInfoSource
{
  /**
   * LOG_XY is a flag used in the constructor to specify that the X and Y 
   * values in the info string are to both be logarithmic.
   */
  public static final int LOG_XY = 0;
  
  /**
   * LOG_X is a flag used in the constructor to specify that the X and Y 
   * values in the info string are to be logarithmic and linear, respectively.
   */
  public static final int LOG_X  = 1;
  
  /**
   * LOG_Y is a flag used in the constructor to specify that the X and Y 
   * values in the info string are to be linear and logarithmic, respectively.
   */
  public static final int LOG_Y  = 2;
    
  private CoordJPanel cjp;
  private String format_str;
  
  private CoordTransform local_transform;
  private CoordBounds    local_bounds;
  private int log_axis;
  
  /**
   * 
   * @param digits - The digits of precision to round the point to.
   * @param cjp - The CoordJPanel where the point is coming from.
   * @param axis - The specified axis to convert.
   */
  public LogInfoSource(int digits, CoordJPanel cjp, int axis)
  {
    this.cjp = cjp;
    
    format_str = "%."+digits+"f";    
    local_transform = cjp.getLocal_transform();
    log_axis = axis;
  }
  
  /**
   * TODO: This method is still a bit inaccurate.  The values in the info 
   * string become more incorrect as they increase.  This is potentially 
   * because the bounds in the display panel are expanded to allow head-room 
   * for the peaks; we are not getting the correct bounds in this method.
   * 
   * @param pix_x - The x value of the point to possibly convert.
   * @param pix_y - The y value of the point to possibly convert.
   * @return String - a String containing the logarithmic/linear point.
   */
  public String getInfoString(int pix_x, int pix_y) 
  {
    local_bounds = cjp.getLocalWorldCoords();
    
    float linear_x = local_transform.MapXFrom( pix_x );
    float linear_y = local_transform.MapYFrom( pix_y );
    String linear_x_str = String.format(format_str, linear_x);
    String linear_y_str = String.format(format_str, linear_y);
    
    //Quick Fix: if the scaled minimum is 0 or less, I'm making it 1
    float ymin = local_bounds.getY2();
    if(ymin <= 0)
    {
      ymin = 1f;
    }
    ////////
    
    LogScaleUtil log_util_x = new LogScaleUtil(local_bounds.getX1(),local_bounds.getX2());
    //See 'new LogScaleUtil' in GraphJPanel.paintComponents in similar case
    LogScaleUtil log_util_y = new LogScaleUtil(ymin,local_bounds.getY1(),local_bounds.getY2(),local_bounds.getY1());
    
    float log_x = log_util_x.toSource(linear_x);
    float log_y = log_util_y.toSource(linear_y);
    String log_x_str = String.format(format_str, log_x);
    String log_y_str = String.format(format_str, log_y);
    
    //assembling the return_string
    String return_str = "[";
    if(log_axis == LOG_XY)
    {
      return_str += log_x_str+","+log_y_str;
    }
    else if(log_axis == LOG_X)
    {
      return_str += log_x_str+","+linear_y_str;
    }
    else if(log_axis == LOG_Y)
    {
      return_str += linear_x_str+","+log_y_str;
    }
    return_str += "]";
    
    //System.out.println("AxisState: "+log_axis);
    //System.out.println("ReturnStr: \""+return_str+"\"");
    //System.out.println("LinearPoint: ("+linear_x+","+linear_y+")");
    //System.out.println("LinearXBound: ["+local_bounds.getX1()+","+local_bounds.getX2()+"]");
    //System.out.println("ScaledLinearYBound: ["+ymin+","+local_bounds.getY1()+"]");
    //System.out.println("UnscaledLinearYBound: ["+local_bounds.getY2()+","+local_bounds.getY1()+"]");    
    
    return return_str;
  }
}
