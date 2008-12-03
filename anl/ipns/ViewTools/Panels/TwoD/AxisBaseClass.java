/*
 * File: AxisBaseClaee.java 
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


import java.awt.*;
import javax.swing.*;

/**
 * This class is an abstract base class for Drawables that draw a calibrated 
 * axis with the specified points marked.
 * 
 */
public abstract class AxisBaseClass extends Drawable
{
  protected int      x0,               // coordinates of lower left corner of
                     y0,               // axis region, in pixels with y  
                                       // increasing upward.
                     width,            // width and height of axis in pixels
                     height;
  protected double   min,
                     max;
  protected double   real_height = 1;  // We'll work on a virtual rectangle
                                    // [min,max] X [0,1] to draw the axis.
  protected double[] points;

  protected TextDrawable[] labels;

  public final Font font = new Font("SansSerif", Font.PLAIN, 9 );


  /**
   * Construct an axis of the specified dimensions, with
   * the specified calibrations.
   * 
   * @param x0      The x-coordinate, of the lower left hand corner of the 
   *                rectangle containing the axis, specified in pixel 
   *                coordinates with y  INCREASING UPWARD 
   * @param y0      The y-coordinate, of the lower left hand corner of the 
   *                rectangle containing the axis, specified in pixel 
   *                coordinates with y  INCREASING UPWARD 
   * @param width   The width of the axis box in pixels 
   * @param height  The height of the axis box in pixels 
   * @param min     The real number associated with the
   *                left hand end point of the axis
   * @param max     The real number associated with the
   *                right hand end point of the axis
   * @param points  The points to mark along the axis
   */
  public AxisBaseClass( int      x0,
                        int      y0,
                        int      width,
                        int      height,
                        double   min,
                        double   max,
                        double[] points )
  {
    this.x0     = x0;
    this.y0     = y0;
    this.width  = width;
    this.height = height;
    this.min    = min;
    this.max    = max;
    this.points = points;
  }

}
