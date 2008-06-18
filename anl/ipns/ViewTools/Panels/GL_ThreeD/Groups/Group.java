/*
 * File:  Group.java
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
 * Revision 1.2  2007/08/13 23:50:17  dennis
 * Switched from old JOGL to the JSR231 version of JOGL.
 *
 * Revision 1.1  2004/07/16 14:51:00  dennis
 * Class to group collections of 3D objects.
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Groups;

import java.util.*;
import javax.media.opengl.*;
import gov.anl.ipns.ViewTools.Panels.GL_ThreeD.*;

public class Group implements IThreeD_GL_Object 
{
  private Vector objects; 

  public Group()
  {
    objects = new Vector();
  }

  public void addChild( IThreeD_GL_Object child )
  {
    if ( child != null )
      objects.addElement( child );
  }

  public void addChild( int index, IThreeD_GL_Object child )
  {
    if ( child != null && index >= 0 && index <= objects.size() )
      objects.add( index, child );
  }

  public void removeChild( IThreeD_GL_Object child )
  {
    if ( child != null )
      objects.remove( child );
  }

  public void removeChild( int index )
  {
    if ( index >= 0 && index < objects.size() )
      objects.remove( index );
  }

  public void Clear()
  {
    objects.clear();
  }

  public int numChildren()
  {
    return objects.size();
  }  

  public IThreeD_GL_Object getChild( int index )
  {
    if ( index < 0 || index >= objects.size() )
      return null;
    else
      return (IThreeD_GL_Object)objects.elementAt( index );
  }


  /* ---------------------------- clearList ------------------------- */
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
  public void clearList()
  {
    for ( int i = 0; i < objects.size(); i++ )
      ((IThreeD_GL_Object)objects.elementAt(i)).clearList();
  }


  /**
   *  Render this object using the specified drawable.
   *
   *  @param  drawable  The drawable on which the object is to be rendered.
   */
  public void Render( GLAutoDrawable drawable )
  {
    for ( int i = 0; i < objects.size(); i++ )
      ((IThreeD_GL_Object)objects.elementAt(i)).Render( drawable );
  }

}
