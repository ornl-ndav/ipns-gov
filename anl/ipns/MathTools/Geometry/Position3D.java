/*
 * @(#) Position3D.java     1.0  98/08/05  Dennis Mikkelson
 *
 */
package  DataSetTools.math;

import java.io.*;

/**
 * Position3D represents a position in 3D space in cartesian, cylindrical
 * and spherical coordinate systems.  Methods are provided to get & set the
 * position in all three of these coordinate systems.  All angle parameters
 * are specified and returned in radians. 
 *  
 * @version 1.0  
 */

public class Position3D implements Serializable
{
  private  float  sph_radius;     // distance from the origin to the point

  private  float  azimuth_angle;  // angle between +X axis and projection of
                                  // the line from origin to point onto the
                                  // XY plane.  This angle is in radians.

  private  float  polar_angle;    // angle between +Z axis and line from the 
                                  // origin to the point.  This angle is in
                                  // radians. 

  public Position3D()
  {
    sph_radius    = 0;
    azimuth_angle = 0;
    polar_angle   = 0;
  }

  /**
   *  Copy constructor
   */
  public Position3D( Position3D position )
  {
    sph_radius    = position.sph_radius;
    azimuth_angle = position.azimuth_angle;
    polar_angle   = position.polar_angle;
  }

  /**
   *  Specify the position as a triple of values, (x, y, z) in Cartesian
   *  coordinates.
   */
  public void setCartesianCoords( float x, float y, float z )
  {
    this.sph_radius  = (float)Math.sqrt( x*x + y*y + z*z );
    float cyl_radius = (float)Math.sqrt( x*x + y*y );
    this.polar_angle = (float)Math.atan2( cyl_radius, z );
    this.azimuth_angle = (float)Math.atan2( y, x );
  }

  /**
   *  Get the position as a triple of values, (x, y, z) in Cartesian
   *  coordinates.
   */
  public float[] getCartesianCoords()
  {
    float[] coords = new float[3];

    float r = sph_radius * (float)Math.sin( polar_angle );
    coords[0] = r * (float)Math.cos( azimuth_angle );
    coords[1] = r * (float)Math.sin( azimuth_angle );
    coords[2] = sph_radius * (float)Math.cos( polar_angle );
    return coords;
  }
   
  /**
   *  Specify the position as a triple of values, (r, theata, phi) in spherical 
   *  polar coordinates.
   */
  public void setSphericalCoords( float sph_radius, 
                                  float azimuth_angle, 
                                  float polar_angle    )
  {
    this.sph_radius    = sph_radius;
    this.azimuth_angle = azimuth_angle;
    this.polar_angle   = polar_angle;
  }

  /**
   *  Get the position as a triple of values, (r, theata, phi) in spherical
   *  polar coordinates.
   */
  public float[] getSphericalCoords()
  {
    float[] coords = new float[3];
    coords[0] = sph_radius;
    coords[1] = azimuth_angle;
    coords[2] = polar_angle;
    return coords;
  }

  /**
   *  Get the distance of the position from the origin.
   */
  public float getDistance()
  {
    return sph_radius;
  }

  /**
   *  Specify the position as a triple of values, (r, theata, z) in cylindrical 
   *  coordinates.
   */
  public void setCylindricalCoords( float cyl_radius, 
                                    float azimuth_angle, 
                                    float z              )
  {
    this.sph_radius    = (float)Math.sqrt( cyl_radius*cyl_radius + z*z );
    this.azimuth_angle = azimuth_angle;
    this.polar_angle   = (float)Math.atan2( cyl_radius, z );
  }

  /**
   *  Get the position as a triple of values, (r, theata, z) in cylindrical 
   *  coordinates.
   */
  public float[] getCylindricalCoords()
  {
    float[] coords = new float[3];
    coords[0] = sph_radius * (float)Math.sin( polar_angle );
    coords[1] = azimuth_angle;
    coords[2] = sph_radius * (float)Math.cos( polar_angle );
    return coords;
  }

  /**
   *  Print the position in Cartesian, Cylindrical and Sphereical coords
   *  for debugging purposes.
   */
  public void PrintPoint( String title )
  {
    float[] coords;
    System.out.println( title );

    System.out.println( "In Cartesian coords: " );
    coords = getCartesianCoords();
    System.out.println( "[ "+coords[0]+", "+coords[1]+", "+coords[2]+" ]" );
    
    System.out.println( "In Cylindrical coords: " );
    coords = getCylindricalCoords();
    System.out.println( "[ "+coords[0]+", "+coords[1]+", "+coords[2]+" ]" );
    
    System.out.println( "In Spherical coords: " );
    coords = getSphericalCoords();
    System.out.println( "[ "+coords[0]+", "+coords[1]+", "+coords[2]+" ]" );
  }

  /**
   *  Make a new Position3D object that contains the same data as the current
   *  one.
   */
  public Object clone()
  {
    Position3D position = new Position3D( this );

    return position;
  }

  /**
   *  Some basic tests of the Position3D object.
   */
  static public void main( String[] args )
  {
    Position3D point = new Position3D();
    float[]    coords;

    point.setSphericalCoords( -10, (float)Math.PI/6, (float)Math.PI/4 );
    point.PrintPoint( "******Spherical coord point: -10, PI/6, PI/4" );

    point.setCartesianCoords( 1, 1, 1 );
    point.PrintPoint( "******Cartesian Coord point: 1, 1, 1" );

    point.setCylindricalCoords( 100, (float)Math.PI/6, 50 );
    point.PrintPoint( "******Cylindrical Coord point: 100, PI/6, 50" );

    Position3D new_point = (Position3D)point.clone();
    point.PrintPoint( "******Cloned point:" );
  }

}
