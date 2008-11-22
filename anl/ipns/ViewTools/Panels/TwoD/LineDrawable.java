/*
 * File: LineDrawable.java 
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

package gov.anl.ipns.ViewTools.Panels.TwoD;


import java.awt.Graphics2D;

/**
 * This class is a Drawable that draws a line between the specified 
 * points.  If the position is set to (x,y), then the center of the 
 * line will be at pixel (x,y).
 * 
 * @author Dennis Mikkelson
 *
 */
public class LineDrawable extends Drawable
{
  private int x0, y0;
  private int x1, y1;
  
  /**
   * Construct a LineDrawable of the specified dimensions.
   * 
   * @param x0    The x coordinate of the first endpoint 
   * @param y0    The y coordinate of the first endpoint 
   * @param x1    The x coordinate of the second endpoint 
   * @param y1    The y coordinate of the second endpoint 
   */
  public LineDrawable( int x0, int y0, int x1, int y1 )
  {
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
  }
  
  
  /**
   * Draw this line using the specified graphics context, 
   * and any specified color, position and rotation angle.
   * 
   * @param graphics  The graphics context for drawing this line.
   */
  public void draw(Graphics2D graphics)
  {
    graphics = setAttributes( graphics );     // Use super class method 
                                              // to set up the color, etc. and
                                              // get a new graphics context
                                              // with those attributes set.
    graphics.drawLine( x0, y0, x1, y1 );      // Draw the line 

    graphics.dispose();                       // get rid of the new 
                                              // graphics context 
  }

}
