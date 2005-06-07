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
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlCheckbox;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlList;
import gov.anl.ipns.ViewTools.Components.ViewControls.FieldEntryControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;
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
   private static final float   default_max = 0;
   private static final int     default_num_levels = 1;
   private static final float[] default_manual_levels = new float[0];
   
   private IVirtualArray2D dataArray;
   /** The Vector of ActionListener associated with this component. */
   private Vector listenerVec;
   private ContourJPanel contourPanel;
   private ViewControl[] controls;
   
   private FieldEntryControl uniformControls;
   private ControlList manualControls;
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
        buildControls(default_min, default_max, default_num_levels, 
                      default_manual_levels, false);
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float minValue, float maxValue, int numLevels)
   {
      this(arr);
      this.contourPanel = new ContourJPanel(arr,minValue,maxValue,numLevels);
      
      //now to build the controls
        buildControls(minValue, maxValue, numLevels, 
                      default_manual_levels, false);
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float[] levels)
   {
      this(arr);
      this.contourPanel = new ContourJPanel(arr,levels);
      
      //now to build the controls
        buildControls(default_min, default_max, default_num_levels,
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
      //TODO Implement this
   }

   public ObjectState getObjectState(boolean is_default)
   {
      return new ObjectState();
   }

   public void setPointedAt(floatPoint2D fpt)
   {
      // TODO Implement this
   }

   public floatPoint2D getPointedAt()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void setSelectedRegions(Region[] rgn)
   {
      // TODO Auto-generated method stub

   }

   public Region[] getSelectedRegions()
   {
      // TODO Auto-generated method stub
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
      private void buildControls(float minValue, float maxValue, int numLevels, 
                                 float[] levels, boolean useManualLevels)
      {
         /*
          * These are the controls used to enter a range for the contour levels
          * The min, max, and number of contours are entered and uniformly 
          * spaced contours are calculated
          */
          String[] minMaxNames = 
                      new String[]{"Minimum", "Maximum", "Number of Levels"};
          float[] minMaxValues = {minValue, maxValue, numLevels};
          uniformControls = new FieldEntryControl(minMaxNames, minMaxValues);
          //set the last value again because its really an int
           uniformControls.setValue(2,numLevels);
          uniformControls.addActionListener(new RedrawListener());
          uniformControls.setButtonText("Redraw");
          //enable filtering so that the user can only enter valid data types
           uniformControls.enableFilter(new FloatFilter(),0);
           uniformControls.enableFilter(new FloatFilter(),1);
           uniformControls.enableFilter(new IntegerFilter(),2);
           
           
         /*
          * These are the controls used to manually enter the contour levels.
          */
          manualControls = new ControlList("Manually Enter Contours",
                                           new FloatFilter());
          manualControls.setSubmitButtonText("Redraw");
          manualControls.addActionListener(new RedrawListener());
          
          /*
           * This is the control used to specify if the aspect ratio should 
           * be preserved.
           */
          aspectRatio = new ControlCheckbox();
          aspectRatio.setText("Preserve Aspect Ratio");
          aspectRatio.addActionListener(new RedrawListener());
          
                    
           //ControlCheckbox manualBox = 
           //   new ControlCheckbox("Manually enter contour levels");
           //manualBox.setSelected(false);
           
         //these are the controls used to manually specify the levels to use
            //TODO:  Implement these controls
           
         controls = new ViewControl[3];
         controls[0] = uniformControls;
         controls[1] = manualControls;
         controls[2] = aspectRatio;
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
   
   private class RedrawListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         if (event.getActionCommand().equals(FieldEntryControl.BUTTON_PRESSED))
         {
            float min = uniformControls.getFloatValue(0);
            float max = uniformControls.getFloatValue(1);
            int numLevels = (int)uniformControls.getFloatValue(2);
            System.out.println("ContourViewComponent#numLevels="+numLevels);
            contourPanel.setContours(new UniformContours(min, max, numLevels));
         }
         else if (event.getActionCommand().equals(ControlList.SUBMIT_PRESSED))
         {
            Object[] obArr = (Object[])manualControls.getControlValue();
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
            
            contourPanel.setContours(new NonUniformContours(validArr));
         }
         else if (event.getActionCommand().
                     equals(ControlCheckbox.CHECKBOX_CHANGED))
         {
            contourPanel.setPreserveAspectRatio(aspectRatio.isSelected());
            contourPanel.repaint();
         }
      }
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
      
      /*
      dataArray[iPeak-1][jPeak-1] =  4.0f;
      dataArray[iPeak-1][jPeak  ] =  6.0f;
      dataArray[iPeak-1][jPeak+1] =  4.0f;
      dataArray[iPeak  ][jPeak-1] =  6.0f;
      dataArray[iPeak  ][jPeak  ] = 10.0f;
      dataArray[iPeak  ][jPeak+1] =  6.0f;
      dataArray[iPeak+1][jPeak-1] =  4.0f;
      dataArray[iPeak+1][jPeak  ] =  6.0f;
      dataArray[iPeak+1][jPeak+1] =  4.0f;
      */
      
      return new VirtualArray2D(dataArray);
   }
   
   /*
   private class TabbedContourControl extends ViewControl
   {
      private JTabbedPane tabbedPane;
      
      public TabbedContourControl()
      {
         super("");
         setBorderVisible(false);
         
         tabbedPane = new JTabbedPane(JTabbedPane.TOP);
          tabbedPane.addTab("",)
         add(tabbedPane);
      }
      
      public void setControlValue(Object value)
      {
      }

      public Object getControlValue()
      {
         return null;
      }

      public ViewControl copy()
      {
         return null;
      }
      
      public ObjectState getObjectState( boolean isDefault )
      {
         
      }
      
      public void setObjectState( ObjectState state )
      {
         
      }
   }
   */
}
