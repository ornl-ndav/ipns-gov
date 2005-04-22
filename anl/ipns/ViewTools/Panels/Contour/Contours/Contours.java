/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

public abstract class Contours
{
   private int numLevels;
   
   public Contours(int numLevels) throws IllegalArgumentException
   {
      if (numLevels<=0)
         throw new IllegalArgumentException(
                   "ContourJPanel$Levels(int numLevels) 'numLevels' " +
                   "cannot be non-negative but 'numLevels'="+numLevels+
                   "was passed to the constructor.");
      this.numLevels = numLevels;
   }
   
   public int getNumLevels() { return numLevels; }
   public abstract float getLevelAt(int i);
}
