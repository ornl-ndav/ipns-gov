/*
 * File:  Texture1D.java
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
 * Revision 1.2  2007/08/13 23:50:19  dennis
 * Switched from old JOGL to the JSR231 version of JOGL.
 *
 * Revision 1.1  2004/06/18 19:22:13  dennis
 * Moved to Textures package
 *
 * Revision 1.1  2004/05/28 20:51:17  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Textures;

import javax.media.opengl.*;
import java.nio.*;

public class Texture1D extends Texture
{
  private int n_cols = 0;
  private float wrap_s = GL.GL_REPEAT;

  public Texture1D( byte rgb_array[], int n_cols )
  {
    setImage( rgb_array, n_cols );
    rebuild_texture = true;
  }

  /**
   * Set the texture image that will be used for this texture.
   *
   * @param rgb_array  Array of rgb byte triples that list the colors for
   *                   the texture.
   * @param n_texels   The number of rgb byte triples to use to form the
   *                   texture.
   */
  public void setImage( byte rgb_array[], int n_texels )
  {
    if ( rgb_array == null )
    {
       System.out.println("ERROR: null rgb_array in Texture2D.setImage ");
       return;
    }
    if ( rgb_array.length < 3 * n_texels )
    {
       System.out.println("ERROR: not enough colors in Texture2D.setImage "+
                           rgb_array.length );
       return;
    }
    this.n_cols = n_texels;

    super.setImage( rgb_array, n_cols );
    rebuild_texture = true;
  }

  public void activate( GL gl )
  {                                      // always do the things that get 
                                         // stored in the display list
    gl.glEnable( GL.GL_TEXTURE_1D );
    int tex_name = getTexture_name( gl );
    gl.glBindTexture( GL.GL_TEXTURE_1D, tex_name );
    gl.glTexEnvf( GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, getMode() );

    if ( rebuild_texture && getImage() != null )     // do the things that are
    {                                                // stored in the current
                                                     // 2D texture object
      gl.glPixelStorei( GL.GL_UNPACK_ALIGNMENT, 1 );
      gl.glTexImage1D( GL.GL_TEXTURE_1D, 0, 3,
                       n_cols, 
                       0, 
                       GL.GL_RGB,
                       GL.GL_UNSIGNED_BYTE,
                       ByteBuffer.wrap(getImage()) );

      gl.glTexParameterf( GL.GL_TEXTURE_1D, GL.GL_TEXTURE_WRAP_S, wrap_s );
      gl.glTexParameterf( GL.GL_TEXTURE_1D, GL.GL_TEXTURE_MAG_FILTER, 
                          getFilter() );
      gl.glTexParameterf( GL.GL_TEXTURE_1D, GL.GL_TEXTURE_MIN_FILTER, 
                          getFilter() );
      rebuild_texture = false;
    }
  }

  /**
   *
   */
  public void deactivate( GL gl )
  {
     gl.glBindTexture( GL.GL_TEXTURE_1D, 0 );
     gl.glDisable( GL.GL_TEXTURE_1D );
  }

  /**
   * @param wrap_code 
   */
  public void setWrap_s( float wrap_code )
  {
    wrap_s = wrap_code;
    rebuild_texture = true;
  }

  /**
   * @return The current wrap mode for the first texture parameter, s,
   *         such as GL.GL_REPEAT
   */
  public float getWrap_s()
  {
    return wrap_s;
  }
}
