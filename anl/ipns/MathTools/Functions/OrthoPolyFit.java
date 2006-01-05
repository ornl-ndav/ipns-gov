/*
 * File:  OrthoPolyFit.java
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
 * Weighted least square fitting to a set of data points using orthogonal
 * polynomials. This is an implmentation of the method described in detail
 * in "Clenshaw (1960), Curve Fitting with a Digital Computer, comput. J.
 * 2 170-173", which represents each polynomial by the coefficients
 * in its Chebyshev series and uses Clenshaw recurrence formula to calculate
 * its numerical values. The advantage of this method over other linear least
 * squares (normal equations, QR, SVD) is that it is much smaller and faster.
 * In some cases it even introduces less round-off errors. This implementation
 * is probably eqivalent to the NAG Fortran library routine E02ADF, with the
 * exception that the Clenshaw recurrent formula used here is not adjusted
 * for numerical stability.
 */
public class OrthoPolyFit {
  
//bookkeeping method to prepare a histogram data for opolyfit();  
  public static double[][] opolyfit_h (float xs0[], float ys[], int kplus1) {
  
    int n = ys.length;
    if (xs0.length != n+1) throw new RuntimeException("!!!!!!input data not a histogram!!!!!!");
    if (kplus1 >= 20) throw new RuntimeException("******fitting polynomial order exceeding 20******");
    double xvals[] = new double[n];
    double yvals[] = new double[n];
    double svals[] = new double[n];
    
    for (int i = 0; i < n; i++) {
      xvals[i] = (double) xs0[i];
      yvals[i] = (double) ys[i];
      svals[i] = 1.0;
    }
    return opolyfit (xvals, yvals, svals, kplus1);         
  }
  
  /**
   * Curve fitting with orthogonal polynomials.
   * 
   * @param xs0     list of x values, they will be transformed linearly into
   *                [-1, 1]. 
   * @param ys      list of y values corresponding to the list of x values.
   * @param ss      list of weighting coefficients, most often standard deviation
   *                associated with each y, weighting for each y value is
   *                calculated as one over square of sigma.
   * @param kplus1  highest order of polynomials used will be kplus1-1.
   * @return a 2D double array A, A[i][j] being the coefficient of jth order
   *         Chebyshev term in the approximating polynomial of degree i. See
   *         the main() method for how to evaluate the polynomial using A.
   */
  public static double[][] opolyfit (double xs0[], double ys[], double ss[], int kplus1) {

//  The symbol notations follow the Clenshaw paper as closely as possible.
//  "cpt" is the P (eq. 12) value array in current cycle, "cpl" the arry
//  in the last cycle, "sump2t" (current cycle), "sump2l" (last cycle),
//  "sumxp2", "sumyp" are those sums in eq. 4 and 6, "msr" the mean square
//  of residuals.

    int m = xs0.length-1;
    double xmin = xs0[0], xmax = xs0[m];
    int k = kplus1-1;
    double cpt[] = new double[k+2], cpl[] = new double[k+2], dummy[];
    double A[][] = new double[kplus1][kplus1];
    double x, y, w;
    double b0, b1, b2, p;
    double sump2t, sump2l = 0, sumxp2, sumyp;
    double alpha, beta, c;
    double ybar=0, sumw=0, deltasqr=0, msr[] = new double[kplus1];
   
    for (int r = 0; r <= m; r++) {
      w = Math.pow(ss[r],2);
      ybar += w*ys[r];
      sumw += w;
    }
    ybar /= sumw;
    for (int r = 0; r <= m; r++)
      deltasqr += Math.pow(ss[r]*(ys[r]-ybar), 2);
    msr[0] = deltasqr/m; //  mean square residual for zero order; 
    
    cpt[0] = 1;
    for (int i = 0; i <= k; i++) {
      
      sump2t = 0;
      sumxp2 = 0;
      sumyp = 0;
      for (int r = 0; r <= m; r++) {
        x = (2*xs0[r]-xmax-xmin)/(xmax-xmin); //transform x into [-1,1];
        y = ys[r];
        w = Math.pow(ss[r],2);
        b1 = 0;
        b2 = 0;
        for (int s = i; s >= 0; s--) {          
          b0 = 2*x*b1-b2+cpt[s];
          b2 = b1;
          b1 = b0;
        } //Clenshaw recurrence formula, eq. 10;
        p = b1 -x*b2-cpt[0]/2;
        sump2t += w*p*p;
        sumxp2 += x*w*p*p;
        sumyp += y*w*p;
      }      
      beta = (i==0)?0:(sump2t/sump2l);
      alpha = sumxp2/sump2t;
      c = sumyp/sump2t; 
      sump2l = sump2t;    

      if (i > 0) {
        deltasqr -= c*c*sump2t;
        msr[i] = deltasqr/(m-i);
      }
//      System.out.println("rms: "+Math.sqrt(msr[i])+" alpha: "+alpha+" beta: "+beta+" c: "+c);        

      for (int j = 0; j <= i; j++) { 
        p = ((j+1>i)?0:cpt[j+1]) + ((Math.abs(j-1)>i)?0:cpt[Math.abs(j-1)])
                - 2*alpha*cpt[j] - ((j>(i-1))?0:(beta*cpl[j]));
        cpl[j] = p;        
        A[i][j] = ((j>(i-1))?0:A[i-1][j]) + c*cpt[j];
      }
      cpl[i+1] = 1;
/*
      for (int j = 0; j <= i; j++) {
        cpl[j] = cpt[j];
        cpt[j] = cp[j]; 
      }
            cpt[i+1] = 1;
*/
       dummy = cpl;
       cpl = cpt;
       cpt = dummy;
//      System.out.println("P[][2]"+cpt[2]);
    }
   
//    System.out.println("A[][]: "+A[3][0]+" "+A[3][1]+" "+A[3][2]+" "+A[3][3]+" "+A[3][4]+" "+A[3][5]+" "+A[3][6]); 
    return A;        
  }
  
  
  
  public static void main(String[] args) {
    
    double x[] = new double[101];
    double y[] = new double[101];
    double w[] = new double[101];
    for (int i = 0; i <= 100; i++) {
      x[i] = i*0.02;
      y[i] = Math.exp(i*0.02);
      w[i] = 100/y[i]/y[i];
    }

    x = new double[] {1.0, 2.1, 3.1, 3.9, 4.9, 5.8, 6.5, 7.1, 7.8, 8.4, 9.0};
    y = new double[] {10.4, 7.9, 4.7, 2.5, 1.2, 2.2, 5.1, 9.2, 16.1, 24.5, 35.3};  
    w = new double[] {1, 1, 1, 1, 1, 0.8, 0.8, 0.7, 0.5, 0.3, 0.2};

    double A3[] = opolyfit(x, y, w, 7)[3];
    A3[0] /= 2;
    ChebyshevSum ycheby3 = new ChebyshevSum(A3);
    double xi, xmin = x[0], xmax = x[x.length-1];
    int m = x.length-1;
    for (int i = 0; i <= m; i++) {
      xi = (2*x[i]-xmax-xmin)/(xmax-xmin);
      System.out.println("x: "+x[i]+" y: "+y[i]+" fit: "+ycheby3.getValue(xi));
    }
  }

}
