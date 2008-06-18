/*
 * File:  Polymarker.java
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
 * Revision 1.2  2007/08/13 23:50:18  dennis
 * Switched from old JOGL to the JSR231 version of JOGL.
 *
 * Revision 1.1  2004/07/14 16:26:45  dennis
 * Initial version of class for drawing markers at points in 3D using
 * OpenGL.
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes;

import javax.media.opengl.*;
import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.ViewTools.Panels.GL_ThreeD.ThreeD_GL_Panel;

public class Polymarker extends GeometryDisplayListObject
{
  public static final int DOT   = 1;
  public static final int PLUS  = 2;
  public static final int STAR  = 3;
  public static final int BOX   = 4;
  public static final int CROSS = 5;
  public static final int N_MARKER_TYPES = 5;

  private Vector3D points[] = null; 
  private int      type = PLUS;
  private float    size = 1;

  public Polymarker( ThreeD_GL_Panel panel,
                     Vector3D new_points[], int type, float size )
  {
    my_panel = panel;
    set( new_points, type, size );
  }


  public void set( Vector3D new_points[], int type, float size )
  {
    if ( new_points == null || new_points.length == 0 )
    {
      System.out.println("Polymarker.set() called with empty points array");
      return;
    }
    
    int n_points = new_points.length;

    points = new Vector3D[ n_points ];
    for ( int i = 0; i < n_points; i++ )
      points[ i ] = new Vector3D( new_points[i] );

    if ( type >= 1 && type <= N_MARKER_TYPES )
      this.type = type;

    if ( size > 0 )
      this.size = size;

    rebuild_list = true;
  }


  protected void Clear()
  {
    points = null;
    clearList();
  }


  protected boolean newData()
  {
    if ( points != null )
      return true;
    else
      return false;
  }


  protected void DrawGeometry( GLAutoDrawable drawable )
  {
    if ( newData() )
    {
      if ( type == DOT )
        draw_dots( drawable );
      else if ( type == PLUS )
        draw_pluses( drawable );
      else if ( type == STAR )
      {
        draw_pluses( drawable );
        draw_crosses( drawable );
      }
      else if ( type == BOX )
        draw_boxes( drawable );
      else if ( type == CROSS )
        draw_crosses( drawable );
      else
        draw_dots( drawable );
    }
  }


  private void draw_dots( GLAutoDrawable drawable )
  {
    if ( points != null )
    {
      float point[];
      GL gl = drawable.getGL();
      gl.glPointSize( size );
      gl.glBegin( GL.GL_POINTS );
        for ( int i = 0; i < points.length; i++ )
        {
          point = points[i].get();
          gl.glVertex3f( point[0], point[1], point[2] );
        }
      gl.glEnd();
    }
  }


  private void draw_pluses( GLAutoDrawable drawable )
  {
    if ( points != null )
    {
      float point[];
      float step = size/2;
      float center_x, center_y, center_z;

      GL gl = drawable.getGL();
      for ( int i = 0; i < points.length; i++ )
      {
        point = points[i].get();
        center_x = point[0];
        center_y = point[1];
        center_z = point[2];
        gl.glBegin( GL.GL_LINES );
          gl.glVertex3f( center_x - step, center_y, center_z );
          gl.glVertex3f( center_x + step, center_y, center_z );

          gl.glVertex3f( center_x, center_y - step, center_z );
          gl.glVertex3f( center_x, center_y + step, center_z );

          gl.glVertex3f( center_x, center_y, center_z - step );
          gl.glVertex3f( center_x, center_y, center_z + step );
        gl.glEnd();
      }
    }
  }


  private void draw_boxes( GLAutoDrawable drawable )
  {
    if ( points != null )
    {
      float point[];
      float x0, y0, z0;
      float x1, y1, z1;
  
      GL gl = drawable.getGL();
      for ( int i = 0; i < points.length; i++ )
      {
        point = points[i].get();
        x0 = point[0] - size/2;
        y0 = point[1] - size/2;
        z0 = point[2] - size/2;

        x1 = point[0] + size/2;
        y1 = point[1] + size/2;
        z1 = point[2] + size/2;

        gl.glBegin( GL.GL_LINE_LOOP );
          gl.glNormal3f( 0, 0, -1 );
          gl.glVertex3f( x0, y0, z0 );
          gl.glVertex3f( x1, y0, z0 );
          gl.glVertex3f( x1, y1, z0 );
          gl.glVertex3f( x0, y1, z0 );
        gl.glEnd();

        gl.glBegin( GL.GL_LINE_LOOP );
          gl.glNormal3f( 0, 0, 1 );
          gl.glVertex3f( x0, y1, z1 );
          gl.glVertex3f( x1, y1, z1 );
          gl.glVertex3f( x1, y0, z1 );
          gl.glVertex3f( x0, y0, z1 );
          gl.glVertex3f( x0, y1, z1 );
        gl.glEnd();

        gl.glBegin( GL.GL_LINES );
          gl.glNormal3f( -0.7071f, -0.7071f, 0 );
          gl.glVertex3f( x0, y0, z0 );
          gl.glVertex3f( x0, y0, z1 );

          gl.glNormal3f( 0.7071f, -0.7071f, 0 );
          gl.glVertex3f( x1, y0, z0 );
          gl.glVertex3f( x1, y0, z1 );

          gl.glNormal3f( -0.7071f, 0.7071f, 0 );
          gl.glVertex3f( x0, y1, z0 );
          gl.glVertex3f( x0, y1, z1 );

          gl.glNormal3f( 0.7071f, 0.7071f, 0 );
          gl.glVertex3f( x1, y1, z0 );
          gl.glVertex3f( x1, y1, z1 );
        gl.glEnd();
      }
    }
  }

  private void draw_crosses( GLAutoDrawable drawable )
  {
    if ( points != null )
    {
      float point[];
      float step = size/2.828f;
      float center_x, center_y, center_z;

      GL gl = drawable.getGL();
      for ( int i = 0; i < points.length; i++ )
      {
        point = points[i].get();
        center_x = point[0];
        center_y = point[1];
        center_z = point[2];
        gl.glBegin( GL.GL_LINES );
          gl.glVertex3f( center_x - step, center_y - step, center_z );
          gl.glVertex3f( center_x + step, center_y + step, center_z );

          gl.glVertex3f( center_x + step, center_y - step, center_z );
          gl.glVertex3f( center_x - step, center_y + step, center_z );

          gl.glVertex3f( center_x, center_y - step, center_z - step );
          gl.glVertex3f( center_x, center_y + step, center_z + step );

          gl.glVertex3f( center_x, center_y + step, center_z - step );
          gl.glVertex3f( center_x, center_y - step, center_z + step );

          gl.glVertex3f( center_x - step, center_y, center_z - step );
          gl.glVertex3f( center_x + step, center_y, center_z + step );

          gl.glVertex3f( center_x + step, center_y, center_z - step );
          gl.glVertex3f( center_x - step, center_y, center_z + step );
        gl.glEnd();
      }
    }
  }

}
