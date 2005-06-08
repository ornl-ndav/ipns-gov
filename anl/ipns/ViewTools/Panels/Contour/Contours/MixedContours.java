/*
 * File: MixedContours.java
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
 * Revision 1.1  2005/06/08 17:27:18  kramer
 * This class represents a union of UniformContours and NonUniformContours
 * in that it holds a collection of uniformly spaced contour levels in
 * addition to a collection of manually specified contour levels.
 *
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

/**
 * Represents a collection of uniformly spaced contour levels unioned with 
 * a collection of manually specified contour levels.
 */
public class MixedContours extends Contours
{
   /** Holds all of the uniformly spaced contour levels. */
   private UniformContours uniformLevels;
   /** Holds all of the manually specified contour levels. */
   private NonUniformContours extraLevels;
   
   /**
    * Create a set of contours that are only uniformly spaced.
    * 
    * @param minValue  The "elevation" of the lowest contour level.
    * @param maxValue  The "elevation" of the hightest contour level.
    * @param numLevels The number of contour levels in the set.  Note:  To 
    *                  calculate the contour levels, there needs to be at 
    *                  least 2 contour levels.
    * @throws IllegalArgumentException If <code>minValue>maxValue</code> or 
    *                                  <code>numLevels<2</code>.
    */
   public MixedContours(float minValue, float maxValue, int numLevels)
   {
      super(numLevels);
      this.uniformLevels = new UniformContours(minValue,maxValue,numLevels);
      this.extraLevels = null;
   }
   
   /**
    * Create a collection of uniformly spaced and manually entered contour 
    * levels.
    * 
    * @param minValue  The "elevation" of the lowest contour level.
    * @param maxValue  The "elevation" of the hightest contour level.
    * @param numLevels The number of contour levels in the set.  Note:  To 
    *                  calculate the contour levels, there needs to be at 
    *                  least 2 contour levels.
    * @param levels    The values of the contour levels.
    */
   public MixedContours(float minValue, float maxValue, int numLevels, 
                        float[] levels)
   {
      //initially start with 'numLevels' number of levels which 
      //correspond to the uniformly spaced contour levels.  Next, the 
      //manually specified levels will be checked, counted, and added to 
      //the number of levels.
      super(numLevels);
      this.uniformLevels = new UniformContours(minValue,maxValue,numLevels);
      
      if (levels!=null)
      {
         //find all of the levels that are not represented as a uniformly 
         //spaced contour level
         float[] realLevels = new float[levels.length];
         int numOk = 0;
         for (int i=0; i<levels.length; i++)
            if (!this.uniformLevels.isLevelCovered(levels[i]))
               realLevels[numOk++] = levels[i];
            
         //if the array can be "trimmed" trim it
         float[] newArray;
         if (numOk<realLevels.length)
         {
            newArray = new float[numOk];
            System.arraycopy(realLevels,0,newArray,0,numOk);
         }
         else
            newArray = realLevels;
         
         //now make the NonUniformContours object to represent these levels
         if (newArray.length>0)
            this.extraLevels = new NonUniformContours(newArray);
         else
            this.extraLevels = null;
         
         //now to set the new number of levels
         setNumLevels(newArray.length+numLevels);
      }
      else
         throw new IllegalArgumentException(
                      "Error:  MixedContours(float minValue, float maxValue, "+
                      "int numLevels, float[] levels) was given 'levels'=null");
   }
   
   /**
    * Create a collection of only manually specified contour levels.
    * 
    * @param levels The values of the contour levels.
    * @throws IllegalArgumentException If <code>levels</code> is 
    *                                  <code>null</code> or if 
    *                                  <code>levels.length==0</code>.
    */
   public MixedContours(float[] levels)
   {
      super((levels!=null)?levels.length:1);
      this.uniformLevels = null;
      this.extraLevels = new NonUniformContours(levels);
   }
   
   /**
    * Get the <code>ith</code> contour level.
    * 
    * @param i The index of the contour level to reference.  For 
    *          <code>i</code> to be valid it must be in the range 
    *          [0,{@link Contours#getNumLevels() Contours.getNumLevels()}).
    * 
    * @return The contour level referenced by the given index <code>i</code> 
    *         or {@link Float#NaN Float.NaN} if <code>i</code> is invalid.
    * 
    * @see Contours#getLevelAt(int)
    */
   public float getLevelAt(int i)
   {
      //first check if the index is valid
      if (i<0 || i>=getNumLevels())
         return Float.NaN;
      
      if (uniformLevels==null && extraLevels!=null)
         return extraLevels.getLevelAt(i);
      else if (uniformLevels!=null && extraLevels==null)
         return uniformLevels.getLevelAt(i);
      else if (uniformLevels!=null && extraLevels!=null)
      {
         if (i<uniformLevels.getNumLevels())
            return uniformLevels.getLevelAt(i);
         else
         {
            int newIndex = i-uniformLevels.getNumLevels();
            return extraLevels.getLevelAt(newIndex);
         }
      }
      else
         return Float.NaN;
   }
}
