/*
 * File: UniformContours.java
 *
 * Copyright (C) 2005, Dominic Kramer
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
 * Primary   Dominic Kramer <kramerd@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
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
 * Modified:
 * $Log$
 * Revision 1.5  2005/06/15 14:23:19  kramer
 * Fixed a malformed @see javadoc tag.
 *
 * Revision 1.4  2005/06/08 22:07:47  kramer
 *
 * Made the error messages for user friendly.
 *
 * Revision 1.3  2005/06/08 17:24:52  kramer
 *
 * Added the GNU header and javadocs.  Also, the methods getMin() and
 * getMax() were changed to getLowestLevel() and getHighestLevel().  Next,
 * the constructor now throws an exception if 'numLevels' is less than 2.
 * The isLevelCovered() method (which is used to see if a given level is
 * an element of this uniform progression of contour levels) has been added.
 *
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

/**
 * Represents a set of uniformly spaced contour levels.
 */
public class UniformContours extends Contours
{
   /** The minimum "elevation" of the contour levels. */
   private float minValue;
   /** The distance between successive contour levels. */
   private float delta;
   
   /**
    * Create a set of contours levels where the lowest contour level has an 
    * "elevation" of <code>minValue</code>, the highest contour level has an 
    * "elevation" of <code>maxValue</code>, and there are a total of 
    * <code>numLevels</code> contour levels.
    * 
    * @param minValue  The "elevation" of the lowest contour level.
    * @param maxValue  The "elevation" of the hightest contour level.
    * @param numLevels The number of contour levels in the set.  Note:  To 
    *                  calculate the contour levels, there needs to be at 
    *                  least 2 contour levels.
    * @throws IllegalArgumentException If <code>minValue>maxValue</code> or 
    *                                  <code>numLevels<2</code>.
    */
   public UniformContours(float minValue,
                          float maxValue,
                          int numLevels) throws IllegalArgumentException
   {
      super(numLevels);
      
      if (minValue>maxValue)
         throw new IllegalArgumentException(
                   "The value of the lowest contour level cannot be " +
                   "greater than the level of the highest contour level.");
      if (numLevels<2)
         throw new IllegalArgumentException(
                   "The number of levels must be greater than or equal to " +
                   "2.  However, the number of levels given was "+numLevels);
      
      this.minValue = minValue;
      this.delta = (maxValue-minValue)/(getNumLevels()-1);
   }
   
   /**
    * Get the "elevation" of the <code>ith</code> contour level.
    * 
    * @param i The index referencing the contour level whose "elevation" is 
    *          to be returned.  This should be in the range 
    *          [0,{@link Contours#getNumLevels() Contours.getNumLevels()}).
    * @see Contours#getLevelAt(int)
    */
   public float getLevelAt(int i)
   {
      return minValue+i*delta;
   }
   
   /**
    * Get the "elevation" of the lowest contour level.
    * 
    * @return The value of the lowest contour level.
    */
   public float getLowestLevel()
   {
      return minValue;
   }
   
   /**
    * Get the "elevation" of the highest contour level.
    * 
    * @return The value of the highest contour level.
    */
   public float getHighestLevel()
   {
      return getLevelAt(getNumLevels());
   }
   
   /**
    * Used to determine if the given "elevation" is represented as a 
    * contour level in this set.
    * 
    * @param value The "elevation" of the contour level in question.
    * 
    * @return True if at least one of the contour levels in this set has an 
    *         "elevation" of <code>value</code> and false otherwise.
    */
   public boolean isLevelCovered(float value)
   {
      //if 'value' is the value of some level then
      //  minValue + i*delta = value
      //should be true for some integer value for 'i'
      float i = (value - minValue)/delta;
      if ( (i-(int)i)==0 )
      {
         //now check if 'i' is in the correct range
         if (i>=0 && i<getNumLevels())
            return true;
         else
            return false;
      }
      else
         return false;
   }
}
