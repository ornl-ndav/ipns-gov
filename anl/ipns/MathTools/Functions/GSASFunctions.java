/*
 * File:  GSASFunctions.java
 *
 * Copyright (C) 2006 J. Tao
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Genernal Public License
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
 * Contact : Julian Tao <taoj@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 */

package gov.anl.ipns.MathTools.Functions;

/**
 * JNI wrapper code for some GSAS Fortran subroutines and functions.
 */

public class GSASFunctions {
  
  static { 
    try{ System.out.println("Loading GSAS library.");
//"libgsas.so" on Linux, "gsas.dll" on Windows;
      System.loadLibrary("gsas");
    } catch (Throwable t) {
      System.out.println("XCannot loadLibrary. Error="+t);
      t.printStackTrace();
    }
  }

//GSAS TOF profile function 1, a convolution of two back-to-back exponentials
//with a gaussian peak shape function (R.B. Von Dreele, J. Jorgensen & C. Windsor,
//J. Appl. Cryst., 1982). The fortran subroutine is:
// SUBROUTINE EXPGAUS1(DT,ALP,BET,SIG,PRFUNC,DPRDT,ALPART,BEPART,SGPART)

  public native int expgaus1 (float pgargs[]);

//GSAS TOF profile function 3, a convolution of two back-to-back exponentials
//with a pseudo-voigt peak shape function. The fortran subroutine is: 
//SUBROUTINE EPSVOIGT(DT,ALP,BET,SIG,GAM,FUNC,DFDX,DFDA,DFDB,DFDS,DFDG)

  public native int epsvoigt (float pvargs[]);

//Complementary error function of a single precision variable implemented in GSAS;
  public native float gerfc (float x);

//Unit testing;  
  public static void main(String[] args) {

//20.9287, 0.38545, 0.03262, 280.8383, 8.1543, 0.0, 0.0    
    float dt=0.0f, alp=.38545f, bet=.03262f, sig=280.8383f, gam=8.1543f, func=0.0f, 
                dfdx=0.0f, dfda=0.0f, dfdb=0.0f, dfds=0.0f, dfdg=0.0f;

    float scalef = 1e5f,
          tof0 = 20940f,
          alpha = 0.38545f,
          beta = 0.03262f,
          sigmasqr = 280.8383f,
          gamma = 8.1543f;
    
    float pgargs[] = new float[] {dt, alpha, beta, sigmasqr, func, 
          dfdx, dfda, dfdb, dfds};
    float pvargs[] = new float[] {dt, alpha, beta, sigmasqr, gamma, func, 
      dfdx, dfda, dfdb, dfds, dfdg};
    int rtcode;
    GSASFunctions gsasf0 = new GSASFunctions();
    
    for (int i = -100; i < 101; i++) {
          pgargs[0] = i*4;      
          rtcode = gsasf0.expgaus1(pgargs);
          System.out.print(pgargs[0]+" "+pgargs[1]+" "+pgargs[2]+" "+pgargs[3]+" "+pgargs[4]+" "+pgargs[5]+" "
          +pgargs[6]+" "+pgargs[7]+" "+pgargs[8]+"\n");
        }
    
    System.out.println("\n\n\n");    
    for (int i = -100; i < 101; i++) {
      pvargs[0] = i*4;      
      rtcode = gsasf0.epsvoigt(pvargs);
      System.out.print(pvargs[0]+" "+pvargs[1]+" "+pvargs[2]+" "+pvargs[3]+" "+pvargs[4]+" "+pvargs[5]+" "
      +pvargs[6]+" "+pvargs[7]+" "+pvargs[8]+" "+pvargs[9]+" "+pvargs[10]+"\n");
    }

/*    
    for (int i = -10; i < 11; i++) {
      System.out.println(i*0.1f+" "+gsasf0.gerfc(i*0.1f));
    }
*/
  }

}
