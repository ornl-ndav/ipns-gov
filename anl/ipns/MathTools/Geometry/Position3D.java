/*
 * File:   Position3D.java
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
 *  Revision 1.15  2004/03/19 17:24:26  dennis
 *  Removed unused variables
 *
 *  Revision 1.14  2004/03/11 22:52:54  dennis
 *  Changed to gov.anl.ipns.MathTools.Geometry package
 *
 *  Revision 1.13  2003/02/05 21:06:43  dennis
 *  Added constructor to make a Position3D object from a Vector3D object.
 *
 *  Revision 1.12  2002/12/05 20:41:34  pfpeterson
 *  Added method to determine distance between two Position3D.
 *
 *  Revision 1.11  2002/11/27 23:15:47  pfpeterson
 *  standardized header
 *
 *  Revision 1.10  2002/08/01 22:39:02  dennis
 *  Set Java's serialVersionUID = 1.
 *  Set the local object's IsawSerialVersion = 1 for our
 *  own version handling.
 *  Added readObject() method to handle reading of different
 *  versions of serialized object.
 *
 *  Revision 1.9  2002/07/08 15:43:03  pfpeterson
 *  Explicitly defined equals() method.
 *
 *  Revision 1.8  2002/06/14 21:24:51  rmikk
 *  Implements IXmlIO interface
 *
 */
package  gov.anl.ipns.MathTools.Geometry;

import java.io.*;
import java.text.*;
import  gov.anl.ipns.Util.File.*;

/**
 * Position3D represents a position in 3D space in cartesian, cylindrical
 * and spherical coordinate systems.  Methods are provided to get & set the
 * position in all three of these coordinate systems.  All angle parameters
 * are specified and returned in radians. 
 */

public class Position3D implements Serializable,
                                   IXmlIO
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
   *  Construct a Position3D object from a Vector3D object.
   */
  public Position3D( Vector3D vector )
  {
    if ( vector == null )                                 // nothing to do
      return;

    float coords[] = vector.get();
    setCartesianCoords( coords[0], coords[1], coords[2] );
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
   * Determine the distance between this and the given Position3D
   */
  public float distance(Position3D pos){
    float[] mycoords  = this.getCartesianCoords();
    float[] hiscoords = pos.getCartesianCoords();
    double diff;
    double distance=0.;

    for( int i=0 ; i<3 ; i++ ){
      diff=(double)(mycoords[i]-hiscoords[i]);
      distance=distance+diff*diff;
    }

    return (float)Math.sqrt(distance);
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
                                            float      weights[] )
  {
    int n_points = points.length;

    if ( n_points > weights.length )
      n_points = weights.length;

    if ( n_points == 0 )
    {
      System.out.println("ERROR: no points or weights specified in" +
                         " Position3D.getCenterOfMass");
      return null;
    }
 
    float sum_x = 0;
    float sum_y = 0;
    float sum_z = 0;
    float sum_w = 0;
    float coords[] = null;
    for ( int i = 0; i < n_points; i++ )
      if ( points[i] != null )
      {
        coords = points[i].getCartesianCoords();
        sum_x += coords[0] * weights[i];
        sum_y += coords[1] * weights[i];
        sum_z += coords[2] * weights[i]; 
        sum_w += weights[i]; 
      }

    if ( sum_w == 0 )
    {
      System.out.println("ERROR: sum of weights is zero in " + 
                         "Position3D.getCenterOfMass " );
      return null;
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

 
 /** Implements the IXmlIO interface so a DetectoPositon can write itself
  *
  * @param stream  the OutputStream to which the data is written
  * @param mode    either IXmlIO.BASE64 or IXmlOP.NORMAL. This indicates 
  *                how a Data's xvals, yvals and errors are written
  * @return true if successful otherwise false<P>
  *
  * NOTE: This routine writes all of the begin and end tags.  These tag names
  *       are NOT TabulatedData but either HistogramTable or FunctionTable
  */
  public boolean XMLwrite( OutputStream stream, int mode )
  { String S="<DetectorPosition>\n";
    S +="<sph_radius>"+ sph_radius+"</sph_radius>\n";
    S += "<azimuth_angle>"+ azimuth_angle +"</azimuth_angle>\n";
    S += "<polar_angle>"+   polar_angle+"</polar_angle>\n";
    try
    {
      stream.write(S.getBytes());
      stream.write("</DetectorPosition>\n".getBytes());
      return true;
    }
    catch( Exception s)
    { return xml_utils.setError("IO Exc="+s.getMessage());
    }
  }

 /** Implements the IXmlIO interface so a Detector Position can read itself
  *
  * @param stream  the InStream to which the data is written

  * @return true if successful otherwise false<P>
  *
  * NOTE: This routine assumes the begin tag has been completely read.  It reads
  *       the end tag.  The tag names 
  *       are NOT TabulatedData but either HistogramTable or FunctionTable
  */
  public boolean XMLread( InputStream stream )
  { 
    try
    {
      String Tag = xml_utils.getTag(stream);
      if( Tag == null)
        return xml_utils.setError( xml_utils.getErrorMessage());
      if( !Tag.equals("sph_radius"))
        return xml_utils.setError("AWrong tag order in Pos3D"+Tag);
      String vString = xml_utils.getValue(stream);
      if(vString == null)
        return xml_utils.setError(xml_utils.getErrorMessage());
      this.sph_radius =(new Float(vString)).floatValue();

      Tag = xml_utils.getTag(stream);
      if( Tag == null)
        return xml_utils.setError( xml_utils.getErrorMessage());
      if( !Tag.equals("azimuth_angle"))
        return xml_utils.setError("BWrong tag order in Pos3D"+Tag);
      vString = xml_utils.getValue(stream);
      if(vString == null)
        return xml_utils.setError(xml_utils.getErrorMessage());
      this.azimuth_angle =(new Float(vString)).floatValue();

      Tag = xml_utils.getTag(stream);
      if( Tag == null)
        return xml_utils.setError( xml_utils.getErrorMessage());
      if( !Tag.equals("polar_angle"))
        return xml_utils.setError("CWrong tag order in Pos3D"+Tag);
      vString = xml_utils.getValue(stream);
      if(vString == null)
        return xml_utils.setError(xml_utils.getErrorMessage());
      this.polar_angle =(new Float(vString)).floatValue();

      Tag = xml_utils.getTag(stream);
      if( Tag == null)
        return xml_utils.setError( xml_utils.getErrorMessage());
      if( !Tag.equals("/DetectorPosition"))
        return xml_utils.setError("No End tag in Pos3D");
      return true;
    }
    catch(Exception s)
    { return xml_utils.setError( "Err="+s.getMessage());
    }
  }

    public boolean equals(Object obj){
        if(!(obj instanceof Position3D))return false;

        if(this.sph_radius    != ((Position3D)obj).sph_radius)return false;
        if(this.azimuth_angle != ((Position3D)obj).azimuth_angle)return false;
        if(this.polar_angle   != ((Position3D)obj).polar_angle)return false;
        return true;
    }

  /**
   *  Some basic tests of the Position3D object.
   */
  static public void main( String[] args )
  {
    Position3D point = new Position3D();

    point.setSphericalCoords( -10, (float)Math.PI/6, (float)Math.PI/4 );
    point.PrintPoint( "******Spherical coord point: -10, PI/6, PI/4" );

    point.setCartesianCoords( 1, 1, 1 );
    point.PrintPoint( "******Cartesian Coord point: 1, 1, 1" );

    point.setCylindricalCoords( 100, (float)Math.PI/6, 50 );
    point.PrintPoint( "******Cylindrical Coord point: 100, PI/6, 50" );

    Position3D new_point = (Position3D)point.clone();
    new_point.PrintPoint( "******Cloned point:" );

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
      System.out.println("Warning:Position3D IsawSerialVersion != 1");
  }

}
