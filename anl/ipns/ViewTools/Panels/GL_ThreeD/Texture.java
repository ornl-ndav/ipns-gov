/*
 * File:  Texture.java
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
 * Revision 1.1  2004/05/28 20:51:16  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;

import net.java.games.jogl.*;

abstract public class Texture 
{
//  static GL my_gl = null;

  public static int DEFAULT_TEXTURE_NAME = 0;
  private int texture_name = DEFAULT_TEXTURE_NAME;

  private  byte image[] = null;
  private  float filter = GL.GL_LINEAR;
  private  float mode   = GL.GL_MODULATE;

  protected boolean rebuild_texture = true;


  public boolean mustRebuild()
  {
    return rebuild_texture;
  }


  /**
   *
   */
  abstract public void activate( GL gl );

  /**
   *
   */
  abstract public void deactivate( GL gl );


  /**
   * Set the texture image that will be used for this texture.
   *
   * @param rgb_array  Array of rgb byte triples that list the colors for
   *                   the texture. 
   * @param n_texels   The number of rgb byte triples to use to form the
   *                   texture.  
   */
  public void setImage( byte[] rgb_array, int n_texels )
  {
    if ( rgb_array.length < 3 * n_texels )
    {
      System.out.println("ERROR not enough texels in rgb_array " + 
                         rgb_array.length + " not " + 3*n_texels );
      return;
    }

    if ( image == null || image.length != rgb_array.length )
      image = new byte[ 3 * n_texels ];

    System.arraycopy( rgb_array, 0, image, 0, 3 * n_texels );
    rebuild_texture = true;
  }

  /**
   * @return A reference to the array of bytes used for this texture
   */
  public byte[] getImage()
  {
    return image;
  }


  /**
   * @return The integer code for the OpenGL texture object,
   *         as obtained from glGenTextures.
   */
  public int getTexture_name( GL gl )
  {
    if ( texture_name == DEFAULT_TEXTURE_NAME )
    {
      int list[] = new int[1];
      gl.glGenTextures( 1, list );
      texture_name = list[0];
//      my_gl = gl;
    }
    return texture_name;
  }

  /**
   * @param filter_code
   */
  public void setFilter( float filter_code )
  {
    filter = filter_code;
    rebuild_texture = true;
  }

  /**
   * @return The code for the current texture filter, such as
   *         GL.GL_LINEAR
   */
  public float getFilter() 
  {
    return filter;
  }

  /**
   * @param mode_code 
   */
  public void setMode( float mode_code )
  {
    mode = mode_code;
    rebuild_texture = true;
  }

  /**
   * @return The code for the current texture mode, such as
   *         GL.GL_MODULATE
   */
  public float getMode() 
  {
    return mode;
  }

/*
  protected void finalize()
  {
    if ( my_gl != null )
    {
      int list[] = new int[0];
      list[0] = texture_name;
      my_gl.glDeleteTextures( 1, list );
    }
  }
*/

}
