/*
 * File:  EulerAngles.java 
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
 *  $Log$
 *  Revision 1.1  2004/03/12 00:21:36  dennis
 *  Extracted EulerAngle calculations from tof_calc.java in ISAW
 *
 */

package gov.anl.ipns.MathTools.Geometry;

import gov.anl.ipns.MathTools.*;

/**
 *  This class has static methods for computing and using Euler Angles
 */
public final class EulerAngles 
{

/* ----------------------- private constructor --------------------- */ 
  /**
   * Don't let anyone instantiate this class.
   */
  private EulerAngles() {}


/* ------------------------ getEulerAngles --------------------------- */
/**
 *  Return an array containing a set of "Euler angles" representing a
 *  rotation that takes the orthonormal basis vectors i,j,k into another 
 *  set of orthonormal basis vectors u,v,n.   Only the basis vectors u,v 
 *  are taken as arguments since the "n" vector is calculated internally 
 *  as u X v.  "v" is then recalculated as n X u, in case v was not originally
 *  perpendicular to "u".  All vectors will be normalized internally.
 *  The angles of rotation are to be applied as follows:
 *
 *  1. Rotate about the positive z-axis by phi
 *  2. Rotate about the positive x-axis by chi
 *  3. Rotate about the positive z-axis by omega
 *
 *  NOTE: All rotations follow a right hand rule and the coordinate system is
 *  assumed to be right handed.  Rotation angles are returned in degrees.
 *
 *  @param u      Vector giving the direction that the basis vector "i" 
 *                maps to.
 *  @param v      Vector giving the direction that the basis  vector "j"
 *                maps to.  In principle, this should be perpendicular to u, 
 *                it at least must not be collinear with u.
 *
 *  @return The Euler angles are returned in an array euler[], with
 *          euler[0]=phi, euler[1]=chi, euler[2]=omega.  If u==0, v==0 or
 *          u and v are collinear, this just returns an array of zeros.
 */

public static double[] getEulerAngles( Vector3D_d u, Vector3D_d v )
{
  double euler[] = {0,0,0};
                                        // first make sure we have valid u,v,n
  if ( u.length() == 0 || v.length() == 0 )
  {
    System.out.println("Error: zero length u or v in getEulerAngles()" );
    System.out.println("u = " + u );
    System.out.println("v = " + v );
    return euler;
  }

  u.normalize();
  v.normalize();

  Vector3D_d n = new Vector3D_d();
  n.cross( u, v );
  if ( n.length() == 0 )
  {
    System.out.println("Error: u and v are collinear in getEulerAngles()" );
    System.out.println("u = " + u );
    System.out.println("v = " + v );
    return euler;
  }

  n.normalize();
  v.cross( n, u );
                          // Now that there are valid u,v,n find the Euler 
                          // angles.  A special case  occurs if the "n" vector 
                          // is collinear with "k". 

  double one = 1 - 1.0E-15;    // We'll consider anything within 1E-15 of 1 to
                               // be 1, to deal with special cases chi == 0
                               // and chi == 180 degrees.

  if ( n.get()[2] >= one )     // chi rotation is 0, just rotate about z-axis  
  {
    euler[0] = Math.atan2( u.get()[1], u.get()[0] );        // phi
    euler[1] = 0;                                           // chi
    euler[2] = 0;                                           // omega 
  }
  else if ( n.get()[2] <= -one ) // chi rotation is 180 degrees 
  {
    euler[0] = -Math.atan2( u.get()[1], u.get()[0] );       // phi
    euler[1] = Math.PI;                                     // chi
    euler[2] = 0;                                           // omega 
  }
  else
  {
    euler[0] = Math.atan2( u.get()[2], v.get()[2] );        // phi
    euler[1] = Math.acos( n.get()[2] );                     // chi
    euler[2] = Math.atan2( n.get()[0], -n.get()[1] );       // omega 
  }

  for ( int i = 0; i < euler.length; i++ )              // convert to degrees
    euler[i] *= 180.0 / Math.PI;

  return euler;
}


/* ------------------------ getEulerAngles --------------------------- */
/**
 *  Return an array containing a set of "Euler angles" representing a
 *  rotation that takes the orthonormal basis vectors i,j,k into another
 *  set of orthonormal basis vectors u,v,n.   Only the basis vectors u,v
 *  are taken as arguments since the "n" vector is calculated internally
 *  as u X v.  "v" is then recalculated as n X u, in case v was not originally
 *  perpendicular to "u".  All vectors will be normalized internally.
 *  The angles of rotation are to be applied as follows:
 *
 *  1. Rotate about the positive z-axis by phi
 *  2. Rotate about the positive x-axis by chi
 *  3. Rotate about the positive z-axis by omega
 *
 *  NOTE: All rotations follow a right hand rule and the coordinate system is
 *  assumed to be right handed.  Rotation angles are returned in degrees.
 *
 *  @param u      Vector giving the direction that the basis vector "i"
 *                maps to.
 *  @param v      Vector giving the direction that the basis  vector "j"
 *                maps to.  In principle, this should be perpendicular to u,
 *                it at least must not be collinear with u.
 *
 *  @return The Euler angles are returned in an array euler[], with
 *          euler[0]=phi, euler[1]=chi, euler[2]=omega.  If u==0, v==0 or
 *          u and v are collinear, this just returns an array of zeros.
 */
public static float[] getEulerAngles( Vector3D u, Vector3D v )
{
  Vector3D_d u_double = new Vector3D_d( u.get()[0], u.get()[1], u.get()[2] );
  Vector3D_d v_double = new Vector3D_d( v.get()[0], v.get()[1], v.get()[2] );
  double euler[] = getEulerAngles( u_double, v_double );

  float float_euler[] = new float[ euler.length ];
  for ( int i = 0; i < euler.length; i++ )
    float_euler[i] = (float)euler[i];

  return float_euler;
}


/* ------------------------ makeEulerRotation ------------------------ */
/**
 *  Make the cumulative rotation matrix representing rotation by Euler angles
 *  phi, chi and omega.  This produces a matrix representing the following 
 *  sequence of rotations, applied in the order listed:
 *
 *  1. Rotate about the positive z-axis by phi 
 *  2. Rotate about the positive x-axis by chi 
 *  3. Rotate about the positive z-axis by omega
 *
 *  NOTE: All rotations follow a right hand rule and the coordinate system is
 *  assumed to be right handed.  Rotation angles are specified in degrees.
 *
 *  @param phi    Angle to rotate about the +z-axis
 *  @param chi    Angle to rotate (the rotated system) about the +x-axis
 *  @param omega  Angle to rotate (the rotated system) about the +z-axis
 *  
 *  @return The cumulative rotation matrix omegaRz * chiRx * phiRz, 
 *          which carries out these rotations, starting with rotation by
 *          phi about the z-axis.
 */
public static Tran3D_d makeEulerRotation( double phi, double chi, double omega )
{
   Tran3D_d omegaR = new Tran3D_d();
   Tran3D_d phiR   = new Tran3D_d();
   Tran3D_d chiR   = new Tran3D_d();

   Vector3D_d i_vec = new Vector3D_d( 1, 0, 0 );
   Vector3D_d k_vec = new Vector3D_d( 0, 0, 1 );

   phiR.setRotation( phi, k_vec );
   chiR.setRotation( chi, i_vec );
   omegaR.setRotation( omega, k_vec );  

   // build the matrix product (omegaR * chiR * phiR).  When applied to a
   // vector v as in  (omegaR * chiR * phiR)v, this has the effect of doing
   // the rotation phiR first!.

   omegaR.multiply_by( chiR );
   omegaR.multiply_by( phiR );
   return omegaR;
}


/* ------------------------ makeEulerRotation ------------------------ */
/**
 *  Make the cumulative rotation matrix representing rotation by Euler angles
 *  chi, phi and omega.  This produces a matrix representing the following
 *  sequence of rotations, applied in the order listed:
 *
 *  1. Rotate about the positive z-axis by phi
 *  2. Rotate about the positive x-axis by chi
 *  3. Rotate about the positive z-axis by omega
 *
 *  NOTE: All rotations follow a right hand rule and the coordinate system is
 *  assumed to be right handed.  Rotation angles are specified in degrees.
 *
 *  @param phi    Angle to rotate about the +z-axis
 *  @param chi    Angle to rotate (the rotated system) about the +x-axis
 *  @param omega  Angle to rotate (the rotated system) about the +z-axis
 *
 *  @return The cumulative rotation matrix omegaRz * chiRx * phiRz,
 *          which carries out these rotations, starting with rotation by
 *          phi about the z-axis.
 */
public static Tran3D makeEulerRotation( float phi, float chi, float omega )
{
   Tran3D omegaR = new Tran3D();
   Tran3D phiR   = new Tran3D();
   Tran3D chiR   = new Tran3D();

   Vector3D i_vec = new Vector3D( 1, 0, 0 );
   Vector3D k_vec = new Vector3D( 0, 0, 1 );

   phiR.setRotation( phi, k_vec );
   chiR.setRotation( chi, i_vec );
   omegaR.setRotation( omega, k_vec );

   // build the matrix product (omegaR * chiR * phiR).  When applied to a
   // vector v as in  (omegaR * chiR * phiR)v, this has the effect of doing
   // the rotation phiR first!.

   omegaR.multiply_by( chiR );
   omegaR.multiply_by( phiR );
   return omegaR;
}


/* ----------------------- makeEulerRotationMatrix --------------------- */
/**
 *  Make the double precision rotation matrix representing rotation by Euler
 *  angles chi, phi and omega.  This produces a matrix representing the
 *  following sequence of rotations, applied in the order listed:
 *
 *  1. Rotate about the positive z-axis by phi
 *  2. Rotate about the positive x-axis by chi
 *  3. Rotate about the positive z-axis by omega
 *
 *  NOTE: All rotations follow a right hand rule and the coordinate system is
 *  assumed to be right handed.  Rotation angles are specified in degrees.
 *
 *  @param phi    Angle to rotate about the +z-axis
 *  @param chi    Angle to rotate (the rotated system) about the +x-axis
 *  @param omega  Angle to rotate (the rotated system) about the +z-axis
 *
 *  @return The cumulative rotation matrix omegaRz * chiRx * phiRz,
 *          which carries out these rotations, starting with rotation by
 *          phi about the z-axis.
 */
public static double[][] makeEulerRotationMatrix( double phi,
                                                  double chi,
                                                  double omega )
{
   phi   *= Math.PI/180;
   chi   *= Math.PI/180;
   omega *= Math.PI/180;

   double sin_phi = Math.sin( phi );
   double cos_phi = Math.cos( phi );

   double sin_chi = Math.sin( chi );
   double cos_chi = Math.cos( chi );

   double sin_omega = Math.sin( omega );
   double cos_omega = Math.cos( omega );

   double phiR[][] = {  { cos_phi, -sin_phi, 0 },
                        { sin_phi,  cos_phi, 0 },
                        {    0,        0,    1 } };

   double chiR[][] = {  { 1,    0,        0    },
                        { 0, cos_chi, -sin_chi },
                        { 0, sin_chi,  cos_chi } };

   double omegaR[][] = {  { cos_omega, -sin_omega, 0 },
                          { sin_omega,  cos_omega, 0 },
                          {    0,          0,      1 } };

   // build the matrix product (omegaR * chiR * phiR).  When applied to a
   // vector v as in  (omegaR * chiR * phiR)v, this has the effect of doing
   // the rotation phiR first!.

   double result[][] = LinearAlgebra.mult( omegaR, chiR );
   result = LinearAlgebra.mult( result, phiR );
   return result;
}



/* ---------------------- makeEulerRotationInverse -------------------- */
/*
 *  Make the cumulative rotation matrix that reverses rotation by Euler angles
 *  phi, chi and omega.  This produces a matrix representing the following   
 *  sequence of rotations, applied in the order listed:
 *
 *  1. Rotate about the positive z-axis by minus omega
 *  2. Rotate about the positive x-axis by minus chi
 *  3. Rotate about the positive z-axis by minus phi
 *
 *  NOTE: All rotations follow a right hand rule and the coordinate system is
 *  assumed to be right handed.  Rotation angles are specified in degrees.
 *  To use this to "unwind" the goniometer rotations on the SCD at IPNS with 
 *  the values of phi, chi and omega stored in the IPNS runfiles, use:
 *
 *     makeEulerRotationInverse(phi, chi, -omega)
 *
 *  @param phi    phi angle that the goniometer was rotated by about +z axis
 *  @param chi    chi angle that the goniometer was rotated by about +x axis
 *  @param omega  omega angle that the goniometer was rotated by about +z axis
 *  
 *  @return The cumulative rotation matrix that reversed the rotations by
 *          phi, chi and omega.  This returns the matrix product:
 *
 *          phiRz_inverse * chiRx_inverse * omegaRz_inverse
 *    
 *          which which carries out these rotations, starting with rotation by
 *          minus omega about the z-axis.
 */
public static Tran3D_d makeEulerRotationInverse(double phi, 
                                                double chi, 
                                                double omega)
{
  Tran3D_d inverse = makeEulerRotation( phi, chi, omega );

  inverse.transpose();  // NOTE: A Rotation matrix is an orthogonal
                        //       transformation and so it's transpose is
                        //       it's inverse.
  return inverse;
}


/* ---------------------- makeEulerRotationInverse -------------------- */
/*
 *  Make the cumulative rotation matrix that reverses rotation by Euler angles
 *  chi, phi and omega.  This produces a matrix representing the following
 *  sequence of rotations, applied in the order listed:
 *
 *  1. Rotate about the positive z-axis by minus omega
 *  2. Rotate about the positive x-axis by minus chi
 *  3. Rotate about the positive z-axis by minus phi
 *
 *  NOTE: All rotations follow a right hand rule and the coordinate system is
 *  assumed to be right handed.  Rotation angles are specified in degrees.
 *  To use this to "unwind" the goniometer rotations on the SCD at IPNS with
 *  the values of phi, chi and omega stored in the IPNS runfiles, use:
 *
 *     makeEulerRotationInverse(phi, chi, -omega)
 *
 *  @param phi    phi angle that the goniometer was rotated by about +z axis
 *  @param chi    chi angle that the goniometer was rotated by about +x axis
 *  @param omega  omega angle that the goniometer was rotated by about +z axis
 *
 *  @return The cumulative rotation matrix that reversed the rotations by
 *          phi, chi and omega.  This returns the matrix product:
 *
 *          phiRz_inverse * chiRx_inverse * omegaRz_inverse
 *
 *          which which carries out these rotations, starting with rotation by
 *          minus omega about the z-axis.
 */
public static Tran3D makeEulerRotationInverse(float phi, float chi, float omega)
{

  Tran3D inverse = makeEulerRotation( phi, chi, omega );

  inverse.transpose();  // NOTE: A Rotation matrix is an orthogonal
                        //       transformation and so it's transpose is
                        //       it's inverse.
  return inverse;
}


/* ---------------------- makeEulerRotationInverseMatrix -------------------- */
/*
 *  Make the double precision rotation matrix that reverses rotation by Euler
 *  angles chi, phi and omega.  This produces a matrix representing the
 *  following sequence of rotations, applied in the order listed:
 *
 *  1. Rotate about the positive z-axis by minus omega
 *  2. Rotate about the positive x-axis by minus chi
 *  3. Rotate about the positive z-axis by minus phi
 *
 *  NOTE: All rotations follow a right hand rule and the coordinate system is
 *  assumed to be right handed.  Rotation angles are specified in degrees.
 *  To use this to "unwind" the goniometer rotations on the SCD at IPNS with
 *  the values of phi, chi and omega stored in the IPNS runfiles, use:
 *
 *     makeEulerRotationInverse(phi, chi, -omega)
 *
 *  @param phi    phi angle that the goniometer was rotated by about +z axis
 *  @param chi    chi angle that the goniometer was rotated by about +x axis
 *  @param omega  omega angle that the goniometer was rotated by about +z axis
 *
 *  @return The cumulative rotation matrix that reversed the rotations by
 *          phi, chi and omega.  This returns the matrix product:
 *
 *          phiRz_inverse * chiRx_inverse * omegaRz_inverse
 *
 *          which which carries out these rotations, starting with rotation by
 *          minus omega about the z-axis.
 */
public static double[][] makeEulerRotationInverseMatrix( double phi,
                                                         double chi,
                                                         double omega )
{
  double inverse[][] = makeEulerRotationMatrix( phi, chi, omega );

                           // NOTE: A Rotation matrix is an orthogonal
                           //       transformation and so it's transpose is
                           //       it's inverse.
  return LinearAlgebra.getTranspose( inverse );
}




/* --------------------------------- main -------------------------------- */
/**
 *  main program for test purposes only
 */

public static void main( String args[] )
{

}

}
