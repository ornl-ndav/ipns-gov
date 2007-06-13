/*
 * File:  DataSetSwapper.java
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
 * $ Log: DataSetSwapper.java,v $
 */

package gov.anl.ipns.ViewTools.Components.ComponentView;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.IViewComponent;
import gov.anl.ipns.ViewTools.Components.IVirtualArray;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.OneD.DataArray1D;
import gov.anl.ipns.ViewTools.Components.OneD.FunctionViewComponent;
import gov.anl.ipns.ViewTools.Components.OneD.VirtualArrayList1D;
import gov.anl.ipns.ViewTools.Components.TwoD.ImageViewComponent;
import gov.anl.ipns.ViewTools.Components.TwoD.TableViewComponent;
import gov.anl.ipns.ViewTools.Components.TwoD.Contour.ContourViewComponent;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Layouts.AbstractComponentSwapper;
import gov.anl.ipns.ViewTools.Layouts.ComponentSwapper2D;
import gov.anl.ipns.ViewTools.Layouts.ComponentViewManager;
//import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
//import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;
import gov.anl.ipns.ViewTools.UI.ActionValueEvent;
import gov.anl.ipns.ViewTools.UI.ActionValueListener;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.FunctionTable;
import DataSetTools.dataset.UniformXScale;

/**
 * This class is used to swap between an image, table, and contour view 
 * of the data encapsulated in a <code>DataSetVirtualArray</code>.  That is, 
 * the raw data from the array is displayed in the image, table, or 
 * contour view.  However, in addition, this swapper also has a view that 
 * displays data as a function.  Specifically, as the user drags the cursor 
 * over the data, the function at the current point is displayed.  Last, 
 * a <code>DataSetVirtualArray</code> has meta-data associated with it.  
 * With this swapper, this meta-data is also displayed in a tabular form.
 */
public class DataSetSwapper extends AbstractComponentSwapper 
                                       implements ComponentListener
{
   /** The data that is currently being viewed. */
   private IVirtualArray2D vArray;
   
   /**
    * The swapper that switches between the image, contour, and table 
    * views of the data.
    */
   private ComponentSwapper2D displaySwapper;
   
   /**
    * The view component that displays a function corresponding to a 
    * slice through the data being viewed.
    */
   private FunctionViewComponent functComp;
   
   /** 
    * A tabular view of the meta-data associated with the 
    * <code>DataSet</code> associated with the 
    * <code>DataSetVirtualArray</code> being viewed.
    */
   //private DataSetStatsControl imageStatsControl;
   
   /**
    * A tabular view of the meta-data associated with a slice through 
    * the <code>DataSetVirtualArray</code> being viewed.
    */
   //private DataSetStatsControl graphStatsControl;
   
   /**
    * A pane split left and right.  The "data panel" of 
    * <code>displaySwapper</code> is placed on the left and the statistic 
    * controls (<code>imageStatsControl</code> and 
    * <code>graphStatsControl</code>) are placed on the right side of the 
    * pane.
    * 
    * @see #mainPane
    */
   private JSplitPane displayPane;
   
   /**
    * A pane split top and bottom.  <code>displayPane</code> is placed on 
    * the top and <code>functComp</code> is placed on the bottom part 
    * of the pane.
    * 
    * @see #displayPane
    */
   private JSplitPane mainPane;
   
   /**
    * With each view of the data (as an image, contour plot, or table), there 
    * is a set of controls used to manipulate the data.  This is a frame 
    * that holds these controls.
    */
   private JFrame controlsFrame;
   
   /**
    * When this button is selected, the frame that holds the current view's 
    * controls is displayed.  When it is de-selected, this frame is made 
    * invisible.
    */
   private JToggleButton showControlsButton;
   
   /**
    * Views the given data initally as an image.
    * 
    * @param vArray The data to view.
    */
   public DataSetSwapper(IVirtualArray2D vArray)
   {
      this(vArray, IMAGE);
   }
   
   /**
    * Constructs a DataSetSwapper object.
    * @param vArray  The data to view.
    * @param viewType A string describing how the data should be viewd.  
    *                 Possible values are: <br>
    *                 <ul>
    *                   <li><code>IComponentSwapper.IMAGE</code></li>
    *                   <li><code>IComponentSwapper.CONTOUR</code></li>
    *                   <li><code>IComponentSwapper.TABLE</code></li>
    *                 </ul>
    */
   public DataSetSwapper(IVirtualArray2D vArray, String viewType)
   {
      super(new String[]{TABLE,IMAGE,CONTOUR}, 
            vArray, 
            viewType);
      
      this.vArray = vArray;
      this.displaySwapper = new ComponentSwapper2D(vArray, 
                                                   viewType, 
                                                   SHOW_ALL);
      
      this.displaySwapper.addActionValueListener(
                             new SwapperMotionListener());
      
      //initializing FunctionViewComponent and setting a graph
      this.functComp = new FunctionViewComponent(new VirtualArrayList1D(
          new DataArray1D(new float[]{},new float[]{})));
      displayRowInFunctionView(0);
      
      this.functComp.addActionListener(new FunctionListener());
      
      /**
       * TODO: (INIT) Make new Stat Control that doesn't use a DataSet
       * and implement it here.
       */
      // construct the stats controls 
      // (for the entire DataSet and the current graph)
      //this.imageStatsControl   = 
      //       new DataSetStatsControl(this.vArray.getDataSet(), 
      //                                "Image Data");
      //this.graphStatsControl = 
      //       new DataSetStatsControl(this.vArray.getDataSet(), 
      //                                "Graph Data");
      
      
      // this will listen to changes to the toggle button that 
      // displays/undisplays the current view component's controls 
      // as well as the frame that holds the controls
      ShowControlsListener controlsListener = new ShowControlsListener();
      
      // this is the JFrame that displays the current view component's 
      // controls
      this.controlsFrame = new JFrame("Controls");
        this.controlsFrame.setVisible(false);
        this.controlsFrame.addWindowListener(controlsListener);
        JPanel pane = new JPanel();
          pane.setLayout(new BoxLayout(pane, 
                                       BoxLayout.Y_AXIS));
        this.controlsFrame.setContentPane(pane);
        
      // this toggle button is used to display/undisplay the window 
      // that displays the current component's controls
      this.showControlsButton = new JToggleButton("View Controls");
        this.showControlsButton.setSelected(false);
        this.showControlsButton.addActionListener(controlsListener);
      
      // construct the panel that holds the DataSet and graph stats
      JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.add(this.showControlsButton);
        
        /**  
         * TODO: Stats controls can be added when it can
         * be implemented correctly (see INIT todo)
         */
        //statsPanel.add(this.imageStatsControl);
        //statsPanel.add(this.graphStatsControl);
        
      // the display pane is a split pane that holds the panel displaying 
      // the data as either an image, contour plot, or table on the left 
      // and the DataSet and graph statistics on the right 
      this.displayPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.displayPane.setResizeWeight(1);
        this.displayPane.setOneTouchExpandable(true);
        this.displayPane.setRightComponent(statsPanel);
      
      // the main pane is a split pane that holds displayPane on the 
      // top and the panel that displays the graph on the bottom
      this.mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.mainPane.setResizeWeight(1);
        this.mainPane.setOneTouchExpandable(true);
        this.mainPane.setTopComponent(this.displayPane);
        this.mainPane.setBottomComponent(this.functComp.getDisplayPanel());
      
      // now the data has to be set and 
      // the GUI has to be constructed 
      // the superclass calls buildComponent() to make the view
      setData(vArray);
      buildComponent(viewType);
      buildDataSetDisplay();
   }
   
   /**
    * Updates the display to reflect any changes made to 
    * <code>displaySwapper</code>.  For example, if 
    * <code>displaySwapper</code> has swapped to a new view of the 
    * data, this new view will be reflected graphically after 
    * this method is invoked.
    */
   private void buildDataSetDisplay()
   {
      IViewComponent comp = this.displaySwapper.getViewComponent();
      if (comp == null)
         return;
      
      removeAll();
      this.displayPane.setLeftComponent(comp.getDisplayPanel());
      add(this.mainPane);
      revalidate();
      repaint();
   }
   
   /**
    * This method displays a specified row in the FunctionViewComponent.
    * 
    * @param row - A row index from a IVirtualArray2D.
    */
   private void displayRowInFunctionView(int row)
   {
     //making a new graph to put into FunctionViewComponent
     float[] yvals = vArray.getRowValues(row,0,vArray.getNumColumns()-1);
     float[] xvals = new float[yvals.length+1];
     float xmin = vArray.getAxisInfo(row).getMin();
     float xmax = vArray.getAxisInfo(row).getMax();
     float xcount = xvals.length;
     float xinc;
          
     //finding the x value increment
     if(xcount > 1)
     {
       xinc = (xmax-xmin)/(xcount-1);
     }
     else 
       xinc = 0;     
     
     //filling xvals
     for(int i=0;i<xcount;i++)
     {
       xvals[i]=xmin+(xinc*i);
     }
     
     DataArray1D datArray = new DataArray1D(xvals,yvals);
     VirtualArrayList1D va1D = new VirtualArrayList1D(datArray);
     
     this.functComp.dataChanged(va1D);
   }
   
//----------------=[ Implemented for AbstractComponentSwapper ]=-------------//
   /**
    * Used to modify the data to view.
    * <p>
    * Note:  The data must be an instance of <code>DataSetVirtualArray</code>.
    * 
    * @param iva The new data to view.
    */
   @Override
   public void setData(IVirtualArray iva)
   {
      if ( (this.displaySwapper != null) && 
           (iva instanceof DataSetVirtualArray) )
         this.displaySwapper.setData(iva);
   }
   
   /**
    * Makes <code>dataSwapper</code> swap to the given view of data.  Also, 
    * this new view's controls are placed in <code>controlsFrame</code> 
    * and the old ones are removed.  
    * <p>
    * Note:  The GUI is not updated by this method.  To update the display 
    *        invoke the <code>buildDataSetDisplay</code> method.
    * 
    * @param viewType A string describing how the data should be viewd.  
    *                 Possible values are: <br>
    *                 <ul>
    *                   <li><code>IComponentSwapper.IMAGE</code></li>
    *                   <li><code>IComponentSwapper.CONTOUR</code></li>
    *                   <li><code>IComponentSwapper.TABLE</code></li>
    *                 </ul>
    */
   @Override
   protected void buildComponent(String viewType)
   {
      if (!isAvailableViewType(viewType))
         return;
      
      // get the panel that holds the data being displayed 
      // as either an image, contour plot, or table
      //JPanel dataPanel = this.displaySwapper.getViewComponent().getDataPanel();
      JPanel dataPanel = this.displaySwapper.getViewComponent().getDisplayPanel();
      
      // remove 'this' as a component listener to the panel
      dataPanel.removeComponentListener(this);
      
      // switch the display swapper to the new view type
      this.displaySwapper.setVisibleViewType(viewType);
      
      // get the display swapper's new ViewComponent
      IViewComponent comp = this.displaySwapper.getViewComponent();
      
      // get the display swapper's new data panel 
      // corresponding to the new view
      //dataPanel = comp.getDataPanel();
      dataPanel = comp.getDisplayPanel();
      
      // inform the panel you want to listen to changes in its size
      dataPanel.addComponentListener(this);
      
      // the superclass needs this to know which 
      // view component is being displayed
      current_tracker = new ComponentTracker(comp);
      
      // in the constructor, this content pane is specifically set to 
      // be a JPanel
      JPanel controlsPanel = (JPanel)this.controlsFrame.getContentPane();
      
      // remove the old view's controls
      controlsPanel.removeAll();
        
      // display all of visible controls
      ViewControl[] controls = comp.getControls();
      int[] visIndices = this.displaySwapper.getVisibleControls();
      for (int index : visIndices)
         controlsPanel.add(controls[index]);
        
      controlsPanel.revalidate();
      this.controlsFrame.pack();
   }
   
   /**
    * This method passes information from the previous view to the 
    * new visible view.
    */ 
   @Override
   protected void linkComponents()
   {
      this.displaySwapper.linkComponents();
   }
//--------------=[ End Implemented for AbstractComponentSwapper ]=------------//

   
   
//--------------------=[ Implemented for ComponentListener ]=-----------------//
   /**
    * Invoked if the current view's data panel has been resized.  
    * The number of columns that the current data can use to holds the 
    * data depends on the width of this panel in pixels.  As the 
    * data panel is resized, the current data is informed of the change.
    */
   public void componentResized(ComponentEvent e)
   {
      Component comp = e.getComponent();
      if (comp == null)
         return;
      
      /**TODO*/
      //this.vArray.setNumColumns(comp.getWidth());
      this.mainPane.invalidate();
      comp.repaint();
   }
   
   /**
    * This method is empty and unused.
    */
   public void componentMoved(ComponentEvent e)
   {
   }
   
   /**
    * This method is empty and unused.
    */
   public void componentShown(ComponentEvent e)
   {
   }
   
   /**
    * This method is empty and unused.
    */
   public void componentHidden(ComponentEvent e)
   {
   }
//----------------=[ End Implemented for ComponentListener ]=-----------------//
   
   
//----------------=[ Overides of AbstractComponentSwapper ]=------------------//
   /**
    * Switches the current view to the new view.
    * 
    * @param viewType A string describing how the data should be viewd.  
    *                 Possible values are: <br>
    *                 <ul>
    *                   <li><code>IComponentSwapper.IMAGE</code></li>
    *                   <li><code>IComponentSwapper.CONTOUR</code></li>
    *                   <li><code>IComponentSwapper.TABLE</code></li>
    *                 </ul>
    */
   public void setVisibleViewType( String viewType )
   {
      // switch to the new view so that the call to the super class 
      // causes the new view's menu items to be displayed
      buildComponent(viewType);
      
      // ask the super class to set the visible view.
      // This will have the affect of saving the view information 
      // in the ObjectState.
      super.setVisibleViewType(viewType);
      
      // again switch to the new view because asking the super class 
      // to set the visible view makes the controls displayed incorrect
      buildComponent(viewType);
      
      // build the display
      buildDataSetDisplay();
   }
//--------------=[ End overides of AbstractComponentSwapper ]=----------------//
   
   
//----------------------=[ Inner classes ]=-----------------------------------//
   /**
    * This listener listens to changes to which data is being pointed at by 
    * the current view in <code>displaySwapper</code>.
    */
   private class SwapperMotionListener implements ActionValueListener
   {
      /**
       * Invoked when the current view has modified.  This method is only 
       * concerns with changes to the current data being pointed at.  
       * If new data is being pointed at, <code>functComp</code> is 
       * updated to view this new data.  Also, the meta-data at this point 
       * is displayed in <code>graphStatsControl</code>.
       */
      public void valueChanged(ActionValueEvent ave)
      {
         if (ave == null)
            return;
      
         if (ave.getActionCommand().equals(IViewComponent.POINTED_AT_CHANGED))
         {               
            Object ob = ave.getSource();
            if (ob == null)
               return;
            
            //the row that is(will be) pointed at
            int row = 0;
            
            if (ob instanceof ImageViewComponent)
            {
               ImageViewComponent imageComp = (ImageViewComponent)ob;         
               floatPoint2D worldCoords = (floatPoint2D)ave.getNewValue();         
               Point rowCol = imageComp.getColumnRowAtWorldCoords(worldCoords);
         
               row = (int)rowCol.getY();
            }
            else if (ob instanceof ContourViewComponent)
            {
               ContourViewComponent contourComp = (ContourViewComponent)ob;               
               floatPoint2D worldCoords = (floatPoint2D)ave.getNewValue();
               
               row = contourComp.getRowForY(worldCoords.y);
             }
            else if (ob instanceof TableViewComponent)
            {
               TableViewComponent tableComp = (TableViewComponent)ob;               
               floatPoint2D worldCoords = tableComp.getPointedAt();
               Point rowCol = tableComp.getColumnRowAtWorldCoords(worldCoords);
               
               row = rowCol.y;
            }
            
            //vArray.getDataSet().setPointedAtIndex(row);
            //functComp.dataChanged();
            displayRowInFunctionView(row);
      
            // No conversions need to be done here to convert worldCoords.x 
            // into the propery value in terms of the XScale of the Data at 
            // index 'row'.  This is because worldCoords.x is in world 
            // coordinates (and not pixel coordinates).
            /**
             * TODO: Can set Stats control when it can be implemented correctly
             * (see INIT todo).
             */
            //imageStatsControl.displayDataAt(worldCoords.x, row);
         }
      }
   }
   
   /**
    * This listener listens to changes to which data is being pointed at in 
    * in <code>functComp</code>.
    */
   private class FunctionListener implements ActionListener
   {
      /**
       * Invoked when <code>functComp</code> is modified.  This method is 
       * only concerned with changes to the current data being pointed at.  
       * As this changes, the meta-data viewed in 
       * <code>graphStatsControl</code> is updated.
       */
      public void actionPerformed(ActionEvent event)
      {
         String message = event.getActionCommand();
         if (message == null)
            return;
         
         if (message.equals(IViewComponent.POINTED_AT_CHANGED))
         {
            floatPoint2D pt = functComp.getPointedAt();
            if (pt == null)
               return;
            
            /**
             * TODO: Can set Stats control when it can be implemented correctly
             * (see INIT todo).
             */
            //int index = vArray.getDataSet().getPointedAtIndex();
            //graphStatsControl.displayDataAt(pt.x, index);
            
         }
      }
   }
   
   /**
    * The display contains the <code>JToggleButton</code>, 
    * <code>showControlButton</code> that the user can press/unpress to 
    * view/hide <code>controlsFrame</code>, which holds the current 
    * view's controls.  This class listens to this button being 
    * pressed/unpressed and makes the frame visible/invisible.
    */
   private class ShowControlsListener 
                       extends WindowAdapter 
                                     implements ActionListener
   {
      /**
       * Invoked when the toggle button displayed above is selected or 
       * unselected.  This class makes <code>controlFrame</code> 
       * visible/invisible depending on whether or not the toggle button 
       * is selected or unselected.
       */
      public void actionPerformed(ActionEvent event)
      {
         controlsFrame.setVisible(showControlsButton.isSelected());
      }
      
      /**
       * Invoked when <code>controlsFrame</code> is closing.  This 
       * method makes the toggle button unselected.
       */
      public void windowClosing(WindowEvent e)
      {
         showControlsButton.setSelected(false);
      }
      
      /**
       * Invoked when <code>controlsFrame</code> is opened.  This method 
       * makes the toggle button selected.
       */
      public void windowOpened(WindowEvent e)
      {
         showControlsButton.setSelected(true);
      }
   }
//----------------------=[ Inner classes ]=-----------------------------------//
   
   /**
    * Testbed.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      DataSet ds = new DataSet();
      
      float[][] testData = ContourViewComponent.getTestDataArr(41,51,3,4);
      for (int i=0; i<testData.length; i++)
         ds.addData_entry(
               new FunctionTable(new UniformXScale(0, 
                                 testData[i].length-1, 
                                 testData[i].length),
                                 testData[i], 
                                 i));
      
      ComponentViewManager manager = 
         new ComponentViewManager(new DataSetVirtualArray(ds, 200), 
                                  ComponentViewManager.DATA_SET);
      WindowShower.show(manager);
      
      manager.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
   }
}
