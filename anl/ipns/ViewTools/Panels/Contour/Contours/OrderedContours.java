/*
 * File: OrderedContours.java
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
 * Revision 1.1  2005/07/12 16:20:34  kramer
 * Initial checkin.  This class represents a collection of both uniformly
 * spaced and non-uniformly spaced contour levels that are in ascending
 * order.
 *
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

import gov.anl.ipns.ViewTools.Components.ObjectState;

import java.util.Arrays;

/**
 * This class is used to fuse a <code>UniformContours</code> and 
 * <code>NonUniformContours</code> object together to form a set of 
 * nonuniform contour levels sorted in increasing order.
 */
public class OrderedContours extends MixedContours
{
//--------------------------=[ ObjectState keys ]=----------------------------//
   /**
    * This static constant String is a key used for referencing the 
    * array of contour levels encapsulated by this class.  The value 
    * that this key references is a float array.
    */
   public static final String CONTOUR_LEVELS_KEY = 
      NonUniformContours.CONTOUR_LEVELS_KEY;
//------------------------=[ End ObjectState keys ]=--------------------------//
   
//------------------------=[ Default field values ]=--------------------------//
   /**
    * The default set of contour levels that this class encapsulates.
    */
   public static final float[] DEFAULT_CONTOUR_LEVELS = 
      NonUniformContours.DEFAULT_CONTOUR_LEVELS;
//----------------------=[ End default field values ]=------------------------//
   
//------------------------------=[ Fields ]=----------------------------------//
   /** This is the union of the uniform and nonuniform contour levels. */
   private float[] levels;
//----------------------------=[ End fields ]=--------------------------------//
   
//---------------------------=[ Constructors ]=-------------------------------//
   /**
    * Creates a default collection of ordered contour levels.
    */
   public OrderedContours()
   {
      this(new UniformContours(), new NonUniformContours());
   }
   
   /**
    * Creates an OrderedContours object with the parameters as 
    * specified such that no contour levels in the collection of 
    * contour levels are repeated.  Notice:  <code>uniformContours</code> 
    * cannot be <code>null</code>.  However, 
    * <code>nonuniformContours</code> can be.  In this case, the levels 
    * will precisely be uniformly spaced contour levels.
    * 
    * @param uniformContours Encapsulates the uniformlly spaced contour 
    *                        levels.
    * @param nonuniformContours Encapsulates the nonuniformlly spaced 
    *                           contour levels.
    * @throws IllegalArgumentException  If <code>uniformContours</code> 
    *                                   is <code>null</code> or 
    *                                   if the total number of levels 
    *                                   in the 
    *                                   <code>UniformContours</code> and 
    *                                   <code>NonUniformContours</code>  
    *                                   is 0.
    */
   public OrderedContours(UniformContours uniformContours, 
                          NonUniformContours nonuniformContours)
   {
      this(uniformContours, nonuniformContours, false);
   }
   
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
      super(1, true, ignoreRepeats);
      setLevels(uniformContours, nonuniformContours, ignoreRepeats);
   }
//-------------------------=[ End constructors ]=-----------------------------//
   
//-----------------=[ Constructor convenience methods ]=----------------------//
   /**
    * Used to create a collection of ordered contour levels and store the 
    * reference in the field <code>this.levels</code>.  
    * Notice:  <code>uniformContours</code> cannot be 
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
   private void setLevels(UniformContours uniformContours, 
                          NonUniformContours nonuniformContours, 
                          boolean ignoreRepeats)
   {
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
      this.levels = new float[uniformContours.getNumLevels()+
                              nonUniformLevels.length];
      if (this.levels.length==0)
         throw new IllegalArgumentException(
                      "error:  no data was entered to allow " +
                      "any contour levels to be plotteds");
      
      //copy the uniform contour levels into 'levels'
      for (int i=0; i<uniformContours.getNumLevels(); i++)
         this.levels[i] = uniformContours.getLevelAt(i);
      
      //now copy the array specifying the nonuniform contour levels into 
      //the array 'levels'
      System.arraycopy(nonUniformLevels, 0, 
                       this.levels, uniformContours.getNumLevels(), 
                       nonUniformLevels.length);
      
      //lastly sort the levels found
      setLevels(this.levels, ignoreRepeats);
   }
   
   /**
    * Used to take the given array of contour levels, order them, remove 
    * any repeats (if specified), and store them in <code>this.levels</code>.
    * 
    * @param levels The contour levels.
    * @param ignoreRepeats If true the collection may possibly contain 
    *                      repeated contour levels.  If false, the 
    *                      collection will only contain a given contour 
    *                      level at most once.
    */
   private void setLevels(float[] levels, boolean ignoreRepeats)
   {
      //store the levels
      this.levels = levels;
      
      //sort the levels
      Arrays.sort(this.levels);
      
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
            if (this.levels[i]!=prevVal)
               levelCopy[numUnique++] = this.levels[i];
            
            prevVal = this.levels[i];
         }
         
         //if there are less unique elements in the array then there 
         //are total elements in the array, some of the non-unique elements 
         //were removed.  Then pack the array because there are 'blanks' 
         //at the end of the array
         if (numUnique!=this.levels.length)
         {
            float[] packedArr = new float[numUnique];
            System.arraycopy(levelCopy, 0, packedArr, 0, packedArr.length);
            this.levels = packedArr;
         }
      }
      
      //set the number of contour levels
      setNumLevels(this.levels.length);
   }
//---------------=[ End constructor convenience methods ]=--------------------//
   
//-------------------------=[ Overriden methods ]=----------------------------//
   /**
    * Used to get the state information for this collection of contour 
    * levels.  This method is overriden to include the float array of ordered 
    * contours in the state information.
    * 
    * @param isDefault If true, the default state for this collection of 
    *                  contour levels is returned.
    *                  If false, the current state is returned.
    * @return The state for this collection of contour levels.
    * @see OrderedContours#getObjectState(boolean)
    */
   public ObjectState getObjectState(boolean isDefault)
   {
      //get the superclass's state
      ObjectState state = super.getObjectState(isDefault);
        //store the contour levels
        if (isDefault)
           state.insert(CONTOUR_LEVELS_KEY, DEFAULT_CONTOUR_LEVELS);
        else
        {
           float[] levelCopy = new float[levels.length];
           System.arraycopy(levels, 0, levelCopy, 0, levelCopy.length);
           state.insert(CONTOUR_LEVELS_KEY, levelCopy);
        }
      return state;
   }
   
   /**
    * Used to set the state information for this collection of ordered 
    * cohtour levels.  This method is overriden to allow the float array of 
    * contours to be stored in the state.
    * 
    * @param state An encapsulation of the state information for this 
    *              collection of contour levels.
    * @see OrderedContours#setObjectState(ObjectState)
    */
   public void setObjectState(ObjectState state)
   {
      //if the state is 'null' do nothing
      if (state==null)
         return;
      
      //set the state information that the superclass maintains
      super.setObjectState(state);
      
      //set the contour levels
      Object val = state.get(CONTOUR_LEVELS_KEY);
      if ( (val != null) && (val instanceof float[]) )
         setLevels((float[])val, getAreRepeatsIgnored());
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
//-----------------------=[ End overriden methods ]=--------------------------//
}
