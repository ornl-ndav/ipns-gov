/*
 * File:  Polyline.java
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
 * Revision 1.1  2001/05/08 21:07:41  dennis
 * Arbitrary single colored polyline that can be drawn
 * by a ThreeD_JPanel.
 *
 *
 */

package DataSetTools.components.ThreeD;

import java.awt.*;
import java.io.*;
import DataSetTools.math.*;
import DataSetTools.components.image.*;

/**
 *  This class represents a 3D polyline of one color.
 */
public class Polyline  extends     ThreeD_Object
                       implements  Serializable
{
  /** 
   *  Construct a polyline consisting of straight line segments of the 
   *  specified color joining the specified vertices in the order listed.
   *
   *  @param verts  Array of 3D points giving the vertices joined by this 
   *                polyline.
   *
   *  @param color  The color to use for this Polyline
   */
  public Polyline( Vector3D verts[], Color color )
  {
    super( verts, color );
  }


  /**
   *  Draw this Polyline using the projected 2D points in the specified
   *  graphics context g.
   *
   *  @param  g   The graphics object into which the Polyline is to be drawn.
   */
  public void Draw( Graphics g )
  {
     if ( clipped )
       return;

     int xtemp[] = new int[ x.length ];
     int ytemp[] = new int[ y.length ];
    
     for ( int i = 0; i < x.length; i++ )
     {
       xtemp[i] = (int)x[i];
       ytemp[i] = (int)y[i];
     }

     g.setColor( color );
     g.drawPolyline( xtemp, ytemp, xtemp.length );      
  }

}
