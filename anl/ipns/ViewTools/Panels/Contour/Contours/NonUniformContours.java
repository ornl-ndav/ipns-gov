/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

public class NonUniformContours extends Contours
{
   private float[] levels;
   
   public NonUniformContours(float[] levels) throws IllegalArgumentException
   {
      super((levels!=null)?levels.length:0);
      if (levels==null)
         throw new IllegalArgumentException(
                     "ContourJPanel$NonUniformLevels(float[] levels) " +
                     "was given a null parameter 'levels'");
      this.levels = levels;
   }

   public float getLevelAt(int i)
   {
      if (i>=0 && i<levels.length)
         return levels[i];
      else
         return Float.NaN;
   }
}
