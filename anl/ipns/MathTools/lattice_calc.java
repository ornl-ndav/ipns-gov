/*
 * File:  lattice_calc.java
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
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 *
 * $Log$
 * Revision 1.7  2006/01/16 04:24:15  dennis
 * Added Ruby lattice paramters to main test program.
 *
 * Revision 1.6  2006/01/05 18:01:49  dennis
 * Fixed one spelling error in printout for test program.
 * Changed default values used for a,b,c, alpha,beta,gamma
 * in test program.
 *
 * Revision 1.5  2005/04/20 21:24:02  dennis
 * Modified LatticeParamsOfG() and LatticeParamsOfUB() to return
 * null if the matrix is singular.
 *
 * Revision 1.4  2004/03/15 23:53:48  dennis
 * Removed unused imports, after factoring out the View components,
 * Math and other utils.
 *
 * Revision 1.3  2004/03/11 23:29:48  dennis
 * Moved to MathTools package
 *
 * Revision 1.2  2003/07/14 13:35:08  dennis
 * Fixed a java doc comment and added more tests to the main program.
 *
 * Revision 1.1  2003/06/19 15:39:10  dennis
 * Initial version of utility class that provides basic calculations
 * for direct and reciprocal lattices such as conversion between lattice
 * parameters, metric matrices, and matrices with lattice vectors as
 * rows.  Also included are routines for "factoring" an orientation
 * matrix UB into factors U and B.
 *
 */

package gov.anl.ipns.MathTools;

import  gov.anl.ipns.Util.Numeric.*;
import  gov.anl.ipns.MathTools.Geometry.Vector3D_d;

/**
 *  This class provides basic calculations for direct and reciprocal lattices.
 *  Conversion between lattice parameters, metric matrices, and matrices 
 *  with lattice vectors as rows are provided, as are routines for "factoring"
 *  an orientation matrix UB into factors U and B.
 */

public class lattice_calc 
{
  /**
   * Don't instantiate this class.
   */
  private lattice_calc() {}


  /**
   *  Construct the matrix "A" whose rows are the lattice vectors
   *  a_, b_ and c_, from the lattice parameters a, b, c, alpha, beta,  
   *  gamma.  If the parameters are for the real space lattice, 
   *  the rows of A will be the direct lattice vectors.  If the
   *  parameters are for the reciprocal space lattice, the rows of
   *  A will be the reciprocal space lattice vectors.  The vectors
   *  are represented relative to a right hand, xyz, coordinate system
   *  where vector a_ is in the direction of the x-axis, and vector
   *  b_ is in the upper half of the x,y plane.     
   *
   *  @param p   An array containing the six lattice parameters,
   *             a,b,c,alpha,beta,gamma in positions 0.. 5. The
   *             angles are in degrees
   *
   *  @return  a 3x3 matrix whose rows are the lattice vectors a_, b_, c_
   */
   static public double[][] A_matrix( double p[] ) 
   {
     return A_matrix( p[0], p[1], p[2], p[3], p[4], p[5] );
   }

  /**
   *  Construct the matrix "A" whose rows are the lattice vectors
   *  a_, b_ and c_, from the lattice parameters a, b, c, alpha, beta, 
   *  gamma.  If the parameters are for the real space lattice, 
   *  the rows of A will be the direct lattice vectors.  If the
   *  parameters are for the reciprocal space lattice, the rows of
   *  A will be the reciprocal space lattice vectors.  The vectors
   *  are represented relative to a right hand, xyz, coordinate system 
   *  where vector a_ is in the direction of the x-axis, and vector 
   *  b_ is in the upper half of the x,y plane. 
   *
   *  @param a      The length of the first lattice vector, a_ 
   *  @param b      The length of the second lattice vector, b_ 
   *  @param c      The length of the third lattice vector, c_ 
   *  @param alpha  The angle between the last two lattice vectors, b_ and c_
   *                in degrees
   *  @param beta   The angle between the first and last lattice vectors, 
   *                a_ and c_ in degrees
   *  @param gamma  The angle between the first two lattice vectors, a_ and b_
   *                in degrees
   *  @return  a 3x3 matrix whose rows are the lattice vectors a_, b_, c_
   */
   static public double[][] A_matrix( double a,     double b,    double c,
                                      double alpha, double beta, double gamma ) 
   {
     double degrees_to_radians = Math.PI / 180;
     alpha *= degrees_to_radians;
     beta  *= degrees_to_radians;
     gamma *= degrees_to_radians;

     double l1 = a;
     double l2 = 0;
     double l3 = 0;
  
     double m1 = b*Math.cos(gamma);
     double m2 = b*Math.sin(gamma);
     double m3 = 0;

     double n1 = Math.cos(beta);
     double n2 = (Math.cos(alpha)- Math.cos(beta)*Math.cos(gamma)) /
                  Math.sin(gamma);
     double n3 = Math.sqrt( 1 - n1*n1 - n2*n2 );
     n1 *= c;
     n2 *= c;
     n3 *= c;
 
     double result[][] = {{l1,l2,l3}, {m1,m2,m3}, {n1,n2,n3}};

     return result;
   }

  /**
   *  Construct the matrix "A" whose rows are unit vectors in the
   *  direction of the lattice vectors a_, b_ and c_, from the lattice
   *  parameters a, b, c, alpha, beta, gamma.  If the parameters are
   *  for the real space lattice, the rows of A will be in the directions
   *  of the real space lattice vectors.  If the parameters are for the
   *  reciprocal space lattice, the rows of A will be in the directions of
   *  the reciprocal space lattice vectors.  The vectors
   *  are represented relative to a right hand, xyz, coordinate system
   *  where vector a_ is in the direction of the x-axis, and vector
   *  b_ is in the upper half of the x,y plane.
   *
   *  @param p   An array containing the six lattice parameters,
   *             a,b,c,alpha,beta,gamma in positions 0.. 5.  The
   *             angles must be in degrees
   */
   static public double[][] A_unit( double p[] ) 
   {
     return A_unit( p[0], p[1], p[2], p[3], p[4], p[5] );
   }

  /**
   *  Construct the matrix "A" whose rows are unit vectors in the
   *  direction of the lattice vectors a_, b_ and c_, from the lattice 
   *  parameters a, b, c, alpha, beta, gamma.  If the parameters are 
   *  for the real space lattice, the rows of A will be in the directions 
   *  of the real space lattice vectors.  If the parameters are for the 
   *  reciprocal space lattice, the rows of A will be in the directions of
   *  the reciprocal space lattice vectors.  The vectors
   *  are represented relative to a right hand, xyz, coordinate system
   *  where vector a_ is in the direction of the x-axis, and vector
   *  b_ is in the upper half of the x,y plane.
   *
   *  @param a      The length of the first lattice vector, a_
   *  @param b      The length of the second lattice vector, b_
   *  @param c      The length of the third lattice vector, c_
   *  @param alpha  The angle between the last two lattice vectors, b_ and c_
   *                in degrees.
   *  @param beta   The angle between the first and last lattice vectors, 
   *                a_ and c_ in degrees.
   *  @param gamma  The angle between the first two lattice vectors, a_ and b_
   *                in degrees.
   *
   *  @return  a 3x3 matrix whose rows are the lattice vectors a_, b_, c_
   */
   static public double[][] A_unit( double a,     double b,    double c,
                                    double alpha, double beta, double gamma )
   {
     double A[][] = A_matrix( a, b, c, alpha, beta, gamma );
     double mag[] = new double[3];
     for ( int i = 0; i < 3; i++ )
       mag[i] = Math.sqrt(A[i][0]*A[i][0] + A[i][1]*A[i][1] + A[i][2]*A[i][2]);

     for ( int i = 0; i < 3; i++ )
       for ( int j = 0; j < 3; j++ )
         A[i][j] /= mag[i];

     return A;
   }


  /**
   *  Construct the metric matrix, whose entries are the dot products of
   *  the lattice vectors a_, b_ and c_ for the lattice determined by the
   *  specified parameters.
   *
   *  @param p   An array containing the six lattice parameters,
   *             a,b,c,alpha,beta,gamma in positions 0.. 5.  The angles
   *             must be in degrees.
   *
   *  @return  a 3x3 matrix whose entries are the dot products between the
   *           vectors a_, b_ and c_
   */
   static public double[][] G_matrix( double p[] ) 
   {
     return G_matrix( p[0], p[1], p[2], p[3], p[4], p[5] );
   }


  /**
   *  Construct the metric matrix, whose entries are the dot products of
   *  the lattice vectors a_, b_ and c_ for the lattice determined by the
   *  specified parameters.
   *
   *  @param a      The length of the first lattice vector, a_
   *  @param b      The length of the second lattice vector, b_
   *  @param c      The length of the third lattice vector, c_
   *  @param alpha  The angle between the last two lattice vectors, b_ and c_
   *                in degrees.
   *  @param beta   The angle between the first and last lattice vectors,
   *                a_ and c_ in degrees.
   *  @param gamma  The angle between the first two lattice vectors, a_ and b_
   *                in degrees.
   *
   *  @return  a 3x3 matrix whose entries are the dot products between the 
   *           vectors a_, b_ and c_
   */
   static public double[][] G_matrix( double a,     double b,    double c,
                                      double alpha, double beta, double gamma ) 
   {
      alpha  *= Math.PI / 180.0;
      beta   *= Math.PI / 180.0;
      gamma  *= Math.PI / 180.0;

      double G[][] = new double[3][3];

      G[0][0] = a * a;
      G[0][1] = a * b * Math.cos(gamma);
      G[0][2] = a * c * Math.cos(beta);

      G[1][0] = G[0][1];
      G[1][1] = b * b;
      G[1][2] = b * c * Math.cos(alpha);

      G[2][0] = G[0][2];
      G[2][1] = G[1][2];
      G[2][2] = c * c;

      return G;
   }

  /** 
   *  Calculate the lattice parameters corresponding to the metric matrix, G,
   *  containing the dot products of the basis vectors.
   *
   *  @param    G  the 3x3 metric matrix for the lattice containing the 9 
   *            dot products of a_, b_ and c_. 
   *
   *  @return   An array containing the six lattice parameters,
   *            a, b, c, alpha, beta, gamma and the unit cell volume in 
   *            positions 0..6.  The angles are in degrees.
   */
   static public double[] LatticeParamsOfG( double G[][] )
   {
     double parms[] = new double[7];
     double a = Math.sqrt( G[0][0] );
     double b = Math.sqrt( G[1][1] );
     double c = Math.sqrt( G[2][2] );
     parms[0] = a;
     parms[1] = b; 
     parms[2] = c;
     double alpha = Math.acos( G[1][2] / (parms[1] * parms[2]) );
     double beta  = Math.acos( G[0][2] / (parms[0] * parms[2]) );
     double gamma = Math.acos( G[0][1] / (parms[0] * parms[1]) );
     parms[3] = (180/Math.PI)*alpha;
     parms[4] = (180/Math.PI)*beta;
     parms[5] = (180/Math.PI)*gamma;
     double cos_alpha = Math.cos( alpha );
     double cos_beta  = Math.cos( beta  );
     double cos_gamma = Math.cos( gamma );

     parms[6] = a * b * c * Math.sqrt(1 - cos_alpha * cos_alpha 
                                        - cos_beta  * cos_beta
                                        - cos_gamma * cos_gamma
                                        + 2*cos_alpha * cos_beta * cos_gamma); 

//   NOTE: Depending on which convention is followed regarding 2PI, the 
//         cell volume may need to be multiplied by (2PI)^3.  Currently 
//         most code in ISAW already adjusts for that, so we will NOT multiply 
//         by that factor here.  Hence the following statement should be
//         commented out.
//
//   parms[6] *= 8 * Math.PI * Math.PI * Math.PI;

     return parms;
   }

  /** 
   *  Calculate the lattice parameters corresponding to the matrix
   *  A whose rows are the lattice vectors.
   *
   *  @param    A  the 3x3 matrix with the lattice vectors as 
   *               rows.
   *
   *  @return   An array containing the six lattice parameters,
   *            a, b, c, alpha, beta, gamma and the unit cell volume in 
   *            positions 0..6. The angles are in degrees.
   */
   static public double[] LatticeParamsOfA( double A[][] )
   {
     double At[][] = LinearAlgebra.getTranspose( A );
     double G[][] = LinearAlgebra.mult( A, At );
     return LatticeParamsOfG( G );
   }

  /**
   *  Calculate the lattice parameters corresponding to the orientation
   *  matrix UB.
   *
   *  @param    UB the 3x3 orientation matrix consisting of the product of a 
   *               cumulative rotation U and the "material matrix" B.  
   *               The matrix B must have the reciprocal space vectors 
   *               a*, b*, c*, divided by 2 PI, as columns.
   *
   *  @return   An array containing the six lattice parameters,
   *            a, b, c, alpha, beta, gamma and the unit cell volume in 
   *            positions 0..6.  The angles are in degrees.  If the matrix
   *            is singular, return null.
   */
   static public double[] LatticeParamsOfUB( double UB[][] )
   {
     double UBt[][] = LinearAlgebra.getTranspose( UB );
     double Gstar[][] = LinearAlgebra.mult( UBt, UB );
     double G[][] = LinearAlgebra.getInverse( Gstar );
     if ( G == null )
       return null;
     else
       return LatticeParamsOfG( G );
   }

  /**
   *  Calculate the combined rotation factor, U, from the orientation 
   *  matrix UB.  The matrix U is represented relative to a right handed 
   *  coordinate system with the direct space lattice vector a_ in the x
   *  direction, and vector b_ in the upper half of the xy plane.
   *
   *  @param    UB the 3x3 orientation matrix consisting of the product of a
   *               cumulative rotation U and the "material matrix" B.
   *               The matrix B must have the reciprocal space vectors
   *               a*, b*, c*, divided by 2 PI, as columns.
   *
   *  @return the factor U.  If the UB matrix is singular, return null
   */ 
   static public double[][] getU( double UB[][] )
   {
     double UBt[][] = LinearAlgebra.getTranspose( UB );
     double Gstar[][] = LinearAlgebra.mult( UBt, UB );
     double G[][] = LinearAlgebra.getInverse( Gstar );
     if ( G == null )
       return null;
     
     double lat_params[] = LatticeParamsOfG( G );      
     double A[][] = A_matrix( lat_params );        // note: B is the transpose 
                                                   //       of Astar, so B is
                                                   //       the inverse of A
     double U[][] = LinearAlgebra.mult( UB, A );
     return U;
   }

  /**
   *  Calculate the material matrix factor, B, from the orientation
   *  matrix UB.  The matrix B is represented relative to a right handed
   *  coordinate system with the direct space lattice vector a_ in the x
   *  direction, and vector b_ in the upper half of the xy plane.
   *
   *  @param    UB the 3x3 orientation matrix consisting of the product of a
   *               cumulative rotation U and the "material matrix" B.
   *               The matrix B must have the reciprocal space vectors
   *               a*, b*, c*, divided by 2 PI, as columns.
   *
   *  @return the factor B.
   */
   static public double[][] getB( double UB[][] )
   {
     double U[][] = getU( UB );                       // since U is a rotation
                                                      // it's inverse is it's
     double Ut[][] = LinearAlgebra.getTranspose( U ); // transpose
     double B[][]  = LinearAlgebra.mult( Ut, UB );

     return B;
   }

  /**
   *  Calculate the material matrix factor, B, from the orientation
   *  matrix UB, and the rotation U. This is a convenience method to allow
   *  more efficient calculation of B in the case that U has already been
   *  calculated. The matrix B is represented relative to a right handed
   *  coordinate system with the direct space lattice vector a_ in the x
   *  direction, and vector b_ in the upper half of the xy plane.
   *
   *  @param    UB the 3x3 orientation matrix consisting of the product of a
   *               cumulative rotation U and the "material matrix" B.
   *               The matrix B must have the reciprocal space vectors
   *               a*, b*, c*, divided by 2 PI, as columns.
   *
   *  @param    U  the cumulative rotation factor of UB
   *
   *  @return the factor B.
   */ 
   static public double[][] getB( double UB[][], double U[][] )
   {                                                  // since U is a rotation
                                                      // it's inverse is it's
     double Ut[][] = LinearAlgebra.getTranspose( U ); // transpose
     double B[][]  = LinearAlgebra.mult( Ut, UB );

     return B;
   }


  /**
   * Construct a UB matrix from the lattice parameters and 
   * two indexed Q-vectors.
   *
   * @param lat_params Array containing the lattice parameters a, b, c,
   *                   alpha, beta, gamma as it's first six entries.
   * @param H1         Array containing first (h,k,l) triple
   * @param H2         Array containing second (h,k,l) triple
   * @param q1         Array containing first Q-Vector
   * @param q2         Array containing second Q-Vector
   *                   
   * @return A matrix UB that maps H1 to q1, and H2 to q2
   */
   static public double[][] getUB( double[] lat_params, 
                                   double[] H1,
                                   double[] H2,
                                   double[] q1,
                                   double[] q2 )
   {
     double[][] A_matrix = A_matrix( lat_params );     
     double[][] B = LinearAlgebra.getInverse( A_matrix );

     Vector3D_d q1_vec = new Vector3D_d( q1 );
     Vector3D_d q2_vec = new Vector3D_d( q2 );
     Vector3D_d q3_vec = new Vector3D_d();
     q3_vec.cross( q1_vec, q2_vec );
     double[] q3 = q3_vec.get();

     double[] b1 = LinearAlgebra.mult( B, H1 );
     double[] b2 = LinearAlgebra.mult( B, H2 );

     Vector3D_d b1_vec = new Vector3D_d( b1 );
     Vector3D_d b2_vec = new Vector3D_d( b2 );
     Vector3D_d b3_vec = new Vector3D_d();
     b3_vec.cross( b1_vec, b2_vec );
     double[] b3 = b3_vec.get();

     double[][] M = { { b1[0], b2[0], b3[0] },
                      { b1[1], b2[1], b3[1] },
                      { b1[2], b2[2], b3[2] } };

     double[][] Q = { { q1[0], q2[0], q3[0] },
                      { q1[1], q2[1], q3[1] },
                      { q1[2], q2[2], q3[2] } };

     double[][] M_inv = LinearAlgebra.getInverse( M );
 
     double[][] U  = LinearAlgebra.mult( Q, M_inv );
     double[][] UB = LinearAlgebra.mult( U, B );

     return UB;
   }


  /*
   *  Main program for testing purposes.
   */

   public static void main( String args[] )
   {
/*
     double a = 4.766;           // Ruby calibrant for LANSCE
     double b = 4.766;
     double c = 12.996; 
     double alpha = 90; 
     double beta  = 90;
     double gamma = 120;

     double a = 4.91642;
     double b = 4.91254;
     double c = 5.42703; 
     double alpha = 88.7985;
     double beta  = 89.3668;
     double gamma = 61.3224;
*/
     double a = 4.9138;          // Quartz calibrant for IPNS
     double b = 4.9138;
     double c = 5.4051;
     double alpha = 90;
     double beta  = 90;
     double gamma = 120;

     double A[][]     = A_matrix( a, b, c, alpha, beta, gamma );
     double Aunit[][] = A_unit( a, b, c, alpha, beta, gamma );
     double G[][]     = G_matrix( a, b, c, alpha, beta, gamma );
     double Ginverse[][] = LinearAlgebra.getInverse( G );
     
     double Astar[][] = LinearAlgebra.mult( Ginverse, A );
     double TwoPI_Astar[][] = new double[3][3];
     for ( int i = 0; i < 3; i++ )
       for ( int j = 0; j < 3; j++ )
         TwoPI_Astar[i][j] = 2 * Math.PI * Astar[i][j]; 
     System.out.println( "A = " );
     LinearAlgebra.print( A );

     System.out.println( "Parameters (A) = " );
     LinearAlgebra.print( LatticeParamsOfA(A) );

     System.out.println( "Aunit = " );
     LinearAlgebra.print( Aunit );

     System.out.println( "G = " );
     LinearAlgebra.print( G );

     System.out.println( "Ginverse = " );
     LinearAlgebra.print( Ginverse );

     System.out.println( "Parameters (G) = " );
     LinearAlgebra.print( LatticeParamsOfG(G) );

     System.out.println( "Astar = " );
     LinearAlgebra.print( Astar );

     System.out.println( "TwoPI_Astar = " );
     LinearAlgebra.print( TwoPI_Astar );

     System.out.println( "Parameters (Astar) = " );
     LinearAlgebra.print( LatticeParamsOfA(Astar) );

     System.out.println( "Parameters (Ginverse) = " );
     LinearAlgebra.print( LatticeParamsOfG(Ginverse) );

     double det_G = LinearAlgebra.determinant( G );
     double cell_V = Math.sqrt( det_G );

     System.out.println("Original a, b, c, alpha, beta, gamma, new V");
     System.out.println("" + Format.real(a,5,5) + "   " +
                             Format.real(b,5,5) + "   " +
                             Format.real(c,5,5) + "   " +
                             Format.real(alpha,5,5) + "   " +
                             Format.real(beta, 5,5) + "   " +
                             Format.real(gamma,5,5) + "   " +
                             Format.real(cell_V,5,5)  );
     double Astar_transp[][] = LinearAlgebra.getTranspose( Astar );
     double A_transp[][] = LinearAlgebra.getTranspose( A );

     System.out.println("Astar transpose = ");
     LinearAlgebra.print( Astar_transp );     

     System.out.println( "A * Astar_transp =" );
     double p1[][] = LinearAlgebra.mult( A, Astar_transp );
     LinearAlgebra.print( p1 );     

     System.out.println( "Astar * A_transp =" );
     double p2[][] = LinearAlgebra.mult( Astar, A_transp );
     LinearAlgebra.print( p2 ); 

     double UB_transpose[][] = { { 0.008789, -0.205259, -0.111754 },
                                 { 0.010465, -0.199442,  0.121459 },
                                 {-0.184146, -0.008733,  0.001372 } };
     double UB[][] = LinearAlgebra.getTranspose( UB_transpose );
     System.out.println("UB = ");
     LinearAlgebra.print( UB );
 
     double U[][] = getU( UB );
     System.out.println("U = ");
     LinearAlgebra.print( U );

     double B[][] = getB( UB );
     System.out.println("B = ");
     LinearAlgebra.print( B );

     double p3[][] = LinearAlgebra.mult( U, B );
     System.out.println("Product of U & B = ");
     LinearAlgebra.print( p3 );

     double Ut[][] = LinearAlgebra.getTranspose( U );
     double p4[][] = LinearAlgebra.mult( U, Ut );
     System.out.println("Product of U & U transpose = ");
     LinearAlgebra.print( p4 );

     System.out.println( "Parameters (UB) = " );
     LinearAlgebra.print( LatticeParamsOfUB(UB) );
       
     UB = LinearAlgebra.getTranspose( Astar );
     System.out.println( "Parameters (Astar_transpose) = " );
     LinearAlgebra.print( LatticeParamsOfUB(UB) );

     double temp[][] = {  {-0.115425, -0.166643, -0.118645 },
                          {-0.123538, -0.162548,  0.116026 },
                          {-0.149326,  0.108474, -0.007203 } };
     double UB_Art[][] = LinearAlgebra.getTranspose( temp );
     double params_Art[] = LatticeParamsOfUB(UB_Art);
     System.out.println("Params from Art's quartz calibration");
     LinearAlgebra.print( params_Art );

     double RanUB[][] = { {  0.23,   12.23, -91.12 },
                          { -23.334, 23.12,  54.21 },
                          { - 3.99, -31.23, 231.34 } };
     System.out.println("RanUB--------");
     LinearAlgebra.print( RanUB );

     double RanUBt[][] = LinearAlgebra.getTranspose( RanUB );
     System.out.println("RanUBt--------");
     LinearAlgebra.print( RanUBt );

     double ran_prod[][] = LinearAlgebra.mult( RanUB, RanUBt );
     System.out.println("ran_prod--------");
     LinearAlgebra.print( ran_prod );

     System.out.println("ran_inv--------");
     double ran_inv[][] = LinearAlgebra.getInverse( ran_prod );
     LinearAlgebra.print( ran_inv );
     
     double RanU[][]  = getU( RanUB );
     double RanUt[][] = LinearAlgebra.getInverse(RanU);
     double prod[][]  = LinearAlgebra.mult( RanU, RanUt );    
     System.out.println("product of RanU and RanUt is ");
     LinearAlgebra.print( prod );


     //
     // Test getUB method using some quartz peaks and indexing from IsawEV:
     // UB:
     //      0.0058656      -0.1280224       0.1454910 
     //     -0.0039880      -0.1582295      -0.1131304 
     //      0.2347778      -0.1166481      -0.0060742 
     //
     System.out.println("\nTesting getUB from lat parameters and indexing");
     double[] lat_par = { 4.913, 4.920, 5.423, 89.881, 89.802, 60.053 };
     double[] H1 = {  1.0,  3.0, -1.0 };
     double[] H2 = {  1.0,  2.0,  0.0 };
     double[] q1 = { -0.525, -0.366, -0.108 };
     double[] q2 = { -0.251, -0.321,  0.001 };

     UB = getUB( lat_par, H1, H2, q1, q2 );

     System.out.println( "AFTER call to getUB, UB = " );
     LinearAlgebra.print( UB );

     System.out.println("Resulting Lattice Parameters, from UB");
     LinearAlgebra.print( LatticeParamsOfUB( UB ) );

     double[] new_q1 = LinearAlgebra.mult( UB, H1 );
     double[] new_q2 = LinearAlgebra.mult( UB, H2 );

     System.out.println("new_q1 = ");
     LinearAlgebra.print( new_q1 );
     System.out.println("new_q2 = ");
     LinearAlgebra.print( new_q2 );
   }
}
