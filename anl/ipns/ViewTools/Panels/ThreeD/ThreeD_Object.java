/*
 * File:  ThreeD_Object.java
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.5  2002/10/31 23:14:37  dennis
 * Clarified documentation for depth() method.
 *
 * Revision 1.4  2002/10/29 23:45:11  dennis
 * Added method position() to get the average position of the
 * vertices for the object.
 *
 * Revision 1.3  2002/10/29 22:12:48  dennis
 * Improved error checking in constructor.  Now allows construction
 * with an initially empty list of vertices.
 *
 * Revision 1.2  2001/05/23 17:31:48  dennis
 * Added clipping using front clipping plane between COP and VRP
 *
 * Revision 1.1  2001/05/08 21:05:45  dennis
 * Abstract base class for Polygons, Polylines, etc., that can be
 * drawn by a ThreeD_JPanel.
 *
 *
 */

package DataSetTools.components.ThreeD;

import java.awt.*;
import java.io.*;
import DataSetTools.math.*;
import DataSetTools.components.image.*;

/**
 *  Abstract base class for basic 3D objects such as Polygons, Polylines and
 *  Polymarkers.
 */
abstract public class ThreeD_Object implements IThreeD_Object
{
  protected  Color     color      = Color.red;
  protected  Vector3D  vertices[] = null;
  protected  float     depth      = 0;
  protected  float     x[]        = null;
  protected  float     y[]        = null;
  protected  int       pick_id    = INVALID_PICK_ID;
  protected  boolean   clipped    = false;

  /** 
   *  Construct a 3D object using the specified list of vertices and the
   *  given color.
   *
   *  @param  verts     List of vertices that determine this 3D object.
   *  @param  color     The color to be used for this object.
   */
  public ThreeD_Object( Vector3D verts[], Color color )
  {
    this.color = color;

    if ( verts != null )
    {
      vertices = new Vector3D[ verts.length ];
      for ( int i = 0; i < verts.length; i++ )
        vertices[i] = new Vector3D( verts[i] );

      x = new float [ verts.length ];
      y = new float [ verts.length ];
    }
  }

  /**
   *  Get the vector that is at the average position of the vertices 
   *  of this object.
   *
   *  @return  The average of the vertices for this object.  If there are
   *           no vertices, this returns null.
   */
  public Vector3D position()
  {
    if ( vertices == null || vertices.length <= 0 )
      return null;

    Vector3D average = new Vector3D( vertices[0] );
    if ( vertices.length == 1 )
      return average;
    else
    {
      for ( int i = 1; i < vertices.length; i++ )
        average.add( vertices[i] );

      average.multiply( 1.0f/vertices.length );
      return average;
    }
  }

  /**
   *  Set the color of this object.
   *
   *  @param  color  Specifies the color to be used when drawing this object.
   */
  public void setColor( Color color )
  {
    this.color = color;
  }


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
                       float          front_clip )
  {
    if ( vertices == null || vertices.length == 0 )
    {
      System.out.println( "Error: no vertices in ThreeD_Object.Project()" );
      return;
    }

    if ( projection == null || window_tran == null )
    {
      System.out.println( "Error: no transform in ThreeD_Object.Project()" );
      return;
    }
    
    Vector3D point = new Vector3D();
    float    coords[];
    float    sum = 0;
    clipped = false;

    for ( int i = 0; i < vertices.length; i++ )        // project each vertex
    { 
      projection.apply_to( vertices[i], point );
      if ( point.get()[2] >= front_clip )
        clipped = true;
      point.standardize();

      coords = point.get();
      sum += coords[2];                                // to calculate the
                                                       // average depth in scene

      x[i] = coords[0]; 
      y[i] = coords[1]; 
    }
    depth = sum / vertices.length;

    CoordBounds bounding_box = new CoordBounds();
    bounding_box.setBounds( x, y );
    if ( bounding_box.intersect( window_tran.getSource() ) == null )
      clipped = true;

    window_tran.MapTo( x, y );
  }


  /**
   *  Transform this object in 3D using the specified transformation.  The
   *  basic data representing the object will be altered.
   *
   *  @param  projection   The transformation to be applied to the 3D 
   *                       coordinates of the object.
   */
  public void Transform( Tran3D transform )
  {
    if ( transform != null )
      transform.apply_to( vertices, vertices );
  }


  /**
   *  Draw this object using the graphics context g.
   *
   *  @param  g   The graphics object into which the object is to be drawn.
   */
  abstract public void Draw( Graphics g );


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
  public float depth( )
  {
    return depth;
  }


  /**
   *  Calculate a nominal distance in the plane from the specified pixel to 
   *  the projection of this object.  The nominal distance should be suitable 
   *  for picking relatively small objects. 
   *
   *  @param   pix_x  X coordinate of point in plane
   *  @param   pix_y  Y coordinate of point in plane
   *
   *  @return  Returns a nominal distance in the plane from the projection of
   *           the object to the specified pixel.  If the object is not
   *           pickable, or is clipped this returns Float.MAX_VALUE.  
   */
  public float distance_to( float pix_x, float pix_y )
  {
    if ( clipped || (pick_id <= INVALID_PICK_ID ) || x == null )
      return Float.MAX_VALUE;

    float sum_x = 0;
    float sum_y = 0;
    for ( int i = 0; i < x.length; i++ )
    {
      sum_x += x[i];
      sum_y += y[i];
    }

    return Math.abs( sum_x/x.length - pix_x ) + 
           Math.abs( sum_y/y.length - pix_y );
  }


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

}
