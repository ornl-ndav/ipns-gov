/*
 * File:  Ball.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2002/10/29 22:16:31  dennis
 * Representation of a 3D "ball".  For efficiency this is currently drawn
 * as a square with edges aligned with the pixel coordinate axes.  The size
 * of the square is based on the radius of the "ball".
 *
 */

package DataSetTools.components.ThreeD;

import java.awt.*;
import java.io.*;
import DataSetTools.math.*;
import DataSetTools.components.image.*;


/**
 *  This class represents a flat shaded 3D sphere of one color.  Currently
 *  this is just drawn as a 2D square.
 */
public class Ball  extends     ThreeD_Object
                   implements  Serializable
{
  private float radius = 0;

  /** 
   *  Construct a Ball using the specified center, radius and color.
   *
   *  @param center  The 3D point giving the center of this Ball 
   *
   *  @param radius  The radius of the Ball in 3D "object space"
   *
   *  @param color   The color to use for this Ball 
   */
  
  public Ball( Vector3D center, float radius, Color color )
  {
    super( null, color );
    this.radius = radius;
                                    // attempt to track the projected 3D radius
                                    // of the ball, based on three adjacent
                                    // points.
    float coords[] = center.get();
    vertices = new Vector3D[4];
    vertices[0] = new Vector3D( center );
    vertices[1] = new Vector3D( coords[0]+radius, coords[1], coords[2] );
    vertices[2] = new Vector3D( coords[0], coords[1]+radius, coords[2] );
    vertices[3] = new Vector3D( coords[0], coords[1], coords[2]+radius );
    x = new float[4]; 
    y = new float[4]; 
  }

  /**
   *  Draw this Ball using the projected 2D points in the specified
   *  graphics context g.
   *
   *  @param  g   The graphics object into which the Ball is to be drawn.
   */

  public void Draw( Graphics g )
  {
     if ( clipped )
       return;

     float sum_sq = 0; 
     for ( int i = 1; i < x.length; i++ )
       sum_sq += (x[i]-x[0])*(x[i]-x[0]) + (y[i]-y[0])*(y[i]-y[0]);

     float r = (float)Math.sqrt( sum_sq );
    
     g.setColor( color );
     if ( r >= 0.51 )
       g.fillRect( (int)(x[0]-r), (int)(y[0]-r), (int)(2*r), (int)(2*r) );    
     else
       g.drawLine( (int)x[0], (int)y[0], (int)x[0], (int)y[0] );    
  }

}
