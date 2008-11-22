/*
 * File: OvalDrawable.java 
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
 * This class is a Drawable that draws a filled oval of the specified 
 * size, location and rotation in a TwoD_JPanel.  If the position is set to
 * (x,y), then the center of the oval will be at pixel (x,y).
 * 
 * @author Dennis Mikkelson
 *
 */
public class OvalDrawable extends Drawable
{
  private int width;
  private int height;
  
  /**
   * Construct an OvalDrawable of the specified dimensions.
   * 
   * @param width   The width of the oval in pixels.
   * @param height  The height of the oval in pixels.
   */
  public OvalDrawable( int width, int height )
  {
    this.width = width;
    this.height = height;
  }
  
  
  /**
   * Get the height of the oval in pixels, as specified when this
   * OvalDrawable was constructed.
   * 
   * @return  The height in pixels of this oval.
   */
  public int getHeight()
  {
    return height;
  }
  
  /**
   * Get the width of the oval in pixels, as specified when this
   * OvalDrawable was constructed.
   * 
   * @return  The width in pixels of this oval.
   */
  public int getWidth()
  {
    return width;
  }
  

  /**
   * Draw this OvalDrawable using the specified graphics context, 
   * and any specified color, position and rotation angle.
   * 
   * @param graphics  The graphics context for drawing this oval.
   */
  public void draw(Graphics2D graphics)
  {
    graphics = setAttributes( graphics );     // Use super class method 
                                              // to set up the color, etc. and
                                              // get a new graphics context
                                              // with those attributes set.
    graphics.fillOval( -width/2, -height/2,   // Draw the oval
                        width,    height  );
    graphics.dispose();                       // get rid of the new 
                                              // graphics context 
  }

}
