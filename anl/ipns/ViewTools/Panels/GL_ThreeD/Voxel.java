/*
 * File:  Voxel.java
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
 * Revision 1.1  2004/05/28 20:51:19  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;

import net.java.games.jogl.*;
import gov.anl.ipns.MathTools.Geometry.*;

public class Voxel extends GL_Shape
{
  private Vector3D corner[][][] = null;

  public Voxel( Vector3D corner[][][] )
  {
    this.corner = new Vector3D[2][2][2];
    for ( int row = 0; row < 2; row++ )
      for ( int col = 0; col < 2; col++ )
        for ( int page = 0; page < 2; page++ )
          this.corner[page][row][col] = 
               new Vector3D( corner[page][row][col] );
  }

  protected void Draw( GLDrawable drawable )
  {
     GL gl = drawable.getGL();

     float pt[];
     gl.glBegin( GL.GL_QUADS );
       pt = normal( corner[0][0][0], corner[1][0][0], corner[1][1][0] );
       gl.glNormal3f( pt[0], pt[1], pt[2] );
       pt = corner[0][0][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][0][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][1][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[0][1][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );

       pt = normal( corner[0][1][0], corner[1][1][0], corner[1][1][1] );
       gl.glNormal3f( pt[0], pt[1], pt[2] );
       pt = corner[0][1][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][1][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][1][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[0][1][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );

       pt = normal( corner[0][1][1], corner[1][1][1], corner[1][0][1] );
       gl.glNormal3f( pt[0], pt[1], pt[2] );
       pt = corner[0][1][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][1][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][0][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[0][0][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );

       pt = normal( corner[0][0][1], corner[1][0][1], corner[1][0][0] );
       gl.glNormal3f( pt[0], pt[1], pt[2] );
       pt = corner[0][0][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][0][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][0][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[0][0][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );

       pt = normal( corner[1][0][0], corner[1][0][1], corner[1][1][1] );
       gl.glNormal3f( pt[0], pt[1], pt[2] );
       pt = corner[1][0][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][0][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][1][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[1][1][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );

       pt = normal( corner[0][0][0], corner[0][1][0], corner[0][1][1] );
       gl.glNormal3f( pt[0], pt[1], pt[2] );
       pt = corner[0][0][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[0][1][0].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[0][1][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
       pt = corner[0][0][1].get(); gl.glVertex3f( pt[0], pt[1], pt[2] );
     gl.glEnd();
   }


  private float[] normal( Vector3D p0, Vector3D p1, Vector3D p2 )
  {
    Vector3D e1 = new Vector3D( p1 );
    e1.subtract( p0 );

    Vector3D e2 = new Vector3D( p2 );
    e2.subtract( p1 );

    Vector3D normal = new Vector3D();
    normal.cross( e1, e2 );
    normal.normalize();

    return normal.get();
  }

}
