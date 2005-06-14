/*
 * Created on Apr 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.anl.ipns.ViewTools.Components.TwoD;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.StringFilter.FloatFilter;
import gov.anl.ipns.Util.StringFilter.IntegerFilter;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.ViewControls.ButtonControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckbox;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlList;
import gov.anl.ipns.ViewTools.Components.ViewControls.FieldEntryControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.TabbedViewControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.MixedContours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.NonUniformContours;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.UniformContours;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JPanel;

import DataSetTools.util.SharedData;

/**
 * 
 */
public class ContourViewComponent implements IViewComponent2D, Serializable
{
   private static final float   default_min = 0;
   private static final float   default_max = 10;
   private static final int     default_num_levels = 11;
   private static final float[] default_manual_levels = new float[0];
   
   private IVirtualArray2D dataArray;
   /** The Vector of ActionListener associated with this component. */
   private Vector listenerVec;
   private ContourJPanel contourPanel;
   private ViewControl[] controls;
   
   private TabbedViewControl tabControl;
   private ControlCheckbox aspectRatio;
   
   public ContourViewComponent(IVirtualArray2D arr)
   {
      this.dataArray = arr;
      this.listenerVec = new Vector();
      this.contourPanel = new ContourJPanel(arr,
                                            default_min,
                                            default_max,
                                            default_num_levels);
      
      //now to build the controls
        initControls(default_min, default_max, default_num_levels, 
                      default_manual_levels, false);
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float minValue, float maxValue, int numLevels)
   {
      this(arr);
      this.contourPanel = new ContourJPanel(arr,minValue,maxValue,numLevels);
      
      //now to build the controls
        initControls(minValue, maxValue, numLevels, 
                      default_manual_levels, false);
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float[] levels)
   {
      this(arr);
      this.contourPanel = new ContourJPanel(arr,levels);
      
      //now to build the controls
        initControls(default_min, default_max, default_num_levels,
                      levels, true);
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
   
   public void setObjectState(ObjectState new_state)
   {
      
   }

   public ObjectState getObjectState(boolean is_default)
   {
      ObjectState state = new ObjectState();
       
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
      
      return new VirtualArray2D(dataArray);
   }
   
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
            reloadAspectRatioControls();
         }
      });
      
      return aspectRatio;
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
   
   private void initControls(float minValue, float maxValue, int numLevels, 
         float[] levels, boolean useManualLevels)
   {
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

      controls = new ViewControl[3];
      controls[0] = tabControl;
      controls[1] = generateRedrawBothButton();
      controls[2] = generateAspectRatioCheckbox();
   }
   
   private FieldEntryControl getUniformControls()
   {
      return (FieldEntryControl)tabControl.getViewControlAt(0);
   }
   
   private ControlList getNonUniformControls()
   {
      return (ControlList)tabControl.getViewControlAt(1);
   }
   
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
   
   private void reloadAspectRatioControls()
   {
      contourPanel.setPreserveAspectRatio(aspectRatio.isSelected());
      contourPanel.repaint();
   }
   
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
}
