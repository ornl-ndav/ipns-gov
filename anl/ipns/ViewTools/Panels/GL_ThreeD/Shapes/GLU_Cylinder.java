/*
 * File:  GLU_Cylinder.java
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
 * Revision 1.1  2004/06/18 19:21:25  dennis
 * Moved to Shapes package.
 *
 * Revision 1.1  2004/05/28 20:51:11  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes;

import net.java.games.jogl.*;

public class GLU_Cylinder extends GLU_QuadricObject
{
  private float base_radius,
                top_radius,
                height;
  private int   slices,
                stacks;

  public GLU_Cylinder( float base_radius, 
                       float top_radius,
                       float height,
                       int   slices, 
                       int   stacks )
  {
     this.base_radius = base_radius;
     this.top_radius  = top_radius;
     this.height      = height;
     this.slices = slices;
     this.stacks = stacks;
     rebuild_list = true;
  }

  protected void Draw( GLDrawable drawable )
  {
     GLU glu = drawable.getGLU();
     setDrawOptions( glu );
     glu.gluCylinder(quadric, base_radius, top_radius, height, slices, stacks);
  }
}
