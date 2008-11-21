/*
 * File:LinearInfoSource.java 
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

import java.io.Serializable;

/**
 * This class is used to make a information string from a linear point.
 */
public class LinearInfoSource implements ICoordInfoSource ,Serializable
{ 
  private CoordTransform local_transform;  
  private String format_str;  
	
  /**
   * @param digits - The digits of precision to round the point to.
   * @param cjp - The CoordJPanel where the point is coming from.
   */
  public LinearInfoSource(int digits, CoordJPanel cjp)
  {    
    local_transform = cjp.getLocal_transform();
    
    format_str = "%."+digits+"f";
  }
	
  /**
   * @return String - a String containing the linear point.
   */
  public String getInfoString(int pix_x, int pix_y) 
  {
    String x = String.format(format_str, local_transform.MapXFrom( pix_x ));
    String y = String.format(format_str, local_transform.MapYFrom( pix_y ));    
    return "["+x+","+y+"]";
  }
}
