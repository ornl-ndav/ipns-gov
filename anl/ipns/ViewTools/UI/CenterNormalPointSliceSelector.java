/*
 * File:  CenterNormalPointSliceSelector.java
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
 * Revision 1.3  2004/03/15 23:53:58  dennis
 * Removed unused imports, after factoring out the View components,
 * Math and other utils.
 *
 * Revision 1.2  2004/03/12 00:00:13  serumb
 * Changed package and imports.
 *
 * Revision 1.1  2004/01/26 20:49:03  dennis
 * Initial version of class that selects a plane based on
 * a "center" point on the plane, a second point on the
 * plane that determines the direction of a local coordinate
 * system on the plane, and a normal vector for the plane.
 *
 */

package gov.anl.ipns.ViewTools.UI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import gov.anl.ipns.MathTools.Geometry.*;

/**
 *  This class selects a plane by specifying three points on the plane.
 */

public class CenterNormalPointSliceSelector extends     ActiveJPanel
                                           implements  ISlicePlaneSelector,
                                                 Serializable
{
  private Vector3D_UI  origin_selector;
  private Vector3D_UI  normal_selector;
  private Vector3D_UI  point_selector;

  /* ------------------------- default constructor ---------------------- */
  /**
   *  Construct the CenterNormalPointSliceSelector with a default current 
   *  slice plane.
   */
  public CenterNormalPointSliceSelector()
  {
    setLayout( new GridLayout( 3, 1 ) );

    origin_selector = new Vector3D_UI( "Center " );
    normal_selector = new Vector3D_UI( "Normal " );
    point_selector  = new Vector3D_UI( "Point  " );

    add( origin_selector ); 
    add( normal_selector ); 
    add( point_selector ); 

    PointListener point_listener = new PointListener();
    origin_selector.addActionListener( point_listener );
    normal_selector.addActionListener( point_listener );
    point_selector.addActionListener( point_listener );

    setPlane( new SlicePlane3D() );
  }


  /* ---------------- constructor with specified plane ------------------- */
  /**
   *  Construct the CenterNormalPointSliceSelector with the specified
   *  slice plane.
   *
   *  @param plane  The plane to initially display in this selector.
   */
  public CenterNormalPointSliceSelector( SlicePlane3D plane )
  {
    this();
    setPlane( plane );
  }


  /* --------------------------- setPlane ----------------------------- */
  /**
   *  Set the plane whose parameters are to be displayed by this plane selector.
   *  
   *  @param new_plane  The plane to display in this selector.
   */
  public void setPlane( SlicePlane3D new_plane )
  {
    if ( new_plane == null )
    {
      System.out.println("ERROR: new_plane is null in " +
                                "CenterNormalPointSliceSelector.setPlane");
      return;
    }

    Vector3D origin = new_plane.getOrigin();
    Vector3D point = new_plane.getU(); 
    point.add( origin );

    origin_selector.setVector( origin );
    normal_selector.setVector( new_plane.getNormal() );
    point_selector.setVector( point );
  }


  /* --------------------------- getPlane ----------------------------- */
  /**
   *  Get a plane determined by the current origin point and normal vector.
   *  If the plane is not properly specified, eg. the normal vector is zero,
   *  then null is returned. 
   *
   *  @return a copy of the specified plane or null if the normal vector 
   *          has zero length.
   */
  public SlicePlane3D getPlane()
  {
    SlicePlane3D plane = new SlicePlane3D();

    Vector3D origin = origin_selector.getVector();
    Vector3D point  = point_selector.getVector();
    Vector3D normal = normal_selector.getVector();
 
    plane.setOrigin( origin );
    point.subtract( origin ); 
    if ( plane.setNormal_and_U( normal, point ) )
      return plane;
    else
      return null;
  }

  /* -----------------------------------------------------------------------
   *
   * Private Classes
   *
   */

  /* ----------------------- PointListener ----------------------------- */
  /*
   *  Listener for changes to the points determining the plane. 
   */ 
  private class PointListener implements ActionListener,
                                         Serializable
  {
     public void actionPerformed( ActionEvent e )
     {
       send_message( PLANE_CHANGED );
     }
  }

}
