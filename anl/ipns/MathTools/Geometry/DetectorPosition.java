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
 *  $Log$
 *  Revision 1.7  2001/08/16 02:38:32  dennis
 *  getAveragePosition() calculates average position by averaging the
 *  components of the positions in spherical coordinates.  It also
 *  now checks for and ignores null positions in the points[] array.
 *
 *  Revision 1.6  2001/07/25 18:04:00  dennis
 *  Added method to calculate an average detector position.
 *
 *  Revision 1.5  2001/04/25 20:56:27  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.4  2001/01/29 21:05:32  dennis
 *  Now uses CVS revision numbers.
 *
 *  Revision 1.3  2000/07/18 18:15:49  dennis
 *  Added toString() method
 *
 *  Revision 1.2  2000/07/10 22:25:12  dennis
 *  Now Using CVS 
 *
 */
package  DataSetTools.math;

import java.io.*;
import java.text.*;

/**
 * DetectorPosition represents the position of a neutron detector relative to
 * the sample, with a specific orientation.  The incident beam comes from the
 * negative X axis.  This class is derived from the Position3D class and adds
 * a method to get the scattering angle.
 */

public class DetectorPosition extends    Position3D 
                              implements Serializable
{
  public DetectorPosition()
  {
    super();
  }
 
  /**
   *  Construct a DetectorPosition object from a generic Position3D object
   */
  public DetectorPosition( Position3D position )
  {
    super( position ); 
  }

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
     f.setMaximumFractionDigits( 2 );
     String scat_ang = f.format( getScatteringAngle() * 180.0/Math.PI );

     f.setMaximumFractionDigits( 3 );
     String r     = f.format( cyl_coords[0] );
     f.setMaximumFractionDigits( 2 );
     String cyl_angle = f.format( cyl_coords[1] * 180.0/Math.PI );
     f.setMaximumFractionDigits( 3 );
     String z     = f.format( cyl_coords[2] );
                                                    // upper case theta: \u0398
                                                    // lower case theta: \u03b8
                                                    // upper case phi:   \u03a6
                                                    // lower case phi:   \u03c6
     String string = "2\u03b8" +"=" + scat_ang +
                     ":r="  + r +
                     ","+"\u03c6" +"=" + cyl_angle +
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

}
