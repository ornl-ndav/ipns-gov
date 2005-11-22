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
 * Revision 1.6  2005/11/22 21:09:59  dennis
 * Modified to point tick marks inward by default.
 *
 * Revision 1.5  2005/10/11 04:53:40  kramer
 *
 * Completed the javadocs for every member of this class.
 *
 * Revision 1.4  2005/10/07 21:36:09  kramer
 *
 * Fully javadoc commented everything in the inner class PartitionedPanel.
 * Approximately 75% of the rest of the code has also been javadoc commented.
 *
 * Revision 1.3  2005/08/02 16:17:51  kramer
 *
 * Modified the getPrecision() method to return the number of significant
 * figures currently being used to render the contour lines.
 *
 * Revision 1.2  2005/07/28 15:33:21  kramer
 *
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
import gov.anl.ipns.Util.Messaging.Information.InformationHandler;
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
 * This is a module of a <code>ContourViewComponent</code> that is 
 * responsible for maintaining the layout of the contour image on 
 * the view component.  That is, this module handles the placement 
 * of the contour image, modifies the size of the contour image so 
 * that its aspect ratio is preserved if required, and stores state 
 * informatino about the layout of the contour image.  In addition, 
 * the layout and placement of the axes and any readouts around the 
 * contour image are maintained by this module.
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
   /**
    * Specifies the default height of the northern region of the panel 
    * that holds the contour image.  In other words, this value can be 
    * thought of as the deault top margin above the contour image.
    */
   private final int DEFAULT_NORTH_HEIGHT;
   
   /**
    * Specifies the default height of the southern region of the panel 
    * that holds the contour image.  In other words, this value can be 
    * thought of as the default bottom margin below the contour image.
    */
   private final int DEFAULT_SOUTH_HEIGHT;
   
   /**
    * Specifies the default width of the eastern region of the panel 
    * that holds the contour image.  In other words, this value can be 
    * thought of as the default right margin beside the contour image.
    */
   private final int DEFAULT_EAST_WIDTH;
   
   /**
    * Specifies the default width of the western region of the panel that 
    * holds the contour image.  In other words, this value can be 
    * thought of as the default left margin beside the contour image.
    */
   private final int DEFAULT_WEST_WIDTH;
//-------------------------=[ End constants ]=--------------------------------//
   
   
//--------------------------------=[ Fields ]=--------------------------------//
   /**
    * This is a panel that is divided into north, south, east, west, 
    * and central sections.  The <code>ContourJPanel</code> that does 
    * the actual work of rendering the contour image is placed in 
    * the central region of this panel.  The other regions then 
    * expand to accomidate the axes that are placed over the contour 
    * image.
    */
   private PartitionedPanel innerPanel;
   
   /**
    * This is a panel that is divided into north, south, east, west, 
    * and central sections.  The panel <code>innerPanel</code> is 
    * placed at the center of this panel.  Then the controls for 
    * the colorscale are placed in either the east or south sections 
    * of this panel as specified by the user.   That is, to make the 
    * controls appear to be located to the right side or below the 
    * contour image, the controls are added to their respective 
    * sections of this panel.
    */
   private PartitionedPanel partitionPanel;
   
   /**
    * This is the "display panel."  That is, it is the root panel 
    * of every other panel and transparency.  It contains 
    * the panel <code>partitionPanel</code> and all of the 
    * transparencies.  These panels contain their respective 
    * components, etc.  Thus, in the end, this panel contains 
    * every graphical element that this module maintains.
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
   
   /**
    * The <code>ContourViewComponent</code> that this module is 
    * associated with.
    */
   private ContourViewComponent contourComp;
//------------------------------=[ End fields ]=------------------------------//
   
   
//--------------=[ Constructors and construction methods ]=-------------------//
   /**
    * Constructs a module for a 
    * {@link ContourViewComponent ContourViewComponent} that handles 
    * displaying the view component's contour plot.
    * 
    * @param connector          Serves to connect several modules of a 
    *                           {@link ContourViewComponent 
    *                           ContourViewComponent} so that if a 
    *                           property in one module is changed, the 
    *                           other modules are notified.
    * @param center             Serves as the central location where the 
    *                           data shared between several modules of a 
    *                           {@link ContourViewComponent 
    *                           ContourViewComponent} is stored.
    * @param panel              The panel that is responsible for 
    *                           rendering the contour plot.
    * @param component          The view component that this module is 
    *                           to maintain.
    * @param v2D                The data that is to be displayed by 
    *                           this module.
    */
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
   
   /**
    * This method instantiates and initializes every field in this class 
    * to their default starting values.
    * 
    * @param contourComp The <code>ViewComponent</code> that this 
    *                    module is to maintain.
    */
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
          this.innerPanel.setPartitionComponent(PartitionedPanel.CENTER, 
                                                contourPanel);
        this.partitionPanel = new PartitionedPanel(0, 0, 0, 0);
         this. partitionPanel.setPartitionComponent(PartitionedPanel.CENTER, 
                                                    this.innerPanel);
         
      this.transparencies = new Vector();
      this.listenerVec = new Vector();
   }
//------------=[ End constructors and construction methods ]=-----------------//
   
   
//--------=[ Methods implemented for the ContourChangeHandler class ]=--------//
   /**
    * This method is implemented for the <code>ContourChangeHandler</code> 
    * class.  This method is invoked when the data that is to be 
    * displayed by the contour plot has changed.  When invoked, 
    * this method updates the display to reflect this new data.
    * 
    * @param v2D The new data that is going to be plotted.
    * 
    * @see ContourChangeHandler#reinit(IVirtualArray2D)
    */
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
                                       // Default tick marks inward for contour 
         AxisOverlay2D axes = new AxisOverlay2D(this,true);
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
   
   /**
    * This method is implemented for the <code>ContourChangeHandler</code> 
    * class.  This method is invoked whenever the display has changed 
    * (i.e. become inconsistent) and needs to be updated.  When invoked, 
    * this method updates the contour plot to reflect the change.
    * 
    * @see ContourChangeHandler#changeDisplay()
    */
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
      this.partitionPanel.revalidate();
      this.partitionPanel.repaint();
      
      this.innerPanel.revalidate();
      this.innerPanel.repaint();
      
      Component topPanel = this.layerPanel;
      while ( topPanel.getParent() != null)
         topPanel = topPanel.getParent();
      topPanel.repaint();
   }
   
   /**
    * This method is implemented for the <code>ContourChangeHandler</code> 
    * class.  If the colorscale being used to color the contour plot 
    * changes, this method is invoked.  When it is invoked, the contour 
    * plot is updated to relect the change.
    * 
    * @param colorscale The name of the colorscale that will now be 
    *                   used to color the contour plot.
    * 
    * @see ContourChangeHandler#changeColorScaleName(String)
    */
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
   
   /**
    * This method is implemented for the <code>ContourChangeHandler</code> 
    * class.  If the color being used to color the contour plot changes, 
    * this method is invoked.  When it is invoked, the contour plot is 
    * updated to reflect the change.
    * 
    * @param color The color that is now going to be used to color the 
    *              contour plot.
    * 
    * @see ContourChangeHandler#changeColor(Color)
    */
   public void changeColor(Color color)
   {
      getContourPanel().setColorScale(color);
      displayChanged();
   }
   
   /**
    * This method is implemented for the <code>ContourChangeHandler</code> 
    * class.  If the colorscale being used to color the contour plot is 
    * modified to be/not be "double-sided", this method is invoked.  
    * When invoked, the contour plot is updated to reflect the change.
    * 
    * @param isDoubleSided <code>True</code> if the colorscale used to 
    *                      color the contour plot is now double-sided and 
    *                      <code>false</code> if it isn't.
    * 
    * @see ContourChangeHandler#changeIsDoubleSided(boolean)
    */
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
   
   /**
    * This method is implemented for the <code>ContourChangeHandler</code> 
    * class.  This method is invoked if the location of the colorscale's 
    * control changes.  When invoked, this method updates the layout 
    * so that the colorscale's control is in the correct location.
    * 
    * @param location A string alias describing the new location of the 
    *                 colorscale's control.  The value of this parameter 
    *                 should be either:
    * <ul>
    *   <li>{@link ContourChangeHandler#CONTROL_PANEL_LOCATION 
    *                                   CONTROL_PANEL_LOCATION}</li>
    *   <li>{@link ContourChangeHandler#BELOW_IMAGE_LOCATION 
    *                                   BELOW_IMAGE_LOCATION}</li>
    *   <li>{@link ContourChangeHandler#RIGHT_IMAGE_LOCATION 
    *                                   RIGHT_IMAGE_LOCATION}</li>
    *   <li>{@link ContourChangeHandler#NONE_LOCATION 
    *                                   NONE_LOCATION}</li>
    * </ul>
    * 
    * @see ContourChangeHandler#changeColorScaleLocation(String)
    */
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
   
   /**
    * This method is implemented for the <code>ContourChangeHandler</code> 
    * class.  If the intensity on the colorscale used to color the contour 
    * plot changes, this method is invoked.  When invoked, the contour plot 
    * is updated to reflect the change.
    * 
    * @param intensity The intensity of the colorscale that is now going 
    *                  to be used to render the contour plot.
    * 
    * @see ContourChangeHandler#changeIntensity(double)
    */
   public void changeIntensity(double intensity)
   {
      getContourPanel().setLogScale(intensity);
      ControlColorScale scale = getVisibleColorScale();
      if (scale != null)
         scale.setLogScale(intensity);
      displayChanged();
   }
   
   /**
    * This method is implemented for the <code>ContourChangeHandler</code> 
    * class.  If the state of whether or not the aspect ratio should be 
    * preserved when rendering the contour plot, this method is invoked.  
    * When invoked, the layout of the display is changed so that the 
    * contour plot has its apsect ratio either preserved or not preserved 
    * (as specified).
    * 
    * @param preserve <code>True</code> if the aspect ratio should 
    *                 be preserved and <code>false</code> if it shouldn't.
    * 
    * @see ContourChangeHandler#changeAspectRatio(boolean)
    */
   public void changeAspectRatio(boolean preserve)
   {
      setPreserveAspectRatio(preserve);
      displayChanged();
   }
//------=[ End methods implemented for the ContourChangeHandler class ]=------//
   
   
// ------=[ Methods implemented for the InformationHandler interface ]=--------//
   /**
    * This method is implemented for the 
    * {@link InformationHandler InformationHandler} interface.  Given a 
    * certain string alias, the data referenced by that alias would normally 
    * be returned.  However, this class does not maintain any data.  Thus, 
    * <code>null</code> is always returned.
    * 
    * @param key The string alias for some particular data.
    * @return    <code>null</code>.
    * 
    * @see InformationHandler#getValue(String)
    */
   public Object getValue(String key)
   {
      //this class isn't registered to maintain any values
      return null;
   }
//----=[ End methods implemented for the InformationHandler interface ]=------//
   
   
//-----------=[ Methods implemented for the IPreserveState interface ]=-------//
   /**
    * Used to set the state information of this object to match the state 
    * information encapsulated in the <code>ObjectStage</code> parameter 
    * given.
    * 
    * @param state An encapsulation of this Object's state.
    */
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

   /**
    * Used to get an encapsulation of this Object's state information.
    * 
    * @param is_default If <code>true</code>, this Object's default state 
    *                   is returned.  Otherwise, its current state is 
    *                   returned.
    * 
    * @return An encapsulation of this Object's state.
    */
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
   /**
    * This method is used by the axis overlays to get an encapsulation of 
    * the information describing one of the coordinate axes of the display.
    * 
    * @param axiscode A code representing either the X, Y, or Z axis.
    * 
    * @return Information about one of the coordinate axes of the display.
    * 
    * @see AxisInfo#X_AXIS
    * @see AxisInfo#Y_AXIS
    * @see AxisInfo#Z_AXIS
    */
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

   /**
    * This method is used by the axis overlays to get the title to 
    * display above the data plotted.
    * 
    * @return The title to display above the data.
    */
   public String getTitle()
   {
      return getContourPanel().getData().getTitle();
   }

   /**
    * This method is used by the axis overlays to determine the 
    * precision of the data as specified by the user.
    * 
    * @return The precision of the data displayed.
    */
   public int getPrecision()
   {
      Integer num = (Integer)getInfoCenter().
         obtainValue(ContourControlHandler.NUM_SIG_FIGS_INFO_KEY);
      int numSigFigs = ContourControlHandler.DEFAULT_NUM_SIG_DIGS;
      if (num != null)
         numSigFigs = num.intValue();
      
      return numSigFigs;
   }

   /**
    * This method is used by the axis overlays to determine the 
    * font to use when rendering the axis labels.
    * 
    * @return This method currently always returns 
    *         {@link FontUtil#LABEL_FONT2 FontUtil.LABEL_FONT2}.
    */
   public Font getFont()
   {
      //TODO Make the font adjustable
      return FontUtil.LABEL_FONT2;
   }

   /**
    * This method is used by any overlay that responds to zooming.  
    * It is used to get the coordinate bounds of the current 
    * zoomed region.
    * 
    * @return The coordinate bounds of the current zoomed region.
    */
   public CoordBounds getLocalCoordBounds()
   {
      return getContourPanel().getLocalWorldCoords().MakeCopy();
   }

   /**
    * This method is used by any overlay that responds to zooming.  
    * It is used to get the coordinate bounds of the entire 
    * image that has been plotted.
    * 
    * @return The coordinate bounds of the entire image.
    */
   public CoordBounds getGlobalCoordBounds()
   {
      return getContourPanel().getGlobalWorldCoords().MakeCopy();
   }

   /**
    * This method can be used by any overlay.  It is used to get 
    * a rectangle describing the size and location of only 
    * the contour image itself.  That is, the rectangle describing 
    * the contour image and all of the axes and other overlays 
    * is not returned.
    * 
    * @return An encapsulation of the size and location of the 
    *         contour image.
    */
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
   /**
    * This method is implemented for the 
    * {@link IColorScaleAddible IColorScaleAddible} interface.  
    * It is used to access the colorscale that is currently being 
    * used to color the contour plot.
    * 
    * @return The colorscale that is used to color the contour plot.
    */
   public String getColorScale()
   {
      String colorscale = 
         (String)getInfoCenter().
            obtainValue(ContourControlHandler.COLORSCALE_NAME_INFO_KEY);
      if (colorscale == null)
         colorscale = ContourControlHandler.DEFAULT_COLOR_SCALE;
      
      return colorscale;
   }

   /**
    * This method is implemented for the 
    * {@link IColorScaleAddible IColorScaleAddible} interface.  
    * It is used to add listeners to this module.
    * 
    * @param actionlistener The object that wants to listen to changes 
    *                       to this module.
    */
   public void addActionListener(ActionListener actionlistener)
   {
      if (actionlistener==null)
         return;
      if (!listenerVec.contains(actionlistener))
         listenerVec.add(actionlistener);
   }

   /**
    * This method is implemented for the 
    * {@link IColorScaleAddible IColorScaleAddible} interface.  
    * It is used to access the axis info about the Z axis 
    * of the contour plot.
    * 
    * @return An encapsulation of information about the Z axis.
    */
   public AxisInfo getValueAxisInfo()
   {
      return getAxisInformation(AxisInfo.Z_AXIS);
   }

   /**
    * This method is implemented for the 
    * {@link gov.anl.ipns.ViewTools.Components.Transparency.ILogAxisAddible 
    * ILogAxisAddible} interface.  It is used to access the scaling 
    * factor that is applied to the colorscale that is used to color the 
    * contour plot.
    * 
    * @return The scaling factor that adjusts the colorscale that is 
    *         used to color the contour plot.
    */
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
   /**
    * Used to access the transparency of this module that is its 
    * <code>AxisOverlay2D</code>.
    * 
    * @return The transparency that displays the axes over the 
    *         contour plot.
    */
   public AxisOverlay2D getAxisOverlay()
   {
      return (AxisOverlay2D)transparencies.get(0);
   }
   
   /**
    * Used to access the root panel that contains all of this module's 
    * components.
    * 
    * @return The panel that contains all of the graphical elements 
    *         that this module maintains (in their current state).
    */
   public JPanel getDisplayPanel()
   {
      return this.layerPanel;
   }
   
   /**
    * Used to set if the aspect ratio should be preserved when 
    * rendering the contour image.
    * 
    * @param preserve <code>True</code> if the aspect ratio should be 
    *                 preserved when rendering the contour image and 
    *                 <code>false</code> if it should not be preserved.
    */
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
   /**
    * This method is used to remove the given listener from the list of 
    * listeners that are notified of changes to this module.
    * 
    * @param act_listener The object that does not want to listen to 
    *                     this module anymore.
    */
   public void removeActionListener(ActionListener act_listener)
   {
      if (act_listener==null)
         return;
      listenerVec.remove(act_listener);
   }

   /**
    * This method is used to remove all of the listeners from the list 
    * of listeners that are notified of changes to this module.
    */
   public void removeAllActionListeners()
   {
      listenerVec.clear();
   }
   //-------------------=[ End for the listeners ]=---------------------------//
   
   /**
    * This method should be invoked when this display is not needed 
    * anymore.  This method then invokes the <code>kill()</code> 
    * method on every one of this module's transparencies 
    * (which are of the type {@link OverlayJPanel OverlayJPanel}).
    */
   public void kill()
   {
      for (int i=0; i<this.transparencies.size(); i++)
         ((OverlayJPanel)this.transparencies.elementAt(i)).kill();
   }
//--------------------------=[ Extra methods ]=-------------------------------//
   
   
//--------------------=[ Private methods ]=-----------------------------------//
   /**
    * Convience method that sends the given message to all 
    * of this class's <code>ActionListeners</code>.
    * 
    * @param message The message component of the <code>ActionEvent</code> 
    *                that is sent to all of this class's 
    *                <code>ActionListeners</code>.  The 
    *                <code>ActionEvent's</code> id is set to 0 and 
    *                its source is set to this module's 
    *                <code>ContourViewComponent</code>.
    */
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
   
   /**
    * Used to access the control for the colorscale that is displayed 
    * by this module.
    * 
    * @return The colorscale's control that is displayed by this 
    *         module or <code>null</code> if it currently not 
    *         being displayed.
    */
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
   
   /**
    * Used to access the inner panel of 
    * {@link #partitionPanel partitionPanel}.  The inner panel 
    * contains the <code>ContourJPanel</code> that displays the 
    * contour plot.
    * 
    * @return The panel in the central section of the panel 
    *         {@link #partitionPanel partitionPanel}.
    */
   private PartitionedPanel getInnerPanel()
   {
      //extract the PartitionedPanel at the center of 'this.partitionPanel'
      return (PartitionedPanel)(this.partitionPanel.
                getPartitionComponent(PartitionedPanel.CENTER));
   }
   
   //gets the ControlColorScale located in this.partitionPanel's 
   //specified partition
   /**
    * Used to access the colorscale control located in the given 
    * section of the panel {@link #partitionPanel partitionPanel}.
    * 
    * @param partition A code specifying a section of the panel 
    *                  {@link #partitionPanel partitionPanel}.
    * 
    * @return The colorscale control located at the given section of the 
    *         panel {@link #partitionPanel partitionPanel} or 
    *         <code>null</code> if no colorscale control is located in 
    *         the specified section.
    * 
    * @see PartitionedPanel#NORTH
    * @see PartitionedPanel#SOUTH
    * @see PartitionedPanel#EAST
    * @see PartitionedPanel#WEST
    * @see PartitionedPanel#CENTER
    */
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
   /**
    * This class is used to listen to this module's 
    * <code>ContourJPanel</code> when it is resized.
    */
   private class ResizeListener extends ComponentAdapter
   {
      /**
       * Invoked when this module's <code>ContourJPanel</code> 
       * is resized, at which time the display is updated.
       */
      public void componentResized(ComponentEvent event)
      {
         displayChanged();
      }
   }

   /**
    * This class is used to listen to 
    * {@link CoordJPanel#CURSOR_MOVED CURSOR_MOVED} 
    * messages that are sent to this module's 
    * <code>ContourJPanel</code>.  When such a message is 
    * recieved, a 
    * {@link IViewComponent#POINTED_AT_CHANGED POINTED_AT_CHANGED} 
    * message is sent to all listeners.  This allows the 
    * cursor movements in one view component to be synchronized 
    * with the movement of the cursor in another view component.
    */
   private class CursorListener implements ActionListener
   {
      /**
       * Invoked when this module's <code>ContourJPanel</code> 
       * recieves a message.
       */
      public void actionPerformed(ActionEvent event)
      {
         if ( event.getActionCommand().equals(CoordJPanel.CURSOR_MOVED) )
            if (!getContourPanel().isDoingBox())
               sendMessage(IViewComponent2D.POINTED_AT_CHANGED);
      }
   }
   
   /**
    * This class is used to listen to this module's 
    * <code>ContourJPanel</code> for changes to the 
    * zoomed region of the panel.
    */
   private class ContourPanelListener implements ActionListener
   {
      /**
       * If a region of this module's <code>ContourJPanel</code> is 
       * zoomed or if the zoom is reset, this method updates the 
       * display.
       */
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
   /**
    * This is a special panel that is divided into five sections:  north, 
    * south, east, west, and central.  Components can be added to each 
    * section of this panel and methods are provided to ensure the 
    * aspect ratio of the center panel is preserved.  This class was 
    * designed in this way because it was intended to be used with a 
    * {@link ContourLayoutHandler ContourLayoutHandler}.  That is, the 
    * layout handler would place a contour plot in the central section of 
    * an object of this class.  This class would in turn be used to 
    * preserve the aspect ratio of the contour image.
    */
   private static class PartitionedPanel extends JPanel
   {
      /** The constant that specifies the northern section of this panel. */
      public static final int NORTH = 0;
      /** The constant that specifies the southern section of this panel. */
      public static final int SOUTH = 1;
      /** The constant that specifies the eastern section of this panel. */
      public static final int EAST = 2;
      /** The constant that specifies the western section of this panel. */
      public static final int WEST = 3;
      /** The constant that specifies the central section of this panel. */
      public static final int CENTER = 4;
      
      /** The panel that is the northern section of this panel. */
      private JPanel northPanel;
      /** The panel that is the southern section of this panel. */
      private JPanel southPanel;
      /** The panel that is the eastern section of this panel. */
      private JPanel eastPanel;
      /** The panel that is the western section of this panel. */
      private JPanel westPanel;
      /** The panel that is the central section of this panel. */
      private JPanel centerPanel;
      
      /**
       * Constructs a panel with a northern, southern, eastern, western, 
       * and central section.  The contents and sizes of these sections 
       * are not initialized.
       */
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
      
      /**
       * Constructs a panel with a northern, southern, eastern, western, 
       * and central section with their sizes adjusted to the sizes 
       * specified.  That is, the panel can be thought of as a document 
       * with margins.  The parameters given specify the sizes of the 
       * margins.  The numbers can also be interpreted as the height of 
       * the northern and southern sections and width of the eastern and 
       * western sections.  The size of the central section is 
       * automatically adjusted to meet the requirements for the sizes of 
       * the other sections. 
       * 
       * @param northHeight The height in pixels of the northern section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's top margin.
       * @param southHeight The height in pixels of the southern section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's bottom margin.
       * @param eastWidth   The width in pixels of the eastern section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's right margin.
       * @param westWidth   The width in pixels of the western section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's left margin.
       */
      public PartitionedPanel(int northHeight, int southHeight, 
                              int eastWidth, int westWidth)
      {
         this();
         adjustPreferredSizes(northHeight, southHeight, eastWidth, westWidth);
      }
      
      /**
       * Used to get a rectangle describing the region encompassed by 
       * the section on this panel as specified by the given integer 
       * code.  The rectangle returned encapsulates the location of the 
       * region as well as its size.
       * 
       * @param partition A code that specifies the section of this 
       *                  panel to use.
       * @return          An encapsulation of the location and size of 
       *                  the specified section of this panel.
       */
      public Rectangle getPartitionRegion(int partition)
      {
         JPanel panel = getPanelInPartition(partition);
         return new Rectangle(panel.getLocation(), 
                              panel.getSize());
      }
      
      /**
       * Sets the given component as the component that is located in the 
       * section of this panel as specified by its integer code.
       * 
       * @param partition A code that specifies the section of this 
       *                  panel to use.
       * 
       * @param comp      The component that is to be located at the 
       *                  specified section on this panel.
       * 
       * @see #NORTH
       * @see #SOUTH
       * @see #EAST
       * @see #WEST
       * @see #CENTER
       */
      public void setPartitionComponent(int partition, Component comp)
      {
         JPanel panel = getPanelInPartition(partition);
           panel.removeAll();
           panel.add(comp);
           
         //revalidate();
         //repaint();
      }
      
      /**
       * Used to get the panel that is located at the specified 
       * section of this panel.
       * 
       * @param partition A code that specifies the section of this 
       *                  panel to use.
       * 
       * @return The component at the specified section of this 
       *         panel or <code>null</code> if no components 
       *         exist in the specified section.  If 
       *         <code>partition</code> is invalid, the component 
       *         in the central section is returned.
       */
      public Component getPartitionComponent(int partition)
      {
         JPanel panel = getPanelInPartition(partition);
         if (0 < panel.getComponentCount())
            return panel.getComponent(0);
         else
            return null;
      }
      
      /**
       * Used to remove the component from the section on this panel 
       * as specified by the integer code given.
       * 
       * @param partition A code that specifies the section of this 
       *                  panel to use.
       */
      public void removePartitionComponent(int partition)//, 
                                           //int northHeight, int southHeight, 
                                           //int eastWidth, int westWidth)
      {
         JPanel panel = getPanelInPartition(partition);
           panel.removeAll();
         
         //panel.revalidate();
         //panel.repaint();
      }
      
      /**
       * Because this class is tailor made to work with a 
       * {@link ContourLayoutHandler ContourLayoutHandler} this method is 
       * designed to work with a contour image.  That is given information 
       * about the contour image, the central region of this panel will 
       * be adjusted so that the aspect ratio of the contour image is 
       * preserved.
       * 
       * @param numRows The number of rows in the contour image's 
       *                {@link gov.anl.ipns.ViewTools.Components.IVirtualArray 
       *                IVirtualArray} that are currently being displayed on 
       *                the screen.  That is, if the user has selected to 
       *                zoom in on a region of the contour image, how many 
       *                rows are being displayed.
       * @param numCols The number of columns in the contour image's 
       *                {@link gov.anl.ipns.ViewTools.Components.IVirtualArray 
       *                IVirtualArray} that are currently being displayed on 
       *                the screen.  That is, if the user has selected to 
       *                zoom in on a region of the contour image, how many 
       *                columns are being displayed.
       * @param maxRows The total number of rows in the contour image's 
       *                {@link gov.anl.ipns.ViewTools.Components.IVirtualArray 
       *                IVirtualArray}.
       * @param maxCols The total number of columns in the contour image's 
       *                {@link gov.anl.ipns.ViewTools.Components.IVirtualArray 
       *                IVirtualArray}.
       */
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
      
      /**
       * Disables the preservation of the aspect ratio of the central 
       * region of this panel.  Instead, the sizes of the northern, 
       * southern, eastern, and western sections are modified to the 
       * sizes given.  As a result, the size of the central region is 
       * automatically adjusted to correspond to these changes.
       * 
       * @param northHeight The height in pixels of the northern section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's top margin.
       * @param southHeight The height in pixels of the southern section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's bottom margin.
       * @param eastWidth   The width in pixels of the eastern section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's right margin.
       * @param westWidth   The width in pixels of the western section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's left margin.
       */
      public void disablePreserveAspectRatio(int northHeight, int southHeight, 
                                             int eastWidth, int westWidth)
      {
         adjustPreferredSizes(northHeight, southHeight, eastWidth, westWidth);
      }
      
      /**
       * Used to adjust the sizes of the northern, southern, eastern, and 
       * western sections to the specified sizes.  The size of the 
       * central region is automatically adjusted to accomidate these 
       * changes.
       * 
       * @param northHeight The height in pixels of the northern section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's top margin.
       * @param southHeight The height in pixels of the southern section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's bottom margin.
       * @param eastWidth   The width in pixels of the eastern section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's right margin.
       * @param westWidth   The width in pixels of the western section of 
       *                    the panel.  This can also be thought of as the 
       *                    panel's left margin.
       */
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
      
      /**
       * Given the integer code for a section of this panel, this method 
       * obtains the panel that is that section.
       * 
       * @param location An integer code that specifies a section of 
       *                 this panel.
       * 
       * @return The panel that is the specified section of this panel.
       * 
       * @see #NORTH
       * @see #SOUTH
       * @see #EAST
       * @see #WEST
       * @see #CENTER
       */
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
