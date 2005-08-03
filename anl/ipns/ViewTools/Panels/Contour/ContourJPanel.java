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
 * Revision 1.23  2005/08/03 15:56:50  kramer
 * -Changed the field 'firstPaint' to 'wcNotInit' because it stores if the
 *  world coordinates have been initialized or not.
 * -Added and improved the javadocs.
 * -Rearranged the code to make it easier to read.  For example, the code
 *  is now divided into sections like:  fields, constructors,
 *  getter/setter methods, private methods, ....
 *
 * Revision 1.22  2005/08/02 21:54:00  kramer
 *
 * -Added the field 'mainImageNotInit'.  This field is used to store if the
 *  main contour image has been rendered yet.  This is now used to determine
 *  if a new thumbnail should be rendered.  This maintains the faster
 *  startup speed of the ContourViewComponent, but removes the bug where
 *  the thumbnail would go blank when the main image was double clicked.
 * -Added javadocs and clarified some of the code's comments.
 * -Rearranged some of the code in the draw() method.  However, the code
 *  still has the same functionality.
 *
 * Revision 1.21  2005/08/02 16:49:45  kramer
 *
 * Modified the getThumbnail() method so that if the main contour image
 * hasn't been drawn yet, the method doesn't even try to draw the thumbnail
 * (because there is no image for it to reflect).  This greatly improves the
 * startup time for the ContourViewComponent (that uses this class).
 *
 * Revision 1.20  2005/08/01 23:23:01  kramer
 *
 * Modified the setColorScale(Color[]) and setLineStyles(int[]) methods.
 * They included a 'while' loop, except their counter was never incremented.
 * Now the counter is incremented so that the while loops will actually end.
 *
 * Revision 1.19  2005/07/28 23:17:13  kramer
 *
 * -Now if the labels or number of significant figures is changed the
 *  thumbnail is not invalidated (this reduces the number of times the
 *  thumbnail needs to be redrawn because the labels are not shown in the
 *  thumbnail anyway).
 * -Added comments to the getThumbnail() method and modified it to paint the
 *  entire thumbnail the same color as the panel's background before it
 *  starts drawing the thumbnail.  This seems to fix the problem that was
 *  occuring where there was a black stripe in the thumbnail.  However, it
 *  is an expensive solution.
 * -Now the thumbnail is not valid if the big image has been drawn yet.  This
 *  fixes the problem where the big image and thumbnail were out of sync.
 * -Modified the setter methods to not invalidate the thumbnail unless the
 *  value given to the setter method actually changes the value already saved.
 *  This reduces the number of times the thumbnail needs to be redrawn.
 *
 * Revision 1.18  2005/07/28 15:58:46  kramer
 *
 * -Changed all occurences of SharedData to SharedMessages.  Now this class
 *  does not use any class from the DataSetTools.* packages.
 * -Added the getThumbnail() and invalidateThumbnail() methods.
 * -Changed the paint() method to a private 'draw()' method.  Now, when the
 *  paint() method or getThumbnail() method is invoked, this draw() method
 *  is invoked (and given the corresponding Graphics2D to draw into).
 * -Modified all setter methods to not call reRender() and instead call
 *  invalidateThumbnail() if applicable.
 *
 * Revision 1.17  2005/07/25 20:14:36  kramer
 *
 * Added a method to generate the row/column to world coordinate
 * transformation.  Also, added methods to get the row or column in the
 * array of data given an x or y pixel location.
 *
 * Revision 1.16  2005/07/19 18:53:57  kramer
 *
 * Added javadocs.  Also, now this class supports using a logarithmic scale
 * to determine the color to draw a specific contour line with.
 *
 * Revision 1.15  2005/07/12 16:47:27  kramer
 *
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
import gov.anl.ipns.Util.Sys.SharedMessages;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.PseudoLogScaleUtil;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.TwoD.Contour.ContourViewComponent;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.Contours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.NonUniformContours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.UniformContours;
import gov.anl.ipns.ViewTools.Panels.Graph.GraphJPanel;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/* TODO List
 * 1.  Compare drawing the lines based on the points located at pixel 
 *     corners or pixel centers.  (Done)
 * 2.  Label the contour lines (or use line types).  (Done)
 * 3.  Figure out how to color the lines and possibly use colored regions.
 *     (for now don't worry about filling colored regions)
 *     Can look at it as an image with the contours overlayed on top of it 
 *     and each pixel is given a color depending on if it is betweeen certain 
 *     levels.  (Right now the contour lines are colored)
 * 4.  Algorithm for finding how to chain the segments found together.
 */
/**
 * This class is a special type of <code>JPanel</code> that has the ability 
 * to draw a contour plot that describes an <code>IVirtualArray2D</code> of 
 * data.
 * <p>
 * With drawing the contour lines, you can:
 * <ul>
 *   <li>
 *     Specify the contours levels to render
 *   </li>
 *   <li>
 *     Specify the contour colors
 *   </li>
 *   <li>
 *     Specify the contour styles
 *   </li>
 *   <li>
 *     Specify if the contour lines should be labeled
 *   </li>
 *   <li>
 *     If the contour lines are labeled, specify the number of significant 
 *     figures reflected in the labels
 *   </li>
 *   <li>
 *     Specify the contour plot's background color
 *   </li>
 *   <li>
 *     Acquire a thumbnail image of the main contour plot
 *   </li>
 *   <li>
 *     Specify that the plot should be drawn with the aspect ratio 
 *     preserved or not preserved
 *   </li>
 *   <li>
 *     Zoom in on a region of the plot
 *   </li>
 * </ul>
 * <p>
 * This class is designed such that there are getter/setter methods to 
 * get and set properties that modify the way the contour plot is rendered.  
 * Except when specified otherwise, these setter methods don't immediately 
 * re-render the contour plot.  Instead, after invoking a setter method, the 
 * {@link #reRender() reRender()} method should be invoked to re-render the 
 * contour plot.  This improves efficiency because several properties can 
 * be set and then the contour plot can be re-rendered <b>one time</b> to 
 * reflect all of the changed properties (instead of re-rendering the 
 * contour plot several times for each changed property).
 * <p>
 * This class caches the thumbnail it creates.  Thus, when the thumbnail is 
 * requested (with the {@link #getThumbnail(int, int) getThumbnail(int, int)} 
 * method), a new thumbnail is not redrawn if the cached copy accurately 
 * reflects the main contour plot.  This improves performance.  To force the 
 * thumbnail to be redrawn (i.e. to specify that the thumbnail does not 
 * accurately reflect the main contour plot), the 
 * {@link #invalidateThumbnail() invalidateThumbnail()} method can be 
 * invoked before requesting the thumbnail.  However, after any of the 
 * setter methods are used to change the way the contour plot is drawn, the 
 * <code>invalidateThumbnail()</code> should <i>not</i> be invoked.  
 * Instead, the setter methods will invalidate the thumbnail if it needs to 
 * be (sometimes the thumbnail is still valid after setting a property).
 */
public class ContourJPanel extends CoordJPanel implements Serializable,
                                                          IPreserveState
{
//------------------=[ Used to identify line styles ]=------------------------//
   /** Identifies a line style as a solid line. */
   public static final int SOLID = GraphJPanel.LINE;
   /** Identifies a line style as a dashed line. */
   public static final int DASHED = GraphJPanel.DASHED;
   /** Identifies a line style as a dashed-dotted line. */
   public static final int DASHED_DOTTED = GraphJPanel.DASHDOT;
   /** Identifies a lines style as a dotted line. */
   public static final int DOTTED = GraphJPanel.DOTTED;
//----------------=[ End used to identify line styles ]=----------------------//
   
   
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

   
//------------------------=[ Private constants ]=-----------------------------//
   /**
    * This is used to define the log scaler's source interval.  
    * The log scaler maps values from the interval 
    * [0, <code>LOG_TABLE_SIZE</code>].
    */
   private final int LOG_TABLE_SIZE = 60000;
   /**
    * This is used to define the log scaler's destination interval.  
    * The log scaler maps from vaues to the interval 
    * [0, <code>NUM_POSITIVE_COLORS</code>}].
    */
   private final int NUM_POSITIVE_COLORS = 127;
//----------------------=[ End private constants ]=---------------------------//
   
   
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
    * labels are rounded to.  Its value is <code>4</code>.
    */
   public static final int DEFAULT_NUM_SIG_DIGS = 4;
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
    * Boolean flag that marks if the contour graph has had its 
    * world coordinates initialized yet or not.  
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
   private boolean wcNotInit;
   /**
    * Boolean flag that marks if the main contour plot has been drawn or 
    * not.  This flag is used by the {@link #getThumbnail(int, int) 
    * getThumbnail(int, int)} method to determine how to generate the 
    * thumbnail.  If this flag is <code>true</code> (i.e. if the main 
    * contour plot has not been drawn yet), the <code>getThumbnail()</code> 
    * method does not try to render a thumbnail and instead returns a blank 
    * image.  This helps the startup performance of viewers like the 
    * <code>ContourViewComponent</code>.
    */
   private boolean mainImageNotInit;
   /** 
    * Used to associate colors to contour levels.  These colors are used when 
    * drawing the contour levels.  The colors reflect the contours "height".
    */
   private PseudoLogScaleUtil logScaler;
   /**
    * Used to affect how the colors are mapped to contour levels.  The closer 
    * this value is to <code>100</code>, the more the colors are pulled to 
    * the high end of the color spectrum.  The closer this value is to 
    * <code>0</code>, the more the colors are pulled to the low end of the 
    * color spectrum.
    */
   private double logScale;
   /** A reference to the thumbnail that refelcts the large contour image. */
   private BufferedImage thumbnail;
   
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
      
      //create the log scaler used to scale the colors of the lines drawn
      this.logScaler = new PseudoLogScaleUtil(0, (float)LOG_TABLE_SIZE, 
                                              0, (float)NUM_POSITIVE_COLORS);
      setLogScale(0);
      
      //set that the contour image has not been initialized yet
      this.wcNotInit = true;
      this.mainImageNotInit = true;
      
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
            //if the zoom was reset, act like the contour image 
            //world coordinates have not been initialized yet
            if (event.getActionCommand().equals(RESET_ZOOM))
               wcNotInit = true;
         }
      });
      
      //invalidate the thumbnail so that when a thumbnail of the plot is 
      //requested, the thumbnail will be generated at that time instead of 
      //now
      invalidateThumbnail();
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
            SharedMessages.addmsg("Warning:  the specific contour levels " +
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
   
   
//----------------------=[ Getter/setter methods ]=---------------------------//
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
    * Set the contours that should be plotted.
    * 
    * @param levels The contours that should be plotted.
    */
   public void setContours(Contours levels)
   {
      if (this.levels != levels)
      {
         this.levels = levels;
         invalidateThumbnail();
      }
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
      {
         boolean equal = false;
         if (this.lineStyles != null)
         {
            equal = (this.lineStyles == lineStyles) && 
                    (this.lineStyles.length == lineStyles.length);
            if (equal)
            {
               int i = 0;
               while ( equal && (i<lineStyles.length) )
               {
                  equal = (this.lineStyles[i] == lineStyles[i]);
                  i++;
               }
            }
         }
         
         if (!equal)
         {
            this.lineStyles = lineStyles;
            invalidateThumbnail();
         }
      }
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
      
      //Note:  If the labels are modified the thumbnail does not have to 
      //       be invalidated because the thumbnail doesn't show the labels
      //invalidateThumbnail();
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
      
      //Note:  If the number of significant figures are modified the 
      //       thumbnail does not have to be invalidated because the 
      //       thumbnail doesn't show the labels and thus does not 
      //       reflect the number of significant figures being used
      //invalidateThumbnail();
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
      
      boolean equal = false;
      if (this.colorScale != null)
      {
         equal = (this.colorScale == colors) && 
                 (this.colorScale.length == colors.length);
         if (equal)
         {
            int i = 0;
            while ( equal && (i<colors.length) )
            {
               equal = (this.colorScale[i] == colors[i]);
               i++;
            }
         }
      }
      
      if (!equal)
      {
         this.colorScale = colors;
         invalidateThumbnail();
      }
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
      
      if (!getBackground().equals(color))
      {
         setBackground(color);
         invalidateThumbnail();
      }
   }
   
   /**
    * Used to get the logscale factor that is used in the logarithmic 
    * mapping that is used to determine the color of the contour lines 
    * as they are drawn.
    * 
    * @return The factor that describes how closely the contour levels are 
    *         mapped to the max value (if the logscale is close to 100), or 
    *         how closely the contour levels are mapped to the min value 
    *         (if the logscale is close to 0).
    */
   public double getLogScale()
   {
      return logScale;
   }
   
   /**
    * Used to set the logscale factor that is used in the logarithmic 
    * mapping that is used to determine the color of the contour lines 
    * as they are drawn.
    * 
    * @param logScale The factor that describes how closely the contour 
    *                 levels are mapped to the max value (if 'logScale' 
    *                 is close to 100), or how closely the contour levels 
    *                 are mapped to the min value (if 'logScale' is close 
    *                 to 0).
    */
   public void setLogScale(double logScale)
   {
      this.logScale = logScale;
      invalidateThumbnail();
   }
   
   /**
    * Used to get a reference to the data onto which the contour lines being 
    * drawn are based.
    * 
    * @return A reference to the <code>IVirtualArray2D</code> that this 
    *         <code>ContourJPanel</code> is using.
    */
   public IVirtualArray2D getData()
   {
      return data2D;
   }
   
   /**
    * Used to determine the row in the virtual array of data that contains 
    * the data point that is displayed at the pixel point with the given 
    * y coordinate.
    * 
    * @param y The y coordinate of the pixel point in question.
    * @return The row in the virtual array that corresponds to the given 
    *         pixel point's y coordinate.
    */
   public int getRowForY(float y)
   {
      CoordTransform rcToWC = getRowColumnToWC();
      
      int row = (int)(rcToWC.MapYFrom(y));
      int maxRows = getData().getNumRows() - 1;
      if (row < 0)
         row = 0;
      else if (row > maxRows)
         row = maxRows;
      
      return row;
   }
   
   /**
    * Used to determine the column in the virtual array of data that contains 
    * the data point that is displayed at the pixel point with the given 
    * x coordinate.
    * 
    * @param x The x coordinate of the pixel point in question.
    * @return The column in the virtual array that corresponds to the given 
    *         pixel point's x coordinate.
    */
   public int getColumnForX(float x)
   {
      CoordTransform rcToWC = getRowColumnToWC();
      
      int col = (int)(rcToWC.MapXFrom(x));
      int maxCols = getData().getNumColumns() - 1;
      if (col < 0)
         col = 0;
      else if (col > maxCols)
         col = maxCols;
      
      return col;
   }
   
   /**
    * Used to get a thumbnail image of the main contour plot image.  This 
    * method caches the thumbnail and only rerenders a new one if the 
    * contour plot has changed.  Thus, if the contour image isn't being 
    * modified (by changing the colorscale, line styles, etc.) this method 
    * can be called often without a large performance hit.
    * 
    * @param width Specifies the width of the thumbnail.
    * @param height Specifies the height of the thumbnail.
    * @return A thumbnail image that reflects the main contour plot image.
    */
   public Image getThumbnail(int width, int height)
   {
      //the thumbnail is chached because it is expensive to create it
      //check if there is a valid cached thumbnail to return
        if (isThumbnailValid(width, height))
           return this.thumbnail;
      
      //correct the width and height if they are invalid
        if (width <= 0)
           width = 100;
        if (height <= 0)
           height = 100;
       
      //create a new blank thumbnail
        BufferedImage image = 
           new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        //If the main image hasn't been drawn yet, don't even bother 
        //  trying to draw the thumbnail.  
        //This improves performance because if there is not image for 
        //  the thumbnail to reflect, time isn't spent trying to make a 
        //  thumbnail.
        //Also, if 'this.mainImageNotInit==true' the thumbnail isn't 
        //  considered valid by 'isThumbnailValid()' anyway
        if (this.mainImageNotInit)
           return image;
        
      //TODO This forces the background color of the image to correspond to 
      //     the background of the panel.  This is here because the draw() 
      //     method sometimes leaves a black stripe on the right side of the 
      //     thumbnail.  This is not very efficient and thus a better 
      //     solution should be found
        int rgb = getBackgroundColor().getRGB();
        for (int x=0; x<width; x++)
           for (int y=0; y<height; y++)
              image.setRGB(x, y, rgb);
        
      //the following variables are effected by the painting of the 
      //thumbnail and need to be reverted when the painting is complete
        //when the thumbnail is painted, the variable describing if the 
        //main image's world coordinates have been initialized 
        //should not be affected
          boolean firstPaintCopy = this.wcNotInit;
        //labels are momentarily disabled in the thumbnail image
          boolean[] labelBackup = getShowLabels();
          setShowLabels(new boolean[] {false});
        //the local transform is momentarily modified to reflect the 
        //size of the thumbnail and not the entire panel
          CoordTransform localBackup = this.local_transform;
          local_transform = 
             new CoordTransform(getGlobalWorldCoords(), 
                                new CoordBounds(0, 0, width, height));
      //now draw the thumbnail
        draw(image.createGraphics());
       
      //now revert all of the saved variables back
        this.local_transform = localBackup;
        setShowLabels(labelBackup);
        this.wcNotInit = firstPaintCopy;
         
      //cache the image made
        this.thumbnail = image;
        
      return this.thumbnail;
   }
//--------------------=[ End getter/setter methods ]=-------------------------//
   
   
//--------------------=[ Extra public methods ]=------------------------------//
   /**
    * Use this method to change the data that is being displayed on the 
    * contour graph.  This method then redraws the graph to reflect the 
    * new data.
    * 
    * @param arr The new data that should be plotted.
    */
   public void changeData(IVirtualArray2D arr)
   {
      if (this.data2D != arr)
      {
         data2D = arr;
         invalidateThumbnail();
         reRender();
      }
   }
   
   /**
    * Used to make the cached thumbnail invalid.  As a result, when the 
    * {@link #getThumbnail(int, int) getThumbnail(int, int)} method is 
    * invoked, the thumbnail is forced to be redrawn.
    */
   public void invalidateThumbnail()
   {
      this.thumbnail = null;
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
      draw((Graphics2D)gr);
      //record that the main contour plot has been initialized
      //(i.e. it has been drawn)
      this.mainImageNotInit = false;
   }
//------------------=[ End extra public methods ]=----------------------------//
   
   
//-------------------------=[ Private methods ]=------------------------------//
   /**
    * Draws a contour plot into the given graphics object.
    * 
    * @param g The graphics object that describes something that can be 
    *          drawn to.  This could, for example, correspond to a 
    *          <code>JPanel</code> or an image.
    */
   private void draw(Graphics2D g)
   {
      stop_box( current_point, false );// if the system redraws this without

      stop_crosshair( current_point ); // our knowlege, we've got to get rid
                                       // of the cursors, or the old position
                                       // will be drawn rather than erased
                                       // when the user moves the cursor (due
                                       // to XOR drawing).
      
      //call the superclass to draw into the given Graphics2D
        super.paint(g);
        
      //first to extract the array of data to use
        float[][] arr = data2D.getRegionValues(0, 
                                               data2D.getNumRows()-1, 
                                               0, 
                                               data2D.getNumColumns()-1);
        
      //check that there is valid data to work with
        if (arr==null)
        {
           SharedMessages.addmsg("Warning:  A null array of data was " +
                                 "found from the IVirtualArray2D in " +
                                 "ContourJPanel.paint(....).  Thus, the " +
                                 "data could not be plotted.");
           return;
        }
        else if (arr.length==0) //return because there is no data to draw
        {
           SharedMessages.addmsg("Warning:  The array given to the " +
                                 "ContourJPanel was empty.  Thus, no data " +
                                 "has been plotted.");
           return;
        }
        
      //Get the transform that maps from the row/column coordinates of the 
      //array of data to the world coordinates of the entire panel.
        CoordTransform rcToWC = getRowColumnToWC();
        
      //If the panel's world coordinates have not been initialized yet, 
      //set the local_transform to have its input region be the output 
      //region of 'rcToWC' (i.e. the entire panel).  After the 
      //contour image is drawn the user can zoom in and the superclass 
      //handles setting the source (input) of local_transform to be the 
      //correct subset of the entire panel.
       if (wcNotInit)
       {
          local_transform.setSource(getGlobalWCBounds());
          wcNotInit = false;
       }
       
      //These are the bounds of the array of data that is currently being 
      //  displayed on the screen.
      //ex.) If the user has zoomed in on some portion of the data, then 
      //  only a subset of the array of data is being displayed on the screen 
      //The bounds are inclusive (i.e. arr[xMaxIndex][yMaxIndex] 
      //  can legally be accessed).
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
        
      /* Variables used when drawing the contour lines */
        
      //holds the contour points found
        Vector contourPts;
      //references an element in 'contourPts'
        floatPoint2D curPt;
      //Holds the x points describing where each contour line segment 
      //  should be drawn.  
      //The units will initially be in terms of row/column and will be 
      //  converted to world coordinates and then pixel coordinates.  
      //The numbers are in the order p1_start, p1_end, p2_start, p2_end ....
        float[] xrcVals;
      //the same as above except this stores y values
        float[] yrcVals;
      //This is the index of the point with the minimum gradient on a level
      //  grad(F(x,y)) := F_x(x,y)*i + F_y(x,y)*j 
      //  F_x(x,y) denotes the partial derivative of F with respect to x
      //  F_y(x,y) is the partial derivative of F with respect to y
      //  F is the 3D function that the array of data can be interpreted as 
      //    representing.
        int minGradIndex;
      //stores the square of the minimum gradient
        float minGradSqr;
        
      /* The following variables are 'sticky'.  In other words, if they are 
       * defined for one level but not the next, the values from the 
       * previous level stick
       */
        
      //the line style
        int lineSytle = DEFAULT_LINE_STYLE;
      //if labels should be displayed
        boolean showLabel = DEFAULT_SHOW_LABEL;
      //the number of significant digits
        int numSigDigits = DEFAULT_NUM_SIG_DIGS;
          
          
      //For each level draw the contour.
      for (int i=0; i<levels.getNumLevels(); i++)
      {
         //first to initialize the variables used to draw this level
           minGradIndex = 0;
           minGradSqr = Float.MAX_VALUE;
           //now to get this level's meta data
             lineSytle = lineStyles[i%lineStyles.length];
             showLabel = showLabels[i%showLabels.length];
             numSigDigits = numSigFigs[i%numSigFigs.length];
         
         //Now to get the contours.  The 'Contour2D.contour()' method does 
         //  the work of calculating the points on the contour level.  
         //The code in this class does the work of graphically 
         //  displaying these points.
           contourPts = Contour2D.contour(arr, levels.getLevelAt(i));
         
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
              
              //only compare the gradient if the current point is inside the 
              //region currently being viewed
              if ( xIndex>=xMinIndex && xIndex<xMaxIndex && 
                   yIndex>=yMinIndex && yIndex<yMaxIndex)
              {
                 //partial derivative of F with respect to x
                 float Fx = arr[xIndex+1][yIndex] - arr[xIndex][yIndex];
                 if ( (yIndex+1)<arr[xIndex].length )
                 {
                    //partial derivative of F with respect to y
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
              //point with the smallest gradient, draw the contour label
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
    * Used to create a transformation that maps from row/column 
    * coordinates to world coordinates.  The transformation maps from the 
    * entire array of data to the world coordinates describing the entire 
    * panel.
    * 
    * @return The transformation that maps the entire array of data 
    *         (in row/column coordinates) to the entire panel 
    *         (in world coordinates).
    */
   private CoordTransform getRowColumnToWC()
   {
      //get the CoordBounds object that encapsulates the bounds of 
      //the panel in world coordinates
        CoordBounds wcBounds = getGlobalWCBounds();
      //now to make a transform that maps from row/column to the world 
      //coordinates of the entire panel.  The row/col is mapped to the 
      //location on the ENTIRE panel because the ALL of the data in the 
      //array needs to be mapped to ALL of the panel (not just a small 
      //region which is what 'local_transform' describes).
        float delta = 0.001f;
      return new CoordTransform(
                                new CoordBounds(delta, 
                                                delta, 
                                                data2D.getNumColumns()-delta, 
                                                data2D.getNumRows()-delta
                                               ),
                                wcBounds
                               );
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
    * Used to determine if the thumbnail that is cached accurately reflects 
    * the contents of the main contour plot and is the correct size.
    * 
    * @param width A thumbnail could be requested whose width is specified 
    *              by this field.  This method uses this width to determine 
    *              if the cached thumbnail is the correct width.
    * @param height A thumbnail could be requested whose height is specified 
    *               by this field.  This method uses this height to 
    *               determine if the cached thumbnail is the correct height.
    * @return True if the cached thumbnail accurately reflects the contents 
    *         of the main contour plot, and false if a new thumbnail needs 
    *         to be created.
    */
   private boolean isThumbnailValid(int width, int height)
   {
      //if the main image hasn't been initialized yet, 
      //the thumbnail is invalid
      if (this.wcNotInit || this.mainImageNotInit)
         return false;
      
      //if the width or height for the newly requested thumbnail are invalid 
      //the thumbnail is invalid
      if ( (width<=0) || (height<=0) )
         return false;
      
      //if the thumbnail is 'null' then it doesn't exist yet and is 
      //therefore invalid
      if (this.thumbnail == null)
         return false;
      
      //otherwise the thumbnail is only valid if the current thumbnail's 
      //width and height correspond to the newly requested width and height
      return ( (this.thumbnail.getWidth()==width) && 
               (this.thumbnail.getHeight()==height) );
   }
   
   /**
    * Used to look at the "elevation" of a particular contour level and 
    * find the color that should be used when drawing the contour level.  
    * This method incorportates the current logarithmic scaling factor when 
    * determining the color.
    * 
    * @param height The "elevation" of the contour line in question.
    * 
    * @see #setLogScale(double)
    * @see #getLogScale()
    */
   private Color getColorForLevel(float height)
   {
      //get the logarithmic scale factor to use
        double scale = getLogScale();
      
      //logarithmically adjust the height given
        height = logScaler.toDest(height, scale);
      //get the max and min such that they correspond to the new 
      //mapped logarithmic region
        float max = logScaler.toDest(levels.getHighestLevel(), scale);
        float min = logScaler.toDest(levels.getLowestLevel(), scale);
      
      //a mapping (or function) y = ax + b is used to map the interval 
      //[max, min] to the interval [0, colorScale.length-1]
        float a = (colorScale.length-1)/(max-min);
        float b = -1*a*min;
      
      //the value of the function is the index in 
      //the colorscale array of colors
        int index = (int)(a*height+b);
      //if the index is out of bounds pull it back in
        if (index < 0)
           index = 0;
        else if (index >= colorScale.length)
           index = colorScale.length - 1;
        
      return colorScale[index];
   }
//-----------------------=[ End private methods ]=----------------------------//
   
   
//------------------=[ Methods used to test this class ]=---------------------//
   /**
    * Generates a float[][] for the data for the function 
    * f(x,y) = x<sup>2</sup>+y<sup>2</sup>
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
      float[] levels = new float[5];
         levels[0] = 0;
         levels[1] = 10;
         levels[2] = 2;
         levels[3] = 7;
         levels[4] = 12.2332f;
         
      final IVirtualArray2D v2d_1 = 
               ContourViewComponent.getTestData(51,51,3,4);
      final IVirtualArray2D v2d_2 = 
               getTestData();
      
      final ContourJPanel panel = new ContourJPanel(v2d_1, 0, 10, 11);
         //initialize the panel's world coordinates
         AxisInfo xInfo = v2d_1.getAxisInfo(AxisInfo.X_AXIS);
         AxisInfo yInfo = v2d_1.getAxisInfo(AxisInfo.Y_AXIS);
         
         panel.initializeWorldCoords(new CoordBounds(xInfo.getMin(), 
                                                     yInfo.getMax(), 
                                                     xInfo.getMax(), 
                                                     yInfo.getMin()));
      //ContourJPanel panel = new ContourJPanel(getTestData(),levels);
         panel.setLineStyles(new int[] {SOLID, DASHED, DASHED});
         panel.setShowLabels(new boolean[]{true, false, false});
         panel.setNumSigDigits(new int[]{3,0,0});
         panel.setPreserveAspectRatio(true);
         panel.setColorScale(IndexColorMaker.HEATED_OBJECT_SCALE_2, false);
         /*
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
         */
      JFrame frame = new JFrame("ContourJPanel test");
         frame.setSize(200,200);
         frame.getContentPane().add(panel);
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setVisible(true);
      
      
      final JButton thumbnailButton = new JButton("New thumbnail");
      final JLabel thumbnailLabel = new JLabel(new ImageIcon());
      thumbnailButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            panel.invalidateThumbnail();
            Image image = panel.getThumbnail(100, 100);
            thumbnailLabel.setIcon(new ImageIcon(image));
         }
      });
      
      JFrame thumbnailPane = new JFrame("Thumbnail");
        thumbnailPane.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        thumbnailPane.getContentPane().setLayout(new BorderLayout());
        thumbnailPane.getContentPane().add(thumbnailButton, 
                                           BorderLayout.NORTH);
        thumbnailPane.getContentPane().add(thumbnailLabel, 
                                           BorderLayout.CENTER);
        thumbnailPane.setSize(200, 200);
      thumbnailPane.setVisible(true);
      
      final String data1 = "Data1";
      final String data2 = "Data2";
      final JButton changeButton1;
      final JButton changeButton2;
      ActionListener changeListener = new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            String message = event.getActionCommand();
            if (message.equals(data1))
               panel.changeData(v2d_1);
            else if (message.equals(data2))
               panel.changeData(v2d_2);
         }
      };
      changeButton1 = new JButton(data1);
        changeButton1.addActionListener(changeListener);
      changeButton2 = new JButton(data2);
        changeButton2.addActionListener(changeListener);
      
      JPanel changePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        changePanel.add(new JLabel("Change the displayed data to"));
        changePanel.add(changeButton1);
        changePanel.add(changeButton2);
        
      JFrame changeFrame = new JFrame("Change the Displayed Data");
        changeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        changeFrame.getContentPane().add(changePanel);
        changeFrame.pack();
      changeFrame.setVisible(true);
   }
//----------------=[ End methods used to test this class ]=-------------------//
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


/*
   /**
    * Used to set if the aspect ratio should be preserved or not.
    * 
    * @param preserve True if the aspect ratio should be preserved and 
    *                 false if it shouldn't be preserved.
    /
   public void setPreserveAspectRatio(boolean preserve)
   {
      super.setPreserveAspectRatio(preserve);
      //setLocalWorldCoords(getLocalWorldCoords());
      
      //invalidateThumbnail();
      //reRender();
   }
   */

//--------------------------=[ End unused code ]=-----------------------------//
