/*
 * @(#) DetectorPosition.java     1.0  99/06/10  Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.2  2000/07/10 22:25:12  dennis
 *  July 10, 2000 version... many changes
 *
 *  Revision 1.4  2000/06/12 14:51:08  dennis
 *  *** empty log message ***
 *
 *  Revision 1.3  2000/05/11 16:08:13  dennis
 *  Added RCS logging
 *
 *
 */
package  DataSetTools.math;

import java.io.*;

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
