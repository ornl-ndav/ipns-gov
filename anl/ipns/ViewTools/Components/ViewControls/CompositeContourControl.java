/*
 * File: CompositeContourControl.java
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
 * Revision 1.4  2005/06/17 19:48:53  kramer
 * Removed the unused import to ContourLevelMetaData (which I never checked
 * into cvs because it was not needed).
 *
 * Revision 1.3  2005/06/17 19:28:29  kramer
 *
 * Added an inner class 'ContourLevelControl' that would contain controls
 * to specify the line type, number of significant digits for, and if labels
 * are drawn on a particular contour level.  Currently, this class is
 * commented out but it might be needed at some time.
 *
 * Revision 1.2  2005/06/16 13:57:03  kramer
 *
 * Modified the getControlValue() method to use the methods
 * getEnteredUniformContours() and getEnteredNonUniformContours().
 *
 * Revision 1.1  2005/06/15 16:33:45  kramer
 *
 * This control contains controls for manually entering contour levels or
 * for entering the min, max, and number of contour levels to specify the
 * conditions used to determine uniformly spaced contour levels.
 *
 * This ViewControl serves as a wrapper to a TabbedViewControl.  This
 * control's controls are placed on this tabbed pane.  Furthermore, this
 * control handles redirecting any control values given to it to the
 * right control in the tabbed pane.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.StringFilter.FloatFilter;
import gov.anl.ipns.Util.StringFilter.IntegerFilter;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.TwoD.ContourViewComponent;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.Contours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.MixedContours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.NonUniformContours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.UniformContours;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import DataSetTools.util.SharedData;

/**
 * 
 */
public class CompositeContourControl extends ViewControl
{
   //------------------------=[ ObjectState keys ]=----------------------------
   public static final String UNIFORM_CONTROL_STATE_KEY = 
                                                   "Uniform control state";
   public static final String NONUNIFORM_CONTROL_STATE_KEY = 
                                                   "Nonuniform control state";
   public static final String REDRAW_BUTON_STATE_KEY = 
                                                   "Redraw button state";
   
   //-----------------=[ Default ObjectState values ]=-------------------------
   private final ObjectState DEFAULT_UNIFORM_STATE = 
                                generateUniformControls(
                                   ContourViewComponent.DEFAULT_LOWEST_CONTOUR,
                                   ContourViewComponent.DEFAULT_HIGHEST_CONTOUR,
                                   ContourViewComponent.DEFAULT_NUM_CONTOURS).
                                      getObjectState(false);
   
   private final ObjectState DEFAULT_NONUNIFORM_STATE = 
                                generateManualControls(
                                  ContourViewComponent.DEFAULT_MANUAL_LEVELS).
                                    getObjectState(false);
   
   private final ObjectState DEFAULT_BUTTON_STATE = 
                                generateRedrawBothButton().
                                  getObjectState(false);
   
   private TabbedViewControl tabControl;
   private ButtonControl redrawBothButton;
   private ContourJPanel contourPanel;
   
   private CompositeContourControl(String con_title, ContourJPanel panel)
   {
      this(con_title, 
           panel, 
           ContourViewComponent.DEFAULT_LOWEST_CONTOUR,
           ContourViewComponent.DEFAULT_HIGHEST_CONTOUR,
           ContourViewComponent.DEFAULT_NUM_CONTOURS, 
           ContourViewComponent.DEFAULT_MANUAL_LEVELS, 
           true);
   }
   
   public CompositeContourControl(String con_title, 
                                  ContourJPanel panel, 
                                  float minValue, float maxValue, int numLevels,
                                  float[] levels, boolean useManualLevels)
   {
      super(con_title);
      
      //set the ContourJPanel where the contours are drawn
      this.contourPanel = panel;
      
      //first to construct all of the ViewControls used in this ViewControl
      FieldEntryControl uniformControls = generateUniformControls(minValue, 
                                                                  maxValue, 
                                                                  numLevels);
      ControlList manualControls = generateManualControls(levels);

      Vector viewControlVec = new Vector();
        viewControlVec.add(uniformControls);
        viewControlVec.add(manualControls);
      
      tabControl = new TabbedViewControl("", viewControlVec);
      if (useManualLevels)
         tabControl.setSelectedTab(manualControls);
      
      redrawBothButton = generateRedrawBothButton();
      
      //now to add the components to this object (which is a panel)
      setLayout(new BorderLayout());
      add(tabControl, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
          bottomPanel.add(new JLabel("Use both contour specifications"));
          bottomPanel.add(redrawBothButton);
      add(bottomPanel, BorderLayout.SOUTH);
   }
   
   public CompositeContourControl(ContourJPanel panel, 
                                  float minValue, float maxValue, int numLevels,
                                  float[] levels, boolean useManualLevels)
   {
      this("", panel, minValue, maxValue, numLevels, levels, useManualLevels);
   }

   public void setControlValue(Object value)
   {
      if (value instanceof UniformContours)
      {
         UniformContours contours = (UniformContours)value;
         float min = contours.getLowestLevel();
         float max = contours.getHighestLevel();
         int numLevels = (int)contours.getNumLevels();
         
         FieldEntryControl controls = getUniformControls();
           controls.setValue(0, min);
           controls.setValue(1, max);
           controls.setValue(2, numLevels);
      }
      else if (value instanceof NonUniformContours)
      {
         NonUniformContours contours = (NonUniformContours)value;
         ControlList levelList = getNonUniformControls();
         
         float[] rawLevels = contours.getLevels();
         Object[] strLevels = new Object[rawLevels.length];
         for (int i=0; i<strLevels.length; i++)
            strLevels[i] = ""+rawLevels[i];
         
         levelList.setControlValue(strLevels);
      }
   }

   public Object getControlValue()
   {
      //get the ViewControl that is on the currently selected tab
      ViewControl selControl = tabControl.getSelectedViewControl();
      if (selControl==null)
         return null;
      
      String[] errMsg = new String[1];
      Contours contours = null;
      if (selControl instanceof FieldEntryControl)
         contours = getEnteredUniformContours(errMsg);
      else if (selControl instanceof ControlList)
         contours = getEnteredNonUniformContours(errMsg);
      
      if (errMsg[0]!=null)
         SharedData.addmsg(errMsg[0]);
      return contours;
   }
   
   public ObjectState getObjectState(boolean isDefault)
   {
      ObjectState state = super.getObjectState(isDefault);
      
      if (isDefault)
      {
         state.insert(UNIFORM_CONTROL_STATE_KEY, DEFAULT_UNIFORM_STATE);
         state.insert(NONUNIFORM_CONTROL_STATE_KEY, DEFAULT_NONUNIFORM_STATE);
         state.insert(REDRAW_BUTON_STATE_KEY, DEFAULT_BUTTON_STATE);
      }
      else
      {
         ObjectState tabState = tabControl.getObjectState(isDefault);
         state.insert(UNIFORM_CONTROL_STATE_KEY, 
                         tabState.get(
                            TabbedViewControl.VIEW_CONTROL_STATE_AT+0));
         state.insert(NONUNIFORM_CONTROL_STATE_KEY, 
                         tabState.get(
                            TabbedViewControl.VIEW_CONTROL_STATE_AT+1));
         state.insert(REDRAW_BUTON_STATE_KEY, 
                         redrawBothButton.getObjectState(isDefault));
      }
      
      return state;
   }
   
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      //first to set the ObjectState of the ViewControls in the tabbed pane
         //this will hold the state information for the ViewControls in the 
         //tabbed pane
         ObjectState tabState = new ObjectState();
      
         Object val = state.get(UNIFORM_CONTROL_STATE_KEY);
         if (val!=null)
            tabState.insert(TabbedViewControl.VIEW_CONTROL_STATE_AT+0, 
                            (ObjectState)val);
      
         val = state.get(NONUNIFORM_CONTROL_STATE_KEY);
         if (val!=null)
            tabState.insert(TabbedViewControl.VIEW_CONTROL_STATE_AT+1, 
                            (ObjectState)val);
        
         //now to actually set the ObjectState of the tabbed pane's controls
         tabControl.setObjectState(tabState);
      
      //now to set the state of the 'redraw' button
      val = state.get(REDRAW_BUTON_STATE_KEY);
      if (val!=null)
         redrawBothButton.setObjectState((ObjectState)val);
   }

   public ViewControl copy()
   {
      CompositeContourControl copy = new CompositeContourControl(getTitle(),
                                                                 contourPanel);
      copy.setObjectState(this.getObjectState(false));
      return copy;
   }

   public static void main(String[] args)
   {
   }
   
   //---------------=[ Private methods ]=------------------------------------
   
   //-----=[ Used to access the controls embedded in the tabbed pane ]=-------
   private FieldEntryControl getUniformControls()
   {
      return (FieldEntryControl)tabControl.getViewControlAt(0);
   }
   
   private ControlList getNonUniformControls()
   {
      return (ControlList)tabControl.getViewControlAt(1);
   }
   
   //---------=[ Used to generate the graphical controls ]=--------------------
   private FieldEntryControl generateUniformControls(float minValue,
                                                     float maxValue,
                                                     int numLevels)
   {
      /*
       * These are the controls used to enter a range for the contour levels
       * The min, max, and number of contours are entered and uniformly 
       * spaced contours are calculated
       */
      String[] minMaxNames = 
         new String[]{"Minimum", "Maximum", "Number of Levels"};
      float[] minMaxValues = {minValue, maxValue, numLevels};
      FieldEntryControl uniformControls = 
         new FieldEntryControl(minMaxNames, minMaxValues);
      //set the title (this title will show up in the tabbed pane 
      //               that this control will be added to)
      uniformControls.setTitle("Uniform Contours");
      //set the last value again because its really an int
      uniformControls.setValue(2,numLevels);
      uniformControls.setButtonText("Redraw");
      //enable filtering so that the user can only enter valid data types
      uniformControls.enableFilter(new FloatFilter(),0);
      uniformControls.enableFilter(new FloatFilter(),1);
      uniformControls.enableFilter(new IntegerFilter(),2);
      //add the action listener for this control
      uniformControls.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            reloadUniformContourControls();
         }
      });

      return uniformControls;
   }
   
   private ControlList generateManualControls(float[] levels)
   {
      /*
       * These are the controls used to manually enter the contour levels.
       */
      ControlList manualControls = 
         new ControlList("Manually Enter Contours",
               new FloatFilter());
      //possibly set the initial values in the list
      if (levels!=null)
      {
         Vector vec = new Vector(levels.length);
         for (int i=0; i<levels.length; i++)
            vec.add(""+levels[i]);
         manualControls.setControlValue(vec);
      }
      //set the title (this title will show up in the tabbed pane 
      //               that this control will be added to)
      manualControls.setTitle("Manual Contours");
      manualControls.setSubmitButtonText("Redraw");
      //add an action listener to this control
      manualControls.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            if (event.getActionCommand().equals(ControlList.SUBMIT_PRESSED))
               reloadNonUniformContourControls();
         }
      });

      return manualControls;
   }
   
   public ButtonControl generateRedrawBothButton()
   {
      ButtonControl control = new ButtonControl("Redraw");
       control.setBorderVisible(false);
       control.addActionListener(new ActionListener()
       {
          public void actionPerformed(ActionEvent event)
          {
             reloadAllContourControls();
          }
       });
      return control;
   }
   
   //-----=[ Used to get the information entered in the GUI ]=-----------------
   private UniformContours getEnteredUniformContours(String[] errMsg)
   {
      if (errMsg==null)
         System.err.println("Warning:  ContourViewComponent#" +
                            "TabbedContourControl." +
                            "getEnteredUniformContours() was given " +
                            "a null error message array.  Error messages " +
                            "will not be able to be generated.");
      else if (errMsg.length < 1)
         System.err.println("Warning:  ContourViewComponent#"+
                            "TabbedContourControl." +
                            "getEnteredUniformContours() was given " +
                            "an error message array with an invalid " +
                            "length (i.e. less than 1).");
      
      FieldEntryControl uniformControls = getUniformControls();
      float min = uniformControls.getFloatValue(0);
      float max = uniformControls.getFloatValue(1);
      int numLevels = (int)uniformControls.getFloatValue(2);
      
      UniformContours contours = null;
      try
      {
         contours = new UniformContours(min, max, numLevels);
         if (errMsg!=null && errMsg.length>0)
            errMsg[0] = null;
      }
      catch (IllegalArgumentException e)
      {
         contours = null;
         if (errMsg!=null && errMsg.length>0)
            errMsg[0] = e.getMessage();
      }
      return contours;
   }
   
   private float[] getEnteredLevels()
   {
      Object[] obArr = (Object[])getNonUniformControls().getControlValue();
      float[] floatArr = new float[obArr.length];
      int numValid = 0;
      for (int i=0; i<obArr.length; i++)
      {
         try
         {
            floatArr[numValid++] = Float.parseFloat(obArr[i].toString());
         }
         catch (NumberFormatException e)
         {
            SharedData.addmsg("warning:  The contour level "+
                               obArr[i].toString()+" is not a proper " +
                               "floating point number and will be " +
                               "ignored");
         }
      }
      
      float[] validArr;
      if (numValid<obArr.length)
      {
         validArr = new float[numValid];
         System.arraycopy(floatArr,0,validArr,0,numValid);
      }
      else
         validArr = floatArr;
      
      if (validArr.length==0)
         return null;
      else
         return validArr;
   }
   
   private NonUniformContours getEnteredNonUniformContours(String[] errMsg)
   {
      if (errMsg==null)
         System.err.println("Warning:  ContourViewComponent#" +
                            "TabbedContourControl." +
                            "getEnteredNonUniformContours() was given " +
                            "a null error message array.  Error messages " +
                            "will not be able to be generated.");
      else if (errMsg.length < 1)
         System.err.println("Warning:  ContourViewComponent#"+
                            "TabbedContourControl." +
                            "getEnteredNonUniformContours() was given " +
                            "an error message array with an invalid " +
                            "length (i.e. less than 1).");
         
      float[] levels = getEnteredLevels();
      
      NonUniformContours contours = null;
      try
      {
         contours = new NonUniformContours(levels);
         if (errMsg!=null && errMsg.length>=0)
            errMsg[0] = null;
      }
      catch (IllegalArgumentException e)
      {
         contours = null;
         if (errMsg!=null && errMsg.length>=0)
            errMsg[0] = e.getMessage();
      }
      return contours;
   }
   
   //------------=[ Used to update the contour display ]=--------------------
   private void reloadUniformContourControls()
   {
      String[] errMsg = new String[1];
      UniformContours contours = getEnteredUniformContours(errMsg);
      if (contours!=null)
         contourPanel.setContours(contours);
      else
      {
         if (errMsg[0]!=null)
            SharedData.addmsg(errMsg[0]);
      }
   }
   
   private void reloadNonUniformContourControls()
   {
      String[] errMsg = new String[1];
      NonUniformContours contours = getEnteredNonUniformContours(errMsg);
      if (contours!=null)
         contourPanel.setContours(contours);
      else
      {
         if (errMsg[0]!=null)
            SharedData.addmsg(errMsg[0]);
      }
   }
   
   private void reloadAllContourControls()
   {
      String[] uniformErrMsg = new String[1];
      UniformContours uniformControls = 
         getEnteredUniformContours(uniformErrMsg);
      
      String[] nonuniformErrMsg = new String[2];
      NonUniformContours nonuniformControls = 
         getEnteredNonUniformContours(nonuniformErrMsg);
      
      if (uniformControls==null && uniformErrMsg[0]!=null)
            SharedData.addmsg(uniformErrMsg[0]);
      
      if (nonuniformControls==null && 
            !getNonUniformControls().isEmpty() && 
               nonuniformErrMsg[0]!=null)
            SharedData.addmsg(nonuniformErrMsg[0]);
         
      contourPanel.setContours(new MixedContours(uniformControls,
                                                 nonuniformControls));
   }
   
   /*
   private class ContourLevelControl extends ViewControl
   {
      private LabelCombobox styleBox;
      private ControlCheckbox showLabelCheckBox;
      private FieldEntryControl roundedToField;
      
      public ContourLevelControl(String title)
      {
         super(title);
         
         
         String[] styles = new String[4];
          styles[getIndexForStyle(ContourLevelMetaData.SOLID)] = 
             "Solid";
          styles[getIndexForStyle(ContourLevelMetaData.DASHED)] = 
             "Dashed";
          styles[getIndexForStyle(ContourLevelMetaData.DASHED_DOTTED)] = 
             "Dashed-Dotted";
          styles[getIndexForStyle(ContourLevelMetaData.DOTTED)] = 
             "Dotted";
         
         styleBox = new LabelCombobox("Style", styles);
           styleBox.setSelectedIndex(ContourLevelMetaData.DEFAULT_LINE_STYLE);
         
         showLabelCheckBox = 
            new ControlCheckbox(ContourLevelMetaData.DEFAULT_SHOW_LABEL);
         
         roundedToField = 
            new FieldEntryControl(new String[]{"# Significant digits"}, 
                                  new int[]{});
           roundedToField.enableFilter(new IntegerFilter(), 0);
           
         setLayout(new FlowLayout(FlowLayout.CENTER));
         add(styleBox);
         add(new JLabel("Show label"));
         add(roundedToField);
      }
      
      private int getIndexForStyle(int style)
      {
         switch (style)
         {
            case ContourLevelMetaData.SOLID:
               return 0;
            case ContourLevelMetaData.DASHED:
               return 1;
            case ContourLevelMetaData.DASHED_DOTTED:
               return 2;
            case ContourLevelMetaData.DOTTED:
               return 3;
            default:
               return -1;
         }
      }
      
      private int getStyleForIndex(int index)
      {
         switch (index)
         {
            case 0:
               return ContourLevelMetaData.SOLID;
            case 1:
               return ContourLevelMetaData.DASHED;
            case 2:
               return ContourLevelMetaData.DASHED_DOTTED;
            case 3:
               return ContourLevelMetaData.DOTTED;
            default:
               return -1;
         }
      }
      
      public void setControlValue(Object value)
      {
         if ( value==null )
            return;
         
         if ( !(value instanceof ContourLevelMetaData) )
            return;
         
         ContourLevelMetaData metaData = (ContourLevelMetaData)value;
           styleBox.setSelectedIndex(getIndexForStyle(metaData.getLineStyle()));
           showLabelCheckBox.setSelected(metaData.getShowLabel());
           roundedToField.setValue(0, metaData.getNumSigDigits());
      }

      public Object getControlValue()
      {
         return new ContourLevelMetaData(
                       getStyleForIndex(styleBox.getSelectedIndex()), 
                       showLabelCheckBox.isSelected(), 
                       (int)roundedToField.getFloatValue(0));
      }

      public ViewControl copy()
      {
         return null;
      }
      
   }
   */
}
