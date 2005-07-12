/*
 * File: UnorderedContours.java
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
 * Revision 1.1  2005/07/12 16:26:00  kramer
 * Initial checkin.  This class represents a collection of uniformly
 * spaced and non-uniformly spaced contour levels that are not necessarily
 * in ascending order.  If ordering is not important, this class should be
 * used because it is more memory efficient than the OrderedContours class
 * for example.
 *
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

import gov.anl.ipns.ViewTools.Components.ObjectState;

/**
 * Represents a collection of uniformly spaced contour lines and 
 * manually specified contour lines such that the collection is 
 * not ordered.
 */
public class UnorderedContours extends MixedContours
{
//--------------------------=[ ObjectState keys ]=----------------------------//
   /**
    * "Uniform levels" - This static constant String is a key used for 
    * referencing the <code>UniformContours</code> in this collection of 
    * contour levels.  The value that this key references is a 
    * <code>UniformContours</code> object.
    */
   public static final String UNIFORM_LEVELS_KEY = "Uniform levels";
   /**
    * "Nonuniform levels" - This static constant String is a key used for 
    * referencing the <code>NonUniformContours</code> in this collection of 
    * contour levels.  The value that this key references is a 
    * <code>NonUniformContours</code> object.
    */
   public static final String NON_UNIFORM_LEVELS_KEY = "Nonuniform levels";
//------------------------=[ End ObjectState keys ]=--------------------------//
   
//------------------------=[ Default field values ]=--------------------------//
   /** The default uniform contours encapsulated by this class. */
   public static final UniformContours DEFAULT_UNIFORM_LEVELS = 
      new UniformContours();
   /** The default nonuniform contours encapsulated by this class. */
   public static final NonUniformContours DEFAULT_NON_UNIFORM_LEVELS = 
      new NonUniformContours();
//----------------------=[ End default field values ]=------------------------//
   
//------------------------------=[ Fields ]=----------------------------------//
   /** Holds all of the uniformly spaced contour levels. */
   private UniformContours uniformLevels;
   /** Holds all of the manually specified contour levels. */
   private NonUniformContours extraLevels;
//----------------------------=[ End fields ]=--------------------------------//
   
//---------------------------=[ Constructors ]=-------------------------------//
   /**
    * Creates a default collection of uniformly spaced and nonuniformly 
    * spaced contour levels where the default constructors of the 
    * <code>UniformContours</code> and <code>NonUniformContours</code> 
    * classes are used to create the contours.
    */
   public UnorderedContours()
   {
      this(new UniformContours(), new NonUniformContours());
   }
   
   /**
    * Creates a collection of both manually entered and uniformly spaced 
    * contour levels where the collection does not contain any duplicate 
    * contour levels.
    * 
    * @param uniformLevels Represents the uniformly spaced contour levels.  
    *                      If this is <code>null</code> it is ignored.
    * 
    * @param manualLevels Represents the manually entered contour levels.
    *                     If this is <code>null</code> it is ignored.
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
    *                      If this is <code>null</code> it is ignored.
    * 
    * @param manualLevels Represents the manually entered contour levels.
    *                     If this is <code>null</code> it is ignored.
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
      super(1, false, ignoreRepeats);
      setLevels(uniformLevels, manualLevels, ignoreRepeats);
   }
//-------------------------=[ End constructors ]=-----------------------------//
   
//-----------------=[ Constructor convenience methods ]=----------------------//
   /**
    * Used to take the collections of contour levels, create a union of the 
    * contour levels, and store the collection in <code>this.levels</code>.
    * 
    * @param uniformLevels The collection of uniformly spaced contour levels 
    *                      to include.
    * @param manualLevels The collection of nonuniformly spaced contour 
    *                     levels to include.
    * @param ignoreRepeats If true, the collection of contour levels will 
    *                      not contain any duplicate levels.  If false, it 
    *                      may contain duplicates (in the case where the 
    *                      a manually entered level is the same as one of the 
    *                      uniformly spaced contour levels).
    */
   private void setLevels(UniformContours uniformLevels, 
                          NonUniformContours manualLevels, 
                          boolean ignoreRepeats)
   {
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
//---------------=[ End constructor convenience methods ]=--------------------//

//-------------------------=[ Overriden methods ]=----------------------------//
   /**
    * Used to get the state information for this collection of contour 
    * levels.
    * 
    * @param isDefault If true the default state for this collection of 
    *                  contour levels is returned.  If false the current 
    *                  state is returned.
    * @return An encapsulation of the state of this collection of contour 
    *         levels.
    */
   public ObjectState getObjectState(boolean isDefault)
   {
      ObjectState state = super.getObjectState(isDefault);
      if (isDefault)
      {
         state.insert(UNIFORM_LEVELS_KEY, DEFAULT_UNIFORM_LEVELS);
         state.insert(NON_UNIFORM_LEVELS_KEY, DEFAULT_NON_UNIFORM_LEVELS);
      }
      else
      {
        state.insert(UNIFORM_LEVELS_KEY, uniformLevels.getCopy());
        state.insert(NON_UNIFORM_LEVELS_KEY, extraLevels.getCopy());
      }
      return state;
   }
   
   /**
    * Used to set the state information for this collection of contour 
    * levels.
    * 
    * @param state An encapsulation of the state of this collection of 
    *        contour levels.
    */
   public void setObjectState(ObjectState state)
   {
      //if the state is 'null' do nothing
      if (state==null)
         return;
      
      //set the state that the superclass maintains
      super.setObjectState(state);
      
      //these will reference the new contour levels
      UniformContours newUniformLevels = null;
      NonUniformContours newNonUniformLevels = null;
      
      //get the uniform contour levels
      Object val = state.get(UNIFORM_LEVELS_KEY);
      if ( (val != null) && (val instanceof UniformContours) )
         newUniformLevels = (UniformContours)val;
      
      //get the nonuniform contour levels
      val = state.get(NON_UNIFORM_LEVELS_KEY);
      if ( (val != null) && (val instanceof NonUniformContours) )
         newNonUniformLevels = (NonUniformContours)val;
      
      //set the new contour levels
      setLevels(newUniformLevels, newNonUniformLevels, getAreRepeatsIgnored());
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
//-----------------------=[ End overriden methods ]=--------------------------//
}

/*  Unused Code

   /**
    * Creates a collection of uniformly spaced contour levels.
    * 
    * @param uniformLevels Represents the uniformly spaced contour levels.  
    *                      If this is <code>null</code>, the method 
    *                      {@link #getLevelAt(int) getLevelAt(int)} will 
    *                      always return {@link Float#NaN Float.NaN}.
    /
   public UnorderedContours(UniformContours uniformLevels)
   {
      super((uniformLevels!=null)?uniformLevels.getNumLevels():1, false);
      this.uniformLevels = uniformLevels;
      this.extraLevels = null;
   }
   
   /**
    * Creates a collection of contour levels that only uses manually 
    * specified contour levels.
    * 
    * @param manualLevels Represents the manually represented contour levels.
    *                     If this is <code>null</code>, the method 
    *                     {@link #getLevelAt(int) getLevelAt(int)} will 
    *                     always return {@link Float#NaN Float.NaN}.
    /
   public UnorderedContours(NonUniformContours manualLevels)
   {
      super((manualLevels!=null)?manualLevels.getNumLevels():1, false);
      this.uniformLevels = null;
      this.extraLevels = manualLevels;
   }   
   
*/