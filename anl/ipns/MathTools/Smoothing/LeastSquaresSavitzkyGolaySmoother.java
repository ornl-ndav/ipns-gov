/*
 * File:  LeastSquaresSavitzkyGolaySmoother.java
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
 * Revision 1.1  2004/11/03 23:58:32  kramer
 *
 * This class implements the Savitzky-Golay smoothing algorithm without
 * making any mathematical simplifications to the algorithm's equations.
 *
 */
package gov.anl.ipns.MathTools.Smoothing;

import gov.anl.ipns.MathTools.LinearAlgebra;

/**
 * This class implements the Savitzky-Golay smoothing algorithm without making 
 * any mathematical simplifications to the equations describing the 
 * algorithm.  The class 
 * {@link gov.anl.ipns.MathTools.Smoothing.QuickSavitzkyGolaySmoother 
 * QuickSavitzkyGolaySmoother} smooths data faster than this class.  However, 
 * the class 
 * {@link gov.anl.ipns.MathTools.Smoothing.QuickSavitzkyGolaySmoother 
 * QuickSavitzkyGolaySmoother} can only smooth data if the points are evenly 
 * spaced.  This class, however, can also smooth data if the points are not 
 * evenly spaced.
 * @author Dominic Kramer
 * @see gov.anl.ipns.MathTools.Smoothing.AbstractSavitzkyGolaySmoother
 */
public class LeastSquaresSavitzkyGolaySmoother
               extends AbstractSavitzkyGolaySmoother
{
   
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
    *                                  parameters is negative.  Depending on 
    *                                  how the algorithm is implemented, 
    *                                  further checks may have to be performed.
    */
   public LeastSquaresSavitzkyGolaySmoother(int numLeft, 
                                            int numRight, 
                                            int polynomialDegree)
   {
      super(numLeft,numRight,polynomialDegree);
      
      //now to perform some further checks on nL, nR, and M
      if (numRight+numLeft<polynomialDegree)
         throw new IllegalArgumentException(
           "Error:  The sum of the left and right points must be at least " +
              "equal to one less than the degree of the polynomial");
   }
   
   /**
    * Smooths the data in the array <code>yValues</code> between, and 
    * including, the indices <code>startingIndex</code> and 
    * <code>endingIndex</code>.  In addition to this method's parameters, the 
    * parameters passed to the constructor are used to smooth the data.  
    * <p>
    * Note:  The x values and y values passed to this method, which correspond 
    * to x and y values of the points to smooth, must correspond to a 
    * function.  In other words, for every point x value there must be exactly 
    * one y value corresponding to it.
    * @param yValues        The array of function values that are going to be 
    *                       smoothed.
    * @param xValues        The x values corresponding to the function values 
    *                       that are to be smoothed.
    * @param startingIndex  The index, in the array <code>yValues</code>, of 
    *                       the first function value that will be smoothed.
    * @param endingIndex    The index, in the array <code>yValues</code>, of 
    *                       the last function value that will be smoothed.
    */
   public void smooth(float[] yValues, float[] xValues, 
                      int startingIndex, int endingIndex)
   {
      int nL = getNumLeft();
      int nR = getNumRight();
      int M = getPolynomialDegree();
      
      if (xValues == null)
         throw new IllegalArgumentException(
           "Error:  The reference to the array of x values to smooth cannot "+
             "be null");
      if (xValues.length != yValues.length)
         throw new IllegalArgumentException(
           "Error:  There must be exactly one y value for every x value "+
             "used to smooth the data");
      
      IllegalArgumentException e2 = verifyParameters(yValues,
                                                     startingIndex,
                                                     endingIndex);
      if (e2 != null)
         throw e2;
      
      //if execution reaches this point, the parameters entered are
      //  legitimate values.
      
      //    now to make a copy of the yValues
      //  the algorithm uses the copy to determine the y values to the left
      //  and right of the point that is being smoothed.  Then, when the 
      //  smoothed value is determined, the value is changed in yValues, not 
      //  copyOfYValues.  Thus, the value of smoothed y value is not dependant 
      //  on the smoothing of previous points.
      float[] copyOfYValues = new float[yValues.length];
         System.arraycopy(yValues,0,copyOfYValues,0,copyOfYValues.length);
      
      float smoothedValue = 0;
      
         for (int yIndex=startingIndex; yIndex<=endingIndex; yIndex++)
         {
            smoothedValue = getSmoothedValue(xValues, copyOfYValues,
                                            yIndex,nL,nR,M);
            if (!Float.isNaN(smoothedValue))
               yValues[yIndex] = smoothedValue;
         }
   }
   
   /**
    * Get the matrix "B" used in solving for a polynomial of best fit.  The 
    * elements in "B" are the y values used to are to be fit by the polynomial.  
    * @param yValues The y values used in the data.
    * @param yIndex  The index from <code>yValues</code> that is to be smoothed.
    * @param nL     The number of points to the left of the point being 
    *               processed that are used to approximate the point 
    *               being processed.
    * @param nR     The number of points to the right of the point being 
    *               processed that are used to approximate the pont 
    *               being processed.
    * @return       The matrix "B" from the equation Aa=B
    */
   private static double[] getMatrixB(float[] yValues, int yIndex, 
                                      int nL, int nR)
   {
      double[] b = new double[nR+nL+1];
      
      for (int i=0; i<b.length; i++)
         b[i] = yValues[yIndex-nL+i];
      return b;
   }

   /**
    * Get the matrix "A" used in solving for a polynomial of best fit.
    * @param M       The degree of the polynomial of best fit.
    * @param xValues     The x values used in the data.
    * @param yValues The y values used in the data.
    * @param yIndex  The index from <code>yValues</code> that is to be smoothed.
    * @param nL      The number of points to the left of the point being 
    *                processed that are used to approximate the point 
    *                being processed.
    * @param nR      The number of points to the right of the point being 
    *                processed that are used to approximate the pont 
    *                being processed.
    * @return        The matrix "B" from the equation Aa=B
    */
   private static double[][] getMatrixA(float[] xValues, float[] yValues, 
                                        int yIndex, int nL, int nR, int M)
   {
      //the matrix is in the form [column][row]
      double[][] A = new double[nR+nL+1][M+1];
      
      //this holds the x value corresponding to the y value at 'yIndex'
      double currentX = Double.NaN;
      //now to have the index of the first y value be 'nL' indices back
      yIndex -= nL;
      
      for (int row=0; row<nR+nL+1; row++)
      {
        if (yIndex<0 || yIndex>=yValues.length || yIndex>=xValues.length)
           System.out.println("Invalid paramter (yIndex="+yIndex+
              ") in getMatrixA\n  Returning Float.NaN");
        else
        {
         currentX = xValues[yIndex];
         A[row][0] = 1;
         for (int column=1; column<(M+1); column++)
            A[row][column] = A[row][column-1]*currentX;
        }
        yIndex++;
      }
      
      return A;
   }
   
   /**
    * Calculate the value of the smoothed polynomial at the x value 
    * <code>x</code>.
    * @param x            The x value for which the y value of the smoothed 
    *                     value is calculated.
    * @param coefficients The coefficients of the smoothing polynomial.
    * @param length       The number of values from <code>coefficients</code> 
    *                     that actually are the coefficients from the 
    *                     smoothing algorithm.  
    *                     <p>
    *                     <code>coefficients[0]</code> should be the first 
    *                     coefficient (the coefficient paried with x^0).
    *                     <p>
    *                     <code>coefficients[length-1]</code> should be the 
    *                     last coefficient (the 
    *                     coefficient paried with x^M (where M is the degree 
    *                     of the polynomial)).
    * @return             The y value of the smoothed polynomial at the x 
    *                     value <code>x</code>.
    */
   private static double computeApproximateValueAt(float x, 
                                                   double[] coefficients, 
                                                   int length)
   {
      if (length >= 1)
      {
         double value    = coefficients[0];
         double currentX = x;
         for (int i=1; i<length; i++)
         {
            value += coefficients[i]*currentX;
            currentX *= x;
         }
         return value;
      }
      else
      {
         System.out.println("Invalid paramter in method " +
                            "computeApproximateValueAt()");
         System.out.println("  length="+length+" < 1");
         System.out.println("  Returning Float.NaN");
         return Float.NaN;
      }
   }
   
   /**
    * Get the smoothed value for the y value at the index <code>yIndex</code>.
    * @param xValues     The array of x values used to calculate the smoothed 
    *                    value.
    * @param yValues     The array of y values that are used calculate the 
    *                    smoothed value.
    * @param yIndex      The index of the y value from <code>yValues</code> that 
    *                    is to be smoothed.
    * @param nL          The number of points to the left of the point being 
    *                    processed that are used to approximate the point 
    *                    being processed.
    * @param nR          The number of points to the right of the point being 
    *                    processed that are used to approximate the pont 
    *                    being processed.
    * @param M           The order of the polynomial used to approximate the 
    *                    data.  This is also equal to the highest moment that 
    *                    is conserved.
    * @return            The smoothed value.
    */
   private static float getSmoothedValue(float[] xValues, float[] yValues, 
                                         int yIndex, int nL, int nR, int M)
   {
      boolean leftCompensate = false;
      boolean rightCompensate = false;
      
      if (nL > yIndex)
      {
         leftCompensate = true;
         yIndex = nL;
      }
      else if (nR > yValues.length-1-yIndex)
      {
         rightCompensate = true;
         yIndex = yValues.length-1-nR;
      }
      
      //now to constuct matrix 'A'
      double[][] A = getMatrixA(xValues, yValues, yIndex, nL, nR, M);

      //now to construct matrix 'b'
      double[]   b = getMatrixB(yValues, yIndex, nL, nR);
      
      double returnedVal = LinearAlgebra.solve(A,b);
          
      if (returnedVal == Double.NaN)
      {
         System.out.println("Invalid parameter in getSmoothedValue()");
         System.out.println("  LinearAlgebra.solve() returned "+returnedVal);
         System.out.println("  Returning Float.NaN");         
         return Float.NaN;
      }
      if (leftCompensate)
      {
        if ((yIndex-nL)<0 || (yIndex-nL)>=yValues.length || 
            (yIndex-nL)>=xValues.length)
        {
           System.out.println("Invalid paramter (y Index="+yIndex+
                 ") in getSmoothedValue\n  Returning Float.NaN");
           return Float.NaN;
        }
        else
          return (float)computeApproximateValueAt(xValues[yIndex-nL],b,M+1);
      }
      else if (rightCompensate)
      {
        if ((yIndex+nR)<0 || (yIndex+nR)>=yValues.length || 
            (yIndex+nR)>=xValues.length)
        {
           System.out.println("Invalid paramter (y Index="+yIndex+
                 ") in getSmoothedValue\n  Returning Float.NaN");
           return Float.NaN;
        }
        else
          return (float)computeApproximateValueAt(xValues[yIndex+nR],b,M+1);
      }
      else
      {
        if (yIndex<0 || yIndex>=yValues.length || yIndex>=xValues.length)
        {
           System.out.println("Invalid paramter (y Index="+yIndex+
                 ") in getSmoothedValue\n  Returning Float.NaN");
           return Float.NaN;
        }
          return (float)computeApproximateValueAt(xValues[yIndex],b,M+1);
      }
   }
}
