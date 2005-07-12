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
 * Revision 1.5  2005/07/12 16:36:04  kramer
 * Made this class an abstract class and now the classes OrderedContours and
 * NonUniformContours extend this class (previously, these two classes were
 * encapsulated in this class).  This was done so that the methods for the
 * IPreserveState interface could be implemented specifically by the
 * subclasses of this class.
 *
 * Revision 1.4  2005/06/28 16:18:46  kramer
 *
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

/**
 * Represents a collection of uniformly spaced contour levels unioned with 
 * a collection of manually specified contour levels.  Subclasses must 
 * define exactly how this union is defined and stored.
 */
public abstract class MixedContours extends Contours
{
//------------------------------=[ Fields ]=----------------------------------//
   /**
    * Flag indicating if this collection of contour levels are 
    * in ascending order.
    */
   private boolean isOrdered;
   /**
    * Flag indicating if duplicate contour levels should be represented 
    * in this collection of contour levels twice or once.
    * <ul>
    *   <li>
    *     If true, a contour level may be represented multiple times in 
    *     this collection of contour levels (if for example it is 
    *     represented in each collection of contour levels that is part 
    *     of the union of contour levels that this class represents).
    *   </li>
    *   <li>
    *     If false, a contour level will be represented at most once in 
    *     this collection of contour levels.
    *   </li>
    * </ul>
    */
   private boolean ignoreRepeats;
//----------------------------=[ End fields ]=--------------------------------//

//---------------------------=[ Constructors ]=-------------------------------//
   /**
    * Creates an abstract description of this union of contour levels.
    * 
    * @param numLevels The number of contour levels in this collection of 
    *                  contour levels.
    * @param isOrdered Indicates if this collection of contour levels is 
    *                  arranged in ascending order.
    * @param ignoreRepeats Indicates if duplicate contour levels in this 
    *                      collection of contour levels are only represented 
    *                      once in the collection.
    */
   public MixedContours(int numLevels, 
                        boolean isOrdered, boolean ignoreRepeats)
   {
      super(numLevels);
      this.isOrdered = isOrdered;
      this.ignoreRepeats = ignoreRepeats;
   }
//-------------------------=[ End constructors ]=-----------------------------//
   
//---------------------=[ Subclass convience methods ]=-----------------------//
   /**
    * Used to determine if this collection of contour levels is arranged in 
    * ascending order or not.
    * 
    * @return True if this collection is arranged in ascending order and 
    *         false if no order has been done.
    */
   public boolean getIsOrdered()
   {
      return isOrdered;
   }
   
   /**
    * Used to determine if duplicate contour levels in this collection of 
    * contour levels are only represented once in this collection of contour 
    * levels.
    * 
    * @return <ul>
    *           <li>
    *             True if a contour level may be represented multiple times 
    *             in this collection of contour levels (if for example it is 
    *             represented in each collection of contour levels that is part 
    *             of the union of contour levels that this class represents).
    *           </li>
    *           <li>
    *             False if a contour level will be represented at most once 
    *             in this collection of contour levels.
    *           </li>
    *         </ul>
    */
   public boolean getAreRepeatsIgnored()
   {
      return ignoreRepeats;
   }
//-------------------=[ End subclass convience methods ]=---------------------//
}

/* Unused Code
 * 
   /** Holds the mixed contour levels. /
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
    /
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
    /
   public float getLevelAt(int i)
   {
      return contours.getLevelAt(i);
   }
   
   /**
    * Get the "elevation" of the lowest contour level.
    * 
    * @return The value of the lowest contour level.
    /
   public float getLowestLevel()
   {
      return contours.getLowestLevel();
   }
   
   /**
    * Get the "elevation" of the highest contour level.
    * 
    * @return The value of the highest contour level.
    /
   public float getHighestLevel()
   {
      return contours.getHighestLevel();
   }
*/
