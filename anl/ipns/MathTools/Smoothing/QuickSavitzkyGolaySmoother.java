/*
 * File:  QuickSavitzkyGolaySmoother.java
 *
 * Copyright (C) 2004, Dominic Kramer
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
 *           Dominic Kramer   <kramerd@uwstout.edu>
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
 * $Log$
 * Revision 1.2  2004/12/01 20:05:40  kramer
 * Fixed some of the javadoc statements so that they didn't have @param
 * arguments referring to variables that didn't exist.
 *
 * Revision 1.1  2004/11/03 23:56:41  kramer
 *
 * This class implements a mathematical simplification of the Savitzky-Golay
 * smoothing algorithm.  It can more quickly smooth the data by assuming the
 * data points are evenly spaced.
 *
 */
package gov.anl.ipns.MathTools.Smoothing;

import gov.anl.ipns.MathTools.LinearAlgebra;

/**
 * This class performs the task of smoothing a particular set of data using 
 * the 'quick' implementation of the Savitzky-Golay smoothing algorithm.  The 
 * 'quick' method uses an algorithm based on a mathematical simplification of 
 * the equations defining the original Savitzky-Golay smoothing algorithm.  
 * These simplifications in turn allow for great speed improvements.  
 * <p>
 * The equations are simplified by assuming that the data points (that are to 
 * be smoothed) are evenly spaced.  Therefore, if the data points <b>are</b> 
 * evenly spaced, the results of this implementation of the algorithm and 
 * the original implementation will be exact.  If the data points are not 
 * evenly spaced, this implementation will cause slightly more 'static' in 
 * the smoothed data as compared to the original algorithm's smoothed data.
 * @author Dominic Kramer
 * @see gov.anl.ipns.MathTools.Smoothing.AbstractSavitzkyGolaySmoother
 */
public class QuickSavitzkyGolaySmoother extends AbstractSavitzkyGolaySmoother
{
   /**
    * The matrix used to smooth the data.  This matrix is calculated using the 
    * method 
    * {@link #calculateCoefficientMatrix() calculateCoefficientMatrix()}.  
    * <p>
    * If the number of points to the left or number of points to the right of a 
    * point to smooth change, or if the degree of the polynomial used to 
    * smooth the data changes, this matrix has to be recalculated.  However, 
    * the setter methods for these parameters are overriden to recalculate 
    * this matrix.
    */
   private float[][] coeffMatrix;
   
   /**
    * Initializes the general information needed by the algorithm to smooth 
    * data.
    * @param  numLeft                  The number of points to the left of 
    *                                  the point in question that are used to 
    *                                  smooth the point in question.
    * @param  numRight                 The number of points to the right of 
    *                                  the point in question that are used to 
    *                                  smooth the point in question.
    * @param  polynomialDegree         The degree of the smoothing polynomial 
    *                                  used to smooth the data.
    * @throws IllegalArgumentException If one of the parameters is known to 
    *                                  be invalid.  This exception is only 
    *                                  thrown if one of the entered 
    *                                  parameters is negative.
    */
   public QuickSavitzkyGolaySmoother(int numLeft, 
                                     int numRight, 
                                     int polynomialDegree)
   {
      super(numLeft,numRight,polynomialDegree);
      calculateCoefficientMatrix();
   }
   
   /**
    * Overriden to synchronize internal data related to the data set by this 
    * method.
    * @see AbstractSavitzkyGolaySmoother
    */
   public void setNumLeft(int numLeft)
   {
      super.setNumLeft(numLeft);
      calculateCoefficientMatrix();
   }
   
   /**
    * Overriden to synchronize internal data related to the data set by this 
    * method.
    * @see AbstractSavitzkyGolaySmoother
    */
   public void setNumRight(int numRight)
   {
      super.setNumRight(numRight);
      calculateCoefficientMatrix();
   }
   
   /**
    * Overriden to synchronize internal data related to the data set by this 
    * method.
    * @see AbstractSavitzkyGolaySmoother
    */
   public void setPolynomialDegree(int degree)
   {
      super.setPolynomialDegree(degree);
      calculateCoefficientMatrix();
   }
   
   /**
    * Smooths the data in the array <code>yValues</code> between, and 
    * including, the indices <code>startingIndex</code> and 
    * <code>endingIndex</code>.  In addition to this method's parameters, the 
    * parameters passed to the constructor are used to smooth the data.
    * @param yValues        The array of function values that are going to be 
    *                       smoothed.
    * @param startingIndex  The index, in the array <code>yValues</code>, of 
    *                       the first function value that will be smoothed.
    * @param endingIndex    The index, in the array <code>yValues</code>, of 
    *                       the last function value that will be smoothed.
    * @throws IllegalArgumentException If parameters are invalid.
    */
   public void smooth(float[] yValues, int startingIndex, int endingIndex)
   {
      int nL = getNumLeft();
      int nR = getNumRight();
      int M = getPolynomialDegree();
      
      IllegalArgumentException e2 = verifyParameters(yValues, startingIndex,
                                                              endingIndex);
      if (e2 != null)
         throw e2;

      //now the startingIndex and endingIndex are safe to use
      //  because if they weren't safe an exception would have been thrown

      if (coeffMatrix == null)
         throw new IllegalArgumentException(
            "Error:  The data cannot be smoothed using the entered values.");

      //now to make a copy of the yValues
      //  the algorithm uses the copy to determine the y values to the left
      //  and right of the point that is being smoothed.  Then, when the 
      //  smoothed value is determined, the value is changed in yValues, not 
      //  copyOfYValues.  Thus, the value of smoothed y value is not dependant 
      //  on the smoothing of previous points.
      float[] copyOfYValues = new float[yValues.length];
      System.arraycopy(yValues,0,copyOfYValues,0,copyOfYValues.length);

      for (int i=startingIndex; i<=endingIndex; i++)
         yValues[i] = getQuickSmoothedValue(coeffMatrix, 
                       copyOfYValues, i, nL, nR, M);
   }
   
   /**
    * Calculates all of the "c" coefficients used to smooth the data.  The 
    * "c" coefficients are used to determine the coefficients of the 
    * smoothing polynomial.
    * <p>
    * Basically (a of i) = (c of -nL)*f(-nL)+(c of -nL+1)*f(-nL+1)
    * + . . . . +(c of nR)*f(nR) where (a of i) is the coefficient 
    * from the smoothing polynomial that is multiplied by x^i.
    * <p>
    * After calculating the "c" coefficients this method places them correctly 
    * in the matrix (ie 2x2 matrix) {@link #coeffMatrix coeffMatrix}.
    */
   private void calculateCoefficientMatrix()
   {
      int nL = getNumLeft();
      int nR = getNumRight();
      int M = getPolynomialDegree();
      
      float[][] B = calculateInverseOfATransposeA(nL,nR,M);
      
      if (B != null)
      {
         float[][] coefArr = new float[M+1][nL+nR+1];
         for (int row=0; row<=M; row++)
            for (int n=-nL; n<=nR; n++)
               coefArr[row][n+nL] = get_c_CoefficientAt(n,row,B,nL,nR,M);
         coeffMatrix = coefArr;
         return;
      }
      else
         coeffMatrix = null; //because the ATransposeDotA is not invertable
   }
   
   /**
    * Get the smoothed value for the y value at the index <code>yIndex</code>.
    * @param cArr     The array returned from the method 
    *                 {@link #calculateCoefficientMatrix() 
    *                 calculateCoefficientMatrix()}.
    * @param yValues  The array of y values that are used calculate the 
    *                 smoothed value.
    * @param yIndex   The index of the y value from <code>yValues</code> that 
    *                 is to be smoothed.
    * @param nL       The number of points to the left of the point being 
    *                 processed that are used to approximate the point 
    *                 being processed.
    * @param nR       The number of points to the right of the point being 
    *                 processed that are used to approximate the pont 
    *                 being processed.
    * @param M        The order of the polynomial used to approximate the 
    *                 data.  This is also equal to the highest moment that 
    *                 is conserved.
    * @return         The smoothed value.
    */
   private static float getQuickSmoothedValue(float[][] cArr, float[] yValues, 
                                              int yIndex, int nL, int nR, int M)
   {
      boolean leftCompensate = false;
      boolean rightCompensate = false;
      
      if (nL > yIndex)
      {
         leftCompensate = true;
         //yIndex = nL;
      }
      else if (nR > yValues.length-1-yIndex)
      {
         rightCompensate = true;
         //yIndex = yValues.length-1-nR;
      }
      
      if (leftCompensate)
      {
         //This is a quick fix.  If the point is too close to the 
         //  edge just return the actual value.
         return yValues[yIndex];
         //return calculateValueOnSmoothedPolynomial(cArr,yValues,
         //                                          yIndex,nL,nR,M,-nL);
      }
      else if (rightCompensate)
      {
         //This is a quick fix.  If the point is too close to the edge 
         //just return the actual value.
         return yValues[yIndex];
         //return calculateValueOnSmoothedPolynomial(cArr,yValues,
         //  yIndex,nL,nR,M,nR);
      }
      else
      {
         float sum = 0;
         for (int n = -nL; n<=nR; n++)
            sum += cArr[0][n+nL]*yValues[yIndex+n];
         return sum;
      }
   }
   
   private static float calculateValueOnSmoothedPolynomial(float[][] coeff, 
         float[] yValues, int yIndex, int nL, int nR, int M, float value)
   {
      float[] aArr = new float[M+1];
      for (int row=0; row<=M; row++)
         aArr[row] = calculateValueForA(coeff,yValues,yIndex,nL,nR,M,row);
      
      float sum = 0;
      for (int i=0; i<=M; i++)
         sum += aArr[0]*Math.pow(value,i);
      return sum;
   }
   
   private static float calculateValueForA(float[][] coeff, float[] yValues, 
         int yIndex, int nL, int nR, int M, int row)
   {
      float sum = 0;
      for (int column=0; column<=M; column++)
         sum += coeff[row][column]*yValues[yIndex-nL+column];
      return sum;
   }
   
   /**
    * Get the nth "c" coefficient used to smooth the data.  The "c" 
    * coefficients are used to determine the coefficients in the 
    * polynomial of best fit.
    * <p>
    * Basically (a of i) = (c of -nL)*f(-nL)+(c of -nL+1)*f(-nL+1)
    * + . . . . +(c of nR)*f(nR) where (a of i) is the coefficient 
    * from the smoothing polynomial that is multiplied by x^i.
    * @param n     The number of the desired "c" coefficient. (the 
    *              first coefficient is found at n=-nL and the last 
    *              coefficient is found at n=nR).
    * @param a_num If <code>a_num=i</code> the "c" coefficent returned 
    *              is the nth coefficient used to determine ai (the 
    *              coefficient from the smoothing polynomial that is 
    *              multiplied by x^i).
    * @param B     The matrix returned by {@link 
    *              #calculateInverseOfATransposeA(int, int, int) 
    *              calculateInverseOfATransposeDotA(int nL, int nR, int M)}.
    * @param nL    The number of points to the left of the point being 
    *              processed that are used to approximate the point 
    *              being processed.
    * @param nR    The number of points to the right of the point being 
    *              processed that are used to approximate the pont 
    *              being processed.
    * @param M     The order of the polynomial used to approximate the 
    *              data.  This is also equal to the highest moment that 
    *              is conserved.
    * @return      The nth coefficient.
    */
   private static float get_c_CoefficientAt(int n, int a_num, float[][] B, 
                                            int nL, int nR, int M)
   {
      float sum = 0;
      for (int m=0; m<=M; m++)
         sum += B[a_num][m]*Math.pow(n,m);
      return sum;
   }
   
   /**
    * Determines the matrix that would result by taking the inverse of 
    * th matrix that would result by taking the product 
    * of transpose of the matrix "A" with the matrix "A".
    * <p>
    * The matrix "A" is defined such that the ith row and jth column in 
    * the matrix has the value i^j and is used to calculate the 
    * coefficients of the polynomial of best fit.
    * @param nL The number of points to the left of the point being 
    *           processed that are used to approximate the point 
    *           being processed.
    * @param nR The number of points to the right of the point being 
    *           processed that are used to approximate the pont 
    *           being processed.
    * @param M  The order of the polynomial used to approximate the 
    *           data.  This is also equal to the highest moment that 
    *           is conserved.
    * @return   ((Transpose of A)(A))^(-1)
    */
   private static float[][] calculateInverseOfATransposeA(int nL, int nR, int M)
   {
      //first to create the matrix
      float[][] A = new float[nL+nR+1][M+1];
      for (int row=-nL; row<=nR; row++)
         for (int col=0; col<=M; col++)
            A[row+nL][col] = (float)Math.pow(row,col);
      
      //this is A Transpose Dot A (A^T.A)
      float[][] atda = calculateATransposeA(A,nL,nR,M);
      
      //now to get the inverse
      return LinearAlgebra.getInverse(atda);
   }
   
   /**
    * Determines the matrix that would result by taking the product 
    * of transpose of the matrix "A" with the matrix "A".  The 
    * matrix "A" is defined such that the ith row and jth column 
    * in the matrix has the value i^j and is used to calculate the 
    * coefficients of the polynomial of best fit.
    * @param A  The matrix used in the computation.
    * @param nL The number of points to the left of the point being 
    *           processed that are used to approximate the point 
    *           being processed.
    * @param nR The number of points to the right of the point being 
    *           processed that are used to approximate the pont 
    *           being processed.
    * @param M  The order of the polynomial used to approximate the 
    *           data.  This is also equal to the highest moment that 
    *           is conserved.
    * @return   (Transpose of A)(A)
    */
   private static float[][] calculateATransposeA(float[][] A, 
                                                 int nL, int nR, int M)
   {
      float[][] AT = LinearAlgebra.getTranspose(A);
      
      float[][] result = new float[M+1][M+1];
      
      for (int i=0; i<=M; i++)
         for (int j=0; j<=M; j++)
            result[i][j] = calculateATransposeA_at_ij(i,j, nL, nR);
      
      return result;
   }
   
   /**
    * Calculates the element in the ith row and jth column in the matrix 
    * that would result by taking the product of transpose of the 
    * matrix "A" with the matrix "A."  The matrix "A" is defined such that 
    * the ith row and jth column in the matrix has the value i^j and is 
    * used to calculate the coefficients of the polynomial of best fit.
    * @param i The row number (starting at 0).
    * @param j The column number (starting at 0).
    * @param nL The number of points to the left of the point being 
    *           processed that are used to approximate the point 
    *           being processed.
    * @param nR The number of points to the right of the point being 
    *           processed that are used to approximate the pont 
    *           being processed.
    * @return   The value at row i and column j.
    */
   private static float calculateATransposeA_at_ij(int i, int j, int nL, int nR)
   {
      //the following equation describes how the equation 
      //used to compute Aij was derived.
      /*
       *             n            n
       *              R            R
       *            ----         ----
       *   T        \            \    i+j
       * {A . A}  =  |  A  A   =  |  k
       *        ij  /    ki kj   /
       *            ----         ----
       *           k=-n         k=-n
       *               L            L
       */
      int iPlusJ = i+j;
      float sum = 0;
      for (int k=-nL; k<=nR; k++)
         sum += Math.pow(k,iPlusJ);
      return sum;
   }
}
