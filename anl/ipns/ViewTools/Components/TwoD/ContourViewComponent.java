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
 * Revision 1.7  2005/06/17 19:33:26  kramer
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
import gov.anl.ipns.Util.StringFilter.IntegerFilter;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.ViewControls.CompositeContourControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckbox;
import gov.anl.ipns.ViewTools.Components.ViewControls.FieldEntryControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.LabelCombobox;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 
 */
public class ContourViewComponent implements IViewComponent2D, Serializable
{
   public static final String CONTOUR_CONTROLS_KEY = "Contour controls key";
   public static final String ASPECT_RATIO_KEY = "Aspect ratio key";
   
   private static final boolean DEFAULT_PRESERVE_ASPECT_RATIO = false;
   
   public static final float   DEFAULT_LOWEST_CONTOUR = 0;
   public static final float   DEFAULT_HIGHEST_CONTOUR = 10;
   public static final int     DEFAULT_NUM_CONTOURS = 11;
   public static final float[] DEFAULT_MANUAL_LEVELS = new float[0];
   
   /*
   private static final int SOLID_INDEX = 0;
   private static final int DASHED_INDEX = 1;
   private static final int DASHED_DOTTED_INDEX = 2;
   private static final int DOTTED_INDEX = 3;
   private static final int SOLID_DASHED_INDEX = 4;
   private static final int SOLID_DASHED_DASHEDDOTTED_DOTTED = 5;
   */
   
   /** The Vector of ActionListener associated with this component. */
   private Vector listenerVec;
   private ContourJPanel contourPanel;
   private ViewControl[] controls;
   
   private CompositeContourControl contourControl;
   private ControlCheckbox aspectRatio;
   //private LabelCombobox lineStyleComboBox;
   private LineStyleControl lineStyleControl;
   private FieldEntryControl labelEveryNthField;
   private FieldEntryControl numSigDigField;
   
   public ContourViewComponent(IVirtualArray2D arr)
   {
      this.listenerVec = new Vector();
      this.contourPanel = new ContourJPanel(arr,
                                            DEFAULT_LOWEST_CONTOUR,
                                            DEFAULT_HIGHEST_CONTOUR,
                                            DEFAULT_NUM_CONTOURS);
      
      //now to build the controls
        initControls(DEFAULT_LOWEST_CONTOUR, 
                     DEFAULT_HIGHEST_CONTOUR, 
                     DEFAULT_NUM_CONTOURS, 
                     DEFAULT_MANUAL_LEVELS, 
                     false);
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float minValue, float maxValue, int numLevels)
   {
      this(arr);
      this.contourPanel = new ContourJPanel(arr,minValue,maxValue,numLevels);
      
      //now to build the controls
        initControls(minValue, maxValue, numLevels, 
                      DEFAULT_MANUAL_LEVELS, false);
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
                     true);
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

   public static void main(String[] args)
   {
   }
   
   public static IVirtualArray2D getTestData(int Nx, int Ny, 
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
   
   public ControlCheckbox generateAspectRatioCheckbox()
   {
      /*
       * This is the control used to specify if the aspect ratio should 
       * be preserved.
       */
      aspectRatio = new ControlCheckbox();
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
   
   public LineStyleControl generateLineStyleControl()
   {
      return new LineStyleControl(contourPanel);
   }
   
   /*
   public LabelCombobox generateLineStyleComboBox()
   {
      String[] styles = new String[6];
        styles[SOLID_INDEX] = "all solid";
        styles[DASHED_INDEX] = "all dashed";
        styles[DASHED_DOTTED_INDEX] = "all dahsed dotted";
        styles[DOTTED_INDEX] = "all dotted";
        styles[SOLID_DASHED_INDEX] = "solid; dashed";
        styles[SOLID_DASHED_DASHEDDOTTED_DOTTED] = 
                                     "solid; dashed dotted; dotted";
      lineStyleComboBox = new LabelCombobox("Line style", styles);
        lineStyleComboBox.addActionListener(new ActionListener()
        {
           private final int[] SOLID_ARR = new int[] {ContourJPanel.SOLID};
           private final int[] DASHED_ARR = new int[] {ContourJPanel.DASHED};
           private final int[] DASHED_DOTTED_ARR = new int[] {ContourJPanel.DASHED_DOTTED};
           private final int[] DOTTED_ARR = new int[] {ContourJPanel.DOTTED};
           private final int[] SOLID__DASHED_ARR = new int[] {ContourJPanel.SOLID, ContourJPanel.DASHED};
           private final int[] SOLID__DASHED_DOTTED__DOTTED_ARR = new int[] {ContourJPanel.SOLID, ContourJPanel.DASHED_DOTTED, ContourJPanel.DOTTED};
           
           public void actionPerformed(ActionEvent event)
           {
              int index = lineStyleComboBox.getSelectedIndex();
              int[] lineStyleArr = SOLID_ARR;
              switch (index)
              {
                 case SOLID_INDEX:
                    lineStyleArr = SOLID_ARR;
                    break;
                 case DASHED_INDEX:
                    lineStyleArr = DASHED_ARR;
                    break;
                 case DASHED_DOTTED_INDEX:
                    lineStyleArr = DASHED_DOTTED_ARR;
                    break;
                 case DOTTED_INDEX:
                    lineStyleArr = DOTTED_ARR;
                    break;
                 case SOLID_DASHED_INDEX:
                    lineStyleArr = SOLID__DASHED_ARR;
                    break;
                 case SOLID_DASHED_DASHEDDOTTED_DOTTED:
                    lineStyleArr = SOLID__DASHED_DOTTED__DOTTED_ARR;
                    break;
              }
              
              contourPanel.setLineStyles(lineStyleArr);
           }
        });
      return lineStyleComboBox;
   }
   */
   
   public FieldEntryControl generateLabelEveryNthField()
   {
      labelEveryNthField = new FieldEntryControl(new String[]{"Label every "}, 
                                                 new int[] {1});
        labelEveryNthField.enableFilter(new IntegerFilter(), 0);
        labelEveryNthField.setButtonText("Draw");
        labelEveryNthField.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent event)
           {
              contourPanel.setShowLabelEvery((int)labelEveryNthField.getFloatValue(0));
           }
        });
     return labelEveryNthField;
   }
   
   public FieldEntryControl generateNumSigFigField()
   {
      numSigDigField = 
         new FieldEntryControl(new String[] {"Number of significant digits"}, 
                               new int[] {ContourJPanel.DEFAULT_NUM_SIG_DIGS});
        numSigDigField.enableFilter(new IntegerFilter(), 0);
        numSigDigField.setButtonText("Draw");
        numSigDigField.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent event)
           {
              contourPanel.setNumSigDigits((int)numSigDigField.getFloatValue(0));
           }
        });
     return numSigDigField;
   }
   
   private void initControls(float minValue, float maxValue, int numLevels, 
         float[] levels, boolean useManualLevels)
   {
      controls = new ViewControl[5];
      controls[0] = generateContourControls(minValue, maxValue, numLevels, 
                                            levels, useManualLevels);
      controls[1] = generateAspectRatioCheckbox();
      controls[2] = generateLineStyleControl();
      controls[3] = generateLabelEveryNthField();
      controls[4] = generateNumSigFigField();
   }
   
   private class LineStyleControl extends ViewControl implements ActionListener
   {
      private final String STYLES_KEY = "Styles";
      
      private final int[] DEFAULT_STYLES = new int[] {ContourJPanel.SOLID, 
                                                      ContourJPanel.SOLID, 
                                                      ContourJPanel.SOLID, 
                                                      ContourJPanel.SOLID};
      
      private ContourJPanel contourPanel;
      private LabelCombobox[] styleComboBoxes;
      private int[] currentStyles;
      
      public LineStyleControl(ContourJPanel panel)
      {
         super("Line styles");
         
         contourPanel = panel;
         
         styleComboBoxes = new LabelCombobox[4];
         for (int i=0; i<styleComboBoxes.length; i++)
           styleComboBoxes[i] = constructStyleBox(i);
         
         currentStyles = new int[styleComboBoxes.length];
         Arrays.fill(currentStyles, ContourJPanel.SOLID);
         
         JButton drawButton = new JButton("Draw");
           drawButton.addActionListener(this);
           
         JPanel stylePanel = new JPanel(); 
           BoxLayout layout = new BoxLayout(stylePanel, BoxLayout.Y_AXIS);
           stylePanel.setLayout(layout);
           for (int i=0; i<styleComboBoxes.length; i++)
              stylePanel.add(styleComboBoxes[i]);
           
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
           buttonPanel.add(drawButton);
           
         setLayout(new BorderLayout());
           add(stylePanel, BorderLayout.CENTER);
           add(buttonPanel, BorderLayout.SOUTH);
      }
      
      private LabelCombobox constructStyleBox(int index)
      {
         String[] styles = new String[4];
           styles[getIndexForStyle(ContourJPanel.SOLID)] = 
                                                    "solid";
           styles[getIndexForStyle(ContourJPanel.DASHED)] = 
                                                    "dashed";
           styles[getIndexForStyle(ContourJPanel.DASHED_DOTTED)] = 
                                                    "dahsed dotted";
           styles[getIndexForStyle(ContourJPanel.DOTTED)] = 
                                                    "dotted";
         return new LabelCombobox("Line "+(index+1),styles);
      }
      
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
      
      public void setControlValue(Object value)
      {
         if (value==null)
            return;
         
         if ( !(value instanceof int[]) ) 
            return;
         
         int[] styles = (int[])value;
         int min = Math.min(styles.length, styleComboBoxes.length);
         for (int i=0; i<min; i++)
            styleComboBoxes[i].setSelectedIndex(getIndexForStyle(styles[i]));
      }
      
      public Object getControlValue()
      {
         int[] styles = new int[styleComboBoxes.length];
         for (int i=0; i<styleComboBoxes.length; i++)
            styles[i] = getStyleForIndex(styleComboBoxes[i].getSelectedIndex());
         return styles;
      }
      
      public ObjectState getObjectState(boolean isDefault)
      {
         ObjectState state = super.getObjectState(isDefault);
           if (isDefault)
              state.insert(STYLES_KEY, DEFAULT_STYLES);
           else
              state.insert(STYLES_KEY, (int[])getControlValue());
         return state;
      }
      
      public void setObjectState(ObjectState state)
      {
         if (state==null)
            return;
         
         Object val = state.get(STYLES_KEY);
         if (val!=null)
            setControlValue((int[])val);
      }

      public ViewControl copy()
      {
         LineStyleControl copy = new LineStyleControl(contourPanel);
         copy.setObjectState(this.getObjectState(false));
         return copy;
      }
      
      public void actionPerformed(ActionEvent event)
      {
         for (int i=0; i<styleComboBoxes.length; i++)
            currentStyles[i] = 
               getStyleForIndex(styleComboBoxes[i].getSelectedIndex());
         
         contourPanel.setLineStyles(currentStyles);
      }
   }
}
