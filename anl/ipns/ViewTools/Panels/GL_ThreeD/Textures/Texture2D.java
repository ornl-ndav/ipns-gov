/*
 * File:  Texture2D.java
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
 * Revision 1.1  2004/06/18 19:22:14  dennis
 * Moved to Textures package
 *
 * Revision 1.1  2004/05/28 20:51:17  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Textures;

import net.java.games.jogl.*;

public class Texture2D extends Texture
{
  private int n_rows = 0;
  private int n_cols = 0;
  private float wrap_s = GL.GL_REPEAT;
  private float wrap_t = GL.GL_REPEAT;

  public Texture2D( byte rgb_array[], int n_rows, int n_cols )
  {
    setImage( rgb_array, n_rows, n_cols );
    rebuild_texture = true;
  }


  /**
   * Set the texture image that will be used for this texture.
   *
   * @param rgb_array  Array of rgb byte triples that list the colors for
   *                   the texture.  There must be at least 
   *                   3 * n_rows * n_cols bytes in this array
   * @param n_rows     The number of rows into which the array of texels
   *                   should be split.
   * @param n_cols     The number of cols into which the array of texels
   *                   should be split.
   */
  public void setImage( byte rgb_array[], int n_rows, int n_cols )
  {
    if ( rgb_array == null )
    {
       System.out.println("ERROR: null rgb_array in Texture2D.setImage ");
       return;
    }
    if ( rgb_array.length < 3 * n_rows * n_cols )
    {
       System.out.println("ERROR: not enough colors in Texture2D.setImage "+
                           rgb_array.length );
       return;
    }
    this.n_rows = n_rows;
    this.n_cols = n_cols;

    super.setImage( rgb_array, n_rows * n_cols );
    rebuild_texture = true;
  }


  public void activate( GL gl )
  {                                      // always do the things that get 
                                         // stored in the display list
    gl.glEnable( GL.GL_TEXTURE_2D );
    int tex_name = getTexture_name( gl );
    gl.glBindTexture( GL.GL_TEXTURE_2D, tex_name );
    gl.glTexEnvf( GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, getMode() );

    if ( rebuild_texture && getImage() != null )     // do the things that are
    {                                                // stored in the current
                                                     // 2D texture object
      gl.glPixelStorei( GL.GL_UNPACK_ALIGNMENT, 1 );
      gl.glTexImage2D( GL.GL_TEXTURE_2D, 0, 3,
                       n_cols, n_rows,
                       0, GL.GL_RGB,
                       GL.GL_UNSIGNED_BYTE,
                       getImage() );

      gl.glTexParameterf( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrap_s );
      gl.glTexParameterf( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrap_t );
      gl.glTexParameterf( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, 
                          getFilter() );
      gl.glTexParameterf( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, 
                          getFilter() );
      rebuild_texture = false;
    }
  }


  /**
   *
   */
  public void deactivate( GL gl )
  {
     gl.glBindTexture( GL.GL_TEXTURE_2D, 0 );
     gl.glDisable( GL.GL_TEXTURE_2D );
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

  /**
   * @param wrap_code 
   */
  public void setWrap_t( float wrap_code )
  {
    wrap_t = wrap_code;
    rebuild_texture = true;
  }

  /**
   * @return The current wrap mode for the second texture parameter, t,
   *         such as GL.GL_REPEAT
   */
  public float getWrap_t()
  {
    return wrap_t;
  }

}
