/*
 * File:  ColoredPoints.java
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.2  2004/06/29 13:31:50  dennis
 * Added size parameter to control size of points.
 *
 * Revision 1.1  2004/06/18 19:21:23  dennis
 * Moved to Shapes package.
 *
 * Revision 1.1  2004/05/28 20:51:10  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes;

import net.java.games.jogl.*;
import gov.anl.ipns.MathTools.Geometry.*;

import java.awt.*;

public class ColoredPoints extends GeometryDisplayListObject
{
  private float point[][] = null;    // temporarily hold points and colors in
                                     // Nx3 arrays
  private float color[][] = null;

  private float point_size = 1;


  public ColoredPoints( Vector3D new_points[], Color new_colors[], float size )
  {
    set( new_points, new_colors, size );
  }


  public void set( Vector3D new_points[], Color new_colors[], float size )
  {
    if ( new_points == null || new_points.length == 0 )
    {
      System.out.println("ColoredPoints.set() called with empty points array");
      return;
    }
    
    if ( size >= 1 )
      point_size = size;

    float temp[];
    point = new float[new_points.length][3];
    System.out.println("n points = " + point.length );
    for ( int i = 0; i < point.length; i++ )
    {
      temp = new_points[i].get();
      for ( int k = 0; k < 3; k++ )
        point[i][k] = temp[k];
    }
    
    int n_colors = 0;
    color = new float[point.length][4];
    if ( new_colors != null && new_colors.length > 0 ) 
    {
      n_colors = new_colors.length;
      if ( n_colors > point.length )
        n_colors = point.length;
      
      System.out.println("n_colors = " + n_colors );
      for ( int i = 0; i < n_colors; i++ )
      {
        if ( new_colors[i] == null )
          System.out.println( "new_colors = null " + i );
        color[i][0] = new_colors[i].getRed()  /255.0f;
        color[i][1] = new_colors[i].getGreen()/255.0f;
        color[i][2] = new_colors[i].getBlue() /255.0f;
      }
    }
                                                      // points without colors
    for ( int i = n_colors; i < point.length; i++ )  // default to white
    {
      color[i][0] = 1;
      color[i][1] = 1;
      color[i][2] = 1;
      color[i][3] = 1;
    }

    rebuild_list = true;
  }


  protected void Clear()
  {
    point = null;
    color = null;
  }


  protected boolean newData()
  {
    if ( point != null )
      return true;
    else
      return false;
  }


  protected void DrawGeometry( GLDrawable drawable )
  {
    GL gl = drawable.getGL();

    if ( point != null )
    {
      gl.glPointSize(point_size);
      gl.glBegin( GL.GL_POINTS );
        for ( int i = 0; i < point.length; i++ )
        {  
          gl.glVertex3f( point[i][0], point[i][1], point[i][2] );
//        gl.glColor3f( color[i][0], color[i][1], color[i][2] );
         gl.glMaterialfv( GL.GL_FRONT_AND_BACK,
                            GL.GL_AMBIENT_AND_DIFFUSE,
                            color[i] );
        }
      gl.glEnd();
    }
  }

}
