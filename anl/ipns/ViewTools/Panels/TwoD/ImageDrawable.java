/*
 * File: ImageDrawable.java
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
import java.awt.Image;

/**
 * This class is a Drawable that draws an image of the specified size
 * location and rotation in a TwoD_JPanel. If the position is set to
 * (x,y), then the center of the image will be at pixel (x,y).
 * 
 * @author Dennis Mikkelson
 *
 */
public class ImageDrawable extends Drawable
{
  private Image image;
  private int   width;
  private int   height;

  /**
   * Construct an ImageDrawable using the specified image, and the
   * specified dimensions.
   * 
   * @param image   The image object to be drawn.
   * @param width   The number of pixels in the horizontal direction
   *                to use for drawing the image..
   * @param height  The number of pixels in the vertical direction
   *                to use for drawing the image.
   */
  public ImageDrawable( Image image, int width, int height )
  {
    this.image  = image.getScaledInstance( (int)width, 
                                           (int)height,
                                           Image.SCALE_DEFAULT );
    this.width  = width;
    this.height = height;
  }

  
  /**
   * Get the height of the image, in pixels that was specified when this
   * ImageDrawable was constructed.
   * 
   * @return  The height in pixels of this image.
   */
  public int getHeight()
  {
    return height;
  }
  
  
  /**
   * Get the width of the image, in pixels that was specified when this
   * ImageDrawable was constructed.
   * 
   * @return  The width in pixels of this image.
   */ 
  public int getWidth()
  {
    return width;
  }

  
  /**
   * Draw this ImageDrawable using the specified graphics context, and
   * any specified color, position and rotation angle.
   * 
   * @param graphics  The graphics context for drawing this image.
   */
  public void draw( Graphics2D graphics )
  {
    graphics = setAttributes( graphics );   // Use super class method 
                                            // to set up the position, etc. and
                                            // get a new graphics context
                                            // with those attributes set.
    
    graphics.scale(1, -1);                                // Change to right
    graphics.drawImage(image, -width/2, -height/2, null );// hand coordinates
                                                          // and draw image
    graphics.dispose();                     // Get rid of the new graphics
  }                                         // contex.

}
