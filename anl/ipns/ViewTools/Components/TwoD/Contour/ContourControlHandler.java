/*
 * File: ContourControlHandler.java
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
 * Revision 1.5  2005/07/29 22:42:49  kramer
 * This class contains controls with checkboxes that enable/disable contour
 * line labels and label formating.  These checkboxes were not working but
 * are now fixed.
 *
 * Revision 1.4  2005/07/29 15:25:53  kramer
 *
 * Now this class stores the state of the PanViewControl in its ObjectState.
 *
 * Revision 1.3  2005/07/28 23:01:29  kramer
 *
 * Changed the reinit() method to invoke on the ContourJPanel the method
 * invalidateThumbnail() ( instead of invalidate() ).  This was a typo.
 *
 * Revision 1.2  2005/07/28 15:28:11  kramer
 *
 * -Added support for a PanViewControl (added a method to create the control
 *  and added a custom listener for the control).
 * -Modified all of the controls listeners so that when they are invoked, the
 *  listeners invoke the appropriate setter methods.
 * -Modified the setter methods to update the gui elements and invoke the
 *  correct *changed() method (i.e. colorChanged() or displayChanged() ....)
 *
 * Revision 1.1  2005/07/25 20:46:00  kramer
 *
 * Initial checkin.  This is a module of the ContourViewComponent that is
 * responsible for handling the various ViewControls on the component.
 *
 */
package gov.anl.ipns.ViewTools.Components.TwoD.Contour;

import gov.anl.ipns.Util.Messaging.Information.InformationCenter;
import gov.anl.ipns.Util.Messaging.Information.InformationHandler;
import gov.anl.ipns.Util.Messaging.Property.PropertyChangeConnector;
import gov.anl.ipns.Util.Sys.ColorSelector;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.ViewControls.ColorControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.CompositeContourControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckbox;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckboxCombobox;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckboxSpinner;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlColorScale;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlSlider;
import gov.anl.ipns.ViewTools.Components.ViewControls.PanViewControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.SpinnerControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.Contours;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

public class ContourControlHandler extends ContourChangeHandler 
                                      implements InformationHandler
{
//--------------------=[ InformationCenter keys ]=----------------------------//
   public static final String INTENSITY_INFO_KEY = 
                                 "Intensity info key";
   public static final String COLORSCALE_NAME_INFO_KEY = 
                                 "Colorscale name info key";
//------------------=[ End InformationCenter keys ]=--------------------------//

   
//------------------------=[ ObjectState keys ]=------------------------------//
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
   public static final String PAN_VIEW_CONTROL_KEY = "PanViewControl key";
//---------------------=[ End ObjectState keys ]=-----------------------------//
   
   
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
    * Specifies the default background color for this component.  The value 
    * of this field is the same as the value of the field 
    * {@link ContourJPanel#DEFAULT_BACKGROUND_COLOR 
    * ContourJPanel.DEFAULT_BACGROUND_COLOR}.
    */
   public static final Color DEFAULT_BACKGROUND_COLOR = 
      ContourJPanel.DEFAULT_BACKGROUND_COLOR;
   
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
   
   public static double DEFAULT_INTENSITY = 30;
   //-----------------=[ Defaults unique to this class ]=-------------------//
   
//------------------=[ End default field values ]=----------------------------//
   
   
//-----------------------------=[ Fields ]=-----------------------------------//
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
   private PanViewControl panControl;
   
   private ViewControl[] controls;
//---------------------------=[ End fields ]=---------------------------------//
   
   
//--------------------------=[ Constructors ]=--------------------------------//
   public ContourControlHandler(PropertyChangeConnector connector, 
                                InformationCenter center, 
                                ContourJPanel panel, boolean useManualLevels)
   {
      this(connector, center, panel, 
                     DEFAULT_LOWEST_CONTOUR, 
                     DEFAULT_HIGHEST_CONTOUR, 
                     DEFAULT_NUM_CONTOURS, 
                     DEFAULT_MANUAL_LEVELS, 
                     useManualLevels,
                     ContourMenuHandler.DEFAULT_PRESERVE_ASPECT_RATIO, 
                     DEFAULT_STYLES, 
                     DEFAULT_ENABLE_STYLE_ARR, 
                     DEFAULT_SHOW_LABEL, 
                     DEFAULT_LABEL_EVERY_N_LINES, 
                     DEFAULT_ENABLE_LABEL_FORMATTING,
                     DEFAULT_NUM_SIG_DIGS, 
                     DEFAULT_COLOR_SCALE, 
                     ContourMenuHandler.DEFAULT_IS_DOUBLE_SIDED, 
                     DEFAULT_BACKGROUND_COLOR, 
                     ContourMenuHandler.DEFAULT_LINE_COLOR);
   }
   
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
   public ContourControlHandler(PropertyChangeConnector connector, 
                                InformationCenter center, 
                                ContourJPanel panel, 
                                float minValue, float maxValue, int numLevels, 
                                float[] levels, boolean useManualLevels, 
                                boolean preserveAspectRatio, 
                                int[] styles, boolean[] stylesEnabled, 
                                boolean enableLabels, int everyNthLineLabeled, 
                                boolean enableFormatting, int numSigFig, 
                                String colorScale, boolean isDoubleSided, 
                                Color backgroundColor, Color contourColor)
   {
      super(connector, center, panel);
      
      //now to tell the info center which values this class knows about
      getInfoCenter().registerHandler(this, INTENSITY_INFO_KEY);
      getInfoCenter().registerHandler(this, COLORSCALE_NAME_INFO_KEY);
      
      //now to connect to the PropertyChangeConnector
      getPropertyConnector().addHandler(this);
      
      controls = new ViewControl[8];
      controls[0] = generateIntensityControls();
      controls[1] = generateColorScaleControls(colorScale, isDoubleSided);
      controls[2] = generateContourControls(minValue, maxValue, numLevels, 
                                                levels, useManualLevels);
      controls[3] = generateLineStyleControl(styles, stylesEnabled);
      controls[4] = generateLabelControls(enableLabels, everyNthLineLabeled);
      controls[5] = generateSigFigControls(enableFormatting, numSigFig);
      controls[6] = generateBackgroundControls(backgroundColor);
      controls[7] = generatePanViewControl();
      
      getContourPanel().addActionListener(new PanelListener());
   }
//------------------------=[ End constructors ]=------------------------------//
   
   
//--------=[ Methods implemented for the ContourChangeHandler class ]=--------//
   public void reinit(IVirtualArray2D v2d)
   {
      //if the data has changed, make sure that the thumbnail is invalidated
      //so that when it is requested, a new thumbnail based on the new data 
      //is returned (and not a cached copy based on the old data)
        getContourPanel().invalidateThumbnail();
//TODO Check if this is needed
//      this.panControl.validate();
//      this.panControl.repaint();
   }
   
   public void changeColorScaleName(String colorscale)
   {
      if (!IndexColorMaker.isValidColorScaleName(colorscale))
         return;
      
      boolean isDoubleSided = true;
      Boolean result = 
         (Boolean)getInfoCenter().
            obtainValue(ContourMenuHandler.IS_DOUBLE_SIDED_INFO_KEY);
      if (result!=null)
         isDoubleSided = result.booleanValue();
      
      //update the ControlColorScale's display
        this.controlColorscale.setColorScale(colorscale, isDoubleSided);
   }

   public void changeColorScaleLocation(String location)
   {
      if ( (location != null) && location.equals(CONTROL_PANEL_LOCATION) )
         controlColorscale.setVisible(true);
      else
         controlColorscale.setVisible(false);
   }

   public void changeIntensity(double intensity)
   {
      intensitySlider.setValue((float)intensity);
      this.controlColorscale.setLogScale(intensity);
   }
   
   public void changeIsDoubleSided(boolean isDoubleSided)
   {
      this.controlColorscale.setColorScale(getColorScale(), isDoubleSided);
   }
   
   public void changeDisplay()
   {
      if (this.panControl != null)
      {
         //when the display is updated the only ViewControl that needs to 
         //updated is the PanViewControl
         this.panControl.validate();
         this.panControl.repaint();
      }
   }
//------=[ End methods implemented for the ContourChangeHandler class ]=------//

   
//------=[ Methods implemented for the InformationHandler interface ]=--------//
   public Object getValue(String key)
   {
      if (key==null)
         return null;
      
      if (key.equals(INTENSITY_INFO_KEY))
         return new Double(getColorScaleIntensity());
      else if (key.equals(COLORSCALE_NAME_INFO_KEY))
         return getColorScale();
      else
         return null;
   }
//----=[ End methods implemented for the InformationHandler interface ]=------//
   
   
//-----------=[ Methods implemented for the IPreserveState interface ]=-------//
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      //    set the states for the ViewControls
      Object val = state.get(INTENSITY_SLIDER_KEY);
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
      
      val = state.get(PAN_VIEW_CONTROL_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         panControl.setObjectState((ObjectState)val);
   }

   public ObjectState getObjectState(boolean is_default)
   {
      ObjectState state = new ObjectState();
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
      state.insert(PAN_VIEW_CONTROL_KEY, 
                   panControl.getObjectState(is_default));
      return state;
   }
//---------=[ End methods implemented for the IPreserveState interface ]=-----//

  
//--------------=[ Getter/setter methods for the controls ]=------------------//
   public ViewControl[] getControls()
   {
      return controls;
   }
   
   public String getColorScale()
   {
      return controlColorscale.getColorScale();
   }
   
   public void setColorScale(Color color)
   {
      changeColor(color);
      colorChanged(color);
   }
   
   public void setColorScale(String colorscale, boolean isDoubleSided)
   {
      changeColorScaleName(colorscale);
      colorScaleNameChanged(colorscale);
      
      changeIsDoubleSided(isDoubleSided);
      isDoubleSidedChanged(isDoubleSided);
   }
   
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
      getContourPanel().setLineStyles(styles);
      displayChanged();
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
      
      if (!enable)
         getContourPanel().setShowAllLabels(false);
      else
         getContourPanel().setShowLabelEvery(getLabelEvery());
      
      displayChanged();
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
      
      if (nthLine < 0)
         setIsLabelEnabled(false);
      else
      {
         labelControl.setSpinnerValue(new Integer(nthLine));
         getContourPanel().setShowLabelEvery(nthLine);
         displayChanged();
      }
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
      
      int numSigFigs = -1;
      if (enable)
         numSigFigs = getNumSigFigs();
         
      getContourPanel().setNumSigDigits(numSigFigs);
      displayChanged();
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
      
      if (num < 0)
         setIsLabelFormattingEnabled(false);
      else
      {
         sigFigControl.setSpinnerValue(new Integer(num));
         getContourPanel().setNumSigDigits(num);
         displayChanged();
      }
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
      getContourPanel().setBackgroundColor(color);
      displayChanged();
   }
   
   public void setIntensity(double intensity)
   {
      changeIntensity(intensity);
      intensityChanged(intensity);
      displayChanged();
   }
   
   public double getColorScaleIntensity()
   {
      if (intensitySlider==null)
         return DEFAULT_INTENSITY;
      
      return intensitySlider.getValue();
   }
//------------=[ End getter/setter methods for the controls ]=----------------//
   
   
//--------------=[ Methods used to generate the controls ]=-------------------//
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
               setBackgroundColor(backgroundControl.getSelectedColor());
            }
         }
      });
      backgroundControl.send_message(ColorControl.COLOR_CHANGED);
      
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
            setIntensity(intensitySlider.getValue());
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
      colorScaleNameChanged(scale);
      isDoubleSidedChanged(isDoubleSided);
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
         new CompositeContourControl(getContourPanel(), 
                                     minValue, maxValue, numLevels, 
                                     levels, 
                                     false);
      contourControl.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            if (event.getActionCommand().
                  equals(CompositeContourControl.CONTOURS_CHANGED))
               displayChanged();
         }
      });
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
                  setLabelEvery( ( (Integer)labelControl.getSpinnerValue() ).
                                   intValue() );
               else
                  setLabelEvery(-1);
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
                    setNumSigFigs(((Integer)sigFigControl.getSpinnerValue()).
                                  intValue());
                 else
                    setNumSigFigs(-1);
              }
           }
        });
      sigFigControl.send_message(ControlCheckbox.CHECKBOX_CHANGED);
      
      return sigFigControl;
   }
   
   private PanViewControl generatePanViewControl()
   {
      this.panControl = new PanViewControl(getContourPanel());
      this.panControl.setTitle("Panning tool");
      this.panControl.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            String message = event.getActionCommand();
            if (message.equals(PanViewControl.BOUNDS_CHANGED) || 
                  message.equals(PanViewControl.BOUNDS_MOVED) || 
                     message.equals(PanViewControl.BOUNDS_RESIZED))
            {
               getContourPanel().
                  setLocalWorldCoords(panControl.getLocalBounds());
               displayChanged();
            }
         }
      });
      
      return this.panControl;
   }
//--------------=[ End methods used to generate the controls ]=---------------//

   
//--------------------------=[ Listeners ]=-----------------------------------//
   private class PanelListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         String message = event.getActionCommand();
         if (message.equals(ContourJPanel.ZOOM_IN) || 
               message.equals(ContourJPanel.RESET_ZOOM))
         {
            ContourJPanel contourPanel = getContourPanel();
            panControl.setGlobalBounds(contourPanel.getGlobalWorldCoords());
            panControl.setLocalBounds(contourPanel.getLocalWorldCoords());
            //panControl.repaint();
         }
      }
   }
//------------------------=[ End listeners ]=---------------------------------//
   
   
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
       *               is too short, 
       *               {@link ContourControlHandler#DEFAULT_STYLES 
       *               DEFAULT_STYLES} is used.  If it is too long, 
       *               the extra elements are ignored.
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
       *                     ContourControlHandler#DEFAULT_ENABLE_STYLE_ARR 
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
               
            setLineStyles(currentStyles);
         }
      }
   }
//--------------------=[ End custom controls ]=-------------------------------//
}
