/*
 * File:  LineStrip.java
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
 * Revision 1.1  2004/07/23 13:14:26  dennis
 * Initial version of class for a "polyline".
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes;

import net.java.games.jogl.*;
import gov.anl.ipns.MathTools.Geometry.*;

public class LineStrip extends GeometryDisplayListObject
{
  Vector3D points[]; 

  public LineStrip( Vector3D new_points[] )
  {
    set( new_points );
  }


  public void set( Vector3D new_points[] )
  {
    if ( new_points == null || new_points.length == 0 ) 
    {
      System.out.println("LineStrip.set() called with empty array");
      return;
    }
    
    int n_points = new_points.length;
    points = new Vector3D[ n_points ];
    for ( int i = 0; i < n_points; i++ )
      points[ i ] = new Vector3D( new_points[i] );

    rebuild_list = true;
  }


  protected void Clear()
  {
    points = null;
  }


  protected boolean newData()
  {
    if ( points != null )
      return true;
    else
      return false;
  }


  protected void DrawGeometry( GLDrawable drawable )
  {
    GL gl = drawable.getGL();
    float point[];

    if ( newData() )
    {
      gl.glBegin( GL.GL_LINE_STRIP );
        for ( int i = 0; i < points.length; i++ )
        {  
          point = points[i].get();
          gl.glVertex3f( point[0], point[1], point[2] );
        }
      gl.glEnd();
    }
  }

}
