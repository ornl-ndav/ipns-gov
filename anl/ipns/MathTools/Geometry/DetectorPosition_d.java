/*
 * File:   DetectorPosition_d.java 
 *
 * Copyright (C) 2003, Dennis Mikkelson
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
 *  $Log$
 *  Revision 1.3  2004/03/19 17:24:26  dennis
 *  Removed unused variables
 *
 *  Revision 1.2  2004/03/11 23:47:44  dennis
 *  Moved to package MathTools.Geometry.
 *  Copied constants for Greek phi & theta used in toString() from
 *  FontUtil, to avoid dependence on ViewTools
 *
 *  Revision 1.1  2003/07/14 22:23:00  dennis
 *  Double precision version, ported from original
 *  single precision version.
 *
 *
 */
package  gov.anl.ipns.MathTools.Geometry;

import java.io.*;
import java.text.*;

/**
 * DetectorPosition_d represents the position of a neutron detector relative to
 * the sample, with a specific orientation, in double precision.  The 
 * incident beam comes from the negative X axis.  This class is derived from 
 * the Position3D_d class and adds a method to get the scattering angle.
 */

public class DetectorPosition_d extends    Position3D_d
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
  public DetectorPosition_d()
  {
    super();
  }
 
  /**
   *  Construct a DetectorPosition_d object from a generic Position3D_d object
   *
   *  @param position  The Position3D_d object that provides the position
   *                   information for this new DetectorPosition_d object. 
   */
  public DetectorPosition_d( Position3D_d position )
  {
    super( position ); 
  }


  /**
   *  Construct a Position3D_d object from a Vector3D_d object.
   * 
   *  @param  vector The vector that supplies the x,y,z coordinates for this
   *                 new Position3D_d object.
   */
  public DetectorPosition_d( Vector3D_d vector )
  {
    super( vector );
  }


  /**
   *  Calculate the angle (in radians) between vector from the origin to 
   *  this detector position and the positive x-axis.
   *  
   *  @return  the scattering angle in radians.
   */
  public double getScatteringAngle()
  {
    double x, y, z, dist_from_x;
    double cartesian_coords[] = getCartesianCoords();

    x = cartesian_coords[0];
    y = cartesian_coords[1];
    z = cartesian_coords[2];
    dist_from_x = Math.sqrt( y*y + z*z ); 

    return Math.atan2( dist_from_x, x ); 
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
   *  @param  weights  Array of doubles giving the weights to be used for the
   *                   detectors.  The weights should be determined by the
   *                   effective solid angles and efficiencies.
   *
   *  @return  A DetectorPosition_d  containing the weighted average of 
   *           the specified positions, or null if the weights and/or
   *           points are not valid.
   */

  public static DetectorPosition_d getAveragePosition( 
                                             DetectorPosition_d points[],
                                             double            weights[] )
  {
    int n_points = points.length;

    if ( n_points > weights.length )
      n_points = weights.length;

    if ( n_points == 0 )
    {
      System.out.println("ERROR: no points or weights specified in" +
                         " DetectorPosition_d.getAveragePosition");
      return null;
    }

    double sum_r     = 0;
    double sum_theta = 0;
    double sum_phi   = 0;
    double sum_w     = 0;
    double coords[] = null;
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
                         "DetectorPosition_d.getAveragePosition() " );
      return null;
    }

    double ave_r     = sum_r / sum_w;
    double ave_theta = sum_theta / sum_w;
    double ave_phi   = sum_phi / sum_w;

    DetectorPosition_d ave_pos = new DetectorPosition_d();
    ave_pos.setSphericalCoords( ave_r, ave_theta, ave_phi );

    return ave_pos;
  }   


  /**
   *  Form a string giving the scattering angle followed by the position
   *  of the detector in cylindrical coordinates.
   */
  public String toString()
  {
     String PHI   = "\u03c6";
     String THETA = "\u03b8";

     double cyl_coords[] = getCylindricalCoords();

     NumberFormat f = NumberFormat.getInstance();
     f.setMaximumFractionDigits( 3 );
     String scat_ang = f.format( getScatteringAngle() * 180.0/Math.PI );

     f.setMaximumFractionDigits( 4 );
     String r     = f.format( cyl_coords[0] );
     f.setMaximumFractionDigits( 3 );
     String cyl_angle = f.format( cyl_coords[1] * 180.0/Math.PI );
     f.setMaximumFractionDigits( 4 );
     String z     = f.format( cyl_coords[2] );
     String string = "2" + THETA + "=" + scat_ang +
                     ":r="  + r +
                     "," + PHI + "=" + cyl_angle +
                     ",z=" + z;
     return string;
  }

  static public void main( String[] args )
  {
    DetectorPosition_d point = new DetectorPosition_d();

    point.setSphericalCoords( -10, Math.PI/6, Math.PI/4 );
    point.PrintPoint( "******Spherical coord point: -10, PI/6, PI/4" );
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );

    point.setCartesianCoords( 1, 1, 1 );
    point.PrintPoint( "******Cartesian Coord point: 1, 1, 1" );
    System.out.println( "Scattering angle = "+ point.getScatteringAngle() );

    point.setCylindricalCoords( 100, Math.PI/6, 50 );
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

    point.setCylindricalCoords( 4, Math.PI, 0.01 );
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
      System.out.println("Warning:DetectorPosition_d IsawSerialVersion != 1");
  }


}
