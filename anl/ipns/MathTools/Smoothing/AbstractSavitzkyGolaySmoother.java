/*
 * File:  AbstractSavitzkyGolaySmoother.java
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
 * Revision 1.1  2004/11/03 23:51:59  kramer
 * This is the abstract superclass that has methods that are used to store
 * and return the general information needed to smooth data using the
 * Savitzky-Golay smoothing algorithm.
 *
 */
package gov.anl.ipns.MathTools.Smoothing;

/**
 * Abstract superclass used to hold and verify the general information 
 * needed to smooth data using the Savitzky-Golay smoothing algorithm.
 * <ul>
 * <li>
 *   This algorithm smooths each point of data by using the following steps.
 * </li>
 *   <ol>
 *     <li>
 *       Use the point and a specified number of points to the left 
 *       and right of the point to generate a set of sample data.
 *     </li>
 *     <li>
 *       Construct a polynomial of a specified degree of best fit that fits 
 *       the data in the set of sample data.
 *     </li>
 *     <li>
 *       Use the function value of this polynomial of the point in question as 
 *       the smoothed value.
 *     </li>
 *   </ol>
 * </ul>
 * This class does not actually implement the smoothing algorithm.  This is 
 * because the algorithm can be modified to make it more efficient for special 
 * types of data.  Thus, this class's subclasses should be used to do the 
 * algorithm's actual work.
 * @author Dominic Kramer
 * @see gov.anl.ipns.MathTools.Smoothing.LeastSquaresSavitzkyGolaySmoother
 * @see gov.anl.ipns.MathTools.Smoothing.QuickSavitzkyGolaySmoother
 */
public abstract class AbstractSavitzkyGolaySmoother
{
   /**
    * The number of points to the left of the point in question used to 
    * smooth the point in question.
    */
   private int numLeft;
   /**
    * The number of points to the right of the point in question used to 
    * smooth the point in question.
    */
   private int numRight;
   /**
    * The degree of the polynomial of best fit that is used to smooth the data.
    */
   private int polynomialDegree;
   
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
   public AbstractSavitzkyGolaySmoother(int numLeft, 
                                        int numRight, 
                                        int polynomialDegree)
   {
      this.numLeft = numLeft;
      this.numRight = numRight;
      this.polynomialDegree = polynomialDegree;
      
      //first to check the validity of the parameters entered
      IllegalArgumentException e1 = verifyParameters(numLeft,
                                                     numRight,
                                                     polynomialDegree);
      if (e1 != null)
         throw e1;
   }
   
   /**
    * Get the number of points to the left of the point question used to 
    * smooth the point in question.
    * @return The number of points to the left of the point question used to 
    *         smooth the point in question.
    */
   public int getNumLeft() { return numLeft; }
   /**
    * Set the number of points to the left of the point question used to 
    * smooth the point in question.
    * @param numLeft The number of points to the left of the point question 
    *                used to smooth the point in question.
    */
   public void setNumLeft(int numLeft) { this.numLeft = numLeft; }
   
   /**
    * Get the number of points to the right of the point question used to 
    * smooth the point in question.
    * @return The number of points to the right of the point question used to 
    *         smooth the point in question.
    */
   public int getNumRight() { return numRight; }
   /**
    * Set the number of points to the right of the point question used to 
    * smooth the point in question.
    * @param numRight The number of points to the right of the point question 
    *                 used to smooth the point in question.
    */
   public void setNumRight(int numRight) { this.numRight = numRight; }
   
   /**
    * Get the degree of the polynomial of best fit that is used to smooth 
    * the data.
    * @return The degree of the smoothing polynomial used to smooth the data.
    */
   public int getPolynomialDegree() { return polynomialDegree; }
   /**
    * Set the degree of the polynomial of best fit that is used to smooth 
    * the data.
    * @param degree  The degree of the smoothing polynomial used to smooth 
    *                the data.
    */
   public void setPolynomialDegree(int degree) { polynomialDegree = degree; }
   
   /**
    * Used to determine the general validity of the parameters specified.  
    * This method only tests if any of the parameters are negative.
    * @param  numLeft                  The number of points to the left of 
    *                                  the point in question that are used to 
    *                                  smooth the point in question.
    * @param  numRight                 The number of points to the right of 
    *                                  the point in question that are used to 
    *                                  smooth the point in question.
    * @param  polynomialDegree         The degree of the smoothing polynomial 
    *                                  used to smooth the data.
    * @return                          This returns an 
    *                                  <code>IllegalArgumentException</code> 
    *                                  if any of parameters are negative.  
    *                                  Otherwise, if the parameters are valid 
    *                                  <code>null</code> is returned.
    */
   protected static IllegalArgumentException verifyParameters(
                                                          int numLeft, 
                                                          int numRight, 
                                                          int polynomialDegree)
   {
      if (numLeft<0)
         return new IllegalArgumentException(
            "Error:  The number of points left of the center cannot be " +
               "negative");
      if (numRight<0)
         return new IllegalArgumentException(
            "Error:  The number of points right of the center cannot be " +
               "negative");
      if (polynomialDegree<0)
         return new IllegalArgumentException(
            "Error:  The degree of the smoothing polynomial cannot be " + 
               "negative");
      
      return null;
   }
   
   /**
    * Determines if the parameters that determine which function values to 
    * smooth are valid.  If this method returns <code>null</code> no general 
    * errors exist with the parameters.  Then, the array <code>yValues</code> 
    * is known to not be <code>null</code>.  Also, the values in the array at 
    * indices <code>startingIndex</code> and <code>endingIndex</code> can 
    * be directly accessed without an <code>IndexOutOfBoundsException</code> 
    * being thrown.
    * @param yValues        The array of function values that are going to be 
    *                       smoothed.
    * @param startingIndex  The index, in the array <code>yValues</code>, of 
    *                       the first function value that will be smoothed.
    * @param endingIndex    The index, in the array <code>yValues</code>, of 
    *                       the last function value that will be smoothed.
    * @return               An <code>IllegalArgumentException</code> if any of 
    *                       the parameters are invalid or <code>null</code> if 
    *                       the parameters can be safely used.
    */
   protected static IllegalArgumentException verifyParameters(float[] yValues, 
                                                    int startingIndex, 
                                                    int endingIndex)
   {
      if (yValues==null)
         throw new IllegalArgumentException(
           "Error:  The reference to the array of y values to smooth cannot "+
              "be null");
     
     if (endingIndex<0)
        throw new IllegalArgumentException(
           "Error:  The index to stop smoothing cannot be negative");
     if (startingIndex<0)
        throw new IllegalArgumentException(
           "Error:  The index to start smoothing cannot be negative");
     if (endingIndex<startingIndex)
        throw new IllegalArgumentException(
           "Error:  The index to start smoothing cannot be greater than " +
              "index to stop smoothing");
     //if execution reaches this point, it is known that 
     //  startingIndex<endingIndex
     //  and startingIndex and endingIndex are non-negative
     if (startingIndex>=yValues.length)
        throw new IllegalArgumentException(
           "Error:  The index to start smoothing cannot be greater than " +
              "or equal to the length of the array of y values to smooth");
     if (endingIndex>=yValues.length)
        throw new IllegalArgumentException(
           "Error:  The index to end smoothing cannot be greater than " +
              "or equal to the length of the array of y values to smooth");
     
     return null;
   }
}
