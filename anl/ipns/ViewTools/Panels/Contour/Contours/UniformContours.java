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
 * Revision 1.8  2005/07/28 23:09:03  kramer
 * Removed an unnecessary System.out.println() statement.
 *
 * Revision 1.7  2005/07/12 16:31:27  kramer
 *
 * Added code comments and javadocs.  Added methods for the IPreserveState
 * interface (which the superclass (Contours) implements directly).  Added
 * a constructor that has parameters:  the lowest contour level, the number
 * of levels, and the between contour lines.
 *
 * Revision 1.6  2005/06/28 16:20:54  kramer
 *
 * Fixed what was basically an 'off by one error' with the getLevelAt()
 * method and added the main() method to test this class.
 *
 * Revision 1.5  2005/06/15 14:23:19  kramer
 *
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

import gov.anl.ipns.ViewTools.Components.ObjectState;

/**
 * Represents a set of uniformly spaced contour levels.
 */
public class UniformContours extends Contours
{
//--------------------------=[ ObjectState keys ]=----------------------------//
   /**
    * "Minimum value" - This static constant String is a key for 
    * referencing the height of the lowest contour level in this 
    * collection of contour levels.  The value that this key 
    * references is a <code>Float</code>.
    */
   public static final String MIN_VALUE_KEY = "Minimum value";
   /**
    * "Delta value" - This static constant String is a key for 
    * referencing the distance between successive contour levels in 
    * this collection of uniformly spaced contour levels.  The 
    * value that this key references is a <code>Float</code>.
    */
   public static final String DELTA_KEY = "Delta value";
//------------------------=[ End objectState keys ]=--------------------------//
   
//------------------------=[ Default field values ]=--------------------------//
   /**
    * The default height of the lowest contour level in this collection of 
    * uniformly spaced contour levels.  The value of this field is 
    * <code>0f</code>.
    */
   public static final float DEFAULT_MIN_VALUE = 0f;
   /**
    * The default distance between successive contour levels in this 
    * collection of uniformly spaced contour levels.  The value of this 
    * field is <code>1f</code>.
    */
   public static final float DEFAULT_DELTA = 1f;
//----------------------=[ End default field values ]=------------------------//
   
//------------------------------=[ Fields ]=----------------------------------//
   /** The minimum "elevation" of the contour levels. */
   private float minValue;
   /** The distance between successive contour levels. */
   private float delta;
//----------------------------=[ End fields ]=--------------------------------//
   
//---------------------------=[ Constructors ]=-------------------------------//
   /**
    * Creates a default set of uniformly spaced contour levels.
    */
   public UniformContours()
   {
      this(DEFAULT_MIN_VALUE, 
           DEFAULT_MIN_VALUE + (DEFAULT_NUM_LEVELS-1)*DEFAULT_DELTA, 
           DEFAULT_NUM_LEVELS);
   }
   
   /**
    * Create a set of contour levels where the lowest contour level has an 
    * "elevation" of <code>minValue</code>, the distance between successive 
    * contour levels is <code>delta</code>, and there are 
    * <code>numLevels</code> contour levels in the collections of contour 
    * levels.
    * 
    * @param minValue The "elevation" of the lowest contour level.
    * @param numLevels The number of contour levels in this collection of 
    *                  contour levels.
    * @param delta The distance between successive contour levels.
    * @throws IllegalArgumentException If <code>numLevels</code> is less 
    *                                  than <code>2</code> or if 
    *                                  {@link Contours#Contours(int) 
    *                                  Contours.Contours(int)} throws an 
    *                                  <code>IllegalArgumentException</code>.
    */
   public UniformContours(float minValue, 
                          int numLevels,
                          float delta) throws IllegalArgumentException
   {
      super(numLevels);
      
      if (numLevels<2)
         throw new IllegalArgumentException(
                   "The number of levels must be greater than or equal to " +
                   "2.  However, the number of levels given was "+numLevels);
      
      this.minValue = minValue;
      this.delta = delta;
   }
   
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
      this(minValue, numLevels, getDelta(minValue, maxValue, numLevels));
      
      if (minValue>maxValue)
         throw new IllegalArgumentException(
                   "The value of the lowest contour level cannot be " +
                   "greater than the level of the highest contour level.");
   }
//--------------------------=[ End constructors ]=----------------------------//
   
//-------------------------=[ Overriden methods ]=----------------------------//
   /**
    * Used to get the state information for this collection of 
    * uniformly spaced contour levels.  This method is overriden to 
    * include in the state the height of the lowest contour level and 
    * the distance between successive contour levels.
    * 
    * @param isDefault If true, the default state for this collection of 
    *                  contour levels is returned.  
    *                  If false, the current state is returned.
    * @return The state of this collection of uniformly spaced contour 
    *         levels.
    * @see Contours#getObjectState(boolean)
    */
   public ObjectState getObjectState(boolean isDefault)
   {
      //get the superclass's state
      ObjectState state = super.getObjectState(isDefault);
        //store the height of the lowest contour level and the 
        //distance between successive contour levels (the 'delta')
        if (isDefault)
        {
           state.insert(MIN_VALUE_KEY, new Float(DEFAULT_MIN_VALUE));
           state.insert(DELTA_KEY, new Float(DEFAULT_DELTA));
        }
        else
        {
           state.insert(MIN_VALUE_KEY, new Float(minValue));
           state.insert(DELTA_KEY, new Float(delta));
        }
      return state;
   }
   
   /**
    * Used to set the state information for this collection of uniformly 
    * spaced contour levels.  This method is overriden to allow storing 
    * in the state the height of the lowest contour level and the distance 
    * between successive contour levels.
    * 
    * @param state An encapsulation of the state information for this 
    *              collection of uniformly spaced contour levels.
    * @see Contours#setObjectState(ObjectState)
    */
   public void setObjectState(ObjectState state)
   {
      System.out.println("UniformContours:  inside setObjectState()");
      
      //if the state is 'null' do nothing
      if (state==null)
         return;
      
      //set the state that the superclass maintains
      super.setObjectState(state);
      
      //set the height of the lowest contour level
      Object val = state.get(MIN_VALUE_KEY);
      if ( (val != null) && (val instanceof Float) )
         this.minValue = ((Float)val).floatValue();
      
      //set the distance between successive contour levels
      val = state.get(DELTA_KEY);
      if ( (val != null) && (val instanceof Float) )
         this.delta = ((Float)val).floatValue();
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
      return getLevelAt(getNumLevels()-1);
   }
//-----------------------=[ End overriden methods ]=--------------------------//

//-----------------------=[ Convience methods ]=------------------------------//
   /**
    * Used to get a copy of this collection of uniformly spaced 
    * contour levels.
    * 
    * @return A deep copy of this collection of contour levels.
    */
   public UniformContours getCopy()
   {
      UniformContours copy = new UniformContours(this.getLowestLevel(), 
                                                 this.getHighestLevel(), 
                                                 this.getNumLevels());
      copy.setObjectState(this.getObjectState(false));
      return copy;
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

   /**
    * Given a collection of uniformly spaced contour levels with the 
    * height of the highest and lowest contour level and the number of 
    * contour levels given, this method determines the distance between 
    * each successive contour level.
    * 
    * @param minValue The height of the lowest contour level in 
    *                 this collection of contour levels.
    * @param maxValue The height of the highest contour level in 
    *                 this collection of contour levels.
    * @param numLevels The number of contour levels in the collection 
    *                  of contour levels.
    * @return The distance between each contour level in this 
    *         collection of uniformly spaced contour levels.
    */
   public static float getDelta(float minValue, float maxValue, int numLevels)
   {
      return (maxValue-minValue)/(numLevels-1);
   }
   
   /**
    * Testbed.  Used to test if the methods for determining the 
    * number of levels and highest and lowest contour level work 
    * as they should.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      UniformContours contours = new UniformContours(0f, 10f, 11);
      System.out.println("Constructor used:  UniformContours(0, 10, 11)");
      System.out.println("getNumLevels() = "+contours.getNumLevels());
      System.out.println("getLowestLevel() = "+contours.getLowestLevel());
      System.out.println("getHighestLevel() = "+contours.getHighestLevel());
      for (int i=0; i<contours.getHighestLevel(); i++)
         System.out.println("  levels["+i+"] = "+contours.getLevelAt(i));
   }
//---------------------=[ End convience methods ]=----------------------------//
}
