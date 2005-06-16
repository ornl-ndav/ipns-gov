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
 * Revision 1.6  2005/06/16 13:45:31  kramer
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
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Vector;

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
   
   /** The Vector of ActionListener associated with this component. */
   private Vector listenerVec;
   private ContourJPanel contourPanel;
   private ViewControl[] controls;
   
   private CompositeContourControl contourControl;
   private ControlCheckbox aspectRatio;
   
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
        initControls(DEFAULT_LOWEST_CONTOUR, DEFAULT_HIGHEST_CONTOUR, DEFAULT_NUM_CONTOURS,
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
      
      return new VirtualArray2D(dataArray);
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
   
   
   
   private void initControls(float minValue, float maxValue, int numLevels, 
         float[] levels, boolean useManualLevels)
   {
      contourControl = 
         new CompositeContourControl(contourPanel, 
                                     minValue, maxValue, numLevels, 
                                     levels, 
                                     false);

      controls = new ViewControl[2];
      controls[0] = contourControl;
      controls[1] = generateAspectRatioCheckbox();
   }
   
   private void reloadAspectRatioControls()
   {
      contourPanel.setPreserveAspectRatio(aspectRatio.isSelected());
      contourPanel.repaint();
   }
}
