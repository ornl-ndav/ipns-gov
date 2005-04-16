/*
 * Created on Apr 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.anl.ipns.ViewTools.Panels.Contour;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * 
 */
public class ContourJPanel extends CoordJPanel implements Serializable,
      IPreserveState
{
   /** Holds the data that is being drwn on this panel. */
   private IVirtualArray2D data2D;
   /**
    * Holds information which contours to draw (i.e. at which 'heights' will 
    * slices be taken from the surface and drawn as contours)
    */
   private Levels          levels;
   
   /**
    * Private constructor used to initialize the 
    * {@link IVirtualArray2D IVirtualArray2D} {@link #data2D data2D} and 
    * set up some of the general properties of the panel.
    * 
    * @param data2D
    * 
    * @throws IllegalArgumentException iff <code>data2D</code> is 
    *                                  <code>null</code>.
    */
   private ContourJPanel(IVirtualArray2D data2D)
   {
      if (data2D==null)
         throw new IllegalArgumentException(
                   "A null IVirtualArray2D was passed to the constructor " +
                   "for a ContourJPanel.");
      this.data2D = data2D;
      this.setBackground(Color.WHITE);
   }
   
   /**
    * Constructs the panel such that it uses the data specified in 
    * <code>data2D</code> and uses contours based on <code>minValue</code>, 
    * <code>maxValue</code>, and <code>numLevels</code>.
    * 
    * @param data2D    The {@link IVirtualArray2D IVirtualArray2D} whose data 
    *                  is plotted.
    * @param minValue  The value of the data about which the first contour 
    *                  will be created.
    * @param maxValue  The value of the data about which the last controu 
    *                  will be created.
    * @param numLevels The number of gaps between the contours.  Thus, there 
    *                  will be <code>numLevels+1</code> contours where the 
    *                  first one has value <code>minValue</code> and the 
    *                  <code>(numLevels+1)</code>nth one has the value 
    *                  <code>maxValue</code>.
    * 
    * @throws IllegalArgumentException iff <code>data2D</code> is null or 
    *                                  <code>numLevels</code><=0 or 
    *                                  <code>minValue>maxValue</code>,
    */
   public ContourJPanel(IVirtualArray2D data2D, 
                        float minValue,
                        float maxValue,
                        int numLevels) throws IllegalArgumentException
   {
      this(data2D);
      this.levels = new UniformLevels(minValue,maxValue,numLevels);
   }
   
   /**
    * Constructs the panel such that it uses the data specified in 
    * <code>data2D</code> and draws the data around the contours specified.
    * 
    * @param data2D The {@link IVirtualArray2D IVirtualArray2D} whose data 
    *               is plotted.
    * @param levels The values of the contours to use.
    * 
    * @throws IllegalArgumentException iff <code>data2D</code> is null or 
    *                                  <code>levels</code> is null or 
    *                                  <code>levels.length<=0</code>.
    */
   public ContourJPanel(IVirtualArray2D data2D,
                        float[] levels) throws IllegalArgumentException
   {
      this(data2D);
      this.levels = new NonUniformLevels(levels);
   }
   
   /**
    * Does the actual work of painting the contour lines on the panel.
    */
   public void paint(Graphics g)
   {
      stop_box( current_point, false );   // if the system redraws this without

      stop_crosshair( current_point );    // our knowlege, we've got to get rid
                                          // of the cursors, or the old position
                                          // will be drawn rather than erased
                                          // when the user moves the cursor (due
                                          // to XOR drawing).
      super.paint(g);
      
      //holds the contour points found
        Vector contourPts;
      //references a start and end point in 'contourPts'
        floatPoint2D curPt1,curPt2;
      //first to extract the array of data to use
        float[][] arr = data2D.getRegionValues(0, 
                                               data2D.getNumRows()-1, 
                                               0,
                                               data2D.getNumColumns()-1);
      if (arr==null)
      {
         System.out.println("Warning:  A null array of data was found in " +
                            "ContourJPanel.paint(....).  Thus, the data " +
                            "could not be plotted.");
         return;
      }
        
      //set the local transforms to map a rectangular region to the 
      //entire panel
        SetTransformsToWindowSize();
      //set the region being mapped from to be the dimesions of the array
        local_transform.setSource(new CoordBounds(0,0,arr[0].length,arr.length));
      
      //for each level draw the contour
      for (int i=0; i<levels.getNumLevels(); i++)
      {
         //now to get the contours
           contourPts = Contour2D.contour(arr,levels.getLevelAt(i));
         
         //this holds the x points describing where each contour line 
         //segment should be drawn.  The units are in terms of row/column and 
         //the numbers are in the order p1_start, p1_end, p2_start, p2_end ....
           float[] xrcVals = new float[contourPts.size()];
         //the same as above except this stores y values
           float[] yrcVals = new float[contourPts.size()];
           
         //now to fill the arrays
           for (int j=0; j<contourPts.size(); j++)
           {
              curPt1 = (floatPoint2D)contourPts.elementAt(j);
              xrcVals[j] = curPt1.x;
              yrcVals[j] = curPt1.y;
           }
         
         //now to transform the points from row/column coordinates to 
         //pixel coordinates
            local_transform.MapXListTo(xrcVals);
            local_transform.MapYListTo(yrcVals);
         
         //now to draw the lines with the new pixel coordinates
           for (int j=0; (j+1)<contourPts.size(); j+=2)
              g.drawLine((int)xrcVals[j],
                         (int)(yrcVals[j]),
                         (int)(xrcVals[j+1]),
                         (int)(yrcVals[j+1]));
      }
   }
   
   /**
    * Temporary method used to print the contents of a float[][] array.
    */
   private static void printArray(float[][] arr)
   {
      System.out.println("About to display an array");
      System.out.println("-------------------------");
      for (int i=0; i<arr.length; i++)
      {
         for (int j=0; j<arr[i].length; j++)
            System.out.print(""+arr[i][j]+" ");
         System.out.println();
      }
      System.out.println("-------------------------");
      System.out.println("Finished displaying an array");
   }

   /**
    * Generates a float[][] for the data for the function 
    * f(x,y) = x+y
    */
   private static float[][] getTestDataArr()
   {
      float xMin = -10;
      float xMax = 10;
      int numXSteps = 40;
      
      float yMin = -10;
      float yMax = 10;
      int numYSteps = 40;
      
      
      float deltaX = (xMax-xMin)/numXSteps;
      float deltaY = (yMax-yMin)/numYSteps;
      
      float[][] dataArr = new float[numXSteps][numYSteps];
      for (int i=0; i<dataArr.length; i++)
         for (int j=0; j<dataArr[i].length; j++)
         {
            dataArr[i][j] = (xMin+deltaX*i)*(xMin+deltaX*i)+(yMin+deltaY*j)*(yMin+deltaY*j);
            //System.out.println("f("+(xMin+deltaX*i)+", "+(yMin+deltaY*j)+") = "+dataArr[i][j]);
         }
      
      return dataArr;
   }
   
   /**
    * Generates a IVirtualArray2D for the data for the function 
    * f(x,y) = x+y
    */
   private static IVirtualArray2D getTestData()
   {
      return new VirtualArray2D(getTestDataArr());
   }
   
   /**
    * Testbed.
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      ContourJPanel panel = new ContourJPanel(getTestData(), 0, 100, 10);
      JFrame frame = new JFrame("ContourJPanel test");
         frame.setSize(200,200);
         frame.getContentPane().add(panel);
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setVisible(true);
   }
   
   /**Internal Classes Used to Determine the Height of Levels on the Surface**/
   private abstract class Levels
   {
      private int numLevels;
      
      public Levels(int numLevels) throws IllegalArgumentException
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
   
      private class UniformLevels extends Levels
      {
         private float minValue;
         private float delta;
         
         public UniformLevels(float minValue,
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
      }
   
      private class NonUniformLevels extends Levels
      {
         private float[] levels;
         
         public NonUniformLevels(float[] levels) throws IllegalArgumentException
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
   /**End of the Classes for Determining the Height of Levels*****************/
}

/*--------------------------Unused Code-------------------------*/
/*
   private CoordTransform createImageToWindowTransform()
   {
      //this will set the superclass's transformations to align with 
      //the size of this JPanel
        SetTransformsToWindowSize();
      CoordBounds panelCoords = getGlobal_transform().getSource();
      float epsilon = 0.001f;
      CoordBounds imageCoords = 
                     new CoordBounds(epsilon, 
                                     epsilon, 
                                     data2D.getNumRows()-epsilon, 
                                     data2D.getNumColumns()-epsilon);
      return new CoordTransform(panelCoords,imageCoords);
   }
   
   private void makeImage()
   {
      //if the panel is not visible return
      if (!isVisible())
         return;
      
   }
*/
