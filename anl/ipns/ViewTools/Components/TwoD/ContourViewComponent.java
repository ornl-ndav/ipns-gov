/*
 * File: ContourViewComponent.java
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
 * Revision 1.15  2005/11/22 21:09:56  dennis
 * Modified to point tick marks inward by default.
 *
 * Revision 1.14  2005/07/20 17:24:46  kramer
 *
 * Added javadocs for all of the fields.  Removed the redundant field
 * DEFAULT_CONTOUR_COLOR (it repeats DEFAULT_LINE_COLOR).  Updated the
 * method setPreserveAspectRatio() to call setPreserveAspectRatio() on the
 * ContourJPanel.
 *
 * Revision 1.13  2005/07/19 19:02:33  kramer
 *
 * -Reorganized the code
 * -Now the ObjectState works so that the
 *  state of the viewer can be stored to
 *  and read from an ObjectState.
 * -The intensity control now works.
 * -Added an inner class ColorOrColorScale
 *  to maintain the color or colorscale
 *  that is currently being used to color
 *  the contour lines.
 * -Currently, when the contour lines are
 *  colored with a solid color, the
 *  ControlColorScale displays the
 *  colorscale that was last used
 *  to color the contour lines.
 *
 * Revision 1.12  2005/07/12 17:08:57  kramer
 *
 * Added support to save the state of the ContourJPanel in the state
 * information for this class.  Added an ActionListener to this class's
 * ContourJPanel so that viewer 'pointed at' synchronization would work.
 * Removed the isChecked() method.
 *
 * Revision 1.11  2005/06/28 22:23:19  kramer
 *
 * Changed some of the controls to ViewMenuItems.  Now there are menu items
 * for setting the aspect ratio, contour line color or colorscale, and
 * setting if the colorscale is double sided or not.  Also, added a
 * control to change the background color (this is useful because with some
 * colorscales some contour levels are hard to see).  The code for some of
 * the old controls has commented out.
 *
 * Revision 1.10  2005/06/28 16:09:36  kramer
 *
 * Added support for modifying the background color or contour line color,
 * and for specifying the color scale (and intensity) used to color the
 * contour lines.  Currently, the latter is incomplete and the controls do
 * not appear on the GUI.
 *
 * Revision 1.9  2005/06/23 21:02:52  kramer
 *
 * Made this class now use the ControlCheckboxSpinner and
 * ControlCheckboxCombobox ViewControls as the controls for the
 * contour labels and label formatting.
 *
 * Revision 1.8  2005/06/22 22:16:37  kramer
 *
 * Added javadocs and rearranged the code to make it easier to read.
 *
 * Modified the default values specified in this class so that this class,
 * the CompositeContourControl class, and the ContourJPanel class all
 * have their defaults linked.
 *
 * Added controls to allow the user to enable/disable labels and label
 * formatting.  Added controls to allow the user to enable/disable
 * certain lines with their styles.
 *
 * Revision 1.7  2005/06/17 19:33:26  kramer
 *
 * Modified to have controls for specifying the line style for a group of
 * 4 lines (this pattern is repeated as the contours are rendered).  Also,
 * controls for the specifying the num of significant digits as well as
 * controls for stating that every 'N' contour labels have been added.
 *
 * Revision 1.6  2005/06/16 13:45:31  kramer
 *
 * Modified to use the CompositeContourControl as the control for entering
 * the levels to plot.
 *
 */
package gov.anl.ipns.ViewTools.Components.TwoD;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.ColorSelector;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.Transparency.AxisOverlay2D;
import gov.anl.ipns.ViewTools.Components.Transparency.IAxisAddible;
import gov.anl.ipns.ViewTools.Components.Transparency.OverlayJPanel;
import gov.anl.ipns.ViewTools.Components.ViewControls.ColorControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.CompositeContourControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckbox;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckboxCombobox;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckboxSpinner;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlColorScale;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlSlider;
import gov.anl.ipns.ViewTools.Components.ViewControls.IColorScaleAddible;
import gov.anl.ipns.ViewTools.Components.ViewControls.SpinnerControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.Contours;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;
import gov.anl.ipns.ViewTools.UI.FontUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.OverlayLayout;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

/**
 * This class is used to create a contour plot of a three dimensional set 
 * of data.  The following features are supported:
 * <ul>
 *   <li> Uniformly spaced contour levels </li>
 *   <li> Manually entered contour levels </li>
 *   <li> Contour labels </li>
 *   <li> Formatting labels to a specified number of significant figures </li>
 *   <li> Contour line styles (solid, dashed, dashed-dotted, or dotted).
 * </ul>
 */
public class ContourViewComponent implements IViewComponent2D, Serializable, 
                                             IColorScaleAddible, IAxisAddible
{
//------------------------=[ ObjectState keys ]=------------------------------//
   //keys for panels
   /**
    * "Contour panel key" - This static constant String is a key used for 
    * referencing the state information for the <code>ContourJPanel</code> 
    * used by this component.  The value that this key references is an 
    * <code>ObjectState</cdoe> of a <code>ContourJPanel</code>.
    */
   public static final String CONTOUR_PANEL_KEY = "Contour panel key";
   /**
    * "Axis overlay key" - This static constant String is a key used for 
    * referencig the state information for the <code>AxisOverlay2D</code> 
    * used by this component to display the axes.  The value that this key 
    * references is an <code>ObjectState</code> of a 
    * <code>AxisOverlay2D</code> object.
    */
   public static final String AXIS_OVERLAY_KEY = "Axis overlay key";
   
   //keys for ViewMenuItems
   /**
    * "Aspect ratio key" - This static constant String is a key used for 
    * referencing whether or not the aspect ratio is currently preserved for 
    * this component.  The value that this key references is a 
    * <code>Boolean</code>.
    */
   public static final String ASPECT_RATIO_KEY = "Aspect ratio key";
   /**
    * "Solid color key" - This static constant String is a key used for 
    * referencing the state information about the <code>ColorControl</code> 
    * that is used to make the contour lines colored with a solid color.  
    * The value that this key references is an <code>ObjectState</code> of 
    * a <code>ColorControl</code> object.
    */
   public static final String SOLID_COLOR_KEY = "Solid color key";
   /**
    * "Is double sided key" - This static constant String is a key used for 
    * referencing whether or not this component should use a double-sided 
    * named colorscale when it uses a colorscale to determine the color to 
    * render the contour lines with.  The value that this key references is 
    * a <code>Boolean</code> object.
    */
   public static final String IS_DOUBLE_SIDED_KEY = "Is double sided key";
   
   //keys for ViewControls
   /**
    * "Intensity slider key" - This static constant String is a key used for 
    * referencing the state information about the control that controls the 
    * intensity of the colorscale that is used to determine the color to 
    * render the contour lines with.  The value that this key references is 
    * an <code>ObjectState</code> of a <code>ControlSlider</code> object.
    */
   public static final String INTENSITY_SLIDER_KEY = "Intensity slider key";
   /**
    * "Control color scale key" - This static constant String is a key used 
    * for referencing the state information for the 
    * <code>ControlColorScale</code> that is used to display the colorscale 
    * that is currently being used to determine the color to orender the 
    * contour lines.  The value that this key references is an 
    * <code>ObjectState</code> of a <code>ControlColorScale</code> object.
    */
   public static final String CONTROL_COLOR_SCALE_KEY = 
                                                  "Control color scale key";
   /**
    * "Contour controls key" - This static constant String is a key used 
    * for referencing the state information for the control that is used to 
    * enter the contour levels that should be rendered.  The value that 
    * this key references is an <code>ObjectState</code> of a 
    * <code>CompositeContourControl</code> object.
    */
   public static final String CONTOUR_CONTROLS_KEY = "Contour controls key";
   /**
    * "Line styles key" - This static constant String is a key used for 
    * referencing the state information for the control that is used to 
    * enter the line styles used when rendering the contour levels.  The 
    * value that this key references is an <code>ObjectState</code> of a 
    * <code>LineStyleControl</code> object.
    */
   public static final String LINE_STYLES_KEY = "Line styles key";
   /**
    * "Line labels key" - This static constant String is a key used for 
    * referencing the state information for the control that is used to 
    * specify if the contour lines should be labeled and which contour lines 
    * should be labeled.  The value that this key references is an 
    * <code>ObjectState</code> of a <code>ControlCheckboxSpinner</code> 
    * object.
    */
   public static final String LINE_LABELS_KEY = "Line labels key";
   /**
    * "Number of significant figures key" - This static constant String is a 
    * key used for referencing the state informatino for the control that is 
    * used to specify if contour label should be formatted and, if so, the 
    * number of significant figures the contour labels are rounded to.  The 
    * value that this key references is an <code>ObjectState</code> of an 
    * <code>ControlCheckboxSpinner</code> object.
    */
   public static final String NUM_SIG_FIGS_KEY = 
                                        "Number of significant figures key";
   /**
    * "Background color key" - This static constant String is a key used for 
    * referencing the state information for the control that is used to 
    * specify the background color of this component.  The value that this 
    * key references is an <code>ObjectState</code> of a 
    * <code>ColorControl</code> object.
    */
   public static final String BACKGROUND_COLOR_KEY = "Background color key";
   
   //keys for general data
   /**
    * "Current color or colorscale key" - This component has separate 
    * controls for specifying the solid color or named colorscale that is 
    * used to determine the color used when rendering the contour lines.  
    * However, this component either uses the solid color or colorscale at 
    * any particular time.  Both specifications cannot be used at the same 
    * time.  This static constant String records which of these 
    * specifications is currently being used.
    * <p>
    * The value that this key references has two possible values:
    * <ul>
    *   <li>
    *     If the value is a <code>Color</code>, then this key references the 
    *     solid color that is currently being used to render the contour 
    *     lines.
    *   </li>
    *     If the value is a <code>String</code>, then this key references 
    *     the named colorscale that is currently being used to render the 
    *     contour lines.
    *   <li>
    *   </li>
    * </ul>
    */
   public static final String CURRENT_COLOR_OR_COLORSCALE_KEY = 
                                          "Current color or colorscale key";
   
//----------------------=[ End ObjectState keys ]=----------------------------//
   
   
//--------------------=[ Default field values ]=------------------------------//
   
   //------------=[ Defaults borrowed from ContourJPanel ]=-----------------//
   /**
    * Specifies if contour line labels will be displayed by default.  The 
    * value of this field is the same as the value of the field 
    * {@link ContourJPanel#DEFAULT_SHOW_LABEL 
    * ContourJPanel.DEFAULT_SHOW_LABEL}.
    */
   public static final boolean DEFAULT_SHOW_LABEL   = 
      ContourJPanel.DEFAULT_SHOW_LABEL;
   /**
    * Specifies if contour line labels will be formatted to a specified 
    * number of significant figures by default (if contour line labels 
    * are being displayed).  The value of this field is the same as the value 
    * of the field 
    * {@link ContourJPanel#DEFAULT_NUM_SIG_DIGS 
    * ContourJPanel.DEFAULT_NUM_SIG_DIGS}.
    */
   public static final int     DEFAULT_NUM_SIG_DIGS = 
      ContourJPanel.DEFAULT_NUM_SIG_DIGS;
   /**
    * Specifies if the aspect ratio will be preserved when displaying the 
    * contour plot by default.  The value of this field is the same as the 
    * value of the field 
    * {@link ContourJPanel#DEFAULT_PRESERVE_ASPECT_RATIO 
    * ContourJPanel.DEFAULT_PRESERvE_ASPEcT_RATIO}.
    */
   public static final boolean DEFAULT_PRESERVE_ASPECT_RATIO = 
      ContourJPanel.DEFAULT_PRESERVE_ASPECT_RATIO;
   /**
    * Specifies the default background color for this component.  The value 
    * of this field is the same as the value of the field 
    * {@link ContourJPanel#DEFAULT_BACKGROUND_COLOR 
    * ContourJPanel.DEFAULT_BACGROUND_COLOR}.
    */
   public static final Color DEFAULT_BACKGROUND_COLOR = 
      ContourJPanel.DEFAULT_BACKGROUND_COLOR;
   /**
    * Specifies the default solid color that the contour lines will be 
    * rendered using.  If the field {@link ContourJPanel#DEFAULT_COLOR_SCALE 
    * ContourJPanel.DEFAULT_COLOR_SCALE} has at least one element, this 
    * field's value is the value of that element.  Otherwise, this field's 
    * value is <code>Color.BLACK</code>
    */
   public static final Color DEFAULT_LINE_COLOR;
      static
      {
         Color[] arr = ContourJPanel.DEFAULT_COLOR_SCALE;
         if (arr.length<1)
            DEFAULT_LINE_COLOR = Color.BLACK;
         else
            DEFAULT_LINE_COLOR = arr[0];
      }
   /**
    * Specifies the default line styles that will be used when rendering the 
    * contour lines.  The value of this field is a four element array where 
    * each element has the value {@link ContourJPanel#DEFAULT_LINE_STYLE 
    * ContourJPanel.DEFAULT_LINE_STYLE}.
    */
   public static final int[] DEFAULT_STYLES = 
      {ContourJPanel.DEFAULT_LINE_STYLE, 
       ContourJPanel.DEFAULT_LINE_STYLE, 
       ContourJPanel.DEFAULT_LINE_STYLE, 
       ContourJPanel.DEFAULT_LINE_STYLE};
   //----------=[ End defaults borrowed from ContourJPanel ]=---------------//
   
   
   //--------=[ Defaults borrowed from CompositeContourControl ]=-----------//
   /**
    * Specifies the default value of the lowest contour level used when 
    * specifying a collection of uniformlly spaced contour levels.   
    * The value of this field is the same as the value of the field 
    * {@link CompositeContourControl#DEFAULT_LOWEST_CONTOUR 
    * CompositeContourControl.DEFAULT_LOWEST_CONTOUR}.
    */
   public static final float   DEFAULT_LOWEST_CONTOUR = 
      CompositeContourControl.DEFAULT_LOWEST_CONTOUR;
   /**
    * Specifies the default value of the highest contour level used when 
    * specifying a collection of uniformlly spaced contour levels.  
    * The value of this field is the same as the value of the field 
    * {@link CompositeContourControl#DEFAULT_HIGHEST_CONTOUR 
    * CompositeContourControl.DEFAULT_HIGHEST_CONTOUR}.
    */
   public static final float   DEFAULT_HIGHEST_CONTOUR =  
      CompositeContourControl.DEFAULT_HIGHEST_CONTOUR;
   /**
    * Specifies the default number of contour levels used when specifying a 
    * collection of uniformlly spaced contour levels.  The value of this 
    * field is the same as the value of the field 
    * {@link CompositeContourControl#DEFAULT_NUM_CONTOURS 
    * CompositeContourControl.DEFAULT_NUM_CONTOURS}.
    */
   public static final int     DEFAULT_NUM_CONTOURS = 
      CompositeContourControl.DEFAULT_NUM_CONTOURS;
   /**
    * Specifies the default manually entered contour levels used when 
    * specifying a collection of non-uniformly spaced contour levels.  The 
    * value of ths field is the same  as the value of the field 
    * {@link CompositeContourControl#DEFAULT_MANUAL_LEVELS 
    * CompositeContourControl.DEFAULT_MANUAL_LEVELS}.
    */
   public static final float[] DEFAULT_MANUAL_LEVELS = 
      CompositeContourControl.DEFAULT_MANUAL_LEVELS;
   //------=[ End defaults borrowed from CompositeContourControl ]=---------//
   
   
   //-----------------=[ Defaults unique to this class ]=-------------------//
   /**
    * Specifies if contour line labeling will be enabled by default.  
    * The value of this field is <code>true</code>.
    */
   public static final boolean DEFAULT_ENABLE_LABEL_FORMATTING = true;
   /**
    * Specifies that every <code>nth</code> contour line will be labeled by 
    * default.  The value of this field is <code>1</code>.
    */
   public static final int DEFAULT_LABEL_EVERY_N_LINES = 1;
   /**
    * Specifies which extra line styles will be enabled by default.  The 
    * value of this field is a three element array where each element has the 
    * value <code>false</code>.  This means that only the standard line 
    * style will be enabled (it is always enabled).  The rest of the 
    * extra line styles will be disabled.
    */
   public static final boolean[] DEFAULT_ENABLE_STYLE_ARR = {false, 
                                                             false, 
                                                             false};
   /**
    * Specifies the default named colorscale used to determine the colors 
    * to render the contour lines with.  The value of this field is 
    * {@link IndexColorMaker#HEATED_OBJECT_SCALE_2 
    * IndexColorMaker.HEATED_OBJECT_SCALE_2}.
    */
   public static final String DEFAULT_COLOR_SCALE = 
      IndexColorMaker.HEATED_OBJECT_SCALE_2;
   /**
    * Specifies if the named colorscales used to determine the colors to 
    * render the contour lines with should be a double-sided contour scale 
    * by default.  The value of this field is <code>false</code>.
    */
   public static final boolean DEFAULT_IS_DOUBLE_SIDED = false;
   //-----------------=[ Defaults unique to this class ]=-------------------//
   
//------------------=[ End default field values ]=----------------------------//

   
//----------------------------=[ Fields ]=------------------------------------//
   /**
    * The panel that contains the <code>ContourJPanel</code> and 
    * all of the transparencies.
    */
   private JPanel layerPanel;
   /** The Vector of ActionListener associated with this component. */
   private Vector listenerVec;
   /** The panel that does the work of rendering the contour plot. */
   private ContourJPanel contourPanel;
   /**
    * The array of <code>ViewControls</code> that are used to 
    * control this component. 
    */
   private ViewControl[] controls;
   /**
    * The array of <code>ViewMenuItems</code> that are used to control 
    * this component.
    */
   private ViewMenuItem[] menuItems;
   /**
    * Used to maintain if the <code>ContourJPanel</code> is currently 
    * using a solid color or a named colorscale to color the contour lines.  
    * In addition, the actual color or colorscale is maintained by this 
    * object.
    */
   private ColorOrColorScale colorOrColorscale;
   /**
    * The Vector of transparencies that are placed on top of the 
    * <code>ContourJPanel</code> to give it more functionality.  This 
    * Vector is a Vector of <code>OverlayJPanel</code> objects.
    */
   private Vector transparencies;
   
   //Used with the ViewControls
   /**
    * This is the <code>ViewControl</code> that is used to specify the 
    * uniformly spaced or non-uniformly spaced contour lines 
    * (or a combination of the two) to be rendered.
    */
   private CompositeContourControl contourControl;
   /**
    * This is the <code>ViewControl</code> that is used to specify the 
    * linestyles that should be used when rendering the contour lines.
    */
   private LineStyleControl lineStyleControl;
   /**
    * This is the <code>ViewControl</code> that is used to specify if 
    * contour line labels should be enabled, and, if so, what multiple of 
    * lines should be labeled (for example, every 4th line).
    */
   private ControlCheckboxSpinner labelControl;
   /**
    * This is the <code>ViewControl</code> that is used to specify if 
    * contoru line labels should be formatted (if contour line labels are 
    * enabled), and, if so, the number of significant figures the contour 
    * line labels are rounded to.
    */
   private ControlCheckboxSpinner sigFigControl;
   /**
    * This is the <code>ViewControl</code> that is used to specify the 
    * background color of this component.
    */
   private ColorControl backgroundControl;
   /**
    * This is the <code>ViewControl</code> that graphically displays the 
    * current colorscale used to color the contour lines.
    */
   private ControlColorScale controlColorscale;
   /**
    * This is the <code>ViewControl</code> that is used to control the 
    * intensity of the colorscale that is used to color the contour lines.
    */
   private ControlSlider intensitySlider;
   
   //Used with the ViewMenuItems
   /**
    * This is the menu item that is used to specify if the contour plot 
    * should be rendered with its aspect ratio preserved or not.
    */
   private JCheckBoxMenuItem aspectRatioItem;
   /**
    * This is the menu item that is used to specify the solid color to 
    * use when coloring the contour lines.
    */
   private ColorControl contourColorItem;
   /**
    * This is the menu item that is used to specify if the named color scale 
    * used to color the contour lines should be double-sided or not.
    */
   private JCheckBoxMenuItem isDoubleSidedItem;
//--------------------------=[ End fields ]=----------------------------------//
  
   
//-------------------------=[ Constructors ]=---------------------------------//
   private ContourViewComponent(IVirtualArray2D v2D, 
                                ContourJPanel contourPanel, 
                                boolean useManualLevels, 
                                boolean useColorScale)
   {
      //first initialize all of the fields and create the appropriate panels
        initFields(v2D, contourPanel);
      
      //and build the menu items
        initMenuItems(DEFAULT_LINE_COLOR, DEFAULT_COLOR_SCALE, 
                      DEFAULT_IS_DOUBLE_SIDED, useColorScale, 
                      DEFAULT_PRESERVE_ASPECT_RATIO);
        
      //and build the controls
        initControls(DEFAULT_LOWEST_CONTOUR, 
                     DEFAULT_HIGHEST_CONTOUR, 
                     DEFAULT_NUM_CONTOURS, 
                     DEFAULT_MANUAL_LEVELS, 
                     useManualLevels,
                     DEFAULT_PRESERVE_ASPECT_RATIO, 
                     DEFAULT_STYLES, 
                     DEFAULT_ENABLE_STYLE_ARR, 
                     DEFAULT_SHOW_LABEL, 
                     DEFAULT_LABEL_EVERY_N_LINES, 
                     DEFAULT_ENABLE_LABEL_FORMATTING,
                     DEFAULT_NUM_SIG_DIGS, 
                     DEFAULT_COLOR_SCALE, 
                     DEFAULT_IS_DOUBLE_SIDED, 
                     DEFAULT_BACKGROUND_COLOR, 
                     DEFAULT_LINE_COLOR);
   }
   
   public ContourViewComponent(IVirtualArray2D arr)
   {
      this(arr, 
           new ContourJPanel(arr, 
                             DEFAULT_LOWEST_CONTOUR, 
                             DEFAULT_HIGHEST_CONTOUR, 
                             DEFAULT_NUM_CONTOURS), 
           false, //don't display the manual levels (display the uniform ones)
           false);//don't use a colorscale for coloring the contour lines 
                  //(use the default solid color)
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float minValue, float maxValue, int numLevels)
   {
      this(arr, 
           new ContourJPanel(arr,minValue,maxValue,numLevels), 
           false, //don't display the manual levels (display the uniform ones)
           false);//don't use a colorscale for coloring the contour lines 
                  //(use the default solid color)
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float[] levels)
   {
      this(arr, 
           new ContourJPanel(arr,levels), 
           true,  //display the manual levels (don't display the uniform ones)
           false); //don't use a colorscale for coloring the contour lines 
                   //(use the default solid color)
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float minValue, float maxValue, int numLevels, 
                               ObjectState state)
   {
      this(arr,minValue,maxValue,numLevels);
      setObjectState(state);
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float[] levels, 
                               ObjectState state)
   {
      this(arr,levels);
      setObjectState(state);
   }
//-------------------------=[ End constructors ]=-----------------------------//
   
   
//---------=[ Methods mplemented for the IViewComponent2D interface ]=--------//
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      //set the states for the panels used in this component
      Object val = state.get(CONTOUR_PANEL_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         contourPanel.setObjectState((ObjectState)val);
      
      val = state.get(AXIS_OVERLAY_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         getAxisOverlay().setObjectState((ObjectState)val);
      
      
      //set the states for the ViewMenuItems
      val = state.get(ASPECT_RATIO_KEY);
      if ( (val != null) && (val instanceof Boolean) )
         setPreserveAspectRatio( ((Boolean)val).booleanValue() );
      
      val = state.get(SOLID_COLOR_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         contourColorItem.setObjectState((ObjectState)val);
      
      val = state.get(IS_DOUBLE_SIDED_KEY);
      if ( (val != null) && (val instanceof Boolean) )
         setIsDoubleSidedColorScale(((Boolean)val).booleanValue());
      
      
      //set the states for the ViewControls
      val = state.get(INTENSITY_SLIDER_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         intensitySlider.setObjectState((ObjectState)val);
      
      val = state.get(CONTROL_COLOR_SCALE_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         controlColorscale.setObjectState((ObjectState)val);
         
      val = state.get(CONTOUR_CONTROLS_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         contourControl.setObjectState((ObjectState)val);
      
      val = state.get(LINE_STYLES_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         lineStyleControl.setObjectState((ObjectState)val);
      
      val = state.get(LINE_LABELS_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         labelControl.setObjectState((ObjectState)val);
      
      val = state.get(NUM_SIG_FIGS_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         sigFigControl.setObjectState((ObjectState)val);
      
      val = state.get(BACKGROUND_COLOR_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         backgroundControl.setObjectState((ObjectState)val);
      
      
      //set the extra state information
      val = state.get(CURRENT_COLOR_OR_COLORSCALE_KEY);
      if ( val != null )
      {
         if ( val instanceof String )
            setColorScale((String)val); //set the contour lines' color to be the colorscale
         else if (val instanceof Color )
            setColorScale((Color)val); //set the contour lines' color to be the solid color
      }
         
      
      paintComponents();
   }

   public ObjectState getObjectState(boolean is_default)
   {
      //get an empty ObjectState
      ObjectState state = new ObjectState();

        //store the states of the panels on this component
          //for the ContourJPanel
          state.insert(CONTOUR_PANEL_KEY, 
                       contourPanel.getObjectState(is_default));
          //for the AxisOverlay2D
          state.insert(CONTOUR_CONTROLS_KEY, 
                       contourControl.getObjectState(is_default));
        
        //store the states for the ViewMenuItems on this component
          //store if the aspect ratio is preserved
            boolean useAspectRatio = DEFAULT_PRESERVE_ASPECT_RATIO;
            if (!is_default)
               useAspectRatio = aspectRatioItem.isSelected();
            state.insert(ASPECT_RATIO_KEY, new Boolean(useAspectRatio));
            
          //store state of the control which controls the solid line 
          //color of the contour lines
            state.insert(SOLID_COLOR_KEY, 
                         contourColorItem.getObjectState(is_default));
            
          //store if the colorscale is double sided
            boolean isDoubleSided = DEFAULT_IS_DOUBLE_SIDED;
            if (!is_default)
               isDoubleSided = isDoubleSidedItem.isSelected();
            state.insert(IS_DOUBLE_SIDED_KEY, new Boolean(isDoubleSided));
            
            
        //store the states of the ViewControls
          state.insert(INTENSITY_SLIDER_KEY, 
                       intensitySlider.getObjectState(is_default));
          state.insert(CONTROL_COLOR_SCALE_KEY, 
                       controlColorscale.getObjectState(is_default));
          state.insert(CONTOUR_CONTROLS_KEY, 
                       contourControl.getObjectState(is_default));
          state.insert(LINE_STYLES_KEY, 
                       lineStyleControl.getObjectState(is_default));
          state.insert(LINE_LABELS_KEY, 
                       labelControl.getObjectState(is_default));
          state.insert(NUM_SIG_FIGS_KEY, 
                       sigFigControl.getObjectState(is_default));
          state.insert(BACKGROUND_COLOR_KEY, 
                       backgroundControl.getObjectState(is_default));
            
        //store the extra state information
          //store the current colorscale 
          //(both the controls for making the lines a solid color or 
          // a colorscale have their state saved).  This stores which 
          //one's color actually is reflected on the ContourJPanel
          if (this.colorOrColorscale.isColorScale())
          {
             String colorscale = DEFAULT_COLOR_SCALE;
             if (!is_default)
                colorscale = this.colorOrColorscale.getColorScale();
             state.insert(CURRENT_COLOR_OR_COLORSCALE_KEY, colorscale);
          }
          else if (this.colorOrColorscale.isColor())
          {
             Color color = DEFAULT_LINE_COLOR;
             if (!is_default)
                color = this.colorOrColorscale.getColor();
             state.insert(CURRENT_COLOR_OR_COLORSCALE_KEY, color);
          }
          
      return state;
   }

   public void setPointedAt(floatPoint2D fpt)
   {
      if (fpt==null)
         return;
      
      contourPanel.set_crosshair_WC(fpt);
   }

   public floatPoint2D getPointedAt()
   {
      return new floatPoint2D(contourPanel.getCurrent_WC_point());
   }

   public void setSelectedRegions(Region[] rgn)
   {
   }

   public Region[] getSelectedRegions()
   {
      return new Region[0];
   }

   public void dataChanged(IVirtualArray2D v2D)
   {
      if (v2D == null)
      {
         this.layerPanel.removeAll();
         this.contourPanel.removeAllActionListeners();
         this.layerPanel.add(
               new JLabel("No data to display as a contour image."));
         
         paintComponents();
      }
      else
      {
         //store the transparencies in a Vector
         this.transparencies.clear();
                                               // tick marks point inward by
                                               // default
         AxisOverlay2D axes = new AxisOverlay2D(this, true);

//TODO For now the AxisOverlay2D is not visible because it does not work.
//     Fix it.

         axes.setVisible(true);
         axes.setDisplayAxes(AxisOverlay2D.DUAL_AXES);
         this.transparencies.add(axes);
         
         //layout used to add transparencies on top of the ContourJPanel
         OverlayLayout layout = new OverlayLayout(this.layerPanel);
         this.layerPanel.setLayout(layout);
         
         //add each transparency and the ContourJPanel to the main layout panel
         for (int i=0; i<this.transparencies.size(); i++)
           this.layerPanel.add((OverlayJPanel)this.transparencies.elementAt(i));
         this.layerPanel.add(this.contourPanel);
         
         //inform the ContourJPanel that the data has changed
         AxisInfo xInfo = v2D.getAxisInfo(AxisInfo.X_AXIS);
         AxisInfo yInfo = v2D.getAxisInfo(AxisInfo.Y_AXIS);
         
         contourPanel.initializeWorldCoords(new CoordBounds(xInfo.getMin(), 
                                                            yInfo.getMax(), 
                                                            xInfo.getMax(), 
                                                            yInfo.getMin()));
         contourPanel.changeData(v2D);
         
         //paint all of the components
         paintComponents();
         
         this.layerPanel.revalidate();
         this.layerPanel.repaint();
      }
   }

   public void dataChanged()
   {
      contourPanel.reRender();
   }

   public void addActionListener(ActionListener act_listener)
   {
      if (act_listener==null)
         return;
      if (!listenerVec.contains(act_listener))
         listenerVec.add(act_listener);
   }

   public void removeActionListener(ActionListener act_listener)
   {
      if (act_listener==null)
         return;
      listenerVec.remove(act_listener);
   }

   public void removeAllActionListeners()
   {
      listenerVec.clear();
   }

   public JPanel getDisplayPanel()
   {
      return layerPanel;
   }

   public ViewControl[] getControls()
   {
      return controls;
   }

   public ViewMenuItem[] getMenuItems()
   {
      return menuItems;
   }

   public void kill()
   {
   }
//-------=[ End methods mplemented for the IViewComponent2D interface ]=------//
   
   
// -----------=[ Implemented for the IColorScaleAddible interface ]=-----------//
   public AxisInfo getValueAxisInfo()
   {
      return getAxisInformation(AxisInfo.Z_AXIS);
   }

   public double getLogScale()
   {
      if (intensitySlider==null)
         return 0;
      
      return intensitySlider.getValue();
   }

   public AxisInfo getAxisInformation(int axiscode)
   {
      float min = 0;
      float max = 0;
      
      AxisInfo axisInfo = contourPanel.getData().getAxisInfo(axiscode);
      String label = axisInfo.getLabel();
      String units = axisInfo.getUnits();
      
      if (axiscode == AxisInfo.X_AXIS)
      {
         min = contourPanel.getGlobalWorldCoords().getX1();
         max = contourPanel.getGlobalWorldCoords().getX2();
      }
      else if (axiscode == AxisInfo.Y_AXIS)
      {
         min = contourPanel.getGlobalWorldCoords().getY1();
         max = contourPanel.getGlobalWorldCoords().getY2();
      }
      else
      {
         min = contourPanel.getContours().getLowestLevel();
         max = contourPanel.getContours().getHighestLevel();
      }
      
      return new AxisInfo(min, max, label, units, AxisInfo.LINEAR);
   }

   public String getTitle()
   {
      return contourPanel.getData().getTitle();
   }

   public int getPrecision()
   {
      return getNumSigFigs();
   }

   public Font getFont()
   {
      //TODO Make the font adjustable
      return FontUtil.LABEL_FONT2;
   }

   public CoordBounds getLocalCoordBounds()
   {
      return contourPanel.getLocalWorldCoords().MakeCopy();
   }

   public CoordBounds getGlobalCoordBounds()
   {
      return contourPanel.getGlobalWorldCoords().MakeCopy();
   }

   public Rectangle getRegionInfo()
   {
      return new Rectangle(contourPanel.getBounds());
   }
//-----------=[ Implemented for the IColorScaleAddible interface ]=-----------//
   
   
//--------------=[ Getter/setter methods for the controls ]=------------------//
   
   public Contours getActiveContours()
   {
      if (contourControl==null)
         return null;
      
      return (Contours)contourControl.getControlValue();
   }
   
   public void setActiveContours(Contours contours)
   {
      if (contours==null || contourControl==null)
         return;
      
      contourControl.setControlValue(contours);
   }
   
   public int[] getLineStyles()
   {
      if (lineStyleControl==null)
         return DEFAULT_STYLES;
      
      return lineStyleControl.getEnabledLineStyles();
   }
   
   public void setLineStyles(int[] styles)
   {
      if (styles==null || lineStyleControl==null)
         return;
      
      lineStyleControl.setEnabledLineStyles(styles);
   }
   
   public boolean getIsLabelEnabled()
   {
      if (labelControl==null)
         return DEFAULT_SHOW_LABEL;
      
      return labelControl.isChecked();
   }
   
   public void setIsLabelEnabled(boolean enable)
   {
      if (labelControl==null)
         return;
      
      labelControl.setChecked(enable);
   }
   
   public int getLabelEvery()
   {
      if (labelControl==null || labelControl.getSpinnerValue()==null)
         return DEFAULT_LABEL_EVERY_N_LINES;
      
      return ((Integer)labelControl.getSpinnerValue()).intValue();
   }
   
   public void setLabelEvery(int nthLine)
   {
      if (labelControl==null)
         return;
      
      labelControl.setSpinnerValue(new Integer(nthLine));
   }
   
   public boolean getIsLabelFormattingEnabled()
   {
      if (sigFigControl==null)
         return DEFAULT_ENABLE_LABEL_FORMATTING;
      
      return sigFigControl.isChecked();
   }
   
   public void setIsLabelFormattingEnabled(boolean enable)
   {
      if (sigFigControl==null)
         return;
      
      sigFigControl.setChecked(enable);
   }
   
   public int getNumSigFigs()
   {
      if (sigFigControl==null || sigFigControl.getSpinnerValue()==null)
         return DEFAULT_NUM_SIG_DIGS;
      
      return ((Integer)sigFigControl.getSpinnerValue()).intValue();
   }
   
   public void setNumSigFigs(int num)
   {
      if (sigFigControl==null)
         return;
      
      sigFigControl.setSpinnerValue(new Integer(num));
   }
   
   public Color getBackgroundColor()
   {
      if (backgroundControl ==null)
         return DEFAULT_BACKGROUND_COLOR;
      
      return backgroundControl.getSelectedColor();
   }
   
   public void setBackgroundColor(Color color)
   {
      if (color==null)
         return;
      
      backgroundControl.setSelectedColor(color);
   }
//------------=[ End getter/setter methods for the controls ]=----------------//
   
   
//-------------=[ Getter/setter methods for the menu items ]=-----------------//
   public boolean getIsDoubleSidedColorScale()
   {
      if (isDoubleSidedItem==null)
         return DEFAULT_IS_DOUBLE_SIDED;
      
      return isDoubleSidedItem.isSelected();
   }
   
   public void setIsDoubleSidedColorScale(boolean doubleSided)
   {
      if (isDoubleSidedItem==null)
         return;
      
      isDoubleSidedItem.setSelected(doubleSided);
      //DON'T CALL doClick() because it will cause an infinite loop
      //call doClick() so that the actionListeners are invoked and 
      //will act just like the user has clicked the isDoubleSidedItem 
      //checkbox
      //isDoubleSidedItem.doClick();
   }
   
   public boolean getPreserveAspectRatio()
   {
      if (aspectRatioItem==null)
         return DEFAULT_PRESERVE_ASPECT_RATIO;
      
      return aspectRatioItem.isSelected();
   }
   
   public void setPreserveAspectRatio(boolean isSelected)
   {
      //update the menu item to be either selected or deselected
      if (aspectRatioItem!=null)
        aspectRatioItem.setSelected(isSelected);
      
      //update the ContourJPanel
        contourPanel.setPreserveAspectRatio(isSelected);
        contourPanel.reRender();
   }
//-----------=[ End getter/setter methods for the menu items ]=---------------//
   
   
//-------------------=[ Extra getter/setter methods ]=------------------------//
   public String getColorScale()
   {
      if (colorOrColorscale.isColorScale())
         return colorOrColorscale.getColorScale();
      else
      {
         if (controlColorscale!=null)
            return controlColorscale.getColorScale();
         else
            return DEFAULT_COLOR_SCALE;
      }
//TODO Fix this to return a good value if the the contour lines are colored 
//     colored with a solid color
//         return IndexColorMaker.getColorScaleForColor(
//                                   colorOrColorscale.getColor());
   }
   
   public void setColorScale(Color color)
   {
      //record that a solid color is used
        this.colorOrColorscale.setUseColor(color);
//TODO Fix this so that the ControlColorScale's display is updated
/*
      //update the ControlColorScale's display
        this.controlColorscale.setColorScale(
            IndexColorMaker.getColorScaleForColor(color), false);
*/
      //update the ContourJPanel's display
        this.contourPanel.setColorScale(color);
      //make the 'double sided' item disabled because it does not 
      //apply (it only applies for builtin colorscales)
        this.isDoubleSidedItem.setEnabled(false);
   }
   
   public void setColorScale(String colorscale)
   {
      //isDoubleSided could equal 
      //(contourPanel.getContours().getLowestLevel()<0);
      boolean isDoubleSided = getIsDoubleSidedColorScale();
      setColorScale(colorscale, isDoubleSided);
   }
   
   public void setColorScale(String colorscale, boolean isDoubleSided)
   {
      if (!IndexColorMaker.isValidColorScaleName(colorscale))
         return;
      
      //record that a colorscale is being used
        this.colorOrColorscale.setUseColorScale(colorscale);
      //then enable the 'double sided' item because it does apply here
        this.isDoubleSidedItem.setEnabled(true);
      //update the ControlColorScale's display
        this.controlColorscale.setColorScale(colorscale, isDoubleSided);
      //update the ContourJPanel's disply
        this.contourPanel.setColorScale(colorscale, isDoubleSided);
      //update the 'double sided' display
        setIsDoubleSidedColorScale(isDoubleSided);
   }
   
   
   public AxisOverlay2D getAxisOverlay()
   {
      return (AxisOverlay2D)transparencies.get(0);
   }
//-----------------=[ End extra getter/setter methods ]=----------------------//
   
   
//--------------------=[ Private methods ]=-----------------------------------//
   private void sendMessage(String message)
   {
      ActionListener listener;
      for (int i=0; i<listenerVec.size(); i++)
      {
         listener = (ActionListener)listenerVec.elementAt(i);
         if (listener!=null)
            listener.actionPerformed(new ActionEvent(this, 0, message));
      }
   }
   
   private void paintComponents()
   {
      Component topPanel = this.layerPanel;
      while ( topPanel.getParent() != null)
         topPanel = topPanel.getParent();
      
      topPanel.repaint();
   }
   
   private void initFields(IVirtualArray2D v2D, ContourJPanel contourPanel)
   {
      this.layerPanel = new JPanel();
      this.listenerVec = new Vector();
      this.contourPanel = contourPanel;
        this.contourPanel.addComponentListener(new ResizeListener());
        this.contourPanel.addActionListener(new CursorListener());
        this.contourPanel.addActionListener(new ContourPanelListener());
      this.colorOrColorscale = new ColorOrColorScale(DEFAULT_COLOR_SCALE);
      this.transparencies = new Vector();
      
      dataChanged(v2D);
   }
   
   //----------=[ Methods used to generate the menu items ]=------------------//
   private void initMenuItems(Color lineColor, String colorScale, 
                              boolean isDoubleSided, boolean useColorScale, 
                              boolean preserveAspectRatio)
   {
      //make the controls for the line colors
        //first make the controls to select if the scale should be double sided
        isDoubleSidedItem = 
          new JCheckBoxMenuItem("Is Double Sided");
        isDoubleSidedItem.setSelected(isDoubleSided);
        final ActionListener doubleSidedListener = new ActionListener()
        {
           /**
            * Used to store the color scale.  If the user selects to 
            * have the color scale double sided, this class will be able 
            * to remember which color scale to make/not make double 
            * sided.
            */
           private String colorScale = 
              IndexColorMaker.HEATED_OBJECT_SCALE_2;
         
           /**
            * Invoked when the user selects a different color scale from the 
            * menu of built-in color scales, or when the user checks the 
            * JCheckboxMenuitem to make the colorscale double sided or not.
            */
           public void actionPerformed(ActionEvent event)
           {
              if ( !(event.getSource() instanceof JCheckBoxMenuItem) )
              {
                 //then a menuitem was selected that specifies one of the 
                 //built-in colorscales and getActionCommand() returns 
                 //the String that identifies the colorscale
                 colorScale = event.getActionCommand();
              }
             
              setColorScale(colorScale);
           }
        };
        isDoubleSidedItem.addActionListener(doubleSidedListener);
      
        //second make the menu that contains the built-in colorscales
        
        //make the listener that will listen to when the user selects a 
        //new built-in colorscale
        ActionListener colorListener = new ActionListener()
        {
           /**
            * Invoked when the user selects an item from the menu that 
            * contains all of the built-in colorscales.
            */
           public void actionPerformed(ActionEvent event)
           {
              doubleSidedListener.actionPerformed(event);
           }
        };
        
        //make the menu that will list the built-in colorscales
        ViewMenuItem colorscaleMenu = 
           new ViewMenuItem(ViewMenuItem.PUT_IN_OPTIONS, 
                            MenuItemMaker.getColorScaleMenu(colorListener));
       
        //make the item that will allow one color to be selected for the 
        //contour lines
        contourColorItem = new ColorControl("", " Solid Color ", 
                                            DEFAULT_LINE_COLOR,
                                            ColorSelector.TABBED);
        contourColorItem.setSelectedColor(lineColor);
        contourColorItem.setBorderVisible(false);
        contourColorItem.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent event)
           {
              if (event.getActionCommand().equals(ColorControl.COLOR_CHANGED))
              {
                 setColorScale(contourColorItem.getSelectedColor());
              }
           }
        });
       
        //make sure that the right color or color scale is applied to the 
        //ContourJPanel and ControlColorScale
        if (useColorScale)
           setColorScale(colorScale);
        else
           setColorScale(lineColor);
       
        //make the menu that will hold the two items made
        JMenu lineColorMenu = new JMenu("Line Colors");
          lineColorMenu.add(contourColorItem);
          lineColorMenu.add(new JSeparator());
          lineColorMenu.add(colorscaleMenu.getItem());
          lineColorMenu.add(isDoubleSidedItem);
       
      //create the array of menu items
      menuItems = new ViewMenuItem[2];
        menuItems[0] = new ViewMenuItem(ViewMenuItem.PUT_IN_OPTIONS, 
                                       lineColorMenu);
      //now make the controls for preserving the aspect ratio 
        menuItems[1] = 
           new ViewMenuItem(ViewMenuItem.PUT_IN_OPTIONS, 
                            generateAspectRatioMenuItem(preserveAspectRatio));
   }
   
   private JCheckBoxMenuItem generateAspectRatioMenuItem(boolean 
                                                          preserveAspectRatio)
   {
      //make the control for the aspect ratio
      aspectRatioItem = 
         new JCheckBoxMenuItem("Preserve Aspect Ratio");
        aspectRatioItem.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent event)
           {
              setPreserveAspectRatio(aspectRatioItem.isSelected());
           }
        });
        setPreserveAspectRatio(preserveAspectRatio);
        
     return aspectRatioItem;
   }
   //---------=[ End methods used to generate the menu items ]=---------------//

   
   //------------=[ Methods used to generate the controls ]=------------------//
   /**
    * Called to instatiate all of the controls used with this view 
    * component.
    * 
    * @param minValue  This is used to describe how to generate uniformly 
    *                  spaced contour levels.  It is the value of the lowest 
    *                  contour level
    * @param maxValue  This is used to describe ho to generate unformly 
    *                  spaced contour levels.  It is the value of the highest 
    *                  uniform contour level.
    * @param numLevels This is used to describe how to generate uniformly 
    *                  spaced contour levels.  It specifies the number of 
    *                  contour levels in the group of uniformly spaced 
    *                  contour levels.
    * @param levels    This is the list of manually entered contour levels.
    * @param useManualLevels If true the controls used to enter the manually 
    *                        entered contours levels will initially have the 
    *                        focus.  If false the controls used to enter 
    *                        the data to generate the uniformly spaced 
    *                        contour levels will have focus.
    */
   private void initControls(float minValue, float maxValue, int numLevels, 
                             float[] levels, boolean useManualLevels, 
                             boolean preserveAspectRatio, 
                             int[] styles, boolean[] stylesEnabled, 
                             boolean enableLabels, int everyNthLineLabeled, 
                             boolean enableFormatting, int numSigFig, 
                             String colorScale, boolean isDoubleSided, 
                             Color backgroundColor, Color contourColor)
   {
      controls = new ViewControl[7];
      controls[0] = generateIntensityControls();
      controls[1] = generateColorScaleControls(colorScale, isDoubleSided);
      controls[2] = generateContourControls(minValue, maxValue, numLevels, 
                                            levels, useManualLevels);
      controls[3] = generateLineStyleControl(styles, stylesEnabled);
      controls[4] = generateLabelControls(enableLabels, everyNthLineLabeled);
      controls[5] = generateSigFigControls(enableFormatting, numSigFig);
      controls[6] = generateBackgroundControls(backgroundColor);
   }
   
   private ColorControl generateBackgroundControls(Color color)
   {
      backgroundControl = new ColorControl("Background Color", 
                                           " Background Color      ", 
                                           color, ColorSelector.TABBED);
      backgroundControl.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            if (event.getActionCommand().equals(ColorControl.COLOR_CHANGED))
            {
               contourPanel.setBackgroundColor(
                     backgroundControl.getSelectedColor());
               contourPanel.reRender();
            }
         }
      });
      backgroundControl.send_message(ColorControl.COLOR_CHANGED);
      
      System.out.println("generated the background control:  "+backgroundControl);
      System.out.println("  isVisible = "+backgroundControl.isVisible());
      
      return backgroundControl;
   }
   
   private ControlSlider generateIntensityControls()
   {
      intensitySlider = new ControlSlider(0, 100, 100);
      intensitySlider.setTitle("Intensity Slider");
      intensitySlider.setValue(0);
      intensitySlider.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            contourPanel.setLogScale(intensitySlider.getValue());
            paintComponents();
         }
      });
      return intensitySlider;
   }
   
   private ControlColorScale generateColorScaleControls(String scale, 
                                                        boolean isDoubleSided)
   {
      this.controlColorscale = 
         new ControlColorScale(scale, isDoubleSided);
         //this, ControlColorScale.HORIZONTAL);
      //   new ControlColorScale(scale, isDoubleSided);
      this.controlColorscale.setTitle("Color Scale");
      //controlColorscale.setAxisVisible(true);
      setColorScale(scale, isDoubleSided);
      return this.controlColorscale;
   }
   
   
   
   private CompositeContourControl generateContourControls(
                                                    float minValue, 
                                                    float maxValue, 
                                                    int numLevels, 
                                                    float[] levels, 
                                                    boolean showManualControls)
   {
      contourControl = 
         new CompositeContourControl(contourPanel, 
                                     minValue, maxValue, numLevels, 
                                     levels, 
                                     false);
      return contourControl;
   }
   
   private LineStyleControl generateLineStyleControl(int[] styles, 
                                                    boolean[] stylesEnabled)
   {
      lineStyleControl = new LineStyleControl(styles, stylesEnabled);
      return lineStyleControl;
   }
   
   private ControlCheckboxSpinner generateLabelControls(boolean enable, 
                                                       int initialValue)
   {
      int min = 1;
      int max = 100;
      int stepSize = 1;
      if (initialValue<min || initialValue>max)
         initialValue = min;
      
      labelControl = 
         new ControlCheckboxSpinner("Labels", enable, "Label every", 
               new SpinnerNumberModel(initialValue, min, max, stepSize), 
                  new Integer(DEFAULT_LABEL_EVERY_N_LINES), 
                     new Integer(initialValue));
      labelControl.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            String mssg = event.getActionCommand();
            if (mssg.equals(SpinnerControl.SPINNER_CHANGED) || 
                  mssg.equals(ControlCheckbox.CHECKBOX_CHANGED))
            {
               if (labelControl.isChecked())
                  contourPanel.setShowLabelEvery((
                     (Integer)labelControl.getSpinnerValue()).
                        intValue());
               else
                  contourPanel.setShowAllLabels(false);
            }
         }
      });
      labelControl.send_message(ControlCheckbox.CHECKBOX_CHANGED);
      
      return labelControl;
   }
   
   private ControlCheckboxSpinner generateSigFigControls(boolean enable, 
                                                        int initialValue)
   {
      int min = 1;
      int max = 19;
      int stepSize = 1;
      if (initialValue<min || initialValue>max)
         initialValue = min;
      
      sigFigControl = 
         new ControlCheckboxSpinner("Significant Figures", enable, 
               "Number of significant figures", 
               new SpinnerNumberModel(initialValue, min, max, stepSize), 
               new Integer(DEFAULT_NUM_SIG_DIGS), new Integer(initialValue));
      sigFigControl.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent event)
           {
              String mssg = event.getActionCommand();
              if (mssg.equals(SpinnerControl.SPINNER_CHANGED) || 
                    mssg.equals(ControlCheckbox.CHECKBOX_CHANGED))
              {
                 if (sigFigControl.isChecked())
                    contourPanel.setNumSigDigits(
                          ((Integer)sigFigControl.getSpinnerValue()).
                             intValue());
                 else
                    contourPanel.setNumSigDigits(-1);
              }
           }
        });
      sigFigControl.send_message(ControlCheckbox.CHECKBOX_CHANGED);
      
      return sigFigControl;
   }
   //------------=[ End methods used to generate the controls ]=--------------//
   
//------------------=[ End private methods ]=---------------------------------//   
   
   
//--------------------=[ Custom controls ]=-----------------------------------//
   /**
    * Controls used to choose the line style for a set of lines.  Currently, 
    * there are four line styles (solid, dashed, dashed-dotted, or dotted).  
    * Thus, this control has options for choosing the line style for a set 
    * of four lines.  It also contains controls for disabling certain lines.  
    * For example, perhaps the pattern, solid-dashed-dashed, is supposed to 
    * be used to generate the line styles.  Then, the fourth line would be 
    * disabled so that only the first three styles specified are used.
    */
   private class LineStyleControl extends ViewControl
   {
      /**
       * "Styles" - This static constant String is a key for referencing 
       * the styles that are displayed on this control.  Each style is 
       * identified by an integer code 
       * (see {@link ContourJPanel ContourJPanel}).  
       * The value that this key references is an int[].
       */
      public final String STYLES_KEY = "Styles";
      /**
       * "Enabled status" - Each style displayed on this control has the 
       * ability to be disabled/enabled via a checkbox.  This static 
       * contstant String is a key for referencing which styles are 
       * enabled and which ones are not.  The value that this key references 
       * is a boolean[].  Given index <code>i</code> in this array, if its 
       * value is <code>true</code> the style at index <code>(i+1)</code> is 
       * enabled.  If it is <code>false</code> the style is disabled.  
       * Note:  The style at index 0 is always enabled.  That is why the 
       * <code>ith</code> element in the boolean array describes the 
       * <code>(i+1)th</code>.  The boolean array starts describing the 
       * styles starting with the style at index 1 (not 0).
       */
      public final String ENABLED_STATUS_KEY = "Enabled status";
      
      /**
       * Specifies the number controls for line styles that this ViewControl 
       * will contain.  Currently, there are only four line styles (solid, 
       * dashed, dashed-dotted, and dotted).  Thus, the value of this field 
       * is 4.
       */
      private final int NUM_STYLE_CONTROLS = 4;
      
      /**
       * Each element in this array is a ViewControl that contains a combobox 
       * that contains the possible styles to associate to a given line.  
       * There is one such control for each of the lines in the group 
       * of lines displayed on this control.
       * <p>
       * The length of this array is equal to <code>NUM_STYLE_CONTROLS</code>.
       */
      private ControlCheckboxCombobox[] styleBoxes;
      
      /**
       * Constructs this ViewControl.
       * 
       * @param styles The initially selected styles for each of the 
       *               controls on this ViewControl.  The length of this 
       *               array should equal 
       *               {@link #getNumOfStyles() getNumOfStyles()}.  If it 
       *              is too short, {@link ContourViewComponent#DEFAULT_STYLES 
       *              DEFAULT_STYLES} is used.  If it is too long, the extra 
       *              elements are ignored.
       * @param linesEnabled The ith element of this array specifies if the 
       *                     line style control with index (i+1) should 
       *                     be enabled.  That is, the control for the first 
       *                     line style is enabled by default and cannot be 
       *                     changed.  This array specifies if the rest of 
       *                     the line styles controls should be enabled or 
       *                     not.  Therefore, the length of this array should 
       *                     equal <code>{@link #getNumOfStyles() 
       *                     getNumOfStyles()}-1</code>.  If it is too short, 
       *                     {@link 
       *                     ContourViewComponent#DEFAULT_ENABLE_STYLE_ARR 
       *                     DEFAULT_ENABLE_STYLE_ARR} is used.  If it is 
       *                     too long, the extra elements are ignored.
       */
      public LineStyleControl(int[] styles, boolean[] linesEnabled)
      {
         super("Line styles");
         
         //verify the inputs
         if (styles==null || linesEnabled.length<NUM_STYLE_CONTROLS)
            styles = DEFAULT_STYLES;
         if (linesEnabled==null || linesEnabled.length<(NUM_STYLE_CONTROLS-1))
            linesEnabled = DEFAULT_ENABLE_STYLE_ARR;
         
         //construct comboboxes that allow the user to select the line style
         styleBoxes = new ControlCheckboxCombobox[NUM_STYLE_CONTROLS];
         for (int i=0; i<NUM_STYLE_CONTROLS; i++)
            styleBoxes[i] = constructStyleBox(i,
                                              (i==0)?true:linesEnabled[i-1],
                                              styles[i]);
         
         //disable the ability to turn off the first line
         styleBoxes[0].setCheckboxEnabled(false);
         
         //make the redraw button and attach it to a panel
         JButton drawButton = new JButton("Redraw");
           drawButton.addActionListener(new DrawListener());
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
           buttonPanel.add(drawButton);
           
         //add the comboboxes to their own panel
         JPanel stylePanel = new JPanel(); 
           BoxLayout layout = new BoxLayout(stylePanel, BoxLayout.Y_AXIS);
           stylePanel.setLayout(layout);
           for (int i=0; i<styleBoxes.length; i++)
              stylePanel.add(styleBoxes[i]);
           
         //add the panels to this control
         setLayout(new BorderLayout());
           add(stylePanel, BorderLayout.CENTER);
           add(buttonPanel, BorderLayout.SOUTH);
      }
      
      /**
       * Used to construct a combobox containing options for selecting the 
       * one of the possible line styles.
       * 
       * @param index Used to produce the label that is placed in front 
       *              of the combobox.
       * @return A combobox containing the possible styles for a line.
       */
      private ControlCheckboxCombobox constructStyleBox(int index, 
                                                        boolean isChecked, 
                                                        int style)
      {
         String[] styles = new String[NUM_STYLE_CONTROLS];
           styles[getIndexForStyle(ContourJPanel.SOLID)] = 
                                                    "solid";
           styles[getIndexForStyle(ContourJPanel.DASHED)] = 
                                                    "dashed";
           styles[getIndexForStyle(ContourJPanel.DASHED_DOTTED)] = 
                                                    "dahsed dotted";
           styles[getIndexForStyle(ContourJPanel.DOTTED)] = 
                                                    "dotted";
         ControlCheckboxCombobox combobox = 
            new ControlCheckboxCombobox("", isChecked, 
                                        "Line "+(index+1), styles);
            combobox.setSelectedIndex(getIndexForStyle(style));
         return combobox;
      }
      
      /**
       * Each combobox in the array <code>styleComboBoxes</code>, has its 
       * contents listed in the same order.  Moreover, each of the 
       * contents of the combobox correspond to a certain line style.  
       * Given the line style, this method returns the index of the entry 
       * in one of the comboboxes that corresponds to this style.
       * 
       * @param style An integer identifying a line style (see 
       *              {@link ContourJPanel ContourJPanel}).
       * @return The index of the item in an one of the comboboxes from 
       *         the array <code>styleComboBoxes</code> that 
       *         corresponds to the specified style.  Note:  If 
       *         <code>style</code> is invalid, -1 is returned.
       */
      private int getIndexForStyle(int style)
      {
         switch (style)
         {
            case ContourJPanel.SOLID:
               return 0;
            case ContourJPanel.DASHED:
               return 1;
            case ContourJPanel.DASHED_DOTTED:
               return 2;
            case ContourJPanel.DOTTED:
               return 3;
            default:
               return -1;
         }
      }
      
      /**
       * Each combobox in the array <code>styleComboBoxes</code>, has its 
       * contents listed in the same order.  Moreover, each of the 
       * contents of the combobox correspond to a certain line style.  
       * Given the index of an item in one of these comboboxes, this method 
       * returns the style specified at that index.
       * 
       * @param index The index of the item in an one of the comboboxes from 
       *              the array <code>styleComboBoxes</code>. 
       * @return The style associated with the given index from any one of 
       *         the comboboxes from the array <code>styleComboBoxes</code>.  
       *         Note:  If <code>index</code> is invalid, -1 is returned.
       */
      private int getStyleForIndex(int index)
      {
         switch (index)
         {
            case 0:
               return ContourJPanel.SOLID;
            case 1:
               return ContourJPanel.DASHED;
            case 2:
               return ContourJPanel.DASHED_DOTTED;
            case 3:
               return ContourJPanel.DOTTED;
            default:
               return -1;
         }
      }
      
      /**
       * Used to set the line styles displayed for the set of lines 
       * described in this control.
       * 
       * @param value An int[] where each element specifies a line style 
       *              (see {@link ContourJPanel ContourJPanel}).  The first  
       *              element from this array and is used to set the style 
       *              for the first line.  Each elements is read until the 
       *              end of this array is reached or until there are no 
       *              more lines to adjust.
       */
      public void setControlValue(Object value)
      {
         if (value==null)
            return;
         
         if ( !(value instanceof int[]) ) 
            return;
         
         int[] styles = (int[])value;
         int min = Math.min(styles.length, styleBoxes.length);
         for (int i=0; i<min; i++)
            styleBoxes[i].setSelectedIndex(getIndexForStyle(styles[i]));
      }
      
      /**
       * Used to get the styles associated with each line described in this 
       * control.
       * 
       * @return An int[] where the ith element of the array describes the 
       *         line style of the ith line on this control.  
       *         (see {@link ContourJPanel ContourJPanel}).
       */
      public Object getControlValue()
      {
         return getLineStyles();
      }
      
      /**
       * Used to get the state of this control.  This state contains the 
       * styles selected and if lines are enabled or not.
       * 
       * @param isDefault If true the default configuration is returned.
       *                  If false the current configuration is returned.
       */
      public ObjectState getObjectState(boolean isDefault)
      {
         ObjectState state = super.getObjectState(isDefault);
           if (isDefault)
           {
              state.insert(STYLES_KEY, DEFAULT_STYLES);
              state.insert(ENABLED_STATUS_KEY, DEFAULT_ENABLE_STYLE_ARR);
           }
           else
           {
              state.insert(STYLES_KEY, (int[])getControlValue());
              state.insert(ENABLED_STATUS_KEY, getEnabledLines());
           }
         return state;
      }
      
      /**
       * Used to set the state of this control (i.e. set which styles are 
       * selected and if certain lines are enabled or not).
       * 
       * @param state Encapsulates which styles are selected and which lines 
       *              are enabled.
       */
      public void setObjectState(ObjectState state)
      {
         if (state==null)
            return;
         
         Object val = state.get(STYLES_KEY);
         if (val!=null)
            setControlValue((int[])val);
         
         val = state.get(ENABLED_STATUS_KEY);
         if (val!=null)
         {
            boolean[] enabledArr = (boolean[])val;
            for (int i=0; i<enabledArr.length; i++)
               styleBoxes[i+1].setChecked(enabledArr[i]);
         }
      }

      /**
       * Used to get a copy of this control.
       * 
       * @return A deep copy of this control.
       */
      public ViewControl copy()
      {
         LineStyleControl copy = 
            new LineStyleControl((int[])getControlValue(), getEnabledLines());
         copy.setObjectState(this.getObjectState(false));
         return copy;
      }
      
      /**
       * Used to determine which lines are enabled.
       * 
       * @return An array where the ith element in the array has a value 
       *         <code>true</code> if the <code>(i+1)th</code> line on this 
       *         control is enabled.  If the value is <code>false</code> the 
       *         line is not enabled.  Note:  the first line style is 
       *         always enabled.
       */
      public boolean[] getEnabledLines()
      {
         boolean[] b = new boolean[styleBoxes.length-1];
         for (int i=1; i<styleBoxes.length; i++)
            b[i-1] = styleBoxes[i].isChecked();
         return b;
      }
      
      /**
       * Used to determine the line styles that are selected for each line.  
       * <p>
       * Note:  This method does not analyze which lines are enabled and 
       * disabled.  Instead, it returns the styles that are associated with 
       * each line as if each line was enabled.
       * 
       * @return An int[] specifying the line styles.
       */
      public int[] getLineStyles()
      {
         int[] styles = new int[styleBoxes.length];
         for (int i=0; i<styleBoxes.length; i++)
            styles[i] = getStyleForIndex(styleBoxes[i].getSelectedIndex());
         return styles;
      }
      
      /**
       * Used to get the array of line styles that are enabled.
       * 
       * @return The array of line styles that are enabled.
       */
      public int[] getEnabledLineStyles()
      {
         int[] totalStyles = getLineStyles();
         boolean[] enabledArr = getEnabledLines();
         
         int numEnabled = 1;
         for (int i=0; i<enabledArr.length; i++)
            if (enabledArr[i])
               numEnabled++;
         
         int[] realStyles = new int[numEnabled];
         int currentIndex = 0;
            realStyles[currentIndex++] = totalStyles[0];
         int i=0;
         while (i<enabledArr.length && currentIndex<realStyles.length)
         {
            if (enabledArr[i])
               realStyles[currentIndex++] = totalStyles[i+1];
            
            i++;
         }
         
         return realStyles;
      }
      
      /**
       * Used to set the enabled line styles.  If <code>styles</code> has 
       * <code>n</code> elements, then only the first <code>n</code> lines 
       * will be enabled and will have the styles specified by 
       * <code>styles</code>.
       * 
       * @param styles The array of line styles.
       */
      public void setEnabledLineStyles(int[] styles)
      {
         if (styles==null)
            return;
         
         for (int i=0; i<styleBoxes.length; i++)
         {
            if (i<styles.length)
            {
               styleBoxes[i].setSelectedIndex(getIndexForStyle(styles[i]));
               styleBoxes[i].setChecked(true);
            }
            else
               styleBoxes[i].setChecked(false);
         }
      }
      
      /**
       * Used to determine the number of possible line styles.  If there are 
       * <code>n</code> styles, then there are controls on this ViewControl 
       * to specify a pattern of styles for a set of <code>n</code> lines.
       * 
       * @return The number of possible line styles.
       */
      public int getNumOfStyles()
      {
         return NUM_STYLE_CONTROLS;
      }
      
      /**
       * Used to listen to the 'Draw' button being pressed.
       */
      private class DrawListener implements ActionListener
      {
         /**
          * Invoked when the 'Draw' button is pressed.  This updates the 
          * contour view to reflect the new line styles.
          */
         public void actionPerformed(ActionEvent event)
         {
            int counter = 0;
            for (int i=0; i<styleBoxes.length; i++)
               if (styleBoxes[i].isChecked())
                  counter++;
            
            int[] currentStyles = new int[counter];
            int j = 0;
            for (int i=0; i<NUM_STYLE_CONTROLS; i++)
               if (styleBoxes[i].isChecked())
                  currentStyles[j++] = 
                     getStyleForIndex(styleBoxes[i].getSelectedIndex());
               
            contourPanel.setLineStyles(currentStyles);
         }
      }
   }
//--------------------=[ End custom controls ]=-------------------------------//

   
//-------------------=[ Listens to changes in this component ]=---------------//
   private class ResizeListener extends ComponentAdapter
   {
      public void componentResized(ComponentEvent event)
      {
         contourPanel.reRender();
      }
   }

   private class CursorListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         if ( event.getActionCommand().equals(CoordJPanel.CURSOR_MOVED) )
            if (!contourPanel.isDoingBox())
               sendMessage(POINTED_AT_CHANGED);
      }
   }
   
   private class ContourPanelListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         String command = event.getActionCommand();
         
         if (command.equals(ContourJPanel.ZOOM_IN) || 
               command.equals(ContourJPanel.RESET_ZOOM))
            paintComponents();
      }
   }
//----------------=[ End listens to changes in this component ]=--------------//
   
   
//-----=[ Used to monitor the color/colorscale of the contour lines ]=--------//
   /**
    * Used exclusively by the enclosing class to maintain:
    * <ol>
    *   <li>
    *     if the <code>ContourJPanel</code> has its contour lines 
    *     colored with a solid color or a colorscale.
    *   </li>
    *   <li>
    *     a reference to the corresponding color or colorscale.
    *   </li>
    * </ol>
    */
   private class ColorOrColorScale
   {
      /** A reference to the current color or colorscale. */
      private Object current;
      
      /**
       * Used to initialize <code>current</code>.
       * 
       * @param val The value to initialize <code>current</code> to.
       */
      private ColorOrColorScale(Object val)
      {
         setCurrent(val);
      }
      
      /**
       * Constructs this object to first encapsulate the given color.
       * 
       * @param color The current line color of the contour lines.
       */
      public ColorOrColorScale(Color color)
      {
         this((Object)color);
      }
      
      /**
       * Constructs this object to first encapsulate the given colorscale.
       * 
       * @param colorscale The colorscale used to color the contour lines.
       */
      public ColorOrColorScale(String colorscale)
      {
         this((Object)colorscale);
      }
      
      /**
       * Used to set the value of <code>current</code>.
       * 
       * @param val The value of <code>current</code>.
       */
      private void setCurrent(Object val)
      {
         this.current = val;
      }
      
      /**
       * Instructs this object to encapsulate only the given color.
       * 
       * @param color The color used to draw the contour lines.
       */
      public void setUseColor(Color color)
      {
         this.current = color;
      }
      
      /**
       * Instructs this object to encapsulate only the given colorscale.
       * 
       * @param colorscale The colorscale used to determine the color 
       *                   to draw the color lines.
       */
      public void setUseColorScale(String colorscale)
      {
         this.current = colorscale;
      }
      
      /**
       * Used to get the value encapsulated by this object.
       * 
       * @return The value that is currently encapsulated by this 
       *         object.
       */
      public Object getColorOrColorScale()
      {
         return current;
      }
      
      /**
       * Used to get the colorscale encapsulated by this object if and only 
       * if this object is encapsulating a colorscale.  This can be 
       * verified by using the {@link #isColorScale() isColorScale()} method.  
       * If this object is not encapsulating a colorscale a 
       * <code>ClassCastException</code> will be thrown.
       * 
       * @return The colorscale that this object is encapsulating.
       */
      public String getColorScale()
      {
         return (String)current;
      }
      
      /**
       * Used to get the color encapsulated by this object if and only if 
       * this object is encapsulating a color.  This can be verified by 
       * using the {@link #isColor() isColor()} method.  If this object is 
       * not encapsulating a color, a <code>ClassCastException</code> will 
       * be thrown.
       * 
       * @return The color that this object is encapsulating.
       */
      public Color getColor()
      {
         return (Color)current;
      }
      
      /**
       * Used to determine if this class is encapsulating a colorscale.
       * 
       * @return True if this class is encapsulating a colorscale and 
       *         false otherwise.
       */
      public boolean isColorScale()
      {
         return (current instanceof String);
      }
      
      /**
       * Used to determine if this class is encapsulating a color.
       * 
       * @return True if this class is encapsulating a color and 
       *         false otherwise.
       */
      public boolean isColor()
      {
         return (current instanceof Color);
      }
   }
//---=[ End used to monitor the color/colorscale of the contour lines ]=------//
   
//----------------=[ Methods used to test this class ]=-----------------------//
   /**
    * Creates a VirtualArray2D of test data that contains a peak.
    * 
    * @param Nx The number of columns in the array.  A good value is 41.
    * @param Ny The number of rows in the array.  A good value is 51.
    * @param xRange The x range.  A good value is 3.0.
    * @param yRange The y range.  A good value is 4.0.
    */
   public static VirtualArray2D getTestData(int Nx, int Ny, 
                                            double xRange, double yRange)
   {
      return new VirtualArray2D(getTestDataArr(Nx, Ny, xRange, yRange));
   }
   
   public static float[][] getTestDataArr(int Nx, int Ny, 
                                          double xRange, double yRange)
   {
      float[][] dataArray = new float[Nx][Ny];
      double xstep = 2.0*xRange/(Nx-1);
      double ystep = 2.0*yRange/(Ny-1);
      
      double xi = -xRange;
      double yj = -yRange;
      
      for (int i=0; i<Nx; i++)
      {
         yj = -yRange;
         for (int j=0; j<Ny; j++)
         {
            double temp1 = Math.max(xi+yj-2,0.2);
            double temp2 = Math.max(Math.pow((xi+3.),2)+Math.pow(yj+2.,2),0.2);
            dataArray[i][j] = 
               (float)(Math.pow(xi,2)-Math.pow(yj,2)+1./temp1+1./temp2);
            yj = yj + ystep;
         }
         xi = xi + xstep;
      }
      
      //now put in a peak
      int iPeak = (int)(Nx/3. + 1);
      int jPeak = (int)(Ny/5. + 1);
      
      dataArray[iPeak  ][jPeak  ] =  4.0f;
      dataArray[iPeak  ][jPeak+1] =  6.0f;
      dataArray[iPeak  ][jPeak+2] =  4.0f;
      dataArray[iPeak+1][jPeak  ] =  6.0f;
      dataArray[iPeak+1][jPeak+1] = 10.0f;
      dataArray[iPeak+1][jPeak+2] =  6.0f;
      dataArray[iPeak+2][jPeak  ] =  4.0f;
      dataArray[iPeak+2][jPeak+1] =  6.0f;
      dataArray[iPeak+2][jPeak+2] =  4.0f;
      
      //This is just for testing purposes
      /*
      dataArray = new float[Nx][Ny];
      for (int i=0; i<dataArray.length; i++)
         for (int j=0; j<dataArray[i].length; j++)
            dataArray[i][j] = i+j;
      
      for (int i=0; i<Nx; i++)
         for (int j=0; j<Ny; j++)
            dataArray[i][j] = i*i+j*j;
      */
      
      return dataArray;
   }
   
   /**
    * Testbed.  Displays this view along with its controls.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      String scale = IndexColorMaker.HEATED_OBJECT_SCALE_2;
      boolean isDoubleSided = true;
      
      VirtualArray2D arr = getTestData(41,51,3.0,4.0);
      ContourViewComponent contour = new ContourViewComponent(arr);
      
      ControlColorScale controlColorscale = 
         new ControlColorScale(contour, true);
      //   new ControlColorScale(scale, isDoubleSided);
      controlColorscale.setTitle("Color Scale");
      //controlColorscale.setAxisVisible(true);
      controlColorscale.setColorScale(scale, isDoubleSided);
      
      JFrame scaleFrame = new JFrame();
        scaleFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        scaleFrame.getContentPane().add(controlColorscale);
        scaleFrame.pack();
        scaleFrame.setVisible(true);
      
      if (true)
         return;
      
      JFrame frame = new JFrame("ContourViewComponent Test");
        JPanel controlPanel = new JPanel();
        Box box = new Box(BoxLayout.Y_AXIS);
        ViewControl[] controls = contour.getControls();
        for (int i=0; i<controls.length; i++)
           box.add(controls[i]);
        controlPanel.add(box);
        
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
          pane.setLeftComponent(contour.getDisplayPanel());
          pane.setRightComponent(controlPanel);
          pane.setDividerLocation(500);
        
        frame.getContentPane().add(pane);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
      frame.setVisible(true);
   }
//----------------=[ End methods used to test this class ]=-------------------//
}





//---------------------------=[ Unused code ]=--------------------------------//
/*
private ColorControl generateLineColorControl(Color color)
{
   contourColors = new ColorControl("Line Color", 
                                    color, 
                                    ColorSelector.TABBED);
   contourColors.addActionListener(new ActionListener()
   {
      public void actionPerformed(ActionEvent event)
      {
         if (event.getActionCommand().equals(ColorControl.COLOR_CHANGED))
         {
            contourPanel.setColorScale(contourColors.getSelectedColor());
            contourPanel.reRender();
         }
      }
   });
   contourColors.send_message(ColorControl.COLOR_CHANGED);
   return contourColors;
}
*/

/*
public ControlCheckbox generateAspectRatioCheckbox(boolean selected)
{
   /*
    * This is the control used to specify if the aspect ratio should 
    * be preserved.
    /
   aspectRatio = new ControlCheckbox(selected);
   aspectRatio.setText("Preserve Aspect Ratio");
   aspectRatio.addActionListener(new ActionListener()
   {
      public void actionPerformed(ActionEvent event)
      {
         contourPanel.setPreserveAspectRatio(aspectRatio.isSelected());
         contourPanel.reRender();
      }
   });
   
   return aspectRatio;
}
*/

/* Currently these are not used as ViewControls
//these are used as menu items
private ColorControl contourColors;
private ControlCheckbox aspectRatio;
*/

/*
public void setLineColor(Color color)
{
   //if there is a a solid color set for the color of the 
   //contour lines, the checkbox specifying if the 
   //colorscale should be double sided should be disabled 
   //because it does not apply
     isDoubleSidedItem.setEnabled(false);
     
   //set the ColorScaleControl's colorscale
     //record the new colorscale
       this.colorscale = IndexColorMaker.getColorScaleForColor(color);
     //TODO:  Currently setting the colorscale with the second parameter 
     //       'true' (aka specifying that the colorscale is double 
     //       sided), causes the ControlColorScale to stay colored black.
     //       If the second parameter is 'false' the ControlColorScale 
     //       changes to the color 'color'.
     controlColorscale.setColorScale(this.colorscale, false);
   //set the line colors of the ContourJPanel
     contourPanel.setColorScale(color);
     contourPanel.reRender();
}

private void setLineColorScale(String colorscale)
{
   //if 'colorscale' does not correspond to a colorscale that is really 
   //only one color then the double sided control can be enabled
   isDoubleSidedItem.
      setEnabled(!IndexColorMaker.isSolidColorColorscale(colorscale));
   
   //record the new colorscale
   this.colorscale = colorscale;
   
   //set the colorscale for the ColorScaleControl
   this.controlColorscale.
     setColorScale(this.colorscale, isDoubleSidedItem.isSelected());

   //set the colorscale for the ContourJPanel
   contourPanel.setColorScale(colorscale, 
                              isDoubleSidedItem.isSelected());
   contourPanel.reRender();
}
*/


//-------------------------=[ End unused code ]=------------------------------//
