/*
 * File:  Lines.java
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
 * Revision 1.1  2004/06/18 19:21:27  dennis
 * Moved to Shapes package.
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes;

import javax.media.opengl.*;
import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.ViewTools.Panels.GL_ThreeD.ThreeD_GL_Panel;

public class Lines extends GeometryDisplayListObject
{
  Vector3D start[]; 
  Vector3D end[]; 

  public Lines( ThreeD_GL_Panel panel, 
                Vector3D        new_start[], 
                Vector3D        new_end[] )
  {
    my_panel = panel;
    set( new_start, new_end );
  }


  public void set( Vector3D new_start[], Vector3D new_end[] )
  {
    if ( new_start == null || new_start.length == 0  ||
         new_end   == null || new_end.length   == 0   ) 
    {
      System.out.println("Lines.set() called with empty start or end array");
      return;
    }
    
    int n_points = new_start.length;
    if ( n_points > new_end.length )
      n_points = new_end.length;

    start = new Vector3D[ n_points ];
    end   = new Vector3D[ n_points ];
    for ( int i = 0; i < n_points; i++ )
    {
      start[ i ] = new Vector3D( new_start[i] );
      end[ i ] = new Vector3D( new_end[i] );
    }

    rebuild_list = true;
  }


  protected void Clear()
  {
    start = null;
    end = null;
    clearList();
  }


  protected boolean newData()
  {
    if ( start != null )
      return true;
    else
      return false;
  }


  protected void DrawGeometry( GLAutoDrawable drawable )
  {
    GL gl = drawable.getGL();
    float point[];

    if ( newData() )
    {
      gl.glBegin( GL.GL_LINES );
        for ( int i = 0; i < start.length; i++ )
        {  
          point = start[i].get();
          gl.glVertex3f( point[0], point[1], point[2] );
          point = end[i].get();
          gl.glVertex3f( point[0], point[1], point[2] );
        }
      gl.glEnd();
    }
  }

}
