/*
 * File: ImageViewComponent.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
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
 *
 *  $Log$
 *  Revision 1.16  2003/07/10 13:38:30  dennis
 *  - Commented out unnecessary import statement
 *  - Made One-sided color models available
 *  (Mike Miller)
 *
 *  Revision 1.15  2003/07/05 19:47:34  dennis
 *  - Added methods getDataMin() and getDataMax().
 *  - Added capability for one- or two-sided color models. Currently,
 *    only two-sided color models are allowed. Alter line 187 to
 *    enable this feature.  (Mike Miller)
 *
 *  Revision 1.14  2003/06/18 22:16:42  dennis
 *  (Mike Miller)
 *  - Changed how horizontal color scale was added to the view
 *    component, now less wasted space.
 *  - Reduced size of vertical color scale.
 *
 *  Revision 1.13  2003/06/18 13:33:50  dennis
 *  (Mike Miller)
 *  - Now implements IColorScaleAddible, which required the addition of
 *    getColorScale() and getLogScale().
 *  - Added methods setColorControlEast() and setColorControlSouth() to
 *    allow user to add the vertical calibrated color scale to the east
 *    panel, or the horizontal calibrated color scale to the south panel.
 *
 *  Revision 1.12  2003/06/13 14:43:53  dennis
 *  - Added methods and implementation for getLocalCoordBounds() and
 *  getGlobalCoordBounds() to allow selection and annotation overlays to adjust
 *  when a zoom occurs. (Mike Miller)
 *
 *  Revision 1.11  2003/06/09 14:46:34  dennis
 *  Added functional HelpMenu under Options. (Mike Miller)
 *
 *  Revision 1.10  2003/06/06 18:51:00  dennis
 *  Added control for editing annotations. (Mike Miller)
 *
 *  Revision 1.9  2003/06/05 17:15:00  dennis
 *   - Added getFocus() call when Selection/AnnotationOverlay checkbox
 *     is selected. (Mike Miller)
 *
 *  Revision 1.8  2003/05/29 14:34:32  dennis
 *  Three changes: (Mike Miller)
 *   -added SelectionOverlay and its on/off control
 *   -added ControlColorScale to controls
 *   -added AnnotationOverlay and its on/off control
 *
 *  Revision 1.7  2003/05/24 17:33:25  dennis
 *  Added on/of control for Axis Overlay. (Mike Miller)
 * 
 *  Revision 1.6  2003/05/22 13:05:58  dennis
 *  Now returns menu items to place in menu bar.
 *
 *  Revision 1.5  2003/05/20 19:46:16  dennis
 *  Now creates a brightness control slider. (Mike Miller)
 *
 *  Revision 1.4  2003/05/16 15:25:12  dennis
 *  Implemented dataChanged() method.
 *  Added grid lines to test image to aid in testing.
 *
 *  Revision 1.3  2003/05/16 14:59:11  dennis
 *  Calculates space needed for labels, and adjusts space as the component
 *  is resized.  (Mike Miller)
 *
 */
 
package DataSetTools.components.View.TwoD;

import javax.swing.*; 
import javax.swing.event.*;
import java.io.Serializable;
import java.awt.*;
import java.lang.*;
import java.awt.event.*;
import java.awt.Rectangle.*;
import java.util.*; 
import java.awt.font.FontRenderContext;

import DataSetTools.util.*; 
import DataSetTools.math.*;
import DataSetTools.components.image.*; //ImageJPanel & CoordJPanel
import DataSetTools.components.View.Transparency.*;
import DataSetTools.components.View.*;  // IVirtualArray2D
import DataSetTools.components.View.ViewControls.*;
import DataSetTools.components.View.Menu.*;
import DataSetTools.components.ui.ColorScaleMenu;

// Component location and resizing within the big_picture
import java.awt.event.ComponentAdapter.*;
import java.awt.geom.*;

/**
 * This class allows the user to view data in the form of an image. Meaning
 * is given to the data by way of overlays, which add calibration, selection,
 * and annotation abilities.
 */
public class ImageViewComponent implements IViewComponent2D, 
                                           ActionListener,
					   IColorScaleAddible
{
   private IVirtualArray2D Varray2D;  //An object containing our array of data
   private Point[] selectedset; //To be returned by getSelectedSet()   
   private Vector Listeners = null;   
   private JPanel big_picture = new JPanel();    
   private ImageJPanel ijp;
   // for component size and location adjustments
   //private ComponentAltered comp_listener;
   private Rectangle regioninfo;
   private CoordBounds local_bounds;
   private CoordBounds global_bounds;
   private Vector transparencies = new Vector();
   private int precision;
   private Font font;
   private ViewControl[] controls = new ViewControl[6];
   private ViewMenuItem[] menus = new ViewMenuItem[2];
   private String colorscale;
   private boolean isTwoSided = true;
   private double logscale = 0;
   private boolean addColorControlEast = false;
   private boolean addColorControlSouth = false;
   
  /**
   * Constructor that takes in a virtual array and creates an imagejpanel
   * to be viewed in a border layout.
   *
   *  @param  ivirtualarray
   */
   public ImageViewComponent( IVirtualArray2D varr )  
   {
      Varray2D = varr; // Get reference to varr
      precision = 4;
      font = FontUtil.LABEL_FONT2;
      ijp = new ImageJPanel();
      //Make ijp correspond to the data in f_array
      ijp.setData(varr.getRegionValues(0, 1000000, 0, 1000000), true); 
      ImageListener ijp_listener = new ImageListener();
      ijp.addActionListener( ijp_listener );
                  
      ComponentAltered comp_listener = new ComponentAltered();   
      ijp.addComponentListener( comp_listener );
      
      regioninfo = new Rectangle( ijp.getBounds() );
      local_bounds = ijp.getLocalWorldCoords().MakeCopy();
      global_bounds = ijp.getGlobalWorldCoords().MakeCopy();
      
      AxisInfo2D xinfo = varr.getAxisInfoVA(AxisInfo2D.XAXIS);
      AxisInfo2D yinfo = varr.getAxisInfoVA(AxisInfo2D.YAXIS);
      
      ijp.initializeWorldCoords( new CoordBounds( xinfo.getMin(),
                                                  yinfo.getMax(),      
                                                  xinfo.getMax(),
						  yinfo.getMin() ) ); 
      
      colorscale = IndexColorMaker.HEATED_OBJECT_SCALE_2;
      // two-sided model
      if( ijp.getDataMin() < 0 )
         isTwoSided = true;
      // one-sided model
      else
         isTwoSided = false;
      ijp.setNamedColorModel(colorscale, isTwoSided, false); 
      
      Listeners = new Vector();
      buildViewComponent();    // initializes big_picture to jpanel containing
                               // the background and transparencies
      buildViewControls(); 
      buildViewMenuItems();  
   }  
   
// These method are required because this component implements 
// IColorScaleAddible
  /**
   * This method returns the color scale used by the imagejpanel.
   *
   *  @return colorscale
   */
   public String getColorScale()
   {
      return colorscale;
   }
  
  /**
   * This method will get the current data minimum from the imagejpanel.
   */
   public float getDataMin()
   {
      return ijp.getDataMin();
   }
   
  /**
   * This method will get the current data maximum from the imagejpanel.
   */ 
   public float getDataMax()
   {
      return ijp.getDataMax();
   }
   
// The following methods are required because this component implements 
// IColorScaleAddible which extends ILogAxisAddible2D which extends 
// IAxisAddible2D
  /**
   * This method returns the info about the specified axis. 
   * 
   *  @param  isX
   *  @return If isX = true, return info about x axis.
   *          If isX = false, return info about y axis.
   */
   public AxisInfo2D getAxisInfo( boolean isX )
   {
      // if true, return x info
      if( isX )
         return new AxisInfo2D( ijp.getLocalWorldCoords().getX1(),
	               ijp.getLocalWorldCoords().getX2(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.XAXIS).getLabel(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.XAXIS).getUnits(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.XAXIS).getIsLinear() );
      // if false return y info
      return new AxisInfo2D( ijp.getLocalWorldCoords().getY1(),
	               ijp.getLocalWorldCoords().getY2(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.YAXIS).getLabel(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.YAXIS).getUnits(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.YAXIS).getIsLinear() );
   }
   
  /**
   * This method returns a rectangle containing the location and size
   * of the imagejpanel.
   *
   *  @return The region info about the imagejpanel
   */ 
   public Rectangle getRegionInfo()
   {
      return regioninfo;
   }    
  
  /**
   * This method will return the title given to the image as specified by
   * the Virtual Array
   *
   *  @return title stored in Virtual Array
   */
   public String getTitle()
   {
      return Varray2D.getTitle();
   }
   
  /**
   * This method will return the precision specified by the user. Precision
   * will be assumed to be 4 if not specified. The overlays will call
   * this method to determine the precision.
   *
   *  @return precision of displayed values
   */
   public int getPrecision() 
   {
      return precision;
   }  
   
  /**
   * This method will return the font used on by the overlays. The axis overlay
   * will call this to determine what font to use.
   *
   *  @return font of displayed values
   */
   public Font getFont()
   {
      return font;
   }
   
  /**
   * This method will return the local coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
   public CoordBounds getLocalCoordBounds()
   {
      //return local_bounds;
      return ijp.getLocalWorldCoords().MakeCopy();
   }
      
  /**
   * This method will return the global coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
   public CoordBounds getGlobalCoordBounds()
   {
      //return global_bounds;
      return ijp.getGlobalWorldCoords().MakeCopy();
   }  
   
  /**
   * This method will get the current log scale value for the imagejpanel.
   */ 
   public double getLogScale()
   {
      return logscale;
   }
   
//****************************************************************************

// Methods required since this component implements IViewComponent2D
  /**
   * This method adjusts the crosshairs on the imagejpanel.
   * setPointedAt is called from the viewer when another component
   * changes the selected point.
   *
   *  @param  pt
   */
   public void setPointedAt( Point pt )
   {
      System.out.println("Entering: void setPointedAt( Point pt )");
      System.out.println("X value = " + pt.getX() );
      System.out.println("Y value = " + pt.getY() );
      
      //Type cast Point pt  into  floatPoint2D fpt
      floatPoint2D fpt = new floatPoint2D( (float)pt.x, (float)pt.y );
      
      //set the cursor position on ImageJPanel
      ijp.setCurrent_WC_point( fpt ); 
      
      System.out.println("");
   }
  
  /**
   * This method creates a selected region to be displayed over the imagejpanel
   * by an overlay.
   *
   *  @param  pts
   */ 
   public void setSelectedSet( Point[] pts ) 
   {
      // implement after selection overlay has been created
      System.out.println("Entering: void setSelectedSet( Point[] coords )");
      System.out.println("");
   }
  
  /**
   * This method will be called to notify this component of a change in data.
   */
   public void dataChanged()  
   {
      float[][] f_array = Varray2D.getRegionValues( 0, 1000000, 0, 1000000 );
      ijp.setData(f_array, true);
   }
  
  /**
   * To be continued...
   */ 
   public void dataChanged( IVirtualArray2D pin_Varray ) // pin == "passed in"
   {
      System.out.println("Now in void dataChanged(VirtualArray2D pin_Varray)");

      //get the complete 2D array of floats from pin_Varray
      float[][] f_array = Varray2D.getRegionValues( 0, 1000000, 0, 1000000 );

      ijp.setData(f_array, true);  
      
      System.out.println("Value of first element: " + f_array[0][0] );
      System.out.println("Thank you for notifying us");
      System.out.println("");
   }
  
  /**
   * Get selected set specified by setSelectedSet. The selection overlay
   * will need to use this method.
   *
   *  @return selectedset
   */ 
   public Point[] getSelectedSet() //keep the same (for now)
   {
      System.out.println("Entering: Point[] getSelectedSet()");
      System.out.println("");
      return selectedset;
   }
   
  /**
   * Method to add a listener to this component.
   *
   *  @param act_listener
   */
   public void addActionListener( ActionListener act_listener )
   {          
      for ( int i = 0; i < Listeners.size(); i++ )    // don't add it if it's
        if ( Listeners.elementAt(i).equals( act_listener ) ) // already there
          return;

      Listeners.add( act_listener ); //Otherwise add act_listener
   }
  
  /**
   * Method to remove a listener from this component.
   *
   *  @param act_listener
   */ 
   public void removeActionListener( ActionListener act_listener )
   {
      Listeners.remove( act_listener );
   }
  
  /**
   * Method to remove all listeners from this component.
   */ 
   public void removeAllActionListeners()
   {
      Listeners.removeAllElements();
   }
  
  /**
   * Returns all of the controls needed by this view component
   *
   *  @return controls
   */ 
   public JComponent[] getSharedControls()
   {    
      return controls;
   }
   
   public JComponent[] getPrivateControls()
   {
      System.out.println("Entering: JComponent[] getPrivateControls()");
      System.out.println("***Currently unimplemented***");
      
      return new JComponent[0];
   }
  
  /**
   * Returns all of the menu items needed by this view component
   *
   *  @return menus;
   */ 
   public ViewMenuItem[] getSharedMenuItems()
   {
      return menus;
   }
   
   public ViewMenuItem[] getPrivateMenuItems()
   {
      System.out.println("Entering: JMenuItems[] getPrivateMenuItems()");
      System.out.println("***Currently unimplemented***");
      
      return new ViewMenuItem[0];
   }
   
  /**
   * Return the "background" or "master" panel
   *
   *  @return JPanel containing imagejpanel in the center of a borderlayout.  
   */
   public JPanel getDisplayPanel()
   {
       return big_picture;   
   }

  /*
   *  Gets the current point
   */
   public Point getCurrentPoint()
   {
     floatPoint2D fpt = new floatPoint2D();
     fpt = ijp.getCurrent_WC_point();
     
     Point pt = new Point((int)fpt.x, (int)fpt.y);
     
     return pt;
   }
   
  /**
   * This method allows the user to place a VERTICAL color control in the
   * east panel of the view component.
   */
   public void setColorControlEast( boolean isOn )
   {
      addColorControlEast = isOn;
      buildViewComponent();
   }
   
  /**
   * This method allows the user to place a HORIZONTAL color control in the
   * south panel of the view component.
   */   
   public void setColorControlSouth( boolean isOn )
   {
      addColorControlSouth = isOn;
      buildViewComponent();
   }
   
  /*
   * Tells all listeners about a new action.
   *
   *  @param  message
   */  
   private void sendMessage( String message )
   {
     for ( int i = 0; i < Listeners.size(); i++ )
     {
       ActionListener listener = (ActionListener)Listeners.elementAt(i);
       listener.actionPerformed( new ActionEvent( this, 0, message ) );
     }
   }
  
  // required since implementing ActionListener
  /**
   * To be continued...
   */ 
   public void actionPerformed( ActionEvent e )
   {
     //get POINTED_AT_CHANGED or SELECTED_CHANGED message from e 
     String message = e.getActionCommand();     
     
     //Send message to tester 
     if ( message.equals(POINTED_AT_CHANGED) )
         sendMessage(POINTED_AT_CHANGED);
   }
   
   private void paintComponents( Graphics g )
   {
      for( int i = big_picture.getComponentCount(); i > 0; i-- )
      {
         if( big_picture.getComponent( i - 1 ).isVisible() )
	    big_picture.getComponent( i - 1 ).update(g);
      }
      big_picture.getParent().getParent().getParent().getParent().repaint();
   }
   
  /*
   * This method takes in an imagejpanel and puts it into a borderlayout.
   * Overlays are added to allow for calibration, selection, and annotation.
   */
   private void buildViewComponent()
   {   
      int westwidth = font.getSize() * precision + 22;
      int southwidth = font.getSize() * 3 + 9;
      // this will be the background for the master panel
      JPanel background = new JPanel(new BorderLayout());
      
      JPanel north = new JPanel();
      north.setPreferredSize(new Dimension( 0, 25 ) );
      JPanel east; 
      JPanel south;
      if( addColorControlEast )
      {
         east = new ControlColorScale( this, ControlColorScale.VERTICAL );
         east.setPreferredSize( new Dimension( 90, 0 ) );
	 ((ControlColorScale)east).setTitle("Y Axis Color Scale");
      }
      else
      {
         east = new JPanel();
         east.setPreferredSize(new Dimension( 50, 0 ) );
      }
      if( addColorControlSouth )
      {
         south = new JPanel( new BorderLayout() );
	 JPanel mininorth = new JPanel();
	 mininorth.setPreferredSize( new Dimension( 0, southwidth ) );
	 south.add( mininorth, "North" );
	 ControlColorScale ccs = new ControlColorScale(
	                               this,ControlColorScale.HORIZONTAL);
	 ccs.setTitle("X Axis Color Scale");
	 south.add( ccs, "Center" );
	 south.setPreferredSize( new Dimension( 0, southwidth + 75) );
      }
      else
      {
         south = new JPanel();    
         south.setPreferredSize(new Dimension( 0, southwidth ) );
      }
  
      JPanel west = new JPanel();
      west.setPreferredSize(new Dimension( westwidth, 0 ) );
      
      //Construct the background JPanel
	
      background.add(ijp, "Center");
      background.add(north, "North");
      background.add(west, "West");
      background.add(south, "South");
      background.add(east, "East" );      
      
      AnnotationOverlay top = new AnnotationOverlay(this);
      top.setVisible(false);      // initialize this overlay to off.
      SelectionOverlay nextup = new SelectionOverlay(this);
      nextup.setVisible(false);   // initialize this overlay to off.
      nextup.setRegionColor(Color.magenta);
      AxisOverlay2D bottom_overlay = new AxisOverlay2D(this);
      
      // add the transparencies to the transparencies vector
      transparencies.add(top);
      transparencies.add(nextup);
      transparencies.add(bottom_overlay); 

      	  
      // create master panel and
      //  add background and transparency to the master layout
      
      JPanel master = new JPanel();
      OverlayLayout overlay = new OverlayLayout(master);
      master.setLayout(overlay);
      for( int trans = 0; trans < transparencies.size(); trans++ )
         master.add((OverlayJPanel)transparencies.elementAt(trans)); 
      master.add(background);

      big_picture = master;
   }
   
  /*
   * This method constructs the controls required by the ImageViewComponent
   */
   private void buildViewControls()
   {
      // Note: If controls are added here, the size of the array controls[]
      // must be incremented.
      controls[0] = new ControlSlider();
      controls[0].setTitle("Intensity Slider");
      logscale = ((ControlSlider)controls[0]).getValue();	       	    
      controls[0].addActionListener( new ControlListener() );
                 
      controls[1] = new ControlColorScale(IndexColorMaker.HEATED_OBJECT_SCALE_2,
                                          isTwoSided );
      controls[1].setTitle("Color Scale");
      
      controls[2] = new ControlCheckbox(true);
      ((ControlCheckbox)controls[2]).setText("Axis Overlay");
      controls[2].addActionListener( new ControlListener() );
    
      controls[3] = new ControlCheckbox();
      ((ControlCheckbox)controls[3]).setText("Selection Overlay");
      controls[3].addActionListener( new ControlListener() );
      
      controls[4] = new ControlCheckbox();
      ((ControlCheckbox)controls[4]).setText("Annotation Overlay");
      controls[4].addActionListener( new ControlListener() );  
      
      controls[5] = new ViewControlMaker(new JButton("Edit Annotations"));
      controls[5].addActionListener( new ControlListener() );            
   }
   
  /*
   * This method constructs the menu items required by the ImageViewComponent
   */   
   private void buildViewMenuItems()
   {
      menus[0] = new ViewMenuItem("Options", 
                                  new ColorScaleMenu( new ColorListener() ));
      menus[0].addActionListener( new MenuListener() );
      
      menus[1] = new ViewMenuItem("Options", new HelpMenu(new HelpListener()));
      menus[1].addActionListener( new MenuListener() );
   }
   
  //***************************Assistance Classes******************************
  /*
   * ComponentAltered monitors if the imagejpanel has been resized. If so,
   * the regioninfo is updated.
   */
   private class ComponentAltered extends ComponentAdapter
   {
      public void componentResized( ComponentEvent e )
      {
         //System.out.println("Component Resized");
	 Component center = e.getComponent();
	 regioninfo = new Rectangle( center.getLocation(), center.getSize() );
	 /*
	 System.out.println("Location = " + center.getLocation() );
	 System.out.println("Size = " + center.getSize() );
	 System.out.println("class is " + center.getClass() );  
	 */
      }
   }

  /*
   * ImageListener monitors if the imagejpanel has sent any messages.
   * If so, process the message and relay it to the viewer.
   */
   private class ImageListener implements ActionListener
   {
      public void actionPerformed( ActionEvent ae )
      {
         String message = ae.getActionCommand();     
         //System.out.println("Image sent message " + message );

         if (message == CoordJPanel.CURSOR_MOVED)
         {
	    //System.out.println("Sending POINTED_AT_CHANGED" );
            sendMessage(POINTED_AT_CHANGED);
	 }
	 if (message == CoordJPanel.ZOOM_IN)
         {
	    //System.out.println("Sending SELECTED_CHANGED " + regioninfo );
	    for(int next = 0; next < transparencies.size(); next++ )
	       ((OverlayJPanel)transparencies.elementAt(next)).repaint();
            sendMessage(SELECTED_CHANGED);
	    ImageJPanel center = (ImageJPanel)ae.getSource();
	    local_bounds = 
	            ((ImageJPanel)center).getLocalWorldCoords().MakeCopy();
	    global_bounds = 
	            ((ImageJPanel)center).getGlobalWorldCoords().MakeCopy();
	 }
	 if (message == CoordJPanel.RESET_ZOOM)
         {
	    //System.out.println("Sending SELECTED_CHANGED" );
	    for(int next = 0; next < transparencies.size(); next++ )
	       ((OverlayJPanel)transparencies.elementAt(next)).repaint();
            sendMessage(SELECTED_CHANGED);
	 }
      }      
   }
   
  /*
   * ControlListener moniters activities of all controls 
   * of the ImageViewComponent.
   */
   private class ControlListener implements ActionListener
   { 
      public void actionPerformed( ActionEvent ae )
      {
         String message = ae.getActionCommand();
                              // set image log scale when slider stops moving
         if ( message == IViewControl.SLIDER_CHANGED )
         {
	    ControlSlider control = (ControlSlider)ae.getSource();
	    logscale = control.getValue();	       	              	       	
	    ijp.changeLogScale( logscale, true );
	    ((ControlColorScale)controls[1]).setLogScale( logscale );
         } 
         else if ( message == IViewControl.CHECKBOX_CHANGED )
         {
	    ControlCheckbox control = (ControlCheckbox)ae.getSource();
	    int bpsize = big_picture.getComponentCount();
	    // if this control turns on/off the axis overlay...
	    if( control.getText().equals("Axis Overlay") )
	    {	    
	       JPanel back = (JPanel)big_picture.getComponent( bpsize - 1 );
               if( !control.isSelected() )
	       {
	          big_picture.getComponent(bpsize - 2).setVisible(false); 
		                                                 // axis overlay
	          back.getComponent(1).setVisible(false);        // north
	          back.getComponent(2).setVisible(false);        // west
	          back.getComponent(3).setVisible(false);        // south
	          back.getComponent(4).setVisible(false);        // east
	       }
	       else
	       {	       
	          back.getComponent(1).setVisible(true);
                  back.getComponent(2).setVisible(true);
	          back.getComponent(3).setVisible(true);
	          back.getComponent(4).setVisible(true);
	          big_picture.getComponent(bpsize - 2).setVisible(true);    
	       }
	    }// end of if( axis overlay control ) 
	    // if this control turns on/off the selection overlay...
	    else if( control.getText().equals("Selection Overlay") )
	    { 
	       SelectionOverlay select = (SelectionOverlay)
	                       big_picture.getComponent(
	                       big_picture.getComponentCount() - 3 ); 
               if( !control.isSelected() )
	          select.setVisible(false);
	       else
	       {
	          select.setVisible(true); 
		  select.getFocus();
	       }
	    } 
	    else if( control.getText().equals("Annotation Overlay") )
	    { 
	       AnnotationOverlay note = (AnnotationOverlay)
	                       big_picture.getComponent(
	                       big_picture.getComponentCount() - 4 ); 
               if( !control.isSelected() )
	          note.setVisible(false);
	       else
	       {
	          note.setVisible(true);
		  note.getFocus();
	       } 
	    }  	            
	 } // end if checkbox
	 else if( message.equals("Edit Annotations") )
	 {
	    AnnotationOverlay note = (AnnotationOverlay)
	                       big_picture.getComponent(
	                       big_picture.getComponentCount() - 4 ); 
	    note.editAnnotation();
	 } 	
	 //repaints overlays accurately	
	 sendMessage( message );
         paintComponents( big_picture.getGraphics() ); 
      }
   } 

  /*
   * This class relays the message sent out by the ColorScaleMenu
   */  
   private class ColorListener implements ActionListener
   {
      public void actionPerformed( ActionEvent ae )
      {
         colorscale = ae.getActionCommand();
         ijp.setNamedColorModel( colorscale, isTwoSided, true );
	 ((ControlColorScale)controls[1]).setColorScale( colorscale, 
	                                                 isTwoSided );
         
	 sendMessage( colorscale );
	 //System.out.println("ViewComponent Color Scheme = " + 
	 //                  ae.getActionCommand() );
	 /*
	 SelectionOverlay so = (SelectionOverlay)big_picture.getComponent(
	      big_picture.getComponentCount() - 3 );
	 
	 if( colorscale.equals(IndexColorMaker.GRAY_SCALE) )
	    so.setRegionColor(Color.red);
	 else if( colorscale.equals(IndexColorMaker.NEGATIVE_GRAY_SCALE) )
	    so.setRegionColor(Color.red);
	 else if( colorscale.equals(IndexColorMaker.GREEN_YELLOW_SCALE) )
	    so.setRegionColor(Color.red);
	 else if( colorscale.equals(IndexColorMaker.HEATED_OBJECT_SCALE) )
	    so.setRegionColor(Color.green);
	 else if( colorscale.equals(IndexColorMaker.HEATED_OBJECT_SCALE_2) )
	    so.setRegionColor(Color.magenta);
	 else if( colorscale.equals(IndexColorMaker.RAINBOW_SCALE) )
	    so.setRegionColor(Color.white);
	 else if( colorscale.equals(IndexColorMaker.OPTIMAL_SCALE) )
	    so.setRegionColor(Color.green);   
	 else if( colorscale.equals(IndexColorMaker.MULTI_SCALE) )
	    so.setRegionColor(Color.pink);
	 else //if( colorscale.equals(IndexColorMaker.SPECTRUM_SCALE) )
	    so.setRegionColor(Color.white);*/
	      
	 paintComponents( big_picture.getGraphics() ); 
      }
   }

  /*
   * This class relays the message sent out by the HelpMenu
   */  
   private class HelpListener implements ActionListener
   {
      public void actionPerformed( ActionEvent ae )
      {
         String button = ae.getActionCommand();
	 if( button.equals("Annotation") )
	 {
	    //System.out.println("AnnotationHelpMenu");
	    AnnotationOverlay.help();
	 }
	 else if( button.equals("Axes") )
	 {
	    //System.out.println("AxesHelpMenu");
	    AxisOverlay2D.help();
	 }
	 else if( button.equals("Selection") )
	 {
	    //System.out.println("SelectionHelpMenu");
	    SelectionOverlay.help();  
	 }	 
      }
   } 

  /*
   * This class relays the message sent out by the ViewMenuItem
   */  
   private class MenuListener implements ActionListener
   {
      public void actionPerformed( ActionEvent ae )
      {
         sendMessage( ae.getActionCommand() );
	 //System.out.println("VCPath = " + 
	 //                  ae.getActionCommand() );
      }
   }   
      
  /*
   * MAIN - Basic main program to test an ImageViewComponent object
   */
   public static void main( String args[] ) 
   {
      int col = 200;
      int row = 200;

      //Make a sample 2D array
      VirtualArray2D va2D = new VirtualArray2D(row, col); 
      va2D.setAxisInfoVA( AxisInfo2D.XAXIS, 0f, 10000f, 
        		 "TestX","TestUnits", true );
      va2D.setAxisInfoVA( AxisInfo2D.YAXIS, 0f, 1000f, 
        		  "TestY","TestYUnits", true );
      va2D.setTitle("Main Test");
      //Fill the 2D array with the function x*y
      for(int i = 0; i < row; i++)
      {
          for(int j = 0; j < col; j++)
          {
	      // adds vertical and horizontal test lines every 25th pixel
              if ( i % 25 == 0 )
        	va2D.setDataValue(i, j, i*col); //put float into va2D
              else if ( j % 25 == 0 )
        	va2D.setDataValue(i, j, j*row); //put float into va2D
              else
        	va2D.setDataValue(i, j, i*j); //put float into va2D
          }
      }

      //Construct an ImageViewComponent with array2D
      ImageViewComponent ivc = new ImageViewComponent(va2D);
      ivc.setColorControlEast(true);
      ivc.setColorControlSouth(true);
      ViewerSim viewer = new ViewerSim(ivc);
      viewer.show();	
   }
}
