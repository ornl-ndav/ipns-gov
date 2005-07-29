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
 * Revision 1.9  2005/07/29 15:40:33  kramer
 * Removed some unnecessary System.out.println() statements.
 *
 * Revision 1.8  2005/07/12 16:33:14  kramer
 *
 * Reorganized the code, added comments, and javadoc statements.
 * Implemented the methods for the IPreserveState interface (which the
 * superclass (Contours) implements directly).
 *
 * Revision 1.7  2005/06/28 18:55:37  kramer
 *
 * Because Float.MIN_VALUE is the smallest positive float, the
 * getHighestLevel() would sometimes return incorrect results.  This was
 * fixed.
 *
 * Revision 1.6  2005/06/28 16:20:00  kramer
 *
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

import gov.anl.ipns.ViewTools.Components.ObjectState;

/**
 * Represents a set of contour levels of any size and arrangement.
 */
public class NonUniformContours extends Contours
{
//--------------------------=[ ObjectState keys ]=----------------------------//
   /**
    * "Levels" - This static constant String is a key for referencing 
    * the array of contour levels encapsulated by this class.  The 
    * value that this key references is a float array.
    */
   public static final String CONTOUR_LEVELS_KEY = "Levels";
//-----------------------=[ End objectState keys ]=---------------------------//
   
//------------------------=[ Default field values ]=--------------------------//
   /**
    * The default array of contour levels that this object 
    * encapsulates.  The value of the <code>ith</code> element 
    * of this array is <code>i</code> and there are 
    * {@link Contours#DEFAULT_NUM_LEVELS Contours.DEFAULT_NUM_LEVELS} 
    * elements in the array.
    */
   public static final float[] DEFAULT_CONTOUR_LEVELS;
   /**
    * Initializes the default array of contour levels that this 
    * object encapsulates.
    */
   static
   {
      DEFAULT_CONTOUR_LEVELS = new float[DEFAULT_NUM_LEVELS];
      for (int i=0; i<DEFAULT_CONTOUR_LEVELS.length; i++)
         DEFAULT_CONTOUR_LEVELS[i] = i;
   }
//----------------------=[ End default field values ]=------------------------//
   
//------------------------------=[ Fields ]=----------------------------------//
   /** Holds the value of each contour level. */
   private float[] levels;
//-----------------------------=[ End fields ]=-------------------------------//
   
//---------------------------=[ Constructors ]=-------------------------------//
   /**
    * Creates a default set of nonuniformly spaced contour levels.
    */
   public NonUniformContours()
   {
      this(DEFAULT_CONTOUR_LEVELS);
   }
   
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
//--------------------------=[ End constructors ]=----------------------------//

//-------------------------=[ Overriden methods ]=----------------------------//
   /**
    * Used to get the state information for this collection of contour 
    * levels.  This method is overriden to include the float array of 
    * contours in the state information.
    * 
    * @param isDefault If true, the default state for this collection of 
    *                  contour levels is returned.
    *                  If false, the current state is returned.
    * @return The state for this collection of contour levels.
    * @see Contours#getObjectState(boolean)
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
           float[] copy = new float[levels.length];
           System.arraycopy(levels, 0, copy, 0, copy.length);
           state.insert(CONTOUR_LEVELS_KEY, copy);
        }
      return state;
   }
   
   /**
    * Used to set the state information for this collection of contour 
    * levels.  This method is overriden to allow the float array of 
    * contours to be stored in the state.
    * 
    * @param state An encapsulation of the state information for this 
    *              collection of contour levels.
    * @see Contours#setObjectState(ObjectState)
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
         this.levels = (float[])val;
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
      float max = -1*Float.MAX_VALUE;
      for (int i=0; i<levels.length; i++)
         if (levels[i]>max)
            max = levels[i];
      return max;
   }
//-----------------------=[ End overriden methods ]=--------------------------//
   
//-----------------------=[ Convience methods ]=------------------------------//
   /**
    * Used to create a copy of this collection of nonuniformly spaced 
    * contour levels.
    * 
    * @return A deep copy of this collection of contour levels.
    */
   public NonUniformContours getCopy()
   {
      NonUniformContours copy = new NonUniformContours(getLevels());
      copy.setObjectState(this.getObjectState(false));
      return copy;
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
//---------------------=[ End convience methods ]=----------------------------//
}
