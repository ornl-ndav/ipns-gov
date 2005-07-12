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
 * Revision 1.15  2005/07/12 16:47:27  kramer
 * -Added javadocs and comments.
 * -Implemented the IPreserveState interface.  Also, in addition to the
 *  'normal' objects stored in the state, the Class of the Contours object
 *  being displayed is stored so that the correct Contours object can be
 *  constructed when the setObjectState() method is invoked.
 * -Implemented the getGlobalWCBounds() which uses the getAxisInfo() method
 *  of the IVirtualArray2D to correctly set the world coordinates of the
 *  panel.  However, to get zooming and viewer synchronization to both
 *  work, this class resets the 'local_transforms' 'source' to be the
 *  world coordinates of the entire panel whenever the zoom is reset.
 *  However, this seems to affect the way the aspect ratio is preserved.
 *
 * Revision 1.14  2005/06/28 16:11:36  kramer
 *
 * Added support for specifing the background color and the colors used to
 * draw the contour lines (a single color, an array of colors, or a
 * color scale are currently supported).
 *
 * Revision 1.13  2005/06/23 20:53:32  kramer
 *
 * Made the setPreserveAspectRatio() method overriden so that the image
 * would be immediately redrawn to reflect the change.
 *
 * Revision 1.12  2005/06/22 22:24:53  kramer
 *
 * Removed unused code, added javadocs, changed the default values in this
 * class so that they more logically relate to the default values in the
 * ContourViewComponent and CompositeContourControl classes, and made the
 * contour labels more compact.
 *
 * Revision 1.11  2005/06/17 19:48:22  kramer
 *
 * Removed the unused import to ContourLevelMetaData (which I never checked
 * into cvs because it was not needed).
 *
 * Revision 1.10  2005/06/17 19:25:18  kramer
 *
 * Added functionality to set the line style of, if labels are drawn for,
 * and the number of significant digits for each contour level.
 *
 * Revision 1.9  2005/06/16 19:47:05  kramer
 *
 * Added support to enable/disable contour labels and to specify the
 * number of significant digits the contour labels are rounded to (and if
 * they are even rounded).
 *
 * Revision 1.8  2005/06/16 16:07:41  kramer
 *
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

import gov.anl.ipns.Util.Numeric.Format;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.Contours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.NonUniformContours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.UniformContours;
import gov.anl.ipns.ViewTools.Panels.Graph.GraphJPanel;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.Arrays;
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
   /** Identifies a line style as a solid line. */
   public static final int SOLID = GraphJPanel.LINE;
   /** Identifies a line style as a dashed line. */
   public static final int DASHED = GraphJPanel.DASHED;
   /** Identifies a line style as a dashed-dotted line. */
   public static final int DASHED_DOTTED = GraphJPanel.DASHDOT;
   /** Identifies a lines style as a dotted line. */
   public static final int DOTTED = GraphJPanel.DOTTED;
   
//--------------------------=[ ObjectState keys ]=----------------------------//
   /**
    * "Contours class" - This static constant String is a key used for 
    * referencing the particular class that the <code>Contours</code> 
    * object used in this class is an instance of.  The value that this 
    * key references is a <code>Class</code> object.  
    */
   public static final String CONTOURS_CLASS_KEY = "Contours class";
   /**
    * "Contours" - This static constant String is a key used for 
    * referencing the state information of the <code>Contours</code> object 
    * which encapsulates the contour levels that this class renders.  The 
    * value that this key references is an <code>ObjectState</code> of a 
    * <code>Contours</code> object.
    */
   public static final String CONTOURS_KEY = "Contours";
   /**
    * "Line styles" - This static constant String is a key used for 
    * referencing the which line styles are used when rendering the 
    * contour levels.  The value that this key references is an 
    * int array where each integer is a code specifying a line style.  
    * <p>
    * If <code>styleArr</code> is a reference to the array of line 
    * styles, then the <code>ith</code> contour line is rendered with 
    * the line style specified by element at index 
    * <code>i%(styleArr.length)</code> in the array.
    */
   public static final String LINE_STYLES_KEY = "Line styles";
   /**
    * "Show labels" - This static constant String is a key used for 
    * referencing which contour labels are given labels as they are 
    * rendered.  The value that this key references is a boolean 
    * array.  Each element in the array is either <code>true</code> if 
    * the label should be displayed and <code>false</code> if it 
    * shouldn't be.
    * <p>
    * If <code>labelArr</code> is a reference to the array specifying 
    * which contour lines have labels, then the <code>ith</code> contour 
    * line is given a label if the element in the array at index 
    * <code>i%(labelArr.length)</code> is <code>true</code>.  If the 
    * element is <code>false</code>, the contour level is not given a 
    * label.
    */
   public static final String SHOW_LABEL_KEY = "Show labels";
   /**
    * "Num of significant figures" - This static constant String is a key 
    * used for referencing the number of significant figures that that the 
    * contour labels are rounded to.  The value that this key references is 
    * an integer.
    */
   public static final String NUM_SIG_FIGS_KEY = "Num of significant figures";
   /**
    * "Color scale" - This static constant String is a key used for 
    * referencing the array of colors used to determine which color to 
    * render a particular contour line.  The value that this key 
    * references is an array of <code>Color</code> objects.
    */
   public static final String COLOR_SCALE_KEY = "Color scale";
   /**
    * "Background color" - This static constant String is a key used for 
    * referencing the background color of the image.  The value that this 
    * key references is a <code>Color</code> object.
    */
   public static final String BACKGROUND_COLOR_KEY = "Background color";
//------------------------=[ End ObjectState keys ]=--------------------------//
   
//------------------------=[ Default field values ]=--------------------------//
   /**
    * The default class that the <code>Contours</code> object used in this 
    * class is an instance of.
    */
   public static final Class DEFAULT_CONTOURS_CLASS = UniformContours.class;
   /**
    * The default line style used to render contour lines.
    * Its value is <code>SOLID</code>.
    */
   public static final int DEFAULT_LINE_STYLE   = SOLID;
   /**
    * Specifies if contour labels are shown by default.  
    * Its value is <code>true</code>.
    */
   public static final boolean DEFAULT_SHOW_LABEL   = true;
   /**
    * Specifies the default number of significant figures the contour 
    * labels are rounded to.  Its value is <code>3</code>.
    */
   public static final int DEFAULT_NUM_SIG_DIGS = 3;
   /**
    * Specifies if by default the aspect ratio should be preserved when 
    * drawing the contour levels.  Its value is <code>false</code>.
    */
   public static final boolean DEFAULT_PRESERVE_ASPECT_RATIO = false;
   /** The display's default background color. */
   public static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
   /**
    * The default array of colors that is used to determine what color 
    * to draw a color line of a given height.
    */
   public static final Color[] DEFAULT_COLOR_SCALE = new Color[] {Color.BLACK};
//----------------------=[ End default field values ]=------------------------//
   
//------------------------------=[ Fields ]=----------------------------------//
   /** Holds the data that is being drwn on this panel. */
   private IVirtualArray2D data2D;
   /**
    * Holds information which contours to draw (i.e. at which 'heights' will 
    * slices be taken from the surface and drawn as contours)
    */
   private Contours levels;
   /**
    * Boolean flag that marks if the contour graph has been drawn yet or not.  
    * <p>
    * Its initial value is 'true' and whenever the zoom is reset its value is 
    * reset to 'true' otherwise it is 'false'.  The paint() method uses this 
    * value to determine if it should set the source (call setSource()) on the 
    * local transform (local_transform) (if 'true') or do nothing with the 
    * locl transform (if 'false').
    * <p>
    * <b>
    * Currently, this seems to be a way to get the contour graph to 
    * support <i>both</i> zooming and have the correct world coordinates set.  
    * However, there may be a better solution.
    * </b>
    */
   private boolean firstPaint;
   
   /** Describes the line styles used to render the contour levels. */
   private int[] lineStyles;
   /** Describes which contour levels are rendered with labels. */
   private boolean[] showLabels;
   /**
    * Describes the number of significant figures each contour level's 
    * label is formatted to (and if there is even any formatting done).
    */
   private int[] numSigFigs;
   /**
    * The array of colors that are referenced when determining which 
    * color to draw a contour line of a given height.
    */
   private Color[] colorScale;
//----------------------------=[ End fields ]=--------------------------------//
   
//---------------------------=[ Constructors ]=-------------------------------//
   /**
    * Private constructor used to initialize the 
    * {@link IVirtualArray2D IVirtualArray2D} {@link #data2D data2D} and 
    * set up some of the general properties of the panel.
    * 
    * @param data2D The {@link IVirtualArray2D IVirtualArray2D} whose data 
    *               is plotted.
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
      
      //configure the way the contour image is drawn
      setLineStyles(DEFAULT_LINE_STYLE);
      setShowAllLabels(DEFAULT_SHOW_LABEL);
      setNumSigDigits(DEFAULT_NUM_SIG_DIGS);
      
      setPreserveAspectRatio(DEFAULT_PRESERVE_ASPECT_RATIO);
      setColorScale(DEFAULT_COLOR_SCALE);
      setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
      
      //set that the contour image has not been drawn yet
      this.firstPaint = true;
      //listen to changes in zooming
      addActionListener(new ActionListener()
      {
         /**
          * Called whenever an 'event' corresponding to 
          * this panel has occured (i.e. the panel was 
          * zoomed, the zoom was reset, etc).
          */
         public void actionPerformed(ActionEvent event)
         {
            //if the zoom was rest act like the contour image 
            //has never been drawn yet
            if (event.getActionCommand().equals(RESET_ZOOM))
               firstPaint = true;
         }
      });
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
//-------------------------=[ End constructors ]=-----------------------------//
   
//-----------=[ Methods implemented for the IPreserveState interface ]=-------//
   /**
    * Used to get the state information for this contour image plotter.  
    * The state information contains information such as which contour lines 
    * are drawn, their line styles, which lines have labels, the number of 
    * significant figures displayed on the labels, etc.  These properties 
    * can be accessed by keys which are the 
    * <code>public static final String</code> fields of this class.
    * 
    * @param isDefault If true the default state of this contour plotter is 
    *                  returned.  If false the current state is returned.
    * @return The state of this contour plotter.
    */
   public ObjectState getObjectState(boolean isDefault)
   {
      ObjectState state = super.getObjectState(isDefault);
        //first set the state of the Contours that this class renders
        state.insert(CONTOURS_KEY, levels.getObjectState(isDefault));
        
        //then set the state information for everything else
        if (isDefault)
        {
           //store the Contours object's specific class so that the 
           //specific Contours subclass can be created later
           state.insert(CONTOURS_CLASS_KEY, DEFAULT_CONTOURS_CLASS);
           
           state.insert(LINE_STYLES_KEY, new int[]{ DEFAULT_LINE_STYLE });
           state.insert(SHOW_LABEL_KEY, new boolean[]{ DEFAULT_SHOW_LABEL });
           state.insert(NUM_SIG_FIGS_KEY, new int[]{ DEFAULT_NUM_SIG_DIGS });
           state.insert(COLOR_SCALE_KEY, DEFAULT_COLOR_SCALE);
           state.insert(BACKGROUND_COLOR_KEY, DEFAULT_BACKGROUND_COLOR);
        }
        else
        {
           //set the class of the contours object so that the specific 
           //contours object can be constructed later in setObjectState(....)
           //In other words, a Contours object has many subclasses and storing  
           //the class helps ensure that the correct subclass is used
           state.insert(CONTOURS_CLASS_KEY, levels.getClass());
           
           state.insert(LINE_STYLES_KEY, lineStyles);
           state.insert(SHOW_LABEL_KEY, showLabels);
           state.insert(NUM_SIG_FIGS_KEY, numSigFigs);
           state.insert(COLOR_SCALE_KEY, colorScale);
           state.insert(BACKGROUND_COLOR_KEY, getBackgroundColor());
        }
      return state;
   }
   
   /**
    * Used to set the state of this contour plotter.  The state contains 
    * information such as which contour lines are drawn, their line styles, 
    * which lines have labels, the number of significant figures displayed 
    * on the labels, etc.  These settings can be set using keys which are the 
    * <code>public static final String</code> fields of this class.
    * 
    * @param state An encapsulation of the state information for this 
    *              contour plotter.
    */
   public void setObjectState(ObjectState state)
   {
      //if the state is 'null' leave
      if (state==null)
         return;
      
      //set the state that the superclass maintains
      super.setObjectState(state);
      
      //first, try to make sure that the Contours object is the right type 
      //(i.e. the right subclass)
      Object val = state.get(CONTOURS_CLASS_KEY);
      if ( (val != null) && (val instanceof Class) )
      {
         try
         {
            //create a new instance that is the correct subclass
            levels = (Contours)((Class)val).newInstance();
         }
         catch (Exception e)
         {
            //if something goes wrong leave 'levels' the way it is 
            //and just inform the user
            SharedData.addmsg("Warning:  the specific contour levels " +
                              "used could not be recreated");
            System.out.println("contours class = "+((Class)val).getName());
            e.printStackTrace();
         }
      }
      
      //set the state for the Contours rendered by this class
      val = state.get(CONTOURS_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         levels.setObjectState((ObjectState)val);
         
      //set the line styles
      val = state.get(LINE_STYLES_KEY);
      if ( (val != null) && (val instanceof int[]) )
         setLineStyles((int[])val);
      
      //set which labels are displayed
      val = state.get(SHOW_LABEL_KEY);
      if ( (val != null) && (val instanceof boolean[]) )
         setShowLabels((boolean[])val);
      
      //set the number of significant figures for each level
      val = state.get(NUM_SIG_FIGS_KEY);
      if ( (val != null) && (val instanceof int[]) )
         setNumSigDigits((int[])val);
      
      //set the color scale
      val = state.get(COLOR_SCALE_KEY);
      if ( (val != null) && (val instanceof Color[]) )
         setColorScale((Color[])val);
      
      //set the background color
      val = state.get(BACKGROUND_COLOR_KEY);
      if ( (val != null) && (val instanceof Color) )
         setBackgroundColor((Color)val);
      
      //redraw the image
      reRender();
   }
//--------=[ End methods implemented for the IPreserveState interface ]=------//
   
   /**
    * Use this method to change the data that is being displayed on the 
    * contour graph.  This method then redraws the graph to reflect the 
    * new data.
    * 
    * @param arr The new data that should be plotted.
    */
   public void changeData(IVirtualArray2D arr)
   {
      data2D = arr;
      reRender();
   }
   
   /**
    * Get the contours that are currently being plotted.
    * 
    * @return The contours that are being plotted for the current data.
    */
   public Contours getContours()
   {
      return levels;
   }
   
   /**
    * Set the contours that should be plotted.  Along with setting the 
    * new contours, this method immediately redraws the graph.
    * 
    * @param levels The contours that should be plotted.
    */
   public void setContours(Contours levels)
   {
      this.levels = levels;
      reRender();
   }
   
   /**
    * Used to get the line styles that a given contour line is rendered as.  
    * The first contour line rendered uses the line style of the first 
    * element of the array returned.  The second line uses the second 
    * element of the array etc.  When the end of the array is reached, 
    * reading starts over from the start of the array.
    * 
    * @return An array of integers each designating a line style
    * 
    * @see #SOLID
    * @see #DASHED
    * @see #DASHED_DOTTED
    * @see #DOTTED
    */
   public int[] getLineStyles()
   {
      return lineStyles;
   }
   
   /**
    * Used to get the line styles that a given contour line is rendered as.  
    * The first contour line rendered uses the line style of the first 
    * element of the array given.  The second line uses the second 
    * element of the array etc.  When the end of the array is reached, 
    * reading starts over from the start of the array.
    * 
    * @param lineStyles An array of integers each designating a line style
    * 
    * @see #SOLID
    * @see #DASHED
    * @see #DASHED_DOTTED
    * @see #DOTTED
    */
   public void setLineStyles(int[] lineStyles)
   {
      if (lineStyles==null || lineStyles.length==0)
         setLineStyles(DEFAULT_LINE_STYLE);
      else
         this.lineStyles = lineStyles;
      
      reRender();
   }
   
   /**
    * Configures this renderer to draw each line with the line style given.
    * 
    * @param lineStyle Designates the line style to render each of the 
    *                  contour lines as.
    * 
    * @see #SOLID
    * @see #DASHED
    * @see #DASHED_DOTTED
    * @see #DOTTED
    */
   public void setLineStyles(int lineStyle)
   {
      setLineStyles(new int[] {lineStyle});
   }
   
   /**
    * Used to determine which contour lines have labels rendered.  The first 
    * contour line rendered uses labels if and only if the first element 
    * in the array returned is 'true'.  The second contour line uses the 
    * second element in the array etc.  When the end of the array is 
    * reached, reading starts over from the start of the array.
    * 
    * @return The array of booleans which describe which contour lines are 
    *         drawn with labels.
    */
   public boolean[] getShowLabels()
   {
      return showLabels;
   }
   
   /**
    * Used to set which contour lines have labels rendered.  The first 
    * contour line rendered uses labels if and only if the first element 
    * in the array given is 'true'.  The second contour line uses the 
    * second element in the array etc.  When the end of the array is 
    * reached, reading starts over from the start of the array.
    * 
    * @param showLabels The array of booleans which describe which 
    *                   contour lines are drawn with labels.
    */
   public void setShowLabels(boolean[] showLabels)
   {
      if (showLabels==null || showLabels.length==0)
         setShowAllLabels(DEFAULT_SHOW_LABEL);
      else
         this.showLabels = showLabels;
      
      reRender();
   }
   
   /**
    * Used to specify if all or none of the contour levels are rendered 
    * with their labels drawn.
    * 
    * @param b True if all contour levels should be rendered with their 
    *          labels drawn and false if none of the contour levels should 
    *          be rendered with their labels drawn.
    */
   public void setShowAllLabels(boolean b)
   {
      setShowLabels(new boolean[] {b});
   }
   
   /**
    * Used to specify that a uniform sequence of contour lines should have 
    * their labels drawn.  In other words, if contour line 1 is the first 
    * contour line drawn, then the contour lines, 
    * <code>1, 1+num, 1+2*num, 1+3*num, ....</code> are the only lines 
    * drawn with labels.
    * 
    * @param num Specifies the multiple of the contour lines that are drawn 
    *            with labels.
    */
   public void setShowLabelEvery(int num)
   {
      if (num<=0)
         setShowAllLabels(DEFAULT_SHOW_LABEL);
      else
      {
         boolean[] arr = new boolean[num];
         Arrays.fill(arr, false);
         arr[0] = true;
         setShowLabels(arr);
      }
   }
   
   /**
    * Used to get the number of significant figures that the contour levels 
    * are rounded to.
    * 
    * @return The first contour level rendered has its label rounded to the 
    *         number of figures as specified by the first element in this 
    *         array.  The second contour level similarly uses the second 
    *         element of this array etc.  When, the end of the array is 
    *         reached, reading starts over at the start of the array.  
    *         Note:  A nonpositive number designates that the contour 
    *                labels should not be formatted.
    */
   public int[] getNumSigDigits()
   {
      return numSigFigs;
   }
   
   /**
    * Used to set the number of significant figures a contour label is 
    * rounded to.  When the contours are rendered, the first contour drawn 
    * uses the number of significant figures as specified by the first 
    * value in this array.  The next contour rendered uses the second element 
    * in the array etc.  When, the end of the array is reached, the contour 
    * starts over using the start of the array.
    * <p>
    * Note:  In the array, a nonpositive number designates that the label 
    *        should not be formatted.  Otherwise, a positive number 
    *        designates that the label should be formatted to the specified 
    *        number of significant figures.
    * 
    * @param numSigDigs Holds the number of significant digits used when 
    *                   rendering the contour's labels.
    */
   public void setNumSigDigits(int[] numSigDigs)
   {
      if (numSigDigs==null)
         setNumSigDigits(DEFAULT_NUM_SIG_DIGS);
      else
         this.numSigFigs = numSigDigs;
      
      reRender();
   }
   
   /**
    * Used to set all of the contour levels to render their labels using 
    * the specified number of significant figures.
    * 
    * @param numSigDigs The number of significant figures that each contour 
    *                   label is rounded to.  A nonpositive number 
    *                   designates that the contour levels should not have 
    *                   their labels formatted.
    */
   public void setNumSigDigits(int numSigDigs)
   {
      setNumSigDigits(new int[] {numSigDigs});
   }
   
   /**
    * Contour levels can be drawn using different colors depending on their 
    * "elevation".  This method returns the colors used to determine the 
    * colors to use for an elevation.
    * 
    * @return The array of colors used to determine what color to draw the 
    *         contour lines.  The contour line of lowest elevation is 
    *         drawn using the color specified by the first element of this 
    *         array.  The contour line of highest elevation is drawn using 
    *         the color specified by the last element of this array.
    */
   public Color[] getColorScale()
   {
      return colorScale;
   }
   
   /**
    * Contour levels can be drawn using different colors depending on their 
    * "elevation".  This method is used to set the scale used to determine 
    * which color to render a contour line with.
    * 
    * @param scaleType A String describing the range of colors to use.  
    *                  This String must be one of the fields from the 
    *                  {@link IndexColorMaker IndexColorMaker} class.
    * @param doubleSided If true negative and positive "elevations" will be 
    *                    drawn with different colors.  If false, a negative 
    *                    "elevation" will be drawn with the same color as 
    *                    its positive version.
    */
   public void setColorScale(String scaleType, boolean doubleSided)
   {
      if (scaleType==null)
         return;
    
      int numColors = 256;
      if (doubleSided)
         setColorScale(IndexColorMaker.getDualColorTable(scaleType, numColors));
      else
         setColorScale(IndexColorMaker.getColorTable(scaleType, numColors));
   }
   
   /**
    * Contour levels can be drawn using different colors depending on their 
    * "elevation".  This method is used to set the scale used to determine 
    * which color to render a contour level with.
    * 
    * @param colors The array of colors used to determine which color to 
    *               render a contour level with.  The lowest contour level 
    *               is drawn using the color specified by the first element 
    *               of this array.  The highest contour level is drawn 
    *               using the color specified by the last element of this 
    *               array.  The other elevations are colored using the 
    *               elements between the first and last elements in the 
    *               array.
    */
   public void setColorScale(Color[] colors)
   {
      if (colors==null || colors.length<1)
         return;
      
      this.colorScale = colors;
      reRender();
   }
   
   /**
    * Used to specify that all of the contour lines should be drawn using 
    * the same line color.
    * 
    * @param color The line color used when drawing the contour lines.
    */
   public void setColorScale(Color color)
   {
      setColorScale(new Color[] {color});
   }
   
   /**
    * Used to get the background color drawn behind all of the contor levels.
    * 
    * @return This panel's background color.
    */
   public Color getBackgroundColor()
   {
      return getBackground();
   }
   
   /**
    * Used to set the background color drawn behind all of the contour levels.
    * 
    * @param color This panel's new background color.
    */
   public void setBackgroundColor(Color color)
   {
      if (color==null)
         return;
      
      setBackground(color);
      reRender();
   }
   
   /**
    * Used to set if the aspect ratio should be preserved or not.  This 
    * method also redraws the image so that it reflects the new state of 
    * the aspect ratio.
    * 
    * @param preserve True if the aspect ratio should be preserved and 
    *                 false if it shouldn't be preserved.
    */
   public void setPreserveAspectRatio(boolean preserve)
   {
      super.setPreserveAspectRatio(preserve);
      //setLocalWorldCoords(getLocalWorldCoords());
      reRender();
   }
   
   /**
    * Used to force the contour graph to be redrawn.
    */
   public void reRender()
   {
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
      //get the CoordBounds object that encapsulates the bounds of 
      //the panel in world coordinates
        CoordBounds wcBounds = getGlobalWCBounds();
      //now to make a transform that maps from row/column to the world 
      //coordinates of the entire panel.  The row/col is mapped to the 
      //location on the ENTIRE panel because the ALL of the data in the 
      //array needs to be mapped to ALL of the panel (not just a small 
      //region which is what 'local_transform' describes).
        float delta = 0.001f;
        CoordTransform rcToWC = 
             new CoordTransform(
                                new CoordBounds(delta, 
                                                delta, 
                                                data2D.getNumColumns()-delta, 
                                                data2D.getNumRows()-delta
                                               ),
                                wcBounds
                               );
        //if this is the first time the contour image is being painted 
        //set the local_transform to have its input region be the output 
        //region of 'rcToWC'.  After the contour image is drawn the user can 
        //zoom in and the superclass handles setting the source (input) of 
        //local_transform to be the correct subset of the entire panel.
        if (firstPaint)
        {
           local_transform.setSource(wcBounds);
           firstPaint = false;
        }

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
        int xMinIndex = getXIndexFromPixel(0, rcToWC, arr.length, true);
        int xMaxIndex = getXIndexFromPixel(getWidth(), rcToWC, 
                                           arr.length, false);
        
        int yMinIndex = getYIndexFromPixel(0, rcToWC, arr[0].length, true);
        int yMaxIndex = getYIndexFromPixel(getHeight(), rcToWC, 
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
        
      //for each level draw the contour
      //the following levels are 'sticky'.  In other words if they are 
      //defined for one level but not the next, the values from the previous 
      //level stick
        //the line style
          int lineSytle = DEFAULT_LINE_STYLE;
        //if labels should be displayed
          boolean showLabel = DEFAULT_SHOW_LABEL;
        //the number of significant digits
          int numSigDigits = DEFAULT_NUM_SIG_DIGS;
      for (int i=0; i<levels.getNumLevels(); i++)
      {
         //first to initialize the variables used to draw this level
           minGradIndex = 0;
           minGradSqr = Float.MAX_VALUE;
           //now to get this level's meta data
             lineSytle = lineStyles[i%lineStyles.length];
             showLabel = showLabels[i%showLabels.length];
             numSigDigits = numSigFigs[i%numSigFigs.length];
         
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
           rcToWC.MapXListTo(xrcVals);
           rcToWC.MapYListTo(yrcVals);
           
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
              //if labels are enabled
              if ( showLabel && (j==minGradIndex || (j+1)==minGradIndex) )
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
                   
                   //now determine if the label output should be formatted
                   //(i.e. should the number be rounded)
                   String label;
                   if (numSigDigits>0)
                      label = Format.choiceFormat(levels.getLevelAt(i), 
                                                  Format.AUTO, 
                                                  numSigDigits);
                   else
                      label = ""+levels.getLevelAt(i);
                   //trim any extra whitespaces
                   label = label.trim();
                   
                   g.drawString(label,
                                (int)(avX),
                                (int)(avY));
                 g.setTransform(trans);
              }
              
               //now to set the line color
               g.setColor(getColorForLevel(levels.getLevelAt(i)));
               //now to set the line style
               g.setStroke(GraphJPanel.createStroke(lineSytle, 1));
               
               //and finally now to draw the line
               g.drawLine((int)(xrcVals[j]),
                          (int)(yrcVals[j]),
                          (int)(xrcVals[j+1]),
                          (int)(yrcVals[j+1]));
           }
      }
   }
   
   /**
    * Used to get the rectangular box that describes the bounds of the 
    * entire panel in terms of the world coordinate system.
    * 
    * @return An encapsulation of the world coordinate bounds of the entire 
    *         panel.
    */
   private CoordBounds getGlobalWCBounds()
   {
      AxisInfo xInfo = this.data2D.getAxisInfo(AxisInfo.X_AXIS);
      AxisInfo yInfo = this.data2D.getAxisInfo(AxisInfo.Y_AXIS);
      
      return new CoordBounds(xInfo.getMin(), yInfo.getMax(), 
                             xInfo.getMax(), yInfo.getMin());
   }
   
   /**
    * Used to look at the "elevation" of a particular contour level and 
    * find the color that should be used when drawing the contour level.
    * 
    * @param height The "elevation" of the contour line in question.
    */
   private Color getColorForLevel(float height)
   {
      float max = levels.getHighestLevel();
      float min = levels.getLowestLevel();
      
      float a = (colorScale.length-1)/(max-min); 
      
      return colorScale[(int)(a*height - a*min)];
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
      
      //if the index is out of bounds 
      //change it so that it is back in bounds
      if (index<0)
         index = 0;
      else if (index>=maxIndex)
         index = maxIndex-1;

      return index;
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
         panel.setLineStyles(new int[] {SOLID, DASHED, DASHED});
         panel.setShowLabels(new boolean[]{true, false, false});
         panel.setNumSigDigits(new int[]{3,0,0});
         panel.setPreserveAspectRatio(false);
         panel.setColorScale(new Color[] {Color.RED,
                                          Color.ORANGE, 
                                          Color.YELLOW, 
                                          Color.GREEN, 
                                          Color.CYAN, 
                                          Color.BLUE, 
                                          Color.MAGENTA, 
                                          Color.PINK,
                                          Color.GRAY, 
                                          Color.BLACK});
      JFrame frame = new JFrame("ContourJPanel test");
         frame.setSize(200,200);
         frame.getContentPane().add(panel);
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setVisible(true);
   }
}

//----------------------------=[ Unused code ]=-------------------------------//
/*
/**
 * Temporary method used to print the contents of a float[][] array.
 *
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
    * The default state for the <code>Contours</code> that are rendered by 
    * this class.  Its value is the <code>ObjectState</code> returned from 
    * the <code>getObjectState()</code> method invoked on a 
    * <code>UniformContours</code> object where the parameter 
    * <code>isDefault=true</code> is given to the 
    * <code>getObjectState()</code> method.
    /
   public static final ObjectState DEFAULT_CONTOURS_STATE = 
      (new UniformContours(UniformContours.DEFAULT_MIN_VALUE, 
                           (UniformContours.DEFAULT_NUM_LEVELS-1)*
                                                UniformContours.DEFAULT_DELTA, 
                           UniformContours.DEFAULT_NUM_LEVELS)).
                              getObjectState(true);
   
*/

/*
private Color fixColors(Color c1, Color c2, int delta)
{
   //get the color's 'coordinates'
   //think of a color as a point in R^3 
   //three dimensional space
   int red1 = c1.getRed();
   int red2 = c2.getRed();
   
   int green1 = c1.getGreen();
   int green2 = c2.getGreen();
   
   int blue1 = c1.getBlue();
   int blue2 = c2.getBlue();
   
   //find the distance between the points
   double distance = Math.sqrt( (red1-red2)*(red1-red2) + 
                                (green1-green2)*(green1-green2) +
                                (blue1-blue2)*(blue1-blue2) );
   double deltaD = Math.sqrt(3)*Math.abs(delta);
   
   Color newColor;
   if (distance<deltaD)
      newColor = new Color(red1-delta, green1-delta, blue1-delta);
   else
      newColor = c1;
   
   return newColor;
}
*/

//--------------------------=[ End unused code ]=-----------------------------//
