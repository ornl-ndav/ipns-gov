/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.anl.ipns.ViewTools.Panels.Contour.Contours;

public class UniformContours extends Contours
{
   private float minValue;
   private float delta;
   
   public UniformContours(float minValue,
                        float maxValue,
                        int numLevels) throws IllegalArgumentException
   {
      super(numLevels);
      
      if (minValue>maxValue)
         throw new IllegalArgumentException(
                   "ContourJPanel$UniformLevels(float minValue, " +
                   "float maxValue, int numLevels) was improperly " +
                   "given 'minValue' and 'maxValue' such that " +
                   "'minValue'>'maxValue'");
      
      this.minValue = minValue;
      this.delta = (maxValue-minValue)/numLevels;
   }

   public float getLevelAt(int i)
   {
      return minValue+i*delta;
   }
   
   public float getMin()
   {
      return minValue;
   }
   
   public float getMax()
   {
      return getLevelAt(getNumLevels());
   }
}
