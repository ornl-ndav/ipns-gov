/*
 * File:   DetectorPosition.java 
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 *  $Log$
 *  Revision 1.12  2003/06/23 15:49:02  dennis
 *  Increased number of decimal places displayed in toString()
 *  method.
 *
 *  Revision 1.11  2003/02/05 21:19:38  dennis
 *  Added constructor to construct a DetectorPosition object from a
 *  Vector3D object.
 *
 *  Revision 1.10  2002/11/27 23:15:47  pfpeterson
 *  standardized header
 *
 *  Revision 1.9  2002/08/01 22:39:02  dennis
 *  Set Java's serialVersionUID = 1.
 *  Set the local object's IsawSerialVersion = 1 for our
 *  own version handling.
 *  Added readObject() method to handle reading of different
 *  versions of serialized object.
 *
 *  Revision 1.8  2002/07/08 20:14:18  dennis
 *  toString() method now uses greek characters from DataSetTools.util.FontUtil.
 *
 */
package  DataSetTools.math;

import java.io.*;
import java.text.*;
import DataSetTools.util.*;

/**
 * DetectorPosition represents the position of a neutron detector relative to
 * the sample, with a specific orientation.  The incident beam comes from the
 * negative X axis.  This class is derived from the Position3D class and adds
 * a method to get the scattering angle.
 */

public class DetectorPosition extends    Position3D 
                              implements Serializable
{
  // NOTE: any field that is static or transient is NOT serialized.
  //
  // CHANGE THE "serialVersionUID" IF THE SERIALIZATION IS INCOMPATIBLE WITH
  // PREVIOUS VERSIONS, IN WAYS THAT CAN NOT BE FIXED BY THE readObject()
  // METHOD.  SEE "IsawSerialVersion" COMMENTS BELOW.  CHANGING THIS CAUSES
  // JAVA TO REFUSE TO READ DIFFERENT VERSIONS.
  //
  public  static final long serialVersionUID = 1L;


  // NOTE: The following fields are serialized.  If new fields are added that
  //       are not static, reasonable default values should be assigned in the
  //       readObject() method for compatibility with old servers, until the
  //       servers can be updated.

  private int IsawSerialVersion = 1;         // CHANGE THIS WHEN ADDING OR
                                             // REMOVING FIELDS, IF
                                             // readObject() CAN FIX ANY
                                             // COMPATIBILITY PROBLEMS

  /**
   *  Construct a default position at the origin.
   */
  public DetectorPosition()
  {
    super();
  }
 
  /**
   *  Construct a DetectorPosition object from a generic Position3D object
   *
   *  @param position  The Position3D object that provides the position
   *                   information for this new DetectorPosition object. 
   */
  public DetectorPosition( Position3D position )
  {
    super( position ); 
  }


  /**
   *  Construct a Position3D object from a Vector3D object.
   * 
   *  @param  vector The vector that supplies the x,y,z coordinates for this
   *                 new Position3D object.
   */
  public DetectorPosition( Vector3D vector )
  {
    super( vector );
  }


  /**
   *  Calculate the angle (in radians) between vector from the origin to 
   *  this detector position and the positive x-axis.
   *  
   *  @return  the scattering angle in radians.
   */
  public float getScatteringAngle()
  {
    float x, y, z, dist_from_x;
    float cartesian_coords[] = getCartesianCoords();

    x = cartesian_coords[0];
    y = cartesian_coords[1];
    z = cartesian_coords[2];
    dist_from_x = (float)Math.sqrt( y*y + z*z ); 

    return (float)Math.atan2( dist_from_x, x ); 
  }


  /**
   *  Calculate a weighted "average" of a list of detector positions.  The
   *  "average" is obtained by calculating the average of the components
   *  of the position in spherical coordinates.  Any points for which a 
   *  weight is not given will be given weight 0.  Null entries in the 
   *  points[] array are ignored.
   *
   *  @param  points   Array of Detector3D objects whose weighted average is
   *                   calculated
   *
   *  @param  weights  Array of floats giving the weights to be used for the
   *                   detectors.  The weights should be determined by the
   *                   effective solid angles and efficiencies.
   *
   *  @return  A DetectorPosition  containing the weighted average of 
   *           the specified positions, or null if the weights and/or
   *           points are not valid.
   */

  public static DetectorPosition getAveragePosition( DetectorPosition points[],
                                                     float            weights[])
  {
    int n_points = points.length;

    if ( n_points > weights.length )
      n_points = weights.length;

    if ( n_points == 0 )
    {
      System.out.println("ERROR: no points or weights specified in" +
                         " DetectorPosition.getAveragePosition");
      return null;
    }

    float sum_r     = 0;
    float sum_theta = 0;
    float sum_phi   = 0;
    float sum_w     = 0;
    float coords[] = null;
    for ( int i = 0; i < n_points; i++ )
      if ( points[i] != null )
      {
        coords = points[i].getSphericalCoords();
        sum_r     += coords[0] * weights[i];
        sum_theta += coords[1] * weights[i];
        sum_phi   += coords[2] * weights[i];
        sum_w     += weights[i];
      }

    if ( sum_w == 0 )
    {
      System.out.println("ERROR: sum of weights is zero in " +
                         "DetectorPosition.getAveragePosition() " );
      return null;
    }

    float ave_r     = sum_r / sum_w;
    float ave_theta = sum_theta / sum_w;
    float ave_phi   = sum_phi / sum_w;

    DetectorPosition ave_pos = new DetectorPosition();
    ave_pos.setSphericalCoords( ave_r, ave_theta, ave_phi );

    return ave_pos;
  }   


  /**
   *  Form a string giving the scattering angle followed by the position
   *  of the detector in cylindrical coordinates.
   */
  public String toString()
  {
     float cyl_coords[] = getCylindricalCoords();

     NumberFormat f = NumberFormat.getInstance();
     f.setMaximumFractionDigits( 3 );
     String scat_ang = f.format( getScatteringAngle() * 180.0/Math.PI );

     f.setMaximumFractionDigits( 4 );
     String r     = f.format( cyl_coords[0] );
     f.setMaximumFractionDigits( 3 );
     String cyl_angle = f.format( cyl_coords[1] * 180.0/Math.PI );
     f.setMaximumFractionDigits( 4 );
     String z     = f.format( cyl_coords[2] );
     String string = "2" + FontUtil.THETA + "=" + scat_ang +
                     ":r="  + r +
                     "," + FontUtil.PHI + "=" + cyl_angle +
                     ",z=" + z;
     return string;
  }

  static public void main( String[] args )
  {
    DetectorPosition point = new DetectorPosition();
    float[]    coords;

    point.setSphericalCoords( -10, (float)Math.PI/6, (float)Math.PI/4 );
    point.PrintPoint( "******Spherical coord point: -10, PI/6, PI/4" );
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );

    point.setCartesianCoords( 1, 1, 1 );
    point.PrintPoint( "******Cartesian Coord point: 1, 1, 1" );
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );

    point.setCylindricalCoords( 100, (float)Math.PI/6, 50 );
    point.PrintPoint( "******Cylindrical Coord point: 100, PI/6, 50" );
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );

    point.setCartesianCoords( 1, 1, 0 );
    point.PrintPoint( "******Cartesian Coord point: 1, 1, 0" );
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );
    
    point.setCartesianCoords( 1, 0, 1 );
    point.PrintPoint( "******Cartesian Coord point: 1, 0, 1" );
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );

    point.setCartesianCoords( -1, 0, 0 );
    point.PrintPoint( "******Cartesian Coord point: -1, 0, 0" );
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );

    point.setCartesianCoords( 1, 0, 0 );
    point.PrintPoint( "******Cartesian Coord point: 1, 0, 0" );
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );

    point.setCylindricalCoords( 4, (float)Math.PI, 0.01f );
    point.PrintPoint( "******Cylindrical Coord point: 4, PI, 0.01");
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );

    point.setCylindricalCoords( 4, 0, 0.01f );
    point.PrintPoint( "******Cylindrical Coord point: 4, 0, 0.01"); 
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );
  }

/* -----------------------------------------------------------------------
 *
 *  PRIVATE METHODS
 *
 */

/* ---------------------------- readObject ------------------------------- */
/**
 *  The readObject method is called when objects are read from a serialized
 *  ojbect stream, such as a file or network stream.  The non-transient and
 *  non-static fields that are common to the serialized class and the
 *  current class are read by the defaultReadObject() method.  The current
 *  readObject() method MUST include code to fill out any transient fields
 *  and new fields that are required in the current version but are not
 *  present in the serialized version being read.
 */

  private void readObject( ObjectInputStream s ) throws IOException,
                                                        ClassNotFoundException
  {
    s.defaultReadObject();               // read basic information

    if ( IsawSerialVersion != 1 )
      System.out.println("Warning:DetectorPosition IsawSerialVersion != 1");
  }


}
