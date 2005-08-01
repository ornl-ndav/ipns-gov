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
 * Revision 1.5  2005/08/01 23:13:27  kramer
 * -Modified the setObjectState() method to set the ObjectState of the
 *  ContourControlHandler after setting the ObjectState of the
 *  ContourLayoutHandler (instead of before it).
 * -Added getter methods for the ContourMenuHandler, ContourControlHandler,
 *  ContourLayoutHandler, and ContourColorScaleHandler.
 *
 * Revision 1.4  2005/07/29 15:38:01  kramer
 *
 * Fixed the typo in setObjectState() where the ContourMenuHandler's
 * ObjectState was mistakenly called on the ContourLayoutHandler's
 * setObjectState() method.
 *
 * Revision 1.3  2005/07/28 23:06:29  kramer
 *
 * There was a problem where the ContourJPanel's and PanViewControl's
 * displays were not in sync.  Some commented code that failed to solve this
 * problem was removed from this class.  Instead the problem appears to have
 * been solved by modifying the ContourJPanel class.
 *
 * Revision 1.2  2005/07/28 15:41:53  kramer
 *
 * The ContourControlHandler has support for a PanViewControl.  Currently,
 * however, sometimes when the ContourViewComponent is made, the thumbnail
 * image or the large display is not displayed.  There are a couple
 * attempts in the code to fix this problem (but none completely work yet).
 *
 * Revision 1.1  2005/07/25 20:55:04  kramer
 *
 * Initial checkin.  This is the redesigned ContourViewComponent that has
 * its work divided between several modules (the ContourControlHandler,
 * ContourMenuHandler, ContourColorScaleHandler, and ContourLayoutHandler)
 * which work with the components ViewControls, ViewMenus, state of the
 * colorscale used, and ContourJPanel and overlays.
 *
 * Revision 1.14  2005/07/20 17:24:46  kramer
 *
 * Added javadocs for all of the fields.  Removed the redundant field
 * DEFAULT_CONTOUR_COLOR (it repeats DEFAULT_LINE_COLOR).  Updated the
 * method setPreserveAspectRatio() to call setPreserveAspectRatio() on the
 * ContourJPanel.
 *
 * Revision 1.13  2005/07/19 19:02:33  kramer
 *
 * -Reorganized the code
 * -Now the ObjectState works so that the
 *  state of the viewer can be stored to
 *  and read from an ObjectState.
 * -The intensity control now works.
 * -Added an inner class ColorOrColorScale
 *  to maintain the color or colorscale
 *  that is currently being used to color
 *  the contour lines.
 * -Currently, when the contour lines are
 *  colored with a solid color, the
 *  ControlColorScale displays the
 *  colorscale that was last used
 *  to color the contour lines.
 *
 * Revision 1.12  2005/07/12 17:08:57  kramer
 *
 * Added support to save the state of the ContourJPanel in the state
 * information for this class.  Added an ActionListener to this class's
 * ContourJPanel so that viewer 'pointed at' synchronization would work.
 * Removed the isChecked() method.
 *
 * Revision 1.11  2005/06/28 22:23:19  kramer
 *
 * Changed some of the controls to ViewMenuItems.  Now there are menu items
 * for setting the aspect ratio, contour line color or colorscale, and
 * setting if the colorscale is double sided or not.  Also, added a
 * control to change the background color (this is useful because with some
 * colorscales some contour levels are hard to see).  The code for some of
 * the old controls has commented out.
 *
 * Revision 1.10  2005/06/28 16:09:36  kramer
 *
 * Added support for modifying the background color or contour line color,
 * and for specifying the color scale (and intensity) used to color the
 * contour lines.  Currently, the latter is incomplete and the controls do
 * not appear on the GUI.
 *
 * Revision 1.9  2005/06/23 21:02:52  kramer
 *
 * Made this class now use the ControlCheckboxSpinner and
 * ControlCheckboxCombobox ViewControls as the controls for the
 * contour labels and label formatting.
 *
 * Revision 1.8  2005/06/22 22:16:37  kramer
 *
 * Added javadocs and rearranged the code to make it easier to read.
 *
 * Modified the default values specified in this class so that this class,
 * the CompositeContourControl class, and the ContourJPanel class all
 * have their defaults linked.
 *
 * Added controls to allow the user to enable/disable labels and label
 * formatting.  Added controls to allow the user to enable/disable
 * certain lines with their styles.
 *
 * Revision 1.7  2005/06/17 19:33:26  kramer
 *
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
package gov.anl.ipns.ViewTools.Components.TwoD.Contour;

import gov.anl.ipns.Util.Messaging.Information.InformationCenter;
import gov.anl.ipns.Util.Messaging.Property.PropertyChangeConnector;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Layouts.ComponentViewManager;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This class is used to create a contour plot of a three dimensional set 
 * of data.  The following features are supported:
 * <ul>
 *   <li> Uniformly spaced contour levels </li>
 *   <li> Manually entered contour levels </li>
 *   <li> Contour labels </li>
 *   <li> Formatting labels to a specified number of significant figures </li>
 *   <li> Contour line styles (solid, dashed, dashed-dotted, or dotted).
 * </ul>
 */
public class ContourViewComponent implements IViewComponent2D, Serializable
{
//------------------------=[ ObjectState keys ]=------------------------------//
   public static final String MENU_HANDLER_KEY = "Menu handler key";
   public static final String CONTROL_HANDLER_KEY = "Controls handler key";
   public static final String LAYOUT_HANDLER_KEY = "Layout handler key";
   public static final String COLORSCALE_HANDLER_KEY = "Colorscale handler key";
//----------------------=[ End ObjectState keys ]=----------------------------//

   
//----------------------------=[ Fields ]=------------------------------------//
   private ContourMenuHandler menuHandler;
   private ContourControlHandler controlHandler;
   private ContourLayoutHandler layoutHandler;
   private ContourColorScaleHandler colorscaleHandler;
//--------------------------=[ End fields ]=----------------------------------//
  
   
//-------------------------=[ Constructors ]=---------------------------------//
   private ContourViewComponent(IVirtualArray2D v2D, 
                                ContourJPanel contourPanel, 
                                boolean useManualLevels, 
                                boolean useColorScale)
   {
      PropertyChangeConnector connector = new PropertyChangeConnector();
      InformationCenter center = new InformationCenter();
      
      //first, build the menu items
        menuHandler = new ContourMenuHandler(connector, 
                                             center, 
                                             contourPanel, 
                                             useColorScale);

      //and the layout
        layoutHandler = new ContourLayoutHandler(connector, 
                                                 center, 
                                                 contourPanel, 
                                                 this, 
                                                 v2D);
        
      //and build the controls
        controlHandler = new ContourControlHandler(connector, 
                                                   center, 
                                                   contourPanel, 
                                                   useManualLevels);
        
      //and make the object that will record if a solid color or 
      //colorscale name is currently being used to color the coontour lines
        colorscaleHandler = 
           new ContourColorScaleHandler(connector, 
                                        center, 
                                        contourPanel, 
                                        useColorScale, 
                                        ContourChangeHandler.
                                           CONTROL_PANEL_LOCATION);
      
      
      //because the ContourMenuHandler is made first, the color it specifies 
      //for the contour lines is overriden by the other modules (the 
      //control and layout handlers).  So restore the menu handler's 
      //specification
        menuHandler.setLineColor(menuHandler.getLineColor());
        
      dataChanged(v2D);
   }
   
   public ContourViewComponent(IVirtualArray2D arr)
   {
      this(arr, 
           new ContourJPanel(arr, 
                             ContourControlHandler.DEFAULT_LOWEST_CONTOUR, 
                             ContourControlHandler.DEFAULT_HIGHEST_CONTOUR, 
                             ContourControlHandler.DEFAULT_NUM_CONTOURS), 
           false, //don't display the manual levels (display the uniform ones)
           false);//don't use a colorscale for coloring the contour lines 
                  //(use the default solid color)
   }
   
   /*
   public ContourViewComponent(IVirtualArray2D arr, 
                               float minValue, float maxValue, int numLevels)
   {
      this(arr, 
           new ContourJPanel(arr,minValue,maxValue,numLevels), 
           false, //don't display the manual levels (display the uniform ones)
           false);//don't use a colorscale for coloring the contour lines 
                  //(use the default solid color)
   }
   
   public ContourViewComponent(IVirtualArray2D arr, 
                               float[] levels)
   {
      this(arr, 
           new ContourJPanel(arr,levels), 
           true,  //display the manual levels (don't display the uniform ones)
           false); //don't use a colorscale for coloring the contour lines 
                   //(use the default solid color)
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
   */
//-------------------------=[ End constructors ]=-----------------------------//
   
   
//---------=[ Methods mplemented for the IViewComponent2D interface ]=--------//
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      
      Object val = state.get(MENU_HANDLER_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         this.menuHandler.setObjectState((ObjectState)val);
      
      val = state.get(LAYOUT_HANDLER_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         this.layoutHandler.setObjectState((ObjectState)val);
      
      val = state.get(CONTROL_HANDLER_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         this.controlHandler.setObjectState((ObjectState)val);
      
      val = state.get(COLORSCALE_HANDLER_KEY);
      if ( (val != null) && (val instanceof ObjectState) )
         this.colorscaleHandler.setObjectState((ObjectState)val);
      
      layoutHandler.displayChanged();
   }

   public ObjectState getObjectState(boolean is_default)
   {
      ObjectState state = new ObjectState();
        state.insert(MENU_HANDLER_KEY, 
                     this.menuHandler.getObjectState(is_default));
        state.insert(CONTROL_HANDLER_KEY, 
                     this.controlHandler.getObjectState(is_default));
        state.insert(LAYOUT_HANDLER_KEY, 
                     this.layoutHandler.getObjectState(is_default));
        state.insert(COLORSCALE_HANDLER_KEY, 
                     this.colorscaleHandler.getObjectState(is_default));
      return state;
   }

   public void setPointedAt(floatPoint2D fpt)
   {
      if (fpt==null)
         return;
      
      layoutHandler.getContourPanel().set_crosshair_WC(fpt);
   }

   public floatPoint2D getPointedAt()
   {
      return new floatPoint2D(layoutHandler.getContourPanel().
                                 getCurrent_WC_point());
   }

   public void setSelectedRegions(Region[] rgn)
   {
   }

   public Region[] getSelectedRegions()
   {
      return new Region[0];
   }

   public void dataChanged(IVirtualArray2D v2D)
   {
      menuHandler.reinit(v2D);
      controlHandler.reinit(v2D);
      layoutHandler.reinit(v2D);
      
      layoutHandler.changeDisplay();
      layoutHandler.displayChanged();
   }

   public void dataChanged()
   {
      layoutHandler.changeDisplay();
   }

   public void addActionListener(ActionListener act_listener)
   {
      layoutHandler.addActionListener(act_listener);
   }

   public void removeActionListener(ActionListener act_listener)
   {
      layoutHandler.removeActionListener(act_listener);
   }

   public void removeAllActionListeners()
   {
      layoutHandler.removeAllActionListeners();
   }

   public JPanel getDisplayPanel()
   {
      return layoutHandler.getDisplayPanel();
   }

   public ViewControl[] getControls()
   {
      return controlHandler.getControls();
   }

   public ViewMenuItem[] getMenuItems()
   {
      return menuHandler.getMenuItems();
   }

   public void kill()
   {
      layoutHandler.kill();
   }
//-------=[ End methods mplemented for the IViewComponent2D interface ]=------//
   
   
//------------------------=[ Getter methods ]=--------------------------------//
   public ContourMenuHandler getMenuHandler()
   {
      return this.menuHandler;
   }
   
   public ContourControlHandler getControlHandler()
   {
      return this.controlHandler;
   }
   
   public ContourLayoutHandler getLayoutHandler()
   {
      return this.layoutHandler;
   }
   
   public ContourColorScaleHandler getColorScaleHandler()
   {
      return this.colorscaleHandler;
   }
//----------------------=[ End getter methods ]=------------------------------//
   
   
//----------------=[ Methods used to test this class ]=-----------------------//
   /**
    * Creates a VirtualArray2D of test data that contains a peak.
    * 
    * @param Nx The number of columns in the array.  A good value is 41.
    * @param Ny The number of rows in the array.  A good value is 51.
    * @param xRange The x range.  A good value is 3.0.
    * @param yRange The y range.  A good value is 4.0.
    * 
    * @return A virtual array of test data.
    */
   public static VirtualArray2D getTestData(int Nx, int Ny, 
                                            double xRange, double yRange)
   {
      return new VirtualArray2D(getTestDataArr(Nx, Ny, xRange, yRange));
   }
   
   /**
    * Creates a float array array of test data.
    * @param Nx The number of columns in the array.  A good value is 41.
    * @param Ny The number of rows in the array.  A good value is 51.
    * @param xRange The x range.  A good value is 3.0.
    * @param yRange The y range.  A good value is 4.0.
    * 
    * @return A float[][] containing test data.
    */
   public static float[][] getTestDataArr(int Nx, int Ny, 
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
      
      return dataArray;
   }
   
   /**
    * Testbed.  Displays this view along with its controls and menu items.  
    * This method just invokes <code>ComponentViewManager.main(args)</code>.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      ComponentViewManager.main(args);
      
      JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent event)
           {
              System.exit(0);
           }
        });
      
      JFrame exitFrame = new JFrame("Exit");
        exitFrame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
        exitFrame.getContentPane().add(exitButton);
        exitFrame.pack();
      exitFrame.setVisible(true);
   }
//----------------=[ End methods used to test this class ]=-------------------//
}
