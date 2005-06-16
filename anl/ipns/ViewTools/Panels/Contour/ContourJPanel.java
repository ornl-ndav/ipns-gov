/*
 * File: ContourJPanel.java
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
 * Revision 1.8  2005/06/16 16:07:41  kramer
 * Now this class will more reliably drawn the contour labels inside the
 * zoomed region when the user zooms in on the plot.  The only time the
 * labels aren't drawn is when the user zooms to a point where none of the
 * endpoints of the contour's line segments are inside the panel.
 *
 * Also, javadocs have been added.
 *
 * Revision 1.7  2005/06/15 21:38:31  kramer
 *
 * Added the GNU GPL preamble to the file.  Also, now when the user zooms
 * in on a region, the contour labels are drawn in the region (most of the
 * time).  Some fixes still need to be done.
 *
 */
package gov.anl.ipns.ViewTools.Panels.Contour;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.Contours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.NonUniformContours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.UniformContours;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import DataSetTools.util.SharedData;

/* TODO List
 * 1.  Compare drawing the lines based on the points located at pixel 
 *     corners or pixel centers.
 * 2.  Label the contour lines (or use line types).
 * 3.  Figure out how to color the lines and possibly use colored regions.
 *     (for now don't worry about filling colored regions)
 *     Can look at it as an image with the contours overlayed on top of it 
 *     and each pixel is given a color depending on if it is betweeen certain 
 *     levels.
 * 4.  Algorithm for finding how to chain the segments found together.
 */
/**
 * This is a special type of panel with zooming and cursor capabilities such 
 * that given an array of data in an <code>IVirtualArray2D</code> a 
 * contour plot of the data can be generated.
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
   private Contours levels;
   
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
      this.levels = new UniformContours(minValue,maxValue,numLevels);
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
      this.levels = new NonUniformContours(levels);
   }
   
   public void changeData(IVirtualArray2D arr)
   {
      data2D = arr;
      repaint();
   }
   
   public Contours getContours()
   {
      return levels;
   }
   
   public void setContours(Contours levels)
   {
      this.levels = levels;
      repaint();
   }
   
   /**
    * Does the actual work of painting the contour lines on the panel.
    */
   public void paint(Graphics gr)
   {
      Graphics2D g = (Graphics2D)gr;
      
      stop_box( current_point, false );   // if the system redraws this without

      stop_crosshair( current_point );    // our knowlege, we've got to get rid
                                          // of the cursors, or the old position
                                          // will be drawn rather than erased
                                          // when the user moves the cursor (due
                                          // to XOR drawing).
      super.paint(g);
      
      //first to extract the array of data to use
        float[][] arr = data2D.getRegionValues(0, 
                                               data2D.getNumRows()-1, 
                                               0, 
                                               data2D.getNumColumns()-1);
      if (arr==null)
      {
         SharedData.addmsg("Warning:  A null array of data was found " +
                           "from the IVirtualArray2D in " +
                           "ContourJPanel.paint(....).  Thus, the data " +
                           "could not be plotted.");
         return;
      }
      else if (arr.length==0) //return because there is no data to draw
      {
         SharedData.addmsg("Warning:  The array given to the ContourJPanel " +
                           "was empty.  Thus, no data has been plotted.");
         return;
      }
        
      //set the local transforms to map a rectangular region to the 
      //entire panel
        SetTransformsToWindowSize();
      //now to make a transform that maps from row/column to the world 
      //coordinates of the entire panel.  The row/col is mapped to the 
      //location on the ENTIRE panel because the ALL of the data in the 
      //array needs to be mapped to ALL of the panel (not just a small 
      //region which is what 'local_transform' describes).
        CoordTransform rcToGlobal = 
           new CoordTransform(new CoordBounds(0,0,arr[0].length,arr.length),
                              global_transform.getSource());

      //this holds the contour points found
        Vector contourPts;
      //references an element in 'contourPts'
        floatPoint2D curPt;
      //this will hold the x points describing where each contour line segment 
      //that should be drawn.  The units will initially be in terms of 
      //row/column and will be converted to world coordinates and then 
      //pixel coordinates.  The numbers are in the order 
      //p1_start, p1_end, p2_start, p2_end ....
        float[] xrcVals;
      //the same as above except this stores y values
        float[] yrcVals;
      //this is the index of the point with the minimum gradient on a level
      //(where grad(F(x,y)) := F_x(x,y)*i + F_y(x,y)*j where 
      // F_x(x,y) denotes the partial derivative of F with respect to x
      // F_y(x,y) is the partial derivative of F with respect to y)
        int minGradIndex;
      //this stores the square of the minimum gradient
        float minGradSqr;
        
      //these are the minimum and maximum x and y indices in the array 
      //that can be accessed and still be in the current view window
      //(all of the data may not be in the view window if the user has 
      // zoomed in on some portion of the data).  Note that the values are 
      // inclusize (i.e. arr[xMaxIndex][yMaxIndex] can legally be accessed).
        int xMinIndex = getXIndexFromPixel(0, rcToGlobal, arr.length, true);
        int xMaxIndex = getXIndexFromPixel(getWidth(), rcToGlobal, 
                                           arr.length, false);
        
        int yMinIndex = getYIndexFromPixel(0, rcToGlobal, arr[0].length, true);
        int yMaxIndex = getYIndexFromPixel(getHeight(), rcToGlobal, 
                                           arr[0].length, false);
        
        //if the min is greater than the max, the user has zoomed in too 
        //much.  Then flip the two so that the ordering is correct.
        int temp;
        if (xMinIndex>xMaxIndex)
        {
           temp = xMinIndex;
           xMinIndex = xMaxIndex;
           xMaxIndex = temp;
        }
        if (yMinIndex>yMaxIndex)
        {
           temp = yMinIndex;
           yMinIndex = yMaxIndex;
           yMaxIndex = yMinIndex;
        }
        
        /* For testing purposes
        System.out.println("----------------------------");
        System.out.println("xMinIndex="+xMinIndex);
        System.out.println("xMaxIndex="+xMaxIndex);
        System.out.println("yMinIndex="+yMinIndex);
        System.out.println("yMaxIndex="+yMaxIndex);
        System.out.println("----------------------------");
        */
        
      //for each level draw the contour
      for (int i=0; i<levels.getNumLevels(); i++)
      {
         //first to initialize the variables used to draw this level
           minGradIndex = 0;
           minGradSqr = Float.MAX_VALUE;
         
         //now to get the contours.  The 'Contour2D.contour()' method does 
         //the work of calculating the points on the contour level.  
         //The code in this class does the work of graphically 
         //displaying these points.
           contourPts = Contour2D.contour(arr,levels.getLevelAt(i));
         
         //now to create space for the arrays
           xrcVals = new float[contourPts.size()];
           yrcVals = new float[contourPts.size()];
               
         //now to fill the arrays
           for (int j=0; j<contourPts.size(); j++)
           {
              curPt = (floatPoint2D)contourPts.elementAt(j);
              xrcVals[j] = curPt.x+1/2f;
              yrcVals[j] = curPt.y+1/2f;
              /* Note:  1/2f is added to each point for the following reason.
               *        First, curPt.x (and curPt.y) return a point related 
               *        in terms of rows and columns.  However, the 
               *        row/column description assumes that data points 
               *        over a square on the grid of row/columns are "drawn" 
               *        at the top-left corner of the square.  However, the 
               *        transforms here assumes the points are "drawn" at the 
               *        center of the boxes.  Adding 1/2f shifts the point 
               *        from the corner to the center.
               */
              
              //now to determine the square of the gradient of the
              //function at this point.  The point with the gradient 
              //closest to 0 is the point that is farthest away from the 
              //contour level above and below the current contour level.
              int xIndex = (int)curPt.x;
              int yIndex = (int)curPt.y;
              if ( xIndex>=xMinIndex && xIndex<xMaxIndex && 
                   yIndex>=yMinIndex && yIndex<yMaxIndex)
              {
                 float Fx = arr[xIndex+1][yIndex] - arr[xIndex][yIndex];
                 if ( (yIndex+1)<arr[xIndex].length )
                 {
                    float Fy = arr[xIndex][yIndex+1] - arr[xIndex][yIndex];
                    float gradSqr = Fx*Fx+Fy*Fy;
                    //if this gradient is as smaller or smaller than the 
                    //current minimum, record it.  The point recorded will 
                    //be used later when rendering the contour's label
                    if (gradSqr<=minGradSqr)
                    {
                       minGradSqr = gradSqr;
                       minGradIndex = j;
                    }
                 }
              }
           }
         
         //now to transform the points from row/column to the world 
         //coordinates on the panel
           rcToGlobal.MapXListTo(xrcVals);
           rcToGlobal.MapYListTo(yrcVals);
           
         //now to transform the points from the world coordinates to 
         //pixel coordinates.  Here 'local_transform' is used because 
         //if the user zooms in on the data, 'local_transform' is modified 
         //to reflect viewing a smaller subsection of the panel.
            local_transform.MapXListTo(xrcVals);
            local_transform.MapYListTo(yrcVals);
            
         //now to draw the lines with the new pixel coordinates
           for (int j=0; (j+1)<contourPts.size(); j+=2)
           {
              //if this or the next index corresponds to the index of the 
              //point with the smallest gradient draw the contour label
              if (j==minGradIndex || (j+1)==minGradIndex)
              {
                 float avX = xrcVals[j];
                 float avY = yrcVals[j];
                 float angle = 0;
                 if (j+1 < xrcVals.length && 
                     j+1 < yrcVals.length)
                 {
                    //now to find the average of the points
                    avX = (float)(avX/2.0 + xrcVals[j+1]/2.0);
                    avY = (float)(avY/2.0 + yrcVals[j+1]/2.0);
                    
                    //now to find the angle to use
                    angle = (float)Math.atan((yrcVals[j+1]-avY)/
                                             (xrcVals[j+1]-avX));
                 }
                 
                 //render the label for this level of the contour
                 //Note:  the transformations are stated in reverse order 
                 //       because when the graphics are drawn, the 
                 //       transformations are done in the opposite order 
                 //       they are given.
                 AffineTransform trans = g.getTransform();
                   g.translate(avX, avY);
                   g.rotate(angle);
                   g.translate(-avX, -avY);
                   g.drawString(""+levels.getLevelAt(i),
                                (int)(avX),
                                (int)(avY));
                 g.setTransform(trans);
              }
              
               //For testing purposes to show the endpoints of the line
               //segment drawn
               /*
               int radius = 5;
                
               g.setColor(Color.GREEN);
               g.drawOval((int)(xrcVals[j])-radius,
                          (int)(yrcVals[j])-radius, radius, radius);
                 
               g.setColor(Color.RED);
               g.drawOval((int)(xrcVals[j+1])-radius,
                          (int)(yrcVals[j+1])-radius, radius, radius);
                 
               g.setColor(Color.BLACK);
               */
                 
               g.drawLine((int)(xrcVals[j]),
                          (int)(yrcVals[j]),
                          (int)(xrcVals[j+1]),
                          (int)(yrcVals[j+1]));
           }
      }
   }
   
   /**
    * Given an index in an array of data, the transformation
    * <code>rcToGlobal</code> maps the index to world coordinates, and the 
    * transformation <code>local_transform</code> maps this new point to 
    * pixel coordinates.  Given a pixel point, this method finds the 
    * closest index that would be mapped to a pixel point closest to this 
    * given pixel point.  For this method, the pixel point is assumed to 
    * correspond to the x value of a two dimenstional coordinate point.  
    * Note:  The index is forced to be in the range [0, maxIndex).
    * 
    * @param px The given x pixel point.
    * @param rcToGlobal The transformation that maps indices to world 
    *                   coordinates.
    * @param maxIndex The smallest positive invalid index.  If this parameter 
    *                 is 5, then the indexes, 0, 1, 2, 3, and 4 are valid 
    *                 indices and 5 is the first invalid index.
    * @param roundUp True if this method should round up to the nearest 
    *                index and false if it should round down to the nearest 
    *                index.
    * @return The closest valid index that is mapped backward from the pixel 
    *         point <code>px</code>.
    */
   private int getXIndexFromPixel(float px, CoordTransform rcToGlobal, 
                                  int maxIndex, boolean roundUp)
   {
      //map the pixel value from pixel to world coordinates
      //(using the local_transform transformation)
      //then using the rcToGlobal transformation, map the pixel coordinate 
      //to the correct column
      float pseudoIndex = rcToGlobal.MapXFrom(local_transform.MapXFrom(px));
      return getValidIndex(pseudoIndex, maxIndex, roundUp);
   }

   /**
    * Given an index in an array of data, the transformation
    * <code>rcToGlobal</code> maps the index to world coordinates, and the 
    * transformation <code>local_transform</code> maps this new point to 
    * pixel coordinates.  Given a pixel point, this method finds the 
    * closest index that would be mapped to a pixel point closest to this 
    * given pixel point.  For this method, the pixel point is assumed to 
    * correspond to the y value of a two dimenstional coordinate point.  
    * Note:  The index is forced to be in the range [0, maxIndex).
    * 
    * @param px The given y pixel point.
    * @param rcToGlobal The transformation that maps indices to world 
    *                   coordinates.
    * @param maxIndex The smallest positive invalid index.  If this parameter 
    *                 is 5, then the indexes, 0, 1, 2, 3, and 4 are valid 
    *                 indices and 5 is the first invalid index.
    * @param roundUp True if this method should round up to the nearest 
    *                index and false if it should round down to the nearest 
    *                index.
    * @return The closest valid index that is mapped backward from the pixel 
    *         point <code>px</code>.
    */
   private int getYIndexFromPixel(float px, CoordTransform rcToGlobal, 
                                  int maxIndex, boolean roundUp)
   {
      //map the pixel value from pixel to world coordinates
      //(using the local_transform transformation)
      //then using the rcToGlobal transformation, map the pixel coordinate 
      //to the correct row
      float pseudoIndex = rcToGlobal.MapYFrom(local_transform.MapYFrom(px));
      return getValidIndex(pseudoIndex, maxIndex, roundUp);
   }
   
   /**
    * Given a floating point number, this method gets the closest 
    * corresponding index.  The index is forced to be in the range 
    * [0,maxIndex).
    * 
    * @param pseudoIndex The floating point number close to an index.
    * @param maxIndex The smallest positive invalid index.  If this parameter 
    *                 is 5, then the indexes, 0, 1, 2, 3, and 4 are valid 
    *                 indices and 5 is the first invalid index.
    * @param roundUp True if this method should round up to the nearest 
    *                index and false if it should round down to the nearest 
    *                index.
    * @return The closest valid index to the given floating point 'pseudo' 
    *         index either rounded up or down.
    */
   private int getValidIndex(float pseudoIndex, int maxIndex, boolean roundUp)
   {
      int index;
      if (roundUp)
         index = (int)Math.ceil(pseudoIndex);
      else
         index = (int)Math.floor(pseudoIndex);
      
      /*
      //check if the index found is already an integer.
      //if it isn't round up
      int index;
      if ( (pseudoIndex-(int)pseudoIndex)==0 )
         index = (int)pseudoIndex;
      else
         index = (int)pseudoIndex+1;
      */
      
      //if the index is out of bounds 
      //change it so that it is back in bounds
      if (index<0)
         index = 0;
      else if (index>=maxIndex)
         index = maxIndex-1;

      return index;
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
      float xMin = 0;
      float xMax = 10;
      int numXSteps = 100;
      
      float yMin = 0;
      float yMax = 10;
      int numYSteps = 100;
      
      
      float deltaX = (xMax-xMin)/numXSteps;
      float deltaY = (yMax-yMin)/numYSteps;
      
      float x,y; //holds the values of x and y
      float[][] dataArr = new float[numXSteps][numYSteps];
      for (int i=0; i<dataArr.length; i++)
         for (int j=0; j<dataArr[i].length; j++)
         {
            x = (xMin+deltaX*i);
            y = (yMin+deltaY*j);
            dataArr[i][j] = x*x*y*y;
         }
      
      return dataArr;
   }
   
   /**
    * Generates a IVirtualArray2D for the data for the function 
    * f(x,y) = x+y
    */
   public static IVirtualArray2D getTestData()
   {
      return new VirtualArray2D(getTestDataArr());
   }
   
   /**
    * Testbed.
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      float[] levels = new float[5];
         levels[0] = 0;
         levels[1] = 10;
         levels[2] = 2;
         levels[3] = 7;
         levels[4] = 12.2332f;
      ContourJPanel panel = new ContourJPanel(getTestData(), 0, 200, 20);
      //ContourJPanel panel = new ContourJPanel(getTestData(),levels);
         panel.setPreserveAspectRatio(false);
      JFrame frame = new JFrame("ContourJPanel test");
         frame.setSize(200,200);
         frame.getContentPane().add(panel);
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setVisible(true);
   }
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
