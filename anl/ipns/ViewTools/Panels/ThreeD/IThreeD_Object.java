/*
 * File:  IThreeD_Object.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.5  2002/11/27 23:12:52  pfpeterson
 * standardized header
 *
 * Revision 1.4  2002/10/31 23:14:37  dennis
 * Clarified documentation for depth() method.
 *
 * Revision 1.3  2002/10/29 23:46:49  dennis
 * Added method position() to get the average position of the
 * vertices for the object.
 *
 */


package DataSetTools.components.ThreeD;

import java.awt.*;
import DataSetTools.components.image.*;
import DataSetTools.math.*;

/** 
 *  This interface is the interface that classes must implement in order to
 *  be drawn by in a ThreeD_JPanel object. 
 */

public interface IThreeD_Object
{

public static final int INVALID_PICK_ID = -1;

  /**
   *  Set the color of this object.
   *
   *  @param  color  Specifies the color to be used when drawing this object.
   */
  public void setColor( Color color );


  /**
   *  Get the vector that is at the average position of the vertices
   *  of this object.
   *
   *  @return  The average of the vertices for this object.  If there are
   *           no vertices, this returns null.
   */
  public Vector3D position();


  /**
   *  Project this object onto pixel coordinates using the given
   *  3D viewing/projection transform and the 2D transform that maps the
   *  virtual viewing screen to the window.
   *
   *  @param  projection   The viewing/projection matrix that will project
   *                       the 3D coordinates of the object onto a 2D virtual
   *                       viewing screen with 2D coordinates centered around
   *                       (0,0).
   *
   *  @param  window_tran  The 2D transform that maps the virtual viewing
   *                       screen to the pixel coordinates for a window.
   *
   *  @param  front_clip   The distance from the virtual viewing screen 
   *                       (at the VRP) to the front clipping plane.  
   */
  public void Project( Tran3D         projection, 
                       CoordTransform window_tran,
                       float          front_clip   );


  /**
   *  Transform this object in 3D using the specified transformation.  The
   *  basic data representing the object will be altered.
   *
   *  @param  projection   The transformation to be applied to the 3D 
   *                       coordinates of the object.
   */
  public void Transform( Tran3D transform );


  /**
   *  Draw this object using the graphics context g.
   *
   *  @param  g   The graphics object into which the object is to be drawn.
   */
  public void Draw( Graphics g );


  /**
   *  Return the depth of the object in the 3D scene.  This is needed since
   *  a ThreeD_JPanel uses the "painters algorithm" for hidden surface removal.
   *  That is, the objects are sorted based on depth and those objects that
   *  are furthest away are drawn first.  This algorithm will not work for
   *  arbitrary scenes, but will work for simple rectangular approximations 
   *  to detector elements.  NOTE: The depth value is on a relative scale with
   *  the depth value increasing as the point gets closer to the observer.
   *
   *  @return  Returns a depth value that can be used to sort the objects
   *           based on their distance from the observer. 
   */
  public float depth( );


  /**
   *  Calculate a nominal distance in the plane from the specified pixel to
   *  the projection of this object.  The nominal distance should be suitable
   *  for picking relatively small objects.
   *
   *  @param   pix_x  X coordinate of point in plane
   *  @param   pix_y  Y coordinate of point in plane
   *
   *  @return  Returns a nominal distance in the plane from the projection of
   *           the object to the specified pixel.
   */
  public float distance_to( float pix_x, float pix_y );


  /**
   *  Set ID to be returned if this object is picked.  The ID is set to 
   *  INVALID_PICK_ID by default, which indicates that the object is not 
   *  pickable.
   *
   *  @param  pick_id   The ID to use for this object when picking objects.
   *                    Set this ID to INVALID_PICK_ID to make the object 
   *                    not pickable.
   */
  public void setPickID( int pick_id );


  /**
   *  Get the pick ID of this object. 
   *
   *  @return  Returns the pick ID for this object.
   */
  public int getPickID( );

}
