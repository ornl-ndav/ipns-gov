/*
 * File:  Cube.java
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
 * Revision 1.1  2004/05/28 20:51:10  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;

import net.java.games.jogl.*;

public class Cube extends GL_Shape
{
  private float center_x,
                center_y,
                center_z,
                size;

  public Cube( float      center_x, 
               float      center_y, 
               float      center_z, 
               float      size      )
  {
     this.center_x = center_x;
     this.center_y = center_y;
     this.center_z = center_z;
     this.size = size;
     rebuild_list = true;
  }


  protected void Draw( GLDrawable drawable )
  {
     GL gl = drawable.getGL();

     float x0 = center_x - size/2;
     float y0 = center_y - size/2;
     float z0 = center_z - size/2;

     float x1 = center_x + size/2;
     float y1 = center_y + size/2;
     float z1 = center_z + size/2;

     gl.glBegin( GL.GL_QUADS );
       gl.glNormal3f( 0, 0, -1 );
       gl.glVertex3f( x0, y0, z0 );
       gl.glVertex3f( x1, y0, z0 );
       gl.glVertex3f( x1, y1, z0 );
       gl.glVertex3f( x0, y1, z0 );

       gl.glNormal3f( 0, 1, 0 );
       gl.glVertex3f( x0, y1, z0 );
       gl.glVertex3f( x1, y1, z0 );
       gl.glVertex3f( x1, y1, z1 );
       gl.glVertex3f( x0, y1, z1 );

       gl.glNormal3f( 0, 0, 1 );
       gl.glVertex3f( x0, y1, z1 );
       gl.glVertex3f( x1, y1, z1 );
       gl.glVertex3f( x1, y0, z1 );
       gl.glVertex3f( x0, y0, z1 );

       gl.glNormal3f( 0, -1, 0 );
       gl.glVertex3f( x0, y0, z1 );
       gl.glVertex3f( x1, y0, z1 );
       gl.glVertex3f( x1, y0, z0 );
       gl.glVertex3f( x0, y0, z0 );

       gl.glNormal3f( 1, 0, 0 );
       gl.glVertex3f( x1, y0, z0 );
       gl.glVertex3f( x1, y0, z1 );
       gl.glVertex3f( x1, y1, z1 );
       gl.glVertex3f( x1, y1, z0 );

       gl.glNormal3f(  -1, 0, 0 );
       gl.glVertex3f( x0, y0, z0 );
       gl.glVertex3f( x0, y1, z0 );
       gl.glVertex3f( x0, y1, z1 );
       gl.glVertex3f( x0, y0, z1 );
     gl.glEnd();
   }

}
