/*
 * @(#) Position3D.java
 *
 *  Programmer: Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.4  2001/01/29 21:05:47  dennis
 *  Now uses CVS revision numbers.
 *
 *  Revision 1.3  2000/07/18 18:15:49  dennis
 *  Added toString() method
 *
 *  Revision 1.2  2000/07/10 22:25:14  dennis
 *  July 10, 2000 version... many changes
 *
 *  Revision 1.4  2000/06/12 14:53:55  dennis
 *  Added method getCenterOfMass() to calculate a weighted sum of position
 *  objects.
 *
 *  Revision 1.3  2000/05/11 16:08:13  dennis
 *  Added RCS logging
 *
 */
package  DataSetTools.math;

import java.io.*;
import java.text.*;

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
   *
   *  @return  Returns the x, y, z values for the point in positions 0, 1 and 2
   *           respectively, of an array of floats.
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
   *
   *  @return  Returns the values of the radius, azimuth angle and polar angle
   *           in positions 0, 1 and 2 respectively, of an array of floats.
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
   *
   *  @return  Returns the values of the radius, azimuth angle and z value in 
   *           positions 0, 1 and 2 respectively, of an array of floats.
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
   *  Calculate the weighted average of a list of 3D points, given a list of
   *  weights for the points.  Any points for which a weight is not given 
   *  will be given weight 0.  
   *
   *  @param  points   Array of Position3D objects whose weighted average is
   *                   calculated
   *
   *  @param  weights  Array of floats giving the weights of the points.
   *
   *  @return  A point containing the weighted average of the specified points.
   */
 
  public static Position3D getCenterOfMass( Position3D points[],
                                            float       weights[] )
  {
    int n_points = points.length;

    if ( n_points > weights.length )
      n_points = weights.length;

    if ( n_points == 0 )
    {
      System.out.println("ERROR: no points or weights specified in" +
                         " Postion3D.getCenterOfMass");
      return null;
    }
 
    float sum_x = 0;
    float sum_y = 0;
    float sum_z = 0;
    float sum_w = 0;
    float coords[] = null;
    for ( int i = 0; i < n_points; i++ )
    {
      coords = points[i].getCartesianCoords();
      sum_x += coords[0] * weights[i];
      sum_y += coords[1] * weights[i];
      sum_z += coords[2] * weights[i]; 
      sum_w += weights[i]; 
    }

    Position3D center = new Position3D();
    center.setCartesianCoords( sum_x / sum_w, sum_y / sum_w, sum_z / sum_w ); 
    return center; 
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
   *  Form a string giving the position of the detector in cylindrical 
   *  coordinates.
   */
  public String toString()
  {
     float cyl_coords[] = getCylindricalCoords();

     NumberFormat f = NumberFormat.getInstance();

     f.setMaximumFractionDigits( 3 );
     String r     = f.format( cyl_coords[0] );
     f.setMaximumFractionDigits( 2 );
     String cyl_angle = f.format( cyl_coords[1] * 180.0/Math.PI );
     f.setMaximumFractionDigits( 3 );
     String z     = f.format( cyl_coords[2] );
                                                    // upper case phi:   \u03a6
                                                    // lower case phi:   \u03c6
     String string = "r="  + r +
                     ","+"\u03c6" +"=" + cyl_angle +
                     ",z=" + z;
     return string;
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

    Position3D points[]  = new Position3D[2];
    float      weights[] = new float[2];

    points[0] = new Position3D();
    points[0].setCartesianCoords( 0, 1, 2 );
    points[1] = new Position3D();
    points[1].setCartesianCoords( 10, 11, 12 );
    weights[0] = 5;
    weights[1] = 10;

    Position3D center = Position3D.getCenterOfMass( points, weights );
    center.PrintPoint("Center = "); 

  }

}
