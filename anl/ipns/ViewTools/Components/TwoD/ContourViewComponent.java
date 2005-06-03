/*
 * Created on Apr 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.anl.ipns.ViewTools.Components.TwoD;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.ViewControls.FieldEntryControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.RangeControl;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;
import gov.anl.ipns.ViewTools.Panels.Contour.Contours.UniformContours;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JPanel;

/**
 * @author kramer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
   private RangeControl manualControls;
   
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
         //these are the controls used to enter a range for the contour levels
           String[] minMaxNames = new String[]{"Minimum", 
                                               "Maximum", 
                                               "Number of Levels"};
           float[] minMaxValues = {minValue, maxValue, numLevels};
           uniformControls = new FieldEntryControl(minMaxNames, minMaxValues);
           uniformControls.addActionListener(new UniformControlListener());
           
         //these are the controls used to manually enter the contour levels
                    
           //ControlCheckbox manualBox = 
           //   new ControlCheckbox("Manually enter contour levels");
           //manualBox.setSelected(false);
           
         //these are the controls used to manually specify the levels to use
            //TODO:  Implement these controls
           
         this.controls = new ViewControl[1];
         this.controls[0] = uniformControls;
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
   
   private class UniformControlListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         float min = uniformControls.getFloatValue(0);
         float max = uniformControls.getFloatValue(1);
         int numLevels = (int)uniformControls.getFloatValue(2);
         System.err.println("Min="+min);
         System.err.println("Max="+max);
         System.err.println("Number of levels="+numLevels);
         contourPanel.setContours(new UniformContours(min, max, numLevels));
      }
   }
}
