/*
 * File:  GL_Shape.java
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
 * Revision 1.1  2004/05/28 20:51:12  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;

import java.awt.*;
import net.java.games.jogl.*;
import gov.anl.ipns.MathTools.Geometry.*;

/** 
 *  This abstract base class contains the basic methods for dealing with
 *  color, transparency, texture mapping and picking that are used by
 *  particular objects drawn in a ThreeD_GL_Panel using OpenGL.
 */

abstract public class GL_Shape implements IThreeD_GL_Object
{
  public static final int INVALID_PICK_ID = -1;
  public static final int INVALID_LIST_ID = -1;

                                               // ID for selection of object
  protected int pick_id = INVALID_PICK_ID;  

  protected boolean rebuild_list = true;       // Flag indicating whether any
                                               // changes were made that require
                                               // a rebuild of the display list.

  private int list_id = INVALID_LIST_ID;   
                                               // ID for the GL display list
                                               // for this entire object.

  private float material[] = null;             // Color and transparency info
                                               // for this object.
  private Tran3D transform = null;

  private Texture  texture = null;

  /**
   *  Set the transform for this object
   */
  public void setTransform( Tran3D trans )
  {
    transform = new Tran3D( trans );
    rebuild_list = true;
  }


  public void setTexture( Texture tex )
  {
    texture = tex;
    rebuild_list = true;
  }


  public boolean isTextured()
  {
    if ( texture == null )
      return false;
    else
      return true;
  }


  public int TextureDimension()
  {
    if ( texture == null )
      return 0;
    else if ( texture instanceof Texture2D )
      return 2;
 
    return 0;
  }


  /**
   *  Set the color of this object.
   *
   *  @param  color  Specifies the color to be used when drawing this object.
   *                 The values stored in the color array are the red, green,
   *                 blue and "alpha" values, respectively.  Each of these
   *                 values must be in the interval [0,1].  Values outside 
   *                 of the interval [0,1] will be "clamped" to [0,1].
   *                 Up to 4 values will be used from the array color[].
   *                 A null, or length zero color array is ignored.
   */
  public void setColor( float color[] )
  {
     if ( color == null || color.length <= 0 )    // reset to no material
     {                                            // specified
       material = null;
       return;
     }

     if ( material == null )                      // if no material previously
       initMaterial();                            // specified, start with 
                                                  // "white"
     int n_vals = 4;
     if ( color.length < n_vals )
       n_vals = color.length;

     float val;
     for ( int i = 0; i < n_vals; i++ )
     {
       val = color[i];
       if ( val > 1 )
         val = 1;
       else if ( val < 0 )
         val = 0;
       material[i] = val;
     }

     rebuild_list = true; // set flag so that the display list will be rebuilt
  }


  /**
   *  
   */
  public void setColor( Color color )
  {}


  /**
   *  Set the transparency of this object.
   *
   *  @param  alpha  Specifies the alpha value for blending
   *                  when drawing this object.
   */
  public void setTransparency( float alpha )
  {
     if ( material == null )                      // if no material previously
       initMaterial();                            // specified, start with 
                                                  // "white"
     if ( alpha > 1 )
       alpha = 1;
     else if ( alpha < 0 )
       alpha = 0;

     material[3] = alpha;

     rebuild_list = true; // set flag so that the display list will be rebuilt
  }


  /**
   *  Draw this object using the specified drawable.
   *
   *  @param  drawable  The drawable into which the object is to be drawn.
   */
  public void Render( GLDrawable drawable )
  {
     GL gl = drawable.getGL();

     if ( texture != null )
       if ( texture.mustRebuild() )            // if this returns true
         texture.activate( gl );               // first call to activate will
                                               // alter the texture object in GL

     if ( rebuild_list )
     {
       if ( list_id == INVALID_LIST_ID )       // make the list_id once
         list_id = gl.glGenLists(1);

       gl.glNewList(list_id, GL.GL_COMPILE);

       if ( texture != null )                  // display list will have the
         texture.activate( gl );               // calls to bind texture object

       if ( transform != null )                // push matrix stack and 
       {                                       // multiply by the transform
          float m[][] = transform.get();
          float vals[] = new float[16];
          int i = 0;
          for ( int col = 0; col < 4; col++ )
            for ( int row = 0; row < 4; row++ )
            {
              vals[i] = m[row][col];
              i++;
            }
          gl.glPushMatrix();
          gl.glMultMatrixf( vals );
       }

       if ( material != null )                    // only use material if set
       {
         gl.glMaterialfv( GL.GL_FRONT_AND_BACK,
                          GL.GL_AMBIENT_AND_DIFFUSE,
                          material );
         if ( material[3] < 1 )                   // if alpha != 1, do blending
           gl.glEnable( GL.GL_BLEND );
       }

       if ( pick_id != INVALID_PICK_ID )
       {
         gl.glPushName( pick_id );
         Draw( drawable );
         gl.glPopName();
       }
       else
         Draw( drawable );

       if ( material != null && material[3] < 1 )
         gl.glDisable( GL.GL_BLEND );

       if ( transform != null )
         gl.glPopMatrix();

       if ( texture != null )
         texture.deactivate( gl );

       gl.glEndList();
       rebuild_list = false;
     }

     gl.glCallList( list_id );
  }


  abstract protected void Draw( GLDrawable drawable );


  /**
   *  Set ID to be returned if this object is picked.  The ID is set to 
   *  INVALID_PICK_ID by default, which indicates that the object is not 
   *  pickable.
   *
   *  @param  pick_id   The ID to use for this object when picking objects.
   *                    Set this ID to INVALID_PICK_ID to make the object 
   *                    not pickable.
   */
  public void setPickID( int pick_id )
  {
    this.pick_id = pick_id;
  }


  /**
   *  Get the pick ID of this object. 
   *
   *  @return  Returns the pick ID for this object.
   */
  public int getPickID( )
  { 
    return pick_id; 
  }
  

  protected void finalize()
  {                                           // free our display list if it
                                              // was allocated.
    if ( list_id != INVALID_LIST_ID )
      ThreeD_GL_Panel.ChangeOldLists( list_id );
  }


  /*
   *  Initialize material to white, by default.
   */
  private void initMaterial()
  {
     material = new float[4];      
     for ( int i = 0; i < 4; i++ )
       material[i] = 1.0f;
  }


}
