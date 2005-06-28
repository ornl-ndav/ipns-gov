/*
 * File: Contours.java
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
 * Revision 1.5  2005/06/28 16:12:58  kramer
 * Added the non-abstract toString() method.  Also, the abstract methods
 * getLowestLevel() and getHighestLevel() were added.
 *
 * Revision 1.4  2005/06/08 22:12:52  kramer
 *
 * Made the error messages that this class's constructor generates more
 * user friendly.
 *
 * Revision 1.3  2005/06/08 17:19:55  kramer
 *
 * Added the GNU header, added javadocs, and added a method setNumLevels()
 * which is used by subclasses if the levels are modified.
 *
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

/**
 * Encapsulates the basic information needed to use a set of contour lines 
 * needed to generate a contour plot of some data.
 */
public abstract class Contours
{
   /** The number of contour levels. */
   private int numLevels;
   
   /**
    * Create a Contours object that corresponds to a set of 
    * <code>numLevels</code> contour lines.
    * 
    * @param numLevels The number of levels in a given set of data at which 
    *                  contour lines should be drawn.
    * @throws IllegalArgumentException If and only if <code>numLevels<=0</code>
    * @see #getLevelAt(int)
    */
   public Contours(int numLevels) throws IllegalArgumentException
   {
      if (numLevels<=0)
         throw new IllegalArgumentException(
                   "The number of levels must be positive.  However, the " +
                   "number of levels given was "+numLevels);
      this.numLevels = numLevels;
   }
   
   /**
    * Used to determine the number of contours lines that this set of 
    * contour lines contains.
    * 
    * @return The number of levels in a given set of data at which countour 
    *         lines should be drawn.
    */
   public int getNumLevels() { return numLevels; }
   
   /**
    * Used by subclasses if they decide to change the contour levels that 
    * they represent (in this case the number of contour levels may change).
    * 
    * @param numLevels The new number of contour levels.
    */
   protected void setNumLevels(int numLevels)
   {
      this.numLevels = numLevels;
   }
   
   /**
    * Used to get a readout describing the contours levels 
    * encapsulated by this <code>Contours</code> object.  
    * The readout returned is designed for debugging purposes.
    * 
    * @return A String describing this <code>Contours</code> object.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      //Display an introduction
      buffer.append("Displaying a ");
      buffer.append(getClass().getName());
      buffer.append(" object\n");
      buffer.append("--------------------\n");
      
      //Display the metadata about the object
      buffer.append("  MetaData:\n");
      buffer.append("    Number of contour levels:  ");
        int numLevels = getNumLevels();
      buffer.append(numLevels);
      buffer.append("\n");
      
      buffer.append("    Lowest contour level:  ");
      buffer.append(getLowestLevel());
      buffer.append("\n");
      
      buffer.append("    Highest contour level:  ");
      buffer.append(getHighestLevel());
      buffer.append("\n");
      buffer.append("  --------------------\n");
      
      //Display the actual levels
      for (int i=0; i<numLevels; i++)
      {
         buffer.append("    level[");
         buffer.append(i);
         buffer.append("] = ");
         buffer.append(getLevelAt(i));
         buffer.append("\n");
      }
      return buffer.toString();
   }
   
   /**
    * Get the value of the <code>ith</code> contour level.  If you think of 
    * contour plot of a given set of data as if it were a topological map, 
    * this method would return the elevation on the <code>ith</code> 
    * contour line.
    * 
    * @param i The index referencing the contour line to analyze.  These 
    *          indexes should be used in exactly the same way that array 
    *          indexes are used.  For example, if 
    *          {@link #getNumLevels() getNumLevels()} returns 4, then indexes 
    *          0, 1, 2, and 3 can be used to reference the 1st, 2nd, 3rd, and 
    *          4th contour level respectively.  <b>Note:  It is the 
    *          responsibility of subclasses to ensure that this behavior is 
    *          maintained.</b>
    * @return The "elevation" of the <code>ith</code> contour level.
    */
   public abstract float getLevelAt(int i);
   
   /**
    * Get the "elevation" of the lowest contour level.
    * 
    * @return The value of the lowest contour level.
    */
   public abstract float getLowestLevel();
   
   /**
    * Get the "elevation" of the highest contour level.
    * 
    * @return The value of the highest contour level.
    */
   public abstract float getHighestLevel();
}
