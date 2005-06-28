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
 * Revision 1.4  2005/06/28 16:18:46  kramer
 * Modified to use the new getLowestLevel() and getHighestLevel() methods
 * from the Contours class.  Also, the constructors have been modified so
 * that the only constructor is one that takes both a
 * UniformContours and NonUniformContours object.
 *
 * Revision 1.3  2005/06/22 22:27:52  kramer
 *
 * Rearranged the code so that the old code is in an inner class called
 * UnorderedContours.  Also, a new inner class called OrderedContours
 * was crated.  Now when an object of this type is created the option to
 * order the contour levels must be given and the corresponding inner class
 * will be used.
 *
 * Revision 1.2  2005/06/08 22:10:57  kramer
 *
 * Modified the constructors to only have UniformContours and/or
 * NonUniformContours objects as parameters and added new javadocs.
 *
 * Revision 1.1  2005/06/08 17:27:18  kramer
 *
 * This class represents a union of UniformContours and NonUniformContours
 * in that it holds a collection of uniformly spaced contour levels in
 * addition to a collection of manually specified contour levels.
 *
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

import java.util.Arrays;

/**
 * Represents a collection of uniformly spaced contour levels unioned with 
 * a collection of manually specified contour levels.
 */
public class MixedContours extends Contours
{
   /** Holds the mixed contour levels. */
   private Contours contours;
   
   /**
    * Creates a list of contour levels by unioning the two lists of 
    * contour levels supplied.
    * 
    * @param uniformContours Encasulates the uniformly spaced contour levels 
    *                        to put in the list.
    * @param nonuniformContours Encapsulates the nonuniformly spaced contour 
    *                           levels to put in the list.
    * @param order If true, the list of contour levels will be sorted in 
    *              order.  If false, no sorting will be done.
    * @param ignoreRepeats If true, the elements in the list could repeat.  
    *                      If false, every element in the list will be 
    *                      unique.
    */
   public MixedContours(UniformContours uniformContours, 
                        NonUniformContours nonuniformContours, 
                        boolean order, boolean ignoreRepeats)
   {
      //for now tell the superclass there is one contour level
      super(1);
      
      //create the appropriate set of contour levels
      if (order)
         contours = new OrderedContours(uniformContours, 
                                        nonuniformContours, 
                                        ignoreRepeats);
      else
         contours = new UnorderedContours(uniformContours, 
                                          nonuniformContours, 
                                          ignoreRepeats);
      
      //set the number of contour levels in this set
      setNumLevels(contours.getNumLevels());
   }
   
   /**
    * Used to get the <code>ith</code> contour level.
    * 
    * @param i The index of the contour level to retrieve.  For <code>i</code> 
    *          to be valid, it must be in the range 
    *          <code>[0,getNumLevels())</code>.
    */
   public float getLevelAt(int i)
   {
      return contours.getLevelAt(i);
   }
   
   /**
    * Get the "elevation" of the lowest contour level.
    * 
    * @return The value of the lowest contour level.
    */
   public float getLowestLevel()
   {
      return contours.getLowestLevel();
   }
   
   /**
    * Get the "elevation" of the highest contour level.
    * 
    * @return The value of the highest contour level.
    */
   public float getHighestLevel()
   {
      return contours.getHighestLevel();
   }
   
   /**
    * This class is used to fuse a <code>UniformContours</code> and 
    * <code>NonUniformContours</code> object together to form a set of 
    * nonuniform contour levels sorted in increasing order.
    */
   private class OrderedContours extends Contours
   {
      /** This is the union of the uniform and nonuniform contour levels. */
      private float[] levels;
      
      /**
       * Creates an OrderedContours object with the parameters as 
       * specified.  Notice:  <code>uniformContours</code> cannot be 
       * <code>null</code>.  However, <code>nonuniformContours</code> can 
       * be.  In this case, the levels will precisely be uniformly spaced 
       * contour levels.
       * 
       * @param uniformContours Encapsulates the uniformlly spaced contour 
       *                        levels.
       * @param nonuniformContours Encapsulates the nonuniformlly spaced 
       *                           contour levels.
       * @param ignoreRepeats Suppose the raw list of contour levels is 
       *                      {-1.2, 1.0, 2.0, 2.0, 3.5, 5}.  If this 
       *                      parameter is true then the list used is 
       *                      exactly this list.  If this parameter is 
       *                      false, the extra 2.0 will be ignored.  Thus, 
       *                      the list that would be used is 
       *                      {-1.2, 1.0, 2.0, 3.5, 5}.
       * @throws IllegalArgumentException  If <code>uniformContours</code> 
       *                                   is <code>null</code> or 
       *                                   if the total number of levels 
       *                                   in the 
       *                                   <code>UniformContours</code> and 
       *                                   <code>NonUniformContours</code>  
       *                                   is 0.
       */
      public OrderedContours(UniformContours uniformContours, 
                             NonUniformContours nonuniformContours, 
                             boolean ignoreRepeats)
      {
         //the number of contour levels will be set later
         //for now tell the superclass there is one level to make it happy
         super(1);
         
         //get the nonuniform contour levels
         float[] nonUniformLevels;
         if (nonuniformContours!=null)
            nonUniformLevels = nonuniformContours.getLevels();
         else
            nonUniformLevels = new float[0];
         
         //verify that the UniformContours specified is valid
         if (uniformContours==null)
            throw new IllegalArgumentException(
                         "error:  no values were specified to be used " +
                         "to construct the uniform contour levels");
         
         //create an array to store all of the levels
         levels = new float[uniformContours.getNumLevels()+
                               nonUniformLevels.length];
         if (levels.length==0)
            throw new IllegalArgumentException(
                         "error:  no data was entered to allow " +
                         "any contour levels to be plotteds");
         
         //copy the uniform contour levels into 'levels'
         for (int i=0; i<uniformContours.getNumLevels(); i++)
            levels[i] = uniformContours.getLevelAt(i);
         
         //now copy the array specifying the nonuniform contour levels into 
         //the array 'levels'
         System.arraycopy(nonUniformLevels, 0, 
                          levels, uniformContours.getNumLevels(), 
                          nonUniformLevels.length);
         
         //sort the levels
         Arrays.sort(levels);
         
         //if repeats are not supposed to be ignored, do some work to 
         //remove the repeats
         if (!ignoreRepeats)
         {
            //make a place to put the values that are unique
             float[] levelCopy = new float[levels.length];
            //the number of unique values
             int numUnique = 0;
            //the previous value used
             float prevVal = Float.NaN;
            for (int i=0; i<levels.length; i++)
            {
               //only include the current element in the list 
               //if it is different from the previous one
               if (levels[i]!=prevVal)
                  levelCopy[numUnique++] = levels[i];
               
               prevVal = levels[i];
            }
            
            //if there are less unique elements in the array then there 
            //are total elements in the array, some of the non-unique elements 
            //were removed.  Then pack the array because there are 'blanks' 
            //at the end of the array
            if (numUnique!=levels.length)
            {
               float[] packedArr = new float[numUnique];
               System.arraycopy(levelCopy, 0, packedArr, 0, packedArr.length);
               this.levels = packedArr;
            }
         }
         
         //set the number of contour levels
         setNumLevels(levels.length);
      }
      
      /**
       * Gets the specified contour level.
       * 
       * @param i The index of the contour level whose value is to be 
       *          retrieved.  For <code>i</code> to be valid it must be 
       *          in the range <code>[0,getNumLevels())</code>
       */
      public float getLevelAt(int i)
      {
         return levels[i];
      }
      
      /**
       * Get the "elevation" of the lowest contour level.
       * 
       * @return The value of the lowest contour level.
       */
      public float getLowestLevel()
      {
         //because the list of contours is ordered, the first element 
         //in the list is the smallest one
         return getLevelAt(0);
      }
      
      /**
       * Get the "elevation" of the highest contour level.
       * 
       * @return The value of the highest contour level.
       */
      public float getHighestLevel()
      {
         //because the list of contours is ordered, the last element 
         //in the list is the largest one
         return getLevelAt(getNumLevels()-1);
      }
   }
   
   /**
    * Represents a collection of uniformly spaced contour lines and 
    * manually specified contour lines such that the collection is 
    * not ordered.
    */
   private class UnorderedContours extends Contours
   {
      /** Holds all of the uniformly spaced contour levels. */
      private UniformContours uniformLevels;
      /** Holds all of the manually specified contour levels. */
      private NonUniformContours extraLevels;
   
      /**
       * Creates a collection of uniformly spaced contour levels.
       * 
       * @param uniformLevels Represents the uniformly spaced contour levels.  
       *                      If this is <code>null</code>, the method 
       *                      {@link #getLevelAt(int) getLevelAt(int)} will 
       *                      always return {@link Float#NaN Float.NaN}.
       */
      public UnorderedContours(UniformContours uniformLevels)
      {
         super((uniformLevels!=null)?uniformLevels.getNumLevels():1);
         this.uniformLevels = uniformLevels;
         this.extraLevels = null;
      }
   
      /**
       * Creates a collection of both manually entered and uniformly spaced 
       * contour levels where the collection does not contain any duplicate 
       * contour levels.
       * 
       * @param uniformLevels Represents the uniformly spaced contour levels.  
       *                      If this is <code>null</code>, this constructor 
       *                      is identical to the constructor 
       *                      {@link #MixedContours(NonUniformContours) 
       *                      MixedContours(NonUniformContours)}.
       * 
       * @param manualLevels Represents the manually entered contour levels.
       *                     If this is <code>null</code>, this constructor 
       *                     is identical to the constructor 
       *                     {@link #MixedContours(UniformContours) 
       *                     MixedContours(UniformContours)}.
       */
      public UnorderedContours(UniformContours uniformLevels, 
                               NonUniformContours manualLevels)
      {
         this(uniformLevels, manualLevels, false);
      }
   
      /**
       * Creates a collection of both manually entered and uniformly spaced 
       * contour levels.
       * 
       * @param uniformLevels Represents the uniformly spaced contour levels.
       *                      If this is <code>null</code>, this constructor 
       *                      is identical to the constructor 
       *                      {@link #MixedContours(NonUniformContours) 
       *                      MixedContours(NonUniformContours)}.
       * 
       * @param manualLevels Represents the manually entered contour levels.
       *                     If this is <code>null</code>, this constructor 
       *                     is identical to the constructor 
       *                     {@link #MixedContours(UniformContours) 
       *                     MixedContours(UniformContours)}.
       * 
       * @param ignoreRepeats If true, the collection of contour levels will 
       *                      not contain any duplicate levels.  If false, it 
       *                      may contain duplicates (in the case where the 
       *                      a manually entered level is the same as one of the 
       *                      uniformly spaced contour levels).
       */
      public UnorderedContours(UniformContours uniformLevels, 
                               NonUniformContours manualLevels, 
                               boolean ignoreRepeats)
      {
         //for now just tell the super class there is one level
         super(1);
         this.uniformLevels = uniformLevels;
         this.extraLevels = manualLevels;
      
         //records the number of levels that will be used
         int numLevels = 0;
         if (this.uniformLevels!=null)
         {
            //include the number of uniform contour levels
            numLevels += this.uniformLevels.getNumLevels();
         
            if (this.extraLevels!=null)
            {
               if (ignoreRepeats)
                  numLevels += this.extraLevels.getNumLevels();
               else
               {
                  //alias 'this.extraLevels' actual levels
                  float[] levels = this.extraLevels.getLevels();
            
                  //If one of the levels in 'extraLevels' 
                  //is already in 'uniformLevels' it is ignored.
                  //This stores the actual levels that will be used.
                  float[] actualLevels = new float[levels.length];
                  //find the unique levels
                  int numOk = 0;
                  for (int i=0; i<levels.length; i++)
                     if (!this.uniformLevels.isLevelCovered(levels[i]))
                        actualLevels[numOk++] = levels[i];
              
                  //change 'this.extraLevels' if it needs changing
                  if (numOk==0)
                     this.extraLevels = null;
                  else if (numOk<actualLevels.length)
                  {
                     System.arraycopy(actualLevels,0,actualLevels,0,numOk);
                     this.extraLevels = new NonUniformContours(actualLevels);
                  }
            
                  //now add the new contour levels to the count
                  numLevels += numOk;
               }
            }
         }
         if (numLevels>0)
            setNumLevels(numLevels);
      }
   
      /**
       * Creates a collection of contour levels that only uses manually 
       * specified contour levels.
       * 
       * @param manualLevels Represents the manually represented contour levels.
       *                     If this is <code>null</code>, the method 
       *                     {@link #getLevelAt(int) getLevelAt(int)} will 
       *                     always return {@link Float#NaN Float.NaN}.
       */
      public UnorderedContours(NonUniformContours manualLevels)
      {
         super((manualLevels!=null)?manualLevels.getNumLevels():1);
         this.uniformLevels = null;
         this.extraLevels = manualLevels;
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
   
      /**
       * Get the "elevation" of the lowest contour level.
       * 
       * @return The value of the lowest contour level.
       */
      public float getLowestLevel()
      {
         float min = Float.MAX_VALUE;
         float curVal;
         for (int i=0; i<getNumLevels(); i++)
            if ( (curVal=getLevelAt(i)) < min)
               min = curVal;
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
         float curVal;
         for (int i=0; i<getNumLevels(); i++)
            if ( (curVal=getLevelAt(i)) > max)
               max = curVal;
         return max;
      }
   }
}
