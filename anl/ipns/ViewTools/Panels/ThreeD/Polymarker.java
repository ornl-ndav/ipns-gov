/*
 * File:  Polymarker.java
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
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.3  2003/10/15 23:27:25  dennis
 * Fixed javadocs to build cleanly with jdk 1.4.2
 *
 * Revision 1.2  2002/11/27 23:12:52  pfpeterson
 * standardized header
 *
 */

package DataSetTools.components.ThreeD;

import java.awt.*;
import java.io.*;
import DataSetTools.math.*;
import DataSetTools.components.image.*;


/**
 *  This class represents a 3D polymarker of one color.
 */
public class Polymarker  extends     ThreeD_Object
                         implements  Serializable
{
  public static final int DOT   = 1;
  public static final int PLUS  = 2;
  public static final int STAR  = 3;
  public static final int BOX   = 4;
  public static final int CROSS = 5;
  
  private int type = 1;
  private int size = 1;

  /**
   *  Construct a polymarker consisting of marks of the specified type at
   *  each of the specified vertices. 
   *
   *  @param verts  Array of 3D points giving the points where the markers
   *                are to be drawn.
   *
   *  @param color  The color to use for this Polymarker
   */
  public Polymarker( Vector3D verts[], Color color )
  {
    super( verts, color );
  }


  /**
   *  Set the size of the markers to be drawn.  The sizes are specified in
   *  terms of the number of pixels to move on each side of the pixel at
   *  which the marker is placed.  This affects all of the marker types,
   *  except the DOT marker.  A DOT marker is always just one pixel.
   *
   *  @param size  Adjust the size of the marker.  The actual size in pixels
   *               is 2*size + 1, since this parameter specifies the distance
   *               to draw from the center position.
   */
  public void setSize( int size )
  {
    if ( size < 1 )
      return;
 
    this.size = size;
  } 

  /**  
   *  Specify the type of marker to be placed at each point of this polymarker.
   *
   *  @param  type  Type code for the marker to use.  This should be one of
   *                the defined marker types such as DOT, PLUS, STAR, etc.
   */
  public void setType( int type )
  {
    if ( type < DOT || type > CROSS )
      return;

    this.type = type;
  }


  /**
   *  Draw this Polymarker using the projected 2D points in the specified
   *  graphics context g.
   *
   *  @param  g   The graphics object into which the Polymarker is to be drawn.
   */
  public void Draw( Graphics g )
  {
     if ( clipped )
       return;

     int x0;
     int y0;
    
     g.setColor( color );
     
     for ( int i = 0; i < x.length; i++ )
     {
       x0 = (int)x[i];
       y0 = (int)y[i];
        
       if ( type == DOT )
         g.drawLine( x0, y0, x0, y0 );      
       else if ( type == PLUS )
       {
         g.drawLine( x0-size, y0,      x0+size, y0      );      
         g.drawLine( x0,      y0-size, x0,      y0+size );      
       }
       else if ( type == STAR )
       {
         g.drawLine( x0-size, y0,      x0+size, y0      );      
         g.drawLine( x0,      y0-size, x0,      y0+size );      
         g.drawLine( x0-size, y0-size, x0+size, y0+size );      
         g.drawLine( x0-size, y0+size, x0+size, y0-size );      
       }
       else if ( type == BOX )
       {
         g.drawLine( x0-size, y0-size, x0-size, y0+size );      
         g.drawLine( x0-size, y0+size, x0+size, y0+size );      
         g.drawLine( x0+size, y0+size, x0+size, y0-size );      
         g.drawLine( x0+size, y0-size, x0-size, y0-size );      
       }
       else   // type = CROSS
       {
         g.drawLine( x0-size, y0-size, x0+size, y0+size );      
         g.drawLine( x0-size, y0+size, x0+size, y0-size );      
       }
     }
  }


}
