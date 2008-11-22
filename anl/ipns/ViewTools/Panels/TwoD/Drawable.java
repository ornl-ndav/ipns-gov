/*
 * File: Drawable.java
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
import java.awt.Color;
import java.awt.Point;

/**
 * This class is an abstract base class for graphical objects that can
 * be drawn in a TwoD_JPanel.  This class includes convenience methods for 
 * setting the color, position and rotation angle for IDrawable objects.
 * NOTE: Transformations are used to changed the basic graphics coordinate
 * system so that the origin (0,0) is in the lower left hand corner of the
 * display, rather than in the upper right hand corner.  This effectively
 * changes from a default left-hand coordinate system to a default right 
 * hand coordinate system.  Derived classes for which the "handedness" 
 * is important, such as TextDrawable and ImageDrawable must include an 
 * additional transformation to correct the handedness.
 *  
 * @author Dennis Mikkelson
 *
 */
abstract public class Drawable implements IDrawable
{
  private double rotation_angle = 0;       // object rotation angle in degrees
  private int    x_shift = 0,              // object offset in pixels
                 y_shift = 0;
  private double sx = 1,                   // scale factors in x,y directions
                 sy = 1;
  private Color  color = null;             // object color will be obtained
                                           // from the TwoD_JPanel or previous
                                           // graphics context if not set.
  
  /**
   * Set the color that will be used when this object is drawn.
   * @param color  The Color object specifying the color for this object.
   */
  public void setColor( Color color )
  {
    this.color = color;
  }
  
  
  /**
   * Get the color that is used when this object is drawn.
   * 
   * @return The Color for this object.
   */
  public Color getColor()
  {
    return this.color;
  }
  
  /**
   * Set the angle of a rotation that will be applied to this object.
   * If this method is not called, the rotation angle will be zero
   * by default. The positive direction of rotation is counter clockwise.
   * 
   * @param angle The rotation angle in degrees.
   */
  public void setRotationAngle( double angle )
  {
    rotation_angle = Math.PI * angle / 180;
  }
  
  /**
   * Get the currently set rotation angle.
   * 
   * @return The current rotation angle in degrees.
   */
  public double getRotationAngle()
  {
    return rotation_angle * 180/Math.PI;
  }
  

  /**
   * Set the scale factors to be applied to this object.
   * If this method is not called, the scale factors will be one 
   * by default. 
   * 
   * @param angle The rotation angle in degrees.
   */
  public void setScaleFactors( double sx, double sy )
  {
    this.sx = sx;
    this.sy = sy;
  }



  /**
   * Set the position at which this object will be drawn.  The point of
   * the object that is shifted to the specified point may vary from 
   * object to object. 
   *  
   * @param point  The (x, y) coordinates at which the object will be 
   *               drawn.  The x coordinate is the horizontal offset 
   *               from the left side of the display. 
   */
  public void setPosition( Point point )
  {
    x_shift = point.x;
    y_shift = point.y;
  }
  
  
  /**
   * Get the position at which this object will be drawn.
   * 
   * @return A Point object containing the x and y coordinates where 
   *         the object will be drawn.
   */
  public Point getPosition()
  {
    return new Point(x_shift, y_shift );
  }
  
  
  /**
   * Draw this object using the specified graphics context.  Concrete
   * derived classes MUST implement this by specifying the actual 
   * drawing commands to draw the object using the specified graphics
   * context.  The concrete method should first call the setAttributes
   * method of this class to copy the attributes of this object ( color
   * position & rotation angle) into a new graphics context before actually
   * drawing the object.  The modified graphics context should be used
   * for drawing and disposed of after drawing.
   * 
   * @param graphics The graphics context to be modified and used for
   *                 drawing.
   */
  abstract public void draw(Graphics2D graphics);
  
 
  /**
   * This method should be called by the draw() method of derived classes
   * to set the color, position and rotation angle for the specified 
   * object in a new graphics context, before doing the actual drawing.
   * 
   * @param graphics  The graphics context that will be modified before
   *                  drawing.
   * @return The modified graphics context that should be used by the
   *         draw routine of the derived class.
   */
  protected Graphics2D setAttributes( Graphics2D graphics )
  {
    Graphics2D new_graphics = (Graphics2D)graphics.create();
    if ( x_shift != 0 || y_shift != 0 )
      new_graphics.translate( x_shift, y_shift );

    if ( rotation_angle != 0 )
      new_graphics.rotate( rotation_angle );

    if ( sx != 1.0 || sy != 1.0 )
      new_graphics.scale( sx, sy );

    if ( color != null )
      new_graphics.setColor( color );
    
    return new_graphics;
  }

}
