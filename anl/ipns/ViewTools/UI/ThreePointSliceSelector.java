/*
 * File:  ThreePointSliceSelector.java
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
 * Revision 1.3  2004/03/12 00:42:53  serumb
 * Changed package and imports.
 *
 * Revision 1.2  2004/01/26 20:43:59  dennis
 * Added constructor that takes a specified initial plane.
 * Simplified default constructor.
 *
 * Revision 1.1  2004/01/26 18:19:01  dennis
 * Initial version of class to select a slice plane by
 * specifying three points on the plane.
 *
 */

package gov.anl.ipns.ViewTools.UI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import gov.anl.ipns.MathTools.Geometry.*;
//import gov.anl.ipns.Util.*;

/**
 *  This class selects a plane by specifying three points on the plane.
 */

public class ThreePointSliceSelector extends     ActiveJPanel
                                     implements  ISlicePlaneSelector,
                                                 Serializable
{
  private Vector3D_UI  origin_selector;
  private Vector3D_UI  p1_selector;
  private Vector3D_UI  p2_selector;


  /* ------------------------- default constructor ---------------------- */
  /**
   *  Construct the ThreePointSliceSelector with a default current slice plane 
   */
  public ThreePointSliceSelector()
  {
    setLayout( new GridLayout( 3, 1 ) );

    origin_selector = new Vector3D_UI( "Center " );
    p1_selector = new Vector3D_UI( "Right  " );
    p2_selector = new Vector3D_UI( "Top    " );

    add( origin_selector ); 
    add( p1_selector ); 
    add( p2_selector ); 

    PointListener point_listener = new PointListener();
    origin_selector.addActionListener( point_listener );
    p1_selector.addActionListener( point_listener );
    p2_selector.addActionListener( point_listener );

    setPlane( new SlicePlane3D() );
  }


  /* ---------------- constructor with specified plane ------------------- */
  /**
   *  Construct the CenterNormalPointSliceSelector with the specified
   *  slice plane.
   *
   *  @param plane  The plane to initially display in this selector.
   */ 
  public ThreePointSliceSelector( SlicePlane3D plane )
  {
    this();
    setPlane( plane );
  }


  /* --------------------------- setPlane ----------------------------- */
  /**
   *  Set the plane whose parameters are to be displayed by this plane selector.
   *  The points are set as point1 = origin + u and point2 = origin + v
   *  
   *  @param new_plane  The plane to display in this selector.
   */
  public void setPlane( SlicePlane3D new_plane )
  {
    if ( new_plane == null )
    {
      System.out.println("ERROR: new_plane is null in " +
                                "ThreePointSliceSelector.setPlane");
      return;
    }

    Vector3D origin = new_plane.getOrigin();
    origin_selector.setVector( origin );

    Vector3D p1 = new Vector3D( origin );
    p1.add( new_plane.getU() );
    p1_selector.setVector( p1 );
     
    Vector3D p2 = new Vector3D( origin );
    p2.add( new_plane.getV() );
    p2_selector.setVector( p2 );
  }


  /* --------------------------- getPlane ----------------------------- */
  /**
   *  Get a plane determined by the three specified points.  If the three
   *  points don't determine a plane, null is returned.
   *
   *  @return a copy of the specified plane or null if the points don't 
   *          determine a plane.
   */
  public SlicePlane3D getPlane()
  {
    SlicePlane3D plane = new SlicePlane3D();
    if ( plane.setPlane( origin_selector.getVector(),
                         p1_selector.getVector(),
                         p2_selector.getVector()  ) )
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
