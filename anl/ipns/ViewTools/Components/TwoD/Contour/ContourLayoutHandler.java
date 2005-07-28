/*
 * File: ContourLayoutHandler.java
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
 * Revision 1.2  2005/07/28 15:33:21  kramer
 * Changed calls from changeDisplay() (which repaints the display) to
 * displayChanged() (which tells all listeners to repaint the display).
 * As a result, the new PanViewControl is repainted as the main window is
 * repainted.
 *
 * Revision 1.1  2005/07/25 20:48:21  kramer
 *
 * Initial checkin.  This is the module of the ContourViewComponent that is
 * responsible for working with the ContourJPanel and overlays on the
 * ContourViewComponent.
 *
 */
package gov.anl.ipns.ViewTools.Components.TwoD.Contour;

import gov.anl.ipns.Util.Messaging.Information.InformationCenter;
import gov.anl.ipns.Util.Messaging.Property.PropertyChangeConnector;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.Transparency.AxisOverlay2D;
import gov.anl.ipns.ViewTools.Components.Transparency.IAxisAddible;
import gov.anl.ipns.ViewTools.Components.Transparency.OverlayJPanel;
import gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlColorScale;
import gov.anl.ipns.ViewTools.Components.ViewControls.IColorScaleAddible;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;
import gov.anl.ipns.ViewTools.UI.FontUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

/**
 */
public class ContourLayoutHandler extends ContourChangeHandler 
                                             implements IAxisAddible, 
                                                        IColorScaleAddible
{
//------------------------=[ ObjectState keys ]=------------------------------//
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
//------------------------=[ ObjectState keys ]=------------------------------//
   
   
//---------------------------=[ Constants ]=----------------------------------//
   private final int DEFAULT_NORTH_HEIGHT;
   private final int DEFAULT_SOUTH_HEIGHT;
   private final int DEFAULT_EAST_WIDTH;
   private final int DEFAULT_WEST_WIDTH;
//-------------------------=[ End constants ]=--------------------------------//
   
   
//--------------------------------=[ Fields ]=--------------------------------//
   private PartitionedPanel innerPanel;
   private PartitionedPanel partitionPanel;
   /**
    * The panel that contains the <code>ContourJPanel</code> and 
    * all of the transparencies.
    */
   private JPanel layerPanel;
   /**
    * The Vector of transparencies that are placed on top of the 
    * <code>ContourJPanel</code> to give it more functionality.  This 
    * Vector is a Vector of <code>OverlayJPanel</code> objects.
    */
   private Vector transparencies;

   /** The Vector of ActionListener associated with this component. */
   private Vector listenerVec;
   
   private ContourViewComponent contourComp;
//------------------------------=[ End fields ]=------------------------------//
   
   
//--------------=[ Constructors and construction methods ]=-------------------//
   public ContourLayoutHandler(PropertyChangeConnector connector, 
                               InformationCenter center, 
                               ContourJPanel panel, 
                               ContourViewComponent component, 
                               IVirtualArray2D v2D)
   {
      super(connector, center, panel);
      
      this.DEFAULT_NORTH_HEIGHT = 25;
      this.DEFAULT_SOUTH_HEIGHT = getFont().getSize()*3+9;
      this.DEFAULT_EAST_WIDTH = 50;
      this.DEFAULT_WEST_WIDTH = getFont().getSize()*getPrecision()+22;
      
      init(component);
      reinit(v2D);
   }
   
   private void init(ContourViewComponent contourComp)
   {
      this.contourComp = contourComp;
      
      //now to connect to the PropertyChangeConnector
      getPropertyConnector().addHandler(this);
      
      ContourJPanel contourPanel = getContourPanel();
        contourPanel.addComponentListener(new ResizeListener());
        contourPanel.addActionListener(new CursorListener());
        contourPanel.addActionListener(new ContourPanelListener());
      
      
      this.layerPanel = new JPanel();
      //layout used to add transparencies on top of the ContourJPanel
      OverlayLayout layout = new OverlayLayout(this.layerPanel);
        this.layerPanel.setLayout(layout);
      
        this.innerPanel = 
           new PartitionedPanel(DEFAULT_NORTH_HEIGHT, DEFAULT_SOUTH_HEIGHT, 
                                DEFAULT_EAST_WIDTH, DEFAULT_WEST_WIDTH);
          innerPanel.setPartitionComponent(PartitionedPanel.CENTER, 
                                               contourPanel);
        this.partitionPanel = new PartitionedPanel(0, 0, 0, 0);
          partitionPanel.setPartitionComponent(PartitionedPanel.CENTER, 
                                               innerPanel);
         
      this.transparencies = new Vector();
      this.listenerVec = new Vector();
   }
//------------=[ End constructors and construction methods ]=-----------------//
   
   
//--------=[ Methods implemented for the ContourChangeHandler class ]=--------//
   public void reinit(IVirtualArray2D v2D)
   {
      ContourJPanel contourPanel = getContourPanel();
      
      if (v2D == null)
      {
         this.layerPanel.removeAll();
         contourPanel.removeAllActionListeners();
         this.layerPanel.add(
               new JLabel("No data to display as a contour image."));
         
         changeDisplay();
      }
      else
      {
         //store the transparencies in a Vector
         this.transparencies.clear();
           AxisOverlay2D axes = new AxisOverlay2D(this);
             axes.setVisible(true);
             axes.setDisplayAxes(AxisOverlay2D.DUAL_AXES);
           this.transparencies.add(axes);
           
         //add each transparency and the ContourJPanel to the main layout panel
         for (int i=0; i<this.transparencies.size(); i++)
           this.layerPanel.add((OverlayJPanel)this.transparencies.elementAt(i));
         this.layerPanel.add(this.partitionPanel);
         
         //inform the ContourJPanel that the data has changed
         AxisInfo xInfo = v2D.getAxisInfo(AxisInfo.X_AXIS);
         AxisInfo yInfo = v2D.getAxisInfo(AxisInfo.Y_AXIS);
         
         contourPanel.initializeWorldCoords(new CoordBounds(xInfo.getMin(), 
                                                            yInfo.getMax(), 
                                                            xInfo.getMax(), 
                                                            yInfo.getMin()));
         contourPanel.changeData(v2D);
         
         //paint all of the components
         displayChanged();
         
         this.layerPanel.revalidate();
         this.layerPanel.repaint();
      }
   }
   
   public void changeDisplay()
   {
      //determine if the aspect ratio is to be preserved
      Boolean preserveAspect = 
         (Boolean)getInfoCenter().
            obtainValue(ContourMenuHandler.PRESERVE_ASPECT_RATIO_INFO_KEY);
      boolean preserve = ContourMenuHandler.DEFAULT_PRESERVE_ASPECT_RATIO;
      if (preserveAspect != null)
         preserve = preserveAspect.booleanValue();
      
      //if the aspect ratio is supposed to be preserved, first redraw the 
      //display without the aspect ratio preserved.  This will make the 
      //display take up the largest amount of the panel that it can.  Then, 
      //redraw the display (with the aspect ratio preserved) to trim 
      //this display down to the closest image with the aspect ratio 
      //preserved.  This will make the display be the largest image that 
      //fits on the panel and has its aspect ratio still preserved
      if (preserve)
         setPreserveAspectRatio(!preserve);
      
      //now redraw the display with the aspect ratio being preserved as 
      //specified
      setPreserveAspectRatio(preserve);
      
      //now repaint and revalidate all of the panels used in the display 
      this.partitionPanel.repaint();
      this.partitionPanel.revalidate();
      
      this.innerPanel.repaint();
      this.innerPanel.revalidate();
      
      Component topPanel = this.layerPanel;
      while ( topPanel.getParent() != null)
         topPanel = topPanel.getParent();
      topPanel.repaint();
   }
   
   public void changeColorScaleName(String colorscale)
   {
      boolean isDoubleSided = ContourMenuHandler.DEFAULT_IS_DOUBLE_SIDED;
      Boolean result = 
         (Boolean)getInfoCenter().
            obtainValue(ContourMenuHandler.IS_DOUBLE_SIDED_INFO_KEY);
      if (result != null)
         isDoubleSided = result.booleanValue();
         
      getContourPanel().setColorScale(colorscale, isDoubleSided);
      
      ControlColorScale ccs = getVisibleColorScale();
      if (ccs != null)
         ccs.setColorScale(colorscale, isDoubleSided);
         
      displayChanged();
   }
   
   public void changeColor(Color color)
   {
      getContourPanel().setColorScale(color);
      displayChanged();
   }
   
   public void changeIsDoubleSided(boolean isDoubleSided)
   {
      ControlColorScale control = getVisibleColorScale();
      if (control != null)
      {
         String colorscale = 
            (String)getInfoCenter().
               obtainValue(ContourControlHandler.COLORSCALE_NAME);
         if (colorscale == null)
            colorscale = ContourControlHandler.DEFAULT_COLOR_SCALE;
         
         control.setColorScale(colorscale, isDoubleSided);
      }
      
      displayChanged();
   }
   
   public void changeColorScaleLocation(String location)
   {
      this.partitionPanel.removePartitionComponent(PartitionedPanel.EAST);
      this.partitionPanel.removePartitionComponent(PartitionedPanel.SOUTH);
      this.partitionPanel.adjustPreferredSizes(0, 0, 0, 0);
      
      String title = getValueAxisInfo().getLabel()+" ("+
                     getValueAxisInfo().getUnits()+")";
      
      if ( location.equals(BELOW_IMAGE_LOCATION) )
      {
         ControlColorScale colorscaleControl = 
            new ControlColorScale(this, ControlColorScale.HORIZONTAL);
           colorscaleControl.setTitle(title);
         this.partitionPanel.setPartitionComponent(PartitionedPanel.SOUTH, 
                                                   colorscaleControl);
         this.partitionPanel.adjustPreferredSizes(0, 100, 0, 0);
      }
      else if ( location.equals(RIGHT_IMAGE_LOCATION) )
      {
         ControlColorScale colorscaleControl = 
            new ControlColorScale(this, ControlColorScale.VERTICAL);
           colorscaleControl.setTitle(title);
         this.partitionPanel.setPartitionComponent(PartitionedPanel.EAST, 
                                                   colorscaleControl);
         this.partitionPanel.adjustPreferredSizes(0, 0, 100, 0);
      }
         
      displayChanged();
   }
   
   public void changeIntensity(double intensity)
   {
      getContourPanel().setLogScale(intensity);
      ControlColorScale scale = getVisibleColorScale();
      if (scale != null)
         scale.setLogScale(intensity);
      displayChanged();
   }
   
   public void changeAspectRatio(boolean preserve)
   {
      setPreserveAspectRatio(preserve);
      displayChanged();
   }
//------=[ End methods implemented for the ContourChangeHandler class ]=------//
   
   
// ------=[ Methods implemented for the InformationHandler interface ]=--------//
   public Object getValue(String key)
   {
      //this class isn't registered to maintain any values
      return null;
   }
//----=[ End methods implemented for the InformationHandler interface ]=------//
   
   
//-----------=[ Methods implemented for the IPreserveState interface ]=-------//
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      //set the states for the panels used in this component
      Object val = state.get(CONTOUR_PANEL_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         getContourPanel().setObjectState((ObjectState)val);
      
      val = state.get(AXIS_OVERLAY_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         getAxisOverlay().setObjectState((ObjectState)val);
   }

   public ObjectState getObjectState(boolean is_default)
   {
      //get an empty ObjectState
      ObjectState state = new ObjectState();

        //store the states of the panels on this component
          //for the ContourJPanel
          state.insert(CONTOUR_PANEL_KEY, 
                       getContourPanel().getObjectState(is_default));
          //for the AxisOverlay2D
          state.insert(AXIS_OVERLAY_KEY, 
                       getAxisOverlay().getObjectState(is_default));
          
      return state;
   }
//-----------=[ Methods implemented for the IPreserveState interface ]=-------//
   
   
//----------=[ Methods implemented for the IAxisAddible interface ]=----------//
   public AxisInfo getAxisInformation(int axiscode)
   {
      float min = 0;
      float max = 0;
      
      AxisInfo axisInfo = getContourPanel().getData().getAxisInfo(axiscode);
      String label = axisInfo.getLabel();
      String units = axisInfo.getUnits();
      
      if (axiscode == AxisInfo.X_AXIS)
      {
         min = getContourPanel().getGlobalWorldCoords().getX1();
         max = getContourPanel().getGlobalWorldCoords().getX2();
      }
      else if (axiscode == AxisInfo.Y_AXIS)
      {
         min = getContourPanel().getGlobalWorldCoords().getY1();
         max = getContourPanel().getGlobalWorldCoords().getY2();
      }
      else
      {
         min = getContourPanel().getContours().getLowestLevel();
         max = getContourPanel().getContours().getHighestLevel();
      }
      
      return new AxisInfo(min, max, label, units, AxisInfo.LINEAR);
   }

   public String getTitle()
   {
      return getContourPanel().getData().getTitle();
   }

   public int getPrecision()
   {
//TODO FIX THIS SO THAT IT RETURNS SOMETHING USEFUL
      return 4;
      //return controlHandler.getNumSigFigs();
   }

   public Font getFont()
   {
      //TODO Make the font adjustable
      return FontUtil.LABEL_FONT2;
   }

   public CoordBounds getLocalCoordBounds()
   {
      return getContourPanel().getLocalWorldCoords().MakeCopy();
   }

   public CoordBounds getGlobalCoordBounds()
   {
      return getContourPanel().getGlobalWorldCoords().MakeCopy();
   }

   public Rectangle getRegionInfo()
   {
      //this.partitionPanel contains another PartiontionedPanel at its center 
      //this returns the location and size (as a rectangle) of the entire
      //outer panel (this.partitionPanel)
      Rectangle partRect = 
         this.partitionPanel.getPartitionRegion(PartitionedPanel.CENTER);
      
      //the PartitionedPanel at the center of this.partitionPanel is extracted
      PartitionedPanel innerPartPanel = getInnerPanel();
      
      //the location and size of this inner panel
      Rectangle innerRelativeRect = 
         innerPartPanel.getPartitionRegion(PartitionedPanel.CENTER);
      
      //innerRelativeRect.getLocation().x gives the x pixel relative to the 
      //corner of the inner panel.  
      //to get the x value relative to the corner of the panel 
      //this.partitionPanel, add the distance from the corner of the panel 
      //this.partitionPanel and the corner of the inner panel 
      //(this distance is partRect.getLocation().x)
      //the same applies for the y values
      int xPt = innerRelativeRect.getLocation().x + partRect.getLocation().x;
      int yPt = innerRelativeRect.getLocation().y + partRect.getLocation().y;
      
      return new Rectangle(new Point(xPt, yPt), innerRelativeRect.getSize());
   }
//----------=[ Methods implemented for the IAxisAddible interface ]=----------//
   
   
//-----=[ Extra methods implemented for the IColorScaleAddible interface ]=---//
   public String getColorScale()
   {
      String colorscale = 
         (String)getInfoCenter().
            obtainValue(ContourControlHandler.COLORSCALE_NAME_INFO_KEY);
      if (colorscale == null)
         colorscale = ContourControlHandler.DEFAULT_COLOR_SCALE;
      
      return colorscale;
   }

   public void addActionListener(ActionListener actionlistener)
   {
      if (actionlistener==null)
         return;
      if (!listenerVec.contains(actionlistener))
         listenerVec.add(actionlistener);
   }

   public AxisInfo getValueAxisInfo()
   {
      return getAxisInformation(AxisInfo.Z_AXIS);
   }

   public double getLogScale()
   {
      Double intensity = 
         (Double)getInfoCenter().
            obtainValue(ContourControlHandler.INTENSITY_INFO_KEY);
      if (intensity==null)
         return ContourControlHandler.DEFAULT_INTENSITY;
      else
         return intensity.doubleValue();
   }
//-----=[ Extra methods implemented for the IColorScaleAddible interface ]=---//
   
   
//-----------------------=[ Getter/setter methods ]=--------------------------//
   public AxisOverlay2D getAxisOverlay()
   {
      return (AxisOverlay2D)transparencies.get(0);
   }
   
   public JPanel getDisplayPanel()
   {
      return this.layerPanel;
   }
   
   public void setPreserveAspectRatio(boolean preserve)
   {
      if (!preserve)
      {
         this.innerPanel.disablePreserveAspectRatio(DEFAULT_NORTH_HEIGHT, 
                                                    DEFAULT_SOUTH_HEIGHT, 
                                                    DEFAULT_EAST_WIDTH, 
                                                    DEFAULT_WEST_WIDTH);
      }
      else
      {
         ContourJPanel contourPanel = getContourPanel();
         CoordBounds localBounds = contourPanel.getLocalWorldCoords();
         int rowMin = contourPanel.getRowForY(localBounds.getY1());
         int rowMax = contourPanel.getRowForY(localBounds.getY2());
         int colMin = contourPanel.getColumnForX(localBounds.getX1());
         int colMax = contourPanel.getColumnForX(localBounds.getX2());
         
         IVirtualArray2D data = contourPanel.getData();
         this.innerPanel.enablePreserveAspectRatio(rowMax - rowMin, 
                                                   colMax - colMin, 
                                                   data.getNumRows() - 1,
                                                   data.getNumColumns() - 1);
      }
   }
//---------------------=[ End getter/setter methods ]=------------------------//
   
   
//--------------------------=[ Extra methods ]=-------------------------------//
   //---------------------=[ For the listeners ]=-----------------------------//
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
   //-------------------=[ End for the listeners ]=---------------------------//
   
   public void kill()
   {
      for (int i=0; i<this.transparencies.size(); i++)
         ((OverlayJPanel)this.transparencies.elementAt(i)).kill();
   }
// --------------------------=[ Extra methods ]=-------------------------------//
   
   
// --------------------=[ Private methods ]=-----------------------------------//
   private void sendMessage(String message)
   {
      Vector listenerVec = this.listenerVec;
      if (listenerVec==null)
         return;
      
      ActionListener listener;
      for (int i=0; i<listenerVec.size(); i++)
      {
         listener = (ActionListener)listenerVec.elementAt(i);
         if (listener!=null)
            listener.actionPerformed(new ActionEvent(this.contourComp, 
                                                     0, message));
      }
   }
   
   private ControlColorScale getVisibleColorScale()
   {
      ControlColorScale scale = null;
      if ( (scale=getControlColorScaleAt(PartitionedPanel.EAST)) != null)
         return scale;
      else if ( (scale=getControlColorScaleAt(PartitionedPanel.SOUTH)) != null)
         return scale;
      else
         return null;
   }
   
   private PartitionedPanel getInnerPanel()
   {
      //extract the PartitionedPanel at the center of 'this.partitionPanel'
      return (PartitionedPanel)(this.partitionPanel.
                getPartitionComponent(PartitionedPanel.CENTER));
   }
   
   //gets the ControlColorScale located in this.partitionPanel's 
   //specified partition
   private ControlColorScale getControlColorScaleAt(int partition)
   {
      ControlColorScale control = null;
      try
      {
         control = 
            (ControlColorScale)this.partitionPanel.
               getPartitionComponent(partition);
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
         control = null;
      }
      catch (ClassCastException e)
      {
         control = null;
      }
      
      return control;
   }
//--------------------=[ Private methods ]=-----------------------------------//
   
   
//---------------------------=[ Listeners ]=----------------------------------//
   private class ResizeListener extends ComponentAdapter
   {
      public void componentResized(ComponentEvent event)
      {
         displayChanged();
      }
   }

   private class CursorListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         if ( event.getActionCommand().equals(CoordJPanel.CURSOR_MOVED) )
            if (!getContourPanel().isDoingBox())
               sendMessage(IViewComponent2D.POINTED_AT_CHANGED);
      }
   }
   
   private class ContourPanelListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         String command = event.getActionCommand();
         
         if (command.equals(ContourJPanel.ZOOM_IN) || 
               command.equals(ContourJPanel.RESET_ZOOM))
            displayChanged();
      }
   }
//-------------------------=[ End listeners ]=--------------------------------//
   
   
//-----------------------=[ Inner classes ]=----------------------------------//
   private static class PartitionedPanel extends JPanel
   {
      public static final int NORTH = 0;
      public static final int SOUTH = 1;
      public static final int EAST = 2;
      public static final int WEST = 3;
      public static final int CENTER = 4;
      
      private JPanel northPanel;
      private JPanel southPanel;
      private JPanel eastPanel;
      private JPanel westPanel;
      private JPanel centerPanel;
      
      public PartitionedPanel()
      {
         super();
         
         GridLayout layout = new GridLayout(1,1);
         
         northPanel = new JPanel(layout);
         southPanel = new JPanel(layout);
         eastPanel = new JPanel(layout);
         westPanel = new JPanel(layout);
         centerPanel = new JPanel(layout);
         
         setLayout(new BorderLayout());
         add(northPanel, BorderLayout.NORTH);
         add(southPanel, BorderLayout.SOUTH);
         add(eastPanel, BorderLayout.EAST);
         add(westPanel, BorderLayout.WEST);
         add(centerPanel, BorderLayout.CENTER);
      }
      
      public PartitionedPanel(int northHeight, int southHeight, 
                              int eastWidth, int westWidth)
      {
         this();
         adjustPreferredSizes(northHeight, southHeight, eastWidth, westWidth);
      }
      
      public Rectangle getPartitionRegion(int partition)
      {
         JPanel panel = getPanelInPartition(partition);
         return new Rectangle(panel.getLocation(), 
                              panel.getSize());
      }
      
      public void setPartitionComponent(int partition, Component comp)
      {
         JPanel panel = getPanelInPartition(partition);
           panel.removeAll();
           panel.add(comp);
           
         //revalidate();
         //repaint();
      }
      
      public Component getPartitionComponent(int partition)
      {
         return getPanelInPartition(partition).getComponent(0);
      }
      
      public void removePartitionComponent(int partition)//, 
                                           //int northHeight, int southHeight, 
                                           //int eastWidth, int westWidth)
      {
         JPanel panel = getPanelInPartition(partition);
           panel.removeAll();
         
         //panel.revalidate();
         //panel.repaint();
      }
      
      public void enablePreserveAspectRatio(int numRows, int numCols, 
                                            int maxRows, int maxCols)
      {
         Dimension totalDim = this.getSize();
         int totalHeight = totalDim.height;
         int totalWidth = totalDim.width;
         
         int northHeight = getPanelInPartition(NORTH).getPreferredSize().height;
         int southHeight = getPanelInPartition(SOUTH).getPreferredSize().height;
         int eastWidth = getPanelInPartition(EAST).getPreferredSize().width;
         int westWidth = getPanelInPartition(WEST).getPreferredSize().width;
         
         int maxHeight = totalHeight - (northHeight + southHeight);
         int maxWidth = totalWidth - (eastWidth + westWidth);
         
         int centerHeight = maxHeight;
         int centerWidth = maxWidth;
         
         float ratio = Math.abs(((float)numRows)/((float)numCols));
         
         boolean adjustWidth = (ratio > 1);
         if (ratio == 1)
            adjustWidth = (centerHeight < centerWidth);
         
         if (adjustWidth)
         {
            centerHeight = maxHeight;
            centerWidth = Math.round(centerHeight/ratio);
            
            int deltaW = totalWidth - centerWidth;
            eastWidth = deltaW/2;
            westWidth = deltaW/2;
            if (deltaW%2 == 0) //if deltaW is odd, add one pixel 
                               //to the east panel
               eastWidth++;
         }
         else
         {
            centerWidth = maxWidth;
            centerHeight = Math.round(centerWidth*ratio);
            
            int deltaH = totalHeight - centerHeight;
            northHeight = deltaH/2;
            southHeight = deltaH/2;
            if (deltaH%2 == 0) //if deltaH is odd, add one pixel 
                               //to the south panel
               southHeight++;
         }
         
         adjustPreferredSizes(northHeight, southHeight, eastWidth, westWidth);
      }
      
      public void disablePreserveAspectRatio(int northHeight, int southHeight, 
                                             int eastWidth, int westWidth)
      {
         adjustPreferredSizes(northHeight, southHeight, eastWidth, westWidth);
      }
      
      private void adjustPreferredSizes(int northHeight, int southHeight, 
                                        int eastWidth, int westWidth)
      {
         northPanel.setPreferredSize(new Dimension(0, northHeight));
         southPanel.setPreferredSize(new Dimension(0, southHeight));
         eastPanel.setPreferredSize(new Dimension(eastWidth, 0));
         westPanel.setPreferredSize(new Dimension(westWidth, 0));
         
         Dimension totalDim = getSize();
         centerPanel.setPreferredSize(
               new Dimension( totalDim.width-eastWidth-westWidth, 
                              totalDim.height-northHeight-southHeight ));
      }
      
      private JPanel getPanelInPartition(int location)
      {
         switch (location)
         {
            case NORTH:
               return northPanel;
            case SOUTH:
               return southPanel;
            case EAST:
               return eastPanel;
            case WEST:
               return westPanel;
            case CENTER:
               return centerPanel;
            default:
               return centerPanel;
         }
      }
   }
//---------------------=[ End inner classes ]=--------------------------------//
}
