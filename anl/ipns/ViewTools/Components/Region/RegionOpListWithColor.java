/* 
 * File: RegionOpListWithColor.java
 *  
 * Copyright (C) 2007  Johnathan Morck, Chad Diller
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
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797.
 *
 * Modified:
 * $Log$
 * Revision 1.2  2007/08/07 20:57:21  rmikk
 * Adds a constructor to make a RegionOpListWithColor from a RegionOpList
 *
 * Revision 1.1  2007/04/28 03:24:44  dennis
 * Initial version of class that adds color and opacity information
 * to use when drawing regions.  (Chad Diller & Jonathan Morck)
 *
 */

package gov.anl.ipns.ViewTools.Components.Region;

import java.awt.Color;

/**
 * This class extends RegionOpList by adding information about the color
 * and opacity to use when drawing the region.
 */

public class RegionOpListWithColor extends RegionOpList{

  private float opacity = 1.0f;
  private Color color = Color.white;


  
  public RegionOpListWithColor(){
     
  }
  /**
   * Converts a RegionOpList to a RegionOpListWithColor
   * 
   * @param RegOp  The RegionOp to be copied
   * @param clone  Not implemented yet. If true, it will
   *               make a copy of the HashTable and all the 
   *               RegionOps. Currently this regionOpList is
   *               a reference to the given opList
   */
  public RegionOpListWithColor( RegionOpList RegOp, boolean clone){
     
     regionOpList = RegOp.regionOpList;
     
    
  }
 /**
  *  Get the currently set color for the region
  *
  *  @return the Color to use when drawing the region
  */
  public Color getColor() {
    return color;
  }


 /**
  *  Set the Color to use when the region is drawn.
  *
  *  @param  color  the Color to use when drawing the region
  */
  public void setColor( Color color ) {
   this.color = color;
  }


 /**
  *  Get the currently set opacity to use when drawing the region
  *
  *  @return the opacity to use when drawing the region
  */
  public float getOpacity() {
   return opacity;
  }


 /**
  *  Set the opacity to use when drawing the region
  *
  *  @param opacity  the opacity to use when drawing the region
  */
  public void setOpacity( float opacity ) {
    this.opacity = opacity;
  }

}
