/*
 * @(#) DetectorPosition.java 
 *
 *  Programmer: Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.4  2001/01/29 21:05:32  dennis
 *  Now uses CVS revision numbers.
 *
 *  Revision 1.3  2000/07/18 18:15:49  dennis
 *  Added toString() method
 *
 *  Revision 1.2  2000/07/10 22:25:12  dennis
 *  July 10, 2000 version... many changes
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
 *  
 * @version 1.0  
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
