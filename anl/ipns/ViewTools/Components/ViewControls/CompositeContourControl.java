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
 * Revision 1.5  2005/06/22 22:19:50  kramer
 * Added javadocs, removed unused code, and modified the default values
 * used so that they more evenly link with the defaults specified in the
 * ContourViewComponent and ContourJPanel classes.  Also, when the user
 * adds contour levels to the list, the list is sorted.
 *
 * Revision 1.4  2005/06/17 19:48:53  kramer
 *
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
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.Contours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.MixedContours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.NonUniformContours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.UniformContours;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import DataSetTools.util.SharedData;

/**
 * This ViewControl was designed specifically for the 
 * {@link gov.anl.ipns.ViewTools.Components.TwoD.ContourViewComponent 
 * ContourViewComponent}.  It ha a control for specifying the data used to 
 * calculate uniformly spaced contor levels to draw, and a control used to 
 * specify manually entered contour levels to draw.  The controls are placed 
 * in a tabbed pane.
 */
public class CompositeContourControl extends ViewControl
{
   //------------------------=[ ObjectState keys ]=----------------------------
   /**
    * "Uniform contour state" - This static constant String is used to 
    * reference the state information for the controls used to enter the 
    * uniformly entered contour levels.  The value that this key 
    * references is an <code>ObjectState</code> of a 
    * <code>FieldEntryControl</code>.
    */
   public static final String UNIFORM_CONTROL_STATE_KEY = 
                                                   "Uniform contour state";
   /**
    * "Nonuniform contour state" - This static constant String is used to 
    * reference the state information for the controls used to enter the 
    * non uniformly entered contour levels.  The value that this key 
    * refeences is an <code>ObjectState</code> of a 
    * <code>ControlList</code>.
    */
   public static final String NONUNIFORM_CONTROL_STATE_KEY = 
                                                   "Nonuniform contour state";
   /**
    * "Redraw button state" - This static constant String is used to 
    * reference the state information for the button used to redraw the 
    * contour plot using both the uinform and manually entered contour 
    * levels.  The value that this key references is an 
    * <code>ObjectState</code> of a <code>ButtonControl</code> object.
    */
   public static final String REDRAW_BUTON_STATE_KEY = 
                                                   "Redraw button state";
   
   //----------------------=[ Default values ]=-------------------------------
   /**
    * The default lowest contour level used in the controls for the 
    * uniformly spaced contour levels.
    */
   public static final float   DEFAULT_LOWEST_CONTOUR = 0;
   /**
    * The default highest contour level used in the controls for the 
    * uniformly spaced contour levels.
    */
   public static final float   DEFAULT_HIGHEST_CONTOUR = 10;
   /**
    * The default number of contour levels to draw used in the controls 
    * for the uniformly spaced contour levels.
    */
   public static final int     DEFAULT_NUM_CONTOURS = 11;
   /**
    * The default manually entered contour levels usded in the controls 
    * for the non uniforly spaced contour levels.
    */
   public static final float[] DEFAULT_MANUAL_LEVELS = new float[0];
   
   //-----------------=[ Default ObjectState values ]=-------------------------
   /**
    * The 'default' state of the control used to enter the information 
    * used to calculate the uniform contour levels to draw.
    */
   private final ObjectState DEFAULT_UNIFORM_STATE = 
                                generateUniformControls(
                                   DEFAULT_LOWEST_CONTOUR,
                                   DEFAULT_HIGHEST_CONTOUR,
                                   DEFAULT_NUM_CONTOURS).
                                      getObjectState(false);
   /**
    * The 'default' state of the control used to enter the non uniform 
    * contour levels.
    */
   private final ObjectState DEFAULT_NONUNIFORM_STATE = 
                                generateManualControls(
                                  DEFAULT_MANUAL_LEVELS).
                                    getObjectState(false);
   /**
    * The 'default' state of the button used to redraw the contour plot 
    * using both the values specified for the uniform and non uniform 
    * contour levels.
    */
   private final ObjectState DEFAULT_BUTTON_STATE = 
                                generateRedrawBothButton().
                                  getObjectState(false);
   
   /**
    * Used to compare Objects that can be converted into floats.  The 
    * Objects in the list of manual contour levels are floats and this 
    * is used when sort the list.
    */
   private static final Comparator LIST_COMPARATOR = new Comparator()
   {
      /**
       * Used to convert the two Objects given into floats.
       * 
       * @param o1 The first object.
       * @param o2 The second object.
       * @return A float array holding the float values.  The first 
       *         element of the array holds the float value for 
       *         <code>o1</code> and the second element holds the 
       *         float value for <code>o2</code>.  If either of the 
       *         Objects cannot be converted into a float, a message 
       *         is sent to <code>SharedData</code> and 
       *         <code>Float.NaN</code> is placed in its corresponding 
       *         position in the array returned.
       */
      private float[] getFloats(Object o1, Object o2)
      {
         float[] fArr = new float[2];
         for (int i=0; i<fArr.length; i++)
         {
            //first initialize the value to an error value 
            //to anticipate if something goes wrong
            fArr[i] = Float.NaN;
            
            try
            {
               if (i==0)
                  fArr[i] = Float.parseFloat(o1.toString());
               else
                  fArr[i] = Float.parseFloat(o2.toString());
            }
            catch (Exception e)
            {
               SharedData.addmsg("Warning:  A non floating-point numbers was " +
                              "found in the list of contour levels to " +
                              "display");
            }
         }
         return fArr;
      }
      
      /**
       * Used to compare the two Objects given if and only if they can 
       * be converted to floats.
       * 
       * @param o1 The first object to compare.
       * @param o2 The second object to compare.
       * @return <ul>
       *           <li>0 iff o1==o2</li>
       *           <li>-1 iff o1<o2</li>
       *           <li>1 iff o1>o2</li>
       *         </ul>
       */
      public int compare(Object o1, Object o2)
      {
         float[] fArr = getFloats(o1, o2);
         if (fArr[0]<fArr[1])
            return -1;
         else if (fArr[0]>fArr[1])
            return 1;
         else
            return 0;
      }
   };
   
   //------------=[ Objects used in constructing this control ]=--------------
   /**
    * The control that displays a list of controls on a tabbed pane.  
    * The controls used in this control are placed in tabs of this 
    * tabbed pane.
    */
   private TabbedViewControl tabControl;
   /** The button that, when pressed, causes the display to be redrawn. */
   private ButtonControl redrawBothButton;
   /**
    * The panel used to display the contour plot of data.  This control 
    * is used to control which contour levels are plotted.
    */
   private ContourJPanel contourPanel;
   
   /**
    * Constructs this control with the specified title such that the 
    * given <code>ContourJPanel</code> is used to draw the contour plot.  
    * All default values are used when populating the current values of the 
    * controls.
    * 
    * @param con_title This control's title.
    * @param panel The panel used to draw the contour plot.
    */
   private CompositeContourControl(String con_title, ContourJPanel panel)
   {
      this(con_title, 
           panel, 
           DEFAULT_LOWEST_CONTOUR,
           DEFAULT_HIGHEST_CONTOUR,
           DEFAULT_NUM_CONTOURS, 
           DEFAULT_MANUAL_LEVELS, 
           true);
   }
   
   /**
    * Constructs this control with the specifid title such that the 
    * given <code>ContourJPanel</code> is used to draw the contour 
    * plot.  The values of the controls are initialized with the 
    * values given.
    * 
    * @param con_title This control's title.
    * @param panel The panel used to draw the contour plot.
    * @param minValue Used in the controls used to specify the uniform 
    *                 contours.  This is the value of the lowest 
    *                 contour level.
    * @param maxValue Used in the controls used to specify the uniform 
    *                 contours.  This is the value of the highest 
    *                 contour level.
    * @param numLevels Used in the controls used to specify the uniform 
    *                  contours.  This specifies the number of contour 
    *                  levels to draw.
    * @param levels Used in the controls used to specify the manually 
    *               entered contour levels.  This specifies the 
    *               manual contour levels to display.
    * @param useManualLevels If true, the controls used to specify the 
    *                        manual contours is given focus.  
    *                        If false, the controls used to specify the 
    *                        uniformly spaced contour levels is given 
    *                        focus.
    */
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
   
   /**
    * Constructs this control with a blank title such that the 
    * given <code>ContourJPanel</code> is used to draw the contour 
    * plot.  The values of the controls are initialized with the 
    * values given.
    * 
    * @param panel The panel used to draw the contour plot.
    * @param minValue Used in the controls used to specify the uniform 
    *                 contours.  This is the value of the lowest 
    *                 contour level.
    * @param maxValue Used in the controls used to specify the uniform 
    *                 contours.  This is the value of the highest 
    *                 contour level.
    * @param numLevels Used in the controls used to specify the uniform 
    *                  contours.  This specifies the number of contour 
    *                  levels to draw.
    * @param levels Used in the controls used to specify the manually 
    *               entered contour levels.  This specifies the 
    *               manual contour levels to display.
    * @param useManualLevels If true, the controls used to specify the 
    *                        manual contours is given focus.  
    *                        If false, the controls used to specify the 
    *                        uniformly spaced contour levels is given 
    *                        focus.
    */
   public CompositeContourControl(ContourJPanel panel, 
                                  float minValue, float maxValue, int numLevels,
                                  float[] levels, boolean useManualLevels)
   {
      this("", panel, minValue, maxValue, numLevels, levels, useManualLevels);
   }

   /**
    * Used to set the values displayed in one of the subcontrols on this 
    * control.
    * 
    * @param value A <code>Contours</code> object.
    *              <ul>
    *                <li>
    *                  If this value is a <code>UniformContours</code> 
    *                  object, the values displayed in the control that 
    *                  is used to specify the uniform contour levels 
    *                  will be changed.
    *                </li>
    *                  If this value is a <code>NonUniformContours</code> 
    *                  object, the values displayed in the control that 
    *                  is used to specify the nonuniform contour levels 
    *                  will be changed.
    *                <li>
    *                  If the value is not of either of these types or 
    *                  if it is <code>null</code>, nothing is done.
    *                </li>
    *              </ul>
    */
   public void setControlValue(Object value)
   {
      if (value==null)
         return;
      
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

   /**
    * Used to get the values listed in the controls that are 
    * currently visible.
    * 
    * @return The values listed in the controls that have focus.  If 
    *         none of the controls are currently visible, or if the 
    *         data entered in one of the controls is invalid 
    *         <code>null</code> is returned.  Otherwise, a 
    *         <code>UniformContours</code> or 
    *         <code>NonUniformContours</code> object is returned to 
    *         encapsulate the data entered in the controls for the 
    *         uniform or nonuniform contour levels respectively.
    */
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
   
   /**
    * Used to get the state of this control.  This ViewControl is composed 
    * of three sub ViewControls (a control for entering the uniform 
    * contour levels, a control for entering the nonuniform contour levels, 
    * and a control for the button used to redraw the contour plot).  
    * The ObjectStates for all three of these controls are encapsulated 
    * in the ObjectState returned from this method.
    * 
    * @param isDefault If true the default state for this control 
    *                  is returned.  Otherwise, the current state 
    *                  is returned.
    */
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
   
   /**
    * Used to set the state of this ViewControl.  This ViewControl is composed 
    * of three sub ViewControls (a control for entering the uniform 
    * contour levels, a control for entering the nonuniform contour levels, 
    * and a control for the button used to redraw the contour plot).  The 
    * ObjectState given to this method should contain the ObjectStates 
    * of each of these sub ViewControls.
    * 
    * @param state The state of this ViewControl.
    */
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

   /**
    * Used to get an exact copy of this ViewControl.
    * 
    * @return A deep copy of this ViewControl.
    */
   public ViewControl copy()
   {
      CompositeContourControl copy = new CompositeContourControl(getTitle(),
                                                                 contourPanel);
      copy.setObjectState(this.getObjectState(false));
      return copy;
   }
   
   //---------------=[ Private methods ]=------------------------------------
   
   //-----=[ Used to access the controls embedded in the tabbed pane ]=-------
   /**
    * Used to get the controls, that are embedded inside the tabbed pane 
    * on this control, that are used to enter the values used to calculate 
    * the uniform contours to draw.
    * 
    * @return The controls used to specify the uniform contours to draw.
    */
   private FieldEntryControl getUniformControls()
   {
      return (FieldEntryControl)tabControl.getViewControlAt(0);
   }
   
   /**
    * Used to get the controls, that are embedded inside the tabbed pane 
    * on this control, that are used to specify the non uniform contours 
    * to draw.
    * 
    * @return The controls that are used to specify the non uniform contours 
    *         to draw.
    */
   private ControlList getNonUniformControls()
   {
      return (ControlList)tabControl.getViewControlAt(1);
   }
   
   //---------=[ Used to generate the graphical controls ]=--------------------
   /**
    * Used to generate a ViewControl that can be used to specify the values 
    * used to draw some uniform contours.
    * 
    * @param minValue The initial value displayed in the control that 
    *                 specifies the value of the lowest contour level.
    * @param maxValue The initial value displayed in the control that 
    *                 specifies the value of the highest contour level.
    * @param numLevels The initial value displayed in the control that 
    *                  specifies the number of contour levels to draw.
    * @return A ViewControl that can be used to enter information about 
    *         the uniform contour levels to draw.
    */
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
   
   /**
    * Used to generate a ViewControl that can be used to specify the 
    * non uniform contour levels to draw.
    * 
    * @param levels The initial values placed in the list of contour 
    *               levels to draw.
    * @return A ViewControl that can be used to specify the non uniform 
    *         contour levels to draw.
    */
   private ControlList generateManualControls(float[] levels)
   {
      /*
       * These are the controls used to manually enter the contour levels.
       */
      final ControlList manualControls = 
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
            else //something was added or removed from the list
               manualControls.sort(LIST_COMPARATOR);
         }
      });

      return manualControls;
   }
   
   /**
    * Generates a button that is used to redraw the contour plot using both 
    * values entered for the uniform and non uniform contour levels.  The 
    * button is already configured so that when it is pressed the 
    * screen is redrawn using the values entered in the controls for 
    * both the uniform and non uniform controls.
    * 
    * @return A button that is used to redraw the contour plot.
    */
   private ButtonControl generateRedrawBothButton()
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
   /**
    * Used to get the values entered that specify the uniform contour 
    * levels to draw.  These values are encapsulated in a 
    * <code>UniformContours</code> object.
    * 
    * @param errMsg This must be a non-null array with at least one 
    *               element (only the first element will be modified).  
    *               If an error occurs when trying to create the 
    *               <code>UniformContours</code> object, a message (that 
    *               can be redirected to the user) is stored as the 
    *               first element of this array.  If no errors have 
    *               occured the first element is set to <code>null</code>.
    * @return An encapsulation of the values used to specify the 
    *         uniform contours to draw.
    */
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
   
   /**
    * This is a convience method for the 
    * {@link #getNonUniformControls() getNonUniformControls()} method.  It 
    * is used to get the floats that have been entered in the list on the 
    * ViewControl used to specify the non uniform contours.
    * 
    * @return The floats entered in the control that is used to specify the 
    *         non uniform contours to plot.  If any one of the values 
    *         entered cannot be converted to a float, a message is 
    *         added to <code>SharedData</code> and the value is not placed 
    *         in the array returned.
    */
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
   
   /**
    * Used to get the values entered that specify the non uniform contour 
    * levels to draw.  These values are encapsulated in a 
    * <code>NonUniformContours</code> object.
    * 
    * @param errMsg This must be a non-null array with at least one 
    *               element (only the first element will be modified).  
    *               If an error occurs when trying to create the 
    *               <code>NonUniformContours</code> object, a message (that 
    *               can be redirected to the user) is stored as the 
    *               first element of this array.  If no errors have 
    *               occured the first element is set to <code>null</code>.
    * @return An encapsulation of the values used to specify the 
    *         non uniform contours to draw.
    */
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
   /**
    * Called when the values entered in the controls for the 
    * uniform contour levels have changed.  This method then 
    * gets the new values and redraws the contour plot.
    */
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
   
   /**
    * Called when the values entered in the controls for the 
    * nonuniform contour levels have changed.  This method 
    * then gets the new values and redraws the contour 
    * plot.
    */
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
   
   /**
    * Called when the values entered in the controls for 
    * either (or both) the uniform or nonuniform contour levels have 
    * changed.  This method then gets the new values and redraws the 
    * contour plot.
    */
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
                                                 nonuniformControls,
                                                 true, 
                                                 false));
   }
}
