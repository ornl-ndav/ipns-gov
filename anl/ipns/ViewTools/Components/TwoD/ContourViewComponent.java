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
 * Revision 1.9  2005/06/23 21:02:52  kramer
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
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.ViewControls.CompositeContourControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckbox;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckboxCombobox;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckboxSpinner;
import gov.anl.ipns.ViewTools.Components.ViewControls.SpinnerControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
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
public class ContourViewComponent implements IViewComponent2D, Serializable
{
   public static final String CONTOUR_CONTROLS_KEY = "Contour controls key";
   public static final String ASPECT_RATIO_KEY = "Aspect ratio key";
   
   public static final int     DEFAULT_LINE_STYLE   = 
      ContourJPanel.DEFAULT_LINE_STYLE;
   public static final boolean DEFAULT_SHOW_LABEL   = 
      ContourJPanel.DEFAULT_SHOW_LABEL;
   public static final int     DEFAULT_NUM_SIG_DIGS = 
      ContourJPanel.DEFAULT_NUM_SIG_DIGS;
   public static final boolean DEFAULT_PRESERVE_ASPECT_RATIO = 
      ContourJPanel.DEFAULT_PRESERVE_ASPECT_RATIO;
   
   public static final float   DEFAULT_LOWEST_CONTOUR = 
      CompositeContourControl.DEFAULT_LOWEST_CONTOUR;
   public static final float   DEFAULT_HIGHEST_CONTOUR =  
      CompositeContourControl.DEFAULT_HIGHEST_CONTOUR;
   public static final int     DEFAULT_NUM_CONTOURS = 
      CompositeContourControl.DEFAULT_NUM_CONTOURS;
   public static final float[] DEFAULT_MANUAL_LEVELS = 
      CompositeContourControl.DEFAULT_MANUAL_LEVELS;
   
   public static final boolean DEFAULT_ENABLE_LABEL_FORMATTING = true;
   public static final int DEFAULT_LABEL_EVERY_N_LINES = 1;
   public static final int[] DEFAULT_STYLES = {DEFAULT_LINE_STYLE, 
                                               DEFAULT_LINE_STYLE, 
                                               DEFAULT_LINE_STYLE, 
                                               DEFAULT_LINE_STYLE};
   public static final boolean[] DEFAULT_ENABLE_STYLE_ARR = {false, 
                                                             false, 
                                                             false};
   
   /** The Vector of ActionListener associated with this component. */
   private Vector listenerVec;
   private ContourJPanel contourPanel;
   private ViewControl[] controls;
   
   private CompositeContourControl contourControl;
   private ControlCheckbox aspectRatio;
   private LineStyleControl lineStyleControl;
   private ControlCheckboxSpinner labelControl;
   private ControlCheckboxSpinner sigFigControl;
   
   
//-------------------------=[ Constructors ]=---------------------------------//
   public ContourViewComponent(IVirtualArray2D arr)
   {
      this.listenerVec = new Vector();
      this.contourPanel = 
         new ContourJPanel(arr, 
                           DEFAULT_LOWEST_CONTOUR, 
                           DEFAULT_HIGHEST_CONTOUR, 
                           DEFAULT_NUM_CONTOURS);
      
      //now to build the controls
        initControls(DEFAULT_LOWEST_CONTOUR, 
                     DEFAULT_HIGHEST_CONTOUR, 
                     DEFAULT_NUM_CONTOURS, 
                     DEFAULT_MANUAL_LEVELS, 
                     false,
                     DEFAULT_PRESERVE_ASPECT_RATIO, 
                     DEFAULT_STYLES, 
                     DEFAULT_ENABLE_STYLE_ARR, 
                     DEFAULT_SHOW_LABEL, 
                     DEFAULT_LABEL_EVERY_N_LINES, 
                     DEFAULT_ENABLE_LABEL_FORMATTING,
                     DEFAULT_NUM_SIG_DIGS);
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float minValue, float maxValue, int numLevels)
   {
      this(arr);
      this.contourPanel = new ContourJPanel(arr,minValue,maxValue,numLevels);
      
      //now to build the controls
        initControls(minValue, maxValue, numLevels, 
                      DEFAULT_MANUAL_LEVELS, false,
                      DEFAULT_PRESERVE_ASPECT_RATIO, 
                      DEFAULT_STYLES, 
                      DEFAULT_ENABLE_STYLE_ARR, 
                      DEFAULT_SHOW_LABEL, 
                      DEFAULT_LABEL_EVERY_N_LINES, 
                      DEFAULT_ENABLE_LABEL_FORMATTING,
                      DEFAULT_NUM_SIG_DIGS);
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float[] levels)
   {
      this(arr);
      this.contourPanel = new ContourJPanel(arr,levels);
      
      //now to build the controls
        initControls(DEFAULT_LOWEST_CONTOUR, 
                     DEFAULT_HIGHEST_CONTOUR, 
                     DEFAULT_NUM_CONTOURS,
                     levels, 
                     true,
                     DEFAULT_PRESERVE_ASPECT_RATIO, 
                     DEFAULT_STYLES, 
                     DEFAULT_ENABLE_STYLE_ARR, 
                     DEFAULT_SHOW_LABEL, 
                     DEFAULT_LABEL_EVERY_N_LINES, 
                     DEFAULT_ENABLE_LABEL_FORMATTING,
                     DEFAULT_NUM_SIG_DIGS);
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
      
      Object val = state.get(CONTOUR_CONTROLS_KEY);
      if (val!=null)
         contourControl.setObjectState((ObjectState)val);
      
      val = state.get(ASPECT_RATIO_KEY);
      if (val!=null)
         aspectRatio.setSelected( ((Boolean)val).booleanValue() ); 
   }

   public ObjectState getObjectState(boolean is_default)
   {
      ObjectState state = new ObjectState();
        state.insert(CONTOUR_CONTROLS_KEY, 
                     contourControl.getObjectState(is_default));
        
        boolean useAspectRatio = DEFAULT_PRESERVE_ASPECT_RATIO;
        if (!is_default)
           useAspectRatio = aspectRatio.isSelected();
        
        state.insert(ASPECT_RATIO_KEY, new Boolean(useAspectRatio));
      return state;
   }

   public void setPointedAt(floatPoint2D fpt)
   {
      if (fpt==null)
         return;
      
      if (getPointedAt().equals(fpt))
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
      return null;
   }

   public void dataChanged(IVirtualArray2D v2D)
   {
      contourPanel.changeData(v2D);
   }

   public void dataChanged()
   {
      contourPanel.repaint();
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
      return contourPanel;
   }

   public ViewControl[] getControls()
   {
      return controls;
   }

   public ViewMenuItem[] getMenuItems()
   {
      return new ViewMenuItem[0];
   }

   public void kill()
   {
   }
//-------=[ End methods mplemented for the IViewComponent2D interface ]=------//


//------------=[ Methods used to generate the controls ]=---------------------//
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
                             boolean enableFormatting, int numSigFig)
   {
      controls = new ViewControl[5];
      controls[0] = generateContourControls(minValue, maxValue, numLevels, 
                                            levels, useManualLevels);
      controls[1] = generateAspectRatioCheckbox(preserveAspectRatio);
      controls[2] = generateLineStyleControl(styles, stylesEnabled);
      controls[3] = generateLabelControls(enableLabels, everyNthLineLabeled);
      controls[4] = generateSigFigControls(enableFormatting, numSigFig);
   }
   
   public CompositeContourControl generateContourControls(
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
   
   public ControlCheckbox generateAspectRatioCheckbox(boolean selected)
   {
      /*
       * This is the control used to specify if the aspect ratio should 
       * be preserved.
       */
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
   
   public LineStyleControl generateLineStyleControl(int[] styles, 
                                                    boolean[] stylesEnabled)
   {
      lineStyleControl = new LineStyleControl(styles, stylesEnabled);
      return lineStyleControl;
   }
   
   public ControlCheckboxSpinner generateLabelControls(boolean enable, 
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
   
   public ControlCheckboxSpinner generateSigFigControls(boolean enable, 
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
// -----------=[ End methods used to generate the controls ]=-----------------//
   
   
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
      private final String STYLES_KEY = "Styles";
      
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
       *                     (i+1)th control for a line style should be 
       *                     enabled.  That is, the control for the first 
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
                                              isChecked(i, linesEnabled), 
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
      
      private boolean isChecked(int index, boolean[] enableLines)
      {
         if (index<0 || index>=enableLines.length)
            return false;
         
         if (index==0)
            return true;
         
         return enableLines[index];
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
         int[] styles = new int[styleBoxes.length];
         for (int i=0; i<styleBoxes.length; i++)
            styles[i] = getStyleForIndex(styleBoxes[i].getSelectedIndex());
         return styles;
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
              state.insert(STYLES_KEY, DEFAULT_STYLES);
           else
              state.insert(STYLES_KEY, (int[])getControlValue());
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
       *         <code>true</code> if the ith line on this control is 
       *         enabled.  If the value is <code>false</code> the line is not 
       *         enabled.
       */
      public boolean[] getEnabledLines()
      {
         boolean[] b = new boolean[styleBoxes.length];
         for (int i=0; i<b.length; i++)
            b[i] = styleBoxes[i].isChecked();
         return b;
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
      */
      
      return new VirtualArray2D(dataArray);
   }
   
   /**
    * Testbed.  Displays this view along with its controls.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      VirtualArray2D arr = getTestData(41,51,3.0,4.0);
      ContourViewComponent contour = new ContourViewComponent(arr);
      
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
