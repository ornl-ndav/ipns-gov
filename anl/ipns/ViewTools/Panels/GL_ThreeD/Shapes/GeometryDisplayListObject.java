/*
 * File:  GeometryDisplayListObject.java
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
 * Revision 1.1  2004/06/18 19:21:26  dennis
 * Moved to Shapes package.
 *
 * Revision 1.1  2004/05/28 20:51:13  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes;

import javax.media.opengl.*;

/**
 *  This class is the base class for ThreeD_GL_Objects that store the 
 *  geometric information about a graphical object in an OpenGL display
 *  list, and then destroy their local copy of the data to avoid duplicating 
 *  the data. 
 */

abstract public class GeometryDisplayListObject extends GL_Shape
{
                                      // ID for the GL display list for the 
                                      // OpenGL commands to draw the object

  private int   draw_list_id = INVALID_LIST_ID;  


  abstract protected void DrawGeometry( GLAutoDrawable drawable );

  abstract protected void Clear();

  abstract protected boolean newData();


  synchronized protected void Draw( GLAutoDrawable drawable )
  {
     DrawGeometry( drawable );
     return;
  }


  protected void finalize()
  {                                           // free our display list if it
                                              // was allocated.
    if ( draw_list_id != INVALID_LIST_ID && my_panel != null )
      my_panel.ChangeOldLists( draw_list_id );

    super.finalize();
  }

}
