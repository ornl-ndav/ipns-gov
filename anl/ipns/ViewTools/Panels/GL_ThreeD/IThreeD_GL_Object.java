/*
 * File:  IThreeD_GL_Object.java
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
 * Revision 1.3  2007/08/13 23:50:17  dennis
 * Switched from old JOGL to the JSR231 version of JOGL.
 *
 * Revision 1.2  2004/06/02 15:47:04  dennis
 * Removed unused imports.
 *
 * Revision 1.1  2004/05/28 20:51:14  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 * 
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;

import javax.media.opengl.*;

/** 
 *  This interface is the interface that classes must implement in order to
 *  be "rendered" in a ThreeD_GL_Panel object.  The action taken to render
 *  the object may be different for different types of objects, such as
 *  Shapes, Lights, Transforms and Groups.
 */

public interface IThreeD_GL_Object
{
  /**
   *  Render this object using the specified drawable.
   *
   *  @param  drawable  The drawable on which the object is to be rendered.
   */
  void Render( GLAutoDrawable drawable );


  /**
   *  Delete the display list and reset the list_id to invalid.  This
   *  will force the display list to be regenerated if this node is
   *  rendered again.  This method should be called when this shape is
   *  no longer needed.  Although the finalize method will free the
   *  cause the display list to be deleted, the garbage collector may
   *  not run for quite some time, and the OpenGL display list would
   *  not be freed until the garbage collector runs.  To reduce the
   *  number of stale display lists maintained by OpenGL, call this 
   *  method, as soon as the shape is no longer needed.
   */
  void clearList();

}
