/*
 * File: NonUniformContours.java
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
 * Revision 1.6  2005/06/28 16:20:00  kramer
 * Implemented the methods getHighestLevel() and getLowestLevel().
 *
 * Revision 1.5  2005/06/15 14:26:05  kramer
 *
 * Fixed a @param javadoc tag that didn't state the name of the parameter.
 *
 * Revision 1.4  2005/06/08 22:09:37  kramer
 *
 * Made the error messages encapsulated in IllegalArgumentExceptions thrown
 * by the constructor more user friendly.
 *
 * Revision 1.3  2005/06/08 17:22:02  kramer
 *
 * Added the GNU header, added javadocs, and modified the constructor so
 * that if the array given to it is null, it calls super(1) (so that the
 * superclass doesn't complain, instead this class throws an Exception).
 *
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

/**
 * Represents a set of contour levels of any size and arrangement.
 */
public class NonUniformContours extends Contours
{
   /** Holds the value of each contour level. */
   private float[] levels;
   
   /**
    * Create a set of any number and arrangement of contour levels.
    * 
    * @param levels The values of the contour levels.
    * @throws IllegalArgumentException If <code>levels</code> is 
    *                                  <code>null</code> or if 
    *                                  <code>levels.length==0</code>.
    */
   public NonUniformContours(float[] levels) throws IllegalArgumentException
   {
      super((levels!=null)?levels.length:1);
      if (levels==null)
         throw new IllegalArgumentException("No contours levels have been " +
                                            "manually entered");
      this.levels = levels;
   }

   /**
    * Get the value of the <code>ith</code> contour level.
    * 
    * @param i The index of the contour level to reference.  For <code>i</code> 
    *          to be valid, it must be in the range 
    *          [0,{@link Contours#getNumLevels() Contours.getNumLevels()}).
    * @return The value of the <code>ith</code> contour level or 
    *         {@link Float#NaN Float.NaN} if <code>i</code> is invalid.
    * @see Contours#getLevelAt(int)
    */
   public float getLevelAt(int i)
   {
      if (i>=0 && i<levels.length)
         return levels[i];
      else
         return Float.NaN;
   }
   
   /**
    * Used to get a reference to all of the contour levels.  Note:  The array 
    * returned is not sorted in any way.
    * 
    * @return All of the contour levels in this set.
    */
   public float[] getLevels()
   {
      return levels;
   }
   
   /**
    * Get the "elevation" of the lowest contour level.
    * 
    * @return The value of the lowest contour level.
    */
   public float getLowestLevel()
   {
      float min = Float.MAX_VALUE;
      for (int i=0; i<levels.length; i++)
         if (levels[i]<min)
            min = levels[i];
      return min;
   }
   
   /**
    * Get the "elevation" of the highest contour level.
    * 
    * @return The value of the highest contour level.
    */
   public float getHighestLevel()
   {
      float max = Float.MIN_VALUE;
      for (int i=0; i<levels.length; i++)
         if (levels[i]>max)
            max = levels[i];
      return max;
   }
}
