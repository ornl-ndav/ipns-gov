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
 * Revision 1.2  2005/06/08 22:10:57  kramer
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
    * Creates a collection of uniformly spaced contour levels.
    * 
    * @param uniformLevels Represents the uniformly spaced contour levels.  
    *                      If this is <code>null</code>, the method 
    *                      {@link #getLevelAt(int) getLevelAt(int)} will 
    *                      always return {@link Float#NaN Float.NaN}.
    */
   public MixedContours(UniformContours uniformLevels)
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
   public MixedContours(UniformContours uniformLevels, 
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
   public MixedContours(UniformContours uniformLevels, 
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
   public MixedContours(NonUniformContours manualLevels)
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
}
