/*
 * File:  GLU_QuadricObject.java
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

abstract public class GLU_QuadricObject  extends  GL_Shape
{
  static protected GLUquadric quadric = null;

  private int draw_style  = GLU.GLU_FILL;
  private int orientation = GLU.GLU_OUTSIDE;
  private int normal_type = GLU.GLU_SMOOTH;

  public void setDrawStyle( int style )
  {
    if ( style == GLU.GLU_FILL  || style == GLU.GLU_LINE || 
         style == GLU.GLU_POINT || style == GLU.GLU_SILHOUETTE )
      draw_style = style;
    else
      System.out.println("WARNING, invalid style in setDrawStyle " + style );
  }


  public void setOrientation( int new_orientation )
  {
    if (new_orientation == GLU.GLU_OUTSIDE || new_orientation == GLU.GLU_INSIDE)
      orientation = new_orientation; 
    else
      System.out.println("WARNING, invalid new_orientation in setOrientation " 
                          + new_orientation );
  }


  public void setNormalType( int type )
  {
    if (type == GLU.GLU_NONE || type == GLU.GLU_FLAT || type == GLU.GLU_SMOOTH)
      normal_type = type;
    else
      System.out.println("WARNING, invalid type in setNormalType " + type );
  }


  protected void setDrawOptions( GLU glu )
  {
     if ( quadric == null )
       quadric = new GLUquadric();

     glu.gluQuadricTexture( quadric, isTextured() );
     glu.gluQuadricOrientation( quadric, orientation );
     glu.gluQuadricDrawStyle( quadric, draw_style );
     glu.gluQuadricNormals( quadric, normal_type );
  }
}
